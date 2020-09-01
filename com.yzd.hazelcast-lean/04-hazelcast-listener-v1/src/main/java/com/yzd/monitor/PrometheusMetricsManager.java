package com.yzd.monitor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.yzd.config.MetricsConfig;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.common.TextFormat;
import io.prometheus.client.hotspot.DefaultExports;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;


/**
 * @author ethan
 */
@Slf4j
public class PrometheusMetricsManager implements MetricsManager {

    private static final String CONNECTION_METRICS = "connections";
    private static final String EXCEPTION_METRICS = "exception_total";
    private static final String PENDING_REQUEST_METRICS = "requests_pending";
    private static final String REQUEST_COUNTER_METRICS = "requests_total";
    private static final String PAYLOAD_SIZE_METRICS = "payload_bytes";
    private static final String REQUEST_LATENCY_METRICS = "request_seconds";
    private static final String GIT_INFO_METRICS = "git_info";
    private static final String CONTAINER_CONFIG_VERSION_METRICS = "container_config_version";

    private static final String SERVICE_NAME_TAG = "service";
    private static final String GIT_SHORT_COMMIT_ID_TAG = "short_commit_id";
    private static final String INNER_STATUS_TAG = "inner_status";
    private static final String TARGET_STATUS_TAG = "target_status";

    private final HttpServer monitorHttpServer;

    private final Gauge connectionGauge;

    private final Counter exceptionCounter;

    private final Gauge pendingRequestGauge;

    private final Counter requestCounter;

    private final Counter payloadCounter;

    private final Histogram requestLatencyHistogram;

    private final Gauge gitShortCommitIdGauge;

    private final Gauge containerConfigVersionGauge;

    public PrometheusMetricsManager(MetricsConfig metricsConfig,
                                    ExecutorService executorService) throws Exception {
        this.connectionGauge = Gauge.build().name(CONNECTION_METRICS)
                .help("Listener total connections.")
                .register();
        this.requestCounter = Counter.build().name(REQUEST_COUNTER_METRICS)
                .help("Listener total requests.")
                .labelNames(SERVICE_NAME_TAG, INNER_STATUS_TAG, TARGET_STATUS_TAG)
                .register();
        this.exceptionCounter = Counter.build().name(EXCEPTION_METRICS)
                .help("Listener total errors.")
                .labelNames(SERVICE_NAME_TAG)
                .register();
        this.requestLatencyHistogram = Histogram.build()
                .buckets(0.1, 0.5, 1, 5)
                .name(REQUEST_LATENCY_METRICS)
                .help("Listener request latency in seconds")
                .labelNames(SERVICE_NAME_TAG)
                .register();

        this.pendingRequestGauge = Gauge.build().name(PENDING_REQUEST_METRICS)
                .help("Listener target service total pending requests.")
                .labelNames(SERVICE_NAME_TAG)
                .register();
        this.payloadCounter = Counter.build().name(PAYLOAD_SIZE_METRICS)
                .help("Listener payload bytes.")
                .labelNames(SERVICE_NAME_TAG)
                .register();
        this.gitShortCommitIdGauge = Gauge.build().name(GIT_INFO_METRICS)
                .help("Listener current git short commit id.")
                .labelNames(GIT_SHORT_COMMIT_ID_TAG)
                .register();
        this.containerConfigVersionGauge = Gauge.build().name(CONTAINER_CONFIG_VERSION_METRICS)
                .help("Listener current running container config version.")
                .register();
        this.monitorHttpServer = HttpServer.create();
        init(metricsConfig, executorService);
    }

    protected static boolean shouldUseCompression(HttpExchange exchange) {
        List<String> encodingHeaders = exchange.getRequestHeaders().get("Accept-Encoding");
        if (encodingHeaders == null) {
            return false;
        }

        for (String encodingHeader : encodingHeaders) {
            String[] encodings = encodingHeader.split(",");
            for (String encoding : encodings) {
                if (encoding.trim().toLowerCase().equals("gzip")) {
                    return true;
                }
            }
        }
        return false;
    }

    protected static Set<String> parseQuery(String query) throws IOException {
        Set<String> names = new HashSet<String>();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx != -1 && URLDecoder.decode(pair.substring(0, idx), "UTF-8").equals("name[]")) {
                    names.add(URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
                }
            }
        }
        return names;
    }

    @Override
    public void init(MetricsConfig metricsConfig, ExecutorService executorService) throws Exception {
        DefaultExports.initialize();
        monitorHttpServer.bind(new InetSocketAddress(metricsConfig.getPort()),
                metricsConfig.getConnectionBacklogSize());
        HttpHandler httpMetricHandler = new HttpMetricHandler(CollectorRegistry.defaultRegistry);
        monitorHttpServer.createContext("/", httpMetricHandler);
        monitorHttpServer.createContext("/metrics", httpMetricHandler);

        monitorHttpServer.setExecutor(executorService);
        executorService.submit(() -> {
            monitorHttpServer.start();
            log.info("The monitor server started successfully! bind to port [{}]  http://localhost:{}/"
                    , metricsConfig.getPort(), metricsConfig.getPort());
        });
    }

    @Override
    public void shutdown() {
        if (monitorHttpServer != null) {
            monitorHttpServer.stop(0);
        }
    }

    @Override
    public void incrementConnectionGauge() {
        connectionGauge.inc();
    }

    @Override
    public void decrementConnectionGauge() {
        connectionGauge.dec();
    }

    @Override
    public void incrementException(String serviceName) {
        exceptionCounter.labels(serviceName).inc();
    }

    @Override
    public void incrementRequestPendingGauge(String serviceName) {
        pendingRequestGauge.labels(serviceName).inc();
    }

    @Override
    public void decrementRequestPendingGauge(String serviceName) {
        pendingRequestGauge.labels(serviceName).dec();
    }

    @Override
    public void incrementRequestCounter(String serviceName, String innerStatus, String targetStatus) {
        requestCounter.labels(serviceName, innerStatus, targetStatus).inc();
    }

    @Override
    public void changeRequestLatencyHistogram(String serviceName, long latency) {
        requestLatencyHistogram.labels(serviceName)
                .observe(TimeUnit.MILLISECONDS.toSeconds(latency));
    }

    @Override
    public void incrementPayloadCounter(String serviceName, long amount) {
        payloadCounter.labels(serviceName).inc(amount);
    }

    @Override
    public void gitShortCommitIdGauge(String shortCommitId) {
        gitShortCommitIdGauge.labels(shortCommitId).set(1);
    }

    @Override
    public void containerConfigVersionGauge(int version) {
        containerConfigVersionGauge.set(version);
    }

    static class HttpMetricHandler implements HttpHandler {

        private final LocalByteArray response = new LocalByteArray();
        private CollectorRegistry registry;

        HttpMetricHandler(CollectorRegistry registry) {
            this.registry = registry;
        }

        @Override
        public void handle(HttpExchange t) throws IOException {
            String query = t.getRequestURI().getRawQuery();
            ByteArrayOutputStream response = this.response.get();
            response.reset();
            OutputStreamWriter osw = new OutputStreamWriter(response);
            TextFormat.write004(osw,
                    registry.filteredMetricFamilySamples(parseQuery(query)));
            osw.flush();
            osw.close();
            response.flush();
            response.close();

            t.getResponseHeaders().set("Content-Type",
                    TextFormat.CONTENT_TYPE_004);
            if (shouldUseCompression(t)) {
                t.getResponseHeaders().set("Content-Encoding", "gzip");
                t.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                final GZIPOutputStream os = new GZIPOutputStream(t.getResponseBody());
                response.writeTo(os);
                os.close();
            } else {
                t.getResponseHeaders().set("Content-Length",
                        String.valueOf(response.size()));
                t.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.size());
                response.writeTo(t.getResponseBody());
            }
            t.close();
        }

    }

    private static class LocalByteArray extends ThreadLocal<ByteArrayOutputStream> {
        @Override
        protected ByteArrayOutputStream initialValue() {
            return new ByteArrayOutputStream(1 << 20);
        }
    }

}

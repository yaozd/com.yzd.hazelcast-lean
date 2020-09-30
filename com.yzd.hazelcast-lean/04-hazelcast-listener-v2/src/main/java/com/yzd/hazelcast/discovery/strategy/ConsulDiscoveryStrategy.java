package com.yzd.hazelcast.discovery.strategy;

import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.discovery.AbstractDiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryNode;
import org.apache.commons.lang3.StringUtils;

import java.net.UnknownHostException;
import java.util.Map;

public class ConsulDiscoveryStrategy extends AbstractDiscoveryStrategy {

    private final Map<String, Comparable> properties;
    private final String consulUrl;
    private final String serviceName;
    private final int tll;
    private final String[] tags;
    private DiscoveryNode localDiscoveryNode;


    public ConsulDiscoveryStrategy(DiscoveryNode localDiscoveryNode, ILogger logger, Map<String, Comparable> properties) {
        super(logger, properties);
        this.localDiscoveryNode = localDiscoveryNode;
        this.properties = properties;
        this.consulUrl = properties.get(ConsulDiscoveryConfiguration.CONSUL_URL.key()).toString();
        this.serviceName = properties.get(ConsulDiscoveryConfiguration.CONSUL_SERVICE_NAME.key()).toString();
        this.tll = Integer.parseInt(properties.get(ConsulDiscoveryConfiguration.TLL.key()).toString());
        String tagsStr = properties.get(ConsulDiscoveryConfiguration.CONSUL_SERVICE_TAGS.key()).toString();
        this.tags = StringUtils.isBlank(tagsStr) ? null : tagsStr.split("|");

    }

    @Override
    public void start() {
        System.out.println("A1: Call start!");
        try {
            ConsulClient.getInstance().init(
                    consulUrl,
                    serviceName,
                    localDiscoveryNode.getPrivateAddress().getInetAddress().getHostAddress(),
                    localDiscoveryNode.getPrivateAddress().getPort(),
                    tll,
                    tags
            );
            ConsulClient.getInstance().register();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Iterable<DiscoveryNode> discoverNodes() {
        System.out.println("A2: Call discoverNodes!");
        /*List<DiscoveryNode> toReturn = new ArrayList<DiscoveryNode>();
        toReturn.add(localDiscoveryNode);
        return toReturn;*/
        return ConsulClient.getInstance().healthService();
    }

    @Override
    public void destroy() {
        System.out.println("A3: Call destroy!");
        ConsulClient.getInstance().deregister();
    }
}

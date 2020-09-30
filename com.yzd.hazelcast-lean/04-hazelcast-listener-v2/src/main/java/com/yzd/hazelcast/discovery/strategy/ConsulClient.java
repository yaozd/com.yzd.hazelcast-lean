package com.yzd.hazelcast.discovery.strategy;

import com.hazelcast.cluster.Address;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.SimpleDiscoveryNode;
import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.NotRegisteredException;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.agent.ImmutableRegCheck;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;
import com.orbitz.consul.model.health.ServiceHealth;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ConsulClient {
    public static final String SERVICE_CREATE_TIME = "[SCT:]";
    public static final String SERVICE_LAST_UPDATE_TIME = "[SLUT:]";
    private static ConsulClient ourInstance = new ConsulClient();
    private Consul consul;
    private String serviceName;
    private String serviceId;
    private String ip;
    private int port;
    private String[] tags;
    private int ttl;
    private ScheduledThreadPoolExecutor heartbeatService;
    private AgentClient agentClient;

    private ConsulClient() {

    }

    public static ConsulClient getInstance() {
        return ourInstance;
    }

    private AgentClient newAgentClient() {
        return consul.agentClient();
    }

    public void init(String url, String serviceName, String ip, int port, int ttl, String... tags) {
        this.consul = newConsul(url);
        this.agentClient = newAgentClient();
        this.serviceName = serviceName;
        this.ip = ip;
        this.port = port;
        this.tags = tags;
        this.ttl = ttl;
        this.serviceId = serviceName + "[" + ip + ":" + port + "]";
    }

    private Consul newConsul(String url) {
        Consul.Builder builder = Consul.builder().withUrl(url);
        return builder.build();
    }

    public void register() {

        Registration.RegCheck ttlCheck = ImmutableRegCheck.ttl(this.ttl);
        ImmutableRegistration.Builder registrationBuilder = ImmutableRegistration.builder()
                .name(this.serviceName)
                .id(this.serviceId)
                .address(this.ip)
                .port(this.port)
                //将创建时间打入到标签。在没有SLUT的时候，通过标签中的创建时间判断是否超时
                .addTags(SERVICE_CREATE_TIME + nowToStr())
                //.putMeta(SERVICE_CREATE_TIME, nowToStr())
                .addChecks(ttlCheck);
        if (tags != null) {
            registrationBuilder.addTags(tags);
        }
        agentClient.register(registrationBuilder.build());
        //超时时间为10S;
        boolean isPass = false;
        for (int i = 0; i < 50; i++) {
            boolean registered = agentClient.isRegistered(this.serviceId);
            if (registered) {
                pass();
                isPass = true;
                continue;
            }
            sleep();
        }
        if (isPass) {
            heartbeat();
            System.out.println("服务注册成功，启动心跳服务！");
        }
    }

    private void sleep() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void deregister() {
        // build my Consul agent client that we will register with
        agentClient.deregister(serviceId);
        System.out.println("服务注销成功！");
    }

    public void heartbeat() {
        this.heartbeatService = new ScheduledThreadPoolExecutor(1, r -> {
            Thread thread = new Thread(r, "T-Consul-Heartbeat");
            //thread.setDaemon(true);
            return thread;
        });
        int period = Math.max(1, (this.ttl - 2) / 2);
        this.heartbeatService.scheduleAtFixedRate(this::pass, 0, period, TimeUnit.SECONDS);
    }

    private void pass() {
        try {
            //Output:
            //增加一些附肋信息，帮助我们更好的判断当前状态发现的时间，做出正确的处理逻辑
            agentClient.pass(this.serviceId, SERVICE_LAST_UPDATE_TIME + nowToStr());
        } catch (NotRegisteredException e) {
            throw new RuntimeException(e);
        }
    }

    private String nowToStr() {
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     * 寻找有效的健康服务
     *
     * @throws UnknownHostException
     */
    public List<DiscoveryNode> healthService() {
        List<DiscoveryNode> toReturn = new ArrayList<DiscoveryNode>();
        HealthClient healthClient = consul.healthClient();
        ConsulResponse<List<ServiceHealth>> serviceInstances = healthClient.getHealthyServiceInstances(serviceName);
        List<ServiceHealth> serviceHealthList = serviceInstances.getResponse();
        try {
            for (ServiceHealth node : serviceHealthList) {
                toReturn.add(new SimpleDiscoveryNode(new Address(
                        node.getService().getAddress(),
                        node.getService().getPort())));
                System.out.println("Discovered healthy node: " + node.getService().getAddress() + ":" + node.getService().getPort());
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return toReturn;
    }
}

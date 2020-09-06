package com.yzd.discovery.consul;

import com.hazelcast.cluster.Address;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.SimpleDiscoveryNode;
import com.orbitz.consul.*;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.State;
import com.orbitz.consul.model.agent.*;
import com.orbitz.consul.model.health.HealthCheck;
import com.orbitz.consul.model.health.ServiceHealth;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ConsulClientTest {
    private String serviceName = "hazelcast-consul-discovery";
    private InetSocketAddress localAddress = randomAddress();
    private String serviceId = getServiceId(serviceName, localAddress);
    private String tags = "";

    /**
     * 服务：注册
     * 健康检查与注册是有时间间隔的。大约在3S左右（TCP的模式下）
     * //
     * 暂时推荐使用TTL模式，可能会减少时间间隔
     * @throws InterruptedException
     */
    @Test
    public void registerTest() throws InterruptedException, NotRegisteredException {
        //Build our consul client to use. We pass in optional TLS information
        Consul consul = newConsul();
        // build my Consul agent client that we will register with
        AgentClient agentClient = consul.agentClient();
        //通过TCP进行检查
        Registration.RegCheck tcpCheck = ImmutableRegCheck.tcp(
                this.localAddress.getAddress().getHostAddress() + ":" + this.localAddress.getPort(),
                10, 15);
        Registration.RegCheck ttlCheck = ImmutableRegCheck.ttl(10L);
        //
        ImmutableRegistration.Builder registrationBuilder = ImmutableRegistration.builder()
                .name(this.serviceName)
                .id(this.serviceId)
                .address(this.localAddress.getAddress().getHostAddress())
                .port(this.localAddress.getPort())
                //.check(regCheck)
                //.addChecks(tcpCheck)
                .addChecks(ttlCheck)
                .tags(Arrays.asList(this.tags));
        agentClient.register(registrationBuilder.build());
        //Check check= ImmutableCheck.builder().serviceId(this.serviceId).interval("5").tcp(this.localAddress.getAddress().getHostAddress() + ":" + this.localAddress.getPort()).build();
        //agentClient.registerCheck(check);
        boolean registered = agentClient.isRegistered(this.serviceId);
        System.out.println("registered:"+registered);
        //
        //Output:增加一些附肋信息，帮助我们更好的判断当前状态发现的时间，做出正确的处理逻辑
        agentClient.pass(this.serviceId, String.valueOf(System.currentTimeMillis()));
        //
//        for (int i = 0; i < 1000; i++) {
//            agentClient.register(registrationBuilder.build());
//            Thread.sleep(3000);
//        }
        //续租操作
        ScheduledExecutorService scheduledExecutor=new ScheduledThreadPoolExecutor(1);
        scheduledExecutor.scheduleAtFixedRate(()->{
            try {
                System.out.println("pass:"+this.serviceId);
                //Output:增加一些附肋信息，帮助我们更好的判断当前状态发现的时间，做出正确的处理逻辑
                agentClient.pass(this.serviceId,String.valueOf(System.currentTimeMillis()));
            } catch (NotRegisteredException e) {
                e.printStackTrace();
            }
        },0,2, TimeUnit.SECONDS);
    }

    private Consul newConsul() {
        Consul.Builder builder = Consul.builder().withUrl("http://127.0.0.1:8500/");
        return builder.build();
    }

    /**
     * 服务:注销
     */
    @Test
    public void deregisterTest() {
        String serviceId = "B";
        Consul consul = newConsul();
        // build my Consul agent client that we will register with
        AgentClient agentClient = consul.agentClient();
        agentClient.deregister(serviceId);
    }

    /**
     * TTL check:（Timeto Live生存时间）
     * 服务周期性汇报健康状态
     *
     * @throws NotRegisteredException
     * @throws InterruptedException
     */
    @Test
    public void servicePassTest() throws NotRegisteredException, InterruptedException {
        Consul consul = newConsul();
        for (int i = 0; i < 1000; i++) {
            Thread.sleep(10);
            consul.agentClient().pass("hazelcast-consul-discovery-127.0.0.1-53088");
        }

    }

    /**
     * 寻找有效的健康服务
     *
     * @throws UnknownHostException
     */
    @Test
    public void healthServiceTest() throws UnknownHostException {
        List<DiscoveryNode> toReturn = new ArrayList<DiscoveryNode>();
        Consul consul = newConsul();
        HealthClient healthClient = consul.healthClient();
        ConsulResponse<List<ServiceHealth>> healthyServiceInstances = healthClient.getHealthyServiceInstances(serviceName);
        List<ServiceHealth> serviceHealthList = healthyServiceInstances.getResponse();
        for (ServiceHealth node : serviceHealthList) {
            toReturn.add(new SimpleDiscoveryNode(
                    new Address(node.getService().getAddress(), node.getService().getPort())));
            System.out.println("Discovered healthy node: " + node.getService().getAddress() + ":" + node.getService().getPort());
        }
        ConsulResponse<List<ServiceHealth>> allServiceInstances = healthClient.getAllServiceInstances(serviceName);
        System.out.println("node size:" + allServiceInstances.getResponse().size());
        for (ServiceHealth node : allServiceInstances.getResponse()) {
            System.out.println("Discovered healthy node: " + node.getService().getAddress()
                    + ":" + node.getService().getPort());
            for (HealthCheck check : node.getChecks()) {
                boolean present = check.getServiceId().isPresent();
                if (node.getService().getId().equals(check.getServiceId().get())) {
                    System.out.println("health status:" + check.getStatus());
                }
            }
        }
    }

    /**
     * 管理nodes和services
     */
    @Test
    public void catalogTest() {
        Consul consul = newConsul();
        CatalogClient catalogClient = consul.catalogClient();
        ConsulResponse<Map<String, List<String>>> services = catalogClient.getServices();
        Map<String, List<String>> response = services.getResponse();
        for (Map.Entry<String, List<String>> entry : response.entrySet()) {
            System.out.println(entry.getKey() + " VALUE:" + entry.getValue().toString());
        }
    }

    private ServerSocket serverSocket;

    /**
     * 模拟动态服务：提供IP+动态端口
     *
     * @return
     */
    private InetSocketAddress randomAddress() {
        try {
            serverSocket = new ServerSocket(0);
            int localPort = serverSocket.getLocalPort();
            InetSocketAddress socketAddress =
                    new InetSocketAddress("127.0.0.1", localPort);
            System.out.println("random port:" + localPort);
            //serverSocket.close();
            return socketAddress;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private String getServiceId(String serviceName, InetSocketAddress localAddress) {
        String id = String.format("%s-%s-%d", serviceName, localAddress.getAddress().getHostAddress()
                , localAddress.getPort());
        System.out.println(id);
        return id;
    }
    /**
     * 删除所有无效的服务
     */
    @Test
    public void cleanAllInvalidServices() {
        System.out.println("***********************consul上无效服务清理开始*******************************************");
        //获取所有的services检查信息
        Consul consul = newConsul();
        Map<String, HealthCheck> serviceMap = consul.agentClient().getChecks();
        for (Map.Entry<String, HealthCheck> entry : serviceMap.entrySet()) {
            HealthCheck item4HealthCheck = entry.getValue();
            String serviceName = item4HealthCheck.getServiceName().get();
            String serviceId = item4HealthCheck.getServiceId().get();
            String status = item4HealthCheck.getStatus();
            //通过标签可以设置灰度识别，是否需要监控，监控的类型：M-JVM,M-REDIS,M-MYSQL
            String tags = item4HealthCheck.getServiceTags().toString();
            System.out.println(String.format("服务名称 :%s/服务ID:$s", serviceName, serviceId));
            System.out.println(String.format("服务 :%s的标签值：%s", serviceId, tags));
            //获取健康状态值  PASSING：正常  WARNING  CRITICAL  UNKNOWN：不正常
            System.out.println(String.format("服务 :%s的健康状态值：%s", serviceId, status));
            if (State.FAIL.getName().equals(item4HealthCheck.getStatus())) {
                System.out.println(String.format("服务名称 :%s/服务ID:%s,健康状态值：%s,为无效服务，准备清理...................",
                        serviceName, serviceId, status));
                //Output:增加一些附肋信息，帮助我们更好的判断当前状态发现的时间，做出正确的处理逻辑
                System.out.println("Output:"+item4HealthCheck.getOutput());
                //删除无效节点
                //consul.agentClient().deregister(serviceId);
            }
        }
    }

    @After
    public void end() throws InterruptedException {
        System.out.println("End!");
        Thread.currentThread().join();
    }

}

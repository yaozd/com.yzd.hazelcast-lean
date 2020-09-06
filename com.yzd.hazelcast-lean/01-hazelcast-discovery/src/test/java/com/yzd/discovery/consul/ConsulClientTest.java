package com.yzd.discovery.consul;

import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.agent.ImmutableRegCheck;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Arrays;

public class ConsulClientTest {
    private String serviceName = "hazelcast-consul-discovery";
    private InetSocketAddress localAddress = randomAddress();
    private String serviceId = getServiceId(serviceName, localAddress);
    private String tags = "";

    /**
     * 服务：注册
     *
     * @throws InterruptedException
     */
    @Test
    public void registerTest() throws InterruptedException {
        //Build our consul client to use. We pass in optional TLS information
        Consul consul = newConsul();
        // build my Consul agent client that we will register with
        AgentClient agentClient = consul.agentClient();
        //通过TCP进行检查
        Registration.RegCheck regCheck = ImmutableRegCheck.tcp(
                this.localAddress.getAddress().getHostAddress() + ":" + this.localAddress.getPort(),
                10, 15);
        //
        ImmutableRegistration.Builder registrationBuilder = ImmutableRegistration.builder()
                .name(this.serviceName)
                .id(this.serviceId)
                .address(this.localAddress.getAddress().getHostAddress())
                .port(this.localAddress.getPort())
                .check(regCheck)
                .tags(Arrays.asList(this.tags));
        //
        for (int i = 0; i < 1000; i++) {
            agentClient.register(registrationBuilder.build());
            Thread.sleep(3000);
        }
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

    private ServerSocket serverSocket;

    /**
     * 模拟动态服务：提供IP+动态端口
     *
     * @return
     */
    private InetSocketAddress randomAddress() {
        try {
            serverSocket = new ServerSocket(0);
            InetSocketAddress socketAddress =
                    new InetSocketAddress("127.0.0.1", serverSocket.getLocalPort());
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

    @After
    public void end() throws InterruptedException {
        System.out.println("End!");
        Thread.currentThread().join();
    }

}

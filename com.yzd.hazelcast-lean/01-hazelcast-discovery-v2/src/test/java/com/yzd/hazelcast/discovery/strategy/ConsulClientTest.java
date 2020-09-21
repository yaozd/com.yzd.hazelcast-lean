package com.yzd.hazelcast.discovery.strategy;

import com.orbitz.consul.Consul;
import com.orbitz.consul.NotRegisteredException;
import com.orbitz.consul.model.State;
import com.orbitz.consul.model.health.HealthCheck;
import com.orbitz.consul.model.health.Service;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;

public class ConsulClientTest {

    private int ttl = 12;

    @Before
    public void init() {
        ConsulClient.getInstance().init(
                "A",
                "127.0.0.1",
                80,
                11,
                "BJ"
        );
    }

    @After
    public void end() throws InterruptedException {
        Thread.currentThread().join();
    }

    @Test
    public void register() {
        ConsulClient.getInstance().register();

    }

    @Test
    public void deregister() {
        ConsulClient.getInstance().deregister();
    }

    @Test
    public void healthService() throws UnknownHostException {
        ConsulClient.getInstance().healthService();
    }



    /**
     * 推荐新版操作 -byArvin
     * 删除所有无效的服务
     *
     * @throws NotRegisteredException
     */
    @Test
    public void cleanAllInvalidServicesNew() throws NotRegisteredException {
        System.out.println("***********************consul上无效服务清理开始*******************************************");
        Date now = new Date();
        //获取所有的services检查信息
        Consul consul = Consul.builder().withUrl("http://127.0.0.1:8500/").build();
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
                System.out.println("Output:" + item4HealthCheck.getOutput());
                String lastUpdateTimeStr = StringUtils.substringAfterLast(
                        item4HealthCheck.getOutput().get(), ConsulClient.SERVICE_LAST_UPDATE_TIME);
                if (StringUtils.isNotBlank(lastUpdateTimeStr)) {
                    Date lastUpdateTime = new Date(Long.parseLong(lastUpdateTimeStr));
                    Date timeoutDate = DateUtils.addSeconds(lastUpdateTime, ttl);
                    if (now.after(timeoutDate)) {
                        System.out.println(ConsulClient.SERVICE_LAST_UPDATE_TIME+"超时！");
                        //删除无效节点
                        consul.agentClient().deregister(serviceId);
                    }
                } else if (!item4HealthCheck.getServiceTags().isEmpty()) {
                    for (String serviceTag : item4HealthCheck.getServiceTags()) {
                        String createTimeStr = StringUtils.substringAfterLast(serviceTag, ConsulClient.SERVICE_CREATE_TIME);
                        if(StringUtils.isNotBlank(createTimeStr)){
                            Date createTime=new Date(Long.parseLong(createTimeStr));
                            Date timeoutTime = DateUtils.addSeconds(createTime, ttl);
                            if(now.after(timeoutTime)){
                                System.out.println(ConsulClient.SERVICE_CREATE_TIME+"超时！");
                                //删除无效节点
                                consul.agentClient().deregister(serviceId);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 删除所有无效的服务
     */
    @Test
    public void cleanAllInvalidServices() throws NotRegisteredException {
        System.out.println("***********************consul上无效服务清理开始*******************************************");
        //获取所有的services检查信息
        Consul consul = Consul.builder().withUrl("http://127.0.0.1:8500/").build();
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
                System.out.println("Output:" + item4HealthCheck.getOutput());
                //删除无效节点
                //consul.agentClient().deregister(serviceId);
                //不存在时，再使用warn做二次验证即可！
                if (!item4HealthCheck.getOutput().isPresent()) {
                    consul.agentClient().warn(serviceId, String.valueOf(System.currentTimeMillis()));
                }
            }
            System.out.println("Output:" + item4HealthCheck.getOutput());
        }
        for (Map.Entry<String, Service> stringServiceEntry : consul.agentClient().getServices().entrySet()) {
            String createTime = stringServiceEntry.getValue().getMeta().get(ConsulClient.SERVICE_CREATE_TIME);
            System.out.println(ConsulClient.SERVICE_CREATE_TIME + ":" + createTime);
        }
    }
}
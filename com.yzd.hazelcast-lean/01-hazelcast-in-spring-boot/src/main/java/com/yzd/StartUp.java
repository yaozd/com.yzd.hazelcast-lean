package com.yzd;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Objects;
import java.util.Set;

/**
 * Created by gz on 2017/10/18
 */
@EnableCaching
@RestController
@SpringBootApplication
public class StartUp {

    private Logger LOGGER = LoggerFactory.getLogger(StartUp.class);

    public static void main(String[] args) {
        SpringApplication.run(StartUp.class, args);
    }

    @Bean
    public Config hazelCastConfig() {
        //如果有集群管理中心，可以配置
        ManagementCenterConfig managementCenterConfig = new ManagementCenterConfig();
        //centerConfig.setUrl("http://172.16.135.165:8200/mancenter");
        //centerConfig.setEnabled(true);
        return new Config()
                .setInstanceName("hazelcast-instance");
                //.setManagementCenterConfig(centerConfig)
//                .addMapConfig(
//                        new MapConfig()
//                                .setName("instruments")
//                                .setMaxSizeConfig(new MaxSizeConfig(200, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
//                                .setEvictionPolicy(EvictionPolicy.LRU)
//                                .setTimeToLiveSeconds(20000));
    }
    public void initConfig(){
        Config config=new Config()
                .setInstanceName("hazelcast-instance")
                .setClusterName("TIM")
                //网络配置
                .setNetworkConfig(new NetworkConfig())
                .addMapConfig(new MapConfig());
        //
        new MapConfig().setTimeToLiveSeconds(Integer.MAX_VALUE)
                .setEvictionConfig(new EvictionConfig().setEvictionPolicy(EvictionConfig.DEFAULT_EVICTION_POLICY));
        Hazelcast.getHazelcastInstanceByName("hazelcast-instance").getMap("").clear();
    }

    @GetMapping("/greet")
    public Object greet() {
        //实例
        //HazelcastInstance hazelcastInstanceByName = Hazelcast.getHazelcastInstanceByName("hazelcast-instance");
        //hazelcastInstanceByName.shutdown();
        Set<HazelcastInstance> allHazelcastInstances = Hazelcast.getAllHazelcastInstances();
        System.out.println(allHazelcastInstances.size());
        Object value = Hazelcast.getHazelcastInstanceByName("hazelcast-instance").getMap("instruments").get("hello");
        if (Objects.isNull(value)) {
            Hazelcast.getHazelcastInstanceByName("hazelcast-instance").getMap("instruments").put("hello", "world!");
        }
        LOGGER.info("从分布式缓存获取到 key=hello,value={}", value);
        //
        boolean contains = Hazelcast.getHazelcastInstanceByName("hazelcast-instance").getSet("instruments").contains("hello");
        LOGGER.info("set contain={}", contains);
        //
        return value;
    }
    @GetMapping("/shutdown")
    public Object shutdown(){
        Hazelcast.getHazelcastInstanceByName("hazelcast-instance").shutdown();
        return "shutdown";
    }
    @Autowired
    private DemoService demoService;

    @GetMapping("/cache")
    public Object cache() {
        String value = demoService.greet("hello");
        Hazelcast.getHazelcastInstanceByName("hazelcast-instance").getMap("instruments").put("hello",value);
        Hazelcast.getHazelcastInstanceByName("hazelcast-instance").getSet("instruments").add("hello");
        LOGGER.info("从分布式缓存获取到 key=hello,value={}", value);
        return value;
    }

    @GetMapping("/session")
    public Object session(HttpSession session) {
        String sessionId = session.getId();
        LOGGER.info("当前请求的sessionId={}", sessionId);
        return sessionId;
    }
}




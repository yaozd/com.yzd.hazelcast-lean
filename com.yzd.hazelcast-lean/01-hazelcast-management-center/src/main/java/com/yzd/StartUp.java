package com.yzd;

import com.yzd.utils.SocketUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: yaozh
 * @Description:
 */
@SpringBootApplication
public class StartUp {
    private Logger LOGGER = LoggerFactory.getLogger(StartUp.class);

    public static void main(String[] args) {
        SpringApplication.run(StartUp.class, args);
    }
   /* @Bean
    public Config hazelCastConfig() {
        //如果有集群管理中心，可以配置
        ManagementCenterConfig centerConfig = new ManagementCenterConfig();
        centerConfig.setUrl("http://localhost:8080/mancenter");
        centerConfig.setEnabled(true);
        ListenerConfig listenerConfig = new ListenerConfig();
        listenerConfig.setImplementation(new ClusterMembershipListener());
        return new Config()
                .setInstanceName("hazelcast-instance")
                .addListenerConfig(listenerConfig)
                .setManagementCenterConfig(centerConfig)
                .addMapConfig(
                        new MapConfig()
                                .setName("instruments")
                                .setMaxSizeConfig(new MaxSizeConfig(200, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
                                .setEvictionPolicy(EvictionPolicy.LRU)
                                .setTimeToLiveSeconds(20000));
    }*/
}

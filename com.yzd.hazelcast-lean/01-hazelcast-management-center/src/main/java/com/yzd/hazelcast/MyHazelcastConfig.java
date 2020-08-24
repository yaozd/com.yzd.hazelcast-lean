package com.yzd.hazelcast;

import com.hazelcast.config.*;
import com.yzd.utils.SocketUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import static com.yzd.hazelcast.MyHazelcast.GRPC_PORT_ATTRIBUTE;

/**
 * @Author: yaozh
 * @Description:
 */
@Component
@Configuration
public class MyHazelcastConfig {
    @Bean
    public Config hazelCastConfig() {
        //如果有集群管理中心，可以配置
        ManagementCenterConfig centerConfig = new ManagementCenterConfig();
        centerConfig.setUrl("http://localhost:8080/mancenter");
        centerConfig.setEnabled(true);
        ListenerConfig listenerConfig = new ListenerConfig();
        listenerConfig.setImplementation(new ClusterMembershipListener());
        Config config= new Config()
                .setInstanceName("hazelcast-instance")
                .addListenerConfig(listenerConfig)
                .setManagementCenterConfig(centerConfig)
                .addMapConfig(
                        new MapConfig()
                                .setName("instruments")
                                .setMaxSizeConfig(new MaxSizeConfig(200, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
                                .setEvictionPolicy(EvictionPolicy.LRU)
                                .setTimeToLiveSeconds(20000));
        //
        //grpc 数据推送端口
        MemberAttributeConfig memberAttributeConfig = new MemberAttributeConfig();
        memberAttributeConfig.setIntAttribute(GRPC_PORT_ATTRIBUTE, SocketUtil.getAvailablePort());
        config.setMemberAttributeConfig(memberAttributeConfig);
        return config;
    }
}

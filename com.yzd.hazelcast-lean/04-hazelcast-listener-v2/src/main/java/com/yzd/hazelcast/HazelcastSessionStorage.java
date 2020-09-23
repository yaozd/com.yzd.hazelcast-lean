package com.yzd.hazelcast;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.yzd.config.SessionConfig;
import com.yzd.hazelcast.discovery.strategy.ConsulDiscoveryConfiguration;
import com.yzd.hazelcast.discovery.strategy.ConsulDiscoveryStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.UUID;

/**
 * @Author: yaozh
 * @Description:
 */
@Slf4j
public class HazelcastSessionStorage implements SessionStorage {

    private static final String INSTANCE_NAME = "yzd";

    public HazelcastSessionStorage(SessionConfig sessionConfig) {
        Config conf = new ClasspathXmlConfig("hazelcast-consul-discovery-spi-example.xml");
        initDiscoveryConfig(sessionConfig, conf);
        conf.setInstanceName(INSTANCE_NAME);
        //
        String SET_ID = "setId";
        String setUUID = UUID.randomUUID().toString();
        SetConfig setConfig = new SetConfig();
        setConfig.setName(setUUID);
        //备份的数量。如果1设置为备份数量，也就是说为了安全将map上的所有条目复制到另一个JVM上
        //0表示没有备份。
        setConfig.setBackupCount(0);
        //statistics-enabled: 一些统计数据就像等待操作数,操作数,操作完成,取消操作数可以通过设置该参数的值是真来进行检索，
        //检索统计数据的方法是getLocalExecutorStats()。
        setConfig.setStatisticsEnabled(false);
        conf.addSetConfig(setConfig);
        MemberAttributeConfig memberAttributeConfig = new MemberAttributeConfig();
        memberAttributeConfig.setAttribute(SET_ID, setUUID);
        conf.setMemberAttributeConfig(memberAttributeConfig);
        //
        conf.getManagementCenterConfig().setScriptingEnabled(false);
        conf.getPartitionGroupConfig().setEnabled(false);
        conf.getMetricsConfig().getManagementCenterConfig().setEnabled(false);
        //
        Hazelcast.newHazelcastInstance(conf);
    }

    private void initDiscoveryConfig(SessionConfig sessionConfig, Config conf) {
        DiscoveryConfig discoveryConfig = conf.getNetworkConfig().getJoin().getDiscoveryConfig();
        if (!discoveryConfig.isEnabled()) {
            return;
        }
        Collection<DiscoveryStrategyConfig> discoveryStrategyConfigs = discoveryConfig.getDiscoveryStrategyConfigs();
        String consulDiscoveryStrategyClassName = ConsulDiscoveryStrategy.class.getName();
        for (DiscoveryStrategyConfig discoveryStrategyConfig : discoveryStrategyConfigs) {
            if (consulDiscoveryStrategyClassName.equals(discoveryStrategyConfig.getClassName())) {
                discoveryStrategyConfig.
                        addProperty(ConsulDiscoveryConfiguration.CONSUL_URL.key(), sessionConfig.getConsulUrl());
                discoveryStrategyConfig.
                        addProperty(ConsulDiscoveryConfiguration.CONSUL_SERVICE_NAME.key(), sessionConfig.getServiceName());
                discoveryStrategyConfig.
                        addProperty(ConsulDiscoveryConfiguration.TLL.key(), sessionConfig.getTll().toString());
                discoveryStrategyConfig.
                        addProperty(ConsulDiscoveryConfiguration.CONSUL_SERVICE_TAGS.key(), sessionConfig.getTags());
            }
        }

    }
}

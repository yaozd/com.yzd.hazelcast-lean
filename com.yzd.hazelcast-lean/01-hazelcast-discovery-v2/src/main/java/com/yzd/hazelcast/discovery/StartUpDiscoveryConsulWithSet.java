package com.yzd.hazelcast.discovery;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.MemberAttributeConfig;
import com.hazelcast.config.SetConfig;
import com.hazelcast.core.Hazelcast;

import java.util.UUID;

/**
 * @Author: yaozh
 * @Description:
 */
public class StartUpDiscoveryConsulWithSet {
    private static final String INSTANCE_NAME = "yzd";

    public static void main(String[] args) throws InterruptedException {
        Config conf = new ClasspathXmlConfig("hazelcast-consul-discovery-spi-example.xml");
        conf.setInstanceName(INSTANCE_NAME);
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
        Thread.currentThread().join();
    }
}

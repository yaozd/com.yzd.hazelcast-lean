package com.yzd;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MemberGroupConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;

/**
 * 服务发现
 * https://github.com/bitsofinfo/hazelcast-etcd-discovery-spi
 * https://github.com/chouyua0912/zookeeper-etcd-hazelcast
 * @Author: yaozh
 * @Description:
 */
public class HazelcastNetworkConfig {
    //https://www.jianshu.com/p/3ab011f44445
    //HazelCast有三种组网协议，MultiCast,TcpIp以及Aws三种,API默认使用MultiCast协议来组网
    //@Bean
    //@PostConstruct
    public HazelcastInstance hazelcastInstance(){
        Config cfg=new Config();
        MemberGroupConfig memberGroupConfig=new MemberGroupConfig();
        JoinConfig join = cfg.getNetworkConfig().getJoin();
        join.getMulticastConfig().setEnabled(false);
        TcpIpConfig tcpIpConfig=join.getTcpIpConfig();
        tcpIpConfig.setEnabled(true);
        tcpIpConfig.addMember("192.168.253.85");
        tcpIpConfig.addMember("172.16.216.127-130");
        tcpIpConfig.setRequiredMember(null);
        HazelcastInstance instance= Hazelcast.newHazelcastInstance(cfg);
        return instance;
    }
}

package com.yzd;

import com.hazelcast.config.Config;
import com.hazelcast.config.MemberAttributeConfig;
import com.hazelcast.config.SetConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ISet;
import com.hazelcast.core.Member;

import java.util.Set;
import java.util.UUID;

/**
 * Hazelcast配置文档（完整）
 * https://my.oschina.net/vdroid/blog/754882
 */
public class MyHazelcast {
    private static final String INSTANCE_NAME = "yzd";
    private static final String SET_ID = "setId";
    private static MyHazelcast ourInstance = new MyHazelcast();
    private final String setUUID;
    private Config hazelcastConfig;

    private MyHazelcast() {
        hazelcastConfig = new Config();
        hazelcastConfig.setInstanceName(INSTANCE_NAME);
        setUUID = UUID.randomUUID().toString();
    }

    public static MyHazelcast getInstance() {
        return ourInstance;
    }

    public void init() {

        SetConfig setConfig = new SetConfig();
        setConfig.setName(setUUID);
        //备份的数量。如果1设置为备份数量，也就是说为了安全将map上的所有条目复制到另一个JVM上
        //0表示没有备份。
        setConfig.setBackupCount(0);
        //statistics-enabled: 一些统计数据就像等待操作数,操作数,操作完成,取消操作数可以通过设置该参数的值是真来进行检索，
        //检索统计数据的方法是getLocalExecutorStats()。
        setConfig.setStatisticsEnabled(false);
        hazelcastConfig.addSetConfig(setConfig);
        MemberAttributeConfig memberAttributeConfig = new MemberAttributeConfig();
        memberAttributeConfig.setStringAttribute(SET_ID, setUUID);
        hazelcastConfig.setMemberAttributeConfig(memberAttributeConfig);
        Hazelcast.newHazelcastInstance(hazelcastConfig);

    }

    public void write() {
        getSet().add(UUID.randomUUID().toString());
    }

    private ISet<Object> getSet() {
        return getHazelcastInstance().getSet(setUUID);
    }

    public void checkOtherSet() {
        Set<Member> members = getHazelcastInstance().getCluster().getMembers();
        for (Member member : members) {
            String uuid = member.getStringAttribute(SET_ID);
            System.out.println("SET_ID:" + uuid + "   size:" + getHazelcastInstance().getSet(uuid).size());
        }
    }

    private HazelcastInstance getHazelcastInstance() {
        return Hazelcast.getHazelcastInstanceByName(INSTANCE_NAME);
    }

    public int size() {
        return getSet().size();
    }
}

package com.yzd.hazelcast;

import com.hazelcast.cluster.Member;
import com.hazelcast.collection.ISet;
import com.hazelcast.config.*;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.yzd.config.SessionConfig;
import com.yzd.hazelcast.discovery.strategy.ConsulDiscoveryConfiguration;
import com.yzd.hazelcast.discovery.strategy.ConsulDiscoveryStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @Author: yaozh
 * @Description:
 */
@Slf4j
public class HazelcastSessionStorage implements SessionStorage {

    private final static String GRPC_PORT_ATTRIBUTE = "grpc_port_key";
    private final String SET_ID_ATTRIBUTE = "set_id_key";
    private final String localSetName;
    private final String instanceName;
    private final HazelcastInstance instance;
    /**
     * 本地节点set对象
     */
    private final ISet<String> localSet;
    private final SessionInfo localSessionInfo;
    /**
     * 外部节点set对象集合
     */
    private List<ISet<String>> outDistributedSetObjects = new ArrayList<>();

    private List<SessionInfo> sessionInfoList = new ArrayList<>();

    public HazelcastSessionStorage(SessionConfig sessionConfig) {
        this.instanceName = sessionConfig.getServiceName();
        Config conf = new ClasspathXmlConfig("hazelcast-consul-discovery-spi-example.xml");
        initDiscoveryConfig(sessionConfig, conf);
        conf.setInstanceName(instanceName);
        //
        localSetName = UUID.randomUUID().toString();
        SetConfig setConfig = new SetConfig();
        setConfig.setName(localSetName);
        //备份的数量。如果1设置为备份数量，也就是说为了安全将map上的所有条目复制到另一个JVM上
        //0表示没有备份。
        setConfig.setBackupCount(0);
        //statistics-enabled: 一些统计数据就像等待操作数,操作数,操作完成,取消操作数可以通过设置该参数的值是真来进行检索，
        //检索统计数据的方法是getLocalExecutorStats()。
        setConfig.setStatisticsEnabled(false);
        conf.addSetConfig(setConfig);
        MemberAttributeConfig memberAttributeConfig = new MemberAttributeConfig();
        memberAttributeConfig.setAttribute(SET_ID_ATTRIBUTE, localSetName);
        memberAttributeConfig.setAttribute(GRPC_PORT_ATTRIBUTE, "6501");
        conf.setMemberAttributeConfig(memberAttributeConfig);
        //
        conf.getManagementCenterConfig().setScriptingEnabled(false);
        conf.getPartitionGroupConfig().setEnabled(false);
        conf.getMetricsConfig().getManagementCenterConfig().setEnabled(false);
        //
        ListenerConfig listenerConfig = new ListenerConfig();
        listenerConfig.setImplementation(new ClusterMembershipListener(this));
        conf.addListenerConfig(listenerConfig);
        //
        this.instance = Hazelcast.newHazelcastInstance(conf);
        this.localSet = this.instance.getSet(localSetName);
        Member localMember = this.instance.getCluster().getLocalMember();
        this.localSessionInfo = newSessionInfo(localMember);
    }

    private SessionInfo newSessionInfo(Member member) {
        return new SessionInfo().setMemberId(getMemberId(member))
                .setIp(member.getSocketAddress().getHostString())
                .setPort(member.getSocketAddress().getPort())
                .setSetid(member.getAttribute(SET_ID_ATTRIBUTE))
                .setGrpc(Integer.parseInt(member.getAttribute(GRPC_PORT_ATTRIBUTE)));
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

    @Override
    public String getMemberId(Member member) {
        return String.format("%s|%d|%s", member.getSocketAddress().getHostString(),
                member.getSocketAddress().getPort(), member.getUuid());
    }

    @Override
    public void destroyDistributedObjectsByMemberId(String memberId) {
        Collection<DistributedObject> distributedObjects = getDistributedObjects();
        for (DistributedObject distributedObject : distributedObjects) {
            if (distributedObject.getName().equals(memberId)) {
                distributedObject.destroy();
            }
        }
    }

    @Override
    public void update() {
        List<SessionInfo> tempSessionInfos = new ArrayList<>();
        tempSessionInfos.add(localSessionInfo);
        addSessionInfosOfOutMember(tempSessionInfos);
        this.sessionInfoList = tempSessionInfos;
    }

    @Override
    public SessionInfo getSessionInfo(String uuid) {
        for (SessionInfo sessionInfo : sessionInfoList) {
            ISet<Object> set = instance.getSet(sessionInfo.getSetid());
            if(set!=null&&set.contains(uuid)){
                return sessionInfo;
            }
        }
        return null;
    }

    private void addSessionInfosOfOutMember(List<SessionInfo> tempSessionInfos) {
        if (instance == null) {
            return;
        }
        Set<Member> allMembers = instance.getCluster().getMembers();
        for (Member member : allMembers) {
            if (member.localMember()) {
                continue;
            }
            tempSessionInfos.add(newSessionInfo(member));
        }
    }

    private void setOutDistributedSetObjects(List<ISet<String>> outDistributedSetObjects) {
        this.outDistributedSetObjects = outDistributedSetObjects;
    }

    private Collection<DistributedObject> getDistributedObjects() {
        return instance.getDistributedObjects();
    }

    /**
     * 关闭并清空本机数据
     */
    @Override
    public void shutdown() {
        if(instance==null){
            return;
        }
        instance.getSet(localSetName).destroy();
        instance.shutdown();
    }

}

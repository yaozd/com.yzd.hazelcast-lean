package com.yzd.hazelcast;

import com.hazelcast.core.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @Author: yaozh
 * @Description:
 */
@Slf4j
public class MyHazelcast {
    public static final String INSTANCE_NAME = "hazelcast-instance";
    /**
     * grpc 数据推送端口
     */
    public static final String GRPC_PORT_ATTRIBUTE = "grpc-port";
    private static String localMemberId;
    private static List<ISet<String>> distributedSetObjects = new ArrayList<>();

    public static String getLocalMemberId() {
        if (localMemberId != null) {
            return localMemberId;
        }
        Member localMember = getLocalMember();
        localMemberId = getMemberId(localMember);
        return localMemberId;
    }

    public static String getMemberId(Member member) {
        return String.format("%s|%d|%s", member.getSocketAddress().getHostString(),
                member.getSocketAddress().getPort(), member.getUuid());
    }

    public static ISet<Object> getInstrumentsSet() {
        return Hazelcast.getHazelcastInstanceByName(INSTANCE_NAME).getSet(getLocalMemberId());
    }

    public static IMap<Object, Object> getInstrumentsMap() {
        return Hazelcast.getHazelcastInstanceByName(INSTANCE_NAME).getMap(getLocalMemberId());
    }

    public static ReplicatedMap<Object, Object> getInstrumentsReplicatedMap() {
        return Hazelcast.getHazelcastInstanceByName(INSTANCE_NAME).getReplicatedMap(getLocalMemberId());
    }

    public static Collection<DistributedObject> getDistributedObjects() {
        return Hazelcast.getHazelcastInstanceByName(INSTANCE_NAME).getDistributedObjects();
    }

    public static void destroyDistributedObjectsByMemberId(String memberId) {
        Collection<DistributedObject> distributedObjects = getDistributedObjects();
        for (DistributedObject distributedObject : distributedObjects) {
            if (distributedObject.getName().equals(memberId)) {
                distributedObject.destroy();
            }
        }
    }

    /**
     * 关闭并清空本机数据
     */
    public static void closeLocalMember() {
        getInstrumentsMap().destroy();
        getInstrumentsReplicatedMap().destroy();
        getInstrumentsSet().destroy();
        Hazelcast.getHazelcastInstanceByName(INSTANCE_NAME).shutdown();
    }

    private static Member getLocalMember() {
        return Hazelcast.getHazelcastInstanceByName(INSTANCE_NAME).getCluster().getLocalMember();
    }

    public static void update() {
        Map<String, Member> allMemberMap = new HashMap<>();
        List<ISet<String>> setList = new ArrayList<>();
        Set<Member> allMembers = Hazelcast.getHazelcastInstanceByName(INSTANCE_NAME).getCluster().getMembers();
        for (Member member : allMembers) {
            allMemberMap.put(getMemberId(member), member);
            setList.add(Hazelcast.getHazelcastInstanceByName(INSTANCE_NAME).getSet(getMemberId(member)));
            Integer grpcPort = member.getIntAttribute(GRPC_PORT_ATTRIBUTE);
            log.warn("grpc port:{}",grpcPort);
        }
        setDistributedSetObjects(setList);
    }

    public static List<ISet<String>> getDistributedSetObjects() {
        return distributedSetObjects;
    }

    private static void setDistributedSetObjects(List<ISet<String>> distributedSetObjects) {
        MyHazelcast.distributedSetObjects = distributedSetObjects;
    }
}

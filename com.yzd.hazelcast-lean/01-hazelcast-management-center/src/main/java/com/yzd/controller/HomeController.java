package com.yzd.controller;

import com.hazelcast.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.UUID;

/**
 * @Author: yaozh
 * @Description:
 */
@RestController
public class HomeController {
    private Logger LOGGER = LoggerFactory.getLogger(HomeController.class);
    private static final String KEY = "key";
    public static final String EMPTY_VALUE = "";

    @GetMapping("/getValue")
    public Object getValue() {
        int mapSize = getInstrumentsMap().size();
        int replicatedMapSize = getInstrumentsReplicatedMap().size();
        int setSize = getInstrumentsSet().size();
        String message = String.format("分布式缓存获取到 map size=%d,replicated map size=%d,set size=%d",
                mapSize, replicatedMapSize, setSize);
        LOGGER.info(message);
        return message;
    }

    @GetMapping("/setValue")
    public Object setValue() {
        String value = UUID.randomUUID().toString();
        getInstrumentsMap().put(value, EMPTY_VALUE);
        getInstrumentsReplicatedMap().put(value, EMPTY_VALUE);
        getInstrumentsSet().add(value);
        return value;
    }
    @GetMapping("/deleteValue")
    public Object deleteValue() {
        getInstrumentsMap().destroy();
        getInstrumentsReplicatedMap().destroy();
        getInstrumentsSet().destroy();
        return "destroy";
    }
    @GetMapping("/distributedObjects")
    public Object distributedObjects(){
        Collection<DistributedObject> distributedObjects = getDistributedObjects();
        for (DistributedObject distributedObject : distributedObjects) {
            LOGGER.info(distributedObject.toString());
        }
        return "getDistributedObjects";
    }

    private ISet<Object> getInstrumentsSet() {
        return Hazelcast.getHazelcastInstanceByName("hazelcast-instance").getSet("instruments");
    }

    private IMap<Object, Object> getInstrumentsMap() {
        return Hazelcast.getHazelcastInstanceByName("hazelcast-instance").getMap("instruments");
    }

    private ReplicatedMap<Object, Object> getInstrumentsReplicatedMap() {
        return Hazelcast.getHazelcastInstanceByName("hazelcast-instance").getReplicatedMap("instruments");
    }
    private void getLocalMember(){
        Member localMember = Hazelcast.getHazelcastInstanceByName("hazelcast-instance").getCluster().getLocalMember();
    }

    private Collection<DistributedObject> getDistributedObjects() {
        return Hazelcast.getHazelcastInstanceByName("hazelcast-instance").getDistributedObjects();
    }
}

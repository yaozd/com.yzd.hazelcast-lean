package com.yzd.controller;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.ISet;
import com.yzd.hazelcast.MyHazelcast;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * @Author: yaozh
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("h")
public class HazelcastController {
    private static final String EMPTY_VALUE = "";

    @GetMapping("/getValue")
    public Object getValue() {
        int mapSize = MyHazelcast.getInstrumentsMap().size();
        int replicatedMapSize = MyHazelcast.getInstrumentsReplicatedMap().size();
        int setSize = MyHazelcast.getInstrumentsSet().size();
        String message = String.format("分布式缓存获取到 map size=%d,replicated map size=%d,set size=%d",
                mapSize, replicatedMapSize, setSize);
        log.info(message);
        return message;
    }

    @GetMapping("/setValue")
    public Object setValue() {
        String value = UUID.randomUUID().toString();
        log.info(value);
        MyHazelcast.getInstrumentsMap().put(value, EMPTY_VALUE);
        MyHazelcast.getInstrumentsReplicatedMap().put(value, EMPTY_VALUE);
        MyHazelcast.getInstrumentsSet().add(value);
        return value;
    }

    @GetMapping("/deleteValue")
    public Object deleteValue() {
        MyHazelcast.getInstrumentsMap().destroy();
        MyHazelcast.getInstrumentsReplicatedMap().destroy();
        MyHazelcast.getInstrumentsSet().destroy();
        return "destroy";
    }

    @GetMapping("/getDistributedObjects")
    public Object getDistributedObjects() {
        log.info("Distributed objects :");
        Collection<DistributedObject> distributedObjects = MyHazelcast.getDistributedObjects();
        for (DistributedObject distributedObject : distributedObjects) {
            log.warn(distributedObject.toString());
        }
        return "getDistributedObjects";
    }

    @GetMapping("/getDistributedSetObjects")
    public Object getDistributedSetObjects() {
        log.info("GetDistributedSetObjects :");
        List<ISet<String>> distributedSetObjects = MyHazelcast.getDistributedSetObjects();
        for (ISet<String> distributedSetObject : distributedSetObjects) {
            log.warn(distributedSetObject.toString());
        }
        return "getDistributedSetObjects";
    }

    @GetMapping("/containsValueInDistributedSetObject")
    public Object containsValueInDistributedSetObject(String uuid) {
        if (StringUtils.isEmpty(uuid)) {
            return false;
        }
        log.info("containsValueInDistributedSetObject :");
        List<ISet<String>> distributedSetObjects = MyHazelcast.getDistributedSetObjects();
        for (ISet<String> distributedSetObject : distributedSetObjects) {
            log.warn(distributedSetObject.getName());
            if (distributedSetObject.contains(uuid)) {
                log.info("true  ,uuid:{}",uuid);
                return true;
            }
        }
        return false;
    }

    @GetMapping("/closeLocalMember")
    public Object closeLocalMember() {
        MyHazelcast.closeLocalMember();
        return "closeLocalMember";
    }
}

package com.yzd.transfer;

import com.yzd.hazelcast.NodeInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: yaozh
 * @Description:
 */
@Slf4j
public class TransferClientManager {
    private Map<String, TransferClient> clientMap = new ConcurrentHashMap<>();

    public TransferClient getClient(String memberId) {
        return clientMap.get(memberId);
    }

    public void updateClient(List<NodeInfo> memberInfos) {
        //add
        for (NodeInfo memberInfo : memberInfos) {
            clientMap.putIfAbsent(memberInfo.getMemberId(), newTransferClient(memberInfo));
        }
        //remove
        Iterator<Map.Entry<String, TransferClient>> iterator = clientMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, TransferClient> entry = iterator.next();
            boolean noneMatch = memberInfos.stream().noneMatch(m -> m.getMemberId().equals(entry.getKey()));
            if (noneMatch) {
                entry.getValue().shutdown();
                iterator.remove();
            }
        }
    }

    private TransferClient newTransferClient(NodeInfo memberInfo) {
        log.warn("transfer server ip:{} , port:{}", memberInfo.getIp(), memberInfo.getGrpcPort());
        return new TransferClient(memberInfo);
    }

    public void shutdown() {
        updateClient(Collections.emptyList());
        log.info("Shutdown transfer client manager success!");
    }
}

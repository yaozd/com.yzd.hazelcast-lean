package com.yzd.transfer;

import com.yzd.hazelcast.NodeInfo;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: yaozh
 * @Description:
 */
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
            boolean noneMatch = memberInfos.stream().noneMatch(m -> m.getMemberId() == entry.getKey());
            if (noneMatch) {
                entry.getValue().shutdown();
                iterator.remove();
            }
        }
    }

    private TransferClient newTransferClient(NodeInfo memberInfo) {
        return new TransferClient(memberInfo);
    }
}

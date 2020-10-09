package com.yzd.transfer;

import com.yzd.hazelcast.NodeInfo;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: yaozh
 * @Description:
 */
public class TransferClientManagerTest {

    TransferClientManager transferClientManager = new TransferClientManager();
    private String memberId = "192.168.56.1|5701|dfd2f365-9805-4589-9f46-07af0aabaab8";

    @Before
    public void init() {
        List<NodeInfo> memberInfos = new ArrayList<>();
        NodeInfo member = new NodeInfo();
        member.setGrpcPort(1001);
        member.setIp("127.0.0.1");
        member.setMemberId(memberId);
        memberInfos.add(member);
        transferClientManager.updateClient(memberInfos);
    }

    @Test
    public void getClient() {
        TransferClient client = transferClientManager.getClient(memberId);
        String uuid = "fd23d4f6-dbbe-474b-925f-16bae912dc70";
        client.call(uuid, 200, uuid + System.currentTimeMillis());
    }

}
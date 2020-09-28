package com.yzd.hazelcast;

import com.hazelcast.cluster.Member;

import java.util.Set;

/**
 * @Author: yaozh
 * @Description:
 */
public interface SessionStorage {
    /**
     * member id
     * @param member
     * @return
     */
    String getMemberId(Member member);

    /**
     * destroy set
     * @param memberId
     */
    void destroyDistributedObjectsByMemberId(String memberId);

    /**
     * update
     */
    void update();

    SessionInfo getSessionInfo(String uuid);

    void shutdown();
}

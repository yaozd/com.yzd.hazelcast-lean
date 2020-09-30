package com.yzd.hazelcast;

import com.hazelcast.cluster.Member;

/**
 * @Author: yaozh
 * @Description:
 */
public interface SessionStorage {
    /**
     * member id
     *
     * @param member
     * @return
     */
    String getMemberId(Member member);

    /**
     * destroy set
     *
     * @param memberId
     */
    void destroyDistributedObjectsByMemberId(String memberId);

    /**
     * update
     */
    void update();

    SessionInfo getSessionInfo(String uuid);

    void addSessionId(String uuid);

    void removeSessionId(String uuid);

    void shutdown();

    int size();
}

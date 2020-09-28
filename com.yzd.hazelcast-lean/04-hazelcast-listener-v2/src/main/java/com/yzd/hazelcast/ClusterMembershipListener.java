package com.yzd.hazelcast;


import com.hazelcast.cluster.Member;
import com.hazelcast.cluster.MembershipEvent;
import com.hazelcast.cluster.MembershipListener;
import lombok.extern.slf4j.Slf4j;

/**
 * 【Hazelcast系列十一】分布式事件
 * https://www.jianshu.com/p/6fba643313b5
 * PS:使用上面的方法配置监听器有一点不足：创建实例和注册监听之间的事件可能丢失。为了解决这个问题，Hazelcast支持在配置中配置监听器。
 *
 * @Author: yaozh
 * @Description:
 */
@Slf4j
public class ClusterMembershipListener implements MembershipListener {
    private final SessionStorage MyHazelcast;

    public ClusterMembershipListener(HazelcastSessionStorage hazelcastSessionStorage) {
        this.MyHazelcast = hazelcastSessionStorage;
    }

    @Override
    public void memberAdded(MembershipEvent membershipEvent) {
        //MembershipEvent.MEMBER_ADDED
        log.warn("member id:{};type:{}", MyHazelcast.getMemberId(membershipEvent.getMember()), membershipEvent.getEventType());
        log.warn(membershipEvent.toString());
        if (MembershipEvent.MEMBER_REMOVED == membershipEvent.getEventType()) {
            MyHazelcast.destroyDistributedObjectsByMemberId(MyHazelcast.getMemberId(membershipEvent.getMember()));
        }
        for (Member member : membershipEvent.getMembers()) {
            log.warn(member.toString());
        }
        MyHazelcast.update();
    }

    @Override
    public void memberRemoved(MembershipEvent membershipEvent) {
        log.warn(membershipEvent.toString());
        for (Member member : membershipEvent.getMembers()) {
            log.warn(member.toString());
        }
        MyHazelcast.update();
    }

}

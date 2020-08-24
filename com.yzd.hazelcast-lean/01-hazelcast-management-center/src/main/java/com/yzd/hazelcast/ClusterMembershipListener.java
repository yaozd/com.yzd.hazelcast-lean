package com.yzd.hazelcast;

import com.hazelcast.core.Member;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * 【Hazelcast系列十一】分布式事件
 *  https://www.jianshu.com/p/6fba643313b5
 *  PS:使用上面的方法配置监听器有一点不足：创建实例和注册监听之间的事件可能丢失。为了解决这个问题，Hazelcast支持在配置中配置监听器。
 * @Author: yaozh
 * @Description:
 */
public class ClusterMembershipListener implements MembershipListener {
    private Logger LOGGER = LoggerFactory.getLogger(ClusterMembershipListener.class);
    @Override
    public void memberAdded(MembershipEvent membershipEvent) {
        //MembershipEvent.MEMBER_ADDED
        LOGGER.warn("member id:{};type:{}",MyHazelcast.getMemberId(membershipEvent.getMember()),membershipEvent.getEventType());
        LOGGER.warn(membershipEvent.toString());
        if(MembershipEvent.MEMBER_REMOVED==membershipEvent.getEventType()){
            MyHazelcast.destroyDistributedObjectsByMemberId(MyHazelcast.getMemberId(membershipEvent.getMember()));
        }
        for (Member member : membershipEvent.getMembers()) {
            LOGGER.warn(member.toString());
        }
        MyHazelcast.update();
    }

    @Override
    public void memberRemoved(MembershipEvent membershipEvent) {
        LOGGER.warn(membershipEvent.toString());
        for (Member member : membershipEvent.getMembers()) {
            LOGGER.warn(member.toString());
        }
        MyHazelcast.update();
    }

    @Override
    public void memberAttributeChanged(MemberAttributeEvent memberAttributeEvent) {
        LOGGER.info(memberAttributeEvent.toString());
    }
}

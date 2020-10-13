package com.yzd.t4;

import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.List;

/**
 * @Author: yaozh
 * @Description:
 */
public class MessageConsumer implements MessageListenerConcurrently {
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        if (CollectionUtils.isEmpty(msgs)) {
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }

        MessageExt message = msgs.get(0);
        int reconsumeTimes = message.getReconsumeTimes();
        System.out.println("打印当前重试次数:" + reconsumeTimes);
        try {
            //逐条消费
            String messageBody = new String(message.getBody(), RemotingHelper.DEFAULT_CHARSET);
            System.err.println("Message Consumer: Handle New Message: messageId: " + message.getMsgId() + ",topic: " +
                    message.getTopic() + ",tags: " + message.getTags() + ",messageBody: " + messageBody);

            //模拟耗时操作2分钟，大于设置的消费超时时间
            //Thread.sleep(1000L * 60 * 2);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            //log.error("Consume Message Error!!", e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }

}

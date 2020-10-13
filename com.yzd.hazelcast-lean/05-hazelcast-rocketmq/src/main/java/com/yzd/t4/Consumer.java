package com.yzd.t4;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

/**
 * 生产者，可以直接使用t1中的Producer
 *
 * @Author: yaozh
 * @Description:
 */
public class Consumer {
    public static void main(String[] args) throws InterruptedException, MQClientException {

        // Instantiate with specified consumer group name.
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("rename_unique_group_name");

        // Specify name server addresses.
        consumer.setNamesrvAddr("127.0.0.1:9876");

        // Subscribe one more more topics to consume.
        consumer.subscribe("CONSUMER-CONTACT-TOPIC", "*");
        //从消息队列头部开始消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        //设置集群消费模式
        consumer.setMessageModel(MessageModel.CLUSTERING);
        //消息广播：broadcasting ,官网描述很清晰：
        //Broadcasting is sending a message to all subscribers of a topic.
        //If you want all subscribers receive messages about a topic, broadcasting is a good choice.
        //就是发送方发消息时，所有订阅该topic的消费者都会收到这个消息。
        //consumer.setMessageModel(MessageModel.BROADCASTING);

        //设置消费超时时间(分钟)
        consumer.setConsumeTimeout(15);
        // Register callback to execute on arrival of messages fetched from brokers.
        consumer.registerMessageListener(new MessageConsumer());

        //Launch the consumer instance.
        consumer.start();
        consumer.getAwaitTerminationMillisWhenShutdown();
        System.out.printf("Consumer Started.%n");
    }
}

package com.yzd.t1;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;
/**
 * @Author: yaozh
 * @Description:
 */
public class Consumer {
    private static final String TASK_ID_NAME = "T_ID";
    public static void main(String[] args) throws InterruptedException, MQClientException {

        // Instantiate with specified consumer group name.
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("rename_unique_group_name");

        // Specify name server addresses.
        consumer.setNamesrvAddr("127.0.0.1:9876");

        // Subscribe one more more topics to consume.
        consumer.subscribe("CONSUMER-CONTACT-TOPIC", "*");
        // Register callback to execute on arrival of messages fetched from brokers.
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                            ConsumeConcurrentlyContext context) {
                System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
                for (Message msg : msgs) {
                    String taskId = msg.getProperty(TASK_ID_NAME);
                    String jsonStr = new String(msg.getBody());
                    System.out.println("receive is " + jsonStr);
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        //Launch the consumer instance.
        consumer.start();
        consumer.getAwaitTerminationMillisWhenShutdown();
        System.out.printf("Consumer Started.%n");
    }
}

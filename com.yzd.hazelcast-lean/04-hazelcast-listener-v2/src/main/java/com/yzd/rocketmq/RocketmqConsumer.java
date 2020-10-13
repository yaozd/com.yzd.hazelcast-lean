package com.yzd.rocketmq;

import com.yzd.hazelcast.NodeInfo;
import com.yzd.hazelcast.SessionStorage;
import com.yzd.internal.Container;
import com.yzd.transfer.TransferClient;
import com.yzd.transfer.TransferClientManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * 需要调整重试时间间隔，加快消费
 * # RocketMQ可在broker.conf文件中配置Consumer端的重试次数和重试时间间隔，如下：
 * messageDelayLevel = 1s 1s 1s 1s 1s 1s 1s 2s 3s 4s 5s
 * @Author: yaozh
 * @Description:
 */
@Slf4j
public class RocketmqConsumer {
    private static final String TASK_ID_NAME = "T_ID";
    private final TransferClientManager transferClientManager;
    private final SessionStorage sessionStorage;

    public RocketmqConsumer(Container container) {
        this.transferClientManager = container.getTransferClientManager();
        this.sessionStorage = container.getSessionStorage();
        //
        try {
            start();
        } catch (MQClientException e) {
            throw new IllegalStateException(e);
        }
    }

    public void start() throws MQClientException {
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
                log.info("Receive New Messages:{},current thread name:{}", msgs, Thread.currentThread().getName());
                for (MessageExt msg : msgs) {
                    //当前消息被重试次数
                    if(msg.getReconsumeTimes()>3){
                        log.warn("ID:{},ReconsumeTimes {},fail!",msg.getMsgId(),msg.getReconsumeTimes());
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }
                    String taskId = msg.getProperty(TASK_ID_NAME);
                    String jsonStr = new String(msg.getBody());
                    log.info("receive is " + jsonStr);
                    boolean isOk = doWork(taskId, jsonStr);
                    log.info("ID:{},IS_OK:{}", taskId, isOk);
                    //一般重复16次 10s、30s、1分钟、2分钟、3分钟等等 ,可根据具体业务再调整即可
                    //如果MQ中消息在请求之前到达，则需要通过重试的方式。
                    if (!isOk) {
                        //返回消费状态
                        //CONSUME_SUCCESS 消费成功
                        //RECONSUME_LATER 消费失败，需要稍后重新消费
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }

                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        //Launch the consumer instance.
        consumer.start();
    }

    private boolean doWork(String taskId, String body) {
        if (StringUtils.isBlank(taskId)) {
            return false;
        }
        NodeInfo nodeInfoBySessionId = sessionStorage.findNodeInfoBySessionId(taskId);
        if (nodeInfoBySessionId == null) {
            return false;
        }
        TransferClient client = transferClientManager.getClient(nodeInfoBySessionId.getMemberId());
        if (client == null) {
            return false;
        }
        return client.call(taskId, 200, body);
    }
}

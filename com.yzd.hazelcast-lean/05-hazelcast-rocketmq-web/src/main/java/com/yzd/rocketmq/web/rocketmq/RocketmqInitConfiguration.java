package com.yzd.rocketmq.web.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: yaozh
 * @Description:
 */

@Slf4j
@Configuration
public class RocketmqInitConfiguration {

    @Bean
    public DefaultMQProducer defaultMQProducer() throws MQClientException {
        //Instantiate with a producer group name.
        DefaultMQProducer producer = new
                DefaultMQProducer("TEST_PRODUCER_GROUP");
        // Specify name server addresses.
        producer.setNamesrvAddr("127.0.0.1:9876");
        //Launch the instance.
        producer.start();
        log.info("Rocketmq producer server is starting....");
        return producer;
    }
}

package com.yzd.rocketmq.web.controller;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.*;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @Author: yaozh
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("user")
public class UserController {
    private static final String TASK_ID_NAME = "T_ID";
    private final ExecutorService executor = newThreadPoolExecutor();
    @Autowired
    DefaultMQProducer mqProducer;

    @GetMapping("get")
    public Object get() {
        String taskId = UUID.randomUUID().toString();
        return sendTaskMq(taskId);
    }

    /**
     * 异步发送任务消息
     * eg :ExecutorService executorService=Executors.newFixedThreadPool(1);
     * @return
     */
    private ExecutorService newThreadPoolExecutor() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("demo-pool-%d").build();
        //Common Thread Pool
        return new ThreadPoolExecutor(1, 1, 0L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1024),
                namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    @GetMapping("newTask")
    public void newTask(HttpServletResponse response) throws IOException {
        String taskId = UUID.randomUUID().toString();
        executor.execute(() ->
                sendTaskMq(taskId)
        );
        /**response.sendRedirect("https://www.baidu.com?UUDI=" + taskId);**/
        response.sendRedirect("http://localhost:1000/?uuid=" + taskId);
    }

    private SendResult sendTaskMq(String taskId) {
        Message msg = new Message("CONSUMER-CONTACT-TOPIC",
                null,
                ("Hello RocketMQ [TID:"+taskId+"]").getBytes(UTF_8)
        );
        msg.putUserProperty(TASK_ID_NAME, taskId);
        try {
            return mqProducer.send(msg);
        } catch (MQClientException | RemotingException | MQBrokerException e) {
            throw newIllegalStateException(e);
        } catch (InterruptedException e) {
            log.warn("Interrupted!", e);
            Thread.currentThread().interrupt();
        }
        throw new IllegalStateException("Send task mq fail!");
    }

    private IllegalStateException newIllegalStateException(Throwable cause) {
        return new IllegalStateException(cause);
    }
}

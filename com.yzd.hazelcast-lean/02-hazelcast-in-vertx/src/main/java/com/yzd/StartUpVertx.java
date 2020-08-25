package com.yzd;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 更多请阅读：https://www.yiibai.com/spring-boot/non-web-application-example.html
 * Spring Boot非Web项目运行的方法
 * https://www.jb51.net/article/169744.htm
 * @Autowired出现Field injection is not recommended警告
 * https://blog.csdn.net/ccr1001ccr1001/article/details/88954216
 * @author yaozh
 */
@SpringBootApplication
@EnableScheduling
public class StartUpVertx implements CommandLineRunner {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(StartUpVertx.class, args);
    }


    /**
     * access command line arguments
     * 此时按照原先的方式启动 SpringBootApplication 会发现启动加载完之后会立即退出，
     * 这时需要做点工作让主线程阻塞让程序不退出
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        //do something
        //Thread.currentThread().join();
    }
}



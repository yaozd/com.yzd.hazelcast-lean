package com.yzd.auto.test;

import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: yaozh
 * @Description:
 */
public class ThreadLocalRandomTest {
    @Test
    public void nextIntTest() {
        while (true) {
            int i = ThreadLocalRandom.current().nextInt(0, 200_000);
            System.out.println(i);
            if(i==200_000){
                return;
            }
        }
    }
}

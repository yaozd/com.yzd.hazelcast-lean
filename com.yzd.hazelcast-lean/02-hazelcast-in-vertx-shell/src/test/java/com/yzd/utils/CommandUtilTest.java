package com.yzd.utils;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import static org.junit.Assert.*;

public class CommandUtilTest {

    @Test
    public void runCmd() throws InterruptedException {
        try {
            // 执行ping命令
            //Process process = Runtime.getRuntime().exec("cmd /c e:&dir");
            Process process = Runtime.getRuntime().exec("java");
            process.waitFor();
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("GBK")));
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void runCmdNew() {
        String result = CommandUtil.runCmdNew("java -version");
        System.out.println(result);
    }
}
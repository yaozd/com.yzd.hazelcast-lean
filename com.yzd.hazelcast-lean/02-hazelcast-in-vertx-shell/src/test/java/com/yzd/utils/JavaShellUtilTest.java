package com.yzd.utils;

import org.junit.Test;

import java.io.IOException;

public class JavaShellUtilTest {

    @Test
    public void executeShell() throws IOException {

        JavaShellUtil javaShellUtil = new JavaShellUtil();
        //参数为要执行的Shell命令，即通过调用Shell脚本sendKondorFile.sh
        //将/temp目录下的tmp.pdf文件发送到192.168.1.200上
        int success = javaShellUtil.executeShell("sh /tmp/sendKondorFile.sh /temp tmp.pdf");
    }
}
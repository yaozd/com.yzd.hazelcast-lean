package com.yzd.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class CommandUtilTest {

    @Test
    public void runCmd() {

    }

    @Test
    public void runCmdNew() {
        String result = CommandUtil.runCmdNew("ipconfig ");
        System.out.println(result);
    }
}
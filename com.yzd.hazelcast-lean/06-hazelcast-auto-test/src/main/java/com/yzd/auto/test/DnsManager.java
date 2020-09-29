package com.yzd.auto.test;

import com.alibaba.dcm.DnsCacheManipulator;

/**
 * @Author: yaozh
 * @Description:
 */
public class DnsManager {
    public static void loadDnsCache(){
        DnsCacheManipulator.setDnsCache("www.hello.com", "192.168.1.1");
        DnsCacheManipulator.setDnsCache("www.world.com", "1234:5678:0:0:0:0:0:200e");
    }
}

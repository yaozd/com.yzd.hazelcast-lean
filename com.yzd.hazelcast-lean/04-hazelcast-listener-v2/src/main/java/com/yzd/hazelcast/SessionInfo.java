package com.yzd.hazelcast;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @Author: yaozh
 * @Description:
 */
@Accessors(chain = true)
@Getter
@Setter
public class SessionInfo {
    private String ip;
    private int port;
    private int grpc;
    private String memberId;
    private String setid;
}

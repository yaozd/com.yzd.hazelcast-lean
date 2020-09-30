package com.yzd.config.internal;

import com.yzd.utils.SocketUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: yaozh
 * @Description:
 */
@Getter
@Setter
public class TransferConfig {
    private Integer port = SocketUtil.getRandomPort();
}

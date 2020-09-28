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
public class RouterConfig {
    private Integer port= SocketUtil.getRandomPort();
}

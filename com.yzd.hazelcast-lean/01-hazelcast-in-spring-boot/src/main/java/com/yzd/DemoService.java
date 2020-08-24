package com.yzd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @Author: yaozh
 * @Description:
 */
@Service
@CacheConfig(cacheNames = "instruments")
class DemoService {

    private Logger LOGGER = LoggerFactory.getLogger(DemoService.class);

    public String greet(String key) {
        LOGGER.info("缓存内没有取到key={}", key);
        return "world！";
    }
}


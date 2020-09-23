package com.yzd.hazelcast.discovery.strategy;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.config.properties.PropertyTypeConverter;
import com.hazelcast.config.properties.SimplePropertyDefinition;

/**
 * @Author: yaozh
 * @Description:
 */
public class ConsulDiscoveryConfiguration {
    public static final PropertyDefinition CONSUL_URL =
            new SimplePropertyDefinition("consul-url", PropertyTypeConverter.STRING);
    public static final PropertyDefinition CONSUL_SERVICE_NAME =
            new SimplePropertyDefinition("consul-service-name", PropertyTypeConverter.STRING);
    public static final PropertyDefinition TLL =
            new SimplePropertyDefinition("consul-service-tll", PropertyTypeConverter.INTEGER) ;
    public static final PropertyDefinition CONSUL_SERVICE_TAGS =
            new SimplePropertyDefinition("consul-service-tags", PropertyTypeConverter.STRING);
}

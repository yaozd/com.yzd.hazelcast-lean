package com.yzd.hazelcast.discovery.strategy;

import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.DiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryStrategyFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class ConsulDiscoveryStrategyFactory implements DiscoveryStrategyFactory {
    private static final Collection<PropertyDefinition> PROPERTIES =
            Arrays.asList(new PropertyDefinition[]{
                    ConsulDiscoveryConfiguration.CONSUL_URL,
                    ConsulDiscoveryConfiguration.CONSUL_SERVICE_NAME,
                    ConsulDiscoveryConfiguration.TLL,
                    ConsulDiscoveryConfiguration.CONSUL_SERVICE_TAGS
            });

    @Override
    public Class<? extends DiscoveryStrategy> getDiscoveryStrategyType() {
        // Returns the actual class type of the DiscoveryStrategy
        // implementation, to match it against the configuration
        return ConsulDiscoveryStrategy.class;
    }

    @Override
    public DiscoveryStrategy newDiscoveryStrategy(DiscoveryNode discoveryNode,
                                                  ILogger logger, Map<String, Comparable> properties) {
        return new ConsulDiscoveryStrategy(discoveryNode, logger, properties);
    }

    @Override
    public Collection<PropertyDefinition> getConfigurationProperties() {
        //return Collections.EMPTY_LIST;
        return PROPERTIES;
    }
}

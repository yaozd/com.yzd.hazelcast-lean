package com.yzd.discovery.consul;

import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.discovery.AbstractDiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConsulDiscoveryStrategy extends AbstractDiscoveryStrategy {

    private DiscoveryNode localDiscoveryNode;


    public ConsulDiscoveryStrategy(DiscoveryNode localDiscoveryNode, ILogger logger, Map<String, Comparable> properties) {
        super(logger, properties);
        this.localDiscoveryNode = localDiscoveryNode;
    }

    @Override
    public Iterable<DiscoveryNode> discoverNodes() {
        System.out.println("A1: Call discoverNodes!");
        List<DiscoveryNode> toReturn = new ArrayList<DiscoveryNode>();
        toReturn.add(localDiscoveryNode);
        return toReturn;
    }

    @Override
    public void start() {
        System.out.println("A2: Call start!");
    }

    @Override
    public void destroy() {
        System.out.println("A3: Call destroy!");
    }
}

package com.yzd.hazelcast.discovery.strategy;

import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.discovery.AbstractDiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryNode;

import java.net.UnknownHostException;
import java.util.Map;

public class ConsulDiscoveryStrategy extends AbstractDiscoveryStrategy {

    private DiscoveryNode localDiscoveryNode;


    public ConsulDiscoveryStrategy(DiscoveryNode localDiscoveryNode, ILogger logger, Map<String, Comparable> properties) {
        super(logger, properties);
        this.localDiscoveryNode = localDiscoveryNode;
    }

    @Override
    public void start() {
        System.out.println("A1: Call start!");
        try {
            ConsulClient.getInstance().init(
                    "H-C",
                    localDiscoveryNode.getPrivateAddress().getInetAddress().getHostAddress(),
                    localDiscoveryNode.getPrivateAddress().getPort(),
                    12,
                    "bj"
            );
            ConsulClient.getInstance().register();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Iterable<DiscoveryNode> discoverNodes() {
        System.out.println("A2: Call discoverNodes!");
        /*List<DiscoveryNode> toReturn = new ArrayList<DiscoveryNode>();
        toReturn.add(localDiscoveryNode);
        return toReturn;*/
        return ConsulClient.getInstance().healthService();
    }

    @Override
    public void destroy() {
        System.out.println("A3: Call destroy!");
        ConsulClient.getInstance().deregister();
    }
}

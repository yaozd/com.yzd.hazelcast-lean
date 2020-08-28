package com.yzd.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yaozh
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "listener.metrics")
public class MetricsConfig {

	private int port = 9311;

	private int connectionBacklogSize = 100;

	private int executorInitSize = 3;

	private int executorMaxSize = 10;

}

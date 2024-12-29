package com.mmieczkowski.serverguard.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "clickhouse")
public record ClickHouseProperties(String url, String jdbcUrl, String username, String password) {

}

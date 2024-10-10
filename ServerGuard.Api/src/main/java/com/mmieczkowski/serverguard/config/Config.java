package com.mmieczkowski.serverguard.config;

import com.clickhouse.client.api.Client;
import com.mmieczkowski.serverguard.config.properties.ClickHouseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaAuditing
@EnableTransactionManagement
@EnableConfigurationProperties(ClickHouseProperties.class)
public class Config {

    @Bean
    public Client chDirectClient(ClickHouseProperties properties) {
        return new Client.Builder()
                .addEndpoint(properties.url())
                .setUsername(properties.username())
                .setPassword(properties.password())
                .useNewImplementation(true)
                .setMaxConnections(100)
                .setLZ4UncompressedBufferSize(1058576)
                .setSocketRcvbuf(500_000)
                .setSocketTcpNodelay(true)
                .setSocketSndbuf(500_000)
                .setClientNetworkBufferSize(500_000)
                .build();
    }
}

package com.mmieczkowski.serverguard.config;

import com.clickhouse.client.api.Client;
import com.mmieczkowski.serverguard.config.properties.ClickHouseProperties;
import com.mmieczkowski.serverguard.config.properties.SmtpProperties;
import com.mmieczkowski.serverguard.config.properties.WebProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.Clock;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

@Configuration
@EnableCaching
@EnableJpaAuditing
@EnableTransactionManagement
@EnableScheduling
@EnableConfigurationProperties({ ClickHouseProperties.class, SmtpProperties.class, WebProperties.class })
public class Config {

    @Bean
    @Profile({"production", "development"})
    public Client clickHouseClient(ClickHouseProperties properties) {
        return new Client.Builder()
                .addEndpoint(properties.url())
                .setUsername(properties.username())
                .setPassword(properties.password())
                .setMaxConnections(100)
                .setLZ4UncompressedBufferSize(1058576)
                .setSocketRcvbuf(500_000)
                .setSocketTcpNodelay(true)
                .setSocketSndbuf(500_000)
                .setClientNetworkBufferSize(500_000)
                .useNewImplementation(true)
                .setSocketTimeout(10, ChronoUnit.SECONDS)
                .build();
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public JavaMailSender mailSender(SmtpProperties properties) {
        var mailSender = new JavaMailSenderImpl();
        var props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        mailSender.setJavaMailProperties(props);
        mailSender.setHost(properties.host());
        mailSender.setPort(properties.port());
        mailSender.setUsername(properties.username());
        mailSender.setPassword(properties.password());
        return mailSender;
    }
}

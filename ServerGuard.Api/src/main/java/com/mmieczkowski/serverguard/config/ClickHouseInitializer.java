package com.mmieczkowski.serverguard.config;

import com.clickhouse.client.api.Client;
import com.mmieczkowski.serverguard.config.properties.ClickHouseProperties;
import com.mmieczkowski.serverguard.metric.model.AvailableMetric;
import com.mmieczkowski.serverguard.metric.model.Metric;
import jakarta.annotation.PostConstruct;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"production", "development"})
public class ClickHouseInitializer {

    private final ClickHouseProperties clickHouseProperties;
    private final Client client;

    public ClickHouseInitializer(ClickHouseProperties clickHouseProperties, Client client) {
        this.clickHouseProperties = clickHouseProperties;
        this.client = client;
    }


    @PostConstruct
    public void setup() {
        migrate();
        setupClient();
    }

    private void setupClient() {
        client.ping();
        var metricSchema = client.getTableSchema("metric");
        var availableMetricSchema = client.getTableSchema("available_metric");
        client.register(Metric.class, metricSchema);
        client.register(AvailableMetric.class, availableMetricSchema);
    }

    private void migrate() {
        Flyway flyway = Flyway.configure()
                .dataSource(clickHouseProperties.jdbcUrl(), clickHouseProperties.username(), clickHouseProperties.password())
                .locations("classpath:clickhouse/migration/")
                .baselineOnMigrate(true)
                .load();
        flyway.migrate();
    }
}

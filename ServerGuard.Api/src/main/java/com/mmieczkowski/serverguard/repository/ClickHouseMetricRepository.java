package com.mmieczkowski.serverguard.repository;

import com.clickhouse.client.api.Client;
import com.mmieczkowski.serverguard.model.Metric;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ClickHouseMetricRepository implements MetricRepository {
    private final Client client;

    @PostConstruct
    public void setup(){
        client.ping();
        var tableSchema = client.getTableSchema("metric");
        client.register(Metric.class, tableSchema);
    }

    @Override
    public void saveAll(List<Metric> metrics) {
        try {
            client.insert("metric", metrics);
        } catch (Exception e) {
            log.error("Error saving metrics to ClickHouse", e);
        }
    }
}

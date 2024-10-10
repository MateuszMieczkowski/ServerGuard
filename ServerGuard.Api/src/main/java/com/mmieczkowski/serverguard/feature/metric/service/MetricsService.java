package com.mmieczkowski.serverguard.feature.metric.service;

import com.mmieczkowski.serverguard.feature.metric.collect.Metrics;
import com.mmieczkowski.serverguard.feature.metric.collect.SensorMetric;
import com.mmieczkowski.serverguard.model.Agent;
import com.mmieczkowski.serverguard.model.Metric;
import com.mmieczkowski.serverguard.repository.AgentRepository;
import com.mmieczkowski.serverguard.repository.MetricRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MetricsService {

    private final AgentRepository agentRepository;
    private final MetricRepository metricRepository;

    public void collectMetrics(String apiKey, Metrics metrics) {
        Agent agent = agentRepository.findAgentByAgentConfigApiKey(apiKey)
                .orElseThrow(() -> new BadCredentialsException("Invalid API key"));
        List<Metric> metricsToInsert = new ArrayList<>();
        for(SensorMetric sensorMetric : metrics.sensorMetrics()) {
            for(com.mmieczkowski.serverguard.feature.metric.collect.Metric metric : sensorMetric.metrics()) {
                if(metric.value() == null) {
                    continue;
                }
                var newMetric = new com.mmieczkowski.serverguard.model.Metric(agent.getId(),
                        LocalDateTime.ofInstant(metrics.time(), ZoneId.of("UTC")),
                        sensorMetric.name(),
                        metric.name(),
                        metric.value(),
                        metric.type());
                metricsToInsert.add(newMetric);
            }
        }
        metricRepository.saveAll(metricsToInsert);

    }
}

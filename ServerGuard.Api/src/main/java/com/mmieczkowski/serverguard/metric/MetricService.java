package com.mmieczkowski.serverguard.metric;

import com.mmieczkowski.serverguard.agent.exception.AgentNotFoundException;
import com.mmieczkowski.serverguard.metric.response.GetAvailableMetricsResponse;
import com.mmieczkowski.serverguard.metric.model.AvailableMetric;
import com.mmieczkowski.serverguard.metric.model.Metric;
import com.mmieczkowski.serverguard.metric.model.MetricType;
import com.mmieczkowski.serverguard.metric.request.SaveMetricsRequest;
import com.mmieczkowski.serverguard.agent.model.Agent;
import com.mmieczkowski.serverguard.agent.AgentRepository;
import com.mmieczkowski.serverguard.resourcegroup.exception.ResourceGroupNotFoundException;
import com.mmieczkowski.serverguard.service.UserService;
import com.mmieczkowski.serverguard.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class MetricService {

    private final AgentRepository agentRepository;
    private final MetricRepository metricRepository;
    private final UserService userService;
    private final Clock clock;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public void collectMetrics(String apiKey, SaveMetricsRequest saveMetricsRequest) {
        Agent agent = agentRepository.findAgentByAgentConfigApiKey(apiKey)
                .orElseThrow(() -> new BadCredentialsException("Invalid API key"));
        List<Metric> metricsToInsert = new ArrayList<>();
        agent.setLastContactAt(clock.instant());
        agentRepository.save(agent);

        for (SaveMetricsRequest.SensorMetric sensorMetric : saveMetricsRequest.sensorMetrics()) {
            for (SaveMetricsRequest.SensorMetric.Metric metric : sensorMetric.metrics()) {
                var newMetric = new Metric(agent.getId(),
                        LocalDateTime.ofInstant(saveMetricsRequest.time(), ZoneId.of("UTC")),
                        sensorMetric.name(),
                        metric.name(),
                        metric.value(),
                        metric.type());
                metricsToInsert.add(newMetric);
            }
        }
        metricRepository.saveAll(metricsToInsert);
        try{
            updateAvailableMetrics(agent.getId(), metricsToInsert);
        } catch (Exception e) {
            log.error("Failed to update available metrics", e);
        }
        String destination = String.format("/topic/agents/%s/metrics", agent.getId());
        simpMessagingTemplate.convertAndSend(destination, saveMetricsRequest);
    }

    private void updateAvailableMetrics(UUID agentId, List<Metric> newMetrics) {
        List<AvailableMetric> existingMetrics = metricRepository.findAvailableMetricsByAgentId(agentId);
        var newAvailableMetrics = newMetrics.stream()
                .map(metric -> new AvailableMetric(
                        agentId,
                        metric.getSensorName(),
                        metric.getMetricName(),
                        MetricType.values()[metric.getType() - 1]))
                .toList();

        var metricsToAdd = newAvailableMetrics.stream()
                .filter(newMetric -> existingMetrics.stream()
                        .noneMatch(existingMetric -> existingMetric.getSensorName().equals(newMetric.getSensorName())
                                && existingMetric.getMetricName().equals(newMetric.getMetricName())
                                && existingMetric.getType() == newMetric.getType()))
                .toList();
        if(metricsToAdd.isEmpty()) {
            return;
        }
        metricRepository.saveAvailableMetrics(agentId, metricsToAdd);
    }

    public GetAvailableMetricsResponse getAvailableMetrics(UUID resourceGroupId, UUID agentId) {
        User user = userService.getLoggedInUser()
                .orElseThrow();
        if (!user.hasAccessToResourceGroup(resourceGroupId)) {
            throw new ResourceGroupNotFoundException(resourceGroupId);
        }
        Agent agent = agentRepository.findAgentByIdAndUserId(agentId, user.getId())
                .orElseThrow(AgentNotFoundException::new);

        List<AvailableMetric> availableMetrics = metricRepository.findAvailableMetricsByAgentId(agent.getId());
        Map<String, List<AvailableMetric>> metricsBySensor = availableMetrics.stream()
                .collect(Collectors.groupingBy(AvailableMetric::getSensorName));

        List<GetAvailableMetricsResponse.Sensor> sensors = metricsBySensor.entrySet().stream()
                .map(entry -> {
                    String sensorName = entry.getKey();
                    List<AvailableMetric> sensorMetrics = entry.getValue();
                    Map<String, List<Integer>> metricsByName = sensorMetrics.stream()
                            .collect(Collectors.groupingBy(
                                    AvailableMetric::getMetricName,
                                    Collectors.mapping(AvailableMetric::getType,
                                            Collectors.toList())));

                    List<GetAvailableMetricsResponse.Sensor.Metric> metrics = metricsByName
                            .entrySet().stream()
                            .map(metricEntry -> new GetAvailableMetricsResponse.Sensor.Metric(
                                    metricEntry.getKey(),
                                    metricEntry.getValue().stream()
                                            .map(x -> new GetAvailableMetricsResponse.Sensor.Metric.MetricType(
                                                    MetricType.values()[x
                                                            - 1]
                                                            .name(),
                                                    MetricType.values()[x
                                                            - 1]
                                                            .getUnit()))
                                            .collect(Collectors.toList())))
                            .collect(Collectors.toList());

                    return new GetAvailableMetricsResponse.Sensor(sensorName, metrics);
                })
                .collect(Collectors.toList());

        return new GetAvailableMetricsResponse(sensors);
    }
}

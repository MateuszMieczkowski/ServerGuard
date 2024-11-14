package com.mmieczkowski.serverguard.metric;

import com.mmieczkowski.serverguard.metric.request.SaveMetricsRequest;
import com.mmieczkowski.serverguard.metric.response.GetAvailableMetricsResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/resourceGroups/{resourceGroupId}/agents/{agentId}/metrics")
public class MetricController {

    private final MetricService metricService;

    @PostMapping
    public void collectMetrics(@RequestHeader("x-api-key") String apiKey,
                               @PathVariable UUID resourceGroupId,
                               @PathVariable UUID agentId,
                               @RequestBody @Valid SaveMetricsRequest saveMetricsRequest) {
        metricService.collectMetrics(apiKey, saveMetricsRequest);
    }

    @GetMapping("/availableMetrics")
    public GetAvailableMetricsResponse getAvailableMetrics(@PathVariable UUID resourceGroupId, @PathVariable UUID agentId) {
        return metricService.getAvailableMetrics(resourceGroupId, agentId);
    }
}

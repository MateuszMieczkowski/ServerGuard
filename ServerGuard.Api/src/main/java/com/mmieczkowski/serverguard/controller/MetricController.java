package com.mmieczkowski.serverguard.controller;

import com.mmieczkowski.serverguard.feature.metric.collect.Metrics;
import com.mmieczkowski.serverguard.feature.metric.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/metrics")
public class MetricController {

    private final MetricsService integrationService;

    @PostMapping
    public void collectMetrics(@RequestHeader("x-api-key") String apiKey, @RequestBody Metrics metrics) {
        integrationService.collectMetrics(apiKey, metrics);
    }
}

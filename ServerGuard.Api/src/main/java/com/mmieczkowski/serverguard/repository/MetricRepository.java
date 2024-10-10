package com.mmieczkowski.serverguard.repository;

import com.mmieczkowski.serverguard.model.Metric;

import java.util.List;

public interface MetricRepository {
    void saveAll(List<Metric> metric);
}

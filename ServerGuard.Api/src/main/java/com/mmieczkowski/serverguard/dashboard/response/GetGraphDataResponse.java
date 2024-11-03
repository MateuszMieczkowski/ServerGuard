package com.mmieczkowski.serverguard.dashboard.response;

import com.mmieczkowski.serverguard.metric.DataPoint;

import java.util.List;

public record GetGraphDataResponse(List<DataPoint> dataPoints) {
}

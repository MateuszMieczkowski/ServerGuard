CREATE TABLE "default"."AgentMetric" (
    "AgentId" UUID,
    "Time" DateTime,
    "SensorName" String,
    "MetricName" String,
    "Value" Float,
    "Type" Int
) 
ENGINE MergeTree()
ORDER BY ("AgentId", "Time", "SensorName", "MetricName")
SETTINGS index_granularity = 8192 SETTINGS flatten_nested=0

create table default.available_metric
(
    agent_id    UUID,
    sensor_name String,
    metric_name String,
    type        Int32
)
engine = MergeTree ORDER BY (agent_id, sensor_name, metric_name, type)
SETTINGS index_granularity = 8192;
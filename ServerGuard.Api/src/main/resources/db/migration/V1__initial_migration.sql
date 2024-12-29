create table app_user
(
    id       uuid         not null
        primary key,
    email    varchar(255) not null
        constraint uq_app_user_email
            unique,
    password varchar(255) not null
);

create table resource_group
(
    id                 uuid         not null
        primary key,
    created_date       timestamp(6) with time zone,
    is_deleted         boolean      not null,
    last_modified_date timestamp(6) with time zone,
    name               varchar(255) not null
);

create table agent
(
    id                     uuid         not null
        primary key,
    api_key                varchar(255),
    collect_every_seconds  integer      not null,
    is_controller_enabled  boolean      not null,
    is_cpu_enabled         boolean      not null,
    is_gpu_enabled         boolean      not null,
    is_memory_enabled      boolean      not null,
    is_motherboard_enabled boolean      not null,
    is_network_enabled     boolean      not null,
    is_storage_enabled     boolean      not null,
    created_date           timestamp(6) with time zone,
    is_deleted             boolean      not null,
    last_modified_date     timestamp(6) with time zone,
    name                   varchar(255) not null,
    resource_group_id      uuid
        constraint fk_agent_resource_group_id
            references resource_group,
    last_contact_at        timestamp(6) with time zone
);


create table alert
(
    id                   uuid         not null
        primary key,
    duration             numeric(21)  not null,
    aggregate_function   varchar(255) not null,
    metric_name          varchar(255) not null,
    sensor_name          varchar(255) not null,
    type                 varchar(255) not null
        constraint alert_type_check
            check ((type)::text = ANY
                   (ARRAY [('VOLTAGE'::character varying)::text, ('CURRENT'::character varying)::text, ('POWER'::character varying)::text, ('CLOCK'::character varying)::text, ('TEMPERATURE'::character varying)::text, ('LOAD'::character varying)::text, ('FREQUENCY'::character varying)::text, ('FAN'::character varying)::text, ('FLOW'::character varying)::text, ('CONTROL'::character varying)::text, ('LEVEL'::character varying)::text, ('FACTOR'::character varying)::text, ('DATA'::character varying)::text, ('SMALL_DATA'::character varying)::text, ('THROUGHPUT'::character varying)::text, ('TIME_SPAN'::character varying)::text, ('ENERGY'::character varying)::text, ('NOISE'::character varying)::text])),
    name                 varchar(255) not null,
    next_check_at        timestamp(6) with time zone,
    next_notification_at timestamp(6) with time zone,
    resolved_at          timestamp(6) with time zone,
    operator             varchar(255) not null,
    threshold            real         not null,
    agent_id             uuid         not null
        constraint fk_alert_agent_id
            references agent
);

create table alert_log
(
    id                 uuid                        not null
        primary key,
    alert_name         varchar(255)                not null,
    duration           numeric(21)                 not null,
    aggregate_function varchar(255)                not null,
    metric_name        varchar(255)                not null,
    sensor_name        varchar(255)                not null,
    type               varchar(255)                not null
        constraint alert_log_type_check
            check ((type)::text = ANY
                   (ARRAY [('VOLTAGE'::character varying)::text, ('CURRENT'::character varying)::text, ('POWER'::character varying)::text, ('CLOCK'::character varying)::text, ('TEMPERATURE'::character varying)::text, ('LOAD'::character varying)::text, ('FREQUENCY'::character varying)::text, ('FAN'::character varying)::text, ('FLOW'::character varying)::text, ('CONTROL'::character varying)::text, ('LEVEL'::character varying)::text, ('FACTOR'::character varying)::text, ('DATA'::character varying)::text, ('SMALL_DATA'::character varying)::text, ('THROUGHPUT'::character varying)::text, ('TIME_SPAN'::character varying)::text, ('ENERGY'::character varying)::text, ('NOISE'::character varying)::text])),
    triggered_at       timestamp(6) with time zone not null,
    triggered_by_value real                        not null,
    operator           varchar(255)                not null,
    threshold          real                        not null,
    agent_id           uuid                        not null
        constraint fk_alert_log_agent_id
            references agent,
    alert_id           uuid
        constraint fk_alert_log_alert_id
            references alert
            on delete set null
);

create table dashboard
(
    id         uuid                  not null
        primary key,
    name       varchar(255)          not null,
    agent_id   uuid
        constraint fk_dashboard_agent_id
            references agent,
    is_deleted boolean default false not null
);


create table graph
(
    id           uuid         not null
        primary key,
    index        integer      not null,
    line_color   varchar(255) not null,
    metric_name  varchar(255) not null,
    metric_type  smallint     not null
        constraint graph_metric_type_check
            check ((metric_type >= 0) AND (metric_type <= 17)),
    sensor_name  varchar(255) not null,
    dashboard_id uuid         not null
        constraint fk_graph_dashboard_id
            references dashboard
);

create table user_resource_group_permission
(
    role              varchar(255) not null,
    user_id           uuid         not null
        constraint fk_user_resource_group_permission_user_id
            references app_user,
    resource_group_id uuid         not null
        constraint fk_user_resource_group_permission_resource_group_id
            references resource_group,
    primary key (resource_group_id, user_id)
);

create table reset_password_link
(
    id           uuid                        not null
        primary key,
    expires_at   timestamp(6) with time zone not null,
    requested_at timestamp(6) with time zone not null,
    token        varchar(255)                not null,
    used_at      timestamp(6) with time zone,
    user_id      uuid
        constraint fk_reset_password_link_user_id
            references app_user
);


create table resource_group_invitation
(
    id                uuid                        not null
        primary key,
    accepted_at       timestamp(6) with time zone,
    created_at        timestamp(6) with time zone not null,
    email             varchar(255)                not null,
    expires_at        timestamp(6) with time zone not null,
    role              varchar(255)                not null
        constraint resource_group_invitation_role_check
            check ((role)::text = ANY ((ARRAY ['USER'::character varying, 'ADMIN'::character varying])::text[])),
    token             varchar(255)                not null,
    resource_group_id uuid                        not null
        constraint fk_resource_group_invitation_resource_group_id
            references resource_group
);


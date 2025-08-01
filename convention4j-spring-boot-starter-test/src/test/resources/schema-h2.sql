create table if not exists `user` (  -- 关键：用双引号包裹表名
  `id` bigint not null,
  `gmt_create` timestamp not null,    -- 使用 timestamp 替代 datetime
  `gmt_modified` timestamp not null,
  `username` varchar(32),
  `password` varchar(64),
  primary key (`id`),
  unique uk_username(`username`),
  index idx_gmtCreate(`gmt_create`)
---- h2的MySQL模式不支持collate=utf8mb4_bin
) engine=InnoDB default charset=utf8mb4 comment='用户表';
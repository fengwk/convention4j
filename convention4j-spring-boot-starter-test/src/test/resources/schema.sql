create table if not exists user
(
    id              bigint unsigned not null comment '主键',
    gmt_create      datetime not null comment '创建时间',
    gmt_modified    datetime not null comment '修改时间',
    username        varchar(32) comment '用户名',
    password        varchar(64) comment '密码',
    primary key (id),
    unique uk_username(username),
    index idx_gmtCreate(gmt_create)
-- h2的MySQL模式不支持collate=utf8mb4_bin
) engine=InnoDB default charset=utf8mb4 comment='用户表';

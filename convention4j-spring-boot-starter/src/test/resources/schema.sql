create table if not exists user
(
    id              bigint unsigned not null comment '主键',
    username        varchar(32) comment '用户名',
    email           varchar(128) comment '邮箱',
    mobile          varchar(32) comment '手机号',
    password        varchar(64) comment '密码',
    age             int comment '年龄',
    city            varchar(32) comment '城市',
    primary key (id),
    unique uk_user_username(username),
    unique uk_user_email(email),
    unique uk_user_mobile(mobile),
    index idx_user_age(age),
    index idx_user_city(city)
) engine=InnoDB default charset=utf8mb4 comment='用户表';

create table if not exists repo_user
(
    id              bigint unsigned not null comment '主键',
    username        varchar(32) comment '用户名',
    email           varchar(128) comment '邮箱',
    mobile          varchar(32) comment '手机号',
    password        varchar(64) comment '密码',
    age             int comment '年龄',
    city            varchar(32) comment '城市',
    primary key (id),
    unique uk_repo_user_username(username),
    unique uk_repo_user_email(email),
    unique uk_repo_user_mobile(mobile),
    index idx_repo_user_age(age),
    index idx_repo_user_city(city)
) engine=InnoDB default charset=utf8mb4 comment='repo用户表';

create table if not exists student
(
    id              bigint unsigned not null auto_increment comment '主键',
    info            text,
    primary key (id)
) engine=InnoDB default charset=utf8mb4 comment='student';
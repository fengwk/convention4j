create table if not exists ds1 (
    id             bigint unsigned not null auto_increment comment '主键id',
    name           varchar(16) not null comment '名称',
    gmt_create     datetime(3) not null default current_timestamp(3) comment '创建时间',
    gmt_modified   datetime(3) not null default current_timestamp(3) on update current_timestamp(3) comment '更新时间',
    version        bigint not null default '0' comment '数据版本号',
    primary key(id),
    index ds1_idx_name(name)
) engine=InnoDB default charset=utf8mb4 comment='ds1';

create table if not exists ds2 (
    id             bigint unsigned not null auto_increment comment '主键id',
    name           varchar(16) not null comment '名称',
    gmt_create     datetime(3) not null default current_timestamp(3) comment '创建时间',
    gmt_modified   datetime(3) not null default current_timestamp(3) on update current_timestamp(3) comment '更新时间',
    version        bigint not null default '0' comment '数据版本号',
    primary key(id),
    index ds2_idx_name(name)
) engine=InnoDB default charset=utf8mb4 comment='ds2';

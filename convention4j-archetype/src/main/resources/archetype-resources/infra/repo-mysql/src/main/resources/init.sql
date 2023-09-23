create table if not exists foo (
    id                  bigint unsigned not null comment 'foo id',
    name                varchar(32) not null comment 'foo name',
    status              varchar(32) not null comment 'foo status',
    primary key (id)
) engine=InnoDB default charset=utf8mb4 comment='foo';
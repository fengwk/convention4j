begin;
-- 注意h2的MySQL模式只能使用单引号表示字符串
insert ignore into user (id, gmt_create, gmt_modified, username, password) values (1, now(), now(), 'fengwk', '123');
insert ignore into user (id, gmt_create, gmt_modified, username, password) values (2, now(), now(), 'xiaoming', '123');
commit;
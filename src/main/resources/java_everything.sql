-- 数据库脚本
-- 创建数据库 java_everything
-- create database if not exists java_everything;  h2不需要创建数据库，在数据源有
-- 嵌入式自动把所指定的目录文件名当做数据库名称


-- 创建表
-- 表存在就删除表  表是文件信息表
drop table if exists file_thing;

create table if not exists file_thing(
  name varchar(256) not null comment '文件名',
  path varchar(1024) not null comment '文件路径',
  depth int not null comment '文件深度',
  file_type varchar(256) not null comment '文件类型'
);

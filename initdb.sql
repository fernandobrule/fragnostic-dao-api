
use mysql;

drop database if exists frgdaodb;
create database if not exists frgdaodb;

delete from user where user ='frgdaousr';
flush privileges;

create user 'frgdaousr'@'%' identified by 'frgdaopsw';
grant all privileges on *.* to frgdaousr@192.168.33.10 identified by "frgdaopsw" with grant option;
grant all privileges on *.* to frgdaousr@localhost identified by "frgdaopsw" with grant option;

flush privileges;



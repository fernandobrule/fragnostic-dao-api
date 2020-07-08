
create user 'mysqltestusr'@'%' identified by 'mysqltestpsw';

grant all on dbmysqlimpltest.* to 'mysqltestusr'@'%' with grant option;

flush privileges;


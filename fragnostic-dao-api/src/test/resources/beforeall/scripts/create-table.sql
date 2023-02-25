
create table if not exists ${db.name}.dummy1(
  dummy1_id             bigint unsigned not null auto_increment,
  dummy1_field1         bigint not null,
  dummy1_field2         varchar(64) not null,
  dummy1_field3         varchar(64) not null,
  constraint dummy1_pk  primary key(dummy1_id)
) engine = innodb default charset=latin1;


use mysql;

delete from user where user ='${env.ANT_PROPS_DB_USR}';
flush privileges;

create user if not exists '${env.ANT_PROPS_DB_USR}'@'%' identified by '${env.ANT_PROPS_DB_PSW}';
grant all on *.* to '${env.ANT_PROPS_DB_USR}'@'%' with grant option;

create user if not exists '${env.ANT_PROPS_DB_USR}'@'${env.IP_HOST}' identified by '${env.ANT_PROPS_DB_PSW}';
grant all on *.* to '${env.ANT_PROPS_DB_USR}'@'${env.IP_HOST}' with grant option;

flush privileges;

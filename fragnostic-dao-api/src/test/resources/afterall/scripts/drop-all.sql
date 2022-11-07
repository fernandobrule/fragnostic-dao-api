
drop user '${env.ANT_PROPS_DB_USR}'@'%';
flush privileges;

drop database ${db.name};


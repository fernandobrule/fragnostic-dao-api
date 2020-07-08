
-- ##############################################################
-- Tabla: TEST_TABLE
--
-- mysql => java
-- https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-type-conversions.html
--
-- MySQL Type Name	Return value of GetColumnClassName	Returned as Java Class
-- BIT(1) (new in MySQL-5.0)	BIT	java.lang.Boolean
-- BIT( > 1) (new in MySQL-5.0)	BIT	byte[]
-- TINYINT	TINYINT	java.lang.Boolean if the configuration property tinyInt1isBit is set to true (the default) and the storage size is 1, or java.lang.Integer if not.
-- BOOL, BOOLEAN	TINYINT	See TINYINT, above as these are aliases for TINYINT(1), currently.
-- SMALLINT[(M)] [UNSIGNED]	SMALLINT [UNSIGNED]	java.lang.Integer (regardless if UNSIGNED or not)
-- MEDIUMINT[(M)] [UNSIGNED]	MEDIUMINT [UNSIGNED]	java.lang.Integer, if UNSIGNED java.lang.Long (C/J 3.1 and earlier), or java.lang.Integer for C/J 5.0 and later
-- INT,INTEGER[(M)] [UNSIGNED]	INTEGER [UNSIGNED]	java.lang.Integer, if UNSIGNED java.lang.Long
-- BIGINT[(M)] [UNSIGNED]	BIGINT [UNSIGNED]	java.lang.Long, if UNSIGNED java.math.BigInteger
-- FLOAT[(M,D)]	FLOAT	java.lang.Float
-- DOUBLE[(M,B)]	DOUBLE	java.lang.Double
-- DECIMAL[(M[,D])]	DECIMAL	java.math.BigDecimal
-- DATE	DATE	java.sql.Date
-- DATETIME	DATETIME	java.sql.Timestamp
-- TIMESTAMP[(M)]	TIMESTAMP	java.sql.Timestamp
-- TIME	TIME	java.sql.Time
-- YEAR[(2|4)]	YEAR	If yearIsDateType configuration property is set to false, then the returned object type is java.sql.Short. If set to true (the default), then the returned object is of type java.sql.Date with the date set to January 1st, at midnight.
-- CHAR(M)	CHAR	java.lang.String (unless the character set for the column is BINARY, then byte[] is returned.
-- VARCHAR(M) [BINARY]	VARCHAR	java.lang.String (unless the character set for the column is BINARY, then byte[] is returned.
-- BINARY(M)	BINARY	byte[]
-- VARBINARY(M)	VARBINARY	byte[]
-- TINYBLOB	TINYBLOB	byte[]
-- TINYTEXT	VARCHAR	java.lang.String
-- BLOB	BLOB	byte[]
-- TEXT	VARCHAR	java.lang.String
-- MEDIUMBLOB	MEDIUMBLOB	byte[]
-- MEDIUMTEXT	VARCHAR	java.lang.String
-- LONGBLOB	LONGBLOB	byte[]
-- LONGTEXT	VARCHAR	java.lang.String
-- ENUM('value1','value2',...)	CHAR	java.lang.String
-- SET('value1','value2',...)	CHAR	java.lang.String
--
--
-- Short
-- Int
-- Long
-- Double
-- BigDecimal
-- String
-- Float
-- Timestamp
-- Date
--
-- ##############################################################
create table if not exists dbmysqlimpltest.test_table(
  test_id                   bigint unsigned not null auto_increment,
  test_short                smallint not null,
  test_int                  smallint unsigned not null,
  test_long                 bigint unsigned not null,
  test_double               double unsigned not null,
  test_float                float not null,
  test_big_decimal          decimal not null,
  test_string               varchar not null,
  test_timestamp            timestamp not null,
  test_date                 date not null,
  constraint test_table_pk  primary key(test_id)
) engine = innodb default charset=latin1;


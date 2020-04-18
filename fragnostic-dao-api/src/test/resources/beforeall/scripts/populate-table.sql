
insert into 
  dbmysqlimpltest.test_table(
    test_short, 
    test_int, 
    test_long, 
    test_double,
    test_float,
    test_big_decimal, 
    test_string, 
    test_timestamp, 
    test_date)
  values(
    1, -- short
    2, -- int
    3, -- long
    4, -- double
    5, -- float
    6, -- big_decimal
    "7", -- string
    "", -- timestamp
    ""); -- date

drop table Employees;
create table Employees (
    id numeric(19,0) identity not null,
    firstName varchar(32) not null,
    lastName varchar(32) not null,
    age tinyint null
);

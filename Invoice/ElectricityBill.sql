create table KHACHHANGVN (
	codeClient varchar(20) not null,
    fullname varchar(255) not null,
    time Datetime,
    qty int not null,
    unitPrice double not null,
    total double,
    customtype varchar(20) not null,
    quota double
);
create table KHACHHANGNN (
	codeClient varchar(20) not null,
    fullname varchar(255) not null,
    time Datetime,
    qty int not null,
    unitPrice double not null,
    total double,
	nationality varchar(20) not null
);
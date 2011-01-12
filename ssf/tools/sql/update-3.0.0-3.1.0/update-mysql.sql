use sitescape;
ALTER TABLE SS_Events MODIFY dtStart datetime;
ALTER TABLE SS_Events add dtCalcStart datetime;
ALTER TABLE SS_Events add dtCalcEnd datetime;
create table SS_EmailLog (id char(32) not null, zoneId bigint, sendDate datetime not null, fromField varchar(255), subj varchar(255), comment longtext, status varchar(16) not null, type varchar(32) not null, toEmailAddresses longtext, fileAttachments longtext, primary key (id)) ENGINE=InnoDB;
create index index_emaillog on SS_EmailLog (sendDate, status, type);
INSERT INTO SS_SchemaInfo values (15);
use sitescape;
ALTER TABLE SS_Events ALTER COLUMN dtStart datetime null;
ALTER TABLE SS_Events add dtCalcStart datetime null;
ALTER TABLE SS_Events add dtCalcEnd datetime null;
create table SS_EmailLog (id char(32) not null, zoneId numeric(19,0) null, sendDate datetime not null, fromField nvarchar(255) null, subj nvarchar(255) null, comment nvarchar(255) null, status varchar(16) not null, type varchar(32) not null, toEmailAddresses ntext null, fileAttachments ntext null, primary key (id));
create index index_emaillog on SS_EmailLog (sendDate, status, type);
INSERT INTO SS_SchemaInfo values (15);
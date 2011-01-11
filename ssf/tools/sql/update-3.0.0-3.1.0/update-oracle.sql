connect sitescape/sitescape;
ALTER TABLE SS_Events MODIFY dtStart timestamp;
ALTER TABLE SS_Events add dtCalcStart timestamp;
ALTER TABLE SS_Events add dtCalcEnd timestamp;
create table SS_EmailLog (id char(32) not null, zoneId number(19,0), sendDate timestamp not null, fromField varchar2(255 char), subj varchar2(255 char), comment varchar2(255 char), status varchar2(16 char) not null, type varchar2(32 char) not null, toEmailAddresses clob, fileAttachments clob, primary key (id));
create index index_emaillog on SS_EmailLog (sendDate, status, type);

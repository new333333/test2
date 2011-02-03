connect sitescape/sitescape;
ALTER TABLE SS_Events MODIFY dtStart timestamp null;
ALTER TABLE SS_Events add dtCalcStart timestamp;
ALTER TABLE SS_Events add dtCalcEnd timestamp;
create table SS_EmailLog (id char(32) not null, zoneId number(19,0), sendDate timestamp not null, fromField varchar2(255 char), subj varchar2(255 char), comment clob, status varchar2(16 char) not null, type varchar2(32 char) not null, toEmailAddresses clob, fileAttachments clob, primary key (id));
create index index_emaillog on SS_EmailLog (sendDate, status, type);
create table SS_FunctionConditionMap (functionId number(19,0) not null, meet varchar2(16 char), conditionId char(32));
create table SS_FunctionConditions (id number(19,0) not null, type varchar2(32 char) not null, zoneId number(19,0) not null, encodedSpec clob, title varchar2(255 char) not null, description_text clob, description_format number(10,0), primary key (id));
alter table SS_FunctionConditionMap add constraint FK945D2AD8BCA364AE foreign key (functionId) references SS_Functions;
alter table SS_FunctionConditionMap add constraint FK945D2AD868DAC30E foreign key (conditionId) references SS_FunctionConditions;
INSERT INTO SS_SchemaInfo values (16);
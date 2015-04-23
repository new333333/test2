use sitescape;
alter table SS_Principals alter column foreignName nvarchar(255); 
alter table SS_Forums drop column popularity;
create table SS_FolderEntryStats (id numeric(19,0) not null, zoneId numeric(19,0) null, popularity numeric(19,0) null, primary key (id));
alter table SS_FolderEntryStats add constraint FKC94AB27AF1E91E10 foreign key (id) references SS_FolderEntries;
INSERT INTO SS_SchemaInfo values (30);

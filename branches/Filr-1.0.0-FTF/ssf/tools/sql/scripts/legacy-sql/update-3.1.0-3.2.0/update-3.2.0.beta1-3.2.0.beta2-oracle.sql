connect sitescape/sitescape;
alter table SS_Forums drop column popularity;
create table SS_FolderEntryStats (id number(19,0) not null, zoneId number(19,0), popularity number(19,0), primary key (id));
alter table SS_FolderEntryStats add constraint FKC94AB27AF1E91E10 foreign key (id) references SS_FolderEntries;
INSERT INTO SS_SchemaInfo values (30);

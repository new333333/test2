use sitescape;
alter table SS_Forums drop column popularity;
create table SS_FolderEntryStats (id bigint not null, zoneId bigint, popularity bigint, primary key (id)) ENGINE=InnoDB;
alter table SS_FolderEntryStats add constraint FKC94AB27AF1E91E10 foreign key (id) references SS_FolderEntries (id);
INSERT INTO SS_SchemaInfo values (30);

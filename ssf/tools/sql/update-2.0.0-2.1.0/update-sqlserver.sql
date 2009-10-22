use sitescape;
alter table SS_ZoneConfig add mobileAccessEnabled tinyint;
alter table SS_AuditTrail add deletedFolderEntryFamily nvarchar(32);
alter table SS_FolderEntries add preDeleted tinyint null;
alter table SS_FolderEntries add preDeletedWhen numeric(19,0) null;
alter table SS_FolderEntries add preDeletedBy numeric(19,0) null;
alter table SS_Forums add preDeleted tinyint null;
alter table SS_Forums add preDeletedWhen numeric(19,0) null;
alter table SS_Forums add preDeletedBy numeric(19,0) null;
alter table SS_Attachments add relevanceUUID nvarchar(256);
alter table SS_Principals add diskSpaceUsed numeric(19,0);
alter table SS_Principals add diskQuota numeric(19,0);
alter table SS_Principals add maxGroupsQuota numeric(19,0);
alter table SS_ZoneConfig add diskQuotasEnabled tinyint;
alter table SS_ZoneConfig add diskQuotaUserDefault numeric(19,0);
alter table SS_ZoneConfig add diskQuotasHighwaterPercentage numeric(19,0);
create table SS_Extensions (id char(32) not null, lockVersion numeric(19,0) not null, name varchar(64) null, description varchar(256) null, type int null, author varchar(255) null, authorEmail varchar(255) null, authorSite varchar(255) null, dateCreated varchar(255) null, dateDeployed varchar(255) null, zoneId numeric(19,0) null, primary key (id), unique (zoneId, name));


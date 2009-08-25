use sitescape;
alter table SS_ZoneConfig add column mobileAccessEnabled bit;
alter table SS_AuditTrail add column deletedFolderEntryFamily varchar(32);

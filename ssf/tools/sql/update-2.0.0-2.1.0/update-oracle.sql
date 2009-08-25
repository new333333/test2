connect sitescape/sitescape;
alter table SS_ZoneConfig add mobileAccessEnabled(1,0);
alter table SS_AuditTrail add deletedFolderEntryFamily varchar2(32 char);

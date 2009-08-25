use sitescape;
alter table SS_ZoneConfig add mobileAccessEnabled tinyint;
alter table SS_AuditTrail add deletedFolderEntryFamily nvarchar(32);

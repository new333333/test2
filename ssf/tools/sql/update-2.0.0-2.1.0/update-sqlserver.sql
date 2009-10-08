use sitescape;
alter table SS_ZoneConfig add mobileAccessEnabled tinyint;
alter table SS_AuditTrail add deletedFolderEntryFamily nvarchar(32);
alter table SS_FolderEntries add preDeleted tinyint;
alter table SS_Forums add preDeleted tinyint;
alter table SS_Attachments add relevanceUUID nvarchar(256);

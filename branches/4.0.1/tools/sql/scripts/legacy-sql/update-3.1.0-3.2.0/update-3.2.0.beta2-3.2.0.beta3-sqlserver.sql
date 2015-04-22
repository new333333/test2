use sitescape;
alter table SS_ZoneConfig add fsaEnabled tinyint null;
alter table SS_ZoneConfig add fsaSynchInterval int null;
alter table SS_ZoneConfig add fsaAutoUpdateUrl nvarchar(255) null;
INSERT INTO SS_SchemaInfo values (31);

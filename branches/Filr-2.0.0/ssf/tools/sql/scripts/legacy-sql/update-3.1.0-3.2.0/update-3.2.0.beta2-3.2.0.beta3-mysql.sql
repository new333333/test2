use sitescape;
alter table SS_ZoneConfig add column fsaEnabled bit;
alter table SS_ZoneConfig add column fsaSynchInterval integer; 
alter table SS_ZoneConfig add column fsaAutoUpdateUrl varchar(255);
INSERT INTO SS_SchemaInfo values (31);

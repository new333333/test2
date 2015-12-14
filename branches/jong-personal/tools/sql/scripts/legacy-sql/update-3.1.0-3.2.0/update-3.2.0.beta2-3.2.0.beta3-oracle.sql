connect sitescape/sitescape;
alter table SS_ZoneConfig add fsaEnabled number(1,0);
alter table SS_ZoneConfig add fsaSynchInterval number(10,0);
alter table SS_ZoneConfig add fsaAutoUpdateUrl varchar2(255 char);
INSERT INTO SS_SchemaInfo values (31);

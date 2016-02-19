connect sitescape/sitescape;

alter table SS_Principals add dynamic number(1,0);
alter table SS_Principals add ldapQuery clob;

INSERT INTO SS_SchemaInfo values (32);

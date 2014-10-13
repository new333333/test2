use sitescape;

alter table SS_Principals add column dynamic bit;
alter table SS_Principals add column ldapQuery longtext;

INSERT INTO SS_SchemaInfo values (32);

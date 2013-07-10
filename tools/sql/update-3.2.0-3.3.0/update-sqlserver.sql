use sitescape;

alter table SS_Principals add dynamic tinyint null;
alter table SS_Principals add ldapQuery ntext null;

INSERT INTO SS_SchemaInfo values (32);

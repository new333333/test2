use sitescape;
alter table SS_LdapConnectionConfig add column ldapGuidAttribute varchar(255);
alter table SS_Principals add column ldapGuid varchar(128);
create index ldapGuid_principal on SS_Principals (ldapGuid);
INSERT INTO SS_SchemaInfo values (6);
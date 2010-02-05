use sitescape;
alter table SS_LdapConnectionConfig add ldapGuidAttribute varchar(255) null;
alter table SS_Principals add ldapGuid varchar(128) null;
create index ldapGuid_principal on SS_Principals (ldapGuid);
INSERT INTO SS_SchemaInfo values (6);

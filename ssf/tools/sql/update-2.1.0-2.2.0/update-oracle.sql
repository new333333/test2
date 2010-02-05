connect sitescape/sitescape;
alter table SS_LdapConnectionConfig add ldapGuidAttribute varchar2(255 char);
alter table SS_Principals add ldapGuid varchar2(128 char);
create index ldapGuid_principal on SS_Principals (ldapGuid);
INSERT INTO SS_SchemaInfo values (6);

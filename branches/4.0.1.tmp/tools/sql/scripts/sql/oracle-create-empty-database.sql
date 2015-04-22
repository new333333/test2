-- drop user sitescape cascade;
create user sitescape identified by sitescape;
grant connect, resource to sitescape;
GRANT UNLIMITED TABLESPACE TO sitescape;
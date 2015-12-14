Teaming create and update database scripts are generated here. 
For create scripts we use hibernate to generate the tables it controls then we append the fixed scripts (from quartz).

To update a database from one version to the next you have 2 options to create the scripts which the installer must run.  A combination of both
will probably be best.
1. 	You can generate the create scripts for the new version, manually compare to the previous version scripts and create the sql by hand.

2. 	Use the previous version create scripts to create fresh (old) tables. For v1 these are checked in under
	tools/sql/create1.0.  Then use hibernate to generate update scripts which add new tables and new constraints.  These are generated 
	under tools/sql/update. This is a temporary area, so the files should be copied into tools/sql/update-1.0.0-1.1.0/update-{database}.sql
	The hibernate schemaUpdate task does not handle dropped tables, columns or constraints, nor does 
	it handle new or changed indecies. So you need to add them by hand.  Manually add drops to tools/sql/update-1.0.0-1.1.0/update-{database}-pre.sql. 
	Manually add create index to tools/sql/update-1.0.0-1.1.0/update-{database}-post.sql  The build will add these to the resulting tools/sql/update/update-{database}.sql

Hibernate does not know about nvarchar,ntext... fields.  For databases that can be created with utf-8 or 
unicode as the default character set this doesn't matter.  For databases like mssql(sqlserver), we must change the 
hibernate generated scripts and convert char,varchar,text fields to nchar,nvarchar,ntext.  This is done 
by supplying the table/column names to convert in table_column_types.  Both generate-create-tables-script and generate-update-tables-script
run the hibernate output throught a process to convert column types.  

Because of the above issue, hibernate autoupdate is disabled.  If you turn it on, the column types will be incorrect and someone will
forget to fix them.  In addition new indecies won't be added.

When a change is made to any hibernate hbm files, run generate-all-update-scripts (need access to all databases) and generate-all-create-scripts. If there are 
indecies or foreign key additions in the create scripts, add them to the tools/sql/update-1.0.0-1.1.0/update-{database}-post.sql scripts so they aren't lost. 
These files are manually maintained.
Add the new update stuff to tools/sql/update-1.0.0-1.1.0/update-{database}.sql  At the end of a product cycle someone should check the scripts 
against a 'virgin' database to make sure nothing got lost.  During the product cycle, re-running the update scripts will work, but may report 
lots of errors for commands that have already been issued. (it is cummulative).  
This may be impossible for testers to deal with.  Maybe a good idea for them to run with hibernate autoudpate enabled.

We had a request to run teaming under a less priveged account than the account which owns the tables.  This was for oracle, but could
apply to others I suppose.  To accomplish this, schema names where added to queries.
	1. hibernate-ext.cfg.xml is changed to cover teaming and jbpm:
		<property name="default_schema">sitescape</property>
	2. ssf-ext.proprties is changed for Quartz:
		org.quartz.jobStore.tablePrefix=sitescape.SSQRTZ_
	3. The few places where we by-pass hibernate had to be changed to use the schema name.
Note: this does not effect liferay.  You are out of luck there.
	
Another option exists for Oracle and can be used for both liferay and teaming.  Oracle has the concept of synonyms,
which are just aliases from 1 schema into another.  So you could create 2 new users liferay and teaming, then add
synonyms from these schemas into the lportal and sitscape schemas.  In addition a dba would have to give the correct access to
each user to delete,insert,select,update each table and sequence.

2 scripts exist for creating oracle synonyms.  The teaming one is generated during the generate-create-tables-script task.
The liferay one is checked in with the liferay create sql scripts.



SchemaInfo

Added the SchemaInfo table to track changes we make in the Teaming schema.

Create database scripts are generated here.  First we use hibernate to generate the tables it controls then 
we then appending the fixed scripts (from quartz).

Hibernate does not know about nvarchar,ntext... fields.  For databases that can be created with utf-8 or 
unicode as the default character set this doesn't matter.  For databases like mssql, we must change the 
hibernate generated scripts and convert char,varchar,text fields to nchar,nvarchar,ntext.  This is done 
by supplying the table/column names to convert in table_column_types and running the generate-create-tables-script
over the output.  

When new columns are added, they should be added with special scripts. Otherwise hibernate autoupdate,
will add the columns and for mssql may be the wrong type.
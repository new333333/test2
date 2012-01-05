@echo off
if "%OS%" == "Windows_NT" setlocal

set "WD=%cd%"
cd "%WD%"

set CP=.\lib\mysql-connector.jar;.\lib\ojdbc6.jar;.\lib\jtds.jar

rem Set default log level. Valid values are debug, info, warning, severe, off.
set LL=info

set JAVA_OPTS=

if ""%1"" == """" goto displayUsage
if ""%2"" == """" goto displayUsage

if ""%1"" == "mysql" goto checkCommand
if ""%1"" == "oracle" goto checkCommand
if ""%1"" == "sqlserver" goto checkCommand
echo Errors:
echo    Invalid database type
goto displayUsage

:checkCommand:
if ""%2"" == ""updateDatabase"" goto updateDatabase
if ""%2"" == ""generateSqlToUpdateDatabase"" goto generateSqlToUpdateDatabase
if ""%2"" == ""markDatabaseAsUpdated"" goto markDatabaseAsUpdated
if ""%2"" == ""generateSqlToMarkDatabaseAsUpdated"" goto generateSqlToMarkDatabaseAsUpdated
if ""%2"" == ""exportSchema"" goto exportSchema
if ""%2"" == ""exportData"" goto exportData
if ""%2"" == ""diffDatabases"" goto diffDatabases
echo Errors:
echo    Invalid command
goto displayUsage

:displayUsage
echo Usage: manage-database <db type> <command>
echo
echo db types:
echo    mysql                               MySQL
echo    oracle                              Oracle
echo    sqlserver                           MS SQL Server
echo
echo commands:
echo    updateDatabase                      Update database schema to current version by applying un-run change sets to the database.
echo    generateSqlToUpdateDatabase         Generate SQL in a file which can be later executed to update database schema to current version.
echo    markDatabaseAsUpdated               Mark all change sets as ran against the database. Used only for transition from earlier legacy schema.
echo    generateSqlToMarkDatabaseAsUpdated  Generate SQL in a file which can be later executed to mark all change sets as ran against the database.
echo    exportSchema                        Export schema from existing database into a change log file. Useful for verification or troubleshooting.
echo    exportData                          Export data from existing database into a change log file which can be later executed to import the data into another database.
echo    diffDatabases                       Outputs schema difference between two databases into a change log file. Useful for verification or troubleshooting.
echo
echo Note: Additional parameters are read in from corresponding [db type]-liquibase.properties file.
go to end

:updateDatabase
java -jar ".\lib\liquibase.jar" --logLevel="%LL%" --defaultsFile=".\%1-liquibase.properties" --classpath="%CP%" --changeLogFile=".\scripts\changelog\%1-changelog-master.xml" update
go to end

:generateSqlToUpdateDatabase
java -jar ".\lib\liquibase.jar" --logLevel="%LL%" --defaultsFile=".\%1-liquibase.properties" --classpath="%CP%" --changeLogFile=".\scripts\changelog\%1-changelog-master.xml" --outputFile=".\%1-update.sql" updateSQL
go to end

:markDatabaseAsUpdated
java -jar ".\lib\liquibase.jar" --logLevel="%LL%" --defaultsFile=".\%1-liquibase.properties" --classpath="%CP%" --changeLogFile=".\scripts\changelog\%1-changelog-master.xml" changeLogSync
go to end

:generateSqlToMarkDatabaseAsUpdated
java -jar ".\lib\liquibase.jar" --logLevel="%LL%" --defaultsFile=".\%1-liquibase.properties" --classpath="%CP%" --changeLogFile=".\scripts\changelog\%1-changelog-master.xml" --outputFile=".\%1-markasupdated.sql" changeLogSyncSQL
go to end

:exportSchema
java -jar ".\lib\liquibase.jar" --logLevel="%LL%" --defaultsFile=".\%1-liquibase.properties" --classpath="%CP%" --outputFile=".\%1-schema-changelog.xml" generateChangeLog
go to end

:exportData
java -jar ".\lib\liquibase.jar" --logLevel="%LL%" --defaultsFile=".\%1-liquibase.properties" --classpath="%CP%" --outputFile=".\%1-data-changelog.xml" --diffTypes="data" generateChangeLog
go to end

:diffDatabases
java -jar ".\lib\liquibase.jar" --logLevel="%LL%" --defaultsFile=".\%1-liquibase.properties" --classpath="%CP%" --outputFile=".\%1-diff-changelog.xml" diff
go to end

:end

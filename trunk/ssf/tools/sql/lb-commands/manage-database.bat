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

if ""%1"" == ""mysql"" goto checkCommand
if ""%1"" == ""oracle"" goto checkCommand
if ""%1"" == ""sqlserver"" goto checkCommand
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
echo Usage: manage-database [db type] [command]
echo.
echo db types:
echo    mysql           MySQL
echo    oracle          Oracle
echo    sqlserver       MS SQL Server
echo.
echo standard commands (used for updating database):
echo    updateDatabase  Update database to current version by applying un-run change
echo                    sets to the database.
echo    generateSqlToUpdateDatabase         
echo                    Apply un-run change sets to generate SQL in a file which can
echo                    later be executed to update database to current version.
echo    markDatabaseAsUpdated
echo                    Mark all change sets as ran against the database. 
echo                    Used only for transition from earlier legacy schema.
echo    generateSqlToMarkDatabaseAsUpdated
echo                    Generate SQL in a file which can later be executed to mark
echo                    all change sets as ran against the database.
echo.
echo troubleshooting commands (useful for verification, testing, or troubleshooting):
echo    exportSchema    Export schema from existing database into a change log file
echo                    which can later be executed to create identical schema in 
echo                    another database.
echo    exportData      Export data from existing database into a change log file
echo                    which can later be executed to import identical data into
echo                    another database. This does not scale or handle all data types.
echo    diffDatabases   Output schema differences between two databases into a change
echo                    log file which can later be executed to upgrade the schema of
echo                    the first database to that of the second database. 
echo                    There are limitations with this function.
echo.
echo Note: Additional parameters are read in from [db type]-liquibase.properties file.
goto end

:updateDatabase
java -jar ".\lib\liquibase.jar" --logLevel="%LL%" --defaultsFile=".\%1-liquibase.properties" --classpath="%CP%" --changeLogFile=".\scripts\changelog\%1-changelog-master.xml" update
goto end

:generateSqlToUpdateDatabase
java -jar ".\lib\liquibase.jar" --logLevel="%LL%" --defaultsFile=".\%1-liquibase.properties" --classpath="%CP%" --changeLogFile=".\scripts\changelog\%1-changelog-master.xml" updateSQL > ".\%1-update.sql"
goto end

:markDatabaseAsUpdated
java -jar ".\lib\liquibase.jar" --logLevel="%LL%" --defaultsFile=".\%1-liquibase.properties" --classpath="%CP%" --changeLogFile=".\scripts\changelog\%1-changelog-master.xml" changeLogSync
goto end

:generateSqlToMarkDatabaseAsUpdated
java -jar ".\lib\liquibase.jar" --logLevel="%LL%" --defaultsFile=".\%1-liquibase.properties" --classpath="%CP%" --changeLogFile=".\scripts\changelog\%1-changelog-master.xml" changeLogSyncSQL > ".\%1-markasupdated.sql"
goto end

:exportSchema
java -jar ".\lib\liquibase.jar" --logLevel="%LL%" --defaultsFile=".\%1-liquibase.properties" --classpath="%CP%" generateChangeLog > ".\%1-schema-changelog.xml"
goto end

:exportData
java -jar ".\lib\liquibase.jar" --logLevel="%LL%" --defaultsFile=".\%1-liquibase.properties" --classpath="%CP%" --diffTypes="data" generateChangeLog > ".\%1-data-changelog.xml"
goto end

:diffDatabases
java -jar ".\lib\liquibase.jar" --logLevel="%LL%" --defaultsFile=".\%1-liquibase.properties" --classpath="%CP%" diffChangeLog > ".\%1-diff-changelog.xml"
goto end

:end

@echo off
if "%OS%" == "Windows_NT" setlocal

set CLASSPATH=.\lib\kablink-teaming-liquibase.jar;.\lib\mysql-connector.jar;.\lib\ojdbc8.jar;.\lib\jtds.jar;.\lib\postgresql-jre7.jar

rem Set default log level. Valid values are debug, info, warning, severe, off.
set LOG_LEVEL=info

set CONTEXTS=schema,data

set JAVA_OPTS=

if ""%1"" == """" goto displayUsage
if ""%2"" == """" goto displayUsage

if ""%1"" == ""mysql"" goto checkCommand
if ""%1"" == ""oracle"" goto checkCommand
if ""%1"" == ""sqlserver"" goto checkCommand
if ""%1"" == ""postgresql"" goto checkCommand
echo Errors:
echo    Invalid database type
goto displayUsage

:checkCommand:
if ""%2"" == ""updateDatabase"" goto updateDatabase
if ""%2"" == ""generateSqlToUpdateDatabase"" goto generateSqlToUpdateDatabase
if ""%2"" == ""markDatabaseAsUpdated"" goto markDatabaseAsUpdated
if ""%2"" == ""generateSqlToMarkDatabaseAsUpdated"" goto generateSqlToMarkDatabaseAsUpdated
if ""%2"" == ""mark33DatabaseAsUpdated"" goto mark33DatabaseAsUpdated
if ""%2"" == ""generateSqlToMark33DatabaseAsUpdated"" goto generateSqlToMark33DatabaseAsUpdated
if ""%2"" == ""exportSchema"" goto exportSchema
if ""%2"" == ""exportData"" goto exportData
if ""%2"" == ""diffDatabases"" goto diffDatabases
if ""%2"" == ""validate"" goto validate
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
echo    postgresql      PostgreSQL
echo.
echo standard commands (used for updating database):
echo    updateDatabase  Update database to current version by applying un-run change
echo                    sets to the database.
echo    generateSqlToUpdateDatabase         
echo                    Apply un-run change sets to generate SQL in a file which can
echo                    later be executed to update database to current version.
echo    mark33DatabaseAsUpdated
echo                    Mark all change sets as ran against the 3.3 database. 
echo                    Used for one-time transition from 3.3 schema to later version.
echo    generateSqlToMark33DatabaseAsUpdated
echo                    Generate SQL in a file which can later be executed to mark
echo                    all change sets as ran against the 3.3 database.
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
echo                    the first database to that of the second (reference) database. 
echo                    There are limitations with this function.
echo    validate        Checks the change log for errors. Useful after making manual
echo                    edits to the change log file (which is not recommended).
echo    markDatabaseAsUpdated
echo                    Mark the database schema as current with the most up-to-date
echo                    schema definition. Can be useful when rebuilding Liquibase 
echo                    meta data after manual update or fix-up of the schema.
echo    generateSqlToMarkDatabaseAsUpdated
echo                    Generate SQL in a file which can later be executed to mark
echo                    the database schema as current.
echo.
echo Note: Additional parameters are read in from [db type]-liquibase.properties file.
goto end

:updateDatabase
java -Xmx2g -jar ".\lib\liquibase.jar" --logLevel="%LOG_LEVEL%" --defaultsFile=".\%1-liquibase.properties" --classpath="%CLASSPATH%" --changeLogFile="scripts\changelog\%1-changelog-master.xml" --contexts="%CONTEXTS%" update
goto end

:generateSqlToUpdateDatabase
java -Xmx2g -jar ".\lib\liquibase.jar" --logLevel="%LOG_LEVEL%" --defaultsFile=".\%1-liquibase.properties" --classpath="%CLASSPATH%" --changeLogFile="scripts\changelog\%1-changelog-master.xml" --contexts="%CONTEXTS%" updateSQL > ".\%1-update.sql"
goto end

:markDatabaseAsUpdated
java -Xmx2g -jar ".\lib\liquibase.jar" --logLevel="%LOG_LEVEL%" --defaultsFile=".\%1-liquibase.properties" --classpath="%CLASSPATH%" --changeLogFile="scripts\changelog\%1-changelog-master.xml" --contexts="%CONTEXTS%" changeLogSync
goto end

:generateSqlToMarkDatabaseAsUpdated
java -Xmx2g -jar ".\lib\liquibase.jar" --logLevel="%LOG_LEVEL%" --defaultsFile=".\%1-liquibase.properties" --classpath="%CLASSPATH%" --changeLogFile="scripts\changelog\%1-changelog-master.xml" --contexts="%CONTEXTS%" changeLogSyncSQL > ".\%1-markasupdated.sql"
goto end

:mark33DatabaseAsUpdated
java -Xmx2g -jar ".\lib\liquibase.jar" --logLevel="%LOG_LEVEL%" --defaultsFile=".\%1-liquibase.properties" --classpath="%CLASSPATH%" --changeLogFile="scripts\changelog\%1-changelog-3.3.xml" --contexts="%CONTEXTS%" changeLogSync
goto end

:generateSqlToMark33DatabaseAsUpdated
java -Xmx2g -jar ".\lib\liquibase.jar" --logLevel="%LOG_LEVEL%" --defaultsFile=".\%1-liquibase.properties" --classpath="%CLASSPATH%" --changeLogFile="scripts\changelog\%1-changelog-3.3.xml" --contexts="%CONTEXTS%" changeLogSyncSQL > ".\%1-markasupdated-3.3.sql"
goto end

:exportSchema
java -Xmx2g -jar ".\lib\liquibase.jar" --logLevel="%LOG_LEVEL%" --defaultsFile=".\%1-liquibase.properties" --classpath="%CLASSPATH%" generateChangeLog > ".\%1-schema-changelog.xml"
goto end

:exportData
java -Xmx2g -jar ".\lib\liquibase.jar" --logLevel="%LOG_LEVEL%" --defaultsFile=".\%1-liquibase.properties" --classpath="%CLASSPATH%" --diffTypes="data" generateChangeLog > ".\%1-data-changelog.xml"
goto end

:diffDatabases
java -Xmx2g -jar ".\lib\liquibase.jar" --logLevel="%LOG_LEVEL%" --defaultsFile=".\%1-liquibase.properties" --classpath="%CLASSPATH%" diffChangeLog > ".\%1-diff-changelog.xml"
goto end

:validate
java -Xmx2g -jar ".\lib\liquibase.jar" --logLevel="%LOG_LEVEL%" --defaultsFile=".\%1-liquibase.properties" --classpath="%CLASSPATH%" --changeLogFile="scripts\changelog\%1-changelog-master.xml" --contexts="%CONTEXTS%" validate
goto end

:end

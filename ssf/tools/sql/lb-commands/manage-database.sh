#!/bin/sh

displayUsage () {
echo "Usage: manage-database <db type> <command>"
echo "db types:"
echo "  mysql                               MySQL"
echo "  oracle                              Oracle"
echo "  sqlserver                           MS SQL Server"
echo "commands:"
echo "  updateDatabase                      Update database schema to current version by applying un-run change sets to the database."
echo "  generateSqlToUpdateDatabase         Generate SQL in a file which can be later executed to update database schema to current version."
echo "  markDatabaseAsUpdated               Mark all change sets as ran against the database. Used only for transition from earlier legacy schema."
echo "  generateSqlToMarkDatabaseAsUpdated  Generate SQL in a file which can be later executed to mark all change sets as ran against the database."
echo "  exportSchema                        Export schema from existing database into a change log file. Useful for verification or troubleshooting."
echo "  exportData                          Export data from existing database into a change log file which can be later executed to import the data into another database."
echo "  diffDatabases                       Outputs schema difference between two databases into a change log file. Useful for verification or troubleshooting."
}

WD='cd'
cd $WD

CP=./lib/mysql-connector.jar:./lib/ojdbc6.jar:./lib/jtds.jar

# Set default log level. Valid values are debug, info, warning, severe, off.
LL=info

JAVA_OPTS=

if [ "$1" != "mysql" ]; then
  if [ "$1" != "oracle" ]; then
    if [ "$1" != "sqlserver" ]; then
      echo "Errors:"
      echo "  Invalid database type"
      displayUsage
      exit 1
    fi
  fi
fi

if [ "$2 = "updateDatabase" ]; then
  java -jar "./lib/liquibase.jar" --logLevel="$LL" --defaultsFile="./$1-liquibase.properties" --classpath="$CP" --changeLogFile="./scripts/changelog/$1-changelog-master.xml" update
elif [ "$2 = "generateSqlToUpdateDatabase" ]; then
  java -jar "./lib/liquibase.jar" --logLevel="$LL" --defaultsFile="./$1-liquibase.properties" --classpath="$CP" --changeLogFile="./scripts/changelog/$1-changelog-master.xml" --outputFile="./$1-update.sql" updateSQL
elif [ "$2 = "markDatabaseAsUpdated" ]; then
  java -jar "./lib/liquibase.jar" --logLevel="$LL" --defaultsFile="./$1-liquibase.properties" --classpath="$CP" --changeLogFile="./scripts/changelog/$1-changelog-master.xml" changeLogSync
elif [ "$2 = "generateSqlToMarkDatabaseAsUpdated" ]; then
  java -jar "./lib/liquibase.jar" --logLevel="$LL" --defaultsFile="./$1-liquibase.properties" --classpath="$CP" --changeLogFile="./scripts/changelog/$1-changelog-master.xml" --outputFile="./$1-markasupdated.sql" changeLogSyncSQL
elif [ "$2 = "exportSchema" ]; then
  java -jar "./lib/liquibase.jar" --logLevel="$LL" --defaultsFile="./$1-liquibase.properties" --classpath="$CP" --outputFile="./$1-schema-changelog.xml" generateChangeLog
elif [ "$2 = "exportData" ]; then
  java -jar "./lib/liquibase.jar" --logLevel="$LL" --defaultsFile="./$1-liquibase.properties" --classpath="$CP" --outputFile="./$1-data-changelog.xml" --diffTypes="data" generateChangeLog
elif [ "$2 = "diffDatabases" ]; then
  java -jar "./lib/liquibase.jar" --logLevel="$LL" --defaultsFile="./$1-liquibase.properties" --classpath="$CP" --outputFile="./$1-diff-changelog.xml" diff
else
  displayUsage
  exit 1
fi

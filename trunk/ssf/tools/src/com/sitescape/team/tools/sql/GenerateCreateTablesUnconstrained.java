package com.sitescape.team.tools.sql;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateCreateTablesUnconstrained {

	public static void main(String[] args) {
		if (args.length == 0 || args.length > 2) {
			System.out.println("usage: java GenerateCreateTablesUnconstrained <constrained script name [append]>");
			return;
		}
		else if (args.length == 1) {
			System.out.println("java GenerateCreateTablesUnconstrained " + args[0]);
		} else {
			System.out.println("java GenerateCreateTablesUnconstrained " + args[0] + " " + args[1]);
		}
		
		try {
			if (args.length == 1) {
				doMain(args[0],false);
			} else {
				if (args[1].equalsIgnoreCase("append"))
					doMain(args[0], true);
				else
					doMain(args[0], false);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void doMain(String inputFileName, boolean append) throws IOException {
		String databaseTypeStr = getDatabaseTypeStr(inputFileName);
		
		File inputFile = new File(inputFileName);
		
		BufferedReader in = new BufferedReader(new FileReader(inputFile));
		
		File createUnconstrainedTablesFile = new File(inputFile.getParentFile(), "create-unconstrained-tables-" + databaseTypeStr + ".sql");
		File addConstraintsFile = new File(inputFile.getParentFile(), "add-constraints-" + databaseTypeStr + ".sql");	
		File dropConstraintsFile = new File(inputFile.getParentFile(), "drop-constraints-" + databaseTypeStr + ".sql");
		File dropTablesFile = new File(inputFile.getParentFile(), "drop-tables-" + databaseTypeStr + ".sql");
		
		BufferedWriter createUnconstrainedTables = new BufferedWriter(new FileWriter(createUnconstrainedTablesFile, append));
		BufferedWriter addConstraints = new BufferedWriter(new FileWriter(addConstraintsFile, append));
		BufferedWriter dropConstraints = new BufferedWriter(new FileWriter(dropConstraintsFile, append));
		BufferedWriter dropTables = new BufferedWriter(new FileWriter(dropTablesFile, append));
		
		boolean createTablesStarted = false;
		
		String line = null;
		while((line = in.readLine()) != null) {
			line = line.trim();
			if (line.trim().length() == 0) continue;
			if (line.startsWith("#")) continue;
			if (line.contains("create table") || line.contains("CREATE TABLE") || 
					line.startsWith("create index") || line.startsWith("CREATE INDEX") ||
					line.startsWith("insert") || line.startsWith("INSERT")) {
				// Processing "create table" statement
				if(!createTablesStarted)
					createTablesStarted = true;
				createUnconstrainedTables.write(line);
				createUnconstrainedTables.newLine();
				while(!line.endsWith(";")) {
					line = in.readLine().trim();
					createUnconstrainedTables.write(line);
					createUnconstrainedTables.newLine();					
				}
			}
			else {
				if(!createTablesStarted) {
					// Processing statement that comes before all "create table" statements.
					if(line.contains("drop constraint") || line.contains("drop foreign") ||
							line.contains("DROP CONSTRAINT") || line.contains("DROP FOREIGN"))
					{
						// Processing statement about dropping constraints
						dropConstraints.write(line);
						dropConstraints.newLine();
						while(!line.endsWith(";")) {
							line = in.readLine().trim();
							dropConstraints.write(line);
							dropConstraints.newLine();					
						}
					}
					else {
						// Processing statement about dropping tables
						dropTables.write(line);
						dropTables.newLine();
						while(!line.endsWith(";")) {
							line = in.readLine().trim();
							dropTables.write(line);
							dropTables.newLine();					
						}
					}
				}
				else {
					// Processing statement that comes after all "create table" statements.
					addConstraints.write(line);
					addConstraints.newLine();
					while(!line.endsWith(";")) {
						line = in.readLine().trim();
						addConstraints.write(line);
						addConstraints.newLine();					
					}					
				}
			}
		}
		
		createUnconstrainedTables.close();
		addConstraints.close();
		dropConstraints.close();
		dropTables.close();
		
		if(dropConstraintsFile.length() == 0) {
			dropConstraintsFile.delete();
		}
	}
	
	private static String getDatabaseTypeStr(String inputFileName) 
		throws IllegalArgumentException {
		if(inputFileName.contains("-db2"))
			return "db2";
		else if(inputFileName.contains("-mysql"))
			return "mysql";
		else if(inputFileName.contains("-oracle"))
			return "oracle";
		else if(inputFileName.contains("-postgresql"))
			return "postgresql";
		else if(inputFileName.contains("-sqlserver"))
			return "sqlserver";
		else if(inputFileName.contains("-frontbase"))
			return "frontbase";
		else
			throw new IllegalArgumentException("Unknown database type");
	}
}

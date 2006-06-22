package com.sitescape.ef.tools.sql;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateCreateTablesUnconstrained {

	public static void main(String[] args) {
		if(args.length != 1) {
			System.out.println("usage: java GenerateCreateTablesUnconstrained <constrained script name>");
			return;
		}
		else {
			System.out.println("java GenerateCreateTablesUnconstrained " + args[0]);
		}
		
		try {
			doMain(args[0]);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void doMain(String inputFileName) throws IOException {
		String databaseTypeStr = getDatabaseTypeStr(inputFileName);
		
		File inputFile = new File(inputFileName);
		
		BufferedReader in = new BufferedReader(new FileReader(inputFile));
		
		File createUnconstrainedTablesFile = new File(inputFile.getParentFile(), "create-unconstrained-tables-" + databaseTypeStr + ".sql");
		File addConstraintsFile = new File(inputFile.getParentFile(), "add-constraints-" + databaseTypeStr + ".sql");	
		File dropConstraintsFile = new File(inputFile.getParentFile(), "drop-constraints-" + databaseTypeStr + ".sql");
		File dropTablesFile = new File(inputFile.getParentFile(), "drop-tables-" + databaseTypeStr + ".sql");
		
		BufferedWriter createUnconstrainedTables = new BufferedWriter(new FileWriter(createUnconstrainedTablesFile));
		BufferedWriter addConstraints = new BufferedWriter(new FileWriter(addConstraintsFile));
		BufferedWriter dropConstraints = new BufferedWriter(new FileWriter(dropConstraintsFile));
		BufferedWriter dropTables = new BufferedWriter(new FileWriter(dropTablesFile));
		
		boolean createTablesStarted = false;
		
		String line = null;
		while((line = in.readLine()) != null) {
			if(line.contains("create table")) {
				// Processing "create table" statement
				if(!createTablesStarted)
					createTablesStarted = true;
				createUnconstrainedTables.write(line);
				createUnconstrainedTables.newLine();
				while(!line.endsWith(";")) {
					line = in.readLine();
					createUnconstrainedTables.write(line);
					createUnconstrainedTables.newLine();					
				}
			}
			else {
				if(!createTablesStarted) {
					// Processing statement that comes before all "create table" statements.
					if(line.contains("drop constraint") || line.contains("drop foreign")) {
						// Processing statement about dropping constraints
						dropConstraints.write(line);
						dropConstraints.newLine();
						while(!line.endsWith(";")) {
							line = in.readLine();
							dropConstraints.write(line);
							dropConstraints.newLine();					
						}
					}
					else {
						// Processing statement about dropping tables
						dropTables.write(line);
						dropTables.newLine();
						while(!line.endsWith(";")) {
							line = in.readLine();
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
						line = in.readLine();
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
		else
			throw new IllegalArgumentException("Unknown database type");
	}
}

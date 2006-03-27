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
		
		File primaryFile = new File(inputFile.getParentFile(), "create-unconstrained-tables-" + databaseTypeStr + ".sql");
		File secondaryFile = new File(inputFile.getParentFile(), "add-constraints-" + databaseTypeStr + ".sql");
		
		BufferedWriter primary = new BufferedWriter(new FileWriter(primaryFile));
		BufferedWriter secondary = new BufferedWriter(new FileWriter(secondaryFile));
		
		String line = null;
		while((line = in.readLine()) != null) {
			if(line.contains("alter table") && !line.contains("drop")) {
				secondary.write(line);
				secondary.newLine();				
				do {
					line = in.readLine();
					secondary.write(line);
					secondary.newLine();
				} while (!line.endsWith(";"));
			}
			else {
				primary.write(line);
				primary.newLine();
			}
		}
		
		primary.close();
		secondary.close();
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

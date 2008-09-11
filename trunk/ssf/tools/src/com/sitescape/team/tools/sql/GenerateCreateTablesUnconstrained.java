package com.sitescape.team.tools.sql;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;

public class GenerateCreateTablesUnconstrained {

	static boolean dropConstraintsStarted = false; 	// state 1
	static boolean dropTablesStarted = false; 		// state 2
	static boolean createTablesStarted = false; 	// state 3
	static boolean addConstraintsStarted = false; 	// state 4
	
	static BufferedWriter createUnconstrainedTables = null;
	static BufferedWriter addConstraints = null;
	static BufferedWriter dropConstraints = null;
	static BufferedWriter dropTables = null;

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("usage: java GenerateCreateTablesUnconstrained <unconstrained script name>");
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
		Map typeMap = getTypeMap(databaseTypeStr);
		String tableGroupName = getTableGroupNameStr(inputFileName);
		
		File inputFile = new File(inputFileName);
		
		BufferedReader in = new BufferedReader(new FileReader(inputFile));
		//list of tables and columns to convert
		Map columnConvert = getColumnConvert(new File(inputFile.getParentFile().getParentFile(), "table_column_types"));
		File createUnconstrainedTablesFile = new File(inputFile.getParentFile(), "internal/create-unconstrained-tables-" + tableGroupName + "-" + databaseTypeStr + ".sql");
		File addConstraintsFile = new File(inputFile.getParentFile(), "internal/add-constraints-" + tableGroupName + "-" + databaseTypeStr + ".sql");	
		File dropConstraintsFile = new File(inputFile.getParentFile(), "internal/drop-constraints-" + tableGroupName + "-" + databaseTypeStr + ".sql");
		File dropTablesFile = new File(inputFile.getParentFile(), "internal/drop-tables-" + tableGroupName + "-" + databaseTypeStr + ".sql");
		
		createUnconstrainedTables = new BufferedWriter(new FileWriter(createUnconstrainedTablesFile));
		addConstraints = new BufferedWriter(new FileWriter(addConstraintsFile));
		dropConstraints = new BufferedWriter(new FileWriter(dropConstraintsFile));
		dropTables = new BufferedWriter(new FileWriter(dropTablesFile));
		
		String line = null;
		String trimmedLine = null;
		while((line = in.readLine()) != null) {
			trimmedLine = line.trim();
			if (trimmedLine.length() == 0) continue;
			if (trimmedLine.startsWith("#")) continue;
			if (trimmedLine.contains("create table") || trimmedLine.contains("CREATE TABLE")) {
				// Processing "create table" statement
				createTablesStarted = true;
				StringBuffer sb = new StringBuffer();
				sb.append(trimTrailing(line));
				while(!trimmedLine.endsWith(";")) {
					line = in.readLine();
					trimmedLine = line.trim();
					sb.append(trimTrailing(line));
				}
				createUnconstrainedTables.write(doUtf8(sb.toString(), typeMap, columnConvert));
				createUnconstrainedTables.newLine();
			}
			else if (trimmedLine.startsWith("create index") || trimmedLine.startsWith("CREATE INDEX") ||
					trimmedLine.startsWith("insert") || trimmedLine.startsWith("INSERT")) {
				// Since "create index" or "insert" can happen at any stage, we must figure out
				// where we are, and put these statements into right place accordingly. 
				addConstraintsStarted = true;
				BufferedWriter bw = addConstraints;
				bw.write(trimTrailing(line));
				bw.newLine();
				while(!trimmedLine.endsWith(";")) {
					line = in.readLine();
					trimmedLine = line.trim();
					bw.write(trimTrailing(line));
					bw.newLine();					
				}
			}
			else if(trimmedLine.contains("drop constraint") || trimmedLine.contains("drop foreign") ||
					trimmedLine.contains("DROP CONSTRAINT") || trimmedLine.contains("DROP FOREIGN")) {
				// Processing statement about dropping constraints
				dropConstraintsStarted = true;
				dropConstraints.write(trimTrailing(line));
				dropConstraints.newLine();
				while(!trimmedLine.endsWith(";")) {
					line = in.readLine();
					trimmedLine = line.trim();
					dropConstraints.write(trimTrailing(line));
					dropConstraints.newLine();					
				}
			}
			else if(trimmedLine.contains("drop table") || trimmedLine.contains("DROP TABLE")
					|| trimmedLine.contains("delete from") || trimmedLine.contains("DELETE FROM")) {
				// Processing statement about dropping tables
				dropTablesStarted = true;
				dropTables.write(trimTrailing(line));
				dropTables.newLine();
				while(!trimmedLine.endsWith(";")) {
					line = in.readLine();
					trimmedLine = line.trim();
					dropTables.write(trimTrailing(line));
					dropTables.newLine();					
				}		
			}
			else if(trimmedLine.contains("alter table") || trimmedLine.contains("ALTER TABLE")) {
				if(createTablesStarted)
					addConstraintsStarted = true;
				else
					dropConstraintsStarted = true;
				BufferedWriter bw = selectWriter();
				bw.write(trimTrailing(line));
				bw.newLine();
				while(!trimmedLine.endsWith(";")) {
					line = in.readLine();
					trimmedLine = line.trim();
					bw.write(trimTrailing(line));
					bw.newLine();					
				}
			}
			else {
				// some command/line we don't recognize
				BufferedWriter bw = selectWriter();
				bw.write(trimTrailing(line));
				bw.newLine();
			}
		}
		
		createUnconstrainedTables.close();
		addConstraints.close();
		dropConstraints.close();
		dropTables.close();
		
		if(createUnconstrainedTablesFile.length() == 0) {
			System.out.println("*** WARNING: zero length file [" + createUnconstrainedTablesFile.getAbsolutePath());
			createUnconstrainedTablesFile.delete();
		}
		if(addConstraintsFile.length() == 0) {
			System.out.println("*** WARNING: zero length file [" + addConstraintsFile.getAbsolutePath());
			addConstraintsFile.delete();
		}
		if(dropConstraintsFile.length() == 0) {
			System.out.println("*** WARNING: zero length file [" + dropConstraintsFile.getAbsolutePath());
			dropConstraintsFile.delete();
		}
		if(dropTablesFile.length() == 0) {
			System.out.println("*** WARNING: zero length file [" + dropTablesFile.getAbsolutePath());
			dropTablesFile.delete();
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
	private static Map getTypeMap(String databaseTypeStr) {
		if(databaseTypeStr.equals("db2"))
			return new HashMap();
		else if(databaseTypeStr.equals("mysql"))  //database created with utf8 already
			//column length is number of characters - storage depends on utf8
			return new HashMap();
		else if(databaseTypeStr.equals("oracle")) {
			//since nchar,nvarchar and nclob are not supported by hibernate and the driver
			// doesn't know any better, the NLS_CHARACTER_SET has to be unicode, so no conversion is done
			return new HashMap();
		} else if(databaseTypeStr.equals("postgresql"))
			return new HashMap();
		else if(databaseTypeStr.equals("sqlserver")) {
			//even though hibernate doesn't support it, the driver appears to do the right thing
			Map result = new TreeMap(String.CASE_INSENSITIVE_ORDER);
			result.put("varchar", "nvarchar");
			result.put("text", "ntext");
			return result;
		
		} else if(databaseTypeStr.equals("frontbase")) {
			Map result = new TreeMap(String.CASE_INSENSITIVE_ORDER);
			result.put("varchar", "nvarchar");
			result.put("clob", "nclob");
			return result;
		} else
			throw new IllegalArgumentException("Unknown database type");
		
	}
	private static String getTableGroupNameStr(String inputFileName) 
	throws IllegalArgumentException {
		if(inputFileName.contains("-core"))
			return "core";
		else if(inputFileName.contains("-jbpm"))
			return "jbpm";
		else if(inputFileName.contains("-quartz"))
			return "quartz";
		else
			throw new IllegalArgumentException("Unknown table group type");
	}
	
	private static String trimTrailing(String str) {
		int len = str.length();
		int st = 0;
		int off = 0;
		char[] val = str.toCharArray();

		while ((st < len) && (val[off + st] <= ' ')) {
		    st++;
		}
		while ((st < len) && (val[off + len - 1] <= ' ')) {
		    len--;
		}
		return ((st > 0) || (len < str.length())) ? str.substring(0, len) : str;
	}
	
	private static BufferedWriter selectWriter() {
		BufferedWriter bw = null;
		if(addConstraintsStarted)
			bw = addConstraints;
		else if(createTablesStarted)
			bw = createUnconstrainedTables;
		else if(dropTablesStarted)
			bw = dropTables;
		else if(dropConstraintsStarted)
			bw = dropConstraints;
		else
			throw new IllegalStateException("*** Shouldn't happen!!!");
		
		return bw;
	}
	private static 	Map getColumnConvert(File file) throws IOException {
		Map columnConvert = new HashMap();
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line,trimmedLine;
		while((line = in.readLine()) != null) {
			trimmedLine = line.trim();
			if (trimmedLine.length() == 0) continue;
			if (trimmedLine.startsWith("#")) continue;
			String[] vals = trimmedLine.split(" ");
			columnConvert.put(vals[0].toLowerCase(), vals);
		}
		return columnConvert;
	}
	private static String doUtf8(String input, Map typeMap, Map columnConvert) {
		if (typeMap.isEmpty()) return input;
		String [] works = input.toString().split(",");
		if (works.length == 0) return input;
		String [] first = works[0].split (" ");
		if (first.length < 3) return input;
		String tableName = first[2].toLowerCase();
		if (!columnConvert.containsKey(tableName)) return input;
		
		String out = new String(input);
		String [] vals = (String[])columnConvert.get(tableName);
		int index = works[0].indexOf("(");
		//strip 'create table xxx'
		works[0] = works[0].substring(index+1);
		for (int i=1; i<vals.length; ++i) {
			String field = vals[i]; //exact case field name
			for (int j=0; j<works.length; ++j) {
				String currentField = works[j].trim();
				if (currentField.startsWith(field + " ")) {
					//string regexp syntax
					int paren = currentField.indexOf("(");					
					if (paren != -1) currentField = currentField.substring(0, paren);
					//get datatype
					String [] column = currentField.split(" ");
					String dataType = column[1];
					String replaceType = (String)typeMap.get(dataType);
					if (replaceType == null) continue;
					String newField = currentField.replaceFirst(" " + dataType, " " + replaceType);
					out = out.replaceFirst(currentField, newField);
					System.out.println("Replaced: " + tableName + " '" + currentField + "' FOR '" + newField + "'");
				}
				
			}
			
			
		}
		return out;
		
		
	}
}

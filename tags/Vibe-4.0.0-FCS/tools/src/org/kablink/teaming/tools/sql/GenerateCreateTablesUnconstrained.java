/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.tools.sql;


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
	static BufferedWriter createSynonyms = null;
	static BufferedWriter updateTables = null;

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("usage: java  GenerateCreateTablesUnconstrained [-update] <unconstrained script name> [schema]");
			return;
		}
		else if  (args.length > 3 ) {
			System.out.println("java GenerateCreateTablesUnconstrained " + args);
		}
		
		try {
			if (args[0].equals("-update")) {
				if (args.length == 2)
					doUpdate(args[1], "sitescape");
				else {
					doUpdate(args[1], args[2]);
				}
				
			} else {
				if (args.length == 1)
					doCreate(args[0], "sitescape");
				else {
					doCreate(args[0], args[1]);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void doCreate(String inputFileName, String schema) throws IOException {
		String databaseTypeStr = getDatabaseTypeStr(inputFileName);
		Map typeMap = getTypeMap(databaseTypeStr);
		String tableGroupName = getTableGroupNameStr(inputFileName);
		
		File inputFile = new File(inputFileName);
		
		BufferedReader in = new BufferedReader(new FileReader(inputFile));
		try {
			//list of tables and columns to convert
			Map columnConvert = getColumnConvert(new File(inputFile.getParentFile().getParentFile().getParentFile(), "table_column_types"));
			File createUnconstrainedTablesFile = new File(inputFile.getParentFile(), "internal/create-unconstrained-tables-" + tableGroupName + "-" + databaseTypeStr + ".sql");
			File addConstraintsFile = new File(inputFile.getParentFile(), "internal/add-constraints-" + tableGroupName + "-" + databaseTypeStr + ".sql");	
			File dropConstraintsFile = new File(inputFile.getParentFile(), "internal/drop-constraints-" + tableGroupName + "-" + databaseTypeStr + ".sql");
			File dropTablesFile = new File(inputFile.getParentFile(), "internal/drop-tables-" + tableGroupName + "-" + databaseTypeStr + ".sql");
			
			createUnconstrainedTables = new BufferedWriter(new FileWriter(createUnconstrainedTablesFile));
			addConstraints = new BufferedWriter(new FileWriter(addConstraintsFile));
			dropConstraints = new BufferedWriter(new FileWriter(dropConstraintsFile));
			dropTables = new BufferedWriter(new FileWriter(dropTablesFile));
			if(databaseTypeStr.equals("oracle")) {
				File createSynonymsFile = new File(inputFile.getParentFile(), "internal/create-synonyms-" + tableGroupName + "-" + databaseTypeStr + ".sql");
				createSynonyms = new BufferedWriter(new FileWriter(createSynonymsFile));
			}
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
					createUnconstrainedTables.write(doTableUtf8(sb.toString(), typeMap, columnConvert));
					createUnconstrainedTables.newLine();
					if(createSynonyms!= null) {
						String [] works = sb.toString().split(",");
						if (works.length != 0) {
							String [] first = works[0].split (" ");
							if (first.length >= 3) {
								String tableName = first[2];
								createSynonyms.write("create synonym " + tableName + " for " + schema + "." + tableName + ";");
								createSynonyms.newLine();
							}
						}
					}
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
				else if (trimmedLine.contains("create sequence") || trimmedLine.contains("CREATE SEQUENCE")) {
					BufferedWriter bw = selectWriter();
					bw.write(trimTrailing(line));
					bw.newLine();
					if(createSynonyms!= null) {
						String [] works = line.toString().split(" ");
						if (works.length >= 3) {
							String seqName = works[2];
							if (seqName.lastIndexOf(";") > -1) seqName=seqName.substring(0, seqName.lastIndexOf(";"));
							createSynonyms.write("create synonym " + seqName + " for " + schema + "." + seqName + ";");
							createSynonyms.newLine();
						}
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
			if(createSynonyms!= null) createSynonyms.close();
			
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
		finally {
			in.close();
		}	
	}
	
	private static void doUpdate(String inputFileName, String schema) throws IOException {
		//Input scripts have create table, alter table only; come from hibernate.  no delimiter except new line
		String databaseTypeStr = getDatabaseTypeStr(inputFileName);
		Map typeMap = getTypeMap(databaseTypeStr);
		String tableGroupName = getTableGroupNameStr(inputFileName);
		
		File inputFile = new File(inputFileName);
		
		BufferedReader in = new BufferedReader(new FileReader(inputFile));
		try {
			//list of tables and columns to convert
			Map columnConvert = getColumnConvert(new File(inputFile.getParentFile().getParentFile().getParentFile(), "table_column_types"));
			File updateTablesFile = new File(inputFile.getParentFile(), "internal/update-tables-" + tableGroupName + "-" + databaseTypeStr + ".sql");
			if(databaseTypeStr.equals("oracle")) {
				File createSynonymsFile = new File(inputFile.getParentFile(), "internal/create-synonyms-" + tableGroupName + "-" + databaseTypeStr + ".sql");
				createSynonyms = new BufferedWriter(new FileWriter(createSynonymsFile));
			}
			
			updateTables = new BufferedWriter(new FileWriter(updateTablesFile));
			String line = null;
			String trimmedLine = null;
			while((line = in.readLine()) != null) { 
				trimmedLine = line.trim();
				if (trimmedLine.length() == 0) continue;
				if (trimmedLine.startsWith("#")) continue;
				trimmedLine = trimmedLine + ";";//on update straight from hibernate, no delimiers
				if (trimmedLine.contains("create table") || trimmedLine.contains("CREATE TABLE")) {
					// Processing "create table" statement
					updateTables.write(doTableUtf8(trimmedLine, typeMap, columnConvert));
					updateTables.newLine();
					if(createSynonyms!= null) {
						String [] works = line.split(",");
						if (works.length != 0) {
							String [] first = works[0].split (" ");
							if (first.length >= 3) {
								String tableName = first[2];
								createSynonyms.write("create synonym " + tableName + " for " + schema + "." + tableName + ";");
								createSynonyms.newLine();
							}
						}
					}
				}
				else if (trimmedLine.startsWith("create index") || trimmedLine.startsWith("CREATE INDEX")) {
					//don't think hibernate generates these
					updateTables.write(trimmedLine);
					updateTables.newLine();
				}
				else if(trimmedLine.contains("alter table") || trimmedLine.contains("ALTER TABLE")) {
					updateTables.write(doColumnUtf8(trimmedLine, typeMap, columnConvert));
					updateTables.newLine();
				}
				else if (trimmedLine.contains("create sequence") || trimmedLine.contains("CREATE SEQUENCE")) {
					updateTables.write(trimmedLine);
					updateTables.newLine();
					if(createSynonyms!= null) {
						String [] works = line.toString().split(" ");
						if (works.length >= 3) {
							String seqName = works[2];
							if (seqName.lastIndexOf(";") > -1) seqName=seqName.substring(0, seqName.lastIndexOf(";"));
							createSynonyms.write("create synonym " + seqName + " for " + schema + "." + seqName + ";");
							createSynonyms.newLine();
						}
					}
				}
			}
		}
		finally {
			in.close();
		}
		
		updateTables.close();
		if(createSynonyms!= null) createSynonyms.close();

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
		try {
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
		finally {
			in.close();
		}
	}
	private static String doTableUtf8(String input, Map typeMap, Map columnConvert) {
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
		for (int i=1; i<vals.length; ++i) {//0 is table name
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
	//alter table add column
	private static String doColumnUtf8(String input, Map typeMap, Map columnConvert) {
		if (typeMap.isEmpty()) return input;
		String [] first = input.split (" ");
		if (first.length < 5) return input;
		String tableName = first[2].toLowerCase();
		if (!columnConvert.containsKey(tableName)) return input;
		if (!first[3].equalsIgnoreCase("add")) return input;
		String [] vals = (String[])columnConvert.get(tableName);
		for (int i=1; i<vals.length; ++i) { //0 is tablename
			String dataType=null;
			if (vals[i].equalsIgnoreCase(first[4])) {
				dataType = first[5];
			}else if ("column".equals(first[4]) && vals[i].equalsIgnoreCase(first[5])) {
				dataType = first[6];
			}
			if (dataType != null) {
			//string regexp syntax
				int paren = dataType.indexOf("(");					
				if (paren != -1) dataType = dataType.substring(0, paren);
				String replaceType = (String)typeMap.get(dataType);
				if (replaceType == null) continue;
				String output = input.replaceFirst(dataType, replaceType);
				System.out.println("Replaced: '" + input + "' FOR '" + output + "'");
				return output;
			}

		}
		return input;
	}
}


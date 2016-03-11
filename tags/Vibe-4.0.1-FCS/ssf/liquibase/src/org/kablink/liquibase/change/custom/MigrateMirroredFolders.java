/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.liquibase.change.custom;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jong
 *
 */
public class MigrateMirroredFolders {
	

	public static void main(String[] args) {
		System.out.println(columnsToCopy);
		System.out.println(columnsToCopyCommaSeparated);
		System.out.println(new Date());
	}

	
	// A list of columns to move from SS_Forums table to SS_NetFolderConfig table.
	static String[] columnsToCopy = new String[] {
		"homeDir",
		"allowDesktopAppToSyncData",
		"allowMobileAppsToSyncData",
		"indexContent",
		"jitsEnabled",
		"jitsMaxAge",
		"jitsAclMaxAge",
		"fullSyncDirOnly",
		"syncScheduleOption",
		"useInheritedIndexContent",
		"useInheritedJitsSettings",
		"allowDAToTriggerInitialHFSync",
		"useInheritedDATriggerSetting"
	};
	
	static String columnsToCopyCommaSeparated;	
	static {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < columnsToCopy.length; i++) {
			sb.append(columnsToCopy[i]);
			if(i < columnsToCopy.length-1)
				sb.append(",");
		}
		columnsToCopyCommaSeparated = sb.toString();
	}
	
	Connection conn;
	String dbType;
	int batchSize; // Number of candidates to read in at once
	int transactionSize; // Number of candidates to update in one transaction
	String logFileName;
	
	List<NetFolderServer> netFolderServerList;
	
	PrintWriter log;

	public MigrateMirroredFolders(Connection conn, String dbType, int batchSize, int transactionSize, String logFileName)  {
		if(!("mysql".equals(dbType) || "sqlserver".equals(dbType) || "oracle".equals(dbType)))
			throw new IllegalArgumentException("Unknown db type: [" + dbType + "]");
		if(batchSize < transactionSize)
			throw new IllegalArgumentException("Batch size must be equal to or greater than the transaction size");
		this.conn = conn;
		this.dbType = dbType;
		this.batchSize = batchSize;
		this.transactionSize = transactionSize;
		this.logFileName = logFileName;
	}
	
	public void migrate() throws SQLException, FileNotFoundException, IOException, MigrateNetFolderConfigException {
		try(FileOutputStream fos = new FileOutputStream(logFileName, true);
				OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
				BufferedWriter bw = new BufferedWriter(osw);
				PrintWriter pw = new PrintWriter(bw)) {
			long startTimeMs = System.currentTimeMillis();
			// Copy the reference to the log file for convenient access.
			this.log = pw;
			logInfo("---------------------------------------------------------------------------------------");
			logInfo("Starting migration: dbType=" + dbType + ", batchSize=" + batchSize + ", transactionSize=" + transactionSize + ", logFilePath=" + (new File(logFileName)).getAbsolutePath());
			
			try {
				// Load and cache all net folder server objects.
				loadAndCacheNetFolderServers();
				// Load and cache all net folder config objects.
				loadAndCacheNetFolderConfigs();
				// Migrate remaining mirrored folders that are also top folders, creating additional net folder config objects as necessary.
				migrateRemainingMirroredTopFolders();
				// Migrate remaining mirrored folders that are not top folders.
				migrateRemainingMirroredNonTopFolders();
			}
			catch(Exception e) {
				logError("Aborting migration due to an error", e);
				throw e;
			}
			long endTimeMs = System.currentTimeMillis();
			logInfo("Migration completed successfully (" + (endTimeMs-startTimeMs)/1000 + " sec)");
			this.log = null;
		}
	}
	
	private void loadAndCacheNetFolderServers() throws SQLException {
		logInfo("Loading and caching all net folder servers");
		netFolderServerList = new ArrayList<NetFolderServer>();
		try(Statement stmt = conn.createStatement()) {
			String sqlQuery = "SELECT id,zoneId,name FROM SS_ResourceDriver";
			try(ResultSet rs = stmt.executeQuery(sqlQuery)) {
				NetFolderServer nfs;
				while(rs.next()) {
					long id = rs.getLong(1);
					long zoneId = rs.getLong(2);
					String name = rs.getString(3);
					nfs = new NetFolderServer(id, zoneId, name);
					netFolderServerList.add(nfs);
					logInfo("Loaded and cached net folder server " + nfs);
				}
			}
		}
		logInfo(netFolderServerList.size() + " net folder servers loaded and cached");
	}
	
	private void loadAndCacheNetFolderConfigs() throws SQLException, MigrateNetFolderConfigException {
		logInfo("Loading and caching all net folder configs");
		int count = 0;
		// First, load up net folder config objects
		try(Statement stmt = conn.createStatement()) {
			String sqlQuery = "SELECT id,zoneId,netFolderServerId,resourcePath,topFolderId FROM SS_NetFolderConfig";
			try(ResultSet rs = stmt.executeQuery(sqlQuery)) {
				NetFolderConfig nfc;
				while(rs.next()) {
					long id = rs.getLong(1);
					long zoneId = rs.getLong(2);
					long netFolderServerId = rs.getLong(3);
					String resourcePath = rs.getString(4);
					if("/".equals(resourcePath))
						resourcePath = "";
					long topFolderId = rs.getLong(5);
					NetFolderServer nfs = findNetFolderServerById(netFolderServerId);
					if(nfs == null)
						throw new MigrateNetFolderConfigException("Cannot find net folder server by id [" + netFolderServerId + "]");
					if(zoneId != nfs.zoneId) {
						throw new MigrateNetFolderConfigException("Bad integrity - The zoneId doesn't match between net folder server " + nfs + " and net folder config with id '" + id + "' whose zoneId is " + zoneId);
					}
					nfc = new NetFolderConfig(id, resourcePath, topFolderId);
					nfs.netFolderConfigList.add(nfc);
					logInfo("Loaded and cached net folder config " + nfc);
					count++;
				}
			}
		}
		// Second, fill in the sort key values for each net folder config objects
		try(PreparedStatement stmt = conn.prepareStatement("SELECT binder_sortKey FROM SS_Forums where id=?")) {
			for(NetFolderServer nfs:netFolderServerList) {
				for(NetFolderConfig nfc:nfs.netFolderConfigList) {
					stmt.setLong(1, nfc.topFolderId);
					try(ResultSet rs = stmt.executeQuery()) {
						String sortKey = null;
						if(rs.next()) {
							sortKey = rs.getString(1);
						}
						nfc.setSortKey(sortKey);
					}
				}
			}
		}		
		logInfo(count + " net folder configs loaded and cached");
	}
	
	public void migrateRemainingMirroredTopFolders() throws SQLException, MigrateNetFolderConfigException {
		logInfo("Migrating top folders (net folders and mirrored folders)");
		List<Map<String,Object>> mirroredTopFolders;
		int count = 0;
		while(true) {
			mirroredTopFolders = loadMirroredTopFoldersOneBatch();
			count += mirroredTopFolders.size();
			if(mirroredTopFolders.size() > 0) {
				// There is something to process
				migrateMirroredTopFolders(mirroredTopFolders);
			}
			if(mirroredTopFolders.size() < batchSize)
				break; // We're done
		}
		logInfo(count + " top folders migrated");
	}	
	
	private List<Map<String,Object>> loadMirroredTopFoldersOneBatch() throws SQLException {
		String query = getQueryForRetrievingMirroredTopFoldersOneBatch();	
		return loadMirroredFoldersOneBatch(query);
	}
	
	private List<Map<String,Object>> loadMirroredNonTopFoldersOneBatch() throws SQLException {
		String query = getQueryForRetrievingMirroredNonTopFoldersOneBatch();		
		return loadMirroredFoldersOneBatch(query);
	}
	
	private List<Map<String,Object>> loadMirroredFoldersOneBatch(String query) throws SQLException {
		try(Statement stmt = conn.createStatement()) {
			try(ResultSet rs = stmt.executeQuery(query)) {
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>();
				Map<String,Object> row;
				while(rs.next()) {
					row = new HashMap<String,Object>();
					for(int i = 1; i <= columnCount; i++) {
						String columnName = rsmd.getColumnName(i);
						Object columnValue = rs.getObject(i);
						if("resourcePath".equals(columnName)) {
							if(columnValue != null) {
								String resourcePath = (String) columnValue;
								if("/".equals(resourcePath))
									columnValue = "";
							}
							else {
								columnValue = "";
							}
						}
						row.put(columnName, columnValue);
					}
					rows.add(row);
				}
				return rows;
			}
		}
	}
	
	private String getQueryForRetrievingMirroredTopFoldersOneBatch() throws SQLException {
		String sqlQueryCommonPart =
				" id,zoneId,name,title,resourceDriverName,resourcePath,binder_sortKey,"
				+ columnsToCopyCommaSeparated
				+ " FROM SS_Forums" 
				+ " WHERE netFolderConfigId is null AND resourceDriverName is not null AND topFolder is null";

		String sqlQuery;
		
		if("mysql".equals(dbType)) {
			sqlQuery =
					"SELECT"
					+ sqlQueryCommonPart
					+ " LIMIT " + batchSize;
		}
		else if("sqlserver".equals(dbType)) {
			sqlQuery =
					"SELECT top " + batchSize
					+ sqlQueryCommonPart;
		}
		else {
			// For Oracle, ignore transaction size, and simply get the whole thing in one shot.
			// Since Filr doesn't support Oracle, this should only cover the situation where
			// Vibe customer has mirrored folders stored in the Oracle database. We do not
			// expect mirrored folders to be that large, so this should be OK.
			sqlQuery =
					"SELECT"
					+ sqlQueryCommonPart;
		}
		
		return sqlQuery;
	}
	
	private String getQueryForRetrievingMirroredNonTopFoldersOneBatch() throws SQLException {
		String sqlQueryCommonPart =
				" id,zoneId,resourceDriverName,resourcePath,binder_sortKey,"
				+ columnsToCopyCommaSeparated
				+ " FROM SS_Forums" 
				+ " WHERE netFolderConfigId is null AND resourceDriverName is not null AND topFolder is not null";

		String sqlQuery;
		
		if("mysql".equals(dbType)) {
			sqlQuery =
					"SELECT"
					+ sqlQueryCommonPart
					+ " LIMIT " + batchSize;
		}
		else if("sqlserver".equals(dbType)) {
			sqlQuery =
					"SELECT top " + batchSize
					+ sqlQueryCommonPart;
		}
		else {
			// For Oracle, ignore transaction size, and simply get the whole thing in one shot.
			// Since Filr doesn't support Oracle, this should only cover the situation where
			// Vibe customer has mirrored folders stored in the Oracle database. We do not
			// expect mirrored folders to be that large, so this should be OK.
			sqlQuery =
					"SELECT"
					+ sqlQueryCommonPart;
		}
		
		return sqlQuery;
	}
	
	private void migrateMirroredTopFolders(List<Map<String,Object>> mirroredTopFolders) throws SQLException, MigrateNetFolderConfigException {
		List<Map<String,Object>> sublist;
		int sublistSize;
		int fromIndex = 0;
		int toIndex = 0;
		while(fromIndex < mirroredTopFolders.size()) {
			sublistSize = Math.min(transactionSize, mirroredTopFolders.size()-fromIndex);
			toIndex = fromIndex + sublistSize;
			sublist = mirroredTopFolders.subList(fromIndex, toIndex);
			migrateMirroredTopFoldersOneTransaction(sublist);
			fromIndex = toIndex;
		}
	}
	
	private void migrateMirroredTopFoldersOneTransaction(List<Map<String,Object>> mirroredTopFolders) throws SQLException, MigrateNetFolderConfigException {
		// Do this in a single transaction - Either the whole thing succeeds or fails.
		logInfo("Starting transaction");
		conn.setAutoCommit(false);
		try {
			String nfcInsertQuery =
					"INSERT INTO SS_NetFolderConfig"
					+ "(lockVersion,zoneId,name,topFolderId,netFolderServerId,resourcePath,"
					+ columnsToCopyCommaSeparated
					+ ") VALUES"
					+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";		
			
			String netFolderTopUpdateQuery = getNetFolderTopUpdateQuery();
			
			String legacyMirroredFolderUpdateQuery = getLegacyMirroredFolderUpdateQuery();
						
			try(PreparedStatement nfcInsertStmt = conn.prepareStatement(nfcInsertQuery, Statement.RETURN_GENERATED_KEYS)) {
				try(PreparedStatement netFolderTopUpdateStmt = conn.prepareStatement(netFolderTopUpdateQuery)) {
					try(PreparedStatement legacyMirroredFolderUpdateStmt = conn.prepareStatement(legacyMirroredFolderUpdateQuery)) {
						for(Map<String,Object> mirroredTopFolder : mirroredTopFolders) {
							migrateMirroredTopFolder(mirroredTopFolder, nfcInsertStmt, netFolderTopUpdateStmt, legacyMirroredFolderUpdateStmt);
						}
					}
				}
			}
			logInfo("Committing transaction");
			conn.commit();
		}
		catch(Exception e) {
			logError("Rolling back transaction due to error: " + e.toString());
			conn.rollback();
			throw e; // Rethrow to abort the whole process immediately (i.e., fail fast)
		}
		finally {
			conn.setAutoCommit(true);
		}
	}
	
	private String getNetFolderTopUpdateQuery() {
		return "UPDATE SS_Forums "
				+ "SET name=?, netFolderConfigId=?, relRscPath=?, resourceDriverName=null "
				+ "WHERE id=?";
	}
	
	private String getNetFolderNonTopUpdateQuery() {
		return "UPDATE SS_Forums "
				+ "SET netFolderConfigId=?, relRscPath=?, resourceDriverName=null "
				+ "WHERE id=?";
	}
	
	private String getLegacyMirroredFolderUpdateQuery() {
		return "UPDATE SS_Forums "
				+ "SET legacyMirroredDriverNameHash=?, relRscPath=?, resourceDriverName=null "
				+ "WHERE id=?";
	}
	
	private void migrateMirroredTopFolder(Map<String,Object> mirroredTopFolder, PreparedStatement nfcInsertStmt, PreparedStatement netFolderTopUpdateStmt, PreparedStatement legacyMirroredFolderUpdateStmt) throws SQLException, MigrateNetFolderConfigException {
		Long folderId = toLong(mirroredTopFolder.get("id"));
		Long zoneId = toLong(mirroredTopFolder.get("zoneId"));
		if(zoneId == null)
			throw new MigrateNetFolderConfigException("Top folder " + folderId + " is missing zoneId");
		String resourceDriverName = (String) mirroredTopFolder.get("resourceDriverName");
		String resourcePath = (String) mirroredTopFolder.get("resourcePath");
		String sortKey = (String) mirroredTopFolder.get("binder_sortKey");
		
		NetFolderServer nfs = findNetFolderServerByName(resourceDriverName, zoneId);
		if(nfs != null) { // This top folder is a net folder.
			// Create a new net folder config object in the system
			nfcInsertStmt.setLong(1, 0L); // lockVersion
			nfcInsertStmt.setLong(2, zoneId); // zoneId
			nfcInsertStmt.setObject(3, mirroredTopFolder.get("title")); // name
			nfcInsertStmt.setObject(4, folderId); // topFolderId
			nfcInsertStmt.setLong(5, nfs.id); // netFolderServerId
			nfcInsertStmt.setObject(6, ("".equals(resourcePath))? "/" : resourcePath); // resourcePath
			int index = 7;
			for(String columnToCopy : columnsToCopy) {
				nfcInsertStmt.setObject(index++, mirroredTopFolder.get(columnToCopy));
			}
			// Insert a new row in the database
			int status = nfcInsertStmt.executeUpdate();
			if(status == 0)
				throw new MigrateNetFolderConfigException("Creating net folder config failed for the top folder " + folderId + " - No row inserted.");
			// Get the ID of the newly created net folder config row
			long nfcId;
	        try(ResultSet generatedKeys = nfcInsertStmt.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                nfcId = generatedKeys.getLong(1);
	            }
	            else {
	                throw new MigrateNetFolderConfigException("Creating net folder config failed for the top folder " + folderId + "] - No ID obtained.");
	            }
	        }
	        logInfo("Created/inserted a new net folder config. The ID is " + nfcId);
	        // Add the new net folder config object into the cache.
			NetFolderConfig nfc = new NetFolderConfig(nfcId, (String) mirroredTopFolder.get("resourcePath"), folderId);
			nfc.setSortKey(sortKey);
			nfs.netFolderConfigList.add(nfc);
			
	   		String folderName = "_NFT_" + mirroredTopFolder.get("name");
	   		if(folderName.length() > 128)
	   			folderName = folderName.substring(0, 128);
	   		netFolderTopUpdateStmt.setString(1, folderName); // name
	   		netFolderTopUpdateStmt.setLong(2, nfcId); // netFolderConfigId
	   		netFolderTopUpdateStmt.setString(3, "/"); // relRscPath - We store forward slash instead of empty string so that it works for all three database types
	   		netFolderTopUpdateStmt.setLong(4, folderId); // id
			status = netFolderTopUpdateStmt.executeUpdate();
			if(status == 0)
				throw new MigrateNetFolderConfigException("Updating failed for the net folder top folder " + folderId);
			logInfo("Migrated/updated the net folder top folder " + folderId);
		}
		else { 
			// This top folder is not a net folder, meaning it is a legacy mirrored folder.
			// For legacy mirrored folder, the treatment is identical for both top and non-top folders.
			migrateLegacyMirroredFolder(mirroredTopFolder, legacyMirroredFolderUpdateStmt);
		}
	}
	
	private void migrateMirroredNonTopFolder(Map<String,Object> mirroredNonTopFolder, PreparedStatement netFolderNonTopUpdateStmt, PreparedStatement legacyMirroredFolderUpdateStmt) throws SQLException, MigrateNetFolderConfigException {
		Long folderId = toLong(mirroredNonTopFolder.get("id"));
		Long zoneId = toLong(mirroredNonTopFolder.get("zoneId"));
		if(zoneId == null)
			throw new MigrateNetFolderConfigException("Non-top folder " + folderId + " is missing zoneId");
		String resourceDriverName = (String) mirroredNonTopFolder.get("resourceDriverName");
		
		NetFolderServer nfs = findNetFolderServerByName(resourceDriverName, zoneId);
		if(nfs != null) { // This non top folder is a net folder.
			// Determine which net folder config this net folder belongs to.
			String resourcePath = (String) mirroredNonTopFolder.get("resourcePath");
			String sortKey = (String) mirroredNonTopFolder.get("binder_sortKey");
			NetFolderConfig netFolderConfig = null;
			for(NetFolderConfig nfc : nfs.netFolderConfigList) {
				if(sortKey.startsWith(nfc.sortKey) && // actually this first condition is sufficient...
						resourcePath.startsWith(nfc.resourcePath)) {
					netFolderConfig = nfc;
					break;
				}
			}
			if(netFolderConfig == null)
				throw new MigrateNetFolderConfigException("Cannot find net folder config that this non-top net folder " + folderId + " belongs to");
			
			String nfcRelativePath = resourcePath.substring(netFolderConfig.resourcePath.length());
			// Remove leading slash or backslash.
			if(nfcRelativePath.startsWith("/") || nfcRelativePath.startsWith("\\"))
				nfcRelativePath = nfcRelativePath.substring(1);
			if("".equals(nfcRelativePath))
				throw new MigrateNetFolderConfigException("Cannot migrate non-top net folder " + folderId + ". For non-top net folder, the path relative to the net folder config cannot be empty");

			netFolderNonTopUpdateStmt.setLong(1, netFolderConfig.id); // netFolderConfigId
			netFolderNonTopUpdateStmt.setString(2, nfcRelativePath); // relRscPath
			netFolderNonTopUpdateStmt.setLong(3, folderId); // id
			int status = netFolderNonTopUpdateStmt.executeUpdate();
			if(status == 0)
				throw new MigrateNetFolderConfigException("Updating failed for the net folder non-top folder " + folderId);
			logInfo("Migrated/updated the net folder non-top folder " + folderId);
		}
		else { 
			// This non top folder is not a net folder, meaning it is a legacy mirrored folder.
			// For legacy mirrored folder, the treatment is identical for both top and non-top folders.
			migrateLegacyMirroredFolder(mirroredNonTopFolder, legacyMirroredFolderUpdateStmt);
		}

	}
	
	private void migrateLegacyMirroredFolder(Map<String,Object> mirroredFolder, PreparedStatement legacyMirroredFolderUpdateStmt) throws SQLException, MigrateNetFolderConfigException {
		Long folderId = toLong(mirroredFolder.get("id"));
		String resourceDriverName = (String) mirroredFolder.get("resourceDriverName");
		legacyMirroredFolderUpdateStmt.setLong(1, toStorageHashAsLong(resourceDriverName)); // legacyMirroredDriverNameHash
		String resourcePath = (String) mirroredFolder.get("resourcePath");
		legacyMirroredFolderUpdateStmt.setString(2, ("".equals(resourcePath))? "/" : resourcePath ); // relRscPath - Carry over old resource path as is
		legacyMirroredFolderUpdateStmt.setLong(3, folderId); // id
		int status = legacyMirroredFolderUpdateStmt.executeUpdate();
		if(status == 0)
			throw new MigrateNetFolderConfigException("Updating failed for the legacy mirrored folder " + folderId);
		logInfo("Migrated/updated the legacy mirrored folder " + folderId);
	}
	
	private void migrateRemainingMirroredNonTopFolders() throws SQLException, MigrateNetFolderConfigException {
		logInfo("Migrating non-top folders (net folders and mirrored folders)");
		List<Map<String,Object>> mirroredNonTopFolders;
		int count = 0;
		while(true) {
			mirroredNonTopFolders = loadMirroredNonTopFoldersOneBatch();
			count += mirroredNonTopFolders.size();
			if(mirroredNonTopFolders.size() > 0) {
				// There is something to process
				migrateMirroredNonTopFolders(mirroredNonTopFolders);
			}
			if(mirroredNonTopFolders.size() < batchSize)
				break; // We're done
		}
		logInfo(count + " non-top folders migrated");
	}
	
	private void migrateMirroredNonTopFolders(List<Map<String,Object>> mirroredNonTopFolders) throws SQLException, MigrateNetFolderConfigException {
		List<Map<String,Object>> sublist;
		int sublistSize;
		int fromIndex = 0;
		int toIndex = 0;
		while(fromIndex < mirroredNonTopFolders.size()) {
			sublistSize = Math.min(transactionSize, mirroredNonTopFolders.size()-fromIndex);
			toIndex = fromIndex + sublistSize;
			sublist = mirroredNonTopFolders.subList(fromIndex, toIndex);
			migrateMirroredNonTopFoldersOneTransaction(sublist);
			fromIndex = toIndex;
		}
	}
	
	private void migrateMirroredNonTopFoldersOneTransaction(List<Map<String,Object>> mirroredNonTopFolders) throws SQLException, MigrateNetFolderConfigException {
		// Do this in a single transaction - Either the whole thing succeeds or fails.
		logInfo("Starting transaction");
		conn.setAutoCommit(false);
		try {			
			String netFolderNonTopUpdateQuery = getNetFolderNonTopUpdateQuery();
			
			String legacyMirroredFolderUpdateQuery = getLegacyMirroredFolderUpdateQuery();
						
			try(PreparedStatement netFolderNonTopUpdateStmt = conn.prepareStatement(netFolderNonTopUpdateQuery)) {
				try(PreparedStatement legacyMirroredFolderUpdateStmt = conn.prepareStatement(legacyMirroredFolderUpdateQuery)) {
					for(Map<String,Object> mirroredNonTopFolder : mirroredNonTopFolders) {
						migrateMirroredNonTopFolder(mirroredNonTopFolder, netFolderNonTopUpdateStmt, legacyMirroredFolderUpdateStmt);
					}
				}
			}
			logInfo("Committing transaction");
			conn.commit();
		}
		catch(Exception e) {
			logError("Rolling back transaction due to error: " + e.toString());
			conn.rollback();
			throw e; // Rethrow to abort the whole process immediately (i.e., fail fast)
		}
		finally {
			conn.setAutoCommit(true);
		}
	}
	
	private NetFolderServer findNetFolderServerById(Long netFolderServerId) {
		for(NetFolderServer netFolderServer:netFolderServerList) {
			if(netFolderServerId.equals(netFolderServer.id))
				return netFolderServer;
		}
		return null;
	}
	
	private NetFolderServer findNetFolderServerByName(String netFolderServerName, long zoneId) {
		for(NetFolderServer netFolderServer:netFolderServerList) {
			if(netFolderServerName.equals(netFolderServer.name) && (zoneId == netFolderServer.zoneId))
				return netFolderServer;
		}
		return null;
	}
	
	private void logInfo(String message) {
		log(message, false);
	}
	
	private void logError(String message) {
		logError(message, null);
	}
	
	private void logError(String message, Exception e) {
		log(message, true);
		if(e != null) {
			e.printStackTrace(log);
			log.flush(); // Flush it immediately
		}
	}
	
	private void log(String message, boolean isError) {
		log.println((new Date()).toString() + " " + ((isError)? "ERROR":"INFO") + " - " + message);
		// We don't want to lose log information when system aborts or crashes abruptly.
		// So flush it immediately after writing each message.
		log.flush();
	}
	
	private long toStorageHashAsLong (String driverName) {
		try {
			// MD5 hash the driver name to 16 byte digest.
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(driverName.getBytes("UTF-8"));
			byte[] digest = messageDigest.digest();
			// Convert the 16 byte digest into a long value.
			BigInteger bi = new BigInteger(digest);		
			long value = bi.longValue();
			// Make sure to return negative value.
			if(value > 0L)
				value *= -1;
			return value;
		}
		catch(Exception e) {
			// This shouldn't happen.
			throw new RuntimeException(e);
		}
	}
	
	private Long toLong(Object obj) {
		if(obj instanceof Long)
			return (Long) obj;
		else if(obj instanceof Number)
			return ((Number)obj).longValue();
		else
			throw new IllegalArgumentException("An object of type [" + obj.getClass().getName() + "] can not be had as Long");
	}

	static class NetFolderServer {
		long id;
		long zoneId;
		String name;
		List<NetFolderConfig> netFolderConfigList;
		
		NetFolderServer(long id, long zoneId, String name) {
			this.id = id;
			this.zoneId = zoneId;
			this.name = name;
			this.netFolderConfigList = new ArrayList<NetFolderConfig>();
		}
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("{id=")
			.append(id)
			.append(",zoneId=")
			.append(zoneId)
			.append(",name=")
			.append(name)
			.append("}");
			return sb.toString();
		}
	}
	
	static class NetFolderConfig {
		long id;
		// Since we can induce zoneId and netFolderServerId from the net folder server object
		// containing this object, we can safely omit those fields to save on memory usage.
		//long zoneId;
		//long netFolderServerId;
		String resourcePath;
		Long topFolderId;
		// Cached sort key of the associated top folder
		String sortKey;
		public NetFolderConfig(long id, String resourcePath, Long topFolderId) {
			this.id = id;
			this.resourcePath = resourcePath;
			this.topFolderId = topFolderId;
		}
		public void setSortKey(String sortKey) {
			this.sortKey = sortKey;
		}
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("{id=")
			.append(id)
			.append(",resourcePath=")
			.append(resourcePath)
			.append(",topFolderId=")
			.append(topFolderId)
			.append("}");
			return sb.toString();
		}
	}
	
	public class MigrateNetFolderConfigException extends Exception {
		private static final long serialVersionUID = 1L;

	    public MigrateNetFolderConfigException(String message) {
	        super(message);
	        logError(message);
	    }
	}
}

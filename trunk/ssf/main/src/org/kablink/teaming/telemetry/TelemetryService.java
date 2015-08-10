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
package org.kablink.teaming.telemetry;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.util.FilterControls;
import org.kablink.teaming.dao.util.Restrictions;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.IndexNode;
import org.kablink.teaming.domain.MobileDevice;
import org.kablink.teaming.domain.NetFolderConfig;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.search.LuceneReadSession;
import org.kablink.teaming.search.LuceneSessionFactory;
import org.kablink.teaming.search.local.LocalLuceneSessionFactory;
import org.kablink.teaming.util.Constants;
import org.kablink.teaming.util.FileHelper;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.util.Validator;
import org.kablink.util.dao.hibernate.DynamicDialect;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @author Jong
 *
 */
public class TelemetryService extends HibernateDaoSupport {

	private CoreDao coreDao;
	private ZoneModule zoneModule;
	private AdminModule adminModule;
	private LuceneSessionFactory luceneSessionFactory;
	
	public void collectAndSaveTelemetryData() throws IOException {
		boolean collectOptin = SPropsUtil.getBoolean("telemetry.optin.enable", false);
		
		String product = getProduct();

		String installationIdentifier = readInstallationIdentifier();
		if(Validator.isNull(installationIdentifier)) {
			installationIdentifier = generateInstallationIdentifier();
			writeInstallationIdentifier(installationIdentifier);
		}
		
		if(logger.isDebugEnabled())
			logger.debug("Collecting telemetry data for '" + product + "' (opt-in=" + collectOptin + ", installation identifider=" + installationIdentifier + ")");

		long currentTime = System.currentTimeMillis();
		
		TelemetryData data = new TelemetryData();
		data.setInstallationIdentifier(installationIdentifier);
		
		// First, collect basic info
		TelemetryDataBasic basic = new TelemetryDataBasic();
		data.setBasic(basic);
		basic.setProductName(ReleaseInfo.getName());
		basic.setProductVersion(ReleaseInfo.getVersion());
		basic.setBuildNumber(ReleaseInfo.getBuildNumber());	
		/*
		Map<String,Long> internalLdapUserCounts = new HashMap<String,Long>();
		List<ZoneInfo> zones = getZoneModule().getZoneInfos();
		for(ZoneInfo zone:zones) {
			internalLdapUserCounts.put(zone.getZoneName(), countActiveLdapUsers(zone.getZoneId()));
		}
		basic.setInternalLdapUserCounts(internalLdapUserCounts);
		*/
		long internalLdapUserCount = countInternalLdapUsers(null);
		basic.setInternalLdapUserCount(internalLdapUserCount);
		
		// Next, collect opt-in info if permitted
		if(collectOptin) {
			TelemetryDataOptin optin = new TelemetryDataOptin();
			data.setOptin(optin);
			
			// Number of application servers (nodes) in the cluster.
			// TODO No easy way to get this info today. We will support this in future release.
			
			// Type of index service and number of index servers in the system
			String indexServiceType; // "ha", "remote", "embedded"
			int indexServerCount;
			List<IndexNode> nodes = getAdminModule().retrieveIndexNodesHA();
			if(nodes != null) {
				// H/A Lucene service
				indexServiceType = "ha";
				indexServerCount = nodes.size();
			}
			else {
				if(getLuceneSessionFactory() instanceof LocalLuceneSessionFactory) {
					// Embedded Lucene service
					indexServiceType = "embedded";
					indexServerCount = 0;
				}
				else {
					// Remote Lucene service
					indexServiceType = "remote";
					indexServerCount = 1;
				}
			}
			IndexService indexService = new IndexService();
			optin.setIndexService(indexService);
			indexService.setType(indexServiceType);
			indexService.setServerCount(indexServerCount);

			// Information about database
			Database database = new Database();
			optin.setDatabase(database);
			String databaseProductName = DynamicDialect.getDatabaseProductName(); // product name given by the vendor
			String databaseProductVersion = DynamicDialect.getDatabaseProductVersion(); // product version given by the vendor
			String databaseType = DynamicDialect.getDatabaseType().name(); // database type as recognized by our system
			database.setProductName(databaseProductName);
			database.setProductVersion(databaseProductVersion);
			database.setType(databaseType);
			
			// Hypervisor type
			String hypervisorType = "unknown";
			if(org.kablink.teaming.util.Utils.checkIfFilr()) {
				try {
					String content = FileHelper.readFirstLine("/opt/novell/base_config/buildformat", Charset.forName("UTF-8"));
					if(content != null) {
						int index = content.indexOf("=");
						if(index >= 0)
							hypervisorType = content.substring(index+1);
					}
				}
				catch(Exception e) {
					if(e instanceof java.nio.file.NoSuchFileException && ReleaseInfo.getBuildNumber() == 0) {
						// This means that the code is executing in a runtime environment that wasn't built
						// or installed from a proper build system (e.g. hand-crafted development system).
						// In this case, absence of this file isn't necessarily unexpected.
						if(logger.isDebugEnabled())
							logger.debug("Cannot read /opt/novell/base_config/buildformat: " + e.toString());						
					}
					else {
						logger.warn("Cannot read /opt/novell/base_config/buildformat", e);
					}
				}
			}
			optin.setHypervisorType(hypervisorType);
			
			// Number of users by type
			User user = new User();
			optin.setUser(user);
			long internalLocalUserCount = countInternalLocalUsers(null);
			long externalLocalUserCount = countExternalLocalUsers(null);
			user.setInternalLocalUserCount(internalLocalUserCount);
			user.setExternalLocalUserCount(externalLocalUserCount);
			
			// Number of groups by type
			Group group = new Group();
			optin.setGroup(group);
			long dynamicGroupCount = countDynamicGroups(null);
			long ldapGroupCount = countLdapGroups(null);
			long containerGroupCount = countContainerGroups(null);
			long localGroupCount = countLocalGroups(null);
			long teamGroupCount = countTeamGroups(null);
			group.setDynamicGroupCount(dynamicGroupCount);
			group.setLdapGroupCount(ldapGroupCount);
			group.setContainerGroupCount(containerGroupCount);
			group.setLocalGroupCount(localGroupCount);
			group.setTeamGroupCount(teamGroupCount);
			
			// Number of files in personal storage
			long personalStorageFileCount = countFilesInPersonalStorage(null);
			optin.setPersonalStorageFileCount(personalStorageFileCount);
			
			// Number of workspaces
			long workspaceCount = countWorkspaces(null);
			optin.setWorkspaceCount(workspaceCount);
			
			// Net folder
			NetFolder netFolder = new NetFolder();
			optin.setNetFolder(netFolder);
			
			// Number of home directories by net folder server type
			Map<ResourceDriverConfig.DriverType,Long> homeDirectoryCounts = countNetFoldersByNetFolderServerType(true);
			
			// Number of non-home-directory net folders by net folder server type
			Map<ResourceDriverConfig.DriverType,Long> nonHomeDirectoryCounts = countNetFoldersByNetFolderServerType(false);
			
			netFolder.setHomeDirectoryCounts(homeDirectoryCounts);
			netFolder.setNonHomeDirectoryCounts(nonHomeDirectoryCounts);
			
			// Number of files and folders per netfolder
			
			// First, get a complete list of IDs of the top folders representing net folders (including home folders).
			List<Long> topFolderIds = getNetFolderTopFolderIds();
			List<Long> fileCounts;
			List<Long> folderCounts;
			if(topFolderIds.size() > 0) {
				// Next, use the search index to compute the number of files and folders in each net folder.
				Map<String,Object> counts;
				LuceneReadSession luceneSession = getLuceneSessionFactory().openReadSession();
				try {
					counts = luceneSession.getNetFolderInfo(topFolderIds);
				} finally {
					luceneSession.close();
				}
				fileCounts = (List<Long>) counts.get(org.kablink.util.search.Constants.NETFOLDER_INFO_FILE_COUNTS);
				folderCounts = (List<Long>) counts.get(org.kablink.util.search.Constants.NETFOLDER_INFO_FOLDER_COUNTS);
			}
			else {
				fileCounts = new ArrayList<Long>();
				folderCounts = new ArrayList<Long>();
			}
			netFolder.setFileCounts(fileCounts);
			netFolder.setFolderCounts(folderCounts);
			
			// Device types connected (iOS mobile, Android mobile, Windows mobile, others)
			Device device = new Device();
			optin.setDevice(device);
			Map<String,Long> mobileDeviceCountsByType = countMobileDevicesByType();
			device.setMobileDeviceCounts(mobileDeviceCountsByType);
		}
		
		String dirPath = SPropsUtil.getDirPath("data.root.dir") + "telemetry" + File.separator + "data";
		FileHelper.mkdirsIfNecessary(dirPath);
		String filePath = dirPath + File.separator + product + "$" + installationIdentifier + "$" + String.valueOf(currentTime) + ".json";
		
		ObjectMapper mapper = new ObjectMapper();

		if(logger.isTraceEnabled()) {
			String telemetryStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
			logger.trace("Saving collected telemetry data in file '" + filePath + "'" + Constants.NEWLINE + telemetryStr);
		} 
		else if(logger.isDebugEnabled()) {
			logger.trace("Saving collected telemetry data in file '" + filePath + "'");
		}

		mapper.writeValue(new File(filePath), data);
	}
	
	public  void uploadTelemetryData() throws IOException {
		String ftpHostname = SPropsUtil.getString("telemetry.ftp.hostname", "ftp.novell.com");
		if(ftpHostname.isEmpty()) {
			// User specified this property without value, which is an instruction to disable
			// sending/uploading of telemetry data.
			if(logger.isDebugEnabled())
				logger.debug("telemetry.ftp.hostname is set to empty string. Will NOT send/upload collected telemetry data.");
			return;
		}
		int ftpPort = SPropsUtil.getInt("telemetry.ftp.port", -1);
		String product = getProduct();
		String ftpDirPath = SPropsUtil.getString("telemetry.ftp.dirpath", (product.equals("filr")? "stats/filr" : "stats/vibe"));
		if(ftpDirPath.length() > 0 && !ftpDirPath.endsWith("/"))
			ftpDirPath += "/";
		String ftpUsername = SPropsUtil.getString("telemetry.ftp.username", "anonymous");
		String ftpPassword = SPropsUtil.getString("telemetry.ftp.password", "fake@fake.com");
		
		FTPClient ftpClient = new FTPClient();
		try {
			// Use 'local passive mode' in which a data connection is made by opening
			// a port on the server for the client to connect, which is usually not
			// blocked by firewall.
			ftpClient.enterLocalPassiveMode();
			if(ftpPort != -1) // port specified
				ftpClient.connect(ftpHostname, ftpPort);
			else // port unspecified, use the default
				ftpClient.connect(ftpHostname);
			ftpClient.login(ftpUsername, ftpPassword);
			
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			
			String dirPath = SPropsUtil.getDirPath("data.root.dir") + "telemetry" + File.separator + "data";
			File[] fileList = new File(dirPath).listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if(name.toLowerCase().endsWith(".json"))
						return true;
					else
						return false;
				}
			});
			Arrays.sort(fileList, new Comparator<File>() {
				@Override
				public int compare(File f1, File f2) {
					// sort in the ascending order of timestamp value in the file name.
					try {
						String n1 = f1.getName().toLowerCase();
						String n2 = f2.getName().toLowerCase();
						n1 = n1.substring(0, n1.lastIndexOf(".json"));
						n2 = n2.substring(0, n2.lastIndexOf(".json"));
						String s1 = n1.substring(n1.lastIndexOf("$")+1);
						String s2 = n2.substring(n2.lastIndexOf("$")+1);
						long t1 = Long.parseLong(s1);
						long t2 = Long.parseLong(s2);
						return Long.compare(t1,t2);
					}
					catch(Exception e) {
						// If anything goes wrong during comparison, simply return 1 and treat
						// the first one as a greater one.
						return 1;
					}
				}			
			});
			for(File file:fileList) {
				String remoteFile = ftpDirPath + file.getName();
				boolean success = false;
				try(InputStream is = new FileInputStream(file);
						BufferedInputStream bis = new BufferedInputStream(is)) {
					success = ftpClient.storeFile(remoteFile, bis);
				}
				if(success) {
					// Now that the file has been uploaded successfully, delete the file.
					file.delete();
				}				
			}
		}
		finally {
			try {
				if(ftpClient.isConnected()) {
					ftpClient.logout();
					ftpClient.disconnect();
				}
			}
			catch(IOException e) {
				// Log the error, but do not allow this error to treat the entire current telemetry cycle as a failure.
				if(logger.isDebugEnabled())
					logger.warn("Error disconnecting from the FTP server", e);
				else 
					logger.warn("Error disconnecting from the FTP server: " + e.toString());
			}
		}
	}
	
	String readInstallationIdentifier() throws IOException {
		String dirPath = SPropsUtil.getDirPath("data.root.dir") + "telemetry";
		FileHelper.mkdirsIfNecessary(dirPath);
		String filePath = dirPath + File.separator + "installationIdentifier";
		if((new File(filePath)).exists()) {
			return FileHelper.readString(filePath, Charset.forName("UTF-8"));
		}
		else {
			return null;
		}
	}
	
	 String generateInstallationIdentifier() {
		return UUID.randomUUID().toString();
	}
	
	 void writeInstallationIdentifier(String installationId) throws IOException {
		String filePath = SPropsUtil.getDirPath("data.root.dir") + "telemetry" + File.separator + "installationIdentifier";
		FileHelper.writeString(filePath, Charset.forName("UTF-8"), installationId);		
	}
	
	// Find all internal users synchronized from LDAP that are not disabled and not deleted.
	 long countInternalLdapUsers( Long zoneId ) {
		FilterControls filterControls = new FilterControls();
	 	filterControls.add( Restrictions.eq( "type", "user" ) );
	 	filterControls.add( Restrictions.eq( "disabled", Boolean.FALSE ) );
	 	filterControls.add( Restrictions.eq( "deleted", Boolean.FALSE ) );
		filterControls.add(Restrictions.eq("identityInfo.internal", Boolean.TRUE));
		filterControls.add(Restrictions.eq("identityInfo.fromLdap", Boolean.TRUE));
		if(zoneId == null)
			filterControls.setZoneCheck(false);

	 	return getCoreDao().countObjects(Principal.class, filterControls, zoneId);
	}
	
	// Find all internal users created in local database that are not disabled and not deleted.
	 long countInternalLocalUsers( Long zoneId ) {
		FilterControls filterControls = new FilterControls();
	 	filterControls.add( Restrictions.eq( "type", "user" ) );
	 	filterControls.add( Restrictions.eq( "disabled", Boolean.FALSE ) );
	 	filterControls.add( Restrictions.eq( "deleted", Boolean.FALSE ) );
		filterControls.add(Restrictions.eq("identityInfo.internal", Boolean.TRUE));
		filterControls.add(Restrictions.eq("identityInfo.fromLocal", Boolean.TRUE));
		filterControls.add(Restrictions.isNull("internalId")); // To filter out system accounts
		if(zoneId == null)
			filterControls.setZoneCheck(false);

	 	return getCoreDao().countObjects(Principal.class, filterControls, zoneId);
	}
	
	// Find all external users created in local database that are not disabled and not deleted.
	 long countExternalLocalUsers( Long zoneId ) {
		FilterControls filterControls = new FilterControls();
	 	filterControls.add( Restrictions.eq( "type", "user" ) );
	 	filterControls.add( Restrictions.eq( "disabled", Boolean.FALSE ) );
	 	filterControls.add( Restrictions.eq( "deleted", Boolean.FALSE ) );
		filterControls.add(Restrictions.eq("identityInfo.internal", Boolean.FALSE));
		filterControls.add(Restrictions.eq("identityInfo.fromLocal", Boolean.TRUE));
		filterControls.add(Restrictions.isNull("internalId")); // To filter out guest system account
		if(zoneId == null)
			filterControls.setZoneCheck(false);

	 	return getCoreDao().countObjects(Principal.class, filterControls, zoneId);
	}
	
	private  FilterControls getBasicControlForGroup() {
		FilterControls filterControls = new FilterControls();
	 	filterControls.add( Restrictions.eq( "type", "group" ) );
	 	filterControls.add( Restrictions.eq( "disabled", Boolean.FALSE ) );
	 	filterControls.add( Restrictions.eq( "deleted", Boolean.FALSE ) );
	 	return filterControls;
	}
	
	// Find all dynamic groups
	 long countDynamicGroups( Long zoneId ) {
		FilterControls filterControls = getBasicControlForGroup();
	 	filterControls.add(Restrictions.isNull("groupType"));
	 	filterControls.add( Restrictions.eq( "dynamic", Boolean.TRUE ) );
		if(zoneId == null)
			filterControls.setZoneCheck(false);

	 	return getCoreDao().countObjects(Principal.class, filterControls, zoneId);
	}
	
	// Find all LDAP groups that are not container
	 long countLdapGroups( Long zoneId ) {
		FilterControls filterControls = getBasicControlForGroup();
	 	filterControls.add(Restrictions.isNull("groupType"));
		filterControls.add(Restrictions.eq("identityInfo.fromLdap", Boolean.TRUE));
	 	filterControls.add( Restrictions.eqOrNull( "ldapContainer", Boolean.FALSE ) );
	 	filterControls.add( Restrictions.eqOrNull( "dynamic", Boolean.FALSE ) );
		if(zoneId == null)
			filterControls.setZoneCheck(false);
	 	return getCoreDao().countObjects(Principal.class, filterControls, zoneId);
	}
	
	// Find all LDAP container groups
	 long countContainerGroups( Long zoneId ) {
		FilterControls filterControls = getBasicControlForGroup();
	 	filterControls.add(Restrictions.isNull("groupType"));
		filterControls.add(Restrictions.eq("identityInfo.fromLdap", Boolean.TRUE));
		filterControls.add(Restrictions.eq("ldapContainer", Boolean.TRUE));
	 	filterControls.add( Restrictions.eqOrNull( "dynamic", Boolean.FALSE ) );
		if(zoneId == null)
			filterControls.setZoneCheck(false);
	 	return getCoreDao().countObjects(Principal.class, filterControls, zoneId);
	}
	
	// Find all local groups that are not teams
	 long countLocalGroups( Long zoneId ) {
		FilterControls filterControls = getBasicControlForGroup();
	 	filterControls.add(Restrictions.isNull("groupType"));
		filterControls.add(Restrictions.eq("identityInfo.fromLocal", Boolean.TRUE));
	 	filterControls.add( Restrictions.eqOrNull( "dynamic", Boolean.FALSE ) );
		filterControls.add(Restrictions.isNull("internalId")); // To filter out system groups such as allusers and allextusers
		if(zoneId == null)
			filterControls.setZoneCheck(false);
	 	return getCoreDao().countObjects(Principal.class, filterControls, zoneId);
	}
	
	// Find all local groups that are teams
	 long countTeamGroups( Long zoneId ) {
		FilterControls filterControls = getBasicControlForGroup();

	 	filterControls.add(Restrictions.eq("groupType", Short.valueOf((short)1)));
	 	// The following clauses are unnecessary
	 	//filterControls.add( Restrictions.eq( "dynamic", Boolean.FALSE ) );
		//filterControls.add(Restrictions.eq("identityInfo.fromLocal", Boolean.TRUE));
		if(zoneId == null)
			filterControls.setZoneCheck(false);

	 	return getCoreDao().countObjects(Principal.class, filterControls, zoneId);
	}
	
		long countFilesInPersonalStorage(Long zoneId) {
			FilterControls filterControls = new FilterControls();
			filterControls.add(Restrictions.eq("type", 'F'));
			filterControls.add(Restrictions.eq("owner.ownerType", "folderEntry"));
			filterControls.add(Restrictions.notEq("repositoryName", "fiAdapter"));
			if (zoneId == null)
				filterControls.setZoneCheck(false);
			return getCoreDao().countObjects(Attachment.class, filterControls,
					zoneId);
		}
		
		long countWorkspaces(Long zoneId) {
			FilterControls filterControls = new FilterControls();
			filterControls.add(Restrictions.eq("binderType", "w"));
			filterControls.add(Restrictions.isNull("internalId")); // To filter out system workspaces
			if (zoneId == null)
				filterControls.setZoneCheck(false);
			return getCoreDao().countObjects(Binder.class, filterControls, zoneId);
		}
		
	Map<ResourceDriverConfig.DriverType,Long> countNetFoldersByNetFolderServerType(final boolean homeDir) {
		Map<ResourceDriverConfig.DriverType,Long> result = new HashMap<ResourceDriverConfig.DriverType,Long>();
		List data = (List)getHibernateTemplate().execute(new HibernateCallback<List>() {
			@Override
			public List doInHibernate(Session session) throws HibernateException {
				Criteria crit = session.createCriteria(NetFolderConfig.class)
						.createAlias("resourceDriverConfig", "rdc")
						.setProjection(Projections.projectionList()
								.add(Projections.groupProperty("rdc.type"))
								.add(Projections.rowCount()));
				if(homeDir) {
					crit.add(org.hibernate.criterion.Restrictions.eq("homeDir", Boolean.TRUE));
				}
				else {
					crit.add(org.hibernate.criterion.Restrictions.or(
							org.hibernate.criterion.Restrictions.eq("homeDir", Boolean.FALSE), 
							org.hibernate.criterion.Restrictions.isNull("homeDir")));
				}
				return crit.list();
			}});
		if(data != null) {
			for(Object o : data) {
				Object[] cols = (Object[]) o;
				ResourceDriverConfig.DriverType driverType = ResourceDriverConfig.DriverType.valueOf(((Integer)cols[0]).intValue());
				result.put(driverType, (Long) cols[1]);
			}
		}
		return result;
	}
	
	long countTotalMobileDevices(Long zoneId) {
		FilterControls filterControls = new FilterControls();
		if (zoneId == null)
			filterControls.setZoneCheck(false);
		return getCoreDao().countObjects(MobileDevice.class, filterControls, zoneId);
	}
	
	/*
	Map<String,Long> countMobileDevicesByType() {
		Map<String,Long> result = new HashMap<String,Long>();
		String[] mobileTypes = SPropsUtil.getStringArray("telemetry.device.mobile.types", ",");
		for(String mobileType:mobileTypes) {
			final String[] mobileKeywords = SPropsUtil.getStringArray("telemetry.device.mobile.keywords." + mobileType.toLowerCase(), ",");
			Long count = (Long)getHibernateTemplate().execute(new HibernateCallback<Long>() {
				@Override
				public Long doInHibernate(Session session) throws HibernateException {
					Disjunction or = org.hibernate.criterion.Restrictions.disjunction();
					for(String mobileKeyword:mobileKeywords) {
						or.add(org.hibernate.criterion.Restrictions.ilike(ObjectKeys.FIELD_MOBILE_DEVICE_DESCRIPTION, mobileKeyword, MatchMode.ANYWHERE));
					}
					Criteria crit = session.createCriteria(MobileDevice.class)
							.setProjection(Projections.rowCount())
							.add(or);
					return (Long) crit.uniqueResult();
				}});
			result.put(mobileType, count);
		}
		return result;
	}
	*/

	Map<String,Long> countMobileDevicesByType() {
		Map<String,Long> result = new HashMap<String,Long>();
		String[] mobileTypes = SPropsUtil.getStringArray("telemetry.device.mobile.types", ",");
		if(mobileTypes.length > 0) {
			long[] counts = new long[mobileTypes.length];
			List<String[]> mobileKeywordsForEachType = new ArrayList<String[]>();
			for(String mobileType:mobileTypes) {
				String[] mobileKeywords = SPropsUtil.getStringArray("telemetry.device.mobile.keywords." + mobileType.toLowerCase(), ",");
				mobileKeywordsForEachType.add(mobileKeywords);
			}
			long otherCount = 0;
			List descriptions = (List)getHibernateTemplate().execute(new HibernateCallback<List>() {
				@Override
				public List doInHibernate(Session session) throws HibernateException {
					Criteria crit = session.createCriteria(MobileDevice.class)
							.setProjection(Projections.property(ObjectKeys.FIELD_MOBILE_DEVICE_DESCRIPTION));
					return crit.list();
				}});
			here:
			for(String description : (List<String>)descriptions) {
				description = description.toLowerCase();
				for(int i = 0; i < mobileTypes.length; i++) {
					String[] mobileKeywords = mobileKeywordsForEachType.get(i);
					for(String mobileKeyword:mobileKeywords) {
						if(description.contains(mobileKeyword)) {
							counts[i]++;
							continue here;
						}
					}
				}
				otherCount++;
			}
			for(int i = 0; i < mobileTypes.length; i++) {
				result.put(mobileTypes[i], counts[i]);
			}
			result.put("other", otherCount);
		}
		return result;
	}

	List<Long> getNetFolderTopFolderIds() {
		return (List)getHibernateTemplate().execute(new HibernateCallback<List>() {
			@Override
			public List doInHibernate(Session session) throws HibernateException {
				Criteria crit = session.createCriteria(NetFolderConfig.class)
						.setProjection(Projections.property(ObjectKeys.FIELD_NET_FOLDER_CONFIG_TOP_FOLDER_ID));
				return crit.list();
			}});
	}
	
	private String getProduct() {
		String product;
		if(Utils.checkIfFilr())
			product = "filr";
		else
			product = "vibe";
		return product;
	}
	
	protected CoreDao getCoreDao() {
		return coreDao;
	}

	public void setCoreDao(CoreDao coreDao) {
		this.coreDao = coreDao;
	}

	protected ZoneModule getZoneModule() {
		return zoneModule;
	}

	public void setZoneModule(ZoneModule zoneModule) {
		this.zoneModule = zoneModule;
	}

	protected AdminModule getAdminModule() {
		return adminModule;
	}

	public void setAdminModule(AdminModule adminModule) {
		this.adminModule = adminModule;
	}

	protected LuceneSessionFactory getLuceneSessionFactory() {
		return luceneSessionFactory;
	}

	public void setLuceneSessionFactory(LuceneSessionFactory luceneSessionFactory) {
		this.luceneSessionFactory = luceneSessionFactory;
	}

	static class TelemetryDataBasic {
		@JsonProperty("productName")
		String productName;
		@JsonProperty("productVersion")
		String productVersion;
		@JsonProperty("buildNumber")
		int buildNumber;
		/*
		@JsonProperty("internalLdapUserCounts")
		Map<String,Long> internalLdapUserCounts;
		*/
		@JsonProperty("internalLdapUserCount")
		long internalLdapUserCount;
		
		public String getProductName() {
			return productName;
		}
		public void setProductName(String productName) {
			this.productName = productName;
		}
		public String getProductVersion() {
			return productVersion;
		}
		public void setProductVersion(String productVersion) {
			this.productVersion = productVersion;
		}
		public int getBuildNumber() {
			return buildNumber;
		}
		public void setBuildNumber(int buildNumber) {
			this.buildNumber = buildNumber;
		}
		/*
		public Map<String, Long> getInternalLdapUserCounts() {
			return internalLdapUserCounts;
		}
		public void setInternalLdapUserCounts(Map<String, Long> internalLdapUserCounts) {
			this.internalLdapUserCounts = internalLdapUserCounts;
		}
		*/
		public long getInternalLdapUserCount() {
			return internalLdapUserCount;
		}
		public void setInternalLdapUserCount(long internalLdapUserCount) {
			this.internalLdapUserCount = internalLdapUserCount;
		}
	}
	
	static class TelemetryDataOptin {
		@JsonProperty("indexService")
		IndexService indexService;
		@JsonProperty("database")
		Database database;
		@JsonProperty("user")
		User user;
		@JsonProperty("group")
		Group group;	
		@JsonProperty("personalStorageFileCount")
		long personalStorageFileCount;
		@JsonProperty("workspaceCount")
		long workspaceCount;
		@JsonProperty("hypervisorType")
		String hypervisorType;
		@JsonProperty("netFolder")
		NetFolder netFolder;
		@JsonProperty("device")
		Device device;

		public IndexService getIndexService() {
			return indexService;
		}
		public void setIndexService(IndexService indexService) {
			this.indexService = indexService;
		}
		public Database getDatabase() {
			return database;
		}
		public void setDatabase(Database database) {
			this.database = database;
		}
		public User getUser() {
			return user;
		}
		public void setUser(User user) {
			this.user = user;
		}
		public Group getGroup() {
			return group;
		}
		public void setGroup(Group group) {
			this.group = group;
		}
		public long getPersonalStorageFileCount() {
			return personalStorageFileCount;
		}
		public void setPersonalStorageFileCount(long personalStorageFileCount) {
			this.personalStorageFileCount = personalStorageFileCount;
		}
		public long getWorkspaceCount() {
			return workspaceCount;
		}
		public void setWorkspaceCount(long workspaceCount) {
			this.workspaceCount = workspaceCount;
		}
		public String getHypervisorType() {
			return hypervisorType;
		}
		public void setHypervisorType(String hypervisorType) {
			this.hypervisorType = hypervisorType;
		}
		public NetFolder getNetFolder() {
			return netFolder;
		}
		public void setNetFolder(NetFolder netFolder) {
			this.netFolder = netFolder;
		}
		public Device getDevice() {
			return device;
		}
		public void setDevice(Device device) {
			this.device = device;
		}
	}
	
	 static class TelemetryData {
		@JsonProperty("installationIdentifier")
		String installationIdentifier;
		@JsonProperty("basic")
		public TelemetryDataBasic basic;
		@JsonProperty("optin")
		public TelemetryDataOptin optin;
		/*
		@JsonProperty("fake")
		List<Long> fake;
		*/
		
		public String getInstallationIdentifier() {
			return installationIdentifier;
		}
		public void setInstallationIdentifier(String installationIdentifier) {
			this.installationIdentifier = installationIdentifier;
		}
		public TelemetryDataBasic getBasic() {
			return basic;
		}
		public void setBasic(TelemetryDataBasic basic) {
			this.basic = basic;
		}
		public TelemetryDataOptin getOptin() {
			return optin;
		}
		public void setOptin(TelemetryDataOptin optin) {
			this.optin = optin;
		}
	}
	 
	static class IndexService {
		@JsonProperty("type")
		String type;
		@JsonProperty("serverCount")
		int serverCount;
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public int getServerCount() {
			return serverCount;
		}
		public void setServerCount(int serverCount) {
			this.serverCount = serverCount;
		}
	}
	
	static class Database {
		@JsonProperty("productName")
		String productName;
		@JsonProperty("productVersion")
		String productVersion;
		@JsonProperty("type")
		String type;
		public String getProductName() {
			return productName;
		}
		public void setProductName(String productName) {
			this.productName = productName;
		}
		public String getProductVersion() {
			return productVersion;
		}
		public void setProductVersion(String productVersion) {
			this.productVersion = productVersion;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
	}
	
	static class User {
		@JsonProperty("internalLocalUserCount")
		long internalLocalUserCount;
		@JsonProperty("externalLocalUserCount")
		long externalLocalUserCount;
		public long getInternalLocalUserCount() {
			return internalLocalUserCount;
		}
		public void setInternalLocalUserCount(long internalLocalUserCount) {
			this.internalLocalUserCount = internalLocalUserCount;
		}
		public long getExternalLocalUserCount() {
			return externalLocalUserCount;
		}
		public void setExternalLocalUserCount(long externalLocalUserCount) {
			this.externalLocalUserCount = externalLocalUserCount;
		}
	}
	
	static class Group {
		@JsonProperty("dynamicGroupCount")
		long dynamicGroupCount;
		@JsonProperty("ldapGroupCount")
		long ldapGroupCount;
		@JsonProperty("containerGroupCount")
		long containerGroupCount;
		@JsonProperty("localGroupCount")
		long localGroupCount;
		@JsonProperty("teamGroupCount")
		long teamGroupCount;
		public long getDynamicGroupCount() {
			return dynamicGroupCount;
		}
		public void setDynamicGroupCount(long dynamicGroupCount) {
			this.dynamicGroupCount = dynamicGroupCount;
		}
		public long getLdapGroupCount() {
			return ldapGroupCount;
		}
		public void setLdapGroupCount(long ldapGroupCount) {
			this.ldapGroupCount = ldapGroupCount;
		}
		public long getContainerGroupCount() {
			return containerGroupCount;
		}
		public void setContainerGroupCount(long containerGroupCount) {
			this.containerGroupCount = containerGroupCount;
		}
		public long getLocalGroupCount() {
			return localGroupCount;
		}
		public void setLocalGroupCount(long localGroupCount) {
			this.localGroupCount = localGroupCount;
		}
		public long getTeamGroupCount() {
			return teamGroupCount;
		}
		public void setTeamGroupCount(long teamGroupCount) {
			this.teamGroupCount = teamGroupCount;
		}
	}

	static class Device {
		@JsonProperty("mobileDeviceCountsByType")
		Map<String,Long> mobileDeviceCounts;

		public Map<String, Long> getMobileDeviceCounts() {
			return mobileDeviceCounts;
		}
		public void setMobileDeviceCounts(Map<String, Long> mobileDeviceCounts) {
			this.mobileDeviceCounts = mobileDeviceCounts;
		}
	}
	
	static class NetFolder {
		@JsonProperty("homeDirectoryCountsByType")
		Map<ResourceDriverConfig.DriverType,Long> homeDirectoryCounts;
		@JsonProperty("nonHomeDirectoryCountsByType")
		Map<ResourceDriverConfig.DriverType,Long> nonHomeDirectoryCounts;
		@JsonProperty("fileCounts")
		List<Long> fileCounts;
		@JsonProperty("folderCounts")
		List<Long> folderCounts;
		
		public Map<ResourceDriverConfig.DriverType, Long> getHomeDirectoryCounts() {
			return homeDirectoryCounts;
		}
		public void setHomeDirectoryCounts(
				Map<ResourceDriverConfig.DriverType, Long> homeDirectoryCounts) {
			this.homeDirectoryCounts = homeDirectoryCounts;
		}
		public Map<ResourceDriverConfig.DriverType, Long> getNonHomeDirectoryCounts() {
			return nonHomeDirectoryCounts;
		}
		public void setNonHomeDirectoryCounts(
				Map<ResourceDriverConfig.DriverType, Long> nonHomeDirectoryCounts) {
			this.nonHomeDirectoryCounts = nonHomeDirectoryCounts;
		}
		public List<Long> getFileCounts() {
			return fileCounts;
		}
		public void setFileCounts(List<Long> fileCounts) {
			this.fileCounts = fileCounts;
		}
		public List<Long> getFolderCounts() {
			return folderCounts;
		}
		public void setFolderCounts(List<Long> folderCounts) {
			this.folderCounts = folderCounts;
		}
	}
	
	public static void main(String[] args) throws Exception {
		TelemetryData data = new TelemetryData();
		data.setInstallationIdentifier(UUID.randomUUID().toString());
		
		/*
		List<Long> fakeData = new ArrayList<Long>() {
			{
			add(3L);
			add(7L);
			add(9L);
			}
		};
		data.fake = fakeData;
		*/
		
		TelemetryDataBasic basic = new TelemetryDataBasic();
		basic.setProductName("Jong Vibe");
		basic.setProductVersion("5.0");
		basic.setBuildNumber(999);
		data.setBasic(basic);
		/*
		Map<String,Long> internalLdapUserCounts = new HashMap<String,Long>() {
				{
					put("kablink", 12L);
					put("myzone", 7L);
				}
		};
		basic.setInternalLdapUserCounts(internalLdapUserCounts);
		*/
		basic.setInternalLdapUserCount(123);

		ObjectMapper mapper = new ObjectMapper();
		System.out.println(mapper.writeValueAsString(data));
		System.out.println();
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data));
		
		Map<String,List<Long>> map = new HashMap<String,List<Long>>();
		List<Long> list = new ArrayList<Long>();
		list.add(10L);
		list.add(20L);
		List<Long> list2 = new ArrayList<Long>();
		list2.add(30L);
		list2.add(40L);
		
		map.put("first", list);
		map.put("second", list2);
		System.out.println(map);
	}
}

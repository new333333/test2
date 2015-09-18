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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.module.ldap.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import org.hibernate.CacheMode;
import org.hibernate.SessionFactory;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.dao.util.FilterControls;
import org.kablink.teaming.dao.util.ObjectControls;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.GroupPrincipal;
import org.kablink.teaming.domain.IdentityInfo;
import org.kablink.teaming.domain.LdapConnectionConfig;
import org.kablink.teaming.domain.LdapConnectionConfig.HomeDirConfig;
import org.kablink.teaming.domain.LdapConnectionConfig.HomeDirCreationOption;
import org.kablink.teaming.domain.LdapSyncException;
import org.kablink.teaming.domain.Membership;
import org.kablink.teaming.domain.NoPrincipalByTheNameException;
import org.kablink.teaming.domain.NoUserByTheNameException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.jobs.LdapSynchronization;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.ldap.ADLdapObject;
import org.kablink.teaming.module.ldap.LdapModule;
import org.kablink.teaming.module.ldap.LdapSchedule;
import org.kablink.teaming.module.ldap.LdapSyncResults;
import org.kablink.teaming.module.ldap.LdapSyncResults.PartialLdapSyncResults;
import org.kablink.teaming.module.ldap.LdapSyncResults.SyncStatus;
import org.kablink.teaming.module.netfolder.NetFolderModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.profile.processor.ProfileCoreProcessor;
import org.kablink.teaming.module.resourcedriver.ResourceDriverModule;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.module.template.TemplateModule;
import org.kablink.teaming.module.workspace.WorkspaceModule;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.search.IndexSynchronizationManager;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.CollectionUtil;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.SessionUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.util.stringcheck.StringCheckUtil;
import org.kablink.teaming.web.util.BuiltInUsersHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.NetFolderHelper;
import org.kablink.util.GetterUtil;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * This implementing class utilizes transactional demarcation strategies that 
 * are finer granularity than typical module implementations, in an effort to
 * avoid lengthy transaction duration that could have occurred if the LDAP
 * database is large and many updates are made during the transaction. To support that,
 * the public methods exposed by this implementation are not transaction 
 * demarcated. Instead, this implementation uses helper methods 
 * for the part of the operations that might
 * require interaction with the database (that is, those operations that 
 * change the state of one or more domain objects) and put the transactional 
 * support around those methods hence reducing individual transaction duration.
 * Of course, this finer granularity transactional control will be of no effect
 * if the caller of this service was already transactional (i.e., it controls
 * transaction boundary that is more coarse). Whenever possible, this practice 
 * is discouraged for obvious performance/scalability reasons.  
 *   
 * @author Janet McCann
 */
@SuppressWarnings({"unchecked", "unused"})
public class LdapModuleImpl extends CommonDependencyInjection implements LdapModule {
	protected Log logger = LogFactory.getLog(getClass());

	private ConcurrentHashMap<Long,List<LdapConnectionConfig>> readOnlyCache = new ConcurrentHashMap<Long, List<LdapConnectionConfig>>();

	public enum LdapDirType
	{
		EDIR,
		AD,
		UNKNOWN
	}
	
	private boolean m_showTiming;
	private int m_numUsersCreated;
	private long m_createUsersStartTime;
	private int m_numUsersModified;
	private long m_createGroupsStartTime;
	private long m_ldapSyncStartTime;

	private static final String GUID_ATTRIBUTE = "GUID";
	private static final String OBJECT_SID_ATTRIBUTE = "objectSid";
	private static final String OBJECT_GUID_ATTRIBUTE = "objectGUID";
	private static final String SAM_ACCOUNT_NAME_ATTRIBUTE = "sAMAccountName";
	private static final String NDS_HOME_DIR_ATTRIBUTE = "ndsHomeDirectory";
	private static final String HOME_DIR_ATTRIBUTE = "homeDirectory";
	private static final String HOST_RESOURCE_NAME_ATTRIBUTE = "hostResourceName";
	private static final String HOST_SERVER_ATTRIBUTE = "hostServer";
	private static final String NETWORK_ADDRESS_ATTRIBUTE = "networkAddress";
	private static final String HOME_DRIVE_ATTRIBUTE = "homeDrive";
	private static final String AD_HOME_DIR_ATTRIBUTE = "homeDirectory";
	private static final String LOGIN_DISABLED_ATTRIBUTE = "loginDisabled";
	private static final String AD_USER_ACCOUNT_CONTROL_ATTRIBUTE = "userAccountControl";
	private static final String PASSWORD_EXPIRATION_TIME_ATTRIBUTE = "passwordExpirationTime";
	private static final String AD_PASSWORD_LAST_SET_ATTRIBUTE = "pwdLastSet";
	private static final String AD_MAX_PWD_AGE_ATTRIBUTE = "maxPwdAge";

	private static int ADDR_TYPE_TCP = 9;
	private static int AD_DISABLED_BIT = 0x02;
	private static int AD_PASSWORD_NEVER_EXPIRES_BIT = 0x00010000;

	private static Pattern m_pattern_uncPath = Pattern.compile( "^\\\\\\\\(.*?)\\\\(.*?)", Pattern.CASE_INSENSITIVE );

	private static Pattern m_pattern_homeDirPath = Pattern.compile( "%[^%]+%", Pattern.CASE_INSENSITIVE );

	protected String [] principalAttrs = new String[]{
												ObjectKeys.FIELD_PRINCIPAL_NAME,
												ObjectKeys.FIELD_ID,
												ObjectKeys.FIELD_PRINCIPAL_DISABLED,
												ObjectKeys.FIELD_INTERNAL,
												ObjectKeys.FIELD_INTERNALID,
												ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME,
												ObjectKeys.FIELD_PRINCIPAL_LDAPGUID};

	protected String [] groupAttrs = new String[]{
												ObjectKeys.FIELD_PRINCIPAL_NAME,
												ObjectKeys.FIELD_ID,
												ObjectKeys.FIELD_PRINCIPAL_DISABLED, 
												ObjectKeys.FIELD_INTERNAL,
												ObjectKeys.FIELD_INTERNALID,
												ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME,
												ObjectKeys.FIELD_PRINCIPAL_LDAPGUID,
												ObjectKeys.FIELD_GROUP_LDAP_CONTAINER };
	
	// The following constants are indexes into the array of Teaming attribute values
	// that are returned from the loadObjects() call.  The value of these constants
	// match the order of the attribute names found in principalAttrs.
	private static final int PRINCIPAL_NAME = 0;
	private static final int PRINCIPAL_ID = 1;
	private static final int PRINCIPAL_DISABLED = 2;
	private static final int PRINCIPAL_INTERNAL = 3;
	private static final int PRINCIPAL_INTERNALID = 4;
	private static final int PRINCIPAL_FOREIGN_NAME = 5;
	private static final int PRINCIPAL_LDAP_GUID = 6;
	private static final int GROUP_LDAP_CONTAINER = 7;
	
	// An ldap sync for a zone should not be started while another ldap sync is running.
	private static Hashtable<Long, Boolean> m_zoneSyncInProgressMap = new Hashtable(); 
	
	protected static final String[] sample = new String[0];
	HashMap defaultProps = new HashMap(); 
	protected HashMap zones = new HashMap();
	protected TransactionTemplate transactionTemplate;
	protected ProfileModule profileModule;
	protected DefinitionModule definitionModule;
	protected TemplateModule templateModule;
	protected BinderModule binderModule;
	protected FolderModule folderModule;
	protected NetFolderModule netFolderModule;
	protected AdminModule adminModule;
	protected ResourceDriverModule resourceDriverModule;
	protected SessionFactory sessionFactory;
	protected static String GROUP_ATTRIBUTES="groupAttributes";
	protected static String MEMBER_ATTRIBUTES="memberAttributes";
	protected static String SYNC_JOB="ldap.job"; //properties in xml file need a unique name
	private ContainerCoordinator m_containerCoordinator;

	// We cache server host names so we don't have to call InetAddress.getByName() over and over
	// Key: ip address
	// Value: host name
	Map<String, String> m_hostNameMap = new HashMap<String, String>();
	
	// As we create net folder servers, we will put the server and vol information in
	// m_server_vol_map.
	// Key: server name / volume name
	// Value: ResourceDriverConfig object
	Map<String, ResourceDriverConfig> m_server_vol_map = new HashMap<String, ResourceDriverConfig>();
	
	// Contains a list of principals that need to be re-indexed;
	Set<Long> m_principalsToIndex = new HashSet<Long>();
	

	/**
	 * 
	 */
	public class HomeDirInfo {
		// If m_netFolderServerName has a value then we use that value to get the net folder server
		// that should be used for the home directory net folder.  Otherwise, use m_serverAddr and
		// m_volume
		private String m_netFolderServerName;
		private String m_serverHostName;
		private String m_serverAddr;
		private String m_volume;
		private String m_path;

		/**
		 * 
		 */
		public HomeDirInfo() {
			m_netFolderServerName = null;
			m_serverHostName = null;
			m_serverAddr = null;
			m_volume = null;
			m_path = null;
		}

		/**
		 * 
		 */
		public String getNetFolderServerName()
		{
			return m_netFolderServerName;
		}
		
		/**
		 * 
		 */
		public String getPath() {
			return m_path;
		}

		/**
		 * 
		 */
		public String getServerAddr() {
			return m_serverAddr;
		}
		
		/**
		 * 
		 */
		public String getServerHostName()
		{
			return m_serverHostName;
		}

		/**
		 * 
		 */
		public String getVolume() {
			return m_volume;
		}

		/**
		 * 
		 */
		public void setNetFolderServerName( String name )
		{
			m_netFolderServerName = name;
		}
		
		/**
		 * 
		 */
		public void setPath(String path) {
			m_path = path;
		}

		/**
		 * 
		 */
		public void setServerAddr(String addr) {
			m_serverAddr = addr;
		}
		
		/**
		 * 
		 */
		public void setServerHostName( String hostName )
		{
			m_serverHostName = hostName;
		}

		/**
		 * 
		 */
		public void setVolume(String volume) {
			m_volume = volume;
		}
	}

	/**
	 * Holds information about an AD group that needs its membership sync'd.
	 */
	private class ADGroup
	{
		private String m_guid;
		private String m_objectSid;
		private String m_name;
		private Long m_dbId;
		
		/**
		 * 
		 */
		public ADGroup( String guid, String objectSid, String name, Long dbId )
		{
			m_guid = guid;
			m_objectSid = objectSid;
			m_name = name;
			m_dbId = dbId;
		}
		
		/**
		 * 
		 */
		public Long getDbId()
		{
			return m_dbId;
		}
		
		/**
		 * 
		 */
		public String getGuid()
		{
			return m_guid;
		}

		/**
		 * 
		 */
		public String getName()
		{
			return m_name;
		}
		
		/**
		 * 
		 */
		public String getObjectSid()
		{
			return m_objectSid;
		}
	}
	

	
	public LdapModuleImpl () {
		defaultProps.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		defaultProps.put(Context.SECURITY_AUTHENTICATION, "simple");
		
		m_containerCoordinator = new ContainerCoordinator();
	}
	
    protected TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	public ProfileModule getProfileModule() {
		return profileModule;
	}
	public void setProfileModule(ProfileModule profileModule) {
		this.profileModule = profileModule;
	}
	
	public DefinitionModule getDefinitionModule() {
		return definitionModule;
	}
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}

	/**
	 * 
	 */
	public TemplateModule getTemplateModule()
	{
		return templateModule;
	}
	
	/**
	 * 
	 */
	public void setTemplateModule( TemplateModule templateModule )
	{
		this.templateModule = templateModule;
	}
	
	/**
	 * 
	 */
	public BinderModule getBinderModule()
	{
		return binderModule;
	}
	
	/**
	 * 
	 */
	public void setBinderModule( BinderModule binderModule )
	{
		this.binderModule = binderModule;
	}
	
	/**
	 * 
	 */
	public FolderModule getFolderModule()
	{
		return folderModule;
	}
	
	/**
	 * 
	 */
	public void setFolderModule( FolderModule folderModule )
	{
		this.folderModule = folderModule;
	}
	
	/**
	 * 
	 * @param folderModule
	 */
	public void setNetFolderModule( NetFolderModule netFolderModule )
	{
		this.netFolderModule = netFolderModule;
	}

	/**
	 * 
	 * @return
	 */
	protected NetFolderModule getNetFolderModule()
	{
		return netFolderModule;
	}

	/**
	 * 
	 */
	public AdminModule getAdminModule()
	{
		return adminModule;
	}
	
	/**
	 * 
	 */
	public void setAdminModule( AdminModule adminModule )
	{
		this.adminModule = adminModule;
	}

	/**
	 * 
	 */
	public ResourceDriverModule getResourceDriverModule()
	{
		return resourceDriverModule;
	}
	
	/**
	 * 
	 */
	public void setResourceDriverModule( ResourceDriverModule resourceDriverModule )
	{
		this.resourceDriverModule = resourceDriverModule;
	}

	/**
	 * @return
	 */
	private static ZoneModule getZoneModule()
	{
		return (ZoneModule) SpringContextUtil.getBean( "zoneModule" );
	}// end getZoneModule()
	
	protected SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public boolean testAccess(LdapOperation operation) {
		try {
			checkAccess(operation);
			return true;
		} catch (AccessControlException ac) {
			return false;
		}
	}
	protected void checkAccess(LdapOperation operation) {
		getAccessControlManager().checkOperation(getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId()), WorkAreaOperation.ZONE_ADMINISTRATION);
	}


	/**
	 * Read the "ndsHomeDirectory" attribute from eDir.  If the "ndsHomeDirectory" attribute does not exist
	 * or does not contain a value, read the "homeDirectory" attribute.
	 */
	private Object readHomeDirectoryAttributeFromEdir(
		LdapContext ldapContext,
		String userDn,
		boolean logErrors )
	{
		Object value = null;
		
		if ( ldapContext == null || userDn == null )
		{
			logger.error( "Invalid arguments passed to readHomeDirectoryAttributeFromEdir()" );
			return null;
		}
		
		try
		{
			String[] attributeNames;
			Attributes attrs;
			Attribute attrib;

			// Read the "ndsHomeDirectory" attribute.
			attributeNames = new String[1];
			attributeNames[0] = NDS_HOME_DIR_ATTRIBUTE;
			attrs = ldapContext.getAttributes( userDn, attributeNames );
			if ( attrs != null )
			{
				attrib = attrs.get( NDS_HOME_DIR_ATTRIBUTE );
				if ( attrib != null )
				{
					Object tmpValue;
					
					tmpValue = attrib.get();
					if ( tmpValue != null && tmpValue instanceof byte[] )
						value = tmpValue;
				}
			}
			
			// Did we find an "ndsHomeDirectory" attribute value?
			if ( value == null )
			{
				// No
				// Read the "homeDirectory" attribute.
				attributeNames[0] = HOME_DIR_ATTRIBUTE;
				attrs = ldapContext.getAttributes( userDn, attributeNames );
				if ( attrs != null )
				{
					attrib = attrs.get( HOME_DIR_ATTRIBUTE );
					if ( attrib != null )
					{
						Object tmpValue;
						
						tmpValue = attrib.get();
						if ( tmpValue != null && tmpValue instanceof byte[] )
							value = tmpValue;
					}
				}
			}
		}
		catch ( Exception ex )
		{
			if ( logErrors || logger.isDebugEnabled() )
			{
				logger.error( "Error reading ndsHomeDirectory attribute for user: " + userDn + " ", ex );
			}
		}
		
		return value;
	}
	
	/**
	 * Read all of the information about the given user's home directory; server
	 * address, volume and path from the ldap directory
	 */
	private HomeDirInfo getHomeDirInfoFromConfig(
		LdapContext ldapContext,
		LdapDirType dirType,
		String userDn,
		HomeDirConfig homeDirConfig,
		boolean logErrors )
	{
		HomeDirInfo homeDirInfo = null;
		HomeDirCreationOption creationOption = HomeDirCreationOption.USE_HOME_DIRECTORY_ATTRIBUTE;
		
		// Figure out how we are suppose to get home directory information.
		if ( homeDirConfig != null )
			creationOption = homeDirConfig.getCreationOption();
		
		logger.debug("In getHomeDirInfoFromConfig() for user: " + MiscUtil.getSafeLogString(userDn));
		logger.debug("\tcreationOption:  " + creationOption.name()      );
		
		switch ( creationOption )
		{
		case DONT_CREATE_HOME_DIR_NET_FOLDER:
			return null;
			
		case USE_CUSTOM_ATTRIBUTE:
			homeDirInfo = getHomeDirInfoFromCustomAttribute(
														ldapContext,
														dirType,
														userDn,
														homeDirConfig,
														logErrors );
			break;
			
		case USE_CUSTOM_CONFIG:
			homeDirInfo = getHomeDirInfoFromCustomConfig(
														ldapContext,
														dirType,
														userDn,
														homeDirConfig,
														logErrors );
			break;
			
		case USE_HOME_DIRECTORY_ATTRIBUTE:
		case UNKNOWN:
		default:
			homeDirInfo = getHomeDirInfoFromHomeDirAttribute( ldapContext, dirType, userDn, logErrors );
			break;
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("\thas homeDirInfo:  " + ((null == homeDirInfo) ? "no" : "yes"));
			if (null != homeDirInfo) {
				logger.debug("\t\thomeDirInfo.m_netFolderServerName:  " + MiscUtil.getSafeLogString(homeDirInfo.m_netFolderServerName));
				logger.debug("\t\thomeDirInfo.m_serverHostName:  "      + MiscUtil.getSafeLogString(homeDirInfo.m_serverHostName)     );
				logger.debug("\t\thomeDirInfo.m_serverAddr:  "          + MiscUtil.getSafeLogString(homeDirInfo.m_serverAddr)         );
				logger.debug("\t\thomeDirInfo.m_volume:  "              + MiscUtil.getSafeLogString(homeDirInfo.m_volume)             );
				logger.debug("\t\thomeDirInfo.m_path:  "                + MiscUtil.getSafeLogString(homeDirInfo.m_path)               );
			}
		}
		
		return homeDirInfo;
	}

	/**
	 * Read all of the information about the given user's home directory; server
	 * address, volume and path from the either the ndsHomeDirectory attribute (eDir)
	 * or homeDirectory attribute (Active Directory) in ldap directory.
	 */
	private HomeDirInfo getHomeDirInfoFromHomeDirAttribute(
		LdapContext ldapContext,
		LdapDirType dirType,
		String userDn,
		boolean logErrors )
	{
		HomeDirInfo homeDirInfo = null;

		if ( dirType == LdapDirType.EDIR )
		{
			try
			{
				Object value;
				
				// Read the "ndsHomeDirectory" attribute
				value = readHomeDirectoryAttributeFromEdir( ldapContext, userDn, logErrors );
				
				// Does the "ndsHomeDirectory" attribute have a value?
				if ( value != null && value instanceof byte[] )
				{
					int i;
					int index;
					String strValue;
					StringBuffer strBuffer;
					boolean validFormat;
					
					homeDirInfo = new HomeDirInfo();
					strBuffer = new StringBuffer();
					strValue = new String( (byte[]) value );
					
					// Is the format of the value found in the home directory attribute valid?
					// The string should be of the format, "dn of volume object#namespace value#home dir path"
					// For example, "cn=test_vol1,o=novell#0#home\Peter"
					validFormat = false;
					index = strValue.indexOf( '#' );
					if ( index > 0 )
					{
						index = strValue.indexOf( '#', index+1 );
						if ( index > 0 )
							validFormat = true;
					}
					
					if ( validFormat )
					{
						// Get the dn of the volume object
						{
							String volumeDn;
	
							i = 0;
							while ( i < strValue.length() )
							{
								char ch;
								
								ch = strValue.charAt( i );
								++i;
								
								if ( ch == '#' )
									break;
								
								strBuffer.append( ch );
							}
							
							volumeDn = strBuffer.toString();
							if ( volumeDn != null && volumeDn.length() > 0 )
							{
								readVolumeAndServerInfoFromLdap( ldapContext, dirType, volumeDn, homeDirInfo, logErrors );
							}
						}
						
						// Skip over the namespace value
						{
							while ( i < strValue.length() )
							{
								char ch;
								
								ch = strValue.charAt( i );
								++i;
								
								if ( ch == '#' )
									break;
							}
						}
						
						// Get the name of the user's home directory
						{
							String dirName;
							int numChars;
							
							strBuffer = new StringBuffer();
							numChars = 0;
	
							while ( i < strValue.length() )
							{
								char ch;
								
								ch = strValue.charAt( i );
								++i;
								
								// We don't want a \ as the first character in the name
								if ( numChars == 0 && ch == '\\' )
									continue;
								
								strBuffer.append( ch );
								++numChars;
							}
							
							dirName = strBuffer.toString();
							homeDirInfo.setPath( dirName );
						}
					}
					else
					{
						if ( logErrors || logger.isDebugEnabled() )
							logger.error( "The value found in the home directory attribute is not in the correct format for the user: " + userDn + " \nhome directory value: " + strValue );
					}
				}
			}
			catch ( Exception ex )
			{
				if ( logErrors || logger.isDebugEnabled() )
				{
					logger.error( "Error reading ndsHomeDirectory attribute for user: " + userDn, ex );
				}
			}
		}
		else if ( dirType == LdapDirType.AD )
		{
			String[] attributeNames;
			Attributes attrs;
			Attribute attrib;

			logger.debug( "\t\tin readHomeDirInfoFromLdap() for user: " + userDn );
			
			attributeNames = new String[3];
			attributeNames[0] = HOME_DRIVE_ATTRIBUTE;
			attributeNames[1] = AD_HOME_DIR_ATTRIBUTE;
			attributeNames[2] = SAM_ACCOUNT_NAME_ATTRIBUTE;
			
			try
			{
				Object value;
				String homeDrive;
				String uncPath;
				
				// Read the "homeDrive" and "homeDirectory" attributes from the given user
				attrs = ldapContext.getAttributes( userDn, attributeNames );
				if ( attrs == null )
					return null;
				
				// Get the homeDrive attribute
				attrib = attrs.get( HOME_DRIVE_ATTRIBUTE );
				if ( attrib == null )
					return null;
				
				value = attrib.get();
				if ( value == null || (value instanceof String) == false )
					return null;
				
				// If homeDrive is not empty it means that homeDirectory contains a unc path.
				// Otherwise, homeDirectory is a fully qualified local path including the drive
				// letter which would be useless to FAMT.
				homeDrive = (String) value;
				if ( homeDrive.length() == 0 )
					return null;
				
				// Get the homeDirectory attribute.
				attrib = attrs.get( AD_HOME_DIR_ATTRIBUTE );
				if ( attrib == null )
					return null;
				
				value = attrib.get();
				if ( value == null || (value instanceof String) == false )
					return null;
				
				// Do we have a unc path to the user's home directory?
				uncPath = (String) value;
				if ( uncPath.length() == 0 )
					return null;
				
				logger.debug( "\t\t\tfound home directory info for the user, uncPath: " + uncPath );
				
				// Parse the unc path into its components, server, volume, path
				homeDirInfo = parseUncPath( uncPath, logErrors );
			}
			catch ( Exception ex )
			{
				if ( logErrors || logger.isDebugEnabled() )
				{
					logger.error( "Error reading homeDrive and homeDirectory attributes from the user: " + userDn, ex );
				}
			}
		}
		
		return homeDirInfo;
	}

	/**
	 * Read the value of the custom attribute defined in HomeDirConfig and extract the server,
	 * volume (or share) and path.
	 */
	private HomeDirInfo getHomeDirInfoFromCustomAttribute(
		LdapContext ldapContext,
		LdapDirType dirType,
		String userDn,
		HomeDirConfig homeDirConfig,
		boolean logErrors )
	{
		String attributeName;
		String attributeValue;
		HomeDirInfo homeDirInfo;
		
		if ( homeDirConfig == null )
			return null;
		
		attributeName = homeDirConfig.getAttributeName();
		if ( attributeName == null || attributeName.length() == 0 )
		{
			if ( logErrors )
				logger.error( "In getHomeDirInfoFromCustomAttribute(), attribute name is null" );
			
			return null;
		}
		
		// Read the value of the given attribute.
		attributeValue = readStringAttribute( ldapContext, userDn, attributeName, logErrors );
		if ( attributeValue == null || attributeValue.length() == 0 )
		{
			if ( logErrors )
				logger.error( "In getHomeDirInfoFromCustomAttribute(), attribute value is null" );
			
			return null;
		}
		
		// Parse the unc path into its components, server, volume, path
		homeDirInfo = parseUncPath( attributeValue, logErrors );
		
		return homeDirInfo;
	}
	
	/**
	 * Construct the information needed to create a user's home dir net folder from
	 * the information found in HomeDirConfig.
	 */
	private HomeDirInfo getHomeDirInfoFromCustomConfig(
		LdapContext ldapContext,
		LdapDirType dirType,
		String userDn,
		HomeDirConfig homeDirConfig,
		boolean logErrors )
	{
		String netFolderServerName;
		String path;
		HomeDirInfo homeDirInfo;
		
		if ( homeDirConfig == null )
			return null;
		
		netFolderServerName = homeDirConfig.getNetFolderServerName();
		path = homeDirConfig.getPath();
//~JW:	netFolderServerName = "net-folder-server-2";
//~JW:	path = "Language\\%Language%\\Cn\\%cn%\\Mail\\%mail%\\Cn\\%cn%";
		
		if ( netFolderServerName == null || netFolderServerName.length() == 0 )
		{
			if ( logErrors )
				logger.error( "In getHomeDirInfoFromCustomConfig(), netFolderServerName is null" );
			
			return null;
		}

		if ( path == null || path.length() == 0 )
		{
			if ( logErrors )
				logger.error( "In getHomeDirInfoFromCustomConfig(), path is null" );
			
			return null;
		}
		
		homeDirInfo = new HomeDirInfo();
		
		// Find the net folder server we are supposed to use.
		{
			ResourceDriverConfig netFolderServer;
			
			netFolderServer = NetFolderHelper.findNetFolderRootByName(
																getAdminModule(),
																getResourceDriverModule(),
																netFolderServerName );
			if ( netFolderServer == null )
			{
				if ( logErrors )
					logger.error( "In readHomeDirInfoUsingCustomConfig(), net folder server not found by the name: " + netFolderServerName );
				
				return null;
			}
			
			homeDirInfo.setNetFolderServerName( netFolderServerName );
		}
		
		// Generate the path that will be used by the home dir net folder.
		{
			Matcher matcher;
			
			// The path can contain a construct like, foo/%ldapAttributeName%/bar
			// We need to read the value of the "ldapAttributeName" and replace "ldapAttributeName"
			// with the value we read from the directory.
		    matcher = m_pattern_homeDirPath.matcher( path );
		    while ( matcher.find() )
		    {
	    		String token;
	    		
	    		token = matcher.group();
	    		if ( token != null )
	    		{
	    			String attribValue;
	    			String attribName;
	    			
	    			// Remove the % from the token to get the attribute name.
	    			attribName = token.replaceAll( "%", "" );
	    			
	    			// Read the value of the attribute from ldap.
	    			attribValue = readStringAttribute( ldapContext, userDn, attribName, logErrors );

	    			if ( attribValue == null )
	    				attribValue = "";
	    			
    				path = path.replaceAll( token, attribValue );
	    			
	    			matcher = m_pattern_homeDirPath.matcher( path );
	    		}
			}
		    
		    homeDirInfo.setPath( path );
		}
		
		return homeDirInfo;
	}
	
	/**
	 * Read the information from the given Server dn
	 */
	private void readServerInfoFromLdap(
		LdapContext ldapContext,
		LdapDirType dirType,
		String serverDn,
		HomeDirInfo homeDirInfo,
		boolean logErrors )
	{
		String serverAddr = null;
		
		if ( dirType == LdapDirType.EDIR )
		{
			String[] attributeNames;
			Attributes attrs;
			Attribute attrib;
			
			attributeNames = new String[1];
			attributeNames[0] = NETWORK_ADDRESS_ATTRIBUTE;
			
			try
			{
				// Read the "networkAddress" attribute from the given server dn.
				attrs = ldapContext.getAttributes( serverDn, attributeNames );
				if ( attrs != null )
				{
					int numValues;
					
					// Get the attribute that holds the server's network address
					attrib = attrs.get( NETWORK_ADDRESS_ATTRIBUTE );
					numValues = attrib.size();
					if ( attrib != null && numValues > 0 )
					{
						NamingEnumeration valEnum;
						
						// networkAddress is a multi-valued attribute.
						// Go through each value.
						valEnum = attrib.getAll();
						while ( valEnum != null && valEnum.hasMoreElements() && serverAddr == null )
						{
							Object nextValue;
							StringBuffer strBuffer;
							byte[] nextByteArray;
							
							nextValue = valEnum.nextElement();
							if ( nextValue != null && nextValue instanceof byte[] )
							{
								int i;
								int addrType = -1;
								
								nextByteArray = (byte[]) nextValue;
								i = 0;
								
								// Get the address type
								{
									strBuffer = new StringBuffer();
									
									while ( i < nextByteArray.length )
									{
										char ch;
										
										ch = (char) nextByteArray[i];
										++i;
										
										if ( ch == '#' )
											break;
										
										strBuffer.append( ch );
									}

									if ( strBuffer.length() > 0 )
									{
										try
										{
											addrType = Integer.parseInt( strBuffer.toString() );
										}
										catch ( NumberFormatException ex )
										{
											if ( logErrors || logger.isDebugEnabled() )
												logger.error( "error parsing address type from network address attribute" );
										}
									}
								}
								
								// Get the server address
								if ( addrType == ADDR_TYPE_TCP )
								{
									strBuffer = new StringBuffer();
									
									// Skip the first 2 bytes.  Don't know what they are for
									i += 2;
									while ( i < nextByteArray.length )
									{
										int ch;
										
										ch = nextByteArray[i];
										++i;
										
										if ( ch < 0 )
											ch += 256;
										
										strBuffer.append( ch );
										
										// Add a "." if we are not at the end
										if ( i < nextByteArray.length )
											strBuffer.append( '.' );
									}
									
									serverAddr = strBuffer.toString();
								}
							}
						}
					}
				}
			}
			catch ( Exception ex )
			{
				if ( logErrors || logger.isDebugEnabled() )
				{
					logger.error( "Error reading attributes from the server object: " + serverDn, ex );
				}
			}
		}
		
		if ( serverAddr != null && serverAddr.length() > 0 )
		{
			String hostName;
			
			// Have we already done a reverse dns lookup on this ip address?
			hostName = m_hostNameMap.get( serverAddr );
			if ( hostName == null )
			{
				// No, do a reverse dns lookup on the ip address.
				try
				{
					InetAddress addr;
					
					addr = InetAddress.getByName( serverAddr );
					if ( addr != null )
					{
						hostName = addr.getHostName();
						
						m_hostNameMap.put( serverAddr,  hostName );
					}
				}
				catch ( Exception ex )
				{
					
				}
			}
			
			if ( hostName != null && hostName.length() > 0 )
			{
				homeDirInfo.setServerHostName( hostName );
			}
				
		}
		
		homeDirInfo.setServerAddr( serverAddr );
	}
	
	/**
	 * Read the value of the given string attribute from the given user 
	 */
	private String readStringAttribute(
		LdapContext ldapContext,
		String userDn,
		String attribName,
		boolean logErrors )
	{
		String attribValue = null;
		
		try
		{
			Object value;
			String[] attributeNames;
			Attributes attrs;
			Attribute attrib;

			attributeNames = new String[1];
			attributeNames[0] = attribName;
			
			// Read the given attribute from the given user
			attrs = ldapContext.getAttributes( userDn, attributeNames );
			if ( attrs == null )
				return null;
	
			// Get the attribute.
			attrib = attrs.get( attribName );
			if ( attrib == null )
				return null;
			
			value = attrib.get();
			if ( value == null || (value instanceof String) == false )
				return null;
			
			attribValue = (String) value;
		}
		catch ( Exception ex )
		{
			if ( logErrors )
			{
				logger.error( "In readStringAttribute(), exception: ", ex );
			}
		}
		
		return attribValue;
	}
	

	/**
	 * Read the information from the given Volume object
	 */
	private HomeDirInfo readVolumeAndServerInfoFromLdap(
		LdapContext ldapContext,
		LdapDirType dirType,
		String volumeDn,
		HomeDirInfo homeDirInfo,
		boolean logErrors )
	{
		
		if ( dirType == LdapDirType.EDIR )
		{
			String[] attributeNames;
			Attributes attrs;
			Attribute attrib;
			
			attributeNames = new String[2];
			attributeNames[0] = HOST_RESOURCE_NAME_ATTRIBUTE;
			attributeNames[1] = HOST_SERVER_ATTRIBUTE;
			
			try
			{
				// Read the "hostResourceName" and "hostServer" attributes from the given volume dn.
				attrs = ldapContext.getAttributes( volumeDn, attributeNames );
				if ( attrs != null )
				{
					// Get the attribute that holds the volume name
					attrib = attrs.get( HOST_RESOURCE_NAME_ATTRIBUTE );
					if ( attrib != null )
					{
						Object value;
						
						value = attrib.get();
						if ( value != null && value instanceof String )
						{
							homeDirInfo.setVolume( (String) value );
						}
					}
					
					// Get the attribute that holds the dn of the server object.
					attrib = attrs.get( HOST_SERVER_ATTRIBUTE );
					if ( attrib != null )
					{
						Object value;
						
						value = attrib.get();
						if ( value != null && value instanceof String )
						{
							String serverDn;
							
							// Read the server's address from the given server dn.
							serverDn = (String) value;
							if ( serverDn.length() > 0 )
							{
								readServerInfoFromLdap( ldapContext, dirType, serverDn, homeDirInfo, logErrors );
							}
						}
					}
				}
			}
			catch ( Exception ex )
			{
				if ( logErrors || logger.isDebugEnabled() )
				{
					logger.error( "Error reading attributes from the volume object: " + volumeDn, ex );
				}
			}
		}
		
		return homeDirInfo;
	}

	/**
	 * Execute the given ldap query.  For each user found by the query, if the user already exists
	 * in Vibe then add them to the list.
	 * @author jwootton
	 */
	@Override
	public HashSet<User> getDynamicGroupMembers( String baseDn, String filter, boolean searchSubtree ) throws LdapSyncException
	{
		HashSet<User> listOfMembers;
		int pageSize = 1500;
		
		pageSize = SPropsUtil.getInt( "ldap.sync.dynamic.group.membership.page.size", 1500 );

		listOfMembers = new HashSet<User>();
		
		// Does the membership criteria have a filter?
		if ( filter != null && filter.length() > 0 )
		{
			List<LdapConnectionConfig> ldapConnectionConfigs;
			Workspace zone;
			Long zoneId;
			ProfileModule profileModule;

			// Yes
			// Remove all carriage returns and line feeds
			{
				filter = filter.replaceAll( "\r", "" );
				filter = filter.replaceAll( "\n", "" );
			}
			
			profileModule = getProfileModule();
			
			zone = RequestContextHolder.getRequestContext().getZone();
			zoneId = zone.getId();

			// Get the list of ldap configurations.
			ldapConnectionConfigs = getCoreDao().loadLdapConnectionConfigs( zoneId );
			
			// Go through each ldap configuration
			for( LdapConnectionConfig nextLdapConfig : ldapConnectionConfigs )
			{
				String ldapGuidAttribute;

				// Does this ldap configuration have the ldap guid defined?
				ldapGuidAttribute = nextLdapConfig.getLdapGuidAttribute();
				if ( ldapGuidAttribute != null && ldapGuidAttribute.length() > 0 )
				{
			   		LdapContext ldapContext;
					byte[] cookie = null;

					// Yes
			   		ldapContext = null;
			   		
			  		try
			  		{
						String[] ldapAttributesToRead = { ldapGuidAttribute };
						int scope;
						SearchControls searchControls;

						// Get an ldap context for the given ldap configuration
						ldapContext = getUserContext( zoneId, nextLdapConfig );
						
						if ( searchSubtree )
							scope = SearchControls.SUBTREE_SCOPE;
						else
							scope = SearchControls.ONELEVEL_SCOPE;
						
						searchControls = new SearchControls( scope, 0, 0, ldapAttributesToRead, false, false );

						// Request the paged results control
						try
						{
							Control[] ctls = new Control[]{ new PagedResultsControl( pageSize, true ) };
							ldapContext.setRequestControls( ctls );
						}
						catch ( IOException ex )
						{
							logger.error( "In getDynamicGroupMembers(), call to new PagedResultsControl() threw an exception: ", ex );
							
							return listOfMembers;
						}

						do
						{
							NamingEnumeration results;

							// Issue an ldap search for users in the given base dn.
							results = ldapContext.search( baseDn, filter, searchControls );
							
							// loop through the results in each page
							while ( hasMore( results ) )
							{
								SearchResult sr;
								Attributes lAttrs = null;
								String guid;
								User user;
								
								// Get the next user/group in the list.
								sr = (SearchResult)results.next();
	
								// Read the guid for this user/group from the ldap directory.
								lAttrs = sr.getAttributes();
								guid = getLdapGuid( lAttrs, ldapGuidAttribute );

								// Does this user exist in Vibe.
								try
								{
									user = profileModule.findUserByLdapGuid( guid );
									if ( user != null )
									{
										// Yes, add them to the membership list.
										listOfMembers.add( user );
									}
								}
								catch ( NoUserByTheNameException nuEx )
								{
									// Nothing to do.
								}
							}
							
							// examine the response controls
							cookie = parseControls( ldapContext.getResponseControls() );

							try
							{
								// pass the cookie back to the server for the next page
								PagedResultsControl prCtrl;
								
								prCtrl = new PagedResultsControl( pageSize, cookie, Control.CRITICAL );
								ldapContext.setRequestControls( new Control[]{ prCtrl } );
							}
							catch ( IOException ex )
							{
								cookie = null;
								logger.error( "In getDynamicGroupMembers(), call to new PagedResultsControl() threw an exception: ", ex );
							}
							
						} while ( (cookie != null) && (cookie.length != 0) );
					}
			  		catch ( Exception ex )
			  		{
			  			logger.error( "In getDynamicGroupMembers(), exceptions was thrown: ", ex );
			  		}
			  		finally
			  		{
						if ( ldapContext != null )
						{
							try
							{
								// Close the ldap context.
								ldapContext.close();
							}
							catch (NamingException ex)
					  		{
					  		}
						}
					}
				}
			}// end for()
		}

		return listOfMembers;
	}

	/**
	 * 
	 */
	private LdapDirType getLdapDirType( String ldapGuidAttrName )
	{
		if ( ldapGuidAttrName != null )
		{
			if ( ldapGuidAttrName.equalsIgnoreCase( OBJECT_GUID_ATTRIBUTE ) )
				return LdapDirType.AD;
			
			if ( ldapGuidAttrName.equalsIgnoreCase( GUID_ATTRIBUTE ) )
				return LdapDirType.EDIR;
		}
	
		return LdapDirType.UNKNOWN;
	}
	
	protected String getLdapProperty(String zoneName, String name) {
		String val = SZoneConfig.getString(zoneName, "ldapConfiguration/property[@name='" + name + "']");
		if (Validator.isNull(val)) {
			val = (String)defaultProps.get(name);
		}
		return val;
	}
	/**
	 * Get the ldap configuration.  This object is stored in the scheduling database.  
	 */
	@Override
	public LdapSchedule getLdapSchedule() {		
		checkAccess(LdapOperation.manageLdap);
		LdapSchedule cfg = new LdapSchedule(getSyncObject().getScheduleInfo(RequestContextHolder.getRequestContext().getZoneId()));
		return cfg;
	}
	/**
	 * Update ldap configuration with scheduler. Only properties specified in the props file will
	 * be modified.
	 * @param zoneId
	 * @param props
	 */
	@Override
	public void setLdapSchedule(LdapSchedule schedule) {
		checkAccess(LdapOperation.manageLdap);
		getSyncObject().setScheduleInfo(schedule.getScheduleInfo());
	}

	
	/**
	 * Return a list of all the dynamic groups.
	 */
	public ArrayList<Long> getListOfDynamicGroups()
	{
		ArrayList<Long> listOfGroupIds;
		
		listOfGroupIds = new ArrayList<Long>();
		
		try
		{
			Map options;
			Map searchResults;
			List groups = null;
			
			options = new HashMap();
			options.put( ObjectKeys.SEARCH_SORT_BY, Constants.SORT_TITLE_FIELD );
			options.put( ObjectKeys.SEARCH_SORT_DESCEND, Boolean.FALSE );
			options.put( ObjectKeys.SEARCH_MAX_HITS, Integer.MAX_VALUE-1 );
			
			// Get the list of all the groups.
			searchResults = getProfileModule().getGroups( options );
	
			groups = (List) searchResults.get( ObjectKeys.SEARCH_ENTRIES );

			if ( groups != null )
			{
				int i;
				
				for (i = 0; i < groups.size(); ++i)
				{
					HashMap nextMap;
					
					if ( groups.get( i ) instanceof HashMap )
					{
						String value;
						
						nextMap = (HashMap) groups.get( i );
					
						// Is this group dynamic?
						value = (String) nextMap.get( "_isGroupDynamic" );
						if ( value != null && value.equalsIgnoreCase( "true" ) )
						{
							Long id;

							// Yes
							// Get the group id
							id = Long.valueOf( (String) nextMap.get( "_docId" ) );
							if ( id != null )
								listOfGroupIds.add( id );
						}
					}
				}
			}
		}
		catch ( Exception ex )
		{
			// Nothing to do
		}
		
		return listOfGroupIds;
	}

	/**
	 * 
	 * @param zoneName
	 * @return
	 */
    protected LdapSynchronization getSyncObject( String zoneName )
    {
    	String jobClass = getLdapProperty( zoneName, SYNC_JOB );
       	if (Validator.isNotNull(jobClass)) {
    		try {
    			return (LdapSynchronization)ReflectHelper.getInstance(jobClass);
    		} catch (Exception e) {
 			   logError("Cannot instantiate LdapSynchronization custom class", e);
    		}
    	}
       	String className = SPropsUtil.getString("job.ldap.synchronization.class", "org.kablink.teaming.jobs.DefaultLdapSynchronization");
    	return (LdapSynchronization)ReflectHelper.getInstance(className);		   		
 
    }// end getSyncObject()	
	
	protected LdapSynchronization getSyncObject()
	{
		return getSyncObject( RequestContextHolder.getRequestContext().getZoneName() );
    }	
	
    private void logError(String msg, Exception e) {
    	logger.error(msg, e);
    }
    
    protected Map getZoneMap(String zoneName)
    {
    	if(zones.containsKey(zoneName)) { return (Map) zones.get(zoneName); }
    	Map zone = new HashMap();
    	List memberAttributes = new ArrayList();
    	List classes = SZoneConfig.getElements("ldapConfiguration/groupMapping/memberAttribute");
    	for(int i=0; i < classes.size(); i++) {
    		Element next = (Element) classes.get(i);
    		memberAttributes.add(next.getTextTrim());
    	}
    	zone.put(MEMBER_ATTRIBUTES, memberAttributes);
    	
    	Map attributes = new LinkedHashMap();
    	List mappings  = SZoneConfig.getElements("ldapConfiguration/groupMapping/mapping");
    	for(int i=0; i < mappings.size(); i++) {
    		Element next = (Element) mappings.get(i);
    		attributes.put(next.attributeValue("from"), next.attributeValue("to"));
    	}
    	zone.put(GROUP_ATTRIBUTES, attributes);
    	
    	zones.put(zoneName, zone);
    	return zone;
    }
    
    /**
     * Has the user specified a value for "LDAP attribute that uniquely identifies a user or group"?
     */
    @Override
	public boolean isGuidConfigured()
    {
    	boolean isConfigured;
		List<LdapConnectionConfig> ldapConnectionConfigs;
		Workspace zone;
		Long zoneId;

    	isConfigured = false;
    	
		zone = RequestContextHolder.getRequestContext().getZone();
		zoneId = zone.getId();

		// Get the list of ldap configurations.
		ldapConnectionConfigs = this.getConfigsReadOnlyCache( zoneId );
		
		// Go through each ldap configuration
		for( LdapConnectionConfig nextLdapConfig : ldapConnectionConfigs )
		{
			String ldapGuidAttribute;
			
			// Get the name of the ldap attribute that holds the guid.
			ldapGuidAttribute = nextLdapConfig.getLdapGuidAttribute();
			
			if ( ldapGuidAttribute != null && ldapGuidAttribute.length() > 0 )
				isConfigured = true;
		}
    	
    	return isConfigured;
    }
    
    /**
     * For the given name, find the Teaming id in the given Map.
     */
    public Long getTeamingId(
    	String name,
		Map<String, Object[]> userMap )	// Key is the user name and value is an array of Teaming attribute values.
    {
    	Object[] attributeValues;
    	Long id = null;
    	
    	// Does this user exist in Teaming?
    	attributeValues = userMap.get( name );
    	if ( attributeValues != null )
    	{
    		// Yes, get the users Teaming id.
    		id = (Long) attributeValues[PRINCIPAL_ID]; 
    	}
    	
      	return id;
    }// end getTeamingId()

    
    /**
     * For all users, sync the ldap attribute that holds the guid.  The name of the ldap
     * attribute that holds the guid is found in the ldap configuration data.  You can get the
     * name of the attribute by calling config.getGuidAttribute().  For eDirectory, the name of
     * the attribute is GUID and for Active Directory, the name of the attribute is objectGUID.
     * This method should be called whenever the user changes the the name of the ldap attribute
     * that holds the guid (in the ldap configuration).
     */
    public void syncGuidAttributeForAllUsers(
    	LdapConnectionConfig ldapConfig,
    	LdapContext ldapContext,
		Map<String, Object[]> userMap,	// Key is the user name and value is an array of Teaming attribute values.
    	LdapSyncResults syncResults ) throws LdapSyncException
    {
		Workspace zone;
		Long zoneId;
		Map<Long, Map> usersToUpdate;
		PartialLdapSyncResults modifiedUsersSyncResults;
		String[] ldapAttributesToRead;
		String ldapGuidAttribute;

		logger.info( "In syncGuidAttributeForAllUsers()" );
		// usersToUpdate will hold the users that need to be updated and the attributes to update.
		// The key is the user's id and the value is the map of attributes.
		usersToUpdate = new HashMap();

		zone = RequestContextHolder.getRequestContext().getZone();
		zoneId = zone.getId();

		// Get the name of the ldap attribute that holds the guid.
		ldapGuidAttribute = ldapConfig.getLdapGuidAttribute();
		
		// Specify the list of attributes to read from the ldap directory.
		ldapAttributesToRead = new String[2];
		ldapAttributesToRead[0] = ldapConfig.getUserIdAttribute();
		ldapAttributesToRead[1] = ldapGuidAttribute;
		
		modifiedUsersSyncResults = null;
		if ( syncResults != null )
		{
			// Yes
			modifiedUsersSyncResults = syncResults.getModifiedUsers();
		}
		
		// Go through each user search criteria
		for ( LdapConnectionConfig.SearchInfo searchInfo : ldapConfig.getUserSearches() )
		{
			if( Validator.isNotNull( searchInfo.getFilterWithoutCRLF() ) )
			{
				int scope;
				SearchControls searchCtrls;
				NamingEnumeration ctxSearch;

				scope = (searchInfo.isSearchSubtree() ? SearchControls.SUBTREE_SCOPE:SearchControls.ONELEVEL_SCOPE);
				searchCtrls = new SearchControls( scope, 0, 0, ldapAttributesToRead, false, false );

				try
				{
					// Search for users using the base dn and filter criteria.
					ctxSearch = ldapContext.search( searchInfo.getBaseDn(), searchInfo.getFilterWithoutCRLF(), searchCtrls );
					while ( hasMore( ctxSearch ) )
					{
						String userName;
						String fixedUpUserName;
						String guid;
						String teamingName;
						Long teamingId;
						Attributes lAttrs = null;
						Attribute attrib;
						Binding binding;
	
						teamingName = null;
						
						// Get the next user in the list.
						binding = (Binding)ctxSearch.next();
						userName = binding.getNameInNamespace();
						
						// Fixup the  by replacing all "/" with "\/".
						fixedUpUserName = fixupName( userName );
						fixedUpUserName = fixedUpUserName.trim();
	
						// Read the necessary attributes for this user from the ldap directory.
						lAttrs = ldapContext.getAttributes( fixedUpUserName, ldapAttributesToRead );
						
						// Get the ldap attribute whose value is used for the users name in Teaming.
						attrib = lAttrs.get( ldapConfig.getUserIdAttribute() );
						if ( attrib != null && attrib.size() == 1 )
						{
							Object value;
							
							value = attrib.get();
							if ( value != null && value instanceof String )
							{
								teamingName = (String) value;
							}
						}
						
						// Did we get the name of the user?
						if ( teamingName == null )
						{
							// No
							continue;
						}
	
						// Is the name of this user a name that is used for a Teaming system user account?
						// Currently there are 5 system user accounts named, "admin", "guest", "_postingAgent",
						// "_jobProcessingAgent", "_synchronizationAgent" and "_fileSyncAgent".
						if ( BuiltInUsersHelper.isSystemUserAccount( teamingName ) )
						{
							// Yes, skip this user.
							continue;
						}
						
						// Do we have an ldap guid attribute?
						// Get the user's teaming id
						teamingId = getTeamingId( teamingName, userMap );
						
						// Does this user exist in Teaming.
						if ( teamingId != null )
						{
							Map userMods;
							
							// Yes
							// Get the guid we read from the ldap directory.
							guid = null;
							if ( ldapGuidAttribute != null )
							{
								guid = getLdapGuid( lAttrs, ldapGuidAttribute );
							}

							// Create the map that will hold the attributes we want updated in the Teaming db.
							userMods = new HashMap();
							userMods.put( ObjectKeys.FIELD_PRINCIPAL_LDAPGUID, guid );
							
							// Add this user to our list of users that need to be updated.
							logger.info( "adding user to list of users to update: " + teamingName );
							usersToUpdate.put( teamingId, userMods );
							
							// Update every 100 users
							if ( usersToUpdate.size() > 99 )
							{
								logger.info( "about to call updateUsers()" );
								updateUsers( zoneId, usersToUpdate, null, LdapSyncMode.PERFORM_SYNC, modifiedUsersSyncResults );
								logger.info( "back from updateUsers()" );
								
								usersToUpdate.clear();
							}
						}
					}// end while()
				}// end try
		  		catch ( Exception ex )
		  		{
		  			logError( NLT.get( "errorcode.ldap.context" ), ex );
		  		}
			}
		}// end for()

		// Update the users with the guid from the ldap directory.
		logger.info( "about to call updateUsers()" );
		updateUsers( zoneId, usersToUpdate, null, LdapSyncMode.PERFORM_SYNC, modifiedUsersSyncResults );
		logger.info( "back from updateUsers()" );
		
    }// end syncGuidAttributeForAllUsers()
    
    
    /**
     * For all groups, sync the ldap attribute that holds the guid.  The name of the ldap
     * attribute that holds the guid is found in the ldap configuration data.  You can get the
     * name of the attribute by calling config.getGuidAttribute().  For eDirectory, the name of
     * the attribute is GUID and for Active Directory, the name of the attribute is objectGUID.
     * This method should be called whenever the user changes the the name of the ldap attribute
     * that holds the guid (in the ldap configuration).
     */
    public void syncGuidAttributeForAllGroups( LdapConnectionConfig ldapConfig, LdapContext ldapContext, LdapSyncResults syncResults ) throws LdapSyncException
    {
		Workspace zone;
		Long zoneId;
		ProfileDao profileDao;
		String[] ldapAttributesToRead;
		String ldapGuidAttribute;

		profileDao = getProfileDao();
		
		zone = RequestContextHolder.getRequestContext().getZone();
		zoneId = zone.getId();

		// Get the name of the ldap attribute that holds the guid.
		ldapGuidAttribute = ldapConfig.getLdapGuidAttribute();
		
		// Specify the list of attributes to read from the ldap directory.
		ldapAttributesToRead = new String[1];
		ldapAttributesToRead[0] = ldapGuidAttribute;
		
		// Go through each group search criteria
		for ( LdapConnectionConfig.SearchInfo searchInfo : ldapConfig.getGroupSearches() )
		{
			if( Validator.isNotNull( searchInfo.getFilterWithoutCRLF() ) )
			{
				int scope;
				SearchControls searchCtrls;
				NamingEnumeration ctxSearch;

				scope = (searchInfo.isSearchSubtree() ? SearchControls.SUBTREE_SCOPE:SearchControls.ONELEVEL_SCOPE);
				searchCtrls = new SearchControls( scope, 0, 0, ldapAttributesToRead, false, false );

				try
				{
					// Search for groups using the base dn and filter criteria.
					ctxSearch = ldapContext.search( searchInfo.getBaseDn(), searchInfo.getFilterWithoutCRLF(), searchCtrls );
					while ( hasMore( ctxSearch ) )
					{
						String groupName;
						String fullDN;
						String guid;
						Attributes lAttrs = null;
						Binding binding;
	
						// Get the next group in the list.
						binding = (Binding)ctxSearch.next();
						groupName = binding.getNameInNamespace();
						
						// Fixup the  by replacing all "/" with "\/".
						fullDN = fixupName( groupName );
						fullDN = fullDN.trim();
	
						// Read the necessary attributes for this group from the ldap directory.
						lAttrs = ldapContext.getAttributes( fullDN, ldapAttributesToRead );
						
						// Is the name of this group a name that is used for a Teaming system user account?
						// Currently there are 5 system user accounts named, "admin", "guest", "_postingAgent",
						// "_jobProcessingAgent", "_synchronizationAgent", and "_fileSyncAgent.
						if ( BuiltInUsersHelper.isSystemUserAccount( fullDN ) )
						{
							// Yes, skip this user.
							continue;
						}
						
						guid = null;
	
						// Do we have an ldap guid attribute?
						if ( ldapGuidAttribute != null )
						{
							guid = getLdapGuid( lAttrs, ldapGuidAttribute );
						}
	
						// Does this group exist in Teaming.
						try
						{
							Principal principal;
							Map userMods;
							
							principal = profileDao.findPrincipalByName( fullDN, zoneId );
	
							// Create the map that will hold the attributes we want updated in the Teaming db.
							userMods = new HashMap();
							userMods.put( ObjectKeys.FIELD_PRINCIPAL_LDAPGUID, guid );

							// Update this group with the value of the guid attribute from the ldap directory.
							updateGroup( zoneId, principal.getId(), userMods, LdapSyncMode.PERFORM_SYNC, syncResults );
						}
						catch (NoPrincipalByTheNameException ex)
						{
							// Nothing to do, this just means the group doesn't exist.
						}
					}// end while()
				}// end try
		  		catch ( Exception ex )
		  		{
		  			logError( NLT.get( "errorcode.ldap.context" ), ex );
		  		}
			}
		}// end for()
    }// end syncGuidAttributeForAllGroups()
    
    
    /**
     * For all users and groups, sync the ldap attribute that holds the guid.  The name of the ldap
     * attribute that holds the guid is found in the ldap configuration data.  You can get the
     * name of the attribute by calling config.getGuidAttribute().  For eDirectory, the name of
     * the attribute is GUID and for Active Directory, the name of the attribute is objectGUID.
     * This method should be called whenever the user changes the the name of the ldap attribute
     * that holds the guid (in the ldap configuration).
     */
    public void syncGuidAttributeForAllUsersAndGroups(
    	String[] listOfLdapConfigsToSyncGuid,
    	LdapSyncResults syncResults ) throws LdapSyncException
    {
		Workspace zone;
		Long zoneId;
		List<LdapConnectionConfig> ldapConnectionConfigs;
		List userList;
		Map<String, Object[]> userMap;
		ObjectControls objCtrls;
		FilterControls filterCtrls;
		int i;
		LdapSyncException ldapSyncEx = null;
		
		if ( listOfLdapConfigsToSyncGuid == null || listOfLdapConfigsToSyncGuid.length == 0 )
		{
			logger.info( "In syncGuidAttributeForAllUsersAndGroups(), listOfLdapConfigsToSyncGuid is empty" );
			return;
		}
		
		zone = RequestContextHolder.getRequestContext().getZone();
		zoneId = zone.getId();

		// Get the list of users in this zone that are not deleted
		objCtrls = new ObjectControls( User.class, principalAttrs );
		filterCtrls = new FilterControls( ObjectKeys.FIELD_ENTITY_DELETED, Boolean.FALSE );
		userList = coreDao.loadObjects( objCtrls, filterCtrls, zoneId ); 
				
		// userList is a list of arrays where each array holds the values of some
		// of the user's Teaming attributes.  Create a map where the key is the
		// user's name and the value is the array of Teaming attributes.
		userMap = new TreeMap( String.CASE_INSENSITIVE_ORDER );
		for (i = 0; i < userList.size(); ++i)
		{
			Object[] attributeValues;

			attributeValues = (Object[]) userList.get( i );
			
			// If this user is not one of the system users, add the user to the map.
			if ( (Validator.isNull( (String)attributeValues[PRINCIPAL_INTERNALID])) )
			{
				String name;

				// Get the user's name.
				name = (String)attributeValues[PRINCIPAL_NAME];
				
				// Map the name to the array of attribute values.
				userMap.put( name, attributeValues );
			}
		}// end for()			

		// Get the list of ldap configurations.
		ldapConnectionConfigs = getCoreDao().loadLdapConnectionConfigs( zoneId );
		
		// Go through each ldap configuration
		for( LdapConnectionConfig nextLdapConfig : ldapConnectionConfigs )
		{
			boolean syncThis;
			String url;
			
			syncThis = false;
			url = nextLdapConfig.getUrl();
			
			// See if we should sync the guid for this ldap source.
			for ( String nextUrl : listOfLdapConfigsToSyncGuid )
			{
				if ( nextUrl != null && nextUrl.equalsIgnoreCase( url ) )
				{
					syncThis = true;
					break;
				}
			}
			
			if ( syncThis )
			{
		   		LdapContext ldapContext;
		   		NamingException namingEx;

		   		namingEx = null;
		   		ldapContext = null;
		  		try
		  		{
					// Get an ldap context for the given ldap configuration
					ldapContext = getContext( zoneId, nextLdapConfig, false );
					
					// Sync the guid attributes for all users.
					logger.info( "about to call syncGuidAttributeForAllUsers()" );
					syncGuidAttributeForAllUsers( nextLdapConfig, ldapContext, userMap, syncResults );
					logger.info( "back from syncGuidAttributeForAllUsers()" );
					
					// Sync the guid attribute for all groups.
					logger.info( "about to call syncGuidAttributeForAllGroups()" );
					syncGuidAttributeForAllGroups( nextLdapConfig, ldapContext, syncResults );
					logger.info( "back from syncGuidAttributeForAllGroups()" );
				}// end try
		  		catch (NamingException ex)
		  		{
		  			namingEx = ex;
		  		}
		  		finally
		  		{
					if ( ldapContext != null )
					{
						try
						{
							// Close the ldap context.
							ldapContext.close();
						}
						catch (NamingException ex)
				  		{
							namingEx = ex;
				  		}
					}
				}
		  		
		  		// Did we encounter a problem?
		  		if ( namingEx != null )
		  		{
		  			// Yes
		  			logError( NLT.get( "errorcode.ldap.context" ), namingEx );
		  			
		  			// Create an LdapSyncException and throw it.  We throw an LdapSyncException so we can return
		  			// the LdapConnectionConfig object that was being used when the error happened.
		  			if ( ldapSyncEx == null )
		  				ldapSyncEx = new LdapSyncException( nextLdapConfig, namingEx );
		  		}
			}
		}// end for()
		
		if ( ldapSyncEx != null )
			throw ldapSyncEx;
		
    }// end syncGuidAttributeForAllUsersAndGroups()
    
    
    /**
     * Read the domain name from AD and convert it to aaa.bbb.ccc.com format
     */
    private ADLdapObject getDomainInfo( LdapConnectionConfig ldapConfig )
    {
        ADLdapObject domainInfo;
    	String mixedCaseDomainName = null, netbiosName = null;            
    		
    	// Read the domain name from AD.  The value returned will be in the format, dc=aaa,dc=bbb,dc=com
    	domainInfo = readDomainInfoFromAD( ldapConfig );
    	
        if (domainInfo == null)
            return null;
        
        netbiosName = domainInfo.getNetbiosName();
        if (netbiosName != null) {
            netbiosName = netbiosName.toLowerCase();
            domainInfo.setNetbiosName(netbiosName);
        }
        
        mixedCaseDomainName = domainInfo.getDomainName();
    	// Convert the domain name from dc=aaa,dc=bbb,dc=com to aaa.bbb.com format
    	if ( mixedCaseDomainName != null )
    	{
    		StringBuffer strBuff;
    		String lowerCaseDomainName;
    		boolean finished;
    		boolean first;
    		int fromIndex;
    		
    		strBuff = new StringBuffer();
    		
    		lowerCaseDomainName = mixedCaseDomainName.toLowerCase();
    		
    		first = true;
    		fromIndex = 0;
    		finished = false;
    		while ( finished == false )
    		{
    			int dcIndex;
    			
    			dcIndex = lowerCaseDomainName.indexOf( "dc=", fromIndex );
    			if ( dcIndex >= 0 )
    			{
    				int commaIndex;
    				
    				if ( first == false )
    					strBuff.append( '.' );
    				
    				commaIndex = mixedCaseDomainName.indexOf( ',', dcIndex );
    				if ( commaIndex > 0 )
    					strBuff.append( mixedCaseDomainName.substring( dcIndex+3, commaIndex ) );
    				else
    				{
    					strBuff.append( mixedCaseDomainName.substring( dcIndex+3 ) );
    					finished = true;
    				}
    				
    				fromIndex = commaIndex;
    				first = false;
    			}
    			else
    				finished = true;
    		}

            domainInfo.setDomainName(strBuff.toString());
    	}
        return domainInfo;
    }

	/**
	 * Read the "defaultNamingContext" attribute from the rootDSE object in AD.
	 * The value of the attribute will be in the format, dc=aaa,dc=bbb,dc=ccc,dc=com
	 */
	private ADLdapObject readDomainInfoFromAD( LdapConnectionConfig config )
    {
        ADLdapObject domainInfo = null;
        LdapContext ctx = null;
        String configContext = null;

        try {
            Workspace zone;
            SearchControls controls;
            NamingEnumeration answer;
            String base;
            String filter;            
                  
            zone = RequestContextHolder.getRequestContext().getZone();

            ctx = getUserContext(zone.getId(), config);

            base = "";
            filter = "(objectclass=*)";
            controls = new SearchControls();
            controls.setSearchScope(SearchControls.OBJECT_SCOPE);
            answer = ctx.search(base, filter, controls);

            if (hasMore(answer)) {                
                SearchResult sr;
                Attributes attrs;

                sr = (SearchResult) answer.next();
                if (sr != null) {
                    attrs = sr.getAttributes();
                    if (attrs != null) {
                        Attribute attrib, configAttrib;

                        attrib = attrs.get("defaultNamingContext");
                        if (attrib != null) {
                            Object value;                           

                            value = attrib.get();
                            if (value != null && value instanceof String) {
                                domainInfo = new ADLdapObject();
                                domainInfo.setDomainName((String) value);
                            }
                        }
                        if (domainInfo != null && domainInfo.getDomainName()!= null)
                        {
                            configAttrib = attrs.get("configurationNamingContext");
                            if (configAttrib != null) {
                                Object value;
                                value = configAttrib.get();
                                if (value != null && value instanceof String) {
                                    configContext = (String) value;
                                }
                            }
                        }
                    }
                }
            }
            if (domainInfo != null && domainInfo.getDomainName()!= null && configContext != null && !configContext.isEmpty()) {
                base = "CN=Partitions," + configContext;
                filter = "(&(objectClass=crossRef)(nCName=" + domainInfo.getDomainName() + ")(systemflags:1.2.840.113556.1.4.803:=2))";
                String[] attr = {"nETBIOSName"};
                controls = new SearchControls();
                controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
                controls.setReturningAttributes(attr);
                answer = ctx.search(base, filter, controls);

                if (hasMore(answer)) {
                    SearchResult sr;
                    Attributes attrs;

                    sr = (SearchResult) answer.next();
                    if (sr != null) {
                        attrs = sr.getAttributes();
                        if (attrs != null) {
                            Attribute attrib;

                            attrib = attrs.get("nETBIOSName");
                            if (attrib != null) {
                                Object value;

                                value = attrib.get();
                                if (value != null && value instanceof String) {
                                    domainInfo.setNetbiosName((String) value);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("readDomainInfoFromAD() caught exception: ", ex);
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException ex) {
                    // Nothing to do
                }
            }
        }

        return domainInfo;
    }

	/**
	 * Create a HomeDirInfo object based on the HomeDirConfig defined in the ldap configuration.
	 * @throws NamingException 
	 */
    @Override
	public HomeDirInfo getHomeDirInfo(
		String teamingUserName,
		String ldapUserName,
		boolean logErrors ) throws NamingException 
	{
		Workspace zone;

		if(logger.isDebugEnabled())
			logger.debug( "Reading home directory attribute for user: " + teamingUserName );
		
		zone = RequestContextHolder.getRequestContext().getZone();

		for( LdapConnectionConfig config : this.getConfigsReadOnlyCache( zone.getZoneId() ) )
		{
			LdapContext ctx;
			String dn=null;
			Map userAttributes;
			String [] userAttributeNames;
	
			ctx = getUserContext( zone.getId(), config );
			dn = null;
			userAttributes = config.getMappings();
			userAttributeNames = (String[])(userAttributes.keySet().toArray(sample));

			for ( LdapConnectionConfig.SearchInfo searchInfo : config.getUserSearches() ) 
			{
				HomeDirInfo homeDirInfo = null;

				try
				{
					int scope;
					SearchControls sch;
					String search;
					String filter;
					NamingEnumeration ctxSearch;
					Binding bd;
					LdapDirType dirType;
					
					scope = (searchInfo.isSearchSubtree() ? SearchControls.SUBTREE_SCOPE : SearchControls.ONELEVEL_SCOPE);
					sch = new SearchControls( scope, 1, 0, userAttributeNames, false, false );
		
					search = "(" + config.getUserIdAttribute() + "=" + ldapUserName + ")";
					filter = searchInfo.getFilterWithoutCRLF();
					if ( !Validator.isNull( filter ) )
					{
						search = "(&"+search+filter+")";
					}
					
					ctxSearch = ctx.search( searchInfo.getBaseDn(), search, sch );
					if ( !hasMore( ctxSearch ) ) 
					{
						continue;
					}
					
					bd = (Binding)ctxSearch.next();

					if ( bd.isRelative() && Validator.isNotNull( ctx.getNameInNamespace() ) ) 
					{
						dn = bd.getNameInNamespace() + "," + ctx.getNameInNamespace();
					}
					else
					{
						dn = bd.getNameInNamespace();
					}

					// Get the home directory info for this user
					dirType = getLdapDirType( config.getLdapGuidAttribute() );
					homeDirInfo = getHomeDirInfoFromConfig(
														ctx,
														dirType,
														dn,
														searchInfo.getHomeDirConfig(),
														logErrors );
				}
				finally
				{
					// Nothing to do.
				}

				if ( ctx != null )
				{
					try
					{
						ctx.close();
					}
					catch ( NamingException ex )
					{
						// Nothing to do
					}
				}

				return homeDirInfo;
			}
			
			if ( ctx != null )
			{
				try
				{
					ctx.close();
				}
				catch ( NamingException ex )
				{
					// Nothing to do
				}
			}
		}
		
		// If we get here we did not find the user.
		return null;
	}
	
	/**
	 * Create a HomeDirInfo object based on the HomeDirConfig defined in the ldap configuration.
	 * @throws NamingException 
	 */
    @Override
	public Map getLdapUserAttributes(User user) throws NamingException 
	{
		Workspace zone;
		zone = RequestContextHolder.getRequestContext().getZone();

		for( LdapConnectionConfig config : this.getConfigsReadOnlyCache( zone.getZoneId() ) )
		{
			LdapContext ctx;
			Map userAttributes;
			String [] userAttributeNames;
	
			ctx = getUserContext( zone.getId(), config );
			userAttributes = config.getMappings();
			userAttributeNames = (String[])(userAttributes.keySet().toArray(sample));

			for ( LdapConnectionConfig.SearchInfo searchInfo : config.getUserSearches() ) 
			{
				try
				{
					int scope;
					SearchControls sch;
					String search;
					String filter;
					NamingEnumeration ctxSearch;
					
					scope = (searchInfo.isSearchSubtree() ? SearchControls.SUBTREE_SCOPE : SearchControls.ONELEVEL_SCOPE);
					sch = new SearchControls( scope, 1, 0, userAttributeNames, false, false );
		
					search = "(" + config.getUserIdAttribute() + "=" + user.getName() + ")";
					filter = searchInfo.getFilterWithoutCRLF();
					if ( !Validator.isNull( filter ) )
					{
						search = "(&"+search+filter+")";
					}
					
					ctxSearch = ctx.search( searchInfo.getBaseDn(), search, sch );
					if ( !hasMore( ctxSearch ) ) 
					{
						continue;
					}
					
				}
				finally
				{
					// Nothing to do.
				}

				if ( ctx != null )
				{
					try
					{
						ctx.close();
					}
					catch ( NamingException ex )
					{
						// Nothing to do
					}
				}

				return userAttributes;
			}
			
			if ( ctx != null )
			{
				try
				{
					ctx.close();
				}
				catch ( NamingException ex )
				{
					// Nothing to do
				}
			}
		}
		
		// If we get here we did not find the user.
		return null;
	}
	
    /**
     * Convert the fully-qualified dn to a typeless dn
     */
    private String getTypelessDN( String fqdn )
    {
    	StringBuffer typelessDN;
		int fromIndex;
		boolean keepGoing = true;
    	
    	if ( fqdn == null || fqdn.length() == 0 )
    		return null;
    	
    	typelessDN = new StringBuffer();
    	
		fromIndex = 0;
		while ( keepGoing )
		{
			int index;

			// Find the next '='
			index = fqdn.indexOf( '=', fromIndex );
			if ( index > 0 && (index+1) < fqdn.length() )
			{
				char prevCh = ' ';
				boolean foundComma;

				typelessDN.append( '.' );
				
				foundComma = false;
				++index;
				while ( index < fqdn.length() && foundComma == false )
				{
					char nextCh;
					
					nextCh = fqdn.charAt( index );
					++index;
					
					if ( nextCh == ',' && prevCh != '\\' )
					{
						// We have found the end of this section of the dn
						foundComma = true;
					}
					else
					{
						if ( nextCh == '+' || nextCh == '=' || nextCh == '.' )
						{
							// '+', '=' and '.'  must be escaped.
							if ( prevCh != '\\' )
								typelessDN.append( '\\' );
						}
						
						typelessDN.append( nextCh );
						prevCh = nextCh;
					}
				}
				
				keepGoing = foundComma;
				fromIndex = index;
			}
			else
				keepGoing = false;
		}
    	
    	return typelessDN.toString();
    }
    
    @Override
    public String readLdapGuidFromDirectory(String userName, Long zoneId, LdapConnectionConfig config) {
		String ldapGuidAttribute;
		
		// Does this ldap configuration have an ldap guid attribute defined?
		ldapGuidAttribute = config.getLdapGuidAttribute();
		if ( ldapGuidAttribute != null && ldapGuidAttribute.length() > 0 )
		{
			// Yes
			LdapContext ctx;

			try
			{
				ctx = getUserContext( zoneId, config);
			}
			catch (NamingException ex)
			{
				return null;
			}
			
			for(LdapConnectionConfig.SearchInfo searchInfo : config.getUserSearches())
			{
				try
				{
					Attributes lAttrs;
					int scope;
					SearchControls sch;
					String search;
					String filter;
					NamingEnumeration ctxSearch;
					Binding bd;
					String ldapGuid;
					String userIdAttributeName[] = {config.getUserIdAttribute()};
					String attributesToRead[] = {ldapGuidAttribute};

					scope = (searchInfo.isSearchSubtree()?SearchControls.SUBTREE_SCOPE:SearchControls.ONELEVEL_SCOPE);
					sch = new SearchControls(scope, 1, 0, userIdAttributeName, false, false);
		
					search = "(" + config.getUserIdAttribute() + "=" + userName + ")";
					filter = searchInfo.getFilterWithoutCRLF();
					if(!Validator.isNull(filter))
					{
						search = "(&"+search+filter+")";
					}
					
					ctxSearch = ctx.search( searchInfo.getBaseDn(), search, sch );
					if (!hasMore( ctxSearch ) )
					{
						continue;
					}
					
					bd = (Binding)ctxSearch.next();

					// Read the ldap guid from the directory.
					lAttrs = ctx.getAttributes( bd.getNameInNamespace(), attributesToRead );
					
					// Get the guid from what we read from the directory.
					ldapGuid = getLdapGuid( lAttrs, ldapGuidAttribute );

					if ( ctx != null )
					{
						try
						{
							ctx.close();
						}
						catch ( NamingException ex )
						{
							// Nothing to do
						}
					}

					return ldapGuid;
				}
				catch (NamingException ex)
				{
					// Nothing to do.
				}
			}// end for()

			if ( ctx != null )
			{
				try
				{
					ctx.close();
				}
				catch ( NamingException ex )
				{
					// Nothing to do
				}
			}
			
			return null;
		}
		else {
			return null;
		}
    }
    
    /**
     * If the ldap configuration has the name of the ldap attribute that holds the guid then
     * read the ldap guid from the ldap directory.
     * @param userName
     * @return
     */
    @Override
	public String readLdapGuidFromDirectory( String userName, Long zoneId )
    {
		LdapSchedule schedule;
		String zoneName;
		ZoneInfo zoneInfo;

		zoneInfo = getZoneModule().getZoneInfo( zoneId );
		zoneName = zoneInfo.getZoneName();
		
		schedule = new LdapSchedule( getSyncObject( zoneName ).getScheduleInfo( zoneId ) );
		String result;
		List<LdapConnectionConfig> configs = this.getConfigsReadOnlyCache(zoneId);
		for(LdapConnectionConfig config : configs)
		{
			result = readLdapGuidFromDirectory(userName, zoneId, config);
			if(result != null)
				return result;
		}// end for()
		
		// If we get here we either didn't find the user or we didn't read the ldap guid.
		return null;
    }// end readLdapGuidFromDirectory()
    

    /**
     * Determine if the user's password in the ldap directory has expired
     */
    private boolean hasPasswordExpired( String userName, Long zoneId, LdapConnectionConfig ldapConfig )
    {
    	boolean expired;
		LdapContext ctx;
		LdapDirType dirType;
		
		dirType = getLdapDirType( ldapConfig.getLdapGuidAttribute() );
		if ( dirType != LdapDirType.AD && dirType != LdapDirType.EDIR )
			return false;

    	expired = false;

    	try
		{
			ctx = getUserContext( zoneId, ldapConfig );
		}
		catch ( NamingException ex )
		{
			return false;
		}
		
		for ( LdapConnectionConfig.SearchInfo searchInfo : ldapConfig.getUserSearches() )
		{
			try
			{
				Attributes lAttrs;
				int scope;
				SearchControls sch;
				String search;
				String filter;
				NamingEnumeration ctxSearch;
				Binding bd;
				String userIdAttributeName[] = { ldapConfig.getUserIdAttribute() };
				Attribute attrib;

				scope = (searchInfo.isSearchSubtree() ? SearchControls.SUBTREE_SCOPE : SearchControls.ONELEVEL_SCOPE);
				sch = new SearchControls( scope, 1, 0, userIdAttributeName, false, false );
	
				search = "(" + ldapConfig.getUserIdAttribute() + "=" + userName + ")";
				filter = searchInfo.getFilterWithoutCRLF();
				if(!Validator.isNull(filter))
				{
					search = "(&"+search+filter+")";
				}
				
				ctxSearch = ctx.search( searchInfo.getBaseDn(), search, sch );
				if (!hasMore( ctxSearch ) )
				{
					continue;
				}
				
				bd = (Binding)ctxSearch.next();

				if ( dirType == LdapDirType.EDIR )
				{
					String attributesToRead[] = { PASSWORD_EXPIRATION_TIME_ATTRIBUTE };

					// Read the "passwordExpirationTime" attribute from the directory.
					lAttrs = ctx.getAttributes( bd.getNameInNamespace(), attributesToRead );
					
					// Get the value of the "passwordExpirationTime" attribute
					attrib = lAttrs.get( PASSWORD_EXPIRATION_TIME_ATTRIBUTE );
					if ( attrib != null )
					{
						try
						{
							Object value;
							
							value = attrib.get();
							if ( value != null && value instanceof String )
							{
								String dateStr;
								
								dateStr = (String) value;
								
								try
								{
									SimpleDateFormat dateFormat;
									Date expirationDate;
									Date today;
									String strValue;
									char ch;
									
									// An example of an expiration date is 20130327204900Z
									// Is the last character of the expiration date a 'Z'?
									ch = dateStr.charAt( dateStr.length() - 1 );
									if ( ch == 'Z' || ch == 'z' )
									{
										// Yes
										// Exclude the 'Z' from the expiration date.
										// SimpleDateFormat.parse() does not know how to parse it.
										strValue = dateStr.substring( 0, dateStr.length()-1 );
									}
									else
									{
										// No
										strValue = dateStr;
									}
									
									today = new Date();
									dateFormat = new SimpleDateFormat( "yyyyMMddHHmmss" );
									expirationDate = dateFormat.parse( strValue );
								
									if ( today.after( expirationDate ) )
									{
										expired = true;
									}
								}
								catch ( ParseException ex )
								{
									logger.info( "In hasPasswordExpired(), unable to parse expiration date: " + dateStr );
								}
							}
						}
						catch ( NamingException ex )
						{
							// Nothing to do.
						}
					}
				}
				else if ( dirType == LdapDirType.AD )
				{
					String attributesToRead[] = { AD_USER_ACCOUNT_CONTROL_ATTRIBUTE, AD_PASSWORD_LAST_SET_ATTRIBUTE };

					// Read the "userAccountControl" and "pwdLastSet" attributes from the directory.
					lAttrs = ctx.getAttributes( bd.getNameInNamespace(), attributesToRead );

					// Is the "password never expires" flag turned on for this user?
					if ( getPasswordNeverExpires( lAttrs ) == false )
					{
						Date pwdLastSetDate;
						
						// No
						// Get the date the password was last set.
						pwdLastSetDate = getPasswordLastSet( lAttrs );
						if ( pwdLastSetDate != null )
						{
							Integer maxPwdAge;
							
							// Read the maximum age of a password.
							maxPwdAge = getMaxPwdAge( ldapConfig );
							
							if ( maxPwdAge != null && maxPwdAge != 0 )
							{
								Date now;
								long sum; 
								
								now = new Date();
								sum = maxPwdAge;
								sum *= (24 * 60 * 60 * 1000);
								sum += pwdLastSetDate.getTime();
								if ( now.getTime() > sum )
									expired = true;
							}
						}
					}
				}
				
				if ( ctx != null )
				{
					try
					{
						ctx.close();
						ctx = null;
					}
					catch ( NamingException ex )
					{
						// Nothing to do
					}
				}

				// We found the user, no need to continue searching.
				break;
			}
			catch (NamingException ex)
			{
				// Nothing to do.
			}
		}// end for()

		if ( ctx != null )
		{
			try
			{
				ctx.close();
			}
			catch ( NamingException ex )
			{
				// Nothing to do
			}
		}
		
    	return expired;
    }
    
    /**
     * Determine if the user's password in the ldap directory has expired
     */
    @Override
    public boolean hasPasswordExpired( String userName, String ldapConfigId )
    {
    	boolean expired;
    	LdapConnectionConfig ldapConnectionConfig = null;
    	Long zoneId = null;
    	
    	expired = false;
    	
		try
		{
			ZoneModule zoneModule;
			
			zoneModule = (ZoneModule) SpringContextUtil.getBean( "zoneModule" );
			if ( zoneModule != null )
			{
				zoneId = zoneModule.getZoneIdByVirtualHost( ZoneContextHolder.getServerName() );
			}

			if(zoneId != null)
				ldapConnectionConfig = this.getConfigReadOnlyCache(zoneId, ldapConfigId);
			else
				ldapConnectionConfig = (LdapConnectionConfig) getCoreDao().load(
					LdapConnectionConfig.class,
					ldapConfigId );

		}
		catch( Exception e )
		{
			logger.warn("Error loading LDAP connection config object by ID [" + ldapConfigId + "]");
		}
	
		if( ldapConnectionConfig != null && zoneId != null )
		{
			// Limit search in LDAP only to the specific LDAP source identified by this configuration object.
			expired = hasPasswordExpired( userName, zoneId, ldapConnectionConfig );
		}
		
    	return expired;
    }
    
	/**
	 * Update a ssf user with an ldap person.  
	 * @param zoneName
	 * @param loginName
	 * @throws NoUserByTheNameException
	 * @throws NamingException
	 */
	@Override
	public void syncUser( String teamingUserName, String ldapUserName ) 
		throws NoUserByTheNameException, NamingException
	{
		if(logger.isDebugEnabled())
			logger.debug("Synching LDAP user '" + ldapUserName + "' to Vibe user '" + teamingUserName + "'");
		
		Workspace zone = RequestContextHolder.getRequestContext().getZone();
                ADLdapObject domainInfo = null;
		LdapSchedule schedule = new LdapSchedule(getSyncObject().getScheduleInfo(zone.getId()));
		Map mods = new HashMap();
		for(LdapConnectionConfig config : this.getConfigsReadOnlyCache(zone.getZoneId())) {
			LdapContext ctx = getUserContext(zone.getId(), config);
			String dn=null;
			Map userAttributes = config.getMappings();
			String [] userAttributeNames = 	(String[])(userAttributes.keySet().toArray(sample));
			String domainName =  null, netbiosName = null;

			domainInfo = getDomainInfo( config);
                        if (domainInfo != null) {
                            domainName = domainInfo.getDomainName();
                            netbiosName = domainInfo.getNetbiosName();
                        }                               
			
			for(LdapConnectionConfig.SearchInfo searchInfo : config.getUserSearches()) 
			{
				HomeDirInfo homeDirInfo = null;

				try {
					String[] attributesToRead;
					Attributes lAttrs;
					String typelessDN;

					int scope = (searchInfo.isSearchSubtree()?SearchControls.SUBTREE_SCOPE:SearchControls.ONELEVEL_SCOPE);
					SearchControls sch = new SearchControls(scope, 1, 0, userAttributeNames, false, false);
		
					String search = "(" + config.getUserIdAttribute() + "=" + ldapUserName + ")";
					String filter = searchInfo.getFilterWithoutCRLF();
					if(!Validator.isNull(filter)) {
						search = "(&"+search+filter+")";
					}
					NamingEnumeration ctxSearch = ctx.search(searchInfo.getBaseDn(), search, sch);
					if (!hasMore( ctxSearch )) {
						continue;
					}
					Binding bd = (Binding)ctxSearch.next();

					// Get the list of the ldap attribute names we want read from the ldap directory.
					attributesToRead = getAttributeNamesToRead( userAttributeNames, config );
					
					lAttrs = ctx.getAttributes( bd.getNameInNamespace(), attributesToRead );
					
					getUpdates( userAttributeNames, userAttributes, lAttrs, mods, config.getLdapGuidAttribute() );
					
					if (bd.isRelative() && Validator.isNotNull(ctx.getNameInNamespace())) {
						dn = bd.getNameInNamespace() + "," + ctx.getNameInNamespace();
					} else {
						dn = bd.getNameInNamespace();
					}
					
					// Get the typeless dn
					typelessDN = getTypelessDN( dn );

					mods.put(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, dn);

					// Make sure the name gets updated.
					mods.put( ObjectKeys.FIELD_PRINCIPAL_NAME, ldapUserName );
					
					mods.put( ObjectKeys.FIELD_PRINCIPAL_DOMAIN_NAME, domainName );
                                        
                                        mods.put( ObjectKeys.FIELD_PRINCIPAL_NETBIOS_NAME, netbiosName);

					// Update the typelessDN field
					mods.put( ObjectKeys.FIELD_PRINCIPAL_TYPELESS_DN, typelessDN );
				}
				finally
				{
					// Nothing to do.
				}

				if ( Utils.checkIfFilr() )
				{
					// Get the home directory info for this user
					LdapDirType dirType = getLdapDirType( config.getLdapGuidAttribute() );
					boolean createAsExternal = config.getImportUsersAsExternalUsers();
					if ( ! createAsExternal )
					{
						homeDirInfo = getHomeDirInfoFromConfig(
															ctx,
															dirType,
															dn,
															searchInfo.getHomeDirConfig(),
															false );
					}

					m_containerCoordinator.clear();
					// Read the list of all containers from the db.
					m_containerCoordinator.getListOfAllContainers();
					m_containerCoordinator.setLdapDirType( dirType );
					m_containerCoordinator.setLdapSyncMode( LdapSyncMode.PERFORM_SYNC );
					m_containerCoordinator.record( dn,    createAsExternal );
					m_containerCoordinator.wrapUp( false, createAsExternal );
				}

				updateUser( zone, teamingUserName, mods, homeDirInfo );
				
				if ( ctx != null )
				{
					try
					{
						ctx.close();
					}
					catch ( NamingException ex )
					{
						// Nothing to do
					}
				}

				return;
			}
			
			if ( ctx != null )
			{
				try
				{
					ctx.close();
				}
				catch ( NamingException ex )
				{
					// Nothing to do
				}
			}
		}
		throw new NoUserByTheNameException( teamingUserName );
	}// end syncUser()
	
	/**
	 * Get the parent container's dn.  Can return null
	 */
	private String getADContainerDN( String dn )
	{
		int index;
		
		if ( dn == null )
			return null;
		
		// Find the first ','
		index = dn.indexOf( ',' );
		if ( index > 0 && (index+1) < dn.length() )
		{
			String parentDn;
			
			parentDn = dn.substring( index+1 );
			
			// Does the parent dn start with "ou=" or "o=" or "l=" or "c=" or "dc=" or "st=" or "cn"?
			if ( parentDn.startsWith( "ou=" ) || parentDn.startsWith( "o=" ) ||
				 parentDn.startsWith( "l=" ) || parentDn.startsWith( "c=" ) ||
				 parentDn.startsWith( "dc=" ) || parentDn.startsWith( "st=" ) ||
				 parentDn.startsWith( "cn=" ) )
			{
				// Yes
				return parentDn;
			}
		}
		
		return null;
	}
	

	/**
	 * 
	 */
	@Override
	public ADLdapObject getLdapObjectFromAD( String fqdn )
		throws NamingException
	{
                ADLdapObject objInfo = null;
		Workspace zone;
		LdapSchedule schedule;
		String samAccountName = null;
		String baseDN;
                
                zone = RequestContextHolder.getRequestContext().getZone();
		schedule = new LdapSchedule(getSyncObject().getScheduleInfo(zone.getId()));
		
		// Get the dn of the container the object is in.
		baseDN = getADContainerDN( fqdn );
		
		if ( baseDN == null || baseDN.length() == 0 )
			return null;
		
		for( LdapConnectionConfig config : this.getConfigsReadOnlyCache(zone.getZoneId()) )
		{
			LdapContext ctx;
			LdapDirType dirType;

			ctx = getUserContext( zone.getId(), config );

			dirType = getLdapDirType( config.getLdapGuidAttribute() );
			if ( dirType != LdapDirType.AD )
				continue;
			
			objInfo = getDomainInfo( config );
                        
			try
			{
				String[] attributesToRead;
				Attributes lAttrs;
				int scope;
				SearchControls sch;
				String search;
				NamingEnumeration ctxSearch;
				Binding bd;
				Attribute att;

				attributesToRead = new String[2];
				attributesToRead[0] = SAM_ACCOUNT_NAME_ATTRIBUTE;
				attributesToRead[1] = "distinguishedName";
				
				scope = SearchControls.SUBTREE_SCOPE;
				sch = new SearchControls( scope, 1, 0, attributesToRead, false, false );
	
				search = "(distinguishedName=" + fqdn + ")";
				
				ctxSearch = ctx.search( baseDN, search, sch );
				if ( !hasMore( ctxSearch ) )
				{
					continue;
				}
				
				bd = (Binding)ctxSearch.next();

				lAttrs = ctx.getAttributes( bd.getNameInNamespace(), attributesToRead );

				att = lAttrs.get( SAM_ACCOUNT_NAME_ATTRIBUTE );
				if ( att != null && att.size() == 1 )
				{
					Object val;

					val = att.get();
					if ( val != null && val instanceof String )
					{
						samAccountName = ((String)val).trim();
					}
				}
			}
			catch ( Exception ex )
			{
				// Nothing to do
				logger.info( "in getLdapObjectFromAD()", ex );
			}
			finally
			{
				// Nothing to do.
				if ( ctx != null )
				{
					try
					{
						ctx.close();
						ctx = null;
					}
					catch ( NamingException ex )
					{
						// Nothing to do
					}
				}
			}

			if ( objInfo != null && samAccountName != null )
			{
				objInfo.setFQDN( fqdn );                                
				objInfo.setSamAccountName( samAccountName );

				return objInfo;
			}
		}
		
		// If we get here we could not find the user with the given fqdn
		return null;
	}
	
	/**
	 * Update a ssf user with an ldap person.  
	 * @param zoneName
	 * @param loginName
	 * @throws NoUserByTheNameException
	 * @throws NamingException
	 */
	@Override
	public void syncUser(Long userId) 
		throws NoUserByTheNameException, NamingException
	{
		Workspace zone;
		LdapSchedule schedule;
		User user;
		String userName;

		zone = RequestContextHolder.getRequestContext().getZone();
		schedule = new LdapSchedule(getSyncObject().getScheduleInfo(zone.getId()));
		user = getProfileDao().loadUser(userId, schedule.getScheduleInfo().getZoneId());

		userName = user.getName();
		syncUser( userName, userName );
	}// end syncUser()
	
	/**
	 * This routine alters group membership without updating the local
	 * caches.  Need to flush cache after use
	 * 
	 * @param syncUsersAndGroups
	 * @param listOfLdapConfigsToSyncGuid
	 * @param syncMode
	 * @param syncResults
	 * 
	 * @throws LdapSyncException
	 */
	@Override
	public void syncAll( boolean syncUsersAndGroups, String[] listOfLdapConfigsToSyncGuid, LdapSyncMode syncMode, LdapSyncResults syncResults ) throws LdapSyncException {
		syncAllImpl( syncUsersAndGroups, listOfLdapConfigsToSyncGuid, syncMode, syncResults, false );
		if ( ! ( Utils.checkIfFilr() ) )
		{
			syncAllImpl( syncUsersAndGroups, listOfLdapConfigsToSyncGuid, syncMode, syncResults, true );
		}
	}

	/*
	 * Main implementation method for syncAll().
	 */
	private void syncAllImpl( boolean syncUsersAndGroups, String[] listOfLdapConfigsToSyncGuid, LdapSyncMode syncMode, LdapSyncResults syncResults, boolean externalUserSync ) throws LdapSyncException
	{
		Workspace zone = RequestContextHolder.getRequestContext().getZone();
		Boolean syncInProgress;
		LdapSyncException ldapSyncEx = null;

//		if ( testPaging() )
//			return;
		
		// Is an ldap sync currently going on for the zone?
		syncInProgress = m_zoneSyncInProgressMap.get( zone.getId() );
		if ( syncInProgress != null && syncInProgress )
		{
			// Yes, don't start another sync.
	    	logger.error( "Unable to start ldap sync because an ldap sync is currently running" );
	    	syncResults.setStatus( SyncStatus.STATUS_SYNC_ALREADY_IN_PROGRESS );
			return;
		}
		
		// (Bug #900745) To avoid the problem disable interaction with the second-level cache
		CacheMode ldapSyncSecondLevelCacheMode = CacheMode.parse(SPropsUtil.getString("ldap.sync.secondlevel.cache.mode", "ignore").toUpperCase());
		if(ldapSyncSecondLevelCacheMode != null)
			SessionUtil.setCacheMode(ldapSyncSecondLevelCacheMode);
		
		try
		{
			boolean errorSyncingUsers;
			boolean errorSyncingGroups;
			boolean doUserCleanup = false;
	   		boolean delContainers;

			m_server_vol_map.clear();
			m_hostNameMap.clear();
			m_principalsToIndex.clear();
			m_zoneSyncInProgressMap.put( zone.getId(), Boolean.TRUE );
			
			// Sync guids if called for.
			if ( listOfLdapConfigsToSyncGuid != null && listOfLdapConfigsToSyncGuid.length > 0 )
			{
				try
				{
					logger.info( "about to call syncGuidAttributeForAllUsersAndGroups()" );
					syncGuidAttributeForAllUsersAndGroups( listOfLdapConfigsToSyncGuid, syncResults );
					logger.info( "back from syncGuidAttributeForAllUsersAndGroups()" );
				}
				catch ( LdapSyncException ex )
				{
					ldapSyncEx = ex;
				}
			}
			
			// If we don't need to sync users and groups then bail.
			if ( syncUsersAndGroups == false )
				return;
			
			m_containerCoordinator.clear();
			m_containerCoordinator.setLdapSyncMode( syncMode );
			
			// Read the list of all containers from the db.
			m_containerCoordinator.getListOfAllContainers();
			
			errorSyncingUsers = false;
			
			{
				Date now;
				
				now = new Date();
				m_numUsersCreated = 0;
				m_numUsersModified = 0;
				m_createUsersStartTime = now.getTime();
				m_ldapSyncStartTime = m_createUsersStartTime;

				m_showTiming = SPropsUtil.getBoolean( "ldap.sync.show.timings", false );
			}

			logger.info( "\n\n---------->Starting ldap sync...\n" );
			LdapSchedule info = new LdapSchedule(getSyncObject().getScheduleInfo(zone.getId()));
	    	UserCoordinator userCoordinator = new UserCoordinator(
	    													zone,
	    													info.isUserSync(),
	    													info.isUserRegister(),
	   														info.isUserDelete(),
	   														info.isUserWorkspaceDelete(),
	   														syncMode,
	   														syncResults,
	   														externalUserSync );
	    	
	    	int userConfigs       = 0;
	    	int userConfigsSynced = 0;
			for(LdapConnectionConfig config : getCoreDao().loadLdapConnectionConfigs(zone.getId())) {
				userConfigs += 1;
				
				// Is this configuration the right internal vs.
				// external type?
				if ( config.getImportUsersAsExternalUsers() != externalUserSync )
				{
					// No!  Skip it.
					continue;
				}
				userConfigsSynced += 1;
				
		   		LdapContext ctx=null;

		   		// Tell the ContainerCoordinator the type of ldap directory we are working with.
				m_containerCoordinator.setLdapDirType( getLdapDirType( config.getLdapGuidAttribute() ) );

		   		try
		   		{
					ctx = getUserContext(zone.getId(), config);

					logger.info( "ldap url used to search for users: " + config.getUrl() );
					syncUsers( zone, ctx, config, userCoordinator, externalUserSync );
					logger.info( "back from call to syncUsers()" );
		  		}
		  		catch ( Exception ex )
		  		{
	  				errorSyncingUsers = true;

	  				logger.error( "syncUsers() threw an exception: ", ex );
	  				
	  				if ( ex instanceof NamingException )
		  			{
			  			logError(NLT.get("errorcode.ldap.context"), ex);

			  			//~JW:  When we re-write the ldap config page in GWT, we need to collect all of these
			  			//~JW:  errors and return them instead of just throwing an exception for the first
			  			//~JW:  problem we find.
			  			// Have we already encountered a problem?
			  			if ( ldapSyncEx == null )
			  			{
			  				// No
				  			// Create an LdapSyncException.  We throw an LdapSyncException so we can return
				  			// the LdapConnectionConfig object that was being used when the error happened.
			  				// We will throw the exception after we have gone through all the ldap configs.
				  			ldapSyncEx = new LdapSyncException( config, (NamingException)ex );
			  			}
		  			}
		  			else
		  			{
		  				logger.error( "Unknown exception: " + ex.toString() );
		  				
			  			//~JW:  When we re-write the ldap config page in GWT, we need to collect all of these
			  			//~JW:  errors and return them instead of just throwing an exception for the first
			  			//~JW:  problem we find.
			  			// Have we already encountered a problem?
			  			if ( ldapSyncEx == null )
			  			{
			  				// No
			  				ldapSyncEx = new LdapSyncException( config, new NamingException( ex.toString() ) );
			  			}
		  			}
		  		}
		  		finally {
					if (ctx != null) {
						try
						{
							ctx.close();
						}
						catch (NamingException namingEx)
				  		{
							logger.error( "closing user context threw an exception: ", namingEx );
							
				  			//~JW:  When we re-write the ldap config page in GWT, we need to collect all of these
				  			//~JW:  errors and return them instead of just throwing an exception for the first
				  			//~JW:  problem we find.
				  			// Have we already encountered a problem?
				  			if ( ldapSyncEx == null )
				  			{
				  				// No
					  			// Create an LdapSyncException.  We throw an LdapSyncException so we can return
					  			// the LdapConnectionConfig object that was being used when the error happened.
				  				// We will throw the exception after we have gone through all the ldap configs.
				  				ldapSyncEx = new LdapSyncException( config, namingEx );
				  			}
				  		}
					}
				}
			}// end for()

	   		logger.info("ldap sync (externalUserSync:  " + externalUserSync + "):  userConfigs (Total):  " + userConfigs + ", userConfigs (Sync'ed):  " + userConfigsSynced);
	   		
	   		if ( ( 0 == userConfigsSynced ) && externalUserSync )
	   		{
		   		logger.info("ldap sync (externalUserSync):  Finished.  With no LDAP configurations to processes, there are no further sync actions required for external user syncs.");
	   			return;
	   		}
	   		
			try
			{
				logger.info( "Starting userCoordinator.wrapUp()" );
				doUserCleanup = false;
				if ( errorSyncingUsers == false )
				{
					// If "Synchronize User Profiles" and "Register ldap User Profiles Automatically" are
					// both turned off then don't disable or delete anyone because we never issued an ldap query which
					// would have caused us to call userCoordinator.record() which would have updated
					// notInLdap.
					if ( userCoordinator.sync != false || userCoordinator.create != false )
						doUserCleanup = true;
				}
				userCoordinator.wrapUp( doUserCleanup, externalUserSync );
				logger.info( "Finished userCoordinator.wrapUp()" );

				if ( m_showTiming )
				{
					long elapsedTimeInSeconds;
					long minutes;
					long seconds;
					Date now;
					
					now = new Date();

					elapsedTimeInSeconds = now.getTime() - m_createUsersStartTime;
					elapsedTimeInSeconds /= 1000;
					minutes = elapsedTimeInSeconds / 60;
					seconds = elapsedTimeInSeconds - (minutes * 60);
					logger.info( "ldap sync timing: ----------> Time taken to create " + m_numUsersCreated + " users and modify " + m_numUsersModified + " users: " + minutes + " minutes " + seconds + " seconds" );
				}
			}
			catch ( Exception ex )
			{
				logger.info( "userCoordinator.wrapUp() threw an exception: ", ex );
  				
	  			//~JW:  When we re-write the ldap config page in GWT, we need to collect all of these
	  			//~JW:  errors and return them instead of just throwing an exception for the first
	  			//~JW:  problem we find.
	  			// Have we already encountered a problem?
	  			if ( ldapSyncEx == null )
	  			{
	  				// No
	  				ldapSyncEx = new LdapSyncException( null, new NamingException( ex.toString() ) );
	  			}
			}

			// Did we encounter an error syncing users?
			if ( errorSyncingUsers )
			{
				logger.error( "An error was encountered syncing users.  Ldap sync will not continue." );
				
				if ( ldapSyncEx != null )
				{
					logger.error( "Exception encountered while syncing users: ", ldapSyncEx );
		   			throw ldapSyncEx;
				}

				logger.error( "Unknown error syncing users" );
				return;
			}
			
			{
				Date now;
				
				now = new Date();
				m_createGroupsStartTime = now.getTime();
			}
			
			errorSyncingGroups = false;
	   		GroupCoordinator groupCoordinator = new GroupCoordinator(
	   															zone,
	   															userCoordinator.dnUsers,
	   															info.isGroupSync(),
	   															info.isGroupRegister(),
	   															info.isGroupDelete(),
	   															syncMode,
	   															syncResults,
	   															externalUserSync );
	   		int groupConfigs       = 0;
	   		int groupConfigsSynced = 0;
	   		for(LdapConnectionConfig config : getCoreDao().loadLdapConnectionConfigs(zone.getId()))
	   		{
	   			groupConfigs += 1;
	   			
				// Is this configuration the right internal vs.
				// external type?
				if ( config.getImportUsersAsExternalUsers() != externalUserSync )
				{
					// No!  Skip it.
					continue;
				}
				groupConfigsSynced += 1;

		   		LdapContext ctx=null;
		  		try {

			   		// Tell the ContainerCoordinator the type of ldap directory we are working with.
					m_containerCoordinator.setLdapDirType( getLdapDirType( config.getLdapGuidAttribute() ) );

		  			ctx = getGroupContext(zone.getId(), config);
				
		  			logger.info( "ldap url used to search for groups: " + config.getUrl() );
					syncGroups( zone, ctx, config, groupCoordinator, info.isMembershipSync(), externalUserSync );
			   		logger.info( "Finished syncGroups()" );
				}
		  		catch (Exception ex)
		  		{
		  			errorSyncingGroups = true;
		  			logger.error( "syncGroups() threw an exception: ", ex );

	  				if ( ex instanceof NamingException )
		  			{
			  			logError( NLT.get( "errorcode.ldap.context" ), ex );

			  			//~JW:  When we re-write the ldap config page in GWT, we need to collect all of these
			  			//~JW:  errors and return them instead of just throwing an exception for the first
			  			//~JW:  problem we find.
			  			// Have we already encountered a problem?
			  			if ( ldapSyncEx == null )
			  			{
			  				// No
				  			// Create an LdapSyncException.  We throw an LdapSyncException so we can return
				  			// the LdapConnectionConfig object that was being used when the error happened.
			  				// We will throw the exception after we have gone through all the ldap configs.
				  			ldapSyncEx = new LdapSyncException( config, (NamingException)ex );
			  			}
		  			}
		  			else
		  			{
		  				logger.error( "Unknown exception: " + ex.toString() );
		  				
			  			//~JW:  When we re-write the ldap config page in GWT, we need to collect all of these
			  			//~JW:  errors and return them instead of just throwing an exception for the first
			  			//~JW:  problem we find.
			  			// Have we already encountered a problem?
			  			if ( ldapSyncEx == null )
			  			{
			  				// No
			  				ldapSyncEx = new LdapSyncException( config, new NamingException( ex.toString() ) );
			  			}
		  			}
		  		}
		  		finally
		  		{
					if (ctx != null) {
						try
						{
							ctx.close();
						}
						catch (NamingException namingEx)
				  		{
							logger.error( "closing group context threw an exception: ", namingEx );
							
				  			//~JW:  When we re-write the ldap config page in GWT, we need to collect all of these
				  			//~JW:  errors and return them instead of just throwing an exception for the first
				  			//~JW:  problem we find.
				  			// Have we already encountered a problem?
				  			if ( ldapSyncEx == null )
				  			{
				  				// No
				  				// Create an LdapSyncException.  We throw an LdapSyncException so we can return
				  				// the LdapConnectionConfig object that was being used when the error happened.
				  				// We will throw the exception after we have gone through all the ldap configs.
				  				ldapSyncEx = new LdapSyncException( config, namingEx );
				  			}
				  		}
					}
		  		}
			}// end for()
	   		
	   		logger.info("ldap sync (externalUserSync:  " + externalUserSync + "):  groupConfigs (Total):  " + groupConfigs + ", groupConfigs (Sync'ed):  " + groupConfigsSynced);
	   		
			if ( m_showTiming )
			{
				long elapsedTimeInSeconds;
				long minutes;
				long seconds;
				Date now;
				
				now = new Date();

				elapsedTimeInSeconds = now.getTime() - m_createGroupsStartTime;
				elapsedTimeInSeconds /= 1000;
				minutes = elapsedTimeInSeconds / 60;
				seconds = elapsedTimeInSeconds - (minutes * 60);
				logger.info( "ldap sync timing: ----------> Time taken to sync groups " + minutes + " minutes " + seconds + " seconds" );
			}
			
	   		if ( errorSyncingGroups )
	   		{
				logger.error( "An error was encountered syncing groups.  Ldap sync will not continue." );
				
				if ( ldapSyncEx != null )
				{
					logger.error( "Exception encountered while syncing groups: ", ldapSyncEx );
		   			throw ldapSyncEx;
				}

				logger.error( "Unknown error syncing groups" );
				return;
	   		}

	   		try
	   		{
		   		logger.info( "About to call groupCoordinator.deleteObsoleteGroups()" );
	   			groupCoordinator.deleteObsoleteGroups();
		   		logger.info( "Finished groupCoordinator.deleteObsoleteGroups()" );
	   		}
	   		catch( Exception ex )
	   		{
  				logger.error( "groupCoordinator.deleteObsoleteGroups() threw exception: ", ex );
  				
	  			//~JW:  When we re-write the ldap config page in GWT, we need to collect all of these
	  			//~JW:  errors and return them instead of just throwing an exception for the first
	  			//~JW:  problem we find.
	  			// Have we already encountered a problem?
	  			if ( ldapSyncEx == null )
	  			{
	  				// No
	  				ldapSyncEx = new LdapSyncException( null, new NamingException( ex.toString() ) );
	  			}
	   		}

	   		// Find all groups that have dynamic membership and update the membership
	   		// of those groups that are supposed to be updated during the ldap sync process
	   		try
	   		{
	   			ArrayList<Long> listOfDynamicGroups;
	   			
				logger.info( "Looking for dynamic groups to update... " );

				listOfDynamicGroups = getListOfDynamicGroups();
	   			
	   			// Do we have any dynamic groups?
	   			if ( listOfDynamicGroups != null && listOfDynamicGroups.size() > 0 )
	   			{
	   				// Yes
	   				for ( Long nextGroupId : listOfDynamicGroups )
	   				{
	   					updateDynamicGroupMembership( nextGroupId, syncMode, groupCoordinator.getLdapSyncResults() );
	   				}
	   			}

	   			logger.info( "Finished looking for dynamic groups." );
	   		}
	   		catch( Exception ex )
	   		{
  				logger.error( "updateDynamicGroupMembership() threw exception: ", ex );
  				
	  			//~JW:  When we re-write the ldap config page in GWT, we need to collect all of these
	  			//~JW:  errors and return them instead of just throwing an exception for the first
	  			//~JW:  problem we find.
	  			// Have we already encountered a problem?
	  			if ( ldapSyncEx == null )
	  			{
	  				// No
	  				ldapSyncEx = new LdapSyncException( null, new NamingException( ex.toString() ) );
	  			}
	   		}
	   		
	   		// Finish creating / deleting containers.
	   		try
	   		{
	   			logger.info( "About to call m_containerCoordinator.wrapUp()" );
		   		delContainers = false;
		   		if ( errorSyncingUsers == false && errorSyncingGroups == false )
		   			delContainers = true;
		   		m_containerCoordinator.wrapUp( delContainers, externalUserSync );
	   			logger.info( "Back from call to m_containerCoordinator.wrapUp();" );
	   		}
	   		catch ( Exception ex )
	   		{
  				logger.error( "m_containerCoordinator.wrapUp() threw exception: ", ex );
  				
	  			//~JW:  When we re-write the ldap config page in GWT, we need to collect all of these
	  			//~JW:  errors and return them instead of just throwing an exception for the first
	  			//~JW:  problem we find.
	  			// Have we already encountered a problem?
	  			if ( ldapSyncEx == null )
	  			{
	  				// No
	  				ldapSyncEx = new LdapSyncException( null, new NamingException( ex.toString() ) );
	  			}
	   		}

	   		// Remove the admin task to run an ldap sync to import typeless dn information
	   		try
	   		{
	   	 		User superUser;
	   			
	   	 		//	get super user from config file - must exist or throws and error
	   			superUser = getProfileDao().getReservedUser(
	   													ObjectKeys.SUPER_USER_INTERNALID,
	   													zone.getId() );
	
				getProfileModule().setUserProperty(
												superUser.getId(),
												ObjectKeys.USER_PROPERTY_UPGRADE_IMPORT_TYPELESS_DN,
												"true" );
	   		}
	   		catch ( Exception ex )
	   		{
  				logger.error( "Removing admin task to run ldap sync threw exception: ", ex );
  				
	  			//~JW:  When we re-write the ldap config page in GWT, we need to collect all of these
	  			//~JW:  errors and return them instead of just throwing an exception for the first
	  			//~JW:  problem we find.
	  			// Have we already encountered a problem?
	  			if ( ldapSyncEx == null )
	  			{
	  				// No
	  				ldapSyncEx = new LdapSyncException( null, new NamingException( ex.toString() ) );
	  			}
	   		}
	   		
	   		if ( ldapSyncEx != null )
	   		{
	   			logger.info( "Finished syncAll() with an exception: " + ldapSyncEx.toString() );
	   			throw ldapSyncEx;
	   		}
	   		
   			// Reindex all principals whose group membership changed.
   			logger.info( "About to call reindexPrincipals()" );
   			reindexPrincipals();
   			logger.info( "Back from call reindexPrincipals()" );
   			
	   		logger.info( "Finished syncAll() with no exceptions" );
	   		
			if ( m_showTiming )
			{
				long elapsedTimeInSeconds;
				long minutes;
				long seconds;
				Date now;
				
				now = new Date();

				elapsedTimeInSeconds = now.getTime() - m_ldapSyncStartTime;
				elapsedTimeInSeconds /= 1000;
				minutes = elapsedTimeInSeconds / 60;
				seconds = elapsedTimeInSeconds - (minutes * 60);
				logger.info( "ldap sync timing: ----------> Total time taken for ldap sync: " + minutes + " minutes " + seconds + " seconds" );
			}
		}// end try
		finally
		{
			m_zoneSyncInProgressMap.put( zone.getId(), Boolean.FALSE );
			
			// Because we called getCoreDao().clear() the ldap configurations in the read-only cache are invalid
			readOnlyCache.clear();
		}
	}

	
	/**
	 * Users whose group membership changed need to be reindex.
	 */
	private void reindexPrincipals()
	{
		long startTime = 0;
		
		if ( m_showTiming )
		{
			Date now;

			now = new Date();
			startTime = now.getTime();
		}

		if ( m_principalsToIndex != null )
			logger.info( "--> Number of principals to reindex: " + m_principalsToIndex.size() );
		
		if ( m_principalsToIndex != null && m_principalsToIndex.size() > 0 )
		{
			try
			{
				ProfileModule profileModule;
				Map<Long,Principal> principalMap;
				Iterator<Long> iter;
				
				profileModule = getProfileModule();
				
				principalMap = new HashMap<Long,Principal>();
				iter = m_principalsToIndex.iterator();
				
				while ( iter.hasNext() )
				{
					Long principalId;
					
					principalId = iter.next();
					
					try
					{
						Principal principal;

						principal = profileModule.getEntry( principalId );
						principalMap.put( principalId, principal );
					}
					catch ( Exception ex )
					{
						logger.info( "In reindexPrincipals(), getProfileModule().getEntry() threw an exception.", ex );
					}
				}
				
				Utils.reIndexPrincipals( profileModule, principalMap );
			}
			catch ( Exception ex )
			{
				logger.error( "In reindexPrincipals(), Utils.reIndexPrincipals() threw an exception: ", ex );
			}
		}

		if ( m_showTiming )
		{
			long elapsedTimeInSeconds;
			long minutes;
			long seconds;
			long milliSeconds;
			Date now;
			
			now = new Date();

			milliSeconds = now.getTime() - startTime;
			elapsedTimeInSeconds = milliSeconds / 1000;
			minutes = elapsedTimeInSeconds / 60;
			seconds = elapsedTimeInSeconds - (minutes * 60);
			milliSeconds = milliSeconds - (elapsedTimeInSeconds * 1000);
			logger.info( "ldap sync timing: ----------> Time to reindex principals: " + minutes + " minutes " + seconds + " seconds " + milliSeconds + " milliseconds " );
		}
	}
	
	/**
	 * Execute the given ldap query and return how many users/groups were found
	 * @author jwootton
	 */
	@Override
	public Integer testGroupMembershipCriteria( String baseDn, String filter, boolean searchSubtree ) throws LdapSyncException
	{
		int count = 0;
		
		// Does the membership criteria have a filter?
		if ( filter != null && filter.length() > 0 )
		{
			HashSet<User> setOfMembers;
			
			setOfMembers = getDynamicGroupMembers( baseDn, filter,  searchSubtree );
			if ( setOfMembers != null )
				count = setOfMembers.size();
		}
		
		return new Integer( count );
	}
	
	/**
	 * 
	 */
	private void updateDynamicGroupMembership(
		Long groupId,
		LdapSyncMode syncMode,
		LdapSyncResults ldapSyncResults )
	{
		Principal principal;
		
		principal = getProfileModule().getEntry( groupId );
		if ( principal != null && principal instanceof Group )
		{
			Group group;
			String ldapQueryXml;
			String baseDn = null;
			String ldapFilter = null;
			boolean searchSubtree = false;
			boolean updateMembership = false;
			
			group = (Group) principal;
			ldapQueryXml = group.getLdapQuery();

			if ( ldapQueryXml != null && ldapQueryXml.length() > 0 )
			{
				try
	    		{
	    			Document doc;
	    			Node node;
	    			Node attrNode;
	    			String value;
					
					// Parse the xml string into an xml document.
					doc = DocumentHelper.parseText( ldapQueryXml );
	    			
	    			// Get the root element.
	    			node = doc.getRootElement();
	    			
	    			// Get the "updateMembershipDuringLdapSync" attribute value.
	    			attrNode = node.selectSingleNode( "@updateMembershipDuringLdapSync" );
	    			if ( attrNode != null )
	    			{
	        			value = attrNode.getText();
	        			if ( value != null && value.equalsIgnoreCase( "true" ) )
	        				updateMembership = true;
	    			}

	    			if ( updateMembership )
	    			{
		    			Node searchNode;

		    			// Get the <search ...> element.
		    			searchNode = node.selectSingleNode( "search" );
		    			if ( searchNode != null )
		    			{
	    					Node baseDnNode;
	    					Node filterNode;
	    					
		    				// Get the "searchSubtree" attribute.
		    				attrNode = searchNode.selectSingleNode( "@searchSubtree" );
		    				if ( attrNode != null )
		    				{
		    					value = attrNode.getText();
		    					if ( value != null && value.equalsIgnoreCase( "true" ) )
		    						searchSubtree = true;
		    					else
		    						searchSubtree = false;
		    				}
		    				
		    				// Get the <baseDn> element.
		    				baseDnNode = searchNode.selectSingleNode( "baseDn" );
		    				if ( baseDnNode != null )
		    				{
		    					baseDn = baseDnNode.getText();
		    				}
		    				
		    				// Get the <filter> element.
		    				filterNode = searchNode.selectSingleNode( "filter" );
		    				if ( filterNode != null )
		    				{
		    					ldapFilter = filterNode.getText();
		    				}
		    			}
	    			}
	    		}
	    		catch(Exception e)
	    		{
	    			// Nothing to do
	    		}
			}
			
			// Should we update the dynamic group membership of this group?
			if ( updateMembership )
			{
				HashSet<User> groupMemberUsers;
				
				// Yes
				try
				{
					ArrayList<Membership> newMembers;
					PartialLdapSyncResults syncResults = null;
					int count;
					int maxCount;
					
					logger.info( "\tEvaluating dynamic group membership for group: " + group.getName() );

					// Get a list of the dynamic group members.
					groupMemberUsers = getDynamicGroupMembers( baseDn, ldapFilter, searchSubtree );
					
					newMembers = new ArrayList<Membership>();
					count = 0;
					
					// Get the maximum number of users that can be in a group.
					maxCount = SPropsUtil.getInt( "dynamic.group.membership.limit", 50000 ); 					
					
					for (User user : groupMemberUsers)
					{
						logger.info( "\t\tAdding user: " + String.valueOf( user.getId() ) );
						newMembers.add( new Membership( groupId, user.getId() ) );
						++count;
						
						if ( count >= maxCount )
						{
							logger.info( "\t\t!!! Maximum number of dynamic users is a group has been reached.  " + String.valueOf( maxCount ) + " users" );
							break;
						}
					}
					
					if ( ldapSyncResults != null )
					{
						syncResults = ldapSyncResults.getModifiedGroups();
					}
					
					logger.info( "\t\tAbout to update dynamic group: " + group.getName() );
					updateMembership( groupId, newMembers, syncMode, syncResults );
				}
				catch ( LdapSyncException e )
				{
					
				}
			}
		}
	}
	

	/**
	 * 
	 */
	private void addPrincipalsToPreviewSyncResults( Map<String,Map> principals, PartialLdapSyncResults syncResults )
	{
		StringBuffer result;

		if ( syncResults == null )
			return;
		
		result = new StringBuffer();

		// Record the names of the users we would have created.
		for (Iterator iter = principals.values().iterator(); iter.hasNext();)
		{
			Map attrs;
			Object value;
			String name;
			String dn;

			name = null;
			dn = null;
			result.setLength( 0 );
			
			attrs = (Map)iter.next();
			value = attrs.get( ObjectKeys.FIELD_PRINCIPAL_NAME ); 
			if (  value != null )
				name = (String) value;
			
			value = attrs.get( ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME );
			if ( value != null )
				dn = (String) value;
			
			if ( name != null )
			{
				result.append( name );
				
				if ( dn != null )
				{
					result.append( " (" );
					result.append( dn );
					result.append( ")" );
				}
			}
			
			if( result.length() > 0 )
				syncResults.addResult( result.toString() );
		}
	}
	
	/**
	 * This class holds information about a container.
	 */
	class ContainerInfo
	{
		// m_referenced tells us whether this container is still being used.
		private boolean m_referenced;
		private String m_dn;
		private Long m_id;
		private String m_typelessDN;
		
		/**
		 * 
		 */
		public ContainerInfo( Long id, String dn, String typelessDN )
		{
			m_referenced = false;
			m_id = id;
			m_dn = dn;
			m_typelessDN = typelessDN;
		}
		
		/**
		 * 
		 */
		public String getDn()
		{
			return m_dn;
		}
		
		/**
		 * 
		 */
		public Long getId()
		{
			return m_id;
		}
		
		/**
		 * 
		 */
		public String getTypelessDN()
		{
			return m_typelessDN;
		}
		
		/**
		 * 
		 */
		public boolean isReferenced()
		{
			return m_referenced;
		}
		
		/**
		 * 
		 */
		public void setReferenced( boolean referenced )
		{
			m_referenced = referenced;
		}
	}
	
	
	/**
	 * This class is used to manage the creation and deletion of Containers. 
	 */
	class ContainerCoordinator
	{
		// m_existingContainers holds a list of all the Container objects that exist in the Vibe db
		// A container's dn is the key and a ContainerInfo object is the value.
		private HashMap<String, ContainerInfo> m_existingContainers;
		
		// m_containersToBeCreated holds the list of dns for containers that we need to create.
		private HashMap<String,String> m_containersToBeCreated;
		
		// The ldap directory type we are currently working with.
		private LdapDirType m_dirType;
		
		private LdapSyncMode m_syncMode;
		
		/**
		 * 
		 */
		public ContainerCoordinator()
		{
			m_existingContainers = new HashMap<String,ContainerInfo>();
			m_containersToBeCreated = new HashMap<String,String>();
			m_dirType = LdapDirType.UNKNOWN;
		}
		
		/**
		 * 
		 */
		private void addContainerToBeCreated( String dn )
		{
			if ( Utils.checkIfFilr() )
			{
				ContainerInfo containerInfo;
				String parentContainerDn;
				
				// Does this container already exist in Filr?
				containerInfo = m_existingContainers.get( dn );
				if ( containerInfo != null )
				{
					// Yes, mark the container as being referenced.
					containerInfo.setReferenced( true );
				}
				else
				{
					// No
					// Does this container already exist in the list of containers to be created?
					if ( m_containersToBeCreated.get( dn ) == null )
					{
						// No
						// Add the given dn to the list of containers to be created.
						logger.info( "\t\tAdding: " + dn + " to the list of containers to be created" );
						m_containersToBeCreated.put( dn, dn );
					}
				}
				
				// Does this container have a parent?
				parentContainerDn = getParentContainerDn( dn );
				if ( parentContainerDn != null && parentContainerDn.length() > 0 )
				{
					// Yes, add it to the list of containers to be created.
					addContainerToBeCreated( parentContainerDn );
				}
			}
		}
		
		/**
		 * Add the dn of the given group to our list of existing containers. 
		 */
		private void addExistingContainer( Group container )
		{
			Long id;
			String dn;
			String typelessDN;
			ContainerInfo containerInfo;
			
			id = container.getId();
			dn = container.getForeignName();
			typelessDN = container.getTypelessDN();
			containerInfo = new ContainerInfo( id, dn, typelessDN );
			m_existingContainers.put( dn, containerInfo );
		}
		
		/**
		 * Add the container (and all its parent containers) for the given principal dn
		 */
		private void addContainersForPrincipal( String principalDn )
		{
			if ( Utils.checkIfFilr() )
			{
				String parentContainerDn;
				
				// Get the dn of the container the given principal lives in.
				parentContainerDn = getParentContainerDn( principalDn );
				if ( parentContainerDn != null )
				{
					// Add the parent container to the list of containers to be created.
					addContainerToBeCreated( parentContainerDn );
				}
			}
		}
		
		/**
		 * 
		 */
		public void clear()
		{
			m_dirType = LdapDirType.UNKNOWN;
			m_containersToBeCreated.clear();
			m_existingContainers.clear();
		}
		
		/*
		 * Create a new "container group" with the given dn.
		 */
		private Group createContainerGroup( String dn )
		{
			Map inputMap;
	    	MapInputData groupMods;
			IdentityInfo identityInfo;
			Workspace zone;
			Long zoneId;
			Group temp;
			Definition groupDef;
			String typelessDN;

			profileModule = getProfileModule();
			
			zone = RequestContextHolder.getRequestContext().getZone();
			zoneId = zone.getId();

			logger.info( "\tAbout to create container: " + dn );

			inputMap = new HashMap();
			
			// Get the typeless dn
			typelessDN = getTypelessDN( dn );

			identityInfo = new IdentityInfo( true, true, false, false );
			
			inputMap.put( ObjectKeys.FIELD_PRINCIPAL_NAME, dn );
			inputMap.put( ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, dn );
			inputMap.put( ObjectKeys.FIELD_USER_PRINCIPAL_IDENTITY_INFO, identityInfo );
		    inputMap.put( ObjectKeys.FIELD_GROUP_LDAP_CONTAINER, true );
			inputMap.put( ObjectKeys.FIELD_PRINCIPAL_TYPELESS_DN, typelessDN );
			inputMap.put( ObjectKeys.FIELD_ZONE, zoneId );

	    	groupMods = new MapInputData( StringCheckUtil.check( inputMap ) );
			
	    	// Get default definition to use
			temp = new Group( identityInfo );
			getDefinitionModule().setDefaultEntryDefinition( temp );
			groupDef = getDefinitionModule().getDefinition( temp.getEntryDefId() );
			try 
			{
		    	ProfileCoreProcessor processor;
				ProfileBinder profileBinder;
		    	List newGroups;

				profileBinder = getProfileDao().getProfileBinder( zoneId );
		    	processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
		    																	profileBinder,
		    																	ProfileCoreProcessor.PROCESSOR_KEY );
		    	newGroups = processor.syncNewEntries(
		    									profileBinder,
		    									groupDef,
		    									Group.class,
		    									Arrays.asList( new MapInputData[] {groupMods} ),
		    									null,
		    									null,
		    									identityInfo );
		    	
		    	IndexSynchronizationManager.applyChanges(); //apply now, syncNewEntries will commit
		    	
		    	return (Group) newGroups.get( 0 );		    	
			}
			catch ( Exception ex )
			{
				logger.error( "\tError creating container: " + dn );
			}

			return null;
		}
		
		/**
		 * Create all containers found in m_containersToBeCreated
		 */
		private void createNewContainers()
		{
			// Are we in "preview" mode?
			if ( m_syncMode == LdapSyncMode.PREVIEW_ONLY )
			{
				// Yes, nothing to do.
				return;
			}
			
			if ( Utils.checkIfFilr() && m_containersToBeCreated.size() > 0 )
			{
				// Go through the list of containers that need to be created and create them.
				for ( Map.Entry<String, String> mapEntry : m_containersToBeCreated.entrySet() )
				{
					String dn;

					dn = mapEntry.getKey();
					createContainerGroup( dn );
				}
			}
		}
		
		/**
		 * Delete containers that are no longer being referenced.
		 */
		private void deleteObsoleteContainers()
		{
			// Are we in "preview" mode?
			if ( m_syncMode == LdapSyncMode.PREVIEW_ONLY )
			{
				// Yes, nothing to do.
				return;
			}
			
			if ( Utils.checkIfFilr() )
			{
				Map options;
				int count;
				
				count = 0;
				options = new HashMap();
				options.put( ObjectKeys.INPUT_OPTION_DELETE_USER_WORKSPACE, Boolean.FALSE );

				// Go through the list of existing containers and delete those that are no longer
				// being referenced.
				for ( Map.Entry<String, ContainerInfo> mapEntry : m_existingContainers.entrySet() )
				{
					ContainerInfo containerInfo;
					
					// Is this container still referenced?
					containerInfo = mapEntry.getValue();
					if ( containerInfo != null && containerInfo.isReferenced() == false )
					{
						logger.info( "\tAbout to delete container: " + mapEntry.getKey() );
						
						try
						{
							getProfileModule().deleteEntry( containerInfo.getId(), options, true );
							
							++count;
						}
						catch ( Exception ex )
						{
							logger.error( "\tError deleting container group: " + containerInfo.getDn() );
						}
					}
				}

				// Did we delete any containers?
				if ( count > 0 )
				{
					// Yes
					getProfileModule().deleteEntryFinish();
				}
				
				IndexSynchronizationManager.applyChanges();
			}
		}
		
		/**
		 * Read all of the "container" objects from the db.
		 */
		public void getListOfAllContainers()
		{
			List<Group> listOfContainers;
			
			listOfContainers = getProfileModule().getLdapContainerGroups();
			if ( listOfContainers != null )
			{
				for ( Group nextContainer : listOfContainers )
				{
					addExistingContainer( nextContainer );
				}
			}
		}
		
		/**
		 * Get the parent container's dn.  Can return null
		 */
		private String getParentContainerDn( String dn )
		{
			int index;
			
			if ( dn == null )
				return null;
			
			// Find the first ','
			index = dn.indexOf( ',' );
			if ( index > 0 && (index+1) < dn.length() )
			{
				String parentDn;
				
				parentDn = dn.substring( index+1 );
				
				// Does the parent dn start with "ou=" or "o=" or "l=" or "c=" or "dc=" or "st="?
				if ( parentDn.startsWith( "ou=" ) || parentDn.startsWith( "o=" ) ||
					 parentDn.startsWith( "l=" ) || parentDn.startsWith( "c=" ) ||
					 parentDn.startsWith( "dc=" ) || parentDn.startsWith( "st=" ) )
				{
					// Yes
					return parentDn;
				}
			}
			
			return null;
		}
		
		/**
		 * 
		 */
		public void record( String principalDn, boolean createAsExternal )
		{
			// We only provision containers for eDirectory
			if ( Utils.checkIfFilr() && ( m_dirType == LdapDirType.EDIR ) && ( ! createAsExternal ))
			{
				principalDn = principalDn.toLowerCase();
				addContainersForPrincipal( principalDn );
			}
		}
		
		/**
		 * Set the type of ldap directory we are working with.
		 */
		public void setLdapDirType( LdapDirType dirType )
		{
			m_dirType = dirType;
		}
		
		/**
		 * 
		 */
		public void setLdapSyncMode( LdapSyncMode syncMode )
		{
			m_syncMode = syncMode;
		}
		
		/**
		 * 
		 */
		public void updateExistingContainers()
		{
			// Are we in "preview" mode?
			if ( m_syncMode == LdapSyncMode.PREVIEW_ONLY )
			{
				// Yes, nothing to do.
				return;
			}
			
			if ( Utils.checkIfFilr() )
			{
				// Go through the list of existing containers and update the typelessDN field
				// for those containers that are still being referenced.  The only reason we
				// need to do this is because ldap container objects may have been created
				// without the typelessDN field being set.
				// See bug 880589
				for ( Map.Entry<String, ContainerInfo> mapEntry : m_existingContainers.entrySet() )
				{
					ContainerInfo containerInfo;
					
					// Is this container still referenced?
					containerInfo = mapEntry.getValue();
					if ( containerInfo != null && containerInfo.isReferenced() )
					{
						String typelessDN;
						
						// Does this container have a typeless dn?
						typelessDN = containerInfo.getTypelessDN();
						if ( typelessDN == null || typelessDN.length() == 0 )
						{
							Map updates;

							// No
							logger.info( "\tAbout to update container: " + containerInfo.getDn() );
							
							typelessDN = getTypelessDN( containerInfo.getDn() );
							
							updates = new HashMap();
							updates.put( ObjectKeys.FIELD_PRINCIPAL_TYPELESS_DN, typelessDN );
	
							try
							{
								getProfileModule().modifyEntry( containerInfo.getId(), new MapInputData( updates ) );
							}
				   			catch ( Exception ex )
				   			{
				   				logger.error( "\tError updating typelessDN for container: " + containerInfo.getDn() );
				   			}
						}
					}
				}
			}
		}
		
		/**
		 * Create any new containers and delete any obsolete container.
		 */
		public void wrapUp( boolean deleteObsoleteContainers, boolean createAsExternal )
		{
			if ( Utils.checkIfFilr() && ( ! createAsExternal ) )
			{
				// Delete obsolete containers.
				if ( deleteObsoleteContainers )
					deleteObsoleteContainers();
				
				// Create new containers.
				createNewContainers();
				
				// Update existing containers
				updateExistingContainers();
				
				// Clear everything out.
				clear();
			}
		}
	}
	
	
	class UserCoordinator
	{
		// m_listOfUsersByLdapGuid is a list of all users returned from the call to loadObjects(...)
		// Key: ldap guid
		// Value: array of values read from db.
		Map<String, Object[]> m_listOfUsersByLdapGuid = new TreeMap(String.CASE_INSENSITIVE_ORDER);

		// ssUsers is a list of all users returned from the call to loadObjects(...)
		// Key: user name
		// Value: array of values read from db.
		Map<String, Object[]> ssUsers = new TreeMap(String.CASE_INSENSITIVE_ORDER);

		// ssDnUsers is a list of all users returned from the call to loadObjects(...)
		// Key: user dn
		// Value: array of values read from db.
		Map<String, Object[]> ssDnUsers = new TreeMap(String.CASE_INSENSITIVE_ORDER);
		
		// notInLdap is initially a list of all users returned from the call to loadObjects(...)
		// When a user is read from the ldap directory they are removed from this list.
		// Key: Teaming id
		// Value: user name
		Map<Long, String> notInLdap = new TreeMap();
		
		// As we find existing users to be sync'd they are added to ldap_existing
		// Key: Teaming id
		// Value: array of values read from db.
		Map<Long, Map> ldap_existing = new HashMap();
		
		// As we find existing users to be sync'd we will create a HomeDirInfo for them in case
		// we need to create or update their "home dir" net folder.  We put the HomeDirInfo object
		// in ldap_existing_homeDirInfo
		// Key: Teaming id
		// Value: HomeDirInfo object.  May be null
		Map<Long, HomeDirInfo> ldap_existing_homeDirInfo = new HashMap<Long, HomeDirInfo>();
		
		// ldap_new will be a list of users we need to create.
		// Key: user name
		// Value: Map of attributes to be written to the db
		Map<String, Map> ldap_new = new HashMap();
		
		// ldap_new_homeDirInfo will be a list of users that need to be created and their home directory information
		// Key: user name
		// Value: HomeDirInfo object.  May be null
		Map<String, HomeDirInfo> ldap_new_homeDirInfo = new HashMap<String, HomeDirInfo>();
		
		// As we find existing users they are added to dnUsers.  Users that are created are added to dnUsers too.
		// Key: user dn
		// Value: array of values read from db.
		Map<String, Object[]> dnUsers = new TreeMap(String.CASE_INSENSITIVE_ORDER);

		//Keep names that have been processed.  Use case_insensitive match cause 
		//mysql treats them as equal
		//This will catch 2 ldap names that differ by case
		Map<String, String> foundNames = new TreeMap(String.CASE_INSENSITIVE_ORDER);

		Map userAttributes;
		String [] userAttributeNames;
		Long zoneId;
		boolean sync;
		boolean create;
		boolean delete;
		boolean deleteWorkspace;
		
		long createSyncSize;
		long modifySyncSize;
		
		private LdapSyncResults	m_ldapSyncResults;	// Store the results of the sync here.

		private LdapConnectionConfig m_ldapConfig;
		
		private LdapSyncMode m_syncMode;

		public UserCoordinator(
			Binder zone,
			boolean sync,
			boolean create,
			boolean delete,
			boolean deleteWorkspace,
			LdapSyncMode syncMode,
			LdapSyncResults syncResults,
			boolean externalUserSync )
		{
			ObjectControls objCtrls;
			FilterControls filterCtrls;
			
			this.zoneId = zone.getId();
			this.sync = sync;
			this.create = create;
			this.delete = delete;
			this.deleteWorkspace = deleteWorkspace;
			m_ldapSyncResults = syncResults;
			m_syncMode = syncMode;
			
			createSyncSize = GetterUtil.getLong(getLdapProperty(zone.getName(), "create.flush.threshhold"), 100);
			modifySyncSize = GetterUtil.getLong(getLdapProperty(zone.getName(), "modify.flush.threshhold"), 100);

			//get list of users in this zone and not deleted
			// Get the list of users in this zone that are not deleted
			objCtrls = new ObjectControls( User.class, principalAttrs );
			filterCtrls = new FilterControls( ObjectKeys.FIELD_ENTITY_DELETED, Boolean.FALSE );
			List userList = coreDao.loadObjects( objCtrls, filterCtrls, zoneId ); 

			// userList is a list of arrays where each array holds the values of some
			// of the user's Teaming attributes.  Create a map where the key is the
			// user's name and the value is the array of Teaming attributes.
			for (int i=0; i<userList.size(); ++i) {
				String ldapGuid;
				Object[] attributeValues = (Object [])userList.get(i);
				
				String ssName = (String)attributeValues[PRINCIPAL_NAME];
				
				//map existing names to row
				ssUsers.put(ssName, attributeValues);
				
				//map existing DN to row
				ssDnUsers.put((String)attributeValues[PRINCIPAL_FOREIGN_NAME], attributeValues);
				
				// If this user has an ldap guid, add the user to the m_listOfUsersByLdapGuid
				ldapGuid = (String) attributeValues[PRINCIPAL_LDAP_GUID];
				if ( ldapGuid != null && ldapGuid.length() > 0 )
					m_listOfUsersByLdapGuid.put( ldapGuid, attributeValues );
				
				//initialize all users as not found unless they are a reserved user.
				if ( Validator.isNull((String)attributeValues[PRINCIPAL_INTERNALID]) )
				{
					boolean rowInternal = ((Boolean) attributeValues[PRINCIPAL_INTERNAL]);
					if (rowInternal != externalUserSync)
					{
						notInLdap.put((Long)attributeValues[PRINCIPAL_ID], ssName);
					}
				}
			}			
		}
		
		public void setAttributes(Map userAttributes)
		{
			this.userAttributes = userAttributes;
			this.userAttributeNames = 	(String[])(userAttributes.keySet().toArray(sample));
		}

		public boolean isDuplicate(String dn)
		{
			return dnUsers.containsKey(dn);
		}

		/**
		 * ?
		 * 
		 * @param dn
		 * @param ssName
		 * @param lAttrs
		 * @param ldapGuidAttribute
		 * @param createAsExternal
		 * 
		 * @throws NamingException
		 */
		public void record(
			String dn,
			String ssName,
			Attributes lAttrs,
			String ldapGuidAttribute,
			String domainName,
            String netbiosName,
			HomeDirInfo homeDirInfo,
			boolean createAsExternal ) throws NamingException
		{
			boolean foundLdapGuid = false;
			Object[] row = null; 
			Object[] row2 = null;
			String ldapGuid = null;
			String typelessDN = null;
			boolean foundLocalUser = false;
			
			if (logger.isDebugEnabled())
				logger.debug("\t\tRecording user: '" + dn + "'");

			// Add the necessary containers for this user.
			m_containerCoordinator.record( dn, createAsExternal );
			
			// Get the typeless dn
			typelessDN = getTypelessDN( dn );
			
			// Do we have the name of the ldap attribute that holds the guid?
			if ( ldapGuidAttribute != null && ldapGuidAttribute.length() > 0 )
			{
				// Yes
				// Get the ldap guid that was read from the ldap directory for this user.
				ldapGuid = getLdapGuid( lAttrs, ldapGuidAttribute );
				
				// Does the ldap user have a guid?
				if ( ldapGuid != null && ldapGuid.length() > 0 )
				{
					// Yes
					// Is there a user in Teaming that has this ldap guid?
					row = m_listOfUsersByLdapGuid.get( ldapGuid ); 
					if ( row != null )
					{
						// Yes
						foundLdapGuid = true;
						notInLdap.remove( row[PRINCIPAL_ID] );
					}
				}
			}
			
			// Did we find the given ldap user in Teaming by their ldap guid?
			if ( !foundLdapGuid )
			{
				// No
				// Are we using the ldap guid to identify users?
				if ( ldapGuidAttribute == null || ldapGuidAttribute.length() == 0 )
				{
					// No
					// Search for the ldap user in Teaming in other ways. 
					//use DN as 1st priority match.  This will catch changes in USER_ID_ATTRIBUTE
					row = ssDnUsers.get(dn); 
					if (row != null)
						notInLdap.remove(row[PRINCIPAL_ID]);
					
					row2 = (Object[])ssUsers.get(ssName);
					if (row2 != null)
					{
						String name;
						String foreignName;
						
						notInLdap.remove(row2[PRINCIPAL_ID]);
	
						// Did we find a local user?
						// A local user will have their name equal to their foreignName
						name = (String) row2[PRINCIPAL_NAME];
						foreignName = (String) row2[PRINCIPAL_FOREIGN_NAME];
						if ( name.equalsIgnoreCase( foreignName ) )
						{
							// We found a local user.  We don't want to sync the ldap user to this user.
							foundLocalUser = true;
							row = null;
							row2 = null;
						}
					}
				}
			}
			
			// Does this ldap user already exist in Teaming?
			if ( foundLdapGuid || (row != null || row2 != null) )
			{
				// Yes
				// Does the ldap configuration say to sync users?
				if ( sync )
				{
					Map userMods = new HashMap();
					
					// Yes
					// Map the attributes read from the ldap directory to Teaming attributes.
					getUpdates( userAttributeNames, userAttributes, lAttrs, userMods, ldapGuidAttribute );

					// Make sure the user's name gets updated.
					userMods.put( ObjectKeys.FIELD_PRINCIPAL_NAME, ssName );
					
					// Update the domain name
					userMods.put( ObjectKeys.FIELD_PRINCIPAL_DOMAIN_NAME, domainName );
                                        
                                        // Update the netbios name
					userMods.put( ObjectKeys.FIELD_PRINCIPAL_NETBIOS_NAME, netbiosName );

					// Update the typelessDN field
					userMods.put( ObjectKeys.FIELD_PRINCIPAL_TYPELESS_DN, typelessDN );

					// Did we find the ldap user in Teaming by their ldap guid?
					if ( foundLdapGuid )
					{
						// Yes
						// Make sure the dn stored in Teaming is updated for this user.
						userMods.put( ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, dn );
						row[PRINCIPAL_FOREIGN_NAME] = dn;
					}
					else
					{
						// No, we found the ldap user in Teaming by their dn or their name.
						
						//remove this incase a mapping exists that is different than the uid attribute
						if (row != null && row2 == null) {
							if (!foundNames.containsKey(ssName)) { //if haven't just added it
								if (logger.isDebugEnabled())
									logger.debug("id changed: " + row[PRINCIPAL_NAME] + "->" + ssName);
								
								userMods.put(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, dn);
							} //otherwise update the other fields, just leave old name
						} else if (row == null && row2 != null) {
							//name exists, DN will be updated.  Either moved or we are going to end up with a conflict
							if (logger.isDebugEnabled())
								logger.debug("dn changed: " + row2[PRINCIPAL_FOREIGN_NAME] + "->" + dn);
							
							userMods.put(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, dn);
							row = row2;
							row[PRINCIPAL_FOREIGN_NAME] = dn;
						} else if (row != row2) {
							//have 2 rows that want the same loginName
							//this could only happen if the user_id_attribute has changed and the new name is already taken
							logger.error(NLT.get( "errorcode.ldap.found2VibeEntries", new Object[] {ssName, dn, row[PRINCIPAL_NAME], row[PRINCIPAL_FOREIGN_NAME], row2[PRINCIPAL_NAME], row2[PRINCIPAL_FOREIGN_NAME]}));
							//but apply updates to row anyway, just leave loginName unchanged
						}
					}
					
					//otherwise equal and all is well
					ldap_existing.put((Long)row[PRINCIPAL_ID], userMods);				

					// Create a HomeDirInfo object for this user in case we need to create a "home dir"
					// net folder or update their existing one.
					if ( Utils.checkIfFilr() )
					{
						ldap_existing_homeDirInfo.put( (Long)row[PRINCIPAL_ID], homeDirInfo );
					}
				}
				
				//setup distinquished name for group sync
				dnUsers.put(dn, row);
			}
			else if ( foundLocalUser )
			{
				// The ldap user matched up with an account in teaming that is a local account.
				// Don't sync the ldap user with the local account.
				logger.error( NLT.get( "errorcode.ldap.foundLocalAccount", new Object[] {ssName, dn, ssName} ) );
				return;
			}
			else if (foundNames.containsKey(ssName))
			{
				//name just created - skip duplicate
				logger.error(NLT.get("errorcode.ldap.duplicate", new Object[] {ssName, dn}));
				return;
			} else {
				if (create) {
					String	timeZone;
					String localeId;
					Map userMods = new HashMap();

					getUpdates( userAttributeNames, userAttributes, lAttrs, userMods, ldapGuidAttribute );
					
					userMods.put(ObjectKeys.FIELD_PRINCIPAL_NAME, ssName);
					userMods.put(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, dn);
					userMods.put( ObjectKeys.FIELD_PRINCIPAL_TYPELESS_DN, typelessDN );
					userMods.put(ObjectKeys.FIELD_ZONE, zoneId);
					userMods.put( ObjectKeys.FIELD_PRINCIPAL_DOMAIN_NAME, domainName );
                                        userMods.put( ObjectKeys.FIELD_PRINCIPAL_NETBIOS_NAME, netbiosName );

					
					// Get the default time zone.
					timeZone = getDefaultTimeZone();
					if ( timeZone != null && timeZone.length() > 0 )
					{
						userMods.put( ObjectKeys.FIELD_USER_TIMEZONE, timeZone );
					}
					
					// Get the default locale.
					localeId = getDefaultLocaleId();
					if ( localeId != null && localeId.length() > 0 )
					{
						userMods.put( ObjectKeys.FIELD_USER_LOCALE, localeId );
					}
					
					ldap_new.put(ssName, userMods); 
					dnUsers.put(dn, new Object[]{ssName, null, Boolean.FALSE, Boolean.valueOf( createAsExternal ), null, dn, ldapGuid});
					
					// Create a HomeDirInfo object for this new user
					if ( Utils.checkIfFilr() )
					{
						ldap_new_homeDirInfo.put( ssName.toLowerCase(), homeDirInfo );
					}
				}
			}
			
			//keep track of users we have processed from ldap
			foundNames.put(ssName, ssName);
			
			//do updates after every 100 users
			if (!ldap_existing.isEmpty() && (ldap_existing.size()%modifySyncSize == 0)) {
				//doLog("Updating users:", ldap_existing);
				PartialLdapSyncResults	syncResults	= null;
				
				// Do we have a place to store the list of modified users?
				if ( m_ldapSyncResults != null )
				{
					// Yes
					syncResults = m_ldapSyncResults.getModifiedUsers();
				}
				updateUsers( zoneId, ldap_existing, ldap_existing_homeDirInfo, m_syncMode, syncResults );
				ldap_existing.clear();
				ldap_existing_homeDirInfo.clear();
			}
			
			//do creates after every 100 users
			if (!ldap_new.isEmpty() && (ldap_new.size()%createSyncSize == 0)) {				
				doLog("Creating users:", ldap_new);
				PartialLdapSyncResults	syncResults	= null;
				
				// Do we have a place to store the list of added users?
				if ( m_ldapSyncResults != null )
				{
					syncResults = m_ldapSyncResults.getAddedUsers();
				}
				List results = createUsers( zoneId, ldap_new, ldap_new_homeDirInfo, m_syncMode, syncResults, createAsExternal );
				ldap_new.clear();
				ldap_new_homeDirInfo.clear();
				
				// fill in mapping from distinquished name to id
				for (int i=0; i<results.size(); ++i) {
					User user = (User)results.get(i);
					row = (Object[])dnUsers.get(user.getForeignName());
					row[PRINCIPAL_ID] = user.getId();
					row[PRINCIPAL_LDAP_GUID] = user.getLdapGuid();
				}
			}
		}
		
		/**
		 * 
		 */
		public void setLdapConfig( LdapConnectionConfig config )
		{
			m_ldapConfig = config;
		}
		
		public Map wrapUp( boolean doCleanup, boolean createAsExternal )
		{
			if (!ldap_existing.isEmpty()) {
				//doLog("Updating users:", ldap_existing);
				PartialLdapSyncResults	syncResults	= null;
				
				// Do we have a place to store the list of modified users?
				if ( m_ldapSyncResults != null )
				{
					// Yes
					syncResults = m_ldapSyncResults.getModifiedUsers();
				}
				updateUsers( zoneId, ldap_existing, ldap_existing_homeDirInfo, m_syncMode, syncResults );
			}
			
			if (!ldap_new.isEmpty()) {
				doLog("Creating users:", ldap_new);
				PartialLdapSyncResults	syncResults = null;
				
				// Do we have a place to store the list of added users?
				if ( m_ldapSyncResults != null )
				{
					syncResults = m_ldapSyncResults.getAddedUsers();
				}
				List results = createUsers( zoneId, ldap_new, ldap_new_homeDirInfo, m_syncMode, syncResults, createAsExternal );
				for (int i=0; i<results.size(); ++i) {
					User user = (User)results.get(i);
					Object[] row = (Object[])dnUsers.get(user.getForeignName());
					row[PRINCIPAL_ID] = user.getId();
					row[PRINCIPAL_LDAP_GUID] = user.getLdapGuid();
				}
			}
			
			// Do the work of disabling or deleting users that were not found in ldap.
			// We will only disable or delete users that were sync'd from ldap but are no
			// longer found in the ldap directory.
			// We will not touch users that were created by the admin or external users.
			if ( doCleanup )
			{
		    	Map users = new HashMap();

				for ( Map.Entry<Long, String>me:notInLdap.entrySet() )
				{
					Long id = me.getKey();
					String name = me.getValue();
					String foreignName;
					String ldapGuid;
					
					Object row[] = (Object[])ssUsers.get(name);
					ldapGuid = (String) row[PRINCIPAL_LDAP_GUID];
					foreignName = (String) row[PRINCIPAL_FOREIGN_NAME];
					
					// Was this user provisioned from ldap?
					if ( (ldapGuid != null && ldapGuid.length() > 0 ) || !name.equalsIgnoreCase( foreignName ) )
					{	
						Map updates = new HashMap();

						// Yes
						// If we disable the user we want to set the foreignName equal to the name
				    	updates.put(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, name);
			    		users.put(id, updates);
			     	}
				}
				
				// Do we have any users we need to disable or delete?
				if ( !users.isEmpty() )
				{
					// Yes
					// Should we delete the users that are no longer found in ldap?
					if ( delete )
					{
						Set userIds;
						Iterator iter;
						PartialLdapSyncResults	syncResults	= null;
						
						// Yes
						logger.info( "About to delete the following users:" );
						userIds = users.keySet();
						for ( iter = userIds.iterator(); iter.hasNext(); )
						{
							String userName;
							Long userIdL;
							HashMap values;
							
							// Get the id of the user.
							userIdL = (Long) iter.next();
							
							// Get the name of the user.
							values = (HashMap) users.get( userIdL );
							userName = (String) values.get(  ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME );
							if ( userName != null )
								logger.info( "\tUser: " + userName );
						}

						// Do we have a place to store the sync results?
						if ( m_ldapSyncResults != null )
						{
							// Yes
							syncResults = m_ldapSyncResults.getDeletedUsers();
						}

						// Delete the users no longer found in ldap that once existed in ldap.
						deletePrincipals(zoneId, users.keySet(), deleteWorkspace, m_syncMode, syncResults );
					}
					else
					{
						PartialLdapSyncResults	disabledUsersSyncResults = null;
						
						// Disable the users.
						// Do we have a place to store the sync results?
						if ( m_ldapSyncResults != null )
						{
							// Yes, get the list to store the disabled users.
							disabledUsersSyncResults = m_ldapSyncResults.getDisabledUsers();
						}
						
						updateUsers( zoneId, users, null, m_syncMode, null );
						
						// Disable the users no longer found in ldap that once existed in ldap.
						disableUsers( users, m_syncMode, disabledUsersSyncResults );
					}
				}
			}
			
			return dnUsers;
		}
		
	}// end UserCoordinator
	
	/**
	 * 
	 */
	private boolean hasMore( NamingEnumeration namingEnumeration )
	{
		boolean hasMore = false;

		if ( namingEnumeration == null )
			return false;
		
		try
		{
			// NamingEnumeration.hasMore() will throw an exception if needed only after all valid
			// objects have been returned as a result of walking through the enumeration.
			hasMore = namingEnumeration.hasMore();
		}
		catch( Exception ex )
		{
			logger.error( "namingEnumeration.hasMore() threw exception: ", ex );
		}
	
		return hasMore;
	}

	/**
	 * 
	 */
	private byte[] parseControls( Control[] controls ) throws NamingException
	{
		byte[] cookie = null;

		if ( controls != null )
		{
			for (int i = 0; i < controls.length; i++)
			{
				if ( controls[i] instanceof PagedResultsResponseControl )
				{
					PagedResultsResponseControl prrc = (PagedResultsResponseControl) controls[i];

					cookie = prrc.getCookie();
				}
			}
		}

		return (cookie == null) ? new byte[0] : cookie;
	}
	
	/**
	 * Parse the given unc path into a server, volume, path 
	 */
	private HomeDirInfo parseUncPath( String uncPath, boolean logErrors )
	{
		Matcher matcher;
		HomeDirInfo homeDirInfo = null;
		
		if ( uncPath == null )
			return null;
		
	    matcher = m_pattern_uncPath.matcher( uncPath );
	    if ( matcher.find() && matcher.groupCount() == 2 )
	    {
	    	String server;
	    	String share;
			String path = null;
			
    		server = matcher.group( 1 );
    		share = matcher.replaceFirst( "" );
    		
    		if ( share != null )
    		{
				int slashIndex;

	    		// Does the share have a '\' in it.
				slashIndex = share.indexOf( '\\' );
	    		if ( slashIndex > 0 )
	    		{
	    			// Yes
	    			path = share.substring( slashIndex+1 );
	    			share = share.substring( 0, slashIndex );
	    		}
    		}

    		if ( path != null && path.length() > 0 )
    		{
    			if ( path.charAt( 0 ) == '\\' )
    				path = path.substring( 1 );
    		}

    		// There may be a case that the unc is \\server\share with no path.
    		// In that case set path to \
    		if ( path == null || path.length() == 0 )
    			path = "\\";

    		logger.debug( "\t\t\tserver: '" + server + "' volume: '" + share + "' path: '" + path + "'" );
    		
			if ( server != null && server.length() > 0 && share != null && share.length() > 0 && 
				 path != null && path.length() > 0 )
			{
				homeDirInfo = new HomeDirInfo();
				homeDirInfo.setServerAddr( server );
				homeDirInfo.setVolume( share );
				homeDirInfo.setPath( path );
			}
		}
	    else
	    {
	    	if ( logErrors || logger.isDebugEnabled() )
	    		logger.error( "\t\t\tCould not parse the home directory unc: " + uncPath );
	    }
	    
	    return homeDirInfo;
	}

	private boolean testPaging()
	{
	    Hashtable<String, Object> env = new Hashtable<String, Object>(11);
	    env.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

	    /* Specify host and port to use for directory service */
//	    env.put( Context.PROVIDER_URL, "ldap://164.99.119.34:389");
	    env.put( Context.PROVIDER_URL, "ldap://151.155.137.65:389" );
//		env.put( Context.INITIAL_CONTEXT_FACTORY, getLdapProperty(zone.getName(), Context.INITIAL_CONTEXT_FACTORY ) );
		env.put( Context.SECURITY_PRINCIPAL, "cn=admin,o=novell" );
//		env.put( Context.SECURITY_PRINCIPAL, "cn=admin,o=novell,l=blr,s=kar,c=in" );
		env.put( Context.SECURITY_CREDENTIALS, "novell" );		
//		env.put( Context.SECURITY_AUTHENTICATION, getLdapProperty(zone.getName(), Context.SECURITY_AUTHENTICATION));
		env.put( Context.REFERRAL, "follow" );

	    try
	    {
	    	LdapContext ctx = new InitialLdapContext(env, null);

			// Activate paged results
			int pageSize = 10; 
			int times = 1;
			byte[] cookie = null;

			ctx.setRequestControls(new Control[]{ new PagedResultsControl(pageSize, Control.CRITICAL) });
			int total;

			do
			{
			    /* perform the search */
				logger.info( "about to call ctx.search(): " + times );
				++times;
				
	            NamingEnumeration results = ctx.search(
	            									"ou=1000-users,ou=Users,o=novell",
	            									//"ou=5k,o=Test",
	            									"(|(objectClass=Person)(objectClass=orgPerson)(objectClass=inetOrgPerson))",
	            									new SearchControls() );
	
	            /* for each entry print out name + all attrs and values */
		        while (results != null && results.hasMore())
		        {
		        	SearchResult entry = (SearchResult) results.next();
		        	logger.info( entry.getName() );
			    }
	
			    // Examine the paged results control response
			    Control[] controls = ctx.getResponseControls();
			    if (controls != null)
			    {
					for (int i = 0; i < controls.length; i++)
					{
					    if (controls[i] instanceof PagedResultsResponseControl)
					    {
							PagedResultsResponseControl prrc = (PagedResultsResponseControl)controls[i];
							total = prrc.getResultSize();
							if (total != 0)
							{
							    logger.info("***************** END-OF-PAGE " + "(total : " + total + ") *****************\n");
							}
							else
							{
							    logger.info("***************** END-OF-PAGE " + "(total: unknown) ***************\n" );
							}
							cookie = prrc.getCookie();
							
							if ( cookie == null )
							{
								logger.info( "*************** cookie is null *************" );
								cookie = new byte[0];
							}
					    }
					}
			    }
			    else
			    {
			    	logger.info( "No controls were sent from the server" );
			    }

			    // Re-activate paged results
		        ctx.setRequestControls( new Control[]{ new PagedResultsControl(pageSize, cookie, Control.CRITICAL) });
	
			} while ( cookie != null && cookie.length != 0 );

			ctx.close();

	    }
	    catch (NamingException e)
	    {
	        logger.info( "PagedSearch failed.", e );
	    }
	    catch (IOException ie)
	    {
	        logger.info( "PagedSearch failed.", ie );
	    }
	    catch ( Exception e )
	    {
	    	logger.info( "PagedSearch failed.", e );
	    }
	    
	    return true;
	}
	
	protected void syncUsers( Binder zone, LdapContext ctx, LdapConnectionConfig config, UserCoordinator userCoordinator, boolean createAsExternal ) throws NamingException
	{
		String ssName;
		String[] attributesToRead;
		String ldapGuidAttribute;
                ADLdapObject domainInfo;
		String domainName = null, netbiosName = null;
		LdapDirType dirType;
		LdapContext ldapContextForReadingHomeDirInfo=null;
		int pageSize = 1500;

		logger.info( "Starting to sync users, syncUsers()" );

		pageSize = SPropsUtil.getInt( "ldap.sync.users.page.size", 1500 );
		logger.info( "    User page size: " + pageSize );

		// If "sync user profiles" is off and "register ldap user profiles automatically" is off
		// then there is nothing to do here.
		if ( userCoordinator.sync == false && userCoordinator.create == false )
		{
			logger.info( "In syncUsers(), 'Synchronize User Profiles' and 'Register LDAP User Profiles Automatically' are both off.  Nothing to do." );
			return;
		}

		// Get the mapping of ldap attributes to Teaming field names
		Map attributeMappings = config.getMappings();

		userCoordinator.setAttributes(attributeMappings);
		userCoordinator.setLdapConfig( config );

		// Get a list of the names of the attributes we want to read from the ldap directory for each user.
		attributesToRead = getAttributeNamesToRead( userCoordinator.userAttributeNames, config );

		String userIdAttribute = config.getUserIdAttribute();

		// Get the ldap attribute name that we will use for a guid.
		ldapGuidAttribute = config.getLdapGuidAttribute();
		
		// Are we working with AD?
		dirType = getLdapDirType( ldapGuidAttribute );
		if ( dirType == LdapDirType.AD )
		{
			// Yes
                    domainInfo = getDomainInfo(config);
                    if (domainInfo != null) {
                        domainName = domainInfo.getDomainName();
                        netbiosName = domainInfo.getNetbiosName();
                    }
		}
		
		if ( Utils.checkIfFilr() )
		{
			try
			{
				// Create an ldap context that we can use to read the home dir info
				// from the user object.  We can't use ctx because MS Windows
				// has a bug where we can't read additional attributes if we are doing paging.
				ldapContextForReadingHomeDirInfo = getUserContext( zone.getId(), config );
			}
			catch ( Exception ex )
	  		{
				logger.error( "In syncUsers(), the call to getUserContext() threw an exception.  Home directory information will NOT be read.", ex );
				
				// It is ok to continue.  We won't try to read the home dir info
	  		}
		}

		for(LdapConnectionConfig.SearchInfo searchInfo : config.getUserSearches()) {
			String filter;

			filter = searchInfo.getFilterWithoutCRLF();
			if ( Validator.isNotNull( filter ) )
			{
				byte[] cookie = null;

				int scope = (searchInfo.isSearchSubtree()?SearchControls.SUBTREE_SCOPE:SearchControls.ONELEVEL_SCOPE);
				SearchControls sch = new SearchControls(
													scope,
													0,
													0,
													attributesToRead,
													false,
													false);
	
				logger.info( "\tSearching for users in base dn: " + searchInfo.getBaseDn() );
				
				// Request the paged results control
				try
				{
					Control[] ctls = new Control[]{ new PagedResultsControl( pageSize, true ) };
					ctx.setRequestControls( ctls );
				}
				catch ( IOException ex )
				{
					logger.error( "Call to new PagedResultsControl() threw an exception: ", ex );
				}

				do
				{
					NamingEnumeration results;
					int numUsersProcessed = 0;
					long startTime = 0;
					long totalTimeTakenByUserRecord = 0;

					if ( m_showTiming )
					{
						Date now;

						now = new Date();
						startTime = now.getTime();
					}
					
					logger.debug( "\t\tAbout to call ctx.search()" );
					
					// Issue an ldap search for users in the given base dn.
					results = ctx.search( searchInfo.getBaseDn(), filter, sch );
					
					// loop through the results in each page
					while ( hasMore( results ) )
					{
						String	userName;
						String	fixedUpUserName;
						Attributes lAttrs = null;
						SearchResult sr;
						long startRecord = 0;
						
						++numUsersProcessed;
						
						sr = (SearchResult)results.next();
						userName = sr.getNameInNamespace();
						
						// Fixup the  by replacing all "/" with "\/".
						fixedUpUserName = fixupName( userName );
						fixedUpUserName = fixedUpUserName.trim();

						// Read the necessary attributes for this user from the ldap directory.
						lAttrs = sr.getAttributes();
						
						Attribute id=null;
						id = lAttrs.get(userIdAttribute);
						if ( id == null )
						{
							logger.error( "The attribute: " + userIdAttribute + " does not exist in the ldap directory.  The value of this attribute is used to for the account name." );
							continue;
						}

						//map ldap id to sitescapeName
						ssName = null;
						Object attrValue;
						attrValue = id.get();
						if ( attrValue != null )
						{
							if ( attrValue instanceof String )
							{
								ssName = idToName( (String) attrValue );
							}
							else if ( attrValue instanceof byte[] )
							{
								logger.info( "------> " + userIdAttribute + " is of type byte[]" );
								try
								{
									String name;
									
									name = new String( (byte[]) attrValue, "UTF-8" );
									ssName = idToName( name );
								}
								catch ( Exception ex )
								{
									logger.error( "Error converting user id from byte[] to String", ex );
								}
							}
						}
						if ( ssName == null )
						{
							logger.error( "Unable to read a value for: " + userIdAttribute + " from the ldap directory.  The value of this attribute is used to for the account name." );
							continue;
						}

						// Is the name of this user a name that is used for a Teaming system user account?
						// Currently there are 5 system user accounts named, "admin", "guest", "_postingAgent",
						// "_jobProcessingAgent", "_synchronizationAgent", and "_fileSyncAgent.
						if ( BuiltInUsersHelper.isSystemUserAccount( ssName ) )
						{
							// Yes, skip this user.  System user accounts cannot be sync'd from ldap.
							continue;
						}
						
						String relativeName = userName.trim();
						String dn;
						if (sr.isRelative() && !"".equals(ctx.getNameInNamespace())) {
							dn = relativeName + "," + ctx.getNameInNamespace().trim();
						} else {
							dn = relativeName;
						}
						
						//~JW:  How do we want to determine if a user is a duplicate?
						if (userCoordinator.isDuplicate(dn)) {
							logger.error( NLT.get( "errorcode.ldap.userAlreadyProcessed", new Object[] {ssName, dn} ) );
							continue;
						}
						
						HomeDirInfo homeDirInfo = null;
						
						if ( Utils.checkIfFilr() && ldapContextForReadingHomeDirInfo != null && ( ! createAsExternal ) )
						{
							homeDirInfo = getHomeDirInfoFromConfig(
																ldapContextForReadingHomeDirInfo,
																dirType,
																dn,
																searchInfo.getHomeDirConfig(),
																true );
						}

						if ( m_showTiming )
						{
							Date now;
							
							now = new Date();
							startRecord = now.getTime();
						}
						
						userCoordinator.record(
											dn,
											ssName,
											lAttrs,
											ldapGuidAttribute,
											domainName,
                                            netbiosName,
											homeDirInfo,
											createAsExternal );
						
						if ( m_showTiming )
						{
							Date now;
							long elapsedTime;
							
							now = new Date();
							elapsedTime = now.getTime() - startRecord;
							totalTimeTakenByUserRecord += elapsedTime;
						}
						
						// clear cache to prevent thrashing resulted from prolonged use of a single session
						if ( (numUsersProcessed % 100) == 0 )
							getCoreDao().clear();
						
					}// end while()
	     
					// examine the response controls
					cookie = parseControls( ctx.getResponseControls() );

					try
					{
						// pass the cookie back to the server for the next page
						PagedResultsControl prCtrl;
						
						prCtrl = new PagedResultsControl( pageSize, cookie, Control.CRITICAL );
						ctx.setRequestControls( new Control[]{ prCtrl } );
					}
					catch ( IOException ex )
					{
						cookie = null;
						logger.error( "Call to PagedResultsControl() threw an exception: ", ex );
					}
					
					if ( m_showTiming )
					{
						long elapsedTimeInSeconds;
						long minutes;
						long seconds;
						long milliSeconds;
						Date now;
						
						now = new Date();

						milliSeconds = now.getTime() - startTime - totalTimeTakenByUserRecord;
						elapsedTimeInSeconds = milliSeconds / 1000;
						minutes = elapsedTimeInSeconds / 60;
						seconds = elapsedTimeInSeconds - (minutes * 60);
						milliSeconds = milliSeconds - (elapsedTimeInSeconds * 1000);
						logger.info( "ldap sync timing: ======> Time to read last " + numUsersProcessed + " users from ldap: " + minutes + " minutes " + seconds + " seconds " + milliSeconds + " milliseconds " );
					}
					
					// clear cache to prevent thrashing resulted from prolonged use of a single session
        			getCoreDao().clear();

				} while ( (cookie != null) && (cookie.length != 0) );
			}
			else
				logger.warn( "In syncUsers(), a user filter was not specified.  This can result in existing users being disabled or deleted." );
		}

		try
		{
			if ( ldapContextForReadingHomeDirInfo != null )
			{
				ldapContextForReadingHomeDirInfo.close();
				ldapContextForReadingHomeDirInfo = null;
			}
		}
		catch ( Exception ex )
		{
			logger.error( "In syncUsers(), closing ldapContextForReadingHomeDirInfo.close() threw an exception.", ex );
		}

		logger.info( "Finished syncUsers()" );
	}

	class GroupCoordinator
	{
		// m_listOfGroupsByLdapGuid is a list of all groups returned from the call to loadObjects(...)
		// Key: ldap guid
		// Value: array of values read from db.
		Map<String, Object[]> m_listOfGroupsByLdapGuid = new TreeMap(String.CASE_INSENSITIVE_ORDER);

		// ssGroups is a list of all groups returned from the call to loadObjects(...)
		// Key: group name
		// Value: array of values read from db.
		Map ssGroups = new TreeMap(String.CASE_INSENSITIVE_ORDER);

		// m_groupsNotInLdap is initially a list of all groups returned from the call to loadObjects(...)
		// that were provisioned from ldap.
		// When a group is read from the ldap directory it removed from this list.
		// Key: Teaming id
		// Value: group name
		Map<Long, String> m_groupsNotInLdap = new TreeMap();
		
		// dnGroups is a list of all groups returned from the call to loadObjects(...) plus
		// any groups that we created during the sync process.
		// Key: group dn
		// Value: array of values read from db.
		Map dnGroups = new TreeMap(String.CASE_INSENSITIVE_ORDER);

		Map groupAttributes;
		String [] groupAttributeNames;
		
		Long zoneId;
		Map dnUsers;
		boolean sync;
		boolean create;
		boolean delete;
	
		long createSyncSize;
		long modifySyncSize;
		
		private LdapSyncResults	m_ldapSyncResults;	// Store the results of the sync here.

		private LdapSyncMode m_syncMode;

		public GroupCoordinator(
			Binder zone,
			Map dnUsers,
			boolean sync,
			boolean create,
			boolean delete,
			LdapSyncMode syncMode,
			LdapSyncResults syncResults,
			boolean externalUserSync )
		{
			ObjectControls objControls;
			FilterControls filterControls;
			
			this.zoneId = zone.getId();
			this.dnUsers = dnUsers;
			this.sync = sync;
			this.create = create;
			this.delete = delete;
			
			m_syncMode = syncMode;
			m_ldapSyncResults = syncResults;	// Store the results of the sync here.
			
			createSyncSize = GetterUtil.getLong(getLdapProperty(zone.getName(), "create.flush.threshhold"), 100);
			modifySyncSize = GetterUtil.getLong(getLdapProperty(zone.getName(), "modify.flush.threshhold"), 100);

			// get list of existing groups in Teaming.
			objControls = new ObjectControls( Group.class, groupAttrs );
			filterControls = new FilterControls( ObjectKeys.FIELD_ENTITY_DELETED, Boolean.FALSE );
			List attrs = coreDao.loadObjects( objControls, filterControls, zone.getId() );
			
			//convert list of objects to a Map of loginNames 
			for (int i=0; i<attrs.size(); ++i)
			{
				String ldapGuid;
				String ssName;
				Object[] row;
				Object value;
				
				row = (Object [])attrs.get(i);
				
				// Is this an "ldap container" group?
				value = row[GROUP_LDAP_CONTAINER];
				if ( value != null )
				{
					// Yes, skip it.
					continue;
				}
				
				ssName = (String)row[PRINCIPAL_NAME];
				ssGroups.put(ssName, row);
				
				if (!Validator.isNull((String)row[PRINCIPAL_FOREIGN_NAME]))
				{
					dnGroups.put(row[PRINCIPAL_FOREIGN_NAME], row);
				}
				
				// If this group has an ldap guid, add the group to the m_listOfGroupsByLdapGuid
				ldapGuid = (String) row[PRINCIPAL_LDAP_GUID];
				if ( ldapGuid != null && ldapGuid.length() > 0 )
					m_listOfGroupsByLdapGuid.put( ldapGuid, row );
				
				//initialize all groups as not found unless they are a reserved group
				if ( Validator.isNull((String)row[PRINCIPAL_INTERNALID]) )
				{
					boolean rowInternal = ((Boolean) row[PRINCIPAL_INTERNAL]);
					if (rowInternal != externalUserSync)
					{
						m_groupsNotInLdap.put((Long)row[PRINCIPAL_ID], ssName);
					}
				}
			}
		}
		
		/**
		 * 
		 */
		public LdapSyncResults getLdapSyncResults()
		{
			return m_ldapSyncResults;
		}
		
		public void setAttributes(Map groupAttributes)
		{
			this.groupAttributes = groupAttributes;
			this.groupAttributeNames = 	(String[])(groupAttributes.keySet().toArray(sample));
		}
		
		/**
		 * Return the list of attribute names we deal with for a group.
		 */
		public String[] getAttributeNames()
		{
			return groupAttributeNames;
		}
		

		/**
		 * @param dn
		 * @param relativeName
		 * @param lAttrs
		 * @param ldapGuidAttribute
		 * @return
		 * @throws NamingException
		 */
		boolean record(
			String dn,
			String teamingName,
			Attributes lAttrs,
			String ldapGuidAttribute,
			String domainName,
            String netbiosName,
            boolean createAsExternal ) throws NamingException
		{
			boolean isSSGroup = false;
			boolean foundLdapGuid = false;
			String ldapGuid = null;
			String ssName;
			String typelessDN;
			Object[] row = null;
			
			if (logger.isDebugEnabled())
				logger.debug("\t\tRecording group: '" + dn + "'");
			
			// Add the necessary containers for this group.
			m_containerCoordinator.record( dn, createAsExternal );
			
			// Get the typeless dn
			typelessDN = getTypelessDN( dn );
			
			if ( domainName != null )
				domainName = domainName.toLowerCase();
                        
                        if (netbiosName != null)
                            netbiosName = netbiosName.toLowerCase();
			
			// Do we have the name of the ldap attribute that holds the guid?
			if ( ldapGuidAttribute != null && ldapGuidAttribute.length() > 0 )
			{
				// Yes
				// Get the ldap guid that was read from the ldap directory for this user.
				ldapGuid = getLdapGuid( lAttrs, ldapGuidAttribute );
				
				// Does the ldap group have a guid?
				if ( ldapGuid != null && ldapGuid.length() > 0 )
				{
					// Yes
					// Is there a group in Teaming that has this ldap guid?
					row = m_listOfGroupsByLdapGuid.get( ldapGuid ); 
					if ( row != null )
					{
						// Yes
						foundLdapGuid = true;
						m_groupsNotInLdap.remove( row[PRINCIPAL_ID] );
					}
				}
			}
			
			// Did we find the given ldap group in Teaming by their ldap guid?
			if ( !foundLdapGuid )
			{
				// No, search for the ldap group in Teaming by its dn 
				row = (Object [])dnGroups.get(dn);
			}
			
			// Does this ldap group already exist in Teaming?
			if ( foundLdapGuid == false && row == null )
			{
				// No, should we create the group?
				if (create)
				{
					// Yes
					// Is there already a group in Filr with this name?
					ssName = teamingName;
					row = (Object[])ssGroups.get(ssName);
					if ( row != null )
					{
						// Yes
						// Try to create a unique name for the group.  For example, ico-group(2)
						logger.debug( "group already exists: '" + ssName + "'" );
						ssName = getUniqueGroupName( ssName );
					}
					
					if ( ssName != null && ssName.length() > 0 )
					{
						Map userMods = new HashMap();
						
						//mapping may change the name and title
						getUpdates( groupAttributeNames, groupAttributes, lAttrs, userMods, ldapGuidAttribute );
						
						userMods.put(ObjectKeys.FIELD_PRINCIPAL_NAME,ssName);
						userMods.put(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, dn);
						userMods.put( ObjectKeys.FIELD_PRINCIPAL_TYPELESS_DN, typelessDN );
						userMods.put(ObjectKeys.FIELD_ZONE, zoneId);
						userMods.put( ObjectKeys.FIELD_PRINCIPAL_DOMAIN_NAME, domainName );
                                                userMods.put(ObjectKeys.FIELD_PRINCIPAL_NETBIOS_NAME, netbiosName);

						if (logger.isDebugEnabled())
							logger.debug("Creating group:" + ssName);
						
						Group group = createGroup( zoneId, ssName, userMods, createAsExternal ); 
						if ( group != null )
						{
							Object[] groupInfo;
							
							groupInfo = new Object[]{ssName, group.getId(), Boolean.FALSE, Boolean.valueOf( createAsExternal ), null, dn, ldapGuid};
							dnGroups.put(dn, groupInfo );
							
							if ( ssGroups.get( ssName ) == null )
								ssGroups.put( ssName, groupInfo );
							
							isSSGroup = true;
						}
					}
				}
			}
			else
			{
				ssName = teamingName;
				if ( sync )
				{
					Map userMods = new HashMap();
					String originalDn;
					boolean renameGroup = false;
					
					if (logger.isDebugEnabled())
						logger.debug("\t\tUpdating group:" + ssName);
					
					// Map the attributes read from the ldap directory to Teaming attributes.
					getUpdates( groupAttributeNames, groupAttributes, lAttrs, userMods, ldapGuidAttribute );

					// Has the dn of this group changed?
					originalDn = (String) row[PRINCIPAL_FOREIGN_NAME];
					if ( dn.equalsIgnoreCase( originalDn ) == false )
					{
						String nameInDb;
						
						// Yes
						// That means the group has either been moved or renamed.
						// Was the group name changed in the ldap directory?
						nameInDb = (String) row[PRINCIPAL_NAME];
						if ( teamingName.equalsIgnoreCase( nameInDb ) == false )
						{
							Object[] tmpRow;
							
							// Yes
							// Does the group's new name already exist in the db?
							tmpRow = (Object[])ssGroups.get( teamingName );
							if ( tmpRow != null && tmpRow != row )
							{
								// Yes
								// Try to create a unique name for the group.  For example, ico-group(2)
								logger.debug( "group name for a rename already exists: '" + nameInDb + "'" );
								ssName = getUniqueGroupName( teamingName );

								// Do we have a new name?
								if ( ssName != null && ssName.length() > 0 )
								{
									// Yes
									renameGroup = true;
								}
							}
							else
							{
								ssName = teamingName;
								renameGroup = true;
							}
						}
					}

					if ( renameGroup == true && ssName != null )
					{
						userMods.put( ObjectKeys.FIELD_PRINCIPAL_NAME, ssName );
						row[PRINCIPAL_NAME] = ssName;
					}
					else
					{
						// Remove the "name" attribute from the list of attributes to be written to the db.
						userMods.remove( ObjectKeys.FIELD_PRINCIPAL_NAME );
					}

					// Make sure the dn stored in Teaming is updated for this user.
					userMods.put( ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, dn );
					row[PRINCIPAL_FOREIGN_NAME] = dn;

					// Update the typelessDN field
					userMods.put( ObjectKeys.FIELD_PRINCIPAL_TYPELESS_DN, typelessDN );

					// Update the domain name
					userMods.put( ObjectKeys.FIELD_PRINCIPAL_DOMAIN_NAME, domainName );
                                        
                                        //Update the Netbios name
                                        userMods.put(ObjectKeys.FIELD_PRINCIPAL_NETBIOS_NAME, netbiosName);

					updateGroup( zoneId, (Long)row[PRINCIPAL_ID], userMods, m_syncMode, m_ldapSyncResults );
				} 
				
				//exists in ldap, remove from missing list
				m_groupsNotInLdap.remove(row[PRINCIPAL_ID]);
				isSSGroup = true;
			}
			
			return isSSGroup;
		}// end record()
		
		/**
		 * Try to compose a unique group name using the given group name as a base
		 */
		private String getUniqueGroupName( String originalGroupName )
		{
			String uniqueName = null;
			int i;
			
			// Yes
			// We don't want to try forever so we try 1,000 times
			uniqueName = null;
			for (i = 1; i <= 1000; ++i)
			{
				String tmpName;
				
				// Does this name exist?
				tmpName = originalGroupName + "(" + i + ")";
				if ( ssGroups.get( tmpName ) == null )
				{
					// No
					logger.debug( "Got unique group name: " + tmpName );
					uniqueName = tmpName;
					break;
				}
			}
			
			if ( uniqueName == null )
				logger.error( "Could not generate a unique name for the group: '" + originalGroupName + "'" );

			return uniqueName;
		}
		
	    /**
	     * Create groups.
	     * 
	     * @param zoneName
	     * @param groups - Map keyed by user id, value is map of attributes
	     * @param createAsExternal
	     * 
	     * @return
	     */
	    protected Group createGroup(Long zoneId, String ssName, Map groupData, boolean createAsExternal ) {
	    	logger.info( "--> Creating group: " + ssName );
	    	
	    	MapInputData groupMods = new MapInputData(StringCheckUtil.check(groupData));
			ProfileBinder pf = getProfileDao().getProfileBinder(zoneId);
			//get default definition to use
			Group temp = new Group(new IdentityInfo());
			getDefinitionModule().setDefaultEntryDefinition(temp);
			Definition groupDef = getDefinitionModule().getDefinition(temp.getEntryDefId());
			try {
				PartialLdapSyncResults	syncResults	= null;
				
				// Are we suppose to store the sync results someplace?
				if ( m_ldapSyncResults != null )
				{
					// Yes, get the ArrayList that holds the list of groups added to Teaming.
					syncResults = m_ldapSyncResults.getAddedGroups();
				}
				
		    	ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
	            	pf, ProfileCoreProcessor.PROCESSOR_KEY);
		    	
		    	if ( m_syncMode == LdapSyncMode.PERFORM_SYNC )
		    	{
			    	List newGroups = processor.syncNewEntries(pf, groupDef, Group.class, Arrays.asList(new MapInputData[] {groupMods}), null, syncResults, new IdentityInfo( ( ! createAsExternal ), true, false, false ) );
			    	IndexSynchronizationManager.applyChanges(); //apply now, syncNewEntries will commit

			    	return (Group) newGroups.get(0);
		    	}
		    	
		    	if ( m_syncMode == LdapSyncMode.PREVIEW_ONLY )
		    	{
		    		HashMap<String,Map> map;
		    		
		    		map = new HashMap<String,Map>();
		    		map.put( ssName, groupData );
					addPrincipalsToPreviewSyncResults( map, syncResults );
					
					return null;
				}
			} catch (Exception ex) {
				logError("Error adding group", ex);
				logger.error("'" + ssName + "':'" + groupData.get(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME) + "'");
			}
			return null;
	    }

		protected void syncMembership(Long groupId, Enumeration valEnum)
		throws NamingException
		{
			Object[] uRow;
			List membership = new ArrayList();
			PartialLdapSyncResults syncResults	= null;
			//build new membership
			while(valEnum.hasMoreElements()) {
				String mDn = ((String)valEnum.nextElement()).trim();
				uRow = (Object[])dnUsers.get(mDn);
				if (uRow == null) uRow = (Object[])dnGroups.get(mDn);
				if (uRow == null || uRow[PRINCIPAL_ID] == null) continue; //never got created
				membership.add(new Membership(groupId, (Long)uRow[PRINCIPAL_ID]));
			}

			// Do we have a place to store the list of modified groups?
			if ( m_ldapSyncResults != null )
			{
				// Yes
				syncResults = m_ldapSyncResults.getModifiedGroups();
			}

			//do inside a transaction
			updateMembership(groupId, membership, m_syncMode, syncResults );	
		}

		public void deleteObsoleteGroups()
		{
			// If delete is enabled, remove groups that were originally sync'd from ldap but
			// are no longer found in ldap.
			if ( delete && !m_groupsNotInLdap.isEmpty() )
			{
				PartialLdapSyncResults	syncResults	= null;
		    	Map groups;
				
		    	groups = new HashMap();

		    	// m_groupsNotInLdap contains local groups and groups that were originally sync'd
				// from ldap.  We only want to delete groups that were originally sync'd from
				// ldap and are no longer in ldap.
				
				// Create a list of groups that were originally sync'd from ldap but are no
				// longer in ldap.
				for ( Map.Entry<Long, String> me: m_groupsNotInLdap.entrySet() )
				{
					Long id;
					String name;
					String foreignName;
					String ldapGuid;
					Object row[];
					
					id = me.getKey();
					name = me.getValue();
					row = (Object[])ssGroups.get( name );
					ldapGuid = (String) row[PRINCIPAL_LDAP_GUID];
					foreignName = (String) row[PRINCIPAL_FOREIGN_NAME];
					
					// Was this group provisioned from ldap?
					if ( (ldapGuid != null && ldapGuid.length() > 0 ) || !name.equalsIgnoreCase( foreignName ) )
					{	
						// Yes
			    		groups.put( id, name );
			     	}
				}
				
				// Do we have a place to store the sync results?
				if ( m_ldapSyncResults != null )
				{
					// Yes
					syncResults = m_ldapSyncResults.getDeletedGroups();
				}

				if ( groups.isEmpty() == false )
					deletePrincipals( zoneId, groups.keySet(), false, m_syncMode, syncResults );
			}
			else if ( !delete && !m_groupsNotInLdap.isEmpty() )
			{
				if ( logger.isDebugEnabled() )
				{
					logger.debug( "Groups not found in ldap:" );
					for ( String name: m_groupsNotInLdap.values() )
					{
						logger.debug("\t'" + name + "'");
					}
				}
			}
		}
		
		public Object[] getGroup(String dn)
		{
			return (Object [])dnGroups.get(dn);
		}

	}// end GroupCoordinator

	protected void syncGroups(
		Binder zone,
		LdapContext ctx,
		LdapConnectionConfig config,
		GroupCoordinator groupCoordinator,
		boolean syncMembership,
		boolean createAsExternal ) throws NamingException
	{
		boolean workingWithAD = false;
		String ldapGuidAttribute;
		int pageSize = 500;
		ArrayList<ADGroup> listOfADGroupsToSyncMembership;
		
		// Get the name of the ldap attribute we will use to get a guid from the ldap directory.
		ldapGuidAttribute = config.getLdapGuidAttribute();
		
		if ( getLdapDirType( ldapGuidAttribute ) == LdapDirType.AD )
			workingWithAD = true;

		logger.info( "Starting to sync groups, syncGroups()" );

		// Create a list that is used to hold the list of groups from AD that we need to
		// sync their membership
		listOfADGroupsToSyncMembership = new ArrayList<ADGroup>();

		for(LdapConnectionConfig.SearchInfo searchInfo : config.getGroupSearches())
		{
			if ( Validator.isNotNull(searchInfo.getFilterWithoutCRLF()))
			{
				String[] attributesToRead;
				String[] attributeNames;
				List memberAttributes;
				int i;
				byte[] cookie = null;
				
				logger.info( "\tSearching for groups in base dn: " + searchInfo.getBaseDn() );
				
				listOfADGroupsToSyncMembership.clear();
				
				// Get the mapping of attributes for a group.
				Map groupAttributes = (Map) getZoneMap(zone.getName()).get(GROUP_ATTRIBUTES);
				groupCoordinator.setAttributes(groupAttributes);
				
				Set la = new HashSet(groupAttributes.keySet());
				if ( workingWithAD == false )
					la.addAll((List) getZoneMap(zone.getName()).get(MEMBER_ATTRIBUTES));
				
				// Create a String[] of all the attributes we need to read from the directory.
				{
					int len;
					int index;
					
					// Get the names of the attributes that may hold the group membership.
					memberAttributes = (List) getZoneMap(zone.getName()).get(MEMBER_ATTRIBUTES);

					// Get the names of the group attributes
					attributeNames = groupCoordinator.getAttributeNames();
					
					len = 3; // Make room for objectSid, sAMAccountName, and ldap guid attribute.
					if ( attributeNames != null )
						len += attributeNames.length;
					
					if ( workingWithAD == false && memberAttributes != null )
						len += memberAttributes.size();
					
					index = 0;
					attributesToRead = new String[len];
					for (i = 0; i < attributeNames.length; ++i)
					{
						attributesToRead[index] = attributeNames[i];
						++index;
					}
					
					if ( workingWithAD == false )
					{
						for (i = 0; i < memberAttributes.size(); ++i)
						{
							attributesToRead[index] = (String)memberAttributes.get( i );
							++index;
						}
					}
					
					attributesToRead[index] = ldapGuidAttribute;
					++index;
					
					// Is the ldap directory AD?
					if ( workingWithAD )
					{
						// Yes
						// Add "objectSid" to the list of ldap attributes to read.
						attributesToRead[index] = OBJECT_SID_ATTRIBUTE;
						++index;

						// Add "sAMAccountName" to the list of ldap attributes to read.
						attributesToRead[index] = SAM_ACCOUNT_NAME_ATTRIBUTE;
						++index;
					}
				}
				
				// Request the paged results control
				try
				{
					Control[] ctls = new Control[]{ new PagedResultsControl( pageSize, true ) };
					ctx.setRequestControls( ctls );
				}
				catch ( IOException ex )
				{
					logger.error( "In syncGroups(), call to new PagedResultsControl() threw an exception: ", ex );
				}
				
				int scope = (searchInfo.isSearchSubtree()?SearchControls.SUBTREE_SCOPE:SearchControls.ONELEVEL_SCOPE);
				SearchControls sch = new SearchControls(
													scope,
													0,
													0,
													attributesToRead,
													false,
													false);
	
				do
				{
					NamingEnumeration results;
					
					results = ctx.search( searchInfo.getBaseDn(), searchInfo.getFilterWithoutCRLF(), sch );

					while ( hasMore( results ) )
					{
						String groupName;
						String fixedUpGroupName;
						String teamingName;
                                                ADLdapObject domainInfo;
						String domainName = null, netbiosName = null;
                                                
						Attribute id;
						SearchResult sr;
						
						sr = (SearchResult) results.next();
						groupName = sr.getNameInNamespace();
						
						// Fixup the  by replacing all "/" with "\/".
						fixedUpGroupName = fixupName( groupName );
						fixedUpGroupName = fixedUpGroupName.trim();
						
						// Read the given attributes for this group from the directory.
						Attributes lAttrs = sr.getAttributes();
						
						String relativeName = groupName.trim();
						String dn;
						if ( sr.isRelative() && !"".equals(ctx.getNameInNamespace())) {
							if(!"".equals(relativeName)) {
								dn = relativeName + "," + ctx.getNameInNamespace().trim();
							} else {
								dn = ctx.getNameInNamespace().trim();
							}
						} else {
							dn = relativeName;
						}
						
						id = lAttrs.get( "cn" );
						if ( id != null )
						{
							teamingName = idToName((String)id.get());
						}
						else
							teamingName = dn;
						
						if ( teamingName == null )
							continue;

						if ( workingWithAD ) {
							domainInfo = getDomainInfo( config );
                                                        if (domainInfo != null) {
                                                            domainName = domainInfo.getDomainName();
                                                            netbiosName = domainInfo.getNetbiosName();   
                                                        }
                                                }
                                                else {
                                                        netbiosName = null;
							domainName = null;
                                                }
						//doing this one at a time is going to be slow for lots of groups
						//not sure why it was changed for v2
						if ( groupCoordinator.record( dn, teamingName, lAttrs, ldapGuidAttribute, domainName, netbiosName, createAsExternal ) && syncMembership )
						{ 
							//Get map indexed by id
							Object[] gRow = groupCoordinator.getGroup(dn);
							if (gRow == null) continue; //not created
							Long groupId = (Long)gRow[PRINCIPAL_ID];
							if (groupId == null) continue; // never got created
							
							if ( workingWithAD == false )
							{
								Attribute att = null;
								for (i=0; i<memberAttributes.size(); i++) {
									att = lAttrs.get((String)memberAttributes.get(i));
									if(att != null && att.get() != null && att.size() != 0) {
										break;
									}
									att = null;
								}
								Enumeration members = null;
								if(att != null) {
									members = att.getAll();
								}
								
								if(members != null) {
									groupCoordinator.syncMembership(groupId, members);
								}
							}
							else
							{
								String guid;
								String objectSid;
								ADGroup group;

								// Get the ldap guid that was read from the ldap directory for this user.
								guid = getLdapGuid( lAttrs, ldapGuidAttribute );
								
								// Get the group's object sid
								objectSid = getObjectSid( lAttrs );
								
								group = new ADGroup( guid, objectSid, teamingName, groupId );
								listOfADGroupsToSyncMembership.add( group );
							}
						}
					}

					// examine the response controls
					cookie = parseControls( ctx.getResponseControls() );

					try
					{
						// pass the cookie back to the server for the next page
						PagedResultsControl prCtrl;
						
						prCtrl = new PagedResultsControl( pageSize, cookie, Control.CRITICAL );
						ctx.setRequestControls( new Control[]{ prCtrl } );
					}
					catch ( IOException ex )
					{
						cookie = null;
						logger.error( "In syncGroups(), call to PagedResultsControl() threw an exception: ", ex );
					}

					// clear cache to prevent thrashing resulted from prolonged use of a single session
        			getCoreDao().clear();

				} while ( (cookie != null) && (cookie.length != 0) );
				
				// Do we have any AD groups that we need to sync their membership?
				if ( syncMembership && listOfADGroupsToSyncMembership != null )
				{
					int cnt;
					
					cnt = 0;
					for ( ADGroup nextADGroup : listOfADGroupsToSyncMembership )
					{
						Enumeration members;
						
						members = getGroupMembershipFromAD(
														nextADGroup.getGuid(),
														nextADGroup.getObjectSid(),
														nextADGroup.getName(),
														zone,
														config,
														searchInfo );
						
						if ( members != null )
						{
							++cnt;

							groupCoordinator.syncMembership( nextADGroup.getDbId(), members );
							
							if ( (cnt % 10) == 0 )
							{
								// clear cache to prevent thrashing resulted from prolonged use of a single session
			        			getCoreDao().clear();
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Read the group membership for the given group in AD
	 */
	private Enumeration getGroupMembershipFromAD(
		String guid,
		String objectSid,
		String name,
		Binder zone,
		LdapConnectionConfig ldapConfig,
		LdapConnectionConfig.SearchInfo searchInfo )
	{
		LdapContext ctx = null;
		Hashtable listOfMembers;
		long startTime = 0;

		if ( guid == null || zone == null || ldapConfig == null || searchInfo == null )
			return null;

		if ( name != null )
			logger.info( "Reading membership for group: " + name );
		
		if ( m_showTiming )
		{
			Date now;

			now = new Date();
			startTime = now.getTime();
		}

		listOfMembers = new Hashtable();
		
		try
		{
			int scope;
			SearchControls searchCtls;
			String search;
			String filter;
			StringBuffer ldapFilterGuid;
			
			ctx = getGroupContext( zone.getId(), ldapConfig );

			if ( searchInfo.isSearchSubtree() )
				scope = SearchControls.SUBTREE_SCOPE;
			else
				scope = SearchControls.ONELEVEL_SCOPE;
			
			searchCtls = new SearchControls();
			searchCtls.setSearchScope( scope );
			
			// Convert the guid into a format that can be used with an ldap filter.
			// ie, \C0\92\B2\A3\E4\E4\FF\4A\8C\50\98\DB\B4\C0\17\64
			ldapFilterGuid = new StringBuffer();
			for (int i = 0; i < guid.length(); ++i)
			{
				if ( (i % 2) == 0 )
					ldapFilterGuid.append( '\\' );
				
				ldapFilterGuid.append( guid.charAt( i ) );
			}
			
			search = "(" + ldapConfig.getLdapGuidAttribute() + "=" + ldapFilterGuid.toString() + ")";
			filter = searchInfo.getFilterWithoutCRLF();
			if ( Validator.isNull( filter ) == false )
			{
				search = "(&"+search+filter+")";
			}

			// Active Directory has a hard-coded limit of 5,000 for the number of values it will
			// return when requesting a multi-valued attribute.
			// We will issue ldap reads to get the membership in groups of 500
			int step = 500;
			int start = 0;
			int finish = step-1;
			boolean finished = false;
			String range;

			// Loop until we have retrieved all of the group membership
			while ( finished == false )
			{          
                NamingEnumeration answer;

                finished = true;
                
                // Specify the range of values to retrieve from the member attribute
				range = start + "-" + finish;
				String returnedAtts[] = {"member;Range=" + range};
				searchCtls.setReturningAttributes( returnedAtts );
          
                // Get the next n members of the group
				answer = ctx.search( searchInfo.getBaseDn(), search, searchCtls );

				// There should only be 1 result returned.
				if ( hasMore( answer ) )
				{
					SearchResult next;
                    Attributes attrs;
                    
                    next = (SearchResult)answer.next();

                    attrs = next.getAttributes();
                    if ( attrs != null )
                    {
                    	NamingEnumeration ne;

                    	// There should only be 1 attribute returned.
                    	ne = attrs.getAll();
                		if ( hasMore( ne ) )
                		{
                			Attribute attr;
                			NamingEnumeration listOfAttrValues;
                			String id;
                			
                			attr = (Attribute)ne.next();

                            // check if we are finished
                			id = attr.getID();
                			if ( id != null && id.endsWith( "*" ) == false )
                			{
                				int dash;
                				
                				// An example of the id is "member;range=0-29"
                				// Find the '-'
                				dash = id.indexOf( '-' );
                				if ( dash > 0 && dash+1 < id.length() )
                				{
                					String tmp;
                					int index;
                					
                					tmp = id.substring( dash+1 );
                					
                					// Get the index of the last value returned.
                					index = Integer.valueOf( tmp );
                					
                					start = index + 1;
                            		finish = start + step - 1;
                            		finished = false;
                				}
                			}

                			// Iterate through the values of the member attribute.
                			listOfAttrValues = attr.getAll();
                			while ( hasMore( listOfAttrValues ) )
                			{
                				Object obj;
                				
                				obj = listOfAttrValues.next();
                				
                				if ( obj != null && obj instanceof String )
                				{
                					String memberDN;
                					
                					memberDN = (String) obj;
                					listOfMembers.put( memberDN, memberDN );
                				}
                            }
                		}
                    }
				}
			}
		}
		catch ( Exception ex )
		{
			listOfMembers = null;
			logger.error( "In getGroupMembershipFromAD(), exception: ", ex );
		}
		finally
		{
			try
			{
				if ( ctx != null )
					ctx.close();
			}
			catch ( Exception ex )
			{
				logger.error( "in getGroupMembershipFromAD(), ctx.close() threw an exception: ", ex );
			}
		}
		
		// Get the members of this group where this group is their primary group
		if ( listOfMembers != null )
			getPrimaryGroupMembershipFromAD( listOfMembers, zone.getId(), guid, objectSid, ldapConfig );
		
		if ( m_showTiming )
		{
			long elapsedTimeInSeconds;
			long minutes;
			long seconds;
			long milliSeconds;
			Date now;
			
			now = new Date();

			milliSeconds = now.getTime() - startTime;
			elapsedTimeInSeconds = milliSeconds / 1000;
			minutes = elapsedTimeInSeconds / 60;
			seconds = elapsedTimeInSeconds - (minutes * 60);
			milliSeconds = milliSeconds - (elapsedTimeInSeconds * 1000);
			logger.info( "ldap sync timing: ----------> Time to read group membership " + minutes + " minutes " + seconds + " seconds " + milliSeconds + " milliseconds " );
		}

		return listOfMembers.keys();
	}
	
	/**
	 * 
	 */
	private void getPrimaryGroupMembershipFromAD(
		Hashtable listOfMembers,
		Long zoneId,
		String guid,
		String objectSid,
		LdapConnectionConfig ldapConfig )
	{
		int pageSize = 500;
		String groupId = null;
		LdapContext ldapContext = null;
		boolean syncAll;
		
		if ( listOfMembers == null || guid == null || objectSid == null || ldapConfig == null )
		{
			logger.error( "in getPrimaryGroupMembershipFromAD(), invalid parameter" );
			return;
		}
		
		syncAll = SPropsUtil.getBoolean( "ldap.sync.all.primary.group.membership", false );

		// Get the group id of the given group
		{
			int index;
			
			index = objectSid.lastIndexOf( '-' );
			if ( index != -1 && (index+1) < objectSid.length() )
				groupId = objectSid.substring( index + 1 );
			
			if ( groupId == null || groupId.length() == 0 )
			{
				logger.error( "in getPrimaryGroupMembershipFromAD(), unable to get the group id for group: " + objectSid );
				return;
			}
			
			// Are we supposed to try and sync the primary group membership for all groups?
			if ( syncAll == false )
			{
				// No
				// We are only going to retrieve the primary group membership for the "Domain Users" group.
				if ( groupId.equalsIgnoreCase( "513" ) == false )
					return;
			}
		}
		
		logger.info( "Getting primary group membership for group: " + objectSid );
		
		try
		{
			String[] attributesToRead;
			
			attributesToRead = new String[1];
			attributesToRead[0] = SAM_ACCOUNT_NAME_ATTRIBUTE;
			
			ldapContext = getUserContext( zoneId, ldapConfig );

			for ( LdapConnectionConfig.SearchInfo searchInfo : ldapConfig.getUserSearches() )
			{
				String userFilter;

				userFilter = searchInfo.getFilterWithoutCRLF();
				if ( Validator.isNotNull( userFilter ) )
				{
					byte[] cookie = null;
					int scope;
					String searchFilter;

					searchFilter = "(primaryGroupID=" + groupId + ")";
					searchFilter = "(&" + searchFilter + userFilter +")";

					if ( searchInfo.isSearchSubtree() )
						scope = SearchControls.SUBTREE_SCOPE;
					else
						scope = SearchControls.ONELEVEL_SCOPE;
					
					SearchControls sch = new SearchControls(
														scope,
														0,
														0,
														attributesToRead,
														false,
														false);
		
					logger.info( "\tSearching for primary group membership in base dn: " + searchInfo.getBaseDn() );
					
					// Request the paged results control
					try
					{
						Control[] ctls = new Control[]{ new PagedResultsControl( pageSize, true ) };
						ldapContext.setRequestControls( ctls );
					}
					catch ( IOException ex )
					{
						logger.error( "Call to new PagedResultsControl() threw an exception: ", ex );
					}

					do
					{
						NamingEnumeration results;

						// Issue an ldap search for users in the given base dn.
						results = ldapContext.search( searchInfo.getBaseDn(), searchFilter, sch );
						
						// loop through the results in each page
						while ( hasMore( results ) )
						{
							String userName;
							String relativeName;
							String memberDN;
							SearchResult sr;
							
							sr = (SearchResult)results.next();
							userName = sr.getNameInNamespace();
							
							relativeName = userName.trim();
							if ( sr.isRelative() && !"".equals( ldapContext.getNameInNamespace() ) )
							{
								memberDN = relativeName + "," + ldapContext.getNameInNamespace().trim();
							}
							else
							{
								memberDN = relativeName;
							}
							
        					listOfMembers.put( memberDN, memberDN );
						}
		     
						// examine the response controls
						cookie = parseControls( ldapContext.getResponseControls() );

						try
						{
							// pass the cookie back to the server for the next page
							PagedResultsControl prCtrl;
							
							prCtrl = new PagedResultsControl( pageSize, cookie, Control.CRITICAL );
							ldapContext.setRequestControls( new Control[]{ prCtrl } );
						}
						catch ( IOException ex )
						{
							cookie = null;
							logger.error( "Call to PagedResultsControl() threw an exception: ", ex );
						}

					} while ( (cookie != null) && (cookie.length != 0) );
				}
			}
		}
		catch ( Exception ex )
		{
		}
		finally
		{
			if ( ldapContext != null )
			{
				try
				{
					ldapContext.close();
				}
				catch ( NamingException namingEx )
		  		{
		  		}
			}
		}
	}
	
	protected void doLog(String caption, Map<String, Map> names) {
		if (logger.isInfoEnabled()) {
			logger.info(caption);
			for (Map.Entry<String, Map> me:names.entrySet()) {
				logger.info("'" + me.getKey() + "':'" + me.getValue().get(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME) + "'");
			}
		}
	}
	
	/**
	 * Fixup the given name by replacing all "/" with "\/"
	 */
	protected String fixupName( String name )
	{
		int		index;
		int		fromIndex;

		fromIndex = 0;
		
		// Find the next '/' in the name.
		while ( (index = name.indexOf( "/", fromIndex )) >= 0 )
		{
			fromIndex = index + 1;
			
			if ( index == 0 )
			{
				// Add a '\' before the '/'
				name = "\\" + name;
				++fromIndex;
			}
			else
			{
				// Add a '\' before the '/'
				name = name.substring( 0, index ) + "\\" + name.substring( index );
				++fromIndex;
			}
		}// end while()
		
		return name;
	}// end fixupName()
	
	
	/**
	 * Initialize context to an ldap server using 
	 * 
	 * @param info
	 * @return LdapContext
	 * @throws NamingException
	 */
	protected LdapContext getUserContext(Long zoneId, LdapConnectionConfig config) throws NamingException {
		return getContext(zoneId, config, false);
	}
	protected LdapContext getGroupContext(Long zoneId, LdapConnectionConfig config) throws NamingException {
		return getContext(zoneId, config, true);
	}
	protected LdapContext getContext(Long zoneId, LdapConnectionConfig config, boolean replaceDn) throws NamingException {
		String ldapGuidAttributeName;
		// Load user from ldap
		String user = config.getPrincipal();
		String pwd = config.getCredentials();

		Hashtable env = new Hashtable();
		Workspace zone = (Workspace)getCoreDao().load(Workspace.class, zoneId);
		env.put(Context.INITIAL_CONTEXT_FACTORY, getLdapProperty(zone.getName(), Context.INITIAL_CONTEXT_FACTORY));
		if (!Validator.isNull(user) && !Validator.isNull(pwd)) {
			env.put(Context.SECURITY_PRINCIPAL, user);
			env.put(Context.SECURITY_CREDENTIALS, pwd);		
			env.put(Context.SECURITY_AUTHENTICATION, getLdapProperty(zone.getName(), Context.SECURITY_AUTHENTICATION));
			env.put( Context.REFERRAL, "follow" );
		} 
		String url = config.getUrl();
		/*
		if(replaceDn && !Validator.isNull(config.getGroupsBasedn())) {
			if(url.lastIndexOf('/') > 8) {
				url = url.substring(0, url.lastIndexOf('/'));
			}
			url = url + "/" + config.getGroupsBasedn();
			logger.debug("Using " + url + " for groups");
		}
		*/

		// Do we have the name of the ldap attribute that holds the guid?
		ldapGuidAttributeName = config.getLdapGuidAttribute();
		if ( ldapGuidAttributeName != null && ldapGuidAttributeName.length() > 0 )
		{
			String attrNames;
			
			// Specify that we want the ldap guid and the objectSid attibute returned as binary data.
			attrNames = ldapGuidAttributeName + " " + OBJECT_SID_ATTRIBUTE;
			attrNames += " " + NDS_HOME_DIR_ATTRIBUTE;
			attrNames += " " + NETWORK_ADDRESS_ATTRIBUTE;
			attrNames += " " + "uid";
			
			if ( getLdapDirType( ldapGuidAttributeName ) == LdapDirType.EDIR )
				attrNames += " " + HOME_DIR_ATTRIBUTE;
			
			env.put( "java.naming.ldap.attributes.binary", attrNames );
		}
		
		env.put(Context.PROVIDER_URL, url);
		String socketFactory = getLdapProperty(zone.getName(), "java.naming.ldap.factory.socket"); 
		if (!Validator.isNull(socketFactory))
			env.put("java.naming.ldap.factory.socket", socketFactory);

		// Set the default timeout
		{
			String timeout;
			
			timeout = SPropsUtil.getString( "com.sun.jndi.ldap.connect.timeout", "60000" );
			env.put( "com.sun.jndi.ldap.connect.timeout", timeout );
			
			// Part of fix for bug 875689
			timeout = SPropsUtil.getString( "com.sun.jndi.ldap.read.timeout", "10000" );
			env.put( "com.sun.jndi.ldap.read.timeout", timeout );
		}
		
		return new InitialLdapContext(env, null);
	}


	/**
	 * Return the list of attribute names we need to read from the ldap directory.  We get the list of
	 * attributes to read from the ldap configuration that maps ldap attribute names to Teaming ids.
	 */
	protected static String[] getAttributeNamesToRead(
		String[] userAttributeNames,
		LdapConnectionConfig ldapConnectionConfig )
	{
		String[] attributeNames = null;
		
		// this.userAttributeNames holds the list of ldap attribute names that are
		// found in the ldap configuration mapping.
		
		if ( userAttributeNames != null )
		{
			int i;
			String attrName;
			
			// Create an array large enough to hold all the ldap attribute names found
			// in the mapping plus the following:
			// 1. name of the attribute that uniquely identifies a user
			// 2. the ldap attribute that identifies a user
			// 3. the objectSid
			// 4. sAMAccountName.
			// 5. the attribute that holds whether a user is disabled.
			attributeNames = new String[userAttributeNames.length + 5];
			
			for (i = 0; i < userAttributeNames.length; ++i)
			{
				attributeNames[i] = userAttributeNames[i];
			}

			// Add the ldap attribute that is used to identify the user.
			attrName = ldapConnectionConfig.getUserIdAttribute();
			if ( attrName != null && attrName.length() > 0 )
			{
				attributeNames[i] = attrName; 
				++i;
			}
			
			// Add the name of the ldap guid attribute to the list of ldap attributes to read.
			attrName = ldapConnectionConfig.getLdapGuidAttribute();
			if ( attrName != null && attrName.length() > 0 )
			{
				attributeNames[i] = attrName;
				++i;
				
				// Is the ldap directory AD?
				if ( attrName.equalsIgnoreCase( OBJECT_GUID_ATTRIBUTE ) )
				{
					// Yes
					// Add "objectSid" to the list of ldap attributes to read.
					attributeNames[i] = OBJECT_SID_ATTRIBUTE;
					++i;

					// Add "sAMAccountName" to the list of ldap attributes to read.
					attributeNames[i] = SAM_ACCOUNT_NAME_ATTRIBUTE;
					++i;
					
					// Add the "userAccountControl" attribute
					attributeNames[i] = AD_USER_ACCOUNT_CONTROL_ATTRIBUTE;
					++i;
				}
				// Is the ldap directory eDir?
				else if ( attrName.equalsIgnoreCase( GUID_ATTRIBUTE ) )
				{
					// Yes
					// Add "loginDisabled" to the list of ldap attributes to read.
					attributeNames[i] = LOGIN_DISABLED_ATTRIBUTE;
					++i;
				}
			}
		}
		
		return attributeNames;
	}// end getAttributeNamesToRead()

	
	/**
	 * 
	 */
	private static boolean canVibeFieldValueBeEmpty( String fieldName )
	{
		if ( fieldName == null || fieldName.length() == 0 )
			return false;
		
		if ( fieldName.equalsIgnoreCase( "name" ) || fieldName.equalsIgnoreCase( "title" ) )
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Look in the given map to see if the given key hold a string value that is not empty
	 */
	private static boolean containsNonEmptyString( Map map, String key )
	{
		boolean retValue = false;
		
		if ( map != null && key != null )
		{
			Object value;

			value = map.get( key );
			if ( value != null && value instanceof String )
			{
				String strValue;
				
				strValue = (String) value;
				if ( strValue.length() > 0 )
					retValue = true;
			}
		}
		
		return retValue;
	}
	
	/**
	 * @param ldapAttrNames
	 * @param mapping
	 * @param attrs
	 * @param mods
	 * @param ldapGuidAttribute
	 * @throws NamingException
	 */
	protected void getUpdates(
		String []ldapAttrNames,
		Map mapping,
		Attributes attrs,
		Map mods,
		String ldapGuidAttribute ) throws NamingException
	{
		if ( ldapAttrNames != null )
		{
			for (int i=0; i<ldapAttrNames.length; i++) {
				String vibeAttrName;
				
				vibeAttrName = (String) mapping.get( ldapAttrNames[i] );
				
				Attribute att = attrs.get(ldapAttrNames[i]);
				if ( att == null )
				{
					// Does the update mapping already hold a value for this attribute?
					if ( containsNonEmptyString( mods, vibeAttrName ) == false )
					{
						// No, can this field be empty?
						if ( canVibeFieldValueBeEmpty( vibeAttrName ) == true )
						{
							// Yes
							mods.put( vibeAttrName, "" );
						}
					}
						
					continue;
				}
				
				Object val = att.get();
				if ( val == null )
				{
					// Does the update mapping already hold a value for this attribute?
					if ( containsNonEmptyString( mods, vibeAttrName ) == false )
					{
						// No, can this field be empty?
						if ( canVibeFieldValueBeEmpty( vibeAttrName ) == true )
						{
							// Yes
							mods.put( vibeAttrName, "" );
						}
					}
				}
				else if ( att.size() == 0 )
				{
					// Does the update mapping already hold a value for this attribute?
					if ( containsNonEmptyString( mods, vibeAttrName ) == false )
					{
						// No, can this field be empty?
						if ( canVibeFieldValueBeEmpty( vibeAttrName ) == true )
						{
							// Yes
							mods.put( vibeAttrName, "" );
						}
					}
				}
				else if ( att.size() == 1 )
				{
					if ( val instanceof String )
					{
						String strValue;

						strValue = ((String)val).trim();
						mods.put( vibeAttrName, strValue );					
					}
					else
					{
						mods.put( vibeAttrName, val );
					}
				}
				else
				{
					String value = "";
					NamingEnumeration valEnum;
					
					// This attribute is a multi-valued attribute.  Teaming doesn't understand
					// multi-valued attributes.  So we will sync the first value.
					valEnum = att.getAll();
					while ( valEnum.hasMoreElements() && value != null && value.length() == 0 )
					{
						Object firstValue;
						
						firstValue = valEnum.nextElement();
						
						// We only know how to deal with Strings
						value = firstValue.toString();
					}
	
					if ( value != null )
					{
						value = value.trim();
					}
					
					mods.put( vibeAttrName, value );
				}
			}
		}
		
		// Do we have an ldap guid attribute?
		if ( ldapGuidAttribute != null )
		{
			String ldapGuid;
			
			ldapGuid = getLdapGuid( attrs, ldapGuidAttribute );
			mods.put( ObjectKeys.FIELD_PRINCIPAL_LDAPGUID, ldapGuid );

			// Is the ldap directory AD?
			if ( ldapGuidAttribute.equalsIgnoreCase( OBJECT_GUID_ATTRIBUTE ) )
			{
				String objectSid;
				String samAccountName;
				Boolean disabled;
				
				// Yes
				// Add the value of the "objectSid" attribute
				objectSid = getObjectSid( attrs );
				if(objectSid != null)
					objectSid = objectSid.toLowerCase();
				mods.put( ObjectKeys.FIELD_PRINCIPAL_OBJECTSID, objectSid );
				
				// Add the value of the "sAMAccountName" attribute.
				samAccountName = getSamAccountName( attrs );
				mods.put( ObjectKeys.FIELD_PRINCIPAL_SAM_ACCOUNT_NAME, samAccountName );

				disabled = getIsDisabledInActiveDirectory( attrs );
				if ( disabled != null )
				{
					mods.put( ObjectKeys.FIELD_PRINCIPAL_DISABLED, disabled.toString() );
				}
			}
			// Is the ldap directory eDir?
			else if ( ldapGuidAttribute.equalsIgnoreCase( GUID_ATTRIBUTE ) )
			{
				Boolean disabled;
				
				// Yes
				disabled = getIsDisabledInEdir( attrs );
				if ( disabled != null )
				{
					mods.put( ObjectKeys.FIELD_PRINCIPAL_DISABLED, disabled.toString() );
				}
			}
		}
	}
	
	
	/**
	 * Get whether the user is disabled in Active Directory.
	 */
	private static Boolean getIsDisabledInActiveDirectory( Attributes attrs )
	{
		Attribute attrib;

		// Get the userAccountControl attribute.
		attrib = attrs.get( AD_USER_ACCOUNT_CONTROL_ATTRIBUTE );
		if ( attrib != null )
		{
			try
			{
				Object value;
				
				value = attrib.get();
				if ( value != null && value instanceof String )
				{
					String strValue;
					Long lValue;
						
					strValue = (String) value;
					lValue = Long.valueOf( strValue );
					
					// Is the disabled bit set?
					if ( (lValue & AD_DISABLED_BIT) > 0 )
					{
						// Yes
						return Boolean.TRUE;
					}
						
					return Boolean.FALSE;
				}
			}
			catch ( Exception ex )
			{
				// Nothing to do.
			}
		}

		// If we get here, something did not work.
		return null;
	}

	/**
	 * Get whether the user is disabled in eDirectory.
	 */
	private static Boolean getIsDisabledInEdir( Attributes attrs )
	{
		Attribute attrib;

		// Get the loginDisabled attribute.
		attrib = attrs.get( LOGIN_DISABLED_ATTRIBUTE );
		if ( attrib != null )
		{
			try
			{
				Object value;
				
				value = attrib.get();
				if ( value != null && value instanceof String )
				{
					String disabled;
						
					disabled = (String) value;
					if ( disabled.equalsIgnoreCase( "true" ) )
						return Boolean.TRUE;
						
					return Boolean.FALSE;
				}
			}
			catch ( NamingException ex )
			{
				// Nothing to do.
			}
		}
		else
		{
			// If the user does not have a loginDisabled attribute it means
			// they are NOT disabled.
			return Boolean.FALSE;
		}

		// If we get here, something did not work.
		return null;
	}

	/**
	 * Convert the given Active Directory date to a Java date 
	 */
	private static Date convertADDateToJavaDate( String value )
	{
		Date javaDate = null;
		long adTime;
		long javaTime;
			
		if ( value == null || value.length() == 0 )
			return null;
		
		// In Active Directory the value is stored as a large integer that represents
		// the number of 100 nanosecond intervals since January 1, 1601 (UTC)
		adTime = Long.parseLong( value );
		
		// Active Directory Epoch is January 1, 1601
		// Java date Epoch is January 1, 1970;
		// Subtract the Java Epoch
		javaTime = adTime - 0x19db1ded53e8000L;
		
		// Convert from (100 nano-seconds) to milliseconds
		javaTime /= 10000L;
		
		javaDate = new Date( javaTime );

		return javaDate;
	}
	
	/**
	 * Return the maximum age of a password in Active Directory.
	 * This value is stored as a large integer that represents the number of 100 nanosecond intervals
	 * from the time the password was set before the password expires.
	 */
	private Integer getMaxPwdAge( LdapConnectionConfig ldapConfig )
	{
		Integer maxPwdAge = null;
		LdapContext ldapCtx = null;

		try
		{
			SearchControls sch;
                        ADLdapObject domainInfo;
			String search;
			String baseDN = null;
			String[] attributesToRead = { AD_MAX_PWD_AGE_ATTRIBUTE };
			NamingEnumeration ctxSearch;

			ldapCtx = getUserContext( RequestContextHolder.getRequestContext().getZone().getId(), ldapConfig );
			
			sch = new SearchControls();
			sch.setSearchScope( SearchControls.OBJECT_SCOPE );
			sch.setReturningAttributes( attributesToRead );

			search = "(objectClass=domain)";
			domainInfo = readDomainInfoFromAD( ldapConfig );
                        if (domainInfo != null)
                            baseDN = domainInfo.getDomainName();
			
			// Get the Domain Root Object
			ctxSearch = ldapCtx.search( baseDN, search, sch );
			if ( hasMore( ctxSearch ) )
			{
				SearchResult searchResult;
				Attributes attrs;
				Attribute attrib;
				
				searchResult = (SearchResult)ctxSearch.next();
	
				// Read the "maxPwdAge" attribute from the directory.
				attrs = searchResult.getAttributes();
				attrib = attrs.get( AD_MAX_PWD_AGE_ATTRIBUTE );
				if ( attrib != null )
				{
					String strValue;

					strValue = (String) attrib.get();
					if ( strValue != null )
					{
						long adTime;
						long milliSeconds;
						int days;
		
						// This value is stored as a large integer that represents the number of
						// 100 nanosecond intervals from the time the password was set before the
						// password expires.
						adTime = Long.parseLong( strValue );
						
						// Convert from (100 nano-seconds) to milliseconds
						milliSeconds = adTime / 10000L;
						
						// Convert milli secons into days
						days = (int) (milliSeconds / (24 * 60 * 60 * 1000));
						maxPwdAge = new Integer( days * -1 );
					}
				}
			}
		}
		catch ( Exception ex )
		{
			logger.error( "getMaxPwdAge() caught an exception: ", ex );
		}
		finally
		{
			if ( ldapCtx != null )
			{
				try
				{
					ldapCtx.close();
					ldapCtx = null;
				}
				catch ( NamingException ex )
				{
					// Nothing to do
				}
			}
		}
		
		return maxPwdAge;
	}
	
	/**
	 * Get the date the password was last set in Active Directory
	 */
	private Date getPasswordLastSet( Attributes attrs )
	{
		Date pwdLastSetDate = null;
		Attribute attrib;
		
		// Get the value of the "pwdLastSet" attribute
		attrib = attrs.get( AD_PASSWORD_LAST_SET_ATTRIBUTE  );
		if ( attrib != null )
		{
			Object value;
			
			try
			{
				value = attrib.get();
				if ( value != null && value instanceof String )
				{
					String strValue;
						
					strValue = (String) value;
					pwdLastSetDate = convertADDateToJavaDate( strValue );
				}
			}
			catch ( NamingException ex )
			{
				// Nothing to do.
				logger.error( "getPasswordLastSet() caught an exception: ", ex );
			}
		}
	
		return pwdLastSetDate;
	}
	
	/**
	 * Get whether the "password never expires" attribute is turned on for the user in Active Directory.
	 */
	private boolean getPasswordNeverExpires( Attributes attrs )
	{
		Attribute attrib;

		// Get the userAccountControl attribute.
		attrib = attrs.get( AD_USER_ACCOUNT_CONTROL_ATTRIBUTE );
		if ( attrib != null )
		{
			try
			{
				Object value;
				
				value = attrib.get();
				if ( value != null && value instanceof String )
				{
					String strValue;
					Long lValue;
						
					strValue = (String) value;
					lValue = Long.valueOf( strValue );
					
					// Is the "password never expires" bit set?
					if ( (lValue & AD_PASSWORD_NEVER_EXPIRES_BIT) > 0 )
					{
						// Yes
						return true;
					}
						
					return false;
				}
			}
			catch ( Exception ex )
			{
				// Nothing to do.
				logger.error( "getPasswordExpires() threw an exception: ", ex );
			}
		}

		// If we get here, something did not work.
		return false;
	}

	/**
	 * Get the guid we read from the ldap directory.  Convert the guid byte array into a string where each
	 * byte in the array is represented by hex characters.
	 */
	private static String getLdapGuid( Attributes attrs, String ldapGuidAttribute )
	{
		Attribute attrib;
		String ldapGuidStr = null;
		
		// Get the ldap attribute that holds the guid.
		attrib = attrs.get( ldapGuidAttribute );
		if ( attrib != null )
		{
			try
			{
				Object value;
				
				value = attrib.get();
				if ( value != null && value instanceof byte[] )
				{
					int i;
					byte[] bytes;
					StringBuffer strBuffer;
					
					bytes = (byte[]) value;
					
					strBuffer = new StringBuffer();
					
					for (i = 0; i < bytes.length; ++i)
					{
						String hexStr;
						
						// Get the next byte as a hex string.
						hexStr = Integer.toHexString( bytes[i] & 0xff );
						
						// If the hex string only has 1 char in it prepend a "0"
						if ( hexStr != null )
						{
							if ( hexStr.length() == 1 )
								strBuffer.append( "0" );
							
							strBuffer.append( hexStr );
						}
					}
					
					ldapGuidStr = strBuffer.toString();
				}
			}
			catch (NamingException ex)
			{
				// Nothing to do.
			}
		}

		return ldapGuidStr;
	}// end getLdapGuid()

	/**
	 * Get a string representation of the value found in the objectSid attribute by converting the
	 * objectSid byte array into a string.
	 * For a description of the string syntax see these sites:
	 * http://msdn.microsoft.com/en-us/library/cc230371(PROT.10).aspx
	 * http://msdn.microsoft.com/en-us/library/ff632068(v=prot.10).aspx
	 */
	private static String getObjectSid( Attributes attrs )
	{
		Attribute attrib;

		// Get the ldap attribute that holds the objectSid.
		attrib = attrs.get( OBJECT_SID_ATTRIBUTE );
		if ( attrib != null )
		{
			try
			{
				Object value;
				
				value = attrib.get();
				if ( value != null && value instanceof byte[] )
				{
					StringBuilder strSID;
					byte[] bytes;
					
					bytes = (byte[]) value;
					
					// Add the 'S' prefix
					strSID = new StringBuilder( "S-" );

					// bytes[0] : in the array is the version (must be 1 but might change in the future)
					strSID.append( bytes[0] );
					strSID.append('-');

					// bytes[2..7] : the Authority
					{
						StringBuilder tmpBuff;
					
						tmpBuff = new StringBuilder();
						for (int t=2; t<=7; t++)
						{
							String hexString;

							hexString = Integer.toHexString( bytes[t] & 0xFF );
							tmpBuff.append( hexString );
						}
						strSID.append( Long.parseLong( tmpBuff.toString(), 16 ) );
					}

					// Add the sub authorities
					{
						int count;
						
						// bytes[1] : the sub authorities count
						count = bytes[1];

						// bytes[8..end] : the sub authorities (these are Integers - notice the endian)
						for (int i = 0; i < count; i++) 
						{
							int currSubAuthOffset;
							StringBuilder tmpBuff;
							
							tmpBuff = new StringBuilder();
							
							currSubAuthOffset = i*4;
							tmpBuff.append( String.format(
													"%02X%02X%02X%02X", 
													(bytes[11 + currSubAuthOffset] & 0xFF),
													(bytes[10 + currSubAuthOffset] & 0xFF),
													(bytes[9 + currSubAuthOffset] & 0xFF),
													(bytes[8 + currSubAuthOffset] & 0xFF)));

							strSID.append('-');
							strSID.append( Long.parseLong( tmpBuff.toString(), 16 ) );
						}
					}

				   // That's it - we have the SID
				   return strSID.toString();
				}
			}
			catch (NamingException ex)
			{
				// Nothing to do.
			}
		}

		// If we get here, something did not work.
		return null;
	}
	
	/**
	 * Get the value of the sAMAccountName attribute.
	 */
	private static String getSamAccountName( Attributes attrs )
	{
		Attribute attrib;

		// Get the ldap attribute that holds the sAMAccountName.
		attrib = attrs.get( SAM_ACCOUNT_NAME_ATTRIBUTE );
		if ( attrib != null && attrib.size() == 1 )
		{
			Object value;
			
			try
			{
				value = attrib.get();
				if ( value != null && value instanceof String )
					return (String) value;
			}
			catch ( NamingException ex )
			{
				// Nothing to do.
			}
		}

		// If we get here, something did not work.
		return null;
	}
	
	/**
	 * Read the default locale id from the global properties
	 * @param name
	 * @return
	 */
    @Override
	public String getDefaultLocaleId()
	{
		String			defaultLocaleId;
		Workspace		topWorkspace;
		WorkspaceModule	workspaceModule;
		
		// Get the top workspace.  That is where global properties are stored.
        workspaceModule = getWorkspaceModule();
        topWorkspace = workspaceModule.getTopWorkspace();
		
		// Get the default locale property.
		defaultLocaleId = (String) topWorkspace.getProperty( ObjectKeys.GLOBAL_PROPERTY_DEFAULT_LOCALE );
		if ( defaultLocaleId == null || defaultLocaleId.length() == 0 )
		{
			Locale locale;
			
			// Get the default system locale;
			locale = NLT.getTeamingLocale();
			if ( locale != null )
				defaultLocaleId = locale.toString();
		}
		
		return defaultLocaleId;
	}// end getDefaultLocaleId()


	/**
	 * Read the default time zone from the global properties
	 * @param name
	 * @return
	 */
	@Override
	public String getDefaultTimeZone()
	{
		String			defaultTimeZone;
		Workspace		topWorkspace;
		WorkspaceModule	workspaceModule;
		
		// Get the top workspace.  That is where global properties are stored.
        workspaceModule = getWorkspaceModule();
        topWorkspace = workspaceModule.getTopWorkspace();
		
		// Get the default time zone property.
		defaultTimeZone = (String) topWorkspace.getProperty( ObjectKeys.GLOBAL_PROPERTY_DEFAULT_TIME_ZONE );
		if ( defaultTimeZone == null || defaultTimeZone.length() == 0 )
			defaultTimeZone = "GMT";
		
		return defaultTimeZone;
	}// end getDefaultTimeZone()


    /**
     * Set the default locale setting.  This setting is used to set the locale  on a user when
     * the user is created from an ldap sync.
     */
    @Override
	public void setDefaultLocale(String localeId)
    {
        Workspace	topWorkspace;

        if ( localeId == null || localeId.length() == 0 )
            return;

        // Get the top workspace.  That is where global properties are stored.
        topWorkspace = getWorkspaceModule().getTopWorkspace();

        // Save the default locale id as a global property
        topWorkspace.setProperty( ObjectKeys.GLOBAL_PROPERTY_DEFAULT_LOCALE, localeId );

    }


    /**
     * Set the default time zone setting.  This setting is used to set the time zone on a user when
     * the user is created from an ldap sync.
     */
    @Override
	public void setDefaultTimeZone(String timeZoneId)
    {
        Workspace	topWorkspace;

        if ( timeZoneId == null || timeZoneId.length() == 0 )
            return;

        // Get the top workspace.  That is where global properties are stored.
        topWorkspace = getWorkspaceModule().getTopWorkspace();

        // Save the default time zone as a global property
        topWorkspace.setProperty( ObjectKeys.GLOBAL_PROPERTY_DEFAULT_TIME_ZONE, timeZoneId );
    }

    @Override
	public void updateHomeDirectoryIfNecessary(User user, String userName, boolean logErrors) {
        if (user.getIdentityInfo().isFromLdap()) {
            try {
                // Does this user have a home directory attribute in ldap?
                HomeDirInfo homeDirInfo = getHomeDirInfo( user.getName(), userName, logErrors );
                if ( homeDirInfo != null )
                {
                    try {
                        // Yes
                        // Create/update the home directory net folder for this user.
                        NetFolderHelper.createHomeDirNetFolder(
                                getProfileModule(),
                                getTemplateModule(),
                                getBinderModule(),
                                getFolderModule(),
                                getNetFolderModule(),
                                getAdminModule(),
                                getResourceDriverModule(),
                                null,
                                homeDirInfo,
                                user,
                                true );
                    } catch (Exception e) {
                        throw new HomeFolderCreateException(e, homeDirInfo.getServerAddr(), userName);
                    }
                }
                else
                {
                    // We only want to delete the home dir net folder if the web client is the one
                    // making the request.
                    Binder netFolderBinder;

                    // The user does not have a home directory attribute.
                    // Does the user already have a home dir net folder?
                    // Does a net folder already exist for this user's home directory
                    netFolderBinder = NetFolderHelper.findHomeDirNetFolder(
                            binderModule,
                            user.getWorkspaceId() );
                    if ( netFolderBinder != null )
                    {
                        // Yes
                        // Delete the home net folder.
                        try {
                            NetFolderHelper.deleteNetFolder( getNetFolderModule(), netFolderBinder.getId(), false );
                        } catch (Exception e) {
                            throw new HomeFolderDeleteException(e, netFolderBinder.getName(), userName);
                        }

                    }
                }
            }
            catch (NamingException e) {
                throw new LdapReadException(e, userName);
            }
        }
    }

    protected String nameToId(String name) {
		return name;
	}
	protected String idToName(String id) {
		return id.trim();
	}
	protected String dnToGroupName(String dn) {
		//already trimmed
		return dn;
	}
	
	/**
	 * Transaction enabled methods for finer granularity.  We load the ldap attributes
	 * as string values.  We are not using the definition builder.
	 * 
	 */
	/**
	 * Update a user with attributes specified in the map.
	 *
	 * @param zoneName
	 * @param loginName
	 * @param mods
	 * @throws NoUserByTheNameException
	 */
	protected void updateUser(
		Binder zone,
		String loginName,
		Map mods,
		HomeDirInfo homeDirInfo ) throws NoUserByTheNameException 
	{
		String newName;

		User profile = getProfileDao().findUserByName(loginName, zone.getName()); 
 		ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
	            	profile.getParentBinder(), ProfileCoreProcessor.PROCESSOR_KEY);
		processor.syncEntry(profile, new MapInputData(StringCheckUtil.check(mods)), null);
		
		// Did the user's name change?
		newName = (String) mods.get( ObjectKeys.FIELD_PRINCIPAL_NAME );
		if ( loginName != null && newName != null && loginName.equalsIgnoreCase( newName ) == false )
		{
   			List<User> listOfRenamedUsers;
   			User user;
			
			// Yes
   			user = getProfileDao().findUserByName( newName, zone.getName() );
   			
			listOfRenamedUsers = new ArrayList<User>();
			listOfRenamedUsers.add( user );
			handleRenamedUsers( listOfRenamedUsers );
		}
	}	
	/**
	 * Update users with their own map of updates
	 * @param users - Map indexed by user id, value is map of updates for a user
	 */
	protected void updateUsers(
		Long zoneId,
		final Map users,
    	Map<Long, HomeDirInfo> homeDirInfoMap,
    	LdapSyncMode syncMode,
		PartialLdapSyncResults syncResults ) 
	{
		ProfileBinder pf;
		List collections;
	   	List foundEntries = null;
	   	Map entries;
	   	Map<Long,String> originalUserNamesMap;
   		ProfileCoreProcessor processor = null;
   		long modifyUsersStartTime = 0;

		if ( users.isEmpty() )
			return;
		
		// Are we in "preview" mode?
		if ( syncMode == LdapSyncMode.PREVIEW_ONLY )
		{
			// Yes, we don't want to do anything
			return;
		}
		
		if ( m_showTiming )
		{
			Date now;
			
			now = new Date();
			modifyUsersStartTime = now.getTime();
		}

		pf = getProfileDao().getProfileBinder(zoneId);
	   	entries = new HashMap();
	   	originalUserNamesMap = new HashMap<Long,String>();

	   	try 
	   	{
	   		Map changedEntries;
   			List<User> listOfRenamedUsers;
   			
			collections = new ArrayList();
			collections.add( "customAttributes" );
			collections.add( "emailAddresses" );
		   	foundEntries = getCoreDao().loadObjects( users.keySet(), User.class, zoneId, collections );
		   	for (int i=0; i<foundEntries.size(); ++i)
		   	{
		   		User u = (User)foundEntries.get(i);
		   		entries.put( u, new MapInputData( StringCheckUtil.check( (Map)users.get( u.getId() ) ) ) );
		   		originalUserNamesMap.put( u.getId(), u.getName() );
		   	}

   	   		processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
   	   																		pf, 
   	   																		ProfileCoreProcessor.PROCESSOR_KEY);
	   		changedEntries = processor.syncEntries( entries, null, syncResults );
	   		IndexSynchronizationManager.applyChanges(); //apply now, syncEntries will commit

	   		// Make the necessary changes to the db tables for each user that was renamed.
	   		listOfRenamedUsers = getListOfRenamedUsers( originalUserNamesMap, changedEntries );
	   		handleRenamedUsers( listOfRenamedUsers );

			if ( m_showTiming && changedEntries != null )
			{
				int numUsersModifiedInThisBatch;
				
				numUsersModifiedInThisBatch = changedEntries.size();
				m_numUsersModified += numUsersModifiedInThisBatch;
				if ( numUsersModifiedInThisBatch > 0 )
				{
					long elapsedTimeInSeconds;
					long minutes;
					long seconds;
					Date now;
					
					now = new Date();

					elapsedTimeInSeconds = now.getTime() - modifyUsersStartTime;
					elapsedTimeInSeconds /= 1000;
					minutes = elapsedTimeInSeconds / 60;
					seconds = elapsedTimeInSeconds - (minutes * 60);
					logger.info( "ldap sync timing: ----------> Time to modify last " + numUsersModifiedInThisBatch + " users: " + minutes + " minutes " + seconds + " seconds" );
				}
			}
	   	}
	   	catch ( Exception ex )
	   	{
	   		logger.error( "An error happened updating a user in the batch of users: ", ex );
	   		
	   		// Try to update each user in the list
		   	for (int i=0; foundEntries != null && i < foundEntries.size() && processor != null; ++i)
		   	{
		   		try
		   		{
			   		User user;
			   		Map changedEntries;
		   			List<User> listOfRenamedUsers;

			   		entries.clear();
			   		user = (User)foundEntries.get( i );
			   		entries.put( user, new MapInputData( StringCheckUtil.check( (Map)users.get( user.getId() ) ) ) );

			   		logger.info( "2nd attempt to update the user: " + user.getName() );
			   		
			   		changedEntries = processor.syncEntries( entries, null, syncResults );
			   		IndexSynchronizationManager.applyChanges(); //apply now, syncEntries will commit
			   		
			   		// Make the necessary changes to the db tables for renamed user.
			   		listOfRenamedUsers = getListOfRenamedUsers( originalUserNamesMap, changedEntries );
			   		handleRenamedUsers( listOfRenamedUsers );
		   		}
		   		catch ( Exception ex2 )
		   		{
		   			logger.error( "2nd attempt to update the user failed: ", ex2 );
		   		}
		   	}
	   	}
	}

	/**
	 * Make the necessary changes to the db tables for each renamed user.
	 * For mapOfRenamedUsers, the key is a User object and the value is the original user name
	 */
	private void handleRenamedUsers( List<User> listOfRenamedUsers )
	{
		ProfileDao profileDao;

		if ( listOfRenamedUsers == null || listOfRenamedUsers.size() == 0 )
			return;

		profileDao = getProfileDao();

		for ( User nextUser : listOfRenamedUsers )
		{
			try
			{
				profileDao.renameUser( nextUser );
				logger.info( "\tRenamed user: " + nextUser.getName() );
			}
			catch ( Exception ex )
			{
				logger.error( "Error updating db for renamed user: " + nextUser.getName() + " Exception: ", ex );
			}
		}
	}
	
	/**
	 * Get a list of users that were renamed.
	 * originalUsersMap:
	 * 	key: user id
	 *  value: original user name
	 *  
	 * The key of the returned map is the user object and the value is the old user name
	 */
	private List<User> getListOfRenamedUsers( Map<Long,String> originalUsersMap, Map modifiedUsersMap )
	{
		List<User> listOfRenamedUsers;

		listOfRenamedUsers = new ArrayList<User>();

		// Do we have any modified users?
   		if ( modifiedUsersMap != null && modifiedUsersMap.size() > 0 && originalUsersMap != null )
   		{
   			Set listOfModifiedUsers;
			Iterator iter;
			
   			// Yes
	   		// Go through the list of modified users and see if their name was changed.
   			listOfModifiedUsers = modifiedUsersMap.keySet();
   			iter = listOfModifiedUsers.iterator();
			while ( iter.hasNext() )
			{
				String originalUserName = null;
				User modifiedUser;
				
				// Get the next modified user
				modifiedUser = (User) iter.next();
				
				// Get the original name of the user.
				originalUserName = originalUsersMap.get( modifiedUser.getId() );
				if ( originalUserName != null && originalUserName.length() > 0 )
				{
					// Did the user name change?
					if ( originalUserName.equalsIgnoreCase( modifiedUser.getName() ) == false )
					{
						// Yes
						listOfRenamedUsers.add( modifiedUser );
					}
				}
			}
   		}
   		
   		return listOfRenamedUsers;
	}
	
	/**
	 * Disable the given list of users.
	 */
	protected void disableUsers(
		Map users,
		LdapSyncMode syncMode,
		PartialLdapSyncResults syncResults )
	{
		Set userIds;
		Iterator iter;
		
		if ( users.isEmpty() )
			return;

		// Get the list of user ids.
		userIds = users.keySet();
		
		for ( iter = userIds.iterator(); iter.hasNext(); )
		{
			String userName;
			Long userIdL;
			HashMap values;
			
			// Get the id of the user.
			userIdL = (Long) iter.next();
			
			// Get the name of the user.
			values = (HashMap) users.get( userIdL );
			userName = (String) values.get(  ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME );
			
			if ( syncMode == LdapSyncMode.PERFORM_SYNC )
			{
				getProfileModule().disableEntry( userIdL, true );

				logger.info( "Disabled user: " + userName );
			}

			if ( syncResults != null )
				syncResults.addResult( userName );
		}
	}

    /**
     * Update group with their own updates
     */    
	protected void updateGroup(
		Long zoneId,
		final Long groupId,
		final Map groupMods,
		LdapSyncMode syncMode,
		LdapSyncResults ldapSyncResults )
	{
		// Are we in "preview" mode?
		if ( syncMode == LdapSyncMode.PREVIEW_ONLY )
		{
			// Yes, nothing to do.
			return;
		}
		
		ProfileBinder pf = getProfileDao().getProfileBinder(zoneId);
		List collections = new ArrayList();
		collections.add("customAttributes");
	   	List foundEntries = getCoreDao().loadObjects(Arrays.asList(new Long[] {groupId}), Group.class, zoneId, collections);
	   	Map entries = new HashMap();
	   	Group g = (Group)foundEntries.get(0);
	   	entries.put(g, new MapInputData(StringCheckUtil.check(groupMods)));
	    try {
	    	PartialLdapSyncResults	syncResults	= null;
	    	
	    	// Do we have a place to store the list of modified groups?
	    	if ( ldapSyncResults != null )
	    	{
	    		syncResults = ldapSyncResults.getModifiedGroups();
	    	}
	    	ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
            	pf, ProfileCoreProcessor.PROCESSOR_KEY);
	    	processor.syncEntries(entries, null, syncResults );
	    	IndexSynchronizationManager.applyChanges(); //apply now, syncEntries will commit
	    } catch (Exception ex) {
	    	//continue 
	    	logError("Error updating groups", ex);	   		
	    }
    }// end updateGroup()


    @SuppressWarnings("deprecation")
	protected void updateMembership(
    	final Long groupId,
    	Collection newMembers,
    	LdapSyncMode syncMode,
    	PartialLdapSyncResults syncResults )
    {
		long startTime = 0;

    	// Are we in "preview" mode?
    	if ( syncMode == LdapSyncMode.PREVIEW_ONLY )
    	{
    		// Yes, nothing to do.
    		return;
    	}
    	
		if ( m_showTiming )
		{
			Date now;

			now = new Date();
			startTime = now.getTime();
		}

		//have a list of users, now compare with what exists already
		List oldMembers = getProfileDao().getMembership(groupId, RequestContextHolder.getRequestContext().getZoneId());
		final Set newM = CollectionUtil.differences(newMembers, oldMembers);
		final Set remM = CollectionUtil.differences(oldMembers, newMembers);

        if(!newM.isEmpty() || !remM.isEmpty()) { // membership changed
	        // The following part requires update database transaction.
	        getTransactionTemplate().execute(new TransactionCallback() {
	        	@Override
				public Object doInTransaction(TransactionStatus status) {
	        		for (Iterator iter=remM.iterator(); iter.hasNext();) {
	        			Membership c = (Membership)iter.next();
	       				getCoreDao().delete(c);
	        		}
			
	        		getCoreDao().save(newM);
	        		return null;
	        	}});
        
        	sessionFactory.evictCollection("org.kablink.teaming.domain.UserPrincipal.memberOf");
        	sessionFactory.getCache().evictCollection("org.kablink.teaming.domain.Group.members", groupId);
        }
		
		if ( m_showTiming )
		{
			long elapsedTimeInSeconds;
			long minutes;
			long seconds;
			long milliSeconds;
			Date now;
			
			now = new Date();

			milliSeconds = now.getTime() - startTime;
			elapsedTimeInSeconds = milliSeconds / 1000;
			minutes = elapsedTimeInSeconds / 60;
			seconds = elapsedTimeInSeconds - (minutes * 60);
			milliSeconds = milliSeconds - (elapsedTimeInSeconds * 1000);
			logger.info( "ldap sync timing: ----------> Time to update group membership " + minutes + " minutes " + seconds + " seconds " + milliSeconds + " milliseconds " );
		}

		ProfileModule profileMod;
		Object tmp;
		Group group = null;
		
		// Get the Group object from the group id.
		try
		{
			profileMod = getProfileModule();
			tmp = (Object) profileMod.getEntry( groupId );
			if ( tmp instanceof Group )
			{
				group = (Group) tmp; 
			}
		}
		catch ( Exception ex )
		{
			logger.info( "In updateMembership(), call to profileModule.getEntry() failed for group id: " + groupId );
			return;
		}

		// Add this group to the list of sync results if the group membership changed.
		if ( syncResults != null && (newM.size() > 0 || remM.size() > 0) && group != null )
		{
			String	groupName;
			
			groupName = group.getName();
			syncResults.addResult( groupName + " (" + group.getForeignName() + ")" );
		}

		if ( m_showTiming )
		{
			Date now;

			now = new Date();
			startTime = now.getTime();
		}

		// Get a list of all the principals that were added or removed from the group.
		if ( (newM != null && newM.isEmpty() == false) || (remM != null && remM.isEmpty() == false) )  
        {
			ArrayList<Long> usersRemovedFromGroup;
			ArrayList<Long> groupsRemovedFromGroup;
			ArrayList<Long> usersAddedToGroup;
			ArrayList<Long> groupsAddedToGroup;
			
			usersRemovedFromGroup = new ArrayList<Long>();
			groupsRemovedFromGroup = new ArrayList<Long>();
			usersAddedToGroup = new ArrayList<Long>();
			groupsAddedToGroup = new ArrayList<Long>();

			// Add the principals that were added to the group.
			if ( newM != null && newM.isEmpty() == false )
			{
				Iterator iter;
				
				iter = newM.iterator();
				while ( iter.hasNext() )
				{
					Membership nextMembership;
					Long principalId;
					
					nextMembership = (Membership) iter.next();
					principalId = nextMembership.getUserId();
					
			    	if ( logger.isDebugEnabled() )
					{
						Long nextGroupId;
						Long nextUserId;
						
						nextGroupId = nextMembership.getGroupId();
						nextUserId = nextMembership.getUserId();
						logger.debug( "In updateMembership(), next principal added to group, nextGroupId: " + String.valueOf( nextGroupId ) + "  nextUserId: " + String.valueOf( nextUserId ) );
					}

					if ( principalId != null )
					{
						Principal nextPrincipal = null;

						try
						{
							nextPrincipal = getProfileModule().getEntry( principalId );
						}
						catch ( Exception ex )
						{
							logger.info( "In updateMembership(), call to profileModule.getEntry() failed for group member: " + principalId );
						}
						
						if ( nextPrincipal != null )
						{
							// Add this principal to the list of principals to be re-indexed.
							m_principalsToIndex.add( principalId );
							
							// Keep track of the principals that were added to this group.
							if ( (nextPrincipal instanceof UserPrincipal) || (nextPrincipal instanceof User) )
								usersAddedToGroup.add( principalId );
							else if ( (nextPrincipal instanceof GroupPrincipal) || (nextPrincipal instanceof Group) )
								groupsAddedToGroup.add( principalId );
						}
					}
				}
			}

			// Add the principals that were removed from the group.
			if ( remM != null && remM.isEmpty() == false )
			{
				Iterator iter;
				
				iter = remM.iterator();
				while ( iter.hasNext() )
				{
					Membership nextMembership;
					Long principalId;
					
					nextMembership = (Membership) iter.next();
					principalId = nextMembership.getUserId();
					
			    	if ( logger.isDebugEnabled() )
					{
						Long nextGroupId;
						Long nextUserId;
						
						nextGroupId = nextMembership.getGroupId();
						nextUserId = nextMembership.getUserId();
						logger.info( "In updateMemberhsip() next principal removed from group, nextGroupId: " + String.valueOf( nextGroupId ) + "  nextUserId: " + String.valueOf( nextUserId ) );
					}

					if ( principalId != null )
					{
						Principal nextPrincipal = null;

						try
						{
							nextPrincipal = getProfileModule().getEntry( principalId );
						}
						catch ( Exception ex )
						{
							logger.info( "In updateMembership(), profileModule.getEntry() failed for group member: " + principalId );
						}
						
						if ( nextPrincipal != null )
						{
							// Add this principal to the list of principals to be re-indexed.
							m_principalsToIndex.add( principalId );
							
							// Keep track of the principals that were removed from this group.
							if ( (nextPrincipal instanceof UserPrincipal) || (nextPrincipal instanceof User) )
								usersRemovedFromGroup.add( principalId );
							else if ( (nextPrincipal instanceof GroupPrincipal) || (nextPrincipal instanceof Group) )
								groupsRemovedFromGroup.add( principalId );
						}
					}
				}
			}
			
			// Update the disk quotas and file size limits for users and groups that were
			// added or removed from the group.
			if ( group != null )
			{
				Utils.updateDiskQuotasAndFileSizeLimits(
						getProfileModule(),
						group,
						usersAddedToGroup,
						usersRemovedFromGroup,
						groupsAddedToGroup,
						groupsRemovedFromGroup );
			}

			if ( m_showTiming )
			{
				long elapsedTimeInSeconds;
				long minutes;
				long seconds;
				long milliSeconds;
				Date now;
				
				now = new Date();

				milliSeconds = now.getTime() - startTime;
				elapsedTimeInSeconds = milliSeconds / 1000;
				minutes = elapsedTimeInSeconds / 60;
				seconds = elapsedTimeInSeconds - (minutes * 60);
				milliSeconds = milliSeconds - (elapsedTimeInSeconds * 1000);
				logger.info( "ldap sync timing: ----------> Time to update disk quotas: " + minutes + " minutes " + seconds + " seconds " + milliSeconds + " milliseconds " );
			}
        }
    }

	/**
	 * Create a Net folder server needed by the user's home directory
	 */
	private void createHomeDirNetFolderServer(
		HomeDirInfo homeDirInfo ) throws AccessControlException
	{
		// Are we running Filr?
		if ( homeDirInfo == null || Utils.checkIfFilr() == false )
		{
			// No,
			return;
		}

		try
		{
			String server;
			String vol;
			
			// Have we already created a net folder server for this server/vol?
			server = homeDirInfo.getServerAddr();
			vol = homeDirInfo.getVolume();
			if ( server != null && vol != null )
			{
				String key;
				
				key = server + "/" + vol;
				if ( m_server_vol_map.get( key ) == null )
				{
					ResourceDriverConfig rdConfig;
					
					// No, create it.
					rdConfig = NetFolderHelper.createHomeDirNetFolderServer(
																		getProfileModule(),
																		getAdminModule(),
																		getResourceDriverModule(),
																		homeDirInfo );
					
					m_server_vol_map.put( key, rdConfig );
				}
			}
			
		}
		catch ( Exception ex )
		{
			logger.info( "Unable to create the home directory net folder server for: " + homeDirInfo.getServerAddr() + " ", ex );
		}
	}
	
    /**
     * For each user in the list, create a net folder server for the user's home directory
     */
    private void createHomeDirNetFolderServers(
    	List listOfUsers,
    	Map<String, HomeDirInfo> homeDirInfoMap )
    {
    	boolean createHomeDirNetFolders = false;
    	boolean createFakeHomeDirNetFolders = false;
    	
    	if ( listOfUsers == null || homeDirInfoMap == null || Utils.checkIfFilr() == false )
    		return;
    	
		// See if we should create the home dir net folder.
		createHomeDirNetFolders = SPropsUtil.getBoolean( "ldap.create.home.dir.net.folders", false );
		
		// For testing purposes should we create a fake home dir net folder?
		createFakeHomeDirNetFolders = SPropsUtil.getBoolean( "ldap.create.fake.home.dir.net.folders", false );

		for ( Object nextObj : listOfUsers )
		{
			if ( nextObj instanceof UserPrincipal )
			{
				UserPrincipal nextUser;

				nextUser = (UserPrincipal) nextObj;

				try 
				{
					HomeDirInfo homeDirInfo = null;
					String userName;
					
					// Get the HomeDirInfo for this user.
					userName = nextUser.getName().toLowerCase();
					homeDirInfo = homeDirInfoMap.get( userName );
					
					if ( homeDirInfo != null )
					{
						createHomeDirNetFolderServer( homeDirInfo );
						
						if ( createHomeDirNetFolders )
							createHomeDirNetFolder( homeDirInfo, nextUser );
					}
					else if ( createFakeHomeDirNetFolders )
					{
						homeDirInfo = new HomeDirInfo();
						
						homeDirInfo.setServerAddr( SPropsUtil.getString( "ldap.home.dir.net.folder.server.addr", "fake.server" ) );
						homeDirInfo.setVolume( SPropsUtil.getString( "ldap.home.dir.net.folder.server.vol", "fakeVol" ) );
						homeDirInfo.setPath( SPropsUtil.getString( "ldap.home.dir.net.folder.relative.path", "fakeHome" ) + "/" + userName );

						createHomeDirNetFolder( homeDirInfo, nextUser );
					}
				} 
				catch ( AccessControlException acEx )
				{
					logger.error( "Unable to create home dir net folder server for user: " + nextUser.getName());
				}
			}
		}
    }
    
	/**
	 * Create a Net folder and if needed a net folder server that represents the
	 * user's home directory.
	 */
	private void createHomeDirNetFolder(
		HomeDirInfo homeDirInfo,
		UserPrincipal userPrincipal ) throws AccessControlException
	{
		User user;
		String userName;

		// Are we running Filr?
		if ( homeDirInfo == null || userPrincipal == null || Utils.checkIfFilr() == false )
		{
			// No,
			return;
		}

		userName = userPrincipal.getName();
		user = profileModule.getUser( userName );

		try
		{
			NetFolderHelper.createHomeDirNetFolder(
											getProfileModule(),
											getTemplateModule(),
											getBinderModule(),
											getFolderModule(),
											getNetFolderModule(),
											getAdminModule(),
											getResourceDriverModule(),
											null,
											homeDirInfo,
											user);
		}
		catch ( Exception ex )
		{
			logger.info( "Unable to create the home directory net folder for user: " + user.getTitle() + " ", ex );
		}
	}



    /**
     * Create users.  
     * @param zoneName
     * @param users - Map keyed by user id, value is map of attributes
     * @return
     */
    protected List<User> createUsers(
    	Long zoneId,
    	Map<String, Map> users,
    	Map<String, HomeDirInfo> homeDirInfoMap,
    	LdapSyncMode syncMode,
    	PartialLdapSyncResults syncResults,
    	boolean createAsExternal ) 
    {
		//SimpleProfiler.setProfiler(new SimpleProfiler(false));
		ProfileCoreProcessor processor;
		long createUsersStartTime = 0;
		
		// Are we in "preview" mode?
		if ( syncMode == LdapSyncMode.PREVIEW_ONLY && syncResults != null )
		{
			// Yes
			addPrincipalsToPreviewSyncResults( users, syncResults );
			
			return new ArrayList<User>();
		}
		
		if ( m_showTiming )
		{
			Date now;
			
			now = new Date();
			createUsersStartTime = now.getTime();
		}
		
		ProfileBinder pf = getProfileDao().getProfileBinder(zoneId);
		List newUsers = new ArrayList();
		for (Iterator i=users.values().iterator(); i.hasNext();) {
			Map attrs = (Map)i.next();

			newUsers.add(new MapInputData(StringCheckUtil.check(attrs)));
		}
		//get default definition to use
		Definition userDef = pf.getDefaultEntryDef();		
		if (userDef == null) {
			User temp = new User(new IdentityInfo());
			getDefinitionModule().setDefaultEntryDefinition(temp);
			userDef = getDefinitionModule().getDefinition(temp.getEntryDefId());
		}

		processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
            																pf,
            																ProfileCoreProcessor.PROCESSOR_KEY );
		try 
		{
			// Try to create all of the users at once.
			newUsers = processor.syncNewEntries( pf, userDef, User.class, newUsers, null, syncResults, new IdentityInfo( ( ! createAsExternal ), true, false, false ) );    

			// Are we running Filr?
			if ( newUsers != null && Utils.checkIfFilr() )
			{
				// Yes
				// For each user we just created, create a net folder server for the
				// user's home directory
				createHomeDirNetFolderServers( newUsers, homeDirInfoMap );
			}

			if ( m_showTiming )
			{
				m_numUsersCreated += newUsers.size();
				if ( newUsers.size() > 0 )
				{
					long elapsedTimeInSeconds;
					long minutes;
					long seconds;
					Date now;
					
					now = new Date();

					elapsedTimeInSeconds = now.getTime() - createUsersStartTime;
					elapsedTimeInSeconds /= 1000;
					minutes = elapsedTimeInSeconds / 60;
					seconds = elapsedTimeInSeconds - (minutes * 60);
					logger.info( "ldap sync timing: ----------> Time to create last " + newUsers.size() + " users: " + minutes + " minutes " + seconds + " seconds" );
					
					if ( (m_numUsersCreated % 500) == 0 )
					{
						elapsedTimeInSeconds = now.getTime() - m_createUsersStartTime;
						elapsedTimeInSeconds /= 1000;
						minutes = elapsedTimeInSeconds / 60;
						seconds = elapsedTimeInSeconds - (minutes * 60);
						logger.info( "ldap sync timing: ----------> Time taken to create " + m_numUsersCreated + " users: " + minutes + " minutes " + seconds + " seconds" );
					}
				}
			}

			if(logger.isDebugEnabled())
				logger.debug("Applying index changes");
			
			IndexSynchronizationManager.applyChanges();  //apply now, syncNewEntries will commit
			//SimpleProfiler.printProfiler();
		   	//SimpleProfiler.clearProfiler();
		}
		catch ( Exception ex )
		{
			List nextUser;
			
			nextUser = new ArrayList();

			// An error happened trying to create one of the users in the batch.  Log the error
			logger.error( "An error occurred attempting to create a batch of users: ", ex );
		
			newUsers.clear();
			
			// Try to create the users one at a time.
			for (Iterator i=users.values().iterator(); i.hasNext();) 
			{
				String userName;
				Map attrs;
				
				attrs = (Map)i.next();
				userName = (String) attrs.get( ObjectKeys.FIELD_PRINCIPAL_NAME );

				try
				{
					// Try to create the next user in the list.
					logger.info( "2nd attempt to create the user: " + userName );
					nextUser.clear();
					nextUser.add( new MapInputData(StringCheckUtil.check( attrs ) ) );
					nextUser = processor.syncNewEntries( pf, userDef, User.class, nextUser, null, syncResults, new IdentityInfo( ( ! createAsExternal ), true, false, false ) );    
					
					if ( nextUser != null && nextUser.size() == 1 )
					{
						// Remember the user that was just created.
						newUsers.add( nextUser.get( 0 ) );
					}
				}
				catch ( Exception except )
				{
					logger.error( "An error occurred trying to create the user the 2nd time: " + userName );
				}
			}
			
			// Are we running Filr?
			if ( newUsers != null && Utils.checkIfFilr() )
			{
				// Yes
				// For each user we just created, create a net folder serverfor the
				// user's home directory
				createHomeDirNetFolderServers( newUsers, homeDirInfoMap );
			}
		}
		
	   	return newUsers;
     }

    /**
     * Create groups.
     * 
     * @param zoneName
     * @param groups - Map keyed by user id, value is map of attributes
     * @param createAsExternal
     * 
     * @return
     */
    protected List<Group> createGroups( Binder zone, Map<String, Map> groups, PartialLdapSyncResults syncResults, boolean createAsExternal )
    {
		ProfileBinder pf = getProfileDao().getProfileBinder(zone.getZoneId());
		List newGroups = new ArrayList();
		for (Iterator i=groups.values().iterator(); i.hasNext();) {
			newGroups.add(new MapInputData(StringCheckUtil.check((Map)i.next())));
		}
		//get default definition to use
		Group temp = new Group(new IdentityInfo());
		getDefinitionModule().setDefaultEntryDefinition(temp);
		Definition groupDef = getDefinitionModule().getDefinition(temp.getEntryDefId());

	    try {
	    	ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
            	pf, ProfileCoreProcessor.PROCESSOR_KEY);
	    	newGroups = processor.syncNewEntries( pf, groupDef, Group.class, newGroups, null, syncResults, new IdentityInfo( ( ! createAsExternal ), true, false, false ) );
	    	IndexSynchronizationManager.applyChanges(); //apply now, syncNewEntries will commit
		} catch (Exception ex) {
			logError("Error adding groups", ex);
			for (Map.Entry<String, Map> me:groups.entrySet()) {
				logger.error("'" + me.getKey() + "':'" + me.getValue().get(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME) + "'");
			}
			//continue, returning empty list
			newGroups.clear();
		}
    	return newGroups;
	    	
    }
     public void deletePrincipals(
    	Long zoneId,
    	Collection ids,
    	boolean deleteWS,
    	LdapSyncMode syncMode,
    	PartialLdapSyncResults syncResults )
     {
    	 long startTime;
		Map options = new HashMap();
		options.put(ObjectKeys.INPUT_OPTION_DELETE_USER_WORKSPACE, Boolean.valueOf(deleteWS));
		
		if ( Utils.checkIfFilr() )
			options.put( ObjectKeys.INPUT_OPTION_DELETE_MIRRORED_FOLDER_SOURCE, Boolean.FALSE );
		else
			options.put( ObjectKeys.INPUT_OPTION_DELETE_MIRRORED_FOLDER_SOURCE, Boolean.TRUE );

		startTime = (new Date()).getTime();
		int count = 0;
		for (Iterator iter=ids.iterator(); iter.hasNext();) {
    		Long id = (Long)iter.next();
    		try {
    			String name = null;
    			if(syncResults != null) {
    				// Try obtaining the current state of the principal about to be deleted, and use  
    				// it to report the status back to user. This information must be obtained BEFORE
    				// the entry gets deleted(to be precise, marked as deleted), because many of the
    				// properties of the principal object (eg. name) get altered during the operation.
    				try {
    					Principal p = (Principal) getProfileModule().getEntry(id);
    					name = p.getName() + " (" + p.getForeignName() + ")";
    					
    					logger.info( "--> Deleting: " + p.getName() );
    				}
    				catch(Exception e) {
    					// Don't report problem here. Instead, let the next step - deleteEntry() - report it.
    				}
    			}

    			// Should we actually do the delete?
    			if ( syncMode == LdapSyncMode.PERFORM_SYNC )
    			{
    				// Yes
        			getProfileModule().deleteEntry(id, options, true);
        			
        			count++;
        			
        			if ( ((count % 100) == 0) )
        			{
						// clear cache to prevent thrashing resulted from prolonged use of a single session
						getCoreDao().clear();

						if ( m_showTiming )
    					{
    						long elapsedTimeInSeconds;
    						long minutes;
    						long seconds;
    						long milliSeconds;
    						Date now;
    						
    						now = new Date();

    						milliSeconds = now.getTime() - startTime;
    						elapsedTimeInSeconds = milliSeconds / 1000;
    						minutes = elapsedTimeInSeconds / 60;
    						seconds = elapsedTimeInSeconds - (minutes * 60);
    						milliSeconds = milliSeconds - (elapsedTimeInSeconds * 1000);
    						logger.info( "ldap sync timing: ======> Time to delete last 100 users: " + minutes + " minutes " + seconds + " seconds " + milliSeconds + " milliseconds " );
    						
    						startTime = now.getTime();
    					}
        			}
    			}
    			
    			if ( syncResults != null && name != null )
    				syncResults.addResult( name );
    			
    		} catch (Exception ex) {
    			logError(NLT.get("errorcode.ldap.delete", new Object[]{id.toString()}), ex);
    		}
    	}
		if(count > 0) {
			getProfileModule().deleteEntryFinish();
		}
		IndexSynchronizationManager.applyChanges();
    }

 	@Override
 	public List<LdapConnectionConfig> getConfigsReadOnlyCache(Long zoneId) {
 		List<LdapConnectionConfig> configs = readOnlyCache.get(zoneId);	
		if(configs == null) {
			// We don't need synchronization on this, since multiple threads executing
			// this code at the same time won't result in data integrity issue. It will
			// lose some efficiency, but that's better than having to synchronize this
			// method which is called infrequently.
			configs = getCoreDao().loadLdapConnectionConfigs( zoneId );
			getCoreDao().evict(configs);
			readOnlyCache.put(zoneId, configs);
		}
		return configs;
 	}

 	@Override
 	public void setConfigsReadOnlyCache(Long zoneId,
 			List<LdapConnectionConfig> configs) {
 		readOnlyCache.put(zoneId, configs);
 	}

 	@Override
 	public LdapConnectionConfig getConfigReadOnlyCache(Long zoneId, String configId) {
 		List<LdapConnectionConfig> configs = getConfigsReadOnlyCache(zoneId);
 		if(configs == null)
 			return null;
 		for(LdapConnectionConfig config:configs) {
 			if(configId.equals(config.getId()))
 				return config;
 		}
 		return null;
 	}

    private WorkspaceModule getWorkspaceModule() {
        WorkspaceModule workspaceModule;
        workspaceModule = (WorkspaceModule) SpringContextUtil.getBean("workspaceModule");
        return workspaceModule;
    }
}

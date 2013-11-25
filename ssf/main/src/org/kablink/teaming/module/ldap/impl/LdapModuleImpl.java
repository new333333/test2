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

package org.kablink.teaming.module.ldap.impl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.hibernate.SessionFactory;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.dao.util.FilterControls;
import org.kablink.teaming.dao.util.ObjectControls;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.GroupPrincipal;
import org.kablink.teaming.domain.LdapConnectionConfig;
import org.kablink.teaming.domain.LdapSyncException;
import org.kablink.teaming.domain.Membership;
import org.kablink.teaming.domain.NoPrincipalByTheNameException;
import org.kablink.teaming.domain.NoUserByTheNameException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.jobs.LdapSynchronization;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.ldap.LdapModule;
import org.kablink.teaming.module.ldap.LdapSchedule;
import org.kablink.teaming.module.ldap.LdapSyncResults;
import org.kablink.teaming.module.ldap.LdapSyncResults.PartialLdapSyncResults;
import org.kablink.teaming.module.ldap.LdapSyncResults.SyncStatus;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.profile.processor.ProfileCoreProcessor;
import org.kablink.teaming.module.shared.MapInputData;
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
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.util.stringcheck.StringCheckUtil;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.GetterUtil;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * This implementing class utilizes transactional demarcation strategies that 
 * are finer granularity than typical module implementations, in an effort to
 * avoid lengthy transaction duration that could have occured if the ldap
 * database is large and many updates are made during the transaction. To support that,
 * the public methods exposed by this implementation are not transaction 
 * demarcated. Instead, this implementation uses helper methods 
 * for the part of the operations that might
 * require interaction with the database (that is, those operations that 
 * change the state of one or more domain objects) and put the transactional 
 * support around those methods hence reducing individual transaction duration.
 * Of course, this finer granularity transactional control will be of no effect
 * if the caller of this service was already transactional (i.e., it controls
 * transaction boundary that is more coarse). Whenever possible, this practise 
 * is discouraged for obvious performance/scalability reasons.  
 *   
 * @author Janet McCann
 *
 */
public class LdapModuleImpl extends CommonDependencyInjection implements LdapModule {
	protected Log logger = LogFactory.getLog(getClass());
	
	protected String [] principalAttrs = new String[]{
												ObjectKeys.FIELD_PRINCIPAL_NAME,
												ObjectKeys.FIELD_ID,
												ObjectKeys.FIELD_PRINCIPAL_DISABLED, 
												ObjectKeys.FIELD_INTERNALID,
												ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME,
												ObjectKeys.FIELD_PRINCIPAL_LDAPGUID};

	// The following constants are indexes into the array of Teaming attribute values
	// that are returned from the loadObjects() call.  The value of these constants
	// match the order of the attribute names found in principalAttrs.
	private static final int PRINCIPAL_NAME = 0;
	private static final int PRINCIPAL_ID = 1;
	private static final int PRINCIPAL_DISABLED = 2;
	private static final int PRINCIPAL_INTERNALID = 3;
	private static final int PRINCIPAL_FOREIGN_NAME = 4;
	private static final int PRINCIPAL_LDAP_GUID = 5;
	
	// An ldap sync for a zone should not be started while another ldap sync is running.
	private static Hashtable<Long, Boolean> m_zoneSyncInProgressMap = new Hashtable(); 
	
	protected static final String[] sample = new String[0];
	HashMap defaultProps = new HashMap(); 
	protected HashMap zones = new HashMap();
	protected TransactionTemplate transactionTemplate;
	protected ProfileModule profileModule;
	protected DefinitionModule definitionModule;
	protected SessionFactory sessionFactory;
	protected static String GROUP_ATTRIBUTES="groupAttributes";
	protected static String MEMBER_ATTRIBUTES="memberAttributes";
	protected static String SYNC_JOB="ldap.job"; //properties in xml file need a unique name
	public LdapModuleImpl () {
		defaultProps.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		defaultProps.put(Context.SECURITY_AUTHENTICATION, "simple");
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
	 * Execute the given ldap query.  For each user found by the query, if the user already exists
	 * in Vibe then add them to the list.
	 * @author jwootton
	 */
	public HashSet<Long> getDynamicGroupMembers( String baseDn, String filter, boolean searchSubtree ) throws LdapSyncException
	{
		HashSet<Long> listOfMembers;
		
		listOfMembers = new HashSet<Long>();
		
		// Does the membership criteria have a filter?
		if ( filter != null && filter.length() > 0 )
		{
			List<LdapConnectionConfig> ldapConnectionConfigs;
			Workspace zone;
			Long zoneId;
			ProfileModule profileModule;

			// Yes
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
			   		NamingException namingEx;

					// Yes
			   		namingEx = null;
			   		ldapContext = null;
			  		try
			  		{
						String[] userAttributeNames = {};
						int scope;
						SearchControls searchControls;
						NamingEnumeration searchCtx;

						// Get an ldap context for the given ldap configuration
						ldapContext = getUserContext( zoneId, nextLdapConfig );
						
						if ( searchSubtree )
							scope = SearchControls.SUBTREE_SCOPE;
						else
							scope = SearchControls.ONELEVEL_SCOPE;
						
						searchControls = new SearchControls( scope, 0, 0, userAttributeNames, false, false );

						// Execute the ldap search using the membership criteria
						searchCtx = ldapContext.search( baseDn, filter, searchControls );
						
						// Go through the list of users/groups found by the search.  If the
						// user/group already exists in Vibe then add them to the list we
						// will return.
						while ( searchCtx.hasMore() )
						{
							Binding binding;
							Attributes lAttrs = null;
							String[] ldapAttributesToRead = { ldapGuidAttribute };
							String guid;
							User user;

							// Get the next user/group in the list.
							binding = (Binding)searchCtx.next();

							// Read the guid for this user/group from the ldap directory.
							lAttrs = ldapContext.getAttributes( binding.getNameInNamespace(), ldapAttributesToRead );
							guid = getLdapGuid( lAttrs, ldapGuidAttribute );

							// Does this user exist in Vibe.
							try
							{
								user = profileModule.findUserByLdapGuid( guid );
								if ( user != null )
								{
									// Yes, add them to the membership list.
									listOfMembers.add( user.getId() );
								}
							}
							catch ( NoUserByTheNameException ex )
							{
								// Nothing to do
							}
						}
					}
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
			  			LdapSyncException	ldapSyncEx;

			  			// Yes
			  			logError( NLT.get( "errorcode.ldap.context" ), namingEx );
			  			
			  			// Create an LdapSyncException and throw it.  We throw an LdapSyncException so we can return
			  			// the LdapConnectionConfig object that was being used when the error happened.
			  			ldapSyncEx = new LdapSyncException( nextLdapConfig, namingEx );
			  			throw ldapSyncEx;
			  		}
				}
			}
		}

		return listOfMembers;
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
    	return (LdapSynchronization)ReflectHelper.getInstance(org.kablink.teaming.jobs.DefaultLdapSynchronization.class);		   		
 
    }// end getSyncObject()	
	
	protected LdapSynchronization getSyncObject()
	{
		return getSyncObject( RequestContextHolder.getRequestContext().getZoneName() );
    }	
	
    private void logError(String msg, Exception e) {
    	logger.error(msg + ": " + e.toString());
    	if(e.getCause() != null)
    		logger.error(e.getCause().toString());
    	// print stack dump only if debug logging is enabled
    	if(logger.isDebugEnabled())
    		logger.error("", e);
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
		ldapConnectionConfigs = getCoreDao().loadLdapConnectionConfigs( zoneId );
		
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
     * name of the attribute by calling config.getLdapGuidAttribute().  For eDirectory, the name of
     * the attribute is GUID and for Activie Directory, the name of the attribute is objectGUID.
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
		ProfileDao profileDao;
		PartialLdapSyncResults modifiedUsersSyncResults;
		String[] ldapAttributesToRead;
		String ldapGuidAttribute;

		logger.info( "In syncGuidAttributeForAllUsers()" );
		// usersToUpdate will hold the users that need to be updated and the attributes to update.
		// The key is the user's id and the value is the map of attributes.
		usersToUpdate = new HashMap();

		profileDao = getProfileDao();
		
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
					while ( ctxSearch.hasMore() )
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
						// "_jobProcessingAgent" and "_synchronizationAgent".
						if ( MiscUtil.isSystemUserAccount( teamingName ) )
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
								updateUsers( zoneId, usersToUpdate, modifiedUsersSyncResults );
								logger.info( "back from updateUsers()" );
								
								usersToUpdate.clear();
							}
						}
					}// end while()
				}// end try
		  		catch (Exception ex)
		  		{
		  			logError( NLT.get( "errorcode.ldap.context" ), ex );
		  		}
			}
		}// end for()

		// Update the users with the guid from the ldap directory.
		logger.info( "about to call updateUsers()" );
		updateUsers( zoneId, usersToUpdate, modifiedUsersSyncResults );
		logger.info( "back from updateUsers()" );
		
    }// end syncGuidAttributeForAllUsers()
    
    
    /**
     * For all groups, sync the ldap attribute that holds the guid.  The name of the ldap
     * attribute that holds the guid is found in the ldap configuration data.  You can get the
     * name of the attribute by calling config.getLdapGuidAttribute().  For eDirectory, the name of
     * the attribute is GUID and for Activie Directory, the name of the attribute is objectGUID.
     * This method should be called whenever the user changes the the name of the ldap attribute
     * that holds the guid (in the ldap configuration).
     */
    public void syncGuidAttributeForAllGroups( LdapConnectionConfig ldapConfig, LdapContext ldapContext, LdapSyncResults syncResults ) throws LdapSyncException
    {
		Workspace zone;
		Long zoneId;
		ProfileDao profileDao;
		PartialLdapSyncResults modifiedUsersSyncResults;
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
					while ( ctxSearch.hasMore() )
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
						// "_jobProcessingAgent" and "_synchronizationAgent".
						if ( MiscUtil.isSystemUserAccount( fullDN ) )
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
							updateGroup( zoneId, principal.getId(), userMods, syncResults );
						}
						catch (NoPrincipalByTheNameException ex)
						{
							// Nothing to do, this just means the group doesn't exist.
						}
					}// end while()
				}// end try
		  		catch (Exception ex)
		  		{
		  			logError( NLT.get( "errorcode.ldap.context" ), ex );
		  		}
			}
		}// end for()
    }// end syncGuidAttributeForAllGroups()
    
    
    /**
     * For all users and groups, sync the ldap attribute that holds the guid.  The name of the ldap
     * attribute that holds the guid is found in the ldap configuration data.  You can get the
     * name of the attribute by calling config.getLdapGuidAttribute().  For eDirectory, the name of
     * the attribute is GUID and for Activie Directory, the name of the attribute is objectGUID.
     * This method should be called whenever the user changes the the name of the ldap attribute
     * that holds the guid (in the ldap configuration).
     */
    public void syncGuidAttributeForAllUsersAndGroups( LdapSyncResults syncResults ) throws LdapSyncException
    {
		Workspace zone;
		Long zoneId;
		List<LdapConnectionConfig> ldapConnectionConfigs;
		List userList;
		Map<String, Object[]> userMap;
		ObjectControls objCtrls;
		FilterControls filterCtrls;
		int i;
		
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
			
			// If this user is not disabled and is not one of the system users, add the
			// user to the map.
			if ( ((Boolean)attributeValues[PRINCIPAL_DISABLED] == Boolean.FALSE) && (Validator.isNull( (String)attributeValues[this.PRINCIPAL_INTERNALID])) )
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
	  			LdapSyncException	ldapSyncEx;

	  			// Yes
	  			logError( NLT.get( "errorcode.ldap.context" ), namingEx );
	  			
	  			// Create an LdapSyncException and throw it.  We throw an LdapSyncException so we can return
	  			// the LdapConnectionConfig object that was being used when the error happened.
	  			ldapSyncEx = new LdapSyncException( nextLdapConfig, namingEx );
	  			throw ldapSyncEx;
	  		}

		}// end for()
    }// end syncGuidAttributeForAllUsersAndGroups()
    
    
    /**
     * If the ldap configuration has the name of the ldap attribute that holds the guid then
     * read the ldap guid from the ldap directory.
     * @param userName
     * @return
     */
    public String readLdapGuidFromDirectory( String userName, Long zoneId )
    {
		LdapSchedule schedule;
		String zoneName;
		ZoneInfo zoneInfo;

		zoneInfo = getZoneModule().getZoneInfo( zoneId );
		zoneName = zoneInfo.getZoneName();
		
		schedule = new LdapSchedule( getSyncObject( zoneName ).getScheduleInfo( zoneId ) );
		for(LdapConnectionConfig config : getCoreDao().loadLdapConnectionConfigs( zoneId ))
		{
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
					continue;
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
						if (!ctxSearch.hasMore() )
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
			}
		}// end for()
		
		// If we get here we either didn't find the user or we didn't read the ldap guid.
		return null;
    }// end readLdapGuidFromDirectory()
    
    
	/**
	 * Update a ssf user with an ldap person.  
	 * @param zoneName
	 * @param loginName
	 * @throws NoUserByTheNameException
	 * @throws NamingException
	 */
	public void syncUser( String teamingUserName, String ldapUserName ) 
		throws NoUserByTheNameException, NamingException
	{
		if(logger.isDebugEnabled())
			logger.debug("Synching LDAP user '" + ldapUserName + "' to Vibe user '" + teamingUserName + "'");
		
		Workspace zone = RequestContextHolder.getRequestContext().getZone();

		LdapSchedule schedule = new LdapSchedule(getSyncObject().getScheduleInfo(zone.getId()));
		Map mods = new HashMap();
		for(LdapConnectionConfig config : getCoreDao().loadLdapConnectionConfigs(zone.getZoneId())) {
			LdapContext ctx = getUserContext(zone.getId(), config);
			String dn=null;
			Map userAttributes = config.getMappings();
			String [] userAttributeNames = 	(String[])(userAttributes.keySet().toArray(sample));
	
			for(LdapConnectionConfig.SearchInfo searchInfo : config.getUserSearches()) {
				try {
					String[] attributesToRead;
					Attributes lAttrs;

					int scope = (searchInfo.isSearchSubtree()?SearchControls.SUBTREE_SCOPE:SearchControls.ONELEVEL_SCOPE);
					SearchControls sch = new SearchControls(scope, 1, 0, userAttributeNames, false, false);
		
					String search = "(" + config.getUserIdAttribute() + "=" + ldapUserName + ")";
					String filter = searchInfo.getFilterWithoutCRLF();
					if(!Validator.isNull(filter)) {
						search = "(&"+search+filter+")";
					}
					NamingEnumeration ctxSearch = ctx.search(searchInfo.getBaseDn(), search, sch);
					if (!ctxSearch.hasMore()) {
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
					mods.put(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, dn);
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

				updateUser(zone, teamingUserName, mods);
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
	 * Update a ssf user with an ldap person.  
	 * @param zoneName
	 * @param loginName
	 * @throws NoUserByTheNameException
	 * @throws NamingException
	 */
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
	 * This routine alters group membership without updating the local caches.
	 * Need to flush cache after use
	 */
	public void syncAll(
		boolean syncUsersAndGroups,
		boolean syncGuids,
		LdapSyncResults syncResults ) throws LdapSyncException
	{
		Workspace zone = RequestContextHolder.getRequestContext().getZone();
		Boolean syncInProgress;

		// Is an ldap sync currently going on for the zone?
		syncInProgress = m_zoneSyncInProgressMap.get( zone.getId() );
		if ( syncInProgress != null && syncInProgress )
		{
			// Yes, don't start another sync.
	    	logger.error( "Unable to start ldap sync because an ldap sync is currently running" );
	    	syncResults.setStatus( SyncStatus.STATUS_SYNC_ALREADY_IN_PROGRESS );
			return;
		}
		
		try
		{
			m_zoneSyncInProgressMap.put( zone.getId(), Boolean.TRUE );
			

			// Sync guids if called for.
			if ( syncGuids == true )
			{
				logger.info( "about to call syncGuidAttributeForAllUsersAndGroups()" );
				syncGuidAttributeForAllUsersAndGroups( syncResults );
				logger.info( "back from syncGuidAttributeForAllUsersAndGroups()" );
			}
			
			// If we don't need to sync users and groups then bail.
			if ( syncUsersAndGroups == false )
				return;
			
			LdapSchedule info = new LdapSchedule(getSyncObject().getScheduleInfo(zone.getId()));
	    	UserCoordinator userCoordinator = new UserCoordinator(zone,info.isUserSync(),info.isUserRegister(),
	   															  info.isUserDelete(), info.isUserWorkspaceDelete(), syncResults );
			for(LdapConnectionConfig config : getCoreDao().loadLdapConnectionConfigs(zone.getId())) {
		   		LdapContext ctx=null;
		  		try {
					ctx = getUserContext(zone.getId(), config);
					logger.info( "ldap url used to search for users: " + config.getUrl() );
					
					syncUsers(zone, ctx, config, userCoordinator);
				}
		  		catch (NamingException namingEx)
		  		{
		  			logError(NLT.get("errorcode.ldap.context"), namingEx);
		  			
		  			LdapSyncException	ldapSyncEx;
		  			
		  			// Create an LdapSyncException and throw it.  We throw an LdapSyncException so we can return
		  			// the LdapConnectionConfig object that was being used when the error happened.
		  			ldapSyncEx = new LdapSyncException( config, namingEx );
		  			throw ldapSyncEx;
		  		}
		  		finally {
					if (ctx != null) {
						try
						{
							ctx.close();
						}
						catch (NamingException namingEx)
				  		{
				  			LdapSyncException	ldapSyncEx;
				  			
				  			// Create an LdapSyncException and throw it.  We throw an LdapSyncException so we can return
				  			// the LdapConnectionConfig object that was being used when the error happened.
				  			ldapSyncEx = new LdapSyncException( config, namingEx );
				  			throw ldapSyncEx;
				  		}
					}
				}
			}
			logger.info( "Finished syncUsers()" );
			Map dnUsers = userCoordinator.wrapUp();
			logger.info( "Finished userCoordinator.wrapUp()" );
	
	   		GroupCoordinator groupCoordinator = new GroupCoordinator(zone, dnUsers, info.isGroupSync(), info.isGroupRegister(), info.isGroupDelete(), syncResults );
	   		for(LdapConnectionConfig config : getCoreDao().loadLdapConnectionConfigs(zone.getId())) {
		   		LdapContext ctx=null;
		  		try {
					ctx = getGroupContext(zone.getId(), config);
					logger.info( "ldap url used to search for groups: " + config.getUrl() );
					syncGroups(zone, ctx, config, groupCoordinator, info.isMembershipSync());
				}
		  		catch (NamingException namingEx)
		  		{
		  			logError(NLT.get("errorcode.ldap.context"), namingEx);
		  			
		  			LdapSyncException	ldapSyncEx;
		  			
		  			// Create an LdapSyncException and throw it.  We throw an LdapSyncException so we can return
		  			// the LdapConnectionConfig object that was being used when the error happened.
		  			ldapSyncEx = new LdapSyncException( config, namingEx );
		  			throw ldapSyncEx;
		  		}
		  		finally {
					if (ctx != null) {
						try
						{
							ctx.close();
						}
						catch (NamingException namingEx)
				  		{
				  			LdapSyncException	ldapSyncEx;
				  			
				  			// Create an LdapSyncException and throw it.  We throw an LdapSyncException so we can return
				  			// the LdapConnectionConfig object that was being used when the error happened.
				  			ldapSyncEx = new LdapSyncException( config, namingEx );
				  			throw ldapSyncEx;
				  		}
					}
								
				}
			}
	   		logger.info( "Finished syncGroups()" );
	   		groupCoordinator.deleteObsoleteGroups();
	   		logger.info( "Finished groupCoordinator.deleteObsoleteGroups()" );

	   		// Find all groups that have dynamic membership and update the membership
	   		// of those groups that are supposed to be updated during the ldap sync process
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
	   					updateDynamicGroupMembership( nextGroupId, groupCoordinator.getLdapSyncResults() );
	   				}
	   			}
	   			
	   			logger.info( "Finished looking for dynamic groups." );
	   		}
		}// end try
		finally
		{
			m_zoneSyncInProgressMap.put( zone.getId(), Boolean.FALSE );
		}
	}
	
	/**
	 * Execute the given ldap query and return how many users/groups were found
	 * @author jwootton
	 */
	public Integer testGroupMembershipCriteria( String baseDn, String filter, boolean searchSubtree ) throws LdapSyncException
	{
		int count = 0;
		
		// Does the membership criteria have a filter?
		if ( filter != null && filter.length() > 0 )
		{
			List<LdapConnectionConfig> ldapConnectionConfigs;
			Workspace zone;
			Long zoneId;

			// Yes
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
			   		NamingException namingEx;

					// Yes
			   		namingEx = null;
			   		ldapContext = null;
			  		try
			  		{
						String[] userAttributeNames = {};
						int scope;
						SearchControls searchControls;
						NamingEnumeration ctxSearch;

						// Get an ldap context for the given ldap configuration
						ldapContext = getUserContext( zoneId, nextLdapConfig );
						
						if ( searchSubtree )
							scope = SearchControls.SUBTREE_SCOPE;
						else
							scope = SearchControls.ONELEVEL_SCOPE;
						
						searchControls = new SearchControls( scope, 0, 0, userAttributeNames, false, false );

						// Execute the ldap search using the membership criteria
						ctxSearch = ldapContext.search( baseDn, filter, searchControls );
						
						// Count the number of users/groups the search found
						while ( ctxSearch.hasMore() )
						{
							ctxSearch.next();

							++count;
						}
					}
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
			  			LdapSyncException	ldapSyncEx;

			  			// Yes
			  			logError( NLT.get( "errorcode.ldap.context" ), namingEx );
			  			
			  			// Create an LdapSyncException and throw it.  We throw an LdapSyncException so we can return
			  			// the LdapConnectionConfig object that was being used when the error happened.
			  			ldapSyncEx = new LdapSyncException( nextLdapConfig, namingEx );
			  			throw ldapSyncEx;
			  		}
				}
			}
		}

		return new Integer( count );
	}
	
	/**
	 * 
	 */
	private void updateDynamicGroupMembership( Long groupId, LdapSyncResults ldapSyncResults )
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
				HashSet<Long> groupMemberIds;
				
				// Yes
				try
				{
					ArrayList<Membership> newMembers;
					PartialLdapSyncResults syncResults = null;
					int count;
					int maxCount;
					
					logger.info( "\tEvaluating dynamic group membership for group: " + group.getName() );

					// Get a list of the dynamic group members.
					groupMemberIds = getDynamicGroupMembers( baseDn, ldapFilter, searchSubtree );
					
					newMembers = new ArrayList<Membership>();
					count = 0;
					
					// Get the maximum number of users that can be in a group.
					maxCount = SPropsUtil.getInt( "dynamic.group.membership.limit", 50000 ); 					
					
					for (Long userId : groupMemberIds)
					{
						logger.info( "\t\tAdding user: " + String.valueOf( userId ) );
						newMembers.add( new Membership( groupId, userId ) );
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
					updateMembership( groupId, newMembers, syncResults );
				}
				catch ( LdapSyncException e )
				{
					
				}
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
		
		// ldap_new will be a list of users we need to create.
		// Key: user name
		// Value: Map of attributes to be written to the db
		Map<String, Map> ldap_new = new HashMap();
		
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

		public UserCoordinator(
			Binder zone,
			boolean sync,
			boolean create,
			boolean delete,
			boolean deleteWorkspace,
			LdapSyncResults syncResults )
		{
			ObjectControls objCtrls;
			FilterControls filterCtrls;
			
			this.zoneId = zone.getId();
			this.sync = sync;
			this.create = create;
			this.delete = delete;
			this.deleteWorkspace = deleteWorkspace;
			m_ldapSyncResults = syncResults;
			
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
				
				//initialize all users as not found unless already disabled or reserved
				if (((Boolean)attributeValues[PRINCIPAL_DISABLED] == Boolean.FALSE) && (Validator.isNull((String)attributeValues[PRINCIPAL_INTERNALID])))
				{
					notInLdap.put((Long)attributeValues[PRINCIPAL_ID], ssName);
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
		 * 
		 * @param dn
		 * @param ssName
		 * @param lAttrs
		 * @param ldapGuidAttribute
		 * @throws NamingException
		 */
		public void record(String dn, String ssName, Attributes lAttrs, String ldapGuidAttribute ) throws NamingException
		{
			boolean foundLdapGuid = false;
			Object[] row = null; 
			Object[] row2 = null;
			String ldapGuid = null;
			boolean foundLocalUser = false;
			
			if (logger.isDebugEnabled())
				logger.debug("Recording user: '" + dn + "'");

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
					
					// Did we find the ldap user in Teaming by their ldap guid?
					if ( foundLdapGuid )
					{
						// Yes
						// We never want to change a user's name in Teaming.  Remove the "name" attribute
						// from the list of attributes to be written to the db.
						userMods.remove( ObjectKeys.FIELD_PRINCIPAL_NAME );

						// Make sure the dn stored in Teaming is updated for this user.
						userMods.put( ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, dn );
						row[PRINCIPAL_FOREIGN_NAME] = dn;
					}
					else
					{
						// No, we found the ldap user in Teaming by their dn or their name.
						
						//remove this incase a mapping exists that is different than the uid attribute
						userMods.remove(ObjectKeys.FIELD_PRINCIPAL_NAME);
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
					userMods.put(ObjectKeys.FIELD_ZONE, zoneId);
					
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
					dnUsers.put(dn, new Object[]{ssName, null, Boolean.FALSE, null, dn, ldapGuid});
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
				updateUsers(zoneId, ldap_existing, syncResults );
				ldap_existing.clear();
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
				List results = createUsers(zoneId, ldap_new, syncResults );
				ldap_new.clear();
				
				// fill in mapping from distinquished name to id
				for (int i=0; i<results.size(); ++i) {
					User user = (User)results.get(i);
					row = (Object[])dnUsers.get(user.getForeignName());
					row[PRINCIPAL_ID] = user.getId();
					row[PRINCIPAL_LDAP_GUID] = user.getLdapGuid();
				}
			}
		}
		
		public Map wrapUp()
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
				updateUsers(zoneId, ldap_existing, syncResults );
			}
			
			if (!ldap_new.isEmpty()) {
				doLog("Creating users:", ldap_new);
				PartialLdapSyncResults	syncResults = null;
				
				// Do we have a place to store the list of added users?
				if ( m_ldapSyncResults != null )
				{
					syncResults = m_ldapSyncResults.getAddedUsers();
				}
				List results = createUsers(zoneId, ldap_new, syncResults );
				for (int i=0; i<results.size(); ++i) {
					User user = (User)results.get(i);
					Object[] row = (Object[])dnUsers.get(user.getForeignName());
					row[PRINCIPAL_ID] = user.getId();
					row[PRINCIPAL_LDAP_GUID] = user.getLdapGuid();
				}
			}
			
			//if disable is enabled, remove users that were not found in ldap
			if (delete && !notInLdap.isEmpty()) {
				PartialLdapSyncResults	syncResults	= null;
				
				if (logger.isInfoEnabled()) {
					logger.info("Deleting users:");
					for (String name:notInLdap.values()) {
						logger.info("'" + name + "'");
					}
				}

				// Do we have a place to store the sync results?
				if ( m_ldapSyncResults != null )
				{
					// Yes
					syncResults = m_ldapSyncResults.getDeletedUsers();
				}
				deletePrincipals(zoneId, notInLdap.keySet(), deleteWorkspace, syncResults );
			}
			
			//!!! Can we use the ldap guid as a better way of doing the following?
			//Set foreign names of users to self; needed to recognize synced names and mark attributes read-only
			if (!delete && !notInLdap.isEmpty()) {
		    	Map users = new HashMap();
				if (logger.isDebugEnabled())
					logger.debug("Users not found in ldap:");
				
				for (Map.Entry<Long, String>me:notInLdap.entrySet()) {
					Long id = me.getKey();
					String name = me.getValue();
					if (logger.isDebugEnabled())
						logger.debug("'"+name+"'");
					
					Object row[] = (Object[])ssUsers.get(name);
					if (!name.equalsIgnoreCase((String)row[PRINCIPAL_FOREIGN_NAME])) {//was synched from somewhere else	
						Map updates = new HashMap();
				    	updates.put(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, name);
			    		users.put(id, updates);
			     	}
				}
				if (!users.isEmpty())
				{
					PartialLdapSyncResults	syncResults	= null;
					
					// Do we have a place to store the sync results?
					if ( m_ldapSyncResults != null )
					{
						// Yes, get the list to store the modified users.
						syncResults = m_ldapSyncResults.getModifiedUsers();
					}
					updateUsers(zoneId, users, syncResults );
					
					// Disable the accounts that are not in ldap
					disableUsers( users );
				}
			}
			return dnUsers;

		}
		
	}// end UserCoordinator
	
	protected void syncUsers(Binder zone, LdapContext ctx, LdapConnectionConfig config, UserCoordinator userCoordinator) 
		throws NamingException {
		String ssName;
		String [] sample = new String[0];
		String[] attributesToRead;
	 
		logger.info( "Starting to sync users, syncUsers()" );

		// Get the mapping of ldap attributes to Teaming field names
		Map userAttributes = config.getMappings();

		userCoordinator.setAttributes(userAttributes);

		// Get a list of the names of the attributes we want to read from the ldap directory for each user.
		attributesToRead = getAttributeNamesToRead( userCoordinator.userAttributeNames, config );

		Set la = new HashSet(userAttributes.keySet());
		String userIdAttribute = config.getUserIdAttribute();
		if (Validator.isNull(userIdAttribute)) userIdAttribute = config.getUserIdAttribute();
		la.add(userIdAttribute);

		for(LdapConnectionConfig.SearchInfo searchInfo : config.getUserSearches()) {
			if(Validator.isNotNull(searchInfo.getFilterWithoutCRLF())) {
				String ldapGuidAttribute;

				// Get the ldap attribute name that we will use for a guid.
				ldapGuidAttribute = config.getLdapGuidAttribute();
				
				int scope = (searchInfo.isSearchSubtree()?SearchControls.SUBTREE_SCOPE:SearchControls.ONELEVEL_SCOPE);
				SearchControls sch = new SearchControls(scope, 0, 0, (String [])la.toArray(sample), false, false);
	
				logger.info( "Searching for users in base dn: " + searchInfo.getBaseDn() );
				
				NamingEnumeration ctxSearch = ctx.search(searchInfo.getBaseDn(), searchInfo.getFilterWithoutCRLF(), sch);
				while (ctxSearch.hasMore()) {
					String	userName;
					String	fixedUpUserName;
					Attributes lAttrs = null;
					
					Binding bd = (Binding)ctxSearch.next();
					userName = bd.getNameInNamespace();
					
					// Fixup the  by replacing all "/" with "\/".
					fixedUpUserName = fixupName( userName );
					fixedUpUserName = fixedUpUserName.trim();

					// Read the necessary attributes for this user from the ldap directory.
					lAttrs = ctx.getAttributes( fixedUpUserName, attributesToRead );
					
					Attribute id=null;
					id = lAttrs.get(userIdAttribute);
					if (id == null) continue;

					//map ldap id to sitescapeName
					ssName = idToName((String)id.get());
					if (ssName == null) continue;

					// Is the name of this user a name that is used for a Teaming system user account?
					// Currently there are 5 system user accounts named, "admin", "guest", "_postingAgent",
					// "_jobProcessingAgent" and "_synchronizationAgent".
					if ( MiscUtil.isSystemUserAccount( ssName ) )
					{
						// Yes, skip this user.  System user accounts cannot be sync'd from ldap.
						continue;
					}
					
					String relativeName = userName.trim();
					String dn;
					if (bd.isRelative() && !"".equals(ctx.getNameInNamespace())) {
						dn = relativeName + "," + ctx.getNameInNamespace().trim();
					} else {
						dn = relativeName;
					}
					
					//!!! How do we want to determine if a user is a duplicate?
					if (userCoordinator.isDuplicate(dn)) {
						logger.error( NLT.get( "errorcode.ldap.userAlreadyProcessed", new Object[] {ssName, dn} ) );
						continue;
					}
					
					userCoordinator.record(dn, ssName, lAttrs, ldapGuidAttribute );
				}
			}
		}
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

		// notInLdap is initially a list of all groups returned from the call to loadObjects(...)
		// When a group is read from the ldap directory it removed from this list.
		// Key: Teaming id
		// Value: group name
		Map<Long, String> notInLdap = new TreeMap();
		
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

		public GroupCoordinator(
			Binder zone,
			Map dnUsers,
			boolean sync,
			boolean create,
			boolean delete,
			LdapSyncResults syncResults )
		{
			ObjectControls objControls;
			FilterControls filterControls;
			
			this.zoneId = zone.getId();
			this.dnUsers = dnUsers;
			this.sync = sync;
			this.create = create;
			this.delete = delete;
			m_ldapSyncResults = syncResults;	// Store the results of the sync here.
			
			createSyncSize = GetterUtil.getLong(getLdapProperty(zone.getName(), "create.flush.threshhold"), 100);
			modifySyncSize = GetterUtil.getLong(getLdapProperty(zone.getName(), "modify.flush.threshhold"), 100);

			// get list of existing groups in Teaming.
			objControls = new ObjectControls( Group.class, principalAttrs );
			filterControls = new FilterControls( ObjectKeys.FIELD_ENTITY_DELETED, Boolean.FALSE );
			List attrs = coreDao.loadObjects( objControls, filterControls, zone.getId() );
			
			//convert list of objects to a Map of loginNames 
			for (int i=0; i<attrs.size(); ++i)
			{
				String ldapGuid;
				String ssName;
				Object[] row;
				
				row = (Object [])attrs.get(i);
				
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
				
				//initialize all groups as not found unless already disabled or reserved
				if (((Boolean)row[PRINCIPAL_DISABLED] == Boolean.FALSE) && (Validator.isNull((String)row[PRINCIPAL_INTERNALID])))
				{
					notInLdap.put((Long)row[PRINCIPAL_ID], ssName);
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
		boolean record(String dn, String teamingName, Attributes lAttrs, String ldapGuidAttribute ) throws NamingException
		{
			boolean isSSGroup = false;
			boolean foundLdapGuid = false;
			String ldapGuid = null;
			String ssName;
			Object[] row = null;
			
			if (logger.isDebugEnabled())
				logger.debug("Recording group: '" + dn + "'");
			
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
						notInLdap.remove( row[PRINCIPAL_ID] );
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
					// Yes, see if name already exists
					ssName = teamingName;
					row = (Object[])ssGroups.get(ssName);
					if (row != null)
					{
						logger.error(NLT.get("errorcode.ldap.groupexists", new Object[]{dn}));
					}
					else
					{
						Map userMods = new HashMap();
						
						//mapping may change the name and title
						getUpdates( groupAttributeNames, groupAttributes, lAttrs, userMods, ldapGuidAttribute );
						
						userMods.put(ObjectKeys.FIELD_PRINCIPAL_NAME,ssName);
						userMods.put(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, dn);
						userMods.put(ObjectKeys.FIELD_ZONE, zoneId);

						if (logger.isDebugEnabled())
							logger.debug("Creating group:" + ssName);
						
						Group group = createGroup(zoneId, ssName, userMods); 
						if(group != null)
						{
							dnGroups.put(dn, new Object[]{ssName, group.getId(), Boolean.FALSE, null, dn, ldapGuid});
							isSSGroup = true;
						}
					}
				}
			}
			else
			{
				ssName = teamingName;
				if (sync)
				{
					Map userMods = new HashMap();
					if (logger.isDebugEnabled())
						logger.debug("Updating group:" + ssName);
					
					// Map the attributes read from the ldap directory to Teaming attributes.
					getUpdates( groupAttributeNames, groupAttributes, lAttrs, userMods, ldapGuidAttribute );

					// We never want to change a group's name in Teaming.  Remove the "name" attribute
					// from the list of attributes to be written to the db.
					userMods.remove( ObjectKeys.FIELD_PRINCIPAL_NAME );

					// Make sure the dn stored in Teaming is updated for this user.
					userMods.put( ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, dn );
					row[PRINCIPAL_FOREIGN_NAME] = dn;

					updateGroup( zoneId, (Long)row[PRINCIPAL_ID], userMods, m_ldapSyncResults );
				} 
				
				//exists in ldap, remove from missing list
				notInLdap.remove(row[PRINCIPAL_ID]);
				isSSGroup = true;
			}
			
			return isSSGroup;
		}// end record()
		
	    /**
	     * Create groups.
	     * @param zoneName
	     * @param groups - Map keyed by user id, value is map of attributes
	     * @return
	     */
	    protected Group createGroup(Long zoneId, String ssName, Map groupData ) {
	    	MapInputData groupMods = new MapInputData(StringCheckUtil.check(groupData));
			ProfileBinder pf = getProfileDao().getProfileBinder(zoneId);
			//get default definition to use
			Group temp = new Group();
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
		    	List newGroups = processor.syncNewEntries(pf, groupDef, Group.class, Arrays.asList(new MapInputData[] {groupMods}), null, syncResults );
		    	IndexSynchronizationManager.applyChanges(); //apply now, syncNewEntries will commit
		    	//flush from cache
		    	getCoreDao().evict(newGroups);
		    	return (Group) newGroups.get(0);		    	
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
			updateMembership(groupId, membership, syncResults );	
		}

		public void deleteObsoleteGroups()
		{
			//if disable is enabled, remove groups that were not found in ldap
			if (delete && !notInLdap.isEmpty()) {
				PartialLdapSyncResults	syncResults	= null;
				
				if (logger.isInfoEnabled()) {
					logger.info("Deleting groups:");
					for (String name:notInLdap.values()) {
						logger.info("'" + name + "'");
					}
				}
				// Do we have a place to store the sync results?
				if ( m_ldapSyncResults != null )
				{
					// Yes
					syncResults = m_ldapSyncResults.getDeletedGroups();
				}
				deletePrincipals(zoneId,notInLdap.keySet(), false, syncResults );
			} else if (!delete && !notInLdap.isEmpty()) {
				if (logger.isDebugEnabled()) {
					logger.debug("Groups not found in ldap:");
					for (String name:notInLdap.values()) {
						logger.debug("'" + name + "'");
					}
					
				}
			}
		}
		
		public Object[] getGroup(String dn)
		{
			return (Object [])dnGroups.get(dn);
		}

	}// end GroupCoordinator

	protected void syncGroups(Binder zone, LdapContext ctx, LdapConnectionConfig config, GroupCoordinator groupCoordinator,
							  boolean syncMembership) 
		throws NamingException {
		//ssname=> forum info
		String [] sample = new String[0];
		//ldap dn => forum info

		logger.info( "Starting to sync groups, syncGroups()" );

		for(LdapConnectionConfig.SearchInfo searchInfo : config.getGroupSearches()) {
			if(Validator.isNotNull(searchInfo.getFilterWithoutCRLF())) {
				String ldapGuidAttribute;
				String[] attributesToRead;
				String[] attributeNames;
				List memberAttributes;
				int i;
				
				logger.info( "Searching for groups in base dn: " + searchInfo.getBaseDn() );
				
				// Get the name of the ldap attribute we will use to get a guid from the ldap directory.
				ldapGuidAttribute = config.getLdapGuidAttribute();
				
				// Get the mapping of attributes for a group.
				Map groupAttributes = (Map) getZoneMap(zone.getName()).get(GROUP_ATTRIBUTES);
				groupCoordinator.setAttributes(groupAttributes);
				
				Set la = new HashSet(groupAttributes.keySet());
				la.addAll((List) getZoneMap(zone.getName()).get(MEMBER_ATTRIBUTES));
				
				int scope = (searchInfo.isSearchSubtree()?SearchControls.SUBTREE_SCOPE:SearchControls.ONELEVEL_SCOPE);
				SearchControls sch = new SearchControls(scope, 0, 0, (String [])la.toArray(sample), false, false);
	
				// Create a String[] of all the attributes we need to read from the directory.
				{
					int len;
					int index;
					
					// Get the names of the attributes that may hold the group membership.
					memberAttributes = (List) getZoneMap(zone.getName()).get(MEMBER_ATTRIBUTES);

					// Get the names of the group attributes
					attributeNames = groupCoordinator.getAttributeNames();
					
					len = 1;
					if ( attributeNames != null )
						len += attributeNames.length;
					
					if ( memberAttributes != null )
						len += memberAttributes.size();
					
					index = 0;
					attributesToRead = new String[len];
					for (i = 0; i < attributeNames.length; ++i)
					{
						attributesToRead[index] = attributeNames[i];
						++index;
					}
					
					for (i = 0; i < memberAttributes.size(); ++i)
					{
						attributesToRead[index] = (String)memberAttributes.get( i );
						++index;
					}
					
					attributesToRead[index] = ldapGuidAttribute;
				}
				
				NamingEnumeration ctxSearch = ctx.search(searchInfo.getBaseDn(), searchInfo.getFilterWithoutCRLF(), sch);

				while (ctxSearch.hasMore()) {
					String groupName;
					String fixedUpGroupName;
					String teamingName;
					Attribute id;
					
					Binding bd = (Binding)ctxSearch.next();
					groupName = bd.getNameInNamespace();
					
					// Fixup the  by replacing all "/" with "\/".
					fixedUpGroupName = fixupName( groupName );
					fixedUpGroupName = fixedUpGroupName.trim();
					
					// Read the given attributes for this group from the directory.
					Attributes lAttrs = ctx.getAttributes( fixedUpGroupName, attributesToRead );
					
					String relativeName = groupName.trim();
					String dn;
					if (bd.isRelative() && !"".equals(ctx.getNameInNamespace())) {
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

					//doing this one at a time is going to be slow for lots of groups
					//not sure why it was changed for v2
					if(groupCoordinator.record(dn, teamingName, lAttrs, ldapGuidAttribute ) && syncMembership ) { 
						//Get map indexed by id
						Object[] gRow = groupCoordinator.getGroup(dn);
						if (gRow == null) continue; //not created
						Long groupId = (Long)gRow[PRINCIPAL_ID];
						if (groupId == null) continue; // never got created
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
						} else {
							NamingEnumeration<NameClassPair> e = ctx.list( fixedUpGroupName );
	
							LinkedList membersList = new LinkedList();
							while(e.hasMore()) {
								NameClassPair pair = e.next();
								membersList.add(pair.getNameInNamespace());
							}
							members = Collections.enumeration(membersList);
						}
						if(members != null) {
							groupCoordinator.syncMembership(groupId, members);
						}
					}
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
			// Specify that we want the ldap guid attibute returned as binary data.
			env.put( "java.naming.ldap.attributes.binary", ldapGuidAttributeName );
		}
		
		env.put(Context.PROVIDER_URL, url);
		String socketFactory = getLdapProperty(zone.getName(), "java.naming.ldap.factory.socket"); 
		if (!Validator.isNull(socketFactory))
			env.put("java.naming.ldap.factory.socket", socketFactory);
	
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
			// in the mapping plus the name of the attribute that uniquely identifies
			// a user plus the ldap attribute that identifies a user.
			attributeNames = new String[userAttributeNames.length + 2];
			
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
			}
		}
		
		return attributeNames;
	}// end getAttributeNamesToRead()

	
	/**
	 * @param ldapAttrNames
	 * @param mapping
	 * @param attrs
	 * @param mods
	 * @param ldapGuidAttribute
	 * @throws NamingException
	 */
	protected static void getUpdates(String []ldapAttrNames, Map mapping, Attributes attrs, Map mods, String ldapGuidAttribute )  throws NamingException
	{
		if ( ldapAttrNames != null )
		{
			for (int i=0; i<ldapAttrNames.length; i++) {
				Attribute att = attrs.get(ldapAttrNames[i]);
				if (att == null) continue;
				Object val = att.get();
				if (val == null) {
					mods.put(mapping.get(ldapAttrNames[i]), null);
				} else if (att.size() == 0) {
					continue;
				} else if (att.size() == 1) {
					mods.put(mapping.get(ldapAttrNames[i]), val);					
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
	
					mods.put( mapping.get( ldapAttrNames[i]), value );
				}
			}
		}
		
		// Do we have an ldap guid attribute?
		if ( ldapGuidAttribute != null )
		{
			String ldapGuid;
			
			ldapGuid = getLdapGuid( attrs, ldapGuidAttribute );
			mods.put( ObjectKeys.FIELD_PRINCIPAL_LDAPGUID, ldapGuid );
		}
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
	 * Read the default locale id from the global properties
	 * @param name
	 * @return
	 */
	private String getDefaultLocaleId()
	{
		String			defaultLocaleId;
		Workspace		topWorkspace;
		WorkspaceModule	workspaceModule;
		
		// Get the top workspace.  That is where global properties are stored.
		workspaceModule = (WorkspaceModule) SpringContextUtil.getBean( "workspaceModule" );
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
	private String getDefaultTimeZone()
	{
		String			defaultTimeZone;
		Workspace		topWorkspace;
		WorkspaceModule	workspaceModule;
		
		// Get the top workspace.  That is where global properties are stored.
		workspaceModule = (WorkspaceModule) SpringContextUtil.getBean( "workspaceModule" );
		topWorkspace = workspaceModule.getTopWorkspace();
		
		// Get the default time zone property.
		defaultTimeZone = (String) topWorkspace.getProperty( ObjectKeys.GLOBAL_PROPERTY_DEFAULT_TIME_ZONE );
		if ( defaultTimeZone == null || defaultTimeZone.length() == 0 )
			defaultTimeZone = "GMT";
		
		return defaultTimeZone;
	}// end getDefaultTimeZone()


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
	protected void updateUser(Binder zone, String loginName, Map mods) throws NoUserByTheNameException {

		User profile = getProfileDao().findUserByName(loginName, zone.getName()); 
 		ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
	            	profile.getParentBinder(), ProfileCoreProcessor.PROCESSOR_KEY);
		processor.syncEntry(profile, new MapInputData(StringCheckUtil.check(mods)), null);
	}	
	/**
	 * Update users with their own map of updates
	 * @param users - Map indexed by user id, value is map of updates for a user
	 */
	protected void updateUsers(Long zoneId, final Map users, PartialLdapSyncResults syncResults ) {
		ProfileBinder pf;
		List collections;
	   	List foundEntries;
	   	Map entries;
   		ProfileCoreProcessor processor;

		if ( users.isEmpty() )
			return;
		
		pf = getProfileDao().getProfileBinder(zoneId);
		collections = new ArrayList();
		collections.add( "customAttributes" );
	   	foundEntries = getCoreDao().loadObjects( users.keySet(), User.class, zoneId, collections );
	   	entries = new HashMap();
	   	for (int i=0; i<foundEntries.size(); ++i)
	   	{
	   		User u = (User)foundEntries.get(i);
	   		entries.put( u, new MapInputData( StringCheckUtil.check( (Map)users.get( u.getId() ) ) ) );
	   	}

   		processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
   																		pf, 
   																		ProfileCoreProcessor.PROCESSOR_KEY);
	   	try 
	   	{
	   		processor.syncEntries( entries, null, syncResults );
	   		IndexSynchronizationManager.applyChanges(); //apply now, syncEntries will commit
	   		//flush from cache
	   		for (int i=0; i<foundEntries.size(); ++i)
	   		{
	   			getCoreDao().evict( foundEntries.get( i ) );
	   		}
	   	}
	   	catch ( Exception ex )
	   	{
	   		logger.error( "An error happened updating a user in the batch of users: " + ex.toString() );
	   		
	   		// Try to update each user in the list
		   	for (int i=0; i < foundEntries.size(); ++i)
		   	{
		   		try
		   		{
			   		User user;

			   		entries.clear();
			   		user = (User)foundEntries.get( i );
			   		entries.put( user, new MapInputData( StringCheckUtil.check( (Map)users.get( user.getId() ) ) ) );

			   		logger.info( "2nd attempt to update the user: " + user.getName() );
			   		
			   		processor.syncEntries( entries, null, syncResults );
			   		IndexSynchronizationManager.applyChanges(); //apply now, syncEntries will commit
		   			getCoreDao().evict( user );
		   		}
		   		catch ( Exception ex2 )
		   		{
		   			logger.error( "2nd attempt to update the user failed: " + ex2.toString() );
		   		}
		   	}
	   	}
	}
	
	/**
	 * Disable the given list of users.
	 */
	protected void disableUsers( Map users )
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
			
			getProfileModule().disableEntry( userIdL, true );

			logger.info( "Disabled user: " + userName );
		}
	}

    /**
     * Update group with their own updates
     */    
	protected void updateGroup( Long zoneId, final Long groupId, final Map groupMods, LdapSyncResults ldapSyncResults )
	{
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
	    	//flush from cache
	    	getCoreDao().evict(g);
	    } catch (Exception ex) {
	    	//continue 
	    	logError("Error updating groups", ex);	   		
	    }
    }// end updateGroup()


    protected void updateMembership(Long groupId, Collection newMembers, PartialLdapSyncResults syncResults ) {
		//have a list of users, now compare with what exists already
		List oldMembers = getProfileDao().getMembership(groupId, RequestContextHolder.getRequestContext().getZoneId());
		final Set newM = CollectionUtil.differences(newMembers, oldMembers);
		final Set remM = CollectionUtil.differences(oldMembers, newMembers);

        if(!newM.isEmpty() || !remM.isEmpty()) { // membership changed
	        // The following part requires update database transaction.
	        getTransactionTemplate().execute(new TransactionCallback() {
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

		ProfileModule profileMod;
		Object tmp;
		Group group = null;
		
		// Get the Group object from the group id.
		profileMod = getProfileModule();
		tmp = (Object) profileMod.getEntry( groupId );
		if ( tmp instanceof Group )
		{
			group = (Group) tmp; 
		}

		// Add this group to the list of sync results if the group membership changed.
		if ( syncResults != null && (newM.size() > 0 || remM.size() > 0) && group != null )
		{
			String	groupName;
			
			groupName = group.getName();
			syncResults.addResult( groupName + " (" + group.getForeignName() + ")" );
		}

		// Get a list of all the principals that were added or removed from the group.
		if ( (newM != null && newM.isEmpty() == false) || (remM != null && remM.isEmpty() == false) )  
        {
			Map<Long, Principal> principalsToIndex;
			ArrayList<Long> usersRemovedFromGroup;
			ArrayList<Long> groupsRemovedFromGroup;
			ArrayList<Long> usersAddedToGroup;
			ArrayList<Long> groupsAddedToGroup;
			
			// Create a list of the principals that need to be reindexed.
			principalsToIndex = new HashMap<Long,Principal>();
			
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
						logger.debug( "In updateMemberhsip(), next principal added to group, nextGroupId: " + String.valueOf( nextGroupId ) + "  nextUserId: " + String.valueOf( nextUserId ) );
					}

					if ( principalId != null )
					{
						Principal nextPrincipal;

						// Add this principal to the list of principals to be re-indexed.
						nextPrincipal = getProfileModule().getEntry( principalId );
						principalsToIndex.put( principalId, nextPrincipal );
						
						// Keep track of the principals that were added to this group.
						if ( (nextPrincipal instanceof UserPrincipal) || (nextPrincipal instanceof User) )
							usersAddedToGroup.add( principalId );
						else if ( (nextPrincipal instanceof GroupPrincipal) || (nextPrincipal instanceof Group) )
							groupsAddedToGroup.add( principalId );
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
						logger.debug( "In updateMemberhsip() next principal removed from group, nextGroupId: " + String.valueOf( nextGroupId ) + "  nextUserId: " + String.valueOf( nextUserId ) );
					}

					if ( principalId != null )
					{
						Principal nextPrincipal;

						// Add this principal to the list of principals to be re-indexed.
						nextPrincipal = getProfileModule().getEntry( principalId );
						principalsToIndex.put( principalId, nextPrincipal );
						
						// Keep track of the principals that were removed from this group.
						if ( (nextPrincipal instanceof UserPrincipal) || (nextPrincipal instanceof User) )
							usersRemovedFromGroup.add( principalId );
						else if ( (nextPrincipal instanceof GroupPrincipal) || (nextPrincipal instanceof Group) )
							groupsRemovedFromGroup.add( principalId );
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

			// Do we have anything to reindex?
			if ( principalsToIndex.size() > 0 )
				Utils.reIndexPrincipals( getProfileModule(), principalsToIndex );
        }
    }
    /**
     * Create users.  
     * @param zoneName
     * @param users - Map keyed by user id, value is map of attributes
     * @return
     */
    protected List<User> createUsers(Long zoneId, Map<String, Map> users, PartialLdapSyncResults syncResults ) {
		//SimpleProfiler.setProfiler(new SimpleProfiler(false));
		ProfileCoreProcessor processor;
		
		ProfileBinder pf = getProfileDao().getProfileBinder(zoneId);
		List newUsers = new ArrayList();
		for (Iterator i=users.values().iterator(); i.hasNext();) {
			Map attrs = (Map)i.next();

			newUsers.add(new MapInputData(StringCheckUtil.check(attrs)));
		}
		//get default definition to use
		Definition userDef = pf.getDefaultEntryDef();		
		if (userDef == null) {
			User temp = new User();
			getDefinitionModule().setDefaultEntryDefinition(temp);
			userDef = getDefinitionModule().getDefinition(temp.getEntryDefId());
		}

		processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
            																pf,
            																ProfileCoreProcessor.PROCESSOR_KEY );
		try 
		{
			// Try to create all of the users at once.
			newUsers = processor.syncNewEntries(pf, userDef, User.class, newUsers, null, syncResults );    

			if(logger.isDebugEnabled())
				logger.debug("Applying index changes");
			IndexSynchronizationManager.applyChanges();  //apply now, syncNewEntries will commit
			//SimpleProfiler.printProfiler();
		   	//SimpleProfiler.clearProfiler();
			//flush from cache
			getCoreDao().evict(newUsers);
		}
		catch ( Exception ex )
		{
			List nextUser;
			
			nextUser = new ArrayList();

			// An error happened trying to create one of the users in the batch.  Log the error
			logger.error( "An error occurred attempting to create a batch of users: " + ex.toString() );
		
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
					nextUser = processor.syncNewEntries( pf, userDef, User.class, nextUser, null, syncResults );    
					
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
			
			if ( newUsers.size() > 0 )
				getCoreDao().evict( newUsers );
		}
		
	   	return newUsers;
     }
    /**
     * Create groups.
     * @param zoneName
     * @param groups - Map keyed by user id, value is map of attributes
     * @return
     */
    protected List<Group> createGroups(Binder zone, Map<String, Map> groups, PartialLdapSyncResults syncResults) {
		ProfileBinder pf = getProfileDao().getProfileBinder(zone.getZoneId());
		List newGroups = new ArrayList();
		for (Iterator i=groups.values().iterator(); i.hasNext();) {
			newGroups.add(new MapInputData(StringCheckUtil.check((Map)i.next())));
		}
		//get default definition to use
		Group temp = new Group();
		getDefinitionModule().setDefaultEntryDefinition(temp);
		Definition groupDef = getDefinitionModule().getDefinition(temp.getEntryDefId());

	    try {
	    	ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
            	pf, ProfileCoreProcessor.PROCESSOR_KEY);
	    	newGroups = processor.syncNewEntries(pf, groupDef, Group.class, newGroups, null, syncResults );
	    	IndexSynchronizationManager.applyChanges(); //apply now, syncNewEntries will commit
	    	//flush from cache
	    	getCoreDao().evict(newGroups);
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
     public void deletePrincipals(Long zoneId, Collection ids, boolean deleteWS, PartialLdapSyncResults syncResults ) {
		Map options = new HashMap();
		options.put(ObjectKeys.INPUT_OPTION_DELETE_USER_WORKSPACE, Boolean.valueOf(deleteWS));

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
    				}
    				catch(Exception e) {
    					// Don't report problem here. Instead, let the next step - deleteEntry() - report it.
    				}
    			}

    			getProfileModule().deleteEntry(id, options, true);
    			getCoreDao().clear(); // clear cache to prevent thrashing resulted from prolonged use of a single session
    			
    			count++;
    			
    			if(syncResults != null && name != null)
    				syncResults.addResult(name);
    		} catch (Exception ex) {
    			logError(NLT.get("errorcode.ldap.delete", new Object[]{id.toString()}), ex);
    		}
    	}
		if(count > 0) {
			getProfileModule().deleteEntryFinish();
		}
		IndexSynchronizationManager.applyChanges();
    }
    //have not implement re-enable so only support delete
/*    protected void disableUsers(final Binder zone, final Collection ids) {
        getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
       		getProfileDao().disablePrincipals(ids, zone.getZoneId());
        		
        		return null;
        	}});
        //remove from index
   		for (Iterator i=ids.iterator(); i.hasNext();) {
   	   	    IndexSynchronizationManager.deleteDocument(BasicIndexUtils.makeUid("org.kablink.teaming.domain.User", (Long)i.next()));  			
   		}
   		IndexSynchronizationManager.applyChanges();
   	    
    }
    protected void disableGroups(final Binder zone, final Collection ids) {
        getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		getProfileDao().disablePrincipals(ids, zone.getZoneId());
        		return null;
        	}});
        //remove from index
   		for (Iterator i=ids.iterator(); i.hasNext();) {
   	   	    IndexSynchronizationManager.deleteDocument(BasicIndexUtils.makeUid("org.kablink.teaming.domain.Group", (Long)i.next()));  			
   		}
   		IndexSynchronizationManager.applyChanges();
   }
 */
}
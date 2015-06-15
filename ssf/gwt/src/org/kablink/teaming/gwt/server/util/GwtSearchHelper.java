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
package org.kablink.teaming.gwt.server.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.Group.GroupType;
import org.kablink.teaming.domain.IdentityInfo;
import org.kablink.teaming.domain.NoUserByTheIdException;
import org.kablink.teaming.domain.NoUserByTheNameException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtGroup;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtSearchCriteria.SearchType;
import org.kablink.teaming.gwt.client.GwtSearchResults;
import org.kablink.teaming.gwt.client.GwtTag;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.GwtSearchCriteria.SearchScope;
import org.kablink.teaming.gwt.client.util.GwtFolderEntryType;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.teaming.search.filter.SearchFilterKeys;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.util.search.Constants;

/**
 * Helper methods for the GWT UI server code that services search requests.
 *
 * @author jwootton@novell.com
 */
public class GwtSearchHelper 
{
	protected static Log m_logger = LogFactory.getLog( GwtSearchHelper.class );

	/**
	 * Execute a search based on the given search criteria.
	 * 
	 * @param ri
	 * @param searchCriteria
	 * 
	 * @return
	 * 
	 * @throws Exception 
	 */
	public static GwtSearchResults executeSearch(
										AllModulesInjected ami,
										HttpServletRequest req,
										GwtSearchCriteria searchCriteria ) throws Exception
	{
		GwtSearchResults searchResults;
		
		switch ( searchCriteria.getSearchType() )
		{
		case APPLICATION:
		case APPLICATION_GROUP:
		case COMMUNITY_TAGS:
		case ENTRIES:
		case FOLDERS:
		case GROUP:
		case PERSON:
		case PERSONAL_TAGS:
		case PLACES:
		case PRINCIPAL:
		case TAG:
		case TEAMS:
		case USER:
			searchResults = doSearch( ami, req, searchCriteria );
			break;
				
		default:
			//~JW:  Finish.
			searchResults = null;
			break;
		}
		
		return searchResults;
	}// end executeSearch()
	
	
	/**
	 * Try to find a user with the given email address.
	 * --- WARNING --- This method will return disabled users.
	 */
	public static GwtUser findUserByEmailAddress(
		AllModulesInjected ami,
		HttpServletRequest request,
		String emailAddress )
	{
		SortedSet<User> users;
		GwtUser gwtUser = null;
		
		// Try to find the user by primary email.
		users = ami.getProfileModule().getUsersByEmail( emailAddress, Principal.PRIMARY_EMAIL );
		if ( users == null || users.size() == 0 )
		{
			// Didn't find a user.
			// Try to find the user by their mobile email address
			users = ami.getProfileModule().getUsersByEmail( emailAddress, Principal.MOBILE_EMAIL );
			if ( users == null || users.size() == 0 )
			{
				// Didn't find a user
				// Try to find the user by their text email address
				users = ami.getProfileModule().getUsersByEmail( emailAddress, Principal.TEXT_EMAIL );
			}
		}
		
		// Did we find a user with the given email address?
		if ( users == null || users.size() == 0 )
		{
			User user = null;
			
			// No
			// Try to find the user by name.
			try
			{
				user = ami.getProfileModule().getUserDeadOrAlive( emailAddress );

				if ( user != null && user.isDeleted() == false )
				{
					users = new TreeSet<User>();
					users.add( user );
				}
			}
			catch ( NoUserByTheNameException ex )
			{
				// We can ignore this.
			}
		}
		
		if ( users != null && users.size() > 0 )
		{
			try
			{
				User user;
				String userId;
				
				user = users.first();
				userId = user.getId().toString();
				gwtUser = getGwtUser( ami, request, GwtSearchCriteria.SearchType.PERSON, userId );
			}
			catch ( GwtTeamingException ex )
			{
				// Nothing to do.
			}
		}

		return gwtUser;
	}
	
	/*
	 * This method is meant to search for applications or entries or groups or places or tags or teams or users.
	 */
	@SuppressWarnings({"unchecked", "unused"})
	private static GwtSearchResults doSearch(
			AllModulesInjected ami,
			HttpServletRequest request,
			GwtSearchCriteria searchCriteria ) throws Exception
	{
		Map options;
		List searchTerms;
		SearchFilter searchTermFilter;
		String searchText;
		Integer startingCount;
		Integer maxResults;
		GwtSearchResults searchResults = null;
		
		// Make sure we are dealing with the right search type.
		GwtSearchCriteria.SearchType searchType = searchCriteria.getSearchType();
		switch ( searchType )
		{
		case APPLICATION:
		case APPLICATION_GROUP:
		case COMMUNITY_TAGS:
		case ENTRIES:
		case FOLDERS:
		case GROUP:
		case PERSON:
		case PERSONAL_TAGS:
		case PLACES:
		case PRINCIPAL:
		case TAG:
		case TEAMS:
		case USER:
			break;
			
		default:
			return null;
		}
		
		maxResults = new Integer( searchCriteria.getMaxResults() );
		startingCount = new Integer( searchCriteria.getPageNumber() ) * maxResults;
		
		options = new HashMap();
		options.put( ObjectKeys.SEARCH_MAX_HITS, maxResults );
		options.put( ObjectKeys.SEARCH_OFFSET, startingCount );
		options.put( ObjectKeys.SEARCH_SORT_BY, Constants.SORT_TITLE_FIELD );
		options.put( ObjectKeys.SEARCH_SORT_DESCEND, new Boolean( false ) );
		
		if ( searchCriteria.getSearchScope() == SearchScope.SEARCH_LOCAL )
		{
			String binderId;
			
			binderId = searchCriteria.getBinderId();
			if ( binderId != null && binderId.length() > 0 )
				options.put( ObjectKeys.SEARCH_ANCESTRY, binderId );;
		}
		
		searchText = searchCriteria.getSearchText();
	    searchText = searchText.replaceAll(" \\*", "\\*").trim();

		searchTermFilter = new SearchFilter();
		
	    // Set up the search filter.
		switch ( searchType )
		{
		case ENTRIES:
			String binderId = null;
			
			//Add the title term
			if ( searchText.length() > 0 )
				searchTermFilter.addTitleFilter( searchText );

			searchTerms = new ArrayList();
			searchTerms.add( EntityIdentifier.EntityType.folderEntry.name() );
			searchTermFilter.addAndNestedTerms( SearchFilterKeys.FilterTypeEntityTypes, SearchFilterKeys.FilterEntityType, searchTerms );
			searchTermFilter.addAndFilter( SearchFilterKeys.FilterTypeTopEntry );
			
			break;

		case FOLDERS:
		case PLACES:
			searchTermFilter.addPlacesFilter(
				searchText,
				(searchCriteria.getFoldersOnly() || (GwtSearchCriteria.SearchType.FOLDERS == searchCriteria.getSearchType())) );
			break;
			
		case COMMUNITY_TAGS:
		case PERSONAL_TAGS:
		case TAG:
			// this has been replaced by a getTags method in the search engine.
			// searchTermFilter.addTagsFilter( null, searchText );
			break;
		
		case TEAMS:
			searchTermFilter.addTeamFilter( searchText );
			break;

		case PERSON:
		case USER:
		case PRINCIPAL:
			searchTermFilter.addTitleFilter( searchText );
			
			if ( searchType == SearchType.PERSON || searchType == SearchType.USER )
			{
				searchTermFilter.addLoginNameFilter( searchText );
				
				if ( searchType == SearchType.PERSON )
					searchTermFilter.addAndPersonFlagFilter( true );
			}
			else if ( searchType == SearchType.PRINCIPAL )
			{
				searchTermFilter.addLoginNameFilter( searchText );
				searchTermFilter.addGroupNameFilter( searchText );
			}
			
			// Are we searching for internal principals only?
			if ( searchCriteria.getSearchForInternalPrincipals() == true && searchCriteria.getSearchForExternalPrincipals() == false )
			{
				// Yes
				searchTermFilter.addAndInternalFilter( true );
			}
			// Are we searching for internal principal?
			else if ( searchCriteria.getSearchForInternalPrincipals() == false )
			{
				// No
				searchTermFilter.addAndInternalFilter( false );
			}
				
			// Should we search for ldap containers?
			if ( searchCriteria.getSearchForLdapContainers() == false )
				searchTermFilter.addAndLdapContainerFilter( false );
			
			// Should "team groups" be included in the search results?
			if ( searchCriteria.getSearchForTeamGroups() == false )
				searchTermFilter.addAndTeamGroupFilter( false );
			
			// Type to find should only return enabled users.
    	    options.put(ObjectKeys.SEARCH_IS_ENABLED_PRINCIPALS, Boolean.TRUE);
    	    
			break;

		case GROUP:
			searchTermFilter.addTitleFilter( searchText );
			searchTermFilter.addGroupNameFilter( searchText );

			// Are we searching for internal groups only?
			if ( searchCriteria.getSearchForInternalPrincipals() == true && searchCriteria.getSearchForExternalPrincipals() == false )
			{
				// Yes
				searchTermFilter.addAndInternalFilter( true );
			}
			// Are we searching for internal principals?
			else if ( searchCriteria.getSearchForInternalPrincipals() == false )
			{
				// No
				searchTermFilter.addAndInternalFilter( false );
			}
			
			// Should we search for ldap containers?
			if ( searchCriteria.getSearchForLdapContainers() == false )
				searchTermFilter.addAndLdapContainerFilter( false );
			
			// Should "team groups" be included in the search results?
			if ( searchCriteria.getSearchForTeamGroups() == false )
				searchTermFilter.addAndTeamGroupFilter( false );
			
			// Type to find should only return enabled groups.
    	    options.put(ObjectKeys.SEARCH_IS_ENABLED_PRINCIPALS, Boolean.TRUE);
    	    
			break;
			
		default:
			//Add the login name term
			if ( searchText.length() >0 ) {
				searchTermFilter.addTitleFilter(searchText);
				searchTermFilter.addLoginNameFilter(searchText);
			}
			break;
		}// end switch()

		try
		{
			Map retMap;

			searchResults = new GwtSearchResults();
		
			options.put( ObjectKeys.SEARCH_SEARCH_FILTER, searchTermFilter.getFilter() );
			
			// Perform the search based on the search type.
			switch ( searchType )
			{
			case APPLICATION:
				//~JW:  Get code from ajaxFind() in TypeToFindAjaxController.java
				break;
				
			case APPLICATION_GROUP:
				//~JW:  Get code from ajaxFind() in TypeToFindAjaxController.java
				break;
				
			case ENTRIES:
			{
				List placesWithCounters;
				List entries;
				ArrayList<GwtTeamingItem> results;
				Iterator it;
				Map foldersMap;
				Integer count;
				
				retMap = ami.getBinderModule().executeSearchQuery( searchTermFilter.getFilter(), Constants.SEARCH_MODE_NORMAL, options );
				entries = (List)retMap.get( ObjectKeys.SEARCH_ENTRIES );
				placesWithCounters = BinderHelper.sortPlacesInEntriesSearchResults( ami.getBinderModule(), entries );
				foldersMap = BinderHelper.prepareFolderList( placesWithCounters, false );
				BinderHelper.extendEntriesInfo( entries, foldersMap );
				
				// Add the search results to the GwtSearchResults object.
				count = (Integer) retMap.get( ObjectKeys.SEARCH_COUNT_TOTAL );
				searchResults.setCountTotal( count.intValue() );
				
				// Create a GwtFolderEntry item for each search result.
				results = new ArrayList( entries.size() );
				it = entries.iterator();
				while ( it.hasNext() )
				{
					Map<String,String> entry;
					GwtFolderEntry folderEntry;
					String entryId;
					String entryName;
					String parentBinderName;

					// Get the next entry in the search results.
					entry = (Map) it.next();

					// Pull information about this entry from the search results.
					folderEntry = new GwtFolderEntry();
					entryId = entry.get( "_docId" );
					folderEntry.setEntryId( entryId );
					entryName = entry.get( "title" );
					folderEntry.setEntryName( entryName );
					parentBinderName = entry.get( WebKeys.BINDER_PATH_NAME );
					folderEntry.setParentBinderName( parentBinderName );
					
					{
						GwtFolderEntryType entryType;
						Long id;
						
						id = Long.valueOf( entryId );
						entryType = GwtFolderEntryTypeHelper.getFolderEntryType( ami, id );
						folderEntry.setEntryType( entryType );
					}

					results.add( folderEntry );
				}
				searchResults.setResults( results);
				break;
			}

			case GROUP:
			{
				Map entries;
		    	List searchEntries;
		    	Integer searchHits;
		    	ArrayList<GwtTeamingItem> results = null;
				
				entries = ami.getProfileModule().getGroups( options );
				
				// Do we get any search hits?
		    	searchEntries = (List)entries.get( ObjectKeys.SEARCH_ENTRIES );
		    	searchHits = (Integer)entries.get( ObjectKeys.SEARCH_COUNT_TOTAL );
		    	if ( (null != searchEntries) && (null != searchHits) && (0 < searchHits) )
		    	{
					boolean sendingEmail;
					boolean allowSendToAllUsers;
					int size;

					// Yes
					results = new ArrayList( searchEntries.size() );

					// Is this search a part of sending an email ui?
					// And are we supposed to disallow sending email to the "all users" group?
					sendingEmail = searchCriteria.getIsSendingEmail();
					allowSendToAllUsers = SPropsUtil.getBoolean( "mail.allowSendToAllUsers", false );

					// Create a GwtGroup object for each group returned by the search.
					size = searchEntries.size();
					for (int i = (size - 1); i >= 0; i -= 1)
					{
			    		Map entry;
						String id;
						boolean useGroup;

						useGroup = true;
			    		entry = (Map)searchEntries.get(i);
			    		id = (String)entry.get(Constants.RESERVEDID_FIELD);
						
						// Is this search part of a sending email ui?
						if ( sendingEmail )
						{
							// Is sending an email to all users allowed?
							if ( allowSendToAllUsers == false )
							{
								// No
								// Is this group the "all users" group?
								if ( (null != id) && (id.equalsIgnoreCase( ObjectKeys.ALL_USERS_GROUP_INTERNALID ) ||
										id.equalsIgnoreCase( ObjectKeys.ALL_EXT_USERS_GROUP_INTERNALID )) )
								{
									// Yes, skip it.
									useGroup = false;
									--searchHits;
								}
							}
						}
						
						// Should we add this group to the search results?
						if ( useGroup )
						{
							ProfileModule profileModule;
							List<Long> groupId = new ArrayList<Long>();
							SortedSet<Principal> groupPrincipals;

							id = (String)entry.get( "_docId" );
							groupId.add( Long.valueOf( id ) );
							profileModule = ami.getProfileModule();
							groupPrincipals = profileModule.getPrincipals( groupId );
							
							if ( groupPrincipals.size() > 0  )
							{
								GwtGroup gwtGroup;
								Principal group;
								IdentityInfo identityInfo;
								
								group = groupPrincipals.first();
								
								identityInfo = group.getIdentityInfo();
								if ( identityInfo != null  )
								{
									// Are we suppose to include groups from ldap?
									if ( searchCriteria.getSearchForLdapGroups() == false )
									{
										// No
										// Is this group from ldap?
										if ( identityInfo.isFromLdap() == true )
											useGroup = false;
									}
								}
								else
									useGroup = false;
								
								if ( useGroup )
								{
									Description desc;
									String name;
									String title;
									
									// Yes
									// Create a GwtGroup item for this group.
									gwtGroup = new GwtGroup();
									
									if ( group instanceof UserPrincipal )
										gwtGroup.setInternal( identityInfo.isInternal() );
									
									gwtGroup.setId( id );

									name = group.getName();
									title = group.getTitle();
									
									if ( group instanceof Group &&
										 ((Group)group).getGroupType() == GroupType.team )
									{
										// Use the title of the group instead of the group name for display purposes.
										name = title;
									}

									gwtGroup.setName( name );
									gwtGroup.setTitle( title );
									gwtGroup.setDn( group.getForeignName() );
									desc = group.getDescription();
									if ( desc != null )
										gwtGroup.setDesc( desc.getText() );
									gwtGroup.setGroupType( GwtServerHelper.getGroupType( group ) );
									
									results.add( gwtGroup );
								}
								else
								{
									--searchHits;
								}
							}
						}
			    	}
		    	}
						
				searchResults.setCountTotal( searchHits.intValue() );
				searchResults.setResults( results );
				break;
			}
				
			case FOLDERS:
			case PLACES:
			{
				List placesEntries;
				ArrayList<GwtTeamingItem> results;
				Iterator it;
				Integer count;
				
				retMap = ami.getBinderModule().executeSearchQuery( searchTermFilter.getFilter(), Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, options );

				// Add the search results to the GwtSearchResults object.
				count = (Integer) retMap.get( ObjectKeys.SEARCH_COUNT_TOTAL );
				searchResults.setCountTotal( count.intValue() );
				
				// Create a GwtFolder item for each search result.
				placesEntries = (List)retMap.get( ObjectKeys.SEARCH_ENTRIES );
				results = new ArrayList( placesEntries.size() );
				it = placesEntries.iterator();
				while ( it.hasNext() )
				{
					Map<String,String> entry;
					GwtFolder folder;
					String folderId;
					String folderTitle;

					// Get the next folder in the search results.
					entry = (Map) it.next();

					// Pull information about this folder from the search results.
					folderId = entry.get( "_docId" );
					folderTitle = entry.get( "_extendedTitle" );
					folder = GwtServerHelper.getFolderImpl( ami, request, null, folderId, folderTitle );
					if ( folder != null )
						results.add( folder );
				}
				searchResults.setResults( results);
				break;
			}

			case COMMUNITY_TAGS:
			case PERSONAL_TAGS:
			case TAG:
			{
				ArrayList<GwtTeamingItem> results;
				int count;
				int i;
				Iterator it;
				List tags;
				String tagType;
				String searchRoot;
				
				searchRoot = searchText;
				i = searchRoot.indexOf( "*" );
				if ( i > 0 )
				{
					searchRoot = searchRoot.substring( 0, i );
				}
				
				switch ( searchCriteria.getSearchType() )
				{
				default:
				case TAG:             tagType = WebKeys.FIND_TYPE_TAGS;           break;
				case COMMUNITY_TAGS:  tagType = WebKeys.FIND_TYPE_COMMUNITY_TAGS; break;
				case PERSONAL_TAGS:   tagType = WebKeys.FIND_TYPE_PERSONAL_TAGS;  break;
				}
				
				tags = ami.getBinderModule().getSearchTags( searchRoot, tagType );
				count = ((null == tags) ? 0 : tags.size());
				searchResults.setCountTotal( count );
				results = new ArrayList( count );
				for ( it = tags.iterator(); it.hasNext(); )
				{
					GwtTag tag;
					Map<String,String> tagInfo;
					
					tagInfo = (Map) it.next();
					tag = new GwtTag();
					tag.setTagName( tagInfo.get( "ssTag" ));
					results.add( tag );
				}
				searchResults.setResults( results );
				break;
			}
			
			case TEAMS:
			{
				List teamEntries;
				ArrayList<GwtTeamingItem> results;
				Integer count;
				Iterator it;

				// Search for teams
				retMap = ami.getBinderModule().executeSearchQuery( searchTermFilter.getFilter(), Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, options );
				
				// Add the search results to the GwtSearchResults object.
				count = (Integer) retMap.get( ObjectKeys.SEARCH_COUNT_TOTAL );
				searchResults.setCountTotal( count.intValue() );
				
				// Create a GwtTeam item for each search result.
				teamEntries = (List)retMap.get( ObjectKeys.SEARCH_ENTRIES );
				results = new ArrayList( teamEntries.size() );
				it = teamEntries.iterator();
				while ( it.hasNext() )
				{
					Map<String,String> entry;
					String teamId;

					// Get the next team in the search results.
					entry = (Map) it.next();

					// Pull information about this team from the search results.
					teamId = entry.get( "_docId" );
					
					//~JW:  Finish
				}
				searchResults.setResults( results );
				break;
			}

			case PERSON:
			case USER:
			{
				List userEntries;
				ArrayList<GwtTeamingItem> results;
				Iterator it;
				Integer count;
				
				retMap = ami.getProfileModule().getUsers(options);

				// Add the search results to the GwtSearchResults object.
				count = (Integer) retMap.get( ObjectKeys.SEARCH_COUNT_TOTAL );
				searchResults.setCountTotal( count.intValue() );
				
				// Create a GwtUser item for each search result.
				userEntries = (List)retMap.get( ObjectKeys.SEARCH_ENTRIES );
				results = new ArrayList( userEntries.size() );
				it = userEntries.iterator();
				while ( it.hasNext() )
				{
					Map<String,String> entry;
					GwtUser gwtUser;
					String userId;

					// Get the next user in the search results.
					entry = (Map) it.next();

					// Pull information about this user from the search results.
					userId = entry.get( "_docId" );
					
					gwtUser = getGwtUser( ami, request, searchType, userId );
					if ( gwtUser != null )
					{
						// Is this an external user?
						if ( gwtUser.isInternal() == false )
						{
							// Yes
							// Should we return external users?
							if ( searchCriteria.getSearchForExternalPrincipals() == false )
							{
								// No
								continue;
							}
						}
						
						results.add( gwtUser );
					}
				}
				searchResults.setResults( results);
				break;
			}
			
			case PRINCIPAL:
			{
				List principalEntries;
				ArrayList<GwtTeamingItem> results;
				Iterator it;
				Integer count;
				List<Long> principalIds = new ArrayList<Long>();
				
				retMap = ami.getProfileModule().getPrincipals( options );

				count = (Integer) retMap.get( ObjectKeys.SEARCH_COUNT_TOTAL );
				
				// Create a list of principal ids from the search results.
				principalEntries = (List)retMap.get( ObjectKeys.SEARCH_ENTRIES );
				results = new ArrayList( principalEntries.size() );
				it = principalEntries.iterator();
				while ( it.hasNext() )
				{
					Map<String,String> entry;

					// Get the next principal in the search results.
					entry = (Map) it.next();

					if ( entry != null )
					{
						String id;

						id = (String)entry.get( "_docId" );
						principalIds.add( Long.valueOf( id ) );
					}
				}
				
				if ( principalIds.size() > 0 )
				{
					ProfileModule profileModule;
					SortedSet<Principal> principals;

					profileModule = ami.getProfileModule();
					principals = profileModule.getPrincipals( principalIds );
					if ( principals != null && principals.size() > 0 )
					{
						it = principals.iterator();
						while (it.hasNext() )
						{
							Principal principal;
							EntityType entityType;
							String principalId;
							String internalId;
							IdentityInfo identityInfo;
							
							principal = (Principal) it.next();
							
							identityInfo = principal.getIdentityInfo();

							// Are we dealing with the _postingagent, _jobprocessingagent,
							// _synchronizationagent, or _filesyncagent?
							internalId = principal.getInternalId();
							if ( internalId != null &&
								 (internalId.equalsIgnoreCase( ObjectKeys.ANONYMOUS_POSTING_USER_INTERNALID ) ||
								  internalId.equalsIgnoreCase( ObjectKeys.JOB_PROCESSOR_INTERNALID ) ||
								  internalId.equalsIgnoreCase( ObjectKeys.SYNCHRONIZATION_AGENT_INTERNALID ) ||
								  internalId.equalsIgnoreCase( ObjectKeys.FILE_SYNC_AGENT_INTERNALID ) ) )
							{
								// Yes
								if ( count != null )
									--count;
							
								continue;
							}

							// Are we dealing with an external user/group?
							if ( identityInfo != null && identityInfo.isInternal() == false )
							{
								// Yes, we never want to return an external user/group
								if ( count != null )
									--count;
								
								continue;
							}
							
							principalId = principal.getId().toString();
							
							entityType = principal.getEntityType();
							if ( entityType == EntityType.group )
							{
								GwtGroup gwtGroup;
								Description desc;
								String name;
								String title;

								// Is this group from ldap?
								if ( identityInfo != null && identityInfo.isFromLdap() )
								{
									// Yes
									// Are we supposed to include groups from ldap?
									if ( searchCriteria.getSearchForLdapGroups() == false )
									{
										// No
										if ( count != null )
											--count;
										continue;
									}
								}
								
								// Create a GwtGroup item for this group.
								gwtGroup = new GwtGroup();
								
								if ( (principal instanceof UserPrincipal) && identityInfo != null )
									gwtGroup.setInternal( identityInfo.isInternal() );
								
								gwtGroup.setId( principalId );

								name = principal.getName();
								title = principal.getTitle();
									
								if ( principal instanceof Group &&
										 ((Group)principal).getGroupType() == GroupType.team )
								{
									// Use the title of the group instead of the group name for display purposes.
									name = title;
								}

								gwtGroup.setName( name );
								gwtGroup.setTitle( title );
								gwtGroup.setDn( principal.getForeignName() );
								desc = principal.getDescription();
								if ( desc != null )
									gwtGroup.setDesc( desc.getText() );
								gwtGroup.setGroupType( GwtServerHelper.getGroupType( principal ));
								
								results.add( gwtGroup );
							}
							else if ( entityType == EntityType.user )
							{
								GwtUser gwtUser;

								gwtUser = getGwtUser( ami, request, searchType, principalId );
								if ( gwtUser != null )
									results.add( gwtUser );
							}
						}// end while()
					}
				}
				
				// Add the search results to the GwtSearchResults object.
				searchResults.setCountTotal( count.intValue() );

				searchResults.setResults( results );
				break;
			}
				
			default:
				searchResults = null;
			}// end switch()
		}
		catch( AccessControlException e )
		{
			//~JW:  What to do here?
			searchResults = null;
		}
		
		return searchResults;
	}// end doSearch()


	/*
	 * Return a GwtUser object for the given user id
	 */
	private static GwtUser getGwtUser(
			AllModulesInjected ami,
			HttpServletRequest request,
			GwtSearchCriteria.SearchType searchType,
			String userId ) throws GwtTeamingException
	{
		Binder binder = null;
		BinderModule bm = ami.getBinderModule();
		GwtUser reply = null;
		ProfileModule pm = ami.getProfileModule();
		User user = null;
		
		try
		{
			Long userIdL;

			// Do we have an ID we can access as a person?
			userIdL = new Long( userId );
			if ( userIdL != null )
			{
				try
				{
					user = pm.getUserDeadOrAlive( userIdL );
				}
				catch ( NoUserByTheIdException ex )
				{
					// Nothing to do
				}
				
				if ( user != null && user.isDeleted() == false )
				{
					// If we are searching for a person and this user
					// is not a person...
					if ( ( searchType == GwtSearchCriteria.SearchType.PERSON ) && ( ! ( user.isPerson() ) ) )
					{
						// ...ignore it.
						user = null;
					}
					
					else {
						Long wsId;

						// Does this user have a workspace ID?
						wsId = user.getWorkspaceId();
						if ( null != wsId )
						{
							try
							{
								// Yes!  Can we access the workspace?
								binder = bm.getBinder( user.getWorkspaceId() );
							}
							catch ( Exception ex )
							{
								// No!  Simply ignore it as this is a
								// permissible condition if the user
								// performing the search does NOT have
								// access to the workspace in question.
								binder = null;
							}
						}
						
						// Note:  Cases where a user won't have a workspace
						//    ID include special user IDs such as the email
						//    posting agent and others as well as users
						//    that have never logged in.
					}
				}
			}
			
			// Do we have access to a user?
			if ( null != user )
			{
				// Yes!  Construct a GwtUser object for it.
				reply = new GwtUser();
				reply.setInternal(user.getIdentityInfo().isInternal());
				reply.setPrincipalType( GwtViewHelper.getPrincipalType( user ) );
				reply.setUserId( user.getId() );
				reply.setName( user.getName() );
				reply.setTitle( Utils.getUserTitle( user ) );
				reply.setWorkspaceTitle( user.getWSTitle() );
				reply.setEmail( user.getEmailAddress() );
				reply.setAvatarUrl( GwtServerHelper.getUserAvatarUrl( ami, request, user ) );
				reply.setDisabled( user.isDisabled() );
				
				GwtServerHelper.setExtUserProvState( reply, user );
				
				// Do we have access to this user's workspace?
				if ( null == binder ) {
					// No!  Provide a permalink to the user's profile.
					reply.setViewWorkspaceUrl( PermaLinkUtil.getPermalink( request, user ) );
				}
				else {
					// Yes, we have access to this user's workspace!
					// Store the workspace's ID and a permalink to it. 
					reply.setWorkspaceId( binder.getId() );
					reply.setViewWorkspaceUrl( PermaLinkUtil.getPermalink( request, binder ) );
				}
			}
		}
		catch ( Exception e )
		{
			throw GwtLogHelper.getGwtClientException( e );
		}
	
		return reply;
	}// end getGwtUser()
	
}

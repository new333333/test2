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
import org.kablink.teaming.domain.ProxyIdentity;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtGroup;
import org.kablink.teaming.gwt.client.GwtProxyIdentity;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtSearchCriteria.SearchType;
import org.kablink.teaming.gwt.client.GwtSearchResults;
import org.kablink.teaming.gwt.client.GwtTag;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.GwtSearchCriteria.SearchScope;
import org.kablink.teaming.gwt.client.util.GwtFolderEntryType;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.teaming.search.filter.SearchFilterKeys;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.util.search.Constants;

/**
 * Helper methods for the GWT UI server code that services search
 * requests.
 *
 * @author drfoster@novell.com 
 */
public class GwtSearchHelper {
	protected static Log m_logger = LogFactory.getLog(GwtSearchHelper.class);

	/**
	 * Execute a search based on the given search criteria.
	 *
	 * @param bs
	 * @param req
	 * @param searchCriteria
	 * 
	 * @return
	 * 
	 * @throws Exception 
	 */
	public static GwtSearchResults executeSearch(AllModulesInjected bs, HttpServletRequest req, GwtSearchCriteria searchCriteria) throws Exception {
		GwtSearchResults searchResults;
		switch (searchCriteria.getSearchType()) {
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
		case PROXY_IDENTITY:
		case TAG:
		case TEAMS:
		case USER:
			searchResults = doSearch(bs, req, searchCriteria);
			break;
				
		default:
			//~JW:  Finish.
			searchResults = null;
			break;
		}
		
		return searchResults;
	}
	
	
	/**
	 * Finds a user with the given email address.
	 * 
	 * --- WARNING --- This method will return disabled users.
	 * 
	 * @param bs
	 * @param request
	 * @param emailAddress
	 * 
	 * @return
	 */
	public static GwtUser findUserByEmailAddress(AllModulesInjected bs, HttpServletRequest request, String emailAddress) {
		GwtUser gwtUser = null;
		
		// Try to find the user by primary email.
		SortedSet<User> users = bs.getProfileModule().getUsersByEmail(emailAddress, Principal.PRIMARY_EMAIL);
		if (!(MiscUtil.hasItems(users))) {
			// Didn't find a user.  Try to find the user by their
			// mobile email address.
			users = bs.getProfileModule().getUsersByEmail(emailAddress, Principal.MOBILE_EMAIL);
			if (!(MiscUtil.hasItems(users))) {
				// Didn't find a user.  Try to find the user by their
				// text email address.
				users = bs.getProfileModule().getUsersByEmail(emailAddress, Principal.TEXT_EMAIL);
			}
		}
		
		// Did we find a user with the given email address?
		if (!(MiscUtil.hasItems(users))) {
			// No!  Try to find the user by name.
			User user = null;
			try {
				user = bs.getProfileModule().getUserDeadOrAlive(emailAddress);
				if ((null != user) && (!(user.isDeleted()))) {
					users = new TreeSet<User>();
					users.add(user);
				}
			}
			
			catch (NoUserByTheNameException ex) {
				// We can ignore this.
			}
		}
		
		if (MiscUtil.hasItems(users)) {
			try {
				User user = users.first();
				String userId = user.getId().toString();
				gwtUser = getGwtUser(bs, request, GwtSearchCriteria.SearchType.PERSON, userId);
			}
			
			catch (GwtTeamingException ex) {
				// Nothing to do.
			}
		}

		return gwtUser;
	}
	
	/*
	 * This method is meant to search for applications, entries,
	 * groups, places, tags, teams, users, ...
	 */
	@SuppressWarnings({"unchecked", "unused"})
	private static GwtSearchResults doSearch(AllModulesInjected bs, HttpServletRequest request, GwtSearchCriteria searchCriteria) throws Exception {
		// Make sure we are dealing with a supported search type.
		GwtSearchCriteria.SearchType searchType = searchCriteria.getSearchType();
		switch (searchType) {
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
		case PROXY_IDENTITY:
		case TAG:
		case TEAMS:
		case USER:
			break;
			
		default:
			return null;
		}
		
		Integer maxResults    =  new Integer(searchCriteria.getMaxResults());
		Integer startingCount = (new Integer(searchCriteria.getPageNumber()) * maxResults);
		
		boolean proxyIdentitySearch = searchType.equals(GwtSearchCriteria.SearchType.PROXY_IDENTITY);
		Map options = new HashMap();
		options.put(ObjectKeys.SEARCH_MAX_HITS,     maxResults   );
		options.put(ObjectKeys.SEARCH_OFFSET,       startingCount);
		options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.FALSE);
		options.put(
			ObjectKeys.SEARCH_SORT_BY,
			(proxyIdentitySearch                      ?
				ObjectKeys.FIELD_PROXY_IDENTITY_TITLE :
				Constants.SORT_TITLE_FIELD));
		
		if (searchCriteria.getSearchScope().equals(SearchScope.SEARCH_LOCAL)) {
			String binderId = searchCriteria.getBinderId();
			if (MiscUtil.hasString(binderId)) {
				options.put(ObjectKeys.SEARCH_ANCESTRY, binderId);
			}
		}
		
		String searchText = searchCriteria.getSearchText();
	    searchText = searchText.replaceAll(" \\*", "\\*").trim();

	    // Set up the search filter.
		SearchFilter searchTermFilter = new SearchFilter();
		List searchTerms;
		switch (searchType) {
		case ENTRIES:
			String binderId = null;
			
			// Add the title term.
			if (searchText.length() > 0) {
				searchTermFilter.addTitleFilter(searchText);
			}

			searchTerms = new ArrayList();
			searchTerms.add(EntityIdentifier.EntityType.folderEntry.name());
			searchTermFilter.addAndNestedTerms(SearchFilterKeys.FilterTypeEntityTypes, SearchFilterKeys.FilterEntityType, searchTerms);
			searchTermFilter.addAndFilter(SearchFilterKeys.FilterTypeTopEntry);
			
			break;

		case FOLDERS:
		case PLACES:
			searchTermFilter.addPlacesFilter(
				searchText,
				(searchCriteria.getFoldersOnly() || (GwtSearchCriteria.SearchType.FOLDERS.equals(searchCriteria.getSearchType()))));
			break;
			
		case COMMUNITY_TAGS:
		case PERSONAL_TAGS:
		case TAG:
			// This has been replaced by a getTags method in the search
			// engine.  
			// searchTermFilter.addTagsFilter(null, searchText);
			break;
		
		case TEAMS:
			searchTermFilter.addTeamFilter(searchText);
			break;

		case PERSON:
		case USER:
		case PRINCIPAL:
			searchTermFilter.addTitleFilter(searchText);
			if (searchType.equals(SearchType.PERSON) || searchType.equals(SearchType.USER)) {
				searchTermFilter.addLoginNameFilter(searchText);
				if (searchType.equals(SearchType.PERSON)) {
					searchTermFilter.addAndPersonFlagFilter(true);
				}
			}
			
			else if (searchType.equals(SearchType.PRINCIPAL)) {
				searchTermFilter.addLoginNameFilter(searchText);
				searchTermFilter.addGroupNameFilter(searchText);
			}
			
			// Are we searching for internal principals only?
			if (searchCriteria.getSearchForInternalPrincipals() && (!(searchCriteria.getSearchForExternalPrincipals()))) {
				// Yes!
				searchTermFilter.addAndInternalFilter(true);
			}
			
			// Are we searching for internal principal?
			else if (!(searchCriteria.getSearchForInternalPrincipals())) {
				// No!
				searchTermFilter.addAndInternalFilter(false);
			}
				
			// Should we search for LDAP containers?
			if (!(searchCriteria.getSearchForLdapContainers())) {
				searchTermFilter.addAndLdapContainerFilter(false);
			}
			
			// Should 'team groups' be included in the search results?
			if (!(searchCriteria.getSearchForTeamGroups())) {
				searchTermFilter.addAndTeamGroupFilter(false);
			}
			
			// Type to find should only return enabled users.
    	    options.put(ObjectKeys.SEARCH_IS_ENABLED_PRINCIPALS, Boolean.TRUE);
			break;

		case GROUP:
			searchTermFilter.addTitleFilter(    searchText);
			searchTermFilter.addGroupNameFilter(searchText);

			// Are we searching for internal groups only?
			if (searchCriteria.getSearchForInternalPrincipals() && (!(searchCriteria.getSearchForExternalPrincipals()))) {
				// Yes!
				searchTermFilter.addAndInternalFilter(true);
			}
			
			// Are we searching for internal principals?
			else if (!(searchCriteria.getSearchForInternalPrincipals())) {
				// No!
				searchTermFilter.addAndInternalFilter(false);
			}
			
			// Should we search for LDAP containers?
			if (!(searchCriteria.getSearchForLdapContainers())) {
				searchTermFilter.addAndLdapContainerFilter(false);
			}
			
			// Should 'team groups' be included in the search results?
			if (!(searchCriteria.getSearchForTeamGroups())) {
				searchTermFilter.addAndTeamGroupFilter(false);
			}
			
			// Type to find should only return enabled groups.
    	    options.put(ObjectKeys.SEARCH_IS_ENABLED_PRINCIPALS, Boolean.TRUE);
			break;

		case PROXY_IDENTITY:
			// Doesn't use the filter.  Handled in a custom manner
			// below.
			break;
			
		default:
			// Add the login name term.
			if (searchText.length() > 0) {
				searchTermFilter.addTitleFilter(    searchText);
				searchTermFilter.addLoginNameFilter(searchText);
			}
			break;
		}

		GwtSearchResults searchResults;
		try {
			searchResults = new GwtSearchResults();
			if (!proxyIdentitySearch) {
				options.put(ObjectKeys.SEARCH_SEARCH_FILTER, searchTermFilter.getFilter());
			}
			
			// Perform the search based on the search type.
			Map retMap;
			switch (searchType) {
			case APPLICATION:
				//~JW:  Get code from ajaxFind() in TypeToFindAjaxController.java
				break;
				
			case APPLICATION_GROUP:
				//~JW:  Get code from ajaxFind() in TypeToFindAjaxController.java
				break;
				
			case ENTRIES:  {
				retMap = bs.getBinderModule().executeSearchQuery(searchTermFilter.getFilter(), Constants.SEARCH_MODE_NORMAL, options);
				List entries = ((List) retMap.get(ObjectKeys.SEARCH_ENTRIES));
				List placesWithCounters = BinderHelper.sortPlacesInEntriesSearchResults(bs.getBinderModule(), entries);
				Map foldersMap = BinderHelper.prepareFolderList(placesWithCounters, false);
				BinderHelper.extendEntriesInfo(entries, foldersMap);
				
				// Add the search results to the GwtSearchResults
				// object.
				Integer count = ((Integer) retMap.get(ObjectKeys.SEARCH_COUNT_TOTAL));
				searchResults.setCountTotal(count.intValue());
				
				// Create a GwtFolderEntry item for each search result.
				ArrayList<GwtTeamingItem> results = new ArrayList(entries.size());
				Iterator it = entries.iterator();
				while (it.hasNext()) {
					// Get the next entry in the search results.
					Map<String, String> entry = ((Map) it.next());

					// Pull information about this entry from the
					// search results.
					GwtFolderEntry folderEntry = new GwtFolderEntry();
					String entryId = entry.get("_docId");
					folderEntry.setEntryId(entryId);
					String entryName = entry.get("title");
					folderEntry.setEntryName(entryName);
					String parentBinderName = entry.get(WebKeys.BINDER_PATH_NAME);
					folderEntry.setParentBinderName(parentBinderName);
					
					Long id = Long.valueOf(entryId);
					GwtFolderEntryType entryType = GwtFolderEntryTypeHelper.getFolderEntryType(bs, id);
					folderEntry.setEntryType(entryType);

					results.add(folderEntry);
				}
				
				searchResults.setResults(results);
				break;
			}

			case GROUP:  {
				// Do we get any search hits?
		    	ArrayList<GwtTeamingItem> results = null;
				Map entries = bs.getProfileModule().getGroups(options);
		    	List searchEntries = ((List) entries.get(ObjectKeys.SEARCH_ENTRIES));
		    	Integer searchHits = ((Integer) entries.get(ObjectKeys.SEARCH_COUNT_TOTAL));
		    	if ((null != searchEntries) && (null != searchHits) && (0 < searchHits)) {
					// Yes!
					results = new ArrayList(searchEntries.size());

					// Is this search a part of sending an email UI?
					// And, are we supposed to disallow sending email
					// to the 'all users' group?
					boolean sendingEmail        = searchCriteria.getIsSendingEmail();
					boolean allowSendToAllUsers = SPropsUtil.getBoolean("mail.allowSendToAllUsers", false);

					// Create a GwtGroup object for each group returned
					// by the search.
					int size = searchEntries.size();
					for (int i = (size - 1); i >= 0; i -= 1) {
						boolean useGroup = true;
			    		Map entry = ((Map) searchEntries.get(i));
						String id = ((String) entry.get(Constants.RESERVEDID_FIELD));
						
						// Is this search part of a sending email UI?
						if (sendingEmail) {
							// Is sending an email to all users allowed?
							if (!allowSendToAllUsers) {
								// No!  Is this group an 'all users'
								// group?
								if ((null != id) &&
										(id.equalsIgnoreCase(ObjectKeys.ALL_USERS_GROUP_INTERNALID) ||
										 id.equalsIgnoreCase(ObjectKeys.ALL_EXT_USERS_GROUP_INTERNALID))) {
									// Yes, skip it.
									useGroup = false;
									searchHits -= 1;
								}
							}
						}
						
						// Should we add this group to the search
						// results?
						if (useGroup) {
							SortedSet<Principal> groupPrincipals;
							List<Long> groupId = new ArrayList<Long>();
							id = ((String) entry.get("_docId"));
							groupId.add(Long.valueOf(id));
							ProfileModule pm = bs.getProfileModule();
							groupPrincipals  = pm.getPrincipals(groupId);
							if (groupPrincipals.size() > 0 ) {
								Principal group = groupPrincipals.first();
								IdentityInfo identityInfo = group.getIdentityInfo();
								if (null != identityInfo) {
									// Are we suppose to include groups
									// from LDAP?
									if (!(searchCriteria.getSearchForLdapGroups())) {
										// No!  Is this group from
										// LDAP?
										if (identityInfo.isFromLdap()) {
											useGroup = false;
										}
									}
								}
								
								else {
									useGroup = false;
								}
								
								if (useGroup) {
									// Yes!  Create a GwtGroup item for
									// this group.
									GwtGroup gwtGroup = new GwtGroup();
									if (group instanceof UserPrincipal) {
										gwtGroup.setInternal(identityInfo.isInternal());
									}
									gwtGroup.setId(id);

									String name  = group.getName();
									String title = group.getTitle();
									if ((group instanceof Group) && ((Group)group).isTeamGroup()) {
										// Use the title of the group
										// instead of the group name
										// for display purposes.
										name = title;
									}
									gwtGroup.setName( name );
									gwtGroup.setTitle(title);
									gwtGroup.setDn(group.getForeignName());
									Description desc = group.getDescription();
									if (desc != null) {
										gwtGroup.setDesc(desc.getText());
									}
									gwtGroup.setGroupType(GwtServerHelper.getGroupType(group));
									results.add(gwtGroup);
								}
								
								else {
									searchHits -= 1;
								}
							}
						}
			    	}
		    	}
						
				searchResults.setCountTotal(searchHits.intValue());
				searchResults.setResults(results);
				break;
			}
				
			case FOLDERS:
			case PLACES:  {
				retMap = bs.getBinderModule().executeSearchQuery(searchTermFilter.getFilter(), Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, options);

				// Add the search results to the GwtSearchResults
				// object.
				Integer count = ((Integer) retMap.get(ObjectKeys.SEARCH_COUNT_TOTAL));
				searchResults.setCountTotal(count.intValue());
				
				// Create a GwtFolder item for each search result.
				List placesEntries = ((List) retMap.get(ObjectKeys.SEARCH_ENTRIES));
				ArrayList<GwtTeamingItem> results = new ArrayList(placesEntries.size());
				Iterator it = placesEntries.iterator();
				while (it.hasNext()) {
					// Get the next folder in the search results.
					Map<String, String> entry = ((Map) it.next());

					// Pull information about this folder from the search results.
					String folderId    = entry.get("_docId"        );
					String folderTitle = entry.get("_extendedTitle");
					GwtFolder folder = GwtServerHelper.getFolderImpl(bs, request, null, folderId, folderTitle);
					if (folder != null) {
						results.add(folder);
					}
				}
				searchResults.setResults(results);
				break;
			}

			case COMMUNITY_TAGS:
			case PERSONAL_TAGS:
			case TAG:  {
				String searchRoot = searchText;
				int i = searchRoot.indexOf("*");
				if (i > 0) {
					searchRoot = searchRoot.substring(0, i);
				}
				
				String tagType;
				switch (searchCriteria.getSearchType()) {
				default:
				case TAG:             tagType = WebKeys.FIND_TYPE_TAGS;           break;
				case COMMUNITY_TAGS:  tagType = WebKeys.FIND_TYPE_COMMUNITY_TAGS; break;
				case PERSONAL_TAGS:   tagType = WebKeys.FIND_TYPE_PERSONAL_TAGS;  break;
				}
				
				List tags = bs.getBinderModule().getSearchTags(searchRoot, tagType);
				int count = ((null == tags) ? 0 : tags.size());
				searchResults.setCountTotal(count);
				ArrayList<GwtTeamingItem> results = new ArrayList(count);
				for (Iterator it = tags.iterator(); it.hasNext();) {
					Map<String, String> tagInfo = ((Map) it.next());
					GwtTag tag = new GwtTag();
					tag.setTagName(tagInfo.get("ssTag"));
					results.add(tag);
				}
				searchResults.setResults(results);
				break;
			}
			
			case TEAMS:  {
				// Search for teams.
				retMap = bs.getBinderModule().executeSearchQuery(searchTermFilter.getFilter(), Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, options);
				
				// Add the search results to the GwtSearchResults object.
				Integer count = ((Integer) retMap.get(ObjectKeys.SEARCH_COUNT_TOTAL));
				searchResults.setCountTotal(count.intValue());
				
				// Create a GwtTeam item for each search result.
				List teamEntries = ((List) retMap.get(ObjectKeys.SEARCH_ENTRIES));
				ArrayList<GwtTeamingItem> results = new ArrayList(teamEntries.size());
				Iterator it = teamEntries.iterator();
				while (it.hasNext()) {
					// Get the next team in the search results.
					Map<String, String> entry = ((Map) it.next());

					// Pull information about this team from the search
					// results.
					String teamId = entry.get("_docId");
					
					//~JW:  Finish
				}
				searchResults.setResults(results);
				break;
			}

			case PERSON:
			case USER:  {
				retMap = bs.getProfileModule().getUsers(options);

				// Add the search results to the GwtSearchResults
				// object.
				Integer count = ((Integer) retMap.get(ObjectKeys.SEARCH_COUNT_TOTAL));
				searchResults.setCountTotal(count.intValue());
				
				// Create a GwtUser item for each search result.
				List userEntries = ((List) retMap.get(ObjectKeys.SEARCH_ENTRIES));
				ArrayList<GwtTeamingItem> results = new ArrayList(userEntries.size());
				Iterator it = userEntries.iterator();
				while (it.hasNext()) {
					// Get the next user in the search results.
					Map<String, String> entry = ((Map) it.next());

					// Pull information about this user from the search
					// results.
					String userId = entry.get("_docId");
					GwtUser gwtUser = getGwtUser(bs, request, searchType, userId);
					if (gwtUser != null) {
						// Is this an external user?
						if (!(gwtUser.isInternal())) {
							// Yes!  Should we return external users?
							if (!(searchCriteria.getSearchForExternalPrincipals())) {
								// No!
								continue;
							}
						}
						
						results.add(gwtUser);
					}
				}
				searchResults.setResults(results);
				break;
			}
			
			case PRINCIPAL:  {
				retMap = bs.getProfileModule().getPrincipals(options);
				Integer count = ((Integer) retMap.get(ObjectKeys.SEARCH_COUNT_TOTAL));
				
				// Create a list of principal IDs from the search
				// results.
				List principalEntries = ((List) retMap.get(ObjectKeys.SEARCH_ENTRIES));
				ArrayList<GwtTeamingItem> results = new ArrayList(principalEntries.size());
				List<Long> principalIds = new ArrayList<Long>();
				Iterator it = principalEntries.iterator();
				while (it.hasNext()) {
					// Get the next principal in the search results.
					Map<String, String> entry = ((Map) it.next());
					if (entry != null) {
						String id = ((String) entry.get("_docId"));
						principalIds.add(Long.valueOf(id));
					}
				}
				
				if (principalIds.size() > 0) {
					ProfileModule pm = bs.getProfileModule();
					SortedSet<Principal> principals = pm.getPrincipals(principalIds);
					if (MiscUtil.hasItems(principals)) {
						it = principals.iterator();
						while (it.hasNext()) {
							EntityType entityType;
							String principalId;
							
							Principal principal = ((Principal) it.next());
							IdentityInfo identityInfo = principal.getIdentityInfo();

							// Are we dealing with the _postingagent,
							// _jobprocessingagent,
							// _synchronizationagent, or
							// _filesyncagent?
							String internalId = principal.getInternalId();
							if ((internalId != null) &&
								 (internalId.equalsIgnoreCase(ObjectKeys.ANONYMOUS_POSTING_USER_INTERNALID) ||
								  internalId.equalsIgnoreCase(ObjectKeys.JOB_PROCESSOR_INTERNALID         ) ||
								  internalId.equalsIgnoreCase(ObjectKeys.SYNCHRONIZATION_AGENT_INTERNALID ) ||
								  internalId.equalsIgnoreCase(ObjectKeys.FILE_SYNC_AGENT_INTERNALID     ))) {
								// Yes!
								if (count != null) {
									count -= 1;
								}
								continue;
							}

							// Are we dealing with an external user/group?
							if ((identityInfo != null) && (!(identityInfo.isInternal()))) {
								// Yes, we never want to return an
								// external user/group.
								if (count != null) {
									count -= 1;
								}
								continue;
							}
							
							principalId = principal.getId().toString();
							entityType = principal.getEntityType();
							if (entityType.equals(EntityType.group)) {
								// Is this group from LDAP?
								if ((identityInfo != null) && identityInfo.isFromLdap()) {
									// Yes!  Are we supposed to include
									// groups from LDAP?
									if (!(searchCriteria.getSearchForLdapGroups())) {
										// No!
										if (count != null) {
											count -= 1;
										}
										continue;
									}
								}
								
								// Create a GwtGroup item for this group.
								GwtGroup gwtGroup = new GwtGroup();
								if ((principal instanceof UserPrincipal) && (identityInfo != null)) {
									gwtGroup.setInternal(identityInfo.isInternal());
								}
								gwtGroup.setId(principalId);

								String name  = principal.getName();
								String title = principal.getTitle();
								if ((principal instanceof Group) && ((Group)principal).isTeamGroup()) {
									// Use the title of the group
									// instead of the group name for
									// display purposes.
									name = title;
								}

								gwtGroup.setName( name );
								gwtGroup.setTitle(title);
								gwtGroup.setDn(principal.getForeignName());
								Description desc = principal.getDescription();
								if (desc != null) {
									gwtGroup.setDesc(desc.getText());
								}
								gwtGroup.setGroupType(GwtServerHelper.getGroupType(principal));
								results.add(gwtGroup);
							}
							
							else if (entityType.equals(EntityType.user)) {
								GwtUser gwtUser = getGwtUser(bs, request, searchType, principalId);
								if (gwtUser != null) {
									results.add(gwtUser);
								}
							}
						}
					}
				}
				
				// Add the search results to the GwtSearchResults
				// object.
				searchResults.setCountTotal(count.intValue());
				searchResults.setResults(results);
				break;
			}
			
			case PROXY_IDENTITY:  {
				// Query the matching proxy identities, using the
				// search text as a quick filter.
				options.put(ObjectKeys.SEARCH_QUICK_FILTER, stripTrailingWildcards(searchText));
				Map proxyIdentities = bs.getProxyIdentityModule().getProxyIdentities(options);

				// Did we find any proxy identities?
				List<ProxyIdentity>       piList  = ((List<ProxyIdentity>) proxyIdentities.get(ObjectKeys.SEARCH_ENTRIES));
				Long                      count   = ((Long) proxyIdentities.get(ObjectKeys.SEARCH_COUNT_TOTAL));
				ArrayList<GwtTeamingItem> results = new ArrayList(count.intValue());
				if (MiscUtil.hasItems(piList)) {
					// Yes!  Scan them...
					for (ProxyIdentity pi:  piList) {
						// ...adding a GwtProxyIdentity for each to the
						// ...results.
						GwtProxyIdentity gwtPI = GwtProxyIdentityHelper.convertPIToGwtPI(pi);
						results.add(gwtPI);
					}
				}
				
				// Finalize the search results.
				searchResults.setCountTotal(results.size());
				searchResults.setResults(results);
				
				break;
			}
				
			default:
				searchResults = null;
				break;
			}
		}
		
		catch(AccessControlException e) {
			//~JW:  What to do here?
			searchResults = null;
		}
		
		return searchResults;
	}


	/*
	 * Return a GwtUser object for the given user ID.
	 */
	private static GwtUser getGwtUser(AllModulesInjected bs, HttpServletRequest request, GwtSearchCriteria.SearchType searchType, String userId) throws GwtTeamingException {
		Binder  binder = null;
		GwtUser reply  = null;
		User    user  = null;
		
		try {
			// Do we have an ID we can access as a person?
			Long userIdL = new Long(userId);
			if (userIdL != null) {
				try {
					user = bs.getProfileModule().getUserDeadOrAlive(userIdL);
				}
				catch (NoUserByTheIdException ex) {
					// Nothing to do
				}
				
				if ((user != null) && (!(user.isDeleted()))) {
					// If we are searching for a person and this user
					// is not a person...
					if (searchType.equals(GwtSearchCriteria.SearchType.PERSON) && (!(user.isPerson()))) {
						// ...ignore it.
						user = null;
					}
					
					else {
						// Does this user have a workspace ID?
						Long wsId = user.getWorkspaceId();
						if (null != wsId) {
							try {
								// Yes!  Can we access the workspace?
								binder = bs.getBinderModule().getBinder(user.getWorkspaceId());
							}
							catch (Exception ex) {
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
			if (null != user) {
				// Yes!  Construct a GwtUser object for it.
				reply = new GwtUser();
				reply.setInternal(user.getIdentityInfo().isInternal());
				reply.setPrincipalType(GwtViewHelper.getPrincipalType(user));
				reply.setUserId(user.getId());
				reply.setName(user.getName());
				reply.setTitle(Utils.getUserTitle(user));
				reply.setWorkspaceTitle(user.getWSTitle());
				reply.setEmail(user.getEmailAddress());
				reply.setAvatarUrl(GwtServerHelper.getUserAvatarUrl(bs, request, user));
				reply.setDisabled(user.isDisabled());
				
				GwtServerHelper.setExtUserProvState(reply, user);
				
				// Do we have access to this user's workspace?
				if (null == binder) {
					// No!  Provide a permalink to the user's profile.
					reply.setViewWorkspaceUrl(PermaLinkUtil.getPermalink(request, user));
				}
				else {
					// Yes, we have access to this user's workspace!
					// Store the workspace's ID and a permalink to it. 
					reply.setWorkspaceId(binder.getId());
					reply.setViewWorkspaceUrl(PermaLinkUtil.getPermalink(request, binder));
				}
			}
		}
		
		catch (Exception e) {
			throw GwtLogHelper.getGwtClientException(e);
		}
	
		return reply;
	}

	/*
	 * Returns the given string with any trailing wild card characters
	 * stripped off.
	 */
	private static String stripTrailingWildcards(String stripThis) {
		// We're we given a string?
		if (null != stripThis) {
			// Yes!  Does it end with a wild card character?
			stripThis = stripThis.trim();
			if (stripThis.endsWith("*") || stripThis.endsWith("?")) {
				// Yes!  Strip it.
				stripThis = stripThis.substring(0, (stripThis.length() - 1));
			}
		}
		
		// If we get here, stripThis refers to the given string with
		// any wild card characters stripped off.  Return it.
		return stripThis;
	}
}

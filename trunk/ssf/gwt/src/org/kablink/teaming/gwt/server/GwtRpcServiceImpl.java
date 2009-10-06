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
package org.kablink.teaming.gwt.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.ExtensionInfo;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtSearchResults;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.admin.ExtensionInfoClient;
import org.kablink.teaming.gwt.client.service.GwtRpcService;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.teaming.search.filter.SearchFilterKeys;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.AbstractAllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.util.search.Constants;



/**
 * 
 * @author jwootton
 *
 */
public class GwtRpcServiceImpl  extends AbstractAllModulesInjected
	implements GwtRpcService
{


/**
	 * This method is meant to search for applications or entries or groups or places or tags or teams or users.
	 */
	@SuppressWarnings("unchecked")
	private GwtSearchResults doSearch( GwtSearchCriteria searchCriteria )
	{
		Map options;
		List searchTerms;
		SearchFilter searchTermFilter;
		String searchText;
		Integer startingCount;
		Integer maxResults;
		GwtSearchResults searchResults = null;
		
		// Make sure we are dealing with the right search type.
		switch ( searchCriteria.getSearchType() )
		{
		case APPLICATION:
		case APPLICATION_GROUP:
		case COMMUNITY_TAGS:
		case ENTRIES:
		case GROUP:
		case PERSONAL_TAGS:
		case PLACES:
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
		
		searchText = searchCriteria.getSearchText();
	    searchText = searchText.replaceAll(" \\*", "\\*").trim();

		searchTermFilter = new SearchFilter();
		
	    // Set up the search filter.
		switch ( searchCriteria.getSearchType() )
		{
		case ENTRIES:
			String binderId;
			
			//Add the title term
			if ( searchText.length() > 0 )
				searchTermFilter.addTitleFilter( searchText );

			searchTerms = new ArrayList();
			searchTerms.add( EntityIdentifier.EntityType.folderEntry.name() );
			searchTermFilter.addAndNestedTerms( SearchFilterKeys.FilterTypeEntityTypes, SearchFilterKeys.FilterEntityType, searchTerms );
			searchTermFilter.addAndFilter( SearchFilterKeys.FilterTypeTopEntry );
			
			//Add terms to search this folder
			binderId = searchCriteria.getBinderId();
			if ( binderId != null && !binderId.equals( "" ) )
			{
				if ( searchCriteria.getSearchSubfolders() == false )
				{
					searchTermFilter.addAndFolderId( binderId );
				}
				else
				{
					searchTermFilter.addAncestryId( binderId );
				}
			}
			break;

		case PLACES:
			searchTermFilter.addPlacesFilter( searchText, searchCriteria.getFoldersOnly() );
			break;
			
		case TAG:
			//!!! Get code from ajaxFind() in TypeToFindAjaxController.java
			break;
		
		case TEAMS:
			searchTermFilter.addTeamFilter();
			break;
			
		default:
			//Add the login name term
			if ( searchText.length() > 0 )
			{
				searchTermFilter.addTitleFilter( searchText );
				searchTermFilter.addLoginNameFilter( searchText );
			}
			break;
		}// end switch()

		try
		{
			Map retMap;

			searchResults = new GwtSearchResults();
		
			options.put( ObjectKeys.SEARCH_SEARCH_FILTER, searchTermFilter.getFilter() );
			
			// Perform the search based on the search type.
			switch ( searchCriteria.getSearchType() )
			{
			case APPLICATION:
				Map appEntries;
				
				appEntries = getProfileModule().getApplications( options );
				break;
				
			case APPLICATION_GROUP:
				Map appGroupEntries;
				
				appGroupEntries = getProfileModule().getApplicationGroups( options );
				break;
				
			case ENTRIES:
				List placesWithCounters;
				List entries;
				ArrayList<GwtTeamingItem> results;
				Iterator it;
				Map foldersMap;
				Integer count;
				
				retMap = getBinderModule().executeSearchQuery( searchTermFilter.getFilter(), options );
				entries = (List)retMap.get( ObjectKeys.SEARCH_ENTRIES );
				placesWithCounters = BinderHelper.sortPlacesInEntriesSearchResults( getBinderModule(), entries );
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
					results.add( folderEntry );
				}
				searchResults.setResults( results);
				break;

			case GROUP:
				//!!! Finish, grab code from ajaxFind() in TypeToFindAjaxController.java
				break;
				
			case PLACES:
				List placesEntries;
				
				retMap = getBinderModule().executeSearchQuery( searchTermFilter.getFilter(), options );
				placesEntries = (List)retMap.get( ObjectKeys.SEARCH_ENTRIES );
				break;

			case COMMUNITY_TAGS:
			case PERSONAL_TAGS:
			case TAG:
				//!!! Finish, grab code from ajaxFild() in TypeToFindAjaxController.java
				break;
			
			case TEAMS:
				List teamsEntries;

				retMap = getBinderModule().executeSearchQuery( searchTermFilter.getFilter(), options );

				teamsEntries = (List)retMap.get( ObjectKeys.SEARCH_ENTRIES );
				break;
				
			case USER:
				Map userEntries;
				List resultList;
				
				userEntries = getProfileModule().getUsers(options);
				
				resultList = (List)userEntries.get( ObjectKeys.SEARCH_ENTRIES );
				if ( searchCriteria.getAddCurrentUser() && searchCriteria.getPageNumber() == 0 && (searchText.equals("") || searchText.equals("*")))
				{
					// add relative option "current user" and "me"
					Map currentUserPlaceholder = new HashMap();
					currentUserPlaceholder.put("title", NLT.get("searchForm.currentUserTitle"));
					currentUserPlaceholder.put("_docId", SearchFilterKeys.CurrentUserId);
					resultList.add(0, currentUserPlaceholder);
				}
				break;
				
			default:
				searchResults = null;
			}// end switch()
		}
		catch( AccessControlException e )
		{
			//!!! What to do here?
			searchResults = null;
		}
		
		return searchResults;
	}// end doSearch()
	
	
	/**
	 * Execute a search based on the given search criteria.
	 */
	public GwtSearchResults executeSearch( GwtSearchCriteria searchCriteria )
	{
		GwtSearchResults searchResults;
		
		switch ( searchCriteria.getSearchType() )
		{
		case APPLICATION:
		case APPLICATION_GROUP:
		case COMMUNITY_TAGS:
		case ENTRIES:
		case GROUP:
		case PERSONAL_TAGS:
		case PLACES:
		case TAG:
		case TEAMS:
		case USER:
			searchResults = doSearch( searchCriteria );
			break;
				
		default:
			//!!! Finish.
			searchResults = null;
			break;
		}
		
		return searchResults;
	}// end executeSearch()
	
	
	/**
	 * Return an Entry object for the given entry id
	 */
	public GwtFolderEntry getEntry( String entryId )
	{
		FolderModule folderModule;
		FolderEntry entry;
		GwtFolderEntry folderEntry = null;
		Binder parentBinder;
		
		//!!! Catch exceptions and do appropriate exception handling.
		try
		{
			folderModule = getFolderModule();
			entry = folderModule.getEntry( null, new Long( entryId ) );
			
			// Initialize the data members of the GwtFolderEntry object.
			folderEntry = new GwtFolderEntry();
			folderEntry.setEntryId( entryId );
			if ( entry != null )
			{
				folderEntry.setEntryName( entry.getTitle() );
			
				parentBinder = entry.getParentBinder();
				folderEntry.setParentBinderName( parentBinder.getPathName() );
			}
		}
		catch (Exception ex)
		{
		}
		
		return folderEntry;
	}// end getEntry()
	
	
	/**
	 * Return a Folder object for the given folder id
	 */
	public GwtFolder getFolder( String folderId )
	{
		BinderModule binderModule;
		Binder binder;
		GwtFolder folder = null;
		Binder parentBinder;
		
		//!!! Catch exceptions and do appropriate exception handling.
		try
		{
			binderModule = getBinderModule();
			binder = binderModule.getBinder( new Long( folderId ) );
			
			// Initialize the data members of the GwtFolder object.
			folder = new GwtFolder();
			folder.setFolderId( folderId );
			if ( binder != null )
			{
				folder.setFolderName( binder.getTitle() );
			
				parentBinder = binder.getParentBinder();
				folder.setParentBinderName( parentBinder.getPathName() );
			}
		}
		catch (Exception ex)
		{
		}
		
		return folder;
	}// end getFolder()
	
	
    /**
     * 
     */
    public String getTutorialPanelState()
    {
    	UserProperties	userProperties;
    	ProfileModule	profileModule;
    	String			tutorialPanelState;
    	
    	profileModule = getProfileModule();

    	if ( profileModule == null )
    		return "!!! profileModule is null!!!";
    	
    	userProperties = profileModule.getUserProperties( null );
		tutorialPanelState = (String) userProperties.getProperty( ObjectKeys.USER_PROPERTY_TUTORIAL_PANEL_STATE );

		if ( tutorialPanelState == null )
			tutorialPanelState = "Not defined";
		
    	return tutorialPanelState;
    }// end getTutorialPanelState()
    
    
    public ExtensionInfoClient[] getExtensionInfo()
    {
    	List<ExtensionInfo> extList =  new ArrayList<ExtensionInfo>(); 
    	AdminModule adminModule;
    	
    	adminModule = getAdminModule();
    	if(adminModule == null)
    	{
    		ExtensionInfoClient[] infoArray = new ExtensionInfoClient[0];
        	return infoArray;
    	}

    	extList = adminModule.getExtensionManager().getExtensions();
    	ArrayList<ExtensionInfoClient> list = new ArrayList<ExtensionInfoClient>();

    	for(ExtensionInfo info: extList){
        	ExtensionInfoClient client = new ExtensionInfoClient();
        	
        	client.setId(info.getId());
        	client.setName(info.getName());
        	client.setDescription("Descriptions "+info.getZoneId());
        	
        	list.add(client);
    	}

    	ExtensionInfoClient[] infoArray = new ExtensionInfoClient[list.size()];
    	list.toArray(infoArray);
    	
    	return infoArray;
    	
    }

    public ExtensionInfoClient[] removeExtension(String id)
    {
    	AdminModule adminModule;
    	adminModule = getAdminModule();
    	if(adminModule == null) {
    		//Throw an exception
    		getExtensionInfo();
    	}
    		
    	adminModule.getExtensionManager().removeExtensions(id);
    	return getExtensionInfo();
    }

    
}// end GwtRpcServiceImpl

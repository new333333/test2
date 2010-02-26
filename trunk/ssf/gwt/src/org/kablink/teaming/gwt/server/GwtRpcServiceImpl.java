/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoFolderEntryByTheIdException;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.gwt.client.GwtBrandingData;
import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtSearchResults;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.admin.ExtensionDefinitionInUseException;
import org.kablink.teaming.gwt.client.admin.ExtensionFiles;
import org.kablink.teaming.gwt.client.admin.ExtensionInfoClient;
import org.kablink.teaming.gwt.client.service.GwtRpcService;
import org.kablink.teaming.gwt.server.util.GwtServerHelper;
import org.kablink.teaming.gwt.client.util.TreeInfo;
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
import org.kablink.teaming.web.util.ExportHelper;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.util.search.Constants;


/**
 * 
 * @author jwootton
 *
 */
public class GwtRpcServiceImpl extends AbstractAllModulesInjected
	implements GwtRpcService
{


/**
	 * This method is meant to search for applications or entries or groups or places or tags or teams or users.
	 */
	@SuppressWarnings("unchecked")
	private GwtSearchResults doSearch( GwtSearchCriteria searchCriteria ) throws Exception
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
			//!!! Get code from ajaxFind() in TypeToFindAjaxController.java
			break;
			
		default:
			//!!! Get code from ajaxFind() in TypeToFindAjaxController.java
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
				//!!! Get code from ajaxFind() in TypeToFindAjaxController.java
				break;
				
			case APPLICATION_GROUP:
				//!!! Get code from ajaxFind() in TypeToFindAjaxController.java
				break;
				
			case ENTRIES:
			{
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
			}

			case GROUP:
				//!!! Finish, grab code from ajaxFind() in TypeToFindAjaxController.java
				break;
				
			case PLACES:
			{
				List placesEntries;
				ArrayList<GwtTeamingItem> results;
				Iterator it;
				Integer count;
				
				retMap = getBinderModule().executeSearchQuery( searchTermFilter.getFilter(), options );

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

					// Get the next folder in the search results.
					entry = (Map) it.next();

					// Pull information about this folder from the search results.
					folderId = entry.get( "_docId" );
					folder = getFolder( null, folderId );
					if ( folder != null )
						results.add( folder );
				}
				searchResults.setResults( results);
				break;
			}

			case COMMUNITY_TAGS:
			case PERSONAL_TAGS:
			case TAG:
				//!!! Finish, grab code from ajaxFild() in TypeToFindAjaxController.java
				break;
			
			case TEAMS:
				//!!! Get code from ajaxFind() in TypeToFindAjaxController.java
				break;
				
			case USER:
				//!!! Get code from ajaxFind() in TypeToFindAjaxController.java
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
	public GwtSearchResults executeSearch( GwtSearchCriteria searchCriteria ) throws Exception
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
	 * Return a GwtBrandingData object from the corporate branding.
	 */
	public GwtBrandingData getCorporateBrandingData()
	{
		//!!! Finish
		return new GwtBrandingData();
	}// end getCorporateBrandingData()
	
	
	/**
	 * Return a GwtBrandingData object for the given binder.
	 */
	public GwtBrandingData getBinderBrandingData( String binderId ) throws GwtTeamingException
	{
		BinderModule binderModule;
		Binder binder;
		Long binderIdL;
		GwtBrandingData brandingData;
		
		brandingData = new GwtBrandingData();
		
		try
		{
			binderModule = getBinderModule();
	
			binderIdL = new Long( binderId );
			
			// Get the binder object.
			if ( binderIdL != null )
			{
				String branding;
				
				binder = binderModule.getBinder( binderIdL );
				
				// Get the branding for the binder.
				branding = binder.getBranding();
				brandingData.setBranding( branding );
			}
		}
		catch (NoBinderByTheIdException nbEx)
		{
			GwtTeamingException ex;
			
			ex = new GwtTeamingException();
			ex.setExceptionType( ExceptionType.NO_BINDER_BY_THE_ID_EXCEPTION );
			throw ex;
		}
		catch (AccessControlException acEx)
		{
			GwtTeamingException ex;
			
			ex = new GwtTeamingException();
			ex.setExceptionType( ExceptionType.ACCESS_CONTROL_EXCEPTION );
			throw ex;
		}
		catch (Exception e)
		{
			GwtTeamingException ex;
			
			ex = new GwtTeamingException();
			ex.setExceptionType( ExceptionType.UNKNOWN );
			throw ex;
		}
		
		return brandingData;
	}// end getBinderBrandingData()
	
	
	/**
	 * Return an Entry object for the given zone and entry id
	 */
	public GwtFolderEntry getEntry( String zoneUUID, String entryId ) throws GwtTeamingException
	{
		FolderModule folderModule;
		FolderEntry entry = null;
		GwtFolderEntry folderEntry = null;
		Binder parentBinder;
		
		try
		{
			ZoneInfo zoneInfo;
			String zoneInfoId;
			Long entryIdL;

			// Get the id of the zone we are running in.
			zoneInfo = ExportHelper.getZoneInfo();
			zoneInfoId = zoneInfo.getId();
			if ( zoneInfoId == null )
				zoneInfoId = "";

			folderModule = getFolderModule();

			entryIdL = new Long( entryId );
			
			// Are we looking for an entry that was imported from another zone?
			if ( zoneUUID != null && zoneUUID.length() > 0 && !zoneInfoId.equals( zoneUUID ) )
			{
				// Yes, get the entry id for the entry in this zone.
				entryIdL = folderModule.getZoneEntryId( entryIdL, zoneUUID );
			}

			// Get the entry object.
			if ( entryIdL != null )
				entry = folderModule.getEntry( null, entryIdL );
			
			// Initialize the data members of the GwtFolderEntry object.
			folderEntry = new GwtFolderEntry();
			if ( entryIdL != null )
				folderEntry.setEntryId( entryIdL.toString() );
			if ( entry != null )
			{
				Long parentBinderId;
				String url;
				
				folderEntry.setEntryName( entry.getTitle() );
			
				parentBinder = entry.getParentBinder();
				if ( parentBinder != null )
				{
					parentBinderId = parentBinder.getId();
					folderEntry.setParentBinderName( parentBinder.getPathName() );
					folderEntry.setParentBinderId( parentBinderId );
				}
				
				// Create a url that can be used to view this entry.
				url = PermaLinkUtil.getPermalink( entry );
				folderEntry.setViewEntryUrl( url );
			}
		}
		catch (NoFolderEntryByTheIdException nbEx)
		{
			GwtTeamingException ex;
			
			ex = new GwtTeamingException();
			ex.setExceptionType( ExceptionType.NO_FOLDER_ENTRY_BY_THE_ID_EXCEPTION );
			throw ex;
		}
		catch (AccessControlException acEx)
		{
			GwtTeamingException ex;
			
			ex = new GwtTeamingException();
			ex.setExceptionType( ExceptionType.ACCESS_CONTROL_EXCEPTION );
			throw ex;
		}
		catch (Exception e)
		{
			GwtTeamingException ex;
			
			ex = new GwtTeamingException();
			ex.setExceptionType( ExceptionType.UNKNOWN );
			throw ex;
		}
		
		
		return folderEntry;
	}// end getEntry()
	
	
	/**
	 * Return a Folder object for the given folder id
	 */
	public GwtFolder getFolder( String zoneUUID, String folderId ) throws GwtTeamingException
	{
		BinderModule binderModule;
		Binder binder = null;
		GwtFolder folder = null;
		Binder parentBinder;
		
		try
		{
			ZoneInfo zoneInfo;
			String zoneInfoId;
			Long folderIdL;

			// Get the id of the zone we are running in.
			zoneInfo = ExportHelper.getZoneInfo();
			zoneInfoId = zoneInfo.getId();
			if ( zoneInfoId == null )
				zoneInfoId = "";

			binderModule = getBinderModule();

			folderIdL = new Long( folderId );
			
			// Are we looking for a folder that was imported from another zone?
			if ( zoneUUID != null && zoneUUID.length() > 0 && !zoneInfoId.equals( zoneUUID ) )
			{
				// Yes, get the folder id for the folder in this zone.
				folderIdL = binderModule.getZoneBinderId( folderIdL, zoneUUID, EntityType.folder.name() );
			}

			// Get the binder object.
			if ( folderIdL != null )
				binder = binderModule.getBinder( folderIdL );
			
			// Initialize the data members of the GwtFolder object.
			folder = new GwtFolder();
			if ( folderIdL != null )
				folder.setFolderId( folderIdL.toString() );
			if ( binder != null )
			{
				String url;

				folder.setFolderName( binder.getTitle() );
			
				parentBinder = binder.getParentBinder();
				if ( parentBinder != null )
					folder.setParentBinderName( parentBinder.getPathName() );

				// Create a url that can be used to view this folder.
				url = PermaLinkUtil.getPermalink( binder );
				folder.setViewFolderUrl( url );
			}
		}
		catch (NoBinderByTheIdException nbEx)
		{
			GwtTeamingException ex;
			
			ex = new GwtTeamingException();
			ex.setExceptionType( ExceptionType.NO_BINDER_BY_THE_ID_EXCEPTION );
			throw ex;
		}
		catch (AccessControlException acEx)
		{
			GwtTeamingException ex;
			
			ex = new GwtTeamingException();
			ex.setExceptionType( ExceptionType.ACCESS_CONTROL_EXCEPTION );
			throw ex;
		}
		catch (Exception e)
		{
			GwtTeamingException ex;
			
			ex = new GwtTeamingException();
			ex.setExceptionType( ExceptionType.UNKNOWN );
			throw ex;
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
        	
        	client.setAuthor(info.getAuthor());
        	client.setAuthorEmail(info.getAuthorEmail());
        	client.setAuthorSite(info.getAuthorSite());
        	client.setDateCreated(info.getDateCreated());
        	client.setDateDeployed(info.getDateDeployed());
        	client.setDescription(info.getDescription());
        	client.setId(info.getId());
        	client.setName(info.getName());
        	client.setZoneId(info.getZoneId());
        	client.setTitle(info.getTitle());
        	client.setVersion(info.getVersion());
        	
        	ZoneInfo zoneInfo = getZoneModule().getZoneInfo(info.getZoneId());
        	if(zoneInfo != null){
            	client.setZoneName(zoneInfo.getZoneName());
        	} 
        	
        	list.add(client);
    	}

    	ExtensionInfoClient[] infoArray = new ExtensionInfoClient[list.size()];
    	list.toArray(infoArray);
    	
    	return infoArray;
    	
    }

    public ExtensionInfoClient[] removeExtension(String id) throws ExtensionDefinitionInUseException
    {
    	AdminModule adminModule;
    	adminModule = getAdminModule();
    	
    	if( adminModule.getExtensionManager().checkDefinitionsInUse(id) )
    	{
    		throw new ExtensionDefinitionInUseException(NLT.get("definition.errror.inUse"));
    	}
    	adminModule.getExtensionManager().removeExtensions(id);

    	return getExtensionInfo();
    }


	public ExtensionFiles getExtensionFiles(String id, String zoneName) {
    	ExtensionFiles extFiles = new ExtensionFiles();
    	try 
    	{
    		AdminModule adminModule;
        	adminModule = getAdminModule();

        	ArrayList<String> results = adminModule.getExtensionManager().getExtensionFiles(id, zoneName);
        	extFiles.setResults(results);
        	extFiles.setCountTotal(results.size());
    	} catch (Exception e)
    	{
    	}
    	
    	return extFiles;
	}


	/**
	 * Returns a permalink to the currently logged in user's workspace.
	 * 
	 * @return
	 */
	public String getUserWorkspacePermalink() {
		Binder userWS = getBinderModule().getBinder(GwtServerHelper.getCurrentUser().getWorkspaceId());
		return PermaLinkUtil.getPermalink(userWS);
	}
	

	/**
	 * Returns a TreeInfo object containing the display information for
	 * the Binder referred to by binderId from the perspective of the
	 * currently logged in user.
	 * 
	 * @param binderIdS
	 * 
	 * @return
	 */
	public TreeInfo getTreeInfo(String binderIdS) {
		// Access the Binder's nearest containing Workspace...
		long binderId = Long.parseLong(binderIdS);
		Binder binder = getBinderModule().getBinder(binderId);
		Workspace binderWS = BinderHelper.getBinderWorkspace(binder);
		
		// ...note that the Workspace should always be expanded...
		Long binderWSId = binderWS.getId();
		ArrayList<Long> expandedBindersList = new ArrayList<Long>();
		expandedBindersList.add(binderWSId);

		// ...calculate which additional Binder's must be expanded to
		// ...show the requested Binder...
		long binderWSIdVal = binderWSId.longValue();
		if (binderId != binderWSIdVal) {
			binder = binder.getParentBinder();
			while (binder.getId().longValue() != binderWSIdVal) {
				expandedBindersList.add(binder.getId());
			}
		}
		
		// ...and build the TreeInfo for it.
		return GwtServerHelper.buildTreeInfoFromBinder(this, binderWS, expandedBindersList);
	}
	
	/**
	 * Saves the fact that the Binder for the given ID should be
	 * collapsed for the current User.
	 * 
	 * @param binderId
	 * 
	 * @return
	 */
	public Boolean collapseTreeNode(String binderId) {
		GwtServerHelper.collapseTreeNode(this, Long.parseLong(binderId));
		return Boolean.TRUE;
	}

	/**
	 * Saves the fact that the Binder for the given ID should be
	 * expanded for the current User.
	 * 
	 * @param binderId
	 * 
	 * @return
	 */
	public Boolean expandTreeNode(String binderId) {
		GwtServerHelper.expandTreeNode(this, Long.parseLong(binderId));
		return Boolean.TRUE;
	}
}// end GwtRpcServiceImpl

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
import java.util.Set;
import java.util.SortedSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.ExtensionInfo;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FileItem;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoFolderEntryByTheIdException;
import org.kablink.teaming.domain.NoUserByTheIdException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.gwt.client.GwtBrandingData;
import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtPersonalPreferences;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtSearchResults;
import org.kablink.teaming.gwt.client.GwtSelfRegistrationInfo;
import org.kablink.teaming.gwt.client.GwtTag;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.admin.ExtensionDefinitionInUseException;
import org.kablink.teaming.gwt.client.admin.ExtensionFiles;
import org.kablink.teaming.gwt.client.admin.ExtensionInfoClient;
import org.kablink.teaming.gwt.client.admin.GwtAdminCategory;
import org.kablink.teaming.gwt.client.admin.GwtUpgradeInfo;
import org.kablink.teaming.gwt.client.mainmenu.FavoriteInfo;
import org.kablink.teaming.gwt.client.mainmenu.GroupInfo;
import org.kablink.teaming.gwt.client.mainmenu.RecentPlaceInfo;
import org.kablink.teaming.gwt.client.mainmenu.SavedSearchInfo;
import org.kablink.teaming.gwt.client.mainmenu.TeamInfo;
import org.kablink.teaming.gwt.client.mainmenu.TeamManagementInfo;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.profile.DiskUsageInfo;
import org.kablink.teaming.gwt.client.profile.ProfileAttribute;
import org.kablink.teaming.gwt.client.profile.ProfileInfo;
import org.kablink.teaming.gwt.client.profile.ProfileStats;
import org.kablink.teaming.gwt.client.profile.UserStatus;
import org.kablink.teaming.gwt.client.service.GwtRpcService;
import org.kablink.teaming.gwt.client.util.ActivityStreamData;
import org.kablink.teaming.gwt.client.util.ActivityStreamData.PagingData;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamParams;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.TagInfo;
import org.kablink.teaming.gwt.client.util.TeamingAction;
import org.kablink.teaming.gwt.client.util.TopRankedInfo;
import org.kablink.teaming.gwt.client.util.WorkspaceType;
import org.kablink.teaming.gwt.client.workspacetree.TreeInfo;
import org.kablink.teaming.gwt.server.util.GwtActivityStreamHelper;
import org.kablink.teaming.gwt.server.util.GwtProfileHelper;
import org.kablink.teaming.gwt.server.util.GwtServerHelper;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.presence.PresenceInfo;
import org.kablink.teaming.presence.PresenceManager;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.teaming.search.filter.SearchFilterKeys;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.OperationAccessControlExceptionNoName;
import org.kablink.teaming.ssfs.util.SsfsUtil;
import org.kablink.teaming.util.AbstractAllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.Favorites;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.GwtUISessionData;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.TrashHelper;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.search.Constants;



/**
 * 
 * @author jwootton
 */
public class GwtRpcServiceImpl extends AbstractAllModulesInjected
	implements GwtRpcService
{
	protected static Log m_logger = LogFactory.getLog(GwtRpcServiceImpl.class);
	
	/*
	 * This method is meant to search for applications or entries or groups or places or tags or teams or users.
	 */
	@SuppressWarnings("unchecked")
	private GwtSearchResults doSearch( HttpServletRequest request, GwtSearchCriteria searchCriteria ) throws Exception
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
		case GROUP:
		case PERSON:
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
			
		case COMMUNITY_TAGS:
		case PERSONAL_TAGS:
		case TAG:
			// this has been replaced by a getTags method in the search engine.
			// searchTermFilter.addTagsFilter( null, searchText );
			break;
		
		case TEAMS:
			//!!! Get code from ajaxFind() in TypeToFindAjaxController.java
			break;

		case PERSON:
		case USER:
			searchTermFilter.addTitleFilter( searchText );
			searchTermFilter.addLoginNameFilter( searchText );
			if ( GwtSearchCriteria.SearchType.PERSON == searchType ) {
				searchTermFilter.addAndPersonFlagFilter( String.valueOf( Boolean.TRUE ) );
			}
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
			switch ( searchType )
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
					String folderTitle;

					// Get the next folder in the search results.
					entry = (Map) it.next();

					// Pull information about this folder from the search results.
					folderId = entry.get( "_docId" );
					folderTitle = entry.get( "_extendedTitle" );
					folder = getFolderImpl( request, null, folderId, folderTitle );
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
				
				tags = getBinderModule().getSearchTags( searchRoot, tagType );
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
				//!!! Get code from ajaxFind() in TypeToFindAjaxController.java
				break;

			case PERSON:
			case USER:
			{
				List userEntries;
				ArrayList<GwtTeamingItem> results;
				Iterator it;
				Integer count;
				
				retMap = getProfileModule().getUsers(options);

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
					gwtUser = getGwtUser( request, searchType, userId );
					if ( gwtUser != null )
						results.add( gwtUser );
				}
				searchResults.setResults( results);
				break;
			}
				
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
	 * 
	 * @param ri
	 * @param searchCriteria
	 * 
	 * @return
	 * 
	 * @throws Exception 
	 */
	public GwtSearchResults executeSearch( HttpRequestInfo ri, GwtSearchCriteria searchCriteria ) throws Exception
	{
		GwtSearchResults searchResults;
		
		switch ( searchCriteria.getSearchType() )
		{
		case APPLICATION:
		case APPLICATION_GROUP:
		case COMMUNITY_TAGS:
		case ENTRIES:
		case GROUP:
		case PERSON:
		case PERSONAL_TAGS:
		case PLACES:
		case TAG:
		case TEAMS:
		case USER:
			searchResults = doSearch( getRequest( ri ), searchCriteria );
			break;
				
		default:
			//!!! Finish.
			searchResults = null;
			break;
		}
		
		return searchResults;
	}// end executeSearch()
	
	/**
	 * Returns a ActivityStreamData of corresponding to activity stream
	 * parameters, paging data and info provided.
	 * 
	 * @param ri
	 * @param asp
	 * @param asi
	 * @param pagingData
	 * 
	 * @return
	 */
	public ActivityStreamData getActivityStreamData( HttpRequestInfo ri, ActivityStreamParams asp, ActivityStreamInfo asi, PagingData pagingData )
	{
		return GwtActivityStreamHelper.getActivityStreamData( getRequest( ri ), this, asp, asi, pagingData );
	}// end getActivityStreamData()
	
	public ActivityStreamData getActivityStreamData( HttpRequestInfo ri, ActivityStreamParams asp, ActivityStreamInfo asi )
	{
		// Always use the initial form of the method.
		return getActivityStreamData(ri, asp, asi, null);
	}// end getActivityStreamData()
	
	/**
	 * Returns an ActivityStreamParams object containing information
	 * the current activity stream setup.
	 * 
	 * @param ri
	 * 
	 * @return
	 */
	public ActivityStreamParams getActivityStreamParams( HttpRequestInfo ri )
	{
		return GwtActivityStreamHelper.getActivityStreamParams(this);
	}// end getActivityStreamParams()
	
	/**
	 * Returns the current user's default activity stream.  If they
	 * don't have one set in their user profile, null is returned.
	 * 
	 * @param ri
	 * @param currentBinderId
	 * 
	 * @return
	 */
	public ActivityStreamInfo getDefaultActivityStream( HttpRequestInfo ri, String currentBinderId )
	{
		return GwtActivityStreamHelper.getDefaultActivityStream( getRequest( ri ), this, currentBinderId );
	}// end getDefaultActivityStream()
	
	/**
	 * Returns true if the data for an activity stream has changed (or
	 * has never been cached) and false otherwise.
	 * 
	 * @param ri
	 * @param asi
	 * 
	 * @return
	 */
	public Boolean hasActivityStreamChanged( HttpRequestInfo ri, ActivityStreamInfo asi )
	{
		return GwtActivityStreamHelper.hasActivityStreamChanged( getRequest( ri ), this, asi );
	}// end hasActivityStreamChanged()
	
	/**
	 * Stores an ActivityStreamIn as the current user's default
	 * activity stream in their user profile.
	 * 
	 * @param ri
	 * @param asi
	 * 
	 * @return
	 */
	public Boolean persistActivityStreamSelection( HttpRequestInfo ri, ActivityStreamInfo asi )
	{
		return GwtActivityStreamHelper.persistActivityStreamSelection( getRequest( ri ), this, asi );
	}// end persistActivityStreamSelection()
	
	/**
	 * Return the administration options the user has rights to run.
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	public ArrayList<GwtAdminCategory> getAdminActions( HttpRequestInfo ri, String binderId ) throws GwtTeamingException
	{
		try
		{
			ArrayList<GwtAdminCategory> adminActions;
			BinderModule binderModule;
			Long binderIdL;
			
			binderModule = getBinderModule();
	
			binderIdL = new Long( binderId );
			
			if ( binderIdL != null )
			{
				Binder binder;
				
				binder = binderModule.getBinder( binderIdL );

				adminActions = GwtServerHelper.getAdminActions( getRequest( ri ), binder, this );
			}
			else
			{
				m_logger.warn( "In GwtRpcServiceImpl.getAdminActions(), binderIdL is null" );
				adminActions = new ArrayList<GwtAdminCategory>();
			}
			
			return adminActions;
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
		
	}// end getAdminActions()
	
	
	/**
	 * Return a GwtBrandingData object for the given binder.
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	public GwtBrandingData getBinderBrandingData( HttpRequestInfo ri, String binderId ) throws GwtTeamingException
	{
		return GwtServerHelper.getBinderBrandingData( this, binderId, getRequest( ri ) );
	}// end getBinderBrandingData()
	
	
	/**
	 * Return the "document base url" that is used in tinyMCE configuration
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	public String getDocumentBaseUrl( HttpRequestInfo ri, String binderId ) throws GwtTeamingException
	{
		String baseUrl = null;
		BinderModule binderModule;
		Binder binder;
		Long binderIdL;
		
		try
		{
			binderModule = getBinderModule();
	
			binderIdL = new Long( binderId );
			
			if ( binderIdL != null )
			{
				String webPath;
				
				binder = binderModule.getBinder( binderIdL );
				webPath = WebUrlUtil.getServletRootURL( getRequest( ri ) );
				baseUrl = WebUrlUtil.getFileUrl( webPath, WebKeys.ACTION_READ_FILE, binder, "" );
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
		
		return baseUrl;
	}// end getDocumentBaseUrl()
	
	
	/**
	 * Return an Entry object for the given zone and entry id
	 * 
	 * @param ri
	 * @param zoneUUID
	 * @param entryId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	public GwtFolderEntry getEntry( HttpRequestInfo ri, String zoneUUID, String entryId ) throws GwtTeamingException
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
			zoneInfo = MiscUtil.getCurrentZone();
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
				url = PermaLinkUtil.getPermalink( getRequest( ri ), entry );
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
	 * Get a permalink that can be used to view the given entry.
	 */
	public String getEntryPermalink( HttpRequestInfo ri, String entryId, String zoneUUID )
	{
		String reply = "";
		
		if ( entryId != null && entryId.length() > 0 )
		{
			FolderModule folderModule;
			Long entryIdL;
			ZoneInfo zoneInfo;
			String zoneInfoId;
			FolderEntry entry = null;

			// Get the id of the zone we are running in.
			zoneInfo = MiscUtil.getCurrentZone();
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
			
			reply = PermaLinkUtil.getPermalink( getRequest( ri ), entry );
		}
		
		return reply;
	}
	
	
	/**
	 * Return a list of the names of the files that are attachments for the given binder
	 * 
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	public ArrayList<String> getFileAttachments( String binderId ) throws GwtTeamingException
	{
		ArrayList<String> fileNames;
		
		fileNames = new ArrayList<String>();

		try
		{
			Long binderIdL;
			BinderModule binderModule;
			Binder binder = null;
			SortedSet<FileAttachment> attachments;

			binderModule = getBinderModule();

			// Get the binder object.
			binderIdL = new Long( binderId );
			binder = binderModule.getBinder( binderIdL );
			
			attachments = binder.getFileAttachments();
	        for(FileAttachment fileAttachment : attachments)
	        {
	        	String fileName;
	    		FileItem fileItem;
	        	
	           	fileItem = fileAttachment.getFileItem();
				fileName = fileItem.getName();
				fileNames.add( fileName );
			}// end for()
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

        return fileNames;
	}// end getFileAttachments()
	
	
	/**
	 * Return a Folder object for the given folder id.
	 * 
	 * @param ri
	 * @param zoneUUID
	 * @param folderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	public GwtFolder getFolder( HttpRequestInfo ri, String zoneUUID, String folderId ) throws GwtTeamingException
	{
		return getFolderImpl( getRequest( ri ), zoneUUID, folderId, null );
	}
	
	private GwtFolder getFolderImpl( HttpServletRequest request, String zoneUUID, String folderId, String folderTitle ) throws GwtTeamingException
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
			zoneInfo = MiscUtil.getCurrentZone();
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

				folder.setFolderName( MiscUtil.hasString( folderTitle ) ? folderTitle : binder.getTitle() );
			
				parentBinder = binder.getParentBinder();
				if ( parentBinder != null )
					folder.setParentBinderName( parentBinder.getPathName() );

				// Create a url that can be used to view this folder.
				url = PermaLinkUtil.getPermalink( request, binder );
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
	
	
	/*
	 * Return a GwtUser object for the given user id
	 */
	private GwtUser getGwtUser( HttpServletRequest request, GwtSearchCriteria.SearchType searchType, String userId ) throws GwtTeamingException
	{
		Binder binder = null;
		BinderModule bm = getBinderModule();
		GwtUser reply = null;
		ProfileModule pm = getProfileModule();
		User user = null;
		
		try
		{
			Long userIdL;

			// Do we have an ID we can access as a person?
			userIdL = new Long( userId );
			if ( userIdL != null )
			{
				ArrayList<Long> userAL;
				Set<User> userSet;
				User[] users;
				
				userAL = new ArrayList<Long>();
				userAL.add( userIdL );
				userSet = pm.getUsers( userAL );
				users = userSet.toArray( new User[0] );
				if ( 1 <= users.length )
				{
					// If we are searching for a person and this user
					// is not a person...
					user = users[0];
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
				reply.setUserId( user.getId() );
				reply.setName( user.getName() );
				reply.setTitle( Utils.getUserTitle( user ) );
				reply.setWorkspaceTitle( user.getWSTitle() );
				
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
		catch ( AccessControlException acEx )
		{
			GwtTeamingException ex;
			
			ex = new GwtTeamingException();
			ex.setExceptionType( ExceptionType.ACCESS_CONTROL_EXCEPTION );
			throw ex;
		}
		catch ( Exception e )
		{
			GwtTeamingException ex;
			
			ex = new GwtTeamingException();
			ex.setExceptionType( ExceptionType.UNKNOWN );
			throw ex;
		}
	
		return reply;
	}// end getGwtUser()
	
	
	/**
	 * Return the "binder permalink" URL.
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 */
	public String getBinderPermalink( HttpRequestInfo ri, String binderId )
	{
		String reply = "";
		
		if ( binderId != null && binderId.length() > 0 )
		{
			Binder binder = GwtUIHelper.getBinderSafely( getBinderModule(), binderId );
			if (null != binder)
			{
				reply = PermaLinkUtil.getPermalink( getRequest( ri ), binder );
			}
		}
		
		return reply;
	}
	
	/**
	 * Return the "modify binder" URL.
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 */
	public String getModifyBinderUrl( HttpRequestInfo ri, String binderId )
	{
		AdaptedPortletURL adapterUrl;
		Binder binder;
		Long binderIdL;

		// Create a URL that can be used to modify a binder.
		adapterUrl = new AdaptedPortletURL( getRequest( ri ), "ss_forum", true );
		adapterUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER );
		adapterUrl.setParameter( WebKeys.URL_OPERATION, WebKeys.OPERATION_MODIFY );
		adapterUrl.setParameter( WebKeys.URL_BINDER_ID, binderId );
		
		binderIdL = new Long( binderId );
		binder = getBinderModule().getBinder( binderIdL );
		adapterUrl.setParameter( WebKeys.URL_BINDER_TYPE, binder.getEntityType().name() );
		
		return adapterUrl.toString();
	}// end getModifyBinderUrl()
	
	/*
	 * Returns the HttpServletRequest from an HttpRequestInfo object.
	 */
	private static HttpServletRequest getRequest(HttpRequestInfo ri) {
		return ((HttpServletRequest) ri.getRequestObj());
	}// end getRequest()
	
	/**
	 * Return a GwtPersonalPreferences object that holds the personal preferences for the logged in user.
	 */
	public GwtPersonalPreferences getPersonalPreferences()
	{
		GwtPersonalPreferences personalPrefs;
		
		personalPrefs = new GwtPersonalPreferences();
		
		try
		{
			User user;
			
			// Is the current user the guest user?
			user = GwtServerHelper.getCurrentUser();
			
			// Are we dealing with the guest user?
			if ( !(ObjectKeys.GUEST_USER_INTERNALID.equals( user.getInternalId() ) ) )
			{
				String displayStyle;
				
				// No
				// Get the user's display style preference
				displayStyle = user.getDisplayStyle();
				personalPrefs.setDisplayStyle( displayStyle );
				
				// Get the tutorial panel state.
				{
					String tutorialPanelState;

					tutorialPanelState = getTutorialPanelState();

					// Is the tutorial panel open?
					if ( tutorialPanelState != null && tutorialPanelState.equalsIgnoreCase( "1" ) )
					{
						// No
						personalPrefs.setShowTutorialPanel( false );
					}
					else
						personalPrefs.setShowTutorialPanel( true );
				}
				
				// Get the number of entries per page that should be displayed when a folder is selected.
				{
					UserProperties userProperties;
					String value;
					Integer numEntriesPerPage = Integer.valueOf(SPropsUtil.getString("folder.records.listed"));
					
					userProperties = getProfileModule().getUserProperties( user.getId() );
					value = (String) userProperties.getProperty( ObjectKeys.PAGE_ENTRIES_PER_PAGE );
					if ( value != null && value.length() > 0 )
					{
						try
						{
							numEntriesPerPage = Integer.parseInt( value );
						}
						catch (NumberFormatException nfe)
						{
							m_logger.warn( "In GwtRpcServiceImpl.getPersonalPreferences(), num entries per page is not an integer." );
						}
					}
					
					personalPrefs.setNumEntriesPerPage( numEntriesPerPage );
				}
				
				// Set the flag that indicates whether "editor overrides" are supported.
				personalPrefs.setEditorOverrideSupported( SsfsUtil.supportAttachmentEdit() );
			}
			else
			{
				m_logger.warn( "GwtRpcServiceImpl.getPersonalPreferences(), user is guest." );
			}
		}
		catch (AccessControlException acEx)
		{
			// Nothing to do
			m_logger.warn( "GwtRpcServiceImpl.getPersonalPreferences() AccessControlException" );
		}
		catch (Exception e)
		{
			// Nothing to do
			m_logger.warn( "GwtRpcServiceImpl.getPersonalPreferences() unknown exception" );
		}
		
		return personalPrefs;
	}// end getPersonalPreferences()
	
	
	/**
	 * Return the URL needed to invoke the "site administration" page.  If the user does not
	 * have rights to run the "site administration" page we will throw an exception.
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	public String getSiteAdministrationUrl( HttpRequestInfo ri, String binderId ) throws GwtTeamingException
	{
		// Does the user have rights to run the "site administration" page?
		if ( getAdminModule().testAccess( AdminOperation.manageFunction ) )
		{
			AdaptedPortletURL adapterUrl;
			
			// Yes
			adapterUrl = new AdaptedPortletURL( getRequest( ri ), "ss_forum", true );
			adapterUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_SITE_ADMINISTRATION );
			adapterUrl.setParameter( WebKeys.URL_BINDER_ID, binderId );
			
			return adapterUrl.toString();
		}
		
		GwtTeamingException ex;
		
		ex = new GwtTeamingException();
		ex.setExceptionType( ExceptionType.ACCESS_CONTROL_EXCEPTION );
		throw ex;
	}// end getSiteAdministrationUrl()
	
	/**
	 * Return the URL needed to invoke the start/schedule meeting dialog.
	 */
	public String getAddMeetingUrl( HttpRequestInfo ri, String binderId ) throws GwtTeamingException
	{
		AdaptedPortletURL adapterUrl;

		// ...store the team meeting URL.
		adapterUrl = new AdaptedPortletURL( getRequest(ri), "ss_forum", true );
		adapterUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_ADD_MEETING );
		adapterUrl.setParameter( WebKeys.URL_BINDER_ID, binderId );

		if (GwtServerHelper.getWorkspaceType(GwtUIHelper.getBinderSafely(getBinderModule(), binderId)) == WorkspaceType.USER) {
			// This is a User Workspace so add the owner in and don't append team members
			Principal p = GwtProfileHelper.getPrincipalByBinderId(this, binderId);
			if (p != null) {
				Long id = p.getId();
				String [] ids = new String[1];
				ids[0] = id.toString();
				adapterUrl.setParameter(WebKeys.USER_IDS_TO_ADD, ids);
			}
			adapterUrl.setParameter( WebKeys.URL_APPEND_TEAM_MEMBERS, Boolean.FALSE.toString() );
		} else {
			adapterUrl.setParameter( WebKeys.URL_APPEND_TEAM_MEMBERS, Boolean.TRUE.toString() );
	    }

		return adapterUrl.toString();
	}// end getAddMeetingUrl()
	
	/**
	 * Return a GwtBrandingData object for the home workspace.
	 * 
	 * @param ri
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	public GwtBrandingData getSiteBrandingData( HttpRequestInfo ri ) throws GwtTeamingException
	{
		Binder topWorkspace;
		GwtBrandingData brandingData;
		
		try
		{
			String binderId;
			
			// Get the top workspace.
			topWorkspace = getWorkspaceModule().getTopWorkspace();				
		
			// Get the branding data from the top workspace.
			binderId = topWorkspace.getId().toString();
			brandingData = GwtServerHelper.getBinderBrandingData( this, binderId, getRequest( ri ) );
		}
		catch (Exception e)
		{
			brandingData = new GwtBrandingData();
		}

		brandingData.setIsSiteBranding( true );

		return brandingData;
	}// end getSiteBrandingData()
	
	
	/**
	 * Returns a List<String> of the user ID's of the people the
	 * current user is tracking.
	 * 
	 * @return
	 */
	public List<String> getTrackedPeople()
	{
		return GwtServerHelper.getTrackedPeople( this );
	}// end getTrackedPeople()
	
	/**
	 * Returns a List<String> of the binder ID's of the places the
	 * current user is tracking.
	 * 
	 * @return
	 */
	public List<String> getTrackedPlaces()
	{
		return GwtServerHelper.getTrackedPlaces( this );
	}// end getTrackedPlaces()
	
    /**
     */
    public String getTutorialPanelState()
    {
    	UserProperties	userProperties;
    	ProfileModule	profileModule;
    	String			tutorialPanelState;
    	
    	profileModule = getProfileModule();

    	userProperties = profileModule.getUserProperties( null );
		tutorialPanelState = (String) userProperties.getProperty( ObjectKeys.USER_PROPERTY_TUTORIAL_PANEL_STATE );

		// Do we have a tutorial panel state?
		if ( tutorialPanelState == null || tutorialPanelState.length() == 0 )
		{
			// No, default to expanded.
			tutorialPanelState = "2";
		}
		
    	return tutorialPanelState;
    }// end getTutorialPanelState()
    
    
	/**
	 * Return a GwtUpgradeInfo object.
	 *
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	public GwtUpgradeInfo getUpgradeInfo() throws GwtTeamingException
	{
		GwtUpgradeInfo upgradeInfo;
		User user;
		
		user = GwtServerHelper.getCurrentUser();

		upgradeInfo = new GwtUpgradeInfo();
		
		// Get the Teaming version and build information
		upgradeInfo.setReleaseInfo( ReleaseInfo.getReleaseInfo() );
		
		// Identify any upgrade tasks that may need to be performed.
		{
	 		Workspace top = null;
	 		String upgradeVersionCurrent;

	 		try
	 		{
				top = getWorkspaceModule().getTopWorkspace();
	 		}
	 		catch( Exception e )
	 		{
	 		}

	 		if ( top != null )
	 			upgradeVersionCurrent = (String)top.getProperty( ObjectKeys.BINDER_PROPERTY_UPGRADE_VERSION );
	 		else
	 			upgradeVersionCurrent = ObjectKeys.PRODUCT_UPGRADE_VERSION;

 			// Are we dealing with the "admin" user?
 	 		if ( ObjectKeys.SUPER_USER_INTERNALID.equals( user.getInternalId() ) )
 	 		{
 	 			// Yes, Were there upgrade tasks to be performed?
 		 		if ( upgradeVersionCurrent == null || !upgradeVersionCurrent.equals( ObjectKeys.PRODUCT_UPGRADE_VERSION ) )
 		 		{
 					UserProperties adminUserProperties;

 					// Yes.
 		 			adminUserProperties = getProfileModule().getUserProperties( user.getId() );

 		 			// See if all upgrade tasks have been done
 		 			if ( "true".equals( adminUserProperties.getProperty( ObjectKeys.USER_PROPERTY_UPGRADE_SEARCH_INDEX ) ) &&
 		 				 "true".equals( adminUserProperties.getProperty( ObjectKeys.USER_PROPERTY_UPGRADE_DEFINITIONS ) ) &&
 		 				 "true".equals( adminUserProperties.getProperty( ObjectKeys.USER_PROPERTY_UPGRADE_TEMPLATES ) ) &&
 		 				 "true".equals( adminUserProperties.getProperty( ObjectKeys.USER_PROPERTY_UPGRADE_ACCESS_CONTROLS ) ) )
 		 			{
 		 				// All upgrade tasks are done, mark the upgrade complete
 		 				if ( top != null )
 		 				{
 		 					getBinderModule().setProperty( top.getId(), ObjectKeys.BINDER_PROPERTY_UPGRADE_VERSION, ObjectKeys.PRODUCT_UPGRADE_VERSION );
 		 					upgradeVersionCurrent = ObjectKeys.PRODUCT_UPGRADE_VERSION;
 		 				}
 		 			}
 		 		}
	 		}
	 		
	 		// Are there upgrade tasks to be performed?
	 		if ( upgradeVersionCurrent == null || upgradeVersionCurrent.equalsIgnoreCase( ObjectKeys.PRODUCT_UPGRADE_VERSION ) == false )
	 		{
	 			// Yes.
	 			upgradeInfo.setUpgradeTasksExist( true );
	 			
		 		// Are we dealing with the "admin" user?
		 		if ( ObjectKeys.SUPER_USER_INTERNALID.equals( user.getInternalId() ) )
		 		{
		 			String property;
		 			UserProperties adminUserProperties;
		 			
		 			// Yes
 		 			adminUserProperties = getProfileModule().getUserProperties( user.getId() );
 		 			upgradeInfo.setIsAdmin( true );

 		 			// Do the definitions need to be reset?
		 			property = (String) adminUserProperties.getProperty( ObjectKeys.USER_PROPERTY_UPGRADE_DEFINITIONS );
		 			if ( property == null || property.length() == 0 )
		 			{
		 				// Yes
		 				upgradeInfo.addUpgradeTask( GwtUpgradeInfo.UpgradeTask.UPGRADE_DEFINITIONS );
		 			}

		 			// Do the templates need to be reset?
		 			property = (String) adminUserProperties.getProperty( ObjectKeys.USER_PROPERTY_UPGRADE_TEMPLATES );
		 			if ( property == null || property.length() == 0 )
		 			{
		 				// Yes
		 				upgradeInfo.addUpgradeTask( GwtUpgradeInfo.UpgradeTask.UPGRADE_TEMPLATES );
		 			}

		 			// Does re-indexing need to be performed?
		 			property = (String) adminUserProperties.getProperty( ObjectKeys.USER_PROPERTY_UPGRADE_SEARCH_INDEX );
		 			if ( property == null || property.length() == 0 )
		 			{
		 				// Yes
		 				upgradeInfo.addUpgradeTask( GwtUpgradeInfo.UpgradeTask.UPGRADE_SEARCH_INDEX );
		 			}
		 			
		 			// Do access controls need to be defined for all applications?
		 			property = (String) adminUserProperties.getProperty( ObjectKeys.USER_PROPERTY_UPGRADE_ACCESS_CONTROLS );
		 			if ( property == null || property.length() == 0 )
		 			{
		 				// Yes
		 				upgradeInfo.addUpgradeTask( GwtUpgradeInfo.UpgradeTask.UPGRADE_ACCESS_CONTROLS );
		 			}
		 		}
	 		}
		}
		
		return upgradeInfo;
	}// end getSiteBrandingData()
	
	
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
	 * @param ri
	 * 
	 * @return
	 */
	public String getUserWorkspacePermalink( HttpRequestInfo ri )
	{
		Binder userWS = getBinderModule().getBinder( GwtServerHelper.getCurrentUser().getWorkspaceId() );
		return PermaLinkUtil.getPermalink( getRequest( ri ), userWS );
	}// end getUserWorkspacePermalink()
	
	/**
	 * Returns true if the GWT UI is the default UI at login and false
	 * otherwise.
	 * 
	 * @return
	 */
	public Boolean getGwtUIDefault()
	{
		return new Boolean( GwtUIHelper.isGwtUIDefault() );
	}// end getGwtUI()
	
	/**
	 * Returns true if the GWT UI is the enabled and false otherwise.
	 * 
	 * @return
	 */
	public Boolean getGwtUIEnabled()
	{
		return new Boolean( GwtUIHelper.isGwtUIEnabled() );
	}// end getGwtUI()
	
	/**
	 * Returns true if there should be no UI exposed to exit the GWT UI
	 * and false otherwise.
	 * 
	 * @return
	 */
	public Boolean getGwtUIExclusive()
	{
		return new Boolean( GwtUIHelper.isGwtUIExclusive() );
	}// end getGwtUI()

	/**
	 * Returns a TreeInfo containing the display information for the
	 * Binder hierarchy referred to by a List<Long> of Binder IDs
	 * (i.e., a bucket list.)
	 * 
	 * The information returned is typically used for driving a
	 * horizontal WorkspaceTreeControl widget.
	 * 
	 * @param ri
	 * @param bucketList
	 * 
	 * @return
	 */
	public TreeInfo expandHorizontalBucket( HttpRequestInfo ri, List<Long> bucketList )
	{
		// Expand the bucket list without regard to persistent Binder
		// expansions.
		return GwtServerHelper.expandBucket( getRequest( ri ), this, bucketList, null );
	}//end expandHorizontalBucket()
	
	/**
	 * Returns a TreeInfo containing the display information for the
	 * Binder hierarchy referred to by a List<Long> of Binder IDs
	 * (i.e., a bucket list.)
	 * 
	 * The information returned is typically used for driving a
	 * vertical WorkspaceTreeControl widget.
	 * 
	 * @param ri
	 * @param bucketList
	 * 
	 * @return
	 */
	public TreeInfo expandVerticalBucket( HttpRequestInfo ri, List<Long> bucketList ) {
		// Expand the bucket list taking any persistent Binder
		// expansions into account.
		return GwtServerHelper.expandBucket( getRequest( ri ), this, bucketList, new ArrayList<Long>() );
	}

	/**
	 * Returns a List<TreeInfo> containing the display information for
	 * the Binder hierarchy referred to by binderId from the
	 * perspective of the currently logged in user.
	 * 
	 * The information returned is typically used for driving a
	 * horizontal WorkspaceTreeControl widget.
	 * 
	 * @param ri
	 * @param binderIdS
	 * 
	 * @return
	 */
	public List<TreeInfo> getHorizontalTree( HttpRequestInfo ri, String binderIdS ) {
		Binder binder;
		List<TreeInfo> reply;
		
		// Can we access the Binder?
		binder = GwtServerHelper.getBinderForWorkspaceTree( this, binderIdS, true );
		if (null == binder) {
			// No!  Then we can't build any TreeInfo objects for it.
			reply = new ArrayList<TreeInfo>();
		}
		else {
			// Yes, we can access the Binder!  Access the Binder's
			// nearest containing Workspace...
			ArrayList<Long> bindersList = new ArrayList<Long>();
			while (true)
			{
				bindersList.add( 0, binder.getId() );
				binder = binder.getParentBinder();
				if ( null == binder )
				{
					break;
				}
			}
	
			// ...and build the TreeInfo for the request Binder.
			reply = GwtServerHelper.buildTreeInfoFromBinderList(
				getRequest( ri ),
				this,
				bindersList );
		}


		// If we get here, reply refers to the TreeInfo for the Binder
		// requested.  Return it.
		return reply;
	}// end getHorizontalTree()
	
	/**
	 * Builds a TreeInfo for a Binder being expanded.
	 * 
	 * The information returned is typically used for driving a
	 * horizontal WorkspaceTreeControl widget.
	 * list.toArray(infoArray);
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 */
	public TreeInfo getHorizontalNode( HttpRequestInfo ri, String binderIdS )
	{
		Binder binder;
		TreeInfo reply;
		
		// Access the Binder...
		binder = GwtServerHelper.getBinderForWorkspaceTree( this, binderIdS );

		// ...and build the TreeInfo for it.
		reply = ((null == binder) ? null : GwtServerHelper.buildTreeInfoFromBinder( getRequest( ri ), this, binder ));
		return reply;
	}// end getHorizontalNode()

	/**
	 * Returns the ID of the nearest containing workspace of a given
	 * Binder.
	 * 
	 * @return
	 */
	public String getRootWorkspaceId( String binderId )
	{
		String reply;
		
		Binder binder = GwtUIHelper.getBinderSafely( getBinderModule(), binderId );
		if (null != binder)
		{
			Workspace binderWS = BinderHelper.getBinderWorkspace( binder );
			reply = String.valueOf( binderWS.getId() );
		}
		else
		{
			binder = getWorkspaceModule().getTopWorkspace();
			reply = String.valueOf( binder.getId() );
		}
		
		return reply;
	}// end getRootWorkspaceId() 

	/**
	 * Returns a TreeInfo object containing the display information for
	 * and activity streams tree using the current Binder referred to
	 * by binderId from the perspective of the currently logged in
	 * user.
	 * 
	 * The information returned is typically used for driving a
	 * vertical WorkspaceTreeControl widget when in activity streams
	 * mode.
	 * 
	 * @param ri
	 * @param binderIdS
	 * 
	 * @return
	 */
	public TreeInfo getVerticalActivityStreamsTree( HttpRequestInfo ri, String binderIdS )
	{
		return GwtActivityStreamHelper.getVerticalActivityStreamsTree( getRequest( ri ), this, binderIdS );
	}// end getVerticalActivityStreamsTree()
	
	/**
	 * Returns a TreeInfo object containing the display information for
	 * the Binder referred to by binderId from the perspective of the
	 * currently logged in user.  Information about the Binder
	 * expansion states for the current user is integrated into the
	 * TreeInfo returned.
	 * 
	 * The information returned is typically used for driving a
	 * vertical WorkspaceTreeControl widget.
	 * 
	 * @param ri
	 * @param binderIdS
	 * 
	 * @return
	 */
	public TreeInfo getVerticalTree( HttpRequestInfo ri, String binderIdS )
	{
		Binder binder;
		TreeInfo reply;
		
		// Can we access the Binder?
		binder = GwtServerHelper.getBinderForWorkspaceTree( this, binderIdS, true );
		if (null == binder) {
			// No!  We can't build a TreeInfo for it.
			reply = new TreeInfo();
		}
		
		else {
			// Yes, we can access the Binder!  Access the Binder's
			// nearest containing Workspace...
			Workspace binderWS = BinderHelper.getBinderWorkspace( binder );
	
			// ...note that the Workspace should always be expanded...
			Long binderWSId = binderWS.getId();
			ArrayList<Long> expandedBindersList = new ArrayList<Long>();
			expandedBindersList.add( binderWSId );
	
			// ...calculate which additional Binder's that must be expanded
			// ...to show the requested Binder...
			long binderId      = Long.parseLong( binderIdS );
			long binderWSIdVal = binderWSId.longValue();
			if ( binderId != binderWSIdVal ) {
				Binder parentBinder = binder.getParentBinder();
				if ( null != parentBinder )
				{
					binder = parentBinder;
					while ( binder.getId().longValue() != binderWSIdVal )
					{
						expandedBindersList.add( binder.getId() );
						binder = binder.getParentBinder();
					}
				}
			}
			
			// ...build the TreeInfo for the request Binder...
			reply = GwtServerHelper.buildTreeInfoFromBinder(
				getRequest( ri ),
				this,
				binderWS,
				expandedBindersList );
	
	
			// ...and if the Binder supports Trash access...
			boolean allowTrash = TrashHelper.allowUserTrashAccess( GwtServerHelper.getCurrentUser() );
			if ( allowTrash && ( !(binder.isMirrored()) ) )
			{
				// ...add a TreeInfo to the reply's children for it.
				GwtServerHelper.addTrashFolder( this, reply, binder );
			}
		}
		
		// If we get here, reply refers to the TreeInfo for the Binder
		// requested.  Return it.
		return reply;
	}// end getVerticalTree()
	
	/**
	 * Builds a TreeInfo for the Binder being expanded and stores the
	 * fact that it has been expanded.
	 * 
	 * The information returned is typically used for driving a
	 * vertical WorkspaceTreeControl widget.
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 */
	public TreeInfo getVerticalNode( HttpRequestInfo ri, String binderIdS )
	{
		Binder binder;
		TreeInfo reply;
		
		// Access the Binder...
		binder = GwtServerHelper.getBinderForWorkspaceTree( this, binderIdS );
		if ( null != binder )
		{
			// ...note that the Binder will now be expanded...
			ArrayList<Long> expandedBindersList = new ArrayList<Long>();
			expandedBindersList.add( Long.parseLong( binderIdS ));
	
			// ...and build the TreeInfo folist.toArray(infoArray);r it.
			reply = GwtServerHelper.buildTreeInfoFromBinder( getRequest( ri ), this, binder, expandedBindersList );
		}
		else
		{
			reply = null;
		}
		return reply;
	}// end getVerticalNode()
	
	/**
	 * Saves the fact that the Binder for the given ID should be
	 * collapsed for the current User.
	 * 
	 * @param binderId
	 * 
	 * @return
	 */
	public Boolean persistNodeCollapse( String binderId )
	{
		GwtServerHelper.persistNodeCollapse( this, Long.parseLong( binderId ) );
		return Boolean.TRUE;
	}// end persistNodeCollapse()

	/**
	 * Saves the fact that the Binder for the given ID should be
	 * expanded for the current User.
	 * 
	 * @param binderId
	 * 
	 * @return
	 */
	public Boolean persistNodeExpand( String binderId )
	{
		GwtServerHelper.persistNodeExpand( this, Long.parseLong( binderId ) );
		return Boolean.TRUE;
	}// end persistNodeExpand()

	
	/**
	 * Adds binderId to the user's list of favorites.
	 * 
	 * @param binderId
	 * 
	 * @return
	 */
	public Boolean addFavorite( String binderId )
	{
		Binder binder;
		Favorites f;
		String viewAction;
		String title;
		UserProperties userProperties;
		
		binder = getBinderModule().getBinder( Long.parseLong( binderId ) );
		userProperties = getProfileModule().getUserProperties( null );
		f = new Favorites( (String) userProperties.getProperty( ObjectKeys.USER_PROPERTY_FAVORITES ) );
		title = binder.getTitle();
		if ( binder instanceof Folder )
		{
			title += " (" + ((Folder)binder).getParentBinder().getTitle() + ")";
		}
		switch ( binder.getEntityType() )
		{
		case folder:     viewAction = "view_folder_listing";  break;
		case profiles:   viewAction = "view_profile_listing"; break;
		default:         viewAction = ""; break;
		}
		f.addFavorite( title, binder.getPathName(), Favorites.FAVORITE_BINDER, binderId.toString(), viewAction, "" );
		getProfileModule().setUserProperty( null, ObjectKeys.USER_PROPERTY_FAVORITES, f.toString() );
		
		return Boolean.TRUE;
	}// end addFavorite()
	
	/**
	 * Removes favoriteId from the user's list of favorites.
	 * 
	 * @param favoriteId
	 * 
	 * @return
	 */
	public Boolean removeFavorite( String favoriteId )
	{
		Favorites f;
		UserProperties userProperties;
		
		userProperties = getProfileModule().getUserProperties( null );
		f = new Favorites( (String) userProperties.getProperty( ObjectKeys.USER_PROPERTY_FAVORITES ) );
		f.deleteFavorite( favoriteId );
		getProfileModule().setUserProperty( null, ObjectKeys.USER_PROPERTY_FAVORITES, f.toString() );
		return Boolean.TRUE;
	}//end removeFavorite()
	
	/**
	 * Sets the user's list of favorites to favoritesList.
	 * 
	 * @param favoritesList
	 * 
	 * @return
	 */
	public Boolean updateFavorites( List<FavoriteInfo> favoritesList )
	{
		Favorites f;
		Iterator<FavoriteInfo> fiIT;
		
		f = new Favorites();
		for ( fiIT = favoritesList.iterator(); fiIT.hasNext(); )
		{
			FavoriteInfo fi;
			
			fi = fiIT.next();
			f.addFavorite(fi.getName(), fi.getHover(), fi.getType(), fi.getValue(), fi.getAction(), fi.getCategory());
		}
		
		getProfileModule().setUserProperty( null, ObjectKeys.USER_PROPERTY_FAVORITES, f.toString() );
		return Boolean.TRUE;
	}//end updateFavorites()

	/**
	 * Returns a List<TagInfo> of the tags defined on a binder.
	 *
	 * @param binderId
	 * 
	 * @return
	 */
	public List<TagInfo> getBinderTags( String binderId )
	{
		return GwtServerHelper.getBinderTags( this, binderId );
	}//end getBinderTags()
	
	/**
	 * Returns true if the user can manage public tags on the binder
	 * and false otherwise.
	 *
	 * @param binderId
	 * 
	 * @return
	 */
	public Boolean canManagePublicBinderTags( String binderId )
	{
		Binder binder = getBinderModule().getBinder(Long.parseLong(binderId));
		return new Boolean(getBinderModule().testAccess(binder, BinderOperation.manageTag));
	}//end canManagePublicBinderTags()
	
	/**
	 * Adds a tag to those defined on a binder.
	 *
	 * @param binderId
	 * @param binderTag
	 * 
	 * @return
	 */
	public TagInfo addBinderTag( String binderId, TagInfo binderTag )
	{
		return GwtServerHelper.addBinderTag( this, binderId, binderTag );
	}//end addBinderTag()
	
	/**
	 * Removes a tag from those defined on a binder.
	 *
	 * @param binderId
	 * @param binderTag
	 * 
	 * @return
	 */
	public Boolean removeBinderTag( String binderId, TagInfo binderTag )
	{
		return GwtServerHelper.removeBinderTag( this, binderId, binderTag );
	}//end removeBinderTag()
	
	/**
	 * Updates the list of tags defined on a binder.
	 *
	 * @param binderId
	 * @param binderTags
	 * 
	 * @return
	 */
	public Boolean updateBinderTags( String binderId, List<TagInfo> binderTags )
	{
		return GwtServerHelper.updateBinderTags( this, binderId, binderTags );
	}//end updateBinderTags()
	
	/**
	 * Returns a BinderInfo describing a binder.
	 *
	 * @param binderId
	 * 
	 * @return
	 */
	public BinderInfo getBinderInfo( String binderId )
	{
		return GwtServerHelper.getBinderInfo( this, binderId );
	}//end getBinderInfo()

	/**
	 * Returns the ID of the default view definition of a folder.
	 * 
	 * @return
	 */
	public String getDefaultFolderDefinitionId( String binderId ) {
		return GwtServerHelper.getDefaultFolderDefinitionId( this, binderId );
	}

	/**
	 * Returns information about the current user's favorites.
	 * 
	 * @return
	 */
	public List<FavoriteInfo> getFavorites()
	{
		return GwtServerHelper.getFavorites( this );
	}// end getFavorites()
	
	/**
	 * Returns information about the groups the current user is a member
	 * of.
	 * 
	 * @param ri
	 * 
	 * @return
	 */
	public List<GroupInfo> getMyGroups( HttpRequestInfo ri )
	{
		return GwtServerHelper.getMyGroups( getRequest( ri ), this );
	}// end getMyGroups()
	
	/**
	 * Returns information about the teams the current user is a member
	 * of.
	 * 
	 * @param ri
	 * 
	 * @return
	 */
	public List<TeamInfo> getMyTeams( HttpRequestInfo ri )
	{
		return GwtServerHelper.getMyTeams( getRequest( ri ), this );
	}// end getMyTeams()
	
	/**
	 * Return the url needed to invoke the user's "micro-blog" page.  
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
 	 */
	public String getMicrBlogUrl( HttpRequestInfo ri, String binderId ) throws GwtTeamingException
	{
		try {
			
			Principal p = GwtProfileHelper.getPrincipalByBinderId(this, binderId);
			
			// Does the user have rights to run the "site administration" page?
			if ( p != null )
			{
				AdaptedPortletURL adapterUrl;
				
				Long random = System.currentTimeMillis();
				
				// Yes
				adapterUrl = new AdaptedPortletURL( getRequest( ri ), "ss_forum", true );
				adapterUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_AJAX_REQUEST );
				adapterUrl.setParameter( WebKeys.URL_OPERATION, WebKeys.OPERATION_VIEW_MINIBLOG );
				adapterUrl.setParameter( WebKeys.URL_USER_ID, p.getId().toString() );
				adapterUrl.setParameter( WebKeys.URL_PAGE, "0" );
				adapterUrl.setParameter( "randomNumber", Long.toString(random) );
				
				return adapterUrl.toString();
			}
		
			GwtTeamingException ex;
			
			ex = new GwtTeamingException();
			ex.setExceptionType( ExceptionType.NO_BINDER_BY_THE_ID_EXCEPTION );
			throw ex;
			
		} catch (OperationAccessControlExceptionNoName oae) {
			GwtTeamingException ex;

			ex = new GwtTeamingException();
			ex.setExceptionType( ExceptionType.ACCESS_CONTROL_EXCEPTION );
			throw ex;
		} 
		
	}// end getSiteAdministrationUrl()
	
	/**
	 * Return the URL needed to start an Instant Message with the user.
	 * 
 	 */
	public Boolean isPresenceEnabled( )
	{
		PresenceManager presenceService = (PresenceManager)SpringContextUtil.getBean("presenceService");
		if (presenceService != null)
		{
			return presenceService.isEnabled();
		}
		else
		{
			return Boolean.FALSE;
		}
	}

	/**
	 * Return the URL needed to start an Instant Message with the user.
	 *
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
 	 */
	public String getImUrl( String binderId ) throws GwtTeamingException
	{
		try {
			Principal p = GwtProfileHelper.getPrincipalByBinderId(this, binderId);
			if ( p != null )
			{
				//Get a user object from the principal
				User user = null;
				if (p != null) {
					if (user instanceof User) {
						user = (User) p;
					} else {
						ProfileDao profileDao = (ProfileDao)SpringContextUtil.getBean("profileDao");
						try {
							user = profileDao.loadUser(p.getId(), p.getZoneId());
						}
						catch(Exception e) {}
					}
				}

				if (user != null) {
					PresenceManager presenceService = (PresenceManager)SpringContextUtil.getBean("presenceService");
					if (presenceService != null)
					{
						String userID = "";
						CustomAttribute attr = user.getCustomAttribute("presenceID");
						if (attr != null)
						{
							userID = (String)attr.getValue();
						}
						if (userID != null && userID.length() > 0)
						{
							return presenceService.getIMProtocolString(userID);
						}
					}
				}
				
				return "";
			}
			
			GwtTeamingException ex;

			ex = new GwtTeamingException();
			ex.setExceptionType( ExceptionType.NO_BINDER_BY_THE_ID_EXCEPTION );
			throw ex;
			
		} catch (OperationAccessControlExceptionNoName oae) {
			GwtTeamingException ex;

			ex = new GwtTeamingException();
			ex.setExceptionType( ExceptionType.ACCESS_CONTROL_EXCEPTION );
			throw ex;
		} 
	}// end getImUrl

	public GwtPresenceInfo getPresenceInfo( String binderId ) throws GwtTeamingException
	{
		try {
			User userAsking = GwtServerHelper.getCurrentUser();
			GwtPresenceInfo gwtPresence = new GwtPresenceInfo();

			// Can't get presence if we are a guest
			if ( userAsking != null && !(ObjectKeys.GUEST_USER_INTERNALID.equals( userAsking.getInternalId() ) ) )
			{
				Principal p = GwtProfileHelper.getPrincipalByBinderId(this, binderId);

				if ( p != null )
				{
					// Get a user object from the principal
					User user = null;
					if (p != null) {
						if (user instanceof User) {
							user = (User) p;
						} else {
							ProfileDao profileDao = (ProfileDao)SpringContextUtil.getBean("profileDao");
							try {
								user = profileDao.loadUser(p.getId(), p.getZoneId());
							}
							catch(Exception e) {}
						}
					}

					if (user != null) {
						PresenceManager presenceService = (PresenceManager)SpringContextUtil.getBean("presenceService");
						if (presenceService != null)
						{
							PresenceInfo pi = null;
							int userStatus =  PresenceInfo.STATUS_UNKNOWN;

							String userID = "";
							CustomAttribute attr = user.getCustomAttribute("presenceID");
							if (attr != null)
							{
								userID = (String)attr.getValue();
							}
							  
							String userIDAsking = "";
							attr = userAsking.getCustomAttribute("presenceID");
							if (attr != null)
							{
								userIDAsking = (String)attr.getValue();
							}

							if (userID != null && userID.length() > 0 && userIDAsking != null && userIDAsking.length() > 0)
							{
								pi = presenceService.getPresenceInfo(userIDAsking, userID);
								if (pi != null) {
									gwtPresence.setStatusText(pi.getStatusText());
									userStatus = pi.getStatus();
								}	
							}
							switch (userStatus) {
								case PresenceInfo.STATUS_AVAILABLE:
									gwtPresence.setStatus(GwtPresenceInfo.STATUS_AVAILABLE);
									break;
								case PresenceInfo.STATUS_AWAY:
									gwtPresence.setStatus(GwtPresenceInfo.STATUS_AWAY);
									break;
								case PresenceInfo.STATUS_IDLE:
									gwtPresence.setStatus(GwtPresenceInfo.STATUS_IDLE);
									break;
								case PresenceInfo.STATUS_BUSY:
									gwtPresence.setStatus(GwtPresenceInfo.STATUS_BUSY);
									break;
								case PresenceInfo.STATUS_OFFLINE:
									gwtPresence.setStatus(GwtPresenceInfo.STATUS_OFFLINE);
									break;
								default:
									gwtPresence.setStatus(GwtPresenceInfo.STATUS_UNKNOWN);
							}
						}
					}
				}
			}

			return gwtPresence;
		} catch (OperationAccessControlExceptionNoName oae) {
			GwtTeamingException ex;
	
			ex = new GwtTeamingException();
			ex.setExceptionType( ExceptionType.ACCESS_CONTROL_EXCEPTION );
			throw ex;
		} catch (Exception e) {
			GwtTeamingException ex;
	
			ex = new GwtTeamingException();
			ex.setExceptionType( ExceptionType.NO_BINDER_BY_THE_ID_EXCEPTION );
			throw ex;
		}
	}// end getPresenceInfo

	/**
	 * Returns information about the recent place the current user has
	 * visited.
	 * 
	 * @param ri
	 * 
	 * @return
	 */
	public List<RecentPlaceInfo> getRecentPlaces( HttpRequestInfo ri )
	{
		return GwtServerHelper.getRecentPlaces( getRequest( ri ), this );
	}// end getRecentPlaces()
	
	/**
	 * Returns information about the saved search the current user has
	 * defined.
	 * 
	 * @return
	 */
	public List<SavedSearchInfo> getSavedSearches()
	{
		return GwtServerHelper.getSavedSearches( this );
	}// end getSavedSearches()
	
	
	/**
	 * Return information about self registration.
	 * 
	 * @param ri
	 * 
	 * @return
	 */
	public GwtSelfRegistrationInfo getSelfRegistrationInfo( HttpRequestInfo ri )
	{
		return GwtServerHelper.getSelfRegistrationInfo( getRequest( ri ), this );
	}// end getSelfRegistrationInfo()
	
	/**
	 * Returns a List<ToolbarItem> of the ToolbarItem's
	 * applicable for the given context.
	 * 
	 * @param binderId
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ToolbarItem> getToolbarItems( String binderId )
	{
		// Construct an ArrayList<ToolbarItem> to hold the toolbar
		// items.
		ArrayList<ToolbarItem> tmiList = new ArrayList<ToolbarItem>();

		// If we can't access the HttpSession...
		HttpSession hSession = GwtServerHelper.getCurrentHttpSession();
		if (null == hSession) {
			// ...we can't access the cached toolbar beans to build the
			// ...toolbar items from.  Bail.
			m_logger.debug("GwtRpcServiceImpl.getToolbarItems( 'Could not access the current HttpSession' )");
			return tmiList;
		}

		// If we can't access the cached toolbar beans... 
		GwtUISessionData tabsObj = ((GwtUISessionData) hSession.getAttribute(GwtUIHelper.CACHED_TOOLBARS_KEY));
		Map<String, Map> tbMaps = ((null == tabsObj) ? null : ((Map<String, Map>) tabsObj.getData()));
		if (null == tbMaps) {
			// ...we can't build any toolbar items.  Bail.
			m_logger.debug("GwtRpcServiceImpl.getToolbarItems( 'Could not access any cached toolbars' )");
			return tmiList;
		}
		
		// Scan the toolbars...
		m_logger.debug("GwtRpcServiceImpl.getToolbarItems():");
		Set<String> tbKeySet = tbMaps.keySet();
		for (Iterator<String> tbKeyIT = tbKeySet.iterator(); tbKeyIT.hasNext(); ) {
			// ...constructing a ToolbarItem for each.
			String tbKey = tbKeyIT.next();
			tmiList.add(buildToolbarItemFromToolbar("...", tbKey, tbMaps.get(tbKey)));
		}

		// If we get here, tmiList refers to the
		// List<ToolbarItem>'s to construct the GWT UI based toolbar
		// from.  Return it.
		return tmiList;
	}

	/**
	 * Returns a List<TopRankedInfo> of the top ranked items from the
	 * most recent search.
	 * 
	 * @param ri
	 * 
	 * @return
	 */
	public List<TopRankedInfo> getTopRanked( HttpRequestInfo ri )
	{
		return GwtServerHelper.getTopRanked( getRequest( ri ), this );
	}

	/**
	 * Removes a search based on its SavedSearchInfo.
	 * 
	 * @param ssi
	 * 
	 * @return
	 */
	public Boolean removeSavedSearch( SavedSearchInfo ssi ) {
		return GwtServerHelper.removeSavedSearch( this, ssi );
	}// end removeSavedSearch()
	
	/**
	 * Saves a search based on its tab ID and SavedSearchInfo.
	 * 
	 * @param searchTabId
	 * @param ssi
	 * 
	 * @return
	 */
	public SavedSearchInfo saveSearch( String searchTabId, SavedSearchInfo ssi ) {
		return GwtServerHelper.saveSearch( this, searchTabId, ssi );
	}// end saveSearch()
	
	/**
	 * Called to mark that the current user is tracking the specified
	 * binder.
	 * 
	 * @param binderId
	 */
	public Boolean trackBinder( String binderId )
	{
		BinderHelper.trackThisBinder( this, Long.parseLong(binderId), "add" );
		return Boolean.TRUE;
	}// endtrackBinder()
	
	/**
	 * Called to mark that the current user is no longer tracking the
	 * specified binder.
	 * 
	 * @param binderId
	 */
	public Boolean untrackBinder( String binderId )
	{
		BinderHelper.trackThisBinder( this, Long.parseLong(binderId), "delete" );
		return Boolean.TRUE;
	}//end untrackBinder()
	
	/**
	 * Called to mark that the current user is no longer tracking the
	 * person whose workspace is the specified binder.
	 * 
	 * @param binderId
	 */
	public Boolean untrackPerson( String binderId )
	{
		Binder binder = getBinderModule().getBinder( Long.parseLong( binderId ) );
		BinderHelper.trackThisBinder( this, binder.getOwnerId(), "deletePerson" );
		return Boolean.TRUE;
	}//end untrackPerson()
	
	/**
	 * Called to check if the current user is tracking the
	 * person whose workspace is the specified binder.
	 * 
	 * @param binderId
	 */
	public Boolean isPersonTracked( String binderId )
	{
		return BinderHelper.isPersonTracked( this, Long.parseLong( binderId ) );
	}//end isPersonTracked()
	
	/*
	 * Constructs a ToolbarItem based on a toolbar.
	 */
	@SuppressWarnings("unchecked")
	private ToolbarItem buildToolbarItemFromToolbar(String traceStart, String tbKey, Map tbMap) {
		// Log the name of the toolbar that we're building a toolbar
		// item for...
		m_logger.debug(traceStart + ":toolbar=" + tbKey);

		// ...and create its toolbar item.
		ToolbarItem toolbarItem = new ToolbarItem();
		toolbarItem.setName(tbKey);

		// Scan the items in this toolbar's map.
		Set kSet = tbMap.keySet();
		for (Iterator kIT = kSet.iterator(); kIT.hasNext(); ) {
			// Is this item a nested map?
			String k = ((String) kIT.next());
			Object o = tbMap.get(k);
			if (o instanceof Map) {
				// Yes!  Is it a map of qualifiers?
				if (k.equalsIgnoreCase("qualifiers")) {
					// Yes!  Add them to the current toolbar.
					Map m = ((Map) o);
					Set qSet = m.keySet();
					for (Iterator qIT = qSet.iterator(); qIT.hasNext(); ) {
						String name  = ((String) qIT.next());
						Object value = m.get(name);
						String sValue;
						if      (value instanceof Boolean) sValue = String.valueOf((Boolean) value);
						else if (value instanceof String)  sValue = ((String) value);
						else                               sValue = null;
						if (null == sValue) {
							m_logger.debug(traceStart + "...:name:<unknown>:IGNORED QUALIFIER=" + name + ":" + ((null == value) ? "null" : value.getClass()));
						}
						else {
							if (name.equalsIgnoreCase(GwtUIHelper.GWTUI_TEAMING_ACTION)) {
								m_logger.debug(traceStart + "...:name:value:TEAMING_ACTION=" + name + ":" + sValue);
								TeamingAction ta = TeamingAction.valueOf(sValue);
								toolbarItem.setTeamingAction(ta);
							}
							else {
								m_logger.debug(traceStart + "...:name:value:QUALIFIER=" + name + ":" + sValue);
								toolbarItem.addQualifier(name, sValue);
							}
						}
					}
				}
				else {
					// No, it's not a map of qualifiers!  Construct a
					// nested toolbar item for it.
					toolbarItem.addNestedItem(buildToolbarItemFromToolbar((traceStart + "..."), k, ((Map) o)));
				}
			}
			
			// No, the item isn't a nested map!  Is it a string?
			else if (o instanceof String) {
				// Yes!  Handle the values we know about...
				String s = ((String) o);
				if (k.equalsIgnoreCase("title")) {
					m_logger.debug(traceStart + "...:key:string:TITLE=" + k + ":" + s);
					toolbarItem.setTitle(s);
				}
				else if (k.equalsIgnoreCase("url") || k.endsWith(GwtUIHelper.URLFIXUP_PATCH)) {
					m_logger.debug(traceStart + "...:key:string:URL=" + k + ":" + s);
					toolbarItem.setUrl(  s);
				}
				else {
					// ...and ignore the rest.
					m_logger.debug(traceStart + "...:key:string:IGNORED=" + k + ":" + s);
				}
			}

			// No, the item isn't a string either!  Is it an adapted
			// portlet URL?
			else if (o instanceof AdaptedPortletURL) {
				// Yes!  Then we ignore it as it will have been handled
				// as a string above.  (See the URLFIXUP_PATCH
				// reference above.)
			}
			
			else {
				// No, the item isn't an adapted portlet URL either!
				// We don't know how to handle it!
				m_logger.debug(traceStart + "...:key:<unknown>:IGNORED=" + k + ":" + ((null == o) ? "null" : o.getClass()));
			}
		}

		// If we get here, toolbarItem refers to the ToolbarItem for
		// this toolbar.  Return it.
		return toolbarItem;
	}

	/**
	 * Returns a TeamManagementInfo object regarding the current user's
	 * team management capabilities.
	 * 
	 * @param ri
	 * @binderId
	 * 
	 * @return
	 */
	public TeamManagementInfo getTeamManagementInfo( HttpRequestInfo ri, String binderId )
	{
		TeamManagementInfo tmi;
		User user;
		
		// Construct a base TeamManagementInfo object.
		tmi = new TeamManagementInfo();
		
		// Is the current user the guest user?
		user = GwtServerHelper.getCurrentUser();
		if ( !(ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) )
		{
			Binder binder;
			
			// No!  Is the binder other than the profiles container?
			binder = GwtUIHelper.getBinderSafely( getBinderModule(), binderId );
			if ( ( null != binder ) && ( EntityIdentifier.EntityType.profiles != binder.getEntityType() ) )
			{
				AdaptedPortletURL adapterUrl;
				HttpServletRequest request = getRequest( ri ); 
			
				// Yes!  Then the user is allowed to view team membership.
				tmi.setViewAllowed( true );
	
				// If the user can manage the team...
				if ( getBinderModule().testAccess( binder, BinderOperation.manageTeamMembers ) )
				{
					// ...store the team management URL...
					adapterUrl = new AdaptedPortletURL( request, "ss_forum", true );
					adapterUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_ADD_TEAM_MEMBER );
					adapterUrl.setParameter( WebKeys.URL_BINDER_ID, binderId );
					adapterUrl.setParameter( WebKeys.URL_BINDER_TYPE, binder.getEntityType().name() );
					tmi.setManageUrl( adapterUrl.toString() );
				}
	
				// ...if the user can send mail to the team...
				if ( MiscUtil.hasString( user.getEmailAddress() ) )
				{
					// ...store the send mail URL...
					adapterUrl = new AdaptedPortletURL( request, "ss_forum", true );
					adapterUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_SEND_EMAIL );
					adapterUrl.setParameter( WebKeys.URL_BINDER_ID, binderId );
					adapterUrl.setParameter( WebKeys.URL_APPEND_TEAM_MEMBERS, Boolean.TRUE.toString() );
					tmi.setSendMailUrl( adapterUrl.toString() );
				}
	
				// ...if the user can start a team meeting...
				if ( getConferencingModule().isEnabled())
				{
					CustomAttribute ca = user.getCustomAttribute("conferencingID");
					if (ca != null && MiscUtil.hasString((String)ca.getValue())) {		
						// ...store the team meeting URL.
						try {
							tmi.setTeamMeetingUrl( getAddMeetingUrl(ri, binderId) );
						} catch (GwtTeamingException e) {
							// Nothing to do...
						}
					}
				}
			}
		}

		// If we get here, tmi refers to a TeamManagementInfo object
		// containing the user's team management capabilities.  Return
		// it.
		return tmi;
	}//end getTeamManagementInfo()
	
	/**
	 * Save the given branding data to the given binder.
	 *
	 * @param binderId
	 * @param brandingData
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	public Boolean saveBrandingData( String binderId, GwtBrandingData brandingData ) throws GwtTeamingException
	{
		BinderModule binderModule;
		Long binderIdL;
		
		try
		{
			binderModule = getBinderModule();
	
			binderIdL = new Long( binderId );
			
			// Get the binder object.
			if ( binderIdL != null )
			{
				String branding;
				HashMap<String, Object> hashMap;
				MapInputData dataMap;
				
				// Create a Map that holds the branding and extended branding.
				hashMap = new HashMap<String, Object>();
				
				// Add the old-style branding to the map.
				//!!! Do we need to do something with the html found in the branding?
				branding = brandingData.getBranding();
				if ( branding == null )
					branding = "";
				hashMap.put( "branding", branding );

				// Add the exteneded branding data to the map.
				branding = brandingData.getBrandingAsXmlString();
				if ( branding == null )
					branding = "";
				hashMap.put( "brandingExt", branding );
				
				// Update the binder with the new branding data.
				dataMap = new MapInputData( hashMap );
				binderModule.modifyBinder( binderIdL, dataMap, null, null, null );
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
		
		return Boolean.TRUE;
	}// end saveBrandingData()


	/**
	 * Save the given personal preferences for the logged in user.
	 *
	 * @param personalPrefs
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	public Boolean savePersonalPreferences( GwtPersonalPreferences personalPrefs ) throws GwtTeamingException
	{
		try
		{
			User user;
			
			// Is the current user the guest user?
			user = GwtServerHelper.getCurrentUser();
			
			// Are we dealing with the guest user?
			if ( !(ObjectKeys.GUEST_USER_INTERNALID.equals( user.getInternalId() ) ) )
			{
				ProfileModule profileModule;
				
				profileModule = getProfileModule();
				
				// No
				// Save the "display style" preference
				{
					Map<String,Object> updates;
					String newDisplayStyle;
					
					updates = new HashMap<String,Object>();
					
					newDisplayStyle = personalPrefs.getDisplayStyle();
					
					// Only allow "word" characters (such as a-z_0-9 )
					if ( newDisplayStyle.equals("") || !newDisplayStyle.matches( "^.*[\\W]+.*$" ) )
					{
						updates.put( ObjectKeys.USER_PROPERTY_DISPLAY_STYLE, newDisplayStyle );
						profileModule.modifyEntry( user.getId(), new MapInputData( updates ) );
					}
				}
				
				// Save the "show tutorial panel" preference
				{
					@SuppressWarnings("unused")
					String tutorialPanelState;
					
					if ( personalPrefs.getShowTutorialPanel() )
						tutorialPanelState = "2";
					else
						tutorialPanelState = "1";
					
					// We don't have a tutorial panel any more (as of Durango).
					// profileModule.setUserProperty( null, ObjectKeys.USER_PROPERTY_TUTORIAL_PANEL_STATE, tutorialPanelState );
				}
				
				// Save the "number of entries per page" preference
				{
					profileModule.setUserProperty(
												user.getId(),
												ObjectKeys.PAGE_ENTRIES_PER_PAGE,
												String.valueOf( personalPrefs.getNumEntriesPerPage() ) );
				}
			}
			else
			{
				m_logger.warn( "GwtRpcServiceImpl.getPersonalPreferences(), user is guest." );
			}
		}
		catch (AccessControlException acEx)
		{
			GwtTeamingException ex;
			
			ex = new GwtTeamingException();
			ex.setExceptionType( ExceptionType.ACCESS_CONTROL_EXCEPTION );
			
			// Nothing to do
			m_logger.warn( "GwtRpcServiceImpl.savePersonalPreferences() AccessControlException" );
			throw ex;
		}
		catch (Exception e)
		{
			GwtTeamingException ex;
			
			ex = new GwtTeamingException();
			ex.setExceptionType( ExceptionType.UNKNOWN );

			m_logger.warn( "GwtRpcServiceImpl.savePersonalPreferences() unknown exception" );
			throw ex;
		}
		
		return Boolean.TRUE;
	}// end savePersonalPreferences()


	/**
	 * Get the profile information based on the binder Id passed in.
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 */
	public ProfileInfo getProfileInfo(HttpRequestInfo ri, String binderId) throws GwtTeamingException {
		try
		{
			//get the binder
			ProfileInfo profile = GwtProfileHelper.buildProfileInfo(getRequest( ri ), this, Long.valueOf(binderId));
			return profile;
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
	}
	
	
	/**
	 * Get the profile information for the Quick View based on the binder Id passed in.
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	public ProfileInfo getQuickViewInfo(HttpRequestInfo ri, String binderId) throws GwtTeamingException {
		
		try {
			Long binderIdL = Long.valueOf(binderId);
			
			//get the binder
			ProfileInfo profile = GwtProfileHelper.buildQuickViewProfileInfo(getRequest( ri ), this, binderIdL);
			
			return profile;
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
	}
	
	/**
	 * Returns Look up the user and return the list of groups they belong to.
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	public List<GroupInfo> getGroups( HttpRequestInfo ri, String binderId ) throws GwtTeamingException
	{
		try {
			Long userId = null;
			Principal p = null;

			if (binderId != null) {
				p = GwtProfileHelper.getPrincipalByBinderId(this, binderId);
			}
			
			if (p != null){
				userId = p.getId();
			}
			
			return GwtServerHelper.getGroups( getRequest( ri ), this, userId );
		} catch (OperationAccessControlExceptionNoName oae) {
			GwtTeamingException ex;

			ex = new GwtTeamingException();
			ex.setExceptionType( ExceptionType.ACCESS_CONTROL_EXCEPTION );
			throw ex;
		} 
		
	}// end getMyGroups()

	/**
	 * Returns Look up the workspace owner and return the list of teams they belong to.
	 *
	 * @param ri
	 * @param binderId  The binderId of the workspace being viewed
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	public List<TeamInfo> getTeams( HttpRequestInfo ri, String binderId ) throws GwtTeamingException
	{
		try {
			Long userId = null;
			Principal p = null;

			if (binderId != null) {
				p = GwtProfileHelper.getPrincipalByBinderId(this, binderId);
			}
			
			if (p != null){
				userId = p.getId();
			}
			
			return GwtServerHelper.getTeams( getRequest( ri ), this, userId );
		} catch (OperationAccessControlExceptionNoName oae) {
			GwtTeamingException ex;

			ex = new GwtTeamingException();
			ex.setExceptionType( ExceptionType.ACCESS_CONTROL_EXCEPTION );
			throw ex;
		} 
		
	}// end getMyTeams()

	/**
	 * Save the User Status
	 * 
	 * @param status The text to store in the Micro-Blog
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	public Boolean saveUserStatus( String status ) throws GwtTeamingException
	{
		try
		{
			BinderHelper.addMiniBlogEntry(this, status);
		}
		catch (Exception e)
		{
			GwtTeamingException ex;
			
			ex = new GwtTeamingException();
			ex.setExceptionType( ExceptionType.UNKNOWN );
			throw ex;
		}
		
		return Boolean.TRUE;
	}

	/**
	 * Return the "user permalink" URL.
	 * 
	 * @param ri
	 * @param userId
	 * 
	 * @return
	 */
	public String getUserPermalink( HttpRequestInfo ri, String userId )
	{
		if ( userId != null && userId.length() > 0 )
		{
			Long userIdL = new Long( userId );
			
			User u = (User) getProfileModule().getEntry(userIdL);
			return PermaLinkUtil.getPermalink( getRequest( ri ), u );
		}
		
		return "";
	}
	
	/**
	 * Get the User Status from their Micro Blog
	 * 
	 * @param binderId This is the binderId of the workspace we are loading
	 * 
	 * @return UserStatus This object contains information about the user status.
	 * 
	 * @throws GwtTeamingException 
	 */
	public UserStatus getUserStatus(String sbinderId)
			throws GwtTeamingException {
	
		return GwtProfileHelper.getUserStatus(this, sbinderId);
	}

	/**
	 * Get the stats for the user
	 * 
	 * @param ri
	 * @param binderId This is the binderId of the person you want to get stats on.
	 * 
	 * @return ProfileStats This object contains the stat info to display
	 */
	public ProfileStats getProfileStats(HttpRequestInfo ri, String userId) throws GwtTeamingException {
		try
		{
			ProfileStats stats = GwtProfileHelper.getStats(getRequest(ri), this, userId);
			return stats;
		}
		catch (AccessControlException e)
		{
			GwtTeamingException ex = new GwtTeamingException();

			ex.setExceptionType( ExceptionType.ACCESS_CONTROL_EXCEPTION );
			throw ex;
		}
		catch (NoUserByTheIdException nex)
		{
			GwtTeamingException ex;

			ex = new GwtTeamingException();
			ex.setExceptionType( ExceptionType.NO_BINDER_BY_THE_ID_EXCEPTION );
			throw ex;
		}
		catch ( Exception e )
		{
			//Log other errors
			logger.error("Error getting stats for user with binderId "+binderId, e);
		}
		
		return null;
	}

	/**
	 * Get the avatars for the user profile.
	 * 
	 * @param ri
	 * @param binderId  This is the binderId of the user.
	 * 
	 * @return ProfileAttribute  The ProfileAttribute contains the information needed to populate the avatars
	 */
	public ProfileAttribute getProfileAvatars(HttpRequestInfo ri, String binderId) {
		ProfileAttribute attr = GwtProfileHelper.getProfileAvatars(getRequest( ri ), this, Long.valueOf(binderId));
		return attr;
	}
	
	
	/**
	 * Get disk usage information per user
	 */
	public  DiskUsageInfo getDiskUsageInfo(HttpRequestInfo ri, String binderId) throws GwtTeamingException {
		
		DiskUsageInfo diskUsageInfo = null;
		try {
			
			
			diskUsageInfo = new DiskUsageInfo();
			GwtProfileHelper.getDiskUsageInfo(getRequest( ri ), this, binderId, diskUsageInfo);
			
			return diskUsageInfo;
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
	}	
}// end GwtRpcServiceImpl

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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.AuditTrail;
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
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.gwt.client.GwtBrandingData;
import org.kablink.teaming.gwt.client.GwtBrandingDataExt;
import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtPersonalPreferences;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtSearchResults;
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
import org.kablink.teaming.gwt.client.mainmenu.RecentPlaceInfo;
import org.kablink.teaming.gwt.client.mainmenu.SavedSearchInfo;
import org.kablink.teaming.gwt.client.mainmenu.TeamInfo;
import org.kablink.teaming.gwt.client.mainmenu.TeamManagementInfo;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.profile.ProfileInfo;
import org.kablink.teaming.gwt.client.profile.ProfileStats;
import org.kablink.teaming.gwt.client.profile.UserStatus;
import org.kablink.teaming.gwt.client.service.GwtRpcService;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.TagInfo;
import org.kablink.teaming.gwt.client.util.TeamingAction;
import org.kablink.teaming.gwt.client.util.TopRankedInfo;
import org.kablink.teaming.gwt.client.workspacetree.TreeInfo;
import org.kablink.teaming.gwt.server.util.GwtProfileHelper;
import org.kablink.teaming.gwt.server.util.GwtServerHelper;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.report.ReportModule;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.presence.PresenceInfo;
import org.kablink.teaming.presence.PresenceManager;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.teaming.search.filter.SearchFilterKeys;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.ssfs.util.SsfsUtil;
import org.kablink.teaming.util.AbstractAllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.ExportHelper;
import org.kablink.teaming.web.util.Favorites;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.MarkupUtil;
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
			
		case COMMUNITY_TAGS:
		case PERSONAL_TAGS:
		case TAG:
			// this has been replaced by a getTags method in the search engine.
			// searchTermFilter.addTagsFilter( null, searchText );
			break;
		
		case TEAMS:
			//!!! Get code from ajaxFind() in TypeToFindAjaxController.java
			break;
			
		case USER:
			searchTermFilter.addTitleFilter( searchText );
			searchTermFilter.addLoginNameFilter( searchText );
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
					gwtUser = getGwtUser( null, userId );
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
	 * Return the administration options the user has rights to run.
	 */
	public ArrayList<GwtAdminCategory> getAdminActions( String binderId ) throws GwtTeamingException
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

				adminActions = GwtServerHelper.getAdminActions( binder, this );
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
				GwtBrandingDataExt brandingExt;
				Binder brandingSourceBinder;
				String brandingSourceBinderId;
				
				binder = binderModule.getBinder( binderIdL );
				
				// Get the binder where branding comes from.
				brandingSourceBinder = binder.getBrandingSource();
				
				brandingSourceBinderId = brandingSourceBinder.getId().toString();
				brandingData.setBinderId( brandingSourceBinderId );

				// Get the branding that should be applied for this binder.
				branding = brandingSourceBinder.getBranding();
				
				// For some unknown reason, if there is no branding in the db the string we get back
				// will contain only a \n.  We don't want that.
				if ( branding != null && branding.length() == 1 && branding.charAt( 0 ) == '\n' )
					branding = "";
				
				// Parse the branding and replace any markup with the appropriate url.  For example,
				// replace {{atachmentUrl: somename.png}} with a url that looks like http://somehost/ssf/s/readFile/.../somename.png
				branding = MarkupUtil.markupStringReplacement( null, null, null, null, brandingSourceBinder, branding, "view" );
	
				brandingData.setBranding( branding );
				
				// Get the additional branding information.
				{
					String xmlStr;
					
					brandingExt = new GwtBrandingDataExt();
					
					// Is there old-style branding?
					if ( branding != null && branding.length() > 0 )
					{
						// Yes
						brandingExt.setBrandingType( GwtBrandingDataExt.BRANDING_TYPE_ADVANCED );
					}
					else
					{
						// No
						brandingExt.setBrandingType( GwtBrandingDataExt.BRANDING_TYPE_IMAGE );
					}

					// Get the xml that represents the branding data.  The following is an example of what the xml should look like.
					// 	<brandingData fontColor="" brandingImgName="some name" brandingType="image/advanced">
					// 		<background color="" imgName="" />
					// 	</brandingData>
					xmlStr = brandingSourceBinder.getBrandingExt();
					
					if ( xmlStr != null )
					{
						try
			    		{
			    			Document doc;
			    			Node node;
			    			Node attrNode;
		        			String imgName;
							String fileUrl;
							String webPath;
							
							webPath = WebUrlUtil.getServletRootURL();

							// Parse the xml string into an xml document.
							doc = DocumentHelper.parseText( xmlStr );
			    			
			    			// Get the root element.
			    			node = doc.getRootElement();
			    			
			    			// Get the font color.
			    			attrNode = node.selectSingleNode( "@fontColor" );
			    			if ( attrNode != null )
			    			{
			        			String fontColor;
			
			        			fontColor = attrNode.getText();
			        			brandingExt.setFontColor( fontColor );
			    			}
			    			
			    			// Get the name of the branding image
			    			attrNode = node.selectSingleNode( "@brandingImgName" );
			    			if ( attrNode != null )
			    			{
			        			imgName = attrNode.getText();

			    				if ( imgName != null && imgName.length() > 0 )
			    				{
			    					brandingExt.setBrandingImgName( imgName );

			    					// Is the image name "__no image__" or "__default teaming image__"?
			    					// These are special names that don't represent a real image file name.
			    					if ( !imgName.equalsIgnoreCase( "__no image__" ) && !imgName.equalsIgnoreCase( "__default teaming image__" ) )
			    					{
			    						// No, Get a url to the file.
				    					fileUrl = WebUrlUtil.getFileUrl( webPath, WebKeys.ACTION_READ_FILE, brandingSourceBinder, imgName );
				    					brandingExt.setBrandingImgUrl( fileUrl );
			    					}
			    				}
			    			}
			    			
			    			// Get the type of branding, "advanced" or "image"
			    			attrNode = node.selectSingleNode( "@brandingType" );
			    			if ( attrNode != null )
			    			{
			    				String type;
			    				
			    				type = attrNode.getText();
			    				if ( type != null && type.equalsIgnoreCase( GwtBrandingDataExt.BRANDING_TYPE_IMAGE ) )
			    					brandingExt.setBrandingType( GwtBrandingDataExt.BRANDING_TYPE_IMAGE );
			    				else
			    					brandingExt.setBrandingType( GwtBrandingDataExt.BRANDING_TYPE_ADVANCED );
			    			}
			    			
			    			// Get the branding rule.
			    			attrNode = node.selectSingleNode( "@brandingRule" );
			    			if ( attrNode != null )
			    			{
			    				String ruleName;
			    				
			    				ruleName = attrNode.getText();
			    				if ( ruleName != null )
			    				{
			    					if ( ruleName.equalsIgnoreCase( GwtBrandingDataExt.BrandingRule.BINDER_BRANDING_OVERRIDES_SITE_BRANDING.toString() ) )
			    						brandingExt.setBrandingRule( GwtBrandingDataExt.BrandingRule.BINDER_BRANDING_OVERRIDES_SITE_BRANDING );
			    					else if ( ruleName.equalsIgnoreCase( GwtBrandingDataExt.BrandingRule.DISPLAY_BOTH_SITE_AND_BINDER_BRANDING.toString() ) )
			    						brandingExt.setBrandingRule( GwtBrandingDataExt.BrandingRule.DISPLAY_BOTH_SITE_AND_BINDER_BRANDING );
			    					else if ( ruleName.equalsIgnoreCase( GwtBrandingDataExt.BrandingRule.DISPLAY_SITE_BRANDING_ONLY.toString() ) )
			    						brandingExt.setBrandingRule( GwtBrandingDataExt.BrandingRule.DISPLAY_SITE_BRANDING_ONLY );
			    				}
			    			}
			    			
			    			// Get the <background color="" imgName="" stretchImg="" /> node
			    			node = node.selectSingleNode( "background" );
			    			if ( node != null )
			    			{
			    				// Get the background color.
			    				attrNode = node.selectSingleNode( "@color" );
			    				if ( attrNode != null )
			    				{
			        				String bgColor;
			
			        				bgColor = attrNode.getText();
			        				brandingExt.setBackgroundColor( bgColor );
			    				}
			    				
			    				// Get the name of the background image.
			    				attrNode = node.selectSingleNode( "@imgName" );
			    				if ( attrNode != null )
			    				{
			        				imgName = attrNode.getText();

				    				if ( imgName != null && imgName.length() > 0 )
				    				{
				    					// Get a url to the file.
				    					fileUrl = WebUrlUtil.getFileUrl( webPath, WebKeys.ACTION_READ_FILE, brandingSourceBinder, imgName );
				    					brandingExt.setBackgroundImgUrl( fileUrl );
				    					
				    					brandingExt.setBackgroundImgName( imgName );
				    				}
			    				}

			    				// Get the value of whether or not to stretch the background image.
	        					brandingExt.setBackgroundImgStretchValue( true );
			    				attrNode = node.selectSingleNode( "@stretchImg" );
			    				if ( attrNode != null )
			    				{
			        				String stretch;
			
			        				stretch = attrNode.getText();
			        				if ( stretch != null && stretch.equalsIgnoreCase( "false" ) )
			        					brandingExt.setBackgroundImgStretchValue( false );
			    				}
			    			}
			    		}
			    		catch(Exception e)
			    		{
			    			m_logger.warn( "Unable to parse branding ext " + xmlStr );
			    		}
					}
					
					brandingData.setBrandingExt( brandingExt );
				}
				
				// Are we dealing with site branding?
				{
					Binder topWorkspace;
					
					// Get the top workspace.
					topWorkspace = getWorkspaceModule().getTopWorkspace();				
					
					// Are we dealing with the site branding.
					if ( binderIdL.compareTo( topWorkspace.getId() ) == 0 )
					{
						// Yes
						brandingData.setIsSiteBranding( true );
					}
				}
			}
		}
		catch (NoBinderByTheIdException nbEx)
		{
			// Nothing to do
		}
		catch (AccessControlException acEx)
		{
			// Nothing to do
		}
		catch (Exception e)
		{
			// Nothing to do
		}
		
		return brandingData;
	}// end getBinderBrandingData()
	
	
	/**
	 * Return the "document base url" that is used in tinyMCE configuration
	 */
	public String getDocumentBaseUrl( String binderId ) throws GwtTeamingException
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
				webPath = WebUrlUtil.getServletRootURL();
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
	 * Return a list of the names of the files that are attachments for the given binder
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
	 * Return a GwtUser object for the given user id
	 */
	public GwtUser getGwtUser( String zoneUUID, String userId ) throws GwtTeamingException
	{
		Binder binder = null;
		BinderModule bm = getBinderModule();
		GwtUser reply = null;
		ProfileModule pm = getProfileModule();
		User user = null;
		
		try
		{
			Long userIdL;
			String zoneInfoId;

			// Get the id of the zone we are running in.
			zoneInfoId = ExportHelper.getZoneInfo().getId();
			if ( zoneInfoId == null )
			{
				zoneInfoId = "";
			}
			
			// Are we looking for an id that was imported from another zone?
			userIdL = new Long( userId );
			if ( zoneUUID != null && zoneUUID.length() > 0 && !zoneInfoId.equals( zoneUUID ) )
			{
				// Yes, get the id for it in this zone.
				userIdL = bm.getZoneBinderId( userIdL, zoneUUID, EntityType.folder.name() );
			}

			// Do we have an id and can we access it as a User with a
			// workspace?
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
					Long wsId;
					
					user = users[0];
					wsId = user.getWorkspaceId();
					if ( null != wsId )
					{
						binder = bm.getBinder( user.getWorkspaceId() );
					}
					
					// Note:  Cases where a user won't have a workspace
					//    ID include special user IDs such as the email
					//    posting agent and others.
				}
			}
			if (( null != binder ) && ( null != user ))
			{
				// Yes!  Construct a GwtUser object for it.
				reply = new GwtUser();
				reply.setUserId( user.getId() );
				reply.setWorkspaceId( binder.getId() );
				reply.setName( user.getName() );
				reply.setTitle( user.getTitle() );
				reply.setWorkspaceTitle( user.getWSTitle() );
				reply.setViewWorkspaceUrl( PermaLinkUtil.getPermalink( binder ) );
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
	 * Return the "binder permalink" url
	 */
	public String getBinderPermalink( String binderId )
	{
		if ( binderId != null && binderId.length() > 0 )
		{
			Long binderIdL = new Long( binderId );
			Binder binder = getBinderModule().getBinder( binderIdL );
			return PermaLinkUtil.getPermalink( binder );
		}
		
		return "";
	}
	
	/**
	 * Return the "modify binder" url
	 */
	public String getModifyBinderUrl( String binderId )
	{
		AdaptedPortletURL adapterUrl;
		Binder binder;
		Long binderIdL;

		// Create a url that can be used to modify a binder.
		adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext( "ss_forum", true );
		adapterUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER );
		adapterUrl.setParameter( WebKeys.URL_OPERATION, WebKeys.OPERATION_MODIFY );
		adapterUrl.setParameter( WebKeys.URL_BINDER_ID, binderId );
		
		binderIdL = new Long( binderId );
		binder = getBinderModule().getBinder( binderIdL );
		adapterUrl.setParameter( WebKeys.URL_BINDER_TYPE, binder.getEntityType().name() );
		
		return adapterUrl.toString();
	}// end getModifyBinderUrl()
	
	
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
				boolean showToolTips;
				
				// No
				// Get the user's display style preference
				displayStyle = user.getDisplayStyle();
				personalPrefs.setDisplayStyle( displayStyle );
				
				// If the display style equals "accessible" then tool tips are on.
				showToolTips = false;
				if ( displayStyle.equalsIgnoreCase( "accessible" ) )
					showToolTips = true;
				personalPrefs.setShowToolTips( showToolTips );
				
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
					int numEntriesPerPage = 10;
					
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
	 * Return the url needed to invoke the "site administration" page.  If the user does not
	 * have rights to run the "site administration" page we will throw an exception.
	 */
	public String getSiteAdministrationUrl( String binderId ) throws GwtTeamingException
	{
		// Does the user have rights to run the "site administration" page?
		if ( getAdminModule().testAccess( AdminOperation.manageFunction ) )
		{
			AdaptedPortletURL adapterUrl;
			
			// Yes
			adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext( "ss_forum", true );
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
	 * Return a GwtBrandingData object for the home workspace.
	 */
	public GwtBrandingData getSiteBrandingData() throws GwtTeamingException
	{
		Binder topWorkspace;
		GwtBrandingData brandingData;
		
		try
		{
			// Get the top workspace.
			topWorkspace = getWorkspaceModule().getTopWorkspace();				
		
			// Get the branding data from the top workspace.
			brandingData = getBinderBrandingData( topWorkspace.getId().toString() );
		}
		catch (Exception e)
		{
			brandingData = new GwtBrandingData();
		}

		brandingData.setIsSiteBranding( true );

		return brandingData;
	}// end getSiteBrandingData()
	
	
    /**
     * 
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
	 */
	public GwtUpgradeInfo getUpgradeInfo() throws GwtTeamingException
	{
		GwtUpgradeInfo upgradeInfo;
		
		upgradeInfo = new GwtUpgradeInfo();
		
		// Get the Teaming version and build information
		upgradeInfo.setReleaseInfo( ReleaseInfo.getReleaseInfo() );
		
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
	 * @return
	 */
	public String getUserWorkspacePermalink()
	{
		Binder userWS = getBinderModule().getBinder( GwtServerHelper.getCurrentUser().getWorkspaceId() );
		return PermaLinkUtil.getPermalink( userWS );
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
	 * Returns a List<TreeInfo> containing the display information for
	 * the Binder hierarchy referred to by binderId from the
	 * perspective of the currently logged in user.
	 * 
	 * The information returned is typically used for driving a
	 * horizontal WorkspaceTreeControl widget.
	 * 
	 * @param binderIdS
	 * 
	 * @return
	 */
	public List<TreeInfo> getHorizontalTree( String binderIdS ) {
		Binder binder;
		List<TreeInfo> reply;
		
		// Can we access the Binder?
		binder = GwtServerHelper.getBinderForWorkspaceTree( this, binderIdS );
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
	 * @param binderId
	 * 
	 * @return
	 */
	public TreeInfo getHorizontalNode( String binderIdS )
	{
		Binder binder;
		TreeInfo reply;
		
		// Access the Binder...
		binder = GwtServerHelper.getBinderForWorkspaceTree( this, binderIdS );

		// ...and build the TreeInfo for it.
		reply = ((null == binder) ? null : GwtServerHelper.buildTreeInfoFromBinder( this, binder ));
		return reply;
	}// end getHorizontalNode()

	/**
	 * Returns the ID of the nearest containing workspace of a given
	 * Binder.
	 * 
	 * @return
	 */
	public String getRootWorkspaceId( String binderId ) {
		Binder binder = getBinderModule().getBinder( Long.parseLong( binderId ) );
		Workspace binderWS = BinderHelper.getBinderWorkspace( binder );
		return String.valueOf( binderWS.getId() );
	}// end getRootWorkspaceId() 

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
	 * @param binderIdS
	 * 
	 * @return
	 */
	public TreeInfo getVerticalTree( String binderIdS )
	{
		Binder binder;
		TreeInfo reply;
		
		// Can we access the Binder?
		binder = GwtServerHelper.getBinderForWorkspaceTree( this, binderIdS );
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
				binder = binder.getParentBinder();
				while ( binder.getId().longValue() != binderWSIdVal )
				{
					expandedBindersList.add( binder.getId() );
					binder = binder.getParentBinder();
				}
			}
			
			// ...build the TreeInfo for the request Binder...
			reply = GwtServerHelper.buildTreeInfoFromBinder(
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
	 * @param binderId
	 * 
	 * @return
	 */
	public TreeInfo getVerticalNode( String binderIdS )
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
			reply = GwtServerHelper.buildTreeInfoFromBinder( this, binder, expandedBindersList );
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
	 * Returns information about the current user's favorites.
	 * 
	 * @return
	 */
	public List<FavoriteInfo> getFavorites()
	{
		return GwtServerHelper.getFavorites( this );
	}// end getFavorites()
	
	/**
	 * Returns information about the teams the current user is a member
	 * of.
	 * 
	 * @return
	 */
	public List<TeamInfo> getMyTeams()
	{
		return GwtServerHelper.getMyTeams( this );
	}// end getMyTeams()
	
	/**
	 * Return the url needed to invoke the user's "micro-blog" page.  
	 * 
 	 */
	public String getMicrBlogUrl( String binderId ) throws GwtTeamingException
	{
		Principal p = GwtProfileHelper.getPrincipalByBinderId(this, binderId);
		
		// Does the user have rights to run the "site administration" page?
		if ( p != null )
		{
			AdaptedPortletURL adapterUrl;
			
			Long random = System.currentTimeMillis();
			
			// Yes
			adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext( "ss_forum", true );
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
 	 */
	public String getImUrl( String binderId ) throws GwtTeamingException
	{
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
	}// end getImUrl

	public GwtPresenceInfo getPresenceInfo( String binderId ) throws GwtTeamingException
	{
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
	}// end getPresenceInfo

	/**
	 * Returns information about the recent place the current user has
	 * visited.
	 * 
	 * @return
	 */
	public List<RecentPlaceInfo> getRecentPlaces()
	{
		return GwtServerHelper.getRecentPlaces( this );
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
		Map<String, Map> tbMaps = ((Map<String, Map>) hSession.getAttribute(GwtUIHelper.CACHED_TOOLBARS_KEY));
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
	 * @return
	 */
	public List<TopRankedInfo> getTopRanked()
	{
		return GwtServerHelper.getTopRanked( this );
	}
	
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
	 * @binderId
	 * 
	 * @return
	 */
	public TeamManagementInfo getTeamManagementInfo( String binderId )
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
			binder = getBinderModule().getBinder( Long.parseLong( binderId ) );
			if ( EntityIdentifier.EntityType.profiles != binder.getEntityType() )
			{
				AdaptedPortletURL adapterUrl;
			
				// Yes!  Then the user is allowed to view team membership.
				tmi.setViewAllowed( true );
	
				// If the user can manage the team...
				if ( getBinderModule().testAccess( binder, BinderOperation.manageTeamMembers ) )
				{
					// ...store the team management URL...
					adapterUrl = new AdaptedPortletURL( ((PortletRequest) null), "ss_forum", true );
					adapterUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_ADD_TEAM_MEMBER );
					adapterUrl.setParameter( WebKeys.URL_BINDER_ID, binderId );
					adapterUrl.setParameter( WebKeys.URL_BINDER_TYPE, binder.getEntityType().name() );
					tmi.setManageUrl( adapterUrl.toString() );
				}
	
				// ...if the user can send mail to the team...
				if ( MiscUtil.hasString( user.getEmailAddress() ) )
				{
					// ...store the send mail URL...
					adapterUrl = new AdaptedPortletURL( ((PortletRequest) null), "ss_forum", true );
					adapterUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_SEND_EMAIL );
					adapterUrl.setParameter( WebKeys.URL_BINDER_ID, binderId );
					adapterUrl.setParameter( WebKeys.URL_APPEND_TEAM_MEMBERS, Boolean.TRUE.toString() );
					tmi.setSendMailUrl( adapterUrl.toString() );
				}
	
				// ...if the user can start a team meeting...
				if ( getIcBrokerModule().isEnabled() )
				{
					// ...store the team meeting URL.
					adapterUrl = new AdaptedPortletURL( ((PortletRequest) null), "ss_forum", true );
					adapterUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_ADD_MEETING );
					adapterUrl.setParameter( WebKeys.URL_BINDER_ID, binderId );
					adapterUrl.setParameter( WebKeys.URL_APPEND_TEAM_MEMBERS, Boolean.TRUE.toString() );
					tmi.setTeamMeetingUrl( adapterUrl.toString() );
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
					
					// For some reason I don't understand the "tooltips preference" and the
					// "entry display style" setting are both stored in the "displayStyle" field.
					// If "tooltips" are turned on we need to put the word "accessible" in the
					// "displayStyle" field.
					if ( personalPrefs.getShowToolTips() == true )
						newDisplayStyle = "accessible";
					
					// Only allow "word" characters (such as a-z_0-9 )
					if ( newDisplayStyle.equals("") || !newDisplayStyle.matches( "^.*[\\W]+.*$" ) )
					{
						updates.put( ObjectKeys.USER_PROPERTY_DISPLAY_STYLE, newDisplayStyle );
						profileModule.modifyEntry( user.getId(), new MapInputData( updates ) );
					}
				}
				
				// Save the "show tutorial panel" preference
				{
					String tutorialPanelState;
					
					if ( personalPrefs.getShowTutorialPanel() )
						tutorialPanelState = "2";
					else
						tutorialPanelState = "1";
					
					profileModule.setUserProperty( null, ObjectKeys.USER_PROPERTY_TUTORIAL_PANEL_STATE, tutorialPanelState );
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
	 */
	public ProfileInfo getProfileInfo(String binderId) {
		
		//get the binder
		ProfileInfo profile = GwtProfileHelper.buildProfileInfo(this, Long.valueOf(binderId));
		
		return profile;
	}
	
	
	/**
	 * Get the profile information for the Quick View based on the binder Id passed in.
	 */
	public ProfileInfo getQuickViewInfo(String binderId) {
		
		Long binderIdL = Long.valueOf(binderId);
		
		//get the binder
		ProfileInfo profile = GwtProfileHelper.buildQuickViewProfileInfo(this, binderIdL);
		
		return profile;
	}
	
	/**
	 * Returns Look up the workspace owner and return the list of teams they belong to.
	 * 
	 * @param binderId  The binderId of the workspace being viewed
	 * 
	 * @return
	 */
	public List<TeamInfo> getTeams(String binderId)
	{
		Long userId = null;
		Principal p = null;

		if(binderId != null) {
			p = GwtProfileHelper.getPrincipalByBinderId(this, binderId);
		}
		
		if(p != null){
			userId = p.getId();
		}
		
		return GwtServerHelper.getTeams( this, userId );
	}// end getMyTeams()

	/**
	 * Save the User Status
	 * @param status The text to store in the Micro-Blog
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
	 * Get the User Status from their Micro Blog
	 * @param binderId This is the binderId of the workspace we are loading
	 * 
	 * @return UserStatus This object contains information about the user status.
	 * 
	 */
	public UserStatus getUserStatus(String sbinderId)
			throws GwtTeamingException {
	
		return GwtProfileHelper.getUserStatus(this, sbinderId);
	}

	public ProfileStats getProfileStats(String binderId) {
		
		Long userId = null;
		Principal p = null;

		ProfileStats stats = new ProfileStats();
		
		if(binderId != null) {
			p = GwtProfileHelper.getPrincipalByBinderId(this, binderId);
		}
		
		if(p != null){
			userId = p.getId();
		}
		
		Set<Long> memberIds = new HashSet();
		memberIds.add(userId);
		
		Date endDate = Calendar.getInstance().getTime();
		Calendar c = Calendar.getInstance();
		c.set(1990, 0, 0);
		
		Date startDate = c.getTime();
		
		
		List<Map<String,Object>> report = getReportModule().generateActivityReportByUser(memberIds, startDate, endDate, ReportModule.REPORT_TYPE_SUMMARY);
		Map<String,Object> row = null;
		if(!report.isEmpty()) row = report.get(0);
		
		if(row!=null){
			Object obj = row.get(AuditTrail.AuditType.add.name());
			
			stats.setEntries(obj.toString());
		}
		
		return stats;
	}
}// end GwtRpcServiceImpl

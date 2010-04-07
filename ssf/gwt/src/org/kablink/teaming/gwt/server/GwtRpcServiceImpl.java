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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
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
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtSearchResults;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.admin.ExtensionDefinitionInUseException;
import org.kablink.teaming.gwt.client.admin.ExtensionFiles;
import org.kablink.teaming.gwt.client.admin.ExtensionInfoClient;
import org.kablink.teaming.gwt.client.mainmenu.FavoriteInfo;
import org.kablink.teaming.gwt.client.mainmenu.TeamInfo;
import org.kablink.teaming.gwt.client.mainmenu.TeamManagementInfo;
import org.kablink.teaming.gwt.client.mainmenu.TeamingMenuItem;
import org.kablink.teaming.gwt.client.profile.ProfileInfo;
import org.kablink.teaming.gwt.client.profile.UserStatus;
import org.kablink.teaming.gwt.client.service.GwtRpcService;
import org.kablink.teaming.gwt.server.util.GwtProfileHelper;
import org.kablink.teaming.gwt.server.util.GwtServerHelper;
import org.kablink.teaming.gwt.client.workspacetree.TreeInfo;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.report.ReportModule;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.teaming.search.filter.SearchFilterKeys;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.AbstractAllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.ExportHelper;
import org.kablink.teaming.web.util.Favorites;
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
		brandingData.setBinderId( binderId );
		
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
				
				binder = binderModule.getBinder( binderIdL );
				
				// Get the binder where branding comes from.
				brandingSourceBinder = binder.getBrandingSource();
				
				// Get the branding that should be applied for this binder.
				branding = brandingSourceBinder.getBranding();
				
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
					xmlStr = binder.getBrandingExt();
					
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
				    					fileUrl = WebUrlUtil.getFileUrl( webPath, WebKeys.ACTION_READ_FILE, binder, imgName );
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
				    					fileUrl = WebUrlUtil.getFileUrl( webPath, WebKeys.ACTION_READ_FILE, binder, imgName );
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
			    			Log logger;

			    			logger = LogFactory.getLog( getClass() );
			    			logger.warn( "Unable to parse branding ext " + xmlStr );
			    		}
					}
					
					brandingData.setBrandingExt( brandingExt );
				}
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
	 * Return the "binder permalink" url
	 */
	public String getBinderPermalink( String binderId )
	{
		Long binderIdL = new Long( binderId );
		Binder binder = getBinderModule().getBinder( binderIdL );
		return PermaLinkUtil.getPermalink(binder);
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
	public String getUserWorkspacePermalink()
	{
		Binder userWS = getBinderModule().getBinder( GwtServerHelper.getCurrentUser().getWorkspaceId() );
		return PermaLinkUtil.getPermalink( userWS );
	}// end getUserWorkspacePermalink()
	

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
		// Access the Binder's nearest containing Workspace...
		long binderId = Long.parseLong( binderIdS );
		Binder binder = getBinderModule().getBinder( binderId );
		
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
		List<TreeInfo> reply = GwtServerHelper.buildTreeInfoFromBinderList(
			this,
			bindersList );


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
		// Access the Binder...
		long binderId = Long.parseLong( binderIdS );
		Binder binder = getBinderModule().getBinder( binderId );

		// ...and build the TreeInfo for it.
		return GwtServerHelper.buildTreeInfoFromBinder( this, binder );
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
		// Access the Binder's nearest containing Workspace...
		long binderId = Long.parseLong( binderIdS );
		Binder binder = getBinderModule().getBinder( binderId );
		Workspace binderWS = BinderHelper.getBinderWorkspace( binder );

		// ...note that the Workspace should always be expanded...
		Long binderWSId = binderWS.getId();
		ArrayList<Long> expandedBindersList = new ArrayList<Long>();
		expandedBindersList.add( binderWSId );

		// ...calculate which additional Binder's that must be expanded
		// ...to show the requested Binder...
		long binderWSIdVal = binderWSId.longValue();
		if (binderId != binderWSIdVal) {
			binder = binder.getParentBinder();
			while ( binder.getId().longValue() != binderWSIdVal )
			{
				expandedBindersList.add( binder.getId() );
				binder = binder.getParentBinder();
			}
		}
		
		// ...build the TreeInfo for the request Binder...
		TreeInfo reply = GwtServerHelper.buildTreeInfoFromBinder(
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
		// Access the Binder...
		long binderId = Long.parseLong( binderIdS );
		Binder binder = getBinderModule().getBinder( binderId );

		// ...note that the Binder will now be expanded...
		ArrayList<Long> expandedBindersList = new ArrayList<Long>();
		expandedBindersList.add( binderId );

		// ...and build the TreeInfo folist.toArray(infoArray);r it.
		return GwtServerHelper.buildTreeInfoFromBinder( this, binder, expandedBindersList );
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
	}//end updateFavorites
	
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
	 * Returns information about the teams the current user is a member of.
	 * 
	 * @return
	 */
	public List<TeamInfo> getMyTeams()
	{
		return GwtServerHelper.getMyTeams( this );
	}// end getMyTeams()
	
	/**
	 * Returns a List<TeamingMenuItem> of the TeamingMenuItem's
	 * applicable for the given context.
	 * 
	 * @param binderId
	 * 
	 * @return
	 */
	public List<TeamingMenuItem> getMenuItems( String binderId )
	{
//!		...this needs to be implemented...
		return new ArrayList<TeamingMenuItem>();
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
			AdaptedPortletURL adapterUrl;
			Binder binder;
			
			// No!  Then the user is allowed to view team membership.
			tmi.setViewAllowed( true );

			// If the user can manage the team...
			binder = getBinderModule().getBinder( Long.parseLong( binderId ) );
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
	 * Get the profile information based on the binder Id passed in.
	 */
	public ProfileInfo getProfileInfo(String binderId) {
		
		//get the binder
		ProfileInfo profile = GwtProfileHelper.buildProfileInfo(this, Long.valueOf(binderId));
		
		return profile;
	}

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
	
		//This is the object that is streamed back to the client
		UserStatus userStatus = new UserStatus();
		//Convert binderID to Long
		Long binderId = Long.valueOf(sbinderId);
		Binder binder = getBinderModule().getBinder(binderId);

		//Get the Owner of the binder
		Principal p = binder.getOwner();
		Long workspaceId = p.getWorkspaceId();

		//We need to match the binder to the correct user, so we can read the miniblog from the correct user
		if(!binderId.equals(workspaceId)) {
			//then we need to find the correct owner
			String owner = binder.getName();
			if(owner!=null && !owner.equals("")){
				List<String> names = new ArrayList<String>();
				names.add(owner);

				Collection<Principal> principals;
				principals = getProfileModule().getPrincipalsByName(names);
				if (!principals.isEmpty()) p = (Principal)principals.iterator().next();
			}
		} 
		
		//Get the list of miniblog entries by this user (uses the "miniblog" family attribute to find them)
		List <Long> userIds = new ArrayList<Long>();
		userIds.add(p.getId());

		//Get the User object for this principle
		SortedSet<User> users = getProfileModule().getUsers(userIds);
		User u = null;
		if (!users.isEmpty()) u = users.iterator().next();
		
		//Check this user object to see if they cleared their status, don't display a status if cleared.
		if(u != null) {
			String sStatus = u.getStatus();
			if(sStatus == null || sStatus.equals("")) {
				return userStatus;
			}
		}
		
		Long[] userIdsArray = new Long[]{p.getId()};
		
		String page = "0";
		int pageStart = Integer.valueOf(page) * Integer.valueOf(SPropsUtil.getString("relevance.entriesPerBox"));
		
		//Calling into api to read the user status because it checks access controls
		List<Map<String,Object>> statuses = getReportModule().getUsersStatuses(userIdsArray, null, null, 
				pageStart + Integer.valueOf(SPropsUtil.getString("relevance.entriesPerBox")));
		if (statuses != null && statuses.size() > pageStart) {
			Map<String,Object> statusMap = statuses.get(0);
			
			User statusUser = (User) statusMap.get(ReportModule.USER);
			statusUser.getStatus();
			
			String description = (String)statusMap.get(ReportModule.DESCRIPTION);
			Date modifyDate = (Date)statusMap.get(ReportModule.DATE);
			
			userStatus.setMiniBlogId(statusUser.getMiniBlogId());
			userStatus.setStatus(description);
			userStatus.setModifyDate(modifyDate);
		}
		
		return userStatus;
	}
}// end GwtRpcServiceImpl

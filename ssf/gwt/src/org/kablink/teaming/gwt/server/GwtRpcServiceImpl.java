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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtSearchResults;
import org.kablink.teaming.gwt.client.GwtSearchCriteria.SearchType;
import org.kablink.teaming.gwt.client.service.GwtRpcService;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.authentication.AuthenticationModule;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.dashboard.DashboardModule;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.file.ConvertedFileModule;
import org.kablink.teaming.module.file.FileModule;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.ic.ICBrokerModule;
import org.kablink.teaming.module.ical.IcalModule;
import org.kablink.teaming.module.ldap.LdapModule;
import org.kablink.teaming.module.license.LicenseModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.report.ReportModule;
import org.kablink.teaming.module.rss.RssModule;
import org.kablink.teaming.module.template.TemplateModule;
import org.kablink.teaming.module.workflow.WorkflowModule;
import org.kablink.teaming.module.workspace.WorkspaceModule;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.teaming.search.filter.SearchFilterKeys;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.util.search.Constants;
import org.springframework.web.portlet.ModelAndView;



/**
 * 
 * @author jwootton
 *
 */
public class GwtRpcServiceImpl  implements GwtRpcService, AllModulesInjected
{
	private AdminModule			m_adminModule;
	private AuthenticationModule	m_authenticationModule;
	private BinderModule			m_binderModule;
	private ConvertedFileModule	m_convertedFileModule;
	private DashboardModule		m_dashboardModule;
	private DefinitionModule		m_definitionModule;
	private FileModule				m_fileModule;
	private FolderModule			m_folderModule;
	private IcalModule				m_icalModule;
	private ICBrokerModule			m_icBrokerModule;
	private LdapModule				m_ldapModule;
	private LicenseModule			m_licenseModule;
	private ProfileModule			m_profileModule;
	private ReportModule			m_reportModule;
	private RssModule				m_rssModule;
	private TemplateModule			m_templateModule;
	private WorkflowModule			m_workflowModule;
	private WorkspaceModule		m_workspaceModule;
	private ZoneModule				m_zoneModule;
	

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
//				searchResults.setResults( entries );
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
    

    /**
     * 
     */
	public AdminModule getAdminModule()
	{
		return m_adminModule;
	}// end getAdminModule()
	

	/**
	 * 
	 */
	public AuthenticationModule getAuthenticationModule()
	{
		return m_authenticationModule;
	}// end getAuthenticationModule()
	

    /**
     * 
     */
	public BinderModule getBinderModule()
	{
		return m_binderModule;
	}// end getBinderModule()
	

	/**
	 * 
	 */
	public ConvertedFileModule getConvertedFileModule()
	{
		return m_convertedFileModule;
	}// end getConvertedFileModule()
	

	/**
	 * 
	 */
	public DashboardModule getDashboardModule()
	{
		return m_dashboardModule;
	}// end getDashboardModule()
	

	/**
	 * 
	 */
	public DefinitionModule getDefinitionModule()
	{
		return m_definitionModule;
	}// end getDefinitionModule()
	
	
	/**
	 * 
	 */
	public FileModule getFileModule()
	{
		return m_fileModule;
	}// end getFileModule()
	
	
	/**
	 * 
	 */
	public FolderModule getFolderModule()
	{
		return m_folderModule;
	}// end getFolderModule()
	

	/**
	 * 
	 */
	public IcalModule getIcalModule()
	{
		return m_icalModule;
	}// end getIcalModule()
	
	
	/**
	 * 
	 */
	public ICBrokerModule getIcBrokerModule()
	{
		return m_icBrokerModule;
	}// end getIcBrokerModule()
	

	/**
	 * 
	 */
	public LdapModule getLdapModule()
	{
		return m_ldapModule;
	}// end getLdapModule()
	
	
	/**
	 * 
	 */
	public LicenseModule getLicenseModule()
	{
		return m_licenseModule;
	}// end getLicenseModule()
	
	
	/**
	 * 
	 */
	public ProfileModule getProfileModule()
	{
		return m_profileModule;
	}// end getProfileModule()
	
	
	/**
	 * 
	 */
	public ReportModule getReportModule()
	{
		return m_reportModule;
	}// end getReportModule()
	
	
	/**
	 * 
	 */
	public RssModule getRssModule()
	{
		return m_rssModule;
	}// end getRssModule()
	
	
	/**
	 * 
	 */
    public TemplateModule getTemplateModule()
    {
    	return m_templateModule;
    }// end getTemplateModule()
    
    
    /**
     * 
     */
	public WorkflowModule getWorkflowModule()
	{
		return m_workflowModule;
	}// end getWorkflowModule()

	
	/**
	 * 
	 */
	public WorkspaceModule getWorkspaceModule()
	{
		return m_workspaceModule;
	}// end getWorkspaceModule()
	

	/**
	 * 
	 */
	public ZoneModule getZoneModule()
	{
		return m_zoneModule;
	}// end getZoneModule()
	
	
	/**
	 * 
	 */
	public void setAdminModule( AdminModule adminModule )
	{
		m_adminModule = adminModule;
	}// end setAdminModule()
	
	
	/**
	 * 
	 */
	public void setAuthenticationModule( AuthenticationModule authenticationModule )
	{
		m_authenticationModule = authenticationModule;
	}// end setAuthenticationModule()

	
    /**
     * 
     */
	public void setBinderModule( BinderModule binderModule )
	{
		m_binderModule = binderModule;
	}// end setBinderModule()
	

	/**
	 * 
	 */
	public void setConvertedFileModule( ConvertedFileModule convertedFileModule )
	{
		m_convertedFileModule = convertedFileModule;
	}// end setConvertedFileModule()
	

	/**
	 * 
	 */
	public void setDashboardModule( DashboardModule dashboardModule )
	{
		m_dashboardModule = dashboardModule;
	}// end setDashboardModule()
	
	
	/**
	 * 
	 */
	public void setDefinitionModule( DefinitionModule definitionModule )
	{
		m_definitionModule = definitionModule;
	}// end setDefinitionModule()
	

	/**
	 * 
	 */
	public void setFileModule( FileModule fileModule )
	{
		m_fileModule = fileModule;
	}// end setFileModule()
	

	/**
	 * 
	 */
	public void setFolderModule( FolderModule folderModule )
	{
		m_folderModule = folderModule;
	}// end setFolderModule()
	
	
	/**
	 * 
	 */
	public void setIcalModule( IcalModule icalModule )
	{
		m_icalModule = icalModule;
	}// end setIcalModule()
	
	
	/**
	 * 
	 */
	public void setIcBrokerModule( ICBrokerModule icBrokerModule )
	{
		m_icBrokerModule = icBrokerModule;
	}// end setIcBrokerModule()
	
	/**
	 * 
	 */
	public void setLdapModule( LdapModule ldapModule )
	{
		m_ldapModule = ldapModule;
	}// end setLdapModule()
	
	
	/**
	 * 
	 */
	public void setLicenseModule( LicenseModule licenseModule )
	{
		m_licenseModule = licenseModule;
	}// end setLicenseModule()
	

	/**
	 * 
	 */
	public void setProfileModule( ProfileModule profileModule )
	{
		m_profileModule = profileModule;
	}// end setProfileModule()
	
	
	/**
	 * 
	 */
	public void setReportModule( ReportModule reportModule )
	{
		m_reportModule = reportModule;
	}// end setReportModule()
	
	
	/**
	 * 
	 */
	public void setRssModule( RssModule rssModule )
	{
		m_rssModule = rssModule;
	}// end setRssModule()
	
	
    /**
     * 
     */
    public void setTemplateModule( TemplateModule templateModule )
    {
    	m_templateModule = templateModule;
    }// end setTemplateModule()
    

	/**
	 * 
	 */
	public void setWorkflowModule( WorkflowModule workflowModule )
	{
		m_workflowModule = workflowModule;
	}// end setWorkflowModule()
	
	
	/**
	 * 
	 */
	public void setWorkspaceModule( WorkspaceModule workspaceModule )
	{
		m_workspaceModule = workspaceModule;
	}// end setWorkspaceModule()
	
	/**
	 * 
	 */
	public void setZoneModule( ZoneModule zoneModule )
	{
		m_zoneModule = zoneModule;
	}// end setZoneModule()

}// end GwtRpcServiceImpl

/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.DateTools;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.gwt.client.BlogArchiveFolder;
import org.kablink.teaming.gwt.client.BlogArchiveInfo;
import org.kablink.teaming.gwt.client.BlogArchiveMonth;
import org.kablink.teaming.gwt.client.BlogPage;
import org.kablink.teaming.gwt.client.BlogPages;
import org.kablink.teaming.gwt.client.util.TagInfo;
import org.kablink.teaming.module.template.TemplateModule;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.search.filter.SearchFilterKeys;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.ListFolderHelper;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Order;
import org.kablink.util.search.Restrictions;



/**
 * Helper methods for the GWT UI server code that services blog folder requests
 *
 * @author jwootton@novell.com
 */
public class GwtBlogHelper
{
	protected static Log m_logger = LogFactory.getLog( GwtBlogHelper.class );

	
	/**
	 * Return the blog archive information for the given blog folder.
	 * The majority of this code was copied from ListFolderHelper.buildBlogBeans()
	 */
	public static BlogArchiveInfo getBlogArchiveInfo( AllModulesInjected ami, Long folderId )
	{
		BlogArchiveInfo info;
		
		info = new BlogArchiveInfo();
		info.setFolderId( folderId );
		
		try
		{
			Document searchFilter;
			Element rootElement;
			Folder folder;
			LinkedHashMap<String,BlogArchiveMonth> monthHits;
			Map<String,BlogArchiveFolder> folderHits;
			Map options2;
	    	Map entriesMap;
			List entries;
			Iterator itEntries;

			folder = ami.getFolderModule().getFolder( folderId );

			searchFilter = DocumentHelper.createDocument();
			rootElement = searchFilter.addElement( SearchFilterKeys.FilterRootName );
	    	rootElement.addElement( SearchFilterKeys.FilterTerms );
			rootElement.addElement( SearchFilterKeys.FilterTerms );

			options2 = new HashMap();
			options2.put(
					ObjectKeys.SEARCH_MAX_HITS, 
					Integer.valueOf( SPropsUtil.getString( "blog.archives.searchCount" ) ) );
			options2.put( ObjectKeys.SEARCH_SORT_DESCEND, new Boolean( true ) );
			options2.put( ObjectKeys.SEARCH_SORT_BY, Constants.CREATION_YEAR_MONTH_FIELD);

			// Look only for binderId=binder and doctype = entry (not attachement)
	    	if ( folder != null )
	    	{
				Document searchFilter2;
	    		Element field;
	        	Element child;
	        	Binder blogSetBinder;

	    		searchFilter2 = DocumentHelper.createDocument();
	    		rootElement = searchFilter2.addElement( Constants.AND_ELEMENT );
	    		field = rootElement.addElement( Constants.FIELD_ELEMENT );
	        	field.addAttribute( Constants.FIELD_NAME_ATTRIBUTE,Constants.ENTRY_ANCESTRY );
	        	child = field.addElement( Constants.FIELD_TERMS_ELEMENT );
	        	
	        	// Find the top-most blog folder that is folder lives in.
	        	blogSetBinder = getTopBlogFolder( ami, folder );
	        	if ( blogSetBinder != null )
	        	{
	        		child.setText( blogSetBinder.getId().toString() );
	        	} else 
	        	{
	        		child.setText( folder.getId().toString() );
	        	}
	        	
	        	// Look only for docType=entry and entryType=entry
	        	field = rootElement.addElement( Constants.FIELD_ELEMENT );
	        	field.addAttribute( Constants.FIELD_NAME_ATTRIBUTE,Constants.DOC_TYPE_FIELD );
	        	child = field.addElement( Constants.FIELD_TERMS_ELEMENT );
	        	child.setText( Constants.DOC_TYPE_ENTRY );
	           	field = rootElement.addElement( Constants.FIELD_ELEMENT );
	           	field.addAttribute( Constants.FIELD_NAME_ATTRIBUTE,Constants.ENTRY_TYPE_FIELD );
	           	child = field.addElement( Constants.FIELD_TERMS_ELEMENT );
	           	child.setText( Constants.ENTRY_TYPE_ENTRY );
	        	options2.put( ObjectKeys.SEARCH_FILTER_AND, searchFilter2 );
	    	}
			
	    	// Find all the blog entries that live in the given folder and all subfolders.
	    	entriesMap = ami.getBinderModule().executeSearchQuery( searchFilter, Constants.SEARCH_MODE_NORMAL, options2 );
			entries = (List) entriesMap.get( ObjectKeys.SEARCH_ENTRIES );
			monthHits = new LinkedHashMap<String,BlogArchiveMonth>();
			folderHits = new HashMap<String,BlogArchiveFolder>();
			itEntries = entries.iterator();
			DateFormat df = DateFormat.getInstance();
	    	SimpleDateFormat sf = (SimpleDateFormat)df;
	    	User user = RequestContextHolder.getRequestContext().getUser();
			sf.setTimeZone(user.getTimeZone());
	    	sf.applyPattern("yyyyMM");

			while ( itEntries.hasNext() )
			{
				Map entry;

				entry = (Map)itEntries.next();
				if ( entry.containsKey( Constants.CREATION_DATE_FIELD ) )
				{
					Date creationDate;
					String yearMonth;
					String entryBinderId;
					BlogArchiveMonth archiveMonth;

					// Have we already found this year/month?
					creationDate = (Date)entry.get( Constants.CREATION_DATE_FIELD );
					yearMonth = sf.format(creationDate);
					if ( !monthHits.containsKey( yearMonth ) )
					{
						String year;
						String monthNumber;
						String monthName;
						int m;
						String[] args;

						// No
						// Get the localized name of the month.
						year = yearMonth.substring( 0, 4 );
						monthNumber = yearMonth.substring( 4, 6 );
						m = Integer.valueOf( monthNumber ).intValue() - 1;
						args = new String[2];
						args[0] = NLT.get( ListFolderHelper.monthNames[m%12] );
						args[1] = year;
						monthName = NLT.get( "calendar.monthYear", args );

						archiveMonth = new BlogArchiveMonth();
						archiveMonth.setName( monthName );
						archiveMonth.setMonthOfYear( Integer.valueOf( m ) );
						archiveMonth.setYear( Integer.valueOf( year ) );
						
						// Set the creation start/end time for this month.  It can be
						// used to search for entries created in this month.
						{
							Calendar startDate;
							Long startTimeInMillis;
							Long endTimeInMillis;
							
							// Yes
							startDate = Calendar.getInstance();
							startDate.set( Calendar.MONTH, archiveMonth.getMonthOfYear() );
							startDate.set( Calendar.DAY_OF_MONTH, 1 );
							startDate.set( Calendar.YEAR, archiveMonth.getYear() );
							startTimeInMillis = new Long( startDate.getTimeInMillis() );
							
							// Add 1 month to the start time minus 1 second.
							startDate.add( Calendar.MONTH, 1 );
							endTimeInMillis = new Long( startDate.getTimeInMillis() - 1000 );
							
							archiveMonth.setCreationStartTime( startTimeInMillis );
							archiveMonth.setCreationEndTime( endTimeInMillis );
						}
						
						monthHits.put( yearMonth, archiveMonth );
					}

					// Increment the number of blog entries for this year/month
					archiveMonth = monthHits.get( yearMonth );
					archiveMonth.incNumBlogEntries();
					
					// Keep track of the hits per folder
					entryBinderId = (String)entry.get( Constants.BINDER_ID_FIELD );
					if ( entryBinderId != null )
					{
						String monthFolder;
						BlogArchiveFolder archiveFolder;
						
						// Have we seen this year/month and binder already?
						monthFolder = yearMonth + "/" + entryBinderId;
						if ( !folderHits.containsKey( monthFolder ) )
						{
							// No
							try
							{
								Folder subFolder;
								
								subFolder = ami.getFolderModule().getFolder( Long.valueOf( entryBinderId ) );
								
								archiveFolder = new BlogArchiveFolder();
								archiveFolder.setFolderId( Long.valueOf( entryBinderId ) );
								archiveFolder.setName( subFolder.getTitle() );

								folderHits.put( monthFolder, archiveFolder );
								
								// Add this folder to the year/month it belongs to
								archiveMonth.addFolder( archiveFolder );
							}
							catch (AccessControlException acEx)
							{
								// Nothing to do.
							}
						}
						
						archiveFolder = folderHits.get( monthFolder );
						archiveFolder.incNumBlogEntries();
					}
				}
				if ( entry.containsKey( Constants.CREATION_YEAR_MONTH_FIELD ) )
				{
					//TODO - ???
				}
			}
			
			// Add the information for each year/month we found
			for (Map.Entry<String, BlogArchiveMonth> nextEntry : monthHits.entrySet() )
			{
				info.addMonth( nextEntry.getValue() );
			}
			
			// Add information about the global tags that exist on the blog entries
			if ( entries != null )
			{
				List entryCommunityTags;
				int intMaxHitsForCommunityTags;
				int intMaxHits;
				int i;

				entryCommunityTags = new ArrayList();
				entryCommunityTags = BinderHelper.sortCommunityTags( entries );
				
				// Gather up information about each global tag.
				intMaxHitsForCommunityTags = BinderHelper.getMaxHitsPerTag( entryCommunityTags );
				intMaxHits = intMaxHitsForCommunityTags;
				entryCommunityTags = BinderHelper.rateCommunityTags( entryCommunityTags, intMaxHits );
				entryCommunityTags = BinderHelper.determineSignBeforeTag( entryCommunityTags, "" );
				
				// Add each tag to the archive info.  entryCommunityTags is a list of Maps.
				// Each Map holds a key/value pair.  The following is an example of what the map
				// looks like:
				// {ssTagSign=+, ssTagSearchText=cbug, searchResultsRatingCSS=ss_largeprint, ssTag=cbug, searchResultsCount=2, searchResultsRating=66}
				for (i = 0; i < entryCommunityTags.size(); ++i)
				{
					TagInfo tagInfo;
					Map tagMap;
					String tagName;
					String tagSearchText;
					Integer tagCnt;
					Integer tagRating;
					
					tagMap = (Map) entryCommunityTags.get( i );
					tagName = (String) tagMap.get( WebKeys.TAG_NAME );
					tagCnt = (Integer) tagMap.get( WebKeys.SEARCH_RESULTS_COUNT );
					tagRating = (Integer) tagMap.get( WebKeys.SEARCH_RESULTS_RATING );
					tagSearchText = (String) tagMap.get( WebKeys.TAG_SEARCH_TEXT );
					
					if ( tagName != null )
					{
						tagInfo = new TagInfo();
						tagInfo.setTagName( tagName );
						
						if ( tagCnt != null )
							tagInfo.setTagSearchResultsCount( tagCnt );
						
						if ( tagRating != null )
							tagInfo.setTagSearchResultsRating( tagRating );
						
						if ( tagSearchText != null )
							tagInfo.setTagSearchText( tagSearchText );
						
						info.addGlobalTag( tagInfo );
					}
				}
			}
		}
		catch (AccessControlException acEx)
		{
			// The user doesn't have rights to see the folder.
		}
		
		return info;
	}
	
	/**
	 * Return a list of all the "pages" (folders) that are found in the given blog folder.
	 */
	public static BlogPages getBlogPages( AllModulesInjected ami, Long folderId )
	{
		BlogPages blogPages;
		
		blogPages = new BlogPages();
		
		try
		{
			Folder folder;
			Binder topFolder;
			Long folderTemplateId;

			folder = ami.getFolderModule().getFolder( folderId );

			// Get the "top" blog folder
	    	topFolder = getTopBlogFolder( ami, folder );
	    	if ( topFolder == null )
	    		topFolder = folder;

	    	blogPages.setTopFolderId( topFolder.getId() );
	    	
	    	// Get the id of the folder template of the top-most folder
	    	folderTemplateId = getFolderTemplateId( ami, topFolder );
	    	blogPages.setFolderTemplateId( folderTemplateId );
	    	
	    	// This code was copied from ListFolderHelper.buildBlogPageBeans() and then tweaked.
	    	{
	    		List folderIds;
	    		Criteria crit;
	    		Map binderMap;
	    		List binderMapList; 
	    		List binderIdList;
	          	SortedSet binderList;

	    		// Get a list of all the binders that are descendants of the top folder.
	    		folderIds = new ArrayList();
	    		folderIds.add( topFolder.getId().toString() );
	    		crit = new Criteria();
	    		crit = crit.add( Restrictions.in( Constants.DOC_TYPE_FIELD, new String[] {Constants.DOC_TYPE_BINDER} ) );
	    		crit.add( Restrictions.in( Constants.ENTRY_ANCESTRY, folderIds ) );
	    		crit.addOrder( Order.asc( Constants.SORT_TITLE_FIELD ) );
	    		binderMap = ami.getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, 0, ObjectKeys.SEARCH_MAX_HITS_SUB_BINDERS,
	    				org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(Constants.DOCID_FIELD));

	    		binderMapList = (List)binderMap.get( ObjectKeys.SEARCH_ENTRIES ); 
	    		binderIdList = new ArrayList();

	          	for (Iterator iter=binderMapList.iterator(); iter.hasNext();)
	          	{
	          		Map entryMap = (Map) iter.next();
	          		binderIdList.add( new Long((String)entryMap.get(Constants.DOCID_FIELD)) );
	          	}
	            //Get sub-binder list including intermediate binders that may be inaccessible
	          	binderList = ami.getBinderModule().getBinders( binderIdList, Boolean.FALSE );
	            for (Iterator iter=binderList.iterator(); iter.hasNext();)
	            {
	         		Binder nextBinder;

	         		nextBinder = (Binder)iter.next();
	          		if ( nextBinder.isDeleted() )
	          			continue;
	          		
	          		if ( nextBinder.getEntityType().equals( EntityIdentifier.EntityType.folder ) )
	          		{
	          			BlogPage blogPage;
	          			String url;
	          			
	          			blogPage = new BlogPage();
	          			blogPage.setFolderId( nextBinder.getId().toString() );
	          			blogPage.setFolderName( nextBinder.getTitle() );
	          			url = PermaLinkUtil.getPermalink( nextBinder );
	          			blogPage.setViewFolderUrl( url );
	          			
	          			blogPages.addPage( blogPage );
	          		}
	    		}
	    	}
		}
		catch (AccessControlException acEx)
		{
			// The user doesn't have rights to see the folder.
		}
		
		return blogPages;
	}
	
	/**
	 * Get the id of the folder template for the given folder
	 */
	private static Long getFolderTemplateId( AllModulesInjected ami, Binder binder )
	{
		Long folderTemplateId;
		TemplateModule templateMod;
		List<TemplateBinder> folderTemplates;
		
		if ( ami == null || binder == null )
			return null;
		
		folderTemplateId = null;
		templateMod = ami.getTemplateModule();
		
		folderTemplates = templateMod.getTemplates( Definition.FOLDER_VIEW );
		folderTemplates.addAll( templateMod.getTemplates( Definition.FOLDER_VIEW, binder, true ) );
		if ( folderTemplates.isEmpty() )
		{
			folderTemplates.add( templateMod.addDefaultTemplate( Definition.FOLDER_VIEW ) );
		}

		// Do we have any templates?
		if ( folderTemplates != null && folderTemplates.size() > 0 )
		{
			String folderEntryDefId;

			// Yes
			folderEntryDefId = binder.getEntryDefId();

			// Find the template that goes with the given binder.
			for (TemplateBinder templateBinder:  folderTemplates)
			{
				// Is this the template for this folder?
				if ( templateBinder.getEntryDefId().equals( folderEntryDefId ) )
				{
					// Yes!  Save its ID.
					folderTemplateId = templateBinder.getId();
					break;
				}
			}

			// Did we find the template ID for the folder?
			if ( folderTemplateId == null )
			{
				// No
				// Default to the first one in the list.
				folderTemplateId = folderTemplates.get( 0 ).getId();
			}
		}
		
		return folderTemplateId;
	}
	
	/**
	 * Get the top-most blog folder that the given folder lives in.
	 */
	private static Binder getTopBlogFolder( AllModulesInjected ami, Folder folder )
	{
		Binder parentBinder;
		Binder topBinder;
		String viewType;

		viewType = Definition.VIEW_STYLE_BLOG;
		parentBinder = folder.getParentBinder();
		topBinder = folder;
		while ( parentBinder != null )
		{
			Integer parentDefType;
			
			parentDefType = parentBinder.getDefinitionType();
			if ( parentDefType != null )
			{
				String parentViewType;
				
				// If the parent binder is not a folder, break.
				if ( parentDefType.equals( Definition.FOLDER_VIEW ) == false )
					break;
				
				// If the parent binder is not a blog, break.
				parentViewType = BinderHelper.getViewType( ami, parentBinder );
				if ( viewType.equals( parentViewType ) == false )
					break;
			}
			
			topBinder = parentBinder;
			parentBinder = parentBinder.getParentBinder();
		}

		return topBinder;
	}
}

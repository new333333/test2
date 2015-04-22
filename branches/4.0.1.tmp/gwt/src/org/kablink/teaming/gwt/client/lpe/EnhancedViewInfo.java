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
package org.kablink.teaming.gwt.client.lpe;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.lpe.EnhancedViewProperties.EnhancedViewType;

/**
 * This class is used to hold information about an enhanced view
 */
public class EnhancedViewInfo
{
	private String m_displayName;
	private String m_desc;
	private String m_jspName;
	private boolean m_folderRequired;
	private boolean m_entryRequired;
	private boolean m_titleOptional;	// Can the user specify whether to display the title?
	private EnhancedViewType m_viewType;
	
	/**
	 * 
	 */
	public EnhancedViewInfo( String jspName )
	{
		GwtTeamingMessages messages;
		
		m_titleOptional = false;
		messages = GwtTeaming.getMessages();
		
		if ( jspName.equalsIgnoreCase( "landing_page_entry.jsp" ) )
		{
			m_displayName = messages.enhancedViewDisplayEntry();
			m_desc = messages.enhancedViewDisplayEntryDesc();
			m_folderRequired = false;
			m_entryRequired = true;
			m_viewType = EnhancedViewType.DISPLAY_ENTRY;
		}
		else if ( jspName.equalsIgnoreCase( "landing_page_folder.jsp" ) )
		{
			m_displayName = messages.enhancedViewDisplayRecentEntries();
			m_desc = messages.enhancedViewDisplayRecentEntriesDesc();
			m_folderRequired = true;
			m_entryRequired = false;
			m_viewType = EnhancedViewType.DISPLAY_RECENT_ENTRIES;
		}
		else if ( jspName.equalsIgnoreCase( "landing_page_folder_list.jsp" ) )
		{
			m_displayName = messages.enhancedViewDisplayRecentEntriesList();
			m_desc = messages.enhancedViewDisplayRecentEntriesListDesc();
			m_folderRequired = true;
			m_entryRequired = false;
			m_titleOptional = true;
			m_viewType = EnhancedViewType.DISPLAY_LIST_OF_RECENT_ENTRIES;
		}
		else if ( jspName.equalsIgnoreCase( "landing_page_folder_list_sorted.jsp" ) )
		{
			m_displayName = messages.enhancedViewDisplayRecentEntriesListSorted();
			m_desc = messages.enhancedViewDisplayRecentEntriesListSortedDesc();
			m_folderRequired = true;
			m_entryRequired = false;
			m_titleOptional = true;
			m_viewType = EnhancedViewType.DISPLAY_SORTED_LIST_RECENT_ENTRIES;
		}
		else if ( jspName.equalsIgnoreCase( "landing_page_folder_list_sorted_files.jsp" ) )
		{
			m_displayName = messages.enhancedViewDisplayFileListSorted();
			m_desc = messages.enhancedViewDisplayFileListSortedDesc();
			m_folderRequired = true;
			m_entryRequired = false;
			m_titleOptional = true;
			m_viewType = EnhancedViewType.DISPLAY_SORTED_LIST_FILES;
		}
		else if ( jspName.equalsIgnoreCase( "landing_page_calendar.jsp" ) )
		{
			m_displayName = messages.enhancedViewDisplayCalendarFolder();
			m_desc = messages.enhancedViewDisplayCalendarFolderDesc();
			m_folderRequired = true;
			m_entryRequired = false;
			m_titleOptional = true;
			m_viewType = EnhancedViewType.DISPLAY_CALENDAR;
		}
		else if ( jspName.equalsIgnoreCase( "landing_page_task_folder.jsp" ) )
		{
			m_displayName = messages.enhancedViewDisplayTaskFolder();
			m_desc = messages.enhancedViewDisplayTaskFolderDesc();
			m_folderRequired = true;
			m_entryRequired = false;
			m_viewType = EnhancedViewType.DISPLAY_TASK_FOLDER;
		}
		else if ( jspName.equalsIgnoreCase( "landing_page_survey.jsp" ) )
		{
			m_displayName = messages.enhancedViewDisplaySurvey();
			m_desc = messages.enhancedViewDisplaySurveyDesc();
			m_folderRequired = false;
			m_entryRequired = true;
			m_viewType = EnhancedViewType.DISPLAY_SURVEY;
		}
		else if ( jspName.equalsIgnoreCase( "landing_page_my_calendar_events.jsp" ) )
		{
			m_displayName = messages.enhancedViewDisplayMyCalendarEvents();
			m_desc = messages.enhancedViewDisplayMyCalendarEventsDesc();
			m_folderRequired = false;
			m_entryRequired = false;
			m_viewType = EnhancedViewType.DISPLAY_MY_CALENDAR_EVENTS;
		}
		else if ( jspName.equalsIgnoreCase( "landing_page_my_tasks.jsp" ) )
		{
			m_displayName = messages.enhancedViewDisplayMyTasks();
			m_desc = messages.enhancedViewDisplayMyTasksDesc();
			m_folderRequired = false;
			m_entryRequired = false;
			m_viewType = EnhancedViewType.DISPLAY_MY_TASKS;
		}
		else
		{
			m_displayName = "Unknown jsp: " + jspName;
			m_desc = "Unknown";
			m_folderRequired = false;
			m_entryRequired = false;
			m_viewType = EnhancedViewType.UNKNOWN;
		}

		m_jspName = jspName;
	}
	
	/**
	 * 
	 */
	public String getDesc()
	{
		return m_desc;
	}
	
	/**
	 * 
	 */
	public String getDisplayName()
	{
		return m_displayName;
	}
	
	/**
	 * 
	 */
	public String getJspName()
	{
		return m_jspName;
	}
	
	/**
	 * For this view, can the user specify whether to show the title?
	 */
	public boolean getTitleOptional()
	{
		return m_titleOptional;
	}
	
	/**
	 * Return the type of view this is.
	 */
	public EnhancedViewType getViewType()
	{
		return m_viewType;
	}
	
	
	/**
	 * Does this view require the user to select an entry?
	 */
	public boolean isEntryRequired()
	{
		return m_entryRequired;
	}
	
	/**
	 * Does this view require the user to select a folder?
	 */
	public boolean isFolderRequired()
	{
		return m_folderRequired;
	}
}


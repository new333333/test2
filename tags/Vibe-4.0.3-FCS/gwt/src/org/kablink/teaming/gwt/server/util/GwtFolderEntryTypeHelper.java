/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.gwt.client.util.GwtFolderEntryType;
import org.kablink.teaming.util.AllModulesInjected;

/**
 * Helper methods for creating GwtFolderEntryType objects in server-side code.
 *
 * @author jwootton@novell.com
 */
public class GwtFolderEntryTypeHelper
{
	protected static Log m_logger = LogFactory.getLog( GwtFolderEntryTypeHelper.class );

	/**
	 * Returns a FolderEntryType for the given folder entry. 
	 */
	public static GwtFolderEntryType getFolderEntryType( AllModulesInjected ami, FolderEntry folderEntry )
	{
		String family;
		
		if ( folderEntry == null )
			return GwtFolderEntryType.OTHER;
		
		family = GwtServerHelper.getFolderEntityFamily( ami, folderEntry );
		
		if ( family == null || family.length() == 0 )
			return GwtFolderEntryType.OTHER;
		
		if ( family.equalsIgnoreCase( Definition.FAMILY_BLOG ) )
			return GwtFolderEntryType.BLOG ;
		
		if ( family.equalsIgnoreCase( Definition.FAMILY_CALENDAR ) )
			return GwtFolderEntryType.CALENDAR;
		
		if ( family.equalsIgnoreCase( Definition.FAMILY_DISCUSSION ) )
			return GwtFolderEntryType.DISCUSSION;
		
		if ( family.equalsIgnoreCase( Definition.FAMILY_FILE ) )
			return GwtFolderEntryType.FILE;
		
		if ( family.equalsIgnoreCase( Definition.FAMILY_MILESTONE ) )
			return GwtFolderEntryType.MILESTONE;
		
		if ( family.equalsIgnoreCase( Definition.FAMILY_MINIBLOG ) )
			return GwtFolderEntryType.MICRO_BLOG;
		
		if ( family.equalsIgnoreCase( Definition.FAMILY_PHOTO ) )
			return GwtFolderEntryType.PHOTO;
		
		if ( family.equalsIgnoreCase( Definition.FAMILY_SURVEY ) )
			return GwtFolderEntryType.SURVEY;
		
		if ( family.equalsIgnoreCase( Definition.FAMILY_TASK ) )
			return GwtFolderEntryType.TASK;
		
		if ( family.equalsIgnoreCase( Definition.FAMILY_WIKI ) )
			return GwtFolderEntryType.WIKI;
		
		return GwtFolderEntryType.OTHER;
	}

	
	/*
	 * Returns a FolderEntryType for the given folder entry id. 
	 */
	public static GwtFolderEntryType getFolderEntryType( AllModulesInjected ami, Long folderEntryId )
	{
		GwtFolderEntryType folderEntryType = GwtFolderEntryType.OTHER;
		FolderEntry entry;
		
		if ( folderEntryId == null )
			return GwtFolderEntryType.OTHER;
		
		try
		{
			entry = ami.getFolderModule().getEntry( null, folderEntryId );
			folderEntryType = GwtFolderEntryTypeHelper.getFolderEntryType( ami, entry );
		}
		catch ( Exception ex )
		{
			m_logger.error( "In GwtFolderEntryTypeHelper.getFolderEntryType(), exception was thrown" );
			ex.printStackTrace();
		}
		
		return folderEntryType;
	}
	

	/**
	 * Returns a FolderEntryType for the given EntitiyIdentifier. 
	 */
	public static GwtFolderEntryType getFolderEntryType( AllModulesInjected ami, EntityIdentifier eid )
	{
		if ( eid.getEntityType() == EntityType.folderEntry )
			return GwtFolderEntryTypeHelper.getFolderEntryType( ami, eid.getEntityId() );
			
		m_logger.error( "In GwtFolderEntryTypeHelper.getFolderEntryType(), the EntityIdentifier param does not refer to a folder entry." );

		return null;
	}
	
	/**
	 * Get the folder entry type for each of the given entry ids
	 */
	public static HashMap<Long,GwtFolderEntryType> getFolderEntryTypes(
		AllModulesInjected ami,
		ArrayList<Long> listOfEntryIds )
	{
		HashMap<Long,GwtFolderEntryType> listOfEntryTypes;
		
		listOfEntryTypes = new HashMap<Long,GwtFolderEntryType>();
		
		if ( listOfEntryIds != null && listOfEntryIds.size() > 0 )
		{
			for ( Long nextEntryId : listOfEntryIds )
			{
				GwtFolderEntryType entryType;
				
				entryType = GwtFolderEntryTypeHelper.getFolderEntryType( ami, nextEntryId );
				listOfEntryTypes.put( nextEntryId, entryType );
			}
		}
		
		return listOfEntryTypes;
	}
}

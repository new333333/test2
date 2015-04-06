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
package org.kablink.teaming.gwt.client.util;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Enumeration used to identify the various folder entry types
 * 
 * @author jwootton@novell.com
 */
public enum GwtFolderEntryType implements IsSerializable
{
	BLOG,
	CALENDAR,
	DISCUSSION,
	FILE,
	MICRO_BLOG,
	MILESTONE,
	PHOTO,
	SURVEY,
	TASK,
	WIKI,
	
	OTHER;
	
	
	/**
	 * 
	 */
	public static GwtFolderEntryType getInstance( String type )
	{
		if ( type == null || type.length() == 0 )
			return OTHER;
		
		if ( type.equalsIgnoreCase( "BLOG" ) )
			return BLOG;
		
		if ( type.equalsIgnoreCase( "CALENDAR" ) )
			return CALENDAR;
		
		if ( type.equalsIgnoreCase( "DISCUSSION" ) )
			return DISCUSSION;
		
		if ( type.equalsIgnoreCase( "FILE" ) )
			return FILE;
		
		if ( type.equalsIgnoreCase( "MICRO_BLOG" ) )
			return MICRO_BLOG;
		
		if ( type.equalsIgnoreCase( "MILESTONE" ) )
			return MILESTONE;
		
		if ( type.equalsIgnoreCase( "PHOTO" ) )
			return PHOTO;
		
		if ( type.equalsIgnoreCase( "SURVEY" ) )
			return SURVEY;
		
		if ( type.equalsIgnoreCase( "TASK" ) )
			return TASK;
		
		if ( type.equalsIgnoreCase( "WIKI" ) )
			return WIKI;
		
		return OTHER;
	}
}

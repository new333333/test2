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
package org.kablink.teaming.gwt.client;

import java.util.ArrayList;

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;
import org.kablink.teaming.gwt.client.util.TagInfo;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * This class is used in rpc calls and represents a serializable class used to hold
 * archive information about a blog folder.
 * @author jwootton
 *
 */
public class BlogArchiveInfo
	implements IsSerializable, VibeRpcResponseData
{
	private Long m_folderId;	// Id of the blog folder we are working with
	private ArrayList<BlogArchiveMonth> m_listOfMonths;	// List of months that have blog entries.
	private ArrayList<TagInfo> m_listOfGlobalTags;		// List of global tags that are on the blog entries for the given folder
	
	/**
	 * 
	 */
	public BlogArchiveInfo()
	{
		m_folderId = null;
		m_listOfMonths = new ArrayList<BlogArchiveMonth>();
		m_listOfGlobalTags = new ArrayList<TagInfo>();
	}
	
	/**
	 * Add a global tag to our list.
	 */
	public void addGlobalTag( TagInfo tagInfo )
	{
		m_listOfGlobalTags.add( tagInfo );
	}
	
	/**
	 * Add a month to our list.
	 */
	public void addMonth( BlogArchiveMonth archiveMonth )
	{
		m_listOfMonths.add( archiveMonth );
	}
	
	/**
	 * 
	 */
	public Long getFolderId()
	{
		return m_folderId;
	}
	
	/**
	 * Return the list of global tags.
	 */
	public ArrayList<TagInfo> getListOfGlobalTags()
	{
		return m_listOfGlobalTags;
	}
	
	/**
	 * Get a list of the months that have blog entries in them
	 */
	public ArrayList<BlogArchiveMonth> getListOfMonths()
	{
		return m_listOfMonths;
	}
	
	/**
	 * 
	 */
	public void setFolderId( Long folderId )
	{
		m_folderId = folderId;
	}
	
	/**
	 * 
	 */
	public void setListOfMonths( ArrayList<BlogArchiveMonth> listOfMonths )
	{
		m_listOfMonths = listOfMonths;
	}
}

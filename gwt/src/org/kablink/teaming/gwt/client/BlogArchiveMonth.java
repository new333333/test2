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

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * This class is used in rpc calls and represents a serializable class used to hold
 * information about a month in a blog archive.
 * @author jwootton
 *
 */
public class BlogArchiveMonth
	implements IsSerializable
{
	private String m_name;
	private Integer m_month;
	private Integer m_year;
	private Long m_creationEndTime;
	private Long m_creationStartTime;
	private int m_numEntries;	// Number of blog entries in this month
	private ArrayList<BlogArchiveFolder> m_listOfFolders;	// List of folders that have blog entries for this month
	
	// The following data members are UI specific.
	transient private boolean m_isMonthOpen;
	
	/**
	 * 
	 */
	public BlogArchiveMonth()
	{
		m_name = null;
		m_month = null;
		m_year = null;
		m_creationEndTime = null;
		m_creationStartTime = null;
		m_numEntries = 0;
		m_listOfFolders = new ArrayList<BlogArchiveFolder>();
		m_isMonthOpen = false;
	}
	
	/**
	 * Add the given BlogArchiveFolder to our list of folders
	 */
	public void addFolder( BlogArchiveFolder archiveFolder )
	{
		m_listOfFolders.add( archiveFolder );
	}
	
	/**
	 * Get a list of the folders that have blog entries in them for the given month.
	 */
	public ArrayList<BlogArchiveFolder> getFolders()
	{
		return m_listOfFolders;
	}
	
	/**
	 * 
	 */
	public Long getCreationEndTime()
	{
		return m_creationEndTime;
	}
	
	/**
	 * 
	 */
	public Long getCreationStartTime()
	{
		return m_creationStartTime;
	}
	
	/**
	 * 
	 */
	public Integer getMonthOfYear()
	{
		return m_month;
	}
	
	/**
	 * 
	 */
	public String getName()
	{
		return m_name;
	}
	
	/**
	 * 
	 */
	public int getNumEntries()
	{
		return m_numEntries;
	}
	
	/**
	 * 
	 */
	public boolean getIsMonthOpen()
	{
		return m_isMonthOpen;
	}
	
	/**
	 * 
	 */
	public Integer getYear()
	{
		return m_year;
	}
	
	/**
	 * Increment the number of blog entries that exist for this month.
	 */
	public int incNumBlogEntries()
	{
		++m_numEntries;
		
		return m_numEntries;
	}
	
	/**
	 * 
	 */
	public void setCreationEndTime( Long endTime )
	{
		m_creationEndTime = endTime;
	}
	
	/**
	 * 
	 */
	public void setCreationStartTime( Long startTime )
	{
		m_creationStartTime = startTime;
	}
	
	/**
	 * 
	 */
	public void setFolders( ArrayList<BlogArchiveFolder> listOfFolders )
	{
		m_listOfFolders = listOfFolders;
	}
	
	/**
	 * 
	 */
	public void setIsMonthOpen( boolean isOpen )
	{
		m_isMonthOpen = isOpen;
	}
	
	/**
	 * 
	 */
	public void setMonthOfYear( Integer month )
	{
		m_month = month;
	}
	
	/**
	 * 
	 */
	public void setName( String name )
	{
		m_name = name;
	}
	
	/**
	 * 
	 */
	public void setNumEntries( int numEntries )
	{
		m_numEntries = numEntries;
	}
	
	/**
	 * 
	 */
	public void setYear( Integer year )
	{
		m_year = year;
	}
}

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
package org.kablink.teaming.gwt.client.admin;


import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * This class represents a category of administration options, ie Reports
 * @author jwootton
 *
 */
public class GwtAdminCategory
	implements IsSerializable
{
	private String m_localizedName = null;
	private ArrayList<GwtAdminAction> m_adminActions;
	private GwtAdminCategoryType m_categoryType;
	
	/**
	 * 
	 */
	public enum GwtAdminCategoryType implements IsSerializable
	{
		MANAGEMENT( "Management" ),
		MANAGE_SEARCH_INDEX( "Manage search index" ),
		REPORTS( "Reports" ),
		SYSTEM( "System" ),

		// This is used as a default case to store a GwtAdminCategoryType when
		// there isn't a real value to store.
		UNDEFINED( "Undefined GwtAdminCategoryType" );

		private final String m_unlocalizedDesc;
		
		/**
		 */
		private GwtAdminCategoryType( String unlocalizedDesc )
		{
			m_unlocalizedDesc = unlocalizedDesc;
		}// end AdminAction()
		
		
		/**
		 */
		public String getUnlocalizedDesc()
		{
			return m_unlocalizedDesc;
		}// end getUnlocalizedDesc()

	}// end GwtAdminCategoryType

	
	/**
	 * 
	 */
	public GwtAdminCategory()
	{
		m_adminActions = new ArrayList<GwtAdminAction>();
		m_categoryType = GwtAdminCategoryType.UNDEFINED;
	}// end GwtAdminCategory()
	

	/**
	 * Add a GwtAdminOption to our list.
	 */

	public void addAdminOption( GwtAdminAction adminOption )
	{
		m_adminActions.add( adminOption );
	}// end addAdminOption()
	
	
	/**
	 * 
	 */

	public ArrayList<GwtAdminAction> getActions()
	{
		return m_adminActions;
	}// end getActions()

	
	/**
	 * 
	 */
	public GwtAdminCategoryType getCategoryType()
	{
		return m_categoryType;
	}// end getCategoryType()
	
	
	/**
	 * 
	 */
	public String getLocalizedName()
	{
		return m_localizedName;
	}// end getLocalizedName()

	
	/**
	 * 
	 */
	public void setLocalizedName( String localizedName )
	{
		m_localizedName = localizedName;
	}// end setLocalizedName()
	
	/**
	 * 
	 */
	public void setCategoryType( GwtAdminCategoryType type )
	{
		m_categoryType = type;
	}// end setCategoryType()
}// end GwtAdminCategory

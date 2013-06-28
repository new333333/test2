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

package org.kablink.teaming.gwt.client.lpe;

import org.kablink.teaming.gwt.client.widgets.PropertiesObj;


/**
 * This class holds all of the properties needed to define a "Utility Element" widget in a landing page.
 * @author jwootton
 *
 */
public class UtilityElementProperties
	implements PropertiesObj
{
	private UtilityElement		m_utilityElement;
	private String m_binderId;
	
	/**
	 * 
	 */
	public UtilityElementProperties()
	{
		m_utilityElement = UtilityElement.LINK_TO_TRACK_FOLDER_OR_WORKSPACE;
		m_binderId = null;
	}// end UtilityElementProperties()
	
	
	/**
	 * 
	 */
	public void copy( PropertiesObj props )
	{
		if ( props instanceof UtilityElementProperties )
		{
			UtilityElementProperties utilityElementProps;
			
			utilityElementProps = (UtilityElementProperties) props;
			setType( utilityElementProps.getType() );
			setBinderId( utilityElementProps.getBinderId() );
		}
	}// end copy()
	

	/**
	 * Return the properties as a string that can be stored in the db.
	 */
	public String createConfigString()
	{
		String str;
		
		str = "utility,element=" + m_utilityElement.getIdentifier() + ";";
		
		return str;
	}// end createConfigString()
	
	
	/**
	 * 
	 */
	public String getBinderId()
	{
		return m_binderId;
	}
	
	
	/**
	 * Return the type of utility element
	 */
	public UtilityElement getType()
	{
		return m_utilityElement;
	}// end getType()
	
	
	/**
	 * Return the name of the selected utility element.
	 */
	public String getUtilityElementName()
	{
		if ( m_utilityElement != null )
			return m_utilityElement.getLocalizedText();
		
		return null;
	}// end getUtilityElementName()
	

	/**
	 * 
	 */
	public void setBinderId( String binderId )
	{
		m_binderId = binderId;
	}
	
	
	/**
	 * 
	 */
	public void setType( UtilityElement utilityElement )
	{
		m_utilityElement = utilityElement;
	}// end setType()
}// end UtilityElementProperties

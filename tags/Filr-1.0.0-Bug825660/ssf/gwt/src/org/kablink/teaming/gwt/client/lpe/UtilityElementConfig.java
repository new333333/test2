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

import org.kablink.teaming.gwt.client.widgets.UtilityElementWidget;
import org.kablink.teaming.gwt.client.widgets.VibeWidget;
import org.kablink.teaming.gwt.client.widgets.WidgetStyles;

/**
 * This class represents the configuration data for a Utility Element
 * @author jwootton
 *
 */
public class UtilityElementConfig extends ConfigItem
{
	private UtilityElementProperties	m_properties;
	
	/**
	 * 
	 */
	public UtilityElementConfig( String configStr, String binderId )
	{
		String[] results;
		
		m_properties = new UtilityElementProperties();
		m_properties.setBinderId( binderId );
		
		// Split the configuration data into its parts.  ie element=xxx
		results = configStr.split( "[,;]" );
		if ( results != null )
		{
			int i;
			
			for (i = 0; i < results.length; ++i)
			{
				String[] results2;
				
				results2 = results[i].split( "=" );
				if ( results2 != null && results2.length == 2 && results2[0] != null && results2[1] != null && results2[1].length() > 0 )
				{
					if ( results2[0].equalsIgnoreCase( "element" ) )
					{
						String type;
						
						type = results2[1];
						if ( type != null )
						{
							if ( type.equalsIgnoreCase( "myWorkspace" ) )
								m_properties.setType( UtilityElement.LINK_TO_MYWORKSPACE );
							else if ( type.equalsIgnoreCase( "siteAdmin" ) )
								m_properties.setType( UtilityElement.LINK_TO_ADMIN_PAGE );
							else if ( type.equalsIgnoreCase( "trackThis" ) )
								m_properties.setType( UtilityElement.LINK_TO_TRACK_FOLDER_OR_WORKSPACE );
							else if ( type.equalsIgnoreCase( "shareThis" ) )
								m_properties.setType( UtilityElement.LINK_TO_SHARE_FOLDER_OR_WORKSPACE );
//							else if ( type.equalsIgnoreCase( "gettingStarted" ) )
//								m_properties.setType( UtilityElement.VIDEO_TUTORIAL );
							else if ( type.equalsIgnoreCase( "signInForm" ) )
								m_properties.setType( UtilityElement.SIGNIN_FORM );
						}
					}
				}
			}
		}
	}// end UtilityElementConfig()
	
	
	/**
	 * 
	 */
	public void addChild( ConfigItem configItem )
	{
		// Nothing to do.
	}// end addChild()
	
	
	/**
	 * Create a composite that can be used on any page.
	 */
	public VibeWidget createWidget( WidgetStyles widgetStyles )
	{
		return new UtilityElementWidget( this, widgetStyles );
	}
	
	/**
	 * Create a DropWidget that can be used in the landing page editor.
	 */
	public UtilityElementDropWidget createDropWidget( LandingPageEditor lpe )
	{
		return new UtilityElementDropWidget( lpe, this );
	}
	
	
	/**
	 * 
	 */
	public UtilityElementProperties getProperties()
	{
		return m_properties;
	}// end getProperties()
}// end UtilityElementConfig

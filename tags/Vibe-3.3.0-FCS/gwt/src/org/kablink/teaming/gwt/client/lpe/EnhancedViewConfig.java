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

import com.google.gwt.dom.client.Style;
import com.google.gwt.http.client.URL;

/**
 * This class represents the configuration data for an enhanced view
 * @author jwootton
 *
 */
public class EnhancedViewConfig extends ConfigItem
{
	private EnhancedViewProperties		m_properties;
	
	/**
	 * 
	 */
	public EnhancedViewConfig( String configStr )
	{
		String[] results;
		
		m_properties = new EnhancedViewProperties();
		
		// Split the configuration data into its parts.  ie jspName=xxx
		results = configStr.split( "[,;]" );
		if ( results != null )
		{
			int i;
			
			for (i = 0; i < results.length; ++i)
			{
				String[] results2;
				
				results2 = ConfigData.splitConfigItem( results[i] );
				if ( results2 != null && results2.length == 2 && results2[0] != null && results2[1] != null && results2[1].length() > 0 )
				{
					try
					{
						if ( results2[0].equalsIgnoreCase( "jspName" ) )
							m_properties.setJspName( URL.decodeComponent( results2[1] ) );
						else if ( results2[0].equalsIgnoreCase( "folderId" ) )
							m_properties.setFolderId( results2[1] );
						else if ( results2[0].equalsIgnoreCase( "entryId" ) )
							m_properties.setEntryId( results2[1] );
						else if ( results2[0].equalsIgnoreCase( "showTitle" ) )
							m_properties.setShowTitle( results2[1].equalsIgnoreCase( "1" ) );
						else if ( results2[0].equalsIgnoreCase( "zoneUUID" ) )
							m_properties.setZoneUUID( results2[1] );
						else if ( results2[0].equalsIgnoreCase( "entriesToShow" ) )
						{
							int numToShow;

							numToShow = 0;
							try
							{
								numToShow = Integer.parseInt( URL.decodeComponent( results2[1] ) );
							}
							catch (Exception ex)
							{
								// Nothing to do.  This is here to handle the case when the data is
								// not properly url encoded or an invalid number string was entered.
							}
							m_properties.setNumEntriesToBeShownValue( numToShow );
						}
						else if ( results2[0].equalsIgnoreCase( "width" ) )
						{
							String value;
							
							// The string looks like, width=nn% or width=nnpx
							value = results2[1];
							if ( value != null )
							{
								int index;
								
								// Is the width using %?
								index = value.indexOf( "%" );
								if ( index != -1 )
								{
									// Yes
									m_properties.setWidthUnits( Style.Unit.PCT );
								}
								else
								{
									// Is the width using px?
									index = value.indexOf( "px" );
									if ( index != -1 )
									{
										// Yes
										m_properties.setWidthUnits( Style.Unit.PX );
									}
								}
								
								// Did we find the units?
								if ( index != -1 )
								{
									String numValue;
									int width;
									
									// Yes
									numValue = value.substring( 0, index );
									try
									{
										width = Integer.parseInt( numValue );
										m_properties.setWidth( width );
									}
									catch (Exception ex)
									{
										// Nothing to do.  This is here to handle the case when the data is
										// not properly url encoded or an invalid number string was entered.
									}
								}
							}
						}
						else if ( results2[0].equalsIgnoreCase( "height" ) )
						{
							String value;
							
							// The string looks like, height=nn% or height=nnpx
							value = results2[1];
							if ( value != null )
							{
								int index;
								
								// Is the height using %?
								index = value.indexOf( "%" );
								if ( index != -1 )
								{
									// Yes
									m_properties.setHeightUnits( Style.Unit.PCT );
								}
								else
								{
									// Is the height using px?
									index = value.indexOf( "px" );
									if ( index != -1 )
									{
										// Yes
										m_properties.setHeightUnits( Style.Unit.PX );
									}
								}
								
								// Did we find the units?
								if ( index != -1 )
								{
									String numValue;
									int width;
									
									// Yes
									numValue = value.substring( 0, index );
									try
									{
										width = Integer.parseInt( numValue );
										m_properties.setHeight( width );
									}
									catch (Exception ex)
									{
										// Nothing to do.  This is here to handle the case when the data is
										// not properly url encoded or an invalid number string was entered.
									}
								}
							}
						}
						else if ( results2[0].equalsIgnoreCase( "overflow" ) )
						{
							String value;
							
							// The string looks like, overflow=auto or overflow=hidden
							value = results2[1];
							if ( value != null )
							{
								if ( value.equalsIgnoreCase( "auto" ) )
									m_properties.setOverflow( Style.Overflow.AUTO );
								else if ( value.equalsIgnoreCase( "hidden" ) )
									m_properties.setOverflow( Style.Overflow.HIDDEN );
							}
						}
					}
					catch (Exception ex)
					{
						// Nothing to do.  This is here to handle the case when the data is
						// not properly url encoded.
					}
				}
			}
		}
	}
	
	
	/**
	 * 
	 */
	public void addChild( ConfigItem configItem )
	{
		// Nothing to do.
	}
	
	
	/**
	 * 
	 */
	public EnhancedViewProperties getProperties()
	{
		return m_properties;
	}

}

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
 * This class holds all of the properties needed to define a "Graphic" widget in a landing page.
 * @author jwootton
 *
 */
public class GraphicProperties
	implements PropertiesObj
{
	private boolean m_showBorder;
	private String m_graphicName;
	private String m_graphicId;
	
	/**
	 * 
	 */
	public GraphicProperties()
	{
		m_showBorder = false;
		m_graphicName = null;
		m_graphicId = null;
	}// end GraphicProperties()
	
	
	/**
	 * 
	 */
	public void copy( PropertiesObj props )
	{
		if ( props instanceof GraphicProperties )
		{
			GraphicProperties graphicProps;
			
			graphicProps = (GraphicProperties) props;
			m_graphicName = graphicProps.getGraphicName();
			m_graphicId = graphicProps.getGraphicId();
			m_showBorder = graphicProps.getShowBorderValue();
		}
	}// end copy()
	

	/**
	 * Return the id of the graphic.
	 */
	public String getGraphicId()
	{
		return m_graphicId;
	}// end getGraphicId()
	
	
	/**
	 * Return the name of the graphic.
	 */
	public String getGraphicName()
	{
		return m_graphicName;
	}// end getGraphicName()
	
	
	/**
	 * Return the "show border" property.
	 */
	public boolean getShowBorderValue()
	{
		return m_showBorder;
	}// end getShowBorderValue()
	
	
	/**
	 * 
	 */
	public void setGraphicId( String id )
	{
		m_graphicId = id;
	}// end setGraphicId()
	
	
	/**
	 * 
	 */
	public void setGraphicName( String name )
	{
		m_graphicName = name;
	}// end setGraphicName()
	
	
	/**
	 * 
	 */
	public void setShowBorder( boolean showBorder )
	{
		m_showBorder = showBorder;
	}// end setShowBorder()
}// end GraphicProperties

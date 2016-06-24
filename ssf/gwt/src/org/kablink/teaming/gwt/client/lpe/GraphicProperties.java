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

import org.kablink.teaming.gwt.client.GetterCallback;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.rpc.shared.GetFileUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.rpc.AsyncCallback;


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
	private String m_binderId;
	
	// The following data members are used to define the width and height of the view.
	private int m_width;
	private Style.Unit m_widthUnits;
	private int m_height;
	private Style.Unit m_heightUnits;
	private Style.Overflow m_overflow;

	/**
	 * 
	 */
	public GraphicProperties()
	{
		m_showBorder = false;
		m_graphicName = null;
		m_graphicId = null;

		// Default the width and height to -1 indicating that a width and height are not set.
		m_width = -1;
		m_widthUnits = Style.Unit.PCT;
		m_height = -1;
		m_heightUnits = Style.Unit.PX;
		m_overflow = Style.Overflow.HIDDEN;

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
			m_binderId = graphicProps.getBinderId();
			m_showBorder = graphicProps.getShowBorderValue();
			m_width = graphicProps.getWidth();
			m_widthUnits = graphicProps.getWidthUnits();
			m_height = graphicProps.getHeight();
			m_heightUnits = graphicProps.getHeightUnits();
			m_overflow = graphicProps.getOverflow();
		}
	}// end copy()
	

	/**
	 * Return the properties as a string that can be stored in the db.
	 */
	public String createConfigString()
	{
		String str;
		
		// The string should look like: "graphic,showBorder=1,graphic=ff80808222665e02012266c5a98d0015,title=StrongBad2.GIF;"
		str = "graphic,showBorder=";
		if ( m_showBorder )
			str += "1";
		else
			str += "0";
		str += ",";
		
		str += "graphic=";
		if ( m_graphicId != null )
			str += ConfigData.encodeConfigData( m_graphicId );
		str += ",";

		str += "title=";
		if ( m_graphicName != null )
			str += ConfigData.encodeConfigData( m_graphicName );

		// Has a width been set?
		if ( m_width > 0 )
		{
			str += ",width=" + String.valueOf( m_width );
			if ( m_widthUnits == Style.Unit.PCT )
				str += "%";
			else
				str += "px";
		}

		// Has a height been set?
		if ( m_height > 0 )
		{
			str += ",height=" + String.valueOf( m_height );
			if ( m_heightUnits == Style.Unit.PCT )
				str += "%";
			else
				str += "px";
		}

		// Add overflow
		str += ",overflow=";
		if ( m_overflow == Style.Overflow.AUTO )
			str += "auto";
		else
			str += "hidden";

		str += ";";

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
	 * Return the value of height.
	 */
	public int getHeight()
	{
		return m_height;
	}
	
	/**
	 * Return the height units
	 */
	public Style.Unit getHeightUnits()
	{
		return m_heightUnits;
	}
	
	/**
	 * Return the value of overflow.
	 */
	public Style.Overflow getOverflow()
	{
		return m_overflow;
	}
	
	/**
	 * Return the "show border" property.
	 */
	public boolean getShowBorderValue()
	{
		return m_showBorder;
	}// end getShowBorderValue()
	
	
	/**
	 * Return the value of width.
	 */
	public int getWidth()
	{
		return m_width;
	}
	
	/**
	 * Return the width units
	 */
	public Style.Unit getWidthUnits()
	{
		return m_widthUnits;
	}
	
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
	 * Get the url needed to display this image
	 */
	public void getGraphicUrl( final GetterCallback<String> callback )
	{
		GetFileUrlCmd cmd;
		
		cmd = new GetFileUrlCmd( m_binderId, m_graphicName );
		
		// Issue an ajax request to get the url needed to display the graphic.
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			public void onFailure( Throwable caught )
			{
				GwtClientHelper.handleGwtRPCFailure(
						caught,
						GwtTeaming.getMessages().rpcFailure_GetFileUrl(),
						getGraphicName() );
			}

			/**
			 * 
			 */
			public void onSuccess( VibeRpcResponse result )
			{
				String url;
				StringRpcResponseData responseData;

				responseData = ((StringRpcResponseData) result.getResponseData());
				url = responseData.getStringValue();
				
				// Return the url
				callback.returnValue( url );
			}
		} );
	}
	
	/**
	 * 
	 */
	public void setHeight( int height )
	{
		m_height = height;
	}
	
	/**
	 * 
	 */
	public void setHeightUnits( Style.Unit units )
	{
		// Ignore this.  The height is always in px
		//m_heightUnits = Style.Unit.PX;
		m_heightUnits = units;
	}
	
	/**
	 * 
	 */
	public void setOverflow( Style.Overflow overflow )
	{
		m_overflow = overflow;
	}
	
	/**
	 * 
	 */
	public void setShowBorder( boolean showBorder )
	{
		m_showBorder = showBorder;
	}// end setShowBorder()

	/**
	 * 
	 */
	public void setWidth( int width )
	{
		m_width = width;
	}
	
	/**
	 * 
	 */
	public void setWidthUnits( Style.Unit units )
	{
		m_widthUnits = units;
	}
	
}// end GraphicProperties

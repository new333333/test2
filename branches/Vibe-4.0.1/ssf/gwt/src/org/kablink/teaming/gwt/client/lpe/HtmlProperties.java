/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.rpc.shared.MarkupStringReplacementCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * This class holds all of the properties needed to define an "HTML" widget in a landing page.
 * @author jwootton
 *
 */
public class HtmlProperties
	implements PropertiesObj
{
	public enum ContextType
	{
		FORM( "form" ),
		VIEW( "view" );
		
		private final String m_strValue;
		
		/**
		 * 
		 */
		private ContextType( String strValue )
		{
			m_strValue = strValue;
		}
		
		/**
		 * 
		 */
		public String getValue()
		{
			return m_strValue;
		}
	}
	
	private String m_html;
	private String m_markedUpHtml;		// html that has markup still in it.
	private GetterCallback<String> m_getterCallback;
	
	/**
	 * 
	 */
	public HtmlProperties()
	{
		m_html = null;
		m_markedUpHtml = null;
		m_getterCallback = null;
	}
	
	
	/**
	 * 
	 */
	public void copy( PropertiesObj props )
	{
		if ( props instanceof HtmlProperties )
		{
			HtmlProperties htmlProps;
			
			htmlProps = (HtmlProperties) props;

			setHtml( htmlProps.getHtml() );
			m_markedUpHtml = htmlProps.getMarkedUpHtml();
		}
	}
	

	/**
	 * Return the properties as a string that can be stored in the db.
	 */
	public String createConfigString()
	{
		String str;
		
		// The string should look like: "html,data=;"
		str = "html,data=";
		if ( m_html != null )
			str += ConfigData.encodeSeparators( m_html );
		
		str += ";";

		return str;
	}
	
	
	/**
	 * 
	 */
	public String getHtml()
	{
		return m_html;
	}
	
	/**
	 * 
	 */
	public String getMarkedUpHtml()
	{
		return m_markedUpHtml;
	}
	
	/**
	 * Issue an ajax request to parse the html and replace any markup with the appropriate html.
	 * For example, replace {{attachmentUrl: somename.png}} with a url that looks like:
	 * http://somehost/ssf/s/readFile.../somename.png
	 * The parameter contextType can be "view" or "form"
	 */
	public void replaceMarkup( String binderId, ContextType contextType, GetterCallback<String> callback )
	{
		m_getterCallback = callback;
		
		// Do we have html that has any markup in it?
		if ( m_markedUpHtml != null && m_markedUpHtml.length() > 0 && binderId != null && binderId.length() > 0 )
		{
			// Yes

			// Issue an ajax request to parse the html and replace any markup with the appropriate html.
			// For example, replace {{attachmentUrl: somename.png}} with a url that looks like:
			// http://somehost/ssf/s/readFile/.../somename.png.
			MarkupStringReplacementCmd cmd = new MarkupStringReplacementCmd( binderId, m_markedUpHtml, contextType.getValue() );
			GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
			{
				@Override
				public void onFailure( Throwable caught )
				{
					GwtClientHelper.handleGwtRPCFailure(
						caught,
						GwtTeaming.getMessages().rpcFailure_markupStringReplacement() );

					m_markedUpHtml = null;

					// Inform the callback that the rpc request failed.
					if ( m_getterCallback != null )
						m_getterCallback.returnValue( null);
				}// end onFailure()

				@Override
				public void onSuccess( VibeRpcResponse result )
				{
					StringRpcResponseData responseData = ((StringRpcResponseData) result.getResponseData());
					String newHtml = responseData.getStringValue();
					
					setHtml( newHtml );
					m_markedUpHtml = null;

					// Inform the callback that the rpc request finished.
					if ( m_getterCallback != null )
						m_getterCallback.returnValue( newHtml );
				}// end onSuccess()
				
			} );
		}
		else
			m_getterCallback.returnValue( null );
	}
	
	
	/**
	 * 
	 */
	public void setHtml( String html )
	{
		m_html = html;
	}
	
	/**
	 * Call this method with html that may contain markup such as {{attachmentUrl: somename.png}}
	 */
	public void setHtmlWithMarkup( String html )
	{
		m_markedUpHtml = html;
	}
}

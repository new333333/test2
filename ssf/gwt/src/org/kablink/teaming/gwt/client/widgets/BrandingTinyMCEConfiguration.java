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

package org.kablink.teaming.gwt.client.widgets;

import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.GetDocBaseUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * 
 */
public class BrandingTinyMCEConfiguration extends AbstractTinyMCEConfiguration
{
	
	/**
	 * 
	 */
	public BrandingTinyMCEConfiguration( String binderId )
	{
		super();
		
		m_binderId = binderId;
		m_rpcInProgress = false;

		// Create the callback that will be used when we issue an ajax call to get
		// the base url for the given binder.
		m_rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetGeneric(),
					m_binderId );
				m_rpcInProgress = false;
			}// end onFailure()
	
			/**
			 * 
			 * @param result
			 */
			public void onSuccess( VibeRpcResponse response )
			{
				String documentBaseUrl;
				StringRpcResponseData responseData;
				
				responseData = (StringRpcResponseData) response.getResponseData();
				documentBaseUrl = responseData.getStringValue();
				
				// Update the url that is used for the document base url when initializing the tinyMCE editor.
				setDocumentBaseUrl( documentBaseUrl );
				m_rpcInProgress = false;
			}// end onSuccess()
		};

		setContentCss();
		setLanguage();
		
		// Issue an ajax request to get the document base url for this binder.
		getDocumentBaseUrlFromServer();
		
	}// end BrandingTinyMCEConfiguration()
	
	
	/**
	 * Add the necessary language packs to the tinyMCE editor that are needed when editing branding.
	 */
	public void addLanguagePacks()
	{
		GwtTeamingMessages messages;
		
		messages = GwtTeaming.getMessages();
		
		// Add a language pack for the "add image dialog" plugin.
		{
			String imageSelectionsHtml;
			
			// Create the html that is used in the "add image" dialog to select from a list of attached files.
			{
				// Do we have a list of file attachments?
				if ( m_listOfFileAttachments != null && m_listOfFileAttachments.size() > 0 )
				{
					int i;
					
					// Yes
					imageSelectionsHtml = "<select name='srcUrl' id='srcUrl'>";
					
					for (i = 0; i < m_listOfFileAttachments.size(); ++i)
					{
						String fileName;
						String option;
						
						// Create an <option value="file name>filename</option>
						fileName = m_listOfFileAttachments.get( i );
						option = "<option value='" + fileName + "' >" + fileName + "</option>";
						imageSelectionsHtml += option;
					}
					
					imageSelectionsHtml += "</select>";
				}
				else
				{
					// No
					imageSelectionsHtml = "<select name='srcUrl' id='srcUrl'></select>";
				}
			}
			
			addAddImageDialogLanguagePack( getLanguage() + ".ss_addimage_dlg", messages, imageSelectionsHtml );
		}
		
		// Add a language pack for the "add image" plugin.
		addAddImageLanguagePack( getLanguage() + ".ss_addimage", messages );
		
		// Add a language pack for the "Insert a link to another Teaming page" plugin
		addInsertLinkToTeamingPageLanguagePack( getLanguage() + ".ss_wikilink", messages );
		
		// Add a language pack for the YouTube plugin.
		addYouTubeLanguagePack( getLanguage() + ".ss_youtube", messages );

		// Add a language pack for the plugin that shows/hides the 2nd row of controls in the tiny mce toolbar.
		addToolbarLanguagePack( getLanguage() + ".pdw", messages );
	}// end addLanguagePacks()

	
	/**
	 * Add a language package to the Tiny MCE editor for the "add image" plugin.
	 */
	protected native void addAddImageLanguagePack( String langPrefix, GwtTeamingMessages messages ) /*-{
		$wnd.tinyMCE.addI18n( langPrefix, {
			desc_no : messages.@org.kablink.teaming.gwt.client.GwtTeamingMessages::overQuota()()
			} );
	}-*/;
	

	/**
	 * Add a language package to the Tiny MCE editor for the "add image dialog" plugin.
	 */
	protected native void addAddImageDialogLanguagePack( String langPrefix, GwtTeamingMessages messages, String imageSelectionsHtml ) /*-{
		var ss_imageSelections_ss_htmleditor = imageSelectionsHtml;
		
		$wnd.tinyMCE.addI18n( langPrefix, {
			overQuota : ' ',
			srcFile : messages.@org.kablink.teaming.gwt.client.GwtTeamingMessages::srcFile()(),
			addFile : messages.@org.kablink.teaming.gwt.client.GwtTeamingMessages::addFile()(),
			addUrl : messages.@org.kablink.teaming.gwt.client.GwtTeamingMessages::addUrl()(),
			imageName : messages.@org.kablink.teaming.gwt.client.GwtTeamingMessages::imageName()(),
			imageSelectBox : ss_imageSelections_ss_htmleditor,
			missing_img : messages.@org.kablink.teaming.gwt.client.GwtTeamingMessages::missingImage()()
			} );
	}-*/;
	

	/**
	 * Add a language package to the Tiny MCE editor for the "Insert a link to another Teaming page" plugin.
	 */
	protected native void addInsertLinkToTeamingPageLanguagePack( String langPrefix, GwtTeamingMessages messages ) /*-{
		$wnd.tinyMCE.addI18n( langPrefix, {
			desc : messages.@org.kablink.teaming.gwt.client.GwtTeamingMessages::insertLinkToTeamingPage()()
			} );
	}-*/;
	

	/**
	 * Add a language package to the Tiny MCE editor for plugins that toggles the 2nd row of controls in the tiny mce toolbar.
	 */
	protected native void addToolbarLanguagePack( String langPrefix, GwtTeamingMessages messages ) /*-{
		$wnd.tinyMCE.addI18n( langPrefix, {
			description : messages.@org.kablink.teaming.gwt.client.GwtTeamingMessages::showHideToolbars()()
			} );
	}-*/;
	

	/**
	 * Add a language package to the Tiny MCE editor for the YouTube plugin.
	 */
	protected native void addYouTubeLanguagePack( String langPrefix, GwtTeamingMessages messages ) /*-{
		$wnd.tinyMCE.addI18n( langPrefix, {
			desc : messages.@org.kablink.teaming.gwt.client.GwtTeamingMessages::youTubeTitle()(),
			youTubeUrl : messages.@org.kablink.teaming.gwt.client.GwtTeamingMessages::youTubeUrl()(),
			dimensions : messages.@org.kablink.teaming.gwt.client.GwtTeamingMessages::youTubeDimensions()()
			} );
	}-*/;
	
	
	/**
	 * Issue an ajax request to get the base url for the binder we are working with.
	 * This url will be used for the document_base_url.
	 */
	protected void getDocumentBaseUrlFromServer()
	{
		GetDocBaseUrlCmd cmd;

		// Issue an ajax request to get the base url for the binder.
		m_rpcInProgress = true;
		cmd = new GetDocBaseUrlCmd( m_binderId );
		GwtClientHelper.executeCommand( cmd, m_rpcCallback );
	}
	
	
	/**
	 * Set the content css we should be using.
	 */
	public void setContentCss()
	{
		contentCss = GwtMainPage.m_requestInfo.getContentCss();
	}
	
	
	/**
	 * Set the language we are using.
	 */
	public void setLanguage()
	{
		language = GwtMainPage.m_requestInfo.getTinyMCELanguage();
	}
}// end BrandingTinyMCEConfiguration

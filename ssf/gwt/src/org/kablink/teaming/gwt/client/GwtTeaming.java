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

import org.kablink.teaming.gwt.client.admin.ExtensionsConfig;
import org.kablink.teaming.gwt.client.lpe.LandingPageEditor;
import org.kablink.teaming.gwt.client.service.GwtRpcService;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.widgets.MastHead;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GwtTeaming implements EntryPoint
{
	private static final GwtTeamingMessages		m_stringMessages = GWT.create( GwtTeamingMessages.class );
	private static final GwtTeamingImageBundle	m_imageBundle = GWT.create( GwtTeamingImageBundle.class );
	private static final GwtRpcServiceAsync		m_gwtRpcService = (GwtRpcServiceAsync) GWT.create( GwtRpcService.class );
	
	private static TextBox m_txtBox;
	private static Frame m_frame = null;
	
	/**
	 * Return the object that is used to retrieve images.
	 */
	public static GwtTeamingImageBundle getImageBundle()
	{
		return m_imageBundle;
	}// end getImageBundle()
	
	
	/**
	 * Return the object that is used to retrieve strings.
	 */
	public static GwtTeamingMessages getMessages()
	{
		return m_stringMessages;
	}// end GwtTeamingMessages()
	
	
	/**
	 * Return the object used to issue ajax requests.
	 */
	public static GwtRpcServiceAsync getRpcService()
	{
		return m_gwtRpcService;
	}// end getRpcService()
	
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad()
	{
		RootPanel	rootPanel;
		
		// Are we in the the Landing Page Editor?
		rootPanel = RootPanel.get( "gwtLandingPageEditorDiv" );
		if ( rootPanel != null )
		{
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
		/*
			if ( false )
			{
				com.smartgwt.client.widgets.menu.Menu menu;
				com.smartgwt.client.widgets.menu.MenuItem newItem;
				com.smartgwt.client.widgets.menu.MenuItem openItem;
				com.smartgwt.client.widgets.menu.MenuItem saveItem;
				com.smartgwt.client.widgets.menu.MenuButton menuBtn;
				
				menu = new com.smartgwt.client.widgets.menu.Menu();
				menu.setShowShadow( true );
				menu.setShadowDepth( 10 );
				
				newItem = new com.smartgwt.client.widgets.menu.MenuItem( "New" );
				openItem = new com.smartgwt.client.widgets.menu.MenuItem( "Open" );
				saveItem = new com.smartgwt.client.widgets.menu.MenuItem( "Save" );
				
				menu.setItems( newItem, openItem, saveItem );
				
				menuBtn = new com.smartgwt.client.widgets.menu.MenuButton( "File", menu );
				rootPanel.add( menuBtn );
			}
		*/
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  
			
			LandingPageEditor	lpEditor;
			
			// Yes
			// Create a Landing Page Editor and add it to the page.
			lpEditor = new LandingPageEditor();
			rootPanel.add( lpEditor );
			
			return;
		}

		// Are we in the the Extensions page?
		rootPanel = RootPanel.get( "gwtExtensionsConfigDiv" );
		if ( rootPanel != null )
		{
			ExtensionsConfig cfgExtension;
			
			// Yes
			// Create the Extensions ui and add it to the page.
			cfgExtension = new ExtensionsConfig();
			rootPanel.add( cfgExtension );
			
			return;
		}
		
		// Are we in the main page?
		rootPanel = RootPanel.get( "gwtMainPageDiv" );
		if ( rootPanel != null )
		{
			FlowPanel flowPanel;
			FlowPanel flowPanel2;
			MastHead mastHead;
			Button btn;
			ClickHandler clkHandler;
			Hidden hidden;
			
			// Has the main GWT page already been loaded.
			if ( isGwtMainPageLoaded() )
			{
				String contentPanelUrl;
				
				contentPanelUrl = getContentPanelUrl();
				
				Window.alert( "GwtMainPage.jsp is being loaded inside GwtMainPage.jsp " );
				passUrlToMainPage( contentPanelUrl );
				return;
			}
			
			flowPanel = new FlowPanel();
			
			// Add a hidden element that indicates that the main GWT page has already been loaded.
			hidden = new Hidden();
			hidden.setID( "gwtMainPageLoaded" );
			flowPanel.add( hidden );
			
			mastHead = new MastHead();
			flowPanel.add( mastHead );
			
			m_txtBox = new TextBox();
			m_txtBox.setVisibleLength( 125 );
			flowPanel.add( m_txtBox );
			
			clkHandler = new ClickHandler()
			{
				public void onClick( ClickEvent event )
				{
					String url;
					
					url = m_txtBox.getText();
					setContentPanelUrl( url );
					m_frame.setUrl( url );
				}
			};
			btn = new Button( "Test", clkHandler );
			flowPanel.add( btn );
			
			flowPanel2 = new FlowPanel();
			flowPanel.add( flowPanel2 );
			
			m_frame = new Frame();
			m_frame.setPixelSize( 700, 500 );
			m_frame.getElement().setId( "gwtContentPanel" );
			flowPanel2.add( m_frame );
			
			rootPanel.add( flowPanel );
			return;
		}
		
	}// end onModuleLoad()


	/**
	 * 
	 */
	public static native String getContentPanelUrl() /*-{
		return window.top.m_contentPanelUrl;
	}-*/;
	

	/**
	 * 
	 */
	public static native String setContentPanelUrl( String url ) /*-{
		window.top.m_contentPanelUrl = url;
	}-*/;
	

	/**
	 * This method will return true if the GWT Main page has already been loaded.  We want
	 * to prevent the GWT main page from being loaded into the Content Panel.  When the
	 * GWT main page loads it will create a hidden input with the id, 'gwtMainPageLoaded'.
	 */
	public static native boolean isGwtMainPageLoaded() /*-{
		var input;
		
		// Does the hidden input, "gwtMainPageLoaded", exist?
		input = window.top.document.getElementById( 'gwtMainPageLoaded' );
		if ( input != null )
			return true;
			
		return false;
	}-*/;
	

	/**
	 * 
	 */
	public static native void passUrlToMainPage( String url ) /*-{
		window.top.location.href = url;
	}-*/;
	
}// end GwtTeaming

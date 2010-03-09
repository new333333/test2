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

package org.kablink.teaming.gwt.client.widgets;


import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.NamedFrame;


/**
 * This widget will display the Teaming content for a given folder/workspace
 */
public class ContentControl extends Composite
{
	private NamedFrame m_frame;
	
	/**
	 * 
	 */
	public ContentControl()
	{
		FlowPanel mainPanel;

		mainPanel = new FlowPanel();
		mainPanel.addStyleName( "contentControl" );

		// Give the iframe a name so that view_workarea_navbar.jsp, doesn't set the url of the browser.
		m_frame = new NamedFrame( "gwtContentIframe" );
		m_frame.setPixelSize( 700, 500 );
		m_frame.getElement().setId( "contentControl" );
		m_frame.setUrl( "" );
		mainPanel.add( m_frame );
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}// end ContentControl()
	
	
	/**
	 * Set the width and height of this control.
	 */
	public void setDimensions( int width, int height )
	{
		m_frame.setPixelSize( width, height );
	}// end setDimensions()
	
	/**
	 * This method will set the url used by the iframe.
	 */
	public void setUrl( String url )
	{
		m_frame.setUrl( url );
	}// end setUrl()
}// end ContentControl

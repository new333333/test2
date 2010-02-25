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


import org.kablink.teaming.gwt.client.RequestInfo;
import org.kablink.teaming.gwt.client.GwtBrandingData;
import org.kablink.teaming.gwt.client.GwtTeaming;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;


/**
 * This widget will display the MastHead 
 */
public class MastHead extends Composite
{
	private RequestInfo m_requestInfo = null;
	private BrandingPanel m_level1BrandingPanel = null;
	private BrandingPanel m_level2BrandingPanel = null;
	
	/**
	 * This class displays branding, either level1(corporate) or level2(sub)
	 */
	public class BrandingPanel extends Composite
	{
		private String m_binderId = null;	// Id of the binder we are displaying branding for.
		private GwtBrandingData m_brandingData = null;	// Branding data for the given binder.
		
		/**
		 * 
		 */
		public BrandingPanel()
		{
			ImageResource imageResource;
			Image img;
			FlowPanel panel;
			
			panel = new FlowPanel();
			panel.addStyleName( "mastHeadGraphicPanel" );
	
			// Add an image to the panel.
			imageResource = GwtTeaming.getImageBundle().mastHeadNovellGraphic();
			img = new Image(imageResource);
			img.setWidth( "500" );
			img.setHeight( "75" );
			panel.add( img );
		
			// Create a BrandingData object that will hold the branding data for the given binder.
			m_brandingData = new GwtBrandingData();
			
			// All composites must call initWidget() in their constructors.
			initWidget( panel );
		}// end BrandingPanel()
		
		
		/**
		 * 
		 */
		public void setBinderId( String binderId )
		{
			m_binderId = binderId;
			
			// Get the branding data for the given binder.
			m_brandingData.setBinderId( binderId );
		}// end setBinderId()
	}// end Branding
	
	
	/**
	 * 
	 */
	public MastHead( RequestInfo requestInfo )
	{
		FlowPanel mainPanel;
		FlowPanel panel;

		m_requestInfo = requestInfo;
		
		mainPanel = new FlowPanel();
		mainPanel.addStyleName( "mastHead" );
		
		// Create the panel that will hold the level-1 or corporate branding
		m_level1BrandingPanel = new BrandingPanel();
		mainPanel.add( m_level1BrandingPanel );
		
		// Create the panel that will hold the level-2 or sub branding.
		m_level2BrandingPanel = new BrandingPanel();
		m_level2BrandingPanel.setBinderId( m_requestInfo.getBinderId() );
		mainPanel.add( m_level2BrandingPanel );
		
		// Create the panel that will hold the global actions such as "My workspace", "My Teams" etc
		{
			Label name;
			
			panel = new FlowPanel();
			panel.addStyleName( "mastHeadGlobalActionsPanel" );
			
			// Create a label that holds the logged-in user's name.
			name = new Label( "Jonathan Smithsonian" );
			name.addStyleName( "mastHeadUserName" );
			panel.add( name );
			
			mainPanel.add( panel );
		}
		
		// Create the panel that will hold the logo.
		{
			ImageResource imageResource;
			Image img;

			panel = new FlowPanel();
			panel.addStyleName( "mastHeadLogoPanel" );
			
			// Add an image to the panel.
			imageResource = GwtTeaming.getImageBundle().mastHeadNovellLogo();
			img = new Image(imageResource);
			panel.add( img );
		
			mainPanel.add( panel );
		}

		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}// end MastHead()
	
}// end MastHead

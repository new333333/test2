/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.ActionRequestor;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;


/**
 * This widget will display the MastHead 
 */
public class MastHead extends Composite
	implements ActionRequestor
{
	private BrandingPanel m_siteBrandingPanel = null;
	private BrandingPanel m_binderBrandingPanel = null;
	private RequestInfo m_requestInfo = null;
	private String m_mastheadBinderId = null;
	private FlowPanel m_mainMastheadPanel = null;
	
	
	
	/**
	 * 
	 */
	public MastHead( RequestInfo requestInfo )
	{
		BrandingPanel brandingPanel = null;
		
        m_requestInfo = requestInfo;
        m_mastheadBinderId = m_requestInfo.getBinderId();
		
		m_mainMastheadPanel = new FlowPanel();
		m_mainMastheadPanel.addStyleName( "mastHead" );
		
		// Create a branding panel that will display "site" branding if it exists.
		//!!!m_siteBrandingPanel = new BrandingPanel( requestInfo );
		//!!!m_mainMastheadPanel.add( m_siteBrandingPanel );
		
		// Create a branding panel that will display the binder branding.
		m_binderBrandingPanel = new BrandingPanel( requestInfo );
		m_mainMastheadPanel.add( m_binderBrandingPanel );
		
		// If the site branding panel exists and is visible, add the global actions to it otherwise add the
		// global actions to the binder branding panel.
		if ( m_siteBrandingPanel != null && m_siteBrandingPanel.isVisible() )
			brandingPanel = m_siteBrandingPanel;
		else if ( m_binderBrandingPanel != null && m_binderBrandingPanel.isVisible() )
			brandingPanel = m_binderBrandingPanel;
		
		// Add the global actions to the appropriate branding panel.
		if ( brandingPanel != null )
		{
			brandingPanel.addAdministrationAction();
			brandingPanel.addLogoutAction();
			brandingPanel.addHelpAction();
			
			// Add the user's name to the branding panel.
			brandingPanel.updateUserName( requestInfo.getUserName() );
		}
		
		// All composites must call initWidget() in their constructors.
		initWidget( m_mainMastheadPanel );

	}// end MastHead()


	/**
	 * Called to add an ActionHandler to this MastHead
	 * @param actionHandler
	 */
	public void addActionHandler( ActionHandler actionHandler )
	{
		// Pass the action handling to the BrandingPanel.
		if ( m_siteBrandingPanel != null )
			m_siteBrandingPanel.addActionHandler( actionHandler );
		
		if ( m_binderBrandingPanel != null )
			m_binderBrandingPanel.addActionHandler( actionHandler );
	}// end addActionHandler()
	
	
	/**
	 * Return the binder id we are working with.
	 */
	public String getBinderId()
	{
		return m_mastheadBinderId;
	}// end getBinderId()
	
	
	/**
	 * Return the branding data we are working with.
	 */
	public GwtBrandingData getBrandingData()
	{
		if ( m_binderBrandingPanel != null )
			return m_binderBrandingPanel.getBrandingData();
		
		return null;
	}// end GwtBrandingData()
	
	
	/**
	 * Refresh the masthead by issuing an ajax request to get the branding data . 
	 */
	public void refreshMasthead()
	{
		if ( m_binderBrandingPanel != null )
			m_binderBrandingPanel.refreshBrandingPanel();
	}// end refreshMasthead()
	
	
	/**
	 * Set the id of the binder the masthead is dealing with.
	 */
	public void setBinderId( String binderId )
	{
		// Did the binder id change?
		if ( m_mastheadBinderId == null || m_mastheadBinderId.equalsIgnoreCase( binderId ) == false )
		{
			// Yes
			m_mastheadBinderId = binderId;
			
			if ( m_binderBrandingPanel != null )
				m_binderBrandingPanel.setBinderId( binderId );
		}
	}// end setBinderId()

}// end MastHead

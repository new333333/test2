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


import org.kablink.teaming.gwt.client.widgets.ContentControl;
import org.kablink.teaming.gwt.client.widgets.FolderControl;
import org.kablink.teaming.gwt.client.widgets.MainMenuControl;
import org.kablink.teaming.gwt.client.widgets.MastHead;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;


/**
 * This widget will display the main Teaming page
 */
public class GwtMainPage extends Composite
{
	private MastHead m_mastHead;
	private MainMenuControl m_mainMenuCtrl;
	private FolderControl m_folderCtrl;
	private ContentControl m_contentCtrl;

	/**
	 * 
	 */
	public GwtMainPage()
	{
		FlowPanel mainPanel;
		FlowPanel panel;

		mainPanel = new FlowPanel();
		mainPanel.addStyleName( "mainTeamingPage" );
		
		// Add the MastHead to the page.
		m_mastHead = new MastHead();
		mainPanel.add( m_mastHead );
		
		// Add the main menu to the page.
		m_mainMenuCtrl = new MainMenuControl();
		mainPanel.add( m_mainMenuCtrl );
		
		// Create a panel to hold the folder control and the content control
		panel = new FlowPanel();
		panel.addStyleName( "mainContentPanel" );
		
		// Create the folder control.
		m_folderCtrl = new FolderControl();
		m_folderCtrl.addStyleName( "mainFolderControl" );
		panel.add( m_folderCtrl );
		
		// Create the content control.
		m_contentCtrl = new ContentControl();
		m_contentCtrl.addStyleName( "mainContentControl" );
		panel.add( m_contentCtrl );
		
		mainPanel.add( panel );
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}// end GwtMainPage()

}// end GwtMainPage

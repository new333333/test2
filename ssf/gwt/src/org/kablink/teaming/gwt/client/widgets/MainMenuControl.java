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


import org.kablink.teaming.gwt.client.GwtTeaming;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;


/**
 * This widget will display Teaming's main menu control.
 */
public class MainMenuControl extends Composite
{
	private Image m_slideLeftImg;
	private Image m_slideRightImg;
	private Image m_slideUpImg;
	private Image m_slideDownImg;
	private Image m_browseHierarchyImg;
	
	/**
	 * 
	 */
	public MainMenuControl()
	{
		FlowPanel mainPanel;
		FlowPanel panel;
		ImageResource imageResource;

		mainPanel = new FlowPanel();
		mainPanel.addStyleName( "mainMenuControl" );
		
		// Add the "slide left/right" actions.
		{
			panel = new FlowPanel();
			panel.addStyleName( "mainMenuNavControl" );
			panel.addStyleName( "mainMenuLeftRightNavControl" );
			
			//!!! Put these images in an anchor.
			
			// Add the slide-left image to the menu.
			imageResource = GwtTeaming.getImageBundle().slideLeft();
			m_slideLeftImg = new Image(imageResource);
			m_slideLeftImg.addStyleName( "paddingTop2px" );
			panel.add( m_slideLeftImg );
			
			// Add the slide-right image to the menu and hide it.
			imageResource = GwtTeaming.getImageBundle().slideRight();
			m_slideRightImg = new Image(imageResource);
			m_slideRightImg.addStyleName( "paddingTop2px" );
			m_slideRightImg.setVisible( false );
			panel.add( m_slideRightImg );
			
			mainPanel.add( panel );
		}

		// Add the "slide up/down" actions.
		{
			panel = new FlowPanel();
			panel.addStyleName( "mainMenuNavControl" );
			panel.addStyleName( "mainMenuUpDownNavControl" );
			
			//!!! Put these images in an anchor.
			
			// Add the slide-up image to the menu.
			imageResource = GwtTeaming.getImageBundle().slideUp();
			m_slideUpImg = new Image(imageResource);
			m_slideUpImg.addStyleName( "paddingTop2px" );
			panel.add( m_slideUpImg );
			
			// Add the slide-down image to the menu and hide it.
			imageResource = GwtTeaming.getImageBundle().slideDown();
			m_slideDownImg = new Image(imageResource);
			m_slideDownImg.addStyleName( "paddingTop2px" );
			m_slideDownImg.setVisible( false );
			panel.add( m_slideDownImg );
			
			mainPanel.add( panel );
		}

		// Add the "browse hierarchy" action.
		{
			panel = new FlowPanel();
			panel.addStyleName( "mainMenuNavControl" );
			panel.addStyleName( "mainMenuUpBrowseHierarchyControl" );
			
			//!!! Put this image in an anchor.
			
			// Add the browse hierarchy image to the menu.
			imageResource = GwtTeaming.getImageBundle().browseHierarchy();
			m_browseHierarchyImg = new Image(imageResource);
			m_browseHierarchyImg.addStyleName( "paddingTop2px" );
			panel.add( m_browseHierarchyImg );
			
			mainPanel.add( panel );
		}

		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}// end MainMenuControl()

}// end MainMenuControl

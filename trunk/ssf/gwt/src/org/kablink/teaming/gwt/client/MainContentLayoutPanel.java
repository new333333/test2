/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

import com.google.gwt.user.client.ui.Widget;
import org.kablink.teaming.gwt.client.util.ActivityStreamDataType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl;
import org.kablink.teaming.gwt.client.widgets.ContentControl;
import org.kablink.teaming.gwt.client.widgets.VibeDockLayoutPanel;
import org.kablink.teaming.gwt.client.widgets.VibeEntityViewPanel;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.ResizeComposite;

/**
 * This panel holds the content of the main Vibe page.  Currently this content panel
 * holds both the ActivityStreamCtrl and the ContentCtrl.  These both must exist but
 * only one is visible.  This panel can hold 1 additional widget.  This widget can be
 * any widget.  When this widget is visible, the ActivityStreamCtrl and the ContentCtrl
 * are hidden.
 * 
 * @author jwootton
 *
 */
public class MainContentLayoutPanel extends VibeDockLayoutPanel implements VibeEntityViewPanel
{
	private VibeFlowPanel m_contentFlowPanel = null;
	private ContentControl m_contentCtrl;
	private ActivityStreamCtrl m_activityStreamCtrl;
	private ResizeComposite m_miscWidget;

	/**
	 * 
	 */
	public MainContentLayoutPanel( ContentControl contentCtrl, ActivityStreamCtrl asCtrl )
	{
		super( Style.Unit.PX );

		m_contentCtrl = contentCtrl;
		m_activityStreamCtrl = asCtrl;
		m_miscWidget = null;
		
		m_contentFlowPanel = new VibeFlowPanel();
		m_contentFlowPanel.getElement().setId( "contentFlowPanel" );
		m_contentFlowPanel.addStyleName( "contentFlowPanel" );
		
		m_contentFlowPanel.add( contentCtrl );
		m_contentFlowPanel.add( asCtrl );
		
		add( m_contentFlowPanel );
	}

	/**
	 * Show the activity stream and hide any other controls 
	 */
	public void showActivityStream( ActivityStreamDataType ss )
	{
		if ( m_miscWidget != null )
			m_miscWidget.setVisible( false );
		m_contentCtrl.setVisible( false );
		m_activityStreamCtrl.show( ss );
	}
	
	public void showActivityStream()
	{
		showActivityStream( ActivityStreamDataType.OTHER );
	}
	
	/**
	 * Show the content control and hide any other controls.
	 */
	public void showContentControl()
	{
		// Do we have a widget that we should show instead of the content control?
		if ( m_miscWidget != null )
		{
			// Yes, show it instead of the content control.
			m_miscWidget.setVisible( true );
			m_contentCtrl.setVisible( false );
		}
		else
		{
			// No, show the content control
			m_contentCtrl.setVisible( true );
		}
			
		m_activityStreamCtrl.hide();
	}
	
	/**
	 * Show a widget other than m_contentCtrl and m_activityStreamCtrl
	 */
	public void showWidget( Widget composite )
	{
		// Do we have a widget to show?
		if ( composite != null )
		{
			// Yes, hide the content control and the activity stream control.
			m_contentCtrl.setVisible( false );
			m_activityStreamCtrl.hide();
		}

		// Remove the previous widget(but not m_contentCtrl or m_activityStreamCtrl)
		if ( m_miscWidget != null )
			m_contentFlowPanel.remove( m_miscWidget );

		m_miscWidget = (ResizeComposite) composite;

		// Add the new widget
		if ( composite != null )
			m_contentFlowPanel.add( composite );
	}
}

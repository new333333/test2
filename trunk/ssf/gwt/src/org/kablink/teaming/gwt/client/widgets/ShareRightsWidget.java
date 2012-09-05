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

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.SetShareRightsEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.util.GwtShareItem;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * This widget is used to display the rights for a given share and allow the user
 * to change the rights.
 */
public class ShareRightsWidget extends Composite
	implements ClickHandler, SetShareRightsEvent.Handler
{
	private GwtShareItem m_shareInfo;
	private InlineLabel m_rightsLabel;
	private Image m_rightsImg;

	private List<HandlerRegistration> m_registeredEventHandlers;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[]
    {
		// Search events.
		TeamingEvents.SET_SHARE_RIGHTS,
	};
	
	private static ShareRightsPopupMenu m_shareRightsPopupMenu;
	
	
	/**
	 * 
	 */
	public ShareRightsWidget( GwtShareItem shareInfo )
	{
		ImageResource imageResource;
		
		m_shareInfo = shareInfo;

		m_rightsLabel = new InlineLabel( shareInfo.getShareRightsAsString() );
		m_rightsLabel.addStyleName( "shareThisDlg_RightsLabel" );
		m_rightsLabel.addClickHandler( this );
		
		imageResource = GwtTeaming.getImageBundle().activityStreamActions1();
		m_rightsImg = new Image( imageResource );
		m_rightsImg.getElement().setAttribute( "align", "absmiddle" );
		m_rightsLabel.getElement().appendChild( m_rightsImg.getElement() );

		registerEvents();
		
		initWidget( m_rightsLabel );
	}

	/**
	 * This method gets called when the user clicks on the current rights a user has.
	 * We will pop up a menu to let the user change the rights.
	 */
	private void handleClickOnShareRights()
	{
		// Have we created the popup menu yet?
		if ( m_shareRightsPopupMenu == null )
		{
			// No
			// Create a pop-up menu that can be used to change the share rights.
			m_shareRightsPopupMenu = new ShareRightsPopupMenu( true, true );
		}
		
		m_shareRightsPopupMenu.setShareInfo( m_shareInfo );
		m_shareRightsPopupMenu.showRelativeToTarget( this );
		
		// If we are dealing with a folder, show the "contributor" menu item.
		if ( m_shareInfo.getEntityId().isBinder() )
			m_shareRightsPopupMenu.showContributorMenuItem();
		else
			m_shareRightsPopupMenu.hideContributorMenuItem();
	}
	
	/**
	 * Called when the dialog is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach()
	{
		// Let the widget attach and then register our event handlers.
		super.onAttach();
		registerEvents();
	}
	
	/**
	 * 
	 */
	@Override
	public void onClick( ClickEvent event )
	{
		Scheduler.ScheduledCommand cmd;
		
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute() 
			{
				handleClickOnShareRights();
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}

	/**
	 * Called when this widget is detached.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach()
	{
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
	}
	
	/**
	 * This gets called when the user select one of the menu items in the
	 * ShareRightsPopupMenu.
	 */
	@Override
	public void onSetShareRights( SetShareRightsEvent event )
	{
		GwtShareItem targetShareInfo;
		
		targetShareInfo = event.getShareInfo();
		if ( m_shareInfo != null && targetShareInfo != null )
		{
			// Was this event meant for us?
			if ( m_shareInfo.equals( targetShareInfo ) )
			{
				// Yes
				m_shareInfo.setShareRights( event.getShareRights() );
				m_shareInfo.setIsDirty( true );
		
				updateRightsLabel();
			}
		}
	}
	
	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents()
	{
		// If we having allocated a list to track events we've
		// registered yet...
		if ( null == m_registeredEventHandlers )
		{
			// ...allocate one now.
			m_registeredEventHandlers = new ArrayList<HandlerRegistration>();
		}

		// If the list of registered events is empty...
		if ( m_registeredEventHandlers.isEmpty() )
		{
			// ...register the events.
			EventHelper.registerEventHandlers(
										GwtTeaming.getEventBus(),
										REGISTERED_EVENTS,
										this,
										m_registeredEventHandlers );
		}
	}

	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	public void unregisterEvents()
	{
		// If we have a non-empty list of registered events...
		if ( ( null != m_registeredEventHandlers ) && ( ! ( m_registeredEventHandlers.isEmpty() ) ) )
		{
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers( m_registeredEventHandlers );
		}
	}
	
	/**
	 * 
	 */
	private void updateRightsLabel()
	{
		m_rightsLabel.setText( m_shareInfo.getShareRightsAsString() );
		m_rightsLabel.getElement().appendChild( m_rightsImg.getElement() );
	}
}


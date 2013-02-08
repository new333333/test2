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

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.util.GwtRecipientType;
import org.kablink.teaming.gwt.client.util.GwtShareItem;
import org.kablink.teaming.gwt.client.util.ShareRights;
import org.kablink.teaming.gwt.client.util.ShareRights.AccessRights;
import org.kablink.teaming.gwt.client.widgets.EditShareRightsDlg.EditShareRightsDlgClient;

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
	implements ClickHandler
{
	private GwtShareItem m_shareInfo;
	private ShareRights m_highestRightsPossible;
	private InlineLabel m_rightsLabel;
	private Image m_rightsImg;
	private EditSuccessfulHandler m_editShareRightsHandler;
	private List<HandlerRegistration> m_registeredEventHandlers;

	private static EditShareRightsDlg m_editShareRightsDlg;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private static TeamingEvents[] m_registeredEvents = new TeamingEvents[] 
	{
	};
	
	/**
	 * 
	 */
	public ShareRightsWidget(
		GwtShareItem shareInfo,
		ShareRights highestRightsPossible )
	{
		ImageResource imageResource;
		
		m_shareInfo = shareInfo;
		m_highestRightsPossible = highestRightsPossible;

		// Are we dealing with an external user?
		if ( m_shareInfo.getRecipientType() == GwtRecipientType.EXTERNAL_USER )
		{
			// Yes, don't let the user give the external user rights to share.
			m_highestRightsPossible = new ShareRights();
			if ( highestRightsPossible != null )
				m_highestRightsPossible.setAccessRights( highestRightsPossible.getAccessRights() );
			m_highestRightsPossible.setCanShareForward( false );
			m_highestRightsPossible.setCanShareWithExternalUsers( false );
			m_highestRightsPossible.setCanShareWithInternalUsers( false );
			m_highestRightsPossible.setCanShareWithPublic( false );
		}
		// Are we dealing with the "Public"?
		else if ( m_shareInfo.getRecipientType() == GwtRecipientType.PUBLIC_TYPE )
		{
			// Yes, the public can only have "Viewer" rights.
			m_highestRightsPossible = new ShareRights();
			m_highestRightsPossible.setAccessRights( AccessRights.VIEWER );
			m_highestRightsPossible.setCanShareForward( false );
			m_highestRightsPossible.setCanShareWithExternalUsers( false );
			m_highestRightsPossible.setCanShareWithInternalUsers( false );
			m_highestRightsPossible.setCanShareWithPublic( false );
		}

		m_rightsLabel = new InlineLabel( shareInfo.getShareRightsAsString() );
		m_rightsLabel.addStyleName( "shareThisDlg_RightsLabel" );
		m_rightsLabel.addClickHandler( this );
		
		imageResource = GwtTeaming.getImageBundle().activityStreamActions1();
		m_rightsImg = new Image( imageResource );
		m_rightsImg.getElement().setAttribute( "align", "absmiddle" );
		m_rightsLabel.getElement().appendChild( m_rightsImg.getElement() );

		initWidget( m_rightsLabel );
	}

	/**
	 * This method gets called when the user clicks on the current rights a user has.
	 * We will pop up a dialog to let the user change the rights.
	 */
	private void invokeEditShareRightsDlg()
	{
		if ( m_editShareRightsDlg != null )
		{
			ArrayList<GwtShareItem> listOfShareItems;
			
			if ( m_editShareRightsHandler == null )
			{
				m_editShareRightsHandler = new EditSuccessfulHandler()
				{
					@Override
					public boolean editSuccessful( Object obj )
					{
						if ( obj instanceof Boolean )
						{
							Boolean retValue;
							
							// Did the "Edit Share Rights" dialog successfully update
							// our GwtShareItem.
							retValue = (Boolean) obj;
							if ( retValue == true )
								updateRightsLabel();
						}

						return true;
					}
				};
			}
			
			// Invoke the "edit share rights" dialog.
			listOfShareItems = new ArrayList<GwtShareItem>();
			listOfShareItems.add( m_shareInfo );
			m_editShareRightsDlg.init( listOfShareItems, m_highestRightsPossible, m_editShareRightsHandler );
			m_editShareRightsDlg.showRelativeToTarget( m_rightsLabel );
		}
		else
		{
			EditShareRightsDlg.createAsync( true, true, new EditShareRightsDlgClient()
			{
				@Override
				public void onUnavailable() 
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( EditShareRightsDlg esrDlg )
				{
					m_editShareRightsDlg = esrDlg;
					invokeEditShareRightsDlg();
				}
			} );
			
		}
	}
	
	/**
	 * Called when the widget is attached.
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
	 * Called when the widget is detached.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach()
	{
		// Let the widget detach and then unregister our event handlers.
		super.onDetach();
		unregisterEvents();
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
				invokeEditShareRightsDlg();
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}

	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents()
	{
		// If we haven't allocated a list to track events we've registered yet...
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
											m_registeredEvents,
											this,
											m_registeredEventHandlers );
		}
	}

	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents()
	{
		// If we have a non-empty list of registered events...
		if ( null != m_registeredEventHandlers && !m_registeredEventHandlers.isEmpty() )
		{
			// ...unregister them.  (Note that this will also empty the list.)
			EventHelper.unregisterEventHandlers( m_registeredEventHandlers );
		}
	}

	/**
	 * 
	 */
	public void updateRightsLabel()
	{
		m_rightsLabel.setText( m_shareInfo.getShareRightsAsString() );
		m_rightsLabel.getElement().appendChild( m_rightsImg.getElement() );
	}
}


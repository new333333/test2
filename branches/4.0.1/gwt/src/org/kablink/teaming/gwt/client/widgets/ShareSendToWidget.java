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


import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuItem;
import org.kablink.teaming.gwt.client.menu.PopupMenu;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * This widget is used to display the option of who will receive a share notification
 */
public class ShareSendToWidget extends Composite
	implements ClickHandler
{
	private InlineLabel m_sendToLabel;
	private Image m_img;
	private SendToValue m_sendToValue;

	private static ShareSendToPopupMenu m_shareSendToPopupMenu;
	
	/**
	 * 
	 */
	public enum SendToValue implements IsSerializable
	{
		ALL_RECIPIENTS,				// Send to all recipients
		ONLY_NEW_RECIPIENTS,		// Send to only newly added recipients
		ONLY_MODIFIED_RECIPIENTS,	// Send to only recipients that have had their share rights modified
		NO_ONE,						// Do not send to anyone
		UNKNOWN
	}
	
	
	/**
	 * This popup menu is used to display a pop-up menu that lists all of the possible
	 * values for "send to"
	 */
	public class ShareSendToPopupMenu extends PopupMenu
	{
		private VibeMenuItem m_allRecipientsMenuItem;
		private VibeMenuItem m_newRecipientsMenuItem;
		private VibeMenuItem m_modifiedRecipientsMenuItem;
		private VibeMenuItem m_noOneMenuItem;

		/**
		 * 
		 */
		public ShareSendToPopupMenu( boolean autoHide, boolean modal )
		{
			super( autoHide, modal, true );
			
			Command cmd;

			// Add the "All recipients" menu item.
			cmd = new Command()
			{
				@Override
				public void execute()
				{
					m_sendToValue = SendToValue.ALL_RECIPIENTS;
					updateSendToLabel();
				};
			};
			m_allRecipientsMenuItem = addMenuItem(
												cmd,
												null,
												GwtTeaming.getMessages().shareSendToWidget_AllRecipients() );
			
			// Add the "New recipients" menu item.
			cmd = new Command()
			{
				@Override
				public void execute()
				{
					m_sendToValue = SendToValue.ONLY_NEW_RECIPIENTS;
					updateSendToLabel();
				};
			};
			m_newRecipientsMenuItem = addMenuItem(
												cmd,
												null,
												GwtTeaming.getMessages().shareSendToWidget_OnlyNewRecipients() );

			// Add the "Modified recipients" menu item.
			cmd = new Command()
			{
				@Override
				public void execute()
				{
					m_sendToValue = SendToValue.ONLY_MODIFIED_RECIPIENTS;
					updateSendToLabel();
				};
			};
			m_modifiedRecipientsMenuItem = addMenuItem(
													cmd,
													null,
													GwtTeaming.getMessages().shareSendToWidget_OnlyModifiedRecipients() );

			// Add the "Do not notify anyone" menu item.
			cmd = new Command()
			{
				@Override
				public void execute()
				{
					m_sendToValue = SendToValue.NO_ONE;
					updateSendToLabel();
				};
			};
			m_noOneMenuItem = addMenuItem(
										cmd,
										null,
										GwtTeaming.getMessages().shareSendToWidget_NoOne() );
		}
		
		/**
		 * Check the appropriate menu item based on the given "send to" setting.
		 */
		public void updateMenu( SendToValue sendToValue )
		{
			// Uncheck all of the menu items.
			setMenuItemCheckedState( m_allRecipientsMenuItem, false );
			setMenuItemCheckedState( m_newRecipientsMenuItem, false );
			setMenuItemCheckedState( m_modifiedRecipientsMenuItem, false );
			setMenuItemCheckedState( m_noOneMenuItem, false );
			
			// Check the appropriate menu item.
			switch ( sendToValue )
			{
			case ALL_RECIPIENTS:
				setMenuItemCheckedState( m_allRecipientsMenuItem, true );
				break;
				
			case ONLY_MODIFIED_RECIPIENTS:
				setMenuItemCheckedState( m_modifiedRecipientsMenuItem, true );
				break;
				
			case ONLY_NEW_RECIPIENTS:
				setMenuItemCheckedState( m_newRecipientsMenuItem, true );
				break;
			
			case NO_ONE:
				setMenuItemCheckedState( m_noOneMenuItem, true );
				break;
				
			case UNKNOWN:
			default:
				break;
			}
		}
	}

	
	
	/**
	 * 
	 */
	public ShareSendToWidget()
	{
		ImageResource imageResource;
		
		m_sendToValue = SendToValue.ALL_RECIPIENTS;
		
		m_sendToLabel = new InlineLabel( "" );
		m_sendToLabel.addStyleName( "shareSendToWidget_mainLabel" );
		m_sendToLabel.addClickHandler( this );
		
		imageResource = GwtTeaming.getImageBundle().activityStreamActions1();
		m_img = new Image( imageResource );
		m_img.getElement().setAttribute( "align", "absmiddle" );
		m_sendToLabel.getElement().appendChild( m_img.getElement() );

		
		initWidget( m_sendToLabel );
	}

	/**
	 * 
	 */
	public SendToValue getSendToValue()
	{
		return m_sendToValue;
	}
	
	/**
	 * 
	 */
	private String getSendToValueAsString( SendToValue sendToValue )
	{
		switch ( sendToValue )
		{
		case ALL_RECIPIENTS:
			return GwtTeaming.getMessages().shareSendToWidget_AllRecipients();
			
		case ONLY_MODIFIED_RECIPIENTS:
			return GwtTeaming.getMessages().shareSendToWidget_OnlyModifiedRecipients();
		
		case ONLY_NEW_RECIPIENTS:
			return GwtTeaming.getMessages().shareSendToWidget_OnlyNewRecipients();
		
		case NO_ONE:
			return GwtTeaming.getMessages().shareSendToWidget_NoOne();
			
		case UNKNOWN:
		default:
			return GwtTeaming.getMessages().shareSendToWidget_Unknown();
		}
	}
	
	/**
	 * This method gets called when the user clicks on the current "send to" value.
	 * We will pop up a menu to let the user change the "send to" value.
	 */
	private void handleClickOnSendTo()
	{
		// Have we created the popup menu yet?
		if ( m_shareSendToPopupMenu == null )
		{
			// No
			// Create a pop-up menu that can be used to change the share rights.
			m_shareSendToPopupMenu = new ShareSendToPopupMenu( true, true );
		}
		
		m_shareSendToPopupMenu.updateMenu( m_sendToValue );
		m_shareSendToPopupMenu.showRelativeToTarget( this );
	}
	
	/**
	 * 
	 */
	public void init( SendToValue sendToValue )
	{
		m_sendToValue = sendToValue;
		updateSendToLabel();
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
				handleClickOnSendTo();
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}

	/**
	 * 
	 */
	private void updateSendToLabel()
	{
		String text;
		
		text = getSendToValueAsString( m_sendToValue ); 
		m_sendToLabel.setText( text );
		m_sendToLabel.getElement().appendChild( m_img.getElement() );
	}
}


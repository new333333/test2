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

package org.kablink.teaming.gwt.client.widgets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.SearchFindResultsEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.GwtGroup;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtSearchCriteria.SearchType;
import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtShareEntryResults;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.mainmenu.TeamInfo;
import org.kablink.teaming.gwt.client.rpc.shared.GetEntryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetMyTeamsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetMyTeamsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetSharingInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.rpc.shared.ShareEntryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ShareEntryResultsRpcResponseData;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.GwtRecipientType;
import org.kablink.teaming.gwt.client.util.GwtShareItem;
import org.kablink.teaming.gwt.client.util.GwtShareItemMember;
import org.kablink.teaming.gwt.client.util.GwtSharingInfo;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue.ShareExpirationType;
import org.kablink.teaming.gwt.client.util.ShareRights;
import org.kablink.teaming.gwt.client.widgets.FindCtrl;
import org.kablink.teaming.gwt.client.widgets.FindCtrl.FindCtrlClient;
import org.kablink.teaming.gwt.client.widgets.ShareExpirationDlg.ShareExpirationDlgClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;


/**
 * This class is used to present a UI the user can use to share an item with
 * users, groups and teams.
 * 
 * @author jwootton
 */
public class ShareThisDlg extends DlgBox
	implements EditSuccessfulHandler, EditCanceledHandler,
	// Event handlers implemented by this class.
		SearchFindResultsEvent.Handler
{
	private FlowPanel m_headerPanel;
	private Image m_headerImg;
	private Label m_headerNameLabel;
	private Label m_headerPathLabel;
	private TextArea m_msgTextArea;
	private RadioButton m_viewRB;
	private RadioButton m_contributorRB;
	private RadioButton m_ownerRB;
	private FindCtrl m_findCtrl;
	private InlineLabel m_expiresLabel;
	private FlowPanel m_mainPanel;
	private ImageResource m_deleteImgR;
	private Image m_defaultExpirationImg;
	private FlexTable m_shareTable;
	private FlowPanel m_shareWithTeamsPanel;
	private FlowPanel m_myTeamsPanel;
	private FlowPanel m_shareTablePanel;
	private FlexCellFormatter m_shareCellFormatter;
	private HTMLTable.RowFormatter m_shareRowFormatter;
	private List<EntityId> m_entityIds;
	private GwtSharingInfo m_sharingInfo;		// Holds all of the sharing info for the entities we are working with.
	private List<HandlerRegistration> m_registeredEventHandlers;
	private AsyncCallback<VibeRpcResponse> m_readTeamsCallback;
	private AsyncCallback<VibeRpcResponse> m_shareEntryCallback;
	private AsyncCallback<VibeRpcResponse> m_getSharingInfoCallback;
	private UIObject m_target;
	private ShareExpirationValue m_defaultShareExpirationValue;
	private ShareExpirationDlg m_shareExpirationDlg;
	private EditSuccessfulHandler m_editDefaultExpirationHandler;

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[]
    {
		// Search events.
		TeamingEvents.SEARCH_FIND_RESULTS,
	};
	
	/**
	 * This class is a checkbox with a TeamInfo object associated with it.
	 */
	public class TeamCheckBox extends Composite
	{
		private TeamInfo m_teamInfo;
		private CheckBox m_checkbox;
		
		/**
		 * 
		 */
		public TeamCheckBox( TeamInfo teamInfo, String label )
		{
			FlowPanel panel;
			
			panel = new FlowPanel();
			m_checkbox = new CheckBox( label );
			m_checkbox.addStyleName( "fontSize75em" );
			panel.add( m_checkbox );
			
			m_teamInfo = teamInfo;
			
			initWidget( panel );
		}
		
		/**
		 * 
		 */
		public TeamInfo getTeamInfo()
		{
			return m_teamInfo;
		}
		
		/**
		 * 
		 */
		public Boolean getValue()
		{
			return m_checkbox.getValue();
		}
	}

	/**
	 * This widget is used to display a recipient's name.  If the recipient is a group
	 * then the user can click on the name and see the members of the group.
	 */
	private class RecipientNameWidget extends Composite
		implements ClickHandler, MouseOverHandler, MouseOutHandler
	{
		private GwtShareItemMember m_shareItemMember;
		private InlineLabel m_nameLabel;
		private GroupMembershipPopup m_groupMembershipPopup;
		
		/**
		 * 
		 */
		public RecipientNameWidget( GwtShareItemMember shareItemMember )
		{
			FlowPanel panel;
			
			m_shareItemMember = shareItemMember;
			
			panel = new FlowPanel();
			
			m_nameLabel = new InlineLabel( shareItemMember.getRecipientName() );
			panel.add( m_nameLabel );
			
			// If we are dealing with a group, let the user click on the group.
			if ( shareItemMember.getRecipientType() == GwtRecipientType.GROUP )
			{
				m_nameLabel.addClickHandler( this );
				m_nameLabel.addMouseOverHandler( this );
				m_nameLabel.addMouseOutHandler( this );
			}
			
			// All composites must call initWidget() in their constructors.
			initWidget( panel );
		}
		
		/**
		 * Close the group membership popup if it is open.
		 */
		public void closePopups()
		{
			if ( m_groupMembershipPopup != null )
				m_groupMembershipPopup.closePopups();
		}
		
		/**
		 * 
		 */
		public GwtShareItemMember getShareItemMember()
		{
			return m_shareItemMember;
		}

		/**
		 * This gets called when the user clicks on the recipient's name.  This will only
		 * be called if the recipient is a group.
		 */
		@Override
		public void onClick( ClickEvent event )
		{
			// Create a popup that will display the membership of this group.
			if ( m_groupMembershipPopup == null )
			{
				m_groupMembershipPopup = new GroupMembershipPopup(
															false,
															false,
															m_shareItemMember.getRecipientName(),
															m_shareItemMember.getRecipientId().toString() );
			}
			
			m_groupMembershipPopup.setPopupPosition( getAbsoluteLeft(), getAbsoluteTop() );
			m_groupMembershipPopup.show();
		}
		
		/**
		 * Remove the mouse-over style from the name. 
		 */
		@Override
		public void onMouseOut( MouseOutEvent event )
		{
			m_nameLabel.removeStyleName( "shareThisDlgNameHover" );
		}

		
		/**
		 * Add the mouse-over style to the name.
		 */
		@Override
		public void onMouseOver( MouseOverEvent event )
		{
			m_nameLabel.addStyleName( "shareThisDlgNameHover" );
		}
	}
	
	/**
	 * This widget is used to display the expiration for a given share and allow the user
	 * to change the expiration.
	 */
	private class ShareExpirationWidget extends Composite
		implements ClickHandler
	{
		private GwtShareItemMember m_shareItemMember;
		private InlineLabel m_expiresLabel;
		private Image m_img;
		private EditSuccessfulHandler m_editShareExpirationHandler;
		
		/**
		 * 
		 */
		public ShareExpirationWidget( GwtShareItemMember shareItemMember )
		{
			ImageResource imageResource;
			
			m_shareItemMember = shareItemMember;

			m_expiresLabel = new InlineLabel();
			m_expiresLabel.addStyleName( "shareThis_ExpiresLabel" );
			m_expiresLabel.addClickHandler( this );
			
			imageResource = GwtTeaming.getImageBundle().activityStreamActions1();
			m_img = new Image( imageResource );
			m_img.getElement().setAttribute( "align", "absmiddle" );
			
			updateExpirationLabel();
			
			initWidget( m_expiresLabel );
		}
		
		/**
		 * Invoke the "Share expiration" dialog
		 */
		private void editShareExpiration()
		{
			if ( m_shareExpirationDlg != null )
			{
				if ( m_editShareExpirationHandler == null )
				{
					m_editShareExpirationHandler = new EditSuccessfulHandler()
					{
						@Override
						public boolean editSuccessful( Object obj )
						{
							if ( obj instanceof ShareExpirationValue )
							{
								ShareExpirationValue expirationValue;
								
								expirationValue = (ShareExpirationValue) obj;
								m_shareItemMember.setShareExpirationValue( expirationValue );
								m_shareItemMember.setIsDirty( true );
								
								updateExpirationLabel();
							}
							
							return true;
						}
					};
				}
				
				// Invoke the "share expiration" dialog.
				m_shareExpirationDlg.init( m_shareItemMember.getShareExpirationValue(), m_editShareExpirationHandler );
				m_shareExpirationDlg.showRelativeToTarget( m_expiresLabel );
			}
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
					if ( m_shareExpirationDlg == null )
					{
						ShareExpirationDlg.createAsync( true, true, new ShareExpirationDlgClient()
						{
							@Override
							public void onUnavailable() 
							{
								// Nothing to do.  Error handled in asynchronous provider.
							}
							
							@Override
							public void onSuccess( ShareExpirationDlg seDlg )
							{
								m_shareExpirationDlg = seDlg;
								editShareExpiration();
							}
						} );
					}
					else
						editShareExpiration();
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
		
		/**
		 * Update the text that shows the value of the share expiration
		 */
		private void updateExpirationLabel()
		{
			if ( m_shareItemMember != null )
			{
				m_expiresLabel.setText( m_shareItemMember.getShareExpirationValueAsString() );
				m_expiresLabel.getElement().appendChild( m_img.getElement() );
			}
		}
	}
	
	/**
	 * This widget is used to remove a share from the list of shares
	 */
	private class RemoveShareWidget extends Composite
		implements ClickHandler
	{
		private GwtShareItemMember m_shareItemMember;
		
		/**
		 * 
		 */
		public RemoveShareWidget( GwtShareItemMember shareItemMember )
		{
			FlowPanel panel;
			Image delImg;
			
			m_shareItemMember = shareItemMember;
			
			panel = new FlowPanel();
			
			delImg = new Image( m_deleteImgR );
			delImg.addStyleName( "cursorPointer" );
			delImg.getElement().setAttribute( "title", GwtTeaming.getMessages().removeShareHint() );
			delImg.addClickHandler( this );
			
			panel.add( delImg );
			
			// All composites must call initWidget() in their constructors.
			initWidget( panel );
		}
		
		/**
		 * 
		 */
		public GwtShareItemMember getShareItemMember()
		{
			return m_shareItemMember;
		}

		/**
		 * This gets called when the user clicks on the remove share image.
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
					removeShare( m_shareItemMember );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
	}
	

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ShareThisDlg()
	{
		// Initialize the superclass.
		super( false, true );
		
		// Create the dialog's content
		createAllDlgContent(
			"",		// // No caption yet.  It's set appropriately when the dialog runs.
			this,	// EditSuccessfulHandler
			this,	// EditCanceledHandler
			null );
	}

	/**
	 * Add the "This item has not been shared" text to the table
	 * that holds the list of shares.
	 */
	private void addNotSharedMessage()
	{
		int row;
		
		row = 1;
		m_shareCellFormatter.setColSpan( row, 0, 5 );
		m_shareCellFormatter.setWordWrap( row, 0, false );
		m_shareCellFormatter.addStyleName( row, 0, "oltBorderLeft" );
		m_shareCellFormatter.addStyleName( row, 0, "oltBorderRight" );
		m_shareCellFormatter.addStyleName( row, 0, "oltContentPadding" );
		m_shareCellFormatter.addStyleName( row, 0, "oltLastRowBorderBottom" );

		m_shareTable.setText( row, 0, GwtTeaming.getMessages().noShareRecipients() );
	}
	
	
	/**
	 * Add the given share to the end of the table that holds the list of shares
	 */
	private void addShare( GwtShareItemMember shareItemMember, boolean highlight )
	{
		String type;
		int row;
		RemoveShareWidget removeWidget;
		RecipientNameWidget recipientNameWidget;
		
		row = m_shareTable.getRowCount();
		
		// Do we have any shares in the table?
		if ( row == 2 )
		{
			String text;
			
			// Maybe
			// The first row might be the message, "This item has not been shared"
			// Get the text from the first row.
			text = m_shareTable.getText( 1, 0 );
			
			// Does the first row contain a message?
			if ( text != null && text.equalsIgnoreCase( GwtTeaming.getMessages().noShareRecipients() ) )
			{
				// Yes
				m_shareTable.removeRow( 1 );
			}
		}
		
		// Remove any highlight that may be on the first row.
		unhighlightRecipient( 1 );
		
		// Add the share as the first share in the table.
		row = 1;
		m_shareTable.insertRow( row );
		
		// Should we highlight the row?
		if ( highlight )
		{
			// Yes
			highlightRecipient( row );
		}
		
		// Add the recipient name in the first column.
		m_shareCellFormatter.setColSpan( row, 0, 1 );
		m_shareCellFormatter.setWordWrap( row, 0, false );
		recipientNameWidget = new RecipientNameWidget( shareItemMember );
		m_shareTable.setWidget( row, 0,  recipientNameWidget );

		// Add the recipient type in the second column.
		m_shareCellFormatter.setWordWrap( row, 1, false );
		type = shareItemMember.getRecipientTypeAsString();
		m_shareTable.setText( row, 1, type );
		
		// Add the share rights in the 3rd column
		{
			ShareRightsWidget accessWidget;
			
			accessWidget = new ShareRightsWidget( shareItemMember );
			m_shareTable.setWidget( row, 2, accessWidget );
		}
		
		// Add the expires values in the 4th column
		{
			ShareExpirationWidget expirationWidget;
			
			expirationWidget = new ShareExpirationWidget( shareItemMember );
			m_shareTable.setWidget( row, 3, expirationWidget );
		}

		// Add the "remove share" widget to the 5th column.
		{
			removeWidget = new RemoveShareWidget( shareItemMember );
			m_shareTable.setWidget( row, 4, removeWidget );
		}

		// Add the necessary styles to the cells in the row.
		m_shareCellFormatter.addStyleName( row, 0, "oltBorderLeft" );
		m_shareCellFormatter.addStyleName( row, 4, "oltBorderRight" );
		m_shareCellFormatter.addStyleName( row, 0, "oltContentBorderBottom" );
		m_shareCellFormatter.addStyleName( row, 1, "oltContentBorderBottom" );
		m_shareCellFormatter.addStyleName( row, 2, "oltContentBorderBottom" );
		m_shareCellFormatter.addStyleName( row, 3, "oltContentBorderBottom" );
		m_shareCellFormatter.addStyleName( row, 4, "oltContentBorderBottom" );
		m_shareCellFormatter.addStyleName( row, 0, "oltContentPadding" );
		m_shareCellFormatter.addStyleName( row, 1, "oltContentPadding" );
		m_shareCellFormatter.addStyleName( row, 2, "oltContentPadding" );
		m_shareCellFormatter.addStyleName( row, 3, "oltContentPadding" );
		m_shareCellFormatter.addStyleName( row, 4, "oltContentPadding" );
		
		adjustShareTablePanelHeight();
	}
	
	
	/**
	 * 
	 */
	private void adjustShareTablePanelHeight()
	{
		Scheduler.ScheduledCommand cmd;
		
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				int height;
				
				// Get the height of the table that holds the list of shares.
				height = m_shareTable.getOffsetHeight();
				
				// If the height is greater than 200 pixels put an overflow auto on the panel
				// and give the panel a fixed height of 200 pixels.
				if ( height >= 200 )
					m_shareTablePanel.addStyleName( "shareThisRecipientTablePanelHeight" );
				else
					m_shareTablePanel.removeStyleName( "shareThisRecipientTablePanelHeight" );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	
	/**
	 * 
	 */
	private void adjustMyTeamsPanelHeight()
	{
		Scheduler.ScheduledCommand cmd;
		
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				int height;
				
				// Get the height of the panel that holds the list of teams.
				height = m_myTeamsPanel.getOffsetHeight();
				
				// If the height is greater than 150 pixels put an overflow auto on the panel
				// and give the panel a fixed height of 150 pixels.
				if ( height >= 150 )
					m_myTeamsPanel.addStyleName( "shareThisMyTeamsPanelHeight" );
				else
					m_myTeamsPanel.removeStyleName( "shareThisMyTeamsPanelHeight" );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	
	/**
	 * Create all the controls that make up the dialog.
	 */
	@Override
	public Panel createContent( Object callbackData )
	{
		// Construct the main dialog panel.
		m_mainPanel = new FlowPanel();
		m_mainPanel.addStyleName( "teamingDlgBoxContent" );
		m_mainPanel.addStyleName( "dlgContent" );

		return m_mainPanel;
	}
	
	/**
	 * Create all the controls needed to share this item with others.
	 */
	private FlowPanel createShareControls()
	{
		FlowPanel mainPanel;
		FlexTable mainTable;
		HTMLTable.RowFormatter mainRowFormatter;
		FlexCellFormatter mainCellFormatter;
		int row;
		
		// Create a panel for all of the controls dealing with the shares.
		mainPanel = new FlowPanel();
		
		mainTable = new FlexTable();
		mainTable.setCellSpacing( 6 );
		mainPanel.add( mainTable );
		
		mainRowFormatter = mainTable.getRowFormatter();
		mainCellFormatter = mainTable.getFlexCellFormatter();
		row = 0;
		
		mainTable.setText( row, 0, GwtTeaming.getMessages().shareDlg_shareLabel() );
		mainCellFormatter.setVerticalAlignment( row, 0, HasVerticalAlignment.ALIGN_MIDDLE );

		// Add the find control.
		{
			HTMLTable.RowFormatter rowFormatter;
			FlexTable findTable;

			// Add a KeyUpHandler to the find control
			{
				KeyUpHandler keyUpHandler;

				keyUpHandler = new KeyUpHandler()
				{
					@Override
					public void onKeyUp( KeyUpEvent event )
					{
				        final int keyCode;

				        // Get the key the user pressed
				        keyCode = event.getNativeEvent().getKeyCode();

				        // Did the user press Enter?
				        if ( keyCode == KeyCodes.KEY_ENTER )
				        {
							// Yes, kill the keystroke.
				        	event.stopPropagation();
				        	event.preventDefault();
				        }

				        ScheduledCommand cmd = new ScheduledCommand()
				        {
							@Override
							public void execute()
							{
						        // Did the user press Enter?
						        if ( keyCode == KeyCodes.KEY_ENTER )
						        {
									// Yes, try to add a new tag.
									handleClickOnAddExternalUser();
						        }
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				};
				m_findCtrl.addKeyUpHandler( keyUpHandler );
			}

			findTable = new FlexTable();
			rowFormatter = findTable.getRowFormatter();
			rowFormatter.setVerticalAlign( 0, HasVerticalAlignment.ALIGN_TOP );
			m_findCtrl.setIsSendingEmail( true );
			findTable.setWidget( 0, 0, m_findCtrl );
			
			mainTable.setWidget( row, 1, findTable );
			mainRowFormatter.setVerticalAlign( row, HasVerticalAlignment.ALIGN_TOP );
			
			// Add an "add external user" image.
			{
				ClickHandler clickHandler;
				ImageResource imageResource;
				Image addImg;
				FlexCellFormatter findCellFormatter;
				
				imageResource = GwtTeaming.getImageBundle().add_btn();
				addImg = new Image( imageResource );
				addImg.addStyleName( "cursorPointer" );
				addImg.getElement().setAttribute( "title", GwtTeaming.getMessages().shareDlg_addExternalUserTitle() );
				findTable.setWidget( 0, 1, addImg );
				findCellFormatter = findTable.getFlexCellFormatter();
				findCellFormatter.getElement( 0, 1 ).getStyle().setPaddingTop( 8, Unit.PX );
		
				// Add a click handler to the "add external user" image.
				clickHandler = new ClickHandler()
				{
					@Override
					public void onClick( ClickEvent clickEvent )
					{
						ScheduledCommand cmd = new ScheduledCommand()
						{
							@Override
							public void execute()
							{
								// Add the email address the user entered.
								handleClickOnAddExternalUser();
								
								// Put the focus back in the find control.
								m_findCtrl.getFocusWidget().setFocus( true );
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				};
				addImg.addClickHandler( clickHandler );
			}

			++row;
		}
		
		// Add the radio button for the sharing rights
		{
			FlowPanel rightsPanel;
			
			rightsPanel = new FlowPanel();
			
			m_viewRB = new RadioButton( "sharing-rights", GwtTeaming.getMessages().shareDlg_view() );
			m_viewRB.setValue( Boolean.TRUE );
			rightsPanel.add( m_viewRB );
			
			m_contributorRB = new RadioButton( "sharing-rights", GwtTeaming.getMessages().shareDlg_contributor() );
			m_contributorRB.setValue( Boolean.FALSE );
			rightsPanel.add( m_contributorRB );
			
			m_ownerRB = new RadioButton( "sharing-rights", GwtTeaming.getMessages().shareDlg_owner() );
			m_ownerRB.setValue( Boolean.FALSE );
			rightsPanel.add( m_ownerRB );
			
			mainTable.setText( row, 0, GwtTeaming.getMessages().shareDlg_rightsLabel() );
			mainTable.setWidget( row, 1, rightsPanel );
			
			++row;
		}
		
		// Add the default expiration controls
		{
			ImageResource imageResource;
			ClickHandler clickHandler;
			
			mainTable.setText( row, 0, GwtTeaming.getMessages().shareDlg_expiresLabel() );
		
			m_expiresLabel = new InlineLabel( GwtTeaming.getMessages().shareDlg_expiresNever() );
			m_expiresLabel.addStyleName( "shareThis_DefaultExpiresLabel" );

			imageResource = GwtTeaming.getImageBundle().activityStreamActions1();
			m_defaultExpirationImg = new Image( imageResource );
			m_defaultExpirationImg.getElement().setAttribute( "align", "absmiddle" );

			m_defaultShareExpirationValue = new ShareExpirationValue();
			m_defaultShareExpirationValue.setType( ShareExpirationType.NEVER );
			
			updateDefaultExpirationLabel();
			
			clickHandler = new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							handleClickOnDefaultExpiration();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};
			m_expiresLabel.addClickHandler( clickHandler );

			mainTable.setWidget( row, 1, m_expiresLabel );
			
			++row;
		}
		
		// Add some space
		{
			FlowPanel spacerPanel;
			
			spacerPanel = new FlowPanel();
			spacerPanel.getElement().getStyle().setMarginTop( 10, Unit.PX );
			mainTable.setWidget( row, 1, spacerPanel );
			++row;
		}
		
		// Create a table to hold the list of shares
		{
			m_shareTablePanel = new FlowPanel();
			
			m_shareTable = new FlexTable();
			m_shareTable.addStyleName( "shareThisRecipientTable" );
			m_shareTable.setCellSpacing( 0 );
			m_shareTable.setWidth( "550px" );

			// Add the column headers.
			{
				m_shareTable.setText( 0, 0, GwtTeaming.getMessages().shareName() );
				m_shareTable.setText( 0, 1, GwtTeaming.getMessages().shareRecipientType() );
				m_shareTable.setText( 0, 2, GwtTeaming.getMessages().shareAccess() );
				m_shareTable.setText( 0, 3, GwtTeaming.getMessages().shareExpires() );
				m_shareTable.setHTML( 0, 4, "&nbsp;" );	// The delete image will go in this column.
				
				m_shareRowFormatter = m_shareTable.getRowFormatter();
				m_shareRowFormatter.addStyleName( 0, "oltHeader" );

				m_shareCellFormatter = m_shareTable.getFlexCellFormatter();
				// On IE calling m_cellFormatter.setWidth( 0, 2, "*" ); throws an exception.
				// That is why we are calling DOM.setElementAttribute(...) instead.
				//!!!m_cellFormatter.setWidth( 0, 2, "*" );
				DOM.setElementAttribute( m_shareCellFormatter.getElement( 0, 4 ), "width", "*" );
				
				m_shareCellFormatter.addStyleName( 0, 0, "oltBorderLeft" );
				m_shareCellFormatter.addStyleName( 0, 0, "oltHeaderBorderTop" );
				m_shareCellFormatter.addStyleName( 0, 0, "oltHeaderBorderBottom" );
				m_shareCellFormatter.addStyleName( 0, 0, "oltHeaderPadding" );
				m_shareCellFormatter.addStyleName( 0, 1, "oltHeaderBorderTop" );
				m_shareCellFormatter.addStyleName( 0, 1, "oltHeaderBorderBottom" );
				m_shareCellFormatter.addStyleName( 0, 1, "oltHeaderPadding" );
				m_shareCellFormatter.addStyleName( 0, 2, "oltHeaderBorderTop" );
				m_shareCellFormatter.addStyleName( 0, 2, "oltHeaderBorderBottom" );
				m_shareCellFormatter.addStyleName( 0, 2, "oltHeaderPadding" );
				m_shareCellFormatter.addStyleName( 0, 3, "oltHeaderBorderTop" );
				m_shareCellFormatter.addStyleName( 0, 3, "oltHeaderBorderBottom" );
				m_shareCellFormatter.addStyleName( 0, 3, "oltHeaderPadding" );
				m_shareCellFormatter.addStyleName( 0, 4, "oltBorderRight" );
				m_shareCellFormatter.addStyleName( 0, 4, "oltHeaderBorderTop" );
				m_shareCellFormatter.addStyleName( 0, 4, "oltHeaderBorderBottom" );
				m_shareCellFormatter.addStyleName( 0, 4, "oltHeaderPadding" );
			}
			
			m_shareTablePanel.add( m_shareTable );

			mainRowFormatter.setVerticalAlign( row, HasVerticalAlignment.ALIGN_TOP );

			mainTable.setText( row, 0, GwtTeaming.getMessages().shareDlg_sharingLabel() );
			mainTable.setWidget( row, 1, m_shareTablePanel );
			mainCellFormatter.setColSpan( row, 1, 2 );
			
			++row;
		}

		// Create a panel for all of the controls dealing with "my teams"
		// Later, we will issue an ajax request to get the list of teams.  updateListOfTeams()
		// will populate the ui.
		{
			Label label;

			m_shareWithTeamsPanel = new FlowPanel();
			m_shareWithTeamsPanel.addStyleName( "shareThisShareWithTeamsPanel" );
			label = new Label( GwtTeaming.getMessages().shareWithTeams() );
			label.addStyleName( "shareThisShareWithTeamsLabel" );
			m_shareWithTeamsPanel.add( label );
			
			mainPanel.add( m_shareWithTeamsPanel );
			
			m_myTeamsPanel = new FlowPanel();
			m_shareWithTeamsPanel.add( m_myTeamsPanel );
		}
		
		// Create an image resource for the delete image.
		m_deleteImgR = GwtTeaming.getImageBundle().delete();

		return mainPanel;
	}
	
	/**
	 * This method gets called when user user presses the Cancel push
	 * button.
	 * 
	 * Implements the EditCanceledHandler.editCanceled() interface
	 * method.
	 * 
	 * @return
	 */
	@Override
	public boolean editCanceled()
	{
		int i;
		
		// Go through the list of shares and close any "Group Membership" popups that may be open.
		for (i = 1; i < m_shareTable.getRowCount(); ++i)
		{
			Widget widget;
			
			if ( m_shareTable.getCellCount( i ) > 2 )
			{
				// Get the RecipientNameWidget from the first column.
				widget = m_shareTable.getWidget( i, 0 );
				if ( widget != null && widget instanceof RecipientNameWidget )
				{
					// Close any group membership popup that this widget may have open.
					((RecipientNameWidget) widget).closePopups();
				}
			}
		}
		
		// Simply return true to allow the dialog to close.
		return true;
	}

	/**
	 * 
	 */
	private void editDefaultExpiration()
	{
		if ( m_shareExpirationDlg != null )
		{
			if ( m_editDefaultExpirationHandler == null )
			{
				m_editDefaultExpirationHandler = new EditSuccessfulHandler()
				{
					@Override
					public boolean editSuccessful( Object obj )
					{
						if ( obj instanceof ShareExpirationValue )
						{
							ShareExpirationValue expirationValue;
							
							expirationValue = (ShareExpirationValue) obj;
							m_defaultShareExpirationValue.set( expirationValue );
							
							updateDefaultExpirationLabel();
						}
						
						return true;
					}
				};
			}
			
			// Invoke the "share expiration" dialog.
			m_shareExpirationDlg.init( m_defaultShareExpirationValue, m_editDefaultExpirationHandler );
			m_shareExpirationDlg.showRelativeToTarget( m_expiresLabel );
		}
	}
	
	/**
	 * This method gets called when user user presses the OK push
	 * button.  We will issue an ajax request to start the work of
	 * sending an email to each of the recipients.
	 * 
	 * Implements the EditSuccessfulHandler.editSuccessful() interface
	 * method.
	 * 
	 * @param callbackData
	 * 
	 * @return
	 */
	@Override
	public boolean editSuccessful( Object callbackData )
	{
		String comment;
		GwtSharingInfo sharingData;
		ArrayList<GwtShareItemMember> listOfShareItemMembers;
		
		if ( m_shareEntryCallback == null )
		{
			m_shareEntryCallback = new AsyncCallback<VibeRpcResponse>()
			{
				@Override
				public void onFailure( Throwable caught )
				{
					GwtClientHelper.handleGwtRPCFailure(
						caught,
						GwtTeaming.getMessages().rpcFailure_ShareEntry() );
					
					hide();
				}// end onFailure()

				@Override
				public void onSuccess( VibeRpcResponse vibeResult )
				{
					GwtShareEntryResults result = ((ShareEntryResultsRpcResponseData) vibeResult.getResponseData()).getShareEntryResults();
					String[] errorMessages;
					FlowPanel errorPanel;
					
					boolean haveErrors;
					
					haveErrors = false;
					
					// Get the panel that holds the errors.
					errorPanel = getErrorPanel();
					errorPanel.clear();
					
					// Were there any errors?
					errorMessages = result.getErrors();
					if ( errorMessages != null && errorMessages.length > 0 )
					{
						// Yes
						haveErrors = true;

						// Add each error message to the error panel.
						{
							Label label;
							
							label = new Label( GwtTeaming.getMessages().shareErrors() );
							label.addStyleName( "dlgErrorLabel" );
							errorPanel.add( label );
							
							for ( String nextErrMsg : errorMessages )
							{
								label = new Label( nextErrMsg );
								label.addStyleName( "bulletListItem" );
								errorPanel.add( label );
							}
						}
					}
					
					// Do we have any errors to display?
					if ( haveErrors )
					{
						// Yes
						// Make the error panel visible.
						showErrors();
					}
					else
					{
						// Close this dialog.
						hide();
					}
				}// end onSuccess()				
			};
		}
		
		// Get the comment the user entered.
		comment = getComment();
		
		// Get a list of all the users/groups/teams that the entities are being shared with.
		listOfShareItemMembers = getListOfShareItemMembers();

		sharingData = new GwtSharingInfo();
		sharingData.setListOfEntityIds( m_entityIds );
		sharingData.setListOfShareItemMembers( listOfShareItemMembers );
		sharingData.setListOfShareItems( m_sharingInfo.getListOfShareItems() );
		
		// Issue an ajax request to share the entities.
		ShareEntryCmd cmd = new ShareEntryCmd( comment, sharingData );
		GwtClientHelper.executeCommand( cmd, m_shareEntryCallback );
		
		// Returning false will prevent the dialog from closing.  We will close
		// the dialog when we get the response back from our ajax request.
		return false;
	}

	/**
	 * Find the given recipient in the table that holds the recipients.
	 */
	private int findShareByRecipientName( GwtShareItemMember shareItemMember )
	{
		int i;
		String name;
		GwtRecipientType type;
		
		name = shareItemMember.getRecipientName();
		type = shareItemMember.getRecipientType();
		
		// Look through the table for the given recipient.
		// Recipients start in row 1.
		for (i = 1; i < m_shareTable.getRowCount() && m_shareTable.getCellCount( i ) > 4; ++i)
		{
			Widget widget;
			
			// Get the RemoveRecipientWidget from the 5 column.
			widget = m_shareTable.getWidget( i, 4 );
			if ( widget != null && widget instanceof RemoveShareWidget )
			{
				GwtShareItemMember nextShareItemMember;
				
				nextShareItemMember = ((RemoveShareWidget) widget).getShareItemMember();
				if ( nextShareItemMember != null )
				{
					if ( type == nextShareItemMember.getRecipientType() )
					{
						if ( name != null && name.equalsIgnoreCase( nextShareItemMember.getRecipientName() ) )
						{
							// We found the recipient.
							return i;
						}
					}
				}
			}
		}
		
		// If we get here we did not find the recipient.
		return -1;
	}
	

	/**
	 * Return the text the user entered for the comment.
	 */
	private String getComment()
	{
		return m_msgTextArea.getText();
	}
	
	/**
	 * Implements the DlgBox.getDataFromDlg() abstract method.
	 * 
	 * @return
	 */
	@Override
	public Object getDataFromDlg()
	{
		return Boolean.TRUE;
	}

	/**
	 * Issue an rpc request to get information about the given entity.
	 */
	private void getEntityInfoFromServer( final EntityId entityId )
	{
		if ( entityId == null )
			return;
		
		// Are we working with a folder entry?
		if ( entityId.isEntry() )
		{
			GetEntryCmd cmd;
			AsyncCallback<VibeRpcResponse> callback;

			// Yes
			callback = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				@Override
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetFolderEntry(),
						entityId.getEntityId() );
				}
		
				/**
				 * 
				 * @param result
				 */
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					final GwtFolderEntry gwtFolderEntry;
					
					gwtFolderEntry = (GwtFolderEntry) response.getResponseData();
					
					if ( gwtFolderEntry != null )
					{
						Scheduler.ScheduledCommand cmd;
						
						cmd = new Scheduler.ScheduledCommand()
						{
							@Override
							public void execute()
							{
								// Update the name of the entity in the header.
								m_headerNameLabel.setText( gwtFolderEntry.getEntryName() );
								m_headerPathLabel.setText( gwtFolderEntry.getParentBinderName() );
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				}
			};

			cmd = new GetEntryCmd( null, entityId.getEntityId().toString() );
			GwtClientHelper.executeCommand( cmd, callback );
		}
		else
		{
			GetFolderCmd cmd;
			AsyncCallback<VibeRpcResponse> callback;
			
			callback = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				@Override
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetFolder(),
						entityId.getEntityId() );
				}
		
				/**
				 * 
				 * @param result
				 */
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					final GwtFolder gwtFolder;
					
					gwtFolder = (GwtFolder) response.getResponseData();
					
					if ( gwtFolder != null )
					{
						Scheduler.ScheduledCommand cmd;

						cmd = new Scheduler.ScheduledCommand()
						{
							@Override
							public void execute()
							{
								// Update the name of the entity in the header
								m_headerNameLabel.setText( gwtFolder.getFolderName() );
								m_headerPathLabel.setText( gwtFolder.getParentBinderName() );
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				}
			};

			cmd = new GetFolderCmd( null, entityId.getEntityId().toString() );
			GwtClientHelper.executeCommand( cmd, callback );
		}
	}
	
	/**
	 * Returns the Widget to give the focus to.
	 * 
	 * Implements the DlgBox.getFocusWidget() abstract method.
	 * 
	 * @return
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		if ( m_findCtrl != null )
			return m_findCtrl.getFocusWidget();
		
		return null;
	}
	

	/**
	 * Return the a list of all the user/groups/teams the user has selected to share
	 * the entities with.
	 */
	private ArrayList<GwtShareItemMember> getListOfShareItemMembers()
	{
		ArrayList<GwtShareItemMember> listOfShareItemMembers;
		int i;
		
		listOfShareItemMembers = new ArrayList<GwtShareItemMember>();
		
		// Get all of the GwtShareItemMember objects from the table that holds the list.
		// Recipients start in row 1.
		for (i = 1; i < m_shareTable.getRowCount(); ++i)
		{
			Widget widget;
			
			if ( m_shareTable.getCellCount( i ) > 2 )
			{
				// Get the RecipientNameWidget from the first column.
				widget = m_shareTable.getWidget( i, 0 );
				if ( widget != null && widget instanceof RecipientNameWidget )
				{
					GwtShareItemMember shareItemMember;
					
					shareItemMember = ((RecipientNameWidget) widget).getShareItemMember();
					if ( shareItemMember != null )
					{
						listOfShareItemMembers.add( shareItemMember );
					}
				}
			}
		}
		
		return listOfShareItemMembers;
	}
	
	/**
	 * Return the text the user has entered for the message.
	 */
	public String getMsg()
	{
		return m_msgTextArea.getText();
	}
	
	/**
	 * Return the selected share rights
	 */
	private ShareRights getSelectedShareRights()
	{
		if (  m_viewRB.getValue() == Boolean.TRUE )
			return ShareRights.VIEW;
		
		if ( m_contributorRB.getValue() == Boolean.TRUE )
			return ShareRights.CONTRIBUTOR;
		
		if ( m_ownerRB.getValue() == Boolean.TRUE )
			return ShareRights.OWNER;
		
		return ShareRights.UNKNOWN;
	
	}
	
	/**
	 * Return the ids of each of the teams the user selected.
	 */
	private ArrayList<String> getTeamIds()
	{
		ArrayList<String> teamIds;
		int i;
		
		teamIds = new ArrayList<String>();
		
		for (i = 0; i < m_myTeamsPanel.getWidgetCount(); ++i)
		{
			Widget nextWidget;
			
			// Get the next widget in the "my teams" panel.
			nextWidget = m_myTeamsPanel.getWidget( i );
			
			// Is this widget a TeamCheckbox widget? 
			if ( nextWidget instanceof TeamCheckBox )
			{
				TeamCheckBox teamCheckbox;
				
				// Yes, is the team selected?
				teamCheckbox = (TeamCheckBox) nextWidget;
				if ( teamCheckbox.getValue() == Boolean.TRUE )
				{
					// Yes, add it to the list.
					teamIds.add( teamCheckbox.getTeamInfo().getBinderId() );
				}
			}
		}
		
		return teamIds;
	}
	
	/**
	 * 
	 */
	private void handleClickOnAddExternalUser()
	{
		String emailAddress;
		
		emailAddress = m_findCtrl.getText();

		if ( emailAddress != null && emailAddress.length() > 0 )
		{
			GwtShareItemMember shareItemMember;
			
			// Clear what the user has typed.
			m_findCtrl.clearText();
			
			shareItemMember = new GwtShareItemMember();
			shareItemMember.setRecipientName( emailAddress );
			shareItemMember.setRecipientType( GwtRecipientType.EXTERNAL_USER );
			shareItemMember.setShareRights( getSelectedShareRights() );
			shareItemMember.setShareExpirationValue( m_defaultShareExpirationValue );
			//!!! Finish
			
			// Is this external user already in the list?
			if ( findShareByRecipientName( shareItemMember ) == -1 )
			{
				// No, add it
				addShare( shareItemMember, true );
			}
			else
			{
				// Tell the user the item has already been shared with the external user.
				Window.alert( GwtTeaming.getMessages().shareDlg_alreadySharedWithSelectedRecipient( emailAddress ) );
			}
		}
	}
	
	/**
	 * 
	 */
	private void handleClickOnDefaultExpiration()
	{
		if ( m_shareExpirationDlg == null )
		{
			ShareExpirationDlg.createAsync( true, true, new ShareExpirationDlgClient()
			{
				@Override
				public void onUnavailable() 
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( ShareExpirationDlg seDlg )
				{
					m_shareExpirationDlg = seDlg;
					editDefaultExpiration();
				}
			} );
		}
		else
			editDefaultExpiration();
	}
	
	/**
	 * 
	 */
	private void highlightRecipient( int row )
	{
		if ( row < m_shareTable.getRowCount() )
			m_shareRowFormatter.addStyleName( row, "shareThisRecipientTable_highlightRow" );
	}
	
	/**
	 * 
	 */
	private void init()
	{
		GetSharingInfoCmd rpcCmd1;
		GetMyTeamsCmd rpcCmd2;
		
		updateHeader();

		m_msgTextArea.setText( "" );
		m_findCtrl.setInitialSearchString( "" );

		// Set the filter of the Find Control to only search for users and groups.
		m_findCtrl.setSearchType( SearchType.PRINCIPAL );

		// Remove all of the rows from the table.
		// We start at row 1 so we don't delete the header.
		while ( m_shareTable.getRowCount() > 1 )
		{
			// Remove the 1st row that holds share information.
			m_shareTable.removeRow( 1 );
		}
		
		// Add a message to the table telling the user this item has not been shared.
		addNotSharedMessage();

		adjustShareTablePanelHeight();
		
		if ( m_readTeamsCallback == null )
		{
			// Create a callback that will be used when we read the teams the user is a member of
			m_readTeamsCallback = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				@Override
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
							t,
							GwtTeaming.getMessages().rpcFailure_GetMyTeams() );
				}
				
				/**
				 * 
				 */
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					final List<TeamInfo> listOfTeams;
					GetMyTeamsRpcResponseData responseData;
					Scheduler.ScheduledCommand cmd;
					
					responseData = (GetMyTeamsRpcResponseData) response.getResponseData();
					listOfTeams = responseData.getTeams();
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							// Update the dialog with the list of teams.
							updateListOfTeams( listOfTeams );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};
		}
		
		if ( m_getSharingInfoCallback == null )
		{
			// Create a callback that will be used when we read the sharing information.
			m_getSharingInfoCallback = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				@Override
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
							t,
							GwtTeaming.getMessages().rpcFailure_GetSharingInfo() );
				}
				
				/**
				 * 
				 */
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					final GwtSharingInfo sharingInfo;
					Scheduler.ScheduledCommand cmd;
					
					sharingInfo = (GwtSharingInfo) response.getResponseData();
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							updateSharingInfo( sharingInfo );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};
		}
		
		// Issue an rpc request to get the share information for the entities we are working with.
		rpcCmd1 = new GetSharingInfoCmd( m_entityIds );
		GwtClientHelper.executeCommand( rpcCmd1, m_getSharingInfoCallback );

		// Issue an rpc request to get the teams this user is a member of.
		rpcCmd2 = new GetMyTeamsCmd();
		GwtClientHelper.executeCommand( rpcCmd2, m_readTeamsCallback );
	}

	/**
	 * Remove the given share from the table
	 */
	public void removeShare( GwtShareItemMember shareItemMember )
	{
		int row;
		
		// Find the row this share lives in.
		row = findShareByRecipientName( shareItemMember );
		
		// Did we find the share in the table?
		if ( row > 0 )
		{
			// Yes
			// Remove the share from the table.
			m_shareTable.removeRow( row );
			
			// Did we remove the last share from the table?
			if ( m_shareTable.getRowCount() == 1 )
			{
				// Yes
				// Add the "no recipients..." message to the table.
				addNotSharedMessage();
			}
			
			adjustShareTablePanelHeight();
		}
	}
	
	
	/*
	 * 
	 */
	private void showDlg()
	{
		init();
		
		hideErrorPanel();
		showContentPanel();
		createFooterButtons( DlgBox.DlgButtonMode.OkCancel );

		if ( null == m_target )
		     show( true );	// true -> Show centered when not given a target.
		else showRelativeToTarget( m_target );
	}
	
	/**
	 * Update the label that holds the default expiration value.
	 */
	private void updateDefaultExpirationLabel()
	{
		if ( m_defaultShareExpirationValue != null )
		{
			m_expiresLabel.setText( m_defaultShareExpirationValue.getValueAsString() );
			m_expiresLabel.getElement().appendChild( m_defaultExpirationImg.getElement() );
		}
	}
	
	/**
	 * Update the header that displays the name of the entity we are working with.
	 * If we are dealing with > 1 entity we don't show a header.
	 */
	private void updateHeader()
	{
		// Are we dealing with > 1 entities?
		if ( m_entityIds != null && m_entityIds.size() == 1 )
		{
			EntityId entityId;
			ImageResource imgResource;
			
			// No
			entityId = m_entityIds.get( 0 );

			m_headerNameLabel.setText( "" );
			m_headerPathLabel.setText( "" );

			// Issue an rpc request to get information about this entity
			getEntityInfoFromServer( entityId );

			// Are we dealing with a folder entry?
			if ( entityId .isEntry() )
			{
				// Yes
				imgResource = GwtTeaming.getFilrImageBundle().entry_large();
			}
			else
			{
				// We must be dealing with a binder.
				imgResource = GwtTeaming.getFilrImageBundle().folder_large();
			}

			m_headerImg.setResource( imgResource );
			m_headerPanel.setVisible( true );
		}
		else
		{
			m_headerPanel.setVisible( false );
		}
	}
	
	/**
	 * Create a checkbox for every team so the user can select the teams that should
	 * receive an email.
	 */
	private void updateListOfTeams( List<TeamInfo> listOfTeams )
	{
		int count = 0;
		Iterator<TeamInfo> teamIT;
		
		m_myTeamsPanel.clear();
		m_myTeamsPanel.removeStyleName( "shareThisMyTeamsPanelHeight" );
		
		teamIT = listOfTeams.iterator();
		while ( teamIT.hasNext() )
		{
			TeamInfo nextTeamInfo;
			TeamCheckBox checkbox;

			nextTeamInfo = teamIT.next();

			// Create a checkbox for this team.
			checkbox = new TeamCheckBox( nextTeamInfo, nextTeamInfo.getTitle() );
			m_myTeamsPanel.add( checkbox );
			
			++count;
		}

		// Do we have any teams?
		if ( count == 0 )
		{
			// No, Hide the panel that holds the teams.
			m_shareWithTeamsPanel.setVisible( false );
		}
		else
		{
			m_shareWithTeamsPanel.setVisible( true );
			adjustMyTeamsPanelHeight();
		}
	}
	
	/**
	 * Update the "comments" field from the given GwtSharingInfo object
	 */
	private void updateCommentFld( GwtSharingInfo sharingInfo )
	{
		ArrayList<GwtShareItem> listOfShareItems;
		
		// Get the list of GwtShareItems
		listOfShareItems = sharingInfo.getListOfShareItems();
		if ( listOfShareItems != null )
		{
			// Use the first non-empty comment
			for (GwtShareItem nextShareItem : listOfShareItems)
			{
				String comment;
				
				comment = nextShareItem.getDesc();
				if ( comment != null && comment.length() > 0 )
				{
					m_msgTextArea.setText( comment );
					return;
				}
			}
		}
	}
	
	/**
	 * Update the sharing information with the given information
	 */
	private void updateSharingInfo( GwtSharingInfo sharingInfo )
	{
		m_sharingInfo = sharingInfo;
		if ( sharingInfo != null )
		{
			ArrayList<GwtShareItemMember> listOfShareItemMembers;
			
			listOfShareItemMembers = sharingInfo.getListOfShareItemMembers();
			if ( listOfShareItemMembers != null )
			{
				for (GwtShareItemMember nextShareItemMember : listOfShareItemMembers)
				{
					addShare( nextShareItemMember, false );
				}
			}
			
			// Update the comment field
			updateCommentFld( sharingInfo );
		}
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
	 * Called when the dialog is detached.
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
	 * Handles SearchFindResultsEvent's received by this class.
	 * 
	 * Implements the SearchFindResultsEvent.Handler.onSearchFindResults() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSearchFindResults( SearchFindResultsEvent event )
	{
		final GwtTeamingItem selectedObj;
		Scheduler.ScheduledCommand cmd;

		// If the find results aren't for this share this dialog...
		if ( !((Widget) event.getSource()).equals( this ) )
		{
			// ...ignore the event.
			return;
		}
		
		selectedObj = event.getSearchResults();
		
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute() 
			{
				GwtShareItemMember shareItemMember = null;
				
				// Are we dealing with a User?
				if ( selectedObj instanceof GwtUser )
				{
					GwtUser user;
					
					// Yes
					user = (GwtUser) selectedObj;
					
					shareItemMember = new GwtShareItemMember();
					shareItemMember.setRecipientName( user.getShortDisplayName() );
					shareItemMember.setRecipientType( GwtRecipientType.USER );
					shareItemMember.setRecipientId( Long.valueOf( user.getUserId() ) );
				}
				// Are we dealing with a group?
				else if ( selectedObj instanceof GwtGroup )
				{
					GwtGroup group;
					
					// Yes
					group = (GwtGroup) selectedObj;
					
					shareItemMember = new GwtShareItemMember();
					shareItemMember.setRecipientName( group.getShortDisplayName() );
					shareItemMember.setRecipientType( GwtRecipientType.GROUP );
					shareItemMember.setRecipientId( Long.valueOf( group.getId() ) );
				}

				// Do we have an object to add to our list of shares?
				if ( shareItemMember != null )
				{
					// Yes
					// Has the item already been shared with the recipient
					if ( findShareByRecipientName( shareItemMember ) == -1 )
					{
						// No
						shareItemMember.setShareRights( getSelectedShareRights() );
						shareItemMember.setShareExpirationValue( m_defaultShareExpirationValue );
						
						// Add the recipient to our list of recipients
						addShare( shareItemMember, true );
					}
					else
					{
						// Yes, tell the user
						Window.alert( GwtTeaming.getMessages().shareDlg_alreadySharedWithSelectedRecipient( shareItemMember.getRecipientName() ) );
					}
				}

				// Hide the search-results widget.
				m_findCtrl.hideSearchResults();
				
				// Clear the text from the find control.
				m_findCtrl.clearText();
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}// end onSearchFindResults()
	
	/*
	 * Asynchronously loads the find control.
	 */
	private void loadPart1Async()
	{
		ScheduledCommand doLoad = new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				loadPart1Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously loads the find control.
	 */
	private void loadPart1Now()
	{
		FindCtrl.createAsync( this, GwtSearchCriteria.SearchType.FOLDERS, new FindCtrlClient()
		{			
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}
			
			@Override
			public void onSuccess( FindCtrl findCtrl )
			{
				// ...and populate the dialog.
				m_findCtrl = findCtrl;
				populateDlgAsync();
			}
		});
	}
	
	/*
	 * Asynchronously runs the given instance of the move entries
	 * dialog.
	 */
	private static void runDlgAsync( final ShareThisDlg stDlg, final UIObject target, final String caption, final String title, final List<EntityId> entityIds )
	{
		ScheduledCommand doRun = new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				stDlg.runDlgNow( target, caption, title, entityIds );
			}
		};
		Scheduler.get().scheduleDeferred( doRun );
	}
	
	/*
	 * Synchronously runs the given instance of the move entries
	 * dialog.
	 */
	private void runDlgNow( UIObject target, String caption, String title, List<EntityId> entityIds )
	{
		// Set the caption...
		setCaption( caption );
		
		// ...and store the parameters.
		m_target    = target;
		m_entityIds = entityIds;
		
		// If we haven't completed construction of the dialog yet,
		// complete it now.  Otherwise, simply show it.
		if (null == m_findCtrl)
		     loadPart1Async();	// This completes construction and shows the dialog.
		else showDlg();			// This simply shows the dialog.
	}

	/*
	 * Asynchronously populates the contents of the dialog.
	 */
	private void populateDlgAsync()
	{
		ScheduledCommand doPopulate = new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				populateDlgNow();
			}
		};
		Scheduler.get().scheduleDeferred( doPopulate );
	}
	
	/*
	 * Synchronously populates the contents of the dialog.
	 */
	private void populateDlgNow()
	{
		FlowPanel inputPanel;
		FlowPanel sharePanel;
		Label comments;
		
		// Create the controls needed in the header
		{
			FlowPanel namePanel;
			
			m_headerPanel = new FlowPanel();
			m_headerPanel.addStyleName( "shareThisDlg_HeaderPanel" );
		
			m_headerImg = new Image();
			m_headerImg.addStyleName( "shareThisDlg_HeaderImg" );
			m_headerPanel.add( m_headerImg );
			
			namePanel = new FlowPanel();
			namePanel.addStyleName( "shareThisDlg_HeaderNamePanel" );
			
			m_headerNameLabel = new Label();
			m_headerNameLabel.addStyleName( "shareThisDlg_HeaderNameLabel" );
			namePanel.add( m_headerNameLabel );
			
			m_headerPathLabel = new Label();
			m_headerPathLabel.addStyleName( "shareThisDlg_HeaderPathLabel" );
			namePanel.add( m_headerPathLabel );
			
			m_headerPanel.add( namePanel );
			
			m_mainPanel.add( m_headerPanel );
		}
		
		// Add the controls needed to manage sharing.
		sharePanel = createShareControls();
		m_mainPanel.add( sharePanel );
		
		// Add a "Comments:" label before the textbox.
		comments = new Label( GwtTeaming.getMessages().commentsLabel() );
		comments.addStyleName( "shareThisCommentsLabel" );
		m_mainPanel.add( comments );
		
		// Create a textbox
		inputPanel = new FlowPanel();
		m_msgTextArea = new TextArea();
		m_msgTextArea.addStyleName( "shareThisTextArea" );
		m_msgTextArea.addStyleName( "shareThisTextAreaBorder" );
		inputPanel.add( m_msgTextArea );
		m_mainPanel.add( inputPanel );
		
		// Show the dialog.
		showDlg();
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

	/**
	 * Unlighlight the given row in the table that holds the list of recipients
	 */
	private void unhighlightRecipient( int row )
	{
		if ( row < m_shareTable.getRowCount() )
			m_shareRowFormatter.removeStyleName( row, "shareThisRecipientTable_highlightRow" );
	}
	
	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents()
	{
		// If we have a non-empty list of registered events...
		if ( ( null != m_registeredEventHandlers ) && ( ! ( m_registeredEventHandlers.isEmpty() ) ) )
		{
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers( m_registeredEventHandlers );
		}
	}

	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the share this dialog and perform some operation on it.       */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the share this dialog
	 * asynchronously after it loads. 
	 */
	public interface ShareThisDlgClient
	{
		void onSuccess( ShareThisDlg stDlg );
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the ShareThisDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final ShareThisDlgClient stDlgClient,
			
			// initAndShow parameters,
			final ShareThisDlg		stDlg,
			final UIObject			target,
			final String			caption,
			final String			title,
			final List<EntityId>	entityIds )
	{
		GWT.runAsync(ShareThisDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure(Throwable reason)
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ShareThisDlg() );
				if ( null != stDlgClient )
				{
					stDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				// Is this a request to create a dialog?
				if ( null != stDlgClient )
				{
					// Yes!  Create it and return it via the callback.
					ShareThisDlg stDlg = new ShareThisDlg();
					stDlgClient.onSuccess( stDlg );
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync( stDlg, target, caption, title, entityIds );
				}
			}
		});
	}
	
	/**
	 * Loads the ShareThisDlg split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param stDlgClient
	 */
	public static void createAsync( ShareThisDlgClient stDlgClient )
	{
		doAsyncOperation( stDlgClient, null, null, null, null, null );
	}
	
	/**
	 * Initializes and shows the share this dialog.
	 * 
	 * @param stDlg
	 * @param caption
	 * @param title
	 * @param entityIds
	 */
	public static void initAndShow( ShareThisDlg stDlg, UIObject target, String caption, String title, List<EntityId> entityIds )
	{
		doAsyncOperation( null, stDlg, target, caption, title, entityIds );
	}
}

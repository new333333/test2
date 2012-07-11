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
import org.kablink.teaming.gwt.client.GwtShareEntryResults;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.mainmenu.TeamInfo;
import org.kablink.teaming.gwt.client.rpc.shared.GetMyTeamsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetMyTeamsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.rpc.shared.ShareEntryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ShareEntryResultsRpcResponseData;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.FindCtrl;
import org.kablink.teaming.gwt.client.widgets.FindCtrl.FindCtrlClient;

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
import com.google.gwt.user.client.rpc.IsSerializable;
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
import com.google.gwt.user.client.ui.TextBox;
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
	private TextBox m_titleTextBox;
	private TextArea m_msgTextArea;
	private RadioButton m_usersRB;
	private RadioButton m_groupsRB;
	private RadioButton m_viewRB;
	private RadioButton m_contributorRB;
	private RadioButton m_ownerRB;
	private FindCtrl m_findCtrl;
	private InlineLabel m_expiresLabel;
	private FlowPanel m_mainPanel;
	private ImageResource m_deleteImgR;
	private FlexTable m_shareTable;
	private FlowPanel m_shareWithTeamsPanel;
	private FlowPanel m_myTeamsPanel;
	private FlowPanel m_shareTablePanel;
	private FlexCellFormatter m_shareCellFormatter;
	private List<EntityId> m_entityIds;
	private List<HandlerRegistration> m_registeredEventHandlers;
	private AsyncCallback<VibeRpcResponse> m_readTeamsCallback;
	private AsyncCallback<VibeRpcResponse> m_shareEntryCallback;
	private String m_title;
	private UIObject m_target;
	private ShareExpirationValue m_defaultShareExpirationValue;

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
	 * This class represents the different share expiration values
	 */
	public enum ShareExpirationType implements IsSerializable
	{
		AFTER_DAYS,
		NEVER,
		ON_DATE,
		
		UNKNOWN
	}
	
	/**
	 * This class represents a share expiration value
	 */
	public class ShareExpirationValue
	{
		private ShareExpirationType m_expirationType;
		private Long m_value;
		
		/**
		 * 
		 */
		public ShareExpirationValue()
		{
			m_expirationType = ShareExpirationType.NEVER;
			m_value = null;
		}
		
		/**
		 * 
		 */
		public ShareExpirationValue( ShareExpirationValue value )
		{
			m_expirationType = value.getExpirationType();
			m_value = value.getValue();
		}
		
		/**
		 * 
		 */
		public ShareExpirationType getExpirationType()
		{
			return m_expirationType;
		}
		
		/**
		 * 
		 */
		public Long getValue()
		{
			return m_value;
		}
		
		/**
		 * 
		 */
		public String getValueAsString()
		{
			if ( m_expirationType == ShareExpirationType.NEVER )
				return GwtTeaming.getMessages().shareDlg_expiresNever();
			
			if ( m_expirationType == ShareExpirationType.ON_DATE )
			{
				String on;
				
				on = "";
				if ( m_value != null )
					m_value.toString();
				
				return GwtTeaming.getMessages().shareDlg_expiresOn( on );
			}
			
			if ( m_expirationType == ShareExpirationType.AFTER_DAYS )
			{
				String after;
				
				after = "";
				if ( m_value != null )
					after = m_value.toString();
				
				return GwtTeaming.getMessages().shareDlg_expiresAfter( after );
			}
			
			return "Unknown expiration type";
		}
		
		/**
		 * 
		 */
		public void setType( ShareExpirationType type )
		{
			m_expirationType = type;
		}
		
		/**
		 * 
		 */
		public void setValue( Long value )
		{
			m_value = value;
		}
	}
	
	/**
	 * This class represents the different share rights
	 */
	public enum ShareRights implements IsSerializable
	{
		VIEW,
		CONTRIBUTOR,
		OWNER,
		
		UNKNOWN
	}
	
	/**
	 * This class represents the different types of recipients.
	 */
	public enum RecipientType implements IsSerializable
	{
		USER,
		GROUP,
		EXTERNAL_USER,
		TEAM,
		
		UNKNOWN,
	}

	
	/**
	 * This class is used to hold information about a share
	 */
	public class ShareInfo
	{
		private Long m_shareId;
		private String m_recipientName;
		private String m_recipientId;
		private RecipientType m_recipientType;
		private ShareRights m_shareRights;
		private ShareExpirationValue m_shareExpirationValue;
		
		/**
		 * 
		 */
		public ShareInfo()
		{
			m_shareId = null;
			m_recipientName = null;
			m_recipientId = null;
			m_recipientType = RecipientType.UNKNOWN;
			m_shareRights = ShareRights.VIEW;
			m_shareExpirationValue = null;
		}
		
		/**
		 * 
		 */
		public String getRecipientId()
		{
			return m_recipientId;
		}
		
		/**
		 * 
		 */
		public String getRecipientName()
		{
			return m_recipientName;
		}

		/**
		 * 
		 */
		public String getShareExpirationValueAsString()
		{
			if ( m_shareExpirationValue != null )
				return m_shareExpirationValue.getValueAsString();
			
			return "";
		}
		
		/**
		 * 
		 */
		public ShareRights getShareRights()
		{
			return m_shareRights;
		}
		
		/**
		 * 
		 */
		public String getShareRightsAsString()
		{
			if ( m_shareRights == ShareRights.VIEW )
				return GwtTeaming.getMessages().shareDlg_view();
			
			if ( m_shareRights == ShareRights.CONTRIBUTOR )
				return GwtTeaming.getMessages().shareDlg_contributor();
			
			if ( m_shareRights == ShareRights.OWNER )
				return GwtTeaming.getMessages().shareDlg_owner();
			
			return "Unknown";
		}
		
		/**
		 * 
		 */
		public RecipientType getRecipientType()
		{
			return m_recipientType;
		}
		
		/**
		 * 
		 */
		public String getRecipientTypeAsString()
		{
			if ( m_recipientType == RecipientType.USER )
				return GwtTeaming.getMessages().shareRecipientTypeUser();
			
			if ( m_recipientType == RecipientType.GROUP )
				return GwtTeaming.getMessages().shareRecipientTypeGroup();
			
			if ( m_recipientType == RecipientType.EXTERNAL_USER )
				return GwtTeaming.getMessages().shareRecipientTypeExternalUser();
			
			if ( m_recipientType == RecipientType.TEAM )
				return GwtTeaming.getMessages().shareRecipientTypeTeam();
			
			return GwtTeaming.getMessages().unknownShareType();
		}
		
		/**
		 * 
		 */
		public void setRecipientId( String id )
		{
			m_recipientId = id;
		}
		
		/**
		 * 
		 */
		public void setRecipientName( String name )
		{
			m_recipientName = name;
		}
		
		/**
		 * 
		 */
		public void setShareExpirationValue( ShareExpirationValue value )
		{
			m_shareExpirationValue = new ShareExpirationValue( value );
		}
		
		/**
		 * 
		 */
		public void setShareRights( ShareRights shareRights )
		{
			m_shareRights = shareRights;
		}
		
		/**
		 * 
		 */
		public void setRecipientType( RecipientType type )
		{
			m_recipientType = type;
		}
	}
	
	
	/**
	 * This widget is used to display a recipient's name.  If the recipient is a group
	 * then the user can click on the name and see the members of the group.
	 */
	private class RecipientNameWidget extends Composite
		implements ClickHandler, MouseOverHandler, MouseOutHandler
	{
		private ShareInfo m_shareInfo;
		private InlineLabel m_nameLabel;
		private GroupMembershipPopup m_groupMembershipPopup;
		
		/**
		 * 
		 */
		public RecipientNameWidget( ShareInfo shareInfo )
		{
			FlowPanel panel;
			
			m_shareInfo = shareInfo;
			
			panel = new FlowPanel();
			
			m_nameLabel = new InlineLabel( shareInfo.getRecipientName() );
			panel.add( m_nameLabel );
			
			// If we are dealing with a group, let the user click on the group.
			if ( shareInfo.getRecipientType() == RecipientType.GROUP )
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
															m_shareInfo.getRecipientName(),
															m_shareInfo.getRecipientId() );
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
		private ShareInfo m_shareInfo;
		
		/**
		 * 
		 */
		public ShareExpirationWidget( ShareInfo shareInfo )
		{
			InlineLabel expiresLabel;
			ImageResource imageResource;
			Image img;
			
			m_shareInfo = shareInfo;

			expiresLabel = new InlineLabel( shareInfo.getShareExpirationValueAsString() );
			expiresLabel.addStyleName( "shareThis_ExpiresLabel" );
			expiresLabel.addClickHandler( this );
			
			imageResource = GwtTeaming.getImageBundle().activityStreamActions1();
			img = new Image( imageResource );
			img.getElement().setAttribute( "align", "absmiddle" );
			expiresLabel.getElement().appendChild( img.getElement() );
			
			initWidget( expiresLabel );
		}

		/**
		 * 
		 */
		@Override
		public void onClick( ClickEvent event )
		{
			Window.alert( "Not yet implemented: " + m_shareInfo.getRecipientName() + " expires: " + m_shareInfo.getShareExpirationValueAsString() );
		}
	}
	
	/**
	 * This widget is used to remove a share from the list of shares
	 */
	private class RemoveShareWidget extends Composite
		implements ClickHandler
	{
		private ShareInfo m_shareInfo;
		
		/**
		 * 
		 */
		public RemoveShareWidget( ShareInfo shareInfo )
		{
			FlowPanel panel;
			Image delImg;
			
			m_shareInfo = shareInfo;
			
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
		public ShareInfo getShareInfo()
		{
			return m_shareInfo;
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
					removeShare( m_shareInfo );
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
	private void addShare( ShareInfo shareInfo )
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
				--row;
			}
		}
		
		// Add the share as the first share in the table.
		row = 1;
		m_shareTable.insertRow( row );
		
		// Add the recipient name in the first column.
		m_shareCellFormatter.setColSpan( row, 0, 1 );
		m_shareCellFormatter.setWordWrap( row, 0, false );
		recipientNameWidget = new RecipientNameWidget( shareInfo );
		m_shareTable.setWidget( row, 0,  recipientNameWidget );

		// Add the recipient type in the second column.
		m_shareCellFormatter.setWordWrap( row, 1, false );
		type = shareInfo.getRecipientTypeAsString();
		m_shareTable.setText( row, 1, type );
		
		// Add the share rights in the 3rd column
		{
			ShareRightsWidget accessWidget;
			
			accessWidget = new ShareRightsWidget( shareInfo );
			m_shareTable.setWidget( row, 2, accessWidget );
		}
		
		// Add the expires values in the 4th column
		{
			ShareExpirationWidget expirationWidget;
			
			expirationWidget = new ShareExpirationWidget( shareInfo );
			m_shareTable.setWidget( row, 3, expirationWidget );
		}

		// Add the "remove share" widget to the 5th column.
		{
			removeWidget = new RemoveShareWidget( shareInfo );
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
		
		// Add the "Users" and "Groups" radio buttons
		{
			FlowPanel rbPanel;
			
			mainTable.setText( row, 0, GwtTeaming.getMessages().shareDlg_shareLabel() );

			rbPanel = new FlowPanel();
			rbPanel.addStyleName( "shareThisFindRBPanel" );
			mainTable.setWidget( row, 1, rbPanel );
			mainRowFormatter.setVerticalAlign( row, HasVerticalAlignment.ALIGN_TOP );

			// Add a "Users" radio button.
			{
				ClickHandler clickHandler;

				m_usersRB = new RadioButton( "recipient-type", GwtTeaming.getMessages().shareWithUsers() );
				m_usersRB.setValue( Boolean.TRUE );
				rbPanel.add( m_usersRB );
			
				// Add a click handler for the users rb
				clickHandler = new ClickHandler()
				{
					@Override
					public void onClick( ClickEvent clickEvent )
					{
						Scheduler.ScheduledCommand cmd;
						
						cmd = new Scheduler.ScheduledCommand()
						{
							@Override
							public void execute()
							{
								// Set the filter of the Find Control to only search for users.
								m_findCtrl.setSearchType( SearchType.USER );
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				};
				m_usersRB.addClickHandler( clickHandler );
			}
			
			// Add a "Groups" radio button
			{
				ClickHandler clickHandler;

				m_groupsRB = new RadioButton( "recipient-type", GwtTeaming.getMessages().shareWithGroups() );
				m_groupsRB.addStyleName( "paddingLeft1em" );
				rbPanel.add( m_groupsRB );

				// Add a click handler for the groups rb
				clickHandler = new ClickHandler()
				{
					@Override
					public void onClick( ClickEvent clickEvent )
					{
						Scheduler.ScheduledCommand cmd;
						
						cmd = new Scheduler.ScheduledCommand()
						{
							@Override
							public void execute()
							{
								// Set the filter of the Find Control to only search for groups.
								m_findCtrl.setSearchType( SearchType.GROUP );
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				};
				m_groupsRB.addClickHandler( clickHandler );
			}
			
			++row;
		}

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
				
				imageResource = GwtTeaming.getImageBundle().add_btn();
				addImg = new Image( imageResource );
				addImg.addStyleName( "cursorPointer" );
				addImg.getElement().setAttribute( "title", GwtTeaming.getMessages().shareDlg_addExternalUserTitle() );
				mainTable.setWidget( row, 2, addImg );
				mainCellFormatter.getElement( row, 2 ).getStyle().setPaddingTop( 10, Unit.PX );
		
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
		
		// Add the expiration controls
		{
			ImageResource imageResource;
			Image img;
			ClickHandler clickHandler;
			
			mainTable.setText( row, 0, GwtTeaming.getMessages().shareDlg_expiresLabel() );
		
			m_expiresLabel = new InlineLabel( GwtTeaming.getMessages().shareDlg_expiresNever() );
			m_expiresLabel.addStyleName( "shareThis_DefaultExpiresLabel" );

			imageResource = GwtTeaming.getImageBundle().activityStreamActions1();
			img = new Image( imageResource );
			img.getElement().setAttribute( "align", "absmiddle" );
			m_expiresLabel.getElement().appendChild( img.getElement() );

			m_defaultShareExpirationValue = new ShareExpirationValue();
			m_defaultShareExpirationValue.setType( ShareExpirationType.NEVER );
			
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
			HTMLTable.RowFormatter rowFormatter;
			
			m_shareTablePanel = new FlowPanel();
			
			m_shareTable = new FlexTable();
			m_shareTable.addStyleName( "shareThisRecipientTable" );
			m_shareTable.setCellSpacing( 0 );

			// Add the column headers.
			{
				m_shareTable.setText( 0, 0, GwtTeaming.getMessages().shareName() );
				m_shareTable.setText( 0, 1, GwtTeaming.getMessages().shareRecipientType() );
				m_shareTable.setText( 0, 2, GwtTeaming.getMessages().shareAccess() );
				m_shareTable.setText( 0, 3, GwtTeaming.getMessages().shareExpires() );
				m_shareTable.setHTML( 0, 4, "&nbsp;" );	// The delete image will go in this column.
				
				rowFormatter = m_shareTable.getRowFormatter();
				rowFormatter.addStyleName( 0, "oltHeader" );

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
		ArrayList<String> principalIds;
		ArrayList<String> teamIds;
		
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
					ArrayList<GwtUser> usersWithoutReadRights;
					String[] errorMessages;
					FlowPanel errorPanel;
					
					boolean haveErrors;
					
					haveErrors = false;
					
					// Get the panel that holds the errors.
					errorPanel = getErrorPanel();
					errorPanel.clear();
					
					// Are there any recipients that did not have read rights to the entry?
					usersWithoutReadRights = result.getUsersWithoutReadRights();
					if ( usersWithoutReadRights != null && usersWithoutReadRights.size() > 0 )
					{
						// Yes
						haveErrors = true;

						// Add a message that tells the user that some of the recipients don't
						// have rights to this entry.
						{
							Label label;
							
							label = new Label( GwtTeaming.getMessages().usersWithoutRights() );
							label.addStyleName( "dlgErrorLabel" );
							errorPanel.add( label );
							
							for ( GwtUser nextUser : usersWithoutReadRights )
							{
								label = new Label( nextUser.getShortDisplayName() );
								label.addStyleName( "bulletListItem" );
								errorPanel.add( label );
							}
							
							// Add some space.
							{
								FlowPanel spacerPanel;
								
								spacerPanel = new FlowPanel();
								spacerPanel.addStyleName( "paddingTop8px" );
								errorPanel.add( spacerPanel );
							}
						}
					}
					
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
		
		// Get the ids of the users and groups we should send an email to.
		principalIds = getRecipientIds();
		
		// Get the ids of the teams we should send an email to.
		teamIds = getTeamIds();
		
		// Did the user specify any recipients or teams to send to?
		if ( (principalIds != null && principalIds.size() > 0) || (teamIds != null && teamIds.size() > 0) )
		{
			// Yes
			// Issue an ajax request to share the entities.
			ShareEntryCmd cmd = new ShareEntryCmd( m_entityIds, comment, principalIds, teamIds );
			GwtClientHelper.executeCommand( cmd, m_shareEntryCallback );
		}
		else
		{
			// No, tell the user to specify at least one recipient.
			Window.alert( GwtTeaming.getMessages().noShareRecipientsOrTeams() );
		}
		
		// Returning false will prevent the dialog from closing.  We will close
		// the dialog when we get the response back from our ajax request.
		return false;
	}

	/**
	 * Find the given recipient in the table that holds the recipients.
	 */
	private int findShareByRecipientName( ShareInfo shareInfo )
	{
		int i;
		String name;
		RecipientType type;
		
		name = shareInfo.getRecipientName();
		type = shareInfo.getRecipientType();
		
		// Look through the table for the given recipient.
		// Recipients start in row 1.
		for (i = 1; i < m_shareTable.getRowCount() && m_shareTable.getCellCount( i ) > 4; ++i)
		{
			Widget widget;
			
			// Get the RemoveRecipientWidget from the 5 column.
			widget = m_shareTable.getWidget( i, 4 );
			if ( widget != null && widget instanceof RemoveShareWidget )
			{
				ShareInfo nextShareInfo;
				
				nextShareInfo = ((RemoveShareWidget) widget).getShareInfo();
				if ( nextShareInfo != null )
				{
					if ( type == nextShareInfo.getRecipientType() )
					{
						if ( name != null && name.equalsIgnoreCase( nextShareInfo.getRecipientName() ) )
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
		//!!! Finish
		return Boolean.TRUE;
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
	 * Return the text the user has entered for the message.
	 */
	public String getMsg()
	{
		return m_msgTextArea.getText();
	}
	
	/**
	 * Return the ids of each of the recipients (users and groups)
	 */
	private ArrayList<String> getRecipientIds()
	{
		ArrayList<String> recipientIds;
		int i;
		
		recipientIds = new ArrayList<String>();
		
		// Look through the table for the given recipient.
		// Recipients start in row 1.
		for (i = 1; i < m_shareTable.getRowCount(); ++i)
		{
			Widget widget;
			
			if ( m_shareTable.getCellCount( i ) > 2 )
			{
				// Get the RemoveRecipientWidget from the 5 column.
				widget = m_shareTable.getWidget( i, 4 );
				if ( widget != null && widget instanceof RemoveShareWidget )
				{
					ShareInfo nextShareInfo;
					
					nextShareInfo = ((RemoveShareWidget) widget).getShareInfo();
					if ( nextShareInfo != null )
					{
						recipientIds.add( nextShareInfo.getRecipientId() );
					}
				}
			}
		}
		
		return recipientIds;
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
			ShareInfo shareInfo;
			
			// Clear what the user has typed.
			m_findCtrl.clearText();
			
			shareInfo = new ShareInfo();
			shareInfo.setRecipientName( emailAddress );
			shareInfo.setRecipientType( RecipientType.EXTERNAL_USER );
			shareInfo.setShareRights( getSelectedShareRights() );
			shareInfo.setShareExpirationValue( m_defaultShareExpirationValue );
			//!!! Finish
			
			// Is this external user already in the list?
			if ( findShareByRecipientName( shareInfo ) == -1 )
			{
				// No, add it
				addShare( shareInfo );
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
		Window.alert( "Not yet implemented" );
	}
	
	/**
	 * 
	 */
	private void init()
	{
		GetMyTeamsCmd cmd;
		
		if ( GwtClientHelper.hasString( m_title ))
		{
			m_titleTextBox.setVisible( true    );
			m_titleTextBox.setText(    m_title );
		}
		else
		{
			m_titleTextBox.setVisible( false );
		}

		m_msgTextArea.setText( "" );
		m_findCtrl.setInitialSearchString( "" );
		m_usersRB.setValue( Boolean.TRUE );
		m_groupsRB.setValue( Boolean.FALSE );

		// Set the filter of the Find Control to only search for users.
		m_findCtrl.setSearchType( SearchType.USER );

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
					List<TeamInfo> listOfTeams;
					GetMyTeamsRpcResponseData responseData;
					
					responseData = (GetMyTeamsRpcResponseData) response.getResponseData();
					listOfTeams = responseData.getTeams();
					
					// Update the dialog with the list of teams.
					updateListOfTeams( listOfTeams );
				}
			};
		}
		
		// Issue an rpc request to get the teams this user is a member of.
		cmd = new GetMyTeamsCmd();
		GwtClientHelper.executeCommand( cmd, m_readTeamsCallback );
	}

	/**
	 * Remove the given share from the table
	 */
	public void removeShare( ShareInfo shareInfo )
	{
		int row;
		
		// Find the row this share lives in.
		row = findShareByRecipientName( shareInfo );
		
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
				ShareInfo shareInfo = null;
				
				// Are we dealing with a User?
				if ( selectedObj instanceof GwtUser )
				{
					GwtUser user;
					
					// Yes
					user = (GwtUser) selectedObj;
					
					shareInfo = new ShareInfo();
					shareInfo.setRecipientName( user.getShortDisplayName() );
					shareInfo.setRecipientType( RecipientType.USER );
					shareInfo.setRecipientId( user.getUserId() );
				}
				// Are we dealing with a group?
				else if ( selectedObj instanceof GwtGroup )
				{
					GwtGroup group;
					
					// Yes
					group = (GwtGroup) selectedObj;
					
					shareInfo = new ShareInfo();
					shareInfo.setRecipientName( group.getShortDisplayName() );
					shareInfo.setRecipientType( RecipientType.GROUP );
					shareInfo.setRecipientId( group.getId() );
				}

				// Do we have an object to add to our list of shares?
				if ( shareInfo != null )
				{
					// Yes
					// Has the item already been shared with the recipient
					if ( findShareByRecipientName( shareInfo ) == -1 )
					{
						// No
						shareInfo.setShareRights( getSelectedShareRights() );
						shareInfo.setShareExpirationValue( m_defaultShareExpirationValue );
						
						// Add the recipient to our list of recipients
						addShare( shareInfo );
					}
					else
					{
						// Yes, tell the user
						Window.alert( GwtTeaming.getMessages().shareDlg_alreadySharedWithSelectedRecipient( shareInfo.getRecipientName() ) );
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
		m_title     = title;
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
		
		// No!  Add a textbox for the title
		inputPanel = new FlowPanel();
		inputPanel.addStyleName( "shareThisTitlePanel" );
		m_titleTextBox = new TextBox();
		m_titleTextBox.addStyleName( "shareThisTitleTextBox" );
		inputPanel.add( m_titleTextBox );
		m_mainPanel.add( inputPanel );
		
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

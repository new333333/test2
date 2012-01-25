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
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.FindCtrl;
import org.kablink.teaming.gwt.client.widgets.FindCtrl.FindCtrlClient;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;


/**
 * This class is used to present a UI the user can use to share an item with
 * users, groups and teams.
 * @author jwootton
 *
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
	private FindCtrl m_findCtrl;
	private ImageResource m_deleteImgR;
	private FlexTable m_recipientTable;
	private FlowPanel m_shareWithTeamsPanel;
	private FlowPanel m_myTeamsPanel;
	private FlowPanel m_recipientTablePanel;
	private FlexCellFormatter m_cellFormatter;
	private String m_entryId;
	private AsyncCallback<VibeRpcResponse> m_readTeamsCallback;
	private AsyncCallback<VibeRpcResponse> m_shareEntryCallback;

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
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
	 * This class represents the different types of recipients.
	 */
	public enum RecipientType implements IsSerializable
	{
		USER,
		GROUP,
		
		UNKNOWN,
	}

	
	/**
	 * This class is used to hold information about a recipient
	 */
	private class RecipientInfo
	{
		private String m_recipientName;
		private String m_id;
		private RecipientType m_type;
		
		/**
		 * 
		 */
		public RecipientInfo()
		{
			m_recipientName = null;
			m_id = null;
			m_type = RecipientType.UNKNOWN;
		}
		
		/**
		 * 
		 */
		public String getId()
		{
			return m_id;
		}
		
		/**
		 * 
		 */
		public String getName()
		{
			return m_recipientName;
		}
		
		/**
		 * 
		 */
		public RecipientType getType()
		{
			return m_type;
		}
		
		/**
		 * 
		 */
		public void setId( String id )
		{
			m_id = id;
		}
		
		/**
		 * 
		 */
		public void setName( String name )
		{
			m_recipientName = name;
		}
		
		/**
		 * 
		 */
		public void setType( RecipientType type )
		{
			m_type = type;
		}
	}
	
	
	/**
	 * This widget is used to display a recipient's name.  If the recipient is a group
	 * then the user can click on the name and see the members of the group.
	 */
	private class RecipientNameWidget extends Composite
		implements ClickHandler, MouseOverHandler, MouseOutHandler
	{
		private RecipientInfo m_recipientInfo;
		private InlineLabel m_nameLabel;
		private GroupMembershipPopup m_groupMembershipPopup;
		
		/**
		 * 
		 */
		public RecipientNameWidget( RecipientInfo recipientInfo )
		{
			FlowPanel panel;
			
			m_recipientInfo = recipientInfo;
			
			panel = new FlowPanel();
			
			m_nameLabel = new InlineLabel( recipientInfo.getName() );
			panel.add( m_nameLabel );
			
			// If we are dealing with a group, let the user click on the group.
			if ( recipientInfo.getType() == RecipientType.GROUP )
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
		public void onClick( ClickEvent event )
		{
			// Create a popup that will display the membership of this group.
			if ( m_groupMembershipPopup == null )
			{
				m_groupMembershipPopup = new GroupMembershipPopup(
															false,
															false,
															m_recipientInfo.getName(),
															m_recipientInfo.getId() );
			}
			
			m_groupMembershipPopup.setPopupPosition( getAbsoluteLeft(), getAbsoluteTop() );
			m_groupMembershipPopup.show();
		}
		
		/**
		 * Remove the mouse-over style from the name. 
		 */
		public void onMouseOut( MouseOutEvent event )
		{
			m_nameLabel.removeStyleName( "shareThisDlgNameHover" );
		}

		
		/**
		 * Add the mouse-over style to the name.
		 */
		public void onMouseOver( MouseOverEvent event )
		{
			m_nameLabel.addStyleName( "shareThisDlgNameHover" );
		}
	}
	
	/**
	 * This widget is used to remove a recipient from the list of recipients
	 */
	private class RemoveRecipientWidget extends Composite
		implements ClickHandler
	{
		private RecipientInfo m_recipientInfo;
		
		/**
		 * 
		 */
		public RemoveRecipientWidget( RecipientInfo recipientInfo )
		{
			FlowPanel panel;
			Image delImg;
			
			m_recipientInfo = recipientInfo;
			
			panel = new FlowPanel();
			
			delImg = new Image( m_deleteImgR );
			delImg.addStyleName( "cursorPointer" );
			delImg.getElement().setAttribute( "title", GwtTeaming.getMessages().removeRecipientHint() );
			delImg.addClickHandler( this );
			
			panel.add( delImg );
			
			// All composites must call initWidget() in their constructors.
			initWidget( panel );
		}
		
		/**
		 * 
		 */
		public RecipientInfo getRecipientInfo()
		{
			return m_recipientInfo;
		}

		/**
		 * This gets called when the user clicks on the remove recipient image.
		 */
		public void onClick( ClickEvent event )
		{
			removeRecipient( m_recipientInfo );
		}
	}
	
	
	/**
	 * 
	 */
	public ShareThisDlg(
		boolean autoHide,
		boolean modal,
		int left,
		int top,
		String dlgCaption )
	{
		// Initialize the superclass
		super( autoHide, modal, left, top );

		// Register the events to be handled by this class.
		EventHelper.registerEventHandlers(
			GwtTeaming.getEventBus(),
			m_registeredEvents,
			this);
		
		// Create the dialog's content
		createAllDlgContent(
			dlgCaption,
			this,	// EditSuccessfulHandler
			this,	// EditCanceledHandler
			null );
	}

	/**
	 * Add the "No recipients have been selected" text to the table
	 * that holds the list of recipients.
	 */
	private void addNoRecipientsMessage()
	{
		int row;
		
		row = 1;
		m_cellFormatter.setColSpan( row, 0, 3 );
		m_cellFormatter.addStyleName( row, 0, "oltBorderLeft" );
		m_cellFormatter.addStyleName( row, 0, "oltBorderRight" );
		m_cellFormatter.addStyleName( row, 0, "oltContentPadding" );
		m_cellFormatter.addStyleName( row, 0, "oltLastRowBorderBottom" );

		m_recipientTable.setText( row, 0, GwtTeaming.getMessages().noShareRecipients() );
	}
	
	
	/**
	 * Add the given recipient to the end of the table that holds the list of recipients.
	 */
	private void addRecipient( RecipientInfo recipientInfo )
	{
		String type;
		int row;
		RemoveRecipientWidget removeWidget;
		RecipientNameWidget recipientNameWidget;
		
		row = m_recipientTable.getRowCount();
		
		// Do we have any recipients in the table?
		if ( row == 2 )
		{
			String text;
			
			// Maybe
			// The first row might be the message, "No recipients have been selected"
			// Get the text from the first row.
			text = m_recipientTable.getText( 1, 0 );
			
			// Does the first row contain a message?
			if ( text != null && text.equalsIgnoreCase( GwtTeaming.getMessages().noShareRecipients() ) )
			{
				// Yes
				m_recipientTable.removeRow( 1 );
				--row;
			}
		}
		
		// Add the recipient as the first recipient in the table.
		row = 1;
		m_recipientTable.insertRow( row );
		
		// Add the recipient name in the first column.
		m_cellFormatter.setColSpan( row, 0, 1 );
		recipientNameWidget = new RecipientNameWidget( recipientInfo );
		m_recipientTable.setWidget( row, 0,  recipientNameWidget );

		// Add the recipient type in the second column.
		if ( recipientInfo.getType() == RecipientType.USER )
			type = GwtTeaming.getMessages().shareTypeUser();
		else if ( recipientInfo.getType() == RecipientType.GROUP )
			type = GwtTeaming.getMessages().shareTypeGroup();
		else
			type = GwtTeaming.getMessages().unknownShareType();
		m_recipientTable.setText( row, 1, type );

		// Add the "remove recipient" widget to the 3rd column.
		removeWidget = new RemoveRecipientWidget( recipientInfo );
		m_recipientTable.setWidget( row, 2, removeWidget );

		// Add the necessary styles to the cells in the row.
		m_cellFormatter.addStyleName( row, 0, "oltBorderLeft" );
		m_cellFormatter.addStyleName( row, 2, "oltBorderRight" );
		m_cellFormatter.addStyleName( row, 0, "oltContentBorderBottom" );
		m_cellFormatter.addStyleName( row, 1, "oltContentBorderBottom" );
		m_cellFormatter.addStyleName( row, 2, "oltContentBorderBottom" );
		m_cellFormatter.addStyleName( row, 0, "oltContentPadding" );
		m_cellFormatter.addStyleName( row, 1, "oltContentPadding" );
		m_cellFormatter.addStyleName( row, 2, "oltContentPadding" );
		
		adjustRecipientTablePanelHeight();
	}
	
	
	/**
	 * 
	 */
	private void adjustRecipientTablePanelHeight()
	{
		Scheduler.ScheduledCommand cmd;
		
		cmd = new Scheduler.ScheduledCommand()
		{
			public void execute()
			{
				int height;
				
				// Get the height of the table that holds the list of recipients.
				height = m_recipientTable.getOffsetHeight();
				
				// If the height is greater than 150 pixels put an overflow auto on the panel
				// and give the panel a fixed height of 150 pixels.
				if ( height >= 150 )
					m_recipientTablePanel.addStyleName( "shareThisRecipientTablePanelHeight" );
				else
					m_recipientTablePanel.removeStyleName( "shareThisRecipientTablePanelHeight" );
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
			public void execute()
			{
				int height;
				
				// Get the height of the panel that holds the list of recipients.
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
	public Panel createContent( Object callbackData )
	{
		FlowPanel mainPanel;
		FlowPanel inputPanel;
		FlowPanel recipientPanel;
		Label comments;
		
		mainPanel = new FlowPanel();
		mainPanel.addStyleName( "teamingDlgBoxContent" );
		mainPanel.addStyleName( "dlgContent" );
		
		// Add a textbox for the title
		inputPanel = new FlowPanel();
		inputPanel.addStyleName( "shareThisTitlePanel" );
		m_titleTextBox = new TextBox();
		m_titleTextBox.addStyleName( "shareThisTitleTextBox" );
		inputPanel.add( m_titleTextBox );
		mainPanel.add( inputPanel );
		
		// Add the controls for defining the recipients
		recipientPanel = createRecipientControls();
		mainPanel.add( recipientPanel );
		
		// Add a "Comments:" label before the textbox.
		comments = new Label( GwtTeaming.getMessages().commentsLabel() );
		comments.addStyleName( "shareThisCommentsLabel" );
		mainPanel.add( comments );
		
		// Create a textbox
		inputPanel = new FlowPanel();
		m_msgTextArea = new TextArea();
		m_msgTextArea.addStyleName( "shareThisTextArea" );
		m_msgTextArea.addStyleName( "shareThisTextAreaBorder" );
		inputPanel.add( m_msgTextArea );
		mainPanel.add( inputPanel );
		
		return mainPanel;
	}
	
	/**
	 * Create all the controls needed to identify who the recipients are.
	 */
	private FlowPanel createRecipientControls()
	{
		FlowPanel mainPanel;
		ClickHandler clickHandler;
		
		// Create a panel for all of the controls dealing with the recipients.
		mainPanel = new FlowPanel();
		
		// Add a "Users" radio button.
		{
			m_usersRB = new RadioButton( "recipient-type", GwtTeaming.getMessages().shareWithUsers() );
			m_usersRB.setValue( Boolean.TRUE );
			mainPanel.add( m_usersRB );
		
			// Add a click handler for the users rb
			clickHandler = new ClickHandler()
			{
				public void onClick( ClickEvent clickEvent )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
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
			m_groupsRB = new RadioButton( "recipient-type", GwtTeaming.getMessages().shareWithGroups() );
			m_groupsRB.addStyleName( "paddingLeft1em" );
			mainPanel.add( m_groupsRB );

			// Add a click handler for the groups rb
			clickHandler = new ClickHandler()
			{
				public void onClick( ClickEvent clickEvent )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
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

		// Add the find control.
		{
			HTMLTable.RowFormatter rowFormatter;

			final FlexTable table = new FlexTable();
			rowFormatter = table.getRowFormatter();
			rowFormatter.setVerticalAlign( 0, HasVerticalAlignment.ALIGN_TOP );
			mainPanel.add( table );
			
			FindCtrl.createAsync(
					this,
					GwtSearchCriteria.SearchType.USER,
					new FindCtrlClient() {				
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}// onUnavailable()
				
				@Override
				public void onSuccess( FindCtrl findCtrl )
				{
					m_findCtrl = findCtrl;
					m_findCtrl.setIsSendingEmail( true );
					table.setWidget( 0, 0, m_findCtrl );
				}// end onSuccess()
			} );
		}
		
		// Add some space
		{
			FlowPanel spacerPanel;
			
			spacerPanel = new FlowPanel();
			spacerPanel.addStyleName( "paddingTop8px" );
			mainPanel.add( spacerPanel );
		}
		
		// Create a table to hold the list of recipients
		{
			HTMLTable.RowFormatter rowFormatter;
			
			m_recipientTablePanel = new FlowPanel();
			m_recipientTablePanel.addStyleName( "shareThisRecipientTablePanel" );
			
			m_recipientTable = new FlexTable();
			m_recipientTable.addStyleName( "shareThisRecipientTable" );
			m_recipientTable.setCellSpacing( 0 );

			// Add the column headers.
			{
				m_recipientTable.setText( 0, 0, GwtTeaming.getMessages().shareName() );
				m_recipientTable.setText( 0, 1, GwtTeaming.getMessages().shareType() );
				m_recipientTable.setHTML( 0, 2, "&nbsp;" );	// The delete image will go in this column.
				
				rowFormatter = m_recipientTable.getRowFormatter();
				rowFormatter.addStyleName( 0, "oltHeader" );

				m_cellFormatter = m_recipientTable.getFlexCellFormatter();
				// On IE calling m_cellFormatter.setWidth( 0, 2, "*" ); throws an exception.
				// That is why we are calling DOM.setElementAttribute(...) instead.
				//!!!m_cellFormatter.setWidth( 0, 2, "*" );
				DOM.setElementAttribute( m_cellFormatter.getElement( 0, 2 ), "width", "*" );
				
				m_cellFormatter.addStyleName( 0, 0, "oltBorderLeft" );
				m_cellFormatter.addStyleName( 0, 0, "oltHeaderBorderTop" );
				m_cellFormatter.addStyleName( 0, 0, "oltHeaderBorderBottom" );
				m_cellFormatter.addStyleName( 0, 0, "oltHeaderPadding" );
				m_cellFormatter.addStyleName( 0, 1, "oltHeaderBorderTop" );
				m_cellFormatter.addStyleName( 0, 1, "oltHeaderBorderBottom" );
				m_cellFormatter.addStyleName( 0, 1, "oltHeaderPadding" );
				m_cellFormatter.addStyleName( 0, 2, "oltBorderRight" );
				m_cellFormatter.addStyleName( 0, 2, "oltHeaderBorderTop" );
				m_cellFormatter.addStyleName( 0, 2, "oltHeaderBorderBottom" );
				m_cellFormatter.addStyleName( 0, 2, "oltHeaderPadding" );
			}
			
			m_recipientTablePanel.add( m_recipientTable );
			mainPanel.add( m_recipientTablePanel );
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
	public boolean editCanceled()
	{
		int i;
		
		// Go through the list of recipients and close any "Group Membership" popups that may be open.
		for (i = 1; i < m_recipientTable.getRowCount(); ++i)
		{
			Widget widget;
			
			if ( m_recipientTable.getCellCount( i ) > 2 )
			{
				// Get the RecipientNameWidget from the first column.
				widget = m_recipientTable.getWidget( i, 0 );
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
			// Issue an ajax request to send the email.
			ShareEntryCmd cmd = new ShareEntryCmd( m_entryId, comment, principalIds, teamIds );
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
		return m_findCtrl.getFocusWidget();
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
		for (i = 1; i < m_recipientTable.getRowCount(); ++i)
		{
			Widget widget;
			
			if ( m_recipientTable.getCellCount( i ) > 2 )
			{
				// Get the RemoveRecipientWidget from the 3 column.
				widget = m_recipientTable.getWidget( i, 2 );
				if ( widget != null && widget instanceof RemoveRecipientWidget )
				{
					RecipientInfo nextRecipientInfo;
					
					nextRecipientInfo = ((RemoveRecipientWidget) widget).getRecipientInfo();
					if ( nextRecipientInfo != null )
					{
						recipientIds.add( nextRecipientInfo.getId() );
					}
				}
			}
		}
		
		return recipientIds;
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
	 * Find the given recipient in the table that holds the recipients.
	 */
	private int findRecipientInTable( RecipientInfo recipientInfo )
	{
		int i;
		String name;
		RecipientType type;
		
		name = recipientInfo.getName();
		type = recipientInfo.getType();
		
		// Look through the table for the given recipient.
		// Recipients start in row 1.
		for (i = 1; i < m_recipientTable.getRowCount(); ++i)
		{
			Widget widget;
			
			// Get the RemoveRecipientWidget from the 3 column.
			widget = m_recipientTable.getWidget( i, 2 );
			if ( widget != null && widget instanceof RemoveRecipientWidget )
			{
				RecipientInfo nextRecipientInfo;
				
				nextRecipientInfo = ((RemoveRecipientWidget) widget).getRecipientInfo();
				if ( nextRecipientInfo != null )
				{
					if ( type == nextRecipientInfo.getType() )
					{
						if ( name != null && name.equalsIgnoreCase( nextRecipientInfo.getName() ) )
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
	 * 
	 */
	private void init( String title, String entryId )
	{
		GetMyTeamsCmd cmd;
		
		m_titleTextBox.setText( title );
		m_msgTextArea.setText( "" );
		m_entryId = entryId;
		m_findCtrl.setInitialSearchString( "" );
		m_usersRB.setValue( Boolean.TRUE );
		m_groupsRB.setValue( Boolean.FALSE );

		// Set the filter of the Find Control to only search for users.
		m_findCtrl.setSearchType( SearchType.USER );

		// Remove all of the rows from the table.
		// We start at row 1 so we don't delete the header.
		while ( m_recipientTable.getRowCount() > 1 )
		{
			// Remove the 1st row that holds recipient information.
			m_recipientTable.removeRow( 1 );
		}
		
		// Add a message to the table telling the user no recipients have been selected.
		addNoRecipientsMessage();

		adjustRecipientTablePanelHeight();
		
		if ( m_readTeamsCallback == null )
		{
			// Create a callback that will be used when we read the teams the user is a member of
			m_readTeamsCallback = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
							t,
							GwtTeaming.getMessages().rpcFailure_GetMyTeams() );
				}
				
				/**
				 * 
				 */
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
	 * Remove the given recipient from the table
	 */
	public void removeRecipient( RecipientInfo recipientInfo )
	{
		int row;
		
		// Find the row this recipient lives in.
		row = findRecipientInTable( recipientInfo );
		
		// Did we find the recipient in the table?
		if ( row > 0 )
		{
			// Yes
			// Remove the recipient from the table.
			m_recipientTable.removeRow( row );
			
			// Did we remove the last recipient from the table?
			if ( m_recipientTable.getRowCount() == 1 )
			{
				// Yes
				// Add the "no recipients..." message to the table.
				addNoRecipientsMessage();
			}
			
			adjustRecipientTablePanelHeight();
		}
	}
	
	
	/**
	 * 
	 */
	public void showDlg( String title, String entryId, final int right, final int top )
	{
		PopupPanel.PositionCallback posCallback;
		
		init( title, entryId );
		
		hideErrorPanel();
		showContentPanel();
		createFooterButtons( DlgBox.DlgButtonMode.OkCancel );
		
		posCallback = new PopupPanel.PositionCallback()
		{
			/**
			 * 
			 */
			public void setPosition( int offsetWidth, int offsetHeight )
			{
				int x;
				
				x = right - offsetWidth;
				setPopupPosition( x, top );
			}
		};
		setPopupPositionAndShow( posCallback );
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
	 * Handles SearchFindResultsEvent's received by this class.
	 * 
	 * Implements the SearchFindResultsEvent.Handler.onSearchFindResults() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSearchFindResults( SearchFindResultsEvent event )
	{
		// If the find results aren't for this share this dialog...
		if ( !((Widget) event.getSource()).equals( this ) )
		{
			// ...ignore the event.
			return;
		}
		
		// Are we dealing with a User?
		RecipientInfo recipientInfo = null;
		GwtTeamingItem selectedObj = event.getSearchResults();
		if ( selectedObj instanceof GwtUser )
		{
			GwtUser user;
			
			// Yes
			user = (GwtUser) selectedObj;
			
			recipientInfo = new RecipientInfo();
			recipientInfo.setName( user.getShortDisplayName() );
			recipientInfo.setType( RecipientType.USER );
			recipientInfo.setId( user.getUserId() );
		}
		// Are we dealing with a group?
		else if ( selectedObj instanceof GwtGroup )
		{
			GwtGroup group;
			
			// Yes
			group = (GwtGroup) selectedObj;
			
			recipientInfo = new RecipientInfo();
			recipientInfo.setName( group.getShortDisplayName() );
			recipientInfo.setType( RecipientType.GROUP );
			recipientInfo.setId( group.getId() );
		}

		// Do we have an object to add to our list of recipients?
		if ( recipientInfo != null )
		{
			// Yes
			
			// Add the recipient to our list of recipients
			addRecipient( recipientInfo );
			
			// Hide the search-results widget.
			m_findCtrl.hideSearchResults();
			
			// Clear the text from the find control.
			m_findCtrl.clearText();
		}
	}// end onSearchFindResults()
}

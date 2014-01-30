/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.datatable.ShareItemCell;
import org.kablink.teaming.gwt.client.datatable.VibeCellTable;
import org.kablink.teaming.gwt.client.datatable.VibeCheckboxCell;
import org.kablink.teaming.gwt.client.event.ContentChangedEvent;
import org.kablink.teaming.gwt.client.event.ContentChangedEvent.Change;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.InvokeEditShareRightsDlgEvent;
import org.kablink.teaming.gwt.client.event.SearchFindResultsEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.GwtGroup;
import org.kablink.teaming.gwt.client.GwtPublic;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtSearchCriteria.SearchType;
import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtSendShareNotificationEmailResults;
import org.kablink.teaming.gwt.client.GwtShareEntryResults;
import org.kablink.teaming.gwt.client.GwtShareItemResult;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.mainmenu.TeamInfo;
import org.kablink.teaming.gwt.client.rpc.shared.FindUserByEmailAddressCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetEntryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetMyTeamsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetMyTeamsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetSharingInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SendShareNotificationEmailCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ValidateEmailAddressCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ValidateEmailRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ValidateEmailRpcResponseData.EmailAddressStatus;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.rpc.shared.ShareEntryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ShareEntryResultsRpcResponseData;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.GwtPublicShareItem;
import org.kablink.teaming.gwt.client.util.GwtRecipientType;
import org.kablink.teaming.gwt.client.util.GwtShareItem;
import org.kablink.teaming.gwt.client.util.GwtSharingInfo;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue.ShareExpirationType;
import org.kablink.teaming.gwt.client.util.ShareRights;
import org.kablink.teaming.gwt.client.util.ShareRights.AccessRights;
import org.kablink.teaming.gwt.client.util.UserType;
import org.kablink.teaming.gwt.client.widgets.FindCtrl;
import org.kablink.teaming.gwt.client.widgets.FindCtrl.FindCtrlClient;
import org.kablink.teaming.gwt.client.widgets.ShareSendToWidget.SendToValue;
import org.kablink.teaming.gwt.client.widgets.ShareWithTeamsDlg.ShareWithTeamsDlgClient;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * This class is used to present a UI the user can use to share an item with
 * users, groups and teams.
 * 
 * This is a 2nd iteration of this UI and replaces ShareThisDlg.java
 * 
 * @author jwootton
 */
public class ShareThisDlg2 extends DlgBox
	implements EditSuccessfulHandler, EditCanceledHandler,
	// Event handlers implemented by this class.
		InvokeEditShareRightsDlgEvent.Handler,
		SearchFindResultsEvent.Handler
{
	private CellTable<GwtShareItem> m_shareTable;
    private MultiSelectionModel<GwtShareItem> m_selectionModel;
	private ListDataProvider<GwtShareItem> m_dataProvider;
	private VibeSimplePager m_pager;
	private SelectAllHeader m_selectAllHeader;
	private ArrayList<GwtShareItem> m_listOfShares;

	private ShareThisDlgMode m_mode;
	private Image m_headerImg;
	private Label m_headerNameLabel;
	private Label m_headerPathLabel;
	private FindCtrl m_findCtrl;
	private FindCtrl m_manageSharesFindCtrl;
	private Image m_addExternalUserImg;
	private FlowPanel m_mainPanel;
	private FlexTable m_addShareTable;
	private InlineLabel m_shareWithTeamsLabel;
	private InlineLabel m_manageSharesFindCtrlLabel;
	private ShareSendToWidget m_sendToWidget;
	private FlowPanel m_makePublicPanel;
	private FlowPanel m_manageShareItemsPanel;
	private FlowPanel m_editSharePanel;
	private ListBox m_findByListbox;
	private List<EntityId> m_entityIds;
	private GwtSharingInfo m_sharingInfo;		// Holds all of the sharing info for the entities we are working with.
	private List<TeamInfo> m_listOfTeams;
	private List<HandlerRegistration> m_registeredEventHandlers;
	private AsyncCallback<VibeRpcResponse> m_readTeamsCallback;
	private AsyncCallback<VibeRpcResponse> m_shareEntryCallback;
	private AsyncCallback<VibeRpcResponse> m_getSharingInfoCallback;
	private AsyncCallback<VibeRpcResponse> m_sendNotificationEmailCallback;
	private ShareExpirationValue m_defaultShareExpirationValue;
	private ShareWithTeamsDlg m_shareWithTeamsDlg;
	private EditShareWidget m_editShareWidget;
	private EditSuccessfulHandler m_editShareHandler;
	private EditSuccessfulHandler m_editShareWithTeamsHandler;
	
	private static final String FIND_SHARES_BY_USER = "by-user";
	private static final String FIND_SHARES_BY_FILE = "by-file";
	private static final String FIND_SHARES_BY_FOLDER = "by-folder";
	private static final String FIND_ALL_SHARES = "find-all";
	private static final String FIND_SHARES_BY_HINT = "by-hint";

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[]
    {
		// Search events.
		TeamingEvents.INVOKE_EDIT_SHARE_RIGHTS_DLG,
		TeamingEvents.SEARCH_FIND_RESULTS,
	};

	/**
	 * ShareThisDlgMode determines how the ShareThisDlg behaves.
	 * ShareThisDlgMode.NORMAL:
	 * 	User can see only the shares he has created
	 * 	User can add/remove/modify shares
	 * 
	 * ShareThisDlgMode.MANAGE_SELECTED:
	 * 	User will see all shares that have been created by anyone for the given entities.
	 *  User can remove/modify shares but cannot add shares
	 *   
	 */
	public enum ShareThisDlgMode
	{
		NORMAL,
		MANAGE_ALL,
		MANAGE_SELECTED
	}
	

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ShareThisDlg2(
		boolean autoHide,
		boolean modal,
		int x,
		int y,
		Integer width,
		Integer height,
		ShareThisDlgMode mode )
	{
		// Initialize the superclass.
		super( autoHide, modal, x, y, width, height, DlgButtonMode.OkCancel );

		m_mode = mode;
		
		// Create the dialog's content
		createAllDlgContent(
			"",		// // No caption yet.  It's set appropriately when the dialog runs.
			this,	// EditSuccessfulHandler
			this,	// EditCanceledHandler
			null );
	}

	/**
	 * Add the given share to the end of the table that holds the list of shares
	 */
	private void addShare( GwtShareItem shareItem, boolean highlight )
	{
		if ( m_listOfShares == null )
			m_listOfShares = new ArrayList<GwtShareItem>();

		m_listOfShares.add( 0, shareItem );
		
		m_dataProvider.refresh();
		
		// Go to the first page
		m_pager.firstPage();

		// Tell the table how many groups we have.
		m_shareTable.setRowCount( m_listOfShares.size(), true );
	}
	
	/**
	 * Add a share for the given GwtTeamingItem
	 */
	private ArrayList<GwtShareItem> addShare( GwtTeamingItem gwtTeamingItem )
	{
		ArrayList<GwtShareItem> returnValue;
		
		if ( gwtTeamingItem == null )
			return null;
		
		returnValue = new ArrayList<GwtShareItem>();
		
		for ( EntityId nextEntityId : m_entityIds )
		{
			GwtShareItem shareItem;
			
			shareItem = null;
			
			// Are we dealing with a User?
			if ( gwtTeamingItem instanceof GwtUser )
			{
				GwtUser user;
				String userId;
				String recipientId;
				String title;
				
				// Yes
				user = (GwtUser) gwtTeamingItem;
				
				// Is the user trying to share the item with themselves?
				userId = GwtClientHelper.getRequestInfo().getUserId();
				if ( userId != null && userId.equalsIgnoreCase( user.getUserId() ) )
				{
					// Yes, tell them they can't.
					Window.alert( GwtTeaming.getMessages().shareDlg_cantShareWithYourself() );
					return null;
				}
				
				// Is this an external user?
				if ( !user.isInternal() )
				{
					// Yes, is sharing this entity with an external user allowed?
					if ( m_sharingInfo.getCanShareWithExternalUsers() == false )
					{
						// No, tell the user they can't do this.
						Window.alert( GwtTeaming.getMessages().shareDlg_cantShareWithExternalUser() );
						return null;
					}
				}
				
				shareItem = new GwtShareItem();
				title = user.getTitle();
				if ( title == null || title.length() == 0 )
					title = user.getName();
				shareItem.setRecipientName( title );
				if ( !user.isInternal() )
					shareItem.setRecipientType( GwtRecipientType.EXTERNAL_USER );
				else
					shareItem.setRecipientType( GwtRecipientType.USER );
				shareItem.setRecipientUserType( user.getUserType() );
				
				recipientId = user.getUserId();
				if ( recipientId != null && recipientId.length() > 0 )
					shareItem.setRecipientId( Long.valueOf( recipientId ) );
			}
			// Are we dealing with a group?
			else if ( gwtTeamingItem instanceof GwtGroup )
			{
				GwtGroup group;
				
				// Yes
				group = (GwtGroup) gwtTeamingItem;
				
				// Is this group the "all external users" group?
				if ( GwtClientHelper.isAllExternalUsersGroup( group.getId() ) )
				{
					// Yes
					// Does the user have rights to share with the "all external users" group?
					if ( m_sharingInfo.getCanShareWithAllExternalUsersGroup() == false )
					{
						// No, tell they user they can't do this.
						Window.alert( GwtTeaming.getMessages().shareDlg_cantShareWithAllExternalUsersGroup() );
						return null;
					}
				}
				// Is this group the "all internal users" group?
				else if ( GwtClientHelper.isAllInternalUsersGroup( group.getId() ) )
				{
					// Yes
					// Does the user have rights to share with the "all internal users" group?
					if ( m_sharingInfo.getCanShareWithAllInternalUsersGroup() == false )
					{
						// No, tell they user they can't do this.
						Window.alert( GwtTeaming.getMessages().shareDlg_cantShareWithAllInternalUsersGroup() );
						return null;
					}
				}
				
				shareItem = new GwtShareItem();
				shareItem.setRecipientName( group.getShortDisplayName() );
				shareItem.setRecipientType( GwtRecipientType.GROUP );
				shareItem.setRecipientUserType( UserType.UNKNOWN );
				shareItem.setRecipientId( Long.valueOf( group.getId() ) );
			}
			// Are we dealing with the "Public" entity?
			else if ( gwtTeamingItem instanceof GwtPublic )
			{
				GwtPublic publicEntity;
				
				// Yes
				publicEntity = (GwtPublic) gwtTeamingItem;
				
				// Does the user have rights to share with the public?
				if ( m_sharingInfo.getCanShareWithPublic() == false )
				{
					// No, tell the user they can't do this.
					Window.alert( GwtTeaming.getMessages().shareDlg_cantShareWithPublic() );
					return null;
				}
				
				shareItem = new GwtPublicShareItem();
				shareItem.setRecipientName( publicEntity.getName() );
				shareItem.setRecipientType( GwtRecipientType.PUBLIC_TYPE );
				shareItem.setRecipientUserType( UserType.UNKNOWN );
				shareItem.setRecipientId( publicEntity.getIdLong() );
			}

			// Do we have an object to add to our list of shares?
			if ( shareItem != null )
			{
				// Yes
				shareItem.setEntityId( nextEntityId );
				shareItem.setEntityName( getEntityName( nextEntityId ) );
				
				// Has the item already been shared with the recipient
				if ( findShareItem( shareItem ) == null )
				{
					// No
					shareItem.setShareRights( getDefaultShareRights() );
					shareItem.setShareExpirationValue( m_defaultShareExpirationValue );
					shareItem.setComments( getDefaultComment() );
					
					// Add the recipient to our list of recipients
					addShare( shareItem, true );
				}
				else
				{
					// Yes, tell the user
					Window.alert( GwtTeaming.getMessages().shareDlg_alreadySharedWithSelectedRecipient( shareItem.getRecipientName() ) );
					shareItem = null;
				}
			}
			
			if ( shareItem != null )
			{
				shareItem.setSharedById( Long.valueOf( GwtTeaming.m_requestInfo.getUserId() ) );
				
				returnValue.add( shareItem );
			}
		}// end for()
		
		return returnValue;
	}
	
	/**
	 * Add the given list of shares to the dialog
	 */
	private void addShares( ArrayList<GwtShareItem> listOfShares )
	{
		m_listOfShares = listOfShares;
		if ( m_listOfShares == null )
			m_listOfShares = new ArrayList<GwtShareItem>();
		
		if ( m_dataProvider == null )
		{
			m_dataProvider = new ListDataProvider<GwtShareItem>( m_listOfShares );
			m_dataProvider.addDataDisplay( m_shareTable );
		}
		else
		{
			m_dataProvider.setList( m_listOfShares );
			m_dataProvider.refresh();
		}

		// Clear all selections.
		m_selectionModel.clear();

		// Go to the first page
		m_pager.firstPage();

		// Tell the table how many groups we have.
		m_shareTable.setRowCount( m_listOfShares.size(), true );
	}


	/**
	 * Add "Public" as a recipient.  Remove "all internal users", "all external users" and "guest"
	 * if they have already been added as recipients.
	 */
	private void addShareWithPublic()
	{
		GwtPublic publicEntity;
		
		publicEntity = new GwtPublic();
		publicEntity.setName( GwtTeaming.getMessages().publicName() );
		addShare( publicEntity );
	}
	
	
	/**
	 * Look at each item in the list and return the highest rights possible
	 * that is available on all items in the list.
	 */
	private ShareRights calculateHighestRightsPossible( ArrayList<GwtShareItem> listOfShareItems )
	{
		ShareRights highestRightsPossible;
		AccessRights accessRights = AccessRights.CONTRIBUTOR;
		boolean canShareForward = true;
		boolean canShareWithInternalUsers = true;
		boolean canShareWithExternalUsers = true;
		boolean canShareWithPublic = true;
		
		if ( listOfShareItems != null )
		{
			for ( GwtShareItem nextShareItem : listOfShareItems )
			{
				ShareRights shareRights;
				AccessRights nextAccessRights;
				
				shareRights = m_sharingInfo.getShareRights( nextShareItem.getEntityId() );
				
				if ( shareRights.getCanShareForward() == false )
					canShareForward = false;
				
				if ( shareRights.getCanShareWithExternalUsers() == false )
					canShareWithExternalUsers = false;
				
				if ( shareRights.getCanShareWithInternalUsers() == false )
					canShareWithInternalUsers = false;
				
				if ( shareRights.getCanShareWithPublic() == false )
					canShareWithPublic = false;

				nextAccessRights = shareRights.getAccessRights();
				
				switch ( accessRights )
				{
				case CONTRIBUTOR:
					if ( nextAccessRights == AccessRights.EDITOR || nextAccessRights == AccessRights.VIEWER )
						accessRights = nextAccessRights;
					break;
					
				case EDITOR:
					if ( nextAccessRights == AccessRights.VIEWER )
						accessRights = nextAccessRights;
					break;
				
				case VIEWER:
					// Nothing to do we are already at the lowest rights.
					break;

				default:
					break;
				}
			}
		}
		
		highestRightsPossible = new ShareRights();
		highestRightsPossible.setAccessRights( accessRights );
		highestRightsPossible.setCanShareForward( canShareForward );
		highestRightsPossible.setCanShareWithInternalUsers( canShareWithInternalUsers );
		highestRightsPossible.setCanShareWithExternalUsers( canShareWithExternalUsers );
		highestRightsPossible.setCanShareWithPublic( canShareWithPublic );
	
		return highestRightsPossible;
	}
	
	/**
	 * Determine if the user has rights to share the entities with the public.
	 */
	private boolean canShareWithPublic()
	{
		// Is sharing with the public turned on at the global level for this user?
		if ( m_sharingInfo.getCanShareWithPublic() == false )
		{
			// No
			return false;
		}

		// See if the user has the rights to share each entity with the public.
		for ( EntityId nextEntityId : m_entityIds )
		{
			ShareRights shareRights;
			
			// Can the user share this entity with the public?
			shareRights = m_sharingInfo.getShareRights( nextEntityId );
			if ( shareRights.getCanShareWithPublic() == false )
			{
				// No
				return false;
			}
		}
		
		// If we get here the user has rights to share each entity with the public.
		return true;
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
		m_mainPanel.addStyleName( "teamingDlgBoxContentOverride" );

		return m_mainPanel;
	}
	
	/**
	 * Create all the controls needed to share this item with others.
	 */
	private void createShareControls()
	{
		GwtTeamingMessages messages;
		int tableWidth = 330;
		
		messages = GwtTeaming.getMessages();
		
		// Add a "Make Public" button
		{
			Anchor anchor;
			ClickHandler clickHandler;
			ImageResource imgResource;
			Image img;
			
			m_makePublicPanel = new FlowPanel();
			m_makePublicPanel.addStyleName( "shareThisDlg_MakePublicDiv" );
			m_mainPanel.add( m_makePublicPanel );
			
			clickHandler = new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent event )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							addShareWithPublic();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};
			anchor = new Anchor( messages.shareDlg_makePublic() );
			anchor.addStyleName( "gwt-Button" );
			anchor.addStyleName( "teamingButton" );
			anchor.addClickHandler( clickHandler );
			anchor.setTitle( messages.shareDlg_makePublic() );
			m_makePublicPanel.add( anchor );
			
			imgResource = GwtTeaming.getImageBundle().public16();
			img = new Image( imgResource );
			img.getElement().getStyle().setVerticalAlign( VerticalAlign.MIDDLE );
			img.getElement().getStyle().setPaddingRight( 3, Unit.PX );
			anchor.getElement().insertFirst( img.getElement() );
		}
		
		m_defaultShareExpirationValue = new ShareExpirationValue();
		m_defaultShareExpirationValue.setType( ShareExpirationType.NEVER );
		
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
									// Yes, try to add an external user.
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
			findTable.addStyleName( "shareThisDlg_findTable" );
			rowFormatter = findTable.getRowFormatter();
			rowFormatter.setVerticalAlign( 0, HasVerticalAlignment.ALIGN_TOP );
			m_findCtrl.setIsSendingEmail( true );
			m_findCtrl.setFloatingHintText( messages.shareDlg_shareWithHint() );
			findTable.setWidget( 0, 0, m_findCtrl );
			
			m_addShareTable = new FlexTable();
			m_addShareTable.setWidget( 0, 0, findTable );
			m_mainPanel.add( m_addShareTable );

			// On IE calling m_cellFormatter.setWidth( row, col, "*" ); throws an exception.
			// That is why we are calling DOM.setElementAttribute(...) instead.
			//mainCellFormatter.setWidth( row, 1, "*" );
			{
				FlexCellFormatter mainCellFormatter;

				mainCellFormatter = m_addShareTable.getFlexCellFormatter();
				DOM.setElementAttribute( mainCellFormatter.getElement( 0, 0 ), "width", "*" );
			}
			
			// Add an "add external user" image.
			{
				ClickHandler clickHandler;
				ImageResource imageResource;
				FlexCellFormatter findCellFormatter;
				
				imageResource = GwtTeaming.getImageBundle().add_btn();
				m_addExternalUserImg = new Image( imageResource );
				m_addExternalUserImg.addStyleName( "cursorPointer" );
				
				// this style hides the + icon
				m_addExternalUserImg.addStyleName( "displayNone" ); 
				
				m_addExternalUserImg.getElement().setAttribute( "title", messages.shareDlg_addExternalUserTitle() );
				findTable.setWidget( 0, 1, m_addExternalUserImg );
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
				m_addExternalUserImg.addClickHandler( clickHandler );
			}
			
			// Add a "Share with teams" link
			{
				// Are we running Filr?
				if ( GwtClientHelper.getRequestInfo().isLicenseFilr() == false )
				{
					ClickHandler clickHandler;
					
					// No, add a link the user can click on to invoke the "Share with teams" dialog
					m_shareWithTeamsLabel = new InlineLabel( messages.shareWithTeams() );
					m_shareWithTeamsLabel.addStyleName( "shareThisDlg_shareWithTeamsLink" );

					// Add a click handler to the "share with teams" label.
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
									// Invoke the "Share with teams" dialog.
									invokeShareWithTeamsDlg();
								}
							};
							Scheduler.get().scheduleDeferred( cmd );
						}
					};
					m_shareWithTeamsLabel.addClickHandler( clickHandler );

					findTable.setWidget( 0, 2, m_shareWithTeamsLabel );
				}
			}
		}
		
		// Create a table to hold the list of shares
		{
			CellTable.Resources cellTableResources;

			// Create the CellTable that will display the list of Net Folders.
			cellTableResources = GWT.create( VibeCellTable.VibeCellTableResources.class );
			m_shareTable = new CellTable<GwtShareItem>( 20, cellTableResources );
			m_shareTable.setWidth( String.valueOf( tableWidth ) + "px" );
			m_shareTable.addStyleName( "shareThisDlg_ListOfSharesTable" );
			
		    // Add a selection model so we can select shares.
		    m_selectionModel = new MultiSelectionModel<GwtShareItem>();
		    m_shareTable.setSelectionModel(
		    							m_selectionModel,
		    							DefaultSelectionEventManager.<GwtShareItem> createCheckboxManager() );

			// Add a checkbox in the first column
			{
				Column<GwtShareItem, Boolean> ckboxColumn;
				VibeCheckboxCell ckboxCell;
				
				// Create a checkbox that will be in the column header and will be used to select/deselect
				// shares
				{
					CheckboxCell cbSelectAllCell;

					cbSelectAllCell = new CheckboxCell();
					m_selectAllHeader = new SelectAllHeader( cbSelectAllCell );
					m_selectAllHeader.setUpdater( new ValueUpdater<Boolean>()
					{
						@Override
						public void update( Boolean checked )
						{
							List<GwtShareItem> shares;

							shares = m_shareTable.getVisibleItems();
							if ( shares != null )
							{
								for ( GwtShareItem nextShareItem : shares )
								{
									m_selectionModel.setSelected( nextShareItem, checked );
								}
							}
						}
					} );
				}
				
	            ckboxCell = new VibeCheckboxCell();
			    ckboxColumn = new Column<GwtShareItem, Boolean>( ckboxCell )
	            {
	            	@Override
			        public Boolean getValue( GwtShareItem shareItem )
			        {
	            		// Get the value from the selection model.
			            return m_selectionModel.isSelected( shareItem );
			        }
			    };
			    
			    // Add a field updater so when the user checks/unchecks the checkbox next to a
			    // share item we will uncheck the "select all" checkbox that is in the header.
			    {
			    	ckboxColumn.setFieldUpdater( new FieldUpdater<GwtShareItem,Boolean>()
			    	{
			    		@Override
			    		public void update( int index, GwtShareItem shareItem, Boolean checked )
			    		{
			    			m_selectionModel.setSelected( shareItem,  checked );
			    			
			    			if ( checked == false )
			    			{
			    				m_selectAllHeader.setValue( false );
			    			}
			    		}
			    	} );
			    }
			    
		        m_shareTable.addColumn( ckboxColumn, m_selectAllHeader );
			    m_shareTable.setColumnWidth( ckboxColumn, 20, Unit.PX );			
			}

			// Add the "Shared with" column.  The user can click on the text in this column
			// to edit the share.
			{
				ShareItemCell cell;
				Column<GwtShareItem,GwtShareItem> sharedWithCol;

				cell = new ShareItemCell();
				sharedWithCol = new Column<GwtShareItem, GwtShareItem>( cell )
				{
					@Override
					public GwtShareItem getValue( GwtShareItem shareItem )
					{
						return shareItem;
					}
					
					@Override
					public void render( Cell.Context context, GwtShareItem shareItem, SafeHtmlBuilder sb )
					{
						ShareItemCell cell;
						
						cell = (ShareItemCell) getCell();
						cell.render( context, shareItem, sb, m_sharingInfo, m_mode );
					}
				};
			
				sharedWithCol.setFieldUpdater( new FieldUpdater<GwtShareItem, GwtShareItem>()
				{
					@Override
					public void update( int index, GwtShareItem shareItem, GwtShareItem value )
					{
						ArrayList<GwtShareItem> listOfShares;
						
						listOfShares = new ArrayList<GwtShareItem>();
						listOfShares.add( shareItem );
						invokeEditShareDlg( listOfShares );
					}
				} );
				m_shareTable.addColumn( sharedWithCol, messages.shareDlg_sharedWithCol() );
			}

			// Add the list of recipients
			{
				VerticalPanel leftPanel;
				HorizontalPanel hPanel;
				FlowPanel leftSubPanel;
			
				leftPanel = new VerticalPanel();
				leftPanel.addStyleName( "shareThisDlg_ListOfSharesParentTable" );
				
				// Put the table that holds the list of recipients into a scrollable div
				leftSubPanel = new FlowPanel();
				leftSubPanel.addStyleName( "shareThisDlg_ListOfSharesPanel" );
				leftSubPanel.add( m_shareTable );
				leftPanel.add( leftSubPanel );

				// Create a menu
				{
					InlineLabel label;
					FlowPanel menuPanel;
					
					menuPanel = new FlowPanel();
					menuPanel.addStyleName( "shareDlg_MenuPanel" );
					menuPanel.addStyleName( "shareDlg_MenuPanelOverride" );
					
					// Add an "Edit" button.
					label = new InlineLabel( messages.shareDlg_editButton() );
					label.addStyleName( "shareDlg_Btn" );
					label.addClickHandler( new ClickHandler()
					{
						@Override
						public void onClick( ClickEvent event )
						{
							Scheduler.ScheduledCommand cmd;
							
							cmd = new Scheduler.ScheduledCommand()
							{
								@Override
								public void execute()
								{
									editSelectedShares();
								}
							};
							Scheduler.get().scheduleDeferred( cmd );
						}
					} );
					menuPanel.add( label );
					
					// Add a "Delete" button.
					label = new InlineLabel( messages.shareDlg_deleteButton() );
					label.addStyleName( "shareDlg_Btn" );
					label.addClickHandler( new ClickHandler()
					{
						@Override
						public void onClick( ClickEvent event )
						{
							Scheduler.ScheduledCommand cmd;
							
							cmd = new Scheduler.ScheduledCommand()
							{
								@Override
								public void execute()
								{
									deleteSelectedShares();
								}
							};
							Scheduler.get().scheduleDeferred( cmd );
						}
					} );
					menuPanel.add( label );
					
					leftPanel.add( menuPanel );
				}

				// Create a pager
				m_pager = new VibeSimplePager();
				m_pager.setDisplay( m_shareTable );
				m_pager.setPageSize( 5 );
				leftPanel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
				leftPanel.setCellHeight( m_pager, "100%" );
				leftPanel.add( m_pager );
				
				// Create a panel where the "edit share" widget will be placed.
				{
					m_editSharePanel = new FlowPanel();
					m_editSharePanel.addStyleName( "shareThisDlg_EditSharePanel" );
				//	m_editSharePanel.setWidth( "400px" );
				//	m_editSharePanel.setHeight( "360px" );
					
					// Create the Edit Share widget
					m_editShareWidget = new EditShareWidget();
					m_editSharePanel.add( m_editShareWidget );
					m_editShareWidget.setVisible( false );
				}

				hPanel = new HorizontalPanel();
				hPanel.add( leftPanel );
				hPanel.add( m_editSharePanel );
				
				m_mainPanel.add( hPanel );
			}
		}
	}
	
	/*
	 * Create the static parts of the dialog.
	 */
	private void createStaticContent()
	{
		FlowPanel tmpPanel;
		GwtTeamingMessages messages;
		
		messages = GwtTeaming.getMessages();
		
		// Create the controls needed in the header
		{
			FlowPanel headerPanel;
			FlowPanel namePanel;
			
			headerPanel = new FlowPanel();
			headerPanel.addStyleName( "shareThisDlg_HeaderPanel" );
		
			m_headerImg = new Image();
			m_headerImg.addStyleName( "shareThisDlg_HeaderImg" );
			headerPanel.add( m_headerImg );
			
			namePanel = new FlowPanel();
			namePanel.addStyleName( "shareThisDlg_HeaderNamePanel" );
			
			m_headerNameLabel = new Label();
			m_headerNameLabel.addStyleName( "shareThisDlg_HeaderNameLabel" );
			namePanel.add( m_headerNameLabel );
			
			m_headerPathLabel = new Label();
			m_headerPathLabel.addStyleName( "shareThisDlg_HeaderPathLabel" );
			namePanel.add( m_headerPathLabel );
		
			// Add the controls needed to select how to manage share items.
			{
				FlexTable table;
				
				m_manageShareItemsPanel = new FlowPanel();
				m_manageShareItemsPanel.addStyleName( "shareThisDlg_ManageSharesPanel" );
				
				table = new FlexTable();
				table.setText( 0, 0, messages.shareDlg_findShareItemsBy() );
				
				m_manageShareItemsPanel.add( table );
				
				// Create a listbox that will hold the options of how to find share items.
				{
					m_findByListbox = new ListBox( false );
					m_findByListbox.setVisibleItemCount( 1 );
					
					m_findByListbox.addItem( messages.shareDlg_findSharesByHint(), FIND_SHARES_BY_HINT );
					m_findByListbox.addItem( messages.shareDlg_findSharesByUser(), FIND_SHARES_BY_USER );
					m_findByListbox.addItem( messages.shareDlg_findSharesByFile(), FIND_SHARES_BY_FILE );
					m_findByListbox.addItem( messages.shareDlg_findSharesByFolder(), FIND_SHARES_BY_FOLDER );
					m_findByListbox.addItem( messages.shareDlg_findAllShares(), FIND_ALL_SHARES );
					m_findByListbox.setSelectedIndex( 0 );
					
					m_findByListbox.addChangeHandler( new ChangeHandler()
					{
						@Override
						public void onChange( ChangeEvent event )
						{
							Scheduler.ScheduledCommand cmd;
							
							cmd = new Scheduler.ScheduledCommand()
							{
								@Override
								public void execute()
								{
									int selectedIndex;
								
									selectedIndex = m_findByListbox.getSelectedIndex();
									if ( selectedIndex >= 0 )
									{
										handleFindSharesBySelectionChanged( m_findByListbox.getValue( selectedIndex ) );
										
										// Since something was selected, remove the hint.
										if ( m_findByListbox.getValue( 0 ).equalsIgnoreCase( FIND_SHARES_BY_HINT ) )
											m_findByListbox.removeItem( 0 );
									}
								}
							};
							Scheduler.get().scheduleDeferred( cmd );
						}
					} );
	
					table.setWidget( 0, 1, m_findByListbox );
	
					// Create a table that will hold the controls needed to search for a user/file/folder
					{
						FlexTable table2;
						
						table2 = new FlexTable();
						table.setWidget( 0, 2, table2 );
						
						m_manageSharesFindCtrlLabel = new InlineLabel( "abc" );
						m_manageSharesFindCtrlLabel.addStyleName( "marginleft2" );
						table2.setWidget( 0, 0, m_manageSharesFindCtrlLabel );
						table2.setWidget( 0, 1, m_manageSharesFindCtrl );
						
						m_manageSharesFindCtrl.setContainerWidget( m_manageShareItemsPanel );
						m_manageSharesFindCtrl.setVisible( false );
						m_manageSharesFindCtrlLabel.setVisible( false );
					}
				}

				headerPanel.add( m_manageShareItemsPanel );
			}

			headerPanel.add( namePanel );
			
			m_mainPanel.add( headerPanel );
		}
		
		// Add the controls needed for sharing.
		createShareControls();
		
		// Create the "notify" controls
		{
			InlineLabel label;
			
			tmpPanel = new FlowPanel();
			tmpPanel.addStyleName( "shareThisDlg_Notify" );
			
			label = new InlineLabel( messages.shareDlg_notifyLabel() );
			tmpPanel.add( label );
			
			m_sendToWidget = new ShareSendToWidget();
			m_sendToWidget.init( SendToValue.ONLY_MODIFIED_RECIPIENTS );
			tmpPanel.add( m_sendToWidget );
			
			m_mainPanel.add( tmpPanel );
		}
	}
	
	/**
	 * This method gets called to delete the selected shares.
	 */
	private void deleteSelectedShares()
	{
		Set<GwtShareItem> selectedShares;
		
		selectedShares = getSelectedShares();
		if ( selectedShares != null && selectedShares.size() > 0 )
		{
			for ( GwtShareItem nextShare : selectedShares )
			{
				// Mark this share as "to be deleted"
				m_sharingInfo.addToBeDeleted( nextShare );
				
				m_listOfShares.remove( nextShare );
			}
			
			m_dataProvider.refresh();

			// Tell the table how many shares we have.
			m_shareTable.setRowCount( m_listOfShares.size(), true );
		}
		else
		{
			Window.alert( GwtTeaming.getMessages().shareDlg_selectSharesToDelete() );
		}
		
		// Is the "edit share" widget visible?
		if ( m_editShareWidget != null && m_editShareWidget.isVisible() )
		{
			// Yes, close it.
			m_editShareWidget.setVisible( false );
		}
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
		//!!!
	/*
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
	*/
		
		// Simply return true to allow the dialog to close.
		return true;
	}

	/**
	 * This method gets called to edit the selected shares.
	 */
	private void editSelectedShares()
	{
		Set<GwtShareItem> selectedShares;
		
		selectedShares = getSelectedShares();
		if ( selectedShares != null && selectedShares.size() > 0 )
		{
			InvokeEditShareRightsDlgEvent event;
			ArrayList<GwtShareItem> listOfShares;

			listOfShares = new ArrayList<GwtShareItem>();
			for ( GwtShareItem nextShare : selectedShares )
			{
				listOfShares.add( nextShare );
			}
			
			// Fire an event to invoke the "edit share rights" dialog.
			event = new InvokeEditShareRightsDlgEvent( listOfShares );
			GwtTeaming.fireEvent( event );
		}
		else
		{
			Window.alert( GwtTeaming.getMessages().shareDlg_selectSharesToEdit() );
		}
	}
	
	/**
	 * This method gets called when user user presses the OK push
	 * button.  We will issue an ajax request to start the work of
	 * sharing the selected entities with the selected users/groups/teams.
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
		GwtSharingInfo sharingData;
		ArrayList<GwtShareItem> listOfShareItems;
		
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
					
					// Enable the Ok button.
					Window.alert( GwtTeaming.getMessages().rpcFailure_ShareEntry() );
					hideStatusMsg();
					setOkEnabled( true );
				}// end onFailure()

				@Override
				public void onSuccess( final VibeRpcResponse vibeResult )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
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
							
							// Get a list of the successful shares we did
							{
								ArrayList<GwtShareItemResult> listOfSuccesses;
								
								listOfSuccesses = result.getSuccesses();
								if ( listOfSuccesses != null )
								{
									ArrayList<Long> emailNeeded;
									
									emailNeeded = null;
									
									// Create a list of the ids of the share items we need to send an email for.
									for ( GwtShareItemResult nextSuccess : listOfSuccesses )
									{
										GwtShareItem shareItem;

										// Find the recipient in the table.
										shareItem = findShareItem( nextSuccess.getGwtShareItem() );
										if ( shareItem != null )
										{
											shareItem.setIsDirty( false );
										}
										
										// Is an email needed?
										if ( nextSuccess.getEmailNeeded() )
										{
											// Yes
											if ( emailNeeded == null )
												emailNeeded = new ArrayList<Long>();
											
											emailNeeded.add( nextSuccess.getId() );
										}
									}
									
									// Do we need to send any emails?
									if ( emailNeeded != null && emailNeeded.size() > 0 )
									{
										// Yes
										sendNotificationEmails( emailNeeded );
									}
								}
							}
							
							// Do we have any errors to display?
							if ( haveErrors )
							{
								// Yes
								// Make the error panel visible.
								showErrorPanel();

								// Enable the Ok button.
								hideStatusMsg();
								setOkEnabled( true );
							}
							else
							{
								// Close this dialog.
								hide();
								
								// Fire the event to notify about the new shares
								GwtTeaming.fireEvent(new ContentChangedEvent(Change.SHARING));
							}
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}// end onSuccess()				
			};
		}

		// Is the "edit share" widget visible?
		if ( m_editShareWidget != null && m_editShareWidget.isVisible() )
		{
			// Yes
			// The user may not have hit apply.  Tell the "edit share" widget to save its changes
			m_editShareWidget.saveSettings();
		}
		
		// Disable the Ok button.
		showStatusMsg( GwtTeaming.getMessages().shareDlg_savingShareInfo() );
		setOkEnabled( false );
		
		sharingData = new GwtSharingInfo();
		sharingData.setEntityNamesMap( m_sharingInfo.getEntityNamesMap() );
		listOfShareItems = getListOfShareItemsFromDlg();
		sharingData.setListOfShareItems( listOfShareItems );
		sharingData.setListOfToBeDeletedShareItems( m_sharingInfo.getListOfToBeDeletedShareItems() );
		
		// Get who should be notified.
		if ( m_sendToWidget.getSendToValue() == SendToValue.NO_ONE )
			sharingData.setNotifyRecipients( false );
		else
			sharingData.setNotifyRecipients( true );
		sharingData.setSendToValue( m_sendToWidget.getSendToValue() );
		
		// Issue an ajax request to share the entities.
		ShareEntryCmd cmd = new ShareEntryCmd( sharingData );
		GwtClientHelper.executeCommand( cmd, m_shareEntryCallback );
		
		// Returning false will prevent the dialog from closing.  We will close
		// the dialog when we get the response back from our ajax request.
		return false;
	}

	/**
	 * Issue an rpc request to find all share items.
	 */
	private void findAllShares()
	{
		GetSharingInfoCmd cmd;
		
		showStatusMsg( GwtTeaming.getMessages().shareDlg_readingShareInfo() );
		
		// Remove all the share items we might have already.
		removeAllShares();

		// Issue an rpc request to get the share information for the entities we are working with.
		cmd = new GetSharingInfoCmd( null, null );
		GwtClientHelper.executeCommand( cmd, m_getSharingInfoCallback );
	}
	
	/**
	 * Find the given recipient in the table that holds the recipients.
	 */
	private GwtShareItem findShareItem( GwtShareItem shareItem )
	{
		if ( shareItem == null )
			return null;

		if ( m_listOfShares == null )
			return null;
		
		// Look through our list of shares for the given GwtShareItem.
		for ( GwtShareItem nextShareItem : m_listOfShares )
		{
			if ( shareItem.equals( nextShareItem ) )
			{
				// We found the recipient
				return nextShareItem;
			}
		}// end for()
		
		// If we get here we did not find the recipient.
		return null;
	}
	

	/**
	 * Issue an rpc request to find share items.
	 */
	private void findSharesBy( String findBy, GwtTeamingItem selectedItem )
	{
		GetSharingInfoCmd cmd = null;
		
		if ( findBy != null )
		{
			if ( findBy.equalsIgnoreCase( FIND_SHARES_BY_FILE ) ||
					findBy.equalsIgnoreCase( FIND_SHARES_BY_FOLDER ) )
			{
				ArrayList<EntityId> listOfEntityIds;
				
				listOfEntityIds = new ArrayList<EntityId>();

				if ( selectedItem instanceof GwtFolderEntry )
				{
					GwtFolderEntry gwtFolderEntry;
					EntityId entityId;
					
					gwtFolderEntry = (GwtFolderEntry) selectedItem;
					entityId = new EntityId();
					entityId.setEntityId( Long.valueOf( gwtFolderEntry.getEntryId() ) );
					entityId.setBinderId( gwtFolderEntry.getParentBinderId() );
					entityId.setEntityType( EntityId.FOLDER_ENTRY );

					listOfEntityIds.add( entityId );
				}
				else if ( selectedItem instanceof GwtFolder )
				{
					GwtFolder gwtFolder;
					EntityId entityId;
					
					gwtFolder = (GwtFolder) selectedItem;
					entityId = new EntityId();
					entityId.setEntityId( Long.valueOf( gwtFolder.getFolderId() ) );
					entityId.setEntityType( EntityId.FOLDER );

					listOfEntityIds.add( entityId );
				}
				
				if ( listOfEntityIds.size() > 0 )
					cmd = new GetSharingInfoCmd( listOfEntityIds, null );
			}
			else if ( findBy.equalsIgnoreCase( FIND_SHARES_BY_USER ) )
			{
				if ( selectedItem instanceof GwtUser )
				{
					GwtUser user;
					
					user = (GwtUser) selectedItem;
					cmd = new GetSharingInfoCmd( null, user.getUserId() );
				}
			}
		}
		
		if ( cmd != null )
		{
			showStatusMsg( GwtTeaming.getMessages().shareDlg_readingShareInfo() );
			
			// Remove all the share items we might have already.
			removeAllShares();

			// Issue an rpc request to get the share information for the entities we are working with.
			GwtClientHelper.executeCommand( cmd, m_getSharingInfoCallback );
		}
	}
	
	/**
	 * Return the default comment
	 */
	private String getDefaultComment()
	{
		return "";
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
								String imgUrl;
								
								// Update the name of the entity in the header.
								m_headerNameLabel.setText( gwtFolderEntry.getEntryName() );
								m_headerPathLabel.setText( gwtFolderEntry.getParentBinderName() );
								m_headerPathLabel.setTitle( gwtFolderEntry.getParentBinderName() );
								
								// Do we have a url for the file image?
								imgUrl = gwtFolderEntry.getFileImgUrl();
								if ( imgUrl != null && imgUrl.length() > 0 )
								{
									m_headerImg.setUrl( GwtClientHelper.getRequestInfo().getImagesPath() + imgUrl );
								}
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
	 * Return the name of the given entity
	 */
	private String getEntityName( EntityId entityId )
	{
		return m_sharingInfo.getEntityName( entityId );
	}
	
	/**
	 * Return the selection of how to find shares.
	 */
	private String getFindBy()
	{
		String findBy = null;
		
		if ( m_findByListbox != null )
		{
			int index;

			index = m_findByListbox.getSelectedIndex();
			if ( index >= 0 )
			{
				findBy = m_findByListbox.getValue( index );
			}
		}
		
		return findBy;
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
	 * 
	 */
	@Override
	public HelpData getHelpData()
	{
		if ( m_mode == ShareThisDlgMode.MANAGE_SELECTED || m_mode == ShareThisDlgMode.MANAGE_ALL )
		{
			HelpData helpData;
			
			helpData = new HelpData();
			helpData.setGuideName( HelpData.ADMIN_GUIDE );
			helpData.setPageId( "share_manage" );
			
			return helpData;
		}
		
		return null;
	}
	
	/**
	 * Return the list of GwtShareItem objects from the table that holds the
	 * list of recipients
	 */
	private ArrayList<GwtShareItem> getListOfShareItemsFromDlg()
	{
		return m_listOfShares;
	}
	
	/**
	 * Return a list of teams that have not been shared with.
	 */
	private List<TeamInfo> getListOfTeamsNotSharedWith()
	{
		ArrayList<TeamInfo> listOfTeams;
		
		listOfTeams = new ArrayList<TeamInfo>();
		
		// Do we have any teams?
		if ( m_listOfTeams != null && m_listOfTeams.size() > 0 )
		{
			GwtShareItem shareItem;

			shareItem = new GwtShareItem();
			shareItem.setRecipientType( GwtRecipientType.TEAM );
			shareItem.setRecipientUserType( UserType.UNKNOWN );

			// Yes
			// Go through each team and see if the entities have already been shared with that team.
			for ( TeamInfo nextTeamInfo : m_listOfTeams )
			{
				boolean alreadySharedWithTeam;
				
				shareItem.setRecipientName( nextTeamInfo.getTitle() );
				shareItem.setRecipientId( Long.valueOf( nextTeamInfo.getBinderId() ) );
				alreadySharedWithTeam = true;
				
				for ( EntityId nextEntityId : m_entityIds )
				{
					shareItem.setEntityId( nextEntityId );
					shareItem.setEntityName( getEntityName( nextEntityId ) );
					
					// Has this entity already been shared with this team?
					if ( findShareItem( shareItem ) == null )
					{
						// No
						alreadySharedWithTeam = false;
						break;
					}
				}
				
				// Have the entities already been shared with this team?
				if ( alreadySharedWithTeam == false )
				{
					// No
					listOfTeams.add( nextTeamInfo );
				}
			}
		}
		
		return listOfTeams;
	}
	

	/**
	 * Return the default share access rights
	 */
	private ShareRights.AccessRights getDefaultShareAccessRights()
	{
		return ShareRights.AccessRights.VIEWER;
	}
	
	/**
	 * 
	 */
	private ShareRights getDefaultShareRights()
	{
		ShareRights shareRights;
		
		shareRights = new ShareRights();
		shareRights.setAccessRights( getDefaultShareAccessRights() );
		
		return shareRights;
	}
	
	/**
	 * Return a list of selected shares.
	 */
	private Set<GwtShareItem> getSelectedShares()
	{
		return m_selectionModel.getSelectedSet();
	}
	
	/**
	 * 
	 */
	private void handleClickOnAddExternalUser()
	{
		final String emailAddress;
		AsyncCallback<VibeRpcResponse> findUserCallback;

		// Is sharing with an external user ok to do?
		if ( m_sharingInfo.getCanShareWithExternalUsers() == false )
		{
			// No, bail.
			return;
		}
		
		emailAddress = m_findCtrl.getText();

		// Clear what the user has typed.
		m_findCtrl.clearText();

		// Hide the search-results widget.
		m_findCtrl.hideSearchResults();

		if ( emailAddress != null && emailAddress.length() > 0 )
		{
			findUserCallback = new AsyncCallback<VibeRpcResponse>()
			{
				@Override
				public void onFailure( Throwable caught )
				{
					GwtClientHelper.handleGwtRPCFailure(
													caught,
													GwtTeaming.getMessages().rpcFailure_FindUserByEmailAddress() );
				}

				@Override
				public void onSuccess( final VibeRpcResponse vibeResult )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							GwtUser gwtUser;
							
							// Was the email associated with an internal user?
							if ( vibeResult.getResponseData() != null )
							{
								final ArrayList<GwtShareItem> listOfShareItems; 
								
								// Yes
								gwtUser = (GwtUser) vibeResult.getResponseData();
								listOfShareItems = addShare( gwtUser );

								if ( listOfShareItems != null )
								{
									Scheduler.ScheduledCommand cmd;
									
									cmd = new Scheduler.ScheduledCommand()
									{
										@Override
										public void execute()
										{
											InvokeEditShareRightsDlgEvent event;

											// Fire an event to invoke the "edit share rights" dialog.
											event = new InvokeEditShareRightsDlgEvent( listOfShareItems );
											GwtTeaming.fireEvent( event );
										}
									};
									Scheduler.get().scheduleDeferred( cmd );
								}
							}
							else
							{
								// No
								// Validate the mail address that was entered.  If the email address
								// is valid then add it to the list of recipients
								validateEmailAddress( emailAddress );
							}
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}				
			};

			// Issue an ajax request to see if the email address that was entered is associated
			// with an internal user.
			FindUserByEmailAddressCmd cmd = new FindUserByEmailAddressCmd( emailAddress, true );
			GwtClientHelper.executeCommand( cmd, findUserCallback );
		}
	}
	
	/**
	 * This method gets called to handle when the user selects how they want to find shares
	 */
	private void handleFindSharesBySelectionChanged( String findBy )
	{
		boolean visible;
		
		if ( findBy == null )
			return;
		
		visible = true;

		m_manageSharesFindCtrl.setInitialSearchString( "" );

		if ( findBy.equalsIgnoreCase( FIND_SHARES_BY_USER ) )
		{
			m_manageSharesFindCtrl.setSearchForExternalPrincipals( true );
			m_manageSharesFindCtrl.setSearchForInternalPrincipals( true );
			m_manageSharesFindCtrl.setSearchType( SearchType.USER );
			m_manageSharesFindCtrlLabel.setText( GwtTeaming.getMessages().shareDlg_findByUserLabel() );
		}
		else if ( findBy.equalsIgnoreCase( FIND_SHARES_BY_FILE ) )
		{
			m_manageSharesFindCtrl.setSearchType( SearchType.ENTRIES );
			m_manageSharesFindCtrlLabel.setText( GwtTeaming.getMessages().shareDlg_findByFileLabel() );
		}
		else if ( findBy.equalsIgnoreCase( FIND_SHARES_BY_FOLDER ) )
		{
			m_manageSharesFindCtrl.setSearchType( SearchType.FOLDERS );
			m_manageSharesFindCtrlLabel.setText( GwtTeaming.getMessages().shareDlg_findByFolderLabel() );
		}
		else if ( findBy.equalsIgnoreCase( FIND_ALL_SHARES ) )
		{
			visible = false;
			findAllShares();
		}

		m_manageSharesFindCtrlLabel.setVisible( visible );
		m_manageSharesFindCtrl.setVisible( visible );
		if ( visible )
			m_manageSharesFindCtrl.getFocusWidget().setFocus( true );
	}
	
	/**
	 * 
	 */
	public void init(
		String caption,
		List<EntityId> entityIds,
		ShareThisDlgMode mode )
	{
		GetSharingInfoCmd rpcCmd1 = null;
		GetMyTeamsCmd rpcCmd2 = null;
		
		// Set the caption...
		setCaption( caption );
		
		m_entityIds = entityIds;
		m_mode = mode;
		
		updateHeader();

		// Enable the Ok button.
		hideStatusMsg();
		setOkEnabled( true );

		m_editShareWidget.setVisible( false );
		
		m_selectAllHeader.setValue( false );

		// Set the widget that will be displayed when there are no shares
		{
			FlowPanel flowPanel;
			InlineLabel noNetFoldersLabel;
			
			flowPanel = new FlowPanel();
			flowPanel.addStyleName( "noObjectsFound" );
			if ( mode == ShareThisDlgMode.MANAGE_ALL )
				noNetFoldersLabel = new InlineLabel( GwtTeaming.getMessages().shareDlg_selectMethodToFindShares() );
			else
				noNetFoldersLabel = new InlineLabel( GwtTeaming.getMessages().noShareRecipients() );
			
			flowPanel.add( noNetFoldersLabel );
			
			m_shareTable.setEmptyTableWidget( flowPanel );
		}
		

		if ( m_findCtrl != null )
		{
			m_findCtrl.setInitialSearchString( "" );
			
			// Set the filter of the Find Control to only search for users and groups.
			m_findCtrl.setSearchType( SearchType.PRINCIPAL );
			
			m_findCtrl.showFloatingHint();
		}
		
		// Remove all of the rows from the table.
		removeAllShares();
		
		// If we are in Administrative mode, we don't allow the user to add shares.
		if ( m_mode == ShareThisDlgMode.MANAGE_SELECTED || m_mode == ShareThisDlgMode.MANAGE_ALL )
		{
			m_addShareTable.setVisible( false );
		}
		else
		{
			m_addShareTable.setVisible( true );
		}
		
		if ( GwtClientHelper.getRequestInfo().isLicenseFilr() == false )
		{
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
						GetMyTeamsRpcResponseData responseData;
						
						responseData = (GetMyTeamsRpcResponseData) response.getResponseData();
						m_listOfTeams = responseData.getTeams();
						if ( m_shareWithTeamsLabel != null )
						{
							if ( m_listOfTeams == null || m_listOfTeams.size() == 0 )
							{
								// Hide the "share with my teams" link.
								m_shareWithTeamsLabel.setVisible( false );
							}
							else
							{
								// Show the "share with my teams" link
								m_shareWithTeamsLabel.setVisible( true );
							}
						}
					}
				};
			}
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
					
					hideStatusMsg();
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
							hideStatusMsg();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};
		}
		
		// Are we in Administrative mode?
		if ( m_mode == ShareThisDlgMode.MANAGE_SELECTED )
		{
			// Yes
			// Passing null to GetSharingInfoCmd() means get all shares by everyone.
			rpcCmd1 = new GetSharingInfoCmd( m_entityIds, null );
		}
		else if ( m_mode == ShareThisDlgMode.NORMAL )
		{
			// No
			rpcCmd1 = new GetSharingInfoCmd( m_entityIds, GwtTeaming.m_requestInfo.getUserId() );
		}
		else if ( m_mode == ShareThisDlgMode.MANAGE_ALL )
		{
			ArrayList<GwtShareItem> listOfShares;
			
			// Initialize the list with an empty list.
			listOfShares = new ArrayList<GwtShareItem>();
			addShares( listOfShares );
		}

		if ( rpcCmd1 != null )
		{
			showStatusMsg( GwtTeaming.getMessages().shareDlg_readingShareInfo() );
	
			// Issue an rpc request to get the share information for the entities we are working with.
			GwtClientHelper.executeCommand( rpcCmd1, m_getSharingInfoCallback );
		}
		
		// Issue an rpc request to get the teams this user is a member of.
		if ( GwtClientHelper.getRequestInfo().isLicenseFilr() == false )
		{
			rpcCmd2 = new GetMyTeamsCmd();
			GwtClientHelper.executeCommand( rpcCmd2, m_readTeamsCallback );
		}
	}
	
	/**
	 * 
	 */
	private void invokeEditShareDlg( final ArrayList<GwtShareItem> listOfShareItems )
	{
		if ( listOfShareItems == null || listOfShareItems.size() == 0 )
			return;
		
		if ( m_editShareHandler == null )
		{
			m_editShareHandler = new EditSuccessfulHandler()
			{
				@Override
				public boolean editSuccessful( Object obj )
				{
					if ( obj instanceof Boolean )
					{
						Boolean retValue;
						
						// Did the "Edit Share" widget successfully update
						// our GwtShareItem.
						retValue = (Boolean) obj;
						if ( retValue == true )
						{
							Scheduler.ScheduledCommand cmd;
							final ArrayList<GwtShareItem> listOfShareItems;
							
							listOfShareItems = m_editShareWidget.getListOfShareItems();
							cmd = new Scheduler.ScheduledCommand()
							{
								@Override
								public void execute() 
								{
									refreshShareInfoUI( listOfShareItems );
								}
							};
							Scheduler.get().scheduleDeferred( cmd );
						}
					}

					return true;
				}
			};
		}

		// Is the "edit share" widget visible?
		if ( m_editShareWidget != null && m_editShareWidget.isVisible() )
		{
			// Yes
			// Tell the "edit share" widget to save its changes
			m_editShareWidget.saveSettings();
		}
		
		ShareRights highestRightsPossible;
		GwtShareItem shareItem;
		boolean recipientIsExternal = false;
		boolean recipientIsPublic = false;
		boolean isPublicLink = false;

		shareItem = listOfShareItems.get( 0 );
		
		// Are we dealing with only 1 share item?
		if ( listOfShareItems.size() == 1 )
		{
			// Yes
			highestRightsPossible = m_sharingInfo.getShareRights( shareItem.getEntityId() );

			// Is the recipient an external user?
			if ( shareItem.getRecipientType() == GwtRecipientType.EXTERNAL_USER )
			{
				// Yes
				recipientIsExternal = true;
			}
			else if ( shareItem.getRecipientType() == GwtRecipientType.PUBLIC_TYPE )
			{
				recipientIsPublic = true;
			}
			else if ( shareItem.getRecipientType() == GwtRecipientType.PUBLIC_LINK )
			{
				isPublicLink = true;
			}
		}
		else
		{
			// Look at each item being shared and return the highest rights possible
			// that is available on all items being shared.
			highestRightsPossible = calculateHighestRightsPossible( listOfShareItems );
			
			// Go through the list of share items and see if a recipient is an external user
			// or a public user.
			for ( GwtShareItem nextShareItem : listOfShareItems )
			{
				if ( nextShareItem.getRecipientType() == GwtRecipientType.EXTERNAL_USER )
				{
					recipientIsExternal = true;
				}
				else if ( nextShareItem.getRecipientType() == GwtRecipientType.PUBLIC_TYPE )
				{
					recipientIsPublic = true;
				}
				else if ( shareItem.getRecipientType() == GwtRecipientType.PUBLIC_LINK )
				{
					isPublicLink = true;
				}
			}
		}

		// Is the recipient the public user or a public link?
		if ( recipientIsPublic || isPublicLink )
		{
			// Yes, the public can only have "Viewer" rights.
			highestRightsPossible = new ShareRights();
			highestRightsPossible.setAccessRights( AccessRights.VIEWER );
			highestRightsPossible.setCanShareForward( false );
			highestRightsPossible.setCanShareWithExternalUsers( false );
			highestRightsPossible.setCanShareWithInternalUsers( false );
			highestRightsPossible.setCanShareWithPublic( false );
		}
		// Is the recipient of the share an external user?
		else if ( recipientIsExternal )
		{
			AccessRights accessRights;
			
			accessRights = highestRightsPossible.getAccessRights();
			
			// Yes, don't let the external user do any re-share
			highestRightsPossible = new ShareRights();
			highestRightsPossible.setAccessRights( accessRights );
			highestRightsPossible.setCanShareForward( false );
			highestRightsPossible.setCanShareWithExternalUsers( false );
			highestRightsPossible.setCanShareWithInternalUsers( false );
			highestRightsPossible.setCanShareWithPublic( false );
		}
		
		m_editShareWidget.setWidgetHeight( m_editSharePanel.getOffsetHeight() );
		m_editShareWidget.init( listOfShareItems, highestRightsPossible, m_editShareHandler );
		m_editShareWidget.setVisible( true );
	}
	
	/**
	 * Invoke the "Share with teams" dialog
	 */
	private void invokeShareWithTeamsDlg()
	{
		if ( m_editShareWithTeamsHandler == null )
		{
			m_editShareWithTeamsHandler = new EditSuccessfulHandler()
			{
				@SuppressWarnings("unchecked")
				@Override
				public boolean editSuccessful( Object obj )
				{
					if ( obj != null && obj instanceof List )
					{
						Scheduler.ScheduledCommand cmd;
						final List<TeamInfo> listOfSelectedTeams;
						
						listOfSelectedTeams = (List<TeamInfo>) obj;
						
						cmd = new Scheduler.ScheduledCommand()
						{
							@Override
							public void execute()
							{
								for ( TeamInfo nextTeamInfo : listOfSelectedTeams )
								{
									// Create a GwtShareItem for every entity we are sharing with.
									for ( EntityId nextEntityId : m_entityIds )
									{
										GwtShareItem shareItem;

										shareItem = new GwtShareItem();
										shareItem.setEntityId( nextEntityId );
										shareItem.setEntityName( getEntityName( nextEntityId ) );
										shareItem.setRecipientId( Long.valueOf( nextTeamInfo.getBinderId() ) );
										shareItem.setRecipientName( nextTeamInfo.getTitle() );
										shareItem.setRecipientType( GwtRecipientType.TEAM );
										shareItem.setRecipientUserType( UserType.UNKNOWN );
										shareItem.setShareRights( getDefaultShareRights() );
										shareItem.setShareExpirationValue( m_defaultShareExpirationValue );
										
										// Is this external user already in the list?
										if ( findShareItem( shareItem ) == null )
										{
											// No, add it
											addShare( shareItem, true );
										}
										else
										{
											// Tell the user the item has already been shared with the team.
											Window.alert( GwtTeaming.getMessages().shareDlg_alreadySharedWithSelectedRecipient( nextTeamInfo.getTitle() ) );
										}
									}
								}
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
					
					return true;
				}
			};
		}

		if ( m_shareWithTeamsDlg == null )
		{
			
			ShareWithTeamsDlg.createAsync(
										true,
										true,
										m_editShareWithTeamsHandler,
										new ShareWithTeamsDlgClient()
			{
				@Override
				public void onUnavailable() 
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( ShareWithTeamsDlg swtDlg )
				{
					m_shareWithTeamsDlg = swtDlg;
					invokeShareWithTeamsDlg();
				}
			} );
		}
		else
		{
			List<TeamInfo> listOfTeams;
			
			// Get the list of teams that have not been shared with.
			listOfTeams = getListOfTeamsNotSharedWith();
			
			if ( listOfTeams == null || listOfTeams.size() == 0 )
			{
				Window.alert( GwtTeaming.getMessages().shareDlg_noTeamsToShareWith() );
			}
			else
			{
				// Invoke the "share with teams" dialog.
				m_shareWithTeamsDlg.init( listOfTeams );
				m_shareWithTeamsDlg.show( true );
			}
		}
	}

	/**
	 * Refresh the ui that displays the list of shares.
	 */
	private void refreshShareInfoUI( ArrayList<GwtShareItem> listOfShares )
	{
		if ( listOfShares != null && listOfShares.size() > 0 )
		{
			for ( GwtShareItem nextShareItem : listOfShares )
			{
				int index;
				
				index = m_listOfShares.indexOf( nextShareItem );
				if ( index != -1 )
				{
					m_dataProvider.getList().set( index, nextShareItem );
				}
			}
		}
	}
	
	/**
	 * Remove all share items that may be in the table.
	 */
	private void removeAllShares()
	{
		if ( m_listOfShares != null )
		{
			m_listOfShares.clear();
	
			m_dataProvider.refresh();
	
			// Tell the table how many groups we have.
			m_shareTable.setRowCount( m_listOfShares.size(), true );
		}
	}
	
	/**
	 * Issue an rpc request to send an email for each of the given share item ids.
	 */
	private void sendNotificationEmails( ArrayList<Long> listOfShareItemIds )
	{
		SendShareNotificationEmailCmd cmd;

		if ( listOfShareItemIds == null || listOfShareItemIds.size() == 0 )
			return;
	
		if ( m_sendNotificationEmailCallback == null )
		{
			m_sendNotificationEmailCallback = new AsyncCallback<VibeRpcResponse>()
			{
				@Override
				public void onFailure( Throwable caught )
				{
					GwtClientHelper.handleGwtRPCFailure(
													caught,
													GwtTeaming.getMessages().rpcFailure_SendNotificationEmail() );
					
					hideStatusMsg();
				}

				@Override
				public void onSuccess( final VibeRpcResponse vibeResult )
				{
					Scheduler.ScheduledCommand cmd;

					hideStatusMsg();

					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							GwtSendShareNotificationEmailResults result = (GwtSendShareNotificationEmailResults) vibeResult.getResponseData();
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
								showErrorPanel();
							}
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}				
			};
		}

		showStatusMsg( GwtTeaming.getMessages().shareDlg_sendingNotificationEmail() );

		// Issue an ajax request to send the share notification emails.
		cmd = new SendShareNotificationEmailCmd( listOfShareItemIds );
		GwtClientHelper.executeCommand( cmd, m_sendNotificationEmailCallback );
	}
	
	/*
	 * 
	 */
	public void showDlg( UIObject target )
	{
		hideErrorPanel();
		showContentPanel();
		createFooterButtons( DlgBox.DlgButtonMode.OkCancel );

		if ( target == null )
			show( true );
		else
			showRelativeToTarget( target );
	}
	
	/**
	 * 
	 */
	public void showDlg()
	{
		hideErrorPanel();
		showContentPanel();
		createFooterButtons( DlgBox.DlgButtonMode.OkCancel );

		show();
	}
	
	/**
	 * Sort the given list of GwtShareItem objects
	 */
	private void sortShareItems( ArrayList<GwtShareItem> listOfGwtShareItems )
	{
		if ( listOfGwtShareItems != null && listOfGwtShareItems.size() > 0 )
		{
			Collections.sort( listOfGwtShareItems, new GwtShareItem.GwtShareItemComparator() );
		}
	}
	
	/**
	 * Update the header that displays the name of the entity we are working with.
	 * If we are dealing with > 1 entity we don't show a header.
	 */
	private void updateHeader()
	{
		ImageResource imgResource;
		int numItems;

		if ( m_mode == ShareThisDlgMode.MANAGE_ALL )
		{
			m_makePublicPanel.setVisible( false );

			m_manageShareItemsPanel.setVisible( true );
			m_manageSharesFindCtrlLabel.setVisible( false );
			m_manageSharesFindCtrl.setVisible( false );

			// Do we need to add the hint to the listbox?
			if ( m_findByListbox.getItemCount() == 4 )
			{
				// Yes
				m_findByListbox.insertItem(
										GwtTeaming.getMessages().shareDlg_findSharesByHint(),
										FIND_SHARES_BY_HINT,
										0 );
			}
			m_findByListbox.setSelectedIndex( 0 );
		}
		else if ( m_mode == ShareThisDlgMode.MANAGE_SELECTED )
		{
			m_makePublicPanel.setVisible( false );
			m_manageShareItemsPanel.setVisible( false );
		}
		else
			m_manageShareItemsPanel.setVisible( false );
		
		if ( m_entityIds == null )
		{
			m_headerImg.setVisible( false );
			return;
		}
		
		// Are we dealing with > 1 entities?
		numItems = m_entityIds.size();
		if ( numItems == 1 )
		{
			EntityId entityId;
			
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
		}
		else
		{
			// We are sharing multiple items.  Use the entry image.
			imgResource = GwtTeaming.getFilrImageBundle().entry_large();
			
			if ( m_mode == ShareThisDlgMode.NORMAL )
				m_headerNameLabel.setText( GwtTeaming.getMessages().sharingMultipleItems( numItems ) );
			else
				m_headerNameLabel.setText( GwtTeaming.getMessages().shareDlg_manageMultipleItems( numItems ) );
			
			// Put a non-breaking space in the path so that it gets a
			// height.  This fixes the layout so that the header
			// doesn't overlap a make public button.
			m_headerPathLabel.getElement().setInnerHTML("&nbsp;");
		}

		m_headerImg.setVisible( true );
		m_headerImg.setResource( imgResource );
	}
	
	/**
	 * Update the sharing information with the given information
	 */
	private void updateSharingInfo( GwtSharingInfo sharingInfo )
	{
		m_sharingInfo = sharingInfo;
		if ( sharingInfo != null )
		{
			ArrayList<GwtShareItem> listOfShareItems;

			// Is sharing with an external user available?
			if ( sharingInfo.getCanShareWithExternalUsers() == false )
			{
				// No
				m_addExternalUserImg.setVisible( false );
			}
			else
			{
				// Yes
				m_addExternalUserImg.setVisible( true );
			}

			// Show/hide the "Make public" button depending on whether the user has rights to
			// share with the public.
			if ( m_mode == ShareThisDlgMode.NORMAL )
				m_makePublicPanel.setVisible( canShareWithPublic() );
			else
				m_makePublicPanel.setVisible( false );
			
			// We never want external users to be included in the name completion.
			m_findCtrl.setSearchForExternalPrincipals( false );

			// Is sharing with an internal user available?
			m_findCtrl.setSearchForInternalPrincipals( sharingInfo.getCanShareWithInternalUsers() );
			
			// Is sharing with ldap groups available?
			m_findCtrl.setSearchForLdapGroups( sharingInfo.getCanShareWithLdapGroups() );

			listOfShareItems = sharingInfo.getListOfShareItems();
			if ( listOfShareItems == null )
				listOfShareItems = new ArrayList<GwtShareItem>();
			
			// Sort the list of share items.
			sortShareItems( listOfShareItems );
				
			addShares( listOfShareItems );
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
	 * Handles the InvokeEditShareRightsDlgEvent received by this class
	 */
	@Override
	public void onInvokeEditShareRightsDlg( InvokeEditShareRightsDlgEvent event )
	{
		final ArrayList<GwtShareItem> listOfShareItems;
		
		// Get the list of GwtShareItems we will be editing.
		listOfShareItems = event.getListOfShareItems();
		
		if ( listOfShareItems != null && listOfShareItems.size() > 0 )
		{
			Scheduler.ScheduledCommand cmd;
			
			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute()
				{
					// Invoke the edit rights dialog.
					invokeEditShareDlg( listOfShareItems );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
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
		Widget sourceWidget;
		
		sourceWidget = (Widget) event.getSource();
		
		if ( sourceWidget == null )
			return;
		
		// Is this meant to add a share with someone?
		if ( sourceWidget.equals( this ) )
		{
			final GwtTeamingItem selectedObj;
			Scheduler.ScheduledCommand cmd;

			// Yes
			selectedObj = event.getSearchResults();
			
			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute() 
				{
					final ArrayList<GwtShareItem> listOfShareItems;
					
					// Hide the search-results widget.
					m_findCtrl.hideSearchResults();
					
					// Clear the text from the find control.
					m_findCtrl.clearText();

					// Create a GwtShareItem for every entity we are sharing with.
					listOfShareItems = addShare( selectedObj );
					
					if ( listOfShareItems != null && listOfShareItems.size() > 0 )
					{
						Scheduler.ScheduledCommand cmd;
						
						cmd = new Scheduler.ScheduledCommand()
						{
							@Override
							public void execute()
							{
								InvokeEditShareRightsDlgEvent event;

								// Fire an event to invoke the "edit share rights" dialog.
								event = new InvokeEditShareRightsDlgEvent( listOfShareItems );
								GwtTeaming.fireEvent( event );
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
		// Is this meant to identify a user/file/folder to search for shares by?
		else if ( sourceWidget.equals( m_manageShareItemsPanel ) )
		{
			final String findBy;
			final GwtTeamingItem selectedObj;
			Scheduler.ScheduledCommand cmd;

			// Yes
			selectedObj = event.getSearchResults();
			
			// Get how the users wants to find the shares
			findBy = getFindBy();

			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute() 
				{
					// Hide the search-results widget.
					m_manageSharesFindCtrl.hideSearchResults();
					
					findSharesBy( findBy, selectedObj );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
		
	}// end onSearchFindResults()
	
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
	
	/**
	 * Validate the given email address.  If it is valid add it to the list of recipients
	 */
	private void validateEmailAddress( final String emailAddress )
	{
		AsyncCallback<VibeRpcResponse> validationCallback;

		validationCallback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable caught )
			{
				GwtClientHelper.handleGwtRPCFailure(
												caught,
												GwtTeaming.getMessages().rpcFailure_ValidateEmailAddress() );
			}

			@Override
			public void onSuccess( final VibeRpcResponse vibeResult )
			{
				Scheduler.ScheduledCommand cmd;
				
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						boolean addToRecipientList = false;
						
						if ( vibeResult.getResponseData() != null )
						{
							EmailAddressStatus emaStatus;
							ValidateEmailRpcResponseData responseData;
							
							// Is the email valid?
							responseData = (ValidateEmailRpcResponseData) vibeResult.getResponseData();
							emaStatus = responseData.getEmailAddressStatus();
							if ( emaStatus.isValid() )
							{
								// Yes
								addToRecipientList = true;
							}
							else
							{
								GwtTeamingMessages messages = GwtTeaming.getMessages();
								switch ( emaStatus )
								{
								case failsBlacklistDomain:
								case failsBlacklistEMA:
								case failsWhitelist:
									String msg = null;
									switch ( emaStatus )
									{
									case failsBlacklistDomain:  msg = messages.shareDlg_emailAddressInvalid_blDomain(); break;
									case failsBlacklistEMA:     msg = messages.shareDlg_emailAddressInvalid_blEMA();    break;
									case failsWhitelist:        msg = messages.shareDlg_emailAddressInvalid_wl();       break;
									}
									// Tell the user about the problem...
									GwtClientHelper.deferredAlert( msg );
									
									// ...and put the email address back in the find control
									m_findCtrl.setInitialSearchString( emailAddress );
									break;
									
								default:
								case failsFormat:
									// No, ask the user if they still want to share with this email address.
									if ( Window.confirm( messages.shareDlg_emailAddressInvalidPrompt() ) == true )
										addToRecipientList = true;
									break;
								}
							}
						}
						
						// Should we add the email address to the list of recipients?
						if ( addToRecipientList )
						{
							ArrayList<GwtShareItem> listOfShareItems;
							GwtUser gwtUser;
							Long userId = null;

							// Yes
							gwtUser = new GwtUser();
							gwtUser.setInternal( false );
							gwtUser.setUserType( UserType.EXTERNAL_OTHERS );
							gwtUser.setName( emailAddress );
							gwtUser.setUserId( userId );
							gwtUser.setEmail( emailAddress );
							
							listOfShareItems = addShare( gwtUser );

							if ( listOfShareItems != null )
							{
								InvokeEditShareRightsDlgEvent event;

								// Fire an event to invoke the "edit share rights" dialog.
								event = new InvokeEditShareRightsDlgEvent( listOfShareItems );
								GwtTeaming.fireEvent( event );
							}
						}
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}				
		};

		// Issue an ajax request to validate the email address.
		ValidateEmailAddressCmd cmd = new ValidateEmailAddressCmd(
															emailAddress,
															true,	// true -> Validate as an external email address.
															ValidateEmailAddressCmd.AddressField.MAIL_TO );
		GwtClientHelper.executeCommand( cmd, validationCallback );
	}


	/**
	 * Create all of the controls that need to be created via GWT.runAsync(...)
	 */
	private void createControlsAsync( LoadAsyncControlsCallback callback )
	{
		createFindCtrl( callback );
	}
	
	/**
	 * Create the FindControl that is used to search for users and groups.
	 */
	private void createFindCtrl( final LoadAsyncControlsCallback callback )
	{
		FindCtrl.createAsync( this, GwtSearchCriteria.SearchType.PRINCIPAL, new FindCtrlClient()
		{			
			@Override
			public void onUnavailable()
			{
				callback.onFailure();
			}
			
			@Override
			public void onSuccess( FindCtrl findCtrl )
			{
				Scheduler.ScheduledCommand cmd;
				
				m_findCtrl = findCtrl;

				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						createFindCtrl2( callback );
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		});
	}
	
	/**
	 * Create the FindControl that is used to search for users/files/folders. 
	 */
	private void createFindCtrl2( final LoadAsyncControlsCallback callback )
	{
		FindCtrl.createAsync( this, GwtSearchCriteria.SearchType.USER, new FindCtrlClient()
		{			
			@Override
			public void onUnavailable()
			{
				callback.onFailure();
			}
			
			@Override
			public void onSuccess( FindCtrl findCtrl )
			{
				Scheduler.ScheduledCommand cmd;
				
				m_manageSharesFindCtrl = findCtrl;
				
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						// Now that we have loaded the necessary controls.  Create the static
						// content in the dialog
						createStaticContent();
						
						// Tell the callback we were successful.
						callback.onSuccess();
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		});
	}

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the share this dialog and perform some operation on it.       */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the share this dialog
	 * asynchronously after it loads. 
	 */
	public interface ShareThisDlg2Client
	{
		void onSuccess( ShareThisDlg2 stDlg );
		void onUnavailable();
	}
	
	/**
	 * Callback interface used to indicate when we have loaded all controls that need to
	 * be loaded via a call to GWT.runAsync(...)
	 */
	private interface LoadAsyncControlsCallback
	{
		void onFailure();
		void onSuccess();
	}

	/**
	 * Create the dialog through the GWT.runAsync() method to ensure the code is in this split point.
	 */
	public static void createDlg(
		final boolean autoHide,
		final boolean modal,
		final int left,
		final int top,
		final Integer width,
		final Integer height,
		final ShareThisDlgMode mode,
		final ShareThisDlg2Client stDlgClient )
	{
		GWT.runAsync( ShareThisDlg2.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ShareThisDlg() );
				if ( stDlgClient != null )
				{
					stDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				final ShareThisDlg2 stDlg;
				LoadAsyncControlsCallback callback;

				stDlg = new ShareThisDlg2(
									autoHide,
									modal,
									left,
									top,
									width,
									height,
									mode );

				// createControlsAsync() will call callback.onSuccess() after it finishes creating
				// the controls that need to be created via GWT.runAsync(...)
				callback = new LoadAsyncControlsCallback()
				{
					@Override
					public void onFailure()
					{
						stDlgClient.onUnavailable();
					}

					@Override
					public void onSuccess()
					{
						if ( stDlgClient != null )
							stDlgClient.onSuccess( stDlg );
					}
				};
				stDlg.createControlsAsync( callback );
			}
		} );
	}

	/**
	 * Initialize and show the dialog through the GWT.runAsync() method to ensure
	 * the executing code is in this split point.
	 */
	public static void initAndShow(
		final ShareThisDlg2 dlg,
		final String caption,
		final List<EntityId> entityIds,
		final ShareThisDlgMode mode,
		final Integer left,
		final Integer top,
		final Integer width,
		final Integer height,
		final Boolean showCentered,
		final ShareThisDlg2Client stDlgClient )
	{
		GWT.runAsync( ShareThisDlg2.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ShareThisDlg() );
				if ( stDlgClient != null )
				{
					stDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				if ( width != null && height != null )
					dlg.setPixelSize( width, height );
				
				dlg.init(
						caption,
						entityIds,
						mode );
				
				if ( left != null && top != null )
					dlg.setPopupPosition( left, top );
				
				if ( showCentered != null && showCentered == true )
					dlg.showDlg( null );
				else
					dlg.showDlg();
			}
		} );
	}

	/**
	 * Initialize and show the dialog through the GWT.runAsync() method to ensure
	 * the executing code is in this split point.
	 */
	public static void initAndShow(
		final ShareThisDlg2 dlg,
		final UIObject target,
		final String caption,
		final List<EntityId> entityIds,
		final ShareThisDlgMode mode,
		final ShareThisDlg2Client stDlgClient )
	{
		GWT.runAsync( ShareThisDlg2.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ShareThisDlg() );
				if ( stDlgClient != null )
				{
					stDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				dlg.init(
						caption,
						entityIds,
						mode );
				
				if ( target == null )
					dlg.show( true );
				else
					dlg.showRelativeToTarget( target );
			}
		} );
	}
}

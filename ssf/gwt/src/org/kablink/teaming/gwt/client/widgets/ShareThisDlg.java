/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
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
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.mainmenu.TeamInfo;
import org.kablink.teaming.gwt.client.rpc.shared.GetDateStrCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetEntryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetMyTeamsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetMyTeamsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetSharingInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SendShareNotificationEmailCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ValidateEmailAddressCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ValidateEmailRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.rpc.shared.ShareEntryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ShareEntryResultsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ValidateEmailRpcResponseData.EmailAddressStatus;
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
import org.kablink.teaming.gwt.client.util.PrincipalType;
import org.kablink.teaming.gwt.client.widgets.EditShareNoteDlg.EditShareNoteDlgClient;
import org.kablink.teaming.gwt.client.widgets.EditShareRightsDlg.EditShareRightsDlgClient;
import org.kablink.teaming.gwt.client.widgets.FindCtrl;
import org.kablink.teaming.gwt.client.widgets.FindCtrl.FindCtrlClient;
import org.kablink.teaming.gwt.client.widgets.ShareExpirationDlg.ShareExpirationDlgClient;
import org.kablink.teaming.gwt.client.widgets.ShareSendToWidget.SendToValue;
import org.kablink.teaming.gwt.client.widgets.ShareWithPublicInfoDlg.ShareWithPublicInfoDlgClient;
import org.kablink.teaming.gwt.client.widgets.ShareWithTeamsDlg.ShareWithTeamsDlgClient;

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
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
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
		SearchFindResultsEvent.Handler,
		InvokeEditShareRightsDlgEvent.Handler
{
	private int m_numCols = 0;
	
	private ShareThisDlgMode m_mode;
	private Image m_headerImg;
	private Label m_headerNameLabel;
	private Label m_headerPathLabel;
	private FindCtrl m_findCtrl;
	private FindCtrl m_manageSharesFindCtrl;
	private CheckBox m_notifyCheckbox;
	private Image m_addExternalUserImg;
	private FlowPanel m_mainPanel;
	private ImageResource m_deleteImgR;
	private FlexTable m_mainTable;
	private FlexTable m_shareTable;
	private InlineLabel m_shareWithTeamsLabel;
	private InlineLabel m_manageSharesFindCtrlLabel;
	private FlowPanel m_shareTablePanel;
	private ShareSendToWidget m_sendToWidget;
	private FlowPanel m_makePublicPanel;
	private FlowPanel m_manageShareItemsPanel;
	private ListBox m_findByListbox;
	private FlexCellFormatter m_shareCellFormatter;
	private HTMLTable.RowFormatter m_shareRowFormatter;
	private int m_rightsCol;
	private List<EntityId> m_entityIds;
	private GwtSharingInfo m_sharingInfo;		// Holds all of the sharing info for the entities we are working with.
	private List<TeamInfo> m_listOfTeams;
	private List<HandlerRegistration> m_registeredEventHandlers;
	private AsyncCallback<VibeRpcResponse> m_readTeamsCallback;
	private AsyncCallback<VibeRpcResponse> m_shareEntryCallback;
	private AsyncCallback<VibeRpcResponse> m_getSharingInfoCallback;
	private AsyncCallback<VibeRpcResponse> m_sendNotificationEmailCallback;
	private UIObject m_target;
	private ShareExpirationValue m_defaultShareExpirationValue;
	private ShareExpirationDlg m_shareExpirationDlg;
	private EditShareNoteDlg m_editShareNoteDlg;
	private ShareWithTeamsDlg m_shareWithTeamsDlg;
	private ShareWithPublicInfoDlg m_shareWithPublicInfoDlg=null;
	private EditShareRightsDlg m_editShareRightsDlg = null;
	private EditSuccessfulHandler m_editShareWithTeamsHandler;
	private EditSuccessfulHandler m_editShareRightsHandler;
	private GwtTeamingDataTableImageBundle m_dtImages;
	
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
	
	/**
	 * This widget is used to display a recipient's name.  If the recipient is a group
	 * then the user can click on the name and see the members of the group.
	 */
	private class RecipientNameWidget extends Composite
		implements ClickHandler, MouseOverHandler, MouseOutHandler
	{
		private GwtShareItem m_shareItem;
		private InlineLabel m_nameLabel;
		private GroupMembershipPopup m_groupMembershipPopup;
		
		/**
		 * 
		 */
		public RecipientNameWidget( GwtShareItem shareItem )
		{
			FlowPanel panel;
			
			m_shareItem = shareItem;
			
			panel = new FlowPanel();
			
			m_nameLabel = new InlineLabel( shareItem.getRecipientName() );
			m_nameLabel.setTitle( shareItem.getRecipientName() );
			m_nameLabel.addStyleName( "shareThisDlg_RecipientNameLabel" );
			panel.add( m_nameLabel );
			
			// Has the share expired?
			if ( shareItem.isExpired() )
			{
				// Yes
				m_nameLabel.addStyleName( "shareThisDlg_ShareExpired" );
			}
			else
				m_nameLabel.removeStyleName( "shareThisDlg_ShareExpired" );
			
			// If we are dealing with a group, let the user click on the group.
			if ( shareItem.getRecipientType() == GwtRecipientType.GROUP )
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
															m_shareItem.getRecipientName(),
															m_shareItem.getRecipientId().toString() );
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
			m_nameLabel.removeStyleName( "shareThisDlg_NameHover" );
		}

		
		/**
		 * Add the mouse-over style to the name.
		 */
		@Override
		public void onMouseOver( MouseOverEvent event )
		{
			m_nameLabel.addStyleName( "shareThisDlg_NameHover" );
		}
	}
	
	/**
	 * This widget is used to display a recipient's type.  If the recipient is Public
	 * then the user can click on the name and see the url they can pass out for people
	 * to access the given entity.
	 */
	private class RecipientTypeWidget extends Composite
		implements ClickHandler
	{
		private GwtShareItem m_shareItem;
		private Image m_typeImage;
		
		/**
		 * 
		 */
		public RecipientTypeWidget( GwtShareItem shareItem )
		{
			FlowPanel panel;
			
			m_shareItem = shareItem;
			
			panel = new FlowPanel();
			panel.addStyleName( "shareThisDlg_RecipientTypeImagePanel" );
			
			m_typeImage = buildRecipientImage( shareItem );
			m_typeImage.setTitle( shareItem.getRecipientTypeAsString() );
			m_typeImage.addStyleName( "shareThisDlg_RecipientTypeImage" );
			panel.add( m_typeImage );
			
			// If we are dealing with a share with public, let the user click on the group.
			if ( shareItem.getRecipientType() == GwtRecipientType.PUBLIC_TYPE )
			{
				m_typeImage.addStyleName( "shareThisDlg_PublicRecipientTypeImage" );
				m_typeImage.setTitle( GwtTeaming.getMessages().shareDlg_sharePublicTitle() );
				m_typeImage.addClickHandler( this );
			}
			
			// All composites must call initWidget() in their constructors.
			initWidget( panel );
		}
		
		/*
		 * 
		 */
		private Image buildRecipientImage( GwtShareItem shareItem )
		{
			ImageResource ir;
			switch ( shareItem.getRecipientType() )
			{
			case USER:
			case EXTERNAL_USER:  ir = shareItem.getRecipientPrincipalTypeImage(); break;
			case GROUP:          ir = m_dtImages.groupType_Local();          break;
			case TEAM:           ir = m_dtImages.team();                     break;
			case PUBLIC_TYPE:    ir = m_dtImages.publicSharee();             break;
			default:             ir = m_dtImages.userPhoto();                break;
			}
			return GwtClientHelper.buildImage(ir);
		}
		
		/**
		 * This gets called when the user clicks on the recipient's type.  This will only
		 * be called if the recipient is public.
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
					invokeShareWithPublicInfoDlg();
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
		
		/**
		 * 
		 */
		private void invokeShareWithPublicInfoDlg()
		{
			if ( m_shareWithPublicInfoDlg == null )
			{
				ShareWithPublicInfoDlg.createAsync( new ShareWithPublicInfoDlgClient()
				{
					@Override
					public void onUnavailable() 
					{
						// Nothing to do.  Error handled in asynchronous provider.
					}
					
					@Override
					public void onSuccess( ShareWithPublicInfoDlg swpiDlg )
					{
						m_shareWithPublicInfoDlg = swpiDlg;
						invokeShareWithPublicInfoDlg();
					}
				} );
			}
			else
			{
				m_shareWithPublicInfoDlg.init( m_shareItem.getEntityId() );
				m_shareWithPublicInfoDlg.showRelativeToTarget( m_typeImage );
			}
		}
	}
	
	/**
	 * This widget is used to display the expiration for a given share and allow the user
	 * to change the expiration.
	 */
	private class ShareExpirationWidget extends Composite
		implements ClickHandler
	{
		private GwtShareItem m_shareItem;
		private InlineLabel m_expiresLabel;
		private Image m_img;
		private EditSuccessfulHandler m_editShareExpirationHandler;
		private AsyncCallback<VibeRpcResponse> m_getDateStrCallback = null;

		/**
		 * 
		 */
		public ShareExpirationWidget( GwtShareItem shareItem )
		{
			ImageResource imageResource;
			
			m_shareItem = shareItem;

			m_expiresLabel = new InlineLabel();
			m_expiresLabel.addStyleName( "shareThisDlg_ExpiresLabel" );
			m_expiresLabel.addClickHandler( this );
			
			// Has the share expired?
			if ( shareItem.isExpired() )
			{
				// Yes
				m_expiresLabel.addStyleName( "shareThisDlg_ShareExpired" );
			}
			else
				m_expiresLabel.removeStyleName( "shareThisDlg_ShareExpired" );
			
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
								m_shareItem.setShareExpirationValue( expirationValue );
								m_shareItem.setIsDirty( true );
								
								updateExpirationLabel();
							}
							
							return true;
						}
					};
				}
				
				// Invoke the "share expiration" dialog.
				m_shareExpirationDlg.init( m_shareItem.getShareExpirationValue(), m_editShareExpirationHandler );
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
			if ( m_shareItem != null )
			{
				// Get the appropriate string that represents the expiration value
				{
					ShareExpirationValue expirationValue;
					
					expirationValue = m_shareItem.getShareExpirationValue();
					if ( expirationValue != null )
					{
						String after;
						Long value;
						
						value = expirationValue.getValue();
						
						switch ( expirationValue.getExpirationType() )
						{
						case NEVER:
							m_expiresLabel.setText( GwtTeaming.getMessages().shareDlg_expiresNever() );
							break;
						
						case AFTER_DAYS:
							after = "";
							if ( value != null )
							{
								if ( value < 0 )
									value = Long.valueOf( 0 );
								
								after = value.toString();
							}
							
							m_expiresLabel.setText( GwtTeaming.getMessages().shareDlg_expiresAfter( after ) );
							break;
						
						case ON_DATE:
							if ( value != null )
								updateExpirationLabel( value );
							else
								m_expiresLabel.setText( GwtTeaming.getMessages().shareDlg_expiresOn( "" ) );
							
							break;
							
						case UNKNOWN:
						default:
							m_expiresLabel.setText( "Unknown expiration type" );
							break;
						}
					}
					else
						m_expiresLabel.setText( "" );
				}
				
				m_expiresLabel.getElement().appendChild( m_img.getElement() );
			}
		}
		
		/**
		 * Issue an rpc request to get the date/time string for the given expiration value.
		 * Then update the given label.
		 */
		private void updateExpirationLabel( Long value )
		{
			GetDateStrCmd cmd;
			
			if ( m_getDateStrCallback == null )
			{
				m_getDateStrCallback = new AsyncCallback<VibeRpcResponse>()
				{
					/**
					 * 
					 */
					@Override
					public void onFailure( Throwable t )
					{
						GwtClientHelper.handleGwtRPCFailure(
													t,
													GwtTeaming.getMessages().rpcFailure_GetDateStr() );
					}
			
					/**
					 * 
					 * @param result
					 */
					@Override
					public void onSuccess( VibeRpcResponse response )
					{
						StringRpcResponseData responseData = null;
						
						if ( response.getResponseData() instanceof StringRpcResponseData )
							responseData = (StringRpcResponseData) response.getResponseData();
						
						if ( responseData != null )
						{
							String dateTimeStr;
							
							dateTimeStr = responseData.getStringValue();
							if ( dateTimeStr != null )
								m_expiresLabel.setText( dateTimeStr );
						}
					}
				};
			}
			
			// Issue an rpc request to get the date/time string.
			cmd = new GetDateStrCmd( value, DateFormat.SHORT );
			GwtClientHelper.executeCommand( cmd, m_getDateStrCallback );
		}
	}
	
	/**
	 * This widget is used to remove a share from the list of shares
	 */
	private class RemoveShareWidget extends Composite
		implements ClickHandler
	{
		private GwtShareItem m_shareItem;
		
		/**
		 * 
		 */
		public RemoveShareWidget( GwtShareItem shareItem )
		{
			FlowPanel panel;
			Image delImg;
			
			m_shareItem = shareItem;
			
			panel = new FlowPanel();
			panel.addStyleName( "shareThisDlg_RemoveRecipientPanel" );
			
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
		public GwtShareItem getShareItem()
		{
			return m_shareItem;
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
					removeShare( m_shareItem );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
	}
	
	/**
	 * This widget is used to allow the user to edit an existing note.
	 */
	private class NoteWidget extends Composite
		implements ClickHandler
	{
		private GwtShareItem m_shareItem;
		private InlineLabel m_noteLabel;
		private EditSuccessfulHandler m_editNoteHandler;
		
		/**
		 * 
		 */
		public NoteWidget( GwtShareItem shareItem )
		{
			m_shareItem = shareItem;
			
			m_noteLabel = new InlineLabel();
			m_noteLabel.addStyleName( "shareThisDlg_NoteLabel" );
			m_noteLabel.addClickHandler( this );
			
			updateNoteLabel();
			
			// All composites must call initWidget() in their constructors.
			initWidget( m_noteLabel );
		}
		
		/**
		 * Invoke the "Edit Note" dialog
		 */
		private void invokeEditNoteDlg()
		{
			if ( m_editShareNoteDlg != null )
			{
				if ( m_editNoteHandler == null )
				{
					m_editNoteHandler = new EditSuccessfulHandler()
					{
						@Override
						public boolean editSuccessful( Object obj )
						{
							if ( obj instanceof String )
							{
								String note;
								
								note = (String) obj;
								m_shareItem.setComments( note );
								m_shareItem.setIsDirty( true );
								
								updateNoteLabel();
							}
							
							return true;
						}
					};
				}
				
				// Invoke the "share expiration" dialog.
				m_editShareNoteDlg.init( m_shareItem.getComments(), m_editNoteHandler );
				m_editShareNoteDlg.showRelativeToTarget( m_noteLabel );
			}
		}
		/**
		 * This gets called when the user clicks on the note.
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
					if ( m_editShareNoteDlg == null )
					{
						EditShareNoteDlg.createAsync( true, true, new EditShareNoteDlgClient()
						{
							@Override
							public void onUnavailable() 
							{
								// Nothing to do.  Error handled in asynchronous provider.
							}
							
							@Override
							public void onSuccess( EditShareNoteDlg esnDlg )
							{
								m_editShareNoteDlg = esnDlg;
								invokeEditNoteDlg();
							}
						} );
					}
					else
						invokeEditNoteDlg();
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
		
		/**
		 * Update the contents of this note.
		 */
		public void updateNoteLabel()
		{
			String note;
			String noteTitle;

			note = m_shareItem.getComments();
			noteTitle = note;

			m_noteLabel.removeStyleName( "shareThisDlg_NoNote" );

			if ( note != null && note.length() > 14 )
			{
				note = note.substring( 0, 14 );
				note += "...";
			}
			else if ( note == null || note.length() == 0 )
			{
				note = GwtTeaming.getMessages().shareDlg_noNote();
				noteTitle = GwtTeaming.getMessages().shareDlg_clickToAddNote();
				m_noteLabel.addStyleName( "shareThisDlg_NoNote" );
			}

			m_noteLabel.setText( note );
			m_noteLabel.setTitle( noteTitle );
		}
	}
	


	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ShareThisDlg( boolean autoHide, boolean modal )
	{
		// Initialize the superclass.
		super( autoHide, modal );

		// Initialize other data members that need it.
		m_dtImages = GwtTeaming.getDataTableImageBundle();
		
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
		m_shareCellFormatter.setColSpan( row, 0, m_numCols );
		m_shareCellFormatter.setWordWrap( row, 0, false );
		m_shareCellFormatter.addStyleName( row, 0, "oltBorderLeft" );
		m_shareCellFormatter.addStyleName( row, 0, "oltBorderRight" );
		m_shareCellFormatter.addStyleName( row, 0, "oltContentPadding" );
		m_shareCellFormatter.addStyleName( row, 0, "oltLastRowBorderBottom" );

		m_shareTable.setText( row, 0, GwtTeaming.getMessages().noShareRecipients() );
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
						Window.alert( GwtTeaming.getMessages().shareDlg_cantShareWithExternalUser_Param( "" ) );
						return null;
					}
				}
				
				shareItem = new GwtShareItem();
				shareItem.setRecipientName( user.getName() );
				if ( !user.isInternal() )
					shareItem.setRecipientType( GwtRecipientType.EXTERNAL_USER );
				else
					shareItem.setRecipientType( GwtRecipientType.USER );
				shareItem.setRecipientPrincipalType( user.getPrincipalType() );
				
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
				shareItem.setRecipientPrincipalType( PrincipalType.UNKNOWN );
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
				shareItem.setRecipientPrincipalType( PrincipalType.UNKNOWN );
				shareItem.setRecipientId( publicEntity.getIdLong() );
			}

			// Do we have an object to add to our list of shares?
			if ( shareItem != null )
			{
				// Yes
				shareItem.setEntityId( nextEntityId );
				shareItem.setEntityName( getEntityName( nextEntityId ) );
				
				// Has the item already been shared with the recipient
				if ( findShareItem( shareItem ) == -1 )
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
	 * Add the given share to the end of the table that holds the list of shares
	 */
	private void addShare( GwtShareItem shareItem, boolean highlight )
	{
		int row;
		int col;
		int i;
		RemoveShareWidget removeWidget;
		RecipientNameWidget recipientNameWidget;
		RecipientTypeWidget recipientTypeWidget;
		
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
		
		col = 0;
		
		// Add the recipient name
		m_shareCellFormatter.setColSpan( row, col, 1 );
		m_shareCellFormatter.setWordWrap( row, col, false );
		m_shareCellFormatter.addStyleName( row, col, "shareThisDlg_RecipientTable_Cell" );
		recipientNameWidget = new RecipientNameWidget( shareItem );
		m_shareTable.setWidget( row, col,  recipientNameWidget );
		++col;

		// Add the recipient type
		m_shareCellFormatter.setWordWrap( row, col, false );
		m_shareCellFormatter.addStyleName( row, col, "shareThisDlg_RecipientTable_Cell" );
		recipientTypeWidget = new RecipientTypeWidget( shareItem );
		m_shareTable.setWidget( row, col, recipientTypeWidget );
		++col;
		
		// Are we in "administrative" mode?
		if ( m_mode == ShareThisDlgMode.MANAGE_SELECTED || m_mode == ShareThisDlgMode.MANAGE_ALL )
		{
			String name;
			InlineLabel label;

			// Yes, add a field for "shared by"
			m_shareCellFormatter.setWordWrap( row, col, false );
			m_shareCellFormatter.addStyleName( row, col, "shareThisDlg_RecipientTable_Cell" );
			
			label = new InlineLabel();
			
			// Only show the first 15 characters
			name = shareItem.getSharedByName();
			label.setText( name );
			m_shareTable.setWidget( row, col, label );
			++col;
		}
		
		// Are we sharing more than 1 entity or are we in "manage all" mode?
		if ( (m_entityIds != null && m_entityIds.size() > 1) || m_mode == ShareThisDlgMode.MANAGE_ALL )
		{
			String entityName;
			InlineLabel label;
			
			// Yes
			// Add the "Item name"
			m_shareCellFormatter.setWordWrap( row, col, false );
			m_shareCellFormatter.addStyleName( row, col, "shareThisDlg_RecipientTable_Cell" );
			
			label = new InlineLabel();
			
			// Only show the first 15 characters
			entityName = shareItem.getEntityName();
			label.setTitle( entityName );
			if ( entityName != null && entityName.length() > 15 )
			{
				entityName = entityName.substring( 0, 12 );
				entityName += "...";
			}
			label.setText( entityName );
			m_shareTable.setWidget( row, col, label );
			++col;
		}
		
		// Add the share rights
		{
			ShareRightsWidget rightsWidget;
			
			m_shareCellFormatter.setWordWrap( row, col, false );
			m_shareCellFormatter.addStyleName( row, col, "shareThisDlg_RecipientTable_Cell" );
			rightsWidget = new ShareRightsWidget(
											shareItem,
											m_sharingInfo.getShareRights( shareItem.getEntityId() ) );
			m_shareTable.setWidget( row, col, rightsWidget );
			m_rightsCol = col;
			++col;
		}
		
		// Add the expires values
		{
			ShareExpirationWidget expirationWidget;
			
			m_shareCellFormatter.setWordWrap( row, col, false );
			m_shareCellFormatter.addStyleName( row, col, "shareThisDlg_RecipientTable_Cell" );
			expirationWidget = new ShareExpirationWidget( shareItem );
			m_shareTable.setWidget( row, col, expirationWidget );
			++col;
		}
		
		// Add the "Note" column
		{
			NoteWidget noteWidget;
			
			m_shareCellFormatter.setWordWrap( row, col, false );
			m_shareCellFormatter.addStyleName( row, col, "shareThisDlg_RecipientTable_Cell" );
			noteWidget = new NoteWidget( shareItem );
			m_shareTable.setWidget( row, col, noteWidget );
			++col;
		}

		// Add the "remove share" widget
		{
			removeWidget = new RemoveShareWidget( shareItem );
			m_shareTable.setWidget( row, col, removeWidget );
			++col;
		}
		
		// Add the necessary styles to the cells in the row.
		m_shareCellFormatter.addStyleName( row, 0, "oltBorderLeft" );
		m_shareCellFormatter.addStyleName( row, m_numCols-1, "oltBorderRight" );
		for (i = 0; i < m_numCols; ++i)
		{
			m_shareCellFormatter.addStyleName( row, i, "oltContentBorderBottom" );
			m_shareCellFormatter.addStyleName( row, i, "oltContentPadding" );
		}
		
		adjustShareTablePanelHeight();
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
					m_shareTablePanel.addStyleName( "shareThisDlg_RecipientTablePanelHeight" );
				else
					m_shareTablePanel.removeStyleName( "shareThisDlg_RecipientTablePanelHeight" );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
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

		return m_mainPanel;
	}
	
	/**
	 * Create all the controls needed to share this item with others.
	 */
	private FlexTable createShareControls()
	{
		HTMLTable.RowFormatter mainRowFormatter;
		FlexCellFormatter mainCellFormatter;
		int row;
		
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
			anchor = new Anchor( GwtTeaming.getMessages().shareDlg_makePublic() );
			anchor.addStyleName( "gwt-Button" );
			anchor.addStyleName( "teamingButton" );
			anchor.addClickHandler( clickHandler );
			anchor.setTitle( GwtTeaming.getMessages().shareDlg_makePublic() );
			m_makePublicPanel.add( anchor );
			
			imgResource = GwtTeaming.getImageBundle().public16();
			img = new Image( imgResource );
			img.getElement().getStyle().setVerticalAlign( VerticalAlign.MIDDLE );
			img.getElement().getStyle().setPaddingRight( 3, Unit.PX );
			anchor.getElement().insertFirst( img.getElement() );
		}
		
		m_mainTable = new FlexTable();
		m_mainTable.setCellSpacing( 6 );
		
		mainRowFormatter = m_mainTable.getRowFormatter();
		mainCellFormatter = m_mainTable.getFlexCellFormatter();
		row = 0;
		
		mainCellFormatter.setVerticalAlignment( row, 0, HasVerticalAlignment.ALIGN_MIDDLE );

		m_defaultShareExpirationValue = new ShareExpirationValue();
		m_defaultShareExpirationValue.setType( ShareExpirationType.NEVER );
		
		// Add the find control.
		{
			Label shareLabel;
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
			rowFormatter = findTable.getRowFormatter();
			rowFormatter.setVerticalAlign( 0, HasVerticalAlignment.ALIGN_TOP );
			m_findCtrl.setIsSendingEmail( true );
			findTable.setWidget( 0, 0, m_findCtrl );
			
			shareLabel = new Label( GwtTeaming.getMessages().shareDlg_shareLabel() );
			shareLabel.addStyleName( "shareThisDlg_shareLabel" );
			m_mainTable.setWidget( row, 0, shareLabel );
			m_mainTable.setWidget( row, 1, findTable );
			mainRowFormatter.setVerticalAlign( row, HasVerticalAlignment.ALIGN_TOP );
			// On IE calling m_cellFormatter.setWidth( row, col, "*" ); throws an exception.
			// That is why we are calling DOM.setElementAttribute(...) instead.
			//mainCellFormatter.setWidth( row, 1, "*" );
			mainCellFormatter.getElement( row, 1 ).setAttribute( "width", "*" );
			
			// Add an "add external user" image.
			{
				ClickHandler clickHandler;
				ImageResource imageResource;
				FlexCellFormatter findCellFormatter;
				
				imageResource = GwtTeaming.getImageBundle().add_btn();
				m_addExternalUserImg = new Image( imageResource );
				m_addExternalUserImg.addStyleName( "cursorPointer" );
				m_addExternalUserImg.getElement().setAttribute( "title", GwtTeaming.getMessages().shareDlg_addExternalUserTitle() );
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
					m_shareWithTeamsLabel = new InlineLabel( GwtTeaming.getMessages().shareWithTeams() );
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

			++row;
		}
		
		// Create a table to hold the list of shares
		{
			m_shareTablePanel = new FlowPanel();
			m_shareTablePanel.addStyleName( "shareThisDlg_RecipientTablePanel" );
			
			m_shareTable = new FlexTable();
			m_shareTable.addStyleName( "shareThisDlg_RecipientTable" );
			m_shareTable.setCellSpacing( 0 );

			m_shareTablePanel.add( m_shareTable );

			mainRowFormatter.setVerticalAlign( row, HasVerticalAlignment.ALIGN_TOP );

			m_mainTable.setWidget( row, 0, m_shareTablePanel );
			mainCellFormatter.setColSpan( row, 0, 2 );
			
			++row;
		}

		// Create an image resource for the delete image.
		m_deleteImgR = GwtTeaming.getImageBundle().delete();

		return m_mainTable;
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
										int row;

										// Find the recipient in the table.
										row = findShareItem( nextSuccess.getGwtShareItem() );
										if ( row >= 0 )
										{
											GwtShareItem shareItem;
											
											shareItem = getShareItem( row );
											if ( shareItem != null )
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

		// Disable the Ok button.
		showStatusMsg( GwtTeaming.getMessages().shareDlg_savingShareInfo() );
		setOkEnabled( false );
		
		sharingData = new GwtSharingInfo();
		sharingData.setEntityNamesMap( m_sharingInfo.getEntityNamesMap() );
		listOfShareItems = getListOfShareItemsFromDlg();
		sharingData.setListOfShareItems( listOfShareItems );
		sharingData.setListOfToBeDeletedShareItems( m_sharingInfo.getListOfToBeDeletedShareItems() );
		
		// Get who should be notified.
		sharingData.setNotifyRecipients( m_notifyCheckbox.getValue() );
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
	private int findShareItem( GwtShareItem shareItem )
	{
		int i;

		if ( shareItem == null )
			return -1;
		
		// Look through the table for the given GwtShareItem.
		// Recipients start in row 1.
		for (i = 1; i < m_shareTable.getRowCount() && m_shareTable.getCellCount( i ) == m_numCols; ++i)
		{
			Widget widget;
			
			// Get the RemoveRecipientWidget from the last column.
			widget = m_shareTable.getWidget( i, m_numCols-1 );
			if ( widget != null && widget instanceof RemoveShareWidget )
			{
				GwtShareItem nextShareItem;
				
				nextShareItem = ((RemoveShareWidget) widget).getShareItem();
				if ( nextShareItem != null )
				{
					if ( shareItem.equals( nextShareItem ) )
					{
						// We found the recipient
						return i;
					}
				}
			}
		}// end for()
		
		// If we get here we did not find the recipient.
		return -1;
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
					entityId = new EntityId(
						gwtFolderEntry.getParentBinderId(),
						Long.valueOf( gwtFolderEntry.getEntryId() ),
						EntityId.FOLDER_ENTRY );

					listOfEntityIds.add( entityId );
				}
				else if ( selectedItem instanceof GwtFolder )
				{
					GwtFolder gwtFolder;
					EntityId entityId;
					
					gwtFolder = (GwtFolder) selectedItem;
					entityId = new EntityId(
						Long.valueOf( gwtFolder.getFolderId() ),
						EntityId.FOLDER );

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
		int i;
		ArrayList<GwtShareItem> listOfShareItems;
		
		listOfShareItems = new ArrayList<GwtShareItem>();
		
		// Look through the table and add each GwtShareItem to the list.
		for (i = 1; i < m_shareTable.getRowCount() && m_shareTable.getCellCount( i ) == m_numCols; ++i)
		{
			Widget widget;
			
			// Get the RemoveRecipientWidget from the last column.
			widget = m_shareTable.getWidget( i, m_numCols-1 );
			if ( widget != null && widget instanceof RemoveShareWidget )
			{
				GwtShareItem nextShareItem;
				
				nextShareItem = ((RemoveShareWidget) widget).getShareItem();
				listOfShareItems.add( nextShareItem );
			}
		}
		
		return listOfShareItems;
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
			shareItem.setRecipientPrincipalType( PrincipalType.UNKNOWN );

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
					if ( findShareItem( shareItem ) == -1 )
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
	 * Return the GwtShareItem from the given row in the table that holds the recipients.
	 */
	private GwtShareItem getShareItem( int row )
	{
		GwtShareItem shareItem;

		shareItem = null;
		
		if ( row >= 1 )
		{
			Widget widget;
			
			// Get the RemoveRecipientWidget from the last column.
			widget = m_shareTable.getWidget( row, m_numCols-1 );
			if ( widget != null && widget instanceof RemoveShareWidget )
				shareItem = ((RemoveShareWidget) widget).getShareItem();
		}
		
		return shareItem;
	}
	

	/**
	 * Return the ShareRightsWidget associated with the given share item.
	 */
	private ShareRightsWidget getShareRightsWidget( GwtShareItem shareItem )
	{
		int row;
		
		row = findShareItem( shareItem );
		if ( row >= 0 )
		{
			Widget widget;
			
			widget = m_shareTable.getWidget( row, m_rightsCol );
			if ( widget instanceof ShareRightsWidget )
				return (ShareRightsWidget)widget;
		}
		
		return null;
	}
	
	/**
	 * 
	 */
	private void handleClickOnAddExternalUser()
	{
		final String emailAddress;
		@SuppressWarnings("unused")
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
//			FindUserByEmailAddressCmd cmd = new FindUserByEmailAddressCmd( emailAddress, true );
//			GwtClientHelper.executeCommand( cmd, findUserCallback );
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
	private void highlightRecipient( int row )
	{
		if ( row < m_shareTable.getRowCount() )
			m_shareRowFormatter.addStyleName( row, "shareThisDlg_RecipientTable_highlightRow" );
	}
	
	/**
	 * 
	 */
	private void init()
	{
		GetSharingInfoCmd rpcCmd1 = null;
		GetMyTeamsCmd rpcCmd2 = null;
		
		updateHeader();

		// Set the column headers.  We do this now because the column headers vary
		// depending on how many entities we are sharing.
		setColumnHeaders();
		
		// Adjust the width of the table
		if ( m_numCols > 7 )
			m_shareTablePanel.getElement().getStyle().setWidth( 740, Unit.PX );
		else if ( m_numCols == 7 )
			m_shareTablePanel.getElement().getStyle().setWidth( 650, Unit.PX );
		else
			m_shareTablePanel.getElement().getStyle().setWidth( 610, Unit.PX );
		
		// Enable the Ok button.
		hideStatusMsg();
		setOkEnabled( true );

		if ( m_findCtrl != null )
		{
			m_findCtrl.setInitialSearchString( "" );
			
			// Set the filter of the Find Control to only search for users and groups.
			m_findCtrl.setSearchType( SearchType.PRINCIPAL );
		}
		
		// Remove all of the rows from the table.
		removeAllShares();
		
		// If we are in Administrative mode, we don't allow the user to add shares.
		if ( m_mode == ShareThisDlgMode.MANAGE_SELECTED || m_mode == ShareThisDlgMode.MANAGE_ALL )
		{
			m_mainTable.getRowFormatter().setVisible( 0, false );
		}
		else
		{
			m_mainTable.getRowFormatter().setVisible( 0, true );
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
	 * This method gets called when to invoke the "edit share rights" dialog.
	 */
	private void invokeEditShareRightsDlg( final ArrayList<GwtShareItem> listOfShareItems )
	{
		if ( listOfShareItems == null || listOfShareItems.size() == 0 )
			return;
		
		if ( m_editShareRightsDlg != null )
		{
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
							{
								Scheduler.ScheduledCommand cmd;
								
								cmd = new Scheduler.ScheduledCommand()
								{
									@Override
									public void execute() 
									{
										updateRightsLabel( m_editShareRightsDlg.getListOfShareItems() );
									}
								};
								Scheduler.get().scheduleDeferred( cmd );
							}
						}

						return true;
					}
				};
			}
			
			// Invoke the "edit share rights" dialog.
			{
				ShareRights highestRightsPossible;
				UIObject showRelativeTo = null;
				GwtShareItem shareItem;
				boolean recipientIsExternal = false;

				shareItem = listOfShareItems.get( 0 );
				
				// Find the ShareRightsWidget for the first share item.
				showRelativeTo = getShareRightsWidget( shareItem );

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
				}
				else
				{
					// Look at each item being shared and return the highest rights possible
					// that is available on all items being shared.
					highestRightsPossible = calculateHighestRightsPossible( listOfShareItems );
					
					// Go through the list of share items and see if a recipient is an external user.
					for ( GwtShareItem nextShareItem : listOfShareItems )
					{
						if ( nextShareItem.getRecipientType() == GwtRecipientType.EXTERNAL_USER )
						{
							recipientIsExternal = true;
							break;
						}
					}
				}

				// Is the recipient of the share an external user?
				if ( recipientIsExternal )
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
				
				m_editShareRightsDlg.init( listOfShareItems, highestRightsPossible, m_editShareRightsHandler );
				m_editShareRightsDlg.showRelativeToTarget( showRelativeTo );
			}
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
					invokeEditShareRightsDlg( listOfShareItems );
				}
			} );
			
		}
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
										shareItem.setRecipientPrincipalType( PrincipalType.UNKNOWN );
										shareItem.setShareRights( getDefaultShareRights() );
										shareItem.setShareExpirationValue( m_defaultShareExpirationValue );
										
										// Is this external user already in the list?
										if ( findShareItem( shareItem ) == -1 )
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
	 * Handles the InvokeEditShareRightsDlgEvent received by this class
	 */
	@Override
	public void onInvokeEditShareRightsDlg( InvokeEditShareRightsDlgEvent event )
	{
		final ArrayList<GwtShareItem> listOfShareItems;
		
		// Get the list of GwtShareItems we will be editing the share rights for.
		listOfShareItems = event.getListOfShareItems();
		
		// Is this event meant for this widget?
		if ( listOfShareItems != null && listOfShareItems.size() > 0 )
		{
			Scheduler.ScheduledCommand cmd;
			
			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute()
				{
					// Invoke the edit rights dialog.
					invokeEditShareRightsDlg( listOfShareItems );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
	}
	
	/**
	 * Remove all share items that may be in the table.
	 */
	private void removeAllShares()
	{
		// We start at row 1 so we don't delete the header.
		while ( m_shareTable.getRowCount() > 1 )
		{
			// Remove the 1st row that holds share information.
			m_shareTable.removeRow( 1 );
		}
		
		// Add a message to the table telling the user this item has not been shared.
		addNotSharedMessage();

		adjustShareTablePanelHeight();
	}
	
	/**
	 * Remove the given share from the table
	 */
	public void removeShare( GwtShareItem shareItem )
	{
		int row;
		
		// Mark this share as "to be deleted"
		m_sharingInfo.addToBeDeleted( shareItem );
		
		// Find the row this share lives in.
		row = findShareItem( shareItem );
		
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
	 * For the given list of share items, update the ShareRightsWidget for each
	 */
	private void updateRightsLabel( ArrayList<GwtShareItem> listOfShareItems )
	{
		if ( listOfShareItems != null )
		{
			for ( GwtShareItem nextShareItem : listOfShareItems )
			{
				ShareRightsWidget rightsWidget;
				
				// Find the ShareRightsWidget for the this share item.
				rightsWidget = getShareRightsWidget( nextShareItem );
				if ( rightsWidget != null )
					rightsWidget.updateRightsLabel();
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

			listOfShareItems = sharingInfo.getListOfShareItems();
			if ( listOfShareItems != null )
			{
				// Sort the list of share items.
				sortShareItems( listOfShareItems );

				for (GwtShareItem nextShareItem : listOfShareItems)
				{
					addShare( nextShareItem, false );
				}
			}
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
				Scheduler.ScheduledCommand cmd;
				
				m_findCtrl = findCtrl;

				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						loadPart2Now();
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		});
	}
	
	/**
	 * 
	 */
	private void loadPart2Now()
	{
		FindCtrl.createAsync( this, GwtSearchCriteria.SearchType.USER, new FindCtrlClient()
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
				m_manageSharesFindCtrl = findCtrl;
				populateDlgAsync();
			}
		});
	}
	
	/*
	 * Asynchronously runs the given instance of the move entries
	 * dialog.
	 */
	private static void runDlgAsync(
		final ShareThisDlg stDlg,
		final UIObject target,
		final String caption,
		final String title,
		final List<EntityId> entityIds,
		final ShareThisDlgMode mode )
	{
		ScheduledCommand doRun = new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				stDlg.runDlgNow( target, caption, title, entityIds, mode );
			}
		};
		Scheduler.get().scheduleDeferred( doRun );
	}
	
	/*
	 * Synchronously runs the given instance of the move entries
	 * dialog.
	 */
	private void runDlgNow(
		UIObject target,
		String caption,
		String title,
		List<EntityId> entityIds,
		ShareThisDlgMode mode )
	{
		// Set the caption...
		setCaption( caption );
		
		// ...and store the parameters.
		m_target    = target;
		m_entityIds = entityIds;
		m_mode = mode;
		
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
		FlexTable mainTable;
		FlowPanel tmpPanel;
		int row;
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
			
			headerPanel.add( namePanel );
			
			m_mainPanel.add( headerPanel );
		}
		
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
			
			m_mainPanel.add( m_manageShareItemsPanel );
		}
		
		// Add the controls needed for sharing.
		mainTable = createShareControls();
		m_mainPanel.add( mainTable );
		
		row = mainTable.getRowCount();
		
		// Create the "notify" controls
		{
			tmpPanel = new FlowPanel();
			m_notifyCheckbox = new CheckBox( messages.shareDlg_notifyLabel() );
			m_notifyCheckbox.setValue( true );
			tmpPanel.add( m_notifyCheckbox );
			
			m_sendToWidget = new ShareSendToWidget();
			m_sendToWidget.init( SendToValue.ONLY_MODIFIED_RECIPIENTS );
			tmpPanel.add( m_sendToWidget );
			
			mainTable.getFlexCellFormatter().setColSpan( row, 0, 2 );
			mainTable.setWidget( row, 0, tmpPanel );
			++row;
		}
		
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
	 * Set the text in each of the header of each column.
	 */
	private void setColumnHeaders()
	{
		int col;
		String text;
		GwtTeamingMessages messages;

		messages = GwtTeaming.getMessages();
		
		// On IE calling m_cellFormatter.setWidth( 0, 2, "*" ); throws an exception.
		// That is why we are calling DOM.setElementAttribute(...) instead.

		m_shareRowFormatter = m_shareTable.getRowFormatter();
		m_shareRowFormatter.addStyleName( 0, "oltHeader" );
		m_shareRowFormatter.addStyleName( 0, "whitespace-normal" );

		m_shareCellFormatter = m_shareTable.getFlexCellFormatter();

		// Remove all the columns from the table.
		if ( m_shareTable.getRowCount() > 0 )
		{
			while ( m_shareTable.getCellCount( 0 ) > 0 )
			{
				m_shareTable.removeCell( 0, 0 );
			}
		}
		
		col = 0;

		if ( m_mode == ShareThisDlgMode.MANAGE_SELECTED || m_mode == ShareThisDlgMode.MANAGE_ALL )
			text = messages.shareDlg_manageShares();
		else
			text = messages.shareName();
		m_shareTable.setText( 0, col, text );
		m_shareCellFormatter.getElement( 0, col ).setAttribute( "width", "90px" );
		++col;
		
		m_shareTable.setText( 0, col, messages.shareRecipientType() );
		m_shareCellFormatter.getElement( 0, col ).setAttribute( "width", "45px" );
		++col;
		
		// Are we in Administrative mode?
		if ( m_mode == ShareThisDlgMode.MANAGE_SELECTED || m_mode == ShareThisDlgMode.MANAGE_ALL )
		{
			// Yes, add a "Shared By" column.
			m_shareTable.setText( 0, col, messages.shareSharedBy() );
			m_shareCellFormatter.getElement( 0, col ).setAttribute( "width", "90px" );
			++col;
		}
		
		// Are we sharing more than 1 item?
		if ( (m_entityIds != null && m_entityIds.size() > 1) || m_mode == ShareThisDlgMode.MANAGE_ALL )
		{
			// Yes, add the "Item Name" column header
			m_shareTable.setText( 0, col, messages.shareEntityName() );
			m_shareCellFormatter.getElement( 0, col ).setAttribute( "width", "80px" );
			++col;
		}
		
		m_shareTable.setText( 0, col, messages.shareAccess() );
		m_shareCellFormatter.getElement( 0, col ).setAttribute( "width", "85px" );
		++col;
		
		m_shareTable.setText( 0, col, messages.shareExpires() );
		m_shareCellFormatter.getElement( 0, col ).setAttribute( "width", "90px" );
		++col;
		
		m_shareTable.setText( 0, col, messages.shareNote() );
		m_shareCellFormatter.getElement( 0, col ).setAttribute( "width", "100px" );
		++col;
		
		m_shareTable.setHTML( 0, col, "&nbsp;" );	// The delete image will go in this column.
		m_shareCellFormatter.getElement( 0, col ).setAttribute( "width", "14px" );
		++col;

		m_numCols = col;
		
		m_shareCellFormatter.addStyleName( 0, 0, "oltBorderLeft" );
		for (col=0; col < m_numCols; ++col)
		{
			m_shareCellFormatter.addStyleName( 0, col, "oltHeaderBorderTop" );
			m_shareCellFormatter.addStyleName( 0, col, "oltHeaderBorderBottom" );
			m_shareCellFormatter.addStyleName( 0, col, "oltHeaderPadding" );
		}
		m_shareCellFormatter.addStyleName( 0, m_numCols-1, "oltBorderRight" );
	}
	
	/**
	 * Unlighlight the given row in the table that holds the list of recipients
	 */
	private void unhighlightRecipient( int row )
	{
		if ( row < m_shareTable.getRowCount() )
			m_shareRowFormatter.removeStyleName( row, "shareThisDlg_RecipientTable_highlightRow" );
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
									case failsBlacklistDomain:  messages.shareDlg_emailAddressInvalid_blDomain_Param(""); break;
									case failsBlacklistEMA:     messages.shareDlg_emailAddressInvalid_blEMA_Param("");    break;
									case failsWhitelist:        messages.shareDlg_emailAddressInvalid_wl_Param("");       break;
									}
									// Tell the user about the problem...
									GwtClientHelper.deferredAlert( msg );
									
									// ...and put the email address back in the find control
									m_findCtrl.setInitialSearchString( emailAddress );
									break;
									
								default:
								case failsFormat:
									// No, ask the user if they still want to share with this email address.
									if ( Window.confirm( messages.shareDlg_emailAddressInvalidPrompt_Param("") ) == true )
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
							gwtUser.setPrincipalType( PrincipalType.EXTERNAL_OTHERS );
							gwtUser.setName( emailAddress );
							gwtUser.setUserId( userId );
							
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
			final Boolean autoHide,
			final Boolean modal,
			
			// initAndShow parameters,
			final ShareThisDlg		stDlg,
			final UIObject			target,
			final String			caption,
			final String			title,
			final List<EntityId>	entityIds,
			final ShareThisDlgMode	mode )
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
					ShareThisDlg stDlg = new ShareThisDlg( autoHide, modal );
					stDlgClient.onSuccess( stDlg );
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync( stDlg, target, caption, title, entityIds, mode );
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
		doAsyncOperation( stDlgClient, false, true, null, null, null, null, null, ShareThisDlgMode.NORMAL );
	}
	
	/**
	 * Loads the ShareThisDlg split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param stDlgClient
	 */
	public static void createAsync(
		ShareThisDlgClient stDlgClient,
		Boolean autoHide,
		Boolean modal )
	{
		doAsyncOperation( stDlgClient, autoHide, modal, null, null, null, null, null, ShareThisDlgMode.NORMAL );
	}
	
	/**
	 * Initializes and shows the share this dialog.
	 * 
	 * @param stDlg
	 * @param caption
	 * @param title
	 * @param entityIds
	 */
	public static void initAndShow(
		ShareThisDlg stDlg,
		UIObject target,
		String caption,
		String title,
		List<EntityId> entityIds )
	{
		doAsyncOperation( null, null, null, stDlg, target, caption, title, entityIds, ShareThisDlgMode.NORMAL );
	}

	/**
	 * Initializes and shows the share this dialog.
	 * 
	 * @param stDlg
	 * @param caption
	 * @param title
	 * @param entityIds
	 */
	public static void initAndShow(
		ShareThisDlg stDlg,
		UIObject target,
		String caption,
		String title,
		List<EntityId> entityIds,
		ShareThisDlgMode mode )
	{
		doAsyncOperation( null, null, null, stDlg, target, caption, title, entityIds, mode );
	}
}

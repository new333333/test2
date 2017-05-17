/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.datatable;

import java.text.DateFormat;
import java.util.ArrayList;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.GetDateStrCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetUserAvatarCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.GwtRecipientType;
import org.kablink.teaming.gwt.client.util.GwtShareItem;
import org.kablink.teaming.gwt.client.util.GwtSharingInfo;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue.ShareExpirationType;
import org.kablink.teaming.gwt.client.util.ShareRights;
import org.kablink.teaming.gwt.client.widgets.CopyPublicLinkDlg;
import org.kablink.teaming.gwt.client.widgets.GroupMembershipPopup;
import org.kablink.teaming.gwt.client.widgets.CopyPublicLinkDlg.CopyPublicLinkDlgClient;
import org.kablink.teaming.gwt.client.widgets.ShareWithPublicInfoDlg;
import org.kablink.teaming.gwt.client.widgets.ShareThisDlg2.ShareThisDlgMode;
import org.kablink.teaming.gwt.client.widgets.ShareWithPublicInfoDlg.ShareWithPublicInfoDlgClient;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.UIObject;

/**
 * A cell used to render a share item.
 * 
 * @author jwootton
 */
public class ShareItemCell extends AbstractCell<GwtShareItem>
{
	private ShareWithPublicInfoDlg m_shareWithPublicInfoDlg=null;
	private CopyPublicLinkDlg m_copyPublicLinkDlg=null;

	/**
	 * 
	 */
	class ElementWrapper extends UIObject
	{
		public ElementWrapper( Element e )
		{
			setElement( e ); // setElement() is protected, so we have to subclass and call here
		}
	}
	
	/**
	 * 
	 */
	public ShareItemCell()
	{
		// We care about click and keydown action
		super( "click", "keydown" );
	}

	/**
	 * Get the text that shows the access rights
	 */
	private String getAccessRightsText( GwtShareItem shareItem )
	{
		if ( shareItem == null )
			return "???";
		
		return shareItem.getShareRightsAsString();
	}
	
	/**
	 * Get the text that shows the value of the share expiration
	 */
	private String getExpirationText( GwtShareItem shareItem )
	{
		String expirationText = "";
		ShareExpirationValue expirationValue;
		
		if ( shareItem == null )
			return "???";
		
		expirationValue = shareItem.getShareExpirationValue();
		if ( expirationValue != null )
		{
			String after;
			Long value;
			
			value = expirationValue.getValue();
			
			switch ( expirationValue.getExpirationType() )
			{
			case NEVER:
				expirationText = GwtTeaming.getMessages().shareDlg_expiresNever();
				break;
			
			case AFTER_DAYS:
				after = "";
				if ( value != null )
				{
					if ( value < 0 )
						value = Long.valueOf( 0 );
					
					after = value.toString();
				}
				
				expirationText = GwtTeaming.getMessages().shareDlg_expiresAfter( after );
				break;
			
			case ON_DATE:
				// At a later point we will issue an rpc request to get the date.
				expirationText = "";
				break;
				
			case UNKNOWN:
			default:
				expirationText = "Unknown expiration type";
				break;
			}
		}
		else
			expirationText = "";
		
		return expirationText;
	}
	
	/**
	 * 
	 */
	private ShareExpirationType getExpirationType( GwtShareItem shareItem )
	{
		if ( shareItem != null )
		{
			ShareExpirationValue expirationValue;

			expirationValue = shareItem.getShareExpirationValue();
			if ( expirationValue != null )
				return expirationValue.getExpirationType();
		}
		
		return ShareExpirationType.UNKNOWN;
	}
	
	/**
	 * Get the text that shows the reshare rights
	 */
	private String getReshareRightsText( GwtShareItem shareItem )
	{
		if ( shareItem == null )
			return "???";
		
		return shareItem.getShareRights().getReshareRightsAsString();
	}

	/**
	 * 
	 */
	private void invokeCopyFilrLinkDlg(
		final GwtShareItem shareItem,
		final UIObject target )
	{
		if ( shareItem == null )
			return;
		
		if ( m_copyPublicLinkDlg == null )
		{
			// No!  Create one now...
			CopyPublicLinkDlg.createAsync( new CopyPublicLinkDlgClient()
			{
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess( CopyPublicLinkDlg cplDlg )
				{
					Scheduler.ScheduledCommand cmd;
					
					m_copyPublicLinkDlg = cplDlg;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							invokeCopyFilrLinkDlg( shareItem, target );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			});
		}
		else
		{
			String caption;
			ArrayList<EntityId> entityIds;

			caption = GwtClientHelper.patchMessage(
											GwtTeaming.getMessages().copyPublicLinkTheseItems(),
											String.valueOf( 1 ) );
			entityIds = new ArrayList<EntityId>();
			entityIds.add( shareItem.getEntityId() );
			CopyPublicLinkDlg.initAndShow( m_copyPublicLinkDlg, caption, entityIds, true );	// true -> Immediately query for the public links.  false -> The user needs to press a button to do so.
		}
	}

	
	/**
	 * 
	 */
	private void invokeGroupMembershipDlg(
		final GwtShareItem shareItem,
		final UIObject target )
	{
		GroupMembershipPopup popup;
		
		// Create a popup that will display the membership of this group.
		popup = new GroupMembershipPopup(
										true,
										false,
										shareItem.getRecipientName(),
										shareItem.getRecipientId().toString() );
		
		popup.setPopupPosition( target.getAbsoluteLeft(), target.getAbsoluteTop() );
		popup.show();
	}
	
	/**
	 * 
	 */
	private void invokeShareWithPublicInfoDlg(
		final GwtShareItem shareItem,
		final UIObject target )
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
					Scheduler.ScheduledCommand cmd;
					
					m_shareWithPublicInfoDlg = swpiDlg;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							invokeShareWithPublicInfoDlg( shareItem, target );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
		}
		else
		{
			m_shareWithPublicInfoDlg.init( shareItem.getEntityId() );
			m_shareWithPublicInfoDlg.showRelativeToTarget( target );
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void onBrowserEvent(
		Context context,
		Element parent,
		final GwtShareItem value,
		NativeEvent event,
		ValueUpdater<GwtShareItem> valueUpdater )
	{
		// Let AbstractCell handle the keydown event.
		super.onBrowserEvent( context, parent, value, event, valueUpdater );
		
		// Handle the click event.
		if ( "click".equals( event.getType() ) )
		{
			EventTarget eventTarget;
			final Element element;
			String publicUrlDiv;
			String filrLinkImg;
			String groupLinkDiv;
			
			eventTarget = event.getEventTarget();
			element = Element.as( eventTarget );
			groupLinkDiv = element.getAttribute( "group-link-div" );
			filrLinkImg = element.getAttribute( "filr-link-img" );
			publicUrlDiv = element.getAttribute( "public-url-div" );
			
			// Did the user click on the "public url" link?
			if ( publicUrlDiv != null && publicUrlDiv.equalsIgnoreCase( "true" ) )
			{
				Scheduler.ScheduledCommand cmd;
				
				// Yes
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						ElementWrapper wrapper;
						
						wrapper = new ElementWrapper( element );
						invokeShareWithPublicInfoDlg( value, wrapper );
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
			// Did the user click on the "Filr link" icon?
			else if ( filrLinkImg != null && filrLinkImg.equalsIgnoreCase( "true" ) )
			{
				Scheduler.ScheduledCommand cmd;
				
				// Yes
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						ElementWrapper wrapper;
						
						// Yes
						wrapper = new ElementWrapper( element );
						invokeCopyFilrLinkDlg( value, wrapper );
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
			// Did the user click on the "Group link" icon?
			else if ( groupLinkDiv != null && groupLinkDiv.equalsIgnoreCase( "true" ) )
			{
				Scheduler.ScheduledCommand cmd;
				
				// Yes
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						ElementWrapper wrapper;
						
						// Yes
						wrapper = new ElementWrapper( element );
						invokeGroupMembershipDlg( value, wrapper );
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
			else
			{
				// Yes
				valueUpdater.update( value );
			}
		}
	}

	/**
	 * 
	 */
	@Override
	protected void onEnterKeyDown(
		Context context,
		Element parent,
		GwtShareItem value,
		NativeEvent event,
		ValueUpdater<GwtShareItem> valueUpdater )
	{
		if ( valueUpdater != null )
		{
			valueUpdater.update( value );
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void render( Context context, GwtShareItem value, SafeHtmlBuilder sb )
	{
		// This method is not used
	}
	
	/**
	 * 
	 */
	public void render(
		Context context,
		final GwtShareItem shareItem,
		SafeHtmlBuilder sb,
		GwtSharingInfo sharingInfo,
		ShareThisDlgMode mode )
	{
		Scheduler.ScheduledCommand cmd;
		GwtTeamingMessages messages;
		FlowPanel topPanel;
		HorizontalPanel hPanel;
		FlowPanel mainPanel;
		Label label;
		String name;
		final String expirationDateId;
		final String avatarId;
		
		if ( shareItem == null )
		{
			GwtClientHelper.renderEmptyHtml( sb );
			return;
		}
		
		messages = GwtTeaming.getMessages();
		
		topPanel = new FlowPanel();
		
		hPanel = new HorizontalPanel();
		hPanel.addStyleName( "shareItem_InfoPanel" );
		
		topPanel.add( hPanel );
		
		// Add the recipient's avatar
		{
			Image img;
			StringBuffer strBuff;
			
			img = new Image();
			img.addStyleName( "shareItem_RecipientAvatar" );
			strBuff = new StringBuffer();
			strBuff.append( "recipientAvatar-" );
			switch( shareItem.getRecipientType() )
			{
			case PUBLIC_TYPE:
				strBuff.append( "public" );
				break;
				
			case PUBLIC_LINK:
				strBuff.append( "PublicLink-ShareItemId:" );
				strBuff.append( shareItem.getId() );
				break;
				
			case EXTERNAL_USER:
				Long recipientId;
				
				recipientId = shareItem.getRecipientId();
				if ( recipientId == null )
					strBuff.append( shareItem.getRecipientName() );
				else
					strBuff.append( String.valueOf( recipientId ) );
				break;
				
			default:
				strBuff.append( String.valueOf( shareItem.getRecipientId() ) );
				break;
			}
			strBuff.append( "-" + String.valueOf( shareItem.getEntityId().getEntityIdString() ) );
			avatarId = strBuff.toString();
			img.getElement().setId( avatarId );
			hPanel.add( img );
		}
		
		mainPanel = new FlowPanel();
		mainPanel.addStyleName( "shareItem_mainPanel" );
		hPanel.add( mainPanel );
		
		// Add the recipients name
		name = shareItem.getRecipientName();
		label = new Label( name );
		label.addStyleName( "shareItem_RecipientName" );
		mainPanel.add( label );
		
		// Are we in administrative mode?
		if ( mode == ShareThisDlgMode.MANAGE_SELECTED || mode == ShareThisDlgMode.MANAGE_ALL )
		{
			// Yes, add a line for "shared by"
			name = shareItem.getSharedByName();
			label = new Label( messages.shareDlg_sharedByLabel() + " " + name );
			label.addStyleName( "shareItem_SharedByName" );
			mainPanel.add( label );
		}
		
		// Add the name of the file being shared if we are sharing more than 1 file
		{
			ArrayList<EntityId> listOfEntities;
			
			// Are we sharing more that 1 file?
			listOfEntities = sharingInfo.getListOfEntities();
			if ( (listOfEntities != null && listOfEntities.size() > 1) || mode == ShareThisDlgMode.MANAGE_ALL )
			{
				// Yes
				if ( shareItem.getEntityId().isEntry() )
					label = new Label( messages.shareDlg_fileLabel() + " " + shareItem.getEntityName() );
				else
					label = new Label( messages.shareDlg_folderLabel() + " " + shareItem.getEntityName() );
				label.addStyleName( "shareItem_FileAccessRights" );
				mainPanel.add( label );
			}
		}
		
		// Add the share expiration
		{
			InlineLabel expireLabel1;
			InlineLabel expireLabel2;
			StringBuffer strBuff;

			expireLabel1 = new InlineLabel( messages.shareDlg_expiresLabel() + " " );
			expireLabel1.addStyleName( "shareItem_Expiration" );

			expireLabel2 = new InlineLabel( getExpirationText( shareItem ) );
			expireLabel2.addStyleName( "shareItem_Expiration" );
			strBuff = new StringBuffer();
			strBuff.append( "expirationDate-" );
			if ( shareItem.getRecipientType() != GwtRecipientType.PUBLIC_LINK )
				strBuff.append( String.valueOf( shareItem.getRecipientId() ) );
			else
			{
				strBuff.append( "PublicLink-ShareItemId:" );
				strBuff.append( shareItem.getId() );
			}
			strBuff.append( "-" + String.valueOf( shareItem.getEntityId().getEntityIdString() ) );
			expirationDateId = strBuff.toString();
			expireLabel2.getElement().setId( expirationDateId );
			if ( shareItem.isExpired() )
			{
				expireLabel1.addStyleName( "shareThisDlg_ShareExpired" );
				expireLabel2.addStyleName( "shareThisDlg_ShareExpired" );
			}

			mainPanel.add( expireLabel1 );
			mainPanel.add( expireLabel2 );
		}
		
		// Add the access rights
		label = new Label( messages.shareDlg_accessLabel() + " " + getAccessRightsText( shareItem ) );
		label.addStyleName( "shareItem_AccessRights" );
		mainPanel.add( label );
		
		// Add reshare rights info.
		{
			ShareRights shareRights;
			
			// Does the recipient have any reshare rights?
			shareRights = shareItem.getShareRights(); 
			if ( shareRights != null && shareRights.getCanShareForward() )
			{
				// Yes
				// Add the reshare rights
				label = new Label( messages.shareDlg_reshareLabel() + " " + getReshareRightsText( shareItem ) );
				label.addStyleName( "shareItem_ReshareRights" );
				mainPanel.add( label );
			}
		}
		
		// Add the note
		{
			String note;

			note = shareItem.getComments();

			if ( note == null || note.length() == 0 )
				note = "";

			if ( note.length() > 0 )
			{
				label = new Label( note );
				label.addStyleName( "shareItem_Note" );
				mainPanel.add( label );
			}
		}
		
		// Are we dealing with a public share?
		if ( shareItem.getRecipientType() == GwtRecipientType.PUBLIC_TYPE )
		{
		   // Yes, add a link that the user can click to get the public urls
			Image img;
            img = new Image( GwtTeaming.getImageBundle().publicLink16());
            img.addStyleName( "shareItem_PublicUrl" );
			img.setTitle( messages.shareDlg_publicUrlLabel() );
         	img.getElement().setAttribute( "public-url-div", "true" );
            mainPanel.add( img );
		}
		
		// Are we dealing with a group?
		if ( shareItem.getRecipientType() == GwtRecipientType.GROUP )
		{
		   // Yes, add a link that the user can click to see the group membership
			Image img;

			img = new Image( GwtTeaming.getFilrImageBundle().filrGroup16() );
            img.addStyleName( "shareItem_GroupLink" );
			img.setTitle( messages.shareDlg_groupMembershipLabel() );
         	img.getElement().setAttribute( "group-link-div", "true" );
            mainPanel.add( img );
		}
		
		// Are we dealing with a "Filr link"?
		if ( shareItem.getRecipientType() == GwtRecipientType.PUBLIC_LINK )
		{
			Image img;
			
			// Yes, add a link that the user can click on to invoke the "Copy Filr Link" dialog.
			img = new Image( GwtTeaming.getImageBundle().publicLink16() );
			img.getElement().setAttribute( "filr-link-img", "true" );
			img.getElement().setTitle( messages.shareDlg_publicLinkTitle() );
			mainPanel.add( img );
		}
		
		sb.append( SafeHtmlUtils.fromSafeConstant( topPanel.getElement().getInnerHTML() ) );
		
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				// Is the expiration type a date?
				if ( getExpirationType( shareItem ) == ShareExpirationType.ON_DATE )
					updateExpirationText( shareItem, expirationDateId );

				// Issue an rpc request to get the user's avatar
				updateAvatar( shareItem, avatarId );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}

	/**
	 * Issue an rpc request to get the recipient's avatar.
	 */
	private void updateAvatar( final GwtShareItem shareItem, final String elementId )
	{
		GetUserAvatarCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback = null;
		GwtTeamingImageBundle imgBundle;
		
		imgBundle = GwtTeaming.getImageBundle();
		
		// Is the recipient "public"?
		if ( shareItem.getRecipientType() == GwtRecipientType.PUBLIC_TYPE )
		{
			// Yes
			updateAvatarUrl( elementId, imgBundle.public48().getSafeUri().asString() );
			return;
		}
		
		// Is the recipient type a "public link"?
		if ( shareItem.getRecipientType() == GwtRecipientType.PUBLIC_LINK )
		{
			// Yes
			updateAvatarUrl( elementId, imgBundle.publicLink48().getSafeUri().asString() );
			return;
		}
		
		// Is the recipient type a "group"?
		if ( shareItem.getRecipientType() == GwtRecipientType.GROUP )
		{
			// Yes
			updateAvatarUrl( elementId, GwtTeaming.getFilrImageBundle().filrGroup48().getSafeUri().asString() );
			return;
		}
		
		rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				// display error
				GwtClientHelper.handleGwtRPCFailure(
												t,
												GwtTeaming.getMessages().rpcFailure_GetUserAvatar(),
												shareItem.getRecipientId() );
			}

			@Override
			public void onSuccess( final VibeRpcResponse response )
			{
				Scheduler.ScheduledCommand cmd;
				
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						
						if ( response.getResponseData() != null )
						{
							StringRpcResponseData responseData;
							String url = null;

							responseData = (StringRpcResponseData) response.getResponseData();
							url = responseData.getStringValue();

							if ( url == null || url.length() == 0 )
							{
								// Are we dealing with an external user?
								if ( shareItem.getRecipientType() == GwtRecipientType.EXTERNAL_USER )
								{
									// Yes, use the external user image.
									url = GwtTeaming.getFilrImageBundle().filrExternalUser48().getSafeUri().asString();
								}
								else
								{
									// No, use the generic user image.
									url = GwtTeaming.getImageBundle().userAvatar().getSafeUri().asString();
								}
							}

							updateAvatarUrl( elementId, url );
						}
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		};

		cmd = new GetUserAvatarCmd();
		cmd.setUserId( shareItem.getRecipientId() );
		GwtClientHelper.executeCommand( cmd, rpcCallback );
	}
	
	/**
	 * Update the url used for the recipient's avatar 
	 */
	private void updateAvatarUrl( String elementId, String url )
	{
		Element element;
		
		// Find the <img> element.
		element = DOM.getElementById( elementId );
		if ( element != null )
		{
			element.setAttribute( "src", url );
		}
	}
	

	/**
	 * Issue an rpc request to get the expiration date.
	 */
	@SuppressWarnings("incomplete-switch")
	private void updateExpirationText( GwtShareItem shareItem, String elementId )
	{
		ShareExpirationValue expirationValue;
		
		if ( shareItem == null )
			return;
		
		expirationValue = shareItem.getShareExpirationValue();
		if ( expirationValue != null )
		{
			Long value;
			
			value = expirationValue.getValue();
			
			switch ( expirationValue.getExpirationType() )
			{
			case ON_DATE:
				if ( value != null )
					updateExpirationText( value, elementId );
				
				break;
			}
		}
	}
	
	/**
	 * Issue an rpc request to get the expiration date.
	 */
	private void updateExpirationText( Long value, final String elementId )
	{
		GetDateStrCmd cmd;
		AsyncCallback<VibeRpcResponse> getDateStrCallback = null;
		
		getDateStrCallback = new AsyncCallback<VibeRpcResponse>()
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
					{
						Element element;
						
						element = DOM.getElementById( elementId );
						if ( element != null )
							element.setInnerText( dateTimeStr );
					}
				}
			}
		};
		
		// Issue an rpc request to get the date/time string.
		cmd = new GetDateStrCmd( value, DateFormat.SHORT );
		GwtClientHelper.executeCommand( cmd, getDateStrCallback );
	}
}

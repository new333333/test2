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
package org.kablink.teaming.gwt.client.datatable;

import java.text.DateFormat;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.profile.ProfileAttribute;
import org.kablink.teaming.gwt.client.profile.ProfileAttributeListElement;
import org.kablink.teaming.gwt.client.rpc.shared.GetDateStrCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetProfileAvatarsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.GwtRecipientType;
import org.kablink.teaming.gwt.client.util.GwtShareItem;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue.ShareExpirationType;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;


/**
 * A cell used to render a share item
 */
public class ShareItemCell extends AbstractCell<GwtShareItem>
{
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
	@Override
	public void onBrowserEvent(
		Context context,
		Element parent,
		GwtShareItem value,
		NativeEvent event,
		ValueUpdater<GwtShareItem> valueUpdater )
	{
		// Let AbstractCell handle the keydown event.
		super.onBrowserEvent( context, parent, value, event, valueUpdater );
		
		// Handle the click event.
		if ( "click".equals( event.getType() ) )
		{
			valueUpdater.update( value );
		}
	}

	/**
	 * 
	 */
	@Override
	public void render( Context context, final GwtShareItem shareItem, SafeHtmlBuilder sb )
	{
		Scheduler.ScheduledCommand cmd;
		GwtTeamingMessages messages;
		FlowPanel topPanel;
		HorizontalPanel hPanel;
		FlowPanel mainPanel;;
		Label label;
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
			if ( shareItem.getRecipientType() == GwtRecipientType.PUBLIC_TYPE )
				strBuff.append( "public" );
			else
				strBuff.append( String.valueOf( shareItem.getRecipientId() ) );
			strBuff.append( "-" + String.valueOf( shareItem.getEntityId().getEntityIdString() ) );
			avatarId = strBuff.toString();
			img.getElement().setId( avatarId );
			hPanel.add( img );
		}
		
		mainPanel = new FlowPanel();
		mainPanel.addStyleName( "shareItem_mainPanel" );
		hPanel.add( mainPanel );
		
		// Add the recipients name
		label = new Label( shareItem.getRecipientName() );
		label.addStyleName( "shareItem_RecipientName" );
		mainPanel.add( label );
		
		// Add the share expiration
		{
			InlineLabel expireLabel1;
			InlineLabel expireLabel2;
			StringBuffer strBuff;

			expireLabel1 = new InlineLabel( messages.shareDlg_expiresLabel() + " " );
			expireLabel1.addStyleName( "shareItem_Expiration" );
			if ( shareItem.isExpired() )
				expireLabel1.addStyleName( "shareThisDlg_ShareExpired" );

			expireLabel2 = new InlineLabel( getExpirationText( shareItem ) );
			expireLabel2.addStyleName( "shareItem_Expiration" );
			strBuff = new StringBuffer();
			strBuff.append( "expirationDate-" );
			strBuff.append( String.valueOf( shareItem.getRecipientId() ) );
			strBuff.append( "-" + String.valueOf( shareItem.getEntityId().getEntityIdString() ) );
			expirationDateId = strBuff.toString();
			expireLabel2.getElement().setId( expirationDateId );
			if ( shareItem.isExpired() )
				expireLabel2.addStyleName( "shareThisDlg_ShareExpired" );

			mainPanel.add( expireLabel1 );
			mainPanel.add( expireLabel2 );
		}
		
		// Add the access rights
		label = new Label( messages.shareDlg_accessLabel() + " " + getAccessRightsText( shareItem ) );
		label.addStyleName( "shareItem_AccessRights" );
		mainPanel.add( label );
		
		// Add the reshare rights
		label = new Label( messages.shareDlg_reshareLabel() + " " + getReshareRightsText( shareItem ) );
		label.addStyleName( "shareItem_ReshareRights" );
		mainPanel.add( label );
		
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
	 * Issue an rpc request to get the recipient's avatar.
	 */
	private void updateAvatar( final GwtShareItem shareItem, final String elementId )
	{
		GetProfileAvatarsCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback = null;
		
		// Is the recipient "public"?
		if ( shareItem.getRecipientType() == GwtRecipientType.PUBLIC_TYPE )
		{
			// Yes
			updateAvatarUrl( elementId, GwtMainPage.m_requestInfo.getImagesPath() + "pics/public16.png" );
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
												GwtTeaming.getMessages().rpcFailure_GetProfileAvatars(),
												shareItem.getRecipientId() );
			}

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess( final VibeRpcResponse response )
			{
				Scheduler.ScheduledCommand cmd;
				
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						ProfileAttribute attr;
						List<ProfileAttributeListElement> value;
						String url = null;
						
						attr = (ProfileAttribute) response.getResponseData();
						
						value = (List<ProfileAttributeListElement>)attr.getValue();
						if ( value != null && value.size() > 0 )
						{
							ProfileAttributeListElement valItem;

							valItem = value.get( 0 );
							url = valItem.getValue().toString();
						}
						
						if ( url == null || url.length() == 0 )
							url = GwtMainPage.m_requestInfo.getImagesPath() + "pics/UserPhoto.png";

						updateAvatarUrl( elementId, url );
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		};

		cmd = new GetProfileAvatarsCmd();
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

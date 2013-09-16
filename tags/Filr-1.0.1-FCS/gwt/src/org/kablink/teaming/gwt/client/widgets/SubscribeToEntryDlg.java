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

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.util.SubscriptionData;
import org.kablink.teaming.gwt.client.rpc.shared.GetSubscriptionDataCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveSubscriptionDataCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SubscriptionDataRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;


/**
 * 
 * @author jwootton
 *
 */
public class SubscribeToEntryDlg extends DlgBox
	implements EditSuccessfulHandler
{
	String m_entryId;
	String m_entryTitle;
	FlowPanel m_mainPanel;
	FlowPanel m_sendEmailToPanel1;
	FlowPanel m_sendEmailToPanel2;
	FlowPanel m_sendTextToPanel;
	FlowPanel m_titlePanel;
	InlineLabel m_entryTitleLabel;
	CheckBox m_primaryAddress1;
	CheckBox m_mobileAddress1;
	CheckBox m_textAddress1;
	CheckBox m_primaryAddress2;
	CheckBox m_mobileAddress2;
	CheckBox m_textAddress2;
	CheckBox m_primaryAddress3;
	CheckBox m_mobileAddress3;
	CheckBox m_textAddress3;
	AsyncCallback<VibeRpcResponse> m_readSubscriptionDataCallback = null;
	AsyncCallback<VibeRpcResponse> m_saveSubscriptionDataCallback = null;
	
	/**
	 * 
	 */
	public SubscribeToEntryDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos )
	{
		super( autoHide, modal, xPos, yPos );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().subscribeToEntryDlgHeader(), this, null, null ); 
	}
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		Label label;
		FlowPanel flowPanel;

		messages = GwtTeaming.getMessages();
		
		m_mainPanel = new FlowPanel();
		m_mainPanel.setStyleName( "teamingDlgBoxContent" );
		m_mainPanel.addStyleName( "dlgContent" );
		
		// Create a panel for the entry title	
		{
			InlineLabel titleLabel;
			
			m_titlePanel = new FlowPanel();
			m_titlePanel.addStyleName( "marginbottom2" );
			
			titleLabel = new InlineLabel( messages.subscribeToEntryHeader() );
			titleLabel.addStyleName( "mediumtext" );
			m_titlePanel.add( titleLabel );

			m_entryTitleLabel = new InlineLabel();
			m_entryTitleLabel.addStyleName( "mediumtext" );
			m_entryTitleLabel.addStyleName( "bold" );
			m_titlePanel.add( m_entryTitleLabel );
			
			m_mainPanel.add( m_titlePanel );
		}

		// Create a panel where the "send email to" ui will go.
		{
			m_sendEmailToPanel1 = new FlowPanel();
			m_sendEmailToPanel1.addStyleName( "marginTop10px" );
			
			label = new Label( messages.sendEmailTo() );
			label.addStyleName( "bold" );
			label.addStyleName( "gray3" );
			label.addStyleName( "smalltext" );
			m_sendEmailToPanel1.add( label );
			
			flowPanel = new FlowPanel();
			flowPanel.addStyleName( "paddingLeft1em" );
			flowPanel.addStyleName( "mediumtext" );
			m_primaryAddress1 = new CheckBox();
			flowPanel.add( m_primaryAddress1 );
			m_sendEmailToPanel1.add( flowPanel );

			flowPanel = new FlowPanel();
			flowPanel.addStyleName( "paddingLeft1em" );
			flowPanel.addStyleName( "mediumtext" );
			m_mobileAddress1 = new CheckBox();
			flowPanel.add( m_mobileAddress1 );
			m_sendEmailToPanel1.add( flowPanel );

			flowPanel = new FlowPanel();
			flowPanel.addStyleName( "paddingLeft1em" );
			flowPanel.addStyleName( "mediumtext" );
			m_textAddress1 = new CheckBox();
			flowPanel.add( m_textAddress1 );
			m_sendEmailToPanel1.add( flowPanel );
			
			m_mainPanel.add( m_sendEmailToPanel1 );
		}
		
		// Create a panel where the "send email without attachment to" ui will go.
		{
			m_sendEmailToPanel2 = new FlowPanel();
			m_sendEmailToPanel2.addStyleName( "marginTop10px" );
		
			label = new Label( messages.sendEmailWithoutAttachmentsTo() );
			label.addStyleName( "bold" );
			label.addStyleName( "gray3" );
			label.addStyleName( "smalltext" );
			m_sendEmailToPanel2.add( label );
			
			flowPanel = new FlowPanel();
			flowPanel.addStyleName( "paddingLeft1em" );
			flowPanel.addStyleName( "mediumtext" );
			m_primaryAddress2 = new CheckBox();
			flowPanel.add( m_primaryAddress2 );
			m_sendEmailToPanel2.add( flowPanel );
			
			flowPanel = new FlowPanel();
			flowPanel.addStyleName( "paddingLeft1em" );
			flowPanel.addStyleName( "mediumtext" );
			m_mobileAddress2 = new CheckBox();
			flowPanel.add( m_mobileAddress2 );
			m_sendEmailToPanel2.add( flowPanel );
			
			flowPanel = new FlowPanel();
			flowPanel.addStyleName( "paddingLeft1em" );
			flowPanel.addStyleName( "mediumtext" );
			m_textAddress2 = new CheckBox();
			flowPanel.add( m_textAddress2 );
			m_sendEmailToPanel2.add( flowPanel );
			
			m_mainPanel.add( m_sendEmailToPanel2 );
		}
		
		// Create a panel where the "send text to" ui will go.
		{
			m_sendTextToPanel = new FlowPanel();
			m_sendTextToPanel.addStyleName( "marginTop10px" );
		
			label = new Label( messages.sendTextTo() );
			label.addStyleName( "bold" );
			label.addStyleName( "gray3" );
			label.addStyleName( "smalltext" );
			m_sendTextToPanel.add( label );
			
			flowPanel = new FlowPanel();
			flowPanel.addStyleName( "paddingLeft1em" );
			flowPanel.addStyleName( "mediumtext" );
			m_primaryAddress3 = new CheckBox();
			flowPanel.add( m_primaryAddress3 );
			m_sendTextToPanel.add( flowPanel );
			
			flowPanel = new FlowPanel();
			flowPanel.addStyleName( "paddingLeft1em" );
			flowPanel.addStyleName( "mediumtext" );
			m_mobileAddress3 = new CheckBox();
			flowPanel.add( m_mobileAddress3 );
			m_sendTextToPanel.add( flowPanel );

			flowPanel = new FlowPanel();
			flowPanel.addStyleName( "paddingLeft1em" );
			flowPanel.addStyleName( "mediumtext" );
			m_textAddress3 = new CheckBox();
			flowPanel.add( m_textAddress3 );
			m_sendTextToPanel.add( flowPanel );
			
			m_mainPanel.add( m_sendTextToPanel );
		}
		
		return m_mainPanel;
	}
	
	
	/**
	 * This method gets called when the user clicks on the Ok button.  We will issue an ajax
	 * request to save the new subscription settings.
	 */
	public boolean editSuccessful( Object data )
	{
		if ( data instanceof SubscriptionData )
		{
			SubscriptionData subscriptionData;
			
			subscriptionData = (SubscriptionData) data;
			
			// Issue an ajax request to save the subscription data.
			if ( m_saveSubscriptionDataCallback == null )
			{
				m_saveSubscriptionDataCallback = new AsyncCallback<VibeRpcResponse>()
				{
					@Override
					public void onFailure( Throwable caught )
					{
						GwtClientHelper.handleGwtRPCFailure(
							caught,
							GwtTeaming.getMessages().rpcFailure_SaveSubscriptionData() );
					}// end onFailure()

					@Override
					public void onSuccess( VibeRpcResponse result )
					{
						// Nothing to do.
					}// end onSuccess()
				};
			}
			
			// Issue an ajax request to save the subscription data for the entry we are working with.
			SaveSubscriptionDataCmd cmd = new SaveSubscriptionDataCmd( m_entryId, subscriptionData );
			GwtClientHelper.executeCommand( cmd, m_saveSubscriptionDataCallback );
		}
		
		return true;
	}
	
	
	/**
	 * Get the data from the controls in the dialog box and store the data in a GwtPersonalPreferences obj.
	 */
	public Object getDataFromDlg()
	{
		SubscriptionData subscriptionData;
		int sendTo;
		
		subscriptionData = new SubscriptionData();
		
		// See which addresses the user selected to sending an email to.
		{
			sendTo = SubscriptionData.SEND_TO_NONE;
			
			if ( m_primaryAddress1.isVisible() && m_primaryAddress1.getValue() == Boolean.TRUE )
				sendTo |= SubscriptionData.SEND_TO_PRIMARY_EMAIL_ADDRESS;
				
			if ( m_mobileAddress1.isVisible() && m_mobileAddress1.getValue() == Boolean.TRUE )
				sendTo |= SubscriptionData.SEND_TO_MOBILE_EMAIL_ADDRESS;
			
			if ( m_textAddress1.isVisible() && m_textAddress1.getValue() == Boolean.TRUE )
				sendTo |= SubscriptionData.SEND_TO_TEXT_ADDRESS;
			
			subscriptionData.setSendEmailTo( sendTo );
		}
		
		// See which addresses the user selected to send an email without an attachment to.
		{
			sendTo = SubscriptionData.SEND_TO_NONE;
			
			if ( m_primaryAddress2.isVisible() && m_primaryAddress2.getValue() == Boolean.TRUE )
				sendTo |= SubscriptionData.SEND_TO_PRIMARY_EMAIL_ADDRESS;
				
			if ( m_mobileAddress2.isVisible() && m_mobileAddress2.getValue() == Boolean.TRUE )
				sendTo |= SubscriptionData.SEND_TO_MOBILE_EMAIL_ADDRESS;
			
			if ( m_textAddress2.isVisible() && m_textAddress2.getValue() == Boolean.TRUE )
				sendTo |= SubscriptionData.SEND_TO_TEXT_ADDRESS;
			
			subscriptionData.setSendEmailToWithoutAttachment( sendTo );
		}
		
		// See which addresses the user selected to send a text to.
		{
			sendTo = SubscriptionData.SEND_TO_NONE;
			
			if ( m_primaryAddress3.isVisible() && m_primaryAddress3.getValue() == Boolean.TRUE )
				sendTo |= SubscriptionData.SEND_TO_PRIMARY_EMAIL_ADDRESS;
				
			if ( m_mobileAddress3.isVisible() && m_mobileAddress3.getValue() == Boolean.TRUE )
				sendTo |= SubscriptionData.SEND_TO_MOBILE_EMAIL_ADDRESS;
			
			if ( m_textAddress3.isVisible() && m_textAddress3.getValue() == Boolean.TRUE )
				sendTo |= SubscriptionData.SEND_TO_TEXT_ADDRESS;
			
			subscriptionData.setSendTextTo( sendTo );
		}

		return subscriptionData;
	}
	
	
	/**
	 * Return the HelpData object needed for invoking help on this dialog.
	 */
	public HelpData getHelpData()
	{
		HelpData helpData;
		
		helpData = new HelpData();
		helpData.setGuideName( HelpData.USER_GUIDE );
		helpData.setPageId( "informed_notifications" );
		
		return helpData;
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	public FocusWidget getFocusWidget()
	{
		return null;
	}
	
	
	/**
	 * Issue an rpc request to get the current subscription settings for the given entry.
	 */
	public void init( String entryId, String entryTitle )
	{
		m_entryId = entryId;
		m_entryTitle = entryTitle;
		
		if ( m_readSubscriptionDataCallback == null )
		{
			m_readSubscriptionDataCallback = new AsyncCallback<VibeRpcResponse>()
			{
				@Override
				public void onFailure( Throwable caught )
				{
					GwtClientHelper.handleGwtRPCFailure(
						caught,
						GwtTeaming.getMessages().rpcFailure_GetSubscriptionData() );
				}// end onFailure()

				@Override
				public void onSuccess( VibeRpcResponse result )
				{
					// Update the dialog with the given subscription data.
					SubscriptionDataRpcResponseData responseData = ((SubscriptionDataRpcResponseData) result.getResponseData());
					SubscriptionData subscriptionData = responseData.getSubscriptionDataResults();
					updateDlg( subscriptionData, m_entryTitle );
				}// end onSuccess()
			};
		}
		
		// Issue an ajax request to get the subscription data for the entry we are working with.
		GetSubscriptionDataCmd cmd = new GetSubscriptionDataCmd( m_entryId );
		GwtClientHelper.executeCommand( cmd, m_readSubscriptionDataCallback );
	}
	
	
	/**
	 * Update the visibility of the given checkboxes.
	 */
	private void updateCheckboxes( String address, CheckBox sendEmailCkbox, CheckBox sendEmailWithoutAttachmentCkbox, CheckBox sendTxtCkbox )
	{
		boolean visible;
		
		// If we don't have an email address then hide the checkboxes.
		visible = true;
		if ( address == null || address.length() == 0 )
			visible = false;

		sendEmailCkbox.setVisible( visible );
		sendEmailWithoutAttachmentCkbox.setVisible( visible );
		sendTxtCkbox.setVisible( visible );

		if ( visible )
		{
			// Update the text used by the checkboxes.
			sendEmailCkbox.setText( address );
			sendEmailWithoutAttachmentCkbox.setText( address );
			sendTxtCkbox.setText( address );
		}			
	}
	

	/**
	 * Update whether the checkboxes should be checked.
	 */
	private void updateCheckboxes( CheckBox sendEmailCkbox, CheckBox sendEmailWithoutAttachmentCkbox, CheckBox sendTxtCkbox, int sendTo )
	{
		if ( sendEmailCkbox.isVisible() )
		{
			// Should the "send email to primary" checkbox be checked?
			if ( (sendTo & SubscriptionData.SEND_TO_PRIMARY_EMAIL_ADDRESS) > 0 )
				sendEmailCkbox.setValue( Boolean.TRUE );
		}
		
		if ( sendEmailWithoutAttachmentCkbox.isVisible() )
		{
			// Should the "send email without attachment" checkbox be checked?
			if ( (sendTo & SubscriptionData.SEND_TO_MOBILE_EMAIL_ADDRESS) > 0 )
				sendEmailWithoutAttachmentCkbox.setValue( Boolean.TRUE );
		}
		
		if ( sendTxtCkbox.isVisible() )
		{
			// Should the "send text" checkbox be checked?
			if ( (sendTo & SubscriptionData.SEND_TO_TEXT_ADDRESS) > 0 )
				sendTxtCkbox.setValue( Boolean.TRUE );
		}
	}
	
	/**
	 * Update this dialog with the given subscription data.
	 */
	private void updateDlg( SubscriptionData subscriptionData, String entryTitle )
	{
		String primaryAddress;
		String mobileAddress;
		String textAddress;
		
		primaryAddress = subscriptionData.getPrimaryEmailAddress();
		mobileAddress = subscriptionData.getMobileEmailAddress();
		textAddress = subscriptionData.getTextMessagingAddress();
		
		// Remove all widgets from the dialog.
		m_mainPanel.clear();
		
		// If the user hasn't specified an email address tell the user.
		if ( (primaryAddress == null || primaryAddress.length() == 0) &&
			 (mobileAddress == null || mobileAddress.length() == 0 ) &&
			 (textAddress == null || textAddress.length() == 0 ) )
		{
			Label label;
			
			// Add a message that tells the user that they can't subscribe because they
			// have not specified any email addresses.
			label = new Label( GwtTeaming.getMessages().cantSubscribeNoEmailAddresses() );
			m_mainPanel.add( label );
			
			return;
		}
		
		// Limit the title we display to 20 characters.
		if ( entryTitle != null && entryTitle.length() > 25 )
		{
			entryTitle = entryTitle.substring( 0, 25 );
			entryTitle += "...";
		}
		m_entryTitleLabel.setText( entryTitle );
		
		m_mainPanel.add( m_titlePanel );
		m_mainPanel.add( m_sendEmailToPanel1 );
		m_mainPanel.add( m_sendEmailToPanel2 );
		m_mainPanel.add( m_sendTextToPanel );
		
		// Uncheck all the checkboxes.
		{
			m_primaryAddress1.setValue( Boolean.FALSE );
			m_primaryAddress2.setValue( Boolean.FALSE );
			m_primaryAddress3.setValue( Boolean.FALSE );
			m_mobileAddress1.setValue( Boolean.FALSE );
			m_mobileAddress2.setValue( Boolean.FALSE );
			m_mobileAddress3.setValue( Boolean.FALSE );
			m_textAddress1.setValue( Boolean.FALSE );
			m_textAddress2.setValue( Boolean.FALSE );
			m_textAddress3.setValue( Boolean.FALSE );
		}
		
		// Set whether the primary email address checkboxes are visible or not.  If the user does not
		// have a primary email address then the 3 checkboxes should not be visible.
		// If the checkbox is visible set it state.
		updateCheckboxes( primaryAddress, m_primaryAddress1, m_primaryAddress2, m_primaryAddress3 );
		updateCheckboxes( m_primaryAddress1, m_mobileAddress1, m_textAddress1, subscriptionData.getSendEmailTo() );
		
		// Set whether the mobile email address checkboxes are visible or not.  If the user does not
		// have a mobile email address then the 3 checkboxes should not be visible.
		// If the checkbox is visible set it state.
		updateCheckboxes( mobileAddress, m_mobileAddress1, m_mobileAddress2, m_mobileAddress3 );
		updateCheckboxes( m_primaryAddress2, m_mobileAddress2, m_textAddress2, subscriptionData.getSendEmailToWithoutAttachment() );

		// Set whether the text address checkboxes are visible or not.  If the user does not
		// have a mobile email address then the 3 checkboxes should not be visible.
		// If the checkbox is visible set it state.
		updateCheckboxes( textAddress, m_textAddress1, m_textAddress2, m_textAddress3 );
		updateCheckboxes( m_primaryAddress3, m_mobileAddress3, m_textAddress3, subscriptionData.getSendTextTo() );
	}
}

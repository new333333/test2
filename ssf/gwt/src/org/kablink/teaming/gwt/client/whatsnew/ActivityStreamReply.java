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


package org.kablink.teaming.gwt.client.whatsnew;

import java.util.HashMap;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

/**
 * This widget is used to let the user enter a reply to an entry
 * @author jwootton
 *
 */
public class ActivityStreamReply extends Composite
{
	private TextBox m_titleTextBox;
	private TextArea m_descTextArea;
	private EditSuccessfulHandler m_onSuccessHandler;
	
	/**
	 * 
	 */
	public ActivityStreamReply( EditSuccessfulHandler onSuccessHandler )
	{
		FlowPanel mainPanel;
		FlowPanel inputPanel;
		FlowPanel footerPanel;
		
		// Remember the handler we should call when the user presses Ok.
		m_onSuccessHandler = onSuccessHandler;
		
		mainPanel = new FlowPanel();
		mainPanel.addStyleName( "activityStreamReply" );
		mainPanel.addStyleName( "roundcornerSM" );
		
		// Add a textbox for the title
		inputPanel = new FlowPanel();
		inputPanel.addStyleName( "activityStreamReplyTitlePanel" );
		m_titleTextBox = new TextBox();
		m_titleTextBox.addStyleName( "activityStreamReplyTitleTextBox" );
		inputPanel.add( m_titleTextBox );
		mainPanel.add( inputPanel );
		
		// Create a textbox
		inputPanel = new FlowPanel();
		m_descTextArea = new TextArea();
		m_descTextArea.addStyleName( "activityStreamReplyTextArea" );
		inputPanel.add( m_descTextArea );
		mainPanel.add( inputPanel );
		
		// Create the footer.
		footerPanel = createFooter();
		mainPanel.add( footerPanel );
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}
	
	/**
	 * 
	 */
	public void close()
	{
		setVisible( false );
	}
	
	/**
	 * Create the footer panel
	 */
	private FlowPanel createFooter()
	{
		FlowPanel footerPanel;
		Button sendBtn;
		Button cancelBtn;
		ClickHandler clickHandler;
		
		footerPanel = new FlowPanel();
		footerPanel.addStyleName( "activityStreamReplyFooter" );

		sendBtn = new Button( GwtTeaming.getMessages().send() );
		sendBtn.addStyleName( "activityStreamReplyBtn" );
		sendBtn.addStyleName( "roundcorner" );
		footerPanel.add( sendBtn );
		
		// Add a click handler for the send button.
		clickHandler = new ClickHandler()
		{
			public void onClick( ClickEvent event )
			{
				handleClickOnSendBtn();
			}
		};
		sendBtn.addClickHandler( clickHandler );
		
		cancelBtn = new Button( GwtTeaming.getMessages().cancel() );
		cancelBtn.addStyleName( "activityStreamReplyBtn" );
		cancelBtn.addStyleName( "roundcorner" );
		footerPanel.add( cancelBtn );
		
		// Add a click handler for the cancel button.
		clickHandler = new ClickHandler()
		{
			public void onClick( ClickEvent event )
			{
				close();
			}
		};
		cancelBtn.addClickHandler( clickHandler );

		return footerPanel;
	}
	
	/**
	 * Return the text the user has entered for the description.
	 */
	public String getDesc()
	{
		return m_descTextArea.getText();
	}
	
	/**
	 * This gets called when the user clicks on the Send button.
	 */
	private void handleClickOnSendBtn()
	{
		if ( m_onSuccessHandler != null )
		{
			String title;
			String replyText;
			HashMap<String, String> results;
			
			// Get the reply entered by the user.
			replyText = m_descTextArea.getText();
			
			if ( GwtClientHelper.hasString( replyText ) == false )
			{
				Window.alert( GwtTeaming.getMessages().noReplyText() );
				setFocusToTextArea();
				return;
			}
			
			// HTML escape the text entered by the user and replace newlines with <br>
			{
				SafeHtmlBuilder builder;
				
				builder = new SafeHtmlBuilder();
				builder = builder.appendEscapedLines( replyText );
				replyText = builder.toSafeHtml().asString();
			}

			title = m_titleTextBox.getText();
			
			results = new HashMap<String, String>();
			results.put( "title", title );
			results.put( "description", replyText );
			m_onSuccessHandler.editSuccessful( results );
		}
		
		close();
	}
	
	/**
	 * 
	 */
	private void init( String title )
	{
		m_titleTextBox.setText( title );
		m_descTextArea.setText( "" );
	}

	/**
	 * 
	 */
	private void setFocusToTextArea()
	{
		Scheduler.ScheduledCommand cmd;
		
		// Issue a deferred command to give the focus to the textarea control.
		cmd = new Scheduler.ScheduledCommand()
		{
			public void execute()
			{
				m_descTextArea.setFocus( true );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}

	/**
	 * 
	 */
	public void show( String title )
	{
		init( title );
		setVisible( true );
		
		// Give the focus to the textarea.
		setFocusToTextArea();
	}
}

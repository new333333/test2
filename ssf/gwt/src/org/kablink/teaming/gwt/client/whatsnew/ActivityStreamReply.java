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
package org.kablink.teaming.gwt.client.whatsnew;

import java.util.HashMap;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

/**
 * This widget is used to let the user enter a reply to an entry.
 * 
 * @author drfoster@novell.com
 */
public class ActivityStreamReply extends Composite {
	private EditSuccessfulHandler	m_onSuccessHandler;	//
	private FlowPanel				m_addACommentPanel;	//
	private GwtTeamingMessages		m_messages;			//
	private HTML					m_descHTMLRenderer;	//
	private TextArea				m_descTextArea;		//
	private TextBox					m_titleTextBox;		//
	
	/**
	 * Constructor method.
	 * 
	 * @param showAddACommentHint
	 * @param onSuccessHandler
	 */
	public ActivityStreamReply(boolean showAddACommentHint, EditSuccessfulHandler onSuccessHandler) {
		// Initialize the super class...
		super();
		
		// ...store the parameters...
		m_onSuccessHandler = onSuccessHandler;
		
		// ...and initialize anything else that requires it.
		m_messages = GwtTeaming.getMessages();
		
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.addStyleName("activityStreamReply roundcornerSM");
		
		// Add a text box for the title.
		FlowPanel inputPanel;
		if (!(GwtTeaming.m_requestInfo.isLicenseFilr())) {
			inputPanel = new FlowPanel();
			inputPanel.addStyleName("activityStreamReplyTitlePanel");
			m_titleTextBox = new TextBox();
			m_titleTextBox.addStyleName("activityStreamReplyTitleTextBox");
			inputPanel.add(m_titleTextBox);
			mainPanel.add(inputPanel);
		}
		
		inputPanel = new FlowPanel();
		inputPanel.addStyleName("activityStreamReply_RelativePos");

		// Add a panel that will hold 'Add a comment' hint.
		m_addACommentPanel = new FlowPanel();
		m_addACommentPanel.addStyleName("activityStreamReplyAddACommentPanel");
		m_addACommentPanel.getElement().setInnerText(m_messages.addAComment());
		inputPanel.add(m_addACommentPanel);
		m_addACommentPanel.setVisible(showAddACommentHint);

		// Create a hidden HTML widget we can use to clean the
		// HTML'isms from descriptions we loading into the description
		// <TEXTAREA>
		m_descHTMLRenderer = new HTML();
		inputPanel.add(m_descHTMLRenderer);
		m_descHTMLRenderer.setVisible(false);
		
		// Create a text box.
		m_descTextArea = new TextArea();
		m_descTextArea.addStyleName("activityStreamReplyTextArea");
		m_descTextArea.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						// Hide the 'Add a comment' message.
						m_addACommentPanel.setVisible(false);
					}
				});
			}
		});
		inputPanel.add(m_descTextArea);
		mainPanel.add(inputPanel);
		
		// Create the footer.
		FlowPanel footerPanel = createFooter();
		mainPanel.add(footerPanel);
		
		// All composites must call initWidget() in their constructors.
		initWidget(mainPanel);
	}
	
	public ActivityStreamReply(EditSuccessfulHandler onSuccessHandler) {
		// Always use the initial form of the constructor.
		this(true, onSuccessHandler);
	}
	
	/**
	 * Hides the reply widget. 
	 */
	public void close() {
		setVisible(false);
	}
	
	/*
	 * Create the footer panel.
	 */
	private FlowPanel createFooter() {
		FlowPanel footerPanel = new FlowPanel();
		footerPanel.addStyleName("activityStreamReplyFooter");

		Button sendBtn = new Button(m_messages.send());
		sendBtn.addStyleName("activityStreamReplyBtn");
		footerPanel.add(sendBtn);
		
		// Add a click handler for the send button.
		sendBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				handleClickOnSendBtn();
			}
		});
		
		Button cancelBtn = new Button(m_messages.cancel());
		cancelBtn.addStyleName("activityStreamReplyBtn");
		footerPanel.add(cancelBtn);
		
		// Add a click handler for the cancel button.
		cancelBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				close();
			}
		});

		return footerPanel;
	}
	
	/**
	 * Return the text the user has entered for the description.
	 * 
	 * @return
	 */
	public String getDesc() {
		return m_descTextArea.getText();
	}
	
	/*
	 * This gets called when the user clicks on the Send button.
	 */
	private void handleClickOnSendBtn() {
		if (null != m_onSuccessHandler) {
			// Get the reply entered by the user.
			String replyText = m_descTextArea.getText();
			if (!(GwtClientHelper.hasString(replyText))) {
				GwtClientHelper.deferredAlert(m_messages.noReplyText());
				setFocusToTextArea();
				return;
			}
			
			// HTML escape the text entered by the user and replace
			// newlines with <BR>.
			SafeHtmlBuilder builder = new SafeHtmlBuilder();
			builder = builder.appendEscapedLines(replyText);
			replyText = builder.toSafeHtml().asString();

			String title;
			if (null != m_titleTextBox)
			     title = m_titleTextBox.getText();
			else title = null;
			
			HashMap<String, String> results = new HashMap<String, String>();
			results.put("title",       title    );
			results.put("description", replyText);
			m_onSuccessHandler.editSuccessful(results);
		}
		
		close();
	}

	/*
	 */
	private void init(String title, String description) {
		if (null != m_titleTextBox) {
			m_titleTextBox.setText(title);
		}
		
		// Bugzilla 947249:  Clean any HTML'isms in the description so
		// that it can be properly handled by the <TEXTAREA>.
		m_descHTMLRenderer.setHTML(description);
		m_descTextArea.setText(m_descHTMLRenderer.getText());
	}

	/*
	 */
	private void setFocusToTextArea() {
		// Issue a deferred command to give the focus to the text area
		// control.
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				m_descTextArea.setFocus(true);
			}
		});
	}

	/**
	 * ?
	 * 
	 * @param title
	 * @param description
	 */
	public void show(String title, String description) {
		init(title, description);
		setVisible(true);
		
		// Give the focus to the text area.
		setFocusToTextArea();
	}
	
	public void show(String title) {
		// Always use the initial form of the method.
		show(title, "");
	}
}

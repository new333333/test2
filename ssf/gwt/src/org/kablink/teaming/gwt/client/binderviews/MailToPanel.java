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
package org.kablink.teaming.gwt.client.binderviews;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * Class used for the content of the mail to form in the binder views.  
 * 
 * @author drfoster@novell.com
 */
public class MailToPanel extends ToolPanelBase {
	private FormPanel		m_mailToForm;	//
	private VibeFlowPanel	m_fp;			// The panel holding the MailToPanel's contents.

	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private MailToPanel(RequiresResize containerResizer, BinderInfo binderInfo, ToolPanelReady toolPanelReady) {
		// Initialize the super class...
		super(containerResizer, binderInfo, toolPanelReady);
		
		// ...initialize the data members...
		
		// ...and construct the panel.
		m_fp = new VibeFlowPanel();
		initWidget(m_fp);
		loadPart1Async();
	}

	/**
	 * Loads the MailToPanel split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param containerResizer
	 * @param binderInfo
	 * @param toolPanelReady
	 * @param tpClient
	 */
	public static void createAsync(final RequiresResize containerResizer, final BinderInfo binderInfo, final ToolPanelReady toolPanelReady, final ToolPanelClient tpClient) {
		GWT.runAsync(MailToPanel.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess() {
				MailToPanel fp = new MailToPanel(containerResizer, binderInfo, toolPanelReady);
				tpClient.onSuccess(fp);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_MailToPanel());
				tpClient.onUnavailable();
			}
		});
	}

	/**
	 * Creates a FormPanel appropriate for use for 'mailto:' URLs.
	 * 
	 * @return
	 */
	public static FormPanel createMailToForm() {
		FormPanel m_reply = new FormPanel("_blank");	// Form target is always '_blank'.
		m_reply.setMethod(FormPanel.METHOD_GET);		// Default to get...
		m_reply.setEncoding("text/plain");				// ...with plain text encoding.
		return m_reply;
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public FormPanel getForm() {
		// Clear the <FORM>'s content and return it.
		m_mailToForm.clear();
		return m_mailToForm;
	}
	
	/*
	 * Asynchronously construct's the contents of the entry menu panel.
	 */
	private void loadPart1Async() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		});
	}
	
	/*
	 * Synchronously construct's the contents of the mail to panel.
	 */
	private void loadPart1Now() {
		m_mailToForm = createMailToForm();
		m_fp.add(m_mailToForm);
		toolPanelReady();
	}

	/**
	 * Called from the binder view to allow the panel to do any
	 * work required to reset itself.
	 * 
	 * Implements ToolPanelBase.resetPanel()
	 */
	@Override
	public void resetPanel() {
		// Reset the widgets and reload the mail to panel.
		m_fp.clear();
		m_fp.removeStyleName("vibe-binderViewTools vibe-MailToPanel");
		loadPart1Async();
	}
}

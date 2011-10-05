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

package org.kablink.teaming.gwt.client.binderviews;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.binderviews.ViewBase;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.widgets.VibeDockLayoutPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * Discussion folder view.
 * 
 * @author drfoster@novell.com
 */
public class DiscussionFolderView extends ViewBase {
	private Long				m_binderId;		// The ID of the discussion folder to be viewed.				
	private VibeDockLayoutPanel	m_mainPanel;	// The panel holding the content of the view.
	
	/**
	 * Constructor method.
	 * 
	 * @param binderId
	 * @param viewReady
	 */
	public DiscussionFolderView(Long binderId, ViewReady viewReady) {
		// Initialize the base class...
		super(viewReady);

		// ...store the parameters...
		m_binderId = binderId;

		// ...create the main content panel...
		m_mainPanel = new VibeDockLayoutPanel(Style.Unit.PX);
		m_mainPanel.addStyleName("gwt-folderView gwt-discussionFolderMainPanel");
		m_mainPanel.add(new FlowPanel());

		// ...initialize the composite...
		initWidget(m_mainPanel);

		// ...and finally, asynchronously initialize the view.
		initializeViewAsync();
	}
	
	/**
	 * Callback interface used to interact with a discussion folder
	 * view asynchronously after it loads. 
	 */
	public interface DiscussionFolderViewClient {
		void onSuccess(DiscussionFolderView dfView);
		void onUnavailable();
	}

	/*
	 * Construct this discussion folder view.
	 */
	private void constructDiscussionFolderView() {
		FlowPanel fp = ((FlowPanel) m_mainPanel.getCenter());
		fp.clear();
		
//!		...this needs to be implemented...
		fp.add(new InlineLabel("DiscussionFolderView.constructDiscussionFolderView(" + m_binderId + "):  ...this needs to be implemented..."));

		// Tell the base class that we're done constructing the
		// discussion folder view.
		super.viewReady();
	}
	
	/**
	 * Loads the DiscussionFolderView split point and returns an
	 * instance of it via the callback.
	 * 
	 * @param dfvClient
	 */
	public static void createAsync(final Long binderId, final ViewReady viewReady, final DiscussionFolderViewClient dfvClient) {
		GWT.runAsync(DiscussionFolderView.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				DiscussionFolderView dfView = new DiscussionFolderView(binderId, viewReady);
				dfvClient.onSuccess(dfView);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_DiscussionFolderView());
				dfvClient.onUnavailable();
			}
		});
	}
	
	/*
	 * Asynchronously initialize this discussion folder.
	 */
	private void initializeViewAsync() {
		Scheduler.ScheduledCommand doInit = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				initializeViewNow();
			}
		};
		Scheduler.get().scheduleDeferred(doInit);
	}
	
	/*
	 * Synchronously initialize this discussion folder.
	 */
	private void initializeViewNow() {
		// Construct the discussion folder view.
		constructDiscussionFolderView();
	}
}

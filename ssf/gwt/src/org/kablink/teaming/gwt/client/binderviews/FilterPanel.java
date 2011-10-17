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
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ResizeComposite;


/**
 * Class used for the content of the additional search options.  
 * 
 * @author drfoster@novell.com
 */
public class FilterPanel extends ResizeComposite {
	private VibeFlowPanel	m_fp;		// The panel holding the AccessoryPanel's contents.
	private Long			m_binderId;	// The ID of the binder whose filters are being managed.
	
	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private FilterPanel(Long binderId) {
		// Initialize the super class...
		super();
		

		// ...store the parameters....
		m_binderId = binderId;
		
		// ...and construct the panel.
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-filterPanel");
		initWidget(m_fp);
		constructFilterPanelAsync();
	}

	/*
	 * Asynchronously construct's the contents of the filter panel
	 */
	private void constructFilterPanelAsync() {
		ScheduledCommand constructFilterPanel = new ScheduledCommand() {
			@Override
			public void execute() {
				constructFilterPanelNow();
			}
		};
		Scheduler.get().scheduleDeferred(constructFilterPanel);
	}
	
	/*
	 * Synchronously construct's the contents of the filter panel
	 */
	private void constructFilterPanelNow() {
//!		...this needs to be implemented...
		m_fp.add(new InlineLabel("FilterPanel.constructFilterPanel( " + m_binderId + " ):  ...this needs to be implemented..."));
	}
	
	/**
	 * Callback interface to interact with the FilterPanel
	 * asynchronously after it loads. 
	 */
	public interface FilterPanelClient {
		void onSuccess(FilterPanel fp);
		void onUnavailable();
	}

	/**
	 * Loads the FilterPanel split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param binderId
	 * @param fpClient
	 */
	public static void createAsync(final Long binderId, final FilterPanelClient fpClient) {
		GWT.runAsync(FilterPanel.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess() {
				FilterPanel fp = new FilterPanel(binderId);
				fpClient.onSuccess(fp);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_FilterPanel());
				fpClient.onUnavailable();
			}
		});
	}
}

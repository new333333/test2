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
package org.kablink.teaming.gwt.client.widgets;

import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ResizeComposite;

/**
 * This widget will display desktop application download control.
 * 
 * @author drfoster@novell.com
 */
public class DesktopAppDownloadControl extends ResizeComposite {
	private GwtMainPage		m_mainPage;		//
	private VibeFlowPanel	m_mainPanel;	//
	
	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private DesktopAppDownloadControl(GwtMainPage mainPage) {
		// Initialize the super class...
		super();

		// ...store the parameter...
		m_mainPage = mainPage;

		// ...create the panel to hold the control's content...
		m_mainPanel = new VibeFlowPanel();
		m_mainPanel.addStyleName("vibe-desktopAppDownloadControl");
		
//!		...this needs to be implemented...

		// Finally, tell the composite that we're good to go.
		initWidget(m_mainPanel);
	}
	
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the desktop application download control and perform some     */
	/* operation on it.                                              */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the desktop application
	 * download control asynchronously after it loads. 
	 */
	public interface DesktopAppDownloadControlClient {
		void onSuccess(DesktopAppDownloadControl dadControl);
		void onUnavailable();
	}

	/**
	 * Asynchronously creates a DesktopAppDownloadControl via its split
	 * point.
	 */
	public static void createAsync(final GwtMainPage mainPage, final DesktopAppDownloadControlClient dadControlClient) {
		GWT.runAsync(DesktopAppDownloadControl.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_DesktopAppDownloadControl());
				if (null != dadControlClient) {
					dadControlClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Create the control and return it via the callback.
				DesktopAppDownloadControl dadControl = new DesktopAppDownloadControl(mainPage);
				dadControlClient.onSuccess(dadControl);
			}
		});
	}
}

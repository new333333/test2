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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.SetDesktopDownloadAppControlVisibilityEvent;
import org.kablink.teaming.gwt.client.rpc.shared.SetDesktopAppDownloadVisibilityCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DesktopAppDownloadControlCookies.Cookie;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ResizeComposite;

/**
 * This widget will display desktop application download control.
 * 
 * @author drfoster@novell.com
 */
public class DesktopAppDownloadControl extends ResizeComposite {
	private GwtTeamingImageBundle	m_images;		// Access to Vibe's images.
	private GwtTeamingMessages		m_messages;		// Access to the GWT localized string resource.
	private VibeFlowPanel			m_mainPanel;	// Panel containing the main content of the control.
	
	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private DesktopAppDownloadControl() {
		// Initialize the super class...
		super();
		
		// ...initialize the data members that require it...
		m_images   = GwtTeaming.getImageBundle();
		m_messages = GwtTeaming.getMessages();

		// ...create the panel to hold the control's content...
		m_mainPanel = new VibeFlowPanel();
		m_mainPanel.addStyleName("vibe-desktopAppCtrl-control");

		// ...create the content itself...
		createContent();

		// Finally, tell the composite that we're good to go.
		initWidget(m_mainPanel);
	}

	/*
	 * Creates the content for the desktop application download
	 * control.
	 */
	private void createContent() {
		// Add the main hint panel...
		VibeFlowPanel fp = new VibeFlowPanel();
		fp.addStyleName("vibe-desktopAppCtrl-hintPanel");
		m_mainPanel.add(fp);
		InlineLabel il = new InlineLabel(GwtClientHelper.isLicenseFilr() ? m_messages.desktopAppCtrl_Hint_Filr() : m_messages.desktopAppCtrl_Hint_Vibe());
		il.addStyleName("vibe-desktopAppCtrl-hintLabel");
		fp.add(il);

		// ...add the 'Don't Show Again' panel...
		fp = new VibeFlowPanel();
		fp.addStyleName("vibe-desktopAppCtrl-dontShowPanel");
		m_mainPanel.add(fp);
		il = new InlineLabel(m_messages.desktopAppCtrl_DontShowAgain());
		il.addStyleName("vibe-desktopAppCtrl-dontShowLabel");
		fp.add(il);
		il.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Store the visibility setting in the user's
				// properties...
				SetDesktopAppDownloadVisibilityCmd cmd = new SetDesktopAppDownloadVisibilityCmd(false);
				GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
					@Override
					public void onFailure(Throwable caught) {
						GwtClientHelper.handleGwtRPCFailure(
							caught,
							m_messages.rpcFailure_SetDesktopAppDownloadVisibility());
					}

					@Override
					public void onSuccess(VibeRpcResponse response) {
						// ...and hide the control.
						GwtTeaming.fireEventAsync(
							new SetDesktopDownloadAppControlVisibilityEvent(
								false));
					}
				});
			}
		});
		
		// ...and add the 'Hide for Session' panel...
		fp = new VibeFlowPanel();
		fp.addStyleName("vibe-desktopAppCtrl-closePanel");
		m_mainPanel.add(fp);
		Image i = GwtClientHelper.buildImage(m_images.closeBorder().getSafeUri().asString(), m_messages.desktopAppCtrl_Alt_HideForSession());
		i.addStyleName("vibe-desktopAppCtrl-closeImg");
		fp.add(i);
		i.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Store the visibility setting as a cookie...
				DesktopAppDownloadControlCookies.setBooleanCookieValue(
					Cookie.HINT_VISIBLE,
					false);
				
				// ...and hide the control.
				GwtTeaming.fireEventAsync(
					new SetDesktopDownloadAppControlVisibilityEvent(
						false));
			}
		});
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
	public static void createAsync(final DesktopAppDownloadControlClient dadControlClient) {
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
				DesktopAppDownloadControl dadControl = new DesktopAppDownloadControl();
				dadControlClient.onSuccess(dadControl);
			}
		});
	}
}

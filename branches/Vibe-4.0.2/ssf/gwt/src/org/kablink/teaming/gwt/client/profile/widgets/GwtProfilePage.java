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

package org.kablink.teaming.gwt.client.profile.widgets;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.SizeChangedEvent;
import org.kablink.teaming.gwt.client.profile.ProfileCategory;
import org.kablink.teaming.gwt.client.profile.ProfileInfo;
import org.kablink.teaming.gwt.client.profile.ProfileRequestInfo;
import org.kablink.teaming.gwt.client.profile.widgets.ProfileAttributeWidget.ProfileAttributeWidgetClient;
import org.kablink.teaming.gwt.client.rpc.shared.GetProfileInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class GwtProfilePage extends Composite {
	// profileRequestInfo is now public static to match the definition
	// of the m_requestInfo in GwtMainPage.  This was necessary for the
	// proper operation of HttpRequestInfo.createHttpRequestInfo() from
	// both the main page and the profile page.
	public static ProfileRequestInfo profileRequestInfo = jsGetProfileRequestInfo();
	
	private ProfileMainPanel profileMainPanel;
	private ProfileSidePanel profileSidePanel;
	private FlowPanel profilePanel;
	private FlowPanel mainProfilePage;

	/*
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private GwtProfilePage() {
		// Outer div around the page
		mainProfilePage = new FlowPanel();
		mainProfilePage.getElement().setId("profileContents");
		mainProfilePage.addStyleName("section1");

		// Main Panel
		profilePanel = new FlowPanel();
		profilePanel.addStyleName("profileSection");
		mainProfilePage.add(profilePanel);

		// The title Bar
		createProfileTitleBar();

		// Create a horizontal Panel to split the panel into a main info panel
		// and a side panel
		HorizontalPanel hPanel = createHorizontalPanel();

		// create main profile info panel
		createProfileMainPanel(hPanel);

		// Add the tracking info and team info to right pane
		createProfileSidePanel(hPanel);

		profileMainPanel.setEditable(false);

		// initialize the page with data
		initialize();

		// All composites must call initWidget() in their constructors.
		initWidget(mainProfilePage);
}

	private void initialize() {

		createProfileInfoSections();
	}

	private HorizontalPanel createHorizontalPanel() {

		// Create a horizontal Panel to split the panel into a main info panel
		// and a side panel
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.addStyleName("profileTable");
		hPanel.setWidth("100%");
		profilePanel.add(hPanel);

		return hPanel;
	}

	private void createProfileSidePanel(HorizontalPanel panel) {
		profileSidePanel = new ProfileSidePanel(profileRequestInfo);
		panel.add(profileSidePanel);
		panel.setCellHorizontalAlignment(profileSidePanel,
				HasHorizontalAlignment.ALIGN_RIGHT);
	}

	private void createProfileMainPanel(HorizontalPanel panel) {
		// Add the profile info to the left pane
		profileMainPanel = new ProfileMainPanel(profileRequestInfo, this);
		panel.add(profileMainPanel);
		panel.setCellWidth(profileMainPanel, "100%");
	}

	private void createProfileTitleBar() {

		FlowPanel titleBar = new FlowPanel();
		titleBar.addStyleName("column-head");
		profilePanel.add(titleBar);

		// Title
		Label profileLabel = new Label(GwtTeaming.getMessages().qViewProfile());
		titleBar.add(profileLabel);
	}

	/**
	 * Create the Profile Heading Sections and their associated Profile
	 * Attributes
	 * 
	 * @param profileRequestInfo
	 */
	private void createProfileInfoSections() {

		GetProfileInfoCmd cmd;
		
		// create an async callback to handle the result of the request to get
		// the state:
		AsyncCallback<VibeRpcResponse> callback = new AsyncCallback<VibeRpcResponse>() {
			public void onFailure(Throwable t) {
				// display error
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetProfileInfo(),
					profileRequestInfo.getBinderId());
			}

			public void onSuccess( VibeRpcResponse response ) {
				ProfileInfo profile;
				
				profile = (ProfileInfo) response.getResponseData();
				
				for (ProfileCategory cat: profile.getCategories()) {
					if(cat != null) {
						String catName = cat.getName();
						if (catName != null && catName.equals("profileSidePanelView")) {
							profileSidePanel.setCategory(cat);
							continue;
						}

						profileMainPanel.setCategory(cat);
					}
				}
				
				// relayout the page now
				relayoutPage();
			}
		};

		cmd = new GetProfileInfoCmd( profileRequestInfo.getBinderId() );
		GwtClientHelper.executeCommand( cmd, callback );
	}

	private void relayoutPage() {
		SizeChangedEvent.fireOne();
	}
	
	/*
	 * Use JSNI to grab the JavaScript object that holds the information about
	 * the request dealing with.
	 */
	private static native ProfileRequestInfo jsGetProfileRequestInfo() /*-{
		// Return a reference to the JavaScript variable called, m_requestInfo.
		return $wnd.m_requestInfo;
	}-*/;
	
	public void updateQuota(String usedQuota) {
		profileSidePanel.updateQuota(usedQuota);
	}
	
	/**
	 * Callback interface to interact with the profile page
	 * asynchronously after it loads. 
	 */
	public interface GwtProfilePageClient {
		void onSuccess(GwtProfilePage profilePage);
		void onUnavailable();
	}

	/**
	 * Loads the GwtProfilePage split point and returns an
	 * instance of it via the callback.
	 * 
	 * @param profilePageClient
	 */
	public static void createAsync(final GwtProfilePageClient profilePageClient) {
		// The GwtProfilePage is dependent on the
		// ProfileAttributeWidget.  Make sure it has been fetched
		// before trying to use it.
		ProfileAttributeWidget.prefetch(new ProfileAttributeWidgetClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in
				// asynchronous provider.
				profilePageClient.onUnavailable();
			}
			
			@Override
			public void onSuccess(ProfileAttributeWidget paw, int row) {
				GWT.runAsync(GwtProfilePage.class, new RunAsyncCallback() {			
					@Override
					public void onSuccess() {
						GwtProfilePage profilePage = new GwtProfilePage();
						profilePageClient.onSuccess(profilePage);
					}
					
					@Override
					public void onFailure(Throwable reason) {
						Window.alert(GwtTeaming.getMessages().codeSplitFailure_ProfilePage());
						profilePageClient.onUnavailable();
					}
				});
			}
		});
	}
}

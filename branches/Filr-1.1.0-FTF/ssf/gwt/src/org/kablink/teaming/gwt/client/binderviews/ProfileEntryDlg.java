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

import java.util.Set;
import java.util.Map;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.GetProfileEntryInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ProfileEntryInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ProfileEntryInfoRpcResponseData.ProfileAttribute;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * Implements Vibe's profile entry dialog.
 *  
 * @author drfoster@novell.com
 */
public class ProfileEntryDlg extends DlgBox {
	private GwtTeamingMessages				m_messages;			// Access to Vibe's messages.
	private Long							m_userId;			// The user whose profile entry we're dealing with.
	private ProfileEntryInfoRpcResponseData	m_profileEntryInfo;	//
	private VibeFlowPanel					m_fp;				// The panel holding the dialog's content.

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ProfileEntryDlg() {
		// Initialize the superclass...
		super(true, false, DlgButtonMode.Close);

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.profileEntryDlgHeader(),	// The dialog's header.
			getSimpleSuccessfulHandler(),		// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),			// The dialog's EditCanceledHandler.
			null);								// Create callback data.  Unused.
	}

	/**
	 * Creates all the controls that make up the dialog.
	 * 
	 * Implements the DlgBox.createContent() abstract method.
	 * 
	 * @param callbackData (unused)
	 * 
	 * @return
	 */
	@Override
	public Panel createContent(Object callbackData) {
		// Create and return a panel to hold the dialog's content.
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-profileEntryDlg-rootPanel");
		return m_fp;
	}

	/**
	 * Unused.
	 * 
	 * Implements the DlgBox.getDataFromDlg() abstract method.
	 * 
	 * @return
	 */
	@Override
	public Object getDataFromDlg() {
		// Unused.
		return "";
	}

	/**
	 * Returns the Widget to give the focus to.
	 * 
	 * Implements the DlgBox.getFocusWidget() abstract method.
	 * 
	 * @return
	 */
	@Override
	public FocusWidget getFocusWidget() {
		return null;
	}

	/*
	 * Ensures a string being used for label ends with a ':'.
	 */
	private String labelizeCaption(String s) {
		if (null != s) s = s.trim();
		int l = ((null == s) ? 0 : s.length());
		if ((0 < l) && (':' != s.charAt(l - 1))) {
			s = m_messages.profileEntryDlgLabelize(s);
		}
		return s;
	}
	
	/*
	 * Asynchronously loads the entry types the user can select from.
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
	 * Synchronously loads the entry types the user can select from.
	 */
	private void loadPart1Now() {
		GwtClientHelper.executeCommand(
				new GetProfileEntryInfoCmd(m_userId),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetProfileEntryInfo());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Store the profile entry information and complete the
				// population of the dialog.
				m_profileEntryInfo = ((ProfileEntryInfoRpcResponseData) response.getResponseData());
				populateDlgAsync();
			}
		});
	}
	
	/*
	 * Asynchronously populates the contents of the dialog.
	 */
	private void populateDlgAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populateDlgNow();
			}
		});
	}
	
	/*
	 * Synchronously populates the contents of the dialog.
	 */
	private void populateDlgNow() {
		// Clear anything already in the dialog (from a previous
		// usage, ...)
		m_fp.clear();
		
		// ...create a grid to hold the dialog's contents...
		FlexTable grid = new FlexTable();
		grid.addStyleName("vibe-profileEntryDlg-grid");
		m_fp.add(grid);
		FlexCellFormatter fcm = grid.getFlexCellFormatter();

		// ...add the user's title...
		Map<String, ProfileAttribute> attrMap = m_profileEntryInfo.getProfileEntryInfo();
		ProfileAttribute pa = attrMap.get("title");
		String title = pa.getAttributeValue();
		InlineLabel il = new InlineLabel(title);
		il.addStyleName("vibe-profileEntryDlg-title");
		il.setWordWrap(false);
		grid.setWidget(0, 0, il);
		fcm.setColSpan(0, 0, 2);
		
		// ...and add the user's avatar.
		Image avatarImg = new Image();
		avatarImg.addStyleName("vibe-profileEntryDlg-avatar");
		String avatarUrl = m_profileEntryInfo.getAvatarUrl();
		if (!(GwtClientHelper.hasString(avatarUrl)))
		     avatarImg.setUrl(GwtTeaming.getDataTableImageBundle().userPhoto().getSafeUri());
		else avatarImg.setUrl(avatarUrl);
		avatarImg.setTitle(title);
		grid.setWidget(1, 0, avatarImg);
		fcm.setColSpan(1, 0, 2);

		// Scan the attributes we have for the user...
		Set<String> attrKeys = attrMap.keySet();
		for (String attrKey:  attrKeys) {
			// ...skipping the title...
			if (attrKey.equals("title")) {
				continue;
			}

			// ...and adding the attribute's caption...
			int row = grid.getRowCount();
			pa = attrMap.get(attrKey);
			il = new InlineLabel(labelizeCaption(pa.getAttributeCaption()));
			il.addStyleName("vibe-profileEntryDlg-attrCaption");
			il.setWordWrap(false);
			grid.setWidget(row, 0, il);

			// ...and value.
			String v = pa.getAttributeValue();
			il = new InlineLabel((null == v) ? "" : v);
			il.addStyleName("vibe-profileEntryDlg-attrValue");
			il.setWordWrap(false);
			grid.setWidget(row, 1, il);
		}

		// Do we have a URL for this user to modify this entry?
		VibeFlowPanel buttonPanel = new VibeFlowPanel();
		buttonPanel.addStyleName("vibe-profileEntryDlg-buttons");
		final String modifyUrl = m_profileEntryInfo.getModifyUrl();
		boolean hasModify = GwtClientHelper.hasString(modifyUrl);
		if (hasModify) {
			// Yes!  Create a push button so they can.
			Button button = new Button(m_messages.profileEntryDlgModify());
			button.addStyleName("vibe-profileEntryDlg-button vibe-profileEntryDlg-modify");
			buttonPanel.add(button);
			button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					hide();
					GwtClientHelper.jsLaunchToolbarPopupUrl(modifyUrl, 850, 600);
				}
			});
			
			// ...add the button panel to the grid.
			int row = grid.getRowCount();
			grid.setWidget(row, 0, buttonPanel);
			fcm.setColSpan(row, 0, 2);
		}

		// Finally, create a footer note about why the Quick View
		// profile is not being shown...
		Label footer = new Label(m_messages.profileEntryDlgNote());
		footer.addStyleName("vibe-profileEntryDlg-footer");
		m_fp.add(footer);

		// ...and show the dialog.
		show(true);
	}
	
	/*
	 * Asynchronously runs the given instance of the profile entry
	 * dialog.
	 */
	private static void runDlgAsync(final ProfileEntryDlg peDlg, final Long userId) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				peDlg.runDlgNow(userId);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the profile entry
	 * dialog.
	 */
	private void runDlgNow(Long userId) {
		// Store the parameter and populate the dialog.
		m_userId = userId;
		loadPart1Async();
	}


	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the profile entry dialog and perform some operation on it.    */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the profile entry dialog
	 * asynchronously after it loads. 
	 */
	public interface ProfileEntryDlgClient {
		void onSuccess(ProfileEntryDlg peDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the ProfileEntryDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final ProfileEntryDlgClient peDlgClient,
			
			// initAndShow parameters,
			final ProfileEntryDlg peDlg,
			final Long userId) {
		GWT.runAsync(ProfileEntryDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_ProfileEntryDlg());
				if (null != peDlgClient) {
					peDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != peDlgClient) {
					// Yes!  Create it and return it via the callback.
					ProfileEntryDlg peDlg = new ProfileEntryDlg();
					peDlgClient.onSuccess(peDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(peDlg, userId);
				}
			}
		});
	}
	
	/**
	 * Loads the ProfileEntryDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param peDlgClient
	 */
	public static void createAsync(ProfileEntryDlgClient peDlgClient) {
		doAsyncOperation(peDlgClient, null, null);
	}
	
	/**
	 * Initializes and shows the profile entry dialog.
	 * 
	 * @param peDlg
	 * @param userId
	 */
	public static void initAndShow(ProfileEntryDlg peDlg, Long userId) {
		doAsyncOperation(null, peDlg, userId);
	}
}

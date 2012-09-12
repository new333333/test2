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
package org.kablink.teaming.gwt.client.mainmenu;

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMainMenuImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.GetWhoHasAccessCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.rpc.shared.WhoHasAccessInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.WhoHasAccessInfoRpcResponseData.AccessInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlexTable;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;
import org.kablink.teaming.gwt.client.widgets.VibeVerticalPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implements Vibe's Who Has Access dialog.
 *  
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class WhoHasAccessDlg extends DlgBox {
	private GwtTeamingMainMenuImageBundle	m_images;			// Access to Vibe's images.
	private GwtTeamingMessages				m_messages;			// Access to Vibe's messages.
	private EntityId						m_entityId;			// EntityId of the entity whose access is being viewed.
	private FlexTable 						m_ft;				// Table that holds the dialog's content.
	private WhoHasAccessInfoRpcResponseData	m_whoHasAccessInfo;	// The 'Who Has Access' information to populate the table with.
	
	private final static int    SCROLL_WHEN	= 5;	// Count of items in one of the ScrollPanel's when scroll bars are enabled.

	/*
	 * Inner class that wraps items displayed in the dialog's content.
	 */
	private class DlgLabel extends InlineLabel {
		/**
		 * Constructor method.
		 * 
		 * @param label
		 * @param style
		 * @param title
		 */
		public DlgLabel(String label, String style, String title) {
			super(label);
			addStyleName("vibe-whoHasAccessDlg-label");
			if (GwtClientHelper.hasString(style)) {
				addStyleName(style);
			}
			if (GwtClientHelper.hasString(title)) {
				setTitle(title);
			}
		}
		
		/**
		 * Constructor method.
		 * 
		 * @param label
		 */
		public DlgLabel(String label) {
			// Always use the initial form of the method.
			this(label, null, null);
		}
		
		/**
		 * Constructor method.
		 * 
		 * @param label
		 * @param style
		 */
		public DlgLabel(String label, String style) {
			// Always use the initial form of the method.
			this(label, style, null);
		}
	}

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private WhoHasAccessDlg() {
		// Initialize the superclass...
		super(false, true, DlgButtonMode.Close);

		// ...initialize everything else...
		m_images   = GwtTeaming.getMainMenuImageBundle();
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.mainMenuWhoHasAccessDlgHeader(),
			getSimpleSuccessfulHandler(),	// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),		// The dialog's EditCanceledHandler.
			null);							// Create callback data.  Unused. 
	}

	/*
	 * Constructs and returns an Image with a spinner in it.
	 */
	private Image buildSpinnerImage() {
		return GwtClientHelper.buildImage(m_images.spinner());
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
		m_ft = new VibeFlexTable();
		m_ft.addStyleName("vibe-whoHasAccessDlg-content");
		return m_ft;
	}

	/*
	 * Clears the contents of the dialog and displays a message that
	 * we're reading the access information for the entity.
	 */
	private void displayReading() {
		m_ft.removeAllRows();
		FlowPanel fp = new FlowPanel();
		fp.addStyleName("vibe-whoHasAccessDlg-readingPanel");
		fp.add(buildSpinnerImage());
		DlgLabel l = new DlgLabel(m_messages.mainMenuWhoHasAccessDlgReading());
		l.addStyleName("vibe-whoHasAccessDlg-readingLabel");
		fp.add(l);
		m_ft.setWidget(0, 0, fp);
	}

	/**
	 * Returns the edited List<FavoriteInfo>.
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
	 * Asynchronously populates the contents of the dialog.
	 */
	private void populateDlgAsync() {
		ScheduledCommand doPopulate = new ScheduledCommand() {
			@Override
			public void execute() {
				populateDlgNow();
			}
		};
		Scheduler.get().scheduleDeferred(doPopulate);
	}
	
	/*
	 * Synchronously populates the contents of the dialog.
	 */
	private void populateDlgNow() {
		GetWhoHasAccessCmd gwhaCmd = new GetWhoHasAccessCmd(m_entityId);
		GwtClientHelper.executeCommand(gwhaCmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetWhoHasAccess());
				
				hide();
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Extract the who has access information from the
				// response data and use it to populate the dialog.
				m_whoHasAccessInfo = ((WhoHasAccessInfoRpcResponseData) response.getResponseData());
				populateFromWhoHasAccessInfoAsync();
			}
		});
	}

	/*
	 * Asynchronously populates the dialog from a
	 * WhoHasAccessInfoRpcResponseData object.
	 */
	private void populateFromWhoHasAccessInfoAsync() {
		ScheduledCommand doPopulate = new ScheduledCommand() {
			@Override
			public void execute() {
				populateFromWhoHasAccessInfoNow();
			}
		};
		Scheduler.get().scheduleDeferred(doPopulate);
	}
	
	/*
	 * Synchronously populates the dialog from a
	 * WhoHasAccessInfoRpcResponseData object.
	 */
	private void populateFromWhoHasAccessInfoNow() {
		// Clear the current content of the dialog...
		m_ft.removeAllRows();

		// ...extract the lists from the who has access info...
		List<AccessInfo>	userList  = m_whoHasAccessInfo.getUsers();  int users  = userList.size(); 
		List<AccessInfo>	groupList = m_whoHasAccessInfo.getGroups(); int groups = groupList.size();
		boolean				scroll    = ((users >= SCROLL_WHEN) || (groups >= SCROLL_WHEN));

		// ...create the user list...
		ScrollPanel usersPanel = new ScrollPanel();
		usersPanel.addStyleName("vibe-whoHasAccessDlg-scrollPanel vibe-whoHasAccessDlg-scrollPanelUsers");
		if (scroll) {
			usersPanel.addStyleName("vibe-whoHasAccessDlg-scrollPanelLimit");
		}
		populateScrollPanel(usersPanel, userList);
		
		// ...create the group list...
		ScrollPanel groupsPanel = new ScrollPanel();
		groupsPanel.addStyleName("vibe-whoHasAccessDlg-scrollPanel vibe-whoHasAccessDlg-scrollPanelGroups");
		if (scroll) {
			groupsPanel.addStyleName("vibe-whoHasAccessDlg-scrollPanelLimit");
		}
		populateScrollPanel(groupsPanel, groupList);
		
		// ...and tie it all together.
		populateTableColumn(0, m_messages.mainMenuWhoHasAccessDlgUsersWithAccess(),  usersPanel );
		populateTableColumn(1, m_messages.mainMenuWhoHasAccessDlgGroupsWithAccess(), groupsPanel);
		
		// Show the dialog (perhaps again) so that it can be positioned
		// correctly based on its new content.
		show(true);
	}

	/*
	 * Populates a ScrollPanel with the information from a
	 * List<AccessInfo>.
	 */
	private void populateScrollPanel(ScrollPanel scrollPanel, List<AccessInfo> accessList) {
		if ((null == accessList) || accessList.isEmpty()) {
			scrollPanel.add(new DlgLabel(m_messages.mainMenuWhoHasAccessDlgNone()));
		}
		
		else {
			VerticalPanel vp = new VibeVerticalPanel(null, null);
			scrollPanel.add(vp);
			for (AccessInfo ai:  accessList) {
				vp.add(new DlgLabel(ai.getName(), null, ai.getHover()));	// null -> No additional styles.
			}
		}
	}

	/*
	 * Populates a column in the who has access FlexTable
	 */
	private void populateTableColumn(int column, String header, ScrollPanel sp) {
		m_ft.setWidget(0, column, new DlgLabel(header, "vibe-whoHasAccessDlg-scrollPanelHeader"));
		m_ft.setWidget(1, column, sp);
		m_ft.getCellFormatter().addStyleName(1, column, "vibe-whoHasAccessDlg-scrollPanelCell");
	}
	
	/*
	 * Asynchronously runs the given instance of the who has access
	 * dialog.
	 */
	private static void runDlgAsync(final WhoHasAccessDlg whaDlg, final EntityId entityId) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				whaDlg.runDlgNow(entityId);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Synchronously runs the given instance of the who has access
	 * dialog.
	 */
	private void runDlgNow(EntityId entityId) {
		// Store the parameter...
		m_entityId = entityId;

		// ...and start populating the dialog and show it.
		displayReading();
		populateDlgAsync();
		show(true);
	}

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the who has access dialog and perform some operation on it.   */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	
	/**
	 * Callback interface to interact with the who has access dialog
	 * asynchronously after it loads. 
	 */
	public interface WhoHasAccessDlgClient {
		void onSuccess(WhoHasAccessDlg whaDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the WhoHasAccessDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final WhoHasAccessDlgClient whaDlgClient,
			
			// initAndShow parameters,
			final WhoHasAccessDlg	whaDlg,
			final EntityId			entityId) {
		GWT.runAsync(WhoHasAccessDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_WhoHasAccessDlg());
				if (null != whaDlgClient) {
					whaDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != whaDlgClient) {
					// Yes!  Create it and return it via the callback.
					WhoHasAccessDlg whaDlg = new WhoHasAccessDlg();
					whaDlgClient.onSuccess(whaDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(whaDlg, entityId);
				}
			}
		});
	}
	
	/**
	 * Loads the WhoHasAccessDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param whaDlgClient
	 */
	public static void createAsync(WhoHasAccessDlgClient whaDlgClient) {
		doAsyncOperation(whaDlgClient, null, null);
	}
	
	/**
	 * Initializes and shows the who has access to an entity dialog.
	 * 
	 * @param whaDlg
	 * @param entityId
	 */
	public static void initAndShow(WhoHasAccessDlg whaDlg, EntityId entityId) {
		doAsyncOperation(null, whaDlg, entityId);
	}
}

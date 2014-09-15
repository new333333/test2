/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Implements a Copy Filters dialog.
 *  
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class CopyFiltersDlg extends DlgBox implements EditSuccessfulHandler {
	public final static boolean	SHOW_COPY_FILTERS	= false;	//! DRF (20140912):  Leave false on checkin until works!
	
	private GwtTeamingImageBundle	m_images;			// Access to Vibe's images.
	private GwtTeamingMessages		m_messages;			// Access to Vibe's messages.
	private BinderInfo				m_folderInfo;		// BinderInfo of the folder filters are to be copied to.
	private ScrollPanel				m_filtersScroller;	// The ScrollPanel that contains the filters from the source folder.
	private VibeFlowPanel			m_contentPanel;		// The panel containing the content of the dialog.
	private VibeVerticalPanel		m_filtersPanel;		// The panel containing the filters themselves.
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private CopyFiltersDlg() {
		// Initialize the superclass...
		super(false, true, DlgButtonMode.OkCancel);

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
		m_images   = GwtTeaming.getImageBundle();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.copyFiltersDlg_Caption(),	// The dialog's caption.
			this,									// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),				// The dialog's EditCanceledHandler.
			null);									// Create callback data.  Unused. 
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
		// Create the main panel for the dialog's content...
		VibeFlowPanel mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName("vibe-copyFiltersDlg-mainPanel");

		// ...create the constituent parts and add them to the main
		// ...panel...
		createContentPanel(mainPanel);

		// ...and return the main panel
		return mainPanel;
	}

	/*
	 * Create the controls needed in the content.
	 */
	private void createContentPanel(Panel mainPanel) {
		// Create the panel to be used for the dialog content (below
		// the header) and add it to the main panel.
		m_contentPanel = new VibeFlowPanel();
		m_contentPanel.addStyleName("vibe-copyFiltersDlg-contentPanel");
		mainPanel.add(m_contentPanel);
		
//!		...this needs to be implemented...
		m_contentPanel.add(new InlineLabel("...this needs to be implemented..."));
		
		// Add a ScrollPanel for the filters.
		m_filtersScroller = new ScrollPanel();
		m_filtersScroller.addStyleName("vibe-copyFiltersDlg-scrollPanel");
		m_contentPanel.add(m_filtersScroller);

		// Add a vertical panel for the ScrollPanel's content.
		m_filtersPanel = new VibeVerticalPanel(null, null);
		m_filtersPanel.addStyleName("vibe-copyFiltersDlg-filtersPanel");
		m_filtersScroller.add(m_filtersPanel);
		
		// Hide the panel we show the filters in until we have some.
		m_filtersScroller.setVisible(false);
	}
	
	/**
	 * This method gets called when user user presses the OK push
	 * button.
	 * 
	 * Implements the EditSuccessfulHandler.editSuccessful() interface
	 * method.
	 * 
	 * @param callbackData
	 * 
	 * @return
	 */
	@Override
	public boolean editSuccessful(Object callbackData) {
//!		...this needs to be implemented...
		return true;
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
//!		...this needs to be implemented...
		
		// Turn off any scrolling currently in force...
		m_filtersPanel.addStyleName(      "vibe-copyFiltersDlg-scrollLimit");	// Limit on the VerticalPanel...
		m_filtersScroller.removeStyleName("vibe-copyFiltersDlg-scrollLimit");	// ...not the ScrollPanel.
		
		// ...and show the dialog centered on the screen.
		center();
	}

	/*
	 * Asynchronously runs the given instance of the copy filters
	 * dialog.
	 */
	private static void runDlgAsync(final CopyFiltersDlg cfDlg, final BinderInfo folderInfo) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				cfDlg.runDlgNow(folderInfo);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the copy filters
	 * dialog.
	 */
	private void runDlgNow(BinderInfo folderInfo) {
		// Store the parameters...
		m_folderInfo = folderInfo;

		// ...and populate the dialog.
		populateDlgAsync();
	}

	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the copy filters dialog and perform some operation on it.     */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the copy filters dialog
	 * asynchronously after it loads. 
	 */
	public interface CopyFiltersDlgClient {
		void onSuccess(CopyFiltersDlg cfDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the CopyFiltersDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final CopyFiltersDlgClient cfDlgClient,
			
			// initAndShow parameters,
			final CopyFiltersDlg	cfDlg,
			final BinderInfo		folderInfo) {
		GWT.runAsync(CopyFiltersDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_CopyFiltersDlg());
				if (null != cfDlgClient) {
					cfDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != cfDlgClient) {
					// Yes!  Create it and return it via the callback.
					CopyFiltersDlg cfDlg = new CopyFiltersDlg();
					cfDlgClient.onSuccess(cfDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(cfDlg, folderInfo);
				}
			}
		});
	}
	
	/**
	 * Loads the CopyFiltersDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param cfDlgClient
	 */
	public static void createAsync(CopyFiltersDlgClient cfDlgClient) {
		doAsyncOperation(cfDlgClient, null, null);
	}
	
	/**
	 * Initializes and shows the copy filters dialog.
	 * 
	 * @param cfDlg
	 * @param folderInfo
	 */
	public static void initAndShow(CopyFiltersDlg cfDlg, BinderInfo folderInfo) {
		doAsyncOperation(null, cfDlg, folderInfo);
	}
}

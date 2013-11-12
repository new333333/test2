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
package org.kablink.teaming.gwt.client.datatable;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.MobileDeviceRemovedCallback;
import org.kablink.teaming.gwt.client.util.MobileDevicesInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.UIObject;

/**
 * Implements Vibe's 'Manage User Devices' dialog.
 *  
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class ManageUserDevicesDlg extends DlgBox {
	private Label						m_mobileDevicesCountLabel;	// The mobile devices count Label that's stored in the dialog's caption.
	private MobileDeviceRemovedCallback	m_removedCallback;			// Interface used to tell who's running the dialog that a device was removed .
	private MobileDevicesInfo			m_mdInfo;					// The MobileDevicesInfo this ManageUserDevicesDlg is running against.
	private UIObject					m_showRelativeTo;			// The UIObject to show the dialog relative to.  null -> Center the dialog.
	private VibeFlowPanel				m_fp;						// The panel that holds the dialog's contents.

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ManageUserDevicesDlg() {
		// Initialize the superclass...
		super(false, true, DlgButtonMode.Close);	// false -> Not auto hide.  true -> Modal.

		// ...and create the dialog's content.
		addStyleName("vibe-manageUserDevicesDlg");
		createAllDlgContent(
			"",										// The dialog's caption will be set each time it is run.
			DlgBox.getSimpleSuccessfulHandler(),	// The dialog's editSuccessful() handler.
			DlgBox.getSimpleCanceledHandler(),		// The dialog's editCanceled()   handler.
			null);									// null -> No callback data is required by createContent(). 
	}

	/**
	 * Creates all the controls that make up the dialog.
	 * 
	 * Implements the DlgBox.createContent() abstract method.
	 * 
	 * @param callbackData
	 * 
	 * @return
	 */
	@Override
	public Panel createContent(Object callbackData) {
		// Create the main panel that holds the dialog's content...
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-manageUserDevicesDlg-panel");

//!		...this needs to be implemented...
		Label il = new Label("...this needs to be implemented...");
		il.setWidth("400px");
		m_fp.add(il);
		
		// ...and return the dialog's main content panel.
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
		// Unused.
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
		
		// ...and show the dialog.
		if (null == m_showRelativeTo)
		     center();
		else showRelativeTo(m_showRelativeTo);
	}
	
	/*
	 * Asynchronously runs the given instance of the manage user
	 * devices dialog.
	 */
	private static void runDlgAsync(final ManageUserDevicesDlg mudDlg, final MobileDevicesInfo ci, final UIObject showRelativeTo, final MobileDeviceRemovedCallback removedCallback) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				mudDlg.runDlgNow(ci, showRelativeTo, removedCallback);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the manage user devices
	 * dialog.
	 */
	private void runDlgNow(MobileDevicesInfo mdInfo, UIObject showRelativeTo, MobileDeviceRemovedCallback removedCallback) {
		// Set the style this dialog needs on the caption label.
		Label hcl = getHeaderCaptionLabel();
		hcl.setStyleName("vibe-manageUserDevicesDlg-headerCaption");
		
		// Set the dialog's caption and caption image...
		setCaption(             mdInfo.getClientItemTitle()  );
		setCaptionImage((Image) mdInfo.getClientItemImage()  );
		setCaptionDevicesCount(mdInfo.getMobileDevicesCount());
		
		// ...store the parameters...
		m_mdInfo          = mdInfo;
		m_showRelativeTo  = showRelativeTo;
		m_removedCallback = removedCallback;
		
		// ...and start populating the dialog.
		populateDlgAsync();
	}

	/*
	 * Sets a devices count label into the header.
	 */
	private void setCaptionDevicesCount(int cCount) {
		// If we haven't added the Label to the dialog's header yet...
		if (null == m_mobileDevicesCountLabel) {
			// ...create it and add it now...
			m_mobileDevicesCountLabel = new Label();
			String bgStyle;
			if (GwtClientHelper.jsIsIE())
			     bgStyle = "teamingDlgBoxHeaderBG_IE";
			else bgStyle = "teamingDlgBoxHeaderBG_NonIE";
			m_mobileDevicesCountLabel.addStyleName("vibe-manageUserDevicesDlg-captionCount " + bgStyle);
			getHeaderPanel().add(m_mobileDevicesCountLabel);
		}
		
		// ...and store the appropriate text into it.
		m_mobileDevicesCountLabel.setText(GwtTeaming.getMessages().manageUserDevicesDlgDevices(cCount));
	}
	

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the manage user devices dialog and perform some operation on  */
	/* it.                                                           */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the manage user devices
	 * dialog asynchronously after it loads. 
	 */
	public interface ManageUserDevicesDlgClient {
		void onSuccess(ManageUserDevicesDlg mudDlg);
		void onUnavailable();
	}
	
	/*
	 * Asynchronously loads the ManageUserDevicesDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// createAsync() parameters.
			final ManageUserDevicesDlgClient mudDlgClient,
			
			// initAndShow() parameters.
			final ManageUserDevicesDlg			mudDlg,
			final MobileDevicesInfo				mdInfo,
			final UIObject						showRelativeTo,
			final MobileDeviceRemovedCallback	removedCallback) {
		GWT.runAsync(ManageUserDevicesDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				GwtClientHelper.deferredAlert(GwtTeaming.getMessages().codeSplitFailure_ManageUserDevicesDlg());
				if (null != mudDlgClient) {
					mudDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != mudDlgClient) {
					// Yes!  Create it and return it via the callback.
					ManageUserDevicesDlg mudDlg = new ManageUserDevicesDlg();
					mudDlgClient.onSuccess(mudDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(mudDlg, mdInfo, showRelativeTo, removedCallback);
				}
			}
		});
	}
	
	/**
	 * Loads the ManageUserDevicesDlg split point and returns an
	 * instance of it via the callback.
	 * 
	 * @param mudDlgClient
	 */
	public static void createAsync(ManageUserDevicesDlgClient mudDlgClient) {
		doAsyncOperation(mudDlgClient, null, null, null, null);
	}
	
	/**
	 * Initializes and shows the manage user devices dialog via its
	 * split point.
	 * 
	 * @param mudDlg
	 * @param mdInfo
	 * @param showRelativeTo
	 * @param removedCallback
	 */
	public static void initAndShow(ManageUserDevicesDlg mudDlg, MobileDevicesInfo mdInfo, UIObject showRelativeTo, MobileDeviceRemovedCallback removedCallback) {
		doAsyncOperation(null, mudDlg, mdInfo, showRelativeTo, removedCallback);
	}
	
	public static void initAndShow(ManageUserDevicesDlg mudDlg, MobileDevicesInfo mdInfo, UIObject showRelativeTo) {
		// Always use the initial form of the method.
		initAndShow(mudDlg, mdInfo, showRelativeTo, null);
	}
}

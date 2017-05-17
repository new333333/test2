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

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingCloudFoldersImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.CloudFolderAuthentication;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Implements Vibe's 'Cloud Folder Authentication' dialog.
 *  
 * @author drfoster@novell.com
 */
//@SuppressWarnings("unused")
public class CloudFolderAuthenticationDlg extends DlgBox {
	private CloudFolderAuthentication			m_cfAuthentication;			// Information about the Cloud Folder authentication.
	private CloudFolderAuthenticationCallback	m_authenticationCallback;	// Callback to tell the callee when the dialog is ready.
	private GwtTeamingCloudFoldersImageBundle	m_cfImages;					// Access to the Cloud Folder images.
	private GwtTeamingMessages					m_messages;					// Access to Vibe's messages.
	private List<HandlerRegistration>			m_registeredEventHandlers;	// Event handlers that are currently registered.
	private VibeFlowPanel						m_fp;						// The panel that holds the dialog's contents.

	// Number of milliseconds AFTER the dialog is ready before issuing
	// the follow up request for rows.
	private final static int CLOUD_FOLDER_AUTHENTICATION_DELAY	= 1000;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
	};

	/**
	 * Interface used by the dialog to inform the caller about what's
	 * going on. 
	 */
	public interface CloudFolderAuthenticationCallback {
		public void dialogReady();
	}
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private CloudFolderAuthenticationDlg() {
		// Initialize the superclass...
		super(false, true, DlgButtonMode.Ok, false);
		
		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
		m_cfImages = GwtTeaming.getCloudFoldersImageBundle();
	
		// ...and create the dialog's content.
		addStyleName("vibe-cloudFolderAuthenticationDlgBox");
		createAllDlgContent(
			m_messages.cloudFolderAuthenticationDlgHeader(),	//
			DlgBox.getSimpleSuccessfulHandler(),				// The dialog's EditSuccessfulHandler.
			DlgBox.getSimpleCanceledHandler(),					// The dialog's EditCanceledHandler.
			null);												// Create callback data.  Unused. 
	}

	/*
	 * Closes the browser window running the Cloud Folder service's
	 * authentication.
	 */
	private static native void closeCloudServiceAuthentication() /*-{
		// If we're tracking a cloud service authentication window...
		if (null != $wnd.top.ss_cloudFolderAuthenticationPopup) {
			// ...close...
			try {$wnd.top.ss_cloudFolderAuthenticationPopup.close();}
			catch (e) {}
			
			// ...and forget about it.
			$wnd.top.ss_cloudFolderAuthenticationPopup = null;
		}
	}-*/;
	  
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
		// Create a panel to hold the dialog's content...
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-cloudFolderAuthenticationDlg_Panel");
		
		// ...and return the Panel that holds the dialog's contents.
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

	/**
	 * Called to hide the dialog.
	 * 
	 * Overrides the DlgBox.hide() method.
	 */
	@Override
	public void hide() {
		closeCloudServiceAuthentication();
		super.hide();
	}
	
	/**
	 * Called when the data table is attached.
	 * 
	 * Overrides Widget.onAttach()
	 */
	@Override
	public void onAttach() {
		// Let the widget attach and then register our event
		// handlers...
		super.onAttach();
		registerEvents();
		
		// ...and tell the callback that the dialog is ready.
		GwtClientHelper.deferCommand(
			new ScheduledCommand() {
				@Override
				public void execute() {
					m_authenticationCallback.dialogReady();
				}
			},
			CLOUD_FOLDER_AUTHENTICATION_DELAY);
	}
	
	/**
	 * Called when the data table is detached.
	 * 
	 * Overrides Widget.onDetach()
	 */
	@Override
	public void onDetach() {
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
	}
	
	/*
	 * Opens a new browser window for the user to authenticate to a
	 * Cloud Folder service.
	 */
	private static native void openCloudServiceAuthentication(String url, String name, int width, int height) /*-{
		$wnd.top.ss_cloudFolderAuthenticationPopup = $wnd.open(
			url,
			name,
			'directories=no,location=no,menubar=yes,resizable=yes,scrollbars=yes,status=no,toolbar=no,width=' + width + ',height=' + height);
	}-*/;
	  
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
		// Clear the current contents of the dialog...
		m_fp.clear();
		
		// ...add labels and an image telling the user what's going
		// ...on...
		Label l = new Label(m_messages.cloudFolderAuthenticationDlgMessageAboveLogo());
		l.addStyleName("vibe-cloudFolderAuthenticationDlg_Message vibe-cloudFolderAuthenticationDlg_MessageAbove");
		m_fp.add(l);

		ImageResource cfImageRes;
		switch (m_cfAuthentication.getCloudFolderType()) {
		default:           cfImageRes = m_cfImages.genericService(); break;
		case BOXDOTNET:    cfImageRes = m_cfImages.boxDotNet();      break;
		case DROPBOX:      cfImageRes = m_cfImages.dropBox();        break;
		case GOOGLEDRIVE:  cfImageRes = m_cfImages.googleDrive();    break;
		case SKYDRIVE:     cfImageRes = m_cfImages.skyDrive();       break;
		}
		Image cfImage = GwtClientHelper.buildImage(cfImageRes);
		cfImage.addStyleName("vibe-cloudFolderAuthenticationDlg_Logo");
		m_fp.add(cfImage);
		
		l = new Label(m_messages.cloudFolderAuthenticationDlgMessageBelowLogo());
		l.addStyleName("vibe-cloudFolderAuthenticationDlg_Message vibe-cloudFolderAuthenticationDlg_MessageBelow");
		m_fp.add(l);

		// ...initiate the authentication...
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				openCloudServiceAuthentication(
					m_cfAuthentication.getAuthenticationUrl(),
					"ss_cloud_folder_authentication",
					1024,
					768);
			}
		});

		// ...and show the dialog.
		center();
	}
	
	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents() {
		// If we having allocated a list to track events we've
		// registered yet...
		if (null == m_registeredEventHandlers) {
			// ...allocate one now.
			m_registeredEventHandlers = new ArrayList<HandlerRegistration>();
		}

		// If the list of registered events is empty...
		if (m_registeredEventHandlers.isEmpty()) {
			// ...register the events.
			EventHelper.registerEventHandlers(
				GwtTeaming.getEventBus(),
				m_registeredEvents,
				this,
				m_registeredEventHandlers);
		}
	}

	/*
	 * Asynchronously runs the given instance of the Cloud Folder
	 * authentication dialog.
	 */
	private static void runDlgAsync(final CloudFolderAuthenticationDlg cfaDlg, final CloudFolderAuthentication cfAuthentication, final CloudFolderAuthenticationCallback authenticationCallback) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				cfaDlg.runDlgNow(cfAuthentication, authenticationCallback);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the Cloud Folder
	 * authentication dialog.
	 */
	private void runDlgNow(CloudFolderAuthentication cfAuthentication, CloudFolderAuthenticationCallback authenticationCallback) {
		// Store the parameters...
		m_cfAuthentication       = cfAuthentication;
		m_authenticationCallback = authenticationCallback;
		
		// ...and start populating the dialog.
		populateDlgAsync();
	}

	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents() {
		// If we have a non-empty list of registered events...
		if ((null != m_registeredEventHandlers) && (!(m_registeredEventHandlers.isEmpty()))) {
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers(m_registeredEventHandlers);
		}
	}
	
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the Cloud Folder authentication dialog and perform some       */
	/* operation on it.                                              */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the Cloud Folder
	 * authentication dialog asynchronously after it loads. 
	 */
	public interface CloudFolderAuthenticationDlgClient {
		void onSuccess(CloudFolderAuthenticationDlg cfaDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the CloudFolderAuthenticationDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final CloudFolderAuthenticationDlgClient cfaDlgClient,
			
			// initAndShow parameters,
			final CloudFolderAuthenticationDlg		cfaDlg,
			final CloudFolderAuthentication			cfAuthentication,
			final CloudFolderAuthenticationCallback	authenticationCallback) {
		GWT.runAsync(CloudFolderAuthenticationDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_CloudFolderAuthenticationDlg());
				if (null != cfaDlgClient) {
					cfaDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != cfaDlgClient) {
					// Yes!  Create it and return it via the callback.
					CloudFolderAuthenticationDlg cfaDlg = new CloudFolderAuthenticationDlg();
					cfaDlgClient.onSuccess(cfaDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(cfaDlg, cfAuthentication, authenticationCallback);
				}
			}
		});
	}
	
	/**
	 * Loads the CloudFolderAuthenticationDlg split point and returns
	 * an instance of it via the callback.
	 * 
	 * @param cfaDlgClient
	 */
	public static void createAsync(CloudFolderAuthenticationDlgClient cfaDlgClient) {
		doAsyncOperation(cfaDlgClient, null, null, null);
	}
	
	/**
	 * Initializes and shows the Cloud Folder authentication dialog.
	 * 
	 * @param cfaDlg
	 * @param cfAuthentication
	 * @param authenticationCallback
	 */
	public static void initAndShow(CloudFolderAuthenticationDlg cfaDlg, CloudFolderAuthentication cfAuthentication, CloudFolderAuthenticationCallback authenticationCallback) {
		doAsyncOperation(null, cfaDlg, cfAuthentication, authenticationCallback);
	}
}

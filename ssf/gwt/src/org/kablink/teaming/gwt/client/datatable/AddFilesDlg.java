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
package org.kablink.teaming.gwt.client.datatable;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;
import org.kablink.teaming.gwt.client.widgets.VibeVerticalPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.NamedFrame;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.UIObject;


/**
 * Implements Vibe's 'add files' dialog.
 *  
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class AddFilesDlg extends DlgBox implements EditSuccessfulHandler, EditCanceledHandler {
	private BinderInfo						m_folderInfo;			// The folder the add files is running against.
	private GwtTeamingDataTableImageBundle	m_images;				// Access to Vibe's images.
	private GwtTeamingMessages				m_messages;				// Access to Vibe's messages.
	private UIObject						m_showRelativeWidget;	// The UIObject to show the dialog relative to.
	private VibeFlowPanel					m_fp;					// The panel that holds the dialog's contents.

	/*
	 * Inner class that wraps items displayed in the dialog's content.
	 */
	private class DlgLabel extends InlineLabel {
		/**
		 * Constructor method.
		 * 
		 * @param label
		 * @param title
		 */
		public DlgLabel(String label, String title) {
			super(label);
			if (GwtClientHelper.hasString(title)) {
				setTitle(title);
			}
			addStyleName("vibe-addFilesDlg_Label");
		}
		
		/**
		 * Constructor method.
		 * 
		 * @param label
		 */
		public DlgLabel(String label) {
			// Always use the initial form of the method.
			this(label, null);
		}
	}

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private AddFilesDlg() {
		// Initialize the superclass...
		super(false, true, DlgButtonMode.Close);

		// ...initialize everything else...
		m_images   = GwtTeaming.getDataTableImageBundle();
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		addStyleName("vibe-addFilesDlgBox");
		createAllDlgContent(
			m_messages.addFilesDlgHeader(),
			this,	// The dialog's EditSuccessfulHandler.
			this,	// The dialog's EditCanceledHandler.
			null);	// Create callback data.  Unused. 
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
		// Create a panel to hold the dialog's content...
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-addFilesDlg_Panel");
		
		// ...and return the Panel that holds the dialog's contents.
		return m_fp;
	}

	/**
	 * This method gets called when user user presses the Cancel push
	 * button.
	 * 
	 * Implements the EditCanceledHandler.editCanceled() interface
	 * method.
	 * 
	 * @return
	 */
	public boolean editCanceled() {
		// Simply return true to allow the dialog to close.
		return true;
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
	public boolean editSuccessful(Object callbackData) {
		// Unused.
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
		// Clear the current contents of the dialog...
		m_fp.clear();

		// ...create an IFRAME to run the applet...
		String frameName = ("ss_iframe_folder_dropbox"+ m_folderInfo.getBinderId() + "_ss_forum_");
		NamedFrame iFrame = new NamedFrame(frameName);
		iFrame.addStyleName("vibe-addFilesDlg_IFrame");
		iFrame.removeStyleName("gwt-Frame");
		Element ife = iFrame.getElement();
		ife.setId(frameName);
		ife.setAttribute("frameborder", "0"   );
		ife.setAttribute("scrolling",   "auto");
		ife.setAttribute("height",      "80%" );
		ife.setAttribute("width",       "96%" );
		iFrame.setTitle(m_messages.addFilesDlgFrameTitle());
		StringBuffer url = new StringBuffer(GwtClientHelper.getRequestInfo().getBaseVibeUrl());
		url.append("&action=__ajax_request");
		url.append("&operation=add_folder_attachment_options");
		url.append("&binderId=" + m_folderInfo.getBinderId());
		url.append("&namespace=_ss_forum_");
		url.append("&library=false");
		iFrame.setUrl(url.toString());
		m_fp.add(iFrame);

		// ...create a panel to hold the help button...
		VibeFlowPanel fp = new VibeFlowPanel();
		fp.addStyleName("vibe-addFilesDlg-HelpPanel");
		fp.getElement().setAttribute("align", "right");
		DlgLabel fpLabel = new DlgLabel(m_messages.addFilesDlgHavingTrouble());
		fpLabel.addStyleName("ss_fineprint");
		fp.add(fpLabel);
		m_fp.add(fp);

		// ...and show the dialog.
		showRelativeTo(m_showRelativeWidget);
	}
	
	/*
	 * Asynchronously runs the given instance of the add files dialog.
	 */
	private static void runDlgAsync(final AddFilesDlg afDlg, final BinderInfo fi, final UIObject showRelativeWidget) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				afDlg.runDlgNow(fi, showRelativeWidget);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Synchronously runs the given instance of the add files dialog.
	 */
	private void runDlgNow(BinderInfo fi, UIObject showRelativeWidget) {
		// Store the parameter...
		m_folderInfo         = fi;
		m_showRelativeWidget = showRelativeWidget;
		
		// ...and start populating the dialog.
		populateDlgAsync();
	}

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the add files dialog and perform some operation on it.        */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	
	/**
	 * Callback interface to interact with the add files dialog
	 * asynchronously after it loads. 
	 */
	public interface AddFilesDlgClient {
		void onSuccess(AddFilesDlg afDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the AddFilesDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final AddFilesDlgClient afDlgClient,
			
			// initAndShow parameters,
			final AddFilesDlg afDlg,
			final BinderInfo fi,
			final UIObject showRelativeWidget) {
		GWT.runAsync(AddFilesDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_AddFilesDlg());
				if (null != afDlgClient) {
					afDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != afDlgClient) {
					// Yes!  Create it and return it via the callback.
					AddFilesDlg afDlg = new AddFilesDlg();
					afDlgClient.onSuccess(afDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(afDlg, fi, showRelativeWidget);
				}
			}
		});
	}
	
	/**
	 * Loads the AddFilesDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param afDlgClient
	 */
	public static void createAsync(AddFilesDlgClient afDlgClient) {
		doAsyncOperation(afDlgClient, null, null, null);
	}
	
	/**
	 * Initializes and shows the add files dialog.
	 * 
	 * @param afDlg
	 * @param fi
	 */
	public static void initAndShow(AddFilesDlg afDlg, BinderInfo fi, UIObject showRelativeWidget) {
		doAsyncOperation(null, afDlg, fi, showRelativeWidget);
	}
}

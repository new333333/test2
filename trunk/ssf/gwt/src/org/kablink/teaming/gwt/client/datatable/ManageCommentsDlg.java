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
package org.kablink.teaming.gwt.client.datatable;

import org.kablink.teaming.gwt.client.datatable.ManageCommentsComposite.ManageCommentsCompositeClient;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.CommentAddedCallback;
import org.kablink.teaming.gwt.client.util.CommentsInfo;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.UIObject;

/**
 * Implements Vibe's 'Manage Comments' dialog.
 *  
 * @author drfoster@novell.com
 */
public class ManageCommentsDlg extends DlgBox implements ManageCommentsCallback {
	private CommentAddedCallback	m_addedCallback;			// Interface used to tell who is running the dialog that a a comment was added.
	private CommentsInfo			m_commentsInfo;				// The CommentsInfo the ManageCommentsDlg is running against.
	private Label					m_commentsCountLabel;		//
	private ManageCommentsComposite	m_manageCommentsComposite;	// The composite containing the main content of the dialog. 
	private UIObject				m_showRelativeTo;			// The UIObject to show the dialog relative to.
	private VibeFlowPanel			m_fp;						// The panel that holds the dialog's contents.

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ManageCommentsDlg(ManageCommentsDlgClient mcDlgClient) {
		// Initialize the superclass...
		super(
			false,					// false -> Not auto hide.
			true,					// true  -> Modal.
			DlgButtonMode.Close,	// Forces the 'X' close button in the upper right corner.
			false);					// false -> Don't show footer.

		// ...and create the dialog's content.
		addStyleName("vibe-manageCommentsDlg");
		createAllDlgContent(
			"",				// The dialog's caption will be set each time it is run.
			DlgBox.getSimpleSuccessfulHandler(),
			DlgBox.getSimpleCanceledHandler(),
			mcDlgClient);	// The callbackData object passed into createContent(). 
	}

	/**
	 * Called when the composite has completed its initializations and
	 * is ready to run.
	 * 
	 * Implements the ManageCommentsCallback.compositeReady() method.
	 */
	@Override
	public void compositeReady() {
		// Simply show the dialog.
		showRelativeTo(m_showRelativeTo);
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
		m_fp.addStyleName("vibe-manageCommentsDlg-panel");

		// ...asynchronously create the manage comments composite...
		final ManageCommentsDlg			mcDlg       = this;
		final ManageCommentsDlgClient	mcDlgClient = ((ManageCommentsDlgClient) callbackData);
		ManageCommentsComposite.createAsync(new ManageCommentsCompositeClient() {
			@Override
			public void onUnavailable() {
				mcDlgClient.onUnavailable();
			}
			
			@Override
			public void onSuccess(ManageCommentsComposite mcc) {
				// Store the composite and add it to the dialog's main
				// content panel.
				m_manageCommentsComposite = mcc;
				m_fp.add(m_manageCommentsComposite);
				mcDlgClient.onSuccess(mcDlg);
			}},
			this,
			"vibe-manageCommentsComposite");
		
		// ...and return the dialog's main content panel.
		return m_fp;
	}
	
	/**
	 * Called when the user presses the escape key in the manage
	 * comments composite.
	 * 
	 * Implements the ManageCommentsCallback.escape() method.
	 */
	@Override
	public void escape() {
		// Simply hide the dialog.
		hide();
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
		// If we've connected with the manage comments composite, ask
		// it for the focus widget.  Otherwise, return null.
		return
			((null == m_manageCommentsComposite) ?
				null                             :
				m_manageCommentsComposite.getFocusWidget());
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
		// Initialize the comments composite with the comments.  It
		// will callback when ready and at that point, we'll show the
		// dialog.
		ManageCommentsComposite.initAsync(
			m_manageCommentsComposite,
			m_commentsInfo,
			new CommentAddedCallback() {
				@Override
				public void commentAdded(Object callbackData) {
					m_addedCallback.commentAdded(callbackData);
					setCaptionCommentsCount(((CommentsInfo) callbackData).getCommentsCount());
				}
			});
	}
	
	/*
	 * Asynchronously runs the given instance of the manage comments
	 * dialog.
	 */
	private static void runDlgAsync(final ManageCommentsDlg mcDlg, final CommentsInfo ci, final UIObject showRelativeTo, final CommentAddedCallback addedCallback) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				mcDlg.runDlgNow(ci, showRelativeTo, addedCallback);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Synchronously runs the given instance of the manage comments
	 * dialog.
	 */
	private void runDlgNow(CommentsInfo commentsInfo, UIObject showRelativeTo, CommentAddedCallback addedCallback) {
		// Set the dialog's caption and caption image...
		setCaption(             commentsInfo.getEntityTitle()    );
		setCaptionImage((Image) commentsInfo.getClientItemImage());
		setCaptionCommentsCount(commentsInfo.getCommentsCount()  );
		
		// ...store the parameters...
		m_commentsInfo   = commentsInfo;
		m_showRelativeTo = showRelativeTo;
		m_addedCallback  = addedCallback;
		
		// ...and start populating the dialog.
		populateDlgAsync();
	}

	/*
	 * Sets a comment count label into the header.
	 */
	private void setCaptionCommentsCount(int cCount) {
		// If we haven't added the Label to the dialog's header yet...
		if (null == m_commentsCountLabel) {
			// ...create it and add it now...
			m_commentsCountLabel = new Label();
			m_commentsCountLabel.addStyleName("vibe-manageCommentsDlg-captionCount");
			getHeaderPanel().add(m_commentsCountLabel);
		}
		
		// ...and store the appropriate text into it.
		m_commentsCountLabel.setText(GwtTeaming.getMessages().manageCommentsDlgComments(cCount));
	}
	

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the manage comment dialog and perform some operation on it.   */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the manage comments dialog
	 * asynchronously after it loads. 
	 */
	public interface ManageCommentsDlgClient {
		void onSuccess(ManageCommentsDlg mcDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the ManageCommentsDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Parameters used to create the dialog.
			final ManageCommentsDlgClient mcDlgClient,
			
			// Parameters used to initialize and show an instance of the dialog.
			final ManageCommentsDlg		mcDlg,
			final CommentsInfo			commentsInfo,
			final UIObject				showRelativeTo,
			final CommentAddedCallback	addedCallback) {
		GWT.runAsync(ManageCommentsDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_ManageCommentsDlg());
				if (null != mcDlgClient) {
					mcDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != mcDlgClient) {
					// Yes!  Create the dialog.  Note that its
					// construction flow will call the appropriate
					// method off the ManageCommentDlgClient object.
					new ManageCommentsDlg(mcDlgClient);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(mcDlg, commentsInfo, showRelativeTo, addedCallback);
				}
			}
		});
	}
	
	/**
	 * Loads the ManageCommentsDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param mcDlgClient
	 */
	public static void createAsync(ManageCommentsDlgClient mcDlgClient) {
		doAsyncOperation(mcDlgClient, null, null, null, null);
	}
	
	/**
	 * Initializes and shows the manage comments dialog.
	 * 
	 * @param mcDlg
	 * @param commentsInfo
	 * @param showRelativeTo
	 * @param addedCallback
	 */
	public static void initAndShow(ManageCommentsDlg mcDlg, CommentsInfo commentsInfo, UIObject showRelativeTo, CommentAddedCallback addedCallback) {
		doAsyncOperation(null, mcDlg, commentsInfo, showRelativeTo, addedCallback);
	}
	
	public static void initAndShow(ManageCommentsDlg mcDlg, CommentsInfo commentsInfo, UIObject showRelativeTo) {
		// Always use the initial form of the method.
		doAsyncOperation(null, mcDlg, commentsInfo, showRelativeTo, null);
	}
}

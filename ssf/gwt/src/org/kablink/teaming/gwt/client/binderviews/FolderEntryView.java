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

import org.kablink.teaming.gwt.client.binderviews.FolderEntryComposite.FolderEntryCompositeClient;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.event.ContributorIdsRequestEvent;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.ViewFolderEntryInfo;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;

/**
 * Folder entry view.
 * 
 * @author drfoster@novell.com
 */
public class FolderEntryView extends ViewBase {
	private FolderEntryComposite	m_composite;	//
	private VibeFlowPanel			m_fp;			//
	
	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private FolderEntryView(ViewFolderEntryInfo vfei, ViewReady viewReady) {
		// Initialize the super class...
		super(viewReady);
		
		// ...create a panel to hold the view's content...
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-feRootView-panel");
		initWidget(m_fp);

		// ...and load it.
		loadPart1Async(vfei, viewReady);
	}
	
	/*
	 * Asynchronously loads the next part of the view.
	 */
	private void loadPart1Async(final ViewFolderEntryInfo vfei, final ViewReady viewReady) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now(vfei, viewReady);
			}
		});
	}
	
	/*
	 * Synchronously loads the next part of the view.
	 */
	private void loadPart1Now(final ViewFolderEntryInfo vfei, final ViewReady viewReady) {
		FolderEntryComposite.createAsync(
			new FolderEntryCompositeClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in asynchronous
					// provider.
				}
	
				@Override
				public void onSuccess(FolderEntryComposite fec) {
					m_composite = fec;
					m_fp.add(m_composite);
				}
			},
			null,	// null -> Not contained in a dialog.
			vfei,
			viewReady);
	}
	
	/**
	 * Not used for folder entry views directly.  The
	 * FolderEntryComposite will list for this and handle it for both
	 * the view and dialog versions.
	 * 
	 * Implements the ViewBase.onContributorIdsRequest() method.
	 */
	@Override
	public void onContributorIdsRequest(ContributorIdsRequestEvent event) {
		// Nothing to do.
	}

	/**
	 * Synchronously sets the size of the composite based on its
	 * position in the view.
	 * 
	 * Overrides the ViewBase.onResize() method.
	 */
	@Override
	public void onResize() {
		// Pass the resize on to the super class...
		super.onResize();
		
		// ...and if we have a folder entry composite...
		if (null != m_composite) {
			// ...tell it to resize.
			m_composite.onResize();
		}
	}

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the folder entry view and perform some operation on it.       */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Loads the FolderEntryView split point and returns an
	 * instance of it via the callback.
	 * 
	 * @param vfei
	 * @param viewReady
	 * @param vClient
	 */
	public static void createAsync(final ViewFolderEntryInfo vfei, final ViewReady viewReady, final ViewClient vClient) {
		GWT.runAsync(FolderEntryView.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				FolderEntryView dfView = new FolderEntryView(vfei, viewReady);
				vClient.onSuccess(dfView);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(m_messages.codeSplitFailure_FolderEntryView());
				vClient.onUnavailable();
			}
		});
	}
}

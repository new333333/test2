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

package org.kablink.teaming.gwt.client.binderviews;

import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.binderviews.FolderViewBase.FolderPanels;
import org.kablink.teaming.gwt.client.util.BinderInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;

/**
 * File folder view.
 * 
 * @author drfoster@novell.com
 */
public class FileFolderView extends DataTableFolderViewBase {
	/**
	 * Constructor method.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 */
	public FileFolderView(BinderInfo folderInfo, ViewReady viewReady) {
		// Simply initialize the base class.
		super(folderInfo, viewReady, "vibe-fileFolderDataTable");
	}
	
	/**
	 * Called from the base class to complete the construction of this
	 * file folder view.
	 * 
	 * Implements the FolderViewBase.constructView() method.
	 */
	@Override
	public void constructView() {
		// Setup the appropriate styles for a file folder, populate the
		// view's contents and tell the base class that we're done with
		// the construction.
		getFlowPanel().addStyleName("vibe-fileFolderFlowPanel");
		populateContent();
		super.viewReady();
	}
	
	/**
	 * Loads the FileFolderView split point and returns an
	 * instance of it via the callback.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 * @param vClient
	 */
	public static void createAsync(final BinderInfo folderInfo, final ViewReady viewReady, final ViewClient vClient) {
		GWT.runAsync(FileFolderView.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				FileFolderView dfView = new FileFolderView(folderInfo, viewReady);
				vClient.onSuccess(dfView);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(m_messages.codeSplitFailure_FileFolderView());
				vClient.onUnavailable();
			}
		});
	}
	
	/**
	 * Returns true for panels that are to be included and false
	 * otherwise.
	 * 
	 * Overrides the FolderViewBase.includePanel() method.
	 * 
	 * @param folderPanel
	 * 
	 * @return
	 */
	@Override
	protected boolean includePanel(FolderPanels folderPanel) {
		boolean reply;

		// In the file folder view, we don't show the binder owner
		// avatar panels.
		switch (folderPanel) {
		case BINDER_OWNER_AVATAR:  reply = false; break;
		default:                   reply = true;  break;
		}
		
		return reply;
	}

	/**
	 * Called from the base class to reset the content of this
	 * file folder view.
	 * 
	 * Implements the FolderViewBase.resetView() method.
	 */
	@Override
	public void resetView() {
		// Clear any existing content from the view and repopulate it.
		resetContent();
		populateContent();
	}
	
	/**
	 * Called from the base class to resize the content of this
	 * file folder view.
	 * 
	 * Implements the FolderViewBase.resizeView() method.
	 */
	@Override
	public void resizeView() {
		// Nothing to do.
	}
}

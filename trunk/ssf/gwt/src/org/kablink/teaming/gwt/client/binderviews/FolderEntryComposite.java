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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.ViewFolderEntryInfo;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ResizeComposite;

/**
 * Class that holds the folder entry viewer.
 * 
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class FolderEntryComposite extends ResizeComposite {
	private GwtTeamingDataTableImageBundle	m_images;		// Access to Vibe's images.
	private GwtTeamingMessages				m_messages;		// Access to Vibe's messages.
	private ViewReady						m_viewReady;	// Stores a ViewReady created for the classes that extends it.
	private VibeFlowPanel					m_fp;			//
	private ViewFolderEntryInfo				m_vfei;			//

	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private FolderEntryComposite(ViewFolderEntryInfo vfei, ViewReady viewReady) {
		// Initialize the super class...
		super();
		
		// ...store the parameters...
		m_vfei      = vfei;
		m_viewReady = viewReady;
		
		// ...initialize the data members requiring it...
		m_images   = GwtTeaming.getDataTableImageBundle();
		m_messages = GwtTeaming.getMessages();
		
		// ...create the base content panel...
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-folderEntryComposite-panel");
		initWidget(m_fp);
		
		loadPart1Async();
	}

	/*
	 * Asynchronously loads the next part of the composite.
	 */
	private void loadPart1Async() {
		ScheduledCommand doPopulate = new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		};
		Scheduler.get().scheduleDeferred(doPopulate);
	}
	
	/*
	 * Synchronously loads the next part of the composite.
	 */
	private void loadPart1Now() {
//!		...this needs to be implemented...
		m_fp.add(new InlineLabel("...this needs to be implemented..."));
		m_viewReady.viewReady();
	}
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the folder entry composite and perform some operation on it.  */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface used to interact with the composite asynchronously
	 * after it loads. 
	 */
	public interface FolderEntryCompositeClient {
		void onSuccess(FolderEntryComposite fec);
		void onUnavailable();
	}
	
	/**
	 * Loads the FolderEntryComposite split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param fecClient
	 * @param vfei
	 * @param viewReady
	 */
	public static void createAsync(final FolderEntryCompositeClient fecClient, final ViewFolderEntryInfo vfei, final ViewReady viewReady) {
		GWT.runAsync(FolderEntryComposite.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_FolderEntryComposite());
				fecClient.onUnavailable();
			}

			@Override
			public void onSuccess() {
				FolderEntryComposite fec = new FolderEntryComposite(vfei, viewReady);
				fecClient.onSuccess(fec);
			}
		});
	}
}

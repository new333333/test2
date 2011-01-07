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
package org.kablink.teaming.gwt.client.tasklisting;

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.TaskListItem;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * Implements a GWT based task folder list user interface.
 * 
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class TaskListing {
	private boolean				m_sortDescend;							// true -> Sort is descending.  false -> Sort is ascending. 
	private Element				m_taskListingDIV;						// The <DIV> in the content pane that's to contain the task listing.
	private GwtTeamingMessages	m_messages = GwtTeaming.getMessages();	//
	private Long				m_binderId;								// The ID of the binder containing the tasks to be listed.
	private String				m_filterType;							// The current filtering in affect, if any.
	private String				m_mode;									// The current mode being displayed (PHYSICAL vs. VITRUAL.)
	private String				m_sortBy;								// The column the tasks are currently sorted by.

	/**
	 * Class constructor.
	 * 
	 * @param taskListingDIV
	 * @param binderId
	 * @param filterType
	 * @param mode
	 * @param sortBy
	 * @param sortDescend
	 */
	public TaskListing(Element taskListingDIV, Long binderId, String filterType, String mode, String sortBy, boolean sortDescend) {
		// Simply store the parameters.
		m_taskListingDIV = taskListingDIV;
		m_binderId       = binderId;
		m_filterType     = filterType;
		m_mode           = mode;
		m_sortBy         = sortBy;
		m_sortDescend    = sortDescend;
	}

	/**
	 * Show's the task listing based on the parameters passed into the
	 * constructor.
	 */
	public void show() {
		GwtTeaming.getRpcService().getTaskList(HttpRequestInfo.createHttpRequestInfo(), m_binderId, m_filterType, m_mode, new AsyncCallback<List<TaskListItem>>() {
			@Override
			public void onFailure(Throwable t) {
				String error = m_messages.rpcFailure_GetTaskList();
				GwtClientHelper.handleGwtRPCFailure(t, error);
				
				GwtClientHelper.removeAllChildren(m_taskListingDIV);
				m_taskListingDIV.appendChild(new InlineLabel(m_messages.rpcFailure_GetTaskList()).getElement());
			}

			@Override
			public void onSuccess(List<TaskListItem> result) {
				Window.alert( "TaskListing.show( " + m_binderId + " )" );
				
				GwtClientHelper.removeAllChildren(m_taskListingDIV);
				m_taskListingDIV.appendChild(new InlineLabel("...this needs to be implemented...").getElement());
			}			
		});		
	}
}

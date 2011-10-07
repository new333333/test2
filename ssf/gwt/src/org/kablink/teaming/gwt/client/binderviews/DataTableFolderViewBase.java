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

package org.kablink.teaming.gwt.client.binderviews;

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.binderviews.ViewBase;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.rpc.shared.FolderColumnsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderColumnsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.FolderColumnInfo;
import org.kablink.teaming.gwt.client.util.FolderType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeDockLayoutPanel;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;
import org.kablink.teaming.gwt.client.widgets.VibeVerticalPanel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Base object of 'data table' based folder views.
 * 
 * @author drfoster@novell.com
 */
public abstract class DataTableFolderViewBase extends ViewBase {
	private boolean					m_folderSortDescend;					// true -> The folder is sorted in descending order.  false -> It's sorted in ascending order.
	private FolderType				m_folderType;							// The type of folder being viewed.
	private GwtTeamingMessages		m_messages = GwtTeaming.getMessages();	// Access to the GWT localized string resource.
	private List<FolderColumnInfo>	m_folderColumnsList;					// The list of columns to be displayed.
	private Long					m_folderId;								// The ID of the folder to be viewed.				
	private String					m_folderSortBy;							// Which column the view is sorted on.
	private VibeDockLayoutPanel		m_mainPanel;							// The main panel holding the content of the view.
	private VibeFlowPanel			m_flowPanel;							// The flow panel used to hold the view specific content of the view.
	private VibeVerticalPanel		m_verticalPanel;						// The vertical panel that holds all components of the view, both common and view specific.
	
	/**
	 * Constructor method.
	 * 
	 * @param folderId
	 * @param viewReady
	 */
	public DataTableFolderViewBase(Long folderId, FolderType folderType, ViewReady viewReady) {
		// Initialize the base class...
		super(viewReady);

		// ...store the parameters...
		m_folderId   = folderId;
		m_folderType = folderType;

		// ...create the main content panels and initialize the
		// ...composite...
		constructCommonContent();
		initWidget(m_mainPanel);

		// ...and finally, asynchronously initialize the view.
		initializeViewAsync();
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	final public boolean				getFolderSortDescend() {return m_folderSortDescend;}
	final public FolderType             getFolder()            {return m_folderType;       }
	final public GwtTeamingMessages     getMessages()          {return m_messages;         }
	final public List<FolderColumnInfo>	getFolderColumns()     {return m_folderColumnsList;}
	final public Long                   getFolderId()          {return m_folderId;         }
	final public String					getFolderSortBy()      {return m_folderSortBy;     }
	final public VibeFlowPanel          getFlowPanel()         {return m_flowPanel;        }
	
	/*
	 * Creates the main content panels, ...
	 */
	private void constructCommonContent() {
		// Create the main layout panel for the content...
		m_mainPanel = new VibeDockLayoutPanel(Style.Unit.PX);
		m_mainPanel.addStyleName("vibe-folderViewBase vibe-dataTableFolderViewBase");

		// ...create the vertical panel that holds the layout that
		// ...flows down the view...
		m_verticalPanel = new VibeVerticalPanel();
		m_verticalPanel.addStyleName("vibe-dataTableFolderVerticalPanelBase");
	
		// ...add the common toolbars, ... to that vertical panel...
//!		...this needs to be implemented...
		
		// ...create a flow panel for the implementing class to put
		// ...its content.
		m_flowPanel = new VibeFlowPanel();
		m_flowPanel.addStyleName("vibe-dataTableFolderFlowPanelBase");
		
		// ...and tie everything together.
		m_verticalPanel.add(m_flowPanel);
		m_mainPanel.add(m_verticalPanel);
	}
	
	/**
	 * Called to allow the implementing class complete the construction
	 * of the view.
	 * 
	 * @param folderColumnsList
	 * @param folderSortBy
	 * @param folderSortDescend
	 */
	public abstract void constructView(List<FolderColumnInfo> folderColumnsList, String folderSortBy, boolean folderSortDescend);
	public abstract void resetView(    List<FolderColumnInfo> folderColumnsList, String folderSortBy, boolean folderSortDescend);

	/*
	 * Asynchronously initialize this view.
	 */
	private void initializeViewAsync() {
		Scheduler.ScheduledCommand doInit = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				initializeViewNow();
			}
		};
		Scheduler.get().scheduleDeferred(doInit);
	}
	
	/*
	 * Synchronously initialize this view.
	 */
	private void initializeViewNow() {
		GwtClientHelper.executeCommand(
				new GetFolderColumnsCmd(m_folderId, m_folderType),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetFolderColumns(),
					m_folderId);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				final FolderColumnsRpcResponseData responseData = ((FolderColumnsRpcResponseData) response.getResponseData());
				ScheduledCommand doConstructView = new ScheduledCommand() {
					@Override
					public void execute() {
						// Store the folder information...
						m_folderColumnsList = responseData.getFolderColumns();
						m_folderSortBy      = responseData.getFolderSortBy();
						m_folderSortDescend = responseData.getFolderSortDescend();
						
						// ...and tell the implementing class to construct
						// ...itself.
						constructView(
							m_folderColumnsList,
							m_folderSortBy,
							m_folderSortDescend);
					}
				};
				Scheduler.get().scheduleDeferred(doConstructView);
			}
		});
	}
	
	/**
	 * Resets the content for the implementing class.
	 */
	public void resetContent() {
		// Clear the flow panel's content...
		m_flowPanel.clear();
		
		// ...and reset anything else that's necessary.
//!		...this needs to be implemented...
	}
}

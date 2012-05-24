/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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

import java.util.ArrayList;

import org.kablink.teaming.gwt.client.BlogArchiveInfo;
import org.kablink.teaming.gwt.client.BlogArchiveMonth;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.rpc.shared.GetBlogArchiveInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.InlineLabel;


/**
 * This control is used to display the months that have entries in them for a given blog folder. 
 * @author jwootton
 *
 */
public class BlogArchiveCtrl extends VibeWidget
{
	private Long m_folderId;
	private FlexTable m_table;
	
	
	/**
	 * Callback interface to interact with the blog archive control asynchronously after it loads. 
	 */
	public interface BlogArchiveCtrlClient
	{
		void onSuccess( BlogArchiveCtrl baCtrl );
		void onUnavailable();
	}


	/**
	 * 
	 */
	private BlogArchiveCtrl()
	{
		VibeFlowPanel mainPanel;
		InlineLabel label;
		
		mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName( "blogArchiveCtrlMainPanel" );
		
		m_table = new FlexTable();
		
		// Add the "Archives" title
		label = new InlineLabel( GwtTeaming.getMessages().blogArchiveTitle() );
		label.addStyleName( "blogArchiveCtrlTitle" );
		m_table.setWidget( 0, 0, label );
		
		mainPanel.add( m_table );
		
		initWidget( mainPanel );
	}

	/**
	 * Add the archive information to this control.
	 */
	private void addArchiveInfo( BlogArchiveInfo info )
	{
		if ( info != null )
		{
			ArrayList<BlogArchiveMonth> listOfMonths;
			
			// Get a list of months that have blog entries
			listOfMonths = info.getListOfMonths();
			
			if ( listOfMonths != null )
			{
				for (BlogArchiveMonth nextMonth: listOfMonths)
				{
					addArchiveMonth( nextMonth );
				}
			}
		}
	}
	
	/**
	 * Add the given month to the list of months
	 */
	private void addArchiveMonth( final BlogArchiveMonth month )
	{
		if ( month != null )
		{
			InlineLabel monthLabel;
			int row;
			ClickHandler clickHandler;
			
			row = m_table.getRowCount() + 1;
			
			// Add the name of the month to the list.
			monthLabel = new InlineLabel( month.getName() );
			monthLabel.addStyleName( "blogArchiveCtrlMonthLabel" );
			m_table.setWidget( row, 0, monthLabel );
			
			// Add the number of blog entries to the list.
			m_table.setText( row, 1, "(" + String.valueOf( month.getNumEntries() ) + ")" );
			
			// Add a click handler to the name of the month
			clickHandler = new ClickHandler()
			{
				/**
				 * 
				 */
				@Override
				public void onClick( ClickEvent event )
				{
					Window.alert( month.getName() );
				}
			};
			monthLabel.addClickHandler( clickHandler );
		}
	}
	
	/**
	 * Loads the BlogArchiveCtrl split point and returns an instance of it via the callback.
	 */
	public static void createAsync( final BlogArchiveCtrlClient baCtrlClient )
	{
		GWT.runAsync( BlogArchiveCtrl.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess()
			{
				BlogArchiveCtrl baCtrl;

				baCtrl = new BlogArchiveCtrl();
				baCtrlClient.onSuccess( baCtrl );
			}
			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_BlogArchiveCtrl() );
				baCtrlClient.onUnavailable();
			}
		} );
	}

	/**
	 * Initialize this control for the given blog folder
	 */
	public void init( Long folderId )
	{
		GetBlogArchiveInfoCmd cmd;
		AsyncCallback<VibeRpcResponse> callback;

		m_folderId = folderId;
		
		// Create the callback used by the rpc request.
		callback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			@Override
			public void onFailure(Throwable t)
			{
				GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetBlogArchiveInfo(),
						m_folderId );
			}
			
			/**
			 * 
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				final BlogArchiveInfo info;

				info = (BlogArchiveInfo) response.getResponseData();
				
				if ( info != null )
				{
					Scheduler.ScheduledCommand schCmd;

					schCmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							// Add the archive information to this control.
							addArchiveInfo( info );
						}
					};
					Scheduler.get().scheduleDeferred( schCmd );
				}
			}
		};
		
		// Issue an rpc request to get the archive information for this blog folder.
		cmd = new GetBlogArchiveInfoCmd( m_folderId );
		GwtClientHelper.executeCommand( cmd, callback );
	}
}

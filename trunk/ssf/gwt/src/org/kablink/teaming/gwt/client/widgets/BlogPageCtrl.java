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

import org.kablink.teaming.gwt.client.BlogPage;
import org.kablink.teaming.gwt.client.BlogPages;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.BlogPageSelectedEvent;
import org.kablink.teaming.gwt.client.rpc.shared.GetBlogPagesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;


/**
 * This control is used to let the user select a blog "page" (folder) and if the user has
 * sufficient rights, provides a "new page" link. 
 * @author jwootton
 *
 */
public class BlogPageCtrl extends VibeWidget
{
	private Long m_defaultFolderId;
	private ListBox m_listbox;
	private ArrayList<BlogPage> m_listOfBlogPages;
	
	
	/**
	 * Callback interface to interact with the blog page control asynchronously after it loads. 
	 */
	public interface BlogPageCtrlClient
	{
		void onSuccess( BlogPageCtrl bpCtrl );
		void onUnavailable();
	}
	
	
	/**
	 * 
	 */
	private BlogPageCtrl()
	{
		VibeFlowPanel mainPanel;
		Label label;
		
		mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName( "blogPageCtrl_mainPanel" );
		
		// Add the "Blog page:" label
		label = new Label( GwtTeaming.getMessages().blogPageCtrl_selectPageLabel() );
		label.addStyleName( "blogPageCtrl_selectPageLabel" );
		mainPanel.add( label );
		
		// Add the listbox where the user can select the blog page they want to view.
		m_listbox = new ListBox( false );
		m_listbox.setVisibleItemCount( 1 );
		m_listbox.addChangeHandler( new ChangeHandler()
		{
			@Override
			public void onChange( ChangeEvent event )
			{
				Scheduler.ScheduledCommand cmd;
				
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						handleBlogPageSelected();
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		} );
		mainPanel.add( m_listbox );
		
		initWidget( mainPanel );
	}
	
	/**
	 * Add the given page to this control.
	 */
	private void addPage( BlogPage page )
	{
		if ( page != null )
		{
			m_listbox.addItem( page.getFolderName(), page.getFolderId() );
		}
	}
	
	/**
	 * Add the given blog pages to this control
	 */
	private void addPages( BlogPages pages )
	{
		if ( pages != null )
		{
			// Get the list of pages
			m_listOfBlogPages = pages.getPages();
			if ( m_listOfBlogPages != null )
			{
				for (BlogPage nextPage : m_listOfBlogPages)
				{
					addPage( nextPage );
				}
				
				// Select the default blog page
				selectDefaultPageInListbox();
			}
		}
	}

	/**
	 * Loads the BlogPageCtrl split point and returns an instance of it via the callback.
	 */
	public static void createAsync( final BlogPageCtrlClient bpCtrlClient )
	{
		GWT.runAsync( BlogPageCtrl.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess()
			{
				BlogPageCtrl bpCtrl;

				bpCtrl = new BlogPageCtrl();
				bpCtrlClient.onSuccess( bpCtrl );
			}
			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_BlogPageCtrl() );
				bpCtrlClient.onUnavailable();
			}
		} );
	}
	
	/**
	 * This method gets called when the user selects a blog page.
	 */
	private void handleBlogPageSelected()
	{
		int index;
		
		index = m_listbox.getSelectedIndex();
		if ( index != -1 && index < m_listOfBlogPages.size() )
		{
			BlogPage selectedPage;
			
			// Get the selected blog page
			selectedPage = m_listOfBlogPages.get( index );
			
			if ( selectedPage != null )
			{
				// Fire the BlogPageSelectedEvent so interested parties will know
				// that this page was selected.
				{
					BlogPageSelectedEvent event;
					
					event = new BlogPageSelectedEvent( selectedPage );
					GwtTeaming.fireEvent( event );
				}
			}
		}
	}
	
	/**
	 * Initialize this control for the given blog folder
	 */
	public void init( Long folderId )
	{
		GetBlogPagesCmd cmd;
		AsyncCallback<VibeRpcResponse> callback;

		m_defaultFolderId = folderId;
		
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
						GwtTeaming.getMessages().rpcFailure_GetBlogPages(),
						m_defaultFolderId );
			}
			
			/**
			 * 
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				final BlogPages pages;
				
				pages = (BlogPages) response.getResponseData();
				
				if ( pages != null )
				{
					Scheduler.ScheduledCommand schCmd;

					schCmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							// Add the pages to this control
							addPages( pages );
						}
					};
					Scheduler.get().scheduleDeferred( schCmd );
				}
			}
		};
		
		// Issue an rpc request to get the pages found in the given blog.
		cmd = new GetBlogPagesCmd( m_defaultFolderId );
		GwtClientHelper.executeCommand( cmd, callback );
	}
	
	/**
	 * Select the blog page in the listbox that is the default blog page.
	 */
	private void selectDefaultPageInListbox()
	{
		if ( m_defaultFolderId != null )
		{
			GwtClientHelper.selectListboxItemByValue( m_listbox, m_defaultFolderId.toString() );
		}
	}
}

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
import java.util.List;

import org.kablink.teaming.gwt.client.BlogArchiveFolder;
import org.kablink.teaming.gwt.client.BlogArchiveMonth;
import org.kablink.teaming.gwt.client.BlogPage;
import org.kablink.teaming.gwt.client.BlogPages;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.BlogArchiveFolderSelectedEvent;
import org.kablink.teaming.gwt.client.event.BlogArchiveMonthSelectedEvent;
import org.kablink.teaming.gwt.client.event.BlogPageCreatedEvent;
import org.kablink.teaming.gwt.client.event.BlogPageSelectedEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.CanAddFolderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetBlogPagesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.CreateBlogPageDlg.CreateBlogPageDlgClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.web.bindery.event.shared.HandlerRegistration;


/**
 * This control is used to let the user select a blog "page" (folder) and if the user has
 * sufficient rights, provides a "new page" link. 
 * @author jwootton
 *
 */
public class BlogPageCtrl extends VibeWidget
	implements
		// Event handlers implemented by this class.
		BlogArchiveFolderSelectedEvent.Handler,
		BlogArchiveMonthSelectedEvent.Handler,
		BlogPageCreatedEvent.Handler
{
	private List<HandlerRegistration> m_registeredEventHandlers;	// Event handlers that are currently registered.
	private Long m_defaultFolderId;
	private ListBox m_listbox;
	private InlineLabel m_newPageLink;
	private BlogPages m_blogPages;
	private ArrayList<BlogPage> m_listOfBlogPages;
	private BlogPage m_topMostBlogPage;
	private CreateBlogPageDlg m_createBlogPageDlg;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[]
    {
		TeamingEvents.BLOG_ARCHIVE_FOLDER_SELECTED,
		TeamingEvents.BLOG_ARCHIVE_MONTH_SELECTED,
		TeamingEvents.BLOG_PAGE_CREATED
	};
	
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
		
		m_topMostBlogPage = null;
		m_createBlogPageDlg = null;
		
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
		
		// Add a "New page" link
		{
			FlowPanel panel;
			
			panel = new FlowPanel();
			panel.addStyleName( "paddingTop8px" );
			m_newPageLink = new InlineLabel( GwtTeaming.getMessages().blogPageCtrl_newPageLabel() );
			m_newPageLink.addStyleName( "blogPageCtrl_newPageLink" );
			m_newPageLink.setVisible( false );
			m_newPageLink.addClickHandler( new ClickHandler()
			{
				/**
				 * 
				 */
				@Override
				public void onClick( ClickEvent event )
				{
					Scheduler.ScheduledCommand cmd;

					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							handleClickOnNewPageLink();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			
			panel.add( m_newPageLink );
			mainPanel.add( panel );
		}
		
		initWidget( mainPanel );
	}
	
	/**
	 * Add this newly created blog page to our listbox and select it.
	 */
	private void addNewPage( Long folderId, String pageName )
	{
		BlogPage blogPage;
		
		blogPage = new BlogPage();
		blogPage.setFolderName( pageName );
		blogPage.setFolderId( folderId.toString() );
		
		m_listOfBlogPages.add( blogPage );
		
		// Add the page to our listbox.
		addPage( blogPage );
		
		// Select the new page.
		GwtClientHelper.selectListboxItemByValue( m_listbox, blogPage.getFolderId() );
		handleBlogPageSelected();
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
		m_blogPages = pages;
		m_topMostBlogPage = null;
		
		if ( pages != null )
		{
			// Get the list of pages
			m_listOfBlogPages = new ArrayList<BlogPage>( pages.getPages() );
			
			// Get the top-most blog page.
			m_topMostBlogPage = getTopMostBlogPage( pages );
			
			// Add the top-most blog page as the first entry in the listbox
			if ( m_topMostBlogPage != null )
			{
				addPage( m_topMostBlogPage );
			}
			
			if ( m_listOfBlogPages != null )
			{
				for (BlogPage nextPage : m_listOfBlogPages)
				{
					// Is this page the top-most blog page?
					if ( nextPage != m_topMostBlogPage )
					{
						// No, add it.
						addPage( nextPage );
					}
				}
				
				// Select the default blog page
				selectDefaultPageInListbox();
			}
		}
	}

	/**
	 * Issue an rpc request to see if the user has rights to create a folder within the
	 * give folder.  If they don't, hide the "New page" link. 
	 */
	private void checkNewPageRights( final String folderId )
	{
		if ( folderId != null )
		{
			CanAddFolderCmd cmd;
			
			// Issue a command to see if the user has rights to add a page to this blog.
			// If they have rights we will show the "New page" link.  Otherwise, we will
			// hide that link.
			cmd = new CanAddFolderCmd( folderId );
			GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				@Override
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_CanAddFolder(),
						folderId );
				}
				
				/**
				 * 
				 */
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					BooleanRpcResponseData responseData;
					Boolean result;

					// Hide the "New blog page" link.
					m_newPageLink.setVisible( false );
					
					// Does the user have rights to add a blog page?
					responseData = (BooleanRpcResponseData) response.getResponseData();
					result = responseData.getBooleanValue();
					if ( result == Boolean.TRUE )
					{
						Scheduler.ScheduledCommand scCmd;
						
						// Yes, show the "New blog page" link
						scCmd = new Scheduler.ScheduledCommand()
						{
							/**
							 * 
							 */
							@Override
							public void execute()
							{
								m_newPageLink.setVisible( true );
							}
						};
						Scheduler.get().scheduleDeferred( scCmd );
					}
				}
			});
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
	 * This method returns the blog page that is currently selected in the listbox.
	 */
	private BlogPage getSelectedBlogPage()
	{
		int index;
		
		index = m_listbox.getSelectedIndex();
		if ( index != -1 && m_listOfBlogPages != null && index < m_listOfBlogPages.size() )
		{
			String selectedFolderId;
			
			// Get the folder id from the listbox.
			selectedFolderId = m_listbox.getValue( index );
			if ( selectedFolderId != null )
			{
				// Find the blog page with the given folder id.
				for (BlogPage nextBlogPage : m_listOfBlogPages)
				{
					String nextFolderId;
					
					// Is this the selected blog page?
					nextFolderId = nextBlogPage.getFolderId();
					if ( nextFolderId != null && nextFolderId.equalsIgnoreCase( selectedFolderId ) )
					{
						// Yes
						return nextBlogPage;
					}
				}
			}
		}
		
		// If we get here, we did not find the selected blog page.
		// This should never happen.
		return null;
	}
	
	/**
	 * Find the top-most blog page.
	 */
	private BlogPage getTopMostBlogPage( BlogPages pages )
	{
		if ( pages != null )
		{
			ArrayList<BlogPage> listOfBlogPages;
			Long topMostFolderId;

			topMostFolderId = pages.getTopFolderId();

			// Get the list of pages
			listOfBlogPages = pages.getPages();
			
			if ( listOfBlogPages != null && topMostFolderId != null )
			{
				String topMostFolderIdS;
				
				topMostFolderIdS = topMostFolderId.toString();

				for (BlogPage nextPage : listOfBlogPages)
				{
					String nextFolderId;
					
					// Is this page the top-most blog page?
					nextFolderId = nextPage.getFolderId();
					if ( nextFolderId != null && nextFolderId.equalsIgnoreCase( topMostFolderIdS ) )
					{
						// Yes
						return nextPage;
					}
				}
			}
		}

		// If we get here we did not find the top-most blog page.
		// This should never happen.
		return null;
	}

	/**
	 * This method gets called when the user selects a blog page.
	 */
	private void handleBlogPageSelected()
	{
		BlogPage selectedPage;
		
		// Get the selected blog page
		selectedPage = getSelectedBlogPage();
		
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
	
	/**
	 * This gets called when the user clicks on the "new blog page" link.  We will invoke the
	 * "new folder" dialog
	 */
	private void handleClickOnNewPageLink()
	{
		// Invoke the add folder dialog.
		invokeNewBlogPageDlg();
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
							Long topFolderId;
							
							// Add the pages to this control
							addPages( pages );

							// See if the user has rights to create a new blog page.
							// We check the rights on the top-most folder because that is
							// where new blog folders will be created.
							topFolderId = pages.getTopFolderId();
							if ( topFolderId != null )
								checkNewPageRights( topFolderId.toString() );
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
	 * Invoke the "add blog page" dialog
	 */
	private void invokeNewBlogPageDlg()
	{
		// Do we have the top-most blog page.
		if ( m_topMostBlogPage != null )
		{
			// Yes
			// Do we already have a "new blog page" dialog?
			if ( m_createBlogPageDlg == null )
			{
				String topMostFolderId;
		
				// No
				// Invoke the "add folder" dialog.  It will create the new blog folder
				// in the top-most blog folder.
				topMostFolderId = m_topMostBlogPage.getFolderId();
				if ( topMostFolderId != null )
				{
					final Long binderId;
					
					binderId = Long.valueOf( topMostFolderId );
	
					CreateBlogPageDlg.createAsync( true, true, new CreateBlogPageDlgClient()
					{			
						@Override
						public void onUnavailable()
						{
							// Nothing to do.  Error handled in asynchronous provider.
						}
						
						@Override
						public void onSuccess( CreateBlogPageDlg cbpDlg )
						{
							ScheduledCommand cmd;
							
							m_createBlogPageDlg = cbpDlg;
							
							cmd = new ScheduledCommand()
							{
								@Override
								public void execute()
								{
									m_createBlogPageDlg.init( binderId, m_blogPages.getFolderTemplateId() );
									m_createBlogPageDlg.show( true );
								}
							};
							Scheduler.get().scheduleDeferred( cmd );
						}
					});
				}
			}
			else
			{
				// Yes, show it.
				m_createBlogPageDlg.show( true );
			}
		}
	}
	
	/**
	 * Called when the blog folder view is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach()
	{
		// Let the widget attach and then register our event handlers.
		super.onAttach();
		registerEvents();
	}
	
	/**
	 * Handles the BlogArchiveFolderSelectedEvent received by this class.
	 * 
	 * Implements the BlogArchiveFolderSelectedEvent.onBlogArchiveFolderSelectedEvent() method.
	 * 
	 */
	@Override
	public void onBlogArchiveFolderSelected( BlogArchiveFolderSelectedEvent event )
	{
		BlogArchiveMonth month;
		final BlogArchiveFolder folder;
		
		// Get the month/year and the folder that was selected.
		month = event.getMonth();
		folder = event.getFolder();
		if ( month != null && folder != null )
		{
			Scheduler.ScheduledCommand cmd;

			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute() 
				{
					Long folderId;
					
					folderId = folder.getFolderId();
					if ( folderId != null )
					{
						// Select the given folder in our list of blog pages
						GwtClientHelper.selectListboxItemByValue( m_listbox, folderId.toString() );
					}
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
	}
	
	/**
	 * Handles the BlogArchiveMonthSelectedEvent received by this class.
	 * 
	 * Implements the BlogArchiveMonthSelectedEvent.onBlogArchiveMonthSelectedEvent() method.
	 * 
	 */
	@Override
	public void onBlogArchiveMonthSelected( BlogArchiveMonthSelectedEvent event )
	{
		Scheduler.ScheduledCommand cmd;

		// A month was selected in the archive control.  If we have a default folder id
		// select it.
		if ( m_defaultFolderId != null )
		{
			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute() 
				{
					// Select the default blog page
					GwtClientHelper.selectListboxItemByValue( m_listbox, m_defaultFolderId.toString() );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
	}
	
	/**
	 * Handles the BlogPageCreatedEvent received by this class.
	 * 
	 * Implements the BlogPageCreatedEvent.onBlogPageCreated() method.
	 */
	@Override
	public void onBlogPageCreated( BlogPageCreatedEvent event )
	{
		final Long folderId;
		final String pageName;
		
		folderId = event.getFolderId();
		pageName = event.getPageName();
		if ( folderId != null && pageName != null && pageName.length() > 0 )
		{
			Scheduler.ScheduledCommand cmd;
			
			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute() 
				{
					// Add the newly created page to the listbox.
					addNewPage( folderId, pageName );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
	}
	
	/**
	 * Called when the blog folder view is detached.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach()
	{
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
	}
	
	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents()
	{
		// If we having allocated a list to track events we've
		// registered yet...
		if ( null == m_registeredEventHandlers )
		{
			// ...allocate one now.
			m_registeredEventHandlers = new ArrayList<HandlerRegistration>();
		}

		// If the list of registered events is empty...
		if ( m_registeredEventHandlers.isEmpty() )
		{
			// ...register the events.
			EventHelper.registerEventHandlers(
										GwtTeaming.getEventBus(),
										m_registeredEvents,
										this,
										m_registeredEventHandlers );
		}
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

	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents()
	{
		// If we have a non-empty list of registered events...
		if ( null != m_registeredEventHandlers && !m_registeredEventHandlers.isEmpty() )
		{
			// ...unregister them.  (Note that this will also empty the list.)
			EventHelper.unregisterEventHandlers( m_registeredEventHandlers );
		}
	}
}

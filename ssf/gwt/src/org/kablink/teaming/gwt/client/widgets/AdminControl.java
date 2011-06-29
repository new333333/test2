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

package org.kablink.teaming.gwt.client.widgets;


import java.util.ArrayList;

import org.kablink.teaming.gwt.client.event.AdministrationExitEvent;
import org.kablink.teaming.gwt.client.event.AdministrationUpgradeCheckEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.admin.AdminAction;
import org.kablink.teaming.gwt.client.admin.GwtAdminAction;
import org.kablink.teaming.gwt.client.admin.GwtAdminCategory;
import org.kablink.teaming.gwt.client.admin.GwtUpgradeInfo;
import org.kablink.teaming.gwt.client.event.TeamingActionEvent;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.TeamingAction;
import org.kablink.teaming.gwt.client.widgets.AdminInfoDlg.AdminInfoDlgClient;
import org.kablink.teaming.gwt.client.widgets.ContentControl.ContentControlClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;


/**
 * This widget will display the controls that make up the "Administration" control.
 * There is a widget that displays the list of administration actions and a widget
 * that displays the page for the selected administration action.
 */
public class AdminControl extends Composite
	implements ActionTrigger, 
	// EventBus handlers implemented by this class.
		AdministrationExitEvent.Handler,
		AdministrationUpgradeCheckEvent.Handler
{
	private AdminActionsTreeControl m_adminActionsTreeControl = null;
	private ContentControl m_contentControl = null;

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		// Administration events.
		TeamingEvents.ADMINISTRATION_EXIT,
		TeamingEvents.ADMINISTRATION_UPGRADE_CHECK,
	};
	
	/**
	 * Class used for the ui for an administration action.
	 */
	private class AdminActionControl extends Composite
		implements ClickHandler, MouseOverHandler, MouseOutHandler
	{
		private GwtAdminAction m_adminAction;
		private InlineLabel m_actionName;
		
		/**
		 * 
		 */
		public AdminActionControl( GwtAdminAction adminAction )
		{
			FlowPanel mainPanel;
			String id;
			
			// Remember the action we are associated with.
			m_adminAction = adminAction;
			
			mainPanel = new FlowPanel();
			
			m_actionName = new InlineLabel( adminAction.getLocalizedName() );
			m_actionName.addClickHandler( this );
			m_actionName.addMouseOverHandler( this );
			m_actionName.addMouseOutHandler( this );
			m_actionName.addStyleName( "adminActionControl" );
			m_actionName.addStyleName( "cursorPointer" );
			
			// For automation purposes, give the label a unique id.
			id = "adminAction-" + String.valueOf( adminAction.getActionType().ordinal() );
			m_actionName.getElement().setId( id );
			
			mainPanel.add( m_actionName );
			
			// All composites must call initWidget() in their constructors.
			initWidget( mainPanel );
			
		}// end AdminActionControl()
		
		/**
		 * 
		 */
		public void onClick( ClickEvent event )
		{
			// Tell the AdminControl that an action was selected.
			adminActionSelected( m_adminAction );
		}// end onClick()
		
		/**
		 * 
		 */
		public void onMouseOver( MouseOverEvent event )
		{
			m_actionName.addStyleName( "adminActionControlMouseOver" );
		}// end onMouseOver()
		
		
		/**
		 * 
		 */
		public void onMouseOut( MouseOutEvent event )
		{
			m_actionName.removeStyleName( "adminActionControlMouseOver" );
		}// end onMouseOut()
	}// end AdminActionControl
	
	
	/**
	 * Class used for the ui for an administration category.
	 */
	private class AdminCategoryControl extends Composite
		implements ClickHandler
	{
		private FlexTable m_mainTable;
		private FlexTable m_actionsTable;
		private Image m_expandedImg;
		private Image m_collapsedImg;
		
		/**
		 * 
		 */
		public AdminCategoryControl( GwtAdminCategory category )
		{
			ArrayList<GwtAdminAction> actions = null;
			ImageResource imgResource;
			FlexTable.FlexCellFormatter cellFormatter; 
			int row = 0;

			// Create the "expanded" and "collapsed" images.
			imgResource = GwtTeaming.getWorkspaceTreeImageBundle().tree_closer();
			m_expandedImg = new Image( imgResource );
			m_expandedImg.addStyleName( "cursorPointer" );
			m_expandedImg.addClickHandler( this );
			imgResource = GwtTeaming.getWorkspaceTreeImageBundle().tree_opener();
			m_collapsedImg = new Image( imgResource );
			m_collapsedImg.addStyleName( "cursorPointer" );
			m_collapsedImg.addClickHandler( this );

			m_mainTable = new FlexTable();
			cellFormatter = m_mainTable.getFlexCellFormatter();
			
			// Add the category image and name.
			{
				InlineLabel categoryName;
				Image img;
				GwtAdminCategory.GwtAdminCategoryType categoryType;
				
				m_mainTable.setWidget( row, 0, m_expandedImg );
				
				// Add the image associated with this category.
				categoryType = category.getCategoryType();
				if ( categoryType == GwtAdminCategory.GwtAdminCategoryType.MANAGEMENT )
				{
					imgResource = GwtTeaming.getImageBundle().management16();
					img = new Image( imgResource );
				}
				else if ( categoryType == GwtAdminCategory.GwtAdminCategoryType.REPORTS )
				{
					imgResource = GwtTeaming.getImageBundle().report16();
					img = new Image( imgResource );
				}
				else if ( categoryType == GwtAdminCategory.GwtAdminCategoryType.SYSTEM )
				{
					imgResource = GwtTeaming.getImageBundle().system16();
					img = new Image( imgResource );
				}
				else
				{
					imgResource = GwtTeaming.getImageBundle().management16();
					img = new Image( imgResource );
				}
				
				m_mainTable.setWidget( row, 1, img );
				
				// Add the category name
				categoryName = new InlineLabel( category.getLocalizedName() );
				categoryName.addStyleName( "adminCategoryName" );
				m_mainTable.setWidget( row, 2, categoryName );
				cellFormatter.setWidth( row, 2, "100%" );
				
				imgResource = GwtTeaming.getImageBundle().spacer1px();
				img = new Image( imgResource );
				m_mainTable.setWidget( row, 3, img );
				cellFormatter.setWidth( row, 3, "100%" );
				
				++row;
			}

			// Create a table to hold all the actions.
			m_actionsTable = new FlexTable();
			cellFormatter.setColSpan( row, 1, 2 );
			m_mainTable.setWidget( row, 1, m_actionsTable );
			
			// Add a ui widget for all of the actions associated with this category.
			actions = category.getActions();
			if ( actions != null )
			{
				for (GwtAdminAction action : actions )
				{
					AdminActionControl adminActionControl;
					
					// Add a ui widget for this administration action.
					adminActionControl = new AdminActionControl( action );
					m_actionsTable.setWidget( m_actionsTable.getRowCount(), 0, adminActionControl );
				}
			}
			
			// All composites must call initWidget() in their constructors.
			initWidget( m_mainTable );

		}// end AdminCategoryControl()
		
		/**
		 * Hide all the actions associated with this category.
		 */
		public void hideActions()
		{
			m_actionsTable.setVisible( false );
			relayoutPage();
		}// end hideActions()
		
		/**
		 * This method gets called when the user clicks on the "expand" or "collapse" image.
		 */
		public void onClick( ClickEvent event )
		{
			if ( event.getSource() == m_expandedImg )
			{
				m_mainTable.setWidget( 0, 0, m_collapsedImg );
				hideActions();
			}
			else if ( event.getSource() == m_collapsedImg )
			{
				m_mainTable.setWidget( 0, 0, m_expandedImg );
				showActions();
			}
		}// end onClick()
		
		
		/**
		 * Show all the actions associated with this category.
		 */
		public void showActions()
		{
			m_actionsTable.setVisible( true );
			relayoutPage();
		}// end showActions()
	}// end AdminCategoryControl
	
	
	/**
	 * 
	 */
	private class AdminActionsTreeControl extends Composite
	{
		// m_rpcGetAdminActionsCallback is our callback that gets called when the ajax request to get the administration actions completes.
		private AsyncCallback<ArrayList<GwtAdminCategory>> m_rpcGetAdminActionsCallback = null;
		private AsyncCallback<GwtUpgradeInfo> m_rpcGetUpgradeInfoCallback2 = null;
		private FlexTable m_mainTable;
		
		/**
		 * 
		 */
		public AdminActionsTreeControl()
		{
			FlowPanel mainPanel;
			
			mainPanel = new FlowPanel();
			mainPanel.addStyleName( "adminActionsTreeControl" );
			
			// Create a table that will hold all the top level administration categories
			m_mainTable = new FlexTable();
			m_mainTable.addStyleName( "adminCategoriesTable" );
			m_mainTable.setCellPadding( 0 );
			m_mainTable.setCellSpacing( 0 );
			
			mainPanel.add( m_mainTable );
			
			// Add the header
			{
				Label header;
				FlexTable table;
				FlexTable.FlexCellFormatter cellFormatter;
				
				table = new FlexTable();
				table.addStyleName( "adminActionsTreeControlHeader" );
				table.setCellPadding( 0 );
				table.setCellSpacing( 0 );
				
				cellFormatter = table.getFlexCellFormatter();
				
				header = new Label( GwtTeaming.getMessages().administrationHeader() );
				header.addStyleName( "adminActionsTreeControlHeader2" );
				table.setWidget( 0, 0, header );
				cellFormatter.setWordWrap( 0, 0, false );
				
				// Add an image the user can click on to get administration information
				{
					ImageResource imgResource;
					Image img;

					imgResource = GwtTeaming.getImageBundle().info2();
					img = new Image( imgResource );
					img.addStyleName( "cursorPointer" );
					if ( GwtClientHelper.jsIsIE() )
						img.addStyleName( "margin-right-22" );
					else
						img.addStyleName( "margin-right-5" );
					img.addClickHandler( new ClickHandler()
					{
						public void onClick( ClickEvent event  )
						{
							Scheduler.ScheduledCommand cmd;
							
							cmd = new Scheduler.ScheduledCommand()
							{
								/**
								 * 
								 */
								public void execute()
								{
									// Issue an ajax request to get the upgrade information from the server.
									// When we get the response the callback will open the AdminInfoDlg.
									getUpgradeInfoFromServer( m_rpcGetUpgradeInfoCallback2 );
								}
							};
							Scheduler.get().scheduleDeferred( cmd );
						}// end onClick()
					}
					);
					table.setWidget( 0, 1, img );
					cellFormatter.setHorizontalAlignment( 0, 1, HasHorizontalAlignment.ALIGN_LEFT );
					cellFormatter.setWidth( 0, 1, "100%" );
				}
				
				m_mainTable.setWidget( 0, 0, table );
			}
			
			// Create the callback that will be used when we issue an ajax call to get the administration actions.
			m_rpcGetAdminActionsCallback = new AsyncCallback<ArrayList<GwtAdminCategory>>()
			{
				/**
				 * 
				 */
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetAdminActions(),
						GwtMainPage.m_requestInfo.getBinderId() );
				}// end onFailure()
		
				/**
				 * 
				 * @param result
				 */
				public void onSuccess( ArrayList<GwtAdminCategory> adminCategories )
				{
					for ( GwtAdminCategory category : adminCategories )
					{
						// Add this administration category to the page.
						addCategory( category );
					}
				}// end onSuccess()
			};

			// Create the callback that will be used when we issue an ajax call to get upgrade information
			m_rpcGetUpgradeInfoCallback2 = new AsyncCallback<GwtUpgradeInfo>()
			{
				/**
				 * 
				 */
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetUpgradeInfo() );
				}// end onFailure()
		
				/**
				 * 
				 * @param result
				 */
				public void onSuccess( GwtUpgradeInfo upgradeInfo )
				{
					int x;
					int y;
					
					// Show the AdminInfoDlg
					x = m_adminActionsTreeControl.getAbsoluteLeft() + m_adminActionsTreeControl.getOffsetWidth();
					y = m_adminActionsTreeControl.getAbsoluteTop();
					showAdminInfoDlg( upgradeInfo, x, y );
				}// end onSuccess()
			};

			// Issue a deferred command to get the administration actions the user has rights to run.
			{
				Scheduler.ScheduledCommand cmd;

				cmd = new Scheduler.ScheduledCommand()
				{
					public void execute()
					{
						getAdminActionsFromServer();
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
			
			initWidget( mainPanel );
		}// end AdminActionsTreeControl()
		
		
		/**
		 * Add an administration category (ie Reports) to the page along with all the actions
		 * in the category.
		 */
		private void addCategory( GwtAdminCategory category )
		{
			AdminCategoryControl categoryControl;
			int row;
			
			// Create a ui control for this category.
			categoryControl = new AdminCategoryControl( category );
			
			// Add the ui control to the table.
			row = m_mainTable.getRowCount();
			m_mainTable.setWidget( row, 0, categoryControl );
		}// end addCategory()
		
		/**
		 * Issue an ajax request to get the list of administration actions the user
		 * has rights to run.
		 */
		public void getAdminActionsFromServer()
		{
			GwtRpcServiceAsync rpcService;
			String binderId;
			
			rpcService = GwtTeaming.getRpcService();
			
			// Issue an ajax request to get the administration actions the user has rights to perform.
			binderId = GwtMainPage.m_requestInfo.getBinderId();
			rpcService.getAdminActions( HttpRequestInfo.createHttpRequestInfo(), binderId, m_rpcGetAdminActionsCallback );
		}// end getAdminActionsFromServer()
	}// end AdminActionsTreeControl

	
	/*
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private AdminControl()
	{
		// Register the events to be handled by this class.
		EventHelper.registerEventHandlers(
			GwtTeaming.getEventBus(),
			m_registeredEvents,
			this );
		
		final FlowPanel mainPanel = new FlowPanel();
		mainPanel.addStyleName( "adminControl" );
		
		// Create the control that holds all of the administration actions
		m_adminActionsTreeControl = new AdminActionsTreeControl();
		mainPanel.add( m_adminActionsTreeControl );
		
		// Create a control to hold the administration page for the selection administration action.
		ContentControl.createAsync(
				"adminContentControl",
				new ContentControlClient()
		{			
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}// end onUnavailable()
			
			@Override
			public void onSuccess( ContentControl contentCtrl )
			{
				m_contentControl = contentCtrl;
				m_contentControl.addStyleName( "adminContentControl" );
				mainPanel.add( m_contentControl );
			}// end onSuccess()
		} );
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}// end AdminControl()

	/**
	 * This method gets called when the user selects one of the administration actions.
	 */
	private void adminActionSelected( GwtAdminAction adminAction )
	{
		// Are we dealing with the "Site Branding" action?
		if ( adminAction.getActionType() == AdminAction.SITE_BRANDING )
		{
			// Yes, inform all registered action handlers that the user wants to edit the site branding.
			triggerAction( TeamingAction.EDIT_SITE_BRANDING );
		}
		else
		{
			String url;
			
			// Position the content control.
			relayoutPageNow();
			
			// Get the url used by the selected action.
			url = adminAction.getUrl();
			if ( url != null && url.length() > 0 )
			{
				Scheduler.ScheduledCommand cmd;

				// Clear the iframe's content 
				m_contentControl.clear();
				
				// Set the iframe's content to the selected administration page.
				m_contentControl.setUrl( url );
				
				cmd = new Scheduler.ScheduledCommand()
				{
					public void execute()
					{
						showContentPanel();
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		}
	}// end adminActionSelected()

	
	/**
	 * For some reason if we try to logout while the "configure ldap" page is still loaded
	 * we see an error in IE.  So clear the content panel.
	 */
	public void doPreLogoutCleanup()
	{
		// Clear the iframe's content 
		m_contentControl.clear();
		
		// Set the iframe's content to nothing.
		m_contentControl.setUrl( "" );
	}// end doPreLogoutCleanup()
	
	
	/**
	 * Issue an ajax request to get information about the upgrade tasks that need to be performed.
	 * If there are upgrade tasks that need to be performed show the list of tasks.
	 */
	public static void showUpgradeTasks()
	{
		AsyncCallback<GwtUpgradeInfo> rpcGetUpgradeInfoCallback = null;

		// Create the callback that will be used when we issue an ajax call to get upgrade information
		rpcGetUpgradeInfoCallback = new AsyncCallback<GwtUpgradeInfo>()
		{
			/**
			 * 
			 */
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetUpgradeInfo() );
			}
	
			/**
			 * 
			 * @param result
			 */
			public void onSuccess( final GwtUpgradeInfo upgradeInfo )
			{
				Scheduler.ScheduledCommand cmd;
				
				cmd = new Scheduler.ScheduledCommand()
				{
					/**
					 * 
					 */
					public void execute()
					{
						// Are there upgrade tasks that need to be performed?
						if ( upgradeInfo.doUpgradeTasksExist() )
						{
							// Yes, invoke the AdminInfoDlg.
							showAdminInfoDlg( upgradeInfo, 250, 100 );
						}
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		};

		// When we get the upgrade info from the server our callback will check to
    	// see if upgrade tasks exists.  If they do, the callback will invoke the
    	// AdminInfoDlg which will show the user the tasks they need to do.
		getUpgradeInfoFromServer( rpcGetUpgradeInfoCallback );
	}
	
	
	/**
	 * Issue an ajax request to get the upgrade information from the server.
	 */
	public static void getUpgradeInfoFromServer( AsyncCallback<GwtUpgradeInfo> callback )
	{
		GwtRpcServiceAsync rpcService;
		
		rpcService = GwtTeaming.getRpcService();
		
		// Issue an ajax request to get the upgrade information
		rpcService.getUpgradeInfo(  HttpRequestInfo.createHttpRequestInfo(), callback );
	}// end getUpgradeInfoFromServer()

	
	/**
	 * 
	 */
	public void hideContentPanel()
	{
		m_contentControl.setVisible( false );	
	}// end hideContentPanel()
	
	
	/**
	 * 
	 */
	public void hideControl()
	{
		setVisible( false );
	}// end hideControl()
	
	
	/**
	 * 
	 */
	public void hideTreeControl()
	{
		m_adminActionsTreeControl.setVisible( false );	
	}// end hideTreeControl()
	
	
	/**
	 * 
	 */
	public void relayoutPage()
	{
		Scheduler.ScheduledCommand cmd;

		cmd = new Scheduler.ScheduledCommand()
		{
			public void execute()
			{
				relayoutPageNow();
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}// end relayoutPage()
	
	
	/**
	 * 
	 */
	private void relayoutPageNow()
	{
		int width;
		int height;
		int x;
		Style style;

		// Calculate where the content control should be positioned.
		x = m_adminActionsTreeControl.getAbsoluteLeft() + m_adminActionsTreeControl.getOffsetWidth() + 8;
		
		// Calculate how wide the ContentControl should be.
		{
			int clientWidth;
			
			// Get the width of the browser window's client area.
			clientWidth = Window.getClientWidth();
			
			width = clientWidth - x - 10; 
		}
		
		// Calculate how high the ContentControl should be.
		{
			int clientHeight;
			
			// Get the height of the browser window's client area.
			clientHeight = Window.getClientHeight();
			
			height = clientHeight - m_adminActionsTreeControl.getAbsoluteTop() - 20;
		}
		
		// Set the width and height of the content control.
		m_contentControl.setDimensions( width, height );

		// Set the left position of the content control.
		style = m_contentControl.getElement().getStyle();
		style.setLeft( x, Style.Unit.PX );

		// Set the height of the tree control.
		style = m_adminActionsTreeControl.getElement().getStyle();
		style.setHeight( height, Style.Unit.PX );
	}// end relayoutPageNow()

	
	/**
	 * 
	 */
	public static void showAdminInfoDlg( final GwtUpgradeInfo upgradeInfo, final int x, final int y )
	{
		AdminInfoDlg.createAsync( false, true, x, y, new AdminInfoDlgClient()
		{			
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}// end onUnavailable()
			
			@Override
			public void onSuccess( final AdminInfoDlg adminInfoDlg )
			{
				ScheduledCommand initAndShowDlg = new ScheduledCommand() {
					@Override
					public void execute()
					{
						showAdminInfoDlgImpl( adminInfoDlg, upgradeInfo );
					}// end execute()
				};
				Scheduler.get().scheduleDeferred( initAndShowDlg );
			}// onSuccess()
		} );
	}// end showAdminInfoDlg()
	
	private static void showAdminInfoDlgImpl( final AdminInfoDlg adminInfoDlg, final GwtUpgradeInfo upgradeInfo ) {
		AdminInfoDlg.initAndShow( adminInfoDlg, upgradeInfo );
	}
	
	
	/**
	 * 
	 */
	public void showContentPanel()
	{
		m_contentControl.setVisible( true );	
	}// end showContentPanel()
	
	
	/**
	 * 
	 */
	public void showControl()
	{
		Scheduler.ScheduledCommand cmd;

		// Set the position of the content control.
		cmd = new Scheduler.ScheduledCommand()
		{
			public void execute()
			{
				Scheduler.ScheduledCommand cmd2;
            	
				relayoutPage();
				
				cmd2 = new Scheduler.ScheduledCommand()
				{
					public void execute()
					{
						setVisible( true );
					}
				};
				Scheduler.get().scheduleDeferred( cmd2 );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );

		// Issue an ajax request to get the upgrade information from the server.
		cmd = new Scheduler.ScheduledCommand()
		{
			public void execute()
			{
            	// Get the upgrade info from the server.  If there are upgrade tasks that
				// need to be performed, AdminInfoDlg will display them.
				showUpgradeTasks();
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}// end showControl()
	
	/**
	 * 
	 */
	public void showTreeControl()
	{
		m_adminActionsTreeControl.setVisible( true );	
	}// end showTreeControl()
	
	
	/**
	 * Fires a TeamingAction at the registered ActionHandler's.
	 * 
	 * Implements the ActionTrigger.triggerAction() method. 
	 *
	 * @param action
	 * @param obj
	 */
	public void triggerAction( TeamingAction action, Object obj )
	{
		GwtTeaming.fireEvent(new TeamingActionEvent(action, obj));
	}// end triggerAction()
	
	/**
	 * 
	 */
	public void triggerAction( TeamingAction action )
	{
		// Always use the initial form of the method.
		triggerAction( action, null );
	}// end triggerAction()

	/**
	 * Handles AdministrationExitEvent's received by this class.
	 * 
	 * Implements the AdministrationExitEvent.Handler.onAdministrationExit() method.
	 * 
	 * @param event
	 */
	@Override
	public void onAdministrationExit( AdministrationExitEvent event )
	{
		hideControl();
	}// end onAdministrationExit()
	
	/**
	 * Handles AdministrationUpgradeCheckEvent's received by this class.
	 * 
	 * Implements the AdministrationUpgradeCheckEvent.Handler.onAdministrationUpgradeCheck() method.
	 * 
	 * @param event
	 */
	@Override
	public void onAdministrationUpgradeCheck( AdministrationUpgradeCheckEvent event )
	{
		showUpgradeTasks();
	}// end onAdministrationUpgradeCheck()
	
	/**
	 * Callback interface to interact with the admin control
	 * asynchronously after it loads. 
	 */
	public interface AdminControlClient {
		void onSuccess(AdminControl adminCtrl);
		void onUnavailable();
	}

	/**
	 * Loads the AdminControl split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param adminCtrlClient
	 */
	public static void createAsync( final AdminControlClient adminCtrlClient )
	{
		GWT.runAsync( AdminControl.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess()
			{
				AdminControl adminCtrl = new AdminControl();
				adminCtrlClient.onSuccess( adminCtrl );
			}// end onSuccess()
			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_AdminControl() );
				adminCtrlClient.onUnavailable();
			}// end onFailure()
		} );
	}// end createAsync()
}// end AdminControl

/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.admin.AdminAction;
import org.kablink.teaming.gwt.client.admin.GwtAdminAction;
import org.kablink.teaming.gwt.client.admin.GwtAdminCategory;
import org.kablink.teaming.gwt.client.admin.GwtUpgradeInfo;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.ActionRequestor;
import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.TeamingAction;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;


/**
 * This widget will display the controls that make up the "Administration" control.
 * There is a widget that displays the list of administration actions and a widget
 * that displays the page for the selected administration action.
 */
public class AdminControl extends Composite
	implements ActionRequestor, ActionTrigger 
{
	private AsyncCallback<GwtUpgradeInfo> m_rpcGetUpgradeInfoCallback = null;
	private List<ActionHandler> m_actionHandlers = new ArrayList<ActionHandler>();
	private AdminActionsTreeControl m_adminActionsTreeControl = null;
	private ContentControl m_contentControl = null;
	private AdminInfoDlg m_adminInfoDlg = null;

	/**
	 * Class used for the ui of the "Administration Information" dialog
	 */
	public class AdminInfoDlg extends DlgBox
	{
		private Button m_closeBtn;
		private FlexTable m_table;
		
		/**
		 * 
		 */
		public AdminInfoDlg(
			boolean autoHide,
			boolean modal,
			int xPos,
			int yPos )
		{
			super( autoHide, modal, xPos, yPos );
		
			String headerText;
			
			// Create the header, content and footer of this dialog box.
			headerText = GwtTeaming.getMessages().adminInfoDlgHeader();
			createAllDlgContent( headerText, null, null, null ); 
		}// end LoginDlg()
		
		/**
		 * Create all the controls that make up the dialog box.
		 */
		public Panel createContent( Object props )
		{
			FlowPanel mainPanel = null;
			
			mainPanel = new FlowPanel();
			mainPanel.setStyleName( "teamingDlgBoxContent" );
			
			m_table = new FlexTable();
			m_table.setCellSpacing( 4 );
			m_table.addStyleName( "dlgContent" );
			
			// The content of the dialog will be created in refreshContent() which will be called
			// when we get the GwtUpgradeInfo object from the server.
			
			mainPanel.add( m_table );

			init( props );

			return mainPanel;
		}// end createContent()
		
		
		/*
		 * Override the createFooter() method so we can control what buttons are in the footer.
		 */
		public Panel createFooter()
		{
			FlowPanel panel;
			
			panel = new FlowPanel();
			
			// Associate this panel with its stylesheet.
			panel.setStyleName( "teamingDlgBoxFooter" );
			
			m_closeBtn = new Button( GwtTeaming.getMessages().close() );
			m_closeBtn.addClickHandler( this );
			m_closeBtn.addStyleName( "teamingButton" );
			panel.add( m_closeBtn );

			return panel;
		}// end createFooter()
		
		
		/**
		 * 
		 */
		public Object getDataFromDlg()
		{
			// Nothing to do.
			return new Object();
		}// end getDataFromDlg()
		
		
		/**
		 *  
		 */
		public FocusWidget getFocusWidget()
		{
			return null;
		}// end getFocusWidget()

		
		/**
		 * Initialize the controls in the dialog with the values from the given GwtUpgradeInfo object.
		 */
		public void init( Object props )
		{
			// Nothing to do.
		}// end init()

		
		/*
		 * This method gets called when the user clicks on a button in the footer.
		 */
		public void onClick( ClickEvent event )
		{
			Object	source;
			
			// Get the object that was clicked on.
			source = event.getSource();
			
			// Did the user click on close?
			if ( source == m_closeBtn )
			{
				// Yes
				hide();
			}
		}// end onClick()

		/**
		 * Refresh the content of this dialog with the new information found in the given GwtUpgradeInfo object.
		 */
		public void refreshContent( GwtUpgradeInfo upgradeInfo )
		{
			int row = 0;
			FlexTable.FlexCellFormatter cellFormatter; 

			// Clear any existing content.
			m_table.clear();
			
			cellFormatter = m_table.getFlexCellFormatter();

			// Add a row for the Teaming release information
			{
				m_table.setText( row, 0, GwtTeaming.getMessages().adminInfoDlgRelease() );
				cellFormatter.setWordWrap( row, 0, false );
				m_table.setText( row, 1, upgradeInfo.getReleaseInfo() );
				cellFormatter.setWordWrap( row, 1, false );
				m_table.setText( row, 2, " " );
				
				++row;
			}
			
			// Are there upgrade tasks that need to be performed?
			if ( upgradeInfo.doUpgradeTasksExist() )
			{
				// Yes
				
				// Add text to let the user know there are upgrade tasks that need to be completed.
				++row;
				cellFormatter.setColSpan( row, 0, 2 );
				cellFormatter.setWordWrap( row, 0, false );
				m_table.setText( row, 0, GwtTeaming.getMessages().adminInfoDlgUpgradeTasksNotDone() );
				++row;

				// Are we dealing with the "admin" user?
				if ( upgradeInfo.getIsAdmin() )
				{
					ArrayList<GwtUpgradeInfo.UpgradeTask> upgradeTasks;
					
					// Yes
					// Get the list of upgrade tasks
					upgradeTasks = upgradeInfo.getUpgradeTasks();
					
					if ( upgradeTasks != null && upgradeTasks.size() > 0 )
					{
						UListElement uList;
						
						uList = Document.get().createULElement();
					
						// Display a message for each upgrade task.
						for ( GwtUpgradeInfo.UpgradeTask task : upgradeTasks )
						{
							String taskInfo;
							
							taskInfo = null;
							switch ( task )
							{
							case UPGRADE_ACCESS_CONTROLS:
								taskInfo = GwtTeaming.getMessages().adminInfoDlgUpgradeAccessControls();
								break;
								
							case UPGRADE_DEFINITIONS:
								taskInfo = GwtTeaming.getMessages().adminInfoDlgUpgradeDefinitions();
								break;
								
							case UPGRADE_SEARCH_INDEX:
								taskInfo = GwtTeaming.getMessages().adminInfoDlgUpgradeSearchIndex();
								break;
								
							case UPGRADE_TEMPLATES:
								taskInfo = GwtTeaming.getMessages().adminInfoDlgUpgradeTemplates();
								break;
							}
							
							if ( taskInfo != null )
							{
								LIElement liElement;

								liElement = Document.get().createLIElement();
								liElement.setInnerText( taskInfo );
								
								uList.appendChild( liElement );
							}
						}
						
						cellFormatter.setColSpan( row, 0, 2 );
						cellFormatter.setWordWrap( row, 0, false );
						m_table.setHTML( row, 0, uList.getString() );
					}
				}
				else
				{
					cellFormatter.setColSpan( row, 0, 2 );
					cellFormatter.setWordWrap( row, 0, false );
					m_table.setText( row, 0, GwtTeaming.getMessages().adminInfoDlgLoginAsAdmin() );
					++row;
				}
			}
		}// end refreshContent()
	}// end AdminInfoDlg
	
	
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
			
			// Remember the action we are associated with.
			m_adminAction = adminAction;
			
			mainPanel = new FlowPanel();
			
			m_actionName = new InlineLabel( adminAction.getLocalizedName() );
			m_actionName.addClickHandler( this );
			m_actionName.addMouseOverHandler( this );
			m_actionName.addMouseOutHandler( this );
			m_actionName.addStyleName( "adminActionControl" );
			m_actionName.addStyleName( "cursorPointer" );
			
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
					img.addStyleName( "margin-right-5" );
					img.addClickHandler( new ClickHandler()
					{
						public void onClick( ClickEvent event  )
						{
							// Issue an ajax request to get the upgrade information from the server.
							// When we get the response the callback will open the AdminInfoDlg.
							getUpgradeInfoFromServer( m_rpcGetUpgradeInfoCallback2 );
						}// end onClick()
					}
					);
					table.setWidget( 0, 1, img );
					cellFormatter.setHorizontalAlignment( 0, 1, HasHorizontalAlignment.ALIGN_RIGHT );
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
						GwtTeaming.getMessages().rpcFailure_GetUpgradeInfo() );
				}// end onFailure()
		
				/**
				 * 
				 * @param result
				 */
				public void onSuccess( GwtUpgradeInfo upgradeInfo )
				{
					// Show the AdminInfoDlg
					showAdminInfoDlg( upgradeInfo );
				}// end onSuccess()
			};

			// Issue a deferred command to get the administration actions the user has rights to run.
			{
				Command cmd;
				
		        cmd = new Command()
		        {
		        	/**
		        	 * 
		        	 */
		            public void execute()
		            {
						getAdminActionsFromServer();
		            }
		        };
		        DeferredCommand.addCommand( cmd );
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
			rpcService.getAdminActions( new HttpRequestInfo(), binderId, m_rpcGetAdminActionsCallback );
		}// end getAdminActionsFromServer()
	}// end AdminActionsTreeControl

	
	/**
	 * 
	 */
	public AdminControl()
	{
		FlowPanel mainPanel;

		mainPanel = new FlowPanel();
		mainPanel.addStyleName( "adminControl" );
		
		// Create the control that holds all of the administration actions
		m_adminActionsTreeControl = new AdminActionsTreeControl();
		mainPanel.add( m_adminActionsTreeControl );
		
		// Create a control to hold the administration page for the selection administration action.
		m_contentControl = new ContentControl( "adminContentControl" );
		m_contentControl.addStyleName( "adminContentControl" );
		mainPanel.add( m_contentControl );
		
		// Create the callback that will be used when we issue an ajax call to get upgrade information
		m_rpcGetUpgradeInfoCallback = new AsyncCallback<GwtUpgradeInfo>()
		{
			/**
			 * 
			 */
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					GwtTeaming.getMessages().rpcFailure_GetUpgradeInfo() );
			}// end onFailure()
	
			/**
			 * 
			 * @param result
			 */
			public void onSuccess( GwtUpgradeInfo upgradeInfo )
			{
				// Are there upgrade tasks that need to be performed?
				if ( upgradeInfo.doUpgradeTasksExist() )
				{
					// Yes, invoke the AdminInfoDlg.
					showAdminInfoDlg( upgradeInfo );
				}
			}// end onSuccess()
		};

		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}// end AdminControl()
	
	

	/**
	 * Called to add an ActionHandler to this AdminControl.
	 * 
	 * Implements the ActionRequestor.addActionHandler() interface method.
	 * 
	 * @param actionHandler
	 */
	public void addActionHandler( ActionHandler actionHandler )
	{
		m_actionHandlers.add( actionHandler );
	}// end addActionHandler()


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
				Command cmd;

				// Clear the iframe's content 
				m_contentControl.clear();
				
				// Set the iframe's content to the selected administration page.
				m_contentControl.setUrl( url );
				
				cmd = new Command()
				{
					public void execute()
					{
						showContentPanel();
					}
				};
				DeferredCommand.addCommand( cmd );
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
	 * Issue an ajax request to get the upgrade information from the server.
	 */
	public void getUpgradeInfoFromServer( AsyncCallback<GwtUpgradeInfo> callback )
	{
		GwtRpcServiceAsync rpcService;
		
		rpcService = GwtTeaming.getRpcService();
		
		// Issue an ajax request to get the upgrade information
		rpcService.getUpgradeInfo(  callback );
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
		Command cmd;
		
		cmd = new Command()
		{
			public void execute()
			{
				relayoutPageNow();
			}
		};
		DeferredCommand.addCommand( cmd );
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
	public void showAdminInfoDlg( GwtUpgradeInfo upgradeInfo )
	{
		// Invoke the "Administration Information" dialog.
		if ( m_adminInfoDlg == null )
		{
			int x;
			int y;
			
			x = m_adminActionsTreeControl.getAbsoluteLeft() + m_adminActionsTreeControl.getOffsetWidth();
			y = m_adminActionsTreeControl.getAbsoluteTop();
			m_adminInfoDlg = new AdminInfoDlg( false, true, x, y );
		}
		
		m_adminInfoDlg.refreshContent( upgradeInfo );
		m_adminInfoDlg.show();
	}// end showAdminInfoDlg()
	
	
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
		Command cmd;
		
		// Set the position of the content control.
        cmd = new Command()
        {
        	/**
        	 * 
        	 */
            public void execute()
            {
            	Command cmd2;
            	
				relayoutPage();
				
				cmd2 = new Command()
				{
					public void execute()
					{
						setVisible( true );
					}
				};
				DeferredCommand.addCommand( cmd2 );
            }
        };
        DeferredCommand.addCommand( cmd );		

		// Issue an ajax request to get the upgrade information from the server.
        cmd = new Command()
        {
        	/**
        	 * 
        	 */
            public void execute()
            {
            	// When we get the upgrade info from the server our callback will check to
            	// see if upgrade tasks exists.  If they do, the callback will invoke the
            	// AdminInfoDlg which will show the user the tasks they need to do.
				getUpgradeInfoFromServer( m_rpcGetUpgradeInfoCallback );
            }
        };
        DeferredCommand.addCommand( cmd );		
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
		// Scan the ActionHandler's that have been registered...
		for ( Iterator<ActionHandler> ahIT = m_actionHandlers.iterator(); ahIT.hasNext(); )
		{
			// ...firing the action at each.
			ahIT.next().handleAction(action, obj);
		}
	}// end triggerAction()
	
	/**
	 * 
	 */
	public void triggerAction( TeamingAction action )
	{
		// Always use the initial form of the method.
		triggerAction( action, null );
	}// end triggerAction()
	
}// end AdminControl

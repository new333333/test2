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
import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.admin.AdminAction;
import org.kablink.teaming.gwt.client.admin.GwtAdminAction;
import org.kablink.teaming.gwt.client.admin.GwtAdminCategory;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.ActionRequestor;
import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.TeamingAction;

import com.google.gwt.dom.client.Style;
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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;


/**
 * This widget will display the controls that make up the "Administration" control.
 * There is a widget that displays the list of administration actions and a widget
 * that displays the page for the selected administration action.
 */
public class AdminControl extends Composite
	implements ActionRequestor, ActionTrigger 
{
	private List<ActionHandler> m_actionHandlers = new ArrayList<ActionHandler>();
	private AdminActionsTreeControl m_adminActionsTreeControl = null;
	private ContentControl m_contentControl = null;

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
		}// end showActions()
	}// end AdminCategoryControl
	
	
	/**
	 * 
	 */
	private class AdminActionsTreeControl extends Composite
	{
		// m_rpcGetAdminActionsCallback is our callback that gets called when the ajax request to get the administration actions completes.
		private AsyncCallback<ArrayList<GwtAdminCategory>> m_rpcGetAdminActionsCallback = null;
		
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
			
			mainPanel.add( m_mainTable );
			
			// Create the callback that will be used when we issue an ajax call to get the administration actions.
			m_rpcGetAdminActionsCallback = new AsyncCallback<ArrayList<GwtAdminCategory>>()
			{
				/**
				 * 
				 */
				public void onFailure( Throwable t )
				{
					String cause;
					
					cause = t.getLocalizedMessage();
					if ( cause == null )
						cause = t.toString();
					
					Window.alert( cause );
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
			rpcService.getAdminActions( binderId, m_rpcGetAdminActionsCallback );
		}// end getAdminActionsFromServer()
	}// end AdminActionsTreeControl

	
	/**
	 * 
	 */
	public AdminControl()
	{
		FlowPanel mainPanel;
		Command cmd;

		mainPanel = new FlowPanel();
		mainPanel.addStyleName( "adminControl" );
		
		// Create the control that holds all of the administration actions
		m_adminActionsTreeControl = new AdminActionsTreeControl();
		mainPanel.add( m_adminActionsTreeControl );
		
		// Create a control to hold the administration page for the selection administration action.
		m_contentControl = new ContentControl( "adminContentControl" );
		m_contentControl.addStyleName( "adminContentControl" );
		mainPanel.add( m_contentControl );
		
		// Set the position of the content control.
        cmd = new Command()
        {
        	/**
        	 * 
        	 */
            public void execute()
            {
				relayoutPage();
            }
        };
        DeferredCommand.addCommand( cmd );		

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
				m_contentControl.setUrl( url );
				showContentPanel();
			}
		}
	}// end adminActionSelected()
	
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
			
			height = clientHeight - m_contentControl.getAbsoluteTop() - 20;
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
	public void showContentPanel()
	{
		m_contentControl.setVisible( true );	
	}// end showContentPanel()
	
	
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

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

import org.kablink.teaming.gwt.client.GwtBrandingData;
import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.admin.GwtAdminAction;
import org.kablink.teaming.gwt.client.admin.GwtAdminCategory;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;


/**
 * This widget will display the controls that make up the "Administration" control.
 * There is a widget that displays the list of administration options and a widget
 * that displays the page for the selected administration option.
 */
public class AdminControl extends Composite
{
	private AdminOptionsControl m_adminOptionsControl = null;
	private ContentControl m_contentControl = null;

	
	/**
	 * 
	 */
	private class AdminOptionsControl extends Composite
	{
		// m_rpcGetAdminActionsCallback is our callback that gets called when the ajax request to get the administration actions completes.
		private AsyncCallback<ArrayList<GwtAdminCategory>> m_rpcGetAdminActionsCallback = null;
		
		// m_adminCategories holds the list of administration actions grouped into categories.
		private ArrayList<GwtAdminCategory> m_adminCategories;
		
		/**
		 * 
		 */
		public AdminOptionsControl()
		{
			FlowPanel mainPanel;
			
			mainPanel = new FlowPanel();
			mainPanel.addStyleName( "adminOptionsControl" );
			mainPanel.add( new Label( "This is the Admin Options Control" ) );
			
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
					int i;
					
					// Update the list of administration actions.
					m_adminCategories = adminCategories;
					
					for (i = 0; i < adminCategories.size(); ++i)
					{
						GwtAdminCategory category;
						ArrayList<GwtAdminAction> actions = null;
						
						category = adminCategories.get( i );
						Window.alert( category.getLocalizedName() );
						
						actions = category.getActions();
						if ( actions != null )
						{
							int j;
							
							for (j = 0; j < actions.size(); ++j)
							{
								GwtAdminAction action;
								
								action = actions.get( j );
								Window.alert( action.getLocalizedName() );
							}
						}
					}
				}// end onSuccess()
			};

			// Issue a deferred command to get the administration options the user has rights to run.
			{
				Command cmd;
				
		        cmd = new Command()
		        {
		        	/**
		        	 * 
		        	 */
		            public void execute()
		            {
						getAdminOptionsFromServer();
		            }
		        };
		        DeferredCommand.addCommand( cmd );
				
			}
			
			initWidget( mainPanel );
		}// end AdminOptionsControl()
		
		
		/**
		 * Issue an ajax request to get the list of administration options the user
		 * has rights to run.
		 */
		public void getAdminOptionsFromServer()
		{
			GwtRpcServiceAsync rpcService;
			String binderId;
			
			rpcService = GwtTeaming.getRpcService();
			
			// Issue an ajax request to get the administration actions the user has rights to perform.
			binderId = GwtMainPage.m_requestInfo.getBinderId();
			rpcService.getAdminActions( binderId, m_rpcGetAdminActionsCallback );
		}// end getAdminOptionsFromServer()
	}// end AdminOptionsControl

	
	/**
	 * 
	 */
	public AdminControl()
	{
		FlowPanel mainPanel;

		mainPanel = new FlowPanel();
		mainPanel.addStyleName( "adminControl" );
		
		// Create the control that holds all of the administration options
		m_adminOptionsControl = new AdminOptionsControl();
		mainPanel.add( m_adminOptionsControl );
		
		// Create a control to hold the administration page for the selection administration option.
		m_contentControl = new ContentControl( "adminContentControl" );
		m_contentControl.addStyleName( "adminContentControl" );
		m_contentControl.setVisible( false );
		mainPanel.add( m_contentControl );

		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}// end AdminControl()
	
	
}// end AdminControl

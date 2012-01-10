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

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.binderviews.landingpage.LandingPageView;
import org.kablink.teaming.gwt.client.lpe.ConfigData;
import org.kablink.teaming.gwt.client.lpe.LandingPageProperties;
import org.kablink.teaming.gwt.client.rpc.shared.GetInheritedLandingPagePropertiesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Panel;


/**
 * 
 * @author jwootton
 *
 */
public class PreviewLandingPageDlg extends DlgBox
{
	private VibeFlowPanel m_mainPanel = null;
	private AsyncCallback<VibeRpcResponse> m_rpcCallback = null;
	
	/**
	 * 
	 */
	public PreviewLandingPageDlg(
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos )
	{
		super( autoHide, modal, xPos, yPos, DlgButtonMode.Close );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().previewLandingPageDlgHeader(), editSuccessfulHandler, editCanceledHandler, null ); 
	}
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	public Panel createContent( Object props )
	{
		m_mainPanel = new VibeFlowPanel();
		m_mainPanel.setStyleName( "previewLandingPageDlgContent" );
		
		return m_mainPanel;
	}
	
	
	/**
	 * Nothing to return
	 */
	public Object getDataFromDlg()
	{
		// Nothing to do.
		return new Object();
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	public FocusWidget getFocusWidget()
	{
		return null;
	}
	
	/**
	 * Issue an rpc request to get the inherited landing page properties
	 */
	private void getInheritedLandingPageProperties( final ConfigData lpData )
	{
		GetInheritedLandingPagePropertiesCmd cmd;
		
		if ( m_rpcCallback == null )
		{
			m_rpcCallback = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetInheritedLandingPageProperties(),
						lpData.getBinderId() );
				}
		
				/**
				 * 
				 * @param result
				 */
				public void onSuccess( VibeRpcResponse response )
				{
					final LandingPageProperties lpProperties;
					
					lpProperties = (LandingPageProperties) response.getResponseData();
					
					if ( lpProperties != null )
					{
						Scheduler.ScheduledCommand cmd;

						cmd = new Scheduler.ScheduledCommand()
						{
							public void execute()
							{
								LandingPageView lp;
								
								// Create a Landing Page widget from the inherited properties
								lpData.initLandingPageProperties( lpProperties );
								lp = new LandingPageView( lpData );
								m_mainPanel.add( lp );
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				}
			};
		}
		
		// Issue an rpc request to get the inherited landing page properties.
		cmd = new GetInheritedLandingPagePropertiesCmd( lpData.getBinderId() );
		GwtClientHelper.executeCommand( cmd, m_rpcCallback );
	}
	
	
	/**
	 * Create a LandingPage widget from the given data.
	 */
	public void init( ConfigData lpData )
	{
		LandingPageView lp;
		
		m_mainPanel.clear();
		
		// Does this landing page inherit its properties?
		if ( lpData.getInheritProperties() )
		{
			// Yes, issue an rpc request to get the inherited landing page properties
			getInheritedLandingPageProperties( lpData );
		}
		else
		{
			// No
			lp = new LandingPageView( lpData );
			m_mainPanel.add( lp );
		}
	}
}

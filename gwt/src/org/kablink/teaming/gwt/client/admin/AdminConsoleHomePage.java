/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.admin;


import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.AdminControl;
import org.kablink.teaming.gwt.client.widgets.AdminInfoDlg;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;

/**
 * Class that is used as the main page for the administration console.
 * 
 * @author jwootton@novell.com
 */
public class AdminConsoleHomePage extends ResizeComposite
{
	private GwtUpgradeInfo m_adminInfo;
	private VibeFlowPanel m_mainPanel;	// The panel containing everything about the composite.

	
	/**
	 * This widget is used to display administration information
	 */
	public class AdminInfoWidget extends Composite
	{
		/**
		 * 
		 */
		public AdminInfoWidget()
		{
			VibeFlowPanel mainPanel;
			GwtTeamingMessages messages;
			
			mainPanel = new VibeFlowPanel();
			mainPanel.addStyleName( "AdminConsoleInfoWidget_mainPanel" );
			
			messages = GwtTeaming.getMessages();
			
			// Create the header icon
			{
				VibeFlowPanel panel;
				ImageResource imgResource;
				Image img;
				
				panel = new VibeFlowPanel();
				panel.addStyleName( "AdminConsoleInfoWidget_headImage" );
				
				imgResource = GwtTeaming.getImageBundle().adminConsoleHomePage();
				img = GwtClientHelper.buildImage( imgResource.getSafeUri().asString() );
				panel.add( img );

				mainPanel.add( panel );
			}
			
			// Create the header text
			{
				VibeFlowPanel panel;
				String header;
				
				panel = new VibeFlowPanel();
				panel.addStyleName( "AdminConsoleInfoWidget_header" );
				if ( GwtClientHelper.isLicenseFilr() )
					header = messages.adminConsoleInfoWidget_FilrHeader();
				else if ( GwtClientHelper.isLicenseKablink() )
					header = messages.adminConsoleInfoWidget_KablinkHeader();
				else
					header = messages.adminConsoleInfoWidget_NovellHeader();
				panel.getElement().setInnerText( header );

				mainPanel.add( panel );
			}
			
			// Add the Release information
			{
				VibeFlowPanel panel;
				InlineLabel label;
				
				panel = new VibeFlowPanel();
				panel.addStyleName( "AdminConsoleInfoWidget_subhead" );
				panel.getElement().setInnerHTML( messages.adminConsoleInfoWidget_ReleaseLabel() + "&nbsp;" );
				
				label = new InlineLabel( m_adminInfo.getNameAndVersion() );
				label.addStyleName( "AdminConsoleInfoWidget_releaseInfo" );
				panel.add( label );
				
				mainPanel.add( panel );
			}
			
			// Add the Build information
			{
				VibeFlowPanel panel;
				InlineLabel label;
				
				panel = new VibeFlowPanel();
				panel.addStyleName( "AdminConsoleInfoWidget_subhead" );
				panel.getElement().setInnerHTML( messages.adminConsoleInfoWidget_BuildLabel() + "&nbsp;" );
				
				label = new InlineLabel( m_adminInfo.getBuild() );
				label.addStyleName( "AdminConsoleInfoWidget_buildInfo" );
				panel.add( label );
				
				mainPanel.add( panel );
			}
			
			// Are we running Filr
			if ( GwtClientHelper.isLicenseFilr() )
			{
				VibeFlowPanel panel;
				InlineLabel label;
				
				// Yes, add the Filr appliance information
				panel = new VibeFlowPanel();
				panel.addStyleName( "AdminConsoleInfoWidget_info" );
				panel.addStyleName( "AdminConsoleInfoWidget_filrApplianceInfo" );
				
				label = new InlineLabel( m_adminInfo.getFilrApplianceReleaseInfo() );
				label.addClickHandler( new ClickHandler()
				{
					@Override
					public void onClick( ClickEvent event )
					{
						invokeFilrApplianceAdmin();
					}
				} );
				panel.add( label );
				
				mainPanel.add( panel );
			}
			
			// Add text describing the admin console
			{
				VibeFlowPanel panel;
				String text;
				
				panel = new VibeFlowPanel();
				panel.addStyleName( "AdminConsoleInfoWidget_info" );
				if ( GwtClientHelper.isLicenseFilr() )
					text = messages.adminConsoleInfoWidget_GeneralInfoFilr();
				else
					text = messages.adminConsoleInfoWidget_GeneralInfoVibe();
				panel.getElement().setInnerText( text );
				
				mainPanel.add( panel );
			}
			
			// Add a link to the administration guide.
			{
				VibeFlowPanel panel;
				Label label;
				
				panel = new VibeFlowPanel();
				panel.addStyleName( "AdminConsoleInfoWidget_info" );
				panel.getElement().setInnerText( messages.adminConsoleInfoWidget_SeeAdminGuide() );
				
				label = new Label( messages.adminConsoleInfoWidget_AdminGuideLabel() );
				label.addStyleName( "AdminConsoleInfoWidget_adminGuideLink" );
				label.addClickHandler( new ClickHandler()
				{
					@Override
					public void onClick( ClickEvent event )
					{
						Scheduler.ScheduledCommand cmd;
						
						cmd = new Scheduler.ScheduledCommand()
						{
							@Override
							public void execute()
							{
								showAdminGuide();
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				} );
				panel.add( label );
				
				mainPanel.add( panel );
			}

			// Add the administrative tasks that need to be performed.
			{
				FlexTable table;

				table = new FlexTable();
				table.addStyleName( "AdminConsoleInfoWidget_tasksToDo" );
				table.setCellSpacing( 4 );
				
				mainPanel.add( table );

				// Create the ui that displays that tasks that need to be completed.
				AdminInfoDlg.createTasksToDoUI( m_adminInfo, table, 0, true, "AdminConsoleInfoWidget_tasksToDo_td" );
			}

			// All composites must call initWidget() in their constructors.
			initWidget( mainPanel );
		}
		
		/**
		 * 
		 */
		private void invokeFilrApplianceAdmin()
		{
			String host;
			
			host = Window.Location.getHostName();
			if ( host != null && host.length() > 0 )
			{
				String url;

				url = "https://" + host + ":9443/";
				Window.open( url, "filr_appliance_admin", "resizeable,scrollbars" );
			}
		}
		
		/**
		 * 
		 */
		private void showAdminGuide()
		{
			HelpData helpData;
			
			helpData = new HelpData();
			helpData.setGuideName( HelpData.ADMIN_GUIDE );

			Window.open(
					helpData.getUrl(),
					"teaming_help_window",
					"resizeable,scrollbars" );
		}
	}
	
	

	/**
	 * Constructor method.
	 * 
	 * @param ct
	 */
	public AdminConsoleHomePage()
	{
		// Initialize the super class...
		super();
		
		m_mainPanel = new VibeFlowPanel();
		m_mainPanel.addStyleName( "AdminConsoleHomePage_mainPanel" );
		
		initWidget( m_mainPanel );
	}

	/**
	 * 
	 */
	public void init( GwtUpgradeInfo adminInfo )
	{
		m_adminInfo = adminInfo;
		
		m_mainPanel.clear();
		
		if ( m_adminInfo != null )
		{
			AdminInfoWidget infoWidget;
			
			infoWidget = new AdminInfoWidget();
			m_mainPanel.add( infoWidget );
		}
	}

	/**
	 * Manages resizing the view.
	 * 
	 * Overrides the RequiresResize.onResize() method.
	 */
	@Override
	public void onResize()
	{
		super.onResize();
	
		setViewSize();
	}

	/**
	 * Sets the size of the view based on the MainContentLayoutPanel
	 * that holds it.
	 */
	public void setViewSize() 
	{
		AdminControl adminControl;
		
		adminControl = GwtTeaming.getMainPage().getAdminControl();
		setPixelSize( adminControl.getContentWidth(), adminControl.getContentHeight() );
	}
}

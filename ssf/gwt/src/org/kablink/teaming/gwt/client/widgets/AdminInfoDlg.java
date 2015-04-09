/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.admin.GwtEnterProxyCredentialsTask;
import org.kablink.teaming.gwt.client.admin.GwtFilrAdminTask;
import org.kablink.teaming.gwt.client.admin.GwtSelectNetFolderServerTypeTask;
import org.kablink.teaming.gwt.client.admin.GwtUpgradeInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Class used for the UI of the 'Administration Information' dialog.
 * 
 * @author jwootton
 */
public class AdminInfoDlg extends DlgBox
{
	private FlexTable m_table;
	private FlowPanel m_mainPanel = null;
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private AdminInfoDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos )
	{
		super( autoHide, modal, xPos, yPos, DlgButtonMode.Close );
	
		String headerText;
		
		// Create the header, content and footer of this dialog box.
		headerText = GwtTeaming.getMessages().adminInfoDlgHeader();
		createAllDlgContent( headerText, null, null, null ); 
	}
	
	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		m_mainPanel = new FlowPanel();
		m_mainPanel.setStyleName( "teamingDlgBoxContent" );
		
		m_table = new FlexTable();
		m_table.setCellSpacing( 4 );
		m_table.addStyleName( "dlgContent" );
		
		// The content of the dialog will be created in refreshContent() which will be called
		// when we get the GwtUpgradeInfo object from the server.
		
		m_mainPanel.add( m_table );

		init( props );

		return m_mainPanel;
	}
	

	/**
	 * Create the ui that displays the admin tasks that need to be completed
	 */
	public static void createTasksToDoUI(
		GwtUpgradeInfo upgradeInfo,
		FlexTable table,
		int row,
		boolean wordWrap,
		String tdStyleName )
	{
		FlexTable.FlexCellFormatter cellFormatter; 

		cellFormatter = table.getFlexCellFormatter();
		
		// Has the license expired?
		if ( upgradeInfo.getIsLicenseExpired() )
		{
			FlowPanel panel;
			FlexTable licenseTable;
			Image img;
			InlineLabel label;
			String productName;
			String txt;
			
			// Yes
			licenseTable = new FlexTable();
			
			panel = new FlowPanel();
			licenseTable.setWidget( 0, 1, panel );
			
			img = new Image( GwtTeaming.getImageBundle().expiredLicenseIcon16() );
			img.getElement().setAttribute( "align", "absmiddle" );
			licenseTable.setWidget( 0, 0, img );
			
			if ( GwtTeaming.m_requestInfo.isLicenseFilr() )
				productName = GwtTeaming.getMessages().novellFilr();
			else if ( GwtMainPage.m_novellTeaming )
				productName = GwtTeaming.getMessages().novellTeaming();
			else
				productName = GwtTeaming.getMessages().kablinkTeaming();
			txt = " " + GwtTeaming.getMessages().adminInfoDlgExpiredLicense( productName );
			label = new InlineLabel();
			label.getElement().setInnerHTML( txt );
			panel.add( label );
			
			// Add a link to the documentation that describes how to update the license
			{
				ClickHandler clickHandler;
				Label doc;
				
				doc = new Label( GwtTeaming.getMessages().adminInfoDlgDocumentationLink() );
				doc.addStyleName( "adminInfoDlg_docLink" );
				panel.add( doc );

				// Add a click handler for the link to the documentation.
				clickHandler = new ClickHandler()
				{
					@Override
					public void onClick( ClickEvent clickEvent )
					{
						GwtClientHelper.deferCommand( new ScheduledCommand()
						{
							@Override
							public void execute()
							{
								HelpData helpData;
								
								helpData = new HelpData();
								helpData.setGuideName( HelpData.ADMIN_GUIDE );
								helpData.setPageId( "license" );
								
								GwtClientHelper.invokeHelp( helpData );
							}
						} );
					}
				};
				doc.addClickHandler( clickHandler );
			}
			
			cellFormatter.setColSpan( row, 0, 2 );
			cellFormatter.setWordWrap( row, 0, wordWrap );
			if ( tdStyleName != null )
				cellFormatter.addStyleName( row, 0, tdStyleName );
			table.setWidget( row, 0, licenseTable );
			++row;
		}
		
		// Are there upgrade tasks that need to be performed?
		if ( upgradeInfo.doUpgradeTasksExist() )
		{
			// Yes
			// Add text to let the user know there are upgrade tasks that need to be completed.
			{
				FlowPanel panel;
				Image img;
				InlineLabel label;
				
				panel = new FlowPanel();
				
				img = new Image( GwtTeaming.getImageBundle().warningIcon16() );
				img.getElement().setAttribute( "align", "absmiddle" );
				panel.add( img );
				
				label = new InlineLabel( " " + GwtTeaming.getMessages().adminInfoDlgUpgradeTasksNotDone() );
				panel.add( label );
				
				row += 3;
				cellFormatter.setColSpan( row, 0, 2 );
				cellFormatter.setWordWrap( row, 0, wordWrap );
				if ( tdStyleName != null )
					cellFormatter.addStyleName( row, 0, tdStyleName );
				table.setHTML( row, 0, panel.getElement().getInnerHTML() );
				++row;
			}

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
					uList.getStyle().setMarginTop( 0, Unit.PX );
					uList.getStyle().setMarginBottom( 0, Unit.PX );
				
					// Display a message for each upgrade task.
					for ( GwtUpgradeInfo.UpgradeTask task : upgradeTasks )
					{
						String taskInfo;
						
						taskInfo = null;
						switch ( task )
						{
						case UPGRADE_DEFINITIONS:
							taskInfo = GwtTeaming.getMessages().adminInfoDlgUpgradeDefinitions();
							break;
							
						case UPGRADE_SEARCH_INDEX:
							taskInfo = GwtTeaming.getMessages().adminInfoDlgUpgradeSearchIndex();
							break;
							
						case UPGRADE_TEMPLATES:
							taskInfo = GwtTeaming.getMessages().adminInfoDlgUpgradeTemplates();
							break;
							
						case UPGRADE_IMPORT_TYPELESS_DN:
							taskInfo = GwtTeaming.getMessages().adminInfoDlgUpgradeImportTypelessDN();
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
					cellFormatter.setWordWrap( row, 0, wordWrap );
					if ( tdStyleName != null )
						cellFormatter.addStyleName( row, 0, tdStyleName );
					table.setHTML( row, 0, uList.getString() );
				}
			}
			else
			{
				cellFormatter.setColSpan( row, 0, 2 );
				cellFormatter.setWordWrap( row, 0, wordWrap );
				if ( tdStyleName != null )
					cellFormatter.addStyleName( row, 0, tdStyleName );
				table.setText( row, 0, GwtTeaming.getMessages().adminInfoDlgLoginAsAdmin() );
				++row;
			}
		}
		
		// Are we running Filr?
		if ( GwtTeaming.m_requestInfo.isLicenseFilr() )
		{
			ArrayList<GwtFilrAdminTask> listOfTasks;
			
			// Yes
			// Are there any tasks the admin needs to do?
			listOfTasks = upgradeInfo.getFilrAdminTasks();
			if ( listOfTasks != null && listOfTasks.size() > 0 )
			{
				UListElement uList;
				FlowPanel panel;
				Image img;
				InlineLabel label;
				
				// Yes
				panel = new FlowPanel();
				
				img = new Image( GwtTeaming.getImageBundle().warningIcon16() );
				img.getElement().setAttribute( "align", "absmiddle" );
				panel.add( img );
				
				label = new InlineLabel( " " + GwtTeaming.getMessages().adminInfoDlgFilrTasksToBeCompleted() );
				panel.add( label );
				
				uList = Document.get().createULElement();
				uList.getStyle().setMarginTop( 0, Unit.PX );
				uList.getStyle().setMarginBottom( 0, Unit.PX );
			
				// Add text to let the user know there are Filr tasks that need to be completed.
				row += 3;
				cellFormatter.setColSpan( row, 0, 2 );
				cellFormatter.setWordWrap( row, 0, wordWrap );
				if ( tdStyleName != null )
					cellFormatter.addStyleName( row, 0, tdStyleName );
				table.setHTML( row, 0, panel.getElement().getInnerHTML() );
				++row;

				// Display a message for each task.
				for ( GwtFilrAdminTask nextTask : listOfTasks )
				{
					if ( nextTask instanceof GwtEnterProxyCredentialsTask )
					{
						GwtEnterProxyCredentialsTask tmpTask;
						LIElement liElement;
						String txt;
						
						panel = new FlowPanel();
						
						tmpTask = (GwtEnterProxyCredentialsTask) nextTask;
						txt = GwtTeaming.getMessages().adminInfoDlgEnterProxyCredentials( tmpTask.getServerName() );
						label = new InlineLabel( " " + txt );
						panel.add( label );

						liElement = Document.get().createLIElement();
						liElement.getStyle().setMarginBottom( 8, Unit.PX );
						liElement.setInnerHTML( panel.getElement().getInnerHTML() );
						
						uList.appendChild( liElement );
					}
					else if ( nextTask instanceof GwtSelectNetFolderServerTypeTask )
					{
						GwtSelectNetFolderServerTypeTask tmpTask;
						LIElement liElement;
						String txt;
						
						panel = new FlowPanel();
						
						tmpTask = (GwtSelectNetFolderServerTypeTask) nextTask;
						txt = GwtTeaming.getMessages().adminInfoDlgSelectNetFolderServerType( tmpTask.getServerName() );
						label = new InlineLabel( " " + txt );
						panel.add( label );

						liElement = Document.get().createLIElement();
						liElement.getStyle().setMarginBottom( 8, Unit.PX );
						liElement.setInnerHTML( panel.getElement().getInnerHTML() );
						
						uList.appendChild( liElement );
					}
				}

				cellFormatter.setColSpan( row, 0, 2 );
				cellFormatter.setWordWrap( row, 0, wordWrap );
				if ( tdStyleName != null )
					cellFormatter.addStyleName( row, 0, tdStyleName );
				table.setHTML( row, 0, uList.getString() );
				++row;
			}
		}

		// Are there upgrade tasks to be performed?
		if ( upgradeInfo.doUpgradeTasksExist() )
		{
			ClickHandler clickHandler;
			InlineLabel label;
			Panel helpPanel;
			String guideName;
			
			// Yes
			// Add a link to the documentation on upgrade tasks.
			helpPanel = new FlowPanel();
			label = new InlineLabel( GwtTeaming.getMessages().adminInfoDlgUpgradeTasksDocumentationLink() );
			helpPanel.add( label );
			guideName = GwtTeaming.getMessages().adminInfoDlgInstallGuide();
			label = new InlineLabel( guideName );
			label.addStyleName( "adminInfoDlg_docLink" );
			helpPanel.add( label );
			
			++row;
			table.setWidget( row, 0, helpPanel );
			++row;

			// Add a click handler for the link to the documentation.
			clickHandler = new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent clickEvent )
				{
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							HelpData helpData;
							
							helpData = new HelpData();
							helpData.setGuideName( HelpData.ADMIN_GUIDE );
							helpData.setPageId( "update_tasks" );
							
							GwtClientHelper.invokeHelp( helpData );
						}
					} );
				}
			};
			label.addClickHandler( clickHandler );
		}
	}
	
	/**
	 * 
	 */
	@Override
	public Object getDataFromDlg()
	{
		// Nothing to do.
		return new Object();
	}// end getDataFromDlg()
	
	
	/**
	 *  
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return null;
	}

	
	/**
	 * Initialize the controls in the dialog with the values from the given GwtUpgradeInfo object.
	 */
	public void init( Object props )
	{
		// Nothing to do.
	}

	
	/**
	 * Refresh the content of this dialog with the new information found in the given GwtUpgradeInfo object.
	 */
	public void refreshContent( GwtUpgradeInfo upgradeInfo )
	{
		int row = 0;
		FlexTable.FlexCellFormatter cellFormatter; 

		// Remove the style that gives this dialog a fixed height.
		m_mainPanel.removeStyleName( "adminInfoDlg_mainPanel" );
		
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
		
		// Are we running Filr
		if ( GwtTeaming.m_requestInfo.isLicenseFilr() )
		{
			String filrApplianceReleaseInfo;
			
			// Yes
			// Do we have release info on the Filr appliance?
			filrApplianceReleaseInfo = upgradeInfo.getFilrApplianceReleaseInfo();
			if ( filrApplianceReleaseInfo != null )
			{
				// Yes
				// Add a row for the Filr appliance release info.
				m_table.setText( row, 1, filrApplianceReleaseInfo );
				cellFormatter.setWordWrap( row, 1, false );
				m_table.setText( row, 2, " " );
				
				++row;
			}
		}

		// Create the ui that displays that tasks that need to be completed.
		AdminInfoDlg.createTasksToDoUI( upgradeInfo, m_table, row, false, null );
	}
	
	/**
	 * Show this dialog.
	 */
	public void showDlg()
	{
		PopupPanel.PositionCallback posCallback;
		
		posCallback = new PopupPanel.PositionCallback()
		{
			/**
			 * 
			 */
			@Override
			public void setPosition( int offsetWidth, int offsetHeight )
			{
				if ( offsetHeight > 400 )
					m_mainPanel.addStyleName( "adminInfoDlg_mainPanel" );
				else
					m_mainPanel.removeStyleName( "adminInfoDlg_mainPanel" );
			}
		};
		setPopupPositionAndShow( posCallback );
	}

	
	/**
	 * Callback interface to interact with the dialog asynchronously
	 * after it loads. 
	 */
	public interface AdminInfoDlgClient
	{
		void onSuccess( AdminInfoDlg dlg );
		void onUnavailable();
	}
	
	/*
	 * Asynchronously loads the AdminInfoDlg and performs some operation
	 * against the code.
	 */
	private static void doAsyncOperation(
		// Prefetch parameters.  true -> Prefetch only.  false -> Something else.
		final AdminInfoDlgClient dlgClient,
		final boolean prefetch,
		
		// Creation parameters.
		final boolean autoHide,
		final boolean modal,
		final int xPos,
		final int yPos,
		
		// initAndShow parameters.
		final AdminInfoDlg adminInfoDlg,
		final GwtUpgradeInfo upgradeInfo )
	{
		loadControl1(
			// Prefetch parameters.
			dlgClient,
			prefetch,
			
			// Creation parameters.
			autoHide,
			modal,
			xPos,
			yPos,
			
			// initAndShow parameters.
			adminInfoDlg,
			upgradeInfo );
	}// doAsyncOperation

	/*
	 * Various control loaders used to load the split points containing
	 * the code for the controls by the AdminInfoDlg object.
	 * 
	 * Loads the split point for the AdminInfoDlg.
	 */
	private static void loadControl1(
		// Prefetch parameters.  true -> Prefetch only.  false -> Something else.
		final AdminInfoDlgClient dlgClient,
		final boolean prefetch,
		
		// Creation parameters.
		final boolean autoHide,
		final boolean modal,
		final int xPos,
		final int yPos,
		
		// initAndShow parameters.
		final AdminInfoDlg adminInfoDlg,
		final GwtUpgradeInfo upgradeInfo )
	{
		GWT.runAsync( AdminInfoDlg.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess()
			{
				initAdminInfoDlg_Finish(
					// Prefetch parameters.
					dlgClient,
					prefetch,
					
					// Creation parameters.
					autoHide,
					modal,
					xPos,
					yPos,
					
					// initAndShow parameters.
					adminInfoDlg,
					upgradeInfo );
			}// end onSuccess()
			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_AdminInfoDlg() );
				dlgClient.onUnavailable();
			}// end onFailure()
		});
	}
	
	/*
	 * Finishes the initialization of the AdminInfoDlg object.
	 */
	private static void initAdminInfoDlg_Finish(
		// Prefetch parameters.  true -> Prefetch only.  false -> Something else.
		final AdminInfoDlgClient dlgClient,
		final boolean prefetch,
		
		// Creation parameters.
		final boolean autoHide,
		final boolean modal,
		final int xPos,
		final int yPos,
		
		// initAndShow parameters.
		final AdminInfoDlg adminInfoDlg,
		final GwtUpgradeInfo upgradeInfo )
	{
		// On a prefetch...
		if ( prefetch )
		{
			// ...we simply call the success handler with null for the
			// ...parameter.
			dlgClient.onSuccess( null );
		}

		// If we weren't given a AdminInfoDlg...
		else if ( null == adminInfoDlg )
		{
			// ...we assume we're to create one.
			AdminInfoDlg dlg = new AdminInfoDlg(
				autoHide,
				modal,
				xPos,
				yPos  );
			
			dlgClient.onSuccess( dlg );
		}
		
		else {
			// Otherwise, we assume we're to initialize and show the
			// AdminInfoDlg we were given!  Initialize...
			adminInfoDlg.refreshContent( upgradeInfo );
			adminInfoDlg.showDlg();
		}
	}
	
	/**
	 * Loads the AdminInfoDlg split point and returns an instance of it via
	 * the callback.
	 * 
	 * @param autoHide
	 * @param modal
	 * @param xPos
	 * @param yPos
	 * @param dlgClient
	 */
	public static void createAsync(
		final boolean autoHide,
		final boolean modal,
		final int xPos,
		final int yPos,
		final AdminInfoDlgClient dlgClient )
	{
		doAsyncOperation(
			// Prefetch parameters.  false -> Not a prefetch.
			dlgClient,
			false,
			
			// Creation parameters.
			autoHide,
			modal,
			xPos,
			yPos,
			
			// initAndShow parameters ignored.
			null,
			null );
	}// end createAsync()

	/**
	 * Initialize and show the dialog.
	 * 
	 * @param adminInfoDlg
	 * @param upgradeInfo
	 */
	public static void initAndShow(
		final AdminInfoDlg adminInfoDlg,
		final GwtUpgradeInfo upgradeInfo )
	{
		doAsyncOperation(
			// Prefetch parameters.  false -> Not a prefetch.
			null,
			false,
			
			// Creation parameters ignored.
			false,
			false,
			-1,
			-1,
			
			// initAndShow parameters.
			adminInfoDlg,
			upgradeInfo );
	}// end initAndShow()
	
	/**
	 * Causes the split point for the AdminInfoDlg to be fetched.
	 * 
	 * @param dlgClient
	 */
	public static void prefetch( AdminInfoDlgClient dlgClient )
	{
		// If we weren't give a AdminInfoDlgClient...
		if ( null == dlgClient )
		{
			// ...create a dummy one...
			dlgClient = new AdminInfoDlgClient()
			{				
				@Override
				public void onUnavailable()
				{
					// Unused.
				}// end onUnavailable()
				
				@Override
				public void onSuccess( AdminInfoDlg dlg )
				{
					// Unused.
				}// end onSuccess()
			};
		}

		// ...and perform the prefetch.
		doAsyncOperation(
			// Prefetch parameters.  true -> Prefetch only.
			dlgClient,
			true,
			
			// Creation parameters ignored.
			false,
			false,
			-1,
			-1,
			
			// initAndShow parameters ignored.
			null,
			null );
	}// end prefetch()
	
	public static void prefetch()
	{
		// Always use the initial form of the method.
		prefetch( null );
	}// end prefetch()
}

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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.admin.GwtUpgradeInfo;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Panel;

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
	}
	
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
	}
	
	
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
	}
	
	
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
	}

	
	/**
	 * Initialize the controls in the dialog with the values from the given GwtUpgradeInfo object.
	 */
	public void init( Object props )
	{
		// Nothing to do.
	}

	
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
	}

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
	}
}



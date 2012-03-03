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
import org.kablink.teaming.gwt.client.GwtDynamicGroupMembershipCriteria;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;


/**
 * This dialog can be used to modify the dynamic membership of a group.
 * @author jwootton
 *
 */
public class ModifyDynamicMembershipDlg extends DlgBox
{
	private TextBox m_baseDnTxtBox;
	private TextArea m_filterTextArea;
	private CheckBox m_searchSubtreeCB;
	private CheckBox m_updateCB;
	
	/**
	 * 
	 */
	public ModifyDynamicMembershipDlg(
		boolean autoHide,
		boolean modal,
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		int xPos,
		int yPos )
	{
		super( autoHide, modal, xPos, yPos );

		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().modifyDynamicMembershipDlgHeader(), editSuccessfulHandler, editCanceledHandler, null ); 
	}
	
	/**
	 * Create all the controls that make up the dialog box.
	 */
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		Panel mainPanel;
		FlexTable table;
		FlexCellFormatter cellFormatter;
		int nextRow;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		
		// Create a table to hold the controls.
		table = new FlexTable();
		table.setCellSpacing( 4 );
		table.addStyleName( "dlgContent" );
		
		cellFormatter = table.getFlexCellFormatter();
		
		nextRow = 0;
		
		// Create the controls for "Base DN"
		{
			InlineLabel label;
			
			label = new InlineLabel( messages.modifyDynamicMembershipDlgBaseDnLabel() );
			table.setWidget( nextRow, 0, label );
			
			m_baseDnTxtBox = new TextBox();
			m_baseDnTxtBox.setVisibleLength( 40 );
			table.setWidget( nextRow, 1, m_baseDnTxtBox );
			++nextRow;
		}
		
		// Create the controls for "ldap filter"
		{
			table.setText( nextRow, 0, messages.modifyDynamicMembershipDlgLdapFilterLabel() );
			++nextRow;
			
			m_filterTextArea = new TextArea();
			m_filterTextArea.setCharacterWidth( 56 );
			m_filterTextArea.setVisibleLines( 15 );
			table.setWidget( nextRow, 0, m_filterTextArea );
			cellFormatter.setColSpan( nextRow, 0, 2 );
			
			++nextRow;
		}
		
		// Add the "Search subtree" checkbox
		{
			m_searchSubtreeCB = new CheckBox( messages.modifyDynamicMembershipDlgSearchSubtreeLabel() );
			table.setWidget( nextRow, 0, m_searchSubtreeCB );
			cellFormatter.setColSpan( nextRow, 0, 2 );
			
			++nextRow;
		}
		
		// Add the "Update group membership during scheduled ldap synchronization" checkbox
		{
			m_updateCB = new CheckBox( messages.modifyDynamicMembershipDlgUpdateLabel() );
			table.setWidget( nextRow, 0, m_updateCB );
			cellFormatter.setColSpan( nextRow, 0, 2 );
			
			++nextRow;
		}
		
		mainPanel.add( table );

		return mainPanel;
	}

	/**
	 * Return the criteria for group membership
	 */
	public Object getDataFromDlg()
	{
		GwtDynamicGroupMembershipCriteria membershipCriteria;
		
		membershipCriteria = new GwtDynamicGroupMembershipCriteria();
		
		// Get the base dn
		membershipCriteria.setBaseDn( m_baseDnTxtBox.getText() );
		
		// Get the ldap filter.
		membershipCriteria.setLdapFilter( m_filterTextArea.getText() );
		
		// Get the "search subtree" value.
		membershipCriteria.setSearchSubtree( m_searchSubtreeCB.getValue() );
		
		// Get the "Update group membership during scheduled ldap synchronization" value.
		membershipCriteria.setUpdateDuringLdapSync( m_updateCB.getValue() );
		
		return membershipCriteria;
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	public FocusWidget getFocusWidget()
	{
		return m_baseDnTxtBox;
	}
	
	/**
	 * 
	 */
	public void init( GwtDynamicGroupMembershipCriteria membershipCriteria, int currentMembershipCnt )
	{
		if ( membershipCriteria != null )
		{
			// Initialeze the base dn text box.
			m_baseDnTxtBox.setValue( membershipCriteria.getBaseDn() );
			
			// Initialize the ldap filter text area
			m_filterTextArea.setText( membershipCriteria.getLdapFilter() );
			
			// Update the "search subtree" checkbox.
			m_searchSubtreeCB.setValue( membershipCriteria.getSearchSubtree() );
			
			// Update the "Update group membership during scheduled ldap synchronization" checkbox.
			m_updateCB.setValue( membershipCriteria.getUpdateDuringLdapSync() );
		}
	}
}

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
import org.kablink.teaming.gwt.client.GwtPersonalPreferences;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;


/**
 * 
 * @author jwootton
 *
 */
public class PersonalPreferencesDlg extends DlgBox
{
	private ListBox m_entryDisplayStyleListbox;
	

	/**
	 * 
	 */
	public PersonalPreferencesDlg(
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos )
	{
		super( autoHide, modal, xPos, yPos );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().personalPreferencesDlgHeader(), editSuccessfulHandler, editCanceledHandler, null ); 
	}// end PersonalPreferencesDlg()
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	public Panel createContent( Object props )
	{
		FlowPanel mainPanel = null;
		FlexTable table;
		int nextRow;

		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		
		table = new FlexTable();
		table.setCellSpacing( 4 );
		table.addStyleName( "dlgContent" );
		
		nextRow = 0;
		
		// Create the controls for "Entry display style"
		{
			table.setText( nextRow, 0, GwtTeaming.getMessages().entryDisplayStyleLabel() );
			
			// Create a listbox that will hold all the possible values for the "Entry Display Style".
			m_entryDisplayStyleListbox = new ListBox( false );
			m_entryDisplayStyleListbox.setVisibleItemCount( 1 );
			m_entryDisplayStyleListbox.addItem( GwtTeaming.getMessages().showEntriesAsAnOverlay(), "iframe" );
			m_entryDisplayStyleListbox.addItem( GwtTeaming.getMessages().showEntriesInNewPage(), "newpage" );
			m_entryDisplayStyleListbox.addItem( GwtTeaming.getMessages().showEntriesInPopupWnd(), "popup" );

			table.setWidget( nextRow, 1, m_entryDisplayStyleListbox );
			++nextRow;
		}
		
		mainPanel.add( table );
		
		return mainPanel;
	}// end createContent()
	
	
	/**
	 * Get the data from the controls in the dialog box and store the data in a GwtPersonalPreferences obj.
	 */
	public Object getDataFromDlg()
	{
		GwtPersonalPreferences personalPrefs;
		String displayStyle;
		
		personalPrefs = new GwtPersonalPreferences();
		
		// Get the entry display style from the dialog.
		displayStyle = getEntryDisplayStyleFromDlg();
		personalPrefs.setDisplayStyle( displayStyle );
		
		return personalPrefs;
	}// end getDataFromDlg()
	
	
	/**
	 * Get the selected value for "entry display style"
	 */
	private String getEntryDisplayStyleFromDlg()
	{
		int index;
		String displayStyle;
		
		index = m_entryDisplayStyleListbox.getSelectedIndex();
		if ( index == -1 )
			index = 0;
		
		displayStyle = m_entryDisplayStyleListbox.getValue( index );
		return displayStyle;
	}// end getEntryDisplayStyleFromDlg()
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	public FocusWidget getFocusWidget()
	{
		return null;
	}// end getFocusWidget()
	
	
	/**
	 * Initialize the controls in the dialog with the values from the given personal preferences.
	 */
	public void init( GwtPersonalPreferences personalPrefs )
	{
		initEntryDisplayStyleControls( personalPrefs );
	}// end init()
	
	
	/**
	 * Initialize the controls used with "Entry display style"
	 */
	private void initEntryDisplayStyleControls( GwtPersonalPreferences personalPrefs )
	{
		int index;
		
		m_entryDisplayStyleListbox.setSelectedIndex( -1 );
		
		// Select the appropriate item in the "display style" listbox.
		index = GwtClientHelper.selectListboxItemByValue( m_entryDisplayStyleListbox, personalPrefs.getDisplayStyle() );
		
		// Did we select an item in the listbox?
		if ( index == -1 )
		{
			// No
			m_entryDisplayStyleListbox.setSelectedIndex( 0 );
		}
	}// end initEntryDisplayStyleControls()

}// end PersonalPreferencesDlg

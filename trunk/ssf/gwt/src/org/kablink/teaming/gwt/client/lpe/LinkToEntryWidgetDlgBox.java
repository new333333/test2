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
package org.kablink.teaming.gwt.client.lpe;

import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.EditCanceledHandler;
import org.kablink.teaming.gwt.client.widgets.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.widgets.FindCtrl;
import org.kablink.teaming.gwt.client.widgets.OnSelectHandler;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author jwootton
 *
 */
public class LinkToEntryWidgetDlgBox extends DlgBox
	implements OnSelectHandler
{
	private FindCtrl m_findCtrl = null;
	private TextBox	m_titleTxtBox = null;
	private CheckBox	m_newWndCkBox = null;
	private String m_entryId = null;
	
	/**
	 * 
	 */
	public LinkToEntryWidgetDlgBox(
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		LinkToEntryProperties properties ) // Where properties used in the dialog are read from and saved to.
	{
		super( autoHide, modal, xPos, yPos );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().linkToEntryProperties(), editSuccessfulHandler, editCanceledHandler, properties ); 
	}// end LinkToEntryWidgetDlgBox()
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	public Panel createContent( PropertiesObj props )
	{
		LinkToEntryProperties properties;
		Label label;
		VerticalPanel	mainPanel;
		FlexTable		table;
		HTMLTable.CellFormatter cellFormatter;
		
		properties = (LinkToEntryProperties) props;

		mainPanel = new VerticalPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		// Add label and find control for "Entry id"
		table = new FlexTable();
		table.setCellSpacing( 8 );
		cellFormatter = table.getCellFormatter();
		cellFormatter.setAlignment( 0, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP );
		label = new Label( GwtTeaming.getMessages().findEntry() );
		table.setWidget( 0, 0, label );
		m_findCtrl = new FindCtrl( this, GwtSearchCriteria.SearchType.ENTRIES );
		table.setWidget( 0, 1, m_findCtrl );
		mainPanel.add( table );
		
		// Add label and edit control for "Title"
		label = new Label( GwtTeaming.getMessages().linkToEntryTitleLabel() );
		table.setWidget( 1, 0, label );
		m_titleTxtBox = new TextBox();
		m_titleTxtBox.setVisibleLength( 30 );
		table.setWidget( 1, 1, m_titleTxtBox );
		
		// Add a checkbox for "Open the entry in a new window"
		table = new FlexTable();
		table.setCellSpacing( 4 );
		m_newWndCkBox = new CheckBox( GwtTeaming.getMessages().openEntryInNewWnd() );
		table.setWidget( 0, 0, m_newWndCkBox );
		mainPanel.add( table );

		init( properties );
		
		return mainPanel;
	}// end createContent()
	
	
	/**
	 * Get the data from the controls in the dialog box and store the data in the properties obj.
	 */
	public PropertiesObj getDataFromDlg()
	{
		LinkToEntryProperties	properties;
		
		properties = new LinkToEntryProperties();
		
		// Save away the entry id.
		properties.setEntryId( getEntryIdValue() );
		
		// Save away the title.
		properties.setTitle( getTitleValue() );

		// Save away the "open in new window" value.
		properties.setOpenInNewWindow( getOpenInNewWindowValue() );
		
		return properties;
	}// end getDataFromDlg()
	
	
	/**
	 * Return entry id of the selected entry.
	 */
	public String getEntryIdValue()
	{
		// m_entryId will always hold the id of the selected entry.
		return m_entryId;
	}// end getEntryIdValue()
	

	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	public FocusWidget getFocusWidget()
	{
		return m_findCtrl.getFocusWidget();
	}// end getFocusWidget()
	
	
	/**
	 * Return true if the "open in new window" checkbox is checked.
	 */
	public boolean getOpenInNewWindowValue()
	{
		return m_newWndCkBox.getValue().booleanValue();
	}// end getOpenInNewWindowValue()
	
	
	/**
	 * Return the text found in the title edit control.
	 */
	public String getTitleValue()
	{
		return m_titleTxtBox.getText();
	}// end getTitleValue()
	
	
	/**
	 * Initialize the controls in the dialog with the values from the properties
	 */
	public void init( PropertiesObj props )
	{
		LinkToEntryProperties properties;
		String tmp;
		
		properties = (LinkToEntryProperties) props;

		// Hide the search-results widget.
		m_findCtrl.hideSearchResults();

		// Populate the find control's text box with the name of the selected entry.
		m_findCtrl.setInitialSearchString( properties.getEntryName() );
		
		// Remember the entry id that was passed to us.
		m_entryId = properties.getEntryId();
		
		tmp = properties.getTitle();
		if ( tmp == null )
			tmp = "";
		m_titleTxtBox.setText( tmp );

		m_newWndCkBox.setValue( properties.getOpenInNewWindow() );
	}// end init()
	

	/**
	 * This method gets called when the user selects an item from the search results in the "find" control.
	 */
	public void onSelect( Object selectedObj )
	{
		// Make sure we are dealing with a GwtFolderEntry object.
		if ( selectedObj instanceof GwtFolderEntry )
		{
			GwtFolderEntry gwtFolderEntry;
			
			gwtFolderEntry = (GwtFolderEntry) selectedObj;
			m_entryId = gwtFolderEntry.getEntryId();
			
			// Hide the search-results widget.
			m_findCtrl.hideSearchResults();
		}
	}// end onSelect()
	
}// end LinkToEntryWidgetDlgBox

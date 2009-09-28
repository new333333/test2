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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.EditCanceledHandler;
import org.kablink.teaming.gwt.client.widgets.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author jwootton
 *
 */
public class FolderWidgetDlgBox extends DlgBox
{
	private CheckBox m_showTitleCkBox = null;
	private CheckBox m_showDescCkBox = null;
	private CheckBox m_showEntriesOpenedCkBox = null;
	private TextBox m_folderTxtBox = null;
	private TextBox m_numEntriesToShowTxtBox;
	
	/**
	 * 
	 */
	public FolderWidgetDlgBox(
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		FolderProperties properties ) // Where properties used in the dialog are read from and saved to.
	{
		super( autoHide, modal, xPos, yPos );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().folderProperties(), editSuccessfulHandler, editCanceledHandler, properties ); 
	}// end FolderWidgetDlgBox()
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	public Panel createContent( PropertiesObj props )
	{
		FolderProperties properties;
		Label			label;
		VerticalPanel	mainPanel;
		FlexTable		table;
		
		properties = (FolderProperties) props;

		mainPanel = new VerticalPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		// Add label and edit control for "Folder id"
		table = new FlexTable();
		table.setCellSpacing( 8 );

		// Add controls for the folder.
		label = new Label( GwtTeaming.getMessages().findFolderLabel() );
		table.setWidget( 0, 0, label );
		m_folderTxtBox = new TextBox();
		m_folderTxtBox.setVisibleLength( 30 );
		table.setWidget( 1, 0, m_folderTxtBox );

		// Add controls for "Number of entries to show"
		label = new Label( GwtTeaming.getMessages().numEntriesToShow() );
		table.setWidget( 2, 0, label );
		m_numEntriesToShowTxtBox = new TextBox();
		m_numEntriesToShowTxtBox.setVisibleLength( 2 );
		table.setWidget( 3, 0, m_numEntriesToShowTxtBox );
		mainPanel.add( table );
		
		// Add a checkbox for "Show title"
		table = new FlexTable();
		table.setCellSpacing( 0 );
		m_showTitleCkBox = new CheckBox( GwtTeaming.getMessages().showTitleBar() );
		table.setWidget( 0, 0, m_showTitleCkBox );
		
		// Add a checkbox for "Show the folder description"
		m_showDescCkBox = new CheckBox( GwtTeaming.getMessages().showFolderDesc() );
		table.setWidget( 1, 0, m_showDescCkBox );

		// Add a checkbox for "Show entries opened"
		m_showEntriesOpenedCkBox = new CheckBox( GwtTeaming.getMessages().showEntriesOpened() );
		table.setWidget( 2, 0, m_showEntriesOpenedCkBox );
		mainPanel.add( table );

		init( properties );
		
		return mainPanel;
	}// end createContent()
	
	
	/**
	 * Get the data from the controls in the dialog box and store the data in the properties obj.
	 */
	public PropertiesObj getDataFromDlg()
	{
		FolderProperties	properties;
		
		properties = new FolderProperties();
		
		// Save away the "show title bar" value.
		properties.setShowTitle( getShowTitleValue() );
		
		// Save away the "show the folder description" value.
		properties.setShowDescValue( getShowDescValue() );
		
		// Save away the "show entries opened" value.
		properties.setShowEntriesOpenedValue( getShowEntriesOpenedValue() );
		
		// Save away the folder id.
		properties.setFolderId( getFolderIdValue() );
		
		// Save away the number of entries to show.
		properties.setNumEntriesToBeShownValue( getNumEntriesToShowValue() );
		
		return properties;
	}// end getDataFromDlg()
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	public FocusWidget getFocusWidget()
	{
		return m_folderTxtBox;
	}// end getFocusWidget()
	
	
	/**
	 * Return folder id of the selected folder.
	 */
	public String getFolderIdValue()
	{
		return m_folderTxtBox.getText();
	}// end getFolderIdValue()
	

	/**
	 * Return the number of entries to show.
	 */
	public int getNumEntriesToShowValue()
	{
		String txt;
		
		// Do validation!!!
		txt = m_numEntriesToShowTxtBox.getText();
		return Integer.parseInt( txt );
	}// end getNumEntriesToShowValue()
	
	
	/**
	 * Return true if the "show the folder description" checkbox is checked.
	 */
	public boolean getShowDescValue()
	{
		return m_showDescCkBox.getValue().booleanValue();
	}// end getShowDescValue()
	
	
	/**
	 * Return true if the "show entries opened" checkbox is checked.
	 */
	public boolean getShowEntriesOpenedValue()
	{
		return m_showEntriesOpenedCkBox.getValue().booleanValue();
	}// end getShowEntriesOpenedValue()
	
	
	/**
	 * Return true if the "show title" checkbox is checked.
	 */
	public boolean getShowTitleValue()
	{
		return m_showTitleCkBox.getValue().booleanValue();
	}// end getShowTitleValue()
	
	
	/**
	 * Initialize the controls in the dialog with the values from the properties
	 */
	public void init( PropertiesObj props )
	{
		FolderProperties properties;
		String value;
		int num;
		
		properties = (FolderProperties) props;

		m_showTitleCkBox.setValue( properties.getShowTitleValue() );
		m_showDescCkBox.setValue( properties.getShowDescValue() );
		m_showEntriesOpenedCkBox.setValue( properties.getShowEntriesOpenedValue() );
		
		value = properties.getFolderId();
		if ( value == null )
			value = "";
		m_folderTxtBox.setText( value );

		num = properties.getNumEntriesToBeShownValue();
		m_numEntriesToShowTxtBox.setText( String.valueOf( num ) );
	}// end init()
	
}// end FolderWidgetDlgBox

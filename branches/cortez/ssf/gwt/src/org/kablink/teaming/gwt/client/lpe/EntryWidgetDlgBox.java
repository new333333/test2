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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author jwootton
 *
 */
public class EntryWidgetDlgBox extends DlgBox
	implements OnSelectHandler
{
	private CheckBox m_showTitleCkBox = null;
	private FindCtrl m_findCtrl = null;
	private String m_entryId = null;
	private InlineLabel m_currentEntryNameLabel = null;
	
	/**
	 * 
	 */
	public EntryWidgetDlgBox(
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		EntryProperties properties ) // Where properties used in the dialog are read from and saved to.
	{
		super( autoHide, modal, xPos, yPos );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().entryProperties(), editSuccessfulHandler, editCanceledHandler, properties ); 
	}// end EntryWidgetDlgBox()
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	public Panel createContent( PropertiesObj props )
	{
		EntryProperties properties;
		Label			label;
		InlineLabel		inlineLabel;
		VerticalPanel	mainPanel;
		FlexTable		table;
		HTMLTable.CellFormatter cellFormatter;
		FlowPanel flowPanel;
		
		properties = (EntryProperties) props;

		mainPanel = new VerticalPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		
		// Add a label that will say Current entry: name of the currently selected entry
		table = new FlexTable();
		table.setCellSpacing( 8 );
		flowPanel = new FlowPanel();
		inlineLabel = new InlineLabel( GwtTeaming.getMessages().currentEntry() );
		m_currentEntryNameLabel = new InlineLabel();
		m_currentEntryNameLabel.addStyleName( "bold" );
		m_currentEntryNameLabel.addStyleName( "marginLeftPoint25em" );
		flowPanel.add( inlineLabel );
		flowPanel.add( m_currentEntryNameLabel );
		table.setWidget( 0, 0, flowPanel );
		mainPanel.add( table );

		// Add label and a "find" control.
		table = new FlexTable();
		table.setCellSpacing( 8 );
		cellFormatter = table.getCellFormatter();
		cellFormatter.setAlignment( 0, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP );
		label = new Label( GwtTeaming.getMessages().findEntry() );
		table.setWidget( 0, 0, label );
		m_findCtrl = new FindCtrl( this, GwtSearchCriteria.SearchType.ENTRIES );
		table.setWidget( 0, 1, m_findCtrl );
		mainPanel.add( table );
		
		// Add a checkbox for "Show title"
		table = new FlexTable();
		table.setCellSpacing( 8 );
		m_showTitleCkBox = new CheckBox( GwtTeaming.getMessages().showTitleBar() );
		table.setWidget( 0, 0, m_showTitleCkBox );
		mainPanel.add( table );

		init( properties );
		
		return mainPanel;
	}// end createContent()
	
	
	/**
	 * Get the data from the controls in the dialog box and store the data in the properties obj.
	 */
	public PropertiesObj getDataFromDlg()
	{
		String entryId;
		
		EntryProperties	properties;
		
		properties = new EntryProperties();
		
		// Save away the "show border" value.
		properties.setShowTitle( getShowTitleValue() );
		
		// Save away the entry id.
		// Did the user select an entry?
		entryId = getEntryIdValue();
		if ( entryId == null || entryId.length() == 0 )
		{
			// No, tell them they need to
			Window.alert( GwtTeaming.getMessages().pleaseSelectAnEntry() );
			return null;
		}
		
		properties.setEntryId( entryId );
		
		return properties;
	}// end getDataFromDlg()
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	public FocusWidget getFocusWidget()
	{
		return m_findCtrl.getFocusWidget();
	}// end getFocusWidget()
	
	
	/**
	 * Return true if the "show title" checkbox is checked.
	 */
	public boolean getShowTitleValue()
	{
		return m_showTitleCkBox.getValue().booleanValue();
	}// end getShowBorderValue()
	
	
	/**
	 * Return entry id of the selected entry.
	 */
	public String getEntryIdValue()
	{
		// m_entryId will always hold the id of the selected entry.
		return m_entryId;
	}// end getEntryIdValue()
	

	/**
	 * Initialize the controls in the dialog with the values from the properties
	 */
	public void init( PropertiesObj props )
	{
		EntryProperties properties;
		
		properties = (EntryProperties) props;

		// Update the name of the currently selected entry.
		m_currentEntryNameLabel.setText( properties.getEntryName() ); 
		 
		m_showTitleCkBox.setValue( properties.getShowTitleValue() );
		
		// Hide the search-results widget.
		m_findCtrl.hideSearchResults();
		
		m_findCtrl.setInitialSearchString( "" );
		
		// Remember the entry id that was passed to us.
		m_entryId = properties.getEntryId();
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
	
}// end EntryWidgetDlgBox

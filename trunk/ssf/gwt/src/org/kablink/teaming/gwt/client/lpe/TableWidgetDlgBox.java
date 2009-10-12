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

import java.util.ArrayList;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.EditCanceledHandler;
import org.kablink.teaming.gwt.client.widgets.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author jwootton
 *
 */
public class TableWidgetDlgBox extends DlgBox
	implements ChangeHandler, KeyPressHandler
{
	private CheckBox		m_showBorderCkBox = null;
	private ListBox		m_numColsCtrl = null;
	private ArrayList<TextBox>	m_columnWidths = null;
	private VerticalPanel	m_mainPanel = null;
	private FlexTable		m_table = null;
	
	/**
	 * 
	 */
	public TableWidgetDlgBox(
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		TableProperties properties ) // Where properties used in the dialog are read from and saved to.
	{
		super( autoHide, modal, xPos, yPos );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().tableProperties(), editSuccessfulHandler, editCanceledHandler, properties ); 
	}// end TableWidgetDlgBox()
	

	/**
	 * Create a text box control for every column.
	 */
	public void addColumnWidthControls( TableProperties properties )
	{
		int		numCols;
		int		i;
		TextBox	txtBox;
		
		// Remove all existing text box controls used to specify column width.
		if ( m_columnWidths != null )
		{
			for (i = 0; i < m_columnWidths.size(); ++i)
			{
				// Remove the 2nd row.  Row 0, contains the "Show border" checkbox and Row 1, contains the "Number of columns" controls.
				m_table.removeRow( 2 );
			}
			
			m_columnWidths.clear();
		}
		
		// Add a label and text box control for every column
		numCols = properties.getNumColumnsInt();
		for (i = 0; i < numCols; ++i)
		{
			Label	label;

			// Add the label, "Column X width:"
			label = new Label( GwtTeaming.getMessages().columnXWidth( i+1 ) );
			m_table.setWidget( i+2, 0, label );
			
			txtBox = new TextBox();
			txtBox.setVisibleLength( 3 );
			txtBox.setValue( properties.getColWidthStr( i ) );
			txtBox.addKeyPressHandler( this );
			m_table.setWidget( i+2, 1, txtBox );
			
			label = new Label( GwtTeaming.getMessages().percent() );
			m_table.setWidget( i+2, 2, label );
			
			m_columnWidths.add( i, txtBox );
		}// end for()
	}// end addColumnWidthControls()
	
	
	/**
	 * Create all the controls that make up the dialog box.
	 */
	@SuppressWarnings("unchecked")
	public Panel createContent( PropertiesObj props )
	{
		TableProperties properties;
		Label			label;
		
		properties = (TableProperties) props;

		m_columnWidths = new ArrayList( 3 );

		m_mainPanel = new VerticalPanel();
		m_mainPanel.setStyleName( "teamingDlgBoxContent" );

		m_table = new FlexTable();
		m_table.setCellSpacing( 8 );
		
		// Add a checkbox for "Show border"
		m_showBorderCkBox = new CheckBox( GwtTeaming.getMessages().showBorder() );
		m_table.setWidget( 0, 0, m_showBorderCkBox );

		// Add label and select control for "Number of columns:".
		label = new Label( GwtTeaming.getMessages().numColumns() );
		m_table.setWidget( 1, 0, label );
		m_numColsCtrl = new ListBox( false );
		m_numColsCtrl.setVisibleItemCount( 1 );
		m_numColsCtrl.addItem( GwtTeaming.getMessages()._1(), "1" );
		m_numColsCtrl.addItem( GwtTeaming.getMessages()._2(), "2" );
		m_numColsCtrl.addItem( GwtTeaming.getMessages()._3(), "3" );
		m_numColsCtrl.addItem( GwtTeaming.getMessages()._4(), "4" );
		m_numColsCtrl.addItem( GwtTeaming.getMessages()._5(), "5" );
		m_numColsCtrl.addChangeHandler( this );
		m_table.setWidget( 1, 1, m_numColsCtrl );
		
		m_mainPanel.add( m_table );
		
		init( properties );

		return m_mainPanel;
	}// end createContent()
	
	
	/**
	 * Get the width of the given column that was entered by the user.
	 */
	public int getColWidth( int col )
	{
		int width;
		
		width = 0;
		
		// Is the requested column valid?
		if ( col < m_columnWidths.size() )
		{
			String value;
			TextBox txtBox;
			
			// Yes, get the string entered by the user.
			txtBox = m_columnWidths.get( col );
			value = txtBox.getValue();

			if ( value != null && value.length() > 0 )
			{
				try
				{
					width = Integer.parseInt( value );
				}
				catch ( NumberFormatException nfEx )
				{
					// This should never happen.  The data should be validated before we get to this point.
				}
			}
		}
		
		return width;
	}// end getColWidth()
	
	
	/**
	 * Get the data from the controls in the dialog box and store the data in the properties obj.
	 */
	public PropertiesObj getDataFromDlg()
	{
		TableProperties	properties;
		int numColumns;
		int i;
		int totalWidth = 0;
		
		properties = new TableProperties();
		
		// Save away the "show border" value.
		properties.setShowBorder( getShowBorderValue() );
		
		// Save away the "number of columns" value.
		numColumns = getNumColumns();
		properties.setNumColumns( numColumns );
		
		// Save away the "column widths" values.
		for (i = 0; i < numColumns; ++i)
		{
			int width;
			
			// Get the width of this column.
			width = getColWidth( i );
			
			// Is the width valid?  Width must be 1-99
			if ( width > 0 && width < 100 )
			{
				// Yes
				properties.setColWidth( i, width );
				totalWidth += width;
			}
			else
			{
				// No, tell the user about the problem.
				Window.alert( GwtTeaming.getMessages().invalidColumnWidth( i+1 ) );
				return null;
			}
		}
		
		// Is the total width entered by the user valid?
		if ( totalWidth > 100 )
		{
			// No, tell the user about the problem.
			Window.alert( GwtTeaming.getMessages().invalidTotalTableWidth() );
			return null;
		}
		
		return properties;
	}// end getDataFromDlg()
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	public FocusWidget getFocusWidget()
	{
		return m_numColsCtrl;
	}// end getFocusWidget()
	
	
	/**
	 * Get the number of columns from the control in the dialog.
	 */
	public int getNumColumns()
	{
		int selectedIndex;
		int numColumns = 0;
		
		// Get the selected index from the select control.
		selectedIndex = m_numColsCtrl.getSelectedIndex();
		if ( selectedIndex >= 0 )
		{
			String value;
			
			value = m_numColsCtrl.getValue( selectedIndex );
			numColumns = Integer.parseInt( value );
		}
		
		return numColumns;
	}// end getNumColumns()
	
	
	/**
	 * Return true if the "show border" checkbox is checked.
	 */
	public boolean getShowBorderValue()
	{
		return m_showBorderCkBox.getValue().booleanValue();
	}// end getShowBorderValue()
	

	/**
	 * Initialize the controls in the dialog with the values from the properties
	 */
	public void init( PropertiesObj props )
	{
		TableProperties properties;
		
		properties = (TableProperties) props;

		m_showBorderCkBox.setValue( properties.getShowBorderValue() );
		m_numColsCtrl.setSelectedIndex( properties.getNumColumnsInt() - 1 );

		// Add a "Column width" text box for every column.
		addColumnWidthControls( properties );
	}// end init()

	
	/**
	 * This method gets called when the user changes the number of columns.
	 */
	public void onChange( ChangeEvent event )
	{
		TableProperties	properties;
		
		properties = new TableProperties();
		properties.setNumColumns( getNumColumns() );
		
		// Add a "Column width" text box for every column.
		addColumnWidthControls( properties );
	}// end onChange()
	
	
	/**
	 * This method gets called when the user types in the "column width" text box.
	 * We only allow the user to enter numbers.
	 */
	public void onKeyPress( KeyPressEvent event )
	{
        int keyCode;

        // Get the key the user pressed
        keyCode = event.getCharCode();
        
        if ( (!Character.isDigit(event.getCharCode())) && (keyCode != KeyCodes.KEY_TAB) && (keyCode != KeyCodes.KEY_BACKSPACE)
            && (keyCode != KeyCodes.KEY_DELETE) && (keyCode != KeyCodes.KEY_ENTER) && (keyCode != KeyCodes.KEY_HOME)
            && (keyCode != KeyCodes.KEY_END) && (keyCode != KeyCodes.KEY_LEFT) && (keyCode != KeyCodes.KEY_UP)
            && (keyCode != KeyCodes.KEY_RIGHT) && (keyCode != KeyCodes.KEY_DOWN))
        {
        	TextBox txtBox;
        	Object source;
        	
        	// Make sure we are dealing with a text box.
        	source = event.getSource();
        	if ( source instanceof TextBox )
        	{
        		// Suppress the current keyboard event.
        		txtBox = (TextBox) source;
        		txtBox.cancelKey();
        	}
        }
	}// end onKeyPress()
}// end TableWidgetDlgBox

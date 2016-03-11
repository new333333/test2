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
import java.lang.Character;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.core.client.Scheduler;
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
	private TextBox m_numRowsCtrl = null;
	private CheckBox m_showBorderCkBox = null;
	private ListBox m_numColsCtrl = null;
	private ArrayList<TextBox> m_colWidths = null;
	private ArrayList<ListBox> m_colWidthUnits = null;
	private VerticalPanel m_mainPanel = null;
	private FlexTable m_table = null;
	
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
		if ( m_colWidths != null )
		{
			for (i = 0; i < m_colWidths.size(); ++i)
			{
				// Remove the row 3.
				// Row 0, contains the "Show border" checkbox
				// Row 1, contains the "Number of rows" controls
				// Row 2, contains the "Number of columns" controls.
				m_table.removeRow( 3 );
			}
			
			m_colWidths.clear();
			m_colWidthUnits.clear();
		}
		
		// Add a label and text box control for every column
		numCols = properties.getNumColumnsInt();
		for (i = 0; i < numCols; ++i)
		{
			Label	label;
			ListBox listBox;
			ColWidthUnit units;
			String colWidth;

			// Add the label, "Column X width:"
			label = new Label( GwtTeaming.getMessages().columnXWidth( i+1 ) );
			m_table.setWidget( i+3, 0, label );
			
			txtBox = new TextBox();
			txtBox.setVisibleLength( 3 );
			colWidth = properties.getColWidth( i );
			txtBox.setValue( colWidth );
			txtBox.addKeyPressHandler( this );
			m_table.setWidget( i+3, 1, txtBox );
			
			// Remember this TextBox
			m_colWidths.add( i, txtBox );

			listBox = new ListBox();
			listBox.setVisibleItemCount( 1 );
			listBox.addItem( GwtTeaming.getMessages().percent(), "1" );
			listBox.addItem( GwtTeaming.getMessages().pxLabel(), "2" );
			units = properties.getColWidthUnit( i );
			if ( units == ColWidthUnit.PERCENTAGE )
				listBox.setSelectedIndex( 0 );
			else if ( units == ColWidthUnit.PX )
				listBox.setSelectedIndex( 1 );
			else
				listBox.setSelectedIndex( 0 );
			m_table.setWidget( i+3, 2, listBox );
			
			// Remember this ListBox
			m_colWidthUnits.add( i, listBox );
			
			danceWidthUnits( txtBox );
		}// end for()
	}// end addColumnWidthControls()
	
	
	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		TableProperties properties;
		Label			label;
		
		properties = (TableProperties) props;

		m_colWidths = new ArrayList<TextBox>( 5 );
		m_colWidthUnits = new ArrayList<ListBox>( 5 );

		m_mainPanel = new VerticalPanel();
		m_mainPanel.setStyleName( "teamingDlgBoxContent" );

		m_table = new FlexTable();
		m_table.setCellSpacing( 8 );
		
		// Add a checkbox for "Show border"
		m_showBorderCkBox = new CheckBox( GwtTeaming.getMessages().showBorder() );
		m_table.setWidget( 0, 0, m_showBorderCkBox );
		
		// Add a label and textbox control for "Number of rows:"
		label = new Label( GwtTeaming.getMessages().numRows() );
		m_table.setWidget( 1, 0, label );
		m_numRowsCtrl = new TextBox();
		m_numRowsCtrl.setVisibleLength( 2 );
		m_numRowsCtrl.addKeyPressHandler( this );
		m_table.setWidget( 1, 1, m_numRowsCtrl );

		// Add label and select control for "Number of columns:".
		label = new Label( GwtTeaming.getMessages().numColumns() );
		m_table.setWidget( 2, 0, label );
		m_numColsCtrl = new ListBox( false );
		m_numColsCtrl.setVisibleItemCount( 1 );
		m_numColsCtrl.addItem( GwtTeaming.getMessages()._1(), "1" );
		m_numColsCtrl.addItem( GwtTeaming.getMessages()._2(), "2" );
		m_numColsCtrl.addItem( GwtTeaming.getMessages()._3(), "3" );
		m_numColsCtrl.addItem( GwtTeaming.getMessages()._4(), "4" );
		m_numColsCtrl.addItem( GwtTeaming.getMessages()._5(), "5" );
		m_numColsCtrl.addItem( GwtTeaming.getMessages()._6(), "6" );
		m_numColsCtrl.addItem( GwtTeaming.getMessages()._7(), "7" );
		m_numColsCtrl.addItem( GwtTeaming.getMessages()._8(), "8" );
		m_numColsCtrl.addItem( GwtTeaming.getMessages()._9(), "9" );
		m_numColsCtrl.addItem( GwtTeaming.getMessages()._10(), "10" );
		m_numColsCtrl.addChangeHandler( this );
		m_table.setWidget( 2, 1, m_numColsCtrl );
		
		m_mainPanel.add( m_table );
		
		init( properties );

		return m_mainPanel;
	}// end createContent()
	
	
	/**
	 * Enable/disable the units listbox that is associated with the given textbox based on what the
	 * given textbox contains.
	 */
	private void danceWidthUnits( TextBox textBox )
	{
		ListBox listBox;
		
		// Get the listbox associated with the given textbox
		listBox = getListBox( textBox );
		
		if ( listBox != null )
		{
			String value;
			
			value = textBox.getValue();
			if ( value == null || value.length() == 0 )
				listBox.setEnabled( false );
			else if ( value.equals( "*" ) )
				listBox.setEnabled( false );
			else
				listBox.setEnabled( true );
		}
	}
	
	
	/**
	 * Get the width of the given column that was entered by the user.
	 */
	private String getColWidth( int col )
	{
		// Is the requested column valid?
		if ( col < m_colWidths.size() )
		{
			String value;
			TextBox txtBox;
			
			// Yes, get the string entered by the user.
			txtBox = m_colWidths.get( col );
			value = txtBox.getValue();

			if ( value != null && value.length() > 0 )
			{
				if ( value.equals( "*" ) )
				{
					// "*" is a valid width.
					return value;
				}
				
				try
				{
					Integer.parseInt( value );
					
					return value;
				}
				catch ( NumberFormatException nfEx )
				{
					// This should never happen.  The data should be validated before we get to this point.
				}
			}
		}
		
		return "";
	}// end getColWidth()
	
	/**
	 * Return the width units of the given column.
	 */
	public ColWidthUnit getColWidthUnits( int col )
	{
		ColWidthUnit units;
		
		// Default to %
		units = ColWidthUnit.NONE;
		
		// Is the requested column valid?
		if ( col < m_colWidthUnits.size() )
		{
			ListBox listBox;
			
			// Yes
			units = ColWidthUnit.NONE;

			listBox = m_colWidthUnits.get( col );
			
			if ( listBox.isEnabled() == true )
			{
				int selectedIndex;

				selectedIndex = listBox.getSelectedIndex();

				if ( selectedIndex == 0 )
					units = ColWidthUnit.PERCENTAGE;
				else if ( selectedIndex == 1 )
					units = ColWidthUnit.PX;
			}
		}
		
		return units;
	}
	
	
	/**
	 * Get the data from the controls in the dialog box and store the data in the properties obj.
	 */
	@Override
	public PropertiesObj getDataFromDlg()
	{
		TableProperties	properties;
		int numRows;
		int numColumns;
		int i;
		int totalPercentageWidth = 0;
		
		properties = new TableProperties();
		
		// Save away the "show border" value.
		properties.setShowBorder( getShowBorderValue() );
		
		// Save away number of rows
		numRows = getNumRows();
		if ( numRows == 0 )
		{
			// Tell the user that the number of rows must be greater than 0.
			Window.alert( GwtTeaming.getMessages().invalidNumberOfRows() );
			return null;
		}
		properties.setNumRows( numRows );
		
		// Save away the "number of columns" value.
		numColumns = getNumColumns();
		properties.setNumColumns( numColumns );
		
		// Save away the "column widths" values.
		for (i = 0; i < numColumns; ++i)
		{
			String width;
			ColWidthUnit units;
			
			// Get the width of this column.
			width = getColWidth( i );
			
			// Did the user enter anything?
			if ( width == null || width.length() == 0 )
			{
				// No, tell the user about the problem.
				Window.alert( GwtTeaming.getMessages().emptyColumnWidth( i+ 1 ) );
				return null;
			}
			
			// Get the width units, % or px
			units = getColWidthUnits( i );
			
			// Are we dealing with %?
			if ( units == ColWidthUnit.PERCENTAGE )
			{
				int widthInt;
				
				// Yes
				// Is the width valid?  Width must be 1-99
				widthInt = Integer.valueOf( width ).intValue();
				if ( widthInt > 0 && widthInt <= 100 )
				{
					// Yes
					properties.setColWidth( i, width );
					totalPercentageWidth += widthInt;
				}
				else
				{
					// No, tell the user about the problem.
					Window.alert( GwtTeaming.getMessages().invalidColumnWidth( i+1 ) );
					return null;
				}
			}
			else if ( units == ColWidthUnit.PX || units == ColWidthUnit.NONE )
			{
				properties.setColWidth( i, width );
			}
			
			// Save away the width units
			properties.setColWidthUnit( i, units );
		}
		
		// Is the total width entered by the user valid?
		if ( totalPercentageWidth > 100 )
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
	@Override
	public FocusWidget getFocusWidget()
	{
		return m_numColsCtrl;
	}// end getFocusWidget()
	
	
	/**
	 * Get the listbox associated with the given textbox
	 */
	private ListBox getListBox( TextBox textBox )
	{
		int i;
		
		i = 0;
		for (TextBox nextTextBox : m_colWidths)
		{
			if ( nextTextBox == textBox )
				return m_colWidthUnits.get( i );
			
			++i;
		}

		return null;
	}
	
	
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
	 * Return the number of rows from the control in the dialog.
	 */
	public int getNumRows()
	{
		String numStr;
		int numRows;
		
		numRows = 0;
		numStr = m_numRowsCtrl.getText();
		
		if ( numStr != null && numStr.length() > 0 )
		{
			numRows = Integer.parseInt( numStr );
		}
		
		return numRows; 
	}
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
		m_numRowsCtrl.setText( properties.getNumRowsStr() );
		m_numColsCtrl.setSelectedIndex( properties.getNumColumnsInt() - 1 );

		// Add a "Column width" text box for every column.
		addColumnWidthControls( properties );
	}// end init()

	
	/**
	 * This method gets called when the user changes the number of columns.
	 */
	@Override
	public void onChange( ChangeEvent event )
	{
		TableProperties	properties;
		
		properties = new TableProperties();
		properties.setNumColumns( getNumColumns() );
		
		// Add a "Column width" text box for every column.
		addColumnWidthControls( properties );
	}// end onChange()
	
	
	/**
	 * This method gets called when the user types in the "number of rows" or the "column width" text box.
	 * We only allow the user to enter numbers.
	 */
	@Override
	public void onKeyPress( KeyPressEvent event )
	{
    	final TextBox txtBox;
        int keyCode;
    	Object source;
    	Scheduler.RepeatingCommand cmd;
    	
    	// Make sure we are dealing with a text box.
    	source = event.getSource();
    	if ( source instanceof TextBox )
    		txtBox = (TextBox) source;
    	else
    		txtBox = null;

    	if ( txtBox != m_numRowsCtrl )
    	{
	        // Allow the user to enter '*'.  That is a valid column width.
	        if ( event.getCharCode() == '*' )
	        {
	        	// Remove any characters from the text box.
	        	if ( txtBox != null )
	        	{
	        		txtBox.setValue( "*" );
	        		txtBox.cancelKey();
	        		
	        		danceWidthUnits( txtBox );
	        	}
	
	        	return;
	        }
    	}
        
        // Get the key the user pressed
        keyCode = event.getNativeEvent().getKeyCode();
        
        if ( GwtClientHelper.isKeyValidForNumericField( event.getCharCode(), keyCode ) == false )
        {
        	// Make sure we are dealing with a text box.
        	if ( txtBox != null )
        	{
        		// Suppress the current keyboard event.
        		txtBox.cancelKey();
        	}
        }

        if ( txtBox != m_numRowsCtrl )
        {
	        cmd = new Scheduler.RepeatingCommand()
	        {
	        	/**
	        	 * 
	        	 */
				@Override
				public boolean execute()
				{
			        danceWidthUnits( txtBox );
			        
			        return false;
				}
			};
			Scheduler.get().scheduleFixedPeriod( cmd, 250 );
        }
	}
	
}// end TableWidgetDlgBox

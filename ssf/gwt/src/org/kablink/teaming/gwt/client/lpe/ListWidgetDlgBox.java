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

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
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
public class ListWidgetDlgBox extends DlgBox
	implements KeyPressHandler
{
	private CheckBox		m_showBorderCkBox = null;
	private TextBox		m_titleTxtBox = null;
	private TextBox m_widthCtrl = null;
	private TextBox m_heightCtrl = null;
	private ListBox m_widthUnitListBox = null;
	private ListBox m_heightUnitListBox = null;
	
	/**
	 * 
	 */
	public ListWidgetDlgBox(
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		ListProperties properties ) // Where properties used in the dialog are read from and saved to.
	{
		super( autoHide, modal, xPos, yPos );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().listProperties(), editSuccessfulHandler, editCanceledHandler, properties ); 
	}// end ListWidgetDlgBox()
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	public Panel createContent( Object props )
	{
		ListProperties properties;
		Label			label;
		VerticalPanel	mainPanel;
		FlexTable		table;
		
		properties = (ListProperties) props;

		mainPanel = new VerticalPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		// Add label and edit control for "Title"
		table = new FlexTable();
		table.setCellSpacing( 8 );
		label = new Label( GwtTeaming.getMessages().title() );
		table.setWidget( 0, 0, label );
		m_titleTxtBox = new TextBox();
		table.setWidget( 0, 1, m_titleTxtBox );
		mainPanel.add( table );
		
		// Add the width and height controls
		{
			FlexTable sizeTable;
			
			sizeTable = new FlexTable();
			
			// Add the label and controls for the width
			{
				sizeTable.setText( 0, 0, GwtTeaming.getMessages().widthLabel() );
				m_widthCtrl = new TextBox();
				m_widthCtrl.addKeyPressHandler( this );
				m_widthCtrl.setVisibleLength( 3 );
				sizeTable.setWidget( 0, 1, m_widthCtrl );

				// Create a listbox that holds the possible units for the width
				{
					m_widthUnitListBox = new ListBox( false );
					m_widthUnitListBox.setVisibleItemCount( 1 );
					
					m_widthUnitListBox.addItem( GwtTeaming.getMessages().percent(), "%" );
					m_widthUnitListBox.addItem( GwtTeaming.getMessages().pxLabel(), "px" );
					
					sizeTable.setWidget( 0, 2, m_widthUnitListBox );
				}
			}
			
			// Add the label and controls for the height
			{
				sizeTable.setText( 1, 0, GwtTeaming.getMessages().heightLabel() );
				m_heightCtrl = new TextBox();
				m_heightCtrl.addKeyPressHandler( this );
				m_heightCtrl.setVisibleLength( 3 );
				sizeTable.setWidget( 1, 1, m_heightCtrl );

				// Create a listbox that holds the possible units for the height
				{
					m_heightUnitListBox = new ListBox( false );
					m_heightUnitListBox.setVisibleItemCount( 1 );
					
					m_heightUnitListBox.addItem( GwtTeaming.getMessages().percent(), "%" );
					m_heightUnitListBox.addItem( GwtTeaming.getMessages().pxLabel(), "px" );
					
					sizeTable.setWidget( 1, 2, m_heightUnitListBox );
				}
			}
			
			mainPanel.add( sizeTable );
		}

		// Add a checkbox for "Show border"
		table = new FlexTable();
		table.setCellSpacing( 8 );
		m_showBorderCkBox = new CheckBox( GwtTeaming.getMessages().showBorder() );
		table.setWidget( 0, 0, m_showBorderCkBox );
		mainPanel.add( table );

		init( properties );
		
		return mainPanel;
	}// end createContent()
	
	
	/**
	 * Get the data from the controls in the dialog box and store the data in the properties obj.
	 */
	public PropertiesObj getDataFromDlg()
	{
		ListProperties	properties;
		
		properties = new ListProperties();
		
		// Save away the "show border" value.
		properties.setShowBorder( getShowBorderValue() );
		
		// Save away the title.
		properties.setTitle( getTitleValue() );
		
		// Get the width and height values
		{
			int width;
			int height;
			Style.Unit units;
			
			// Get the width
			width = getWidth();
			units = getWidthUnits();
			if ( width == 0 )
			{
				// Default to 100%
				width = 100;
				units = Style.Unit.PCT;
			}
			properties.setWidth( width );
			properties.setWidthUnits( units );
			
			// Get the height
			height = getHeight();
			units = getHeightUnits();
			if ( height == 0 )
			{
				// Default to 100%
				height = 100;
				units = Style.Unit.PCT;
			}
			properties.setHeight( height );
			properties.setHeightUnits( units );
		}

		return properties;
	}// end getDataFromDlg()
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	public FocusWidget getFocusWidget()
	{
		return m_titleTxtBox;
	}// end getFocusWidget()
	
	
	/**
	 * 
	 */
	private int getHeight()
	{
		int height = 0;
		String txt;
		
		// Yes
		txt = m_heightCtrl.getText();
		if ( txt != null && txt.length() > 0 )
		{
			try
			{
				height = Integer.parseInt( txt );
			}
			catch ( NumberFormatException nfEx )
			{
				// This should never happen.  The data should be validated before we get to this point.
			}
		}
		
		return height;
	}
	
	/**
	 * 
	 */
	private Style.Unit getHeightUnits()
	{
		Style.Unit unit = Style.Unit.PCT;
		int selectedIndex;
		String value;
		
		// Yes
		// Get the selected index from the listbox that holds the list of units.
		selectedIndex = m_heightUnitListBox.getSelectedIndex();
		if ( selectedIndex < 0 )
			selectedIndex = 0;
		
		value = m_heightUnitListBox.getValue( selectedIndex );
		if ( value != null && value.equalsIgnoreCase( "%" ) )
			unit = Style.Unit.PCT;
		else
			unit = Style.Unit.PX;
		
		return unit;
	}
	

	/**
	 * Return true if the "show border" checkbox is checked.
	 */
	public boolean getShowBorderValue()
	{
		return m_showBorderCkBox.getValue().booleanValue();
	}// end getShowBorderValue()
	
	
	/**
	 * Return the text found in the title edit control.
	 */
	public String getTitleValue()
	{
		return m_titleTxtBox.getText();
	}// end getTitleValue()
	

	/**
	 * 
	 */
	private int getWidth()
	{
		int width = 0;
		String txt;
		
		// Yes
		txt = m_widthCtrl.getText();
		if ( txt != null && txt.length() > 0 )
		{
			try
			{
				width = Integer.parseInt( txt );
			}
			catch ( NumberFormatException nfEx )
			{
				// This should never happen.  The data should be validated before we get to this point.
			}
		}
		
		return width;
	}
	
	/**
	 * 
	 */
	private Style.Unit getWidthUnits()
	{
		Style.Unit unit = Style.Unit.PCT;
		int selectedIndex;
		String value;
		
		// Yes
		// Get the selected index from the listbox that holds the list of units.
		selectedIndex = m_widthUnitListBox.getSelectedIndex();
		if ( selectedIndex < 0 )
			selectedIndex = 0;
		
		value = m_widthUnitListBox.getValue( selectedIndex );
		if ( value != null && value.equalsIgnoreCase( "%" ) )
			unit = Style.Unit.PCT;
		else
			unit = Style.Unit.PX;
		
		return unit;
	}
	

	/**
	 * Initialize the controls in the dialog with the values from the properties
	 */
	public void init( PropertiesObj props )
	{
		ListProperties properties;
		
		properties = (ListProperties) props;

		m_showBorderCkBox.setValue( properties.getShowBorderValue() );
		m_titleTxtBox.setText( properties.getTitle() );

		// Initialize the width controls.
		initWidthControls( properties );
		
		// Initialize the height controls.
		initHeightControls( properties );

	}// end init()
	
	/**
	 * Initialize the controls dealing with the height.
	 */
	private void initHeightControls( ListProperties properties )
	{
		int i;
		String unitValue;
		
		m_heightCtrl.setText( String.valueOf( properties.getHeight() ) );
		
		if ( properties.getHeightUnits() == Style.Unit.PCT )
			unitValue = "%";
		else
			unitValue = "px";

		// Select the appropriate unit in the listbox.
		for (i = 0; i < m_heightUnitListBox.getItemCount(); ++i)
		{
			String nextUnit;
			
			nextUnit = m_heightUnitListBox.getValue( i );
			if ( nextUnit != null && nextUnit.equalsIgnoreCase( unitValue ) )
			{
				m_heightUnitListBox.setSelectedIndex( i );
				break;
			}
		}
	}

	/**
	 * Initialize the controls dealing with the width.
	 */
	private void initWidthControls( ListProperties properties )
	{
		int i;
		String unitValue;
		
		m_widthCtrl.setText( String.valueOf( properties.getWidth() ) );

		if ( properties.getWidthUnits() == Style.Unit.PCT )
			unitValue = "%";
		else
			unitValue = "px";

		// Select the appropriate unit in the listbox.
		for (i = 0; i < m_widthUnitListBox.getItemCount(); ++i)
		{
			String nextUnit;
			
			nextUnit = m_widthUnitListBox.getValue( i );
			if ( nextUnit != null && nextUnit.equalsIgnoreCase( unitValue ) )
			{
				m_widthUnitListBox.setSelectedIndex( i );
				break;
			}
		}
	}

	/**
	 * This method gets called when the user types in the "width" or "height" text box.
	 * We only allow the user to enter numbers.
	 */
	public void onKeyPress( KeyPressEvent event )
	{
        int keyCode;

        // Get the key the user pressed
        keyCode = event.getNativeEvent().getKeyCode();
        
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
	}

}// end ListWidgetDlgBox

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
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
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
public class GraphicWidgetDlgBox extends DlgBox
	implements KeyPressHandler
{
	private CheckBox m_showBorderCkBox = null;
	private ListBox m_graphicListBox = null;
	private CheckBox m_setImgSizeCkBox = null;
	private TextBox m_widthCtrl = null;
	private TextBox m_heightCtrl = null;
	private ListBox m_widthUnitListBox = null;
	private ListBox m_heightUnitListBox = null;
	
	/**
	 * 
	 */
	public GraphicWidgetDlgBox(
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		GraphicProperties properties ) // Where properties used in the dialog are read from and saved to.
	{
		super( autoHide, modal, xPos, yPos );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().graphicProperties(), editSuccessfulHandler, editCanceledHandler, properties ); 
	}// end GraphicWidgetDlgBox()
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		GraphicProperties properties;
		Label			label;
		VerticalPanel	mainPanel;
		FlexTable		table;
		FileAttachments fileAttachments;
		int numAttachments;
		int i;
		
		properties = (GraphicProperties) props;

		mainPanel = new VerticalPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		table = new FlexTable();
		table.setCellSpacing( 2 );

		// Get the number of file attachments.
		fileAttachments = LandingPageEditor.getFileAttachments();
		numAttachments = fileAttachments.getNumAttachments();
		
		// Do we have any file attachments?
		if ( numAttachments > 0 )
		{
			// Yes
			// Add the "Graphic" label.
			label = new Label( GwtTeaming.getMessages().selectGraphicLabel() );
			table.setWidget( 0, 0, label );

			// Create a list box to hold the list of graphics.
			m_graphicListBox = new ListBox( false );
			m_graphicListBox.setVisibleItemCount( 1 );
			table.setWidget( 1, 0, m_graphicListBox );
			
			// Fill the list box with the names of the file attachments.
			for (i = 0; i < numAttachments; ++i)
			{
				String fileName;
				String fileId;
				
				fileName = fileAttachments.getFileName( i );
				fileId = fileAttachments.getFileId( i );
				m_graphicListBox.addItem( fileName, fileId );
			}
		}
		else
		{
			Label noAttachmentsLabel;
			
			// Add some text that informs the user there are no attachments.
			noAttachmentsLabel = new Label( GwtTeaming.getMessages().noFileAttachmentsHint() );
			table.setWidget( 1, 0, noAttachmentsLabel );
		}
		
		mainPanel.add( table );
		
		// Add controls for stretching the width/height of the image
		{
			FlowPanel panel;
			FlexTable sizeTable;
			
			m_setImgSizeCkBox = new CheckBox( GwtTeaming.getMessages().editGraphicPropertiesDlgSetImageSize() );
			m_setImgSizeCkBox.addClickHandler( new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent event )
				{
					danceDlg();
				}
			} );
			table.setWidget( 2, 0, m_setImgSizeCkBox );
			
			// Add a panel to hold the text box and combobox
			panel = new FlowPanel();
			panel.addStyleName( "marginleft2" );
			
			sizeTable = new FlexTable();
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
						
			
			//sizeTable.setText( 1, 2, GwtTeaming.getMessages().pxLabel() );

			panel.add( sizeTable );
			table.setWidget( 3, 0, panel );
		}

		// Add a checkbox for "Show border"
		table = new FlexTable();
		table.setCellSpacing( 4 );
		m_showBorderCkBox = new CheckBox( GwtTeaming.getMessages().showBorder() );
		table.setWidget( 0, 0, m_showBorderCkBox );
		mainPanel.add( table );

		// Initialize the controls in the dialog with the values from the properties.
		init( properties );
		
		return mainPanel;
	}// end createContent()
	
	
	/**
	 * Dance the controls in this dialog that deal with width and height.
	 */
	private void danceDlg()
	{
		boolean enabled;
		
		// Enable/disable the width and height controls depending on whether "set image size" is checked.
		enabled = m_setImgSizeCkBox.getValue();
		
		m_widthCtrl.setEnabled( enabled );
		m_widthUnitListBox.setEnabled( enabled );
		m_heightCtrl.setEnabled( enabled );
		m_heightUnitListBox.setEnabled( enabled );
	}
	
	/**
	 * Get the data from the controls in the dialog box and store the data in the properties obj.
	 */
	@Override
	public PropertiesObj getDataFromDlg()
	{
		GraphicProperties	properties;
		
		properties = new GraphicProperties();
		
		// Save away the "show border" value.
		properties.setShowBorder( getShowBorderValue() );
		
		// Save away the information about the selected graphic.
		properties.setGraphicId( getGraphicId() );
		properties.setGraphicName( getGraphicName() );
		
		// Get the width and height values
		{
			int width;
			int height;
			Style.Unit units;
			
			// Get the width
			width = getWidth();
			units = getWidthUnits();
			properties.setWidth( width );
			properties.setWidthUnits( units );
			
			// Get the height
			height = getHeight();
			units=getHeightUnits();			
			properties.setHeight( height );
			properties.setHeightUnits( units );
			
			properties.setOverflow( getOverflow() );
		}

		return properties;
	}// end getDataFromDlg()

	
	/**
	 * Return the id of the selected graphic.
	 */
	private String getGraphicId()
	{
		int index;
		
		// Do we have any file attachments?
		if ( m_graphicListBox == null )
		{
			// No
			return null;
		}
		
		// Get the index of the selected graphic.
		index = m_graphicListBox.getSelectedIndex();
		if ( index >= 0 )
			return m_graphicListBox.getValue( index );

		// If we get here, a graphic has not been selected.
		return null;
	}// end getGraphicId()
	
	
	/**
	 * Return the name of the selected graphic.
	 */
	private String getGraphicName()
	{
		int index;
		
		// Do we have any file attachments?
		if ( m_graphicListBox == null )
		{
			// No
			return null;
		}
		
		// Get the index of the selected graphic.
		index = m_graphicListBox.getSelectedIndex();
		if ( index >= 0 )
			return m_graphicListBox.getItemText( index );

		// If we get here, a graphic has not been selected.
		return null;
	}
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		if ( m_graphicListBox != null )
			return m_graphicListBox;
		
		return m_showBorderCkBox;
	}// end getFocusWidget()
	
	
	/**
	 * 
	 */
	private int getHeight()
	{
		int height;
		
		height = -1;
		
		// Is the "set image size" checkbox checked?
		if ( m_setImgSizeCkBox.getValue() == true )
		{
			String txt;
			
			// Yes
			txt = m_heightCtrl.getText();
			if ( txt != null && txt.length() > 0 )
			{
				try
				{
					height = Integer.parseInt( txt );
					if ( height == 0 )
						height = -1;
				}
				catch ( NumberFormatException nfEx )
				{
					// This should never happen.  The data should be validated before we get to this point.
				}
			}
		}
		
		return height;
	}
	
	/**
	 * 
	 */
	private Style.Overflow getOverflow()
	{
		return Style.Overflow.AUTO;
	}

	/**
	 * Return true if the "show border" checkbox is checked.
	 */
	public boolean getShowBorderValue()
	{
		return m_showBorderCkBox.getValue().booleanValue();
	}// end getShowBorderValue()
	
	
	/**
	 * 
	 */
	private int getWidth()
	{
		int width;
		
		width = -1;
		
		// Is the "stretch width" checkbox checked?
		if ( m_setImgSizeCkBox.getValue() == true )
		{
			String txt;
			
			// Yes
			txt = m_widthCtrl.getText();
			if ( txt != null && txt.length() > 0 )
			{
				try
				{
					width = Integer.parseInt( txt );
					if ( width == 0 )
						width = -1;
				}
				catch ( NumberFormatException nfEx )
				{
					// This should never happen.  The data should be validated before we get to this point.
				}
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
	 * Initialize the controls on the page with the values from the properties.
	 */
	public void init(
		PropertiesObj props )
	{
		GraphicProperties properties;
		String fileId;
		int selectedIndex = 0;
		int i;
		
		properties = (GraphicProperties) props;

		if ( m_graphicListBox != null )
		{
			// Select the graphic in the ListBox.
			for (i = 0; i < m_graphicListBox.getItemCount(); ++i)
			{
				fileId = m_graphicListBox.getValue( i );
				
				// Is this graphic the currently selected graphic?
				if ( fileId.equalsIgnoreCase( properties.getGraphicId() ) )
				{
					selectedIndex = i;
					break;
				}
			}
			
			// Select the appropriate graphic.
			m_graphicListBox.setSelectedIndex( selectedIndex );
		}
		
		m_showBorderCkBox.setValue( properties.getShowBorderValue() );

		// Initialize the controls dealing with width and height
		{
			Style.Unit widthUnits;
			Style.Unit heightUnits;
			String unitValue;
			int width;
			int height;
			
			height = properties.getHeight();
			width = properties.getWidth();
			widthUnits = properties.getWidthUnits();
			heightUnits=properties.getHeightUnits();
			
			if ( width > 0 || height > 0 )
			{
				m_setImgSizeCkBox.setValue( true );
				
				if ( width > 0 )
					m_widthCtrl.setText( String.valueOf( width ) );
				
				if ( height > 0 )
					m_heightCtrl.setText( String.valueOf( height ) );
			}
			else
			{
				m_setImgSizeCkBox.setValue( false );
				m_widthCtrl.setText( "" );
				m_heightCtrl.setText( "" );
			}

			if ( widthUnits == Style.Unit.PCT )
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
			
			if ( heightUnits == Style.Unit.PCT )
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
		
		// Dance the controls on the dialog.
		danceDlg();

	}// end init()

	/**
	 * This method gets called when the user types in the "width" or "height" text box.
	 * We only allow the user to enter numbers.
	 */
	@Override
	public void onKeyPress( KeyPressEvent event )
	{
        int keyCode;

        // Get the key the user pressed
        keyCode = event.getNativeEvent().getKeyCode();
        
        if ( GwtClientHelper.isKeyValidForNumericField( event.getCharCode(), keyCode ) == false )
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
}// end GraphicWidgetDlgBox

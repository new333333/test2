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

import com.google.gwt.user.client.ui.*;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;


/**
 * This control is used to specify a width, height and the overflow preference 
 * @author jwootton
 *
 */
public class SizeCtrl extends VibeWidget
	implements KeyPressHandler
{
	private TextBox m_widthCtrl = null;
	private TextBox m_heightCtrl = null;
	private InlineLabel m_heightLabel = null;
	private InlineLabel m_heightPxLabel = null;
	private ListBox m_widthUnitListBox = null;
	private CheckBox m_overflowCheckbox = null;
	private InlineLabel m_overflowLabel = null;
	private ListBox m_overflowListBox = null;

	public SizeCtrl() {
		this(false);
	}

	/**
	 * 
	 */
	public SizeCtrl(boolean fullOverflowDetails)
	{
		VibeFlowPanel mainPanel;
		
		mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName( "sizeCtrlMainPanel" );
		
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
			m_heightLabel = new InlineLabel( GwtTeaming.getMessages().heightLabel() );
			sizeTable.setWidget( 1, 0, m_heightLabel );
			
			m_heightCtrl = new TextBox();
			m_heightCtrl.addKeyPressHandler( this );
			m_heightCtrl.setVisibleLength( 3 );
			sizeTable.setWidget( 1, 1, m_heightCtrl );

			// Height can only be specified in pixels.  Add "px" after the text box.
			m_heightPxLabel = new InlineLabel( GwtTeaming.getMessages().pxLabel() );
			sizeTable.setWidget( 1, 2, m_heightPxLabel );
		}

		if (fullOverflowDetails)
		{
			m_overflowLabel = new InlineLabel( GwtTeaming.getMessages().overflowShortLabel("overflow") );
			sizeTable.getFlexCellFormatter().setColSpan( 2, 0, 2 );
			sizeTable.setWidget( 2, 0, m_overflowLabel );
			m_overflowListBox = new ListBox(false);
			m_overflowListBox.setVisibleItemCount(1);
			m_overflowListBox.addItem( "auto", "auto");
			m_overflowListBox.addItem( "hidden", "hidden");
			m_overflowListBox.addItem( "scroll", "scroll");
			m_overflowListBox.addItem( "visible", "visible");
			sizeTable.setWidget( 2, 1, m_overflowListBox);
		}
		else
		// Add the "Show scroll bars when necessary"
		{
			m_overflowCheckbox = new CheckBox( GwtTeaming.getMessages().overflowLabel() );
			sizeTable.getFlexCellFormatter().setColSpan( 2, 0, 3 );
			sizeTable.setWidget( 2, 0, m_overflowCheckbox );

		}

		mainPanel.add( sizeTable );
		
		initWidget( mainPanel );
	}
	
	/**
	 * 
	 */
	public int getHeight()
	{
		int height = -1;
		
		// Is the height control visible?
		if ( m_heightCtrl.isVisible() )
		{
			String txt;

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
	public Style.Unit getHeightUnits()
	{
		// Height must always be specified in pixels
		return Style.Unit.PX;
	}
	
	/**
	 * 
	 */
	public Style.Overflow getOverflow()
	{
		if (m_overflowListBox!=null) {
			String value = m_overflowListBox.getSelectedValue();
			for (Style.Overflow overflow : Overflow.values()) {
				if (value.equals(overflow.getCssName())) {
					return overflow;
				}
			}
		} else if (m_overflowCheckbox!=null && m_overflowCheckbox.getValue() == Boolean.TRUE) {
			return Style.Overflow.AUTO;
		}
		return Overflow.HIDDEN;

	}
	
	/**
	 * 
	 */
	public int getWidth()
	{
		int width = -1;
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
		
		return width;
	}
	
	/**
	 * 
	 */
	public Style.Unit getWidthUnits()
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
	public void hideHeightControls()
	{
		m_heightLabel.setVisible( false );
		m_heightCtrl.setVisible( false );
		m_heightPxLabel.setVisible( false );
	}
	
	/**
	 * 
	 */
	public void hideOverflowCheckbox()
	{
		if ( m_overflowCheckbox != null )
			m_overflowCheckbox.setVisible( false );
		if (m_overflowListBox!=null) {
			m_overflowLabel.setVisible(false);
			m_overflowListBox.setVisible( false );
		}
	}
	
	/**
	 * Initialize the width and height controls.
	 */
	public void init( int width, Style.Unit widthUnits, int height, Style.Unit heightUnits, Style.Overflow overflow )
	{
		initWidthControls( width, widthUnits );
		initHeightControls( height, heightUnits );

		if (m_overflowCheckbox!=null) {
			if ( overflow == Overflow.AUTO )
				m_overflowCheckbox.setValue( true );
			else
				m_overflowCheckbox.setValue( false );
		} else {
			String overflowStr = overflow.getCssName();
			// Select the appropriate value in the listbox.
			for (int i = 0; i < m_overflowListBox.getItemCount(); ++i) {
				String nextValue;

				nextValue = m_overflowListBox.getValue(i);
				if (nextValue != null && nextValue.equalsIgnoreCase(overflowStr)) {
					m_overflowListBox.setSelectedIndex(i);
					break;
				}
			}
		}
	}
	
	/**
	 * Initialize the controls dealing with the height.
	 */
	private void initHeightControls( int height, Style.Unit heightUnits )
	{
		if ( height > 0 )
			m_heightCtrl.setText( String.valueOf( height ) );
		else
			m_heightCtrl.setText( "" );

		// Ignore the heightUnits param.  Height must always be specified in pixels.
	}

	/**
	 * Initialize the controls dealing with the width.
	 */
	private void initWidthControls( int width, Style.Unit widthUnits )
	{
		int i;
		String unitValue;
		
		if ( width > 0 )
			m_widthCtrl.setText( String.valueOf( width ) );
		else
			m_widthCtrl.setText( "" );

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
	}

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

	/**
	 * 
	 */
	public void showHeightControls()
	{
		m_heightLabel.setVisible( true );
		m_heightCtrl.setVisible( true );
		m_heightPxLabel.setVisible( true );
	}
}

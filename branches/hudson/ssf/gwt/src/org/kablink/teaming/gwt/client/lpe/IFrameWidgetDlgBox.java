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
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.lpe.IFrameProperties.ScrollbarValue;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.InlineLabel;
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
public class IFrameWidgetDlgBox extends DlgBox
	implements KeyPressHandler
{
	private TextBox m_urlTxtBox;
	private TextBox m_titleTxtBox;
	private TextBox m_frameNameTxtBox;
	private TextBox m_heightTxtBox;
	private TextBox m_widthTxtBox;
	private CheckBox m_frameBorderCkBox;
	private ListBox m_scrollbarListBox;
	
	/**
	 * 
	 */
	public IFrameWidgetDlgBox(
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		IFrameProperties properties ) // Where properties used in the dialog are read from and saved to.
	{
		super( autoHide, modal, xPos, yPos );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().iframeProperties(), editSuccessfulHandler, editCanceledHandler, properties ); 
	}
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		IFrameProperties properties;
		Label			label;
		InlineLabel inlineLabel;
		VerticalPanel	mainPanel;
		FlexTable		table;
		FlowPanel		panel;
		int row;
		GwtTeamingMessages messages;
		
		messages = GwtTeaming.getMessages();
		
		properties = (IFrameProperties) props;

		mainPanel = new VerticalPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		table = new FlexTable();
		table.setCellSpacing( 2 );
		row = 0;

		mainPanel.add( table );
		
		// Add label and edit control for "URL"
		label = new Label( messages.urlLabel() );
		table.setWidget( row, 0, label );
		++row;
		m_urlTxtBox = new TextBox();
		m_urlTxtBox.setVisibleLength( 50 );
		table.setWidget( row, 0, m_urlTxtBox );
		++row;
		
		table = new FlexTable();
		table.setCellSpacing( 2 );
		row = 0;

		mainPanel.add( table );
		
		// Add lable and edit control for the title.
		label = new Label( messages.title() );
		table.setWidget( row, 0, label );
		m_titleTxtBox = new TextBox();
		m_titleTxtBox.setVisibleLength( 30 );
		table.setWidget( row, 1, m_titleTxtBox );
		++row;
		
		// Add label and edit control for frame name.
		label = new Label( messages.frameNameLabel() );
		table.setWidget( row, 0, label );
		m_frameNameTxtBox = new TextBox();
		m_frameNameTxtBox.setVisibleLength( 30 );
		table.setWidget( row, 1, m_frameNameTxtBox );
		++row;
		
		// Add an empty div for space.
		{
			Label spacer;
			
			spacer = new Label();
			spacer.getElement().getStyle().setWidth( 10, Unit.PX );
			spacer.getElement().getStyle().setHeight( 4, Unit.PX );
			table.setWidget( row, 0, spacer );
			++row;
		}
		
		// Add label and edit control for height
		label = new Label( messages.heightLabel() );
		table.setWidget( row, 0, label );
		m_heightTxtBox = new TextBox();
		m_heightTxtBox.setVisibleLength( 4 );
		m_heightTxtBox.addKeyPressHandler( this );
		panel = new FlowPanel();
		panel.add( m_heightTxtBox );
		inlineLabel = new InlineLabel( messages.pxLabel() );
		panel.add( inlineLabel );
		table.setWidget( row, 1, panel );
		++row;

		// Add a label and edit control for width
		label = new Label( messages.widthLabel() );
		table.setWidget( row, 0, label );
		m_widthTxtBox = new TextBox();
		m_widthTxtBox.setVisibleLength( 4 );
		m_widthTxtBox.addKeyPressHandler( this );
		panel = new FlowPanel();
		panel.add( m_widthTxtBox );
		inlineLabel = new InlineLabel( messages.pxLabel() );
		panel.add( inlineLabel );
		table.setWidget( row, 1, panel );
		++row;
		
		// Add an empty div for space.
		{
			Label spacer;
			
			spacer = new Label();
			spacer.getElement().getStyle().setWidth( 10, Unit.PX );
			spacer.getElement().getStyle().setHeight( 4, Unit.PX );
			table.setWidget( row, 0, spacer );
			++row;
		}
		
		// Add a listbox for the scrollbar value.
		{
			label = new Label( messages.showScrollbarsLabel() );
			table.setWidget( row, 0, label );
			
			m_scrollbarListBox = new ListBox();
			m_scrollbarListBox.setVisibleItemCount( 1 );
			m_scrollbarListBox.addItem( messages.showScrollbars_Always(), "yes" );
			m_scrollbarListBox.addItem( messages.showScrollbars_Never(), "no" );
			m_scrollbarListBox.addItem( messages.showScrollbars_Auto(), "auto" );
			
			table.setWidget( row, 1, m_scrollbarListBox );
			++row;
		}
		
		// Add a label and checkbox for frame border
		m_frameBorderCkBox = new CheckBox( messages.showBorder() );
		table.setWidget( row, 0, m_frameBorderCkBox );
		++row;
		
		init( properties );
		
		return mainPanel;
	}
	
	
	/**
	 * Get the data from the controls in the dialog box and store the data in the properties obj.
	 */
	@Override
	public PropertiesObj getDataFromDlg()
	{
		IFrameProperties	properties;
		
		properties = new IFrameProperties();
		
		// Save away the url
		properties.setUrl( getUrlValue() );
		
		// Save away the title.
		properties.setTitle( m_titleTxtBox.getText() );
		
		// Save the frame name.
		properties.setName( m_frameNameTxtBox.getText() );
		
		// Save the height.
		properties.setHeight( m_heightTxtBox.getText() );
		
		// Save the width.
		properties.setWidth( m_widthTxtBox.getText() );
		
		// Save whether or not the frame should have a border
		properties.setShowBorder( m_frameBorderCkBox.getValue().booleanValue() );
		
		// Save whether to scrollbar setting
		{
			int index;
			String value;
			
			value = "auto";
			
			// Get the selected setting.
			index = m_scrollbarListBox.getSelectedIndex();
			if ( index != -1 )
				value = m_scrollbarListBox.getValue( index );
			
			properties.setScrollbarValue( value );
		}
		
		return properties;
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return m_urlTxtBox;
	}
	
	
	/**
	 * Return the text found in the url edit control.
	 */
	public String getUrlValue()
	{
		return m_urlTxtBox.getText();
	}
	

	/**
	 * Initialize the controls in the dialog with the values from the properties
	 */
	public void init( PropertiesObj props )
	{
		IFrameProperties properties;
		String value;
		
		properties = (IFrameProperties) props;

		value = properties.getUrl();
		if ( value == null || value.length() == 0 )
			value = "http://";
		m_urlTxtBox.setText( value );
		
		value = properties.getTitle();
		if ( value == null )
			value = "";
		m_titleTxtBox.setText( value );
		
		value = properties.getName();
		if ( value == null )
			value = "";
		m_frameNameTxtBox.setText( value );
		
		value = properties.getHeightAsString();
		if ( value == null )
			value = "";
		m_heightTxtBox.setText( value );
		
		value = properties.getWidthAsString();
		if ( value == null )
			value = "";
		m_widthTxtBox.setText( value );
		
		// Select the appropriate option in the list box
		selectScrollbarValue( properties.getScrollbarValue() );

		if ( properties.getShowBorder() )
			m_frameBorderCkBox.setValue( Boolean.TRUE );
		else
			m_frameBorderCkBox.setValue( Boolean.FALSE );
	}
	
	/**
	 * This method gets called when the user types in the height, width, text boxes.
	 * We only want to let the user enter numbers.
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
	public void selectScrollbarValue( ScrollbarValue value )
	{
		if ( value == ScrollbarValue.ALWAYS )
			m_scrollbarListBox.setItemSelected( 0, true );
		else if ( value == ScrollbarValue.NEVER )
			m_scrollbarListBox.setItemSelected( 1, true );
		else if ( value == ScrollbarValue.AUTO )
			m_scrollbarListBox.setItemSelected( 2, true );
		else
			m_scrollbarListBox.setItemSelected( 0, true );
	}
}

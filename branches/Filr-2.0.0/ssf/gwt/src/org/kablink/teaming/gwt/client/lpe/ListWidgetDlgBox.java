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
import org.kablink.teaming.gwt.client.widgets.SizeCtrl;

import com.google.gwt.dom.client.Style;
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
public class ListWidgetDlgBox extends DlgBox
{
	private CheckBox		m_showBorderCkBox = null;
	private TextBox		m_titleTxtBox = null;
	private SizeCtrl m_sizeCtrl = null;
	
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
			m_sizeCtrl = new SizeCtrl();
			mainPanel.add( m_sizeCtrl );
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
			properties.setOverflow( getOverflow() );
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
		return m_sizeCtrl.getHeight();
	}
	
	/**
	 * 
	 */
	private Style.Unit getHeightUnits()
	{
		return m_sizeCtrl.getHeightUnits();
	}
	
	/**
	 * 
	 */
	private Style.Overflow getOverflow()
	{
		return m_sizeCtrl.getOverflow();
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
		return m_sizeCtrl.getWidth();
	}
	
	/**
	 * 
	 */
	private Style.Unit getWidthUnits()
	{
		return m_sizeCtrl.getWidthUnits();
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

		// Initialize the size control.
		m_sizeCtrl.init( properties.getWidth(), properties.getWidthUnits(), properties.getHeight(), properties.getHeightUnits(), properties.getOverflow() );

	}// end init()
}// end ListWidgetDlgBox

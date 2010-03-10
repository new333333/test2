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

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtBrandingData;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;


/**
 * 
 * @author jwootton
 *
 */
public class EditBrandingDlg extends DlgBox
{
	private RadioButton m_useBrandingImgRb;
	private RadioButton m_useAdvancedBrandingRb;
	private ListBox m_brandingImgListbox;
	private ListBox m_backgroundImgListbox;
	private RadioButton m_noRepeatRb;
	private RadioButton m_repeatRb;
	private RadioButton m_repeatXRb;
	private RadioButton m_repeatYRb;
	private TextBox m_backgroundColorTextbox;
	private TextBox m_textColorTextbox;

	/**
	 * 
	 */
	public EditBrandingDlg(
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		GwtBrandingData brandingData ) // Where properties used in the dialog are read from.
	{
		super( autoHide, modal, xPos, yPos );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().brandingDlgHeader(), editSuccessfulHandler, editCanceledHandler, brandingData ); 
	}// end EditBrandingDlg()
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	public Panel createContent( Object props )
	{
		FlowPanel mainPanel = null;
		FlexTable table = null;
		GwtBrandingData brandingData;
		HorizontalPanel hPanel;
		int nextRow;
		
		brandingData = (GwtBrandingData) props;

		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		table = new FlexTable();
		table.setCellSpacing( 4 );
		table.addStyleName( "dlgContent" );
		
		nextRow = 0;
		
		// Add the controls for "Use Branding image"
		{
			m_useBrandingImgRb = new RadioButton( "brandingType", GwtTeaming.getMessages().useBrandingImgLabel() );
			table.setWidget( nextRow, 0, m_useBrandingImgRb );

			// Create a list box to hold the list of attachments for the given binder.
			// The user can select one of these files to use as the branding image.
			m_brandingImgListbox = new ListBox( false );
			m_brandingImgListbox.setVisibleItemCount( 1 );
			m_brandingImgListbox.addItem( GwtTeaming.getMessages().noImagesAvailable() );
			table.setWidget( nextRow, 1, m_brandingImgListbox );
			++nextRow;
		}
		
		// Add the controls for "Use Advanced Branding"
		{
			m_useAdvancedBrandingRb = new RadioButton( "brandingType", GwtTeaming.getMessages().useAdvancedBrandingLabel() );
			table.setWidget( nextRow, 0, m_useAdvancedBrandingRb );
			++nextRow;
		}
		
		// Add the controls for "Background Image"
		{
			table.setText( nextRow, 0, GwtTeaming.getMessages().backgroundImgLabel() );

			// Create a list box to hold the list of attachments for the given binder.
			// User user can select of of these files to use as the background image.
			m_backgroundImgListbox = new ListBox( false );
			m_backgroundImgListbox.setVisibleItemCount( 1 );
			m_backgroundImgListbox.addStyleName( "marginTop5" );
			m_backgroundImgListbox.addItem( GwtTeaming.getMessages().noImagesAvailable() );
			table.setWidget( nextRow, 1, m_backgroundImgListbox );
			++nextRow;
			
			// Add the radio buttons that will be used to select how the background image repeats.
			m_noRepeatRb = new RadioButton( "backgroundRepeat", GwtTeaming.getMessages().noRepeatLabel() );
			m_repeatRb = new RadioButton( "backgroundRepeat", GwtTeaming.getMessages().repeatLabel() );
			m_repeatXRb = new RadioButton( "backgroundRepeat", GwtTeaming.getMessages().repeatXLabel() );
			m_repeatYRb = new RadioButton( "backgroundRepeat", GwtTeaming.getMessages().repeatYLabel() );
			hPanel = new HorizontalPanel();
			hPanel.add( m_noRepeatRb );
			hPanel.add( m_repeatRb );
			hPanel.add( m_repeatXRb );
			hPanel.add( m_repeatYRb );
			table.setWidget( nextRow, 1, hPanel );
			++nextRow;
		}
		
		// Add the controls for "Background color"
		{
			table.setText( nextRow, 0, GwtTeaming.getMessages().backgroundColorLabel() );

			// Add a text box where the user can enter the background color
			m_backgroundColorTextbox = new TextBox();
			m_backgroundColorTextbox.setVisibleLength( 20 );
			table.setWidget( nextRow, 1, m_backgroundColorTextbox );
			++nextRow;
		}
		
		// Add the controls for "Text color"
		{
			table.setText( nextRow, 0, GwtTeaming.getMessages().textColorLabel() );

			// Add a text box where the user can enter the font color.
			m_textColorTextbox = new TextBox();
			m_textColorTextbox.setVisibleLength( 20 );
			table.setWidget( nextRow, 1, m_textColorTextbox );
			++nextRow;
		}
		
		mainPanel.add( table );
		
		init( brandingData );

		return mainPanel;
	}// end createContent()
	
	
	/**
	 * Get the data from the controls in the dialog box and store the data in a GwtBrandingData obj.
	 */
	public Object getDataFromDlg()
	{
		GwtBrandingData brandingData;
		
		brandingData = new GwtBrandingData();
		
		return brandingData;
	}// end getDataFromDlg()
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	public FocusWidget getFocusWidget()
	{
		return null;	//!!! Finish
	}// end getFocusWidget()
	
	
	/**
	 * Initialize the controls in the dialog with the values from the branding data.
	 */
	public void init( GwtBrandingData brandingData )
	{
	}// end init()

}// end EditBrandingDlg

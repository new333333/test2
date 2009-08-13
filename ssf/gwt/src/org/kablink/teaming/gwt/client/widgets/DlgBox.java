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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.widgets.EditCanceledHandler;
import org.kablink.teaming.gwt.client.widgets.EditSuccessfulHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * 
 * @author jwootton
 *
 */
public abstract class DlgBox extends DialogBox
	implements ClickHandler
{
	private EditSuccessfulHandler	m_editSuccessfulHandler;	// Handler to call when the user presses Ok.
	private EditCanceledHandler	m_editCanceledHandler;		// Handler to call when the user presses Cancel.
	private PropertiesObj			m_properties;	// Where properties used in the dialog are read from and saved to.
	private Button		m_okBtn;
	private Button		m_cancelBtn;
	
	/**
	 * 
	 */
	public DlgBox(
		String	caption,
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		PropertiesObj properties ) // Where properties used in the dialog are read from and saved to.
	{
		super( autoHide, modal );
		
		FlowPanel	panel;
		Panel		content;
		Panel		header;
		Panel		footer;
		
		panel = new FlowPanel();

		// Associate the panel with its stylesheet.
		panel.addStyleName( "teamingDlgBox" );
		
		// Add the header.
		header = createHeader( caption );
		panel.add( header );
		
		// Add the main content of the dialog box.
		m_properties = properties;
		content = createContent( properties );
		panel.add( content );
		
		// Create the footer.
		footer = createFooter( editSuccessfulHandler, editCanceledHandler );
		panel.add( footer );
		
		setWidget( panel );
		
		setPopupPosition( xPos, yPos );
	}// end DlgBox()
	
	
	/**
	 * Get the Composite that holds the widgets that make up the content of the dialog box.
	 */
	public abstract Panel createContent( PropertiesObj propertiesObj );
	
	
	/*
	 * Create the footer panel for this dialog box.
	 */
	public Panel createFooter( EditSuccessfulHandler editSuccessfulHandler, EditCanceledHandler editCanceledHandler )
	{
		HorizontalPanel	panel;
		
		panel = new HorizontalPanel();
		panel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_RIGHT );
		
		// Associate this panel with its stylesheet.
		panel.addStyleName( "teamingDlgBoxFooter" );
		
		// Remember the handlers to call when the user presses ok or cancel.
		m_editSuccessfulHandler = editSuccessfulHandler;
		m_editCanceledHandler = editCanceledHandler;
		
		m_okBtn = new Button( GwtTeaming.getMessages().ok() );
		m_okBtn.addClickHandler( this );
		panel.add( m_okBtn );
		
		m_cancelBtn = new Button( GwtTeaming.getMessages().cancel() );
		m_cancelBtn.addClickHandler( this );
		panel.add( m_cancelBtn );
		
		return panel;
	}// end createFooter()
	
	
	/**
	 * Get the Panel that holds the dialog box's header.
	 */
	public Panel createHeader( String caption )
	{
		FlowPanel	flowPanel;
		Label		label;
		
		flowPanel = new FlowPanel();
		flowPanel.addStyleName( "teamingDlgBoxHeader" );

		label = new Label( caption );
		label.addStyleName( "head4" );
		flowPanel.add( label );
		
		return flowPanel;
	}// end createHeader()
	
	
	/**
	 * This method will gather up the data from the controls in the dialog box.
	 */
	public abstract void getDataFromDlg( PropertiesObj propertiesObj );
	
	
	/*
	 * This method gets called when the user clicks on the ok or cancel button.
	 */
	public void onClick( ClickEvent event )
	{
		Object	source;
		
		// Get the object that was clicked on.
		source = event.getSource();
		
		// Did the user click on ok?
		if ( source == m_okBtn )
		{
			// Yes
			// Get the data from the controls in the dialog box.
			getDataFromDlg( m_properties );
			
			// Do we have a handler we need to call?
			if ( m_editSuccessfulHandler != null )
			{
				// Yes
				m_editSuccessfulHandler.editSuccessful( m_properties );
			}
			
			return;
		}
		
		// Did the user click on cancel?
		if ( source == m_cancelBtn )
		{
			// Yes
			// Do we have a handler we need to call?
			if ( m_editCanceledHandler != null )
			{
				// Yes
				m_editCanceledHandler.editCanceled();
			}
		}
	}// end onClick()
}// end DlgBox

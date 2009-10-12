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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * 
 * @author jwootton
 *
 */
public abstract class DlgBox extends PopupPanel
	implements ClickHandler
{
	private EditSuccessfulHandler	m_editSuccessfulHandler;	// Handler to call when the user presses Ok.
	private EditCanceledHandler	m_editCanceledHandler;		// Handler to call when the user presses Cancel.
	private Button		m_okBtn;
	private Button		m_cancelBtn;
	private FocusWidget m_focusWidget;	// Widget that should receive the focus when this dialog is shown.
	
	/**
	 * 
	 */
	public DlgBox(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos )
	{
		super( autoHide, modal );
	
		m_focusWidget = null;
		
		// Override the style used for PopupPanel
		setStyleName( "teamingDlgBox" );
		
		setAnimationEnabled( true );
		
		setPopupPosition( xPos, yPos );
	}// end DlgBox()
	
	
	/**
	 * Get the Composite that holds the widgets that make up the content of the dialog box.
	 */
	public abstract Panel createContent( PropertiesObj propertiesObj );
	
	/**
	 * Create the header, content and footer for the dialog box.
	 */
	public void createAllDlgContent(
		String	caption,
		EditSuccessfulHandler editSuccessfulHandler,// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 	// This gets called when the user presses the Cancel button
		PropertiesObj properties ) 					// Where properties used in the dialog are read from and saved to.
	{
		FlowPanel	panel;
		Panel		content;
		Panel		header;
		Panel		footer;
		
		panel = new FlowPanel();

		// Add the header.
		header = createHeader( caption );
		panel.add( header );
		
		// Add the main content of the dialog box.
		content = createContent( properties );
		panel.add( content );
		
		// Create the footer.
		footer = createFooter();
		panel.add( footer );
		
		// Initialize the handlers
		initHandlers( editSuccessfulHandler, editCanceledHandler );
		
		setWidget( panel );
	}// end createAllDlgContent()

	
	/*
	 * Create the footer panel for this dialog box.
	 */
	public Panel createFooter()
	{
		FlowPanel panel;
		
		panel = new FlowPanel();
		
		// Associate this panel with its stylesheet.
		panel.setStyleName( "teamingDlgBoxFooter" );
		
		m_okBtn = new Button( GwtTeaming.getMessages().ok() );
		m_okBtn.addClickHandler( this );
		m_okBtn.addStyleName( "teamingButton" );
		panel.add( m_okBtn );
		
		m_cancelBtn = new Button( GwtTeaming.getMessages().cancel() );
		m_cancelBtn.addClickHandler( this );
		m_cancelBtn.addStyleName( "teamingButton" );
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
		flowPanel.setStyleName( "teamingDlgBoxHeader" );

		label = new Label( caption );
		label.addStyleName( "head4" );
		flowPanel.add( label );
		
		return flowPanel;
	}// end createHeader()
	
	
	/**
	 * This method will gather up the data from the controls in the dialog box.
	 */
	public abstract PropertiesObj getDataFromDlg();
	
	
	/**
	 * Get the widget that should receive the focus when this dialog is shown.
	 */
	public abstract FocusWidget getFocusWidget();
	
	
	/**
	 * Initialize the controls in the dialog with the values from the PropertiesObj.
	 */
//!!!	public abstract void init( PropertiesObj properties );
	
	/**
	 * Initialize the edit/cancel handlers.
	 */
	public void initHandlers(
		EditSuccessfulHandler editSuccessfulHandler,// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler ) 	// This gets called when the user presses the Cancel button
	{
		// Remember the handlers to call when the user presses ok or cancel.
		m_editSuccessfulHandler = editSuccessfulHandler;
		m_editCanceledHandler = editCanceledHandler;
	}// end initHandlers()
	
	
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
			PropertiesObj props;
			
			// Yes
			// Get the data from the controls in the dialog box.
			props = getDataFromDlg();
			
			// If getDataFromDlg() returns null it means that the data entered by the user
			// is not valid.  getDataFromDlg() will notify the user of problems.
			// Is the data valid?
			if ( props != null )
			{
				// Yes
				// Do we have a handler we need to call?
				if ( m_editSuccessfulHandler != null )
				{
					// Yes
					m_editSuccessfulHandler.editSuccessful( props );
				}
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
	
	
	/**
	 * Show this dialog.
	 */
	public void show()
	{
		// Show this dialog.
		super.show();
		
		// Get the widget that should be given the focus when this dialog is displayed.
		m_focusWidget = getFocusWidget();
		
		// We need to set the focus after the dialog has been shown.  That is why we use a timer. 
		if ( m_focusWidget != null )
		{
			Timer timer;
			
			timer = new Timer()
			{
				/**
				 * 
				 */
				@Override
				public void run()
				{
					if ( m_focusWidget != null )
						m_focusWidget.setFocus( true );
				}// end run()
			};
			
			timer.schedule( 250 );
		}
	}// end show()
}// end DlgBox

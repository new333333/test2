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

import org.kablink.teaming.gwt.client.widgets.EditCanceledHandler;
import org.kablink.teaming.gwt.client.widgets.EditSuccessfulHandler;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * 
 * @author jwootton
 *
 */
public class TableDropWidget extends DropWidget
	implements EditSuccessfulHandler, EditCanceledHandler
{
	private EditSuccessfulHandler	m_editSuccessfulHandler = null;
	private EditCanceledHandler	m_editCanceledHandler = null;
	private TableWidgetDlgBox		m_dlgBox = null;
	
	/**
	 * 
	 */
	public TableDropWidget()
	{
		FlowPanel panel;
		
		panel = new FlowPanel();
		panel.addStyleName( "teamingDlgBoxFooter" );	//!!! Remove this
		panel.add( new Label( "This is the TableDropWidget" ) );
		
		// All composites must call initWidget() in their constructors.
		initWidget( panel );
	}// end TableDropWidget()
	
	
	/**
	 * Invoke the Edit Properties dialog for this widget.
	 */
	public void editProperties( EditSuccessfulHandler editSuccessfulHandler, EditCanceledHandler editCanceledHandler, int xPos, int yPos )
	{
		m_editSuccessfulHandler = editSuccessfulHandler;
		m_editCanceledHandler = editCanceledHandler;
		
		// Pass in the object that holds all the properties for a TableDropWidget.
		// properties = new TableDropWidgetProperties();
		m_dlgBox = new TableWidgetDlgBox( this, this, false, true, xPos, yPos, null );
		m_dlgBox.show();
	}// end editProperties()
	
	/**
	 * This method is called when the user presses the cancel button in the properties dialog box.
	 */
	public boolean editCanceled()
	{
		boolean retVal = true;
		
		// Do we have a handler we are supposed to call?
		if ( m_editCanceledHandler != null )
			retVal =  m_editCanceledHandler.editCanceled();

		// If the handler returned false, don't close the dialog.
		if ( retVal )
			m_dlgBox.hide();
		
		return retVal;
	}// end cancelBtnPressed()
	
	/**
	 * This method is called when the user presses the ok button in the properties dialog box.
	 */
	public boolean editSuccessful( Object propertiesObj )
	{
		boolean retVal = true;
		
		// Relayout this widget based on the new properties
		Window.alert( "finish TableDropWidget.editSuccessful()" );
		
		// Do we have a handler we are supposed to call?
		if ( m_editSuccessfulHandler != null )
		{
			// Yes
			retVal = m_editSuccessfulHandler.editSuccessful( (DropWidget)this );
		}
		
		// If the handler returned false, don't close the dialog.
		if ( retVal )
			m_dlgBox.hide();
		
		return retVal;
	}// end editSuccessful()
}// end TableDropWidget

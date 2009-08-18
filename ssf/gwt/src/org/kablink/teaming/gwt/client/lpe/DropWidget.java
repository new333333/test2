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

import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.EditCanceledHandler;
import org.kablink.teaming.gwt.client.widgets.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.user.client.ui.Composite;


/**
 * 
 * @author jwootton
 *
 */
public abstract class DropWidget extends Composite
	implements EditSuccessfulHandler, EditCanceledHandler
{
	private DlgBox					m_dlgBox = null;
	private EditSuccessfulHandler	m_editSuccessfulHandler = null;
	private EditCanceledHandler	m_editCanceledHandler = null;


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
	}// end editCanceled()
	

	/**
	 * 
	 * @param onSuccess
	 * @param onCancel
	 * @param xPos
	 * @param yPos
	 */
	public void editProperties( EditSuccessfulHandler onSuccess, EditCanceledHandler onCancel, int xPos, int yPos )
	{
		m_editSuccessfulHandler = onSuccess;
		m_editCanceledHandler = onCancel;
		
		// Get the dialog box that is used to edit properties for this widget.
		m_dlgBox = getPropertiesDlgBox( xPos, yPos );
		m_dlgBox.show();
	}// end editProperties()
	
	
	/**
	 * This method is called when the user presses the ok button in the properties dialog box.
	 */
	public boolean editSuccessful( Object propertiesObj )
	{
		boolean retVal = true;
		
		// Layout the controls on this widget according to the values found in the properties object.
		updateWidget( (PropertiesObj) propertiesObj );

		// Do we have a handler we are supposed to call?
		if ( m_editSuccessfulHandler != null )
		{
			// Yes, call the handler.
			retVal = m_editSuccessfulHandler.editSuccessful( (DropWidget)this );
		}
		
		// If the handler returned false, don't close the dialog.
		if ( retVal )
			m_dlgBox.hide();
		
		return retVal;
	}// end editSuccessful()

	
	/**
	 * Return the dialog box used to edit the properties of this widget.
	 */
	public abstract DlgBox getPropertiesDlgBox( int xPos, int yPos );
	
	
	/**
	 * Layout the widget according to the values found in the properties object.
	 */
	public abstract void updateWidget( PropertiesObj props );
	
}// end DropWidget

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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.widgets.DeleteHandler;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.EditCanceledHandler;
import org.kablink.teaming.gwt.client.widgets.EditHandler;
import org.kablink.teaming.gwt.client.widgets.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;


/**
 * 
 * @author jwootton
 *
 */
public abstract class DropWidget extends Composite
	implements EditSuccessfulHandler, EditCanceledHandler, EditHandler, DeleteHandler
{
	private DlgBox					m_dlgBox = null;
	private EditSuccessfulHandler	m_editSuccessfulHandler = null;
	private EditCanceledHandler	m_editCanceledHandler = null;
	protected LandingPageEditor	m_lpe = null;


	/**
	 * Create a DropWidget from the given configuration data.
	 * @param lpe
	 * @param configItem
	 * @return
	 */
	public static DropWidget createDropWidget( LandingPageEditor lpe, ConfigItem configItem )
	{
		if ( configItem instanceof CustomJspConfig )
			return new CustomJspDropWidget( lpe, (CustomJspConfig)configItem );
		
		if ( configItem instanceof LinkToUrlConfig )
			return new LinkToUrlDropWidget( lpe, (LinkToUrlConfig)configItem );

		if ( configItem instanceof ListConfig )
			return new ListDropWidget( lpe, (ListConfig)configItem );

		if ( configItem instanceof TableConfig )
			return new TableDropWidget( lpe, (TableConfig)configItem );

		if ( configItem instanceof UtilityElementConfig )
			return new UtilityElementDropWidget( lpe, (UtilityElementConfig)configItem );

		//!!! Add new DropWidgets
		
		// If we get here we didn't recognize the type of widget being requested.
		return null;
	}// end createDropWidget()
	
	
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
		
		// Tell the landing page editor to adjust the height of all the table widgets.
		m_lpe.adjustHeightOfAllTableWidgets();
		
		return retVal;
	}// end editSuccessful()

	
	/**
	 * Return the dialog box used to edit the properties of this widget.
	 */
	public abstract DlgBox getPropertiesDlgBox( int xPos, int yPos );
	
	
	/**
	 * This method will return the following values:
	 * 	-1, the mouse is above this widget
	 *  -2, the mouse is below this widget
	 *   1, the mouse is over the top-half of this widget
	 *   2, the mouse is over the bottom-half of this widget.
	 */
	public int getMousePosOverWidget( int clientY )
	{
		int widgetY;
		int widgetHeight;
		int mouseY;
		
		widgetY = getAbsoluteTop();
		widgetHeight = getOffsetHeight();
		mouseY = clientY + Window.getScrollTop();
		
		// Is the mouse above this widget?
		if ( mouseY < widgetY )
		{
			// Yes
			return -1;
		}
		
		// Is the mouse below this widget?
		if ( mouseY > (widgetY + widgetHeight) )
		{
			// Yes
			return -2;
		}
		
		// If we get here the mouse is over the widget.
		// Is the mouse over the top half of the widget?
		if ( mouseY <= (widgetY + (widgetHeight/2)) )
		{
			// Yes
			return 1;
		}
		
		// If we get here the mouse is over the bottom-half of the widget.
		return 2;
	}// end isMouseOverWidget()
	
	
	/**
	 * This method gets called when the user clicks on the "delete" link.
	 */
	public void onDelete()
	{
		// Ask the user if they really want to delete this widget.
		if ( Window.confirm( GwtTeaming.getMessages().lpeDeleteWidget() ) )
		{
			// Delete this widget
			removeFromParent();

			// Tell the landing page editor to adjust the height of all the table widgets.
			m_lpe.adjustHeightOfAllTableWidgets();
		}
	}// end onDelete()
	
	
	/**
	 * This method gets called when the user clicks on the "edit" link.
	 */
	public void onEdit()
	{
		int x;
		int y;
		
		x = m_lpe.getCanvasLeft() + 5;
		y = m_lpe.getCanvasTop() + 5;
		editProperties( null, null, x, y );
	}// end onEdit()
	
	
	/**
	 * Layout the widget according to the values found in the properties object.
	 */
	public abstract void updateWidget( PropertiesObj props );
	
}// end DropWidget

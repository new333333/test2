/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.DeleteHandler;
import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.DlgBox.DlgBoxClient;
import org.kablink.teaming.gwt.client.widgets.TinyMCEDlg;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;


/**
 * 
 * @author jwootton
 *
 */
public abstract class DropWidget extends Composite
	implements EditSuccessfulHandler, EditCanceledHandler, EditHandler, DeleteHandler, MouseDownHandler
{
	private DlgBox					m_dlgBox = null;
	private EditSuccessfulHandler	m_editSuccessfulHandler = null;
	private EditCanceledHandler	m_editCanceledHandler = null;
	private PopupPanel.PositionCallback m_popupCallback = null;
	private int m_dlgX;
	private int m_dlgY;
	private DropZone m_parentDropZone = null;	// The DropZone this widget lives in.
	protected DragProxy m_dragProxy = null;
	protected LandingPageEditor	m_lpe = null;


	/**
	 * Create a configuration string that represents this widget and that can be stored in the db.
	 */
	public abstract String createConfigString();
	
	
	/**
	 * This method is called when the user presses the cancel button in the properties dialog box.
	 */
	@Override
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
	public void editProperties( EditSuccessfulHandler onSuccess, EditCanceledHandler onCancel, final int xPos, final int yPos )
	{
		m_editSuccessfulHandler = onSuccess;
		m_editCanceledHandler = onCancel;

		// Get the dialog box that is used to edit properties for this widget.
		getPropertiesDlgBox( xPos, yPos, new DlgBoxClient()
		{			
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}// end onUnavailable()
			
			@Override
			public void onSuccess(DlgBox dlg) {
				m_dlgBox = dlg;
				
				// Remember the position where the dialog should be shown.
				m_dlgX = xPos + Window.getScrollLeft();
				m_dlgY = yPos + Window.getScrollTop();
				
				// Create a popup callback if we haven't created one yet.
				if ( m_popupCallback == null )
				{
					m_popupCallback = new PopupPanel.PositionCallback()
					{
						/**
						 * 
						 */
						@Override
						public void setPosition( int offsetWidth, int offsetHeight )
						{
							int canvasRightEdge;
							int canvasBottomEdge;
							int dlgRightEdge;
							int dlgBottomEdge;
							int overlap;
							
							canvasRightEdge = m_lpe.getCanvasLeft() + m_lpe.getCanvasWidth();
							dlgRightEdge = m_dlgX + offsetWidth;
							
							// If we position the dialog where the mouse is will the right side of the dialog
							// extend past the right side of the canvas?
							overlap = dlgRightEdge - canvasRightEdge;
							if ( overlap > 0 )
							{
								// Adjust the x position so the right edge of the dialog does not extend past the right edge of the canvas.
								m_dlgX -= overlap;
								if ( m_dlgX < 0 )
									m_dlgX = m_lpe.getCanvasLeft();
							}
							
							// For some unknown reason the tiny mce dialog does not position correctly.  Alway
							// place its left edge on the left edge of the canvas.
							if ( m_dlgBox instanceof TinyMCEDlg )
							{
								m_dlgX = m_lpe.getCanvasLeft();
							}
							
							canvasBottomEdge = m_lpe.getCanvasTop() + m_lpe.getCanvasHeight();
							dlgBottomEdge = m_dlgY + offsetHeight;
							
							// If we position the dialog where the mouse is will the bottom of the dialog
							// extend past the bottom of the canvas?
							overlap = dlgBottomEdge - canvasBottomEdge;
							if ( overlap > 0 )
							{
								// Adjust the y position so the bottom of the dialog does not extend pas the bottom of the canvas.
								m_dlgY -= overlap;
								if ( m_dlgY < 0 )
									m_dlgY = 50;
							}
							
							m_dlgBox.setPopupPosition( m_dlgX, m_dlgY );
						}// end setPosition()
					};
				}

				// We call setPopupPositionAndShow() instead of show() so we can position the
				// dialog based on the dialog width which is not available until the popup is visible.
				m_dlgBox.setPopupPositionAndShow( m_popupCallback );
			}
		});		
	}// end editProperties()
	
	
	/**
	 * This method is called when the user presses the ok button in the properties dialog box.
	 */
	@Override
	public boolean editSuccessful( Object propertiesObj )
	{
		boolean retVal = true;
		
		// Layout the controls on this widget according to the values found in the properties object.
		updateWidget( propertiesObj );

		// Do we have a handler we are supposed to call?
		if ( m_editSuccessfulHandler != null )
		{
			// Yes, call the handler.
			retVal = m_editSuccessfulHandler.editSuccessful( (DropWidget)this );
		}
		
		// If the handler returned false, don't close the dialog.
		if ( retVal )
			m_dlgBox.hide();
		
		// Notify the landing page editor that this widget has been updated.
		m_lpe.notifyWidgetUpdated( this );
		
		return retVal;
	}// end editSuccessful()

	
	/**
	 * Return the drag proxy object that should be displayed when the user drags this item.
	 */
	public abstract DragProxy getDragProxy();
	
	
	/**
	 * Return the DropZone this widget lives in.
	 */
	public DropZone getParentDropZone()
	{
		return m_parentDropZone;
	}
	

	/**
	 * Return the dialog box used to edit the properties of this widget.
	 */
	public abstract void getPropertiesDlgBox( int xPos, int yPos, DlgBoxClient dlgClient );
	
	
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
	@Override
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
	@Override
	public void onEdit( int x, int y )
	{
		editProperties( null, null, x, y );
	}// end onEdit()
	
	
	/**
	 * Handles the MouseDownEvent.  This will initiate the dragging of this item.
	 */
	@Override
	public void onMouseDown( MouseDownEvent event )
	{
		Object	eventSender;
		
		// Is the object that sent this event a PaletteItem object?
		eventSender = event.getSource();
		if ( eventSender instanceof Image )
		{
			final int x;
			final int y;
			final DropWidget dropWidget;
			Scheduler.ScheduledCommand cmd;
			
			dropWidget = this;
			x = event.getClientX();
			y = event.getClientY();
			
			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute()
				{
					// Yes
					m_lpe.startDragExistingItem( dropWidget, x, y );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );

			// Kill this mouse-down event so text on the page does not get highlighted when the user moves the mouse.
			event.getNativeEvent().preventDefault();
		}
	}// end onMouseDown()
	
	
	/**
	 * Set the DropZone this widget lives in.
	 */
	public void setParentDropZone( DropZone dropZone )
	{
		m_parentDropZone = dropZone;
	}
	
	
	/**
	 * Layout the widget according to the values found in the properties object.
	 */
	public abstract void updateWidget( Object props );
	
}// end DropWidget

/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.EditHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * 
 * @author jwootton
 *
 */
public class ActionsControl extends Composite
	implements ClickHandler
{
	private EditHandler		m_editHandler;	// Handler to call when the user presses the "edit" link.
	private DeleteHandler	m_deleteHandler;// Handler to call when the user presses the "delete" link.
	private Image			m_editImg;
	private Image			m_deleteImg;
	
	/**
	 * This control has 2 images, an "edit" image and a "delete" image.  When the user clicks
	 * on the "edit" image, the editHandler will be called.  When the user clicks on the "delete"
	 * image, the deleteHandler will be called.
	 */
	public ActionsControl(
		MouseDownHandler mouseDownHandler,
		EditHandler editHandler,	// Gets called when the user clicks on the "edit" link.
		DeleteHandler deleteHandler )// Gets called when the user clicks on the "delete" link.
	{
		FlowPanel mainPanel;
		ImageResource imageResource;
		
		m_editHandler = editHandler;
		m_deleteHandler = deleteHandler;

		mainPanel = new FlowPanel();
		mainPanel.addStyleName( "lpeActionsControl" );
		
		// Create a "move" image
		{
			Image img;
			
			imageResource = GwtTeaming.getImageBundle().move10();
			img = new Image(imageResource);
			img.addMouseDownHandler( mouseDownHandler );
			img.addStyleName( "margin-right-5" );
			img.addStyleName( "cursorMove" );
			img.setTitle( GwtTeaming.getMessages().lpeAltMoveElement() );
			
			mainPanel.add( img );
		}
		
		// Create an "edit" image
		{
			imageResource = GwtTeaming.getImageBundle().edit10();
			m_editImg = new Image(imageResource);
			m_editImg.addClickHandler( this );
			m_editImg.addStyleName( "lpeEditImg" );
			m_editImg.setTitle( GwtTeaming.getMessages().lpeAltEditElementProperties() );
			
			mainPanel.add( m_editImg );
		}
		
		// Create a "delete" image.
		{
			imageResource = GwtTeaming.getImageBundle().delete10();
			m_deleteImg = new Image(imageResource);
			m_deleteImg.addStyleName( "lpeDeleteImg" );
			m_deleteImg.addClickHandler( this );
			m_deleteImg.setTitle( GwtTeaming.getMessages().lpeAltDeleteElement() );
			
			mainPanel.add( m_deleteImg );
		}
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}// end ActionsControl()
	
	
	/*
	 * This method gets called when the user clicks on the ok or cancel button.
	 */
	public void onClick( ClickEvent event )
	{
		Object	source;

		// Get the object that was clicked on.
		source = event.getSource();
		
		// Did the user click on the "edit" image?
		if ( source == m_editImg )
		{
			// Yes
			// Do we have a handler we need to call?
			if ( m_editHandler != null )
			{
				int x;
				int y;
				
				// Yes
				x = event.getNativeEvent().getClientX();
				y = event.getNativeEvent().getClientY();
				m_editHandler.onEdit( x, y );
			}
			
			return;
		}
		
		// Did the user click on the "delete" image?
		if ( source == m_deleteImg )
		{
			// Yes
			// Do we have a handler we need to call?
			if ( m_deleteHandler != null )
			{
				// Yes
				m_deleteHandler.onDelete();
			}
		}
	}// end onClick()
}// end ActionsControl

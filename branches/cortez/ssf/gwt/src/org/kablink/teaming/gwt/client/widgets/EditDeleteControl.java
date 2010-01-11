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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * 
 * @author jwootton
 *
 */
public class EditDeleteControl extends Composite
	implements ClickHandler
{
	private EditHandler	m_editHandler;	// Handler to call when the user presses the "edit" link.
	private DeleteHandler	m_deleteHandler;// Handler to call when the user presses the "delete" link.
	private Anchor			m_editAnchor;
	private Anchor			m_deleteAnchor;
	
	/**
	 * This control has 2 images, an "edit" image and a "delete" image.  When the user clicks
	 * on the "edit" image, the editHandler will be called.  When the user clicks on the "delete"
	 * image, the deleteHandler will be called.
	 */
	public EditDeleteControl(
		EditHandler editHandler,	// Gets called when the user clicks on the "edit" link.
		DeleteHandler deleteHandler )// Gets called when the user clicks on the "delete" link.
	{
		FlowPanel mainPanel;
		AbstractImagePrototype abstractImg;
		Image img;
		
		m_editHandler = editHandler;
		m_deleteHandler = deleteHandler;

		mainPanel = new FlowPanel();
		mainPanel.addStyleName( "editDeleteControl" );
		
		// Create an "edit" anchor.
		{
			m_editAnchor = new Anchor();
			m_editAnchor.addClickHandler( this );
			m_editAnchor.addStyleName( "editDeleteControlAnchor" );
			
			abstractImg = GwtTeaming.getImageBundle().edit10();
			img = abstractImg.createImage();
			img.addStyleName( "margin-right-5" );
			
			// Add the edit image to the anchor.
			m_editAnchor.getElement().appendChild( img.getElement() );
			
			mainPanel.add( m_editAnchor );
		}
		
		// Create a "delete" anchor.
		{
			m_deleteAnchor = new Anchor();
			m_deleteAnchor.addClickHandler( this );
			m_deleteAnchor.addStyleName( "editDeleteControlAnchor" );
			
			abstractImg = GwtTeaming.getImageBundle().delete10();
			img = abstractImg.createImage();

			// Add the delete image to the anchor.
			m_deleteAnchor.getElement().appendChild( img.getElement() );
			
			mainPanel.add( m_deleteAnchor );
		}
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}// end EditDeleteControl()
	
	
	/*
	 * This method gets called when the user clicks on the ok or cancel button.
	 */
	public void onClick( ClickEvent event )
	{
		Object	source;

		// Get the object that was clicked on.
		source = event.getSource();
		
		// Did the user click on the "edit" link?
		if ( source == m_editAnchor )
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
		
		// Did the user click on the "delete" link?
		if ( source == m_deleteAnchor )
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
}// end EditDeleteControl

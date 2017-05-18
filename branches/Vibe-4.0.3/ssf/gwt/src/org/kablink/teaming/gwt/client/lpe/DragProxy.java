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


import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PopupPanel;


/**
 * The DragProxy class is used to create the object that will be dragged from the palette.
 */
public class DragProxy extends PopupPanel
{
	/**
	 * 
	 */
	public DragProxy( 	ImageResource imageResource, String text )
	{
		// Turn off the 'auto-hide' behavior.
		super( false );
		
		FlowPanel	panel;
		InlineLabel	label;
		Image		img;
		
		// Associate this popup with its stylesheet.
		addStyleName( "lpeDragProxyPopup" );
		
		// Create a FlowPanel for the image and text to live in.
		panel = new FlowPanel();

		// Associate the panel with its stylesheet.
		panel.addStyleName( "lpeDragProxy" );

		// Add the image to this widget
		img = new Image(imageResource);
		img.addStyleName( "lpeDragProxyImg" );
		panel.add( img );
		
		// Add the text to this widget.
		label = new InlineLabel( text );
		panel.add( label );

		setWidget( panel );
	}
}


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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This widget is the Landing Page Editor.  As its name implies, it is used to edit a 
 * landing page configuration.
 * @author jwootton
 *
 */
public class LandingPageEditor extends Composite
{
	private Palette	m_palette;
	private Canvas		m_canvas;
	
	/**
	 * 
	 */
	public LandingPageEditor()
	{
		HorizontalPanel	hPanel;
		VerticalPanel	vPanel;
		Label			hintLabel;
		
		// Create a vertical panel for the hint and the horizontal panel to live in.
		vPanel = new VerticalPanel();
		
		// Create a hint
		hintLabel = new Label( GwtTeaming.getMessages().lpeHint() );
		hintLabel.addStyleName( "lpeHint" );
		vPanel.add( hintLabel );
		
		// Create a panel for the palette and canvas to live in.
		hPanel = new HorizontalPanel();
		
		// Create some space between the palette and the canvas.
		hPanel.setSpacing( 5 );
		
		// Create a palette and a canvas.
		m_palette = new Palette();
		m_canvas = new Canvas();
		
		// Add the palette and canvas to the panel.
		hPanel.add( m_palette );
		hPanel.add( m_canvas );
		
		vPanel.add( hPanel );
		
		// All composites must call initWidget() in their constructors.
		initWidget( vPanel );
	}// end LandingPageEditor()
}// end LandingPageEditor

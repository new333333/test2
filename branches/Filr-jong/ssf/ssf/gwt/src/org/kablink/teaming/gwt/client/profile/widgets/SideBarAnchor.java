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

package org.kablink.teaming.gwt.client.profile.widgets;

import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * Class used for a Profile Side bar Anchor item.  
 * 
 * @author nbjensen@novell.com
 *
 */
public class SideBarAnchor extends FlowPanel 
{

	private Anchor mpA;
	private Image image;

	/**
	 * Class constructor.
	 * 
	 * @param id
	 * @param displayText
	 * @param altText
	 * @param ch
	 */
	public SideBarAnchor(String id, String displayText, String altText, ClickHandler ch) {
		// Initialize the super class...
		super();
		addStyleName("profileSideBar_ItemPanel");
	
		// ...create a FlowPanel to hold the Label...
		FlowPanel mpaLabelPanel = new FlowPanel();
		mpaLabelPanel.getElement().setId(id);
		mpaLabelPanel.addStyleName("profileSideBar_Item");

		// ...create the Anchor...
		mpA = new Anchor();
		//mpA.setWidth("100%");
		mpA.addStyleName("profileSideBar_ItemA");
		if (GwtClientHelper.hasString(altText)) {
			mpA.setTitle(altText);
		}

		if(displayText != null){
			Label mpaLabel = new Label(displayText);
			mpaLabel.addStyleName("profileSideBar_ItemText");
			// ...and connect everything together.
			mpA.getElement().appendChild(mpaLabel.getElement());
		}
		
		mpA.addClickHandler(ch);
		HoverByID mpaHover = new HoverByID(id, "workspaceTreeControlRowHover");
		mpA.addMouseOverHandler(mpaHover);
		mpA.addMouseOutHandler( mpaHover);
	
		mpaLabelPanel.add(mpA);
		add(mpaLabelPanel);
	}
	
	private class HoverByID implements MouseOverHandler, MouseOutHandler {
		private String m_hoverStyle;	// The style to use with the hover.
		private String m_hoverId;		// The ID of the widget to apply the hover style to.
		
		/**
		 * Class constructor.
		 * 
		 * @param hoverId
		 * @param hoverString;
		 */
		HoverByID(String hoverId, String hoverStyle) {
			// Simply store the parameters.
			m_hoverId = hoverId;
			m_hoverStyle = hoverStyle;
		}
		
		/**
		 * Called when the mouse leaves a menu item.
		 * 
		 * @param me
		 */
		public void onMouseOut(MouseOutEvent me) {
			// Simply remove the hover style.
			Element selectorPanel_New = Document.get().getElementById(m_hoverId);
			selectorPanel_New.removeClassName(m_hoverStyle);
		}
		
		/**
		 * Called when the mouse enters a menu item.
		 * 
		 * @param me
		 */
		public void onMouseOver(MouseOverEvent me) {
			// Simply add the hover style.
			Element selectorPanel_New = Document.get().getElementById(m_hoverId);
			selectorPanel_New.addClassName(m_hoverStyle);
		}
	}

	public void setImage(Image img) {
		
		if(image != null){
			mpA.getElement().removeChild(image.getElement());
		}
		
		if(img != null)
		{
			mpA.getElement().appendChild(image.getElement());
			image = img;
		}
	}
}

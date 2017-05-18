/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.mainmenu;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * Class that wraps the GWT MenuItem implementation for use within Vibe.
 * 
 * @author drfoster@novell.com
 */
public class VibeMenuItem extends MenuItem {
	FlowPanel			m_outerPanel;	//
	Image				m_checkMarkImg;	// Image used to put a checkmark next to a menu item.
	Image				m_spacerImg;	// Image used as a spacer if this menu item does not use the checkmark image
	VibeEventBase<?>	m_event;		//
	
	/**
	 * Constructor method.
	 *
	 * @param text
	 * @param asHtml
	 * @param cmd
	 * @param style
	 */
	public VibeMenuItem(String text, boolean asHtml, Command cmd, String style) {
		// Initialize the super class...
		super(text, asHtml, cmd);
		
		// ...and if we were given a style for the MenuItem...
		if (GwtClientHelper.hasString(style)) {
			// ...add it to it.
			addStyleName(style);
		}
	}
	
	/**
	 * Constructor method.
	 *
	 * @param text
	 * @param asHtml
	 * @param subMenu
	 * @param style
	 */
	public VibeMenuItem(String text, boolean asHtml, MenuBar subMenu, String style) {
		// Initialize the super class...
		super(text, asHtml, subMenu);
		
		// ...and if we were given a style for the MenuItem...
		if (GwtClientHelper.hasString(style)) {
			// ...add it to it.
			addStyleName(style);
		}
	}
	
	/**
	 * Constructor method.
	 *
	 * @param text
	 * @param cmd
	 * @param style
	 */
	public VibeMenuItem(String text, Command cmd, String style) {
		// Always use one of the initial forms of the constructor.
		this(text, false, cmd, style);
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param text
	 * @param asHtml
	 * @param cmd
	 */
	public VibeMenuItem(String text, boolean asHtml, Command cmd) {
		// Always use one of the initial forms of the constructor.
		this(text, asHtml, cmd, null);
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param safeHtml
	 */
	public VibeMenuItem(SafeHtml safeHtml) {
		// Initialize the super class.
		super(safeHtml);
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param text
	 * @param cmd
	 */
	public VibeMenuItem(String text, Command cmd) {
		// Always use one of the initial forms of the constructor.
		this(text, false, cmd, null);
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param text
	 * @param subMenu
	 */
	public VibeMenuItem(String text, MenuBar subMenu) {
		// Always use one of the initial forms of the constructor.
		this(text, false, subMenu, null);
	}
	
	/**
	 * Constructor method.
	 * 
	 * This is used instead of using PopupMenuItem.
	 * 
	 * @param cmd
	 * @param event
	 * @param img
	 * @param text
	 * @param styleName
	 * @param allowForCheckMark
	 */
	public VibeMenuItem(Command cmd, VibeEventBase<?> event, Image img, String text, String styleName, boolean allowForCheckMark) {
		super("", cmd);

		m_event      = event;
		m_outerPanel = new FlowPanel();
		
		FlowPanel mainPanel = new FlowPanel();
		if (null != styleName) {
			mainPanel.addStyleName(styleName);
		}
		m_outerPanel.add(mainPanel);
		
		// Do we need to allow space for a check image?
		if (allowForCheckMark) {
			
			// Yes!  Create a checkbox image in case we need it.
			ImageResource imageResource = GwtTeaming.getImageBundle().check12();
			m_checkMarkImg = new Image(imageResource);
			m_checkMarkImg.setVisible(false);
			m_checkMarkImg.getElement().setAttribute("align", "absmiddle");
			mainPanel.add(m_checkMarkImg);

			// Create a spacer image that will be used instead of a
			// checkmark image.
			imageResource = GwtTeaming.getImageBundle().spacer1px();
			m_spacerImg = new Image(imageResource);
			m_spacerImg.setWidth("12px");
			m_spacerImg.setVisible(false);
			mainPanel.add(m_spacerImg);
		}

		// Do we have an image?
		if (null != img) {
			img.getElement().setAttribute("align", "absmiddle");
			img.addStyleName("vibe-popupMenuItemImg");
			mainPanel.add(img);
		}
		
		InlineLabel label = new InlineLabel(text);
		mainPanel.add(label);
		
		setMenuItemHTML();
	}
	
	/**
	 * Get'er method.
	 * 
	 * @return
	 */
	public VibeEventBase<?> getEvent() {
		return m_event;
	}
	
	/**
	 * Is this menu item checked.
	 * 
	 * @return
	 */
	public boolean isChecked() {
		if (null != m_checkMarkImg) {
			return m_checkMarkImg.isVisible();
		}
		
		return false;
	}

	/**
	 * Set'er method.
	 * 
	 * Set the checked state of this menu item.
	 * 
	 * @param checked
	 */
	public void setCheckedState(boolean checked) {
		if (null != m_checkMarkImg) {
			m_checkMarkImg.setVisible(checked);
			m_spacerImg.setVisible(  !checked);
			
			// Refresh the HTML used by this menu item.
			setMenuItemHTML();
		}
	}
	
	/**
	 * Set the HTML for this menu item.
	 */
	private void setMenuItemHTML() {
		if (null != m_outerPanel) {
			setHTML(SafeHtmlUtils.fromTrustedString(m_outerPanel.getElement().getInnerHTML()));
		}
	}
}

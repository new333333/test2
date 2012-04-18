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
package org.kablink.teaming.gwt.client.tasklisting;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.EventWrapper;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * Class used to implement a task tool button.  
 * 
 * @author drfoster@novell.com
 */
public class TaskButton extends Anchor {
	private boolean				m_enabled;			// true -> The button is enabled.  false -> The button is disabled.
	private Image				m_buttonImage;		// For image buttons, the Image widget that displays the button.
	private ImageResource		m_baseImgRes;		// For image buttons, the base       image resource. 
	private ImageResource		m_disabledImgRes;	// For image buttons, the disabled   image resource.
	private ImageResource		m_overImgRes;		// For image buttons, the mouse over image resource.
	private InlineLabel			m_buttonLabel;		// For text  buttons, the InlineLabel widget that displays the text.
	private String				m_imgTitle;			// The default title text for the button.
	private TeamingEvents		m_eventEnum;		// The enumeration of the event to fire when the button is clicked.
	private VibeEventBase<?>	m_event;			// The                    event to fire when the button is clicked.
	
	/*
	 * Inner class that handle mousing over the button.
	 */
	private class ButtonHover implements MouseOverHandler, MouseOutHandler {
		private Image		m_hoverImage;	// For image buttons, the Widget the ButtonHover is for.
		private InlineLabel	m_hoverLabel;	// FOr text  buttons, the Widget the ButtonHover is for.
		
		/**
		 * Class constructors.
		 * 
		 * @param hoverWidget
		 */
		public ButtonHover(Image hoverWidget) {
			// Simply store the parameter.
			m_hoverImage = hoverWidget;
		}
		
		public ButtonHover(InlineLabel hoverWidget) {
			// Simply store the parameter.
			m_hoverLabel = hoverWidget;
		}
		
		/**
		 * Called when the mouse leaves the button.
		 * 
		 * @param me
		 */
		@Override
		public void onMouseOut(MouseOutEvent me) {
			if (m_enabled) {
				if      (null != m_hoverLabel) m_hoverLabel.removeStyleName("gwtTaskToolsButton_WidgetTextHover");
				else if (null != m_hoverImage) m_hoverImage.setResource(m_baseImgRes);
			}
		}
		
		/**
		 * Called when the mouse enters the button.
		 * 
		 * @param me
		 */
		@Override
		public void onMouseOver(MouseOverEvent me) {
			if (m_enabled) {
				if      (null != m_hoverLabel) m_hoverLabel.addStyleName("gwtTaskToolsButton_WidgetTextHover");
				else if (null != m_hoverImage) m_hoverImage.setResource(m_overImgRes);
			}
		}
	}
	
	/*
	 * Inner class that handles clicking the button.
	 */
	private class ButtonSelector implements ClickHandler {
		/**
		 * Called when the button is clicked.
		 * 
		 * @param event
		 */
		@Override
		public void onClick(ClickEvent event) {
			// If the button is enabled...
			if (m_enabled) {
				// ...fire the event.
				GwtTeaming.fireEvent(m_event);
			}
		}
	}

	/**
	 * Class constructor for image buttons.
	 * 
	 * @param baseImgRes
	 * @param disabledImgRes
	 * @param overImgRes
	 * @param enabled
	 * @param imgTitle
	 * @param eventEnum
	 * @param event
	 */
	private TaskButton(ImageResource baseImgRes, ImageResource disabledImgRes, ImageResource overImgRes, boolean enabled, String imgTitle, TeamingEvents eventEnum, VibeEventBase<?> event) {
		// Initialize the super class...
		super();
		
		// ...store the parameters...
		if (null == event) {
			m_eventEnum = eventEnum;
			m_event     = EventHelper.createSimpleEvent(m_eventEnum);
		}
		else {
			m_eventEnum = event.getEventEnum();
			m_event     = event;
		}
		m_enabled        = enabled;
		m_baseImgRes     = baseImgRes;
		m_disabledImgRes = disabledImgRes;
		m_overImgRes     = overImgRes;
		m_imgTitle       = imgTitle;
		
		// ...initialize the Anchor...
		addStyleName("gwtTaskToolsButton_WidgetAnchor");
		m_enabled = (!enabled);
		setEnabled(enabled);
		
		// ...create the Image...
		m_buttonImage = new Image(enabled ? baseImgRes : disabledImgRes);
		if (GwtClientHelper.hasString(imgTitle)) {
			m_buttonImage.setTitle(imgTitle);
		}
		m_buttonImage.addStyleName("gwtTaskToolsButton_WidgetImage");
		m_buttonImage.getElement().setAttribute("align", "absmiddle");
		
		// ...tie things together, including the various event
		// ...handlers.
		getElement().appendChild(m_buttonImage.getElement());		
		List<EventHandler> ehs = new ArrayList<EventHandler>();
		ehs.add(new ButtonSelector());
		ehs.add(new ButtonHover(m_buttonImage));
		EventWrapper.addHandlers(this, ehs);
	}
	
	public TaskButton(ImageResource baseImgRes, ImageResource disabledImgRes, ImageResource overImgRes, boolean enabled, String imgTitle, TeamingEvents eventEnum) {
		this(baseImgRes, disabledImgRes, overImgRes, enabled, imgTitle, eventEnum, null);
	}
	
	public TaskButton(ImageResource baseImgRes, ImageResource disabledImgRes, ImageResource overImgRes, boolean enabled, String imgTitle, VibeEventBase<?> event) {
		this(baseImgRes, disabledImgRes, overImgRes, enabled, imgTitle, null, event);
	}
	
	/**
	 * Class constructor for text buttons.
	 * 
	 * @param buttonText
	 * @param buttonTitle
	 * @param enabled
	 * @param eventEnum
	 * @param event
	 */
	private TaskButton(String buttonText, String buttonTitle, boolean enabled, TeamingEvents eventEnum, VibeEventBase<?> event) {
		// Initialize the super class...
		super();
		
		// ...store the parameters...
		if (null == event) {
			m_eventEnum = eventEnum;
			m_event     = EventHelper.createSimpleEvent(m_eventEnum);
		}
		else {
			m_eventEnum = event.getEventEnum();
			m_event     = event;
		}
		m_enabled = enabled;
		
		// ..initialize the Anchor...
		addStyleName("gwtTaskToolsButton_WidgetAnchor");
		m_enabled = (!enabled);
		setEnabled(enabled);
		
		// ...create the button...
		m_buttonLabel = new InlineLabel(buttonText);
		m_buttonLabel.setTitle(buttonTitle);
		m_buttonLabel.addStyleName("gwtTaskToolsButton_WidgetText");
		if (!enabled) {
			m_buttonLabel.addStyleName("gwtTaskToolsButton_WidgetTextDisabled");
		}
		
		// ...tie things together, including the various event
		// ...handlers.
		getElement().appendChild(m_buttonLabel.getElement());
		List<EventHandler> ehs = new ArrayList<EventHandler>();
		ehs.add(new ButtonSelector());
		ehs.add(new ButtonHover(m_buttonLabel));
		EventWrapper.addHandlers(this, ehs);
	}
	
	public TaskButton(String buttonText, String buttonTitle, boolean enabled, TeamingEvents eventEnum) {
		this(buttonText, buttonTitle, enabled, eventEnum, null);
	}
	
	public TaskButton(String buttonText, String buttonTitle, boolean enabled, VibeEventBase<?> event) {
		this(buttonText, buttonTitle, enabled, null, event);
	}
	
	/**
	 * Enables/disables the button.
	 * 
	 * @param enabled
	 * @param imgTitle
	 */
	public void setEnabled(boolean enabled, String imgTitle) {
		// Make sure an image button has the correct title text on it.
		if (null != m_buttonImage) {
			if (!(GwtClientHelper.hasString(imgTitle))) {
				imgTitle = m_imgTitle;
			}
			m_buttonImage.setTitle(imgTitle);
		}
		
		// If the existing state is what's being requested...
		if (enabled == m_enabled) {
			// ...there's nothing to do.
			return;
		}

		// Enable/disable the button.
		m_enabled = enabled;
		super.setEnabled(enabled);
		if (null != m_buttonLabel) {
			m_buttonLabel.removeStyleName("gwtTaskToolsButton_WidgetTextHover");
			if (enabled) m_buttonLabel.removeStyleName("gwtTaskToolsButton_WidgetTextDisabled");
			else         m_buttonLabel.addStyleName(   "gwtTaskToolsButton_WidgetTextDisabled");
		}
		else if (null != m_buttonImage) {
			m_buttonImage.setResource(enabled ? m_baseImgRes : m_disabledImgRes);
		}
		
		String addCursorStyle;
		String removeCursorStyle;
		if (m_enabled) {addCursorStyle = "cursorPointer"; removeCursorStyle = "cursorDefault";}
		else           {addCursorStyle = "cursorDefault"; removeCursorStyle = "cursorPointer";}
		removeStyleName(removeCursorStyle);
		addStyleName(addCursorStyle);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		setEnabled(enabled, null);
	}
}

/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.binderviews;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.MenuIds;
import org.kablink.teaming.gwt.client.event.QuickFilterEvent;
import org.kablink.teaming.gwt.client.util.EventWrapper;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Class used to encapsulate the quick filter widgets.
 * 
 * @author drfoster@novell.com
 */
public class QuickFilter extends ResizeComposite {
	private boolean			m_quickFilterEmpty = true;	//
	private boolean			m_quickFilterOff   = true;	//
	private Image			m_quickFilterImage;			// <IMG> for the filter.  Changes based on whether a filter is active or not.
	private Long			m_folderId;					//
	private TextBox			m_quickFilterInput;			// The <INPUT> for entering a filter.
	private VibeFlowPanel	m_quickFilterDIV;			// <DIV> containing the filter widgets.
	
	private final GwtTeamingMessages				m_messages   = GwtTeaming.getMessages();				//
	private final GwtTeamingDataTableImageBundle	m_images     = GwtTeaming.getDataTableImageBundle();	//
	
	/**
	 * Class constructor.
	 */
	public QuickFilter(Long folderId) {
		// Initialize the super class...
		super();

		// ...store the parameters...
		m_folderId = folderId;
		
		// ...create the filter <IMG>...
		m_quickFilterImage = new Image(m_images.filterOff());
		m_quickFilterImage.getElement().setId(MenuIds.ENTRY_FILTER_IMAGE);
		m_quickFilterImage.setTitle(m_messages.quickFilterAltOff());
		m_quickFilterImage.setStyleName("vibe-quickFilter_image");
		m_quickFilterImage.getElement().setAttribute("align", "absmiddle");
		EventWrapper.addHandler(m_quickFilterImage, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Simply kill any filter information.
				killFilter();
			}				
		});
		
		// ...create the filter <INPUT>...
		m_quickFilterInput = new TextBox();
		m_quickFilterInput.getElement().setId(MenuIds.ENTRY_FILTER_INPUT);
		m_quickFilterInput.setValue(m_messages.quickFilter_empty());
		m_quickFilterInput.addStyleName("vibe-quickFilter_input");
		setBlurStyles();
		List<EventHandler> inputHandlers = new ArrayList<EventHandler>();
		inputHandlers.add(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				// Is this the enter key being pressed?
				int key = event.getNativeEvent().getKeyCode();
				if (KeyCodes.KEY_ENTER == key) {
					// Yes!  Is there anything in the filter?
					String filter = getFilterValue();
					if (0 < filter.length()) {
						// Yes!  Put the filter into effect.
						m_quickFilterImage.setResource(m_images.filterOn());
						m_quickFilterImage.setTitle(m_messages.quickFilterAltOn());
						m_quickFilterOff   =
						m_quickFilterEmpty = false;
					}						
					else {
						// No, there's nothing in the filter!  Turn
						// off any filter that's in effect.
						m_quickFilterImage.setResource(m_images.filterOff());
						m_quickFilterImage.setTitle(m_messages.quickFilterAltOff());
						m_quickFilterOff   =
						m_quickFilterEmpty = true;
					}
					filterListAsync(filter);
				}
			}
		});
		inputHandlers.add(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				// Set the appropriate styles on the input...
				setBlurStyles();

				// ...and if the filter input is empty...
				String filter      = getFilterValue();
				m_quickFilterEmpty = (0 == filter.length());
				if (m_quickFilterEmpty) {
					// ...display an empty message in it.
					m_quickFilterInput.setValue(m_messages.quickFilter_empty());
				}
			}
		});
		inputHandlers.add(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				// Set the appropriate styles on the input...
				setFocusStyles();
				
				// ...and if the filter input is empty...
				if (m_quickFilterEmpty) {
					// ...remove any empty message from it.
					m_quickFilterInput.setValue("");
				}
			}
		});
		EventWrapper.addHandlers(m_quickFilterInput, inputHandlers);

		// ...tie it all together...
		m_quickFilterDIV = new VibeFlowPanel();
		m_quickFilterDIV.getElement().setId(MenuIds.ENTRY_FILTER_DIV);
		m_quickFilterDIV.add(m_quickFilterImage);
		m_quickFilterDIV.add(m_quickFilterInput);
		
		// ...and tell the Composite that we're good to go.
		initWidget(m_quickFilterDIV);
	}

	/*
	 * Asynchronously sets/clears a filter.
	 */
	private void filterListAsync(final String filter) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				filterListNow(filter);
			}
		});
	}
	
	/*
	 * Synchronously sets/clears a filter.
	 */
	private void filterListNow(String filter) {
		GwtTeaming.fireEvent(new QuickFilterEvent(m_folderId, filter));
	}

	/*
	 * Returns a non-null, non-space padded filter value from the
	 * input widget.
	 */
	private String getFilterValue() {
		String reply = m_quickFilterInput.getValue();
		if (null == reply)           reply = "";
		else if (0 < reply.length()) reply = reply.trim();
		return reply;
	}

	/*
	 * Sets the appropriate styles on the input widget for when
	 * it loses focus.
	 */
	private void setBlurStyles() {
		if (m_quickFilterOff) {
			m_quickFilterInput.removeStyleName("vibe-quickFilter_inputFocus");
			m_quickFilterInput.addStyleName(   "vibe-quickFilter_inputBlur");
		}
		
		else {
			setFocusStyles();
		}
	}
	
	/*
	 * Sets the appropriate styles on the input widget for when
	 * it gets focus.
	 */
	private void setFocusStyles() {
		m_quickFilterInput.removeStyleName("vibe-quickFilter_inputBlur");
		m_quickFilterInput.addStyleName(   "vibe-quickFilter_inputFocus");
	}		
	
	/*
	 * Does what's necessary to turn off a filter.
	 */
	private void killFilter() {
		boolean filterWasOn = (!m_quickFilterOff);

		m_quickFilterInput.setValue(m_messages.quickFilter_empty());
		m_quickFilterImage.setResource(m_images.filterOff());
		m_quickFilterImage.setTitle(m_messages.quickFilterAltOff());
		m_quickFilterOff   =
		m_quickFilterEmpty = true;
		setBlurStyles();
		
		if (filterWasOn) {
			filterListAsync(null);
		}
	}
}

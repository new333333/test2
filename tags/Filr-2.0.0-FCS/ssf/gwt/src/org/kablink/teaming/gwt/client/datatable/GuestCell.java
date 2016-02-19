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
package org.kablink.teaming.gwt.client.datatable;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.binderviews.folderdata.GuestInfo;
import org.kablink.teaming.gwt.client.event.GotoContentUrlEvent;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * Data table cell that represents a signer of a guest book.
 * 
 * @author drfoster@novell.com
 */
public class GuestCell extends AbstractCell<GuestInfo> {
	/*
	 * Inner class used to encapsulate information about an event
	 * received by an GuestCell.
	 */
	private static class EventInfo {
		private boolean	m_isGuestAvatar;		//
		private int		m_stepsToTD;			//
		private String	m_eventTag        = "";	//
		private String	m_widgetAttribute = "";	//

		/**
		 * Constructor method.
		 * 
		 * @param parent
		 * @param event
		 */
		public EventInfo(Element parent, NativeEvent event) {
			// Scan up the event element's parentage until we find
			// a <TD>.
			Element eventTarget = Element.as(event.getEventTarget());
			m_eventTag          = eventTarget.getTagName();
			while (!(m_eventTag.equalsIgnoreCase("td"))) {
				// Is this one of our GuestCell widgets?
				m_widgetAttribute = eventTarget.getAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE);
				if ((null != m_widgetAttribute) && (0 < m_widgetAttribute.length())) {
					// Yes!  Is it the guest avatar?
					m_isGuestAvatar = m_widgetAttribute.equals(VibeDataTableConstants.CELL_WIDGET_GUEST_AVATAR);
					break;
				}

				// We haven't found a GuestCell widget yet.  Move up
				// a level.
				m_stepsToTD += 1;
				eventTarget  = eventTarget.getParentElement();
				m_eventTag   = eventTarget.getTagName();
			}
			
			// debugDump();
		}
		
		/*
		 * Displays the contents of the EventInfo in an alert().
		 */
		@SuppressWarnings("unused")
		private void debugDump() {
			Window.alert(
				  "T:" + m_eventTag        +
				", A:" + m_isGuestAvatar   +
				", W:" + m_widgetAttribute +
				", S:" + m_stepsToTD);
		}

		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean isGuestAvatar() {return m_isGuestAvatar;}
	}
	
	/**
	 * Constructor method.
	 */
	public GuestCell() {
		/*
		 * Sink the events we need to process a guest book signer.
	     */
		super(
			VibeDataTableConstants.CELL_EVENT_CLICK,
			VibeDataTableConstants.CELL_EVENT_KEYDOWN,
			VibeDataTableConstants.CELL_EVENT_MOUSEOVER,
			VibeDataTableConstants.CELL_EVENT_MOUSEOUT);
	}

	/*
	 * Adds an information Label widget to an information panel.
	 */
	private void addInfoLabel(VibeFlowPanel infoPanel, String s, String style) {
		if (GwtClientHelper.hasString(s)) {
			Label l = new Label(s);
			l.addStyleName(style);
			infoPanel.add(l);
		}
	}
	
	/**
     * Called when an event occurs in a rendered instance of this
     * cell.  The parent element refers to the element that contains
     * the rendered cell, NOT to the outermost element that the cell
     * rendered.
     * 
     * @param context
     * @param parent
     * @param gi
     * @param event
     * @param valueUpdater
     * 
     * Overrides AbstractCell.onBrowserEvent()
     */
    @Override
    public void onBrowserEvent(Context context, Element parent, GuestInfo gi, NativeEvent event, ValueUpdater<GuestInfo> valueUpdater) {
		// If we're processing a key down event...
    	String eventType = event.getType();
    	if (VibeDataTableConstants.CELL_EVENT_KEYDOWN.equals(eventType)) {
        	// ...let AbstractCell handle it.  It will convert it to an
    		// ...entry key down, ... as necessary.
        	super.onBrowserEvent(context, parent, gi, event, valueUpdater);
        	return;
    	}
    	
    	// Is this a guest avatar being operated on? 
		Element eventTarget = Element.as(event.getEventTarget());
		EventInfo ei = new EventInfo(parent, event);
		if (ei.isGuestAvatar()) {
			// Yes!  What event is being processed?
	    	if (VibeDataTableConstants.CELL_EVENT_CLICK.equals(eventType)) {
	    		// A click!  Strip off any over style and invoke the
	    		// user profile page on that guest.
    			eventTarget.removeClassName("vibe-dataTableGuest-avatarHover");
    			GwtTeaming.fireEvent(new GotoContentUrlEvent(gi.getProfileUrl()));
	    	}
	    	
	    	else if (VibeDataTableConstants.CELL_EVENT_MOUSEOVER.equals(eventType)) {
	    		// A mouse over!  Add the hover style.
				eventTarget.addClassName("vibe-dataTableGuest-avatarHover");
	    	}
	    	
	    	else if (VibeDataTableConstants.CELL_EVENT_MOUSEOUT.equals(eventType)) {
	    		// A mouse out!  Remove the hover style.
				eventTarget.removeClassName("vibe-dataTableGuest-avatarHover");
	    	}
		}
    }
    
    /**
     * Called when the user presses the ENTER key will the cell is
     * selected.  You are not required to override this method, but
     * it's a common convention that allows your cell to respond to key
     * events.
     *
     * @param context
     * @param parent
     * @param gi
     * @param event
     * @param valueUpdater
     * 
     * Overrides AbstractCell.onEnterKeyDown()
     */
    @Override
    protected void onEnterKeyDown(Context context, Element parent, GuestInfo gi, NativeEvent event, ValueUpdater<GuestInfo> valueUpdater) {
    	// If the key down is targeting a guest avatar...
		if (new EventInfo(parent, event).isGuestAvatar()) {
			// ...invoke the user profile page on that guest.
			GwtTeaming.fireEvent(new GotoContentUrlEvent(gi.getProfileUrl()));
		}
    }
    
	/**
	 * Called to render an instance of this cell.
	 * 
	 * @param context
	 * @param gi
	 * @param sb
	 * 
	 * Overrides AbstractCell.render()
	 */
	@Override
	public void render(Context context, GuestInfo gi, SafeHtmlBuilder sb) {
		// If we done have a guest...
		if (null == gi) {
			// ...bail.  Cell widgets can pass null to cells if the
			// ...underlying data contains a null, or if the data
			// ...arrives out of order.
			GwtClientHelper.renderEmptyHtml(sb);
			return;
		}
		
		// Create a panel to render the guest book signer widgets
		// into...
		VibeFlowPanel htmlPanel = new VibeFlowPanel();

		// ...add the avatar link...
		Image avatarImg = new Image();
		avatarImg.addStyleName("vibe-dataTableGuest-avatar");
		if (GwtClientHelper.hasString(gi.getProfileUrl())) {
			avatarImg.getElement().setAttribute(
				VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE,
				VibeDataTableConstants.CELL_WIDGET_GUEST_AVATAR);
		}
		String avatarUrl = gi.getAvatarUrl();
		if (!(GwtClientHelper.hasString(avatarUrl)))
		     avatarImg.setUrl(GwtTeaming.getDataTableImageBundle().userPhoto().getSafeUri());
		else avatarImg.setUrl(avatarUrl);
		GwtTeamingMessages messages = GwtTeaming.getMessages();
		avatarImg.setTitle(messages.guestBook_GotoProfile());
		htmlPanel.add(avatarImg);

		// ...add the information widgets...
		VibeFlowPanel infoPanel = new VibeFlowPanel();
		infoPanel.addStyleName("vibe-dataTableGuest-info");
		String s = gi.getTitle();
		if (!(GwtClientHelper.hasString(s))) {
			s = messages.noTitle();
		}
		addInfoLabel(infoPanel, s,                          "vibe-dataTableGuest-infoTitle"    );
		addInfoLabel(infoPanel, gi.getPhone(),              "vibe-dataTableGuest-infoPhone"    );
		addInfoLabel(infoPanel, gi.getEmailAddress(),       "vibe-dataTableGuest-infoEMA"      );
		addInfoLabel(infoPanel, gi.getMobileEmailAddress(), "vibe-dataTableGuest-infoMobileEMA");
		addInfoLabel(infoPanel, gi.getTextEmailAddress(),   "vibe-dataTableGuest-infoTextEMA"  );
		htmlPanel.add(infoPanel);
		
		// ...and render that into the cell.
		SafeHtml rendered = SafeHtmlUtils.fromTrustedString(htmlPanel.getElement().getInnerHTML());
		sb.append(rendered);
	}
}

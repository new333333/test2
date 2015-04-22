/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.event.InvokeUserPropertiesDlgEvent;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.presence.PresenceControl;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.PrincipalInfo;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Label;

/**
 * Data table cell that represents presence.
 * 
 * @author drfoster@novell.com
 */
public class PresenceCell extends AbstractCell<PrincipalInfo> {
	private GwtTeamingDataTableImageBundle	m_images;		//
	private PresenceClickAction				m_clickAction;	//

	/**
	 * Enumeration value that defines what happens when a presence
	 * widget is clicked.
	 */
	public enum PresenceClickAction {
		SHOW_SIMPLE_PROFILE,
		SHOW_USER_PROPERTIES,
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param clickAction
	 */
	public PresenceCell(PresenceClickAction clickAction) {
		// Sink the events we need to process presence...
		super(
			VibeDataTableConstants.CELL_EVENT_CLICK,
			VibeDataTableConstants.CELL_EVENT_KEYDOWN,
			VibeDataTableConstants.CELL_EVENT_MOUSEOVER,
			VibeDataTableConstants.CELL_EVENT_MOUSEOUT);
		
		// ...store the parameter...
		m_clickAction = clickAction;
		
		// ...and initialize everything else.
		m_images = GwtTeaming.getDataTableImageBundle();
	}

	/*
	 * Returns the URL to the image to display for presence for the
	 * cell.
	 */
	private String getPresenceImage(PrincipalInfo pi) {
		String reply = pi.getAvatarUrl();
		if (!(GwtClientHelper.hasString(reply))) {
			reply = m_images.userPhoto().getSafeUri().asString();
		}
		return reply;
	}
	
	/*
	 * Called to invoke the simple profile dialog on the principal's
	 * presence.
	 */
	private void invokeSimpleProfileDlg(PrincipalInfo pi, Element pElement) {
		Long wsId = pi.getPresenceUserWSId();
		String wsIdS = ((null == wsId) ? null : String.valueOf(wsId));
		GwtClientHelper.invokeSimpleProfile(pElement, String.valueOf(pi.getId()), wsIdS, pi.getTitle());
	}
	
	/*
	 * Called to invoke the user properties dialog on the principal.
	 */
	private void invokeUserPropertiesDlg(PrincipalInfo pi, Element pElement) {
		// Simply fire the event to invoke the user properties dialog.
		GwtTeaming.fireEventAsync(new InvokeUserPropertiesDlgEvent(pi.getId()));
	}
	
	/**
     * Called when an event occurs in a rendered instance of this
     * cell.  The parent element refers to the element that contains
     * the rendered cell, NOT to the outermost element that the cell
     * rendered.
     * 
     * @param context
     * @param parent
     * @param pi
     * @param event
     * @param valueUpdater
     * 
     * Overrides AbstractCell.onBrowserEvent()
     */
    @Override
    public void onBrowserEvent(Context context, Element parent, PrincipalInfo pi, NativeEvent event, ValueUpdater<PrincipalInfo> valueUpdater) {
    	// Which of our presence widgets is being operated on? 
		Element eventTarget = Element.as(event.getEventTarget());
		boolean isPresence = (parent.getFirstChildElement().isOrHasChild(eventTarget));
		boolean isLabel    = (!isPresence);
		if (isLabel) {
			String wt = eventTarget.getAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE);
			isLabel = ((null != wt) && wt.equals(VibeDataTableConstants.CELL_WIDGET_PRESENCE_LABEL));
		}

		// What type of event are we processing?
    	String eventType = event.getType();
    	if (VibeDataTableConstants.CELL_EVENT_KEYDOWN.equals(eventType)) {
        	// A key down!  Let AbstractCell handle it.  It will
    		// convert it to an entry key down, ... as necessary.
        	super.onBrowserEvent(context, parent, pi, event, valueUpdater);
    	}

    	else if (VibeDataTableConstants.CELL_EVENT_CLICK.equals(eventType)) {
    		// A click!  Is it the label being clicked?
    		if (isLabel) {
    			// Yes!  Strip off any over style...
    			eventTarget.removeClassName("vibe-dataTableLink-hover");
    		}
    		
    		// ...and process the click action.
    		if (isPresence || isLabel) {
    			processClickAction(pi, eventTarget);
    		}
    	}
    	
    	else if (isLabel && VibeDataTableConstants.CELL_EVENT_MOUSEOVER.equals(eventType)) {
    		// A mouse over!  Add the hover style.
			eventTarget.addClassName("vibe-dataTableLink-hover");
    	}
    	
    	else if (isLabel && VibeDataTableConstants.CELL_EVENT_MOUSEOUT.equals(eventType)) {
    		// A mouse out!  Remove the hover style.
			eventTarget.removeClassName("vibe-dataTableLink-hover");
    	}
    }
    
    /**
     * Called when the user presses the ENTER key will the cell is
     * selected.  You are not required to override this method, but
     * it's a common convention that allows your cell to respond to key
     * events.
     * 
     * Overrides AbstractCell.onEnterKeyDown()
     */
    @Override
    protected void onEnterKeyDown(Context context, Element parent, PrincipalInfo pi, NativeEvent event, ValueUpdater<PrincipalInfo> valueUpdater) {
		Element eventTarget = Element.as(event.getEventTarget());
		boolean isPresence = (parent.getFirstChildElement().isOrHasChild(eventTarget));
		boolean isLabel    = (!isPresence);
		if (isLabel) {
			String wt = eventTarget.getAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE);
			isLabel = ((null != wt) && wt.equals(VibeDataTableConstants.CELL_WIDGET_PRESENCE_LABEL));
		}
		if (isPresence || isLabel) {
			processClickAction(pi, eventTarget);
		}
    }

    /*
     * Processes what needs to be done when a item is clicked.
     */
    private void processClickAction(PrincipalInfo pi, Element eventTarget) {
    	// What action should we take when the profile is clicked on?
		switch (m_clickAction) {
		default:
		case SHOW_SIMPLE_PROFILE:
			// Invoke the simple profile dialog.
			invokeSimpleProfileDlg(pi, eventTarget);
			break;
			
		case SHOW_USER_PROPERTIES:
			// Invoke the user properties dialog.
			invokeUserPropertiesDlg(pi, eventTarget);
			break;
		}
    }
    
	/**
	 * Called to render an instance of this cell.
	 * 
	 * @param context
	 * @param pi
	 * @param sb
	 * 
	 * Overrides AbstractCell.render()
	 */
	@Override
	public void render(Context context, PrincipalInfo pi, SafeHtmlBuilder sb) {
		// If we weren't given a PrincipalInfo...
		if (null == pi) {
			// ...bail.  Cell widgets can pass null to cells if the
			// ...underlying data contains a null, or if the data
			// ...arrives out of order.
			GwtClientHelper.renderEmptyHtml(sb);
			return;
		}

		// Generate the presence control...
		VibeFlowPanel fp = new VibeFlowPanel();
		GwtPresenceInfo presence = pi.getPresence();
		PresenceControl presenceControl = new PresenceControl(String.valueOf(pi.getId()), String.valueOf(pi.getPresenceUserWSId()), false, false, false, presence);
		presenceControl.setImageAlignment("top");
		presenceControl.addStyleName("vibe-dataTablePresence-control displayInline verticalAlignTop");
		presenceControl.setAnchorStyleName("cursorPointer");
		presenceControl.getElement().setAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE, VibeDataTableConstants.CELL_WIDGET_PRESENCE);
		presenceControl.setImageOverride(getPresenceImage(pi));
		presenceControl.addImageStyleName("vibe-dataTablePresence-image");
		fp.add(presenceControl);
		
		// ...add a name link for it...
		Label presenceLabel = new Label(pi.getTitle());
		presenceLabel.addStyleName("vibe-dataTablePresence-label");
		presenceLabel.addStyleName(pi.isUserDisabled() ? "vibe-dataTablePresence-disabled" : "vibe-dataTablePresence-enabled");
		if ((!(pi.isUserWSInTrash())) && (!(pi.isUserDisabled()))) {
			// ...unless the user's workspace is in the trash or the
			// ...user is disabled...
			presenceLabel.getElement().setAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE, VibeDataTableConstants.CELL_WIDGET_PRESENCE_LABEL);
		}
		String hover = pi.getEmailAddress();
		if (GwtClientHelper.hasString(hover)) {
			presenceLabel.setTitle(hover);
		}
		fp.add(presenceLabel);
		
		// ...and render that into the cell.
		SafeHtml rendered = SafeHtmlUtils.fromTrustedString(fp.getElement().getInnerHTML());
		sb.append(rendered);
	}
}

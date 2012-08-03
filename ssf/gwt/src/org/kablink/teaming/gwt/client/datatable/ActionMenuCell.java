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
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.EntryTitleInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;

/**
 * Data table cell that represents a menu for an entry
 * 
 * @author drfoster@novell.com
 */
public class ActionMenuCell extends AbstractCell<EntryTitleInfo> {
	private GwtTeamingDataTableImageBundle	m_images;	//
	private GwtTeamingMessages				m_messages;	//
	
	/**
	 * Constructor method.
	 */
	public ActionMenuCell() {
		// Sink the events we need to process an entry title...
		super(
			VibeDataTableConstants.CELL_EVENT_CLICK,
			VibeDataTableConstants.CELL_EVENT_KEYDOWN,
			VibeDataTableConstants.CELL_EVENT_MOUSEOVER,
			VibeDataTableConstants.CELL_EVENT_MOUSEOUT);

		// ...and initialize everything else.
		m_images   = GwtTeaming.getDataTableImageBundle();
		m_messages = GwtTeaming.getMessages();
	}

	/*
	 * Called when the mouse leaves the action menu image.
	 */
	private void handleMouseOut(Element eventTarget) {
		eventTarget.removeClassName("vibe-dataTableActions-hover");
		eventTarget.setAttribute("src", m_images.entryActions1().getSafeUri().asString());
	}
	
	/*
	 * Called when the mouse enters the action menu image.
	 */
	private void handleMouseOver(Element eventTarget) {
		eventTarget.addClassName("vibe-dataTableActions-hover");
		eventTarget.setAttribute("src", m_images.entryActions2().getSafeUri().asString());
	}
	
	/**
     * Called when an event occurs in a rendered instance of this
     * cell.  The parent element refers to the element that contains
     * the rendered cell, NOT to the outermost element that the cell
     * rendered.
     * 
     * @param context
     * @param parent
     * @param eti
     * @param event
     * @param valueUpdater
     * 
     * Overrides AbstractCell.onBrowserEvent()
     */
	@Override
    public void onBrowserEvent(Context context, Element parent, EntryTitleInfo eti, NativeEvent event, ValueUpdater<EntryTitleInfo> valueUpdater) {
		// What type of event are we processing?
    	String eventType = event.getType();
    	if (VibeDataTableConstants.CELL_EVENT_KEYDOWN.equals(eventType)) {
        	// A key down!  Let AbstractCell handle it.  It will
    		// convert it to an entry key down, ... as necessary.
        	super.onBrowserEvent(context, parent, eti, event, valueUpdater);
    	}
    	
    	else {
    		// Something other than a key down!  Is it targeted to this
    		// action menu image?
    		Element	eventTarget  = Element.as(event.getEventTarget()                                    );
    		String	wt           = eventTarget.getAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE);
    		if ((null != wt) && wt.equals(VibeDataTableConstants.CELL_WIDGET_ENTRY_ACTION_MENU_IMAGE)){
    			// Yes!  What type of event are we processing?
		    	if (VibeDataTableConstants.CELL_EVENT_CLICK.equals(eventType)) {
		    		// A click!  Remove the hover and show the action
		    		// menu.
		    		handleMouseOut(eventTarget);
					showActionMenu(eventTarget);
		    	}
		    	
		    	else if (VibeDataTableConstants.CELL_EVENT_MOUSEOVER.equals(eventType)) {
		    		// A mouse over!  Add the hover style.
		    		handleMouseOver(eventTarget);
		    	}
		    	
		    	else if (VibeDataTableConstants.CELL_EVENT_MOUSEOUT.equals(eventType)) {
		    		// A mouse out!  Remove the hover style.
		    		handleMouseOut(eventTarget);
		    	}
    		}
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
    protected void onEnterKeyDown(Context context, Element parent, EntryTitleInfo eti, NativeEvent event, ValueUpdater<EntryTitleInfo> valueUpdater) {
    	Element eventTarget = Element.as(event.getEventTarget());
		showActionMenu(eventTarget);
    }
    
	/**
	 * Called to render an instance of this cell.
	 * 
	 * @param context
	 * @param eti
	 * @param sb
	 * 
	 * Overrides AbstractCell.render()
	 */
	@Override
	public void render(Context context, EntryTitleInfo eti, SafeHtmlBuilder sb) {
		// If we weren't given a EntryTitleInfo...
		if (null == eti) {
			// ...bail.  Cell widgets can pass null to cells if the
			// ...underlying data contains a null, or if the data
			// ...arrives out of order.
			GwtClientHelper.renderEmptyHtml(sb);
			return;
		}

		// Create a panel to contain the HTML rendering...
		VibeFlowPanel fp = new VibeFlowPanel();
		
		// ...generate the appropriate widgets...
		Image actionMenuImg = GwtClientHelper.buildImage(m_images.entryActions1().getSafeUri().asString());
		actionMenuImg.addStyleName("vibe-dataTableActions-img");
		actionMenuImg.setTitle(m_messages.vibeDataTable_Alt_EntryActions());
		Element amiE = actionMenuImg.getElement();
		amiE.setAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE, VibeDataTableConstants.CELL_WIDGET_ENTRY_ACTION_MENU_IMAGE);
		amiE.setAttribute(VibeDataTableConstants.CELL_WIDGET_ENTITY_ID, eti.getEntityId().getEntityIdString()                     );
		fp.add(actionMenuImg);
		
		// ...and render that into the cell.
		SafeHtml rendered = SafeHtmlUtils.fromTrustedString(fp.getElement().getInnerHTML());
		sb.append(rendered);
	}

	/*
	 * Shows the action menu for the given entity.
	 */
	private void showActionMenu(Element eventTarget) {
//!		...this needs to be implemented...
		EntityId eid = EntityId.parseEntityIdString(eventTarget.getAttribute(VibeDataTableConstants.CELL_WIDGET_ENTITY_ID));
		GwtClientHelper.deferredAlert("ActionMenuCell.showActionMenu( " + eid.getEntityIdString() + " ):  ...this needs to be implemented...");
	}
}

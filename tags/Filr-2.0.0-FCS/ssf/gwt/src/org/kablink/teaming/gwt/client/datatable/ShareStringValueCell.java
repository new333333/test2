/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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

import java.util.List;

import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.ShareMessageInfo;
import org.kablink.teaming.gwt.client.util.ShareStringValue;
import org.kablink.teaming.gwt.client.widgets.HoverHintPopup;
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
 * Data table cell that represents a list of string values 
 * corresponding to a list of values for a share item.
 * 
 * @author drfoster@novell.com
 */
public class ShareStringValueCell extends AbstractCell<List<ShareStringValue>> {
	private HoverHintPopup	m_hoverHintPopup;	//
	
	/**
	 * Constructor method.
	 */
	public ShareStringValueCell() {
		// Sink the events we need to process on a share string.
		super(
			VibeDataTableConstants.CELL_EVENT_MOUSEOVER,
			VibeDataTableConstants.CELL_EVENT_MOUSEOUT);
	}

	/*
	 * Returns true if we're working with share messages and false
	 * otherwise.
	 */
	private boolean isShareMessages(List<ShareStringValue> ssvList) {
		boolean reply = GwtClientHelper.hasItems(ssvList);
		if (reply) {
			reply = (ssvList.get(0) instanceof ShareMessageInfo);
		}
		return reply;
	}
	
	/**
     * Called when an event occurs in a rendered instance of this
     * cell.  The parent element refers to the element that contains
     * the rendered cell, NOT to the outermost element that the cell
     * rendered.
     * 
     * @param context
     * @param parent
     * @param ssvList
     * @param event
     * @param valueUpdater
     * 
     * Overrides AbstractCell.onBrowserEvent()
     */
    @Override
    public void onBrowserEvent(Context context, Element parent, List<ShareStringValue> ssvList, NativeEvent event, ValueUpdater<List<ShareStringValue>> valueUpdater) {
    	// Do we show details of the share string when it's clicked?
    	if (!(isShareMessages(ssvList))) {
    		// No!  Ignore the event.
    		return;
    	}
    	
    	// Is the event targeted to the share string? 
		Element eventTarget = Element.as(event.getEventTarget());
		String wt = eventTarget.getAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE);
		if (!(GwtClientHelper.hasString(wt))) {
			eventTarget = eventTarget.getParentElement();
			wt = eventTarget.getAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE);
		}
		boolean isLabel = ((null != wt) && wt.equals(VibeDataTableConstants.CELL_WIDGET_ENTRY_TITLE_LABEL));
		if (!isLabel) {
			// No!  Ignore it.
			return;
		}
		
		// What type of event are we processing?
    	String eventType = event.getType();
    	if (VibeDataTableConstants.CELL_EVENT_MOUSEOVER.equals(eventType)) {
    		// A mouse over!  Is there a message?
    		int svsIndex = Integer.parseInt(eventTarget.getAttribute(VibeDataTableConstants.CELL_WIDGET_INDEX));
    		ShareMessageInfo smi = ((ShareMessageInfo) ssvList.get(svsIndex));
    		String sm= smi.getShareMessage();
    		if (GwtClientHelper.hasString(sm)) {
    			// Yes!  If we haven't created a hover hint panel
    			// yet...
				if (null == m_hoverHintPopup) {
					// ...create it now...
					m_hoverHintPopup = new HoverHintPopup();
				}
				
				// ...and show it with the message.
				m_hoverHintPopup.setHoverText(smi.getShareMessage(), false);
				m_hoverHintPopup.showHintRelativeTo(eventTarget);
    		}
    		
    		else if (null != m_hoverHintPopup) {
				// ...make sure it's hidden.
				m_hoverHintPopup.hide();
			}
    	}
    	
    	else if (VibeDataTableConstants.CELL_EVENT_MOUSEOUT.equals(eventType)) {
    		// A mouse out!  If there's a title hint panel...
			if (null != m_hoverHintPopup) {
				// ...make sure it's hidden.
				m_hoverHintPopup.hide();
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
     * @param ssvList
     * @param event
     * @param valueUpdater
     * 
     * Overrides AbstractCell.onEnterKeyDown()
     */
    @Override
    protected void onEnterKeyDown(Context context, Element parent, List<ShareStringValue> ssvList, NativeEvent event, ValueUpdater<List<ShareStringValue>> valueUpdater) {
    	// If we don't have to show details of the share string when
    	// it's clicked...
    	if (!(isShareMessages(ssvList))) {
    		// ...simply ignore the event.
    		return;
    	}
    }
    
	/**
	 * Called to render an instance of this cell.
	 * 
	 * @param context
	 * @param ssvList
	 * @param sb
	 * 
	 * Overrides AbstractCell.render()
	 */
	@Override
	public void render(Context context, List<ShareStringValue> ssvList, SafeHtmlBuilder sb) {
		// If we weren't given a List<ShareStringValue>...
		if ((null == ssvList) || ssvList.isEmpty()) {
			// ...bail.  Cell widgets can pass null to cells if the
			// ...underlying data contains a null, or if the data
			// ...arrives out of order.
			GwtClientHelper.renderEmptyHtml(sb);
			return;
		}

		// Create the panel to hold the HTML of the strings.
		VibeFlowPanel renderPanel = new VibeFlowPanel();

		// Scan the string values.
		int svsIndex = (-1);
		for (ShareStringValue ssv:  ssvList) {
			// Generate a panel to hold the string.
			svsIndex += 1;
			VibeFlowPanel fp = new VibeFlowPanel();
			fp.addStyleName("vibe-dataTableShareStringValue-panel displayBlock verticalAlignMiddle");
			if (0 < svsIndex) {
				fp.addStyleName("margintop3px");
			}
			String	v = ssv.getValue();
			Label	l = new Label(v);
			if (ssv instanceof ShareMessageInfo) {
				Element lE = l.getElement();
				lE.setAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE, VibeDataTableConstants.CELL_WIDGET_ENTRY_TITLE_LABEL);
				lE.setAttribute(VibeDataTableConstants.CELL_WIDGET_INDEX,     String.valueOf(svsIndex)                            );
			}
			else {
				l.setTitle(ssv.getValue());
			}
			l.addStyleName("vibe-dataTableShareStringValue-label");
			String addedStyle = ssv.getAddedStyle();
			if (GwtClientHelper.hasString(addedStyle)) {
				l.addStyleName(addedStyle);
			}
			fp.add(l);
			renderPanel.add(fp);
		}
		
		// Finally, render the panel with the list of strings into the
		// cell.
		SafeHtml rendered = SafeHtmlUtils.fromTrustedString(renderPanel.getElement().getInnerHTML());
		sb.append(rendered);
	}
}

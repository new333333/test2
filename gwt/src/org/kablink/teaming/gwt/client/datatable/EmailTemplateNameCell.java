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

import org.kablink.teaming.gwt.client.util.EntryTitleInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.HoverHintPopup;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Data table cell that represents an email template's name.
 * 
 * @author drfoster@novell.com
 */
public class EmailTemplateNameCell extends AbstractCell<EntryTitleInfo> {
	private HoverHintPopup	m_hoverHintPopup;	// A hint popup that gets displayed when the user mouses over this cell.

	// The following is used as the class name on the root <DIV>
	// containing this cell.
	private static final String ENTRY_TITLE_ROOT_STYLE	= "vibe-dataTableEntry-panel";
	
	/**
	 * Constructor method.
	 * 
	 * @param fla
	 * @param bi
	 * @param uploadHost
	 */
	public EmailTemplateNameCell() {
		// Sink the events we need to process an email template name.
		super(
			VibeDataTableConstants.CELL_EVENT_CLICK,
			VibeDataTableConstants.CELL_EVENT_KEYDOWN,
			VibeDataTableConstants.CELL_EVENT_MOUSEOVER,
			VibeDataTableConstants.CELL_EVENT_MOUSEOUT);
	}

	/*
	 * Adds the styles to an element to reflect a mouse hover.
	 */
	private void hoverStyleAdd(EntryTitleInfo eti, Element e) {
		e.addClassName("vibe-dataTableLink-hover");
		if (null != eti.getClientItemImage()) {
			e.addClassName("vibe-dataTableLink-hoverNoLPad");
		}
	}
	
	/*
	 * Removes the styles from an element that reflect a mouse hover.
	 */
	private void hoverStyleRemove(EntryTitleInfo eti, Element e) {
		e.removeClassName("vibe-dataTableLink-hover"      );
		e.removeClassName("vibe-dataTableLink-hoverNoLPad");
	}
	
	/**
     * Called when an event occurs in a rendered instance of this cell.
     * The parent element refers to the element that contains the
     * rendered cell, NOT to the outermost element that the cell
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
    	// Which of our email template name widgets is being operated
    	// on? 
		Element eventTarget = Element.as(event.getEventTarget());
		String wt = eventTarget.getAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE);
		if (!(GwtClientHelper.hasString(wt))) {
			eventTarget = eventTarget.getParentElement();
			wt = eventTarget.getAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE);
		}
		boolean isLabel = ((null != wt) && wt.equals(VibeDataTableConstants.CELL_WIDGET_ENTRY_TITLE_LABEL ));

		// What type of event are we processing?
    	String eventType = event.getType();
    	if (VibeDataTableConstants.CELL_EVENT_KEYDOWN.equals(eventType)) {
        	// A key down!  Let AbstractCell handle it.  It will
    		// convert it to an entry key down, ... as necessary.
        	super.onBrowserEvent(context, parent, eti, event, valueUpdater);
    	}

    	else if (VibeDataTableConstants.CELL_EVENT_CLICK.equals(eventType)) {
    		// A click!  Is it the label being clicked?
    		if (isLabel) {
    			// Yes!  Strip off any over style.
    			hoverStyleRemove(eti, eventTarget);
    		}
    	}
    	
    	else if (isLabel && VibeDataTableConstants.CELL_EVENT_MOUSEOVER.equals(eventType)) {
    		// A mouse over!  Add the hover style...
    		hoverStyleAdd(eti, eventTarget);
			
			// ...if have a description...
			String	description       = eti.getDescription();
			boolean	descriptionIsHTML = eti.isDescriptionHtml();
			if (eti.isFile() && (!(GwtClientHelper.hasString(description)))) {
				description       = eti.getTitle();
				descriptionIsHTML = false;
			}
			if (GwtClientHelper.hasString(description)) {
				// ...and we haven't create a popup panel for the hover
				// ...HTML yet...
				if (null == m_hoverHintPopup) {
					// ...create it now...
					m_hoverHintPopup = new HoverHintPopup();
				}
				
				// ...and show it with the description HTML.
				m_hoverHintPopup.setHoverText(description, descriptionIsHTML);
				m_hoverHintPopup.showHintRelativeTo(eventTarget);
			}
			
			else if (null != m_hoverHintPopup) {
				m_hoverHintPopup.hide();
			}
    	}
    	
    	else if (isLabel && VibeDataTableConstants.CELL_EVENT_MOUSEOUT.equals(eventType)) {
    		// A mouse out!  Remove the hover style...
    		hoverStyleRemove(eti, eventTarget);
			
			// ...and if there's a hint panel...
			if (null != m_hoverHintPopup) {
				// ...make sure it's hidden.
				m_hoverHintPopup.hide();
			}
    	}
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

		// Initialize the variables required to render the email
		// template name cell.
		VibeFlowPanel html = new VibeFlowPanel();
		
		// We don't word wrap the email template name.
		VibeFlowPanel etContainerWidget = new VibeFlowPanel();
		etContainerWidget.addStyleName(ENTRY_TITLE_ROOT_STYLE);
		html.add(etContainerWidget );
		etContainerWidget.addStyleName("gwtUI_nowrap");
		
		// Add the email template name link...
		Anchor a = new Anchor();
		a.setHref(eti.getFileDownloadUrl());
		a.setTarget("_blank");
		Widget titleWidget = a;
		titleWidget.setStyleName("vibe-dataTableEntry-titleLinkPanel");
		Element titleElement = titleWidget.getElement();
		InlineLabel titleLabel = new InlineLabel(eti.getTitle());
		titleLabel.addStyleName("vibe-dataTableEntry-title");
		Element elE = titleLabel.getElement(); 
		String widgetAttr = VibeDataTableConstants.CELL_WIDGET_ENTRY_TITLE_LABEL;
		elE.setAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE, widgetAttr);
		elE.setId(VibeDataTableConstants.CELL_WIDGET_ENTRY_TITLE_LABEL + "_" + eti.getTitle());
		titleElement.appendChild(titleLabel.getElement());
		etContainerWidget.add(titleWidget);
		
		// ...and render that into the cell.
		SafeHtml rendered = SafeHtmlUtils.fromTrustedString(html.getElement().getInnerHTML());
		sb.append(rendered);
	}
}

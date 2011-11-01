/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * Data table cell that represents a file download link.
 * 
 * @author drfoster@novell.com
 */
public class DownloadCell extends AbstractCell<Long> {
	/**
	 * Constructor method.
	 */
	public DownloadCell() {
		/*
		 * Sink the events we need to process a download link.
	     */
		super(
			VibeDataTable.CELL_EVENT_CLICK,
			VibeDataTable.CELL_EVENT_KEYDOWN,
			VibeDataTable.CELL_EVENT_MOUSEOVER,
			VibeDataTable.CELL_EVENT_MOUSEOUT);
	}

	/*
	 * Invokes a file download on an entry.
	 */
	private void invokeFileDownload(final Long entryId, Element pElement) {
//!		...this needs to be implemented...
		Window.alert("DownloadCell.invokeFileDownload()");
	}
	
	/**
     * Called when an event occurs in a rendered instance of this
     * cell.  The parent element refers to the element that contains
     * the rendered cell, NOT to the outermost element that the cell
     * rendered.
     * 
     * @param context
     * @param parent
     * @param entryId
     * @param event
     * @param valueUpdater
     * 
     * Overrides AbstractCell.onBrowserEvent()
     */
    @Override
    public void onBrowserEvent(Context context, Element parent, Long entryId, NativeEvent event, ValueUpdater<Long> valueUpdater) {
    	// Which of our download widgets is being operated on? 
		Element eventTarget = Element.as(event.getEventTarget());
		String wt = eventTarget.getAttribute(VibeDataTable.CELL_WIDGET_ATTRIBUTE);
		boolean isLabel = ((null != wt) && wt.equals(VibeDataTable.CELL_WIDGET_ENTRY_DOWNLOAD_LABEL ));

		// What type of event are we processing?
    	String eventType = event.getType();
    	if (VibeDataTable.CELL_EVENT_KEYDOWN.equals(eventType)) {
        	// A key down!  Let AbstractCell handle it.  It will
    		// convert it to an entry key down, ... as necessary.
        	super.onBrowserEvent(context, parent, entryId, event, valueUpdater);
    	}

    	else if (VibeDataTable.CELL_EVENT_CLICK.equals(eventType)) {
    		// A click!  Is it the label being clicked?
    		if (isLabel) {
    			// Yes!  Strip off any over style.
    			eventTarget.removeClassName("vibe-dataTableLink-hover");
    			invokeFileDownload(entryId, eventTarget);
    		}
    	}
    	
    	else if (isLabel && VibeDataTable.CELL_EVENT_MOUSEOVER.equals(eventType)) {
    		// A mouse over!  Add the hover style.
			eventTarget.addClassName("vibe-dataTableLink-hover");
    	}
    	
    	else if (isLabel && VibeDataTable.CELL_EVENT_MOUSEOUT.equals(eventType)) {
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
    protected void onEnterKeyDown(Context context, Element parent, Long value, NativeEvent event, ValueUpdater<Long> valueUpdater) {
    	invokeFileDownload(value, Element.as(event.getEventTarget()));
    }
    
	/**
	 * Called to render an instance of this cell.
	 * 
	 * @param context
	 * @param entryId
	 * @param sb
	 * 
	 * Overrides AbstractCell.render()
	 */
	@Override
	public void render(Context context, Long entryId, SafeHtmlBuilder sb) {
		// If we weren't given a Long...
		if (null == entryId) {
			// ...bail.  Cell widgets can pass null to cells if the
			// ...underlying data contains a null, or if the data
			// ...arrives out of order.
			return;
		}

		// Create the download link...
		VibeFlowPanel fp = new VibeFlowPanel();
		InlineLabel downloadLabel = new InlineLabel(GwtTeaming.getMessages().vibeDataTable_Download());
		downloadLabel.addStyleName("vibe-dataTableEntry-download");
		Element elE = downloadLabel.getElement(); 
		elE.setAttribute(VibeDataTable.CELL_WIDGET_ATTRIBUTE, VibeDataTable.CELL_WIDGET_ENTRY_DOWNLOAD_LABEL);
		elE.setId(VibeDataTable.CELL_WIDGET_ENTRY_DOWNLOAD_LABEL + "_" + entryId);
		fp.add(downloadLabel);
		
		// ...and render that into the cell.
		SafeHtml rendered = SafeHtmlUtils.fromTrustedString(fp.getElement().getInnerHTML());
		sb.append(rendered);
	}
}

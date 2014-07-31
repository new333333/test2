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

import org.kablink.teaming.gwt.client.util.EmailAddressInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * Data table cell that represents an email address.
 * 
 * @author drfoster@novell.com
 */
public class EmailAddressCell extends AbstractCell<EmailAddressInfo> {
	/**
	 * Constructor method.
	 */
	public EmailAddressCell() {
		/*
		 * Sink the events we need to process an email address.
	     */
		super(
			VibeDataTableConstants.CELL_EVENT_CLICK,
			VibeDataTableConstants.CELL_EVENT_KEYDOWN,
			VibeDataTableConstants.CELL_EVENT_MOUSEOVER,
			VibeDataTableConstants.CELL_EVENT_MOUSEOUT);
	}

	/*
	 * Asynchronously invokes an email address.
	 */
	private void invokeEmailAddressAsync(final EmailAddressInfo emai) {
		Scheduler.ScheduledCommand doSendToEMA = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				jsInvokeEmailAddressNow("mailto:" + emai.getEmailAddress());
			}
		};
		Scheduler.get().scheduleDeferred(doSendToEMA);
	}
	
	/*
	 * Synchronously invokes an email address.
	 */
	private native void jsInvokeEmailAddressNow(String url) /*-{ 
		$wnd.location = url; 
	}-*/; 
	
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
    public void onBrowserEvent(Context context, Element parent, EmailAddressInfo eti, NativeEvent event, ValueUpdater<EmailAddressInfo> valueUpdater) {
    	// Which of our email address widgets is being operated on? 
		Element eventTarget = Element.as(event.getEventTarget());
		String wt = eventTarget.getAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE);
		boolean isLabel     = ((null != wt) && wt.equals(VibeDataTableConstants.CELL_WIDGET_EMAIL_ADDRESS_LABEL ));

		// What type of event are we processing?
    	String eventType = event.getType();
    	if (VibeDataTableConstants.CELL_EVENT_KEYDOWN.equals(eventType)) {
        	// A key down!  Let AbstractCell handle it.  It will
    		// convert it to a key down, ... as necessary.
        	super.onBrowserEvent(context, parent, eti, event, valueUpdater);
    	}

    	else if (VibeDataTableConstants.CELL_EVENT_CLICK.equals(eventType)) {
    		// A click!  Is it the label being clicked?
    		if (isLabel) {
    			// Yes!  Strip off any over style.
    			eventTarget.removeClassName("vibe-dataTableEMA-hover");
    			invokeEmailAddressAsync(eti);
    		}
    	}
    	
    	else if (isLabel && VibeDataTableConstants.CELL_EVENT_MOUSEOVER.equals(eventType)) {
    		// A mouse over!  Add the hover style...
			eventTarget.addClassName("vibe-dataTableEMA-hover");
    	}
    	
    	else if (isLabel && VibeDataTableConstants.CELL_EVENT_MOUSEOUT.equals(eventType)) {
    		// A mouse out!  Remove the hover style...
			eventTarget.removeClassName("vibe-dataTableEMA-hover");
    	}
    }
    
    /**
     * Called when the user presses the ENTER key will the cell is
     * selected.  You are not required to override this method, but
     * it's a common convention that allows your cell to respond to key
     * events.
     * 
     * Overrides AbstractCell.onEnterKeyDown()
     * 
     * @param context
     * @param parent
     * @param emai
     * @param event
     * @param valueUpdater
     */
    @Override
    protected void onEnterKeyDown(Context context, Element parent, EmailAddressInfo emai, NativeEvent event, ValueUpdater<EmailAddressInfo> valueUpdater) {
    	invokeEmailAddressAsync(emai);
    }
    
	/**
	 * Called to render an instance of this cell.
	 * 
	 * @param context
	 * @param emai
	 * @param sb
	 * 
	 * Overrides AbstractCell.render()
	 */
	@Override
	public void render(Context context, EmailAddressInfo emai, SafeHtmlBuilder sb) {
		// If we weren't given a EmailAddressInfo...
		if (null == emai) {
			// ...bail.  Cell widgets can pass null to cells if the
			// ...underlying data contains a null, or if the data
			// ...arrives out of order.
			GwtClientHelper.renderEmptyHtml(sb);
			return;
		}

		VibeFlowPanel fp = new VibeFlowPanel();
		
		// ...add the title link...
		boolean isUserWSTrash = emai.isUserWSInTrash();
		String ema = emai.getEmailAddress();
		if (null == ema) {
			ema = "";
		}
		boolean emaIsLink = ((!isUserWSTrash) && (0 < ema.length()));
		InlineLabel emaLabel = new InlineLabel(ema);
		emaLabel.addStyleName(emaIsLink ? "vibe-dataTableEMA-title" : "vibe-dataTableEMA-titleNoLink");
		Element elE = emaLabel.getElement(); 
		String widgetAttr = (emaIsLink ? VibeDataTableConstants.CELL_WIDGET_EMAIL_ADDRESS_LABEL : VibeDataTableConstants.CELL_WIDGET_EMAIL_ADDRESS_LABEL_NOLINK);
		elE.setAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE, widgetAttr);
		fp.add(emaLabel);
		
		// ...and render that into the cell.
		SafeHtml rendered = SafeHtmlUtils.fromTrustedString(fp.getElement().getInnerHTML());
		sb.append(rendered);
	}
}

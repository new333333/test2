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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.MobileDeviceWipeScheduleStateChangedEvent;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.SetMobileDevicesWipeScheduledStateCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * Data table cell that represents a mobile devices wipe scheduled
 * status.
 * 
 * @author drfoster@novell.com
 */
public class MobileDeviceWipeScheduledCell extends AbstractCell<MobileDeviceWipeScheduleInfo> {
	private GwtTeamingMessages	m_messages;	// Access to the Vibe string resources we need for this cell.
	
	/**
	 * Constructor method.
	 */
	public MobileDeviceWipeScheduledCell() {
		// Sink the events we need to process selecting the cell...
		super(
			VibeDataTableConstants.CELL_EVENT_CLICK,
			VibeDataTableConstants.CELL_EVENT_KEYDOWN,
			VibeDataTableConstants.CELL_EVENT_MOUSEOVER,
			VibeDataTableConstants.CELL_EVENT_MOUSEOUT);
		
		// ...and initialize everything else.
		m_messages = GwtTeaming.getMessages();
	}

	/**
     * Called when an event occurs in a rendered instance of this
     * cell.  The parent element refers to the element that contains
     * the rendered cell, NOT to the outermost element that the cell
     * rendered.
     * 
     * @param context
     * @param parent
     * @param wipeScheduled
     * @param event
     * @param valueUpdater
     * 
     * Overrides AbstractCell.onBrowserEvent()
     */
	@Override
    public void onBrowserEvent(Context context, Element parent, MobileDeviceWipeScheduleInfo wipeScheduled, NativeEvent event, ValueUpdater<MobileDeviceWipeScheduleInfo> valueUpdater) {
		// What type of event are we processing?
    	String eventType = event.getType();
    	if (VibeDataTableConstants.CELL_EVENT_KEYDOWN.equals(eventType)) {
        	// A key down!  Let AbstractCell handle it.  It will
    		// convert it to an entry key down, ... as necessary.
        	super.onBrowserEvent(context, parent, wipeScheduled, event, valueUpdater);
    	}
    	
    	else {
    		// Something other than a key down!  Is it targeted to this
    		// mobile devices cell?
    		Element	eventTarget  = Element.as(event.getEventTarget()                                    );
    		String	wt           = eventTarget.getAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE);
    		if ((null != wt) && wt.equals(VibeDataTableConstants.CELL_WIDGET_MOBILE_WIPE_SCHEDULED)) {
    			// Yes!  What type of event are we processing?
		    	if (VibeDataTableConstants.CELL_EVENT_CLICK.equals(eventType)) {
		    		// A click!  Toggle the device's wipe scheduled
		    		// status.
					toggleWipeStatusAsync(wipeScheduled, eventTarget);
		    	}
		    	
		    	else if (VibeDataTableConstants.CELL_EVENT_MOUSEOVER.equals(eventType)) {
		    		// A mouse over!  Add the hover style.
		    		eventTarget.addClassName("vibe-dataTableLink-hover");
		    	}
		    	
		    	else if (VibeDataTableConstants.CELL_EVENT_MOUSEOUT.equals(eventType)) {
		    		// A mouse out!  Remove the hover style.
		    		eventTarget.removeClassName("vibe-dataTableLink-hover");
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
     * 
     * @param context
     * @param parent
     * @param emai
     * @param event
     * @param valueUpdater
     */
    @Override
    protected void onEnterKeyDown(Context context, Element parent, MobileDeviceWipeScheduleInfo wipeScheduled, NativeEvent event, ValueUpdater<MobileDeviceWipeScheduleInfo> valueUpdater) {
    	// If the key down is targeted to the devices panel...
    	Element eventTarget = Element.as(event.getEventTarget());
		String	wt           = eventTarget.getAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE);
		if ((null != wt) && wt.equals(VibeDataTableConstants.CELL_WIDGET_MOBILE_WIPE_SCHEDULED)){
			// ...toggle the device's wipe scheduled status.
			toggleWipeStatusAsync(wipeScheduled, eventTarget);
		}
    }
    
	/**
	 * Called to render an instance of this cell.
	 * 
	 * @param context
	 * @param wipeScheduled
	 * @param sb
	 * 
	 * Overrides AbstractCell.render()
	 */
	@Override
	public void render(Context context, MobileDeviceWipeScheduleInfo wipeScheduled, SafeHtmlBuilder sb) {
		// If we weren't given a MobileDeviceWipeScheduleInfo...
		if (null == wipeScheduled) {
			// ...bail.  Cell widgets can pass null to cells if the
			// ...underlying data contains a null, or if the data
			// ...arrives out of order.
			GwtClientHelper.renderEmptyHtml(sb);
			return;
		}

		// Create the HTML for the row's wipe scheduled...
		VibeFlowPanel html = new VibeFlowPanel();
		VibeFlowPanel wipePanel = new VibeFlowPanel();
		wipePanel.addStyleName("vibe-dataTableMobileDeviceWipe-panel");
		InlineLabel wipeDisplay = new InlineLabel(wipeScheduled.getDisplay());
		wipeDisplay.addStyleName("vibe-dataTableMobileDeviceWipe-text");
		
		Element wdE = wipeDisplay.getElement();
		wipeDisplay.addStyleName("cursorPointer");
		String title;
		if (wipeScheduled.isWipeScheduled())
		     title = m_messages.vibeDataTable_Alt_CancelWipe();
		else title = m_messages.vibeDataTable_Alt_ScheduleWipe();
		wipeDisplay.setTitle(title);
		wdE.setId("wipeScheduled-" + wipeScheduled.getEntityId().getEntityIdString());
		wdE.setAttribute(
			VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE,
			VibeDataTableConstants.CELL_WIDGET_MOBILE_WIPE_SCHEDULED);

		// ...add the display to the HTML panel...
		wipePanel.add(wipeDisplay);
		html.add(wipePanel);
		
		// ...and render that into the cell.
		SafeHtml rendered = SafeHtmlUtils.fromTrustedString(html.getElement().getInnerHTML());
		sb.append(rendered);
	}

	/*
	 * Asynchronously toggles the wipe scheduled state of the device. 
	 */
	private void toggleWipeStatusAsync(final MobileDeviceWipeScheduleInfo wipeScheduled, final Element wipeStatusElement) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				toggleWipeStatusNow(wipeScheduled, wipeStatusElement);
			}
		});
	}
	
	/*
	 * Synchronously toggles the wipe scheduled state of the device. 
	 */
	private void toggleWipeStatusNow(final MobileDeviceWipeScheduleInfo wipeScheduled, final Element wipeStatusElement) {
		// Can we toggle this device's wipe scheduled state?
		SetMobileDevicesWipeScheduledStateCmd cmd = new SetMobileDevicesWipeScheduledStateCmd(
			wipeScheduled.getEntityId(),
			(!(wipeScheduled.isWipeScheduled())));
		
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_SetMobileDevicesWipeScheduledState());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				boolean reply = ((BooleanRpcResponseData) response.getResponseData()).getBooleanValue();
				if (reply) {
					// Yes!  Update the cell's display to reflect the
					// change...
					boolean newWipeScheduled = (!(wipeScheduled.isWipeScheduled()));
					wipeScheduled.setWipeScheduled(newWipeScheduled);
					String display;
					String title;
					if (newWipeScheduled) {
						display = m_messages.yes();
						title   = m_messages.vibeDataTable_Alt_CancelWipe();
					}
					else {
						display = m_messages.no();
						title   = m_messages.vibeDataTable_Alt_ScheduleWipe();
					}
					wipeScheduled.setDisplay(      display);
					wipeStatusElement.setInnerText(display);
					wipeStatusElement.setTitle(    title  );
					
					// ...and let the data table know about it too.
					GwtTeaming.fireEventAsync(
						new MobileDeviceWipeScheduleStateChangedEvent(
							wipeScheduled));
				}
			}
		});
	}
}

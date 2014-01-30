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
package org.kablink.teaming.gwt.client.datatable;

import org.kablink.teaming.gwt.client.binderviews.MobileDevicesView;
import org.kablink.teaming.gwt.client.datatable.ManageMobileDevicesDlg;
import org.kablink.teaming.gwt.client.datatable.ManageMobileDevicesDlg.ManageMobileDevicesDlgClient;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.CreateDummyMobileDevicesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.MobileDeviceRemovedCallback;
import org.kablink.teaming.gwt.client.util.MobileDevicesInfo;
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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Data table cell that represents a mobile devices count on an user.
 * 
 * @author drfoster@novell.com
 */
public class MobileDevicesCell extends AbstractCell<MobileDevicesInfo> implements MobileDeviceRemovedCallback {
	private GwtTeamingMessages		m_messages;			// Access to the Vibe string resources we need for this cell.
	private ManageMobileDevicesDlg	m_manageMobileDevicesDlg;	// A manage mobile devices dialog, once one is instantiated.
	
	/**
	 * Constructor method.
	 */
	public MobileDevicesCell() {
		// Sink the events we need to process selecting the cell...
		super(
			VibeDataTableConstants.CELL_EVENT_CLICK,
			VibeDataTableConstants.CELL_EVENT_KEYDOWN);
		
		// ...and initialize everything else.
		m_messages = GwtTeaming.getMessages();
	}

	/*
	 * Asynchronously creates some dummy mobile devices and shows them
	 * in the mobile devices dialog.
	 */
	private void createDummyMobileDevicesAsync(final MobileDevicesInfo mdInfo, final Element relativeToThis) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				createDummyMobileDevicesNow(mdInfo, relativeToThis);
			}
		});
	}
	
	/*
	 * Synchronously creates some dummy mobile devices and shows them
	 * in the mobile devices dialog.
	 */
	private void createDummyMobileDevicesNow(final MobileDevicesInfo mdInfo, final Element relativeToThis) {
		GwtClientHelper.executeCommand(new CreateDummyMobileDevicesCmd(mdInfo.getUserId(), 5), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				// No!  Tell the user about the problem...
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_CreateDummyMobileDevices());
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						mobileDevicesRemoved(mdInfo, (-5));	// Updates the display to reflect the change.
						showManageMobileDevicesDlg(mdInfo, relativeToThis);
					}
				});
			}
		});
	}
	
	/**
	 * Called by the manage devices dialog saying a device was removed
	 * from the user.
	 * 
	 * Implements the MobileDeviceRemovedCallback.mobileDeviceRemoved() method.
	 * 
	 * @param callbackData
	 * @param count
	 */
	@Override
	public void mobileDevicesRemoved(Object callbackData, int count) {
		// Decrement the count of devices in the MobileDevicesInfo...
		MobileDevicesInfo mdi = ((MobileDevicesInfo) callbackData);
		int deviceCount = (mdi.getMobileDevicesCount() - count);
		if (0 > deviceCount) {
			deviceCount = 0;
		}
		mdi.setMobileDevicesCount(deviceCount);

		// ...and update the display of the device bubble.
		Element cpE = DOM.getElementById("mobileDevices-" + mdi.getUserId());
		if (0 == deviceCount) {
			cpE.addClassName("vibe-dataTableMobileDevices-panel0");
			cpE.removeClassName("cursorPointer");
			cpE.setInnerHTML("&nbsp;&nbsp;");
			cpE.setTitle(m_messages.vibeDataTable_Alt_MobileDevices_None());
			cpE.removeAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE);
		}
		
		else {
			cpE.removeClassName("vibe-dataTableMobileDevices-panel0");
			cpE.addClassName("cursorPointer");
			cpE.setInnerHTML(String.valueOf(deviceCount));
			cpE.setTitle(m_messages.vibeDataTable_Alt_MobileDevices());
			cpE.setAttribute(
				VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE,
				VibeDataTableConstants.CELL_WIDGET_MOBILE_DEVICES_PANEL);
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
     * @param mdInfo
     * @param event
     * @param valueUpdater
     * 
     * Overrides AbstractCell.onBrowserEvent()
     */
	@Override
    public void onBrowserEvent(Context context, Element parent, MobileDevicesInfo mdInfo, NativeEvent event, ValueUpdater<MobileDevicesInfo> valueUpdater) {
		// What type of event are we processing?
    	String eventType = event.getType();
    	if (VibeDataTableConstants.CELL_EVENT_KEYDOWN.equals(eventType)) {
        	// A key down!  Let AbstractCell handle it.  It will
    		// convert it to an entry key down, ... as necessary.
        	super.onBrowserEvent(context, parent, mdInfo, event, valueUpdater);
    	}
    	
    	else {
    		// Something other than a key down!  Is it targeted to this
    		// mobile devices cell?
    		Element	eventTarget  = Element.as(event.getEventTarget()                                    );
    		String	wt           = eventTarget.getAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE);
    		if ((null != wt) && wt.equals(VibeDataTableConstants.CELL_WIDGET_MOBILE_DEVICES_PANEL)) {
    			// Yes!  What type of event are we processing?
		    	if (VibeDataTableConstants.CELL_EVENT_CLICK.equals(eventType)) {
		    		// A click!  Run the manage devices dialog on the
		    		// user.
		    		if (0 == mdInfo.getMobileDevicesCount())
		    		     createDummyMobileDevicesAsync(mdInfo, eventTarget);
		    		else showManageMobileDevicesDlg(   mdInfo, eventTarget);
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
    protected void onEnterKeyDown(Context context, Element parent, MobileDevicesInfo mdInfo, NativeEvent event, ValueUpdater<MobileDevicesInfo> valueUpdater) {
    	// If the key down is targeted to the devices panel...
    	Element eventTarget = Element.as(event.getEventTarget());
		String	wt           = eventTarget.getAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE);
		if ((null != wt) && wt.equals(VibeDataTableConstants.CELL_WIDGET_MOBILE_DEVICES_PANEL)){
			// ...run the manage devices dialog on the entity.
    		if (0 == mdInfo.getMobileDevicesCount())
    		     createDummyMobileDevicesAsync(mdInfo, eventTarget);
    		else showManageMobileDevicesDlg(   mdInfo, eventTarget);
		}
    }
    
	/**
	 * Called to render an instance of this cell.
	 * 
	 * @param context
	 * @param mdInfo
	 * @param sb
	 * 
	 * Overrides AbstractCell.render()
	 */
	@Override
	public void render(Context context, MobileDevicesInfo mdInfo, SafeHtmlBuilder sb) {
		// If we weren't given a MobileDevicesInfo...
		if (null == mdInfo) {
			// ...bail.  Cell widgets can pass null to cells if the
			// ...underlying data contains a null, or if the data
			// ...arrives out of order.
			GwtClientHelper.renderEmptyHtml(sb);
			return;
		}

		// Create the HTML for the row's devices bubble...
		VibeFlowPanel html = new VibeFlowPanel();
		VibeFlowPanel devicesPanel = new VibeFlowPanel();
		devicesPanel.addStyleName("vibe-dataTableMobileDevices-panel");
		VibeFlowPanel devicesBubble = new VibeFlowPanel();
		devicesBubble.addStyleName("vibe-dataTableMobileDevices-bubble");
		
		// ...we use a wider bubble for >=100 devices...
		int deviceCount = mdInfo.getMobileDevicesCount();
		String addedBubbleStyle;
		if (100 <= deviceCount)
		     addedBubbleStyle = "vibe-dataTableMobileDevices-bubbleBig";
		else addedBubbleStyle = "vibe-dataTableMobileDevices-bubbleSmall";
		devicesBubble.addStyleName(addedBubbleStyle);
		
		// ...and if there are any devices...
		Element dpE = devicesBubble.getElement();
		boolean debugClickOnZero = MobileDevicesView.CLICK_ON_ZERO_TO_CREATE_DUMMIES;
		if ((0 < deviceCount) || debugClickOnZero) {
			// ...we make the bubble clickable so that the device list
			// ...can be managed...
			devicesBubble.addStyleName("cursorPointer"                             );
			devicesBubble.setTitle(    m_messages.vibeDataTable_Alt_MobileDevices());
			dpE.setId("mobileDevices-" + mdInfo.getUserId());
			dpE.setAttribute(
				VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE,
				VibeDataTableConstants.CELL_WIDGET_MOBILE_DEVICES_PANEL);
		}
		else {
			devicesBubble.setTitle(m_messages.vibeDataTable_Alt_MobileDevices_None());
		}

		// ...store the device count in the bubble...
		String devices;
		if (0 == deviceCount) {
			// ...for no devices, we store spaces, not a 0...
			dpE.addClassName("vibe-dataTableMobileDevices-panel0");
			devices = "&nbsp;&nbsp;";
		}
		else {
			devices = String.valueOf(deviceCount);
		}
		dpE.setInnerHTML(devices);
		
		// ...add the bubble to the HTML panel...
		devicesPanel.add(devicesBubble);
		html.add(devicesPanel);
		
		// ...and render that into the cell.
		SafeHtml rendered = SafeHtmlUtils.fromTrustedString(html.getElement().getInnerHTML());
		sb.append(rendered);
	}

	/*
	 * Runs the manage devices dialog against the given user. 
	 */
	private void showManageMobileDevicesDlg(final MobileDevicesInfo mdInfo, final Element relativeToThis) {
		if (!MobileDevicesView.SHOW_MOBILE_DEVICES_USER) {
			GwtClientHelper.deferredAlert("MobileDevicesCell.showManageMobileDevicesDlg():  ...this needs to be implemented...");
			return;
		}
		
		// Have we instantiated a manage devices dialog yet?
		if (null == m_manageMobileDevicesDlg) {
			// No!  Instantiate one now.
			ManageMobileDevicesDlg.createAsync(new ManageMobileDevicesDlgClient() {			
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(final ManageMobileDevicesDlg mmdDlg) {
					// ...and show it.
					m_manageMobileDevicesDlg = mmdDlg;
					showManageMobileDevicesDlgAsync(mdInfo, relativeToThis);
				}
			});
		}
		
		else {
			// Yes, we've instantiated a manage devices dialog already!
			// Simply show it.
			showManageMobileDevicesDlgAsync(mdInfo, relativeToThis);
		}
	}
	
	/*
	 * Asynchronously shows the manage devices dialog.
	 */
	private void showManageMobileDevicesDlgAsync(final MobileDevicesInfo mdInfo, final Element relativeToThis) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				showManageMobileDevicesDlgNow(mdInfo, relativeToThis);
			}
		});
	}
	
	/*
	 * Synchronously shows the manage devices dialog.
	 */
	private void showManageMobileDevicesDlgNow(final MobileDevicesInfo mdInfo, final Element relativeToThis) {
		ManageMobileDevicesDlg.initAndShow(
			m_manageMobileDevicesDlg,
			mdInfo,
			null,	// null -> Center the dialog.
			this);	// Provides a MobileDeviceRemovedCallback.
	}
}

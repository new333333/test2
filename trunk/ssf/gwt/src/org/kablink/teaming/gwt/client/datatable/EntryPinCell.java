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
import org.kablink.teaming.gwt.client.rpc.shared.PinEntryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.UnpinEntryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.EntryPinInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;


/**
 * Data table cell pin/unpin an entry.
 * 
 * @author drfoster@novell.com
 */
public class EntryPinCell extends AbstractCell<EntryPinInfo> {
	private GwtTeamingDataTableImageBundle	m_images;	// Access to the data table image bundle.
	private GwtTeamingMessages				m_messages;	// Access to the GWT localized string resource.
	
	/**
	 * Constructor method.
	 */
	public EntryPinCell() {
		// Sink the events we need to process a pin...
		super(
			VibeDataTableConstants.CELL_EVENT_CLICK,
			VibeDataTableConstants.CELL_EVENT_KEYDOWN,
			VibeDataTableConstants.CELL_EVENT_MOUSEOVER,
			VibeDataTableConstants.CELL_EVENT_MOUSEOUT);

		// ...and initialize the data members.
		m_images   = GwtTeaming.getDataTableImageBundle();
		m_messages = GwtTeaming.getMessages();
	}

	/*
	 * Toggles the pin state of an entry.
	 */
	private void toogleEntryPinState(final EntryPinInfo pinInfo, final Element pElement) {
		// Is the entry currently pinned?
		if (pinInfo.getPinned()) {
			// Yes!  Can we unpin it?
			UnpinEntryCmd cmd = new UnpinEntryCmd(pinInfo.getFolderId(), pinInfo.getEntryId());
			GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_UnpinEntry(),
						String.valueOf(pinInfo));
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response) {
					// Yes!  Update it to reflect its new state.
					pinInfo.setPinned(false);
					pElement.setAttribute("src",   m_images.grayPin().getSafeUri().asString());
					pElement.setAttribute("title", m_messages.vibeDataTable_Alt_PinEntry());
				}
			});
		}
		
		else {
			// No, the entry is not currently pinned!  Can we pin it?
			PinEntryCmd cmd = new PinEntryCmd(pinInfo.getFolderId(), pinInfo.getEntryId());
			GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_PinEntry(),
						String.valueOf(pinInfo));
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response) {
					// Yes!  Update it to reflect its new state.
					pinInfo.setPinned(true);
					pElement.setAttribute("src",   m_images.orangePin().getSafeUri().asString());
					pElement.setAttribute("title", m_messages.vibeDataTable_Alt_UnpinEntry());
				}
			});
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
     * @param pinInfo
     * @param event
     * @param valueUpdater
     * 
     * Overrides AbstractCell.onBrowserEvent()
     */
    @Override
    public void onBrowserEvent(Context context, Element parent, EntryPinInfo pinInfo, NativeEvent event, ValueUpdater<EntryPinInfo> valueUpdater) {
    	// Which of our pin widgets is being operated on? 
		Element eventTarget = Element.as(event.getEventTarget());
		String wt = eventTarget.getAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE);
		boolean isImage = ((null != wt) && wt.equals(VibeDataTableConstants.CELL_WIDGET_ENTRY_PIN_IMAGE));

		// What type of event are we processing?
    	String eventType = event.getType();
    	if (VibeDataTableConstants.CELL_EVENT_KEYDOWN.equals(eventType)) {
        	// A key down!  Let AbstractCell handle it.  It will
    		// convert it to an entry key down, ... as necessary.
        	super.onBrowserEvent(context, parent, pinInfo, event, valueUpdater);
    	}

    	else if (VibeDataTableConstants.CELL_EVENT_CLICK.equals(eventType)) {
    		// A click!  Is it the image being clicked?
    		if (isImage) {
    			// Yes!  Strip off any over style.
    			eventTarget.removeClassName("vibe-dataTableLink-hover");
    			toogleEntryPinState(pinInfo, eventTarget);
    		}
    	}
    	
    	else if (isImage && VibeDataTableConstants.CELL_EVENT_MOUSEOVER.equals(eventType)) {
    		// A mouse over!  Add the hover style.
			eventTarget.addClassName("vibe-dataTableLink-hover");
    	}
    	
    	else if (isImage && VibeDataTableConstants.CELL_EVENT_MOUSEOUT.equals(eventType)) {
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
    protected void onEnterKeyDown(Context context, Element parent, EntryPinInfo value, NativeEvent event, ValueUpdater<EntryPinInfo> valueUpdater) {
    	toogleEntryPinState(value, Element.as(event.getEventTarget()));
    }
    
	/**
	 * Called to render an instance of this cell.
	 * 
	 * @param context
	 * @param pinInfo
	 * @param sb
	 * 
	 * Overrides AbstractCell.render()
	 */
	@Override
	public void render(Context context, EntryPinInfo pinInfo, SafeHtmlBuilder sb) {
		// If we weren't given a EntryPinInfo...
		if (null == pinInfo) {
			// ...bail.  Cell widgets can pass null to cells if the
			// ...underlying data contains a null, or if the data
			// ...arrives out of order.
			GwtClientHelper.renderEmptyHtml(sb);
			return;
		}

		// Create the pin image...
		VibeFlowPanel fp = new VibeFlowPanel();
		ImageResource pinImgRes;
		String pinImgAlt;
		if (pinInfo.getPinned()) {
			pinImgRes = m_images.orangePin();
			pinImgAlt = m_messages.vibeDataTable_Alt_UnpinEntry();
		}
		else {
			pinImgRes = m_images.grayPin();
			pinImgAlt = m_messages.vibeDataTable_Alt_PinEntry();
		}
		Image pinImg = new Image(pinImgRes.getSafeUri());
		pinImg.setTitle(pinImgAlt);
		Element piE = pinImg.getElement(); 
		piE.setAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE, VibeDataTableConstants.CELL_WIDGET_ENTRY_PIN_IMAGE);
		piE.setId(VibeDataTableConstants.CELL_WIDGET_ENTRY_PIN_IMAGE + "_" + pinInfo.getEntryId());
		fp.add(pinImg);
		
		// ...and render that into the cell.
		SafeHtml rendered = SafeHtmlUtils.fromTrustedString(fp.getElement().getInnerHTML());
		sb.append(rendered);
	}
}

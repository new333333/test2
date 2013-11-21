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
import org.kablink.teaming.gwt.client.rpc.shared.GetDownloadFileUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
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
			VibeDataTableConstants.CELL_EVENT_CLICK,
			VibeDataTableConstants.CELL_EVENT_KEYDOWN,
			VibeDataTableConstants.CELL_EVENT_MOUSEOVER,
			VibeDataTableConstants.CELL_EVENT_MOUSEOUT);
	}

	/*
	 * Invokes a file download on an entry's file.
	 */
	private void invokeFileDownload(final Long entryId, Element pElement) {
		GetDownloadFileUrlCmd cmd = new GetDownloadFileUrlCmd(null, entryId);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetDownloadFileUrl(),
					String.valueOf(entryId));
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				String downloadFileUrl = ((StringRpcResponseData) response.getResponseData()).getStringValue();
				GwtClientHelper.jsLaunchUrlInWindow(downloadFileUrl, "_blank");
			}
		});
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
		Element et = Element.as(event.getEventTarget());
		String  wt = et.getAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE);
		boolean isLabel = ((null != wt) && wt.equals(VibeDataTableConstants.CELL_WIDGET_ENTRY_DOWNLOAD_LABEL));
		Element es;
		if (isLabel) {
			es = et;
		}
		else {
			Element ep = et.getParentElement();
			wt = ep.getAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE);
			isLabel = ((null != wt) && wt.equals(VibeDataTableConstants.CELL_WIDGET_ENTRY_DOWNLOAD_LABEL));
			es = (isLabel ? ep : null);
		}

		// What type of event are we processing?
    	String eventType = event.getType();
    	if (VibeDataTableConstants.CELL_EVENT_KEYDOWN.equals(eventType)) {
        	// A key down!  Let AbstractCell handle it.  It will
    		// convert it to an entry key down, ... as necessary.
        	super.onBrowserEvent(context, parent, entryId, event, valueUpdater);
    	}

    	else if (VibeDataTableConstants.CELL_EVENT_CLICK.equals(eventType)) {
    		// A click!  Is it the label being clicked?
    		if (isLabel) {
    			// Yes!  Strip off any over style.
    			es.removeClassName("vibe-dataTableLink-hover");
    			invokeFileDownload(entryId, et);
    		}
    	}
    	
    	else if (isLabel && VibeDataTableConstants.CELL_EVENT_MOUSEOVER.equals(eventType)) {
    		// A mouse over!  Add the hover style.
    		es.addClassName("vibe-dataTableLink-hover");
    	}
    	
    	else if (isLabel && VibeDataTableConstants.CELL_EVENT_MOUSEOUT.equals(eventType)) {
    		// A mouse out!  Remove the hover style.
    		es.removeClassName("vibe-dataTableLink-hover");
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
			GwtClientHelper.renderEmptyHtml(sb);
			return;
		}
		
		// Create the download link...
		VibeFlowPanel fp = new VibeFlowPanel();
		Anchor downloadAnchor = new Anchor();
		Image downloadImage = new Image(GwtTeaming.getDataTableImageBundle().moveDown());
		downloadImage.getElement().setAttribute("align", "absmiddle");
		Element elA = downloadAnchor.getElement();
		elA.appendChild(downloadImage.getElement());
		elA.appendChild(new InlineLabel(GwtTeaming.getMessages().vibeDataTable_Download()).getElement());
		downloadAnchor.addStyleName("vibe-dataTableEntry-download");
		Element elE = downloadAnchor.getElement(); 
		elE.setAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE, VibeDataTableConstants.CELL_WIDGET_ENTRY_DOWNLOAD_LABEL);
		elE.setId(VibeDataTableConstants.CELL_WIDGET_ENTRY_DOWNLOAD_LABEL + "_" + entryId);
		fp.add(downloadAnchor);
		
		// ...and render that into the cell.
		SafeHtml rendered = SafeHtmlUtils.fromTrustedString(fp.getElement().getInnerHTML());
		sb.append(rendered);
	}
}

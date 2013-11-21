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
import org.kablink.teaming.gwt.client.rpc.shared.GetViewFileUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.ViewFileInfo;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * Data table cell that represents a file view link.
 * 
 * @author drfoster@novell.com
 */
public class ViewCell extends AbstractCell<ViewFileInfo> {
	/**
	 * Constructor method.
	 */
	public ViewCell() {
		/*
		 * Sink the events we need to process a view link.
	     */
		super(
			VibeDataTableConstants.CELL_EVENT_CLICK,
			VibeDataTableConstants.CELL_EVENT_KEYDOWN,
			VibeDataTableConstants.CELL_EVENT_MOUSEOVER,
			VibeDataTableConstants.CELL_EVENT_MOUSEOUT);
	}

	/*
	 * Invokes a file view on an entry's file.
	 */
	private void invokeFileView(final ViewFileInfo vfi, Element pElement) {
		// Have we store the view file HREF on the Anchor yet?
		final Element viewAE = DOM.getElementById(VibeDataTableConstants.CELL_WIDGET_ENTRY_VIEW_ANCHOR + "_" + vfi.getFileId());
		if (!(GwtClientHelper.hasString(viewAE.getAttribute("href")))) {
			// No!  Build one now...
			GetViewFileUrlCmd cmd = new GetViewFileUrlCmd(vfi);
			GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetViewFileUrl(),
						vfi.getFileId());
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response) {
					// ...store it on the Anchor...
					String viewFileUrl = ((StringRpcResponseData) response.getResponseData()).getStringValue();					
					viewAE.setAttribute("href", viewFileUrl);
					
					// ...and invoke it.
					invokeFileViewUIAsync(viewAE);
				}
			});
		}
		
		else {
			// Yes, we've stored the HREF on the anchor already!
			// Nothing more to do as the native click stuff will have
			// taken care of invoking it.
		}
	}

	/*
	 * Asynchronously runs the file view based on the give file view
	 * Anchor Element.
	 */
	private void invokeFileViewUIAsync(final Element viewAE) {
		ScheduledCommand doFileView = new ScheduledCommand() {
			@Override
			public void execute() {
				invokeFileViewUINow(viewAE);
			}
		};
		Scheduler.get().scheduleDeferred(doFileView);
	}
	
	/*
	 * Synchronously runs the file view based on the give file view
	 * Anchor Element.
	 */
	private void invokeFileViewUINow(Element viewAE) {
		GwtClientHelper.simulateElementClick(viewAE);
	}
	
	/**
     * Called when an event occurs in a rendered instance of this
     * cell.  The parent element refers to the element that contains
     * the rendered cell, NOT to the outermost element that the cell
     * rendered.
     * 
     * @param context
     * @param parent
     * @param fileId
     * @param event
     * @param valueUpdater
     * 
     * Overrides AbstractCell.onBrowserEvent()
     */
    @Override
    public void onBrowserEvent(Context context, Element parent, ViewFileInfo fileId, NativeEvent event, ValueUpdater<ViewFileInfo> valueUpdater) {
    	// Which of our view widgets is being operated on? 
		Element eventTarget = Element.as(event.getEventTarget());
		String wt = eventTarget.getAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE);
		boolean isLabel = ((null != wt) && wt.equals(VibeDataTableConstants.CELL_WIDGET_ENTRY_VIEW_LABEL ));

		// What type of event are we processing?
    	String eventType = event.getType();
    	if (VibeDataTableConstants.CELL_EVENT_KEYDOWN.equals(eventType)) {
        	// A key down!  Let AbstractCell handle it.  It will
    		// convert it to an entry key down, ... as necessary.
        	super.onBrowserEvent(context, parent, fileId, event, valueUpdater);
    	}

    	else if (VibeDataTableConstants.CELL_EVENT_CLICK.equals(eventType)) {
    		// A click!  Is it the label being clicked?
    		if (isLabel) {
    			// Yes!  Strip off any over style.
    			eventTarget.removeClassName("vibe-dataTableLink-hover");
    			invokeFileView(fileId, eventTarget);
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
    protected void onEnterKeyDown(Context context, Element parent, ViewFileInfo value, NativeEvent event, ValueUpdater<ViewFileInfo> valueUpdater) {
    	invokeFileView(value, Element.as(event.getEventTarget()));
    }
    
	/**
	 * Called to render an instance of this cell.
	 * 
	 * @param context
	 * @param fileId
	 * @param sb
	 * 
	 * Overrides AbstractCell.render()
	 */
	@Override
	public void render(Context context, ViewFileInfo vfi, SafeHtmlBuilder sb) {
		// If we weren't given a single fileId...
		String fileId = ((null == vfi) ? null : vfi.getFileId());
		if ((!(GwtClientHelper.hasString(fileId))) || ((-1) != fileId.indexOf(','))) {
			// ...bail.  Cell widgets can pass null to cells if the
			// ...underlying data contains a null, or if the data
			// ...arrives out of order.
			GwtClientHelper.renderEmptyHtml(sb);
			return;
		}

		// Create the view link...
		VibeFlowPanel fp = new VibeFlowPanel();
		Anchor viewA = new Anchor();
		viewA.addStyleName("vibe-dataTableEntry-viewAnchor");
		viewA.setTarget("_blank");
		viewA.setHref(vfi.getViewFileUrl());
		Element viewAE = viewA.getElement();
		viewAE.setAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE, VibeDataTableConstants.CELL_WIDGET_ENTRY_VIEW_ANCHOR);
		viewAE.setId(VibeDataTableConstants.CELL_WIDGET_ENTRY_VIEW_ANCHOR + "_" + fileId);
		
		InlineLabel viewL = new InlineLabel(GwtTeaming.getMessages().vibeDataTable_View());
		viewL.setTitle(GwtTeaming.getMessages().vibeDataTable_Alt_View());
		viewL.addStyleName("vibe-dataTableEntry-viewLabel");
		Element viewLE = viewL.getElement(); 
		viewLE.setAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE, VibeDataTableConstants.CELL_WIDGET_ENTRY_VIEW_LABEL);
		viewLE.setId(VibeDataTableConstants.CELL_WIDGET_ENTRY_VIEW_LABEL + "_" + fileId);
		
		viewAE.appendChild(viewLE);
		fp.add(viewA);
		
		// ...and render that into the cell.
		SafeHtml rendered = SafeHtmlUtils.fromTrustedString(fp.getElement().getInnerHTML());
		sb.append(rendered);
	}
}

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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.binderviews.ModifyLimitedUserVisibilityDlg;
import org.kablink.teaming.gwt.client.binderviews.ModifyLimitedUserVisibilityDlg.ModifyLimitedUserVisibilityDlgClient;
import org.kablink.teaming.gwt.client.binderviews.ModifyLimitedUserVisibilityDlg.ModifyLimitedUserVisibilityCallback;
import org.kablink.teaming.gwt.client.event.SetSelectedPrincipalsLimitedUserVisibilityEvent;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.LimitedUserVisibilityInfo;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * A data table cell that displays the contents for the limited user
 * visibility settings.
 * 
 * @author drfoster@novell.com
 */
public class LimitedUserVisibilityCell extends AbstractCell<LimitedUserVisibilityInfo> implements ModifyLimitedUserVisibilityCallback {
	private GwtTeamingMessages				m_messages;	//
	private ModifyLimitedUserVisibilityDlg	m_mluvDlg;	//
	
	/**
	 * Constructor method.
	 */
	public LimitedUserVisibilityCell() {
		// Sink the events we need to process a limited user visibility
		// setting...
		super(
			VibeDataTableConstants.CELL_EVENT_CLICK,
			VibeDataTableConstants.CELL_EVENT_KEYDOWN,
			VibeDataTableConstants.CELL_EVENT_MOUSEOVER,
			VibeDataTableConstants.CELL_EVENT_MOUSEOUT);
		
		// ...and initialize anything else the needs it.
		m_messages = GwtTeaming.getMessages();
	}

	/*
	 * Asynchronously invokes an edit limited user visibility setting
	 * dialog.
	 */
	private void invokeLimitedUserVisibilityAsync(final LimitedUserVisibilityInfo luvi) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				invokeLimitedUserVisibilityNow(luvi);
			}
		});
	}
	
	/*
	 * Synchronously invokes an edit limited user visibility setting
	 * dialog.
	 */
	private void invokeLimitedUserVisibilityNow(final LimitedUserVisibilityInfo luvi) {
		// Have we created a modify limited user visibility dialog yet?
		final ModifyLimitedUserVisibilityCallback mluvCallback = this;
		if (null == m_mluvDlg) {
			// No!  Create one now...
			ModifyLimitedUserVisibilityDlg.createAsync(new ModifyLimitedUserVisibilityDlgClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in asynchronous
					// provider.
				}
				
				@Override
				public void onSuccess(ModifyLimitedUserVisibilityDlg mluvDlg) {
					// ...and save the dialog...
					m_mluvDlg = mluvDlg;
					GwtClientHelper.deferCommand(new ScheduledCommand() {
						@Override
						public void execute() {
							// ...and run it.
							ModifyLimitedUserVisibilityDlg.initAndShow(
								m_mluvDlg,
								luvi,
								mluvCallback);
						}
					});
				}
			});
		}
		
		else {
			// Yes, we have a modify limited user visibility dialog!
			// Run it.
			ModifyLimitedUserVisibilityDlg.initAndShow(
				m_mluvDlg,
				luvi,
				mluvCallback);
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
     * @param luvi
     * @param event
     * @param valueUpdater
     * 
     * Overrides AbstractCell.onBrowserEvent()
     */
    @Override
    public void onBrowserEvent(Context context, Element parent, LimitedUserVisibilityInfo luvi, NativeEvent event, ValueUpdater<LimitedUserVisibilityInfo> valueUpdater) {
    	// Which of our limited user visibility setting widgets is
    	// being operated on? 
		Element eventTarget = Element.as(event.getEventTarget());
		String wt = eventTarget.getAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE);
		boolean isLabel = ((null != wt) && wt.equals(VibeDataTableConstants.CELL_WIDGET_LIMITED_USER_VISIBILITY_LABEL));

		// What type of event are we processing?
    	String eventType = event.getType();
    	if (VibeDataTableConstants.CELL_EVENT_KEYDOWN.equals(eventType)) {
        	// A key down!  Let AbstractCell handle it.  It will
    		// convert it to a key down, ... as necessary.
        	super.onBrowserEvent(context, parent, luvi, event, valueUpdater);
    	}

    	else if (VibeDataTableConstants.CELL_EVENT_CLICK.equals(eventType)) {
    		// A click!  Is it the label being clicked?
    		if (isLabel) {
    			// Yes!  Strip off any over style.
    			eventTarget.removeClassName("vibe-dataTableLUV-hover");
    			invokeLimitedUserVisibilityAsync(luvi);
    		}
    	}
    	
    	else if (isLabel && VibeDataTableConstants.CELL_EVENT_MOUSEOVER.equals(eventType)) {
    		// A mouse over!  Add the hover style...
			eventTarget.addClassName("vibe-dataTableLUV-hover");
    	}
    	
    	else if (isLabel && VibeDataTableConstants.CELL_EVENT_MOUSEOUT.equals(eventType)) {
    		// A mouse out!  Remove the hover style...
			eventTarget.removeClassName("vibe-dataTableLUV-hover");
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
     * @param luvi
     * @param event
     * @param valueUpdater
     */
    @Override
    protected void onEnterKeyDown(Context context, Element parent, LimitedUserVisibilityInfo luvi, NativeEvent event, ValueUpdater<LimitedUserVisibilityInfo> valueUpdater) {
    	invokeLimitedUserVisibilityAsync(luvi);
    }

    /**
     * Called when the user presses OK in the modify limited user
     * visibility dialog.
     * 
     * Implements the ModifyLimitedUserVisibilityCallback.onEditSuccessful() method.
     * 
     * @param luvEid,
     * @param luvInfo
     * 
     * @return
     */
    @Override
	public boolean onEditSuccessful(EntityId luvEid, LimitedUserVisibilityInfo luvInfo) {
    	// Put the new limited user visibility settings into affect...
    	Boolean limited;
    	Boolean override;
    	if      (luvInfo.isOverride()) {limited = Boolean.FALSE; override = Boolean.TRUE; }	// Override only.
    	else if (luvInfo.isLimited())  {limited = Boolean.TRUE;  override = Boolean.FALSE;}	// Limited  only.
    	else                           {limited =                override = Boolean.FALSE;}	// Remove both.
    	GwtTeaming.fireEventAsync(
    		new SetSelectedPrincipalsLimitedUserVisibilityEvent(
        		luvEid,
        		limited,
        		override,
        		false));	// false -> Use the entity provide.
    	
    	// ...and return true to allow the dialog to be closed.
		return true;
	}
	
    /**
     * Called when the user presses Cancel in the modify limited user
     * visibility dialog.
     * 
     * Implements the ModifyLimitedUserVisibilityCallback.onEditCanceled() method.
     */
    @Override
	public void onEditCanceled() {
		// Nothing to do.
	}
	
	/**
	 * Called to render an instance of this cell.
	 * 
	 * @param context
	 * @param luvi
	 * @param sb
	 * 
	 * Overrides AbstractCell.render()
	 */
	@Override
	public void render(Context context, LimitedUserVisibilityInfo luvi, SafeHtmlBuilder sb) {
		// If we weren't given a LimitedUserVisibilityInfo...
		if (null == luvi) {
			// ...bail.  Cell widgets can pass null to cells if the
			// ...underlying data contains a null, or if the data
			// ...arrives out of order.
			GwtClientHelper.renderEmptyHtml(sb);
			return;
		}

		// Add the user visibility information link...
		VibeFlowPanel fp = new VibeFlowPanel();
		InlineLabel luvLabel = new InlineLabel(luvi.getDisplay());
		luvLabel.addStyleName("vibe-dataTableLUV-title");
		luvLabel.setTitle(m_messages.vibeDataTable_LimitedUserVisibility_Alt());
		Element luvE = luvLabel.getElement(); 
		String widgetAttr = VibeDataTableConstants.CELL_WIDGET_LIMITED_USER_VISIBILITY_LABEL;
		luvE.setAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE, widgetAttr);
		fp.add(luvLabel);
		
		// ...and render that into the cell.
		SafeHtml rendered = SafeHtmlUtils.fromTrustedString(fp.getElement().getInnerHTML());
		sb.append(rendered);
	}
}

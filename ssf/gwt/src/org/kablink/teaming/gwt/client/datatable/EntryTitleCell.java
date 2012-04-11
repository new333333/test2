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
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtTeamingWorkspaceTreeImageBundle;
import org.kablink.teaming.gwt.client.rpc.shared.GetViewFolderEntryUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SetSeenCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.EntryTitleInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TeamingPopupPanel;

/**
 * Data table cell that represents an entry's title.
 * 
 * @author drfoster@novell.com
 */
public class EntryTitleCell extends AbstractCell<EntryTitleInfo> {
	private InlineLabel			m_titleHintLabel;	//
	private TeamingPopupPanel	m_titleHintPanel;	//
	
	/**
	 * Constructor method.
	 */
	public EntryTitleCell() {
		/*
		 * Sink the events we need to process an entry title.
	     */
		super(
			VibeDataTableConstants.CELL_EVENT_CLICK,
			VibeDataTableConstants.CELL_EVENT_KEYDOWN,
			VibeDataTableConstants.CELL_EVENT_MOUSEOVER,
			VibeDataTableConstants.CELL_EVENT_MOUSEOUT);
	}

	/*
	 * Invokes an entry viewer on the entry.
	 */
	private void invokeViewEntry(final EntryTitleInfo eti, Element pElement) {
		GetViewFolderEntryUrlCmd cmd = new GetViewFolderEntryUrlCmd(null, eti.getEntryId());
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetViewFolderEntryUrl(),
					String.valueOf(eti.getEntryId()));
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				String viewFolderEntryUrl = ((StringRpcResponseData) response.getResponseData()).getStringValue();
				GwtClientHelper.jsShowForumEntry(viewFolderEntryUrl);
				markEntryUISeenAsync(eti);
			}
		});
	}
	
	/*
	 * Marks the entry as having been seen.
	 */
	private void markEntrySeen(final EntryTitleInfo eti, final Element pElement) {
		final Long entryId = eti.getEntryId();
		SetSeenCmd cmd = new SetSeenCmd(entryId);
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					GwtTeaming.getMessages().rpcFailure_SetSeen(),
					String.valueOf(entryId));
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				markEntryUISeenAsync(eti);
			}
		});
	}

	/*
	 * Asynchronously marks the UI components to show that the entry
	 * has been seen.
	 */
	private void markEntryUISeenAsync(final EntryTitleInfo eti) {
		ScheduledCommand doMarkEntrySeen = new ScheduledCommand() {
			@Override
			public void execute() {
				markEntryUISeenNow(eti);
			}
		};
		Scheduler.get().scheduleDeferred(doMarkEntrySeen);
	}
	
	/*
	 * Synchronously marks the UI components to show that the entry
	 * has been seen.
	 */
	private void markEntryUISeenNow(final EntryTitleInfo eti) {
		// Hide the marker to set the entry unseen...
		String entryIdS = String.valueOf(eti.getEntryId());
		Element e = DOM.getElementById(VibeDataTableConstants.CELL_WIDGET_ENTRY_UNSEEN_IMAGE + "_" + entryIdS);
		if (null != e) {
			e.getStyle().setDisplay(Display.NONE);
		}

		// ...and take the bold off the title.
		e = DOM.getElementById(VibeDataTableConstants.CELL_WIDGET_ENTRY_TITLE_LABEL + "_" + entryIdS);
		if (null != e) {
			e.removeClassName("bold");
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
     * @param eti
     * @param event
     * @param valueUpdater
     * 
     * Overrides AbstractCell.onBrowserEvent()
     */
    @Override
    public void onBrowserEvent(Context context, Element parent, EntryTitleInfo eti, NativeEvent event, ValueUpdater<EntryTitleInfo> valueUpdater) {
    	// Which of our entry title widgets is being operated on? 
		Element eventTarget = Element.as(event.getEventTarget());
		String wt = eventTarget.getAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE);
		boolean isLabel     = ((null != wt) && wt.equals(VibeDataTableConstants.CELL_WIDGET_ENTRY_TITLE_LABEL ));
		boolean isUnseenImg = ((null != wt) && wt.equals(VibeDataTableConstants.CELL_WIDGET_ENTRY_UNSEEN_IMAGE));

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
    			eventTarget.removeClassName("vibe-dataTableLink-hover");
    			invokeViewEntry(eti, eventTarget);
    		}
    		
    		else if (isUnseenImg) {
    			markEntrySeen(eti, eventTarget);
    		}
    	}
    	
    	else if (isLabel && VibeDataTableConstants.CELL_EVENT_MOUSEOVER.equals(eventType)) {
    		// A mouse over!  Add the hover style...
			eventTarget.addClassName("vibe-dataTableLink-hover");
			
			// ...if have a description...
			String description = eti.getDescription();
			if (GwtClientHelper.hasString(description)) {
				// ...and we haven't create a popup panel for the hover
				// ...HTML yet...
				if (null == m_titleHintPanel) {
					// ...create it now...
					m_titleHintPanel = new TeamingPopupPanel(false, false);
					m_titleHintPanel.removeStyleName("gwt-PopupPanel");
					m_titleHintPanel.addStyleName("vibe-dataTableLink-hoverHint");
					m_titleHintLabel = new InlineLabel();
					m_titleHintLabel.removeStyleName("gwt-InlineLabel");
					m_titleHintLabel.addStyleName("vibe-dataTableLink-hoverHintLabel");
					m_titleHintPanel.setWidget(m_titleHintLabel);
				}
				
				// ...and show it with the description HTML.
				m_titleHintLabel.getElement().setInnerHTML(description);
				m_titleHintPanel.setPopupPosition((eventTarget.getAbsoluteLeft() + 0), (eventTarget.getAbsoluteBottom() + 8));	// 20,12:  Same as JSP way of showing these hints.
				m_titleHintPanel.show();
			}
    	}
    	
    	else if (isLabel && VibeDataTableConstants.CELL_EVENT_MOUSEOUT.equals(eventType)) {
    		// A mouse out!  Remove the hover style...
			eventTarget.removeClassName("vibe-dataTableLink-hover");
			
			// ...and if there's a title hint panel...
			if (null != m_titleHintPanel) {
				// ...make sure it's hidden.
				m_titleHintPanel.hide();
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
     */
    @Override
    protected void onEnterKeyDown(Context context, Element parent, EntryTitleInfo value, NativeEvent event, ValueUpdater<EntryTitleInfo> valueUpdater) {
    	invokeViewEntry(value, Element.as(event.getEventTarget()));
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

		// If we're dealing with an item in the trash...
		String entryIdS = String.valueOf(eti.getEntryId());
		VibeFlowPanel fp = new VibeFlowPanel();
		boolean entryUnseen = (!(eti.getSeen()));
		boolean isTrash     = eti.getTrash();
		String entityType   = eti.getEntityType();
		if (isTrash) {
			// ...and we know what type of item it is...
			if (null != entityType) {
				// ...we need to display an image next to it...
				GwtTeamingMessages                 messages = GwtTeaming.getMessages(); 
				GwtTeamingWorkspaceTreeImageBundle images   = GwtTeaming.getWorkspaceTreeImageBundle();
				ImageResource                      entityImage;
				String                             entityAlt;
				if      (entityType.equals("folderEntry")) {entityImage = images.folder_entry();     entityAlt = messages.treeAltEntry();    }
				else if (entityType.equals("folder"))      {entityImage = images.folder();           entityAlt = messages.treeAltFolder();   }
				else if (entityType.equals("workspace"))   {entityImage = images.folder_workspace(); entityAlt = messages.treeAltWorkspace();}
				else                                       {entityImage = null;                      entityAlt = null;                       }
				if (null != entityImage) {
					Image i = GwtClientHelper.buildImage(entityImage, entityAlt);
					i.addStyleName("vibe-dataTableEntity-Marker");
					fp.add(i);
				}
			}
		}
		
		// ...otherwise, if the entry has not been seen...
		else if (entryUnseen) {
			// ...add a widget so the user can mark it so.  (Note that
			// ...we don't mess with the unread bubble when viewing the
			// ...trash)...
			Image i = GwtClientHelper.buildImage(GwtTeaming.getDataTableImageBundle().unread(), GwtTeaming.getMessages().vibeDataTable_Alt_Unread());
			i.addStyleName("vibe-dataTableEntry-unseenMarker");
			Element iE = i.getElement();
			iE.setAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE, VibeDataTableConstants.CELL_WIDGET_ENTRY_UNSEEN_IMAGE);
			iE.setId(VibeDataTableConstants.CELL_WIDGET_ENTRY_UNSEEN_IMAGE + "_" + entryIdS);
			fp.add(i);
		}

		// ...add the title link...
		boolean titleIsLink = ((!isTrash) || ((null != entityType) && entityType.equals("folderEntry")));
		InlineLabel titleLabel = new InlineLabel(eti.getTitle());
		titleLabel.addStyleName(titleIsLink ? "vibe-dataTableEntry-title" : "vibe-dataTableEntry-titleNoLink");
		if ((!isTrash) && entryUnseen) {
			titleLabel.addStyleName("bold");
		}
		Element elE = titleLabel.getElement(); 
		String widgetAttr = (titleIsLink ? VibeDataTableConstants.CELL_WIDGET_ENTRY_TITLE_LABEL : VibeDataTableConstants.CELL_WIDGET_ENTRY_TITLE_LABEL_NOLINK);
		elE.setAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE, widgetAttr);
		elE.setId(VibeDataTableConstants.CELL_WIDGET_ENTRY_TITLE_LABEL + "_" + entryIdS);
		fp.add(titleLabel);
		
		// ...and render that into the cell.
		SafeHtml rendered = SafeHtmlUtils.fromTrustedString(fp.getElement().getInnerHTML());
		sb.append(rendered);
	}
}

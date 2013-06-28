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
import org.kablink.teaming.gwt.client.GwtTeamingWorkspaceTreeImageBundle;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.rpc.shared.GetBinderPermalinkCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetViewFolderEntryUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SetSeenCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.EntryTitleInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.widgets.HoverHintPopup;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Data table cell that represents an entry's title.
 * 
 * @author drfoster@novell.com
 */
public class EntryTitleCell extends AbstractCell<EntryTitleInfo> {
	private HoverHintPopup	m_hoverHintPopup;	//
	
	/**
	 * Constructor method.
	 */
	public EntryTitleCell() {
		// Sink the events we need to process an entry title.
		super(
			VibeDataTableConstants.CELL_EVENT_CLICK,
			VibeDataTableConstants.CELL_EVENT_KEYDOWN,
			VibeDataTableConstants.CELL_EVENT_MOUSEOVER,
			VibeDataTableConstants.CELL_EVENT_MOUSEOUT);
	}

	/*
	 * Returns the string to use for an entry image.
	 */
	private String getEntryImage(EntryTitleInfo eti, GwtTeamingWorkspaceTreeImageBundle images) {
		// Is the entry a file entry? 
		if (eti.isFile()) {
			// Yes!  Do we have an icon for that file?
			String fileIcon = eti.getFileIcon();
			if (GwtClientHelper.hasString(fileIcon)) {
				// Yes!  Return the full URL to it.
				return (GwtClientHelper.getRequestInfo().getImagesPath() + fileIcon);
			}
		}
		
		// The entry is either not a file or we don't have an icon for
		// it.  Return the generic entry icon.
		return images.folder_entry().getSafeUri().asString();
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
	
	/*
	 * Invokes an entry viewer on the entry.
	 */
	private void invokeViewEntry(final EntryTitleInfo eti, Element pElement) {
		// Is this entry a folder?
		if (eti.getEntityId().isBinder()) {
			// Yes!  Can we get a permalink to it?
			final String folderId = String.valueOf(eti.getEntityId().getEntityId());
			GetBinderPermalinkCmd cmd = new GetBinderPermalinkCmd(folderId);
			GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					// No!  Tell the user about the problem.
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetBinderPermalink(),
						folderId);
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response) {
					// Yes, we have a permalink to the folder!  Change
					// contexts to it.
					StringRpcResponseData responseData = ((StringRpcResponseData) response.getResponseData());
					String binderPermalink = responseData.getStringValue();
					OnSelectBinderInfo osbInfo = new OnSelectBinderInfo(binderPermalink, Instigator.GOTO_CONTENT_URL);
					if (GwtClientHelper.validateOSBI(osbInfo)) {
						GwtTeaming.fireEvent(new ChangeContextEvent(osbInfo));
					}
				}
			});
		}
		
		else {
			// No this entry isn't a folder!  It must be an entry.
			GetViewFolderEntryUrlCmd cmd = new GetViewFolderEntryUrlCmd(null, eti.getEntityId().getEntityId());
			GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetViewFolderEntryUrl(),
						String.valueOf(eti.getEntityId().getEntityId()));
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response) {
					String viewFolderEntryUrl = ((StringRpcResponseData) response.getResponseData()).getStringValue();
					GwtClientHelper.jsShowForumEntry(viewFolderEntryUrl);
					markEntryUISeenAsync(eti);
				}
			});
		}
	}
	
	/*
	 * Marks the entry as having been seen.
	 */
	private void markEntrySeen(final EntryTitleInfo eti, final Element pElement) {
		final Long entryId = eti.getEntityId().getEntityId();
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
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				markEntryUISeenNow(eti);
			}
		});
	}
	
	/*
	 * Synchronously marks the UI components to show that the entry
	 * has been seen.
	 */
	private void markEntryUISeenNow(final EntryTitleInfo eti) {
		// Hide the marker to set the entry unseen...
		String entryIdS = String.valueOf(eti.getEntityId().getEntityId());
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
		if (!(GwtClientHelper.hasString(wt))) {
			eventTarget = eventTarget.getParentElement();
			wt = eventTarget.getAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE);
		}
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
    			hoverStyleRemove(eti, eventTarget);
    			if (!(eti.isFile())) {
    				invokeViewEntry(eti, eventTarget);
    			}
    		}
    		
    		else if (isUnseenImg) {
    			markEntrySeen(eti, eventTarget);
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
			
			// ...and if there's a title hint panel...
			if (null != m_hoverHintPopup) {
				// ...make sure it's hidden.
				m_hoverHintPopup.hide();
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
    protected void onEnterKeyDown(Context context, Element parent, EntryTitleInfo eti, NativeEvent event, ValueUpdater<EntryTitleInfo> valueUpdater) {
    	Element eventTarget = Element.as(event.getEventTarget());
    	if (!(eti.isFile())) {
    		invokeViewEntry(eti, eventTarget);
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

		// Initialize the variables required to render the title cell.
		Image			binderImg    = ((Image) eti.getClientItemImage());
		String			entryIdS     = String.valueOf(eti.getEntityId().getEntityId());
		String			entityType   = eti.getEntityId().getEntityType();
		boolean			entryUnseen  = (!(eti.isSeen()));
		boolean			hasBinderImg = (null != binderImg);
		boolean			isHidden     = eti.isHidden();
		boolean			isTrash      = eti.isTrash();
		boolean			isEntry      = entityType.equals("folderEntry");
		boolean			titleIsLink  = ((!isTrash) || ((null != entityType) && entityType.equals("folderEntry")));
		VibeFlowPanel	html         = new VibeFlowPanel();
		
		// We don't word wrap the title of files or folders.
		VibeFlowPanel etContainerWidget = new VibeFlowPanel();
		etContainerWidget.addStyleName("vibe-dataTableEntry-panel");
		html.add(etContainerWidget );
		if (eti.isFile() || eti.getEntityId().isBinder()) {
			etContainerWidget.addStyleName("gwtUI_nowrap");
		}
		
		// If we're dealing with an item in the trash...
		Image titleImg = null;
		if (isTrash) {
			// ...and we know what type of item it is...
			if (null != entityType) {
				// ...we need to display an image next to it...
				GwtTeamingMessages                 messages = GwtTeaming.getMessages(); 
				GwtTeamingWorkspaceTreeImageBundle images   = GwtTeaming.getWorkspaceTreeImageBundle();
				String                             entityImage;
				String                             entityAlt;
				if      (isEntry)                        {entityImage = getEntryImage(eti, images);                        entityAlt = messages.treeAltEntry();    }
				else if (entityType.equals("folder"))    {entityImage = images.folder().getSafeUri().asString();           entityAlt = messages.treeAltFolder();   }
				else if (entityType.equals("workspace")) {entityImage = images.folder_workspace().getSafeUri().asString(); entityAlt = messages.treeAltWorkspace();}
				else                                     {entityImage = null;                                              entityAlt = null;                       }
				if (hasBinderImg || (null != entityImage)) {
					Image i = (hasBinderImg ? binderImg : GwtClientHelper.buildImage(entityImage));
					i.setTitle(entityAlt);
					i.addStyleName("vibe-dataTableEntity-Marker");
					titleImg = i;
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
			etContainerWidget.add(i);
		}
		
		// Do we have a client image for this item?
		if ((!isTrash) && hasBinderImg) {
			// Yes!  Add it to the flow panel.
			binderImg.addStyleName("vibe-dataTableItem-Img");
			titleImg = binderImg;
		}
		
		// ...add the title link...
		Widget  titleWidget;
		Element titleElement;
		if (null != titleImg) {
			if (titleIsLink && eti.isFile()) {
				titleImg.getElement().setAttribute("border", "0");
				Anchor a = new Anchor();
				a.setHref(eti.getFileDownloadUrl());
				a.setTarget("_blank");
				titleWidget = a;
			}
			else {
				titleWidget = new VibeFlowPanel();
			}
			titleWidget.setStyleName("vibe-dataTableEntry-titleLinkPanel");
			titleElement = titleWidget.getElement();
			titleElement.appendChild(titleImg.getElement());
		}
		else {
			titleWidget  = null;
			titleElement = null;
		}
		InlineLabel titleLabel = new InlineLabel(eti.getTitle());
		String titleStyles;
		if (isHidden)
		     titleStyles = (titleIsLink ? "vibe-dataTableEntry-titleHidden" : "vibe-dataTableEntry-titleNoLinkHidden");
		else titleStyles = (titleIsLink ? "vibe-dataTableEntry-title"       : "vibe-dataTableEntry-titleNoLink"      );
		if ((!isTrash) && entryUnseen) {
			titleStyles += " bold";
		}
		titleLabel.addStyleName(titleStyles);
		Element elE = titleLabel.getElement(); 
		String widgetAttr = (titleIsLink ? VibeDataTableConstants.CELL_WIDGET_ENTRY_TITLE_LABEL : VibeDataTableConstants.CELL_WIDGET_ENTRY_TITLE_LABEL_NOLINK);
		elE.setAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE, widgetAttr);
		elE.setId(VibeDataTableConstants.CELL_WIDGET_ENTRY_TITLE_LABEL + "_" + entryIdS);
		if (null == titleElement) {
			etContainerWidget.add(titleLabel);
		}
		else {
			titleElement.appendChild(titleLabel.getElement());
			etContainerWidget.add(titleWidget);
		}
		
		// ...and render that into the cell.
		SafeHtml rendered = SafeHtmlUtils.fromTrustedString(html.getElement().getInnerHTML());
		sb.append(rendered);
	}
}

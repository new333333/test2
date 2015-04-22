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

import org.kablink.teaming.gwt.client.datatable.ManageCommentsDlg;
import org.kablink.teaming.gwt.client.datatable.ManageCommentsDlg.ManageCommentsDlgClient;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.CommentAddedCallback;
import org.kablink.teaming.gwt.client.util.CommentsInfo;
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

/**
 * Data table cell that represents a comments count on an entity.
 * 
 * @author drfoster@novell.com
 */
public class CommentsCell extends AbstractCell<CommentsInfo> implements CommentAddedCallback {
	private GwtTeamingMessages	m_messages;				// Access to the Vibe string resources we need for this cell.
	private ManageCommentsDlg	m_manageCommentsDlg;	//
	
	/**
	 * Constructor method.
	 */
	public CommentsCell() {
		// Sink the events we need to process an action menu...
		super(
			VibeDataTableConstants.CELL_EVENT_CLICK,
			VibeDataTableConstants.CELL_EVENT_KEYDOWN);
		
		// ...and initialize everything else.
		m_messages = GwtTeaming.getMessages();
	}

	/**
	 * Called by the manage comments dialog saying a new comment was
	 * added to the item.
	 * 
	 * Implements the CommentAddedCallback.commentAdded() method.
	 * 
	 * @param callbackData
	 */
	@Override
	public void commentAdded(Object callbackData) {
		// Increment the count of comments in the CommentsInfo...
		CommentsInfo ci = ((CommentsInfo) callbackData);
		int commentCount = (ci.getCommentsCount() + 1);
		ci.setCommentsCount(commentCount);

		// ...and update the display of the comment bubble.
		Element cpE = DOM.getElementById(ci.getEntityId().getEntityIdString());
		cpE.removeClassName("vibe-dataTableComments-panel0");
		cpE.setInnerHTML(String.valueOf(commentCount));
	}
	
	/**
     * Called when an event occurs in a rendered instance of this
     * cell.  The parent element refers to the element that contains
     * the rendered cell, NOT to the outermost element that the cell
     * rendered.
     * 
     * @param context
     * @param parent
     * @param commentsInfo
     * @param event
     * @param valueUpdater
     * 
     * Overrides AbstractCell.onBrowserEvent()
     */
	@Override
    public void onBrowserEvent(Context context, Element parent, CommentsInfo commentsInfo, NativeEvent event, ValueUpdater<CommentsInfo> valueUpdater) {
		// What type of event are we processing?
    	String eventType = event.getType();
    	if (VibeDataTableConstants.CELL_EVENT_KEYDOWN.equals(eventType)) {
        	// A key down!  Let AbstractCell handle it.  It will
    		// convert it to an entry key down, ... as necessary.
        	super.onBrowserEvent(context, parent, commentsInfo, event, valueUpdater);
    	}
    	
    	else {
    		// Something other than a key down!  Is it targeted to this
    		// comments cell?
    		Element	eventTarget  = Element.as(event.getEventTarget()                                    );
    		String	wt           = eventTarget.getAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE);
    		if ((null != wt) && wt.equals(VibeDataTableConstants.CELL_WIDGET_ENTRY_COMMENTS_PANEL)) {
    			// Yes!  What type of event are we processing?
		    	if (VibeDataTableConstants.CELL_EVENT_CLICK.equals(eventType)) {
		    		// A click!  Run the manage comments dialog on the
		    		// entity.
					showManageCommentsDlg(commentsInfo, eventTarget);
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
    protected void onEnterKeyDown(Context context, Element parent, CommentsInfo commentsInfo, NativeEvent event, ValueUpdater<CommentsInfo> valueUpdater) {
    	// If the key down is targeted to the comments panel...
    	Element eventTarget = Element.as(event.getEventTarget());
		String	wt           = eventTarget.getAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE);
		if ((null != wt) && wt.equals(VibeDataTableConstants.CELL_WIDGET_ENTRY_COMMENTS_PANEL)){
			// ...run the manage comments dialog on the entity.
			showManageCommentsDlg(commentsInfo, eventTarget);
		}
    }
    
	/**
	 * Called to render an instance of this cell.
	 * 
	 * @param context
	 * @param commentsInfo
	 * @param sb
	 * 
	 * Overrides AbstractCell.render()
	 */
	@Override
	public void render(Context context, CommentsInfo commentsInfo, SafeHtmlBuilder sb) {
		// If we weren't given a CommentsInfo or it's for binder or
		// comments are disabled on the entity...
		if ((null == commentsInfo) || commentsInfo.getEntityId().isBinder() || commentsInfo.isCommentsDisabled()) {
			// ...bail.  Cell widgets can pass null to cells if the
			// ...underlying data contains a null, or if the data
			// ...arrives out of order.
			GwtClientHelper.renderEmptyHtml(sb);
			return;
		}

		// Are we processing the comments on a folder entry?
		VibeFlowPanel html = new VibeFlowPanel();
		if (commentsInfo.getEntityId().isEntry()) {
			// Yes!  Generate the number of comments panel...
			VibeFlowPanel commentPanel = new VibeFlowPanel();
			commentPanel.addStyleName("vibe-dataTableComments-panel");
			VibeFlowPanel commentBubble = new VibeFlowPanel();
			commentBubble.addStyleName("vibe-dataTableComments-bubble");
			int commentCount = commentsInfo.getCommentsCount();
			String addedBubbleStyle;
			if (1000 <= commentCount)
			     addedBubbleStyle = "vibe-dataTableComments-bubbleBig";
			else addedBubbleStyle = "vibe-dataTableComments-bubbleSmall";
			commentBubble.addStyleName(addedBubbleStyle);
			Element cpE = commentBubble.getElement();
			commentBubble.addStyleName("cursorPointer"                        );
			commentBubble.setTitle(    m_messages.vibeDataTable_Alt_Comments());
			cpE.setId(commentsInfo.getEntityId().getEntityIdString());
			cpE.setAttribute(
				VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE,
				VibeDataTableConstants.CELL_WIDGET_ENTRY_COMMENTS_PANEL);
			
			String comments;
			if (0 == commentCount) {
				cpE.addClassName("vibe-dataTableComments-panel0");
				comments = "&nbsp;&nbsp;";
			}
			else {
				comments = String.valueOf(commentCount);
			}
			cpE.setInnerHTML(comments);
			
			// ...and render it as the cell's HTML.
			commentPanel.add(commentBubble);
			html.add(commentPanel);
		}
		
		// ...and render that into the cell.
		SafeHtml rendered = SafeHtmlUtils.fromTrustedString(html.getElement().getInnerHTML());
		sb.append(rendered);
	}

	/*
	 * Runs the manage comments dialog against the given entity. 
	 */
	private void showManageCommentsDlg(final CommentsInfo commentsInfo, final Element relativeToThis) {
		// Have we instantiated a manage comments dialog yet?
		if (null == m_manageCommentsDlg) {
			// No!  Instantiate one now.
			ManageCommentsDlg.createAsync(new ManageCommentsDlgClient() {			
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(final ManageCommentsDlg mcDlg) {
					// ...and show it.
					m_manageCommentsDlg = mcDlg;
					showManageCommentsDlgAsync(commentsInfo, relativeToThis);
				}
			});
		}
		
		else {
			// Yes, we've instantiated a manage comments dialog
			// already!  Simply show it.
			showManageCommentsDlgAsync(commentsInfo, relativeToThis);
		}
	}
	
	/*
	 * Asynchronously shows the manage comments dialog.
	 */
	private void showManageCommentsDlgAsync(final CommentsInfo commentsInfo, final Element relativeToThis) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				showManageCommentsDlgNow(commentsInfo, relativeToThis);
			}
		});
	}
	
	/*
	 * Synchronously shows the manage comments dialog.
	 */
	private void showManageCommentsDlgNow(final CommentsInfo commentsInfo, final Element relativeToThis) {
		ManageCommentsDlg.initAndShow(
			m_manageCommentsDlg,
			commentsInfo,
			null,	// null -> Center the dialog.
			this);	// Provides a CommentAddedCallback.
	}
}

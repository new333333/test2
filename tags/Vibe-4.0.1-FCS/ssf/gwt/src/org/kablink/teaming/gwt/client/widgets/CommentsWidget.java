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
package org.kablink.teaming.gwt.client.widgets;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.rpc.shared.ActivityStreamEntryListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetEntryCommentsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.ActivityStreamEntry;
import org.kablink.teaming.gwt.client.util.CommentAddedCallback;
import org.kablink.teaming.gwt.client.util.CommentsInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.whatsnew.ActionsPopupMenu;
import org.kablink.teaming.gwt.client.whatsnew.ActionsPopupMenu.ActionMenuItem;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamComment;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCommentsContainer;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl.ActivityStreamCtrlClient;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl.ActivityStreamCtrlUsage;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl.DescViewFormat;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;

/**
 * This widget is used to display the comments for a given entity.
 * 
 * @author drfoster@novell.com
 */
public class CommentsWidget extends Composite implements ActivityStreamCommentsContainer, CommentAddedCallback {
	private ActivityStreamCtrl		m_activityStreamCtrl;	//
	private boolean					m_acsPseudoAttached;	// Set true once m_activityStreamCtrl is used and pseudo attached to the DOM.
	private boolean					m_showTitle;			//
	private CommentAddedCallback	m_commentAddedCallback;	//
	private CommentsInfo			m_commentsInfo;			//
	private VibeFlowPanel			m_mainPanel;			//
	
	private static final BigDecimal	DELTA				= BigDecimal.valueOf(1, 1);
	private static final boolean	DUMP_DOC_NUMBERS	= false;	// DRF (20140423):  Debug only, leave false on checkin. 
	

	/**
	 * Constructor method.
	 * 
	 * @param showTitle
	 */
	public CommentsWidget(boolean showTitle) {
		// Initialize the super class.
		super();
		
		// Store the parameter.
		m_showTitle = showTitle;
		
		// Create a panel for the widget...
		m_mainPanel = new VibeFlowPanel();
		m_mainPanel.addStyleName("commentsWidget_commentsPanel");

		// ...and use that panel to initialize the composite.
		initWidget(m_mainPanel);
	}

	/**
	 * Add the given comment to our list of comments.
	 * 
	 * @param activityStreamEntry
	 * @param append
	 * @param scrollIntoView
	 */
	public void addComment(ActivityStreamEntry activityStreamEntry, boolean scrollIntoView) {
		ActivityStreamComment commentUI = new ActivityStreamComment(
			m_activityStreamCtrl,
			this,
			DescViewFormat.FULL,
			m_showTitle);
		commentUI.addStyleName("commentsWidget_commentStylesOverride");
		commentUI.setData(activityStreamEntry);
		m_mainPanel.add(commentUI);

		// If the activity stream control hasn't been pseudo attached
		// to the DOM yet...
		if (!m_acsPseudoAttached) {
			// ...tell it to attach.
			m_activityStreamCtrl.onAttach();
			m_acsPseudoAttached = true;
		}
		
		if (scrollIntoView) {
			showNewComment(commentUI);
		}
	}
	
	/*
	 * Add the list of comments to out list of comments.
	 */
	private void addComments(List<ActivityStreamEntry> listOfComments) {
		if (null != listOfComments) {
			// Get the base doc number
			String baseDocNum = (GwtClientHelper.hasItems(listOfComments) ? getBaseDocNum(listOfComments.get(0)) : null);
			if (null != baseDocNum) {
				// Get a list of all the top-level comments.
				ArrayList<ActivityStreamEntry> topLevelComments = getListOfChildComments(baseDocNum, listOfComments);
				
				// For each top-level comment get all of it's children.
				getChildComments(topLevelComments, listOfComments);
				for (ActivityStreamEntry nextComment:  topLevelComments) {
					// Add this comment to our UI.
					addComment(nextComment, false);
				}
			}
		}
	}
	
	/**
	 * This method gets called when a reply is added to one of our sub
	 * comments.
	 * 
	 * @param callbackData
	 */
	@Override
	public void commentAdded(Object callbackData) {
		if (null != m_commentAddedCallback) {
			m_commentAddedCallback.commentAdded(m_commentsInfo);
		}
	}

	/*
	 * If enabled, dumps document numbers as they're analyzed via an
	 * alert.
	 */
	private void dumpDocNumbers(String start, String baseDocNum, String docNum) {
		if (DUMP_DOC_NUMBERS) {
			if (null == start)      start      = "";
			if (null == baseDocNum) baseDocNum = "";
			if (null == docNum)     docNum     = "";
			
			GwtClientHelper.deferredAlert(start + ": baseDocNum: " + baseDocNum + ", docNum: " + docNum);
		}
	}
	
	/*
	 * Return the base doc number.
	 * 
	 * Examples:
	 * 1) If the given ActivityStreamEntry has a doc number of '4.x',   it returns '4'.
	 * 2) If the given ActivityStreamEntry has a doc number of '4.x.y', it returns '4.x'/
	 */
	private String getBaseDocNum(ActivityStreamEntry activityStreamEntry) {
		String baseDocNum = null;
		if ((null != activityStreamEntry) && (null != activityStreamEntry.getEntryDocNum())) {
			String entryDocNum = activityStreamEntry.getEntryDocNum();
			int index = entryDocNum.lastIndexOf('.');
			if (0 < index) {
				baseDocNum = entryDocNum.substring(0, index);
			}
		}
		return baseDocNum;
	}
	
	/*
	 * Issue GWT RPC request to get the comments for the given entity.
	 */
	private void getCommentsFromServer() {
		if (null != m_commentsInfo) {
			// Issue a request to get all the comments for this entry.
			GetEntryCommentsCmd cmd = new GetEntryCommentsCmd(getEntityId());
			GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetEntryComments(),
						m_commentsInfo.getEntityTitle());
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response) {
					// Clear all data from the existing ui comment objects
					if (null != m_mainPanel) {
						m_mainPanel.clear();
					}
					
					// Get the list of comments from the response.
					ActivityStreamEntryListRpcResponseData responseData = ((ActivityStreamEntryListRpcResponseData) response.getResponseData());
					final List<ActivityStreamEntry> listOfComments = responseData.getActivityStreamEntryList();
					GwtClientHelper.deferCommand(new ScheduledCommand() {
						@Override
						public void execute() {
							// Do we have a list of comments?
							if (null != listOfComments) {
								// Add the comments to the widget.
								addComments(listOfComments);
							}
						}
					});
				}
			});
		}
	}
	
	/*
	 */
	private String getEntityId() {
		String entityIdS = "";
		if (null != m_commentsInfo) {
			EntityId entityId = m_commentsInfo.getEntityId();
			if (null != entityId) {
				if (entityId.getEntityType().equalsIgnoreCase(EntityId.FOLDER_ENTRY))
				     entityIdS = entityId.getEntityId().toString();
				else entityIdS = entityId.getBinderId().toString();
			}
		}
		return entityIdS;
	}
	
	/*
	 * Return a list of all the child sub comments for the given doc
	 * number.  For example, if we are passed '4' as the doc number we
	 * would return '4.1', '4.2' but not '4.1.1'.  If we are passed
	 * '4.1' we would '4.1.1', '4.1.2' but not '4.1.1.1'.
	 */
	private ArrayList<ActivityStreamEntry> getListOfChildComments(String baseDocNum, List<ActivityStreamEntry> listOfComments) {
		ArrayList<ActivityStreamEntry> listOfChildComments = new ArrayList<ActivityStreamEntry>();
		if ((null != listOfComments) && (null != baseDocNum)) {
			baseDocNum += ".";
			int baseDocNumLen = baseDocNum.length();
			for (int i = 0; i < listOfComments.size(); i += 1) {
				// Does the doc number have a '.' in it after the base doc number?
				ActivityStreamEntry nextComment = listOfComments.get(i);
				String docNum = nextComment.getEntryDocNum();
				if ((null != docNum) &&
					 docNum.startsWith(baseDocNum) &&
					 (docNum.indexOf('.', baseDocNumLen) < 0)) {
					// No, comment is a child comment.
					dumpDocNumbers("CommentsWidget.getListOfChildComments( Matched )", baseDocNum, docNum);
					listOfChildComments.add(nextComment);
					
					// Remove the comment from the list of comments so we don't include it when
					// we are searching for sub comments.
					listOfComments.remove(i);
					i -= 1;
				}
				
				else {
					dumpDocNumbers("CommentsWidget.getListOfChildComments( Not matched )", baseDocNum, docNum);
				}
			}
		}
		return listOfChildComments;
	}
	
	/*
	 * For each parent comment, find it's child comments from the
	 * listOfAllComments.
	 */
	private void getChildComments(ArrayList<ActivityStreamEntry> listOfParentComments, List<ActivityStreamEntry> listOfAllComments) {
		if ((null != listOfParentComments) && (null != listOfAllComments)) {
			for (ActivityStreamEntry nextComment:  listOfParentComments) {
				// Get the list of child comments
				String baseDocNum = nextComment.getEntryDocNum();
				if (null != baseDocNum) {
					ArrayList<ActivityStreamEntry> childComments = getListOfChildComments(baseDocNum, listOfAllComments);
					nextComment.setComments(childComments);
					
					// For each child comment, get its child comments.
					if (null != childComments) {
						getChildComments(childComments, listOfAllComments);
					}
				}
			}
		}
	}
	
	/**
	 * Initialize the widget
	 * 
	 * @param commentsInfo
	 * @param commentAddedCallback
	 */
	public void init(CommentsInfo commentsInfo, CommentAddedCallback commentAddedCallback) {
		m_commentsInfo         = commentsInfo;
		m_commentAddedCallback = commentAddedCallback;
		
		// Have we created an ActivityStreamCtrl before?
		if (null == m_activityStreamCtrl) {
			// Note:  The order of the items in the popup menu is the
			// same as that used in the base, 'What's New' display.
			ArrayList<ActionMenuItem> list = new ArrayList<ActionMenuItem>();
			list.add(ActionMenuItem.REPLY);
			boolean notFilr = (!(GwtTeaming.m_requestInfo.isLicenseFilr()));
			if (notFilr) {
				list.add(ActionMenuItem.SEND_TO_FRIEND);
			}
			list.add(ActionMenuItem.SUBSCRIBE);
			if (notFilr) {
				list.add(ActionMenuItem.TAG);
			}
			list.add(ActionMenuItem.EDIT  );
			list.add(ActionMenuItem.DELETE);
			if (notFilr && m_showTitle) {
				// Note:  We only add the read/unread options when were
				// showing the title since that where the clickable
				// 'blue bubble' is located.
				list.add(ActionMenuItem.SEPARATOR  );
				list.add(ActionMenuItem.MARK_READ  );
				list.add(ActionMenuItem.MARK_UNREAD);
			}
			
			// No, create one.  The only reason we need to create an
			// ActivityStreamCtrl is because the ActivityStreamComment
			// object needs one.
			ActionsPopupMenu actionsMenu = new ActionsPopupMenu(true, true, list.toArray(new ActionMenuItem[0]));
			ActivityStreamCtrl.createAsync(ActivityStreamCtrlUsage.COMMENTS, false, actionsMenu, new ActivityStreamCtrlClient() {			
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in asynchronous
					// provider.
				}
				
				@Override
				public void onSuccess(ActivityStreamCtrl asCtrl) {
					m_activityStreamCtrl = asCtrl;
					m_activityStreamCtrl.setCheckForChanges(false);
					m_activityStreamCtrl.setDefaultDescViewFormat(DescViewFormat.FULL);
					
					// Issue GWT RPC request to get the comments on the
					// given entity.
					getCommentsFromServer();
				}
			});
		}
		
		else {
			// Issue a GWT RPC request to get the comments on the given
			// entity.
			getCommentsFromServer();
		}
	}
	
	public void init(CommentsInfo commentsInfo) {
		// Always use the initial form of the method.
		init(commentsInfo, null);	// null -> Caller doesn't need notifications of added comments.
	}
	
	/**
	 * Insert the given comments as the first comment.
	 * 
	 * @param reply
	 */
	@Override
	public void insertReply(ActivityStreamEntry reply) {
		addComment(reply, true);
		if (null != m_commentAddedCallback) {
			m_commentAddedCallback.commentAdded(m_commentsInfo);
		}
	}
	
	/*
	 */
	private void showNewComment(ActivityStreamComment asComment) {
		final Element element = asComment.getElement();
		
		element.scrollIntoView();
		element.getStyle().setOpacity(0);

		Timer showTimer = new Timer() {
			@Override
			public void run() {
				boolean increased = false;
				String opacityStr = element.getStyle().getOpacity();
				if (GwtClientHelper.hasString(opacityStr)) {
					try {
						BigDecimal opacity = new BigDecimal(opacityStr);
						if (0 > opacity.compareTo(new BigDecimal(1))) {
							element.getStyle().setOpacity(opacity.add(DELTA).doubleValue());
							increased = true;
						}
					}
					catch (NumberFormatException nfe) {/* Ignore. */}
				}
				
				if (!increased) {
					element.getStyle().setOpacity(1);
					cancel();
				}
			}
		};
		showTimer.scheduleRepeating(75);
	}

	/**
	 * Called when the CommentsWidget is attached to the DOM.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach() {
		super.onAttach();
	}
	
	/**
	 * Called when the CommentsWidget is detached from the DOM.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach() {
		super.onDetach();

		// If the activity stream control has been pseudo attached to
		// the DOM...
		if (m_acsPseudoAttached) {
			// ...tell it to detach.
			m_activityStreamCtrl.onDetach();
			m_acsPseudoAttached = false;
		}
	}
}

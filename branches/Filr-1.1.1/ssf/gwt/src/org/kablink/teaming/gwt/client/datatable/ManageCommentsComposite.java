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

import java.util.Map;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.ActivityStreamEntryRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.EntityRightsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetEntityRightsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetUserAvatarCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ReplyToEntryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.ActivityStreamEntry;
import org.kablink.teaming.gwt.client.util.CommentAddedCallback;
import org.kablink.teaming.gwt.client.util.CommentsInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.EntityRights;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.CommentsWidget;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextArea;

/**
 * Implements Vibe's 'Manage Comments' composite.
 * 
 * This composite provides the actual comment management for the manage
 * comments dialog and the View Details on a folder entry.
 *  
 * @author drfoster@novell.com
 */
public class ManageCommentsComposite extends ResizeComposite implements KeyDownHandler {
	private boolean							m_isIE;				// true -> The composite is running in IE.  false -> It's running in some other browser.
	private CommentAddedCallback			m_addedCallback;	// Callback interface used to tell the callee a new comment was added.
	private CommentsInfo					m_commentsInfo;		// The CommentsInfo the ManageCommentsComposite is running against.
	private CommentsWidget					m_commentsWidget;	// Widget that displays the current comments.
	private FlexTable						m_addCommentPanel;	//
	private GwtTeamingDataTableImageBundle	m_images;			// Access to Vibe's images.
	private GwtTeamingMessages				m_messages;			// Access to Vibe's messages.
	private Image							m_userAvatarImg;	// Image containing the user's avatar to the left of m_addCommentTA.
	private ManageCommentsCallback			m_manageCallback;	// Callback interface used to tell the callee about some management event within the composite.
	private String							m_baseStyle;		// Base name used to construct the various styles used throughout the composite.
	private TextArea						m_addCommentTA;		// The TextArea containing the comments being added.
	private VibeFlowPanel					m_fp;				// The panel that holds the composite's contents.
	
	private static final int COMMENT_WIDGET_SCROLL_DELAY	= 750;	// Time, in milliseconds to delay scrolling the comment widget after an operation on it.

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ManageCommentsComposite(ManageCommentsCallback manageCallback, String baseStyle) {
		// Initialize the superclass...
		super();
		
		// ...store the parameter...
		m_manageCallback = manageCallback;

		// ...initialize everything else...
		m_isIE     = GwtClientHelper.jsIsIE();
		m_images   = GwtTeaming.getDataTableImageBundle();
		m_messages = GwtTeaming.getMessages();
		if (GwtClientHelper.hasString(baseStyle))
		     m_baseStyle = baseStyle;
		else m_baseStyle = "vibe-manageCommentsComposite";
	
		// Create a panel to hold the composite's content...
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName(m_baseStyle + "-panel");

		// ...use it to initialize the composite...
		initWidget(m_fp);
		
		// ...and create the remainder of the composite's content.
		createContent();
	}

	/*
	 * Asynchronously adds a comment to the entry.
	 */
	private void addCommentAsync(final String comment) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				addCommentNow(comment);
			}
		});
	}

	/*
	 * Synchronously adds a comment to the entry.
	 */
	private void addCommentNow(String comment) {
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder = builder.appendEscapedLines(comment);
		
		ReplyToEntryCmd cmd = new ReplyToEntryCmd(m_commentsInfo.getEntityId().getEntityId(), builder.toSafeHtml().asString(), null);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_ReplyToEntry());
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				final ActivityStreamEntry asEntry = ((ActivityStreamEntryRpcResponseData) result.getResponseData()).getActivityStreamEntry();
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						// Add the comment to the comment widget...
						m_commentsWidget.addComment(asEntry, true);
						
						// ...clear the text from the TextArea...
						m_addCommentTA.setText("");
						
						// ...and if the caller wanted a
						// ...notification...
						if (null != m_addedCallback) {
							// ...tell them a comment was added.
							m_addedCallback.commentAdded(m_commentsInfo);
							if (m_commentsWidget.isVisible()) {
								scrollCommentsToBottomAsync();
							}
						}
					}
				});
			}
		});
	}

	/*
	 * Creates all the controls that make up the composite.
	 */
	private void createContent() {
		// Add the style to the composite...
		addStyleName(m_baseStyle);
		
		// ...create the widget to display the comments...
		m_commentsWidget = new CommentsWidget(false);
		m_commentsWidget.addStyleName(m_baseStyle + "-commentsWidget");
		m_fp.add(m_commentsWidget);

		// ...and create the widgets to add comments.
		m_addCommentPanel = new FlexTable();
		m_addCommentPanel.setCellPadding(0);
		m_addCommentPanel.setCellSpacing(0);
		m_addCommentPanel.addStyleName(m_baseStyle + "-addCommentPanel");
		if (!m_isIE) {
			m_addCommentPanel.addStyleName(m_baseStyle + "-addCommentPanel-nonIE");
		}
		m_userAvatarImg = GwtClientHelper.buildImage(m_images.userPhoto().getSafeUri().asString());
		m_userAvatarImg.addStyleName(m_baseStyle + "-addCommentAvatar");
		m_addCommentPanel.setWidget(0, 0, m_userAvatarImg);
		m_addCommentTA = new TextArea();
		m_addCommentTA.addStyleName(m_baseStyle + "-addCommentTextArea");
		m_addCommentTA.addKeyDownHandler(this);
		m_addCommentPanel.setWidget(0, 1, m_addCommentTA);
		m_addCommentPanel.getRowFormatter().setVerticalAlign(0, HasVerticalAlignment.ALIGN_TOP);
		VibeFlowPanel hintPanel = new VibeFlowPanel();
		hintPanel.addStyleName(m_baseStyle + "-addCommentHintPanel");
		Button sendButton = new Button(m_messages.manageCommentsCompositeSend());
		String sendStyle = (m_baseStyle + "-sendButton ");
		if (m_isIE)
		     sendStyle += (m_baseStyle + "-sendButton-IE");
		else sendStyle += (m_baseStyle + "-sendButton-nonIE");
		sendButton.addStyleName(sendStyle);
		sendButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String comment = getTrimmedComment();
				if (GwtClientHelper.hasString(comment)) {
					addCommentAsync(comment);
				}
			}
		});
		sendButton.addKeyDownHandler(this);
		hintPanel.add(sendButton);
		InlineLabel hint = new InlineLabel(m_messages.manageCommentsCompositeWhoHasAccess());
		hint.addStyleName(m_baseStyle + "-addCommentHint");
		hintPanel.add(hint);
		m_addCommentPanel.setWidget(1, 1, hintPanel);
		m_fp.add(m_addCommentPanel);
	}

	/**
	 * Returns the Widget to give the focus to.
	 * 
	 * @return
	 */
	public FocusWidget getFocusWidget() {
		return (((null != m_addCommentPanel) && m_addCommentPanel.isVisible()) ? m_addCommentTA : null);
	}

	/*
	 * Returns a non-null comment string with the leading and trailing
	 * white space trimmed off.
	 */
	private String getTrimmedComment() {
		String comment = m_addCommentTA.getText();
		if (null == comment)
		     comment = "";
		else comment = comment.trim();
		return comment;
	}
	
	/*
	 * Asynchronously initializes the given instance of the manage
	 * comments composite.
	 */
	private static void initCompositeAsync(final ManageCommentsComposite mcc, final CommentsInfo commentsInfo, final CommentAddedCallback addedCallback) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				mcc.initCompositeNow(commentsInfo, addedCallback);
			}
		});
	}
	
	/*
	 * Synchronously initializes the given instance of the manage
	 * comments composite.
	 */
	private void initCompositeNow(CommentsInfo commentsInfo, CommentAddedCallback addedCallback) {
		// Store the parameters...
		m_commentsInfo  = commentsInfo;
		m_addedCallback = addedCallback;
		
		// ...and start populating the composite.
		populateCompositeAsync();
	}

	/**
	 * Called when the user presses a key in the popup.
	 * 
	 * Implements the KeyDownHandler.onKeyDown() method.
	 * 
	 * @param event
	 */
	@Override
	public void onKeyDown(KeyDownEvent event) {
		String comment = getTrimmedComment();
		boolean hasComment = GwtClientHelper.hasString(comment);
		
		// What key is being pressed?
		switch (event.getNativeEvent().getKeyCode()) {
		case KeyCodes.KEY_ESCAPE:
			// Escape!  Has the user entered any text for a comment?
			if (!hasComment) {
				// No!  Then simply pass the escape to the caller.
				m_manageCallback.escape();
			}
			break;
		}
	}
	
	/*
	 * Asynchronously populates the contents of the composite.
	 */
	private void populateCompositeAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populateCompositeNow();
			}
		});
	}
	
	/*
	 * Synchronously populates the contents of the composite.
	 */
	private void populateCompositeNow() {
		// Initialize the comments widget with the comments...
		m_commentsWidget.init(m_commentsInfo, m_addedCallback);
		
		// ...initialize the comment entry widget...
		m_addCommentTA.setText("");

		// ...determine whether the user has rights to add a reply...
		final EntityId eid = m_commentsInfo.getEntityId();
		GwtClientHelper.executeCommand(
				new GetEntityRightsCmd(eid),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					GwtTeaming.getMessages().rpcFailure_GetEntityRights());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// ...hide/show the add comment panel based on the
				// ...user's right to add a reply...
				EntityRightsRpcResponseData responseData = ((EntityRightsRpcResponseData) response.getResponseData());
				Map<String, EntityRights> entityRightsMap = responseData.getEntityRightsMap();
				EntityRights er = entityRightsMap.get(EntityRights.getEntityRightsKey(eid));
				boolean canAddReplies = ((null != er) && er.isCanAddReplies());
				m_addCommentPanel.setVisible(canAddReplies);
				
				// ...tell the container that we're ready...
				m_manageCallback.compositeReady();
				scrollCommentsToBottomAsync();
				
				// ...force the user's avatar to be updated...
				updateUserAvatarAsync();
				
				// ...and force the focus into the TextArea.
				GwtClientHelper.setFocusDelayed(m_addCommentTA);
			}
		});
	}

	/*
	 * Asynchronously scrolls the comments widget to the bottom of its
	 * vertical scroll bar.
	 */
	private void scrollCommentsToBottomAsync(int delay) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				scrollCommentsToBottomNow();
			}
		},
		delay);
	}
	
	private void scrollCommentsToBottomAsync() {
		// Always use the initial form of the method.
		scrollCommentsToBottomAsync(COMMENT_WIDGET_SCROLL_DELAY);
	}
	
	/*
	 * Synchronously scrolls the comments widget to the bottom of its
	 * vertical scroll bar.
	 */
	private void scrollCommentsToBottomNow() {
		Element cwE = m_commentsWidget.getElement();
		cwE.setScrollTop(cwE.getScrollHeight());
	}
	
	/*
	 * Shows/hides the comments widget.
	 */
	private void setCommentsVisible(boolean show) {
		m_commentsWidget.setVisible(show);
		if (show) {
			scrollCommentsToBottomAsync(0);
		}
	}
	
	/*
	 * Asynchronously updates the user's avatar.
	 */
	private void updateUserAvatarAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				updateUserAvatarNow();
			}
		});
	}
	
	/*
	 * Synchronously updates the user's avatar.
	 */
	private void updateUserAvatarNow() {
		final String defaultAvatarUrl = m_images.userPhoto().getSafeUri().asString();
		GetUserAvatarCmd cmd = new GetUserAvatarCmd();
		cmd.setUserId(Long.parseLong(GwtClientHelper.getRequestInfo().getUserId()));
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetUserAvatar());
				m_userAvatarImg.setUrl(defaultAvatarUrl);
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				String response = ((StringRpcResponseData) result.getResponseData()).getStringValue();
				String avatarUrl;
				if (GwtClientHelper.hasString(response))
				     avatarUrl = response;
				else avatarUrl = defaultAvatarUrl;
				m_userAvatarImg.setUrl(avatarUrl);
			}
		});
	}

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the manage comments composite and perform some operation on   */
	/* it.                                                           */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the manage comments
	 * composite asynchronously after it loads. 
	 */
	public interface ManageCommentsCompositeClient {
		void onSuccess(ManageCommentsComposite mcc);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the ManageCommentsComposite and performs
	 * some operation against the code.
	 */
	private static void doAsyncOperation(
			// Parameters used to create the composite.
			final ManageCommentsCompositeClient mccClient,
			final ManageCommentsCallback		manageCallback,
			final String						baseStyle,

			// Parameters used to initialize an instance of the composite.
			final ManageCommentsComposite		mccInit,
			final CommentsInfo					commentsInfo,
			final CommentAddedCallback			addedCallback,
			
			// Parameters used to set the visibility state of the comments widget in an instance of the composite.
			final ManageCommentsComposite		mccShow,
			final boolean						show) {
		GWT.runAsync(ManageCommentsComposite.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_ManageCommentsComposite());
				if (null != mccClient) {
					mccClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a composite?
				if (null != mccClient) {
					// Yes!  Create it and return it via the callback.
					ManageCommentsComposite mcc = new ManageCommentsComposite(manageCallback, baseStyle);
					mccClient.onSuccess(mcc);
				}
				
				// No, it's not a request to create a composite!
				// Is it a request to initialize an existing one?
				else if (null != mccInit) {
					// Yes!  Initialize it.
					initCompositeAsync(mccInit, commentsInfo, addedCallback);
				}
				
				// No, it's not a request to initialize an existing one
				// either!  Is it a request to show/hide the comments
				// widget in an existing one?
				else if (null != mccShow) {
					// Yes!
					mccShow.setCommentsVisible(show);
				}
			}
		});
	}
	
	/**
	 * Loads the ManageCommentsComposite split point and returns an
	 * instance of it via the callback.
	 * 
	 * @param mccClient
	 * @param manageCallback
	 * @param baseStyle
	 */
	public static void createAsync(ManageCommentsCompositeClient mccClient, ManageCommentsCallback manageCallback, String baseStyle) {
		doAsyncOperation(mccClient, manageCallback, baseStyle, null, null, null, null, false);
	}
	
	public static void createAsync(ManageCommentsCompositeClient mccClient, ManageCommentsCallback manageCallback) {
		createAsync(mccClient, manageCallback, null);
	}

	/**
	 * Initializes an instance of the ManageCommentsComposite via an
	 * asynchronous call through its split point.
	 * 
	 * @param mcc
	 * @param commentsInfo
	 * @param addedCallback
	 */
	public static void initAsync(ManageCommentsComposite mcc, CommentsInfo commentsInfo, CommentAddedCallback addedCallback) {
		doAsyncOperation(null, null, null, mcc, commentsInfo, addedCallback, null, false);
	}
	
	/**
	 * Shows/hides the comments widget within an instance of the
	 * ManageCommentsComposite via an asynchronous call through its
	 * split point.
	 * 
	 * @param mcc
	 * @param show
	 */
	public static void setCommentsVisibleAsync(ManageCommentsComposite mcc, boolean show) {
		doAsyncOperation(null, null, null, null, null, null, mcc, show);
	}
}

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
package org.kablink.teaming.gwt.client.profile.widgets;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.presence.InstantMessageClickHandler;
import org.kablink.teaming.gwt.client.presence.PresenceControl;
import org.kablink.teaming.gwt.client.profile.ProfileCategory;
import org.kablink.teaming.gwt.client.profile.ProfileClientUtil;
import org.kablink.teaming.gwt.client.profile.ProfileInfo;
import org.kablink.teaming.gwt.client.profile.UserStatus;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetAddMeetingUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetBinderPermalinkCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetMicroBlogUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetQuickViewInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetUserStatusCmd;
import org.kablink.teaming.gwt.client.rpc.shared.IsPersonTrackedCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.TrackBinderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.UntrackPersonCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * This is the QuickView Dialog
 * 
 * @author nbjensen@novell.com
 */
public class GwtQuickViewDlg extends DlgBox {
	private Anchor				m_miniBlogA;			//
	private FlowPanel			m_pictureDiv;			//
	private Grid				m_grid;					//
	private Image				m_avatar;				//
	private Label				m_statusLabel;			//
	private ProfileActionWidget	m_instantMessageBtn;	//
	private ProfileActionWidget	m_profileBtn;			//
	private ProfileActionWidget	m_workspaceBtn;			//
	private QuickViewAction		m_followBtn;			//
	private String				m_binderId;				//
	private String				m_userId;				//
	private String				m_userName;				//

	/*
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private GwtQuickViewDlg(int pos, int pos2, String userId, String binderId, String userName, Element element) {
		super(true, false, pos, pos2);

		m_userId   = userId;
		m_binderId = binderId;
		m_userName = userName;

		createAllDlgContent("", null, null, null);
	}

	/**
	 * Create the header, content and footer for the dialog box.
	 * 
	 * @param caption
	 * @param editSuccessfulHandler
	 * @param editCanceledHandler
	 * @param properties
	 */
	@Override
	public void createAllDlgContent(String caption, EditSuccessfulHandler editSuccessfulHandler, EditCanceledHandler editCanceledHandler, Object properties) {
		FlowPanel panel = new FlowPanel();
		panel.addStyleName("qViewDlg");

		// Add the header.
		Panel header = createHeader(caption);
		panel.add(header);

		// Add the main content of the dialog box.
		Panel content = createContent(properties);
		panel.add(content);

		// Create the footer.
		Panel footer = createFooter();
		panel.add(footer);

		init(properties);

		// Initialize the handlers.
		initHandlers(editSuccessfulHandler, editCanceledHandler);

		setWidget(panel);
	}

	/**
	 * Clear all the content from the dialog and start fresh.
	 */
	public void clearContent() {
		// Nothing to do.
	}

	@Override
	public Panel createContent(Object props) {

		HorizontalPanel mainPanel = new HorizontalPanel();
		mainPanel.addStyleName("qViewMain");

		FlowPanel leftPanel = new FlowPanel();
		leftPanel.addStyleName("qViewLeft");
		mainPanel.add(leftPanel);

		Panel photoPanel = createPhotoPanel();
		Panel actionsPanel = createActionsPanel();
		leftPanel.add(photoPanel);
		leftPanel.add(actionsPanel);

		FlowPanel rightPanel = new FlowPanel();
		rightPanel.addStyleName("qViewRight");
		mainPanel.add(rightPanel);

		if (!(GwtClientHelper.isLicenseFilr())) {
			Panel statusPanel = createStatusPanel();
			rightPanel.add(statusPanel);
		}
		
		Panel infoPanel = createInfoPanel();
		rightPanel.add(infoPanel);

		return mainPanel;
	}

	private Panel createPhotoPanel() {
		m_pictureDiv = new FlowPanel();
		m_pictureDiv.addStyleName("qViewPhoto");

		m_avatar = new Image();
		m_pictureDiv.add(m_avatar);

		return m_pictureDiv;
	}

	private Panel createActionsPanel() {

		ClickHandler clickHandler;
		FlowPanel panel = new FlowPanel();
		panel.addStyleName("qViewActions");
		
		m_profileBtn = new ProfileActionWidget(GwtTeaming.getMessages().qViewProfile(),
										GwtTeaming.getMessages().qViewProfileTitle(), 
										"qView-a", "qView-action");
		m_profileBtn.addClickHandler(new WorkspaceEventHandler(true));
		
		boolean isFilr = GwtClientHelper.isLicenseFilr();
		if (!isFilr) {
			m_workspaceBtn = new ProfileActionWidget(GwtTeaming.getMessages().qViewWorkspace(),
											 GwtTeaming.getMessages().qViewWorkspaceTitle(),
											 "qView-a",	"qView-action");
			m_workspaceBtn.addClickHandler(new WorkspaceEventHandler(false));
		}
		
		m_instantMessageBtn = new ProfileActionWidget(GwtTeaming.getMessages().qViewInstantMessage(),
				GwtTeaming.getMessages().qViewInstantMessageTitle(),
				"qView-a", "qView-action");
		m_instantMessageBtn.addClickHandler(new InstantMessageClickHandler(m_binderId) {
				// Override onClick so we can hide the dialog after launching
				// the instant message.
				@Override
				public void onClick(ClickEvent event) {
					super.onClick(event);
					hide();
				}
			});
		m_instantMessageBtn.setVisible(false);

		if (!isFilr) {
			m_followBtn = new QuickViewAction("", "", "qView-a",
										    "qView-action-following");
	
			// Add a clickhandler to the "advanced" link. When the user clicks on
			// the link we
			// will invoke the "edit advanced branding" dialog.
			clickHandler = new ClickHandler() {
				/**
				 * Invoke the "edit advanced branding" dialog
				 */
				@Override
				public void onClick(ClickEvent event) {
					if(m_followBtn.isChecked()) {
						unFollowAction();
					} else {
						followAction();
					}
				}
	
				private void followAction() {
					TrackBinderCmd cmd;
					
					cmd = new TrackBinderCmd( m_binderId );
					GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
					{
						@Override
						public void onFailure( Throwable t )
						{
							GwtClientHelper.handleGwtRPCFailure(
								t,
								GwtTeaming.getMessages().rpcFailure_TrackingBinder(),
								m_binderId);
						}
						
						@Override
						public void onSuccess( VibeRpcResponse response )
						{
							boolean success = ((BooleanRpcResponseData) response.getResponseData()).getBooleanValue();
							if (success)
							     updateFollowingButton(true);
							else GwtClientHelper.deferredAlert(GwtTeaming.getMessages().qViewErrorCantTrack());
						}
					});
				}
	
				private void unFollowAction() {
					UntrackPersonCmd cmd;
					
					cmd = new UntrackPersonCmd( Long.parseLong( m_binderId ) );
					GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
					{
						@Override
						public void onFailure( Throwable t )
						{
							GwtClientHelper.handleGwtRPCFailure(
								t,
								GwtTeaming.getMessages().rpcFailure_UntrackingPerson(),
								m_binderId);
						}
						
						@Override
						public void onSuccess( VibeRpcResponse response )
						{
							boolean success = ((BooleanRpcResponseData) response.getResponseData()).getBooleanValue();
							if (success)
							     updateFollowingButton(false);
							else GwtClientHelper.deferredAlert(GwtTeaming.getMessages().qViewErrorCantUntrack());
						}
					});
				}
			};
			m_followBtn.addClickHandler(clickHandler);
		}
		
		panel.add(m_profileBtn);
		if (!isFilr) {
			panel.add(m_workspaceBtn);
		}
		panel.add(m_instantMessageBtn);
		if (!isFilr) {
			panel.add(m_followBtn);
		}

		return panel;
	}

	private Panel createStatusPanel() {

		FlowPanel status = new FlowPanel();
		status.addStyleName("qViewStatus");
		
		InlineLabel span = new InlineLabel();
		status.add(span);
		
		m_statusLabel = new Label();
		span.getElement().appendChild(m_statusLabel.getElement());
		m_statusLabel.addStyleName("qViewStatusInline");
		
		m_miniBlogA = new Anchor(GwtTeaming.getMessages().qViewMicroBlog());
		m_miniBlogA.setTitle(GwtTeaming.getMessages().qViewMicroBlogTitle());
		status.add(m_miniBlogA);
		
		m_miniBlogA.addStyleName("qViewStatus-anchor");
		m_miniBlogA.addStyleName("qViewStatusInline");
		
		m_miniBlogA.addClickHandler(new MicroBlogClickHandler(m_binderId));

		return status;
	}

	private Grid createInfoPanel() {

		m_grid = new Grid();
		m_grid.setWidth("100%");
		m_grid.setCellSpacing(0);
		m_grid.setCellPadding(0);
		m_grid.resizeColumns(3);
		m_grid.setStyleName("qViewInfoTable");

		return m_grid;
	}


	/*
	 * Override the createFooter() method so we can control what buttons are in
	 * the footer.
	 */
	@Override
	public FlowPanel createFooter() {
		FlowPanel panel;

		panel = new FlowPanel();
		panel.addStyleName("qViewFooter");

		return panel;
	}

	/**
	 * Override the createHeader() method because we need to make it nicer.
	 */
	@Override
	public Panel createHeader(String caption) {
		FlowPanel panel;

		panel = new FlowPanel();
		panel.addStyleName("teamingDlgBoxHeader");

		FlowPanel titlePanel = new FlowPanel();
		titlePanel.addStyleName("qViewTitle");

		InlineLabel userName = new InlineLabel(m_userName);
		userName.addStyleName("qViewTitleText");

		PresenceControl presence = new PresenceControl(m_userId, m_binderId, false, true, true);
		presence.addStyleName("qViewPresence");

		titlePanel.add(presence);
		titlePanel.add(userName);

		panel.add(titlePanel);

		Anchor closeA = new Anchor();
		closeA.addStyleName("qViewClose");

		Image cancelImage = new Image(GwtTeaming.getImageBundle().closeBorder());
		closeA.getElement().appendChild(cancelImage.getElement());

		// GwtTeaming.getMessages().cancel()
		closeA.setVisible(true);
		panel.add(closeA);

		closeA.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		return panel;
	}

	@Override
	public Object getDataFromDlg() {
		return null;
	}

	@Override
	public FocusWidget getFocusWidget() {
		return ((null == m_workspaceBtn) ? m_profileBtn : m_workspaceBtn);
	}

	/**
	 * Initialize the controls in the dialog with the values from the given
	 * object. 
	 */
	private void init(Object props) {
		createProfileInfoSections();
	}

	
	/**
	 * Create the Profile Heading Sections and their associated Profile
	 * Attributes
	 * 
	 * @param profileRequestInfo
	 */
	private void createProfileInfoSections() { 
		// Get the quick view information for this user.
		GwtClientHelper.executeCommand(
			new GetQuickViewInfoCmd(m_userId, m_binderId),
			new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					if ((t instanceof GwtTeamingException) && ExceptionType.ACCESS_CONTROL_EXCEPTION.equals(((GwtTeamingException) t).getExceptionType()))
					     GwtClientHelper.deferredAlert(         GwtTeaming.getMessages().qViewErrorNoRights()            );
					else GwtClientHelper.handleGwtRPCFailure(t, GwtTeaming.getMessages().rpcFailure_GetStatus(), m_binderId);
					
					hide();
				}

				@Override
				public void onSuccess(VibeRpcResponse response) {
					ProfileInfo profileInfo = ((ProfileInfo) response.getResponseData());
					boolean isPictureEnabled = profileInfo.isPictureEnabled();
					String url = profileInfo.getPictureScaledUrl();
					if (isPictureEnabled) {
						if(url != null && !url.equals("")) {
							m_avatar.setUrl(url);
						}
						else {
							FlowPanel w = ((FlowPanel) m_avatar.getParent());
							w.addStyleName("qViewPhoto");
							FlowPanel panel = new FlowPanel();
							w.add(panel);
							panel.addStyleName("qViewPhotoHeight_No");
							panel.addStyleName("qViewPhoto_none");
							m_avatar.removeFromParent();
						}
					}
					else {
						m_pictureDiv.addStyleName("qViewPhotoDisabled");
					}
					
					int count = profileInfo.getCategories().size();
					int row   = 0;
					for (int i = 0; i < count; i += 1) {
						ProfileCategory cat = profileInfo.get(i);
						if (cat.getName().equals("profileSidePanelView")) {
							continue;
						}
						row = ProfileClientUtil.createProfileInfoSection(cat, m_grid, row, false, false);
					}
		
					getUserStatus();
					updateFollowingStatus();
					
					m_instantMessageBtn.setVisible(profileInfo.isPresenceEnabled());

					// If the viewing user doesn't have access to the
					// target user's personal workspace...
					if (!(profileInfo.canAccessUserWS())) {
						// ...hide the widgets that would allow them
						// ...to interact with it.
						if (null != m_followBtn)    m_followBtn.setVisible(   false);
						if (null != m_profileBtn)   m_profileBtn.setVisible(  false);
						if (null != m_workspaceBtn) m_workspaceBtn.setVisible(false);
					}
					
					// If target user doesn't have a personal
					// workspace...
					if (!(profileInfo.hasUserWS())) {
						// ...hide the widgets that require one.
						if (null != m_miniBlogA) m_miniBlogA.setVisible(false);
					}
				}
			});
	}

	
	
	/**
	 * This WorkspaceEventHandler handles the actions on the profile button or the workspace button.
	 * 
	 */
	private class WorkspaceEventHandler implements ClickHandler {

		boolean showProfile = false;

		public WorkspaceEventHandler(boolean profile){
			showProfile = profile;
		}
		
		@Override
		public void onClick(ClickEvent event) {
			GetBinderPermalinkCmd cmd;
			
			cmd = new GetBinderPermalinkCmd( m_binderId );
			GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
			{
				@Override
				public void onFailure( Throwable t ) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetBinderPermalink(),
						m_binderId);
				}
				
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					String binderUrl;
					StringRpcResponseData responseData;

					responseData = (StringRpcResponseData) response.getResponseData();
					binderUrl = responseData.getStringValue();
					
					if ( binderUrl == null || binderUrl.length() == 0 )
					{
						if ( showProfile )
						{
							// The user does not have a workspace.  Tell the user about it.
							Window.alert( GwtTeaming.getMessages().qViewErrorNoProfile() );
						}
						else
						{
							// The user does not have a workspace.  Tell the user about it.
							Window.alert( GwtTeaming.getMessages().qViewErrorDeletedWorkspace() );
						}
						
						return;
					}

					if(showProfile){
						binderUrl = GwtClientHelper.appendUrlParam( binderUrl, "operation", "showProfile" );
					} else {
						binderUrl = GwtClientHelper.appendUrlParam( binderUrl, "operation", "showWorkspace" );
					}
					
					//Fire event to notify that a selection has changed
					EventHelper.fireChangeContextEventAsync( m_binderId, binderUrl, Instigator.PROFILE_QUICK_VIEW_SELECT );
					
					hide();
				}
			});
		}
	}

	/**
	 * Checks to see if the current User is following this person
	 * @return
	 */
	private void updateFollowingStatus() {
		IsPersonTrackedCmd cmd;
		
		cmd = new IsPersonTrackedCmd( m_binderId );
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
				{
					@Override
					public void onFailure( Throwable t )
					{
						GwtClientHelper.handleGwtRPCFailure(
							t,
							GwtTeaming.getMessages().rpcFailure_IsPersonTracked(),
							m_binderId);
					}
					
					@Override
					public void onSuccess( VibeRpcResponse response )
					{
						Boolean success;
						BooleanRpcResponseData responseData;
						
						responseData = (BooleanRpcResponseData) response.getResponseData();
						success = responseData.getBooleanValue();
						
						updateFollowingButton(success.booleanValue());
					}
				});
	}
	
	/**
	 * Update the follow button 
	 * @param isFollowing
	 */
	private void updateFollowingButton(boolean isFollowing) {
		if (null != m_followBtn) {
			if(isFollowing) {
				m_followBtn.setText(GwtTeaming.getMessages().qViewFollowing());
				m_followBtn.setTitle(GwtTeaming.getMessages().qViewFollowingTitle());
				m_followBtn.setChecked(true);
			} else {
				m_followBtn.setText(GwtTeaming.getMessages().qViewFollow());
				m_followBtn.setTitle(GwtTeaming.getMessages().qViewFollowTitle());
				m_followBtn.setChecked(false);
			}
		}
	}
	
	private void getUserStatus() {
		// Issue an ajax request to save the user status to the db.  rpcCallback will
		// be called when we get the response back.
		GwtClientHelper.executeCommand(
			new GetUserStatusCmd(m_userId, m_binderId),
			new AsyncCallback<VibeRpcResponse>(){
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetStatus(),
						m_binderId );
				}

				@Override
				public void onSuccess( VibeRpcResponse response ) {
					UserStatus result = null;
					
					if ( response.getResponseData() != null )
						result = (UserStatus) response.getResponseData();
					
					if(result != null) {
						String description = result.getStatus();
						if(description != null && !description.equals("")){
							m_statusLabel.setText(description);
						} 
					}
				}
			});
	}

	/*
	 * ?
	 */
	private class MicroBlogClickHandler implements ClickHandler {
		String	m_mbBinderId;	//
		
		public MicroBlogClickHandler(String binderId){
			m_mbBinderId = binderId;
		}
		
		@Override
		public void onClick(ClickEvent event) {
			// Issue an ajax request to save the user status to the db.  rpcCallback will
			// be called when we get the response back.
			GetMicroBlogUrlCmd cmd = new GetMicroBlogUrlCmd( m_mbBinderId );
			GwtClientHelper.executeCommand( cmd, rpcCallback );
		}

		
		AsyncCallback<VibeRpcResponse> rpcCallback = new AsyncCallback<VibeRpcResponse>(){
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_QViewMicroBlog(),
					m_binderId );
				}

				@Override
				public void onSuccess(VibeRpcResponse response) {
					String url = null;
					
					if ( response.getResponseData() != null )
					{
						StringRpcResponseData responseData;
					
						responseData = (StringRpcResponseData) response.getResponseData();
						url = responseData.getStringValue();
					}

					if(GwtClientHelper.hasString(url)) {
						//GwtClientHelper.loadUrlInContentFrame(url);
						GwtClientHelper.jsLaunchUrlInWindow(url,"_blank", 500, 500);
						hide();
					}
				}
			};
	}

	@SuppressWarnings("unused")
	private class ConferencingClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			GetAddMeetingUrlCmd cmd;
			
			// Get the URL to the meeting start/schedule dialog and launch it in a new window
			cmd = new GetAddMeetingUrlCmd( m_binderId );
			GwtClientHelper.executeCommand( cmd,
					new AsyncCallback<VibeRpcResponse>() {
						@Override
						public void onSuccess( VibeRpcResponse response ) {
							String url=null;
							
							if ( response.getResponseData() != null )
							{
								StringRpcResponseData responseData;
								
								responseData = (StringRpcResponseData) response.getResponseData();
								url = responseData.getStringValue();
							}
							
							if (GwtClientHelper.hasString(url)) {
								GwtClientHelper.jsLaunchUrlInWindow(url, "_blank", 500, 600);
								hide();
							}
						}
						@Override
						public void onFailure(Throwable t) {
							GwtClientHelper.handleGwtRPCFailure(
								t,
								GwtTeaming.getMessages().rpcFailure_GetAddMeetingUrl(),
								m_binderId );
						}
					});
		}
	}
	
	private class QuickViewAction extends Anchor {

		private Label label;
		private Image img;
		private boolean isChecked;
		private FlowPanel panel;
		private String labelStyle;

		public QuickViewAction(String text, String title, String anchorStlyeName,
				String labelStyleName) {
			super();

			labelStyle = labelStyleName;
			addStyleName(anchorStlyeName);

			if(labelStyle.equals("qView-action-following") ){
				panel = new FlowPanel();
				panel.addStyleName(labelStyleName);

				img = new Image(GwtTeaming.getImageBundle().check12());
				img.addStyleName("qView-follow-img");
				img.setVisible(false);
				panel.add(img);

				label = new Label(text);
				label.addStyleName("qView-follow-label");
				label.setTitle(title);
				panel.add(label);
				
				getElement().appendChild(panel.getElement());
			} else {
				label = new Label(text);
				label.setTitle(title);
				label.addStyleName(labelStyleName);
				getElement().appendChild(label.getElement());
			}
			
			addMouseOverHandler(new MouseOverHandler() {
				@Override
				public void onMouseOver(MouseOverEvent event) {
					if(labelStyle.equals("qView-action-following") ){
						panel.addStyleName("qView-action2");
					} else {
						label.addStyleName("qView-action2");
					}
				}});
			
			addMouseOutHandler(new MouseOutHandler(){
				@Override
				public void onMouseOut(MouseOutEvent event) {
					if(labelStyle.equals("qView-action-following") ){
						panel.removeStyleName("qView-action2");
					} else {
						label.removeStyleName("qView-action2");
					}

				}});
		}

		public boolean isChecked() {
			return isChecked;
		}

		public void setChecked(boolean checked) {
			if(checked) {
				isChecked = true;
				
				if(panel != null){
					panel.removeStyleName("qView-action");
					panel.addStyleName("qView-action-following");
				}
				
				if(img != null){
					img.setVisible(true);
				}
			} else {
				isChecked = false;
				
				if(panel != null){
					panel.removeStyleName("qView-action-following");
					panel.addStyleName("qView-action");
				}				

				if(img != null){
					img.setVisible(false);
				}
			}
		}
		
		@Override
		public void setText(String text) {
			if(label!=null){
				label.setText(text);
			} else {
				setText(text);
			}
		}
	}

	/**
	 * Callback interface to interact with the quick view dialog
	 * asynchronously after it loads. 
	 */
	public interface GwtQuickViewDlgClient {
		void onSuccess(GwtQuickViewDlg qvd);
		void onUnavailable();
	}

	/**
	 * Loads the GwtQuickViewDlg split point and returns an
	 * instance of it via the callback.
	 * 
	 * @param pos
	 * @param pos2
	 * @param m_userId
	 * @param m_binderId
	 * @param m_userName
	 * @param element
	 * @param qvdClient
	 */
	public static void createAsync(
			final int pos,
			final int pos2,
			final String userId,
			final String binderId,
			final String userName,
			final Element element,
			final GwtQuickViewDlgClient qvdClient) {
		GWT.runAsync(GwtQuickViewDlg.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				GwtQuickViewDlg qvd = new GwtQuickViewDlg(pos, pos2, userId, binderId, userName, element);
				qvdClient.onSuccess(qvd);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_QuickViewDlg());
				qvdClient.onUnavailable();
			}
		});
	}
}

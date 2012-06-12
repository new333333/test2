/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
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
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
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
import com.google.gwt.user.client.ui.Widget;

/**
 * This is the QuickView Dialog
 * 
 * @author nbjensen
 *
 */
public class GwtQuickViewDlg extends DlgBox implements NativePreviewHandler{

	private String binderId;
	private Grid grid;
	
	private ProfileActionWidget workspaceBtn;
	private ProfileActionWidget profileBtn;
	private QuickViewAction followBtn;
	
	private Label statusLabel;
	
	private String userName;
	private Image avatar;
	private Anchor miniBlogA;
	private ProfileActionWidget instantMessageBtn;
	@SuppressWarnings("unused")
	private Element clientElement;
	private FlowPanel pictureDiv;

	/*
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private GwtQuickViewDlg(boolean autoHide, boolean modal, int pos,
			int pos2, String binderId, String userName, Element element) {
		super(autoHide, modal, pos, pos2);

		this.binderId = binderId;
		this.userName = userName;
		this.clientElement = element;

		createAllDlgContent("", null, null, null);
		
		// Register a preview-event handler.  We do this so we can see the mouse-down event
		// in and out side of the widget.
		Event.addNativePreviewHandler( this );
	}

	/**
	 * Create the header, content and footer for the dialog box.
	 */
	public void createAllDlgContent(String caption,
			EditSuccessfulHandler editSuccessfulHandler,// We will call this
														// handler when the user
														// presses the ok button
			EditCanceledHandler editCanceledHandler, // This gets called when
														// the user presses the
														// Cancel button
			Object properties) // Where properties used in the dialog are read
								// from and saved to.
	{
		FlowPanel panel;
		Panel content;
		Panel header;
		Panel footer;

		panel = new FlowPanel();
		panel.addStyleName("qViewDlg");

		// Add the header.
		header = createHeader(caption);
		panel.add(header);

		// Add the main content of the dialog box.
		content = createContent(properties);
		panel.add(content);

		// Create the footer.
		footer = createFooter();
		panel.add(footer);

		init(properties);

		// Initialize the handlers
		initHandlers(editSuccessfulHandler, editCanceledHandler);

		setWidget(panel);
	}// end createAllDlgContent()

	/**
	 * Clear all the content from the dialog and start fresh.
	 */
	public void clearContent() {
	}// end clearContent()

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

		Panel statusPanel = createStatusPanel();
		Panel infoPanel = createInfoPanel();

		rightPanel.add(statusPanel);
		rightPanel.add(infoPanel);

		return mainPanel;
	}

	private Panel createPhotoPanel() {
		pictureDiv = new FlowPanel();
		pictureDiv.addStyleName("qViewPhoto");

		avatar = new Image();
		pictureDiv.add(avatar);

		return pictureDiv;
	}

	private Panel createActionsPanel() {

		ClickHandler clickHandler;
		FlowPanel panel = new FlowPanel();
		panel.addStyleName("qViewActions");
		
		profileBtn = new ProfileActionWidget(GwtTeaming.getMessages().qViewProfile(),
										GwtTeaming.getMessages().qViewProfileTitle(), 
										"qView-a", "qView-action");
		profileBtn.addClickHandler(new WorkspaceEventHandler(true));
		
		workspaceBtn = new ProfileActionWidget(GwtTeaming.getMessages().qViewWorkspace(),
										 GwtTeaming.getMessages().qViewWorkspaceTitle(),
										 "qView-a",	"qView-action");
		workspaceBtn.addClickHandler(new WorkspaceEventHandler(false));
		
		instantMessageBtn = new ProfileActionWidget(GwtTeaming.getMessages().qViewInstantMessage(),
				GwtTeaming.getMessages().qViewInstantMessageTitle(),
				"qView-a", "qView-action");
		instantMessageBtn.addClickHandler(new InstantMessageClickHandler(binderId) {
				// Override onClick so we can hide the dialog after launching
				// the instant message.
				public void onClick(ClickEvent event) {
					super.onClick(event);
					hide();
				}
			});
		instantMessageBtn.setVisible(false);

		followBtn = new QuickViewAction("", "", "qView-a",
									    "qView-action-following");

		// Add a clickhandler to the "advanced" link. When the user clicks on
		// the link we
		// will invoke the "edit advanced branding" dialog.
		clickHandler = new ClickHandler() {
			/**
			 * Invoke the "edit advanced branding" dialog
			 */
			public void onClick(ClickEvent event) {
				if(followBtn.isChecked()) {
					unFollowAction();
				} else {
					followAction();
				}
			}// end onClick()

			private void followAction() {
				TrackBinderCmd cmd;
				
				cmd = new TrackBinderCmd( binderId );
				GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
				{
					public void onFailure( Throwable t )
					{
						GwtClientHelper.handleGwtRPCFailure(
							t,
							GwtTeaming.getMessages().rpcFailure_TrackingBinder(),
							binderId);
					}//end onFailure()
					
					public void onSuccess( VibeRpcResponse response )
					{
						updateFollowingButton(true);
					}// end onSuccess()
				});
			}

			private void unFollowAction() {
				UntrackPersonCmd cmd;
				
				cmd = new UntrackPersonCmd( binderId );
				GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
				{
					public void onFailure( Throwable t )
					{
						GwtClientHelper.handleGwtRPCFailure(
							t,
							GwtTeaming.getMessages().rpcFailure_UntrackingPerson(),
							binderId);
					}//end onFailure()
					
					public void onSuccess( VibeRpcResponse response )
					{
						updateFollowingButton(false);
					}// end onSuccess()
				});
			}
		};
		followBtn.addClickHandler(clickHandler);
		
		panel.add(profileBtn);
		panel.add(workspaceBtn);
		panel.add(instantMessageBtn);
		panel.add(followBtn);

		return panel;
	}

	private Panel createStatusPanel() {

		FlowPanel status = new FlowPanel();
		status.addStyleName("qViewStatus");
		
		InlineLabel span = new InlineLabel();
		status.add(span);
		
		statusLabel = new Label();
		span.getElement().appendChild(statusLabel.getElement());
		statusLabel.addStyleName("qViewStatusInline");
		
		miniBlogA = new Anchor(GwtTeaming.getMessages().qViewMicroBlog());
		miniBlogA.setTitle(GwtTeaming.getMessages().qViewMicroBlogTitle());
		status.add(miniBlogA);
		
		miniBlogA.addStyleName("qViewStatus-anchor");
		miniBlogA.addStyleName("qViewStatusInline");
		
		miniBlogA.addClickHandler(new MicroBlogClickHandler(binderId));

		return status;
	}

	private Grid createInfoPanel() {

		grid = new Grid();
		grid.setWidth("100%");
		grid.setCellSpacing(0);
		grid.setCellPadding(0);
		grid.resizeColumns(3);
		grid.setStyleName("qViewInfoTable");

		return grid;
	}


	/*
	 * Override the createFooter() method so we can control what buttons are in
	 * the footer.
	 */
	public Panel createFooter() {
		FlowPanel panel;

		panel = new FlowPanel();
		panel.addStyleName("qViewFooter");

		return panel;
	}// end createFooter()

	/**
	 * Override the createHeader() method because we need to make it nicer.
	 */
	public Panel createHeader(String caption) {
		FlowPanel panel;

		panel = new FlowPanel();
		panel.addStyleName("teamingDlgBoxHeader");

		FlowPanel titlePanel = new FlowPanel();
		titlePanel.addStyleName("qViewTitle");

		InlineLabel userName = new InlineLabel(this.userName);
		userName.addStyleName("qViewTitleText");

		PresenceControl presence = new PresenceControl(binderId, false, true, true);
		presence.addStyleName("qViewPresence");

		titlePanel.add(presence);
		titlePanel.add(userName);

		panel.add(titlePanel);

		Anchor closeA = new Anchor();
		closeA.addStyleName("qViewClose");

		Image cancelImage = new Image(GwtTeaming.getMainMenuImageBundle()
				.closeXMouseOver());
		closeA.getElement().appendChild(cancelImage.getElement());

		// GwtTeaming.getMessages().cancel()
		closeA.setVisible(true);
		panel.add(closeA);

		closeA.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		return panel;
	}// end createHeader()

	@Override
	public Object getDataFromDlg() {
		return null;
	}

	@Override
	public FocusWidget getFocusWidget() {

		return workspaceBtn;
	}

	/**
	 * Initialize the controls in the dialog with the values from the given
	 * object. 
	 */
	private void init(Object props) {
		createProfileInfoSections();
	}// end init()

	
	/**
	 * Create the Profile Heading Sections and their associated Profile
	 * Attributes
	 * 
	 * @param profileRequestInfo
	 */
	private void createProfileInfoSections() {

		GetQuickViewInfoCmd cmd;

		// create an async callback to handle the result of the request to get
		// the state:
		AsyncCallback<VibeRpcResponse> callback = new AsyncCallback<VibeRpcResponse>() {
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetStatus(),
					binderId );
				
				hide();
			}

			public void onSuccess( VibeRpcResponse response ) {
				ProfileInfo profile;
				
				profile = (ProfileInfo) response.getResponseData();
				
				boolean isPictureEnabled = profile.isPictureEnabled();
				String url = profile.getPictureScaledUrl();
				if(isPictureEnabled) {
					if(url != null && !url.equals("")){
						avatar.setUrl(url);
					} else {
						FlowPanel w = (FlowPanel)avatar.getParent();
						w.removeStyleName("qViewPhoto");
						w.addStyleName("qViewPhoto_No");
						FlowPanel panel = new FlowPanel();
						w.add(panel);
						panel.addStyleName("qViewPhotoHeight_No");
						panel.addStyleName("ss_profile_photo_box_empty");
						avatar.removeFromParent();
					}
				} else {
					pictureDiv.addStyleName("qViewPhotoDisabled");
				}
				
				int count = profile.getCategories().size();
				int row = 0;
				for (int i = 0; i < count; i++) {

					ProfileCategory cat = profile.get(i);
					if (cat.getName().equals("profileSidePanelView")) {
						continue;
					}

					row = ProfileClientUtil.createProfileInfoSection(cat, grid,	row, false, false);
				}
	
				getUserStatus();
				updateFollowingStatus();
				
				instantMessageBtn.setVisible(profile.isPresenceEnabled());
			}
		};

		cmd = new GetQuickViewInfoCmd( binderId );
		GwtClientHelper.executeCommand( cmd, callback );
	}

	
	
	/**
	 * This WorkspaceEventHandler handles the actions on the profile button or the workspace button.
	 * 
	 */
	private class WorkspaceEventHandler implements ClickHandler {

		boolean showProfile = false;

		public WorkspaceEventHandler(boolean profile){
			this.showProfile = profile;
		}
		
		public void onClick(ClickEvent event) {
			GetBinderPermalinkCmd cmd;
			
			cmd = new GetBinderPermalinkCmd( binderId );
			GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
			{
				public void onFailure( Throwable t ) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetBinderPermalink(),
						binderId);
				}//end onFailure()
				
				public void onSuccess( VibeRpcResponse response )
				{
					String binderUrl;
					OnSelectBinderInfo osbInfo;
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
					osbInfo = new OnSelectBinderInfo( binderId, binderUrl, false, Instigator.PROFILE_QUICK_VIEW_SELECT );
					
					//Fire event to notify that a selection has changed
					GwtTeaming.fireEvent(new ChangeContextEvent(osbInfo));
					
					hide();
				}// end onSuccess()
			});// end AsyncCallback()
		}
	}

	/**
	 * Checks to see if the current User is following this person
	 * @return
	 */
	private void updateFollowingStatus() {
		IsPersonTrackedCmd cmd;
		
		cmd = new IsPersonTrackedCmd( binderId );
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
				{
					public void onFailure( Throwable t )
					{
						GwtClientHelper.handleGwtRPCFailure(
							t,
							GwtTeaming.getMessages().rpcFailure_IsPersonTracked(),
							binderId);
					}//end onFailure()
					
					public void onSuccess( VibeRpcResponse response )
					{
						Boolean success;
						BooleanRpcResponseData responseData;
						
						responseData = (BooleanRpcResponseData) response.getResponseData();
						success = responseData.getBooleanValue();
						
						updateFollowingButton(success.booleanValue());
					}// end onSuccess()
				});
	}
	
	/**
	 * Update the follow button 
	 * @param isFollowing
	 */
	private void updateFollowingButton(boolean isFollowing) {
		if(isFollowing) {
			followBtn.setText(GwtTeaming.getMessages().qViewFollowing());
			followBtn.setTitle(GwtTeaming.getMessages().qViewFollowingTitle());
			followBtn.setChecked(true);
		} else {
			followBtn.setText(GwtTeaming.getMessages().qViewFollow());
			followBtn.setTitle(GwtTeaming.getMessages().qViewFollowTitle());
			followBtn.setChecked(false);
		}
	}
	
	private void getUserStatus() {
		
		GetUserStatusCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback = new AsyncCallback<VibeRpcResponse>(){

			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetStatus(),
					binderId );
			}

			public void onSuccess( VibeRpcResponse response ) {
				UserStatus result = null;
				
				if ( response.getResponseData() != null )
					result = (UserStatus) response.getResponseData();
				
				if(result != null) {
					String description = result.getStatus();
					if(description != null && !description.equals("")){
						statusLabel.setText(description);
					} 
				}
			}
		};
		
		// Issue an ajax request to save the user status to the db.  rpcCallback will
		// be called when we get the response back.
		cmd = new GetUserStatusCmd( binderId );
		GwtClientHelper.executeCommand( cmd, rpcCallback );
	}
	
	private class MicroBlogClickHandler implements ClickHandler {
		
		String mbBinderId;
		
		public MicroBlogClickHandler(String binderId){
			this.mbBinderId = binderId;
		}
		
		public void onClick(ClickEvent event) {

			GetMicroBlogUrlCmd cmd;
			
			// Issue an ajax request to save the user status to the db.  rpcCallback will
			// be called when we get the response back.
			cmd = new GetMicroBlogUrlCmd( mbBinderId );
			GwtClientHelper.executeCommand( cmd, rpcCallback );
		}

		
		AsyncCallback<VibeRpcResponse> rpcCallback = new AsyncCallback<VibeRpcResponse>(){
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_QViewMicroBlog(),
					binderId );
				}

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

	private class ConferencingClickHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
			GetAddMeetingUrlCmd cmd;
			
			// Get the URL to the meeting start/schedule dialog and launch it in a new window
			cmd = new GetAddMeetingUrlCmd( binderId );
			GwtClientHelper.executeCommand( cmd,
					new AsyncCallback<VibeRpcResponse>() {
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
						public void onFailure(Throwable t) {
							GwtClientHelper.handleGwtRPCFailure(
								t,
								GwtTeaming.getMessages().rpcFailure_GetAddMeetingUrl(),
								binderId );
						}
					});
		}
	}
	
	/**
	 * Show this dialog.
	 */
	public void show()
	{
		// Is this dialog suppose to be modal
		if ( m_modal )
		{
			// Yes
			// If there is already a dialog visible then the glass panel is already visible.
			// We don't want 2 glass panels.
			if ( m_numDlgsVisible == 0 )
			{
				setGlassEnabled( true );
				setGlassStyleName( "n-Transparent-Black-Div" );
			}
		}
		
		if ( m_visible == false )
			++m_numDlgsVisible;
		
		m_visible = true;
		
		// Show this dialog.
		super.show();
		
		// Get the widget that should be given the focus when this dialog is displayed.
		m_focusWidget = getFocusWidget();
		
		// We need to set the focus after the dialog has been shown.  That is why we use a timer. 
		if ( m_focusWidget != null )
		{
			Scheduler.ScheduledCommand cmd;

			cmd = new Scheduler.ScheduledCommand()
			{
				public void execute()
				{
					if ( m_focusWidget != null )
						m_focusWidget.setFocus( true );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
	}// end show()
	
	/**
	 * Using this onPreviewNativeEvent to check if the mouse click is in the input widget 
	 */
	public void onPreviewNativeEvent(NativePreviewEvent previewEvent) {


		int eventType = previewEvent.getTypeInt();
		
		// We are only interested in mouse-down events.
		if ( eventType != Event.ONMOUSEDOWN )
			return;
		
		NativeEvent nativeEvent = previewEvent.getNativeEvent();
		//EventTarget target = event.getEventTarget();
		
		if ( isMouseOver(this, nativeEvent.getClientX(), nativeEvent.getClientY())) {
			return;
		} else {
			hide();
			return;
		}
	}
	
	/**
	 * Determine if the given coordinates are over this control.
	 */
	public boolean isMouseOver( Widget widget, int mouseX, int mouseY )
	{
		int left;
		int top;
		int width;
		int height;
		
		// Get the position and dimensions of this control.
		left = widget.getAbsoluteLeft() - widget.getElement().getOwnerDocument().getScrollLeft();
		top = widget.getAbsoluteTop() - widget.getElement().getOwnerDocument().getScrollTop();
		height = widget.getOffsetHeight();
		width = widget.getOffsetWidth();
		
		//Window.alert("mouseX: "+ mouseX + " mouseY: "+ mouseY + " left: "+ left + " top: "+ top + " height: "+ height + " width: "+ width + " ScrollTop: "+widget.getElement().getOwnerDocument().getScrollTop());
		
		// Is the mouse over this control?
		if ( mouseY >= top && mouseY <= (top + height) && mouseX >= left && (mouseX <= left + width) )
			return true;
		
		return false;
	}// end isMouseOver()
	
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
				public void onMouseOver(MouseOverEvent event) {
					if(labelStyle.equals("qView-action-following") ){
						panel.addStyleName("qView-action2");
					} else {
						label.addStyleName("qView-action2");
					}
				}});
			
			addMouseOutHandler(new MouseOutHandler(){
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
	 * @param autoHide
	 * @param modal
	 * @param pos
	 * @param pos2
	 * @param binderId
	 * @param userName
	 * @param element
	 * @param qvdClient
	 */
	public static void createAsync(
			final boolean autoHide,
			final boolean modal,
			final int pos,
			final int pos2,
			final String binderId,
			final String userName,
			final Element element,
			final GwtQuickViewDlgClient qvdClient) {
		GWT.runAsync(GwtQuickViewDlg.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				GwtQuickViewDlg qvd = new GwtQuickViewDlg(autoHide, modal, pos, pos2, binderId, userName, element);
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

package org.kablink.teaming.gwt.client.profile.widgets;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.presence.InstantMessageClickHandler;
import org.kablink.teaming.gwt.client.presence.PresenceControl;
import org.kablink.teaming.gwt.client.profile.ProfileCategory;
import org.kablink.teaming.gwt.client.profile.ProfileClientUtil;
import org.kablink.teaming.gwt.client.profile.ProfileInfo;
import org.kablink.teaming.gwt.client.profile.UserStatus;
import org.kablink.teaming.gwt.client.service.GwtRpcService;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.ActionRequestor;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.TeamingAction;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
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
public class GwtQuickViewDlg extends DlgBox implements ActionRequestor, NativePreviewHandler{

	private String binderId;
	private Grid grid;
	
	private ProfileActionWidget workspaceBtn;
	private ProfileActionWidget profileBtn;
	private ProfileActionWidget conferenceBtn;
	private QuickViewAction followBtn;
	
	private Label statusLabel;
	
	private String userName;
	private ActionHandler actionHandler;
	private Image avatar;
	private Anchor miniBlogA;
	private ProfileActionWidget instantMessageBtn;
	private Element clientElement;
	
	public GwtQuickViewDlg(boolean autoHide, boolean modal, int pos,
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

		Panel arrow = new FlowPanel();
		arrow.addStyleName("qView-arrow");
		panel.add(arrow);
		
		Image img = new Image(GwtTeaming.getImageBundle().arrowTrans50Left());
		arrow.add(img);
		
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
		FlowPanel pictureDiv = new FlowPanel();
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
		profileBtn.addClickHandler(new WorkSpaceActionHandler(true));
		
		workspaceBtn = new ProfileActionWidget(GwtTeaming.getMessages().qViewWorkspace(),
										 GwtTeaming.getMessages().qViewWorkspaceTitle(),
										 "qView-a",	"qView-action");
		workspaceBtn.addClickHandler(new WorkSpaceActionHandler(false));
		
		conferenceBtn = new ProfileActionWidget(GwtTeaming.getMessages().qViewConference(),
										GwtTeaming.getMessages().qViewConferenceTitle(),
										"qView-a", "qView-action");
		
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

		// Default button to not visible
		instantMessageBtn.setVisible(false);

		// Check if presence is enabled; set the button visible if it is.
		GwtTeaming.getRpcService().isPresenceEnabled(new AsyncCallback<Boolean>() {
				public void onFailure(Throwable t) {
					instantMessageBtn.setVisible(false);
				}
				public void onSuccess(Boolean enabled) {
					instantMessageBtn.setVisible(enabled);
				}
			});

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
				GwtTeaming.getRpcService().trackBinder( binderId, new AsyncCallback<Boolean>()
				{
					public void onFailure( Throwable t )
					{
						Window.alert( t.toString() );
					}//end onFailure()
					
					public void onSuccess( Boolean success )
					{
						updateFollowingButton(true);
					}// end onSuccess()
				});
			}

			private void unFollowAction() {
				GwtTeaming.getRpcService().untrackPerson( binderId, new AsyncCallback<Boolean>()
				{
					public void onFailure( Throwable t )
					{
						Window.alert( t.toString() );
					}//end onFailure()
					
					public void onSuccess( Boolean success )
					{
						updateFollowingButton(false);
					}// end onSuccess()
				});
			}
		};
		followBtn.addClickHandler(clickHandler);
		
		panel.add(profileBtn);
		panel.add(workspaceBtn);
		panel.add(conferenceBtn);
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
				.closeCircle16());
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

		getUserStatus();
		createProfileInfoSections();
		
		updateFollowingStatus();
	}// end init()

	
	/**
	 * Create the Profile Heading Sections and their associated Profile
	 * Attributes
	 * 
	 * @param profileRequestInfo
	 */
	private void createProfileInfoSections() {

		GwtRpcServiceAsync gwtRpcService;

		// create an async callback to handle the result of the request to get
		// the state:
		AsyncCallback<ProfileInfo> callback = new AsyncCallback<ProfileInfo>() {
			public void onFailure(Throwable t) {
				// display error
				Window.alert("Error: " + t.getMessage());
			}

			public void onSuccess(ProfileInfo profile) {

				String url = profile.getPictureUrl();
				if(url != null && !url.equals("")){
					avatar.setUrl(profile.getPictureUrl());
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
				
				int count = profile.getCategories().size();
				int row = 0;
				for (int i = 0; i < count; i++) {

					ProfileCategory cat = profile.get(i);
					if (cat.getName().equals("profileSidePanelView")) {
						continue;
					}

					row = ProfileClientUtil.createProfileInfoSection(cat, grid,	row, false, false);
				}

			}
		};

		gwtRpcService = (GwtRpcServiceAsync) GWT.create(GwtRpcService.class);
		gwtRpcService.getQuickViewInfo(binderId, callback);
	}

	
	
	/**
	 * This workSpaceActionHandler handles the actions on the profile button or the workspace button.
	 * 
	 */
	private class WorkSpaceActionHandler implements ClickHandler {

		boolean showProfile = false;

		public WorkSpaceActionHandler(boolean profile){
			this.showProfile = profile;
		}
		
		public void onClick(ClickEvent event) {
			GwtRpcServiceAsync rpcService = GwtTeaming.getRpcService();
			rpcService.getBinderPermalink( binderId, new AsyncCallback<String>()
			{
				public void onFailure( Throwable t ) {
					Window.alert( t.toString() );
				}//end onFailure()
				
				public void onSuccess( String binderUrl )
				{
					OnSelectBinderInfo osbInfo;
					
					if(showProfile){
						binderUrl = GwtClientHelper.appendUrlParam( binderUrl, "operation", "showProfile" );
					} else {
						binderUrl = GwtClientHelper.appendUrlParam( binderUrl, "operation", "showWorkspace" );
					}
					osbInfo = new OnSelectBinderInfo( binderId, binderUrl, false, Instigator.OTHER );
					
					actionHandler.handleAction(TeamingAction.SELECTION_CHANGED, osbInfo );
					
					hide();
				}// end onSuccess()
			});// end AsyncCallback()
		}
	}

	public void addActionHandler(ActionHandler actionHandler) {
		this.actionHandler = actionHandler;
	}
	
	/**
	 * Checks to see if the current User is following this person
	 * @return
	 */
	private void updateFollowingStatus() {
		
		GwtTeaming.getRpcService().isPersonTracked( binderId, new AsyncCallback<Boolean>()
				{
					public void onFailure( Throwable t )
					{
						Window.alert( t.toString() );
					}//end onFailure()
					
					public void onSuccess( Boolean success )
					{
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
		
		AsyncCallback<UserStatus> rpcCallback = new AsyncCallback<UserStatus>(){

			public void onFailure(Throwable t) {
				String errMsg;
				String cause = "";
				GwtTeamingMessages	messages = GwtTeaming.getMessages();
				
				if ( t instanceof GwtTeamingException )	{
					ExceptionType type;
				
					// Determine what kind of exception happened.
					type = ((GwtTeamingException)t).getExceptionType();
					if ( type == ExceptionType.ACCESS_CONTROL_EXCEPTION )
						cause = messages.errorAccessToFolderDenied( binderId );
					else if ( type == ExceptionType.NO_BINDER_BY_THE_ID_EXCEPTION )
						cause = messages.errorFolderDoesNotExist( binderId );
					else
						cause = messages.errorUnknownException();
				}
				else {
					cause = t.getLocalizedMessage();
					if ( cause == null )
						cause = t.toString();
				}
				
				errMsg = messages.getStatusRPCFailed( cause );
				Window.alert( errMsg );
			}

			public void onSuccess(UserStatus result) {
				if(result != null) {
					String description = result.getStatus();
					if(description != null && !description.equals("")){
						statusLabel.setText(description);
					} 
				}
			}
		};
		
		GwtRpcServiceAsync rpcService = GwtTeaming.getRpcService();
			
		// Issue an ajax request to save the user status to the db.  rpcCallback will
		// be called when we get the response back.
		rpcService.getUserStatus(binderId, rpcCallback );
	}
	
	private class MicroBlogClickHandler implements ClickHandler {
		
		String mbBinderId;
		
		GwtRpcServiceAsync rpcService = GwtTeaming.getRpcService();
		
		public MicroBlogClickHandler(String binderId){
			this.mbBinderId = binderId;
		}
		
		public void onClick(ClickEvent event) {

			// Issue an ajax request to save the user status to the db.  rpcCallback will
			// be called when we get the response back.
			rpcService.getMicrBlogUrl(mbBinderId, rpcCallback );
		}

		
		AsyncCallback<String> rpcCallback = new AsyncCallback<String>(){
			public void onFailure(Throwable t) {
					String errMsg;
					String cause = "";
					GwtTeamingMessages	messages = GwtTeaming.getMessages();
					
					if ( t instanceof GwtTeamingException )	{
						ExceptionType type;
					
						// Determine what kind of exception happened.
						type = ((GwtTeamingException)t).getExceptionType();
						if ( type == ExceptionType.ACCESS_CONTROL_EXCEPTION )
							cause = messages.errorAccessToFolderDenied( binderId );
						else if ( type == ExceptionType.NO_BINDER_BY_THE_ID_EXCEPTION )
							cause = messages.errorFolderDoesNotExist( binderId );
						else
							cause = messages.errorUnknownException();
					}
					else {
						cause = t.getLocalizedMessage();
						if ( cause == null )
							cause = t.toString();
					}
					
					errMsg = messages.qViewMicroBlogRPCFailed( cause );
					Window.alert( errMsg );
				}

				public void onSuccess(String url) {
					if(GwtClientHelper.hasString(url)) {
						//GwtClientHelper.jsLoadUrlInContentFrame(url);
						GwtClientHelper.jsLaunchUrlInWindow(url, GwtTeaming.getMessages().qViewMicroBlog(), 500, 500);
						hide();
					}
				}
			};
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
			Command cmd;
			
			cmd = new Command()
			{
			     	/**
			     	 * 
			     	 */
			      public void execute()
			      {
						if ( m_focusWidget != null )
							m_focusWidget.setFocus( true );
			      }
			};
			DeferredCommand.addCommand( cmd );
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

}
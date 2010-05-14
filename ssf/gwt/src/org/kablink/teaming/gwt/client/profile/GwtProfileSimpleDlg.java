package org.kablink.teaming.gwt.client.profile;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

public class GwtProfileSimpleDlg extends DlgBox implements ActionRequestor {

	private String binderId;
	private Grid grid;
	private QuickViewAction workspaceBtn;
	private QuickViewAction profileBtn;
	private QuickViewAction conferenceBtn;
	private QuickViewAction followBtn;
	private Label followingStat;
	private Label followersStat;
	private Label entriesStat;
	private Label statusLabel;
	private String userName;
	private String userId;
	private ActionHandler actionHandler;
	private Image avatar;
	private Anchor miniBlogA;
	
	public GwtProfileSimpleDlg(boolean autoHide, boolean modal, int pos,
			int pos2, String binderId, String userName) {
		super(autoHide, modal, pos, pos2);

		this.binderId = binderId;
		this.userName = userName;

		createAllDlgContent("Test Dialog", null, null, null);
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

		FlowPanel mainPanel = new FlowPanel();
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

		rightPanel.add(createStatsPanel());

		return mainPanel;
	}

	private Panel createPhotoPanel() {
		FlowPanel panel = new FlowPanel();
		panel.addStyleName("qViewPhoto");

		avatar = new Image();
		panel.add(avatar);

		return panel;
	}

	private Panel createActionsPanel() {

		ClickHandler clickHandler;
		FlowPanel panel = new FlowPanel();
		
		profileBtn = new QuickViewAction("Profile", "qView-a", "qView-action");
		profileBtn.addClickHandler(new WorkSpaceActionHandler(true));
		
		workspaceBtn = new QuickViewAction("Workspace", "qView-a",
				"qView-action");
		workspaceBtn.addClickHandler(new WorkSpaceActionHandler(false));
		
		conferenceBtn = new QuickViewAction("Conference", "qView-a", "qView-action");

		followBtn = new QuickViewAction("Following", "qView-a",
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
		
		miniBlogA = new Anchor("Micro-Blog");
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

	private Panel createStatsPanel() {
		FlowPanel panel = new FlowPanel();
		panel.addStyleName("qViewStats");

		InlineLabel span;
		Label label;

		span = new InlineLabel();
		panel.add(span);

		label = new Label("Following:");
		label.addStyleName("qViewStatsLabel");
		span.getElement().appendChild(label.getElement());

		followingStat = new Label();
		followingStat.addStyleName("qViewStatsLabel");
		followingStat.addStyleName("bold");
		span.getElement().appendChild(followingStat.getElement());

		span = new InlineLabel();
		panel.add(span);

		label = new Label("Followers:");
		label.addStyleName("qViewStatsLabel");
		span.getElement().appendChild(label.getElement());

		followersStat = new Label();
		followersStat.addStyleName("qViewStatsLabel");
		followersStat.addStyleName("bold");
		span.getElement().appendChild(followersStat.getElement());

		span = new InlineLabel();
		panel.add(span);

		label = new Label("Entries");
		label.addStyleName("qViewStatsLabel");
		span.getElement().appendChild(label.getElement());

		entriesStat = new Label();
		entriesStat.addStyleName("qViewStatsLabel");
		entriesStat.addStyleName("bold");
		span.getElement().appendChild(entriesStat.getElement());

		return panel;
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

		Label userName = new Label(this.userName);
		userName.addStyleName("qViewTitle");
		panel.add(userName);

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FocusWidget getFocusWidget() {

		return null;
	}

	/**
	 * Initialize the controls in the dialog with the values from the given
	 * object. Currently there is nothing to initialize.
	 */
	public void init(Object props) {

		getUserStatus();
		createProfileInfoSections();

		followingStat.setText("25");
		followersStat.setText("231");
		entriesStat.setText("1044");
		
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

				userId = profile.getUserId();
				avatar.setUrl(profile.getPictureUrl());
				
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

	private class QuickViewAction extends Anchor {

		private Label label;
		private Image img;
		private boolean isChecked;

		public QuickViewAction(String text, String anchorStlyeName,
				String labelStyleName) {
			super();

			addStyleName(anchorStlyeName);

			label = new Label(text);
			label.addStyleName(labelStyleName);

			getElement().appendChild(label.getElement());
		}

		public boolean isChecked() {
			return isChecked;
		}

		public void setChecked(boolean checked) {
			if(checked) {
				isChecked = true;
				label.removeStyleName("qView-action");
				label.addStyleName("qView-action-following");
				
				if(img == null){
					img = new Image(GwtTeaming.getImageBundle().check12());
					getElement().appendChild(img.getElement());
					img.addStyleName("qView-action-img");
				} else {
					img.setVisible(true);
				}
			} else {
				isChecked = false;
				label.removeStyleName("qView-action-following");
				label.addStyleName("qView-action");

				if(img != null){
					img.setVisible(false);
				}
			}
		}
		
		public void setText(String text) {
			label.setText(text);
		}
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
		
		@Override
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
						binderUrl = GwtClientHelper.appendUrlParam( binderUrl, "operation", "profile" );
					}
					osbInfo = new OnSelectBinderInfo( binderId, binderUrl, false, Instigator.OTHER );
					
					actionHandler.handleAction(TeamingAction.SELECTION_CHANGED, osbInfo );
					
					hide();
				}// end onSuccess()
			});// end AsyncCallback()
		}
	}

	@Override
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
			followBtn.setText("Following");
			followBtn.setChecked(true);
		} else {
			followBtn.setText("Follow");
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
				
				errMsg = messages.getBrandingRPCFailed( cause );
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
		
		@Override
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
					
					errMsg = messages.getBrandingRPCFailed( cause );
					Window.alert( errMsg );
				}

				public void onSuccess(String url) {
					if(GwtClientHelper.hasString(url)) {
						//GwtClientHelper.jsLoadUrlInContentFrame(url);
						GwtClientHelper.jsLaunchUrlInWindow(url, "Micro-blog", 500, 500);
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
				//setGlassEnabled( true );
				//setGlassStyleName( "n-Transparent-Black-Div" );
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
	
}
package org.kablink.teaming.gwt.client.profile.widgets;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.presence.PresenceControl;
import org.kablink.teaming.gwt.client.profile.ProfileAttribute;
import org.kablink.teaming.gwt.client.profile.ProfileCategory;
import org.kablink.teaming.gwt.client.profile.ProfileRequestInfo;
import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.TeamingAction;

import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

public class ProfileMainPanel extends Composite implements SubmitCompleteHandler {

	ProfileRequestInfo profileRequestInfo;
	
	private FlexTable grid;
	private FlowPanel mainPanel;
	private FlowPanel titlePanel;
	private ProfileFollowingWidget followingAnchor;
	private boolean isEditable = false;
	private Anchor edit;
	private int m_uniqueId = 1;

	private ActionTrigger actionTrigger;

	/**
	 * Constructor
	 * 
	 * @param profileRequestInfo
	 */
	public ProfileMainPanel(final ProfileRequestInfo profileRequestInfo, ActionTrigger trigger) {

		this.profileRequestInfo = profileRequestInfo;
		this.actionTrigger = trigger;
		
		// create the main panel
		mainPanel = new FlowPanel();
		mainPanel.setStyleName("profile-Content-c");

		// add user's title to the profile div
		createTitleArea();

		// add the actions area to the title div
		createActionsArea();

		// ...its content panel...
		createContentPanel();

		// All composites must call initWidget() in their constructors.
		initWidget(mainPanel);
	}

	/**
	 * Create the the main content panel that will hold all of the attributes
	 * name value pairs
	 */
	private void createContentPanel() {
		grid = new FlexTable();
		grid.setWidth("100%");
		grid.setCellSpacing(0);
		grid.setCellPadding(0);
		//grid.resizeColumns(3);
		grid.setStyleName("sectionTable");
		mainPanel.add(grid);
	}

	/**
	 * Create the Title Area that contains the user's Name
	 */
	private void createTitleArea() {

		// create a title div for the user title and actionable items
		titlePanel = new FlowPanel();
		titlePanel.addStyleName("profile-title-area");
		mainPanel.add(titlePanel);

		String userName = profileRequestInfo.getUserName();
		String url = profileRequestInfo.getAdaptedUrl();
		
		Anchor anchor = new Anchor(userName, url);
		anchor.setTitle(GwtTeaming.getMessages().qViewWorkspaceTitle());
		
		anchor.addStyleName("profile-title");
		titlePanel.add(anchor);

		PresenceControl presence = new PresenceControl(profileRequestInfo.getBinderId(), true, true, true);

		Anchor workspace = new Anchor( GwtTeaming.getMessages().qViewWorkspace(), url);
		workspace.setTitle(GwtTeaming.getMessages().qViewWorkspaceTitle());
		
		workspace.addStyleName("profile-workspace-link");

		titlePanel.add(workspace);
		titlePanel.add(presence);
	}

	/**
	 * Create the Actions Area, this is an area that should hold the additional
	 * actions any user can perform like follow or unfollow a user.
	 * 
	 */
	private void createActionsArea() {
		
		FlowPanel actionsPanel = new FlowPanel();
		actionsPanel.addStyleName("profile-actions");
		titlePanel.add(actionsPanel);
		
		//create the following action
		followingAnchor = createFollowingAction(actionsPanel);
		
		//create the edit action
		edit = createEditAction(actionsPanel);

		// Add a mouse-over handler
		MouseOverHandler mouseOverHandler = new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent event) {
				Widget widget;

				widget = (Widget) event.getSource();
				widget.removeStyleName("subhead-control-bg1");
				widget.addStyleName("subhead-control-bg2");
			}// end onMouseOver()
		};
		edit.addMouseOverHandler(mouseOverHandler);
		

		// Add a mouse-out handler
		MouseOutHandler mouseOutHandler = new MouseOutHandler() {
			public void onMouseOut(MouseOutEvent event) {
				Widget widget;

				// Remove the background color we added to the anchor when the
				// user moved the mouse over the anchor.
				widget = (Widget) event.getSource();
				removeMouseOverStyles(widget);
			}// end onMouseOut()
		};
		edit.addMouseOutHandler(mouseOutHandler);
		
		updateFollowingStatus();
	}


	private ProfileFollowingWidget createFollowingAction(FlowPanel panel){

		FlowPanel followPanel = new FlowPanel();
		followPanel.addStyleName("profile-action");
		panel.add(followPanel);
		
		ProfileFollowingWidget fAnchor = new ProfileFollowingWidget(GwtTeaming.getMessages().qViewFollowing(),
																	GwtTeaming.getMessages().qViewFollowingTitle());
		fAnchor.setVisible(showFollowButton());
		followPanel.add(fAnchor);
		
		fAnchor.addStyleName("editBrandingLink");
		fAnchor.addStyleName("editBrandingAdvancedLink");
		fAnchor.addStyleName("roundcornerSM");
		fAnchor.addStyleName("subhead-control-bg1");
		
		fAnchor.addClickHandler(new ActionClickHandler("FollowId"));;
		
		return fAnchor;
	}
	
	private Anchor createEditAction(FlowPanel panel) {
		
		FlowPanel actions = new FlowPanel();
		actions.addStyleName("profile-action");
		panel.add(actions);

		Anchor eAnchor = new Anchor(GwtTeaming.getMessages().profileEdit());
		eAnchor.setTitle(GwtTeaming.getMessages().profileEditTitle());
		
		eAnchor.addStyleName("editBrandingLink");
		eAnchor.addStyleName("editBrandingAdvancedLink");
		eAnchor.addStyleName("roundcornerSM");
		eAnchor.addStyleName("subhead-control-bg1");
		eAnchor.setVisible(showEditButton());

		actions.add(eAnchor);
		eAnchor.addClickHandler(new ActionClickHandler("EditId"));

		return eAnchor;
	}
	
	/**
	 * Use to determine if should show the follow button
	 * @return
	 */
	private boolean showFollowButton() {
		return !profileRequestInfo.isOwner();
	}
	
	

	/**
	 * Create the Profile Main Content Section, this creates the grid that
	 * should hold all of the user attribute name value pairs
	 * 
	 * @param cat
	 * @param grid
	 * @param rowCount
	 * @return
	 */
	public void createProfileInfoSection(final ProfileCategory cat, FlexTable grid) {

		int row = grid.getRowCount();
		
		Label sectionHeader = new Label(cat.getTitle());
		sectionHeader.setStyleName("sectionHeading");

		grid.setWidget(row, 0, sectionHeader);
		grid.getFlexCellFormatter().setColSpan(row, 0, 2);

		// remove the bottom border from the section heading titles
		grid.getFlexCellFormatter().setStyleName(row, 0, "sectionHeadingRBB");
		
		if(cat.getName().equals("profileHeadingPhotosAndImagesView")) {
			if(profileRequestInfo.isModifyAllowed()){
				buildUploadImage(grid);
			}
		}
		
		for (ProfileAttribute attr : cat.getAttributes()) {

			Label title = new Label(attr.getTitle() + ":");
			title.setStyleName("attrLabel");
			Widget value = new ProfileAttributeWidget(attr, isEditable).getWidget();

			if(!attr.getDataName().equals("picture")) {
				row = grid.getRowCount();
				grid.setWidget(row, 0, title);
				grid.setWidget(row, 1, value);
				grid.getFlexCellFormatter().setWidth(row, 1, "70%");
				grid.getFlexCellFormatter().setHorizontalAlignment(row, 1,
						HasHorizontalAlignment.ALIGN_LEFT);
			} else {
				//This must be a picture attribute
				value = new ProfileAvatarArea(attr, isEditable).getWidget();
				row = grid.getRowCount();
				grid.setWidget(row, 0, value);
				grid.getFlexCellFormatter().setColSpan(row, 0, 2);
				grid.getFlexCellFormatter().setHorizontalAlignment(row, 0,
						HasHorizontalAlignment.ALIGN_LEFT);
			}
		}
	}

	private void buildUploadImage(FlexTable grid) {
		
		FlowPanel panel = new FlowPanel();
		panel.addStyleName("profileUploadPanel");
		panel.setWidth("100%");
		
		//create a form element in order to upload a new photo
		final FormPanel formPanel = new FormPanel();
		panel.add(formPanel);
		
		formPanel.setEncoding( FormPanel.ENCODING_MULTIPART );
		formPanel.setMethod( FormPanel.METHOD_POST );
		formPanel.addSubmitCompleteHandler( this );
		
		FileUpload fileUpload = new FileUpload();
		fileUpload.addStyleName("profileUpload");
		String name = "picture";
		fileUpload.setName( name );
		InputElement input = fileUpload.getElement().cast();
		input.setSize(40);
		input.setId(name);
		
		formPanel.add(fileUpload);
		formPanel.getElement().setId("form1");
		formPanel.setAction( profileRequestInfo.getModifyUrl() + "&okBtn=1" );
	
		final Anchor uploadBtn = new Anchor("Upload");
		uploadBtn.setTitle("Select to upload photo");
		uploadBtn.addClickHandler( new ClickHandler(){
				public void onClick(ClickEvent event){
					formPanel.submit();
				}
			});
		
		uploadBtn.setVisible(false);
		uploadBtn.addStyleName("profile-anchor");
		uploadBtn.addStyleName("profileUploadButton");
		//add the upload button to the div
		panel.add(uploadBtn);
		
		//if the file upload changes, add the upload button
		fileUpload.addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event) {
				uploadBtn.setVisible(true);
			}
		});
		
		//panel.add(upload);
		int row = grid.getRowCount();
		grid.setWidget(row, 0, panel );
		grid.getFlexCellFormatter().setColSpan(row, 0 , 2);
		// remove the bottom border from the section heading titles
		grid.getFlexCellFormatter().setStyleName(row, 0, "sectionHeadingRBB");
	}
	
	/**
	 * Set the Profile Category which contains the heading label and all of the
	 * child Profile Attributes
	 * 
	 * @param cat
	 */
	public void setCategory(ProfileCategory cat) {
		createProfileInfoSection(cat, grid);
	}

	/**
	 * Remove the styles that were added to the given widget when the user moved
	 * the mouse over the widget.
	 */
	private void removeMouseOverStyles(Widget widget) {
		widget.removeStyleName("subhead-control-bg2");
		widget.addStyleName("subhead-control-bg1");
	}// end removeMouseOverStyles()

	/*
	 * This method will be called to track the current binder.
	 * 
	 * Implements the TRACK_BINDER teaming action.
	 */
	private void followPerson() {
		
		actionTrigger.triggerAction(TeamingAction.TRACK_BINDER);
		
//		GwtTeaming.getRpcService().trackBinder(
//				profileRequestInfo.getBinderId(), new AsyncCallback<Boolean>() {
//					public void onFailure(Throwable t) {
//						Window.alert(t.toString());
//					}// end onFailure()
//
//					public void onSuccess(Boolean success) {
//
//						updateFollowingStatus();
//						
//						// It's overkill to force a full context reload, which
//						// this does, but it's the only way right now to ensure
//						// the What's New tab and other information gets fully
//						// refreshed.
//						// contextLoaded( profileRequestInfo.getBinderId(),
//						// Instigator.OTHER );
//						GwtClientHelper.jsContextLoaded( profileRequestInfo.getBinderId() );
//					}// end onSuccess()
//				});
	}
	
	/*
	 * This method will be called to remove the tracking on the person
	 * whose workspace is the current binder.
	 * 
	 * Implements the UNTRACK_PERSON teaming action.
	 */
	private void unfollowPerson() {
		
		actionTrigger.triggerAction(TeamingAction.UNTRACK_PERSON);
		
//		GwtTeaming.getRpcService().untrackPerson( profileRequestInfo.getBinderId(), new AsyncCallback<Boolean>()
//		{
//			public void onFailure( Throwable t )
//			{
//				Window.alert( t.toString() );
//			}//end onFailure()
//			
//			public void onSuccess( Boolean success )
//			{
//				updateFollowingStatus();
//
//				// It's overkill to force a full context reload, which
//				// this does, but it's the only way right now to ensure
//				// the What's New tab and other information gets fully
//				// refreshed.
//				GwtClientHelper.jsContextLoaded( profileRequestInfo.getBinderId() );
//			}// end onSuccess()
//		});
	}
	
	/**
	 * Checks to see if the current User is following this person
	 * @return
	 */
	private void updateFollowingStatus() {
		
		GwtTeaming.getRpcService().isPersonTracked( profileRequestInfo.getBinderId(), new AsyncCallback<Boolean>()
				{
					public void onFailure( Throwable t )
					{
						Window.alert( t.toString() );
					}//end onFailure()
					
					public void onSuccess( Boolean success )
					{
						if(success.booleanValue()) {
							updateFollowingButton(true);
						} else {
							updateFollowingButton(false);
						}
					}// end onSuccess()
				});
	}


	public void setEditable(boolean edit) {
		isEditable  = edit;
	}
	
	public class ActionClickHandler implements ClickHandler {

		String handlerId = null;
		public ActionClickHandler(String id) {
			handlerId = id;
		}

		public void onClick(ClickEvent event) {
			if (handlerId.equals("EditId")) {
				String url = profileRequestInfo.getModifyUrl();
				GwtClientHelper.jsLaunchUrlInWindow(url, "Modify Entry", 800, 800);
			} else if(handlerId.equals("FollowId")) {
				if(followingAnchor.isChecked()) {
					unfollowPerson();
				} else {
					followPerson();
				}
			}
		}
	}

	/**
	 * Use to determine if should show the edit button
	 * 
	 * @return true if owns this profile or is binderAdmin
	 */
	private boolean showEditButton() {
		return profileRequestInfo.isBinderAdmin()
				|| profileRequestInfo.isModifyAllowed();
	}

	/**
	 * Update the follow button 
	 * @param isFollowing
	 */
	private void updateFollowingButton(boolean isFollowing) {
		if(isFollowing) {
			followingAnchor.setText(GwtTeaming.getMessages().qViewFollowing());
			followingAnchor.setTitle(GwtTeaming.getMessages().qViewFollowingTitle());
			followingAnchor.setChecked(true);
		} else {
			followingAnchor.setText(GwtTeaming.getMessages().qViewFollow());
			followingAnchor.setTitle(GwtTeaming.getMessages().qViewFollowTitle());
			followingAnchor.setChecked(false);
		}
	}
	
	private class ProfileFollowingWidget extends Anchor {

		private Label label;
		private Image img;
		private boolean isChecked;
		private FlowPanel panel;

		public ProfileFollowingWidget(String text, String title) {
			super();

			panel = new FlowPanel();

			img = new Image(GwtTeaming.getImageBundle().check12());
			img.addStyleName("qView-follow-img");
			img.setVisible(false);
			panel.add(img);

			label = new Label(text);
			label.addStyleName("qView-follow-label");
			label.setTitle(title);
			panel.add(label);
			
			getElement().appendChild(panel.getElement());
			
			
			// Add a mouse-over handler
			addMouseOverHandler( new MouseOverHandler() {
				public void onMouseOver(MouseOverEvent event) {
					if(!isChecked()){
						removeStyleName("subhead-control-bg1");
						addStyleName("subhead-control-bg2");
					} else {
						removeStyleName("profile-selected-bg");
						addStyleName("subhead-control-bg2");
					}
				}// end onMouseOver()
			});

			// Add a mouse-out handler
			addMouseOutHandler( new MouseOutHandler() {
				public void onMouseOut(MouseOutEvent event) {
					if(!isChecked()){
						removeStyleName("subhead-control-bg2");
						addStyleName("subhead-control-bg1");
					} else {
						removeStyleName("subhead-control-bg2");
						addStyleName("profile-selected-bg");
					}
				}// end onMouseOut()
			});
		}

		public boolean isChecked() {
			return isChecked;
		}

		public void setChecked(boolean checked) {
			if(checked) {
				isChecked = true;
				if(img != null){
					removeStyleName("subhead-control-bg1");
					addStyleName("profile-selected-bg");
					img.setVisible(true);
				}
			} else {
				isChecked = false;
				if(img != null){
					removeStyleName("profile-selected-bg");
					addStyleName("subhead-control-bg1");
					img.setVisible(false);
				}
			}
		}
		
		public void setText(String text) {
			label.setText(text);
		}
		
		public void setTitle(String text) {
			label.setTitle(text);
		}
	}

	public void onSubmitComplete(SubmitCompleteEvent event) {
		// TODO Auto-generated method stub
		
	}
}
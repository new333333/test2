package org.kablink.teaming.gwt.client.profile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.presence.PresenceControl;
import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.ActionRequestor;
import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.TeamingAction;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ProfileMainPanel extends Composite {

	ProfileRequestInfo profileRequestInfo;
	
	private Grid grid;
	private int row = 0;
	private FlowPanel mainPanel;
	private FlowPanel titlePanel;
	private ProfileFollowingWidget followingAnchor;
	private boolean isEditable = false;
	private Anchor edit;

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
		grid = new Grid();
		grid.setWidth("100%");
		grid.setCellSpacing(0);
		grid.setCellPadding(0);
		grid.resizeColumns(3);
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
	public int createProfileInfoSection(final ProfileCategory cat, Grid grid,
			int rowCount) {
		int row = rowCount;

		Label sectionHeader = new Label(cat.getTitle());
		sectionHeader.setStyleName("sectionHeading");

		grid.insertRow(row);
		grid.setWidget(row, 0, sectionHeader);

		// remove the bottom border from the section heading titles
		grid.getCellFormatter().setStyleName(row, 0, "sectionHeadingRBB");
		grid.getCellFormatter().setStyleName(row, 1, "sectionHeadingRBB");
		grid.getCellFormatter().setStyleName(row, 2, "sectionHeadingRBB");
		row = row + 1;

		for (ProfileAttribute attr : cat.getAttributes()) {

			Label title = new Label(attr.getTitle() + ":");
			title.setStyleName("attrLabel");
			Widget value = new ProfileAttributeWidget(attr, isEditable).getWidget();

			grid.insertRow(row);
			grid.setWidget(row, 0, title);
			grid.setWidget(row, 1, value);
			grid.getCellFormatter().setWidth(row, 1, "70%");
			grid.getCellFormatter().setHorizontalAlignment(row, 1,
					HasHorizontalAlignment.ALIGN_LEFT);

			row = row + 1;
		}

		return row;
	}

	/**
	 * Set the Profile Category which contains the heading label and all of the
	 * child Profile Attributes
	 * 
	 * @param cat
	 */
	public void setCategory(ProfileCategory cat) {
		row = createProfileInfoSection(cat, grid, row);
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
}
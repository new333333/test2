package org.kablink.teaming.gwt.client.profile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.ActionRequestor;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ProfileMainPanel extends Composite implements ActionRequestor {

	private List<ActionHandler> m_actionHandlers = new ArrayList<ActionHandler>();
	ProfileRequestInfo profileRequestInfo;
	private Grid grid;
	private int row = 0;
	private FlowPanel mainPanel;
	private FlowPanel titlePanel;
	private Anchor followingAnchor;
	private boolean isFollowing;
	private boolean isEditable = false;

	/**
	 * Constructor
	 * 
	 * @param profileRequestInfo
	 */
	public ProfileMainPanel(final ProfileRequestInfo profileRequestInfo) {

		this.profileRequestInfo = profileRequestInfo;

		// create the main panel
		mainPanel = new FlowPanel();
		mainPanel.setStyleName("profile-Content-c");

		// add user's title to the profile div
		createTitleArea();

		// add the actions area to the title div
		createActionsArea();

		// ...its content panel...
		createContentPanel();

		//Register with GwtMainPage, so we can fire an event
		GwtClientHelper.jsRegisterActionHandler(this);
		
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
		anchor.addStyleName("profile-title");
		titlePanel.add(anchor);

		Anchor workspace = new Anchor("Workspace", url);
		workspace.addStyleName("profile-workspace-link");
		titlePanel.add(workspace);
	}

	/**
	 * Create the Actions Area, this is an area that should hold the additional
	 * actions any user can perform like follow or unfollow a user.
	 * 
	 */
	private void createActionsArea() {

		ClickHandler clickHandler;
		MouseOverHandler mouseOverHandler;
		MouseOutHandler mouseOutHandler;

		FlowPanel actionPanel = new FlowPanel();
		actionPanel.addStyleName("profile-follow");

		titlePanel.add(actionPanel);

		followingAnchor = new Anchor("Follow");
		followingAnchor.setVisible(showFollowButton());
		actionPanel.add(followingAnchor);
		followingAnchor.addStyleName("editBrandingLink");
		followingAnchor.addStyleName("editBrandingAdvancedLink");
		followingAnchor.addStyleName("roundcornerSM");
		followingAnchor.addStyleName("subhead-control-bg1");

		// Add a clickhandler to the "advanced" link. When the user clicks on
		// the link we
		// will invoke the "edit advanced branding" dialog.
		clickHandler = new ClickHandler() {
			/**
			 * Invoke the "edit advanced branding" dialog
			 */
			public void onClick(ClickEvent event) {
				Anchor anchor;

				anchor = (Anchor) event.getSource();

				if(isFollowing()) {
					unfollowPerson();
				} else {
					followPerson();
				}
			}// end onClick()
		};
		followingAnchor.addClickHandler(clickHandler);

		// Add a mouse-over handler
		mouseOverHandler = new MouseOverHandler() {
			/**
			 * 
			 */
			public void onMouseOver(MouseOverEvent event) {
				Widget widget;

				widget = (Widget) event.getSource();
				widget.removeStyleName("subhead-control-bg1");
				widget.addStyleName("subhead-control-bg2");
			}// end onMouseOver()
		};
		followingAnchor.addMouseOverHandler(mouseOverHandler);

		// Add a mouse-out handler
		mouseOutHandler = new MouseOutHandler() {
			/**
			 * 
			 */
			public void onMouseOut(MouseOutEvent event) {
				Widget widget;

				// Remove the background color we added to the anchor when the
				// user moved the mouse over the anchor.
				widget = (Widget) event.getSource();
				removeMouseOverStyles(widget);
			}// end onMouseOut()
		};
		followingAnchor.addMouseOutHandler(mouseOutHandler);
		
		updateFollowingStatus();
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
		
		triggerAction(TeamingAction.TRACK_BINDER);
		
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
		
		triggerAction(TeamingAction.UNTRACK_PERSON);
		
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
							followingAnchor.setText("Following");
							isFollowing = true;
						} else {
							followingAnchor.setText("Follow");
							isFollowing = false;
						}
					}// end onSuccess()
				});
	}

	/**
	 * Check if is following this person
	 * 
	 * @return
	 */
	private boolean isFollowing(){
		return isFollowing;
	}

	public void addActionHandler(ActionHandler actionHandler) {
		m_actionHandlers.add(actionHandler);
	}
	
	/**
	 * Fires a TeamingAction at the registered ActionHandler's.
	 * 
	 * Implements the ActionTrigger.triggerAction() method. 
	 *
	 * @param action
	 * @param obj
	 */
	public void triggerAction(TeamingAction action, Object obj) {
		// Scan the ActionHandler's that have been registered...
		for (Iterator<ActionHandler> ahIT = m_actionHandlers.iterator(); ahIT.hasNext(); ) {
			// ...firing the action at each.
			ahIT.next().handleAction(action, obj);
		}
	}
	
	/**
	 * Use to trigger an action to GwtMainPage
	 * @param action
	 */
	public void triggerAction(TeamingAction action) {
		// Always use the initial form of the method.
		triggerAction(action, null);
	}

	public void setEditable(boolean edit) {
		isEditable  = edit;
	}
}
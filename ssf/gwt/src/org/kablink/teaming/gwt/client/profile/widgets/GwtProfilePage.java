package org.kablink.teaming.gwt.client.profile.widgets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.profile.ProfileCategory;
import org.kablink.teaming.gwt.client.profile.ProfileInfo;
import org.kablink.teaming.gwt.client.profile.ProfileRequestInfo;
import org.kablink.teaming.gwt.client.service.GwtRpcService;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.ActionRequestor;
import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.TeamingAction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class GwtProfilePage extends Composite implements ActionRequestor, ActionTrigger {

	private List<ActionHandler> m_actionHandlers = new ArrayList<ActionHandler>();
	private ProfileRequestInfo profileRequestInfo = null;
	private ProfileMainPanel profileMainPanel;
	private ProfileSidePanel profileSidePanel;
	private FlowPanel profilePanel;
	private FlowPanel mainProfilePage;

	public GwtProfilePage() {

		// Get information about the request we are dealing with.
		profileRequestInfo = getProfileRequestInfo();
		
		//Register with GwtMainPage, so we can fire an event
		GwtClientHelper.jsRegisterActionHandler(this);

		// Outer div around the page
		mainProfilePage = new FlowPanel();
		mainProfilePage.getElement().setId("profileContents");
		mainProfilePage.addStyleName("section1");

		// Main Panel
		profilePanel = new FlowPanel();
		profilePanel.setHeight("600px");
		profilePanel.addStyleName("profileSection");
		mainProfilePage.add(profilePanel);

		// The title Bar
		createProfileTitleBar();

		// Create a horizontal Panel to split the panel into a main info panel
		// and a side panel
		HorizontalPanel hPanel = createHorizontalPanel();

		// create main profile info panel
		createProfileMainPanel(hPanel);

		// Add the tracking info and team info to right pane
		createProfileSidePanel(hPanel);

		profileMainPanel.setEditable(false);

		// initialize the page with data
		initialize();

		// All composites must call initWidget() in their constructors.
		initWidget(mainProfilePage);
	}

	private void initialize() {

		Timer timer = new Timer() {
			public void run() {
				createProfileInfoSections();
			}// end run()
		};

		timer.schedule(25);
	}

	private HorizontalPanel createHorizontalPanel() {

		// Create a horizontal Panel to split the panel into a main info panel
		// and a side panel
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.addStyleName("profileTable");
		hPanel.setWidth("100%");
		profilePanel.add(hPanel);

		return hPanel;
	}

	private void createProfileSidePanel(HorizontalPanel panel) {
		profileSidePanel = new ProfileSidePanel(profileRequestInfo, this);
		panel.add(profileSidePanel);
		panel.setCellHorizontalAlignment(profileSidePanel,
				HasHorizontalAlignment.ALIGN_RIGHT);
	}

	private void createProfileMainPanel(HorizontalPanel panel) {
		// Add the profile info to the left pane
		profileMainPanel = new ProfileMainPanel(profileRequestInfo, this);
		panel.add(profileMainPanel);
		panel.setCellWidth(profileMainPanel, "100%");
	}

	private void createProfileTitleBar() {

		FlowPanel titleBar = new FlowPanel();
		titleBar.addStyleName("column-head");
		profilePanel.add(titleBar);

		// Title
		Label profileLabel = new Label(GwtTeaming.getMessages().qViewProfile());
		titleBar.add(profileLabel);
	}

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
				int count = profile.getCategories().size();
				for (int i = 0; i < count; i++) {

					ProfileCategory cat = profile.get(i);
					if (cat.getName().equals("profileSidePanelView")) {
						profileSidePanel.setCategory(cat);
						continue;
					}

					profileMainPanel.setCategory(cat);
				}
				
				// relayout the page now
				relayoutPage();
			}
		};

		gwtRpcService = (GwtRpcServiceAsync) GWT.create(GwtRpcService.class);
		gwtRpcService
				.getProfileInfo(profileRequestInfo.getBinderId(), callback);

	}

	private void relayoutPage() {
		Timer timer = new Timer() {
			public void run() {
				profilePanel.setHeight("");
				triggerAction(TeamingAction.SIZE_CHANGED);
			}
		};
		
		timer.schedule(25);
	}
	
	/**
	 * Use JSNI to grab the JavaScript object that holds the information about
	 * the request dealing with.
	 */
	private native ProfileRequestInfo getProfileRequestInfo() /*-{
		// Return a reference to the JavaScript variable called, m_requestInfo.
		return $wnd.profileRequestInfo;
	}-*/;

	/**
	 * Add an action Handler.  This handler will perform the necessary action when an action is triggered.
	 */
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

}

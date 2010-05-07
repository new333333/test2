package org.kablink.teaming.gwt.client.profile;

import org.kablink.teaming.gwt.client.service.GwtRpcService;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

public class GwtProfilePage extends Composite {

	private ProfileRequestInfo profileRequestInfo = null;
	private ProfileMainPanel profileMainPanel;
	private ProfileSidePanel profileSidePanel;
	private FlowPanel profilePanel;
	private InlineLabel edit;
//	private InlineLabel cancel;
//	private InlineLabel save;

	public GwtProfilePage() {

		// Get information about the request we are dealing with.
		profileRequestInfo = getProfileRequestInfo();

		// Outer div around the page
		FlowPanel mainProfilePage = new FlowPanel();
		mainProfilePage.getElement().setId("profileContents");
		mainProfilePage.addStyleName("section1");

		// Main Panel
		profilePanel = new FlowPanel();
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
			@Override
			public void run() {
				createProfileInfoSections();
			}// end run()
		};

		timer.schedule(5);
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
		profileSidePanel = new ProfileSidePanel(profileRequestInfo);
		panel.add(profileSidePanel);
		panel.setCellHorizontalAlignment(profileSidePanel,
				HasHorizontalAlignment.ALIGN_RIGHT);
	}

	private void createProfileMainPanel(HorizontalPanel panel) {
		// Add the profile info to the left pane
		profileMainPanel = new ProfileMainPanel(profileRequestInfo);
		panel.add(profileMainPanel);
		panel.setCellWidth(profileMainPanel, "100%");
	}

	private void createProfileTitleBar() {

		FlowPanel titleBar = new FlowPanel();
		titleBar.addStyleName("column-head");
		profilePanel.add(titleBar);

		// Title
		Label profileLabel = new Label("Profile");
		titleBar.add(profileLabel);

		createActions(titleBar);
	}

	private void createActions(FlowPanel panel) {
		FlowPanel actions = new FlowPanel();
		actions.addStyleName("profile-actions-edit");
		panel.add(actions);

		edit = new InlineLabel("Edit");
		edit.setVisible(showEditButton());
		edit.addStyleName("profile-action");
		edit.addStyleName("profile-edit-button");
		actions.add(edit);
		edit.addClickHandler(new ActionClickHandler("Edit"));

//		cancel = new InlineLabel("Cancel");
//		cancel.addStyleName("profile-action");
//		cancel.addStyleName("profile-cancel-button");
//		actions.add(cancel);
//		cancel.setVisible(false);
//		cancel.addClickHandler(new ActionClickHandler("Cancel"));
//
//		save = new InlineLabel("Save");
//		save.addStyleName("profile-action");
//		save.addStyleName("profile-save-button");
//		actions.add(save);
//		save.setVisible(false);
//		save.addClickHandler(new ActionClickHandler("Save"));
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
				int row = 0;
				for (int i = 0; i < count; i++) {

					ProfileCategory cat = profile.get(i);
					if (cat.getName().equals("profileSidePanelView")) {
						profileSidePanel.setCategory(cat);
						continue;
					}

					profileMainPanel.setCategory(cat);
				}
			}
		};

		gwtRpcService = (GwtRpcServiceAsync) GWT.create(GwtRpcService.class);
		gwtRpcService
				.getProfileInfo(profileRequestInfo.getBinderId(), callback);

	}

	/**
	 * Use JSNI to grab the JavaScript object that holds the information about
	 * the request dealing with.
	 */
	private native ProfileRequestInfo getProfileRequestInfo() /*-{
		// Return a reference to the JavaScript variable called, m_requestInfo.
		return $wnd.profileRequestInfo;
	}-*/;

	public class ActionClickHandler implements ClickHandler {

		String handlerId = null;

		public ActionClickHandler(String id) {
			handlerId = id;
		}

		public void onClick(ClickEvent event) {
			if (handlerId.equals("Edit")) {

				String url = profileRequestInfo.getModifyUrl();
				GwtClientHelper.jsLaunchUrlInWindow(url, "Modify Entry", 800, 800);
				
//				boolean visible = true;
//				cancel.setVisible(visible);
//				save.setVisible(visible);
//				edit.setVisible(!visible);
			} else {
//				boolean visible = false;
//				cancel.setVisible(visible);
//				save.setVisible(visible);
//				edit.setVisible(!visible);
			}
		}
	}
}

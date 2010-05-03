package org.kablink.teaming.gwt.client.profile;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProfileMainPanel extends Composite {

	ProfileRequestInfo profileRequestInfo;
	private Grid grid;
	private int row = 0;
	private FlowPanel mainPanel;
	private FlowPanel titlePanel;
	
	public ProfileMainPanel(final ProfileRequestInfo profileRequestInfo) {
		
		this.profileRequestInfo = profileRequestInfo;
		
		//create the main panel
		mainPanel = new FlowPanel();
		mainPanel.setStyleName("profile-Content-c");
		
		//add user's title to the profile div
		createTitleArea();
		
		//add the actions area to the title div
		createActionsArea();

		// ...its content panel...
		createContentPanel();

		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}

	private void createContentPanel() {
		grid = new Grid();
		grid.setWidth("100%");
		grid.setCellSpacing(0);
		grid.setCellPadding(0);
		grid.resizeColumns(3);
		grid.setStyleName("sectionTable");
		mainPanel.add(grid);
	}

	private void createTitleArea() {
		
		//create a title div for the user title and actionable items
		titlePanel = new FlowPanel();
		titlePanel.addStyleName("profile-title-area");
		mainPanel.add(titlePanel);
		
		String userName = profileRequestInfo.getUserName();
		String url = profileRequestInfo.getAdaptedUrl();
		Anchor anchor = new Anchor(userName,url);
		anchor.addStyleName("profile-title");
		titlePanel.add(anchor);
		
		Anchor workspace = new Anchor("Workspace", url);
		workspace.addStyleName("profile-workspace-link");
		titlePanel.add(workspace);
	}

	private void createActionsArea() {
		FlowPanel actions = new FlowPanel();
		actions.addStyleName("profile-follow");
		
		titlePanel.add(actions);
		
		Anchor updateAnchor = new Anchor();
		actions.add(updateAnchor);
		updateAnchor.addStyleName("ss_tabsCCurrent");
		
		InlineLabel label = new InlineLabel("Follow");
		updateAnchor.getElement().appendChild(label.getElement());
	}

	public int createProfileInfoSection(final ProfileCategory cat, Grid grid, int rowCount) {
		int row = rowCount;

		Label sectionHeader = new Label(cat.getTitle());
		sectionHeader.setStyleName("sectionHeading");
		
		grid.insertRow(row);
		grid.setWidget(row, 0, sectionHeader);
		
		//remove the bottom border from the section heading titles
		grid.getCellFormatter().setStyleName(row, 0, "sectionHeadingRBB");
		grid.getCellFormatter().setStyleName(row, 1, "sectionHeadingRBB");
		grid.getCellFormatter().setStyleName(row, 2, "sectionHeadingRBB");
		row = row + 1;
		
		for(ProfileAttribute attr: cat.getAttributes()) {
			
			Label title = new Label(attr.getTitle()+":");
			title.setStyleName("attrLabel");
			Widget value = new ProfileAttributeWidget(attr).getWidget();
			
			grid.insertRow(row);
			grid.setWidget(row, 0, title);
			grid.setWidget(row, 1, value);
			grid.getCellFormatter().setWidth(row, 1, "70%");
			grid.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_LEFT);
			
			row = row + 1;
		}

		return row;
	}
	
	public void setCategory(ProfileCategory cat) {
		row  = createProfileInfoSection(cat, grid, row);
	}
}
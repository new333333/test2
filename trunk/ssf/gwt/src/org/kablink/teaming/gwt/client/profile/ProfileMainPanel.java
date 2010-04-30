package org.kablink.teaming.gwt.client.profile;

import org.kablink.teaming.gwt.client.service.GwtRpcService;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ProfileMainPanel extends Composite {

	ProfileRequestInfo profileRequestInfo;
	private Grid grid;
	private int row = 0;
	
	public ProfileMainPanel(final ProfileRequestInfo profileRequestInfo) {
		
		this.profileRequestInfo = profileRequestInfo;
		
		FlowPanel infoPanel = new FlowPanel();
		infoPanel.setStyleName("profile-Content-c");

		String userName = profileRequestInfo.getUserName();
		String url = profileRequestInfo.getAdaptedUrl();
		Anchor anchor = new Anchor(userName,url);
		anchor.setStyleName("profile-title");
		infoPanel.add(anchor);

		// ...its content panel...
		grid = new Grid();
		grid.setWidth("100%");
		grid.setCellSpacing(0);
		grid.setCellPadding(0);
		grid.resizeColumns(3);
		grid.setStyleName("sectionTable");
		infoPanel.add(grid);
		
		
		

		// All composites must call initWidget() in their constructors.
		initWidget( infoPanel );
	}

	public int createProfileInfoSection(final ProfileCategory cat, Grid grid, int rowCount) {
		int row = rowCount;

		Label sectionHeader = new Label(cat.getTitle());
		sectionHeader.setStyleName("sectionHeading");
		
		grid.insertRow(row);
		grid.setWidget(row, 0, sectionHeader);
		row = row + 1;
		
		for(ProfileAttribute attr: cat.getAttributes()) {
			
			Label title = new Label(attr.getTitle()+":");
			title.setStyleName("attrLabel");
			Widget value = new Label(attr.getValue().toString());
			
			if(attr.getDisplayType().equals("email") ) {
				String url = "mailto:"+ attr.getValue().toString();
				value = new Anchor(attr.getValue().toString(), url);
			}
			
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
package org.kablink.teaming.gwt.client.profile;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;

public class ProfileSection extends Composite {

	private ProfileCategory cat;

	public ProfileSection(ProfileCategory category) {
		
		this.cat = category;
		
		FlowPanel panel = new FlowPanel();
		
		Label sectionHeader = new Label(cat.getTitle());
		sectionHeader.setStyleName("sectionHeading");
		panel.add(sectionHeader);
		
		// ...its content panel...
		Grid grid = new Grid();
		grid.setWidth("100%");
		grid.setCellSpacing(0);
		grid.setCellPadding(0);
		grid.resizeColumns(2);
		grid.setStyleName("sectionTable");
		panel.add(grid);
		
		//insert the number of rows
		int row = 0;

		for(ProfileAttribute attr: cat.getAttributes()) {
			
			Label title = new Label(attr.getTitle()+" "+row);
			Label value = new Label(attr.getValue().toString()+" "+row);
			
			grid.insertRow(row);
			grid.setWidget(row, 0, title);
			grid.setWidget(row, 1, value);
			row = row + 1;
		}
		
		initWidget(panel);
	}

}

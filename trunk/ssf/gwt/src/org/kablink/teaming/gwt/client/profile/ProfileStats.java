package org.kablink.teaming.gwt.client.profile;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

public class ProfileStats extends Composite {


	int row = 0;
	FlowPanel mainPanel;
	
	public ProfileStats(ProfileRequestInfo profileRequestInfo) {
		
		mainPanel = new FlowPanel();
		
		// ...its content panel...
		Grid grid = new Grid();
		grid.setCellSpacing(0);
		grid.setCellPadding(0);
		grid.resizeColumns(2);
		grid.setStyleName("statsTable");
		mainPanel.add(grid);
		
		//insert the number of rows
		int row = 0;
			
		addStat(grid, "TRACKING:", "25");
		addStat(grid, "TRACKING BY:", "211");
		addStat(grid, "ENTRIES:", "10441234");
		addStat(grid, "DATA QUOTA:", "20", "MB");
		addStat(grid, "DATA QUOTA:", "2.77", "MB");
		
		initWidget(mainPanel);
	}

	private void addStat(Grid grid, String title, String value) {
		
		Label titleLabel = new Label(title);
		//Label valueLabel = new Label(value);
		InlineLabel valueLabel = new InlineLabel(value);
		valueLabel.addStyleName( "bold" );
		
		grid.insertRow(row);
		grid.setWidget(row, 0, titleLabel);
		grid.setWidget(row, 1, valueLabel);
		row = row + 1;
	}
	
private void addStat(Grid grid, String title, String value, String end) {
		
		Label titleLabel = new Label(title);
		//Label valueLabel = new Label(value);
		InlineLabel valueLabel = new InlineLabel(value);
		valueLabel.addStyleName( "bold" );
		
//TODO figure the label
		//		InlineLabel endLabel = new InlineLabel(end);
//		valueLabel.getElement().appendChild(endLabel.getElement());
		
		grid.insertRow(row);
		grid.setWidget(row, 0, titleLabel);
		grid.setWidget(row, 1, valueLabel);
		row = row + 1;
	}
	
}

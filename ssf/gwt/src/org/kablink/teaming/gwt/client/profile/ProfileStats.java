package org.kablink.teaming.gwt.client.profile;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

public class ProfileStats extends Composite {


	private int row = 0;
	private FlowPanel mainPanel;
	private Grid grid;
	private ProfileRequestInfo profileRequestInfo;
	
	public ProfileStats(ProfileRequestInfo requestInfo) {
		
		
		this.profileRequestInfo = requestInfo;
		mainPanel = new FlowPanel();
		
		// ...its content panel...
		grid = new Grid();
		grid.setCellSpacing(0);
		grid.setCellPadding(0);
		grid.resizeColumns(2);
		grid.setStyleName("statsTable");
		mainPanel.add(grid);
		
		{
			Timer timer;
			timer = new Timer()
			{
				/**
				 * 
				 */
				@Override
				public void run()
				{
					addStats();
				}// end run()

				
			};
			
			timer.schedule( 25 );
		}
		
		initWidget(mainPanel);
	}

	private void addStats() {
		
		addStat(grid, "Following:", "25");
		addStat(grid, "Followers:", "211");
		addStat(grid, "Entries:", "10441234");
		
		if(profileRequestInfo.isOwner()) {
			addStat(grid, "Data Quota:", "20", "MB");
			addStat(grid, "Quota Used:", "2.77", "MB");
		}
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
		
		grid.insertRow(row);
		grid.setWidget(row, 0, titleLabel);
		grid.setWidget(row, 1, valueLabel);
		row = row + 1;
	}
}

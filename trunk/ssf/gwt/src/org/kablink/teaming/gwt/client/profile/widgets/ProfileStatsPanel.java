package org.kablink.teaming.gwt.client.profile.widgets;

import org.kablink.teaming.gwt.client.profile.ProfileRequestInfo;
import org.kablink.teaming.gwt.client.profile.ProfileStats;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

public class ProfileStatsPanel extends Composite {


	private int row = 0;
	private FlowPanel mainPanel;
	private Grid grid;
	private ProfileRequestInfo profileRequestInfo;
	
	public ProfileStatsPanel(ProfileRequestInfo requestInfo) {
		
		
		this.profileRequestInfo = requestInfo;
		mainPanel = new FlowPanel();
		mainPanel.addStyleName("user_stats");
		
		// ...its content panel...
		grid = new Grid();
		grid.setCellSpacing(0);
		grid.setCellPadding(0);
		grid.resizeColumns(2);
		grid.setStyleName("statsTable");
		mainPanel.add(grid);
		
		initWidget(mainPanel);
	}

	/**
	 * Add the user stats to the side panel
	 */
	public void addStats(ProfileStats stats) {

		//addStat(grid, "Entries", stats.getEntries());
		//addStat(grid, "Following:", Integer.toString(stats.getTrackedCnt()) );

		//if the quotas enable and is the owner or the admin then can see the quota
		if(profileRequestInfo.isQuotasEnabled() && (profileRequestInfo.isOwner()) ) {
			addStat(grid, "Data Quota:", profileRequestInfo.getQuotasUserMaximum(), " MB");
			addStat(grid, "Quota Used:", profileRequestInfo.getQuotasDiskSpacedUsed(), " MB");
		}
	}
	
	/**
	 * Helper method to build the user stats
	 * @param grid
	 * @param title
	 * @param value
	 */
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
	
	/**
	 * Helper method to build the user stats
	 * 
	 * @param grid
	 * @param title
	 * @param value
	 * @param end
	 */
	private void addStat(Grid grid, String title, String value, String end) {
		
		Label titleLabel = new Label(title);
		//Label valueLabel = new Label(value);
		InlineLabel valueLabel = new InlineLabel(value);
		valueLabel.addStyleName( "bold" );
		InlineLabel endLabel = new InlineLabel(end);
		valueLabel.getElement().appendChild(endLabel.getElement());
		
		grid.insertRow(row);
		grid.setWidget(row, 0, titleLabel);
		grid.setWidget(row, 1, valueLabel);
		row = row + 1;
	}
}

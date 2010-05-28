package org.kablink.teaming.gwt.client.profile;

import org.kablink.teaming.gwt.client.service.GwtRpcService;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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

	/**
	 * Add the user stats to the side panel
	 */
	private void addStats() {
		
		// create an async callback to handle the result of the request to get
		// the state:
		AsyncCallback<ProfileStats> callback = new AsyncCallback<ProfileStats>() {
			public void onFailure(Throwable t) {
				// display error
				Window.alert("Error: " + t.getMessage());
			}

			public void onSuccess(ProfileStats stats) {
				addStat(grid, "Entries", stats.getEntries());
				addStat(grid, "Following:", stats.getFollowing());

				//if the quotas enable and is the owner or the admin then can see the quota
				if(profileRequestInfo.isQuotasEnabled() && (profileRequestInfo.isOwner()) ) {
					addStat(grid, "Data Quota:", profileRequestInfo.getQuotasUserMaximum(), " MB");
					addStat(grid, "Quota Used:", profileRequestInfo.getQuotasDiskSpacedUsed(), " MB");
				}
			}
		};

		GwtRpcServiceAsync gwtRpcService = (GwtRpcServiceAsync) GWT.create(GwtRpcService.class);
		gwtRpcService.getProfileStats(profileRequestInfo.getBinderId(), callback);
		
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

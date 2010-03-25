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

public class ProfileInfoPanel extends Composite {

	ProfileRequestInfo profileRequestInfo;
	
	public ProfileInfoPanel(final ProfileRequestInfo profileRequestInfo) {
		
		this.profileRequestInfo = profileRequestInfo;
		
		FlowPanel infoPanel = new FlowPanel();
		infoPanel.setStyleName("profile-Content-c");

		String userName = profileRequestInfo.getUserName();
		String url = profileRequestInfo.getAdaptedUrl();
		Anchor anchor = new Anchor(userName,url);
		anchor.setStyleName("profile-title");
		infoPanel.add(anchor);

		// ...its content panel...
		final Grid grid = new Grid();
		grid.setWidth("100%");
		grid.setCellSpacing(0);
		grid.setCellPadding(0);
		grid.resizeColumns(3);
		grid.setStyleName("sectionTable");
		infoPanel.add(grid);
		
		
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
					createProfileInfoSections(profileRequestInfo, grid);
				}// end run()
			};
			
			timer.schedule( 20 );
		}

		// All composites must call initWidget() in their constructors.
		initWidget( infoPanel );
	}
	
	private void createProfileInfoSections(ProfileRequestInfo profileRequestInfo, final Grid grid) {
			
			GwtRpcServiceAsync	gwtRpcService;
			
			// create an async callback to handle the result of the request to get the state:
			AsyncCallback<ProfileInfo> callback = new AsyncCallback<ProfileInfo>()
			{
				public void onFailure(Throwable t)
				{
					// display error
					Window.alert( "Error: "+ t.getMessage() );
				}
			
				public void onSuccess(ProfileInfo profile) {
					int count = profile.getCategories().size();
					int row = 0;
					for(int i=0; i < count; i++ ) {
						ProfileCategory cat = profile.get(i);
						row = createProfileInfoSection(cat, grid, row);
						
						//ProfileSection section = new ProfileSection(cat);
						//grid.insertRow(row);
						//grid.setWidget(row, 0, section);
						//row = row + 1;
					}
				}
			};
		
			gwtRpcService = (GwtRpcServiceAsync) GWT.create( GwtRpcService.class );
			gwtRpcService.getProfileInfo(profileRequestInfo.getBinderId(), callback);
			
	}


	private int createProfileInfoSection(final ProfileCategory cat, Grid grid, int rowCount) {
		int row = rowCount;

		Label sectionHeader = new Label(cat.getTitle());
		sectionHeader.setStyleName("sectionHeading");
		
		grid.insertRow(row);
		grid.setWidget(row, 0, sectionHeader);
		row = row + 1;
		
		for(ProfileAttribute attr: cat.getAttributes()) {
			
			Label title = new Label(attr.getTitle()+":");
			title.setStyleName("attrLabel");
			Label value = new Label(attr.getValue().toString());
			
			grid.insertRow(row);
			grid.setWidget(row, 0, title);
			grid.setWidget(row, 1, value);
			grid.getCellFormatter().setWidth(row, 1, "70%");
			grid.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_LEFT);
			
			row = row + 1;
		}

		return row;
	}
}
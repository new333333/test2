package org.kablink.teaming.gwt.client.profile;

import java.util.Iterator;

import org.kablink.teaming.gwt.client.service.GwtRpcService;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.ActionRequestor;
import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.TeamingAction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class GwtProfilePage extends Composite implements ActionHandler
{
	private ProfileRequestInfo profileRequestInfo = null;
	private ProfileMainPanel profileMainPanel;
	private ProfileSidePanel profileSidePanel;
	
	public GwtProfilePage() {
		
		// Get information about the request we are dealing with.
		profileRequestInfo = getProfileRequestInfo();
		
		FlowPanel mainProfilePage = new FlowPanel();
		mainProfilePage.getElement().setId("profileContents");
		mainProfilePage.addStyleName("section1");
		
		FlowPanel profilePanel = new FlowPanel();
		profilePanel.addStyleName("profileSection");
		mainProfilePage.add(profilePanel);
				
		Label profileLabel = new Label("Profile");
		profileLabel.addStyleName("column-head");
		profilePanel.add(profileLabel);
		
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.addStyleName("profileTable");
		hPanel.setWidth("100%");
		profilePanel.add(hPanel);

		profileMainPanel = new ProfileMainPanel(profileRequestInfo);
		profileSidePanel = new ProfileSidePanel(profileRequestInfo);
		
		//Add the profile info to the left pane
		hPanel.add(profileMainPanel);
		hPanel.setCellWidth(profileMainPanel, "100%");
		
		//Add the tracking info and team info to right pane
		hPanel.add(profileSidePanel);
		hPanel.setCellHorizontalAlignment(profileSidePanel, HasHorizontalAlignment.ALIGN_RIGHT);
		
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
					createProfileInfoSections(profileRequestInfo);
				}// end run()
			};
			
			timer.schedule( 20 );
		}
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainProfilePage );
	}

	private void createProfileInfoSections(ProfileRequestInfo profileRequestInfo) {
		
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
					if(cat.getName().equals("profileSidePanelView")) {
						profileSidePanel.setCategory(cat);
						continue;
					}
					
					profileMainPanel.setCategory(cat);
				}
			}
		};
	
		gwtRpcService = (GwtRpcServiceAsync) GWT.create( GwtRpcService.class );
		gwtRpcService.getProfileInfo(profileRequestInfo.getBinderId(), callback);
		
}


	/**
	 * Use JSNI to grab the JavaScript object that holds the information about the request dealing with.
	 */
	private native ProfileRequestInfo getProfileRequestInfo() /*-{
		// Return a reference to the JavaScript variable called, m_requestInfo.
		return $wnd.profileRequestInfo;
	}-*/;

	/*
	 * Does what's necessary to wire the GwtMainPage to an
	 * ActionRequestor.
	 */
	private void registerActionHandler( ActionRequestor actionRequestor )
	{
		// For now, all we need to do is add the GwtMainPage as an
		// ActionHandler to the ActionRequestor.
		actionRequestor.addActionHandler( this );
	}// end registerActionHandler()

	public void handleAction(TeamingAction action, Object obj) {
		// TODO Auto-generated method stub
		
	}
}

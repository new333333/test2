package org.kablink.teaming.gwt.client.profile;

import java.util.Iterator;

import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.ActionRequestor;
import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.TeamingAction;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class GwtProfilePage extends Composite implements ActionHandler
{
	private ProfileRequestInfo profileRequestInfo = null;
	
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

		ProfileMainPanel profileInfoPanel = new ProfileMainPanel(profileRequestInfo);
		ProfileSidePanel profileTrackPanel = new ProfileSidePanel(profileRequestInfo);
		
		//Add the profile info to the left pane
		hPanel.add(profileInfoPanel);
		hPanel.setCellWidth(profileInfoPanel, "100%");
		
		//Add the tracking info and team info to right pane
		hPanel.add(profileTrackPanel);
		hPanel.setCellHorizontalAlignment(profileTrackPanel, HasHorizontalAlignment.ALIGN_RIGHT);
			
		// All composites must call initWidget() in their constructors.
		initWidget( mainProfilePage );
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

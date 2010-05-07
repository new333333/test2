package org.kablink.teaming.gwt.client.profile;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

class ProfileSectionPanel extends FlowPanel {

	protected GwtRpcServiceAsync	rpcService;			//
	protected GwtTeamingMessages	messages; 			// The menu's messages.
	ProfileRequestInfo 				profileRequestInfo; // Initial values passed 
	private Label 					headingLabel;
	
	public ProfileSectionPanel(ProfileRequestInfo requestInfo, String title) {

		rpcService			= GwtTeaming.getRpcService();
		messages			= GwtTeaming.getMessages();
		profileRequestInfo 	= requestInfo;
		
		//add a title to the section
		if(title !=null) {
			headingLabel = new Label(title);
			add(headingLabel);
		}
	}
	
	public Label getHeadingLabel() {
		return headingLabel;
	}
}
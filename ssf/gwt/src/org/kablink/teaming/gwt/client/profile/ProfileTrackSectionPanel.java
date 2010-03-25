package org.kablink.teaming.gwt.client.profile;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class ProfileTrackSectionPanel extends Composite {

	ProfileRequestInfo profileRequestInfo;
	
	public ProfileTrackSectionPanel(ProfileRequestInfo profileRequestInfo, String title) {
		
		this.profileRequestInfo = profileRequestInfo;
		
		FlowPanel main = new FlowPanel();
		
		//add a title to the section
		if(title!=null && !title.equals("")) {
			main.setStyleName("tracking-subhead");

			Label label = new Label(title);
			main.add(label);
		}
		
		// All composites must call initWidget() in their constructors.
		initWidget( main );
	}
	
}

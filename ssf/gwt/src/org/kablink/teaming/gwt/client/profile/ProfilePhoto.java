package org.kablink.teaming.gwt.client.profile;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

public class ProfilePhoto extends Composite {

	public ProfilePhoto(ProfileRequestInfo profileRequestInfo) {
		
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.setStyleName("photo");
		
		String userName = profileRequestInfo.getUserName();
		String imageUrl = "";
		String url = profileRequestInfo.getAdaptedUrl();
		
		Anchor anchor = new Anchor("<img src='http://www.kablink.org/~teaming_ux/1Web/testing/common/images/photos/Peter.jpg'/>",true, url, "_blank");
		mainPanel.add(anchor);
	
		
		
		initWidget(mainPanel);
	}
	
}

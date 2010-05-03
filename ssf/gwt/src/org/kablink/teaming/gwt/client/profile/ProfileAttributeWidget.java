package org.kablink.teaming.gwt.client.profile;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ProfileAttributeWidget  {
	
	private Widget widget;
	private final String IDBASE = "myAttrs_";
	
	public ProfileAttributeWidget(ProfileAttribute attr) {
		createWidget(attr);
	}
	
	private void createWidget(ProfileAttribute attr){
		
		widget = new Label(attr.getValue().toString());
		widget.addStyleName("profile-value");
		
		if(attr.getDisplayType().equals("email") ) {
			String url = "mailto:"+ attr.getValue().toString();

			widget = new Anchor(attr.getValue().toString(), url);
			widget.addStyleName("profile-value");
			widget.addStyleName("profile-anchor");
		}
	}

	public Widget getWidget() {
		return widget;
	}

}

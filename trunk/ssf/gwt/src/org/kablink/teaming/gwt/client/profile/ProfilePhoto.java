package org.kablink.teaming.gwt.client.profile;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

public class ProfilePhoto extends Composite {

	public ProfilePhoto(ProfileRequestInfo profileRequestInfo) {
		
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.setStyleName("profilePhotoMain");
		
		// Find the element that this RootPanel will wrap.
	    Element elem = Document.get().getElementById("profilePhoto");
        
	    if(elem != null){
	    	elem.removeFromParent();
		    elem.removeAttribute("style");
		    
		    //Document.get().removeChild(elem);
		    mainPanel.getElement().appendChild(elem);
	    }
		
		initWidget(mainPanel);
	}
}

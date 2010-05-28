package org.kablink.teaming.gwt.client.profile;

import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

public class ProfileActionWidget extends Anchor {
	
	protected Image img;
	protected FlowPanel panel;

	protected ProfileActionWidget(String text, String title, String anchorStlyeName, String labelStyleName) {
		super();

		this.setText(text);
		this.setTitle(title);

		if(GwtClientHelper.hasString(anchorStlyeName)){
			addStyleName(anchorStlyeName);
		}

		if(GwtClientHelper.hasString(anchorStlyeName)){
			addStyleName(labelStyleName);
		}
		
		addMouseOverHandler(new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent event) {
				addStyleName("qView-action2");
			}});
		
		addMouseOutHandler(new MouseOutHandler(){
			public void onMouseOut(MouseOutEvent event) {
				removeStyleName("qView-action2");
			}});
	}
}

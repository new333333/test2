package org.kablink.teaming.gwt.client.profile.widgets;

import java.util.List;

import org.kablink.teaming.gwt.client.profile.ProfileAttribute;
import org.kablink.teaming.gwt.client.profile.ProfileAttributeListElement;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ProfileAttributeWidget  {
	
	private Widget widget;
	private final String IDBASE = "myAttrs_";
	private boolean isEditMode = false;

	public ProfileAttributeWidget(ProfileAttribute attr, boolean editMode) {
		isEditMode = editMode;
		
		createWidget(attr);
	}
	
	private void createWidget(ProfileAttribute attr){
		
		if(isEditMode){
			widget = new TextBox();
			widget.addStyleName("profile-value");

			((TextBox) widget).setText(attr.getValue().toString());
			boolean readOnly = false;
			if(readOnly){
				((TextBox) widget).setReadOnly(readOnly);
			}
		} else {

			if(attr.getDisplayType().equals("email") ) {
				String url = "mailto:"+ attr.getValue().toString();
				widget = new Anchor(attr.getValue().toString(), url);
				widget.addStyleName("profile-value");
				widget.addStyleName("profile-anchor");
			} else if (attr.getName().equals("profileWebsite")) {
				String label = "";
				String uri = "";
				
				String labeledUri = attr.getValue().toString();
				if(GwtClientHelper.hasString(labeledUri)){
					int index = hasLabel(labeledUri);
					if(index > -1){
						label = labeledUri.substring(0, index);
						uri = appendUrlToHttp(labeledUri.substring(index+1, labeledUri.length()));
					} else {
						label = labeledUri;
						uri = appendUrlToHttp(labeledUri);
					}
				}
				
				widget = new Anchor(label, uri, "_blank");
				widget.addStyleName("profile-value");
				widget.addStyleName("profile-anchor");
			}
			else {
				int type = attr.getValueType();
				switch(type){
					case ProfileAttribute.STRING:
					case ProfileAttribute.BOOLEAN:
					case ProfileAttribute.LONG:
					case ProfileAttribute.DATE:	
						widget = new Label(attr.getValue().toString());
						break;
					case ProfileAttribute.LIST:
						List<ProfileAttributeListElement> value = (List<ProfileAttributeListElement>)attr.getValue();
						if(value != null){
							widget = new FlowPanel();
							for(ProfileAttributeListElement valItem: value){
								if(attr.getDataName().equals("picture")) {
									widget.addStyleName("profile_gallery");
									widget.addStyleName("ss_thumbnail_small");
								
									FlowPanel div = new FlowPanel();
									((FlowPanel)widget).add(div);

									Anchor anchor = new Anchor();
									div.add(anchor);
									
									String sval = valItem.getValue().toString();
									Image img = new Image(sval);
									
									anchor.getElement().appendChild(img.getElement());
								} else {
									((FlowPanel)widget).add(new Label(valItem.getValue().toString()));
								}
							}
						}
						break;
					default: 
						widget = new Label("");
						break;
				}

				widget.addStyleName("profile-value");
			}
		}
	}

	public Widget getWidget() {
		return widget;
	}

	public boolean isEditMode() {
		return isEditMode;
	}
	
	/**
	 * Prepends http:// to a uri to create a link
	 * 
	 * @param urlString
	 * 
	 * @return
	 */
	public String appendUrlToHttp(String urlString) {
		
		boolean found = ( urlString.indexOf("http:") > -1);
		if (!found) {
			urlString = "http://" + urlString;
		}

		return urlString;
	}

	/**
	 * 
	 */
	public int hasLabel(String labeledUri) {
		
		int index = -1;
		if(GwtClientHelper.hasString(labeledUri)){
			boolean found = (0 < labeledUri.indexOf(":"));
			if (found) {
				index = labeledUri.indexOf(':');

				//Make sure http is not prepended to it.
				String label = labeledUri.substring(0, index+1);
				found = (0 < labeledUri.indexOf("http:"));
				if(!found){
					return index;
				}
			}
		}
		return index;
	}
}

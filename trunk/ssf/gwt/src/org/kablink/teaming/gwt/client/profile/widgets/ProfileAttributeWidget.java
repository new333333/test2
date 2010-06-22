package org.kablink.teaming.gwt.client.profile.widgets;

import java.util.List;

import org.kablink.teaming.gwt.client.profile.ProfileAttribute;
import org.kablink.teaming.gwt.client.profile.ProfileAttributeListElement;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
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
		
		widget = new Label("");
		
		if(isEditMode){
			if(attr.getValue() != null) {
				widget = new TextBox();
				((TextBox) widget).setText(attr.getValue().toString());
				boolean readOnly = false;
				if(readOnly){
					((TextBox) widget).setReadOnly(readOnly);
				}
			}
			widget.addStyleName("profile-value");
		} else {
			if(attr.getDisplayType().equals("email") ) {
				if(attr.getValue() != null) {
					String url = "mailto:"+ attr.getValue().toString();
					widget = new Anchor(attr.getValue().toString(), url);
				} 
				widget.addStyleName("profile-value");
				widget.addStyleName("profile-anchor");
			} else if (attr.getDataName().equals("labeledUri")) {
				String label = "";
				String uri = "";
				if(attr.getValue() != null) {
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
				} 
				widget.addStyleName("profile-value");
				widget.addStyleName("profile-anchor");
			} else {
				int type = attr.getValueType();
				switch(type){
					case ProfileAttribute.STRING:
					case ProfileAttribute.BOOLEAN:
					case ProfileAttribute.LONG:
					case ProfileAttribute.DATE:
						if(attr.getValue() != null) {
							String s = attr.getValue().toString();
							widget = new HTML(s);
						}
						break;
					case ProfileAttribute.LIST:
						if(attr.getValue() != null) {
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
										
										if(valItem.getValue() != null){
											String sval = valItem.getValue().toString();
											Image img = new Image(sval);
											anchor.getElement().appendChild(img.getElement());
										}
									} else {
										String val = "";
										if(valItem.getValue() != null) {
											val = valItem.getValue().toString();
										}
										
										((FlowPanel)widget).add(new Label(val));
									}
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
				String label = labeledUri.substring(0, index);
				if(label.indexOf("http") > -1) {
					index = -1;
					return index;
				}
			}
		}
		return index;
	}
}

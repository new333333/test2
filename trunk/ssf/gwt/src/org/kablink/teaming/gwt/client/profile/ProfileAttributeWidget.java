package org.kablink.teaming.gwt.client.profile;

import java.util.List;

import com.google.gwt.user.client.ui.Anchor;
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
			} else {
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
						if(value != null && !value.isEmpty()){
							ProfileAttributeListElement valItem = value.get(0);
							if(valItem != null){
								widget = new Label(valItem.getValue().toString());
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
}

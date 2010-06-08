package org.kablink.teaming.gwt.client.profile.widgets;

import java.util.List;

import org.kablink.teaming.gwt.client.profile.ProfileAttribute;
import org.kablink.teaming.gwt.client.profile.ProfileAttributeListElement;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProfileAvatarArea  {
	
	private Widget widget;
	private boolean isEditMode = false;

	public ProfileAvatarArea(ProfileAttribute attr, boolean editMode) {
		isEditMode = editMode;
		widget = new FlowPanel();
		widget.addStyleName("profile_gallery");
		widget.addStyleName("ss_thumbnail_small");

		createWidget(attr);
	}
	
	private void createWidget(ProfileAttribute attr){
		
		List<ProfileAttributeListElement> value = (List<ProfileAttributeListElement>)attr.getValue();
		if(value != null){
			for(ProfileAttributeListElement valItem: value){
				if(attr.getDataName().equals("picture")) {
					
					FlowPanel div = new FlowPanel();
					((FlowPanel)widget).add(div);

					Anchor anchor = new Anchor();
					div.add(anchor);
					anchor.addClickHandler(new PictureClickHandler(valItem, anchor));
					
					String sval = valItem.getValue().toString();
					Image img = new Image(sval);
					anchor.getElement().appendChild(img.getElement());
				} else {
					((FlowPanel)widget).add(new Label(valItem.getValue().toString()));
				}
			}
		}
	} //end createWidget
	

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
	
	private class PictureClickHandler implements ClickHandler {
		
		private ProfileAttributeListElement item;
		private Anchor anchor;
		private int x = 0;

		private int y = 0;
		
		public PictureClickHandler(ProfileAttributeListElement valItem, Anchor imgAnchor) {
			item = valItem;
			anchor = imgAnchor;
		}

		public void onClick(ClickEvent event) {
			
			final ModifyAvatarDlg dlg = new ModifyAvatarDlg(false, false, 0, 0, item);
			PopupPanel.PositionCallback posCallback = new PopupPanel.PositionCallback()
			{
				public void setPosition(int offsetWidth, int offsetHeight)
				{
					x = anchor.getAbsoluteLeft();
					y = anchor.getAbsoluteTop() - widget.getOffsetHeight();

					dlg.setPopupPosition( x, y );
				}
			};
			dlg.setPopupPositionAndShow( posCallback );
		}
	}
}

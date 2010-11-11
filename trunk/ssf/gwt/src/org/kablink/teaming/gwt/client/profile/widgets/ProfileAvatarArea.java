/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */

package org.kablink.teaming.gwt.client.profile.widgets;

import java.util.List;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.profile.ProfileAttribute;
import org.kablink.teaming.gwt.client.profile.ProfileAttributeListElement;
import org.kablink.teaming.gwt.client.profile.ProfileRequestInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

public class ProfileAvatarArea extends FlowPanel  {
	
	private boolean isEditMode = false;
	private EditSuccessfulHandler editSuccessfulHandler;
	private ProfileRequestInfo profileRequestInfo;
	
	public ProfileAvatarArea(ProfileAttribute attr, boolean editMode, ProfileRequestInfo requestInfo, EditSuccessfulHandler editSuccessfulHandler) {
		
		super();
		isEditMode = editMode;
		this.editSuccessfulHandler=editSuccessfulHandler;
		this.profileRequestInfo = requestInfo;
		
		addStyleName("profile_gallery");
		addStyleName("ss_thumbnail_profile_high");

		createWidget(attr);
	}
	
	public void createWidget(ProfileAttribute attr){
		if(attr.getValue() != null) {
			List<ProfileAttributeListElement> value = (List<ProfileAttributeListElement>)attr.getValue();
			if(value != null){
				for(ProfileAttributeListElement valItem: value){
					if(attr.getDataName().equals("picture")) {
						FlowPanel div = new FlowPanel();
						add(div);

						Anchor anchor = new Anchor();
						div.add(anchor);
						anchor.addClickHandler(new PictureClickHandler(valItem, anchor));
						
						if(valItem.getValue() != null) {
							String sval = valItem.getValue().toString();
							Image img = new Image(sval);
							anchor.getElement().appendChild(img.getElement());
						}
					} else {
						if(valItem.getValue() != null) {
							add(new Label(valItem.getValue().toString()));
						} else {
							add(new Label(""));
						}
					}
				}
			}
		}
	} //end createWidget

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
			
			if(item != null && anchor != null) {
				final ModifyAvatarDlg dlg = new ModifyAvatarDlg(false, false, 0, 0, item, profileRequestInfo, editSuccessfulHandler);
				
				PopupPanel.PositionCallback posCallback = new PopupPanel.PositionCallback()
				{
					public void setPosition(int offsetWidth, int offsetHeight)
					{		
						boolean resize = false;
						
						int xOff = dlg.getOffsetWidth();
						int yOff = dlg.getOffsetHeight();
						
						//is the dialog larger than 500px wide
						if(xOff >  500) {
							xOff = 500;
							resize = true;
						}
						
						//is the dialog larger than 600px high
						if(yOff > 600) {
							yOff = 600;
							resize = true;
						}
						
						//set the size of the dialog because the image is too large
						if(resize) {
							//set the size of the dialog
							dlg.setSize(xOff+"px", yOff+"px");
							//set the size of the panel that contains the photo, so that scroll bars will show
							dlg.setPhotoPanelSize(xOff-30,yOff-120 );
						}
						
						//set the position to launch the new dialog
						x = anchor.getAbsoluteLeft() + 60;
						y = anchor.getAbsoluteTop() - dlg.getOffsetHeight() - 60;

						//if the starting positions is half way into the page
						//and if the dialog is going to be large than set the starting point to the left more
						if(x > 300 && xOff > 499) {
							x = 150;
						}

						//if the starting position is less than the size of the dialog then move down
						if(y < 600 && yOff > 599) {
							y = y + 60;
						}
						
						dlg.setPopupPosition( x, y );
					}
				};
				dlg.setPopupPositionAndShow( posCallback );
			} else {
				Window.alert("Error finding Avatar, no item was found to modify.");
			}
		}
	}
}

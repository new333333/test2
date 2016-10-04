/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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

/**
 * ?
 * 
 * @author nbjensen@novell.com
 */
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
	
	@SuppressWarnings("unchecked")
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
				@SuppressWarnings("unused")
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

		// Various size calculation constants.
		private final static int PAD_BASE			= 25;
		private final static int PAD_LEFT_RIGHT		= PAD_BASE;
		private final static int PAD_WIDTH			= (PAD_LEFT_RIGHT * 2);	
		private final static int PAD_TOP_BOTTOM		= PAD_BASE;
		private final static int PAD_HEIGHT			= (PAD_TOP_BOTTOM * 2);
		private final static int PAD_FOR_BUTTONS	= 100;	// Padding between bottom of dialog and bottom of image area.
		private final static int PAD_FOR_VSCROLL	=   5;	// Padding to account for having a vertical scroll bar.
		
		@Override
		public void onClick(ClickEvent event) {
			
			if(item != null && anchor != null) {
				final ModifyAvatarDlg dlg = new ModifyAvatarDlg(0, 0, item, profileRequestInfo, editSuccessfulHandler);
				
				PopupPanel.PositionCallback posCallback = new PopupPanel.PositionCallback()
				{
					@Override
					public void setPosition(int offsetWidth, int offsetHeight)
					{
						// Determine the sizes we need to perform the
						// positioning calculations with.
						int dlgCX = dlg.getOffsetWidth();
						int dlgCY = dlg.getOffsetHeight();
						
						int wndCX = (Window.getClientWidth()  - PAD_WIDTH);
						int wndCY = (Window.getClientHeight() - PAD_HEIGHT);
						
						int wndScrollX = Window.getScrollLeft();
						int wndScrollY = Window.getScrollTop();
						
//!						// To assist with debugging, dumps the sizes.
//!						Window.alert("dlgCX: " + dlgCX + "\ndlgCY: " + dlgCY + "\nwndCX: " + wndCX + "\nwndCY: " + wndCY + "\nwndScrollX: " + wndScrollX + "\nwndScrollY: " + wndScrollY);

						// If the dialog wider than the window...
						boolean resizeX = (dlgCX > wndCX);
						if (resizeX) {
							// ...force a resize.
							dlgCX = wndCX;
						}
						
						// If the dialog taller than window...
						boolean resizeY = (dlgCY > wndCY);
						if (resizeY) {
							// ...force a resize.
							dlgCY  = wndCY;
						}
						
						// Do we need to resize of the dialog because
						// the image is too large?
						if (resizeX || resizeY) {
							// Yes!  Set the size of the dialog...
							dlg.setSize(((resizeX ? dlgCX : (dlgCX + PAD_FOR_VSCROLL)) + "px"), (dlgCY + "px"));
							
							// ...and of the photo panel so that scroll
							// ...bars will show, as appropriate.
							int photoCX = (-1); 
							int photoCY = (-1);
							if (resizeX) {
								photoCX = (dlgCX - PAD_WIDTH);
								if (PAD_LEFT_RIGHT > photoCX) {
									photoCX = PAD_LEFT_RIGHT;
								}
							}
							if (resizeY) {
								photoCY = (dlgCY - (PAD_HEIGHT + PAD_FOR_BUTTONS));
								if (PAD_TOP_BOTTOM > photoCY) {
									photoCY = PAD_TOP_BOTTOM;
								}
							}
							dlg.setPhotoPanelSize(photoCX, photoCY);	
						}
						
						// Set the position to launch the dialog.
						x = (anchor.getAbsoluteLeft() +                         PAD_LEFT_RIGHT);
						y = (anchor.getAbsoluteTop()  - dlg.getOffsetHeight() - PAD_TOP_BOTTOM);

						// Adjust the starting position so that the
						// dialog is in view...
						if ((x + dlgCX) > wndCX) {
							x = (PAD_LEFT_RIGHT + wndScrollX);
						}
						if ((0 > y) || (y + dlgCY) > wndCY) {
							y = (PAD_TOP_BOTTOM + wndScrollY);
						}

						// ...and show it.
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

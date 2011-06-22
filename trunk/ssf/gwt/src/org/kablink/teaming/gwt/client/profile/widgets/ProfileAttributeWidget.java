/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.RequestInfo;
import org.kablink.teaming.gwt.client.profile.ProfileAttribute;
import org.kablink.teaming.gwt.client.profile.ProfileAttributeListElement;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ProfileAttributeWidget  {
	
	private Widget widget;
	private boolean isEditMode = false;

	/*
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ProfileAttributeWidget(ProfileAttribute attr, boolean editMode) {
		isEditMode = editMode;
		
		createWidget(attr);
	}
	
	private void createWidget(ProfileAttribute attr){
		
		widget = new Label("..");
		
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
			if(attr.getDisplayType()!= null && attr.getDisplayType().equals("email") ) {
				if(attr.getValue() != null) {
					String url = "mailto:"+ attr.getValue().toString();
					String val = "..";
					if(attr.getValue() != null){
						if(GwtClientHelper.hasString(attr.getValue().toString())) {
							val = attr.getValue().toString();
						}
					}
					widget = new Anchor(val, url);
				} 
				widget.addStyleName("profile-value");
				widget.addStyleName("profile-anchor");
			} else if (attr.getDataName().equals("labeledUri")) {
				String label = "..";
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
			} else if (attr.getDataName().equals("skypeId")) {
				String label = "..";
				if(attr.getValue() != null) {
					label = attr.getValue().toString();
					if(GwtClientHelper.hasString(label)){						
						String pathToJs;
						String pathToImage;
						RequestInfo ri = GwtClientHelper.getRequestInfo();
						if (null != ri) {
							pathToJs    = ri.getJSPath();
							pathToImage = ri.getImagesPath();
						}
						else {
							pathToJs    =
							pathToImage = "";
						}
						
						String skypeAlt   = GwtTeaming.getMessages().profileCallMe();
						String skypeCheck = (pathToJs  + "skype/skypeCheck.js"); 
						String skypeImage = (pathToImage + "pics/skypeCallMe.png");
						
						String html =
							"<script type=\"text/javascript\" src=\""+ skypeCheck + "\"></script>" +
							"<a href=\"skype:" + label + "?call\"><img src=\"" + skypeImage + "\" style=\"border: none;\" width=\"70\" height=\"23\" alt=\"" + skypeAlt + "\" title=\"" + skypeAlt + "\" /></a>";
						
						widget = new HTML(html);
					}
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
							if(GwtClientHelper.hasString(s)){
								widget = new HTML(s);
							}
						}
						break;
					case ProfileAttribute.LIST:
						if(attr.getValue() != null) {
							@SuppressWarnings("unchecked")
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
										String val = "..";
										if(valItem.getValue() != null) {
											if(GwtClientHelper.hasString(valItem.getValue().toString())){
												val = valItem.getValue().toString();
											}
										}
										
										((FlowPanel)widget).add(new Label(val));
									}
								}
							}
						} 
						break;
					default: 
						widget = new Label("..");
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
	
	/**
	 * Callback interface to interact with the profile attribute widget
	 * asynchronously after it loads. 
	 */
	public interface ProfileAttributeWidgetClient {
		void onSuccess(ProfileAttributeWidget paw, int row);
		void onUnavailable();
	}

	/**
	 * Loads the ProfileAttributeWidget split point and returns an
	 * instance of it via the callback.
	 * 
	 * @param attr
	 * @param editMode
	 * @param row		-1 will simply cause the code to be fetched.
	 * @param pawClient
	 */
	public static void createAsync(final ProfileAttribute attr, final boolean editMode, final int row, final ProfileAttributeWidgetClient pawClient) {
		GWT.runAsync(ProfileAttributeWidget.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				ProfileAttributeWidget paw;
				if ((-1) != row)
				     paw = new ProfileAttributeWidget(attr, editMode);
				else paw = null;
				pawClient.onSuccess(paw, row);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_ProfileAttributeWidget());
				pawClient.onUnavailable();
			}
		});
	}

	/**
	 * Causes the code split for the ProfileAttributeWidget to be
	 * fetched.
	 */
	public static void prefetch (ProfileAttributeWidgetClient pawClient) {
		// If we weren't given a ProfileAttributeWidgetClient...
		if (null == pawClient) {
			// ...create one we can use.
			pawClient = new ProfileAttributeWidgetClient() {				
				@Override
				public void onUnavailable() {
				}
				
				@Override
				public void onSuccess(ProfileAttributeWidget paw, int row) {
				}
			};
		}
		
		// A row of -1 will simply cause the code to be fetched.
		createAsync(null, false, -1, pawClient);
	}
	
	public static void prefetch() {
		prefetch(null);
	}
}

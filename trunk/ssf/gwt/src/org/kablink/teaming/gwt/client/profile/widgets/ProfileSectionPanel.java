/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

import java.util.Iterator;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.profile.ProfileRequestInfo;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.WidgetCollection;

/**
 * ?
 * 
 * @author ?
 */
public abstract class ProfileSectionPanel extends FlowPanel {
	protected ProfileRequestInfo 		profileRequestInfo; // Initial values passed 
	protected GwtRpcServiceAsync	rpcService;			//
	protected GwtTeamingMessages	messages; 			// The menu's messages.
	protected Label 				headingLabel;
	protected String 				sectionTitle;
	protected boolean               selectedMore;
	private Image 					expandImage;
	private Image					collapseImage;
	private FlowPanel 				titlePanel;
	@SuppressWarnings("unused")
	private SideBarAnchor 			sbA;
	
	public ProfileSectionPanel(ProfileRequestInfo requestInfo, String title) {
		messages			= GwtTeaming.getMessages();
		profileRequestInfo 	= requestInfo;
		sectionTitle        = title;
		
		createTitleAreaWidget();
	}
	
	public Label getHeadingLabel() {
		return headingLabel;
	}
	
	protected void addContentWidget(Widget w, boolean visible) {
		add(w);
		w.setVisible(visible);
	}

	protected void showExpandButton() {
		collapseImage.setVisible(false);
		expandImage.setVisible(true);
	}

	protected void showCollapseButton() {
		expandImage.setVisible(false);
		collapseImage.setVisible(true);
	}
	
	protected void createTitleAreaWidget(){
		//add a title to the section
		if(sectionTitle !=null) {
			titlePanel = new FlowPanel();
			titlePanel.addStyleName("profileSectionTitle");
			add(titlePanel);
			
			headingLabel = new Label(sectionTitle);
			headingLabel.addStyleName("sideBarTitle");
			titlePanel.add(headingLabel);
			
			expandImage = new Image(GwtTeaming.getImageBundle().expand16());
			expandImage.addStyleName("sideBarTitle");
			expandImage.setTitle("Expand");
			expandImage.setVisible(false);
			expandImage.addClickHandler(new SelectedMoreClickHandler());
			titlePanel.add(expandImage);

			collapseImage = new Image(GwtTeaming.getImageBundle().collapse16());
			collapseImage.addStyleName("sideBarTitle");
			collapseImage.setTitle("Collapse");
			collapseImage.setVisible(false);
			collapseImage.addClickHandler(new SelectedMoreClickHandler());
			titlePanel.add(collapseImage);
		}
	}
	
	/**
	 * Clear all of the widgets expect the title area widget
	 */
	protected void clearWidgets() {
		WidgetCollection widgets = this.getChildren();
		int count = (widgets != null) ? widgets.size() : 0;
		
		for(int index = 1; index < count; index++ ){
			this.remove(index);
		}
	}
	
	/*
	 * Inner class that handles clicks on the more link
	 */
	private class SelectedMoreClickHandler implements ClickHandler {
		/**
		 * Called when the user clicks on a team.
		 * @param event
		 */
		@Override
		public void onClick(ClickEvent event) {
			if(selectedMore) {
				selectedMore = false;
				collapse();
			} else {
				selectedMore = true;
				expand();
			}
		}
	}
	
	protected void expand() {
		WidgetCollection widgets = getChildren();
		Iterator<Widget> wi = widgets.iterator();
		
		for(;wi.hasNext();){
			Widget w = wi.next();
			w.setVisible(true);
		}
		
		showCollapseButton();
	}
	protected void collapse() {
		WidgetCollection widgets = getChildren();
		Iterator<Widget> wi = widgets.iterator();
		
		int count = 0;
		for(;wi.hasNext();){
			Widget w = wi.next();
			if(count < 5){
				w.setVisible(true);
			} else {
				w.setVisible(false);
			}
			count++;
		}
		
		showExpandButton();
	}
}

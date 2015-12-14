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

package org.kablink.teaming.gwt.client.profile;

import org.kablink.teaming.gwt.client.profile.widgets.ProfileAttributeWidget;
import org.kablink.teaming.gwt.client.profile.widgets.ProfileAttributeWidget.ProfileAttributeWidgetClient;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ProfileClientUtil {
	
	/**
	 * Create the Profile Main Content Section, this creates the grid that
	 * should hold all of the user attribute name value pairs
	 * 
	 * @param cat
	 * @param grid
	 * @param rowCount
	 * @return
	 */
	public static int createProfileInfoSection(final ProfileCategory cat, final Grid grid, int rowCount, boolean isEditable, boolean showHeading) {
		int row = rowCount;
		
		if(showHeading) {
			Label sectionHeader = new Label(cat.getTitle());
			sectionHeader.setStyleName("sectionHeading");

			grid.insertRow(row);
			grid.setWidget(row, 0, sectionHeader);

			// remove the bottom border from the section heading titles
			grid.getCellFormatter().setStyleName(row, 0, "sectionHeadingRBB");
			grid.getCellFormatter().setStyleName(row, 1, "sectionHeadingRBB");
			grid.getCellFormatter().setStyleName(row, 2, "sectionHeadingRBB");
			row = row + 1;
		}

		for (ProfileAttribute attr : cat.getAttributes()) {

			if(attr == null) {
				continue;
			}
			
			if(attr.getDataName().equals("picture")){
				continue;
			}
			
			Label title = new Label(attr.getTitle() + ":");
			title.setStyleName("attrLabel");
			grid.insertRow(row);
			grid.setWidget(row, 0, title);
			
			ProfileAttributeWidget.createAsync(attr, isEditable, row, new ProfileAttributeWidgetClient() {				
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(ProfileAttributeWidget paw, int row) {
					Widget value = paw.getWidget();

					grid.setWidget(row, 1, value);
					grid.getCellFormatter().setWidth(row, 1, "70%");
					grid.getCellFormatter().setHorizontalAlignment(row, 1,
							HasHorizontalAlignment.ALIGN_LEFT);
				}
			});

			row = row + 1;
		}

		return row;
	}

	
	/**
	 * Uses JavaScript native method to URI encode a string.
	 * 
	 * @param s
	 * 
	 * @return
	 */
	public static native String jsLaunchMiniBlog(String userId,int page, boolean popup) /*-{
		return window.top.ss_viewMiniBlog(userId, page, popup);
	}-*/;
	
}

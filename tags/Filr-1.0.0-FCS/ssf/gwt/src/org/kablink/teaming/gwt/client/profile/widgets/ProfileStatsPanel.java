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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.profile.ProfileRequestInfo;
import org.kablink.teaming.gwt.client.profile.ProfileStats;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

/**
 * ?
 * 
 * @author nbjensen@novell.com
 */
public class ProfileStatsPanel extends Composite {
	private int row = 0;
	private FlowPanel mainPanel;
	private Grid grid;
	private ProfileRequestInfo profileRequestInfo;
	private InlineLabel quotaUsedValueLabel;
	
	public ProfileStatsPanel(ProfileRequestInfo requestInfo) {
		
		
		this.profileRequestInfo = requestInfo;
		mainPanel = new FlowPanel();
		mainPanel.addStyleName("user_stats");
		
		// ...its content panel...
		grid = new Grid();
		grid.setCellSpacing(0);
		grid.setCellPadding(0);
		grid.resizeColumns(2);
		grid.setStyleName("statsTable");
		mainPanel.add(grid);
		
		initWidget(mainPanel);
	}

	/**
	 * Add the user stats to the side panel
	 */
	public void addStats(ProfileStats stats) {

		//addStat(grid, "Entries", stats.getEntries());
		//addStat(grid, "Following:", Integer.toString(stats.getTrackedCnt()) );

		//if the quotas enable and is the owner or the admin then can see the quota
		if(profileRequestInfo.isQuotasEnabled() && (profileRequestInfo.isOwner()) ) {

			String quotaTitle = GwtTeaming.getMessages().profileDataQuota();
			Label quotaTitleLabel = new Label(quotaTitle);
			InlineLabel quotaValueLabel = new InlineLabel(GwtTeaming.getMessages().profileQuotaMegaBytes(profileRequestInfo.getQuotasUserMaximum()));
			quotaValueLabel.addStyleName( "bold" );
			
			grid.insertRow(row);
			grid.setWidget(row, 0, quotaTitleLabel);
			grid.setWidget(row, 1, quotaValueLabel);
			row = row + 1;

			String usedTitle = GwtTeaming.getMessages().profileQuotaUsed();
			Label quotaUsedLabel = new Label(usedTitle);
			quotaUsedValueLabel = new InlineLabel(GwtTeaming.getMessages().profileQuotaMegaBytes(profileRequestInfo.getQuotasDiskSpacedUsed()));
			quotaUsedValueLabel.addStyleName( "bold" );
			
			grid.insertRow(row);
			grid.setWidget(row, 0, quotaUsedLabel);
			grid.setWidget(row, 1, quotaUsedValueLabel);
			row = row + 1;
		}
	}

	public void updateQuota(String usedQuota) {
		quotaUsedValueLabel.setText(GwtTeaming.getMessages().profileQuotaMegaBytes(usedQuota));
	}
}

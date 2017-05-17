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
package org.kablink.teaming.gwt.client.datatable;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.GroupType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;

/**
 * Data table cell that represents a type of group.
 * 
 * @author drfoster@novell.com
 */
public class GroupTypeCell extends AbstractCell<GroupType> {
	/**
	 * Constructor method.
	 */
	public GroupTypeCell() {
		// Initialize the super class.
		super();
	}

	/**
	 * Returns the title string to add to a group type <IMG>.
	 * 
	 * @param groupType
	 * 
	 * @return
	 */
	public static String getGroupTypeAlt(GroupType groupType) {
		String reply;
		GwtTeamingMessages messages = GwtTeaming.getMessages();
		if (groupType.isAdmin()) {
			switch (groupType.getGroupClass()) {
			case INTERNAL_LDAP:    reply = messages.vibeDataTable_Alt_Ldap_GroupAdmin();  break;
			case INTERNAL_SYSTEM:  reply = messages.vibeDataTable_Alt_System_Group();     break;
			case INTERNAL_LOCAL:   reply = messages.vibeDataTable_Alt_Local_GroupAdmin(); break;
			default:               reply = messages.vibeDataTable_Alt_UnknownGroupType(); break;
			}
		}
		else {
			switch (groupType.getGroupClass()) {
			case EXTERNAL_LDAP:    reply = messages.vibeDataTable_Alt_Ldap_ExternalGroup();  break;
			case EXTERNAL_LOCAL:   reply = messages.vibeDataTable_Alt_Local_ExternalGroup(); break;
			case INTERNAL_LDAP:    reply = messages.vibeDataTable_Alt_Ldap_Group();          break;
			case INTERNAL_SYSTEM:  reply = messages.vibeDataTable_Alt_System_Group();        break;
			case INTERNAL_LOCAL:   reply = messages.vibeDataTable_Alt_Local_Group();         break;
			default:               reply = messages.vibeDataTable_Alt_UnknownGroupType();    break;
			}
		}
		return reply;
	}
	
	/**
	 * Called to render an instance of this cell.
	 * 
	 * @param context
	 * @param groupType
	 * @param sb
	 * 
	 * Overrides AbstractCell.render()
	 */
	@Override
	public void render(Context context, GroupType groupType, SafeHtmlBuilder sb) {
		// If we weren't given a GroupType...
		if (null == groupType) {
			// ...bail.  Cell widgets can pass null to cells if the
			// ...underlying data contains a null, or if the data
			// ...arrives out of order.
			GwtClientHelper.renderEmptyHtml(sb);
			return;
		}

		// Create the HTML for the group type image...
		ImageResource ir = GwtClientHelper.getGroupTypeImage(groupType);
		Image i = GwtClientHelper.buildImage(ir.getSafeUri().asString(), getGroupTypeAlt(groupType));
		i.addStyleName("vibe-dataTableGroupType-image");
		VibeFlowPanel groupTypePanel = new VibeFlowPanel();
		groupTypePanel.addStyleName("vibe-dataTableGroupType-panel");
		groupTypePanel.add(i);
		VibeFlowPanel html = new VibeFlowPanel();
		html.add(groupTypePanel);
		
		// ...and render that into the cell.
		SafeHtml rendered = SafeHtmlUtils.fromTrustedString(html.getElement().getInnerHTML());
		sb.append(rendered);
	}
}

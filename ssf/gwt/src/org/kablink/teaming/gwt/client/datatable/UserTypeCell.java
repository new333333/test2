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
package org.kablink.teaming.gwt.client.datatable;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.UserType;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;

/**
 * Data table cell that represents a type of user.
 * 
 * @author drfoster@novell.com
 */
public class UserTypeCell extends AbstractCell<UserType> {
	/**
	 * Constructor method.
	 */
	public UserTypeCell() {
		// Initialize the super class.
		super();
	}

	/**
	 * Returns the title string to add to a user type <IMG>.
	 * 
	 * @param userType
	 * 
	 * @return
	 */
	public static String getUserTypeAlt(UserType userType) {
		GwtTeamingMessages messages = GwtTeaming.getMessages();
		String reply;
		switch (userType) {
		case EXTERNAL_GUEST:          reply = messages.vibeDataTable_Alt_ExternalUser_Guest();        break;
		case EXTERNAL_OTHERS:         reply = messages.vibeDataTable_Alt_ExternalUser_Others();       break;
		case INTERNAL_LDAP:           reply = messages.vibeDataTable_Alt_InternalUser_LDAP();         break;
		case INTERNAL_PERSON_ADMIN:   reply = messages.vibeDataTable_Alt_InternalUser_PersonAdmin();  break;
		case INTERNAL_PERSON_OTHERS:  reply = messages.vibeDataTable_Alt_InternalUser_PersonOthers(); break;
		case INTERNAL_SYSTEM:         reply = messages.vibeDataTable_Alt_InternalUser_System();       break;
		default:                      reply = messages.vibeDataTable_Alt_UnknownUser();               break;
		}
		return reply;
	}
	
	/**
	 * Returns the ImageResource to use for a user type <IMG>.
	 * 
	 * @param userType
	 * 
	 * @return
	 */
	public static ImageResource getUserTypeImage(UserType userType) {
		GwtTeamingDataTableImageBundle images = GwtTeaming.getDataTableImageBundle();
		ImageResource reply;
		switch (userType) {
		case EXTERNAL_GUEST:          reply = images.externalUser_Guest();        break;
		case EXTERNAL_OTHERS:         reply = images.externalUser_Others();       break;
		case INTERNAL_LDAP:           reply = images.internalUser_LDAP();         break;
		case INTERNAL_PERSON_ADMIN:   reply = images.internalUser_PersonAdmin();  break;
		case INTERNAL_PERSON_OTHERS:  reply = images.internalUser_PersonOthers(); break;
		case INTERNAL_SYSTEM:         reply = images.internalUser_System();       break;
		default:                      reply = images.unknownUser();               break;
		}
		return reply;		
	}
	
	/**
	 * Called to render an instance of this cell.
	 * 
	 * @param context
	 * @param userType
	 * @param sb
	 * 
	 * Overrides AbstractCell.render()
	 */
	@Override
	public void render(Context context, UserType userType, SafeHtmlBuilder sb) {
		// If we weren't given a UserType...
		if (null == userType) {
			// ...bail.  Cell widgets can pass null to cells if the
			// ...underlying data contains a null, or if the data
			// ...arrives out of order.
			GwtClientHelper.renderEmptyHtml(sb);
			return;
		}

		// Create the HTML for the user type image...
		ImageResource ir = getUserTypeImage(userType);
		Image i = GwtClientHelper.buildImage(ir.getSafeUri().asString(), getUserTypeAlt(userType));
		i.addStyleName("vibe-dataTableUserType-image");
		VibeFlowPanel userTypePanel = new VibeFlowPanel();
		userTypePanel.addStyleName("vibe-dataTableUserType-panel");
		userTypePanel.add(i);
		VibeFlowPanel html = new VibeFlowPanel();
		html.add(userTypePanel);
		
		// ...and render that into the cell.
		SafeHtml rendered = SafeHtmlUtils.fromTrustedString(html.getElement().getInnerHTML());
		sb.append(rendered);
	}
}

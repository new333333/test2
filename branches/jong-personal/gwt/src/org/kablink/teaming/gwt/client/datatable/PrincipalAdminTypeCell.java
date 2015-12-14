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
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.PrincipalAdminType;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;

/**
 * Data table cell that represents a type of principal.
 * 
 * @author drfoster@novell.com
 */
public class PrincipalAdminTypeCell extends AbstractCell<PrincipalAdminType> {
	/**
	 * Constructor method.
	 */
	public PrincipalAdminTypeCell() {
		// Initialize the super class.
		super();
	}

	/**
	 * Returns the title string to add to a principal type <IMG>.
	 * 
	 * @param pat
	 * 
	 * @return
	 */
	public static String getPrincipalAdminTypeAlt(PrincipalAdminType pat) {
		GwtTeamingMessages messages = GwtTeaming.getMessages();
		String reply = null;
		switch (pat.getPrincipalType()) {
		// External users.
		case EXTERNAL_GUEST:                  reply = messages.vibeDataTable_Alt_ExternalUser_Guest();             break;
		case EXTERNAL_LDAP:                   reply = messages.vibeDataTable_Alt_ExternalUser_LDAP();              break;
		case EXTERNAL_OPEN_ID:
		case EXTERNAL_OTHERS:                 reply = messages.vibeDataTable_Alt_ExternalUser_Others();            break;
		
		// Internal system users.
		case INTERNAL_PERSON_ADMIN:           reply = messages.vibeDataTable_Alt_InternalUser_PersonAdmin();       break;
		
		// Internal local users.
		case INTERNAL_LDAP:
		case INTERNAL_PERSON_OTHERS:
		case INTERNAL_SYSTEM:  {
			if (pat.isAdmin()) {
				switch (pat.getPrincipalType()) {
				case INTERNAL_LDAP:           reply = messages.vibeDataTable_Alt_InternalUser_LDAPAdmin();         break;
				case INTERNAL_PERSON_OTHERS:  reply = messages.vibeDataTable_Alt_InternalUser_PersonOthersAdmin(); break;
				case INTERNAL_SYSTEM:         reply = messages.vibeDataTable_Alt_InternalUser_SystemAdmin();       break;
				}
			}
			else {
				switch (pat.getPrincipalType()) {
				case INTERNAL_LDAP:           reply = messages.vibeDataTable_Alt_InternalUser_LDAP();              break;
				case INTERNAL_PERSON_OTHERS:  reply = messages.vibeDataTable_Alt_InternalUser_PersonOthers();      break;
				case INTERNAL_SYSTEM:         reply = messages.vibeDataTable_Alt_InternalUser_System();            break;
				}
			}
			break;
		}
		
		// Groups.
		case EXTERNAL_LDAP_GROUP:             reply = messages.vibeDataTable_Alt_Ldap_ExternalGroup();             break;
		case EXTERNAL_LOCAL_GROUP:            reply = messages.vibeDataTable_Alt_Local_ExternalGroup();            break;
		case INTERNAL_LDAP_GROUP:
		case INTERNAL_LOCAL_GROUP:
		case SYSTEM_GROUP:  {
			if (pat.isAdmin()) {
				switch (pat.getPrincipalType()) {
				case INTERNAL_LDAP_GROUP:     reply = messages.vibeDataTable_Alt_Ldap_GroupAdmin();                break;
				case INTERNAL_LOCAL_GROUP:    reply = messages.vibeDataTable_Alt_Local_GroupAdmin();               break;
				case SYSTEM_GROUP:            reply = messages.vibeDataTable_Alt_System_GroupAdmin();              break;
				}
			}
			else {
				switch (pat.getPrincipalType()) {
				case INTERNAL_LDAP_GROUP:     reply = messages.vibeDataTable_Alt_Ldap_Group();                     break;
				case INTERNAL_LOCAL_GROUP:    reply = messages.vibeDataTable_Alt_Local_Group();                    break;
				case SYSTEM_GROUP:            reply = messages.vibeDataTable_Alt_System_Group();                   break;
				}
			}
			break;
		}
		
		// Unknown.
		default:                              reply = messages.vibeDataTable_Alt_UnknownUser();                    break;
		}
		return reply;
	}
	
	/**
	 * Returns the ImageResource to use for a principal type <IMG>.
	 * 
	 * @param pat
	 * 
	 * @return
	 */
	public static ImageResource getPrincipalAdminTypeImage(PrincipalAdminType pat) {
		GwtTeamingDataTableImageBundle images = GwtTeaming.getDataTableImageBundle();
		ImageResource reply = null;
		switch (pat.getPrincipalType()) {
		// External users.
		case EXTERNAL_GUEST:                  reply = images.externalUser_Guest();              break;
		case EXTERNAL_LDAP:                   reply = images.externalUser_LDAP();               break;
		case EXTERNAL_OPEN_ID:
		case EXTERNAL_OTHERS:                 reply = images.externalUser_Others();             break;
		
		// Internal system users.
		case INTERNAL_PERSON_ADMIN:           reply = images.internalUser_PersonAdminBuiltIn(); break;
		
		// Internal local users.
		case INTERNAL_LDAP:
		case INTERNAL_PERSON_OTHERS:
		case INTERNAL_SYSTEM:  {
			if (pat.isAdmin()) {
				switch (pat.getPrincipalType()) {
				case INTERNAL_LDAP:           reply = images.internalUser_LDAPAdmin();          break;
				case INTERNAL_PERSON_OTHERS:  reply = images.internalUser_PersonAdmin();        break;
				case INTERNAL_SYSTEM:         reply = images.internalUser_SystemAdmin();        break;
				}
			}
			else {
				switch (pat.getPrincipalType()) {
				case INTERNAL_LDAP:           reply = images.internalUser_LDAP();               break;
				case INTERNAL_PERSON_OTHERS:  reply = images.internalUser_PersonOthers();       break;
				case INTERNAL_SYSTEM:         reply = images.internalUser_System();             break;
				}
			}
			break;
		}

		// Groups.
		case EXTERNAL_LDAP_GROUP:             reply = images.groupType_LDAPExternal();          break;
		case EXTERNAL_LOCAL_GROUP:            reply = images.groupType_LocalExternal();         break;
		case INTERNAL_LDAP_GROUP:
		case INTERNAL_LOCAL_GROUP:
		case SYSTEM_GROUP:  {
			if (pat.isAdmin()) {
				switch (pat.getPrincipalType()) {
				case INTERNAL_LDAP_GROUP:     reply = images.groupType_LDAPAdmin();             break;
				case INTERNAL_LOCAL_GROUP:    reply = images.groupType_LocalAdmin();            break;
				case SYSTEM_GROUP:            reply = images.groupType_SystemAdmin();           break;
				}
			}
			else {
				switch (pat.getPrincipalType()) {
				case INTERNAL_LDAP_GROUP:     reply = images.groupType_LDAP();                  break;
				case INTERNAL_LOCAL_GROUP:    reply = images.groupType_Local();                 break;
				case SYSTEM_GROUP:            reply = images.groupType_System();                break;
				}
			}
			break;
		}
		
		// Unknown.
		default:                              reply = images.unknownUser();                     break;
		}
		return reply;		
	}
	
	/**
	 * Called to render an instance of this cell.
	 * 
	 * @param context
	 * @param pt
	 * @param sb
	 * 
	 * Overrides AbstractCell.render()
	 */
	@Override
	public void render(Context context, PrincipalAdminType pt, SafeHtmlBuilder sb) {
		// If we weren't given a PrincipalAdminType...
		if (null == pt) {
			// ...bail.  Cell widgets can pass null to cells if the
			// ...underlying data contains a null, or if the data
			// ...arrives out of order.
			GwtClientHelper.renderEmptyHtml(sb);
			return;
		}

		// Create the HTML for the principal type image...
		ImageResource ir = getPrincipalAdminTypeImage(pt);
		Image i = GwtClientHelper.buildImage(ir.getSafeUri().asString(), getPrincipalAdminTypeAlt(pt));
		i.addStyleName("vibe-dataTablePrincipalAdminType-image");
		VibeFlowPanel ugtPanel = new VibeFlowPanel();
		ugtPanel.addStyleName("vibe-dataTablePrincipalAdminType-panel");
		ugtPanel.add(i);
		VibeFlowPanel html = new VibeFlowPanel();
		html.add(ugtPanel);
		
		// ...and render that into the cell.
		SafeHtml rendered = SafeHtmlUtils.fromTrustedString(html.getElement().getInnerHTML());
		sb.append(rendered);
	}
}

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
package org.kablink.teaming.gwt.client.admin;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class defines all the possible types of administration actions
 * that a user can perform.
 * 
 * @author drfoster@novell.com
 */
public enum AdminAction implements IsSerializable {
	ACCESS_CONTROL_FOR_ZONE_ADMIN_FUNCTIONS("Access control for zone administration functions"),
	ADD_USER("Add user"),
	CONFIGURE_ADHOC_FOLDERS("Configure adhoc folder"),
	CONFIGURE_ANTIVIRUS("Configure anti virus settings"),
	CONFIGURE_EMAIL("Configure email"),
	CONFIGURE_EMAIL_TEMPLATES("Configure email templates"),
	CONFIGURE_FILE_SYNC_APP("Configure File Sync App"),
	CONFIGURE_FILE_VERSION_AGING("Configure file version aging"),
	CONFIGURE_FOLDER_INDEX("Configure folder index"),
	CONFIGURE_FOLDER_SEARCH_NODES("Configure folder search nodes"),
	CONFIGURE_FOLDER_UPDATE_LOGS("Configure folder update logs"),
	CONFIGURE_HOME_PAGE("Configure home page"),
	CONFIGURE_MOBILE_ACCESS("Configure mobile access"),
	CONFIGURE_MOBILE_APPS("Configure Mobile Apps"),
	CONFIGURE_NAME_COMPLETION("Configure name completion settings"),
	CONFIGURE_PASSWORD_POLICY("Configure password policy"),
	CONFIGURE_ROLE_DEFINITIONS("Configure role definitions"),
	CONFIGURE_SCHEDULE("Configure weekend and holiday schedule"),
	CONFIGURE_SEARCH_INDEX("Configure search index"),
	CONFIGURE_SHARE_SETTINGS("Configure share settings"),
	CONFIGURE_TELEMETRY("Configure telemetry settings"),
	CONFIGURE_USER_ACCESS("Configure user access"),
	FORM_VIEW_DESIGNER("Form/View Designer"),
	IMPORT_PROFILES("Import profiles"),
	KEYSHIELD_CONFIG("KeyShield configuration"),
	NET_FOLDER_GLOBAL_SETTINGS("Net Folder Global Settings"),
	LDAP_CONFIG("LDAP configuration"),
	MANAGE_ADMINISTRATORS("Manage administrators"),
	MANAGE_APPLICATIONS("Manage applications"),
	MANAGE_APPLICATION_GROUPS("Manage application groups"),
	MANAGE_DATABASE_PRUNE("Manage database prune"),
	MANAGE_DEFAULT_USER_SETTINGS("Manage default user settings"),
	MANAGE_EXTENSIONS("Manage extensions"),
	MANAGE_GROUPS("Manage groups"),
	MANAGE_LICENSE("Manage license"),
	MANAGE_NET_FOLDERS("Manage net folders"),
	MANAGE_QUOTAS("Manage quotas"),
	MANAGE_FILE_UPLOAD_LIMITS("Manage file upload limits"),
	MANAGE_MOBILE_DEVICES("Manage mobile devices"),
	MANAGE_PROXY_IDENTITIES("Manage Proxy Identities"),
	MANAGE_RESOURCE_DRIVERS("Manage resource drivers"),
	MANAGE_SHARE_ITEMS("Manage share items"),
	MANAGE_TEAMS("Manage teams"),
	MANAGE_USER_ACCOUNTS("Manage user accounts"),
	MANAGE_USER_VISIBILITY("Manage user visibility"),
	MANAGE_WORKSPACE_AND_FOLDER_TEMPLATES("Manage workspace and folder templates"),
	MANAGE_ZONES("Manage zones"),
	REPORT_ACTIVITY_BY_USER("Report: activity by user"),
	REPORT_CREDITS("Report: credits"),
	REPORT_DATA_QUOTA_EXCEEDED("Report: data quota exceeded"),
	REPORT_DATA_QUOTA_HIGHWATER_EXCEEDED("Report: data quota highwater exceeded"),
	REPORT_DISK_USAGE("Report: disk usage"),
	REPORT_EMAIL("Report: email"),
	REPORT_LICENSE("Report: license"),
	REPORT_LOGIN("Report: login"),
	REPORT_USER_ACCESS("Report: user access"),
	REPORT_XSS("Report: xss"),
	REPORT_VIEW_CHANGELOG("Report: view change log"),
	REPORT_VIEW_CREDITS("Report: view credits"),
	REPORT_VIEW_SYSTEM_ERROR_LOG("Report: view system error log"),
	RUN_A_REPORT("Reports: select a report to run"),
	SITE_BRANDING("Site branding"),
	DESKTOP_SITE_BRANDING("Site branding for Desktop Applications"),
	MOBILE_SITE_BRANDING("Site branding for Mobile Applications"),

	// This is used as a default case to store a AdminAction when
	// there isn't a real value to store.
	UNDEFINED("Undefined Admin Action - Should Never Be Triggered");

	private final String m_unlocalizedDesc;	//
	
	/*
	 * Constructor method.
	 */
	private AdminAction(String unlocalizedDesc) {
		m_unlocalizedDesc = unlocalizedDesc;
	}
	
	/**
	 * Returns an unlocalized description of the administrative action.
	 * 
	 * @return
	 */
	public String getUnlocalizedDesc() {
		return m_unlocalizedDesc;
	}
	
	/**
	 * Converts the ordinal value of an AdminAction to its enumeration
	 * equivalent.
	 * 
	 * @param actionOrdinal
	 * 
	 * @return
	 */
	public static AdminAction getEnum(int actionOrdinal) {
		AdminAction action;
		try {
			action = AdminAction.values()[actionOrdinal];
		}
		catch (ArrayIndexOutOfBoundsException e) {
			action = AdminAction.UNDEFINED;
		}
		return action;
	}
}

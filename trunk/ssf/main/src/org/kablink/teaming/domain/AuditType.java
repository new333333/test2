/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
package org.kablink.teaming.domain;

/**
 * @author Jong
 *
 */
public enum AuditType {

	unknown((short)-1),
	view((short)1), // viewed a data item
	add((short)2), // added a data item
	modify((short)3), // modified a data item
	delete((short)4), // deleted a data item
	preDelete((short)5), // pre-deleted a data item
	restore((short)6), // restored a data item
	workflow((short)7), // workflow change
	login((short)8), // user, client, or user agent login
	download((short)9), // user, client, or user agent download
	userStatus((short)10), // ?
	//token((short)11), // application-scoped token generation
	acl((short)12), // ACL change on a data item
	shareAdd((short)13), // added a share item - dummy type only used in activity report
	shareModify((short)14), // modified a share item - dummy type only used in activity report
	shareDelete((short)15), // deleted a share item - dummy type only used in activity report
	rename((short)16); // renamed (and possibly also modified) a data item

	short value;
	
	AuditType(short value) {
		this.value = value;
	}
	
	public short getValue() {
		return value;
	}
	
	public static AuditType valueOf(short value) {
		switch(value) {
		case -1: return AuditType.unknown;
		case 1: return AuditType.view;
		case 2: return AuditType.add;
		case 3: return AuditType.modify;
		case 4: return AuditType.delete;
		case 5: return AuditType.preDelete;
		case 6: return AuditType.restore;
		case 7: return AuditType.workflow;
		case 8: return AuditType.login;
		case 9: return AuditType.download;
		case 10: return AuditType.userStatus;
		//case 11: return AuditType.token;
		case 12: return AuditType.acl;
		case 13: return AuditType.shareAdd;
		case 14: return AuditType.shareModify;
		case 15: return AuditType.shareDelete;
		case 16: return AuditType.rename;
		default: throw new IllegalArgumentException("Invalid db value " + value + " for enum AuditType");
		}
	}
}

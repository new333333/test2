/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.dao.impl;

/**
 * Parameter names are used for the implementation of Hibernate
 * based DAO functionality and typically used to get or set
 * parameters on query object, etc. 
 * For example, "forumName" and zoneName in the following query string:
 * from Binder forum where forum.name=:forumName and forum.zone.id=:zoneName
 * 
 * @author Jong Kim
 *
 */
public class ParameterNames {
    public static final String USER_NAME = "userName";
    public static final String LOWER_USER_NAME = "lowerUserName";
    public static final String UPPER_USER_NAME = "upperUserName";
    public static final String INTERNAL_ID = "internalId";
    public static final String ZONE_ID="zoneId";
    public static final String NAME="name";
    public static final String EMAIL_ADDRESS="emailAddress";
    public static final String LDAP_UGID="ldapGuid";
    public static final String OBJECT_SID="objectSid";
    public static final String FOREIGN_NAME="foreignName";
    public static final String SAMACCOUNTNAME="samAccountName";
    public static final String OPENID_IDENTITY="openidIdentity";
    public static final String DOMAIN_NAME="domainName";
    public static final String TYPELESS_DN="typelessDN";
}


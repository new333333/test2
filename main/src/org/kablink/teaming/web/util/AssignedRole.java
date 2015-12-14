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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 *
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.web.util;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Principal;

/**
 * User: David
 * Date: 11/21/13
 * Time: 12:43 PM
 */
public class AssignedRole {
    public enum RoleType implements IsSerializable
    {
        ShareExternal (ObjectKeys.FUNCTION_ALLOW_SHARING_EXTERNAL_INTERNALID, true, false),
        ShareForward (ObjectKeys.FUNCTION_ALLOW_SHARING_FORWARD_INTERNALID, true, false),
        ShareInternal (ObjectKeys.FUNCTION_ALLOW_SHARING_INTERNAL_INTERNALID, true, false),
        SharePublic (ObjectKeys.FUNCTION_ALLOW_SHARING_PUBLIC_INTERNALID, true, false),
        SharePublicLinks (ObjectKeys.FUNCTION_ALLOW_SHARING_PUBLIC_LINKS_INTERNALID, true, false),
        EnableShareExternal (ObjectKeys.FUNCTION_ENABLE_EXTERNAL_SHARING_INTERNALID, false, true),
        EnableShareForward (ObjectKeys.FUNCTION_ENABLE_FORWARD_SHARING_INTERNALID, false, true),
        EnableShareInternal (ObjectKeys.FUNCTION_ENABLE_INTERNAL_SHARING_INTERNALID, false, true),
        EnableSharePublic (ObjectKeys.FUNCTION_ENABLE_PUBLIC_SHARING_INTERNALID, false, true),
        EnableShareWithAllInternal (ObjectKeys.FUNCTION_ENABLE_SHARING_ALL_INTERNAL_INTERNALID, false, true),
        EnableShareWithAllExternal (ObjectKeys.FUNCTION_ENABLE_SHARING_ALL_EXTERNAL_INTERNALID, false, true),
        EnableLinkSharing(ObjectKeys.FUNCTION_ENABLE_LINK_SHARING_INTERNALID, false, true),
        AllowAccess (ObjectKeys.FUNCTION_ALLOW_ACCESS_NET_FOLDER_INTERNALID, true, false),
        Unknown (null, false, false);

        private String internalId;
        private boolean applicableToNetFolders;
        private boolean applicableToZoneConfig;

        private RoleType(String internalId, boolean applicableToNetFolders, boolean applicableToZoneConfig) {
            this.internalId = internalId;
            this.applicableToNetFolders = applicableToNetFolders;
            this.applicableToZoneConfig = applicableToZoneConfig;
        }

        public String getInternalId() {
            return internalId;
        }

        public boolean isApplicableToNetFolders() {
            return applicableToNetFolders;
        }

        public boolean isApplicableToZoneConfig() {
            return applicableToZoneConfig;
        }
    }

    private Principal principal;
    private Set<RoleType> roles;

    public AssignedRole(Principal principal) {
        this.principal = principal;
        roles = new LinkedHashSet<RoleType>();
    }

    public Principal getPrincipal() {
        return principal;
    }

    public void addRole(RoleType role) {
        roles.add(role);
    }

    public Set<RoleType> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleType> roles) {
        this.roles = roles;
    }
}

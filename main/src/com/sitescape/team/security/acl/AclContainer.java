/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.security.acl;

import java.util.List;

/**
 * <code>AclContainer</code> contains other objects and is used to specify
 * a default ACL for the objects contained in the container.
 * 
 * Warning: The methods defined in this class are meant to be used by security 
 * manager only. Application should never invoke these methods directly. 
 * 
 * @author Jong Kim
 */
public interface AclContainer extends AclControlled {
    
    /**
     * Returns the id of the acl container. 
     * 
     * @return
     */
    public Long getAclContainerId();
    
    /**
     * Return parent acl container. 
     * 
     * @return
     */
    public AclContainer getParentAclContainer();
    
    /**
     * Return a list of child acl containers. 
     * 
     * @return a list of <code>AclContainer</code>s
     */
    public List getChildAclContainers();
    
    /**
     * Returns a list of child acl-controlled objects that are not themselves
     * acl containers. 
     * <p>
     * This only returns immediate children and does not work recursively. 
     * 
     * @return a list of <code>AclControlled</code>s
     */
    public List getChildAclControlled();
}

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
package org.kablink.teaming.security.function;
import java.util.Set;

import org.kablink.teaming.domain.Principal;
/**
 * 
 * 
 */
public interface WorkArea {
    
    public Long getWorkAreaId();
    
    /**
     * The type of the work area. 
     * The value must be between 1 and 16 characters long.
     * 
     * @return
     */
    public String getWorkAreaType();
    /**
     * Return parent workArea or <code>null</code> if top
     * @return
     */
    public WorkArea getParentWorkArea();
    /**
     * Return true if workArea can inherit function membership
     * @return
     */
    public boolean isFunctionMembershipInheritanceSupported();
    /**
     * Return true if workArea is currently inheritting function membership.
     * @return
     */
    public boolean isFunctionMembershipInherited();
    
    public void setFunctionMembershipInherited(boolean functionMembershipInherited);
    /**
     * Return the id of the owner of the workArea
     * @return
     */
    public Long getOwnerId();
    /**
     * Return the principal that is the owner of the workArea
     * @return
     */
    public Principal getOwner();
    public void setOwner(Principal owner);
    /**
     * Return true if currently inheritty team membership
     * @return
     */
    public boolean isTeamMembershipInherited();
    /**
     * Return the team members ids
     * @return
     */
    public Set<Long> getTeamMemberIds();
    public void setTeamMemberIds(Set<Long> memberIds);
    /**
     * Return the ids of child workAreas
     * @return
     */
    public Set<Long> getChildWorkAreas();

}

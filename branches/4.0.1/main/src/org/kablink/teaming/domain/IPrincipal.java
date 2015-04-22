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
package org.kablink.teaming.domain;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IPrincipal extends PersistentLongId {

    public String getInternalId();
    
    public void setInternalId(String internalId);

    public boolean isReserved();
    
    public boolean isDisabled();

    public void setDisabled(boolean disabled);
    
    public boolean isActive();

    public Long getWorkspaceId();
    
    public void setWorkspaceId(Long workspaceId);

    public Long getZoneId();
    
    public void setZoneId(Long zoneId);

    public String getTheme();

    public void setTheme(String theme);
    
    public String getEmailAddress();
    
    public void setEmailAddress(String address);

    public Map<String, EmailAddress> getEmailAddresses();
 
    public String getEmailAddress(String type);
    
    public void setEmailAddress(String type, String address);
    
    public String getMobileEmailAddress();
    
    public void setMobileEmailAddress(String address);
    
    public String getTxtEmailAddress();
    
    public void setTxtEmailAddress(String address);
    
    public String getName();
    
    public void setName(String name);
 
    public String getNormalTitle();
    
    public String getForeignName();

    public void setForeignName(String foreignName);
     
    public Set computePrincipalIds(GroupPrincipal reservedGroup);
    
    public List getMemberOf();

    public void setMemberOf(Collection groups);

    public Date getMemberOfLastModified();

    public void setMemberOfLastModified(Date date);
    
    public AverageRating getAverageRating();

    public void setAverageRating(AverageRating rating);

    public Long getPopularity();

    public void setPopularity(Long popularity);
 
    public void setIndexMemberOf(List iMemberOf);
    
    public void setIndexEmailAddresses(Map iEmailAddresses);

}

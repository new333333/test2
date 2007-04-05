/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.domain;

import java.util.List;
import java.util.ArrayList;
/**
 * @hibernate.subclass discriminator-value="PROFILES" dynamic-update="true" 
 */
public class ProfileBinder extends Workspace {

    public ProfileBinder() {
    	setType(EntityIdentifier.EntityType.profiles.name());
    	setDefinitionsInherited(false);
    }
	public EntityIdentifier.EntityType getEntityType() {
		return EntityIdentifier.EntityType.profiles;
	}
	
    public List getEntryDefinitions() {
   		return getDefs(Definition.PROFILE_ENTRY_VIEW);
    }
    public List getViewDefinitions() {
   		return getDefs(Definition.PROFILE_VIEW);
    }	
    /**
     * Always returns false.  Overloaded method
     */
    public boolean isDefinitionsInherited() {
    	return false;
    }
    
    public void setDefinitionsInherited(boolean definitionsInherited) {
    	this.definitionsInherited=false;
    }
    public boolean isDefinitionInheritanceSupported() {
    	return false;
    }

}

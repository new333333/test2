package com.sitescape.ef.domain;

import java.util.List;
import java.util.ArrayList;
/**
 * @hibernate.subclass discriminator-value="PROFILES" dynamic-update="true" 
 */
public class ProfileBinder extends Binder {
	private List emptyList = new ArrayList();

    public ProfileBinder() {
    	setType(EntityIdentifier.EntityType.profiles.name());
    	setDefinitionsInherited(false);
    }
    public EntityIdentifier getEntityIdentifier() {
    	return new EntityIdentifier(getId(), EntityIdentifier.EntityType.profiles);
    }
	
	public List getChildAclContainers() {
		// TODO Auto-generated method stub
		return emptyList;
	}

	public List getChildAclControlled() {
		// TODO Auto-generated method stub
		return emptyList;
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

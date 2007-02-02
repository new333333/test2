package com.sitescape.team.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TemplateBinder extends Binder implements Cloneable {
	protected Description tDescription;
	protected String tTitle;
	public EntityIdentifier.EntityType getEntityType() {
		if (definitionType == Definition.FOLDER_VIEW)
			return EntityIdentifier.EntityType.folder;
		else return EntityIdentifier.EntityType.workspace;
	}
    public List getEntryDefinitions() {
    	if (definitionType == Definition.FOLDER_VIEW)
    		return getDefs(Definition.FOLDER_ENTRY);
    	return new ArrayList();
    }
    public List getViewDefinitions() {
		return getDefs(definitionType);
    }
    public List getChildAclContainers() {
        return new ArrayList();
    }
    
    public List getChildAclControlled() {
        return new ArrayList(); // empty
    }
    /**
     * @hibernate.component prefix="tDescription_"
     */
    public Description getTemplateDescription() {
        return this.tDescription;
    }
    public void setTemplateDescription(Description tDescription) {
        if (this.tDescription != null)
        	// try to avoid unecessary updates
        	if (this.tDescription.equals(tDescription)) return;
    	this.tDescription = tDescription; 
    }
  
    public void setTemplateDescription(String tDescriptionText) {
		Description tmp = new Description(tDescriptionText);
    	if (tDescription != null) {
    		if (tDescription.equals(tmp)) return;
    	}
        this.tDescription = tmp; 
    }
    /**
     * @hibernate.property length="128"
     */
    public String getTemplateTitle() {
        return tTitle;
    }
    public void setTemplateTitle(String tTitle) {
    	this.tTitle = tTitle;
    }
    //we always can inherit
    public boolean isFunctionMembershipInherited() {
       return functionMembershipInherited;
    }
    //this is needed for templates, which may inherit from a yet to be determined parent
    public boolean isFunctionMembershipInheritanceSupported() {
    	return true;
    }


    public boolean isDefinitionInheritanceSupported() {
    	if (isRoot()) return true;  //may want config to always inherit
    	return getParentBinder().getEntityType().equals(getEntityType());
    }
    public TemplateBinder clone() {
 	   try {
 		   TemplateBinder other = (TemplateBinder)super.clone();
 		   other.setId(null);
 		   if (definitions != null)
 			   //can copy definitions since they are shared
 			   other.definitions = new ArrayList(definitions);
 		   other.parentBinder=null;
 		   other.binders = null;
 		   other.events = null;
 		   other.attachments = null;
 		   other.customAttributes = null;
 		   other.setNotificationDef(null);
 		   if (workflowAssociations != null)
 			   //can copy workflow associations since they are shared
			   other.workflowAssociations = new HashMap(workflowAssociations);
 		   return other;
 	   }  catch (CloneNotSupportedException e) {
 	        // This shouldn't happen, since we are Cloneable
 	        throw new InternalError("Clone error: " + e.getMessage());
 	   }
    }
}

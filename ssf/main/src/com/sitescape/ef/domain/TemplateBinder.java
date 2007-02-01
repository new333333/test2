package com.sitescape.ef.domain;

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
    public boolean isDefinitionInheritanceSupported() {
    	if (isRoot()) return true;  //may want config to always inherit
    	return getParentBinder().getDefinitionType() == getDefinitionType();
    }
    public TemplateBinder clone() {
 	   try {
 		   TemplateBinder other = (TemplateBinder)super.clone();
 		   other.setId(null);
 		   if (definitions != null)
 			   other.definitions = new ArrayList(definitions);
 		   other.parentBinder=null;
 		   other.binders = null;
 		   other.events = null;
 		   other.attachments = null;
 		   other.customAttributes = null;
 		   
 		   if (workflowAssociations != null)
 			   other.workflowAssociations = new HashMap(workflowAssociations);
 		   return other;
 	   }  catch (CloneNotSupportedException e) {
 	        // This shouldn't happen, since we are Cloneable
 	        throw new InternalError("Clone error: " + e.getMessage());
 	   }
    }
}

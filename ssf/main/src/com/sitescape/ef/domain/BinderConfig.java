package com.sitescape.ef.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import com.sitescape.ef.security.acl.AclControlled;
import com.sitescape.ef.security.acl.AclSet;
import com.sitescape.util.Validator;

/**
 * Represent the definition configuration for a binder.  To be used to quickly
 * setup new binders.
 * @author Janet McCann
 *
 */
public class BinderConfig extends PersistentTimestampObject implements AclControlled {
	private String zoneName, title;
	private int definitionType;
	private PersistentAclSet aclSet; 
    private String iId;
    private SSBlobSerializable definitions,workflows; //assigned by hibernate access="field"
    /**
     * Used by security manager only. Application should NEVER invoke this
     * method directly.  
     * @hibernate.component prefix="acl_" class="com.sitescape.ef.domain.PersistentAclSet" 
     */
    public void setAclSet(AclSet aclSet) {
        this.aclSet = (PersistentAclSet)aclSet;
    }
    /**
     * Used by security manager only. Application should NEVER invoke this
     * method directly.  
     */
    public AclSet getAclSet() {
        return aclSet;
    } 	
    public boolean getInheritAclFromParent() {
        return false;
    }

    public void setInheritAclFromParent(boolean inherit) {
    	//ignore
    }
    public Long getCreatorId() {
    	HistoryStamp creation = getCreation();
    	if(creation != null) {
    		Principal principal = creation.getPrincipal();
    		if (principal != null)
    			return principal.getId();
    	}
    	return null;
    }   
	/**
     * @hibernate.property length="100" not-null="true" node="zoneName"
     */
    public String getZoneName() {
    	return this.zoneName;
    }
    public void setZoneName(String zoneName) {
    	this.zoneName = zoneName;
    }
    /**
     * @hibernate.property length="128"
     */
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
    	this.title = title;
    }
    /**
     * Internal id used to identify default definitions.  This id plus
     * the zoneName are used to locate default definitions.  If we just used the primary key id
     * the zones would need the same default and that may not be desirable.
     * @hibernate.property length="32"
     */
    public String getInternalId() {
    	return this.iId;
    }
    public void setInternalId(String iId) {
    	this.iId = iId;
    }
    /**
     * @hibernate.property
     * @return
     */
    public int getDefinitionType() {
    	return this.definitionType;
    }
    public void setDefinitionType(int definitionType) {
    	this.definitionType = definitionType;
    }
    public List getDefinitionIds() {
    	if (definitions == null) return new ArrayList();
    	return (List)definitions.getValue();
    }
    public void setDefinitionIds(List definitions) {
 	   //immutable type, requires new object
    	this.definitions = new SSBlobSerializable(definitions); 
    }
    public Map getWorkflowIds() {
    	if (workflows == null) return new HashMap();
    	return (Map)workflows.getValue();
    }
   public void setWorkflowIds(Map workflows) {
	   //immutable type, requires new object
	   this.workflows = new SSBlobSerializable(workflows);
   }
 
}

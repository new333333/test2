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
import java.util.Date;

import org.dom4j.Element;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.module.shared.ChangeLogUtils;
import com.sitescape.util.Validator;

/**
 * @hibernate.class table="SS_WorkflowResponses" dynamic-update="true" lazy="false" 
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * @author janet
 */

public class WorkflowResponse {
	protected String id;
	protected String definitionId;
	protected String name;
	protected String response;
	protected AnyOwner owner;
	protected Long responderId;
	protected Date responseDate;
   //no versioning on custom attributes
	/**
	 * @hibernate.id generator-class="uuid.hex" unsaved-value="null"
	 * @hibernate.column name="id" sql-type="char(32)"
	 */    
   public String getId() {
       return id;
   }
   public void setId(String id) {
       this.id = id;
   }
    /**
    * @hibernate.component class="com.sitescape.team.domain.AnyOwner"
    * @return
    */
    public AnyOwner getOwner() {
   		return owner;
   	}
    public void setOwner(AnyOwner owner) {
   		this.owner = owner;
   	}
    public void setOwner(DefinableEntity entity) {
   		owner = new AnyOwner(entity); 		
  	}   	
    /**
     * @hibernate.property access="field" length="64"
     * @return
     */
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    /**
     * The definition that this attribute belongs to
     * @hibernate.property length=32
     * @return
     */
    public String getDefinitionId() {
 	   return definitionId;
    }
    public void setDefinitionId(String definitionId) {
 	   this.definitionId = definitionId;
    }
   /**
     * @hibernate.property length="2000"
     */
    public String getResponse() {
        return this.response;
    }
    public void setResponse(String response) {
        this.response = response;
    }  
    /**
     * @hibernate.property
     * @return
     */
    public Long getResponderId() {
    	return responderId;
    }
    public void setResponderId(Long responderId) {
    	this.responderId = responderId;
    }
    /**
     * @hibernate.property
     * @return
     */
    public Date getResponseDate() {
    	return responseDate;
    }
    public void setResponseDate(Date responseDate) {
    	this.responseDate = responseDate;
    }
    /**
     * Compares objects using the database Id.  This implies objects must be
     * persisted prior to making this call.
     */
    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        //objects can be proxied so don't compare classes.  UUIDS are unique 
        if (obj == null) 
            return false;
        
        if (!(obj instanceof WorkflowResponse)) return false;
        WorkflowResponse o = (WorkflowResponse) obj;
        //assume not persisted yet
        if (o.getId() == null) return false;
        if (getId() == null) return false;
        if (this.id.equals(o.getId()))
            return true;
                
        return false;
    }
    public int hashCode() {
    	return id.hashCode();
    }
	public Element addChangeLog(Element parent) {
		Element element = parent.addElement("workflowResponse");
		element.addAttribute(ObjectKeys.XTAG_ID, getId().toString());
		element.addAttribute(ObjectKeys.XTAG_NAME, getName());
		
		ChangeLogUtils.addLogProperty(element, ObjectKeys.XTAG_WFR_DEFINITION, getDefinitionId());
		ChangeLogUtils.addLogProperty(element, ObjectKeys.XTAG_WFR_RESPONDER, getResponderId());
		ChangeLogUtils.addLogProperty(element, ObjectKeys.XTAG_WFR_RESPONSEDATE, getResponseDate());
		ChangeLogUtils.addLogProperty(element, ObjectKeys.XTAG_WFR_RESPONSE, getResponse());
		return element;
    	
    }
}

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
import java.util.Date;

import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.module.shared.XmlUtils;


/**
 * @hibernate.class table="SS_WorkflowResponses" dynamic-update="true" lazy="false" 
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * @author janet
 */

public class WorkflowResponse extends ZonedObject {
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
    * @hibernate.component class="org.kablink.teaming.domain.AnyOwner"
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
		Element element = parent.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_WORKFLOWREPONSE);
		element.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_DATABASEID, getId().toString());
		element.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_NAME, getName());
		
		XmlUtils.addProperty(element, ObjectKeys.XTAG_WFR_DEFINITION, getDefinitionId());
		XmlUtils.addProperty(element, ObjectKeys.XTAG_WFR_RESPONDER, getResponderId());
		XmlUtils.addProperty(element, ObjectKeys.XTAG_WFR_RESPONSEDATE, getResponseDate());
		XmlUtils.addProperty(element, ObjectKeys.XTAG_WFR_RESPONSE, getResponse());
		return element;
    	
    }
}

package com.sitescape.ef.domain;
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
    * @hibernate.component class="com.sitescape.ef.domain.AnyOwner"
    * @return
    */
    public AnyOwner getOwner() {
   		return owner;
   	}
   	protected void setOwner(AnyOwner owner) {
   		this.owner = owner;
   	}
 	protected void setOwner(DefinableEntity entity) {
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
     * @hibernate.property length="4000"
     */
    private String getResponse() {
        return this.response;
    }
    private void setResponse(String value) {
        this.response = response;
    }    
}

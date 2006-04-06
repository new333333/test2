package com.sitescape.ef.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitescape.util.Validator;
import com.sitescape.ef.modelprocessor.InstanceLevelProcessorSupport;
import com.sitescape.ef.security.acl.AclContainer;
import com.sitescape.ef.security.acl.AclSet;
import com.sitescape.ef.security.function.WorkArea;

/**
 * This object represents a container.
 * 
 * @hibernate.class table="SS_Forums" dynamic-update="true" dynamic-insert="false" lazy="false"
 * @hibernate.discriminator type="string" length="16" column="type"
 * @hibernate.query name="find-Binder-Company" query="from com.sitescape.ef.domain.Binder binder where binder.name=:binderName and binder.zoneName=:zoneName"
 * @hibernate.cache usage="read-write"
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * @author Jong Kim
 *
 */
public abstract class Binder extends DefinableEntity implements DefinitionArea, WorkArea, AclContainer, InstanceLevelProcessorSupport  {
	protected String name="";
    protected HistoryStamp owner;
    protected Map properties;
    protected Binder parentBinder;
    protected NotificationDef notificationDef;
    protected List postings;
    protected Integer upgradeVersion;   
    protected String zoneName; 
    protected String type;
    protected List definitions;	//initialized by hiberate access=field
    protected Definition defaultPostingDef;//initialized by hiberate access=field
    protected List binders;//initialized by hibernate access="field"
    protected Map workflowAssociations;//initialized by hibernate access="field"
    protected boolean definitionsInherited=true;
    protected boolean functionMembershipInherited = true;
    protected PersistentAclSet aclSet; 
    protected boolean inheritAclFromParent = true;
    // these bits signify whether entries of a binder can allow wider access
    // than the binder's .  This does not apply to sub-binders.
    protected boolean widenRead=false;
    protected boolean widenModify=false;
    protected boolean widenDelete=false;
    
    /**
     * @hibernate.property length="100" not-null="true" node="zoneName"
     */
    public String getZoneName() {
    	return this.zoneName;
    }
    public void setZoneName(String id) {
    	this.zoneName = id;
    }
    /**
     * @hibernate.many-to-one
     * @return
     */
    public Binder getParentBinder() {
   	 return parentBinder;
    }
    public void setParentBinder(Binder parentBinder) {
   	 this.parentBinder = parentBinder;
    }
    /**
     * @hibernate.bag access="field" lazy="true" cascade="all" inverse="true" optimistic-lock="false" 
	 * @hibernate.key column="parentBinder" 
	 * @hibernate.one-to-many class="com.sitescape.ef.domain.Binder" 
     * @hibernate.cache usage="read-write"
     * Returns a List of binders.
     * @return
     */
    public List getBinders() {
    	if (binders == null) binders = new ArrayList();
    	return binders;
    }
    public void addBinder(Binder binder) {
 		binders.add(binder);
 		binder.setParentBinder(this);
	}
    public void removeBinder(Binder binder) {
 		binders.remove(binder);
 		binder.setParentBinder(null);
 		
	}
    /**
     * @hibernate.many-to-one access="field" class="com.sitescape.ef.domain.Definition"
     * @hibernate.column name="defaultPostingDef" sql-type="char(32)"
     * @return
     */
    public Definition getDefaultPostingDef() {
  		return defaultPostingDef;
    }
    public void setDefaultPostingDef(Definition defaultPostingDef) {
        this.defaultPostingDef = defaultPostingDef;
    }
    
    /**
     * @hibernate.property length="16" insert="false" update="false" node="type"
     *
     */
    public String getType() {
    	return type;
    }
    public void setType(String type) {
    	this.type = type;
    }

    /**
     * @hibernate.property length="128" 
     * @return
     */
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
       this.name = name;
    }
    public String getFullName() {
    	if (parentBinder == null) return name;
    	return parentBinder.getFullName() + "." + name;
    }
    /**
     * @hibernate.component prefix="notify_"
     * @return
     */
    public NotificationDef getNotificationDef() {
    	if (notificationDef ==null) notificationDef = new NotificationDef();
        return notificationDef;
    }
    public void setNotificationDef(NotificationDef notificationDef) {
        this.notificationDef = notificationDef;
    }
    /**
     * @hibernate.bag  lazy="true" cascade="all,delete-orphan" inverse="true" optimistic-lock="false" 
     * @hibernate.key column="binder" 
     * @hibernate.one-to-many class="com.sitescape.ef.domain.PostingDef" 
     * @return
     */
    public List getPostings() {
    	if (postings == null) return new ArrayList();
    	return postings;
    }
    public void setPostings(List postings) {
    	this.postings = postings;
    }
    public void addPosting(PostingDef post) {
    	post.setBinder(this);
    	getPostings().add(post);
    }
    public void removePosting(PostingDef post) {
    	getPostings().remove(post);
    }
    public PostingDef getPosting(String postingId) {
       	//initialize them first
    	getPostings();
    	for (int i=0; i<postings.size(); ++i) {
    		PostingDef post = (PostingDef)postings.get(i);
    		if (post.getId().equals(postingId)) 
    			return post;
       	}
       	return null;
    }
    /**
     * @hibernate.component prefix="owner_" node="owner"
     */
    public HistoryStamp getOwner() {
        return owner;
    }
    public void setOwner(HistoryStamp owner) {
        this.owner = owner;
    }
    /**
     * @hibernate.property type="org.springframework.orm.hibernate3.support.BlobSerializableType"
     * @return
     */
    public Map getProperties() {
    	return properties;
    }
    public void setProperties(Map properties) {
        this.properties = properties;
    }
    public void setProperty(String name, Object value) {
    	if (properties == null) properties = new HashMap();
    	properties.put(name, value);
    }
    public Object getProperty(String name) {
    	if (properties == null) return null;
    	return properties.get(name);
    }

    
    /**
     * hibernate.property node="upgradeVersion"
     */
    public Integer getUpgradeVersion() {
        return this.upgradeVersion;
    }
    public void setUpgradeVersion(Integer upgradeVersion) {
        this.upgradeVersion = upgradeVersion;
    }
    

    public String toString() {
    	return getZoneName() + ":" + name; 
    }

    public Long getWorkAreaId() {
        return getId();
    }
    public String getWorkAreaType() {
        return getType();
    }
    public WorkArea getParentWorkArea() {
        return this.getParentBinder();
    }
	/**
	 * @hibernate.property not-null="true" node="functionMembershipInherited"
	 * @return
	 */
    public boolean isFunctionMembershipInherited() {
    	if (parentBinder == null) return false;
        return functionMembershipInherited;
    }
    public void setFunctionMembershipInherited(boolean functionMembershipInherited) {
        this.functionMembershipInherited = functionMembershipInherited;
    }

    public AclContainer getParentAclContainer() {
        return this.getParentBinder();
    }
    
    public Long getAclContainerId() {
        return getId();
    }
    
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
    
    /**
     * @hibernate.property column="acl_inheritFromParent" 
     */
    public boolean getInheritAclFromParent() {
        return inheritAclFromParent;
    }

    public void setInheritAclFromParent(boolean inherit) {
        this.inheritAclFromParent = inherit;
    }
    /**
     * @hibernate.property column="acl_widenRead" 
     */
    public boolean isWidenRead() {
        return widenRead;
    }

    public void setWidenRead(boolean widenRead) {
        this.widenRead = widenRead;
    }
    /**
     * @hibernate.property column="acl_widenModify" 
     */
    public boolean isWidenModify() {
        return widenModify;
    }

    public void setWidenModify(boolean widenModify) {
        this.widenModify = widenModify;
    }
    /**
     * @hibernate.property column="acl_widenDelete" 
     */
    public boolean isWidenDelete() {
        return widenDelete;
    }

    public void setWidenDelete(boolean widenDelete) {
        this.widenDelete = widenDelete;
    }    
    public Long getCreatorId() {
    	HistoryStamp creation = getCreation();
    	if(creation != null) {
    		Principal principal = creation.getPrincipal();
    		if(principal != null)
    			return principal.getId();
    	}
    	return null;
    }

    public String getProcessorClassName(String processorKey) {
        return (String) getProperty(processorKey);
    }
    
    public void setProcessorClassName(String processorKey, String processorClassName) {
        setProperty(processorKey, processorClassName);
    }
    //Support for DefinitionArea interface
    public Long getDefinitionAreaId() {
    	return getId();
    }
    
    public String getDefinitionAreaType() {
    	return getEntityIdentifier().getEntityType().name();
    }
    
    public DefinitionArea getParentDefinitionArea() {
    	return getParentBinder();
    }
    
    /**
     * @hibernate.property
     * @return
     */
    public boolean isDefinitionsInherited() {
    	return definitionsInherited;
    }
    
    public void setDefinitionsInherited(boolean definitionsInherited) {
    	this.definitionsInherited = definitionsInherited;
    }
    public boolean isDefinitionInheritanceSupported() {
    	if (parentBinder != null) return true;
    	return false;
    }
    protected List getDefs(int type) {
       	if (definitionsInherited && parentBinder != null)
    		return new ArrayList(parentBinder.getDefs(type));
      	Definition def;
    	List result = new ArrayList(); 
     	if (definitions == null) definitions = new ArrayList();
    	for (int i=0; i<definitions.size(); ++i) {
    		def = (Definition)definitions.get(i);
    		if (def.getType() == type) {
    			result.add(def);
    		}
       	}
       	return result;
    }

    // Setup by hibernate
    public List getDefinitions() {
    	if (definitionsInherited && parentBinder != null)
    		return new ArrayList(parentBinder.getDefinitions());
     	if (definitions == null) definitions = new ArrayList();
     	return definitions;
     }
    //definitions doesn't keep an inverse collection so just update here
    public void setDefinitions(List definitions) {
     	if (this.definitions == null) this.definitions = new ArrayList();
 		//order matters.
 		this.definitions.clear();
 		if (definitions != null) this.definitions.addAll(definitions); 
    }
     public Definition getDefaultEntryDef() {
    	
    	List eDefinitions = getEntryDefinitions();
    	if (eDefinitions.size() > 0)
    		return (Definition)eDefinitions.get(0);
    	return null;
	}
     public Definition getDefaultViewDef() {
     	
     	List eDefinitions = getViewDefinitions();
     	if (eDefinitions.size() > 0)
     		return (Definition)eDefinitions.get(0);
     	return null;
 	}
     public Definition getEntryDef() {
     	return getDefaultViewDef();
     }
       // Setup by hibernate
     public Map getWorkflowAssociations() {
     	if (definitionsInherited && parentBinder != null)
    		return new HashMap(parentBinder.getWorkflowAssociations());
    	if (workflowAssociations == null) workflowAssociations = new HashMap();
    	return workflowAssociations;
    }
    public void setWorkflowAssociations(Map workflowAssociations) {
       	if (this.workflowAssociations == null) this.workflowAssociations = new HashMap();
       	else this.workflowAssociations.clear(); 
       	if (workflowAssociations != null) this.workflowAssociations.putAll(workflowAssociations);
    }
    public abstract List getEntryDefinitions();
    public abstract List getViewDefinitions();
  
}

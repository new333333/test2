/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package org.kablink.teaming.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.HashSet;

import org.dom4j.Document;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.fi.connection.ResourceDriver;
import org.kablink.teaming.fi.connection.ResourceDriverManagerUtil;
import org.kablink.teaming.modelprocessor.InstanceLevelProcessorSupport;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;


/**
 * This object represents a container.
 * 
 * @hibernate.class table="SS_Forums" dynamic-update="true" dynamic-insert="false" lazy="false"
 * @hibernate.discriminator type="string" length="16" column="type"
 * @hibernate.cache usage="read-write"
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 *
 */
public abstract class Binder extends DefinableEntity implements WorkArea, InstanceLevelProcessorSupport  {
	protected String name="";
    protected Principal owner; //initialized by hibernate access=field  
    protected Map properties;
    protected NotificationDef notificationDef;
    protected PostingDef posting;
    protected String pathName;
    protected List definitions;	//initialized by hiberate access=field
    protected List binders;//initialized by hibernate access="field"
    protected Map workflowAssociations;//initialized by hibernate access="field"
    protected boolean definitionsInherited=false;
    protected boolean functionMembershipInherited = true;
    protected boolean teamMembershipInherited = true;
    //uuid to identify a reserved binder
    private String internalId;
    //force attachments of all child objects to have unique names.
    protected boolean library=false;
    //force child objects to have a unique normalized title.  This is an aide to 
    //wikis which link to titles
    protected boolean uniqueTitles=false;
    protected boolean mirrored = false;
    protected String resourceDriverName;
    protected String resourcePath;
    protected int binderCount=0;
    protected HKey binderKey;
    protected int nextBinderNumber=1;
    protected String branding; 
    protected Boolean postingEnabled;
    protected String type;
    public Binder() {
    }
    /**
     * Populate binder with source binder
     * @param source
     */
    public Binder(Binder source) {
    	super(source);
 		if (source.definitions != null)
			//can copy definitions since they are shared
			definitions = new ArrayList(source.definitions);
		 if (source.workflowAssociations != null)
  			 //can copy workflow associations since they are shared
  			 workflowAssociations = new HashMap(source.workflowAssociations);
		 //don't copy names		 name = source.name;
		 zoneId = source.zoneId;
		 definitionsInherited=source.definitionsInherited;
		 functionMembershipInherited=source.functionMembershipInherited;
		 teamMembershipInherited=source.teamMembershipInherited;
		 library=source.library;
		 uniqueTitles = source.uniqueTitles;
		 if (source.properties != null) properties = new HashMap(source.properties);
		 owner = source.owner;
		 mirrored = source.mirrored;
		 resourceDriverName = source.resourceDriverName;
		 resourcePath = source.resourcePath;
		 branding = source.branding;
		 //don't copy postingDef, notificationDef, internalId, binders, or pathName
 
     }
    /**
     * Return the zone id
     * @hibernate.property not-null="true"
     */
    public Long getZoneId() {
    	return this.zoneId;
    }
    public void setZoneId(Long zoneId) {
    	this.zoneId = zoneId;
    }
    /**
     * Return true if this binder is the top workspace
     * @return
     */
    public boolean isZone() {
    	if (getZoneId().equals(getId())) return true;
    	return false;
    }
    /**
     * Return true if this binder is to root of a tree
     * @return
     */
    public boolean isRoot() {
    	return getParentBinder() == null;
    }
    /**
     * Return the top parent of the binder, the top of the tree
     * @return
     */
    public Binder getRoot() {
    	if (getParentBinder() == null) return this;
    	return getParentBinder().getRoot();
    }
    /**
     * Initialize the sort key for the top of a tree. 
     * Used during zone initialization
     *
     */
    public void setupRoot() {
    	if (isRoot()) setBinderKey(new HKey(HKey.generateRootKey(getId()) + "00001"));
    }
    /**
     * Return the title. Sub-classes should override.
     * @return
     */
    public String getSearchTitle() {
    	return getTitle();
    }
   
    /**
     * Return true if all file names of all entries must be unique
     * @hibernate.property
     */
    public boolean isLibrary() {
    	return library;
    }
    public void setLibrary(boolean library) {
    	this.library = library;
    }
    /**
     * Return true if titles on entries must be unique
     * @hibernate.property
     */
    public boolean isUniqueTitles() {
    	return uniqueTitles;
    }
    public void setUniqueTitles(boolean uniqueTitles) {
    	this.uniqueTitles = uniqueTitles;
    }
    /**
     * Internal id used to identify default binders.  This id plus
     * the zoneId are used to locate default binders. 
     * @hibernate.property length="32"
     */
    public String getInternalId() {
    	return this.internalId;
    }
    public void setInternalId(String internalId) {
    	this.internalId = internalId;
    }
    /**
     * Return true if the binder is a 'system binder'
     * @return
     */
    public boolean isReserved() {
    	return Validator.isNotNull(internalId);
    }
    /**
     * Return the path name of this binder from the root of the binder tree.
     * @hibernate.property length="1024" 
     */
    public String getPathName() {
    	return this.pathName;
    }
    public void setPathName(String pathName) {
    	this.pathName = pathName;
    }

    /**
     * Return all sub-binders.  Controllers should use the modules to retrieve a list
     * that has been access checked. 
     * @hibernate.bag access="field" lazy="true" cascade="all" inverse="true" optimistic-lock="false" 
	 * @hibernate.key column="parentBinder" 
	 * @hibernate.one-to-many class="org.kablink.teaming.domain.Binder" 
     * @hibernate.cache usage="read-write"
     * @return
     */
    public List getBinders() {
    	if (binders == null) binders = new ArrayList();
    	return binders;
    }
    /**
     * Add a child binder
     * @param binder
     */
    public void addBinder(Binder binder) {
    	getBinders().add(binder);
 		binder.setParentBinder(this);
 		++binderCount;
 		binder.setBinderKey(new HKey(getBinderKey(), nextBinderNumber++));
	}
    /**
     * Remove a child binder
     * @param binder
     */
    public void removeBinder(Binder binder) {
 		getBinders().remove(binder);
 		binder.setParentBinder(null);
		--binderCount;
		//we don't clear to binderKey, cause logging/audits need it
    }
    /**
     * Optimization to determine the number of subbinders, without
     * forcing database lookup.
     * @hibernate.property
     */
    public int getBinderCount() {
    	return binderCount;
    }
    protected void setBinderCount(int binderCount) {
    	this.binderCount = binderCount;
    }
 
    /**
     * @hibernate.property insert="false" update="false"
     *
     */
    protected String getType() {
    	return type;
    }
    protected void setType(String type) {
    	this.type = type;
    }
    /**
     * Only used on top workspace.
     * @hibernate.property length="128" 
     * @return
     */
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
       this.name = name;
    }
    /** 
     * @hibernate.property 
     * @return
     */
    protected int getNextBinderNumber() {
    	return nextBinderNumber;
    }
    protected void setNextBinderNumber(int nextBinderNumber) {
    	this.nextBinderNumber = nextBinderNumber;
    }   

    /**
     * Return the hierarchical key.
     * @hibernate.component class="org.kablink.teaming.domain.HKey" prefix="binderRoot_"
     */
    public HKey getBinderKey() {
    	return binderKey;
    }
    protected void setBinderKey(HKey binderKey) {
        this.binderKey = binderKey;
    }
    /**
     * Fix up fields relative to parent chain during a move.
     * @param to
     */
    public void move(Binder to) {
    	//assume a parent has moved, so fixup path and hKey
    	if (getParentBinder().equals(to)) {
           	setPathName(to.getPathName() + "/" + getTitle());
           	//relative position remains the same
    		setBinderKey(new HKey(to.getBinderKey(), this.getBinderKey().getLastNumber()));
    	} else {
    		//top level of move.  
    		getParentBinder().removeBinder(this);
        	to.addBinder(this);
           	setPathName(to.getPathName() + "/" + getTitle());
    	}
    }
    /**
     * Return the notifications 
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
     * Return the posting
     * @hibernate.many-to-one 
     * @return
     */
    public PostingDef getPosting() {
    	return posting;
    }
    public void setPosting(PostingDef posting) {
    	this.posting = posting;
    }

    /**
     * Return the owner of the binder.
     * The owner default to the creator.
     * Used in access management.
     * @hibernate.many-to-one
     */
 	public Principal getOwner() {
		if (owner != null) return owner;
	   	HistoryStamp creation = getCreation();
    	if ((creation != null) && creation.getPrincipal() != null) {
    		return creation.getPrincipal();
    	}
    	return null;
		
	}
	public void setOwner(Principal owner) {
		this.owner = owner;
	}

    /**
     * Return all binder properties.
     * @hibernate.property type="org.springframework.orm.hibernate3.support.BlobSerializableType"
     * @return
     */
    public Map getProperties() {
    	return properties;
    }
    public void setProperties(Map properties) {
        this.properties = properties;
    }
    /**
     * Remove a binder property.
     * @param name
     */
    public void removeProperty(String name) {
    	if (properties == null) return;
    	properties.remove(name);
    	if (properties.isEmpty()) properties = null;
    }
    /**
     * Set a binder property.
     * @param name
     * @param value If null, remove property
     */
    public void setProperty(String name, Object value) {
 	   if (value instanceof Object[]) throw new IllegalArgumentException("Arrays not supported");
 	   if (value instanceof Document) throw new IllegalArgumentException("XML docs not supported");
 	   if (value == null) removeProperty(name);
 	   if (properties == null) properties = new HashMap();
 	   properties.put(name, value);
    }
    /**
     * Return a property value.
     * Return property
     * @param name
     * @return
     */
    public Object getProperty(String name) {
    	if (properties == null) return null;
    	return properties.get(name);
    }
    
     
    /**
     * Return the pathName
     */
    public String toString() {
    	return getPathName(); 
    }

    /**
     * The <code>BinderProcessor</code> key
     * @return
     */
    public String getProcessorKey(String processorKey) {
    	return processorKey;
    }
       
    /**
     * Return true if definitions are inherited from parent binder.
     * @hibernate.property
     * @return
     */
    public boolean isDefinitionsInherited() {
    	return definitionsInherited;
    }
    
    public void setDefinitionsInherited(boolean definitionsInherited) {
    	this.definitionsInherited = definitionsInherited;
    }
    /**
     * Return true if definitions can be inheritted.
     * @return
     */
    public boolean isDefinitionInheritanceSupported() {
    	return false;
    }
    protected List<Definition> getDefs(int type) {
       	if (isDefinitionInheritanceSupported() && isDefinitionsInherited()) {
       		if (!isRoot()) return new ArrayList(getParentBinder().getDefs(type));
        	if (this.definitions == null) this.definitions = new ArrayList();
        	this.definitions.clear();
        	definitions.add(entryDef);
       	}
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
    /**
     * Return configured definitions.
     * @return
     */
    // Setup by hibernate
    public List<Definition> getDefinitions() {
    	if (isDefinitionsInherited() && !isRoot())
    		return new ArrayList(getParentBinder().getDefinitions());
     	if (definitions == null) definitions = new ArrayList();
     	return definitions;
     }
    public Map<String,Definition>getDefinitionMap() {
    	List<Definition> defs = getDefinitions();
    	//convert to map of id->def
    	Map results = new TreeMap();
    	for (Definition def:defs) {
    		results.put(def.getId(), def);
    	}
    	return results;	
    }
    /**
     * Replace current configured definitions.
     * @param definitions
     */
    //definitions doesn't keep an inverse collection so just update here
    public void setDefinitions(List<Definition> definitions) {
     	if (this.definitions == null) this.definitions = new ArrayList();
 		//order matters. = don't squash self
     	if (definitions != this.definitions) {
     		this.definitions.clear();
     		if (definitions != null) this.definitions.addAll(definitions);
     	}
    }
    /**
     * Remove a definition from the list of definitions and workflowAssociations
     * @param def
     */
    public void removeDefinition(Definition def) {
    	getDefinitions().remove(def);
    	Map myDefs = getWorkflowAssociations();
    	myDefs.remove(def.getId());
    }
    /**
     * Return a definition to use for entries. 
     * @return
     */
     public Definition getDefaultEntryDef() {
    	
    	List eDefinitions = getEntryDefinitions();
    	if (eDefinitions.size() > 0)
    		return (Definition)eDefinitions.get(0);
    	return null;
	}
     /**
      * Return a definition to use for viewing the binder.
      * @return
      */
     public Definition getDefaultViewDef() {
     	
     	List eDefinitions = getViewDefinitions();
     	if (eDefinitions.size() > 0)
     		return (Definition)eDefinitions.get(0);
     	//return original so have something.
     	return entryDef;
 	}
     /**
      * @see #getDefaultViewDef()
      */
     public Definition getEntryDef() {
    	 //Peter wants the currently configured default for binders.
    	 //doesn't care what it was created with
     	return getDefaultViewDef();
     }
     /**
      * Returning mapping of definition ids to workflows definitions.
      * @return
      */
       // Setup by hibernate
     public Map<String, Definition>getWorkflowAssociations() {
     	if (isDefinitionsInherited() && !isRoot())
    		return new HashMap(getParentBinder().getWorkflowAssociations());
    	if (workflowAssociations == null) workflowAssociations = new HashMap();
    	return workflowAssociations;
    }
    public void setWorkflowAssociations(Map<String, Definition> workflowAssociations) {
       	if (this.workflowAssociations == null) this.workflowAssociations = new HashMap();
       	if (workflowAssociations != this.workflowAssociations) {
       		this.workflowAssociations.clear(); 
       		if (workflowAssociations != null) this.workflowAssociations.putAll(workflowAssociations);
       	}
    }
    /**
     * Remove the mapping from a definition to a workflow.
     * @param def
     */
    public void removeWorkflow(Definition def) {
    	Map myDefs = getWorkflowAssociations();
    	//make a copy since we are altering the contents
    	Map<String, Definition> defs = new HashMap(myDefs);
        //The same workflow may be mapped to multiple times. 
    	for (Iterator iter=defs.entrySet().iterator(); iter.hasNext();) {
    		Map.Entry e =(Map.Entry)iter.next();
    		if (def.equals(e.getValue())) myDefs.remove(e.getKey()); 
    	}
    }
    /**
     * Return a list of definitions for entries
     * @return
     */
    public abstract List<Definition> getEntryDefinitions();
    /**
     * Return a list of definitions for views
     * @return
     */
    public abstract List<Definition> getViewDefinitions();
    /**
     * Return a list of definitions for workflows
     * @return
     */
    public List<Definition> getWorkflowDefinitions() {
    	return getDefs(Definition.WORKFLOW);
    }	
    public boolean isMirroredAllowed() {
    	return true;
    }
	public boolean isMirrored() {
		return mirrored;
	}
	public void setMirrored(boolean mirrored) {
		this.mirrored = mirrored;
	}
	public String getResourcePath() {
		return resourcePath;
	}
	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}
	
	public String getResourceDriverName() {
		return resourceDriverName;
	}
	public void setResourceDriverName(String resourceDriverName) {
		this.resourceDriverName = resourceDriverName;
	}

	public ResourceDriver getResourceDriver() {
		// Just a convenience method
		return ResourceDriverManagerUtil.findResourceDriver(getResourceDriverName());
	}
	
	public boolean isMirroredAndReadOnly() {
		return isMirrored() && getResourceDriver().isReadonly();
	}
	public Binder getBrandingSource() {
    	if (Validator.isNotNull(branding)) return this;
        if (parentBinder == null) return this;
        return parentBinder.getBrandingSource();
		
	}
    public String getBranding() {
    	return branding;
    }
    public void setBranding(String branding) {
    	this.branding = branding; 
    }
    
    /**
     * @hibernate.property
     * @return
     */
    public Boolean getPostingEnabled()
    {
    	return postingEnabled != null && postingEnabled;
    }
    public void setPostingEnabled(Boolean postingEnabled)
    {
    	this.postingEnabled = postingEnabled;
    }
    

    //*****************WorkArea interface stuff***********/
    public Long getWorkAreaId() {
        return getId();
    }
    public String getWorkAreaType() {
        return getEntityType().name();
    }
    public WorkArea getParentWorkArea() {
        return this.getParentBinder();
    }
    public Set getChildWorkAreas() {
    	return new HashSet(getBinders());
    }
	/**
	 * @hibernate.property not-null="true"
	 * @return
	 */
    public boolean isFunctionMembershipInherited() {
    	if (isRoot()) return false;
        return functionMembershipInherited;
    }
    public void setFunctionMembershipInherited(boolean functionMembershipInherited) {
        this.functionMembershipInherited = functionMembershipInherited;
    }
     public boolean isFunctionMembershipInheritanceSupported() {
    	if (isRoot()) return false;
    	return true;
    }
     public Long getOwnerId() {
    	Principal owner = getOwner();
    	if (owner == null)	return null;
    	return owner.getId();
    }
     public boolean isTeamMembershipInherited() {
    	return teamMembershipInherited;   	
    }
    public void setTeamMembershipInherited(boolean teamMembershipInherited) {
    	this.teamMembershipInherited = teamMembershipInherited;
    }
    /**
     * Return the team member ids
     * @return
     */
    public Set<Long> getTeamMemberIds() {
    	if (!isRoot() && isTeamMembershipInherited()) return getParentBinder().getTeamMemberIds();
    	String members = (String)getProperty(ObjectKeys.BINDER_PROPERTY_TEAM_MEMBERS);
    	return LongIdUtil.getIdsAsLongSet(members);
    	
    }
     public void setTeamMemberIds(Set<Long> memberIds) {
    	//setting inherited flag handled separate
    	if ((memberIds == null) || memberIds.isEmpty()) removeProperty(ObjectKeys.BINDER_PROPERTY_TEAM_MEMBERS);
    	else setProperty(ObjectKeys.BINDER_PROPERTY_TEAM_MEMBERS, LongIdUtil.getIdsAsString(memberIds));
     }
     /*****************End WorkArea interface stuff***********/
    
     /**
      * Return acl index string representing team membership
      * @return
      */
     public String getTeamMemberString() {
     	if (!isRoot() && isTeamMembershipInherited()) return getParentBinder().getTeamMemberString();
     	String members = (String)getProperty(ObjectKeys.BINDER_PROPERTY_TEAM_MEMBERS);
     	if (Validator.isNull(members)) return Constants.EMPTY_ACL_FIELD;
     	return members;
     	
     }
    /*****************InstanceLevelProcessorSupport interface stuff***********/
    public String getProcessorClassName(String processorKey) {
        return (String) getProperty(processorKey);
    }
    
    public void setProcessorClassName(String processorKey, String processorClassName) {
        setProperty(processorKey, processorClassName);
    }
    /*****************End InstanceLevelProcessorSupport interface stuff***********/	
}

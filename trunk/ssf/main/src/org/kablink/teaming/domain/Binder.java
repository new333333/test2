/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.NetFolderConfig.SyncScheduleOption;
import org.kablink.teaming.fi.FIException;
import org.kablink.teaming.fi.connection.ResourceDriver;
import org.kablink.teaming.fi.connection.ResourceDriverManagerUtil;
import org.kablink.teaming.fi.connection.acl.AclResourceDriver;
import org.kablink.teaming.modelprocessor.InstanceLevelProcessorSupport;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.netfolder.NetFolderUtil;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.util.Validator;

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
@SuppressWarnings("unchecked")
public abstract class Binder extends DefinableEntity implements WorkArea, InstanceLevelProcessorSupport  {
	
	private static Log logger = LogFactory.getLog(Binder.class);
	
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
    // Relative path information.
    // For net folders, this is a path relative to the path information specified
    // in the net folder configuration object (rather than to the path information 
    // specified in the net folder server object)
    // For legacy mirrored folder, this path is relative to the path specified in
    // the static resource driver configuration.
    // For others, this is null.
    // Again, this field is about how the data is stored in the database, NOT how
    // the path is presented to the application layer.
    protected String relRscPath;
    protected int binderCount=0;
    protected HKey binderKey;
    protected int nextBinderNumber=1;
    protected String branding;
    protected String brandingExt;	// Additional branding information such as background color, font color, background image.
    protected Boolean postingEnabled;
    protected String binderType;
    protected Definition entryDef; // initialized by hibernate access=field
    //File related settings
    protected Boolean versionsEnabled;
	protected Long versionsToKeep;
	protected Long versionAgingDays;
    protected Boolean versionAgingEnabled;
    protected Long maxFileSize;	//MB (stored as the maximum number of mega-bytes)
    protected Boolean fileEncryptionEnabled;
    protected Boolean extFunctionMembershipInherited = Boolean.TRUE;
    protected Boolean myFilesDir = Boolean.FALSE;
    protected String resourceHandle;
    
    protected Long teamGroupId;
    
    // For net folders, this field contains ID of the net folder config object.
    // For all others (including legacy mirrored folders), this field is null.
    protected Long netFolderConfigId;
    // This field is used only for legacy mirrored folders.
    protected Long legacyMirroredDriverNameHash;
    
    // This is full path relative to the net folder server (if net folder) or to the static
    // mirrored folder configuration (if mirrored folder). 
    // This field is NOT persisted. That is, this field is not about how the data is stored
    // in the database, but how the path is presented to the application layer.
    protected transient String fullResourcePath = null; 

    private ResourceDriver resolvedDriver; // To avoid having to resolve multiple times for same binder instance
    
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
		 relRscPath = source.relRscPath;
		 branding = source.branding;
		 brandingExt = source.brandingExt;
		 //don't copy postingDef, notificationDef, internalId, binders, or pathName
		 entryDef = source.entryDef;
		 netFolderConfigId = source.netFolderConfigId;
		 legacyMirroredDriverNameHash = source.legacyMirroredDriverNameHash;
     }
    /**
     * Return the zone id
     * @hibernate.property not-null="true"
     */
    @Override
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
     * Return true if this binder is a TemplateBinder
     * @return
     */
    public boolean isTemplateBinder() {
    	return this instanceof TemplateBinder;
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
    protected String getBinderType() {
    	return binderType;
    }
    protected void setBinderType(String binderType) {
    	this.binderType = binderType;
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
 	@Override
	public Principal getOwner() {
		if (owner != null) return owner;
	   	HistoryStamp creation = getCreation();
    	if ((creation != null) && creation.getPrincipal() != null) {
    		return creation.getPrincipal();
    	}
    	return null;
		
	}
	@Override
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
 	   if (value != null) properties.put(name, value);
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

    public String getSerializedProperties() throws IOException {
        if (properties==null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        try {
            oos.writeObject(properties);
            oos.flush();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } finally {
            oos.close();
        }
    }

    public void setSerializedProperties(String props) throws IOException, ClassNotFoundException {
        if (props==null) {
            properties = null;
        } else {
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(props));
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                properties = (Map) obj;
            }
        }
    }
     
    /**
     * Return the pathName
     */
    @Override
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
    public void copyInheritedDefinitions() {
        if (isDefinitionsInherited()) {
            setDefinitions(getDefinitions());
            setDefinitionsInherited(false);
        }
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
      * Return a definition to use for file entries when dropped into the folder. 
      * @return
      */
      public Definition getDefaultFileEntryDef() {
     	Definition folderDef = this.getEntryDef();
		Element useFileEntry = (Element) folderDef.getDefinition()
			.getRootElement().selectSingleNode("//properties/property[@name='defaultToFileEntries']");
		if (useFileEntry == null || useFileEntry.attributeValue("value", "true").equals("true")) {
			//This binder defaults to adding file entries from "Add files to folder" operations
			Definition fileDef = DefinitionHelper.getDefinition(ObjectKeys.DEFAULT_LIBRARY_ENTRY_DEF);
			if (fileDef != null) return fileDef;
		}
     	
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
     
     @Override
	public String getEntryDefId() {
    	 Definition def = getEntryDef();
    	 if(def != null)
    		 return def.getId();
    	 else
    		 return null;
     }

     @Override
	public void setEntryDef(Definition entryDef) {
    	 this.entryDef = entryDef;
     }
     
     @Override
	public String getCreatedWithDefinitionId() {
      	// returns the original definition with which this entity was created. 
      	if(entryDef != null)
      		return entryDef.getId();
      	else
      		return null;
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

    protected abstract Binder newInstance();
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
	
	/**
	 * Return resource path that is relative to the net folder server (= resource driver config),
	 * NOT to the net folder config.
	 * 
	 * NOTE: This method is guaranteed to return non-null value for all mirrored folders.
	 */
	public String getResourcePath() {
		if(fullResourcePath == null) {
			// Either full resource path hasn't been constructed yet or it was impossible to construct it.
			if(getRelRscPath() != null) {
				// The relative path has a value. Construct a full path from it.
				pathFromRelativeToFull();
			}
		}
		return fullResourcePath;
	}
	
	/**
	 * 
	 * @param resourcePath This resource path is relative to the net folder server 
	 * (= resource driver config), NOT to the net folder config. This is so that we
	 * won't have to change the way this has always been working in previous releases.
	 */
	public void setResourcePath(String resourcePath) {
		// It is expected that the resource path was already normalized by the caller, so we're not doing it again here.
		this.fullResourcePath = resourcePath;
		setRelRscPath(null);
	}

    public String getResourceDriverName() {
    	ResourceDriver rd = this.getResourceDriver();
    	if(rd != null)
    		return rd.getName();
    	else
    		return null;
    }
	
	public void setResourceDriverName(String resourceDriverName) {
		if(resourceDriverName == null) {
			// It must be that this is not a mirrored folder.
			this.legacyMirroredDriverNameHash = null;
			this.netFolderConfigId = null;
		}
		else {
			// It must be that this is a mirrored folder.
			ResourceDriver driver = ResourceDriverManagerUtil.findResourceDriver(resourceDriverName);
			if(ResourceDriverManagerUtil.isStaticallyCreated(driver)) {
				// The resource driver is for legacy mirrored folders. Store hash value of the driver name in the database.
				this.legacyMirroredDriverNameHash = ResourceDriverManagerUtil.toStorageHashAsLong(resourceDriverName);
				// net folder config id is never used for legacy mirrored folder.
				this.netFolderConfigId = null;
			}
			else {
				// For Filr drivers, driver name or its hash value is not stored with binders.
				this.legacyMirroredDriverNameHash = null;
				// Net folder config ID is set by the caller via a separate call. Since we don't know when 
				// the caller would make that call (i.e. before or after this call), do NOT nullify its
				// effect by setting the variable to null here!
			}
		}
	}

	public ResourceDriver getResourceDriver() {
		// Just a convenience method
		if(resolvedDriver == null) {
			if(netFolderConfigId != null) {
				// This is net folder.
				try {
					resolvedDriver = NetFolderUtil.getResourceDriverByNetFolderConfigId(netFolderConfigId);
				} catch(FIException e) {
					logger.warn("Cannot find resource driver given net folder config id '" + netFolderConfigId + "' on binder '" + this.getId() + "'", e);
				}
			}	
			else if(legacyMirroredDriverNameHash != null) {
				// This is legacy mirrored folder.
				try {
					resolvedDriver = ResourceDriverManagerUtil.findStaticResourceDriverByNameHash(legacyMirroredDriverNameHash);
				} catch(FIException e) {
					logger.warn("Cannot find legacy resource driver by hash '" + legacyMirroredDriverNameHash + "' on binder '" + this.getId() + "'", e);
				}
			}
		}
		return resolvedDriver;
	}
	
	public boolean noAclDredgedWithEntries() {
		ResourceDriver driver = getResourceDriver();
		if (driver != null) {
			ResourceDriverConfig config = driver.getConfig();
			if (config != null) {
				return config.isAclAware();
			}
		}
		return false;
	}
	
	public boolean isMirroredAndReadOnly() {
		return isMirrored() && getResourceDriver() != null && getResourceDriver().isReadonly();
	}
	public Binder getBrandingSource() {
		// If there is any advanced branding or extended branding on this binder, return this binder.
    	if ( Validator.isNotNull( branding ) || Validator.isNotNull( brandingExt ) )
    		return this;
        
    	if (parentBinder == null)
    		return this;
        
    	return parentBinder.getBrandingSource();
		
	}
    public String getBranding() {
    	return branding;
    }
    public void setBranding(String branding) {
    	this.branding = branding; 
    }
    
    /**
     * Return the BrandingExt object that holds the additional branding information.
     * @return
     */
    public String getBrandingExt()
    {
    	return brandingExt;
    }// end getBrandingExt()
    
    
    public void setBrandingExt ( String brandingExt )
    {
    	this.brandingExt = brandingExt; 
    }// end setBrandingExt()
    
    
    /**
     * Return the computed value of "allow desktop app to trigger initial home folder sync".
     * If this binder is inheriting the value of "allow desktop app to trigger initial home folder sync"
     * then we will get the value of "allow desktop app to trigger initial home folder sync"
     * from the net folder server this binder is pointing to.  Otherwise, we will use the value
     * of "allow desktop app to trigger initial home folder sync" from this binder.
     */
    public boolean getComputedAllowDesktopAppToTriggerInitialHomeFolderSync()
    {
    	ResourceDriver resourceDriver;
    	
    	if ( getUseInheritedDesktopAppTriggerSetting() == false )
    		return getAllowDesktopAppToTriggerInitialHomeFolderSync();
    	
    	resourceDriver = getResourceDriver();
    	if ( resourceDriver != null )
    	{
    		ResourceDriverConfig rdConfig;
    		
    		rdConfig = resourceDriver.getConfig();
    		if ( rdConfig != null )
    			return rdConfig.getAllowDesktopAppToTriggerInitialHomeFolderSync();
    	}
    	
    	return false;
    }
        	
    /**
     * Get the xml document that holds the landing page properties such as the background color,
     * background image, etc. 
     */
    public Document getLandingPageProperties()
    {
		Document doc = null;
        CustomAttribute customAttr = getMashupPropertiesAttribute();
		if ( customAttr != null )
		{
			doc = (Document) customAttr.getValue();
		}
		
		return doc;
    }

    /**
     * 
     */
	public Binder getLandingPagePropertiesSourceBinder()
	{
        CustomAttribute customAttr = getMashupPropertiesAttribute();
		if ( customAttr != null )
    		return this;
        
    	if ( parentBinder == null )
    		return this;
        
    	return parentBinder.getLandingPagePropertiesSourceBinder();
	}
        
    public String getResourceHandle() {
		return resourceHandle;
	}
    
	public void setResourceHandle(String resourceHandle) {
		this.resourceHandle = resourceHandle;
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
        
    public boolean isMyFilesDir() {
    	if(myFilesDir == null)
    		return false;
    	else
    		return myFilesDir.booleanValue();
    }
    public void setMyFilesDir(boolean myFilesDir) {
    	this.myFilesDir = myFilesDir;
    }
    
    //*****************WorkArea interface stuff***********/
    @Override
	public Long getWorkAreaId() {
        return getId();
    }
    @Override
	public String getWorkAreaType() {
        return getEntityType().name();
    }
    @Override
	public WorkArea getParentWorkArea() {
        return this.getParentBinder();
    }
    @Override
	public Set getChildWorkAreas() {
    	return new HashSet(getBinders());
    }
    @Override
	public boolean isAclExternallyControlled() {
    	if (this.getResourceDriver() instanceof AclResourceDriver) {
    		return Boolean.TRUE;
    	} else {
    		return Boolean.FALSE;
    	}
    }
    @Override
	public List<WorkAreaOperation> getExternallyControlledRights() {
    	if (this.getResourceDriver() instanceof AclResourceDriver) {
    		return ((AclResourceDriver)this.getResourceDriver()).getExternallyControlledlRights();
    	} else {
    		return Collections.EMPTY_LIST; // return empty immutable list
    	}
    }
    @Override
	public String getRegisteredRoleType() {
    	if (this.getResourceDriver() instanceof AclResourceDriver) {
    		return ((AclResourceDriver)this.getResourceDriver()).getRegisteredRoleTypeName();
    	}
    	return "";
    }

	/**
	 * @hibernate.property not-null="true"
	 * @return
	 */
    @Override
	public boolean isFunctionMembershipInherited() {
    	if (isRoot()) return false;
        return functionMembershipInherited;
    }
    @Override
	public void setFunctionMembershipInherited(boolean functionMembershipInherited) {
        this.functionMembershipInherited = functionMembershipInherited;
    }
     @Override
	public boolean isFunctionMembershipInheritanceSupported() {
    	if (isRoot()) return false;
    	return true;
    }
     @Override
	public Long getOwnerId() {
    	Principal owner = getOwner();
    	if (owner == null)	return null;
    	return owner.getId();
    }
     @Override
	public boolean isTeamMembershipInherited() {
    	return teamMembershipInherited;   	
    }
    public void setTeamMembershipInherited(boolean teamMembershipInherited) {
    	this.teamMembershipInherited = teamMembershipInherited;
    }
    
     @Override
	public boolean isExtFunctionMembershipInherited() {
     	 if (isRoot()) return false;
    	 if(extFunctionMembershipInherited == null)
    		 return true;
    	 else
    		 return extFunctionMembershipInherited.booleanValue();
     }
     
     @Override
	public void setExtFunctionMembershipInherited(boolean extFunctionMembershipInherited) {
    	 this.extFunctionMembershipInherited = Boolean.valueOf(extFunctionMembershipInherited);
     }
     
     /*****************End WorkArea interface stuff***********/
    
    /*****************InstanceLevelProcessorSupport interface stuff***********/
    @Override
	public String getProcessorClassName(String processorKey) {
        return (String) getProperty(processorKey);
    }
    
    @Override
	public void setProcessorClassName(String processorKey, String processorClassName) {
        setProperty(processorKey, processorClassName);
    }
    /*****************End InstanceLevelProcessorSupport interface stuff***********/	
    
    /*****************File Related Stuff***********/	
    public Boolean isVersionsEnabled() {
    	if (versionsEnabled == null) return Boolean.TRUE;		//Default is that versions are enabled
    	return versionsEnabled;
	}
	public void setVersionsInherited() {
		this.versionsEnabled = null;
		this.versionsToKeep = null;
	}
	public Boolean getVersionsEnabled() {
		return versionsEnabled;
	}
	public void setVersionsEnabled(Boolean versionsEnabled) {
		this.versionsEnabled = versionsEnabled;
	}
	public Long getVersionsToKeep() {
		return versionsToKeep;
	}
	public void setVersionsToKeep(Long versionsToKeep) {
		this.versionsToKeep = versionsToKeep;
	}
	public Long getVersionAgingDays() {
		return versionAgingDays;
	}
	public void setVersionAgingDays(Long versionAgingDays) {
		this.versionAgingDays = versionAgingDays;
	}
	public Boolean getVersionAgingEnabled() {
		return versionAgingEnabled;
	}
	public Boolean isVersionAgingEnabled() {
		if (versionAgingEnabled == null) return Boolean.TRUE;	//Default is that aging is enabled
		return versionAgingEnabled;
	}
	public void setVersionAgingEnabled(Boolean versionAgingEnabled) {
		this.versionAgingEnabled = versionAgingEnabled;
	}
	public Long getMaxFileSize() {
		return maxFileSize;
	}
	public void setMaxFileSize(Long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}
	public Boolean isFileEncryptionEnabled() {
		return fileEncryptionEnabled != null && fileEncryptionEnabled;
	}
	public Boolean getFileEncryptionEnabled() {
		return fileEncryptionEnabled;	//Can be null if no value is set for this binder
	}
	public void setFileEncryptionEnabled(Boolean fileEncryptionEnabled) {
		this.fileEncryptionEnabled = fileEncryptionEnabled;
	}
	public void setFileEncryptionInherited() {
		this.fileEncryptionEnabled = null;
	}
	/*****************End File Related Stuff***********/

    public Binder asLimitedBinder(boolean setParent) {
        Binder limited = newInstance();
        limited.entryDef = this.entryDef;
        limited.id = this.id;
        if (setParent) {
            parentBinder = this.getParentBinder();
            if (parentBinder!=null) {
                limited.parentBinder = this.getParentBinder().asLimitedBinder(false);
            }
        }
        limited.pathName = this.pathName;
        limited.library = this.library;
        limited.mirrored = this.mirrored;
        limited.title = this.title;
        return limited;
    }
    
    @Override
    public boolean supportsCustomFields() {
    	return !this.isAclExternallyControlled();
    }
    
	public Long getTeamGroupId() {
		return teamGroupId;
	}
	public void setTeamGroupId(Long teamGroupId) {
		this.teamGroupId = teamGroupId;
	}
    
	public Long getNetFolderConfigId() {
		return netFolderConfigId;
	}
	public void setNetFolderConfigId(Long netFolderConfigId) {
		this.netFolderConfigId = netFolderConfigId;
		if(netFolderConfigId != null)
			this.legacyMirroredDriverNameHash = null;
	}
    
	public NetFolderConfig getNetFolderConfig() {
		if(netFolderConfigId == null)
			return null;
		return NetFolderUtil.getNetFolderConfig(netFolderConfigId);
	}
	
	// Relative path used to construct full path used by application.
	// Empty string denotes empty path.
	private void setRelRscPath(String relRscPath) {
		this.relRscPath = relRscPath;
	}
    
	// Relative path used to construct full path used by application.
	// Empty string denotes empty path.
	private String getRelRscPath() {
		return relRscPath;
	}
	
	// For use by Hibernate only
	// The value passed to this method is the value loaded from the database
	// where an empty path is represented as "/".
	private void setRelRscPathHibernate(String relRscPathHibernate) {
		if("/".equals(relRscPathHibernate))
			relRscPathHibernate = "";
		setRelRscPath(relRscPathHibernate);
		this.fullResourcePath = null;
	}
    
	// For use by Hibernate only
	// The value returned from this method is what gets stored in the database
	// where an empty path is represented as "/".
	private String getRelRscPathHibernate() {
		if(fullResourcePath != null) {
			pathFromFullToRelative();
		}
		String relRscPathHibernate = getRelRscPath();
		if("".equals(relRscPathHibernate)) {
			// (bug #513609) - To workaround problem with Oracle
			// Oracle converts "" to null, which could result in lots of pointless update
			// attempts by Hibernate during dirty check because persistent value and 
			// in-memory value are different. To avoid this issue, we store empty string
			// as "/" consistently for all database types.
			relRscPathHibernate = "/";
		}
		return relRscPathHibernate;
	}
	
	private void pathFromRelativeToFull() {
		NetFolderConfig nfc = this.getNetFolderConfig();
		if(nfc != null) { 
			// This is net folder. Build a full path from the relative one.
			fullResourcePath =  nfc.buildResourcePathRelativeToNetFolderServer(getRelRscPath());
		}
		else {
			// This is either a regular (non-mirrored) binder or a legacy mirrored folder
			// for which there's no difference between full and relative.
			fullResourcePath =  getRelRscPath();
		}
	}
	
	private void pathFromFullToRelative() {
		NetFolderConfig nfc = this.getNetFolderConfig();
		if(nfc != null) {
			// This is a net folder. Get the path relative to the net folder config.
			if(fullResourcePath.equals("")) {
				if("".equals(nfc.getResourcePath()))
					this.setRelRscPath("");
				else
					throw new IllegalArgumentException("Cannot set resource path [" + fullResourcePath + "] on binder (id=" + this.getId() + ") since it doesn't start with net folder config path [" + nfc.getResourcePath() + "]");
			}
			else {
				if("".equals(nfc.getResourcePath())) {
					this.setRelRscPath(fullResourcePath);
				}
				else {
					if(fullResourcePath.startsWith(nfc.getResourcePath())) {
						if(fullResourcePath.length() <= nfc.getResourcePath().length()+1) {
							this.setRelRscPath("");
						}
						else {
							this.setRelRscPath(fullResourcePath.substring(nfc.getResourcePath().length()+1));
						}
					}
					else {
						throw new IllegalArgumentException("Cannot set resource path [" + fullResourcePath + "] on binder (id=" + this.getId() + ") because it doesn't start with net folder config path [" + nfc.getResourcePath() + "]");
					}
				}			
			}
		}
		else {
			// This is a legacy mirrored folder or a regular non-mirrored folder
			// for which there is no difference between full and relative.
			this.setRelRscPath(fullResourcePath);
		}
	}
	
	///// BEGIN: EVERY METHODS BETWEEN BEGIN & END MUST GO AS SOON AS WE CAN FIND TIME TO CLEAN UP
    public boolean getAllowDesktopAppToSyncData() {
    	NetFolderConfig nf = this.getNetFolderConfig();
    	if(nf != null)
    		return nf.getAllowDesktopAppToSyncData();
    	else
    		return false;
    }
	
    public boolean getAllowMobileAppsToSyncData() {
    	NetFolderConfig nf = this.getNetFolderConfig();
    	if(nf != null)
    		return nf.getAllowMobileAppsToSyncData();
    	else
    		return false;
    }
    
    public boolean getComputedIndexContent() {
    	NetFolderConfig nf = this.getNetFolderConfig();
    	if(nf != null)
    		return nf.getComputedIndexContent();
    	else
    		return false;
    }
    
    public boolean getComputedIsJitsEnabled() {
    	NetFolderConfig nf = this.getNetFolderConfig();
    	if(nf != null)
    		return nf.getComputedIsJitsEnabled();
    	else
    		return false;
    }
    
    public long getComputedJitsAclMaxAge() {
    	NetFolderConfig nf = this.getNetFolderConfig();
    	if(nf != null)
    		return nf.getComputedJitsAclMaxAge();
    	else
    		return 0;
    }
    
    public long getComputedJitsMaxAge() {
    	NetFolderConfig nf = this.getNetFolderConfig();
    	if(nf != null)
    		return nf.getComputedJitsMaxAge();
    	else
    		return 0;
    }
    
	public Boolean getFullSyncDirOnly() {
    	NetFolderConfig nf = this.getNetFolderConfig();
    	if(nf != null)
    		return nf.getFullSyncDirOnly();
    	else
    		return false;
	}
	
    public boolean getIndexContent() {
    	NetFolderConfig nf = this.getNetFolderConfig();
    	if(nf != null)
    		return nf.getIndexContent();
    	else
    		return false;
    }
    
	public long getJitsAclMaxAge() {
    	NetFolderConfig nf = this.getNetFolderConfig();
    	if(nf != null)
    		return nf.getJitsAclMaxAge();
    	else
    		return 0;
	}
	
	public long getJitsMaxAge() {
    	NetFolderConfig nf = this.getNetFolderConfig();
    	if(nf != null)
    		return nf.getJitsMaxAge();
    	else
    		return 0;
	}
	
    public boolean isJitsEnabled() {
    	NetFolderConfig nf = this.getNetFolderConfig();
    	if(nf != null)
    		return nf.isJitsEnabled();
    	else
    		return false;
    }
    
    public SyncScheduleOption getSyncScheduleOption() {
    	NetFolderConfig nf = this.getNetFolderConfig();
    	if(nf != null)
    		return nf.getSyncScheduleOption();
    	else
    		return null;
    }

    public boolean getUseInheritedIndexContent() {
    	NetFolderConfig nf = this.getNetFolderConfig();
    	if(nf != null)
    		return nf.getUseInheritedIndexContent();
    	else
    		return false;
    }
    
    public boolean getUseInheritedJitsSettings() {
    	NetFolderConfig nf = this.getNetFolderConfig();
    	if(nf != null)
    		return nf.getUseInheritedJitsSettings();
    	else
    		return false;	
    }
    
	public boolean isFullSyncDirOnly() {
    	NetFolderConfig nf = this.getNetFolderConfig();
    	if(nf != null)
    		return nf.isFullSyncDirOnly();
    	else
    		return false;	
	}
	
    public boolean isHomeDir() {
    	// This check applies ONLY to the home folder top, not sub-folders in them!
    	// In other word, this method should return true ONLY for the top of a home folder and nothing else.
    	NetFolderConfig nf = this.getNetFolderConfig();
    	if(nf != null) {
    		if(nf.getTopFolderId().equals(getId()))
    			return nf.isHomeDir();
    		else
    			return false;
    	}
    	else {
    		return false;
    	}
    }
    
    /*
     * Returns true if this binder represents a folder in a net folder (including its top folder).
     * False otherwise (e.g. a folder in home folder or personal storage, etc.).
     */
    public boolean isFolderInNetFolder() {
    	NetFolderConfig nf = this.getNetFolderConfig();
    	if(nf != null)
    		return !nf.isHomeDir();
    	else
    		return false;
    }
    
    public boolean getAllowDesktopAppToTriggerInitialHomeFolderSync()
    {
    	NetFolderConfig nf = this.getNetFolderConfig();
    	if(nf != null)
    		return nf.getAllowDesktopAppToTriggerInitialHomeFolderSync();
    	else
    		return false;	
    }
    
    /**
     * Return whether the the "allow desktop app to trigger initial home folder sync" setting
     * should be inherited from the net folder server.
     * @return
     */
    public boolean getUseInheritedDesktopAppTriggerSetting()
    {
    	NetFolderConfig nf = this.getNetFolderConfig();
    	if(nf != null)
    		return nf.getUseInheritedDesktopAppTriggerSetting();
    	else
    		return false;
    }
        
	///// END: EVERY METHODS BETWEEN BEGIN & END MUST GO AS SOON AS WE CAN FIND TIME TO CLEAN UP

}

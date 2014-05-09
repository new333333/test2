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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.HashSet;

import org.dom4j.Document;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.ResourceDriverConfig.DriverType;
import org.kablink.teaming.fi.FIException;
import org.kablink.teaming.fi.connection.ResourceDriver;
import org.kablink.teaming.fi.connection.ResourceDriverManagerUtil;
import org.kablink.teaming.fi.connection.acl.AclResourceDriver;
import org.kablink.teaming.modelprocessor.InstanceLevelProcessorSupport;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.NetFolderHelper;
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
@SuppressWarnings("unchecked")
public abstract class Binder extends DefinableEntity implements WorkArea, InstanceLevelProcessorSupport  {
	/**
	 * Different values for the sync schedule option 
	 */
	public enum SyncScheduleOption
	{
		/**
		 * The sync schedule defined on the net folder server this net folder points to should be
		 * used to perform scheduled syncs on this net folder.
		 * 
		 */
		useNetFolderServerSchedule( (short)1 ),
		
		/**
		 * The sync schedule defined on the net folder should be used to perform schedule syncs on
		 * this net folder.
		 */
		useNetFolderSchedule( (short)2 );
		
		short value;
		
		/**
		 * 
		 */
		SyncScheduleOption( short value )
		{
			this.value = value;
		}
		
		/**
		 * 
		 */
		public short getValue()
		{
			return value;
		}
		
		/**
		 * 
		 */
		public static SyncScheduleOption valueOf( short value )
		{
			switch(value)
			{
			case 1:
				return SyncScheduleOption.useNetFolderServerSchedule;
				
			case 2:
				return SyncScheduleOption.useNetFolderSchedule;
				
			default:
				throw new IllegalArgumentException( "Invalid db value " + value + " for enum SyncScheduleOption" );
			}
		}
	}

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
    protected String brandingExt;	// Additional branding information such as background color, font color, background image.
    protected Boolean postingEnabled;
    protected String type;
    protected Definition entryDef; // initialized by hibernate access=field
    //File related settings
    protected Boolean versionsEnabled;
	protected Long versionsToKeep;
	protected Long versionAgingDays;
    protected Boolean versionAgingEnabled;
    protected Long maxFileSize;	//MB (stored as the maximum number of mega-bytes)
    protected Boolean fileEncryptionEnabled;
    protected Boolean extFunctionMembershipInherited = Boolean.TRUE;
    protected Boolean homeDir = Boolean.FALSE;
    protected Boolean myFilesDir = Boolean.FALSE;
    protected Boolean allowDesktopAppToSyncData = Boolean.TRUE;
    protected Boolean allowMobileAppsToSyncData = Boolean.TRUE;
    protected Boolean indexContent = Boolean.TRUE;
    protected Boolean jitsEnabled; // Applicable only to mirrored folders
    protected Long jitsMaxAge; // in milliseconds
    protected Long jitsAclMaxAge; // in milliseconds
    protected Boolean fullSyncDirOnly; // Applicable only to mirrored folders
    protected Boolean allowDesktopAppToTriggerInitialHomeFolderSync = Boolean.FALSE;
    protected Short syncScheduleOption;	// SyncScheduleOption
    protected String resourceHandle;
    protected Boolean useInheritedIndexContent = Boolean.TRUE;
    protected Boolean useInheritedJitsSettings = Boolean.TRUE;
    protected Boolean useInheritedDesktopAppTriggerSetting = Boolean.TRUE;

    
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
		 brandingExt = source.brandingExt;
		 //don't copy postingDef, notificationDef, internalId, binders, or pathName
		 entryDef = source.entryDef;
		 allowDesktopAppToSyncData = source.allowDesktopAppToSyncData;
		 allowMobileAppsToSyncData = source.allowMobileAppsToSyncData;
		 indexContent = source.indexContent;
		 jitsEnabled = source.jitsEnabled;
		 jitsMaxAge = source.jitsMaxAge;
		 jitsAclMaxAge = source.jitsAclMaxAge;
		 fullSyncDirOnly = source.fullSyncDirOnly;
		 allowDesktopAppToTriggerInitialHomeFolderSync = source.allowDesktopAppToTriggerInitialHomeFolderSync;
		 syncScheduleOption = source.syncScheduleOption;
		 useInheritedIndexContent = source.useInheritedIndexContent;
		 useInheritedJitsSettings = source.useInheritedJitsSettings;
		 useInheritedDesktopAppTriggerSetting = source.useInheritedDesktopAppTriggerSetting;
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
	public String getResourcePath() {
		if(resourcePath != null && resourcePath.equals("/"))
			return "";
		else
			return resourcePath;
	}
	public void setResourcePath(String resourcePath) {
		if(resourcePath != null && resourcePath.equals("")) {
			// bugzilla 513609 - To workaround problem with Oracle
			resourcePath = "/";
		}
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
		ResourceDriver rd = null;
		if (getResourceDriverName() != null) {
			try {
				rd = ResourceDriverManagerUtil.findResourceDriver(getResourceDriverName());
			} catch(FIException e) {}
			return rd;
		} else {
			return null;
		}
	}
	
	public DriverType getResourceDriverType() {
		// Just a convenience method
		if(getResourceDriverName() != null)
			return ResourceDriverManagerUtil.getResourceDriverType(getResourceDriverName());
		else
			return null;
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
     * Return whether the desktop app can trigger initial home folder sync
     * @return
     */
    public boolean getAllowDesktopAppToTriggerInitialHomeFolderSync()
    {
    	if ( allowDesktopAppToTriggerInitialHomeFolderSync == null )
    		return false;
    	else
    		return allowDesktopAppToTriggerInitialHomeFolderSync.booleanValue();
    }
    
    /**
     * 
     */
    public void setAllowDesktopAppToTriggerInitialHomeFolderSync( boolean allow )
    {
   		allowDesktopAppToTriggerInitialHomeFolderSync = new Boolean( allow );
    }
    
    /**
     * Return whether the desktop app can sync data from this binder
     * @return
     */
    public boolean getAllowDesktopAppToSyncData()
    {
    	if ( allowDesktopAppToSyncData == null )
    		return true;
    	else
    		return allowDesktopAppToSyncData.booleanValue();
    }
    
    public void setAllowDesktopAppToSyncData( boolean allow )
    {
   		allowDesktopAppToSyncData = new Boolean( allow );
    }
    
    /**
     * Return whether mobile apps can sync data from this binder
     * @return
     */
    public boolean getAllowMobileAppsToSyncData()
    {
    	if ( allowMobileAppsToSyncData == null )
    		return true;
    	else
    		return allowMobileAppsToSyncData.booleanValue();
    }
    
    public void setAllowMobileAppsToSyncData( boolean allow )
    {
   		allowMobileAppsToSyncData = new Boolean( allow );
    }
    
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
     * Return whether the the "allow desktop app to trigger initial home folder sync" setting
     * should be inherited from the net folder server.
     * @return
     */
    public boolean getUseInheritedDesktopAppTriggerSetting()
    {
    	boolean useInherited;
    	
    	if ( useInheritedDesktopAppTriggerSetting == null )
   			useInherited = true;
    	else
    		useInherited = useInheritedDesktopAppTriggerSetting.booleanValue();
    	
    	return useInherited;
    }

    /**
     * 
     */
    public void setUseInheritedDesktopAppTriggerSetting( boolean inherit )
    {
   		useInheritedDesktopAppTriggerSetting = new Boolean( inherit );
    }
    
    /**
     * Return whether the contents of this binder should be indexed.
     * @return
     */
    public boolean getIndexContent()
    {
    	if ( indexContent == null )
    		return true;
    	else
    		return indexContent .booleanValue();
    }

    /**
     * 
     */
    public void setIndexContent( boolean index )
    {
   		indexContent = new Boolean( index );
    }
    
    /**
     * Return the computed value of "index content".  If this binder is inheriting the value
     * of "index content" then we will get the value of "index content" from the net folder server
     * this binder is pointing to.  Otherwise, we will use the value of "index content" from
     * this binder.
     */
    public boolean getComputedIndexContent()
    {
    	ResourceDriver resourceDriver;
    	
    	if ( getUseInheritedIndexContent() == false )
    		return getIndexContent();
    	
    	resourceDriver = getResourceDriver();
    	if ( resourceDriver != null )
    	{
    		ResourceDriverConfig rdConfig;
    		
    		rdConfig = resourceDriver.getConfig();
    		if ( rdConfig != null )
    			return rdConfig.getIndexContent();
    	}
    	
    	return false;
    }
    
    /**
     * Return whether the the "index content" setting should be inherited from the net folder server.
     * @return
     */
    public boolean getUseInheritedIndexContent()
    {
    	boolean useInherited;
    	
    	// If the useInheritedIndexContent field is null that means this binder existed
    	// before we added this field.
    	if ( useInheritedIndexContent == null )
    	{
    		// If the content of this binder should be indexed, then we will say not to inherit
    		// the "index content" setting from the net folder server.
    		if ( getIndexContent() == true )
    			useInherited = false;
    		else
    			useInherited = true;
    	}
    	else
    	{
    		useInherited = useInheritedIndexContent.booleanValue();
    	}
    	
    	return useInherited;
    }

    /**
     * 
     */
    public void setUseInheritedIndexContent( boolean inherit )
    {
   		useInheritedIndexContent = new Boolean( inherit );
    }
    

    /**
     * Return whether the the jits settings should be inherited from the net folder server.
     * @return
     */
    public boolean getUseInheritedJitsSettings()
    {
    	boolean useInherited;
    	
    	// If the useInheritedJitsSettings field is null that means this binder existed
    	// before we added this field.
    	if ( useInheritedJitsSettings == null )
    	{
    		// Are we dealing with a home dir net folder?
    		if ( isHomeDir() )
    		{
    			// Yes
    			// Has the value of "enable jits" changed from the default?
    			// The default is true.
    			if ( isJitsEnabled() == false )
    			{
    				// Yes
    				useInherited = false;
    			}
    			else
    				useInherited = true;
    		}
    		else
    		{
    			// No
    			useInherited = false;
    		}
    	}
    	else
    	{
    		useInherited = useInheritedJitsSettings.booleanValue();
    	}
    	
    	return useInherited;
    }

    /**
     * 
     */
    public void setUseInheritedJitsSettings( boolean inherit )
    {
   		useInheritedJitsSettings = new Boolean( inherit );
    }
    
    /**
     * Return the computed value of "Enable Jits".  If this binder is inheriting the jits
     * settings then we will get the value of "enable jits" from the net folder server
     * this binder is pointing to.  Otherwise, we will use the value of "enable jits" from
     * this binder.
     */
    public boolean getComputedIsJitsEnabled()
    {
    	ResourceDriver resourceDriver;
    	
    	if ( getUseInheritedJitsSettings() == false )
    		return isJitsEnabled();
    	
    	resourceDriver = getResourceDriver();
    	if ( resourceDriver != null )
    	{
    		ResourceDriverConfig rdConfig;
    		
    		rdConfig = resourceDriver.getConfig();
    		if ( rdConfig != null )
    			return rdConfig.isJitsEnabled();
    	}
    	
    	return false;
    }
    
    /**
     * Return the computed value of "Jits max age".  If this binder is inheriting the jits
     * settings then we will get the value of "jits max age" from the net folder server
     * this binder is pointing to.  Otherwise, we will use the value of "jits max age" from
     * this binder.
     */
    public long getComputedJitsMaxAge()
    {
    	ResourceDriver resourceDriver;
    	
    	if ( getUseInheritedJitsSettings() == false )
    		return getJitsMaxAge();
    	
    	resourceDriver = getResourceDriver();
    	if ( resourceDriver != null )
    	{
    		ResourceDriverConfig rdConfig;
    		
    		rdConfig = resourceDriver.getConfig();
    		if ( rdConfig != null )
    			return rdConfig.getJitsMaxAge();
    	}
    	
    	return getJitsMaxAge();
    }
    
    /**
     * Return the computed value of "Jits max acl age".  If this binder is inheriting the jits
     * settings then we will get the value of "jits max acl age" from the net folder server
     * this binder is pointing to.  Otherwise, we will use the value of "jits max acl age" from
     * this binder.
     */
    public long getComputedJitsAclMaxAge()
    {
    	ResourceDriver resourceDriver;
    	
    	if ( getUseInheritedJitsSettings() == false )
    		return getJitsAclMaxAge();
    	
    	resourceDriver = getResourceDriver();
    	if ( resourceDriver != null )
    	{
    		ResourceDriverConfig rdConfig;
    		
    		rdConfig = resourceDriver.getConfig();
    		if ( rdConfig != null )
    			return rdConfig.getJitsAclMaxAge();
    	}
    	
    	return getJitsAclMaxAge();
    }
    
    public boolean isJitsEnabled() {
    	if(jitsEnabled == null)
    		return SPropsUtil.getBoolean("nf.jits.enabled", true);
    	else
    		return jitsEnabled.booleanValue();
    }
    public void setJitsEnabled(boolean jitsEnabled) {
    	this.jitsEnabled = jitsEnabled;
    }
    
	public long getJitsMaxAge() {
		if(jitsMaxAge == null)
			return NetFolderHelper.getDefaultJitsResultsMaxAge();
		else 
			return jitsMaxAge.longValue();
	}
	public void setJitsMaxAge(long jitsMaxAge) {
		this.jitsMaxAge = Long.valueOf(jitsMaxAge);
	}
    
	public long getJitsAclMaxAge() {
		if(jitsAclMaxAge == null)
			return NetFolderHelper.getDefaultJitsAclMaxAge();
		else 
			return jitsAclMaxAge.longValue();
	}
	public void setJitsAclMaxAge(long jitsAclMaxAge) {
		this.jitsAclMaxAge = Long.valueOf(jitsAclMaxAge);
	}
    
	/**
	 * 
	 */
	public Boolean getFullSyncDirOnly()
	{
		return fullSyncDirOnly;
	}
	
	public boolean isFullSyncDirOnly() {
		if(fullSyncDirOnly == null)
			return SPropsUtil.getBoolean("nf.full.sync.dir.only", false);
		else
			return fullSyncDirOnly.booleanValue();
	}
	public void setFullSyncDirOnly( Boolean fullSyncDirOnly ) {
		this.fullSyncDirOnly = fullSyncDirOnly;
	}
	
    /**
     * Get the xml document that holds the landing page properties such as the background color,
     * background image, etc. 
     */
    public Document getLandingPageProperties()
    {
		CustomAttribute customAttr;
		Document doc;

		doc = null;
		customAttr = getCustomAttribute( "mashup" + DefinitionModule.MASHUP_PROPERTIES );
		if ( customAttr != null && customAttr.getValueType() == CustomAttribute.XML )
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
		Document doc;
		
		doc = getLandingPageProperties();
		if ( doc != null )
    		return this;
        
    	if ( parentBinder == null )
    		return this;
        
    	return parentBinder.getLandingPagePropertiesSourceBinder();
	}
    

    /**
     * Return the sync schedule option.  Currently there are 2 possible values:
     * "Use sync schedule from net folder server" and "Use sync schedule from net folder"
     * @return
     */
    public SyncScheduleOption getSyncScheduleOption()
    {
    	if ( syncScheduleOption == null )
    		return null;
    	
    	return SyncScheduleOption.valueOf( syncScheduleOption.shortValue() );
    }

    /**
     * 
     */
    public void setSyncScheduleOption( SyncScheduleOption option )
    {
    	if ( option == null )
    		syncScheduleOption = null;
    	else
    		syncScheduleOption = new Short( option.getValue() );
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
    
    public boolean isHomeDir() {
    	if(homeDir == null)
    		return false;
    	else
    		return homeDir.booleanValue();
    }
    public void setHomeDir(boolean homeDir) {
    	this.homeDir = homeDir;
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
    /**
     * Return the team member ids
     * @return
     */
    @Override
	public Set<Long> getTeamMemberIds() {
    	if (!isRoot() && isTeamMembershipInherited()) return getParentBinder().getTeamMemberIds();
    	String members = (String)getProperty(ObjectKeys.BINDER_PROPERTY_TEAM_MEMBERS);
    	return LongIdUtil.getIdsAsLongSet(members);
    	
    }
     @Override
	public void setTeamMemberIds(Set<Long> memberIds) {
    	//setting inherited flag handled separate
    	if ((memberIds == null) || memberIds.isEmpty()) removeProperty(ObjectKeys.BINDER_PROPERTY_TEAM_MEMBERS);
    	else setProperty(ObjectKeys.BINDER_PROPERTY_TEAM_MEMBERS, LongIdUtil.getIdsAsString(memberIds));
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
}

package com.sitescape.team.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.team.util.CollectionUtil;
import com.sitescape.team.web.util.WebHelper;

public abstract class DefinableEntity extends PersistentLongIdTimestampObject {
    protected String title=""; //initialized by hibernate access=field
    protected String normalTitle=""; 
    protected Description description;
    protected Long logVersion=Long.valueOf(0);
    protected Binder parentBinder; 
    protected boolean attachmentsParsed = false;
    protected Set attachments;	//initialized by hiberate access=field
    protected Map customAttributes;	//initialized by hiberate access=field
    protected Definition entryDef; //initialized by hiberate access=field
    protected Set events;	//initialized by hiberate access=field
    protected String iconName="";
    protected Integer definitionType=null;
    protected AverageRating rating=null;
    protected Long popularity=null;
    // these collections are loaded for quicker indexing, hibernate will not persist them
    protected Set iEvents,iAttachments;
    protected Map iCustomAttributes;
    protected EntityIdentifier entityIdentifier;
    
    public DefinableEntity() {
    }
    public DefinableEntity(DefinableEntity source) {
    	title = source.title;
    	normalTitle = source.normalTitle;
    	description = new Description(source.description);
    	entryDef = source.entryDef;
    	iconName = source.iconName;
    	definitionType = source.definitionType;
    	//don't copy parentBinder, 
    	//cannot copy events,customattribute and attachments, since they need a ownerId
    	
    }
	public EntityIdentifier getEntityIdentifier() {
		if (entityIdentifier == null) entityIdentifier = new EntityIdentifier(getId(), getEntityType());
		return entityIdentifier;
	}
	public abstract EntityIdentifier.EntityType getEntityType();

   	
    /**
     * @hibernate.component prefix="description_"
     */
    public Description getDescription() {
        return this.description;
    }
    public void setDescription(Description description) {
        if (this.description != null)
        	// try to avoid unecessary updates
        	if (this.description.equals(description)) return;
    	this.description = description; 
    }
  
    public void setDescription(String descriptionText) {
		Description tmp = new Description(descriptionText);
    	if (description != null) {
    		if (description.equals(tmp)) return;
    	}
        this.description = tmp; 
    }
    /**
     * @hibernate.property length="256"
     * @return
     */
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
        //set the normalized title
		setNormalTitle(WebHelper.getNormalizedTitle(title));
    }
    /**
     * Normalized title for wiki links
     * @hibernate.property length="256"
     * @return
     */
    public String getNormalTitle() {
        return normalTitle;
    }
    public void setNormalTitle(String normalTitle) {
        this.normalTitle = normalTitle;
    }
    /** 
     * @hibernate.many-to-one access="field" class="com.sitescape.team.domain.Definition"
     * @hibernate.column name="entryDef" sql-type="char(32)"
     * @return
     */
    public Definition getEntryDef() {
    	return entryDef;
    }
    
    public void setEntryDef(Definition entryDef) {
        this.entryDef = entryDef;
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
     * @hibernate.property
     * @return
     */
    public Long getLogVersion() {
    	return logVersion;
    }
    public void setLogVersion(Long logVersion) {
    	this.logVersion = logVersion;
    }
    public void incrLogVersion() {
    	logVersion = logVersion + 1;
    }
    /**
     * @hibernate.property
     * @return
     */
    public Integer getDefinitionType() {
   	 	return definitionType;
    }
    public void setDefinitionType(Integer definitionType) {
   	 	this.definitionType = definitionType;
    }
    /**
     * @hibernate.component prefix="rating_"
     * @return
     */
    public AverageRating getAverageRating() {
   	 	return rating;
    }
    public void setAverageRating(AverageRating rating) {
   	 	this.rating = rating;
    }
    /**
     * @hibernate.property
     * @return
     */
    public Long getPopularity() {
   	 	return popularity;
    }
    public void setPopularity(Long popularity) {
   	 	this.popularity = popularity;
    }
    /**
     * @hibernate.property length="64"
     */
    public String getIconName() {
    	return iconName;
    }
    public void setIconName(String iconName) {
    	this.iconName = iconName;
    }

    /**
     * Events are only accessed through custom attributes
     * Remove an event.  Event object will be deleted from
     * database unless it is added somewhere else.
     * @param event
     */
    protected void removeEvent(Event event) {
       	if (event == null) return;
        if (events == null) events = new HashSet();
        events.remove(event);
        event.setOwner((AnyOwner)null);           	
    }
    /**
     * Add an event
     * @param event
     */
    protected void addEvent(Event event) {
    	if (event == null) return;
        if (events == null) events = new HashSet();
        events.add(event);
        event.setOwner(this);
    }
    /**
     * Find event by id - used by CustomAttribute
     * @param id
     */
    protected Event getEvent(String id) {
    	//if not indexing, using hibernate maintained list
    	if (iEvents == null) {
    		if (events == null) return null;
    		for (Iterator iter=events.iterator(); iter.hasNext();) {
    			Event e = (Event)iter.next();
    			if (e.getId().equals(id)) {
    				return e;
    			}	
    		}
    	} else {
    		//must be indexing.  Check manually loaded list
       		for (Iterator iter=iEvents.iterator(); iter.hasNext();) {
    			Event e = (Event)iter.next();
    			if (e.getId().equals(id)) {
    				return e;
    			}	
    		}
    		
    	}
    	return null;
    	
    }
    public Set getEvents() {
    	if (iEvents !=null) return iEvents;
    	if (events == null) events = new HashSet();
    	return events;
    }
    /**
     * Return all attachments 
     * 
	 */
    public Set getAttachments() {
    	if (iAttachments != null) return iAttachments;
       	//need to implement here to setup the doclet tags
    	if (attachments == null) attachments = new HashSet();
    	return attachments;
    	
    }
    /**
     * Set attachments - this will effect File type custom attributes also
     */
    public void setAttachments(Collection attachments) {   	
    	getAttachments();
    	Set remM = CollectionUtil.differences(this.attachments, attachments);
    	Set addM = CollectionUtil.differences(attachments, this.attachments);
        for (Iterator iter = remM.iterator(); iter.hasNext();) {
        	Attachment a = (Attachment)iter.next();
        	cleanupAttributes(a);
        	a.setOwner((AnyOwner)null);
        	this.attachments.remove(a);
        }
        for (Iterator iter = addM.iterator(); iter.hasNext();) {
        	Attachment a = (Attachment)iter.next();
        	a.setOwner(this);
        	this.attachments.add(a);
        }
    }
    /**
     * Get an attachment by database id - used by CustomAttribute lookup
     */
    public Attachment getAttachment(String id) {
       	//if not indexing, using hibernate maintained list
    	if (iAttachments == null) {
    		//make sure loaded
    		getAttachments();
    		for (Iterator iter=attachments.iterator(); iter.hasNext();) {
    			Attachment a = (Attachment)iter.next();
    			if (a.getId().equals(id)) {
    				return a;
    			}
    		}
    	} else {
    		//must be indexing.  Check manually loaded list
      		for (Iterator iter=iAttachments.iterator(); iter.hasNext();) {
    			Attachment a = (Attachment)iter.next();
    			if (a.getId().equals(id)) {
    				return a;
    			}
    		}
   		
    	}
    	return null;
    }
    /**
     * Remove an attachment.  Attachment object will be deleted from
     * database unless it is added somewhere else.
     * @param attachemnt
     */
    public void removeAttachment(Attachment att) {
       	if (att == null) return;
        getAttachments();
        //remove any custom attributes that point here
        cleanupAttributes(att);
        attachments.remove(att);
       	att.setOwner((AnyOwner)null);           	
    }
    /**
     * Add an attachment
     * @param att
     */
    public void addAttachment(Attachment att) {
    	if (att == null) return;
        getAttachments();
        attachments.add(att);
 	   	att.setOwner(this);
    }

    /**
     * Return list of FileAttachments
     * @return
     */
    public List getFileAttachments() {
    	Set atts = getAttachments();
    	List result = new ArrayList();
    	Attachment att;
    	for (Iterator iter=atts.iterator(); iter.hasNext();) {
    		att = (Attachment)iter.next();
    		//return only file attachments, not versions
    		if (att instanceof FileAttachment) {
    			result.add(att);
    		}
    	}
    	return result;
    }
    
    /**
     * Returns a list of <code>FileAttachment</code> whose repository name
     * matches the argument. 
     * 
     * @param repositoryName
     * @return
     */
    public List getFileAttachments(String repositoryName) {
    	Set atts = getAttachments();
    	Attachment att;
    	FileAttachment fatt;
    	List result = new ArrayList();
    	for (Iterator iter=atts.iterator(); iter.hasNext();) {
    		att = (Attachment)iter.next();
    		if (att instanceof FileAttachment) {
    			fatt = (FileAttachment) att;
    			if(fatt.getRepositoryName().equals(repositoryName))
    				result.add(att);
    		}
    	}
    	return result;
    }

    /**
     * Return FileAttachment corresponding to the specified combination of 
     * repository service name and file name. In other words, file namespace
     * is not based on the file name alone. 
     * 
     * @param fileName
     * @return
     */
/*To reduce confusion with library folders, don't allow repositorys to store the same file
 *    public FileAttachment getFileAttachment(String repositoryName, String fileName) {
    	Set atts = getAttachments();
    	Attachment att;
    	FileAttachment fatt;
    	for (Iterator iter=atts.iterator(); iter.hasNext();) {
    		att = (Attachment)iter.next();
    		if (att instanceof FileAttachment) {
    			fatt = (FileAttachment) att;
    			if(fatt.getRepositoryName().equals(repositoryName) &&
    					fatt.getFileItem().getName().equalsIgnoreCase(fileName))
    				return fatt;
    		}
    	}
    	return null;
    }
*/   
    /**
     * Return FileAttachment corresponding to the specified combination of 
     * repository service name and file name. In other words, file namespace
     * is not based on the file name alone. 
     * 
     * @param fileName
     * @return
     */
    public FileAttachment getFileAttachment(String fileName) {
    	Set atts = getAttachments();
    	Attachment att;
    	FileAttachment fatt;
    	for (Iterator iter=atts.iterator(); iter.hasNext();) {
    		att = (Attachment)iter.next();
    		if (att instanceof FileAttachment) {
    			fatt = (FileAttachment) att;
    			if(fatt.getFileItem().getName().equalsIgnoreCase(fileName))
    				return fatt;
    		}
    	}
    	return null;
    }
    
    /**
     * Return list of custom attributes. 
     */
    // doclet tags need to be specified in concrete class
	public Map getCustomAttributes() {
		if (iCustomAttributes != null) return iCustomAttributes;
		if (customAttributes == null) customAttributes = new HashMap();
    	return customAttributes;
    }
	/**
	 * Create a new custom attribute and add it to this entry
	 * @param name
	 * @param value
	 * @return
	 */
	public CustomAttribute addCustomAttribute(String name, Object value) {
    	//make sure set up
    	Map atts = getCustomAttributes();
    	if (atts.containsKey(name)) throw new IllegalArgumentException("name exists");
    	CustomAttribute attr = new CustomAttribute(this, name, value);
    	atts.put(name, attr);
    	return attr;
    }
    /**
     * Remove a customAttribute.  Will be deleted from the persistent store.
     * @param customAttribute
     */
    public void removeCustomAttribute(CustomAttribute attr) {
       	if (attr == null) return;   	
       	getCustomAttributes().remove(attr.getName());
       	//the setowner code will make sure any event/files are disconnected
        attr.setOwner((AnyOwner)null);      	
       	
    }
    /**
     * Remove a customAttribute.  Will be deleted from the persistent store.
     * @param name
     */
    public void removeCustomAttribute(String name) {
        CustomAttribute c = getCustomAttribute(name);
        if (c != null) removeCustomAttribute(c);
     }
    /**
     * Retrieve named customAttibute
     * @param name
     * @return
     */
    public CustomAttribute getCustomAttribute(String name) {
    	return (CustomAttribute)getCustomAttributes().get(name);
   }
    /**
     * Retrieve customAttibute
     * @param name
     * @return
     */
    public CustomAttribute getCustomAttributeById(String id) {
    	Map attrs = getCustomAttributes();
    	for (Iterator iter=attrs.values().iterator(); iter.hasNext(); ) {
    		CustomAttribute a = (CustomAttribute)iter.next();
    		if (id.equals(a.getId())) return a;
    	}
    	return null;
   }
    
    /**
     * After an attachment is removed, we must remove it from
     * any custom attributes
     * @param att
     */
    protected void cleanupAttributes(Attachment attachment) {
    	Map attrs = getCustomAttributes();
    	for (Iterator iter=attrs.values().iterator(); iter.hasNext();) {
    		CustomAttribute attr = (CustomAttribute)iter.next();
    		int type = attr.getValueType();
    		if (type == CustomAttribute.ATTACHMENT) {
    			if (attachment.getId().equals(attr.getValue())) 
    				removeCustomAttribute(attr);
    		} else if (type == CustomAttribute.SET) {
    			Set vals = (Set)attr.getValue();
    			
    			Iterator vIter=vals.iterator();
    			if (vIter.hasNext()) {
    				Object obj = vIter.next();
    				//see if set of attachments
    				if (obj instanceof Attachment) {
    					vals.remove(attachment);
    					if (vals.isEmpty()) removeCustomAttribute(attr);
    					else attr.setValue(vals);
    				}
    			}
    		}
    	}
    }
    public String getIndexDocumentUid() {
        return BasicIndexUtils.makeUid(this.getClass().getName(), this.getId());
    }
    /*
     * The following methods are used for performance optimization during indexing.
     * The values of each collection are loaded and built by hand.  
     * They are not persisted.  This allows us to load greater than the 
     * hibernate "batch-size" number of collections at once.
     */
     public void setIndexEvents(Set iEvents) {
    	this.iEvents = iEvents;
    }    
    public void setIndexAttachments(Set iAttachments) {
    	this.iAttachments = iAttachments;
    }
    public void setIndexCustomAttributes(Map iCustomAttributes) {
    	this.iCustomAttributes = iCustomAttributes;
    }
    
    public String getTypedId() {
    	return getEntityType().name() + "_" + getEntityIdentifier().getEntityId();
    }
}

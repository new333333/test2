/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.dom4j.Document;

import org.kablink.teaming.comparator.FileAttachmentComparator;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.search.BasicIndexUtils;
import org.kablink.teaming.util.CollectionUtil;
import org.kablink.teaming.util.IconSize;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.util.cache.DefinitionCache;
import org.kablink.teaming.web.util.WebHelper;

/**
 * Abstract class used to define both binders and entries.  All <code>AnyOwner</code> associations refer to
 * this class.
 *
 */
@SuppressWarnings("unchecked")
public abstract class DefinableEntity extends BaseEntity {
    protected String title=""; //initialized by hibernate access=field
    protected String normalTitle=""; 
    protected Description description;
    protected Long logVersion=Long.valueOf(0);
    protected Binder parentBinder; 
    protected boolean attachmentsParsed = false;
	protected Set attachments;	//initialized by hibernate access=field
    protected Map customAttributes;	//initialized by hibernate access=field
    protected Set events;	//initialized by hibernate access=field
    protected String iconName="";
    protected Integer definitionType=null;
    protected AverageRating rating=null;
    // these collections are loaded for quicker indexing, hibernate will not persist them
    protected Set iEvents,iAttachments;
    protected Map iCustomAttributes;
    protected EntityIdentifier entityIdentifier;
	protected boolean deleted=false;
    
    public DefinableEntity() {
    }
    public DefinableEntity(DefinableEntity source) {
    	super(source);
    	title = source.title;
    	normalTitle = source.normalTitle;
    	description = new Description(source.description);
    	iconName = source.iconName;
    	definitionType = source.definitionType;
    	//don't copy parentBinder, 
    	//cannot copy events,custom attribute and attachments, since they need an ownerId
    	
    }
    
    @Override
	public EntityIdentifier getEntityIdentifier() {
		if (entityIdentifier == null) entityIdentifier = new EntityIdentifier(getId(), getEntityType());
		return entityIdentifier;
	}
   	
    /**
     * @hibernate.component prefix="description_"
     */
    public Description getDescription() {
        return this.description;
    }
    public void setDescription(Description description) {
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
    
    public abstract String getEntryDefId();
    
    public Document getEntryDefDoc() {
    	if(getEntryDefId() != null)
    		return DefinitionCache.getDocumentWithId(getEntryDefId());
    	else
    		return null;
    }
        
    public abstract void setEntryDef(Definition entryDef);
    
    public abstract String getCreatedWithDefinitionId();

    public Document getCreatedWithDefinitionDoc() {
    	if(getCreatedWithDefinitionId() != null)
    		return DefinitionCache.getDocumentWithId(getCreatedWithDefinitionId());
    	else
    		return null;
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
     * Objects may be in this state, awaiting final audit logging
     * before being purged from the system.
     * @hibernate.property
     */
    public boolean isDeleted() {
    	return deleted;
    }
    public void setDeleted(boolean deleted) {
    	this.deleted = deleted;
    }
   /**
     * Current version number.  This value is incremented when
     * a new <code>ChangeLog</code> is created
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
     * The type of definition used to create this entity.
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
     * @hibernate.component prefix="ratings_"
     * @return
     */
    public AverageRating getAverageRating() {
   	 	return rating;
    }
    public void setAverageRating(AverageRating rating) {
   	 	this.rating = rating;
    }
    /**
     * @hibernate.property length="64"
     */
    public String getIconName() {
    	String icoName = ((null == this.iconName) ? "" : this.iconName);
    	return Utils.getIconNameTranslated(icoName);
    }
    public String getIconName(IconSize size) {
    	String icoName = ((null == this.iconName) ? "" : this.iconName);
    	return Utils.getIconNameTranslated(icoName, size);
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
     * Return all attachments.
     * 
	 */
    public Set<Attachment> getAttachments() {
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
    	if (att == null || att.getId() == null) return;
        getAttachments();
        attachments.add(att);
 	   	att.setOwner(this);
    }

    /**
     * Return list of FileAttachments
     * @return
     */
    public SortedSet<FileAttachment> getFileAttachments() {
    	Set atts = getAttachments();
    	FileAttachmentComparator c = new FileAttachmentComparator(
    			RequestContextHolder.getRequestContext().getUser().getLocale(), FileAttachmentComparator.SortByField.name);
    	
    	SortedSet result = new TreeSet(c);
    	Attachment att;
    	for (Iterator iter=atts.iterator(); iter.hasNext();) {
    		att = (Attachment)iter.next();
    		//return only file attachments.  Version not in attachment list
    		if (att instanceof FileAttachment) {
    			result.add(att);
    		}
    	}
    	return result;
    }
    
    /**
     * Return the number of FileAttachments.
     * @return
     */
    public int getFileAttachmentsCount() {
    	Set atts = getAttachments();
    	int count = 0;
    	Attachment att;
    	for (Iterator iter=atts.iterator(); iter.hasNext();) {
    		att = (Attachment)iter.next();
    		//return only file attachments.  Version not in attachment list
    		if (att instanceof FileAttachment) {
    			count++;
    		}
    	}
    	return count;
    }
    
    /**
     * Returns a list of <code>FileAttachment</code> whose repository name
     * matches the argument. 
     * 
     * @param repositoryName
     * @return
     */
    public List<FileAttachment> getFileAttachments(String repositoryName) {
    	Set atts = getAttachments();
    	Attachment att;
    	FileAttachment fatt;
    	List<FileAttachment> result = new ArrayList();
    	for (Iterator iter=atts.iterator(); iter.hasNext();) {
    		att = (Attachment)iter.next();
    		if (att instanceof FileAttachment) {
    			fatt = (FileAttachment) att;
    			if(fatt.getRepositoryName().equals(repositoryName))
    				result.add(fatt);
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

    public String getPrimaryFileAttachmentId() {
        Set atts = getAttachments();
        Attachment att;
        @SuppressWarnings("unused")
        FileAttachment fatt;
        for (Iterator iter=atts.iterator(); iter.hasNext();) {
            att = (Attachment)iter.next();
            if (att instanceof FileAttachment) {
                return att.getId();
            }
        }
        return null;
    }

    public FileAttachment getPrimaryFileAttachment() {
        Set atts = getAttachments();
        Attachment att;
        @SuppressWarnings("unused")
        FileAttachment fatt;
        for (Iterator iter=atts.iterator(); iter.hasNext();) {
            att = (Attachment)iter.next();
            if (att instanceof FileAttachment) {
                return (FileAttachment) att;
            }
        }
        return null;
    }
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
	 * Create a new custom attribute and add it to this entity.
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
     * Retrieve named <code>CustomAttibute</code>
     * @param name
     * @return
     */
    public CustomAttribute getCustomAttribute(String name) {
    	return (CustomAttribute)getCustomAttributes().get(name);

   }
    /**
     * Retrieve <code>CustomAttibute</code> by its id.
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
    	//make a copy so removes won't collide with iterator
    	Collection caValues = getCustomAttributes().values();
    	Set<CustomAttribute> attrs = new HashSet(caValues);
    	for (CustomAttribute attr:attrs) {
    		int type = attr.getValueType();
    		if (type == CustomAttribute.ATTACHMENT) {
    			if (attachment.equals(attr.getValue())) 
    				removeCustomAttribute(attr);
    		} else if ((type == CustomAttribute.SET) || (type == CustomAttribute.ORDEREDSET)) {
    			Set vals = (Set)attr.getValue();
    			//if in set, see if set now empty, otherwise update value
    			if (vals.remove(attachment)) {	
    				if (vals.isEmpty()) removeCustomAttribute(attr);
    				else attr.setValue(vals);
    			}
    		}
    	}
    }
    /**
     * Unique identifier for indexing.
     * @return
     */
    public String getIndexDocumentUid() {
        return BasicIndexUtils.makeUid(this.getClass().getName(), this.getId());
    }
    /*
     * The following methods are used for performance optimization during indexing.
     * The values of each collection are loaded and built by hand.  
     * They are not persisted.  This allows us to load greater than the 
     * hibernate "batch-size" number of collections at once.
     */
    /**
     * Internal use for optimized indexing only.
     */
    public void setIndexEvents(Set iEvents) {
    	this.iEvents = iEvents;
    }    
    /**
     * Internal use for optimized indexing only.
     */
    public void setIndexAttachments(Set iAttachments) {
    	this.iAttachments = iAttachments;
    }
    /**
      * Internal use for optimized indexing only.
     */
   public void setIndexCustomAttributes(Map iCustomAttributes) {
    	this.iCustomAttributes = iCustomAttributes;
    }
    
    @Override
	public String getEntityTypedId() {
    	return getEntityType().name() + "_" + getEntityIdentifier().getEntityId();
    }
    /**
     * Return the title.
     */
    @Override
	public String toString() {
    	return title;
    }
    
    public boolean supportsCustomFields() {
    	return true;
    }

    public CustomAttribute getMashupPropertiesAttribute() {
        CustomAttribute customAttr;
        customAttr = getCustomAttribute( "mashup" + DefinitionModule.MASHUP_PROPERTIES );

        if (customAttr==null ||  customAttr.getValueType() != CustomAttribute.XML) {
            for (Object obj : getCustomAttributes().values()) {
                CustomAttribute attr = (CustomAttribute) obj;
                if (attr.getName().endsWith(DefinitionModule.MASHUP_PROPERTIES) && attr.getValueType() == CustomAttribute.XML) {
                    customAttr = attr;
                    break;
                }
            }
        }
        return customAttr;
    }

}

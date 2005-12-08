package com.sitescape.ef.domain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sitescape.ef.search.BasicIndexUtils;
import com.sitescape.ef.util.CollectionUtil;
import java.util.Collection;


/**
 * @author Jong Kim
 *
 */
public abstract class Entry extends PersistentLongIdTimestampObject 
	implements AttachmentSupport {

    private String title="";
    private Description description;
    protected boolean attachmentsParsed = false;
    protected List allAttachments;
    protected List unnamedAttachments;
    protected Map namedAttachments;
    protected Map customAttributes;
    protected Definition entryDef;
    protected boolean eventsParsed = false;
    protected List allEvents;
    protected List unnamedEvents;
    protected Map namedEvents;
    public Entry() {
    }
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
     * @hibernate.property length="1024"
     * @return
     */
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    /** 
     * @hibernate.many-to-one class="com.sitescape.ef.domain.Definition"
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
     * This will only return unnamed attachments.  Named attachments 
     * need to be handed as customAttributes
     * 
	 */
    public List getAttachments() {
       	//need to implement here to setup the doclet tags
    	setupAttachments();
    	return unnamedAttachments;
    }
    /**
     * Set list of unnamed attachments
     */
    public void setAttachments(Collection attachments) {   	
    	setupAttachments();
    	Set remM = CollectionUtil.differences(unnamedAttachments, attachments);
    	Set addM = CollectionUtil.differences(attachments, unnamedAttachments);
        for (Iterator iter = remM.iterator(); iter.hasNext();) {
        	Attachment a = (Attachment)iter.next();
        	a.setOwner((AnyOwner)null);
        	unnamedAttachments.remove(a);
        	allAttachments.remove(a);
        }
        for (Iterator iter = addM.iterator(); iter.hasNext();) {
        	Attachment a = (Attachment)iter.next();
        	a.setOwner(this);
        	a.setName(null);
        	unnamedAttachments.add(a);
        	allAttachments.add(a);
        }
    }
    /**
     * Get an unnamed attachment by database id
     */
    public Attachment getAttachment(String id) {
    	setupAttachments();
    	for (int i=0; i<unnamedAttachments.size(); ++i) {
    		Attachment a = (Attachment)unnamedAttachments.get(i);
    		if (a.getId().equals(id)) {
    			return a;
    		}
    	}
    	return null;
    }
    /**
     * Remove an unnamed attachment.  Attachment object will be deleted from
     * database unless it is added somewhere else.
     * @param attachemnt
     */
    public void removeAttachment(Attachment att) {
       	if (att == null) return;
        setupAttachments();
        unnamedAttachments.remove(att);
    	allAttachments.remove(att);
       	att.setOwner((AnyOwner)null);           	
    }
    /**
     * Add an unnamged attachment
     * @param att
     */
    public void addAttachment(Attachment att) {
    	if (att == null) return;
        setupAttachments();
        unnamedAttachments.add(att);
 	   	allAttachments.add(att);
 	   	att.setOwner(this);
      	att.setName(null);
    }
    /**
     * Used by customAttribute to getValue when the valueType is attachment
     * @param name
     * @return
     */
    protected Attachment getNamedAttachment(String name) {
    	if (name == null) throw new IllegalArgumentException("name is null");
    	setupAttachments();
     	return (Attachment)namedAttachments.get(name);
    }
    /**
     * Used by customAttribute to alter a value.
     * @param name
     */
    protected void removeNamedAttachment(String name) {
        Attachment att = getNamedAttachment(name);
    	if (att != null) {
    		allAttachments.remove(att);
    		namedAttachments.remove(name);
    		att.setOwner((AnyOwner)null);
    	}
    }
    /**
     * Used by customAttribue to add a attribute that is an attachment
     * @param att
     */
    protected void addNamedAttachment(Attachment att) {
        Attachment oldA = getNamedAttachment(att.getName());
        if (oldA != null) throw new IllegalArgumentException("name exists");
        allAttachments.add(att);
    	namedAttachments.put(att.getName(), att);   	
   		att.setOwner(this);
    }
    /**
     * Return list of unnamed FileAttachments
     * @return
     */
    public List getFileAttachments() {
    	List atts = getAttachments();
    	List result = new ArrayList();
    	Attachment att;
    	for (int i=0; i<atts.size(); ++i) {
    		att = (Attachment)atts.get(i);
    		//return only file attachments, not versions
    		if (att instanceof FileAttachment) {
    			result.add(att);
    		}
    	}
    	return result;
    }
    /**
     * Return list of unnamed bookmark Attachments
     * @return
     */
 
    public List getBookmarks() {
        List atts = getAttachments();
       	List result = new ArrayList();
    	Attachment att;
    	for (int i=0; i<atts.size(); ++i) {
    		att = (Attachment)atts.get(i);
     		if (att instanceof Bookmark) {
    			result.add(att);
    		}
    	}
    	return result;
    }
         
    /*
     * Internal routine to break attachments into a list of unnamed attachments and
     * a map of named attachments.   Named attachments are accesseed by customAttributes
     * Attachments are accessed by hibernate with 1 query to load them all as efficiently
     * as possible
     */
    protected void setupAttachments() {
    	if (attachmentsParsed) return;
    	attachmentsParsed=true;
        namedAttachments = new HashMap();
        unnamedAttachments = new ArrayList();
    	if (allAttachments == null)
    		allAttachments = new ArrayList();
            
        Attachment att;
        String aName;
        for (int i=0; i<allAttachments.size(); ++i) {
            att = (Attachment)allAttachments.get(i);
            aName = att.getName();
            if (aName != null) {
            	namedAttachments.put(aName, att);
            } else {
            	unnamedAttachments.add(att);
            }
        }
    }    
    /**
     * Return list of custom attributes. 
     */
    // doclet tags need to be specified in concrete class
	public Map getCustomAttributes() {
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
     * Return list of unnamed events.
     * @return
     */
    // doclet tags must be specified in concrete classes
    public List getEvents() {
  		//need to implement here to setup the doclet tags
    	setupEvents();
   		return unnamedEvents;
   	}   
    
    /**
     * Set collection of unnamed events.  Orphans will be disconnected, and
     * deleted from the database
     * @param events
     */
    public void setEvents(Collection events) {
    	setupEvents();
    	Set remM = CollectionUtil.differences(unnamedEvents, events);
    	Set addM = CollectionUtil.differences(events, unnamedEvents);
        for (Iterator iter = remM.iterator(); iter.hasNext();) {
        	Event e = (Event)iter.next();
        	e.setOwner((AnyOwner)null);
        	unnamedEvents.remove(e);
        	allEvents.remove(e);
        }
        for (Iterator iter = addM.iterator(); iter.hasNext();) {
        	Event e = (Event)iter.next();
        	e.setOwner(this);
        	e.setName(null);
        	unnamedEvents.add(e);
        	allEvents.add(e);
        }
 	
    }
    /**
     * Add a new unnamed event
     * @param event
     */
    public void addEvent(Event event) {
    	if (event == null) return;
    	setupEvents();
   		event.setOwner(this);
   	 	event.setName(null);
   	 	unnamedEvents.add(event);
   	 	allEvents.add(event);
    }
    /**
     * Remove an unnamed event
     * @param event
     */
    public void removeEvent(Event event) {
    	if (event == null) return;
    	setupEvents();
       	unnamedEvents.remove(event);
       	allEvents.remove(event);
       	event.setOwner((AnyOwner)null);
    }
    /**
     * Used by customAttribute to getValue when the valueType is event
     * @param name
     * @return
     */
    protected Event getNamedEvent(String name) {
    	if (name == null) throw new IllegalArgumentException("name is null");
    	setupEvents();
        return (Event)namedEvents.get(name);
    }
    /**
     * Used by customAttribute to change a value 
     * @param name
     */
    protected void removeNamedEvent(String name) {
        Event event = getNamedEvent(name);
    	if (event != null) {
    		allEvents.remove(event);
    		namedEvents.remove(name);
    		event.setOwner((AnyOwner)null);
    	}
    }
    /**
     * Used by customAttribute to add a new named event
     * @param event
     */
    protected void addNamedEvent(Event event) {
    	if (event == null) return;
        Event oldE = getNamedEvent(event.getName());
    	if (oldE != null) throw new IllegalArgumentException("name exists");
   		event.setOwner(this);
  		allEvents.add(event);
     	namedEvents.put(event.getName(), event);   	
    }
    /**
     * Internal routine to split the list of events into named and unnamed 
     * lists.  Named events are only accessed through the CustomAttribute that 
     * defines them
     *
     */
    protected void setupEvents() {
    	if (eventsParsed) return;
    	eventsParsed=true;
        namedEvents = new HashMap();
        unnamedEvents = new ArrayList();
    	if (allEvents == null)
    		allEvents = new ArrayList();
            
        Event e;
        String aName;
        for (int i=0; i<allEvents.size(); ++i) {
            e = (Event)allEvents.get(i);
            aName = e.getName();
            if (aName != null) {
            	namedEvents.put(aName, e);
            } else {
            	unnamedEvents.add(e);
            }
        }
    }   
    public String getIndexDocumentUid() {
        return BasicIndexUtils.makeUid(this.getClass().getName(), this.getId());
    }
}

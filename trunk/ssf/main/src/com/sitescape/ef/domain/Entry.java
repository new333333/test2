package com.sitescape.ef.domain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.Map;

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
    //this list is the entire collection of attachments.  It includes attachments associated with
    // custom attributes, so don't what the entire list exposed as attachments
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
        this.description = description; 
    }
  
    public void setDescription(String descriptionText) {
        this.description = new Description(descriptionText); 
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
    /*
     * doclet tags must be specified in concrete class
     * This will only return unnamed attachments
	 */
    public List getAttachments() {
       	//need to implement here to setup the doclet tags
    	setupAttachments();
    	return unnamedAttachments;
    }
 
    public void setAttachments(Collection attachments) {   	
        this.unnamedAttachments = CollectionUtil.mergeAsSet(getAttachments(), attachments);
        for (Iterator iter = attachments.iterator(); iter.hasNext();) {
        	Attachment a = (Attachment)iter.next();
        	a.setOwner(this);
        	a.setName(null);
        }
    }
    /**
     * Remove an attachment.  Will be deleted from the persistent store
     * @param attachemnt
     */
    public void removeAttachment(Attachment att) {
    	getAttachments().remove(att);
    }
    public void addAttachment(Attachment att) {
    	if (att == null) return;
    	//allways reset incase usage is changing an needs to be reparsed 
    	List aList= getAttachments();
    	if (!aList.contains(att)) {
    		aList.add(att);
      	   	att.setOwner(this);
      	   	att.setName(null);
    	}
    }
    /**
     * Used by customAttribute to getValue
     * @param attachmentId
     * @return
     */
    protected Attachment getNamedAttachment(String name) {
    	setupAttachments();
     	return (Attachment)namedAttachments.get(name);
    }
    protected void removeNamedAttachment(String name) {
        Attachment att = getNamedAttachment(name);
    	if (att != null) {
    		allAttachments.remove(att);
    		namedAttachments.remove(name);
    	}
    }
    protected void addNamedAttachment(Attachment att) {
    	setupAttachments();
    	if (!allAttachments.contains(att)) {
    		allAttachments.add(att);
    		att.setOwner(this);
    	}
    	namedAttachments.put(att.getName(), att);   	
    }
    public List getFileAttachments() {
    	List atts = getAttachments();
    	List result = new ArrayList();
    	Attachment att;
    	for (int i=0; i<atts.size(); ++i) {
    		att = (Attachment)atts.get(i);
    		//return only file attachments, not versions
    		if ((att instanceof FileAttachment) && !(att instanceof VersionAttachment)) {
    			result.add(att);
    		}
    	}
    	return result;
    }
    public List getLinks() {
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
            //skip named attachments - custom attributes will handle
            if (aName != null) {
            	namedAttachments.put(aName, att);
            } else {
            	unnamedAttachments.add(att);
            }
        }
    }    
    /**
     * doclet tags need to be specified in concrete class
     * @see com.sitescape.ef.domain.IEntry#getCustomAttributes()
     * 
     * There is not addCustomAttribute - because creating one links it to
     * this entry.
     */
    public Map getCustomAttributes() {
    	if (customAttributes == null) customAttributes = new HashMap();
    	return customAttributes;
    }
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
    /*
     * doclet tags must be specified in concrete classes
	 */
    public List getEvents() {
  		//need to implement here to setup the doclet tags
    	setupEvents();
   		return unnamedEvents;
   	}   
    
    
    public void setEvents(Collection events) {
    	unnamedEvents = CollectionUtil.mergeAsSet(getEvents(), events);
        for (Iterator iter = events.iterator(); iter.hasNext();) {
        	Event e = (Event)iter.next();
        	e.setOwner(this);
        	e.setName(null);
        }   	
    }
    public void addEvent(Event event) {
    	if (event == null) throw new IllegalArgumentException("event is null");
    	List eList = getEvents();
    	if (!eList.contains(event)) {
       		event.setOwner(this);
       	 	eList.add(event);
       	 	event.setName(null);
    	}
    }
    public void removeEvent(Event event) {
    	if (event == null) return;
       	getEvents().remove(event);
       	event.setOwner((AnyOwner)null);
    }
    protected Event getNamedEvent(String name) {
    	setupEvents();
        return (Event)namedEvents.get(name);
    }
    
    protected void removeNamedEvent(String name) {
        Attachment att = getNamedAttachment(name);
    	if (att != null) {
    		allEvents.remove(att);
    		namedEvents.remove(name);
    	}
    }
    protected void addNamedEvent(Event event) {
    	setupEvents();
    	if (!allEvents.contains(event)) {
    		allEvents.add(event);
    		event.setOwner(this);
    	}
    	namedEvents.put(event.getName(), event);   	
    }
    
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
            //skip named attachments - custom attributes will handle
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

package com.sitescape.ef.domain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import com.sitescape.ef.search.BasicIndexUtils;
import com.sitescape.ef.util.CollectionUtil;
import java.util.Collection;


/**
 * @author Jong Kim
 *
 */
public abstract class Entry extends PersistentLongIdTimestampObject 
	implements AttachmentSupport, MultipleWorkflowSupport {

    private String title="";
    private Description description;
    protected boolean attachmentsParsed = false;
    protected Set attachments;
    protected Map customAttributes;
    protected Definition entryDef;
    protected boolean eventsParsed = false;
    protected List allEvents;
    protected List unnamedEvents;
    protected Map namedEvents;
    protected Set workflowStates;   
    protected HistoryStamp workflowChange;
    protected Binder parentBinder;
    
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
    public Set getWorkflowStates() {
   	 	if (workflowStates == null) return new HashSet();
   	 	return workflowStates;  
     }
     public void setWorkflowStates(Set workflowStates) {
    	 //Since ids are assigned on WorkflowState, don't need to do anything
    	 //special to reduce updates.
    	 this.workflowStates = workflowStates;
     }
     public WorkflowState getWorkflowState(Long id) {
     	//Make sure initialized
     	getWorkflowStates();
		WorkflowState ws=null;
		for (Iterator iter=workflowStates.iterator(); iter.hasNext();) {
			ws = (WorkflowState)iter.next();
			if (ws.getId().equals(id)) return ws;
		}
		return null;
     }
     public void addWorkflowState(WorkflowState state) {
     	if (state == null) return;
    	//Make sure initialized
    	getWorkflowStates();
        workflowStates.add(state);
 	   	state.setOwner(this);
    }
    public void removeWorkflowState(WorkflowState state) {
     	if (state == null) return;
    	//Make sure initialized
    	getWorkflowStates();
        workflowStates.remove(state);
 	   	state.setOwner((AnyOwner)null);
    }
    /**
     * @hibernate.component class="com.sitescape.ef.domain.HistoryStamp" prefix="wrk_" 
     */
    public HistoryStamp getWorkflowChange() {
        return this.workflowChange;
    }
    public void setWorkflowChange(HistoryStamp workflowChange) {
        this.workflowChange = workflowChange;
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
     * @hibernate.many-to-one access="field" class="com.sitescape.ef.domain.Definition"
     * @hibernate.column name="entryDef" sql-type="char(32)"
     * @return
     */
    public Definition getEntryDef() {
    	if (entryDef != null) return entryDef;
    	return getParentBinder().getDefaultEntryDef();
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
     * Return all attachments 
     * 
	 */
    public Set getAttachments() {
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
     * Get an attachment by database id
     */
    public Attachment getAttachment(String id) {
    	//make sure loaded
    	getAttachments();
    	for (Iterator iter=attachments.iterator(); iter.hasNext();) {
    		Attachment a = (Attachment)iter.next();
    		if (a.getId().equals(id)) {
    			return a;
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
     * Return FileAttachment corresponding to the specified combination of 
     * repository service name and file name. In other words, file namespace
     * is not based on the file name alone. 
     * 
     * @param fileName
     * @return
     */
    public FileAttachment getFileAttachment(String repositoryServiceName, String fileName) {
    	Set atts = getAttachments();
    	Attachment att;
    	FileAttachment fatt;
    	for (Iterator iter=atts.iterator(); iter.hasNext();) {
    		att = (Attachment)iter.next();
    		if (att instanceof FileAttachment) {
    			fatt = (FileAttachment) att;
    			if(fatt.getRepositoryServiceName().equals(repositoryServiceName) &&
    					fatt.getFileItem().getName().equals(fileName))
    				return fatt;
    		}
    	}
    	return null;
    }
    
    /**
     * Return list of unnamed bookmark Attachments
     * @return
     */
 
    public List getBookmarks() {
        Set atts = getAttachments();
       	List result = new ArrayList();
    	Attachment att;
    	for (Iterator iter=atts.iterator(); iter.hasNext();) {
   		att = (Attachment)iter.next();
     		if (att instanceof Bookmark) {
    			result.add(att);
    		}
    	}
    	return result;
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
}

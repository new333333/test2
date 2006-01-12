/*
 * Created on Sep 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.domain;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import com.sitescape.ef.util.CollectionUtil;
import com.sitescape.util.Validator;
import org.dom4j.Document;
/**
 * @hibernate.class table="SS_CustomAttributes" dynamic-update="true" lazy="false" discriminator-value="A"
 * @hibernate.discriminator type="char" column="type"
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * @author janet
 * 
 *  Object to represent user attributes in the database.  Since the definition must be
 *  generic, we try to optimize by defining 2 columns.  1 is a varchar the other is a clob
 *  Assume the varchar is more efficient.
 *  Hide whether data is stored as clob or varchar.
 * 
 * Sets are stored in multiple entries.  Because all elements have the same name, we
 * don't set the foreign key on list elements.  
 * The name can remain the same, so user reporting can find the name of list members.
 * The folder/owner/ownerType fields should be used to walk up the object hierarchy.
 */
public class CustomAttribute  {
    protected String stringValue;
    protected Description description;
    protected Long longValue;
    protected Date dateValue;
    protected SSBlobSerializable serializedValue;
    protected SSBlobXML xmlValue;
    protected Boolean booleanValue;
    protected Set values;
    protected User user;

    protected int valueType=NONE;
    	private static final int NONE=0;
    	public static final int STRING= 1;
    	public static final int NUMBER= 2;
    	public static final int DATE= 3;
    	public static final int SERIALIZED= 4;
    	public static final int SET=5;
       	public static final int BOOLEAN=6;
       	public static final int DESCRIPTION=7;
       	public static final int XML=8;
       	public static final int EVENT=9;
       	public static final int ATTACHMENT=10;
       	public static final int USER=11;
   protected String name;
   protected AnyOwner owner;
   protected String id;
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
   //keep protected, want name set up
   protected CustomAttribute() {   	
   }
 
   //only accessible threw entry
   protected CustomAttribute(Entry parent, String name, Object value) {
   		setName(name);
   		//do before setValue incase value needs owner
   		setOwner(parent);
   		setValue(value);
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
 	protected void setOwner(Entry entry) {
   		owner = new AnyOwner(entry); 		
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
        if ((values != null) && !values.isEmpty()) {
        	for (Iterator iter=values.iterator(); iter.hasNext();) {
        		CustomAttributeListElement element = (CustomAttributeListElement)iter.next();
        		element.name = name;
        	}
        }
    }
    /**
     * @hibernate.property length="4000"
     */
    private String getStringValue() {
        return this.stringValue;
    }
    private void setStringValue(String value) {
        this.stringValue = value;
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
    
    /**
     * @hibernate.property 
     */
    private Long getLongValue() {
        return this.longValue;
    }
    private void setLongValue(Long value) {
        this.longValue = value;
    }
    /**
     * @hibernate.property 
     */
    private Date getDateValue() {
        return this.dateValue;
    }
    private void setDateValue(Date value) {
        this.dateValue = value;
    }        
    /**
     * @hibernate.property type="com.sitescape.ef.dao.util.SSBlobSerializableType"
     */
    private SSBlobSerializable getSerializedValue() {
        return this.serializedValue;
    }
    private void setSerializedValue(SSBlobSerializable value) {
        this.serializedValue = value;
    } 
    /**
     * @hibernate.property type="com.sitescape.ef.dao.util.SSXmlBlobType"
     */
    private SSBlobXML getXmlValue() {
        return this.xmlValue;
    }
    private void setXmlValue(SSBlobXML value) {
        this.xmlValue = value;
    } 
    /**
     * @hibernate.property 
     */
    private Boolean getBooleanValue() {
        return this.booleanValue;
    }
    private void setBooleanValue(Boolean value) {
        this.booleanValue = value;
    }  
    
    /**
     * @hibernate.set lazy="true" inverse="true" cascade="all,delete-orphan"  batch-size="4" 
     * @hibernate.key column="parent"
 	 * @hibernate.one-to-many class="com.sitescape.ef.domain.CustomAttributeListElement"
     * 
     * @return
     */
    private Set getValues() {
    	return values;
    }
    private void setValues(Set values) {
    	this.values = values;
    }

   /**
    * @hibernate.property 
    */   	
    public int getValueType() {
    	return this.valueType;
    }
    protected void setValueType(int type) {
        this.valueType = type;
    }

    private void clearVals() {
       if (valueType == EVENT) {
        	Event e = owner.getEntry().getEvent(stringValue);
        	if (e != null) {
        		owner.getEntry().removeEvent(e);
        		e.setName(null);
        	}
       }
       if (valueType == ATTACHMENT) {
        	Attachment a = owner.getEntry().getAttachment(stringValue);
        	if (a != null) a.setName(null);
        }
        stringValue=null;
        longValue=null;
        dateValue=null;
        booleanValue=null;
        //allways setting mutable values to null, causes unnecessary updates
        if ((description !=null) && !description.getText().equals(""))
        	description=null;
        if ((serializedValue != null) && (serializedValue.getValue() != null))
         	serializedValue = null;
        if ((xmlValue != null) && (xmlValue.getValue() != null))
         	xmlValue = null;
         //let hibernate delete the existing objects.
        if (values != null) values.clear();
        
    }
    public void setValue(Object value) {
    	//don't do unnecessary updates - especially for descriptions
    	if ((value != null) && value.equals(getValue())) return;
    	setValue(value, true);
    }

    protected void setValue(Object value, boolean allowed) {
    	if (value instanceof String) {
            clearVals();
            valueType = STRING;
            String val = (String) value;
            // this is returning unicode-16 lengths
            if (val.length() <= 2000) {
                stringValue=val;
                description = null;
            } else {
                description = new Description(val, Description.FORMAT_NONE);
                stringValue = null;
            }
        } else if (value instanceof Description) {
            valueType = DESCRIPTION;
            description = (Description)value;
    	} else if (value instanceof Boolean) {
            clearVals();
			valueType = BOOLEAN;
			booleanValue = (Boolean)value;			
        } else if (value instanceof Long) {
            clearVals();
            valueType = NUMBER;
            longValue = (Long)value;
        } else if (value instanceof Integer) {
            clearVals();
        	valueType = NUMBER;
        	longValue=new Long(((Integer)value).longValue());
        } else if (value instanceof Date) {
            clearVals();
            valueType = DATE;
            dateValue = (Date)value;
         } else if (allowed && (value instanceof Set)) {
            Set oldValues = values;
            values = null;
            clearVals();
            values = oldValues;
            valueType = SET;
         	HashSet newValues =	new HashSet();
         	for (Iterator iter=((Set)value).iterator(); iter.hasNext();) {
     			CustomAttributeListElement element = new CustomAttributeListElement(getName(), this, getOwner().getEntry());
     			//don't allow recursive collections
     			element.setValue(iter.next(), false);
     			newValues.add(element);
     		}
         	if (values == null) {
         		values = new HashSet();
         	}
         	Set remM = CollectionUtil.differences(values, newValues);
         	Set addM = CollectionUtil.differences(newValues, values);
         	values.removeAll(remM);
         	values.addAll(addM);
         } else if (value instanceof String[]) {
         	Set newValues = new HashSet();
         	String[] vals = (String[]) value;
         	for (int i=0;i<vals.length; ++i) {
         		newValues.add(vals[i]);
         	}
         	setValue(newValues);
         }	else if (value instanceof Document) {
         	clearVals();
         	valueType = XML;
         	xmlValue = new SSBlobXML(value);
         } else if (value instanceof Attachment) {
         	clearVals();
         	valueType = ATTACHMENT;
         	Attachment att = (Attachment)value; 
         	att.setName(name);
         	owner.getEntry().addAttachment(att);
         	stringValue=att.getId();
         } else if (value instanceof Event) {
         	clearVals();
         	valueType = EVENT;  
        	Event e = (Event) value;
         	e.setName(name);
        	owner.getEntry().addEvent(e);
         	stringValue = e.getId();
         } else if (value instanceof User) {
        	clearVals();
        	valueType = USER;
        	user = (User)value;
         } else {
            if (valueType != SERIALIZED) clearVals();
            valueType = SERIALIZED;
            serializedValue = new SSBlobSerializable(value);
        }
        
	}
	public Object getValue() {
	    switch(getValueType()) {
    		case STRING:
    		    if (!Validator.isNull(stringValue))
    		        return stringValue;
    		    else if (description != null)
    		        return description.getText();
    		case DESCRIPTION:
    			return description;
    		case BOOLEAN:
    			return booleanValue;
    		case NUMBER:
    		    return longValue;
    		case DATE:
    		    return dateValue;
    		case SERIALIZED:
    		    return serializedValue.getValue();
    		case XML:
    			return xmlValue.getValue();
    	    case SET:
    	    	Set v = new HashSet();
    	    	for (Iterator iter=values.iterator(); iter.hasNext();) {
    	    		v.add(((CustomAttributeListElement)iter.next()).getValue());
    	    	}
    	    	return v;
       		case EVENT:
    		    return owner.getEntry().getEvent(stringValue);
    		case ATTACHMENT:
    			return owner.getEntry().getAttachment(stringValue);
 	    }
	    return null;
	}
	public Set getValueSet() {
		Object result = getValue();
		if (result instanceof Set) return (Set)result;
    	Set v = new HashSet();
    	v.add(result);
    	return v;
		
	}
	public int hashCode() {
    	return getValue().hashCode();
    }
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if ((obj == null) || (obj.getClass() != getClass()))
            return false;
        
        CustomAttribute o = (CustomAttribute) obj;
        if (getName().equals(o.getName()) && getValue().equals(o.getValue()))
            return true;
        return false;
    }	

 
}

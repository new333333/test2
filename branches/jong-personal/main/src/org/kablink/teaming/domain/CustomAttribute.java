/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
/*
 * Created on Sep 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.kablink.teaming.domain;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.module.shared.XmlUtils;
import org.kablink.teaming.survey.Survey;
import org.kablink.teaming.util.XmlFileUtil;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.util.Validator;

/**
 * @hibernate.class table="SS_CustomAttributes" dynamic-update="true" lazy="false" discriminator-value="A"
 * @hibernate.discriminator type="char" column="type"
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * 
 *  Object to represent user attributes in the database. 
 * 
 * Sets are stored in multiple entries.  Because all elements have the same name, we
 * don't set the foreign key on list elements.  
 * The name can remain the same, so user reporting can find the name of list members.
 * The owner field should be used to walk up the object hierarchy.
 */
public class CustomAttribute extends ZonedObject {
    protected String stringValue;
    protected Description description;
    protected Long longValue;
    protected Date dateValue;
    protected SSBlobSerializable serializedValue;
    protected SSClobString xmlValue;
    //frontbase doesn't like null booleans
    protected Boolean booleanValue=Boolean.FALSE;
    protected Set values;
    // these collections are loaded for quicker indexing, hibernate will not persist them
    protected Set iValues;
    protected int valueType=NONE;
    	private static final int NONE=0;
    	public static final int STRING= 1;
    	public static final int LONG= 2;
    	public static final int DATE= 3;
    	public static final int SERIALIZED= 4;
    	public static final int SET=5;
    	public static final int ORDEREDSET=13;
       	public static final int BOOLEAN=6;
       	public static final int DESCRIPTION=7;
       	public static final int XML=8;
       	public static final int EVENT=9;
       	public static final int ATTACHMENT=10;
       	public static final int COMMASEPARATEDSTRING=11;
       	public static final int SURVEY= 12;
       	public static final int PACKEDSTRING= 14;
       	public static final int ENCRYPTEDSTRING=15;
   protected String name;//set by hibernate access="field"
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
   //keep protected for hibernate want name set up
   protected CustomAttribute() {   	
   }
 
   //only accessible through entry
   protected CustomAttribute(DefinableEntity parent, String name, Object value) {
   		setName(name);
   		//do before setValue incase value needs owner
   		setOwner(parent);
   		setValue(value);
   }
   /**
    * Return the entity that this attribute is associated with.
    * @hibernate.component class="org.kablink.teaming.domain.AnyOwner"
    * @return
    */
    public AnyOwner getOwner() {
   		return owner;
   	}
   	protected void setOwner(AnyOwner owner) {
   		this.owner = owner;
   	}
 	protected void setOwner(DefinableEntity entity) {
   		owner = new AnyOwner(entity); 		
  	}   	
    /**
     * Return the name of the attribute.
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
     * @hibernate.property length="2000"
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
    private Description getDescription() {
        return this.description;
    }
    private void setDescription(Description description) {
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
     * @hibernate.property type="org.kablink.teaming.dao.util.SSBlobSerializableType"
     */
    private SSBlobSerializable getSerializedValue() {
        return this.serializedValue;
    }
    private void setSerializedValue(SSBlobSerializable value) {
        this.serializedValue = value;
    } 
    /**
     * @hibernate.property type="org.kablink.teaming.dao.util.SSClobStringType"
     */
    private SSClobString getXmlValue() {
        return this.xmlValue;
    }
    private void setXmlValue(SSClobString value) {
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
     * @hibernate.set lazy="true" inverse="true" cascade="all,delete-orphan"  batch-size="4" order-by="position"
     * @hibernate.key column="parent"
 	 * @hibernate.one-to-many class="org.kablink.teaming.domain.CustomAttributeListElement"
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
        	Event e = owner.getEntity().getEvent(stringValue);
        	if (e != null) {
        		owner.getEntity().removeEvent(e);
        		e.setName(null);
        	}
       }
       if (valueType == ATTACHMENT) {
        	Attachment a = owner.getEntity().getAttachment(stringValue);
        	if (a != null) a.setName(null);
        }
        stringValue=null;
        longValue=null;
        dateValue=null;
        //frontbase doesn't like null booleans
        booleanValue=Boolean.FALSE;
        //allways setting mutable values to null, causes unnecessary updates
        if ((description !=null) && !Validator.isNull(description.getText()))
        	description=null;
        if ((serializedValue != null) && (serializedValue.getValue() != null))
         	serializedValue = null;
        if ((xmlValue != null) && !Validator.isNull(xmlValue.getText()))
         	xmlValue = null;
         //let hibernate delete the existing objects.
        if (values != null) values.clear();
        
    }
    /**
     * The value may be any valueType.
     * @param value
     * @return
     */
    public boolean setValue(Object value) {
    	if (value == null) {
       		setValue(value, true);
    		return true;
    	}
    	if (value instanceof String[]) {
    		//convert to set than compare, order not maintained
         	Set newValues = new HashSet();
         	String[] vals = (String[]) value;
         	for (int i=0; i<vals.length; ++i) {
         		newValues.add(vals[i]);
         	}
         	value = newValues;
    	}
    	//immutable types must be set
    	if ((valueType == XML) || (valueType == SERIALIZED)) {
       		setValue(value, true);
    		return true;
    	}
      	//don't do unnecessary updates - especially for descriptions
    	if ((valueType != SET) && (valueType != ORDEREDSET)) {
    		if (value.equals(getValue())) return false;
    		setValue(value, true);
    		return true;
    	}
    	if (valueType == SET) {
    		//currently unordered, if input unorderd, do compare
    		if (!(value instanceof LinkedHashSet) &&
    				!(value instanceof SortedSet) && !(value instanceof List)) {
    	   		//set compares don't seem to care about order
    			 if (value.equals(getValue())) return false;
    		}
            setValue(value, true);
    		return true;   			
    	} 
    	
    	//currently ordered
   		if ((value instanceof LinkedHashSet) ||
				(value instanceof SortedSet) || (value instanceof List)) {
   			//make sure values are in same order
   			Set currentValues = getValues();
   			Collection newValues = (Collection)value;
   			if (currentValues.size() == newValues.size()) {
   				Iterator cI = currentValues.iterator();
   				Iterator nI = newValues.iterator();
   				while (cI.hasNext()) {
   					if (!cI.next().equals(nI.next())) {
   			         	setValue(newValues, true);
   						return true;   						
   					}
   				}
   				return false; //must be equal   				
   			}
         	setValue(value, true);
			return true;
			
		} else if (value.equals(getValue())) {
			//if currently ordered and now don't care, don't have to change anything
			return false;
		} else {
        	setValue(value, true);
			return true;   			
		}

    }

    protected void setValue(Object value, boolean allowed)  {
       	if (value == null) {
    		clearVals();
    		valueType=NONE;
    		return;    		
    	}
    	if (value instanceof String) {
            clearVals();
            valueType = STRING;
            String val = (String) value;
            // this is returning unicode-16 lengths
            if (val.length() <= 1000) {
                stringValue=val;
            } else {
                description = new Description(val);
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
            valueType = LONG;
            longValue = (Long)value;
        } else if (value instanceof Integer) {
            clearVals();
        	valueType = LONG;
        	longValue=new Long(((Integer)value).longValue());
        } else if (value instanceof Date) {
            clearVals();
            valueType = DATE;
            dateValue = (Date)value;
        } else if (allowed && (value instanceof Collection)) {
        	if (((Collection)value).isEmpty()) {
        		clearVals();
        		valueType=NONE;
        		return;
        	}
        	Map oldValues = new HashMap();
        	//convert old set into map for easier locating
        	if (values != null) {
        		for (Iterator iter=values.iterator(); iter.hasNext();) {
        			CustomAttributeListElement le = (CustomAttributeListElement)iter.next();
        			oldValues.put(le.getValue(), le);
        		}
        	}
            clearVals();
      		if ((value instanceof LinkedHashSet) ||
    				(value instanceof SortedSet) || (value instanceof List)) {
      			valueType = ORDEREDSET;
      		} else {
      			valueType = SET;
      		}
         	LinkedHashSet newValues = new LinkedHashSet();
         	int count = 0;
         	for (Iterator iter=((Collection)value).iterator(); iter.hasNext();) {
         		Object val = iter.next();
         		CustomAttributeListElement element = (CustomAttributeListElement)oldValues.get(val);
         		if (element == null) {
         			element = new CustomAttributeListElement(getName(), this, getOwner().getEntity());
         		}
         		element.setValue(val, false);
         		if (valueType == ORDEREDSET) element.setPosition(count++);
     			newValues.add(element);
     		}
         	if (values == null) values = new LinkedHashSet();
         	values.addAll(newValues);
         } else if (value instanceof CommaSeparatedValue) {
        	 //store as a string
        	 setValue(value.toString());
        	 valueType = COMMASEPARATEDSTRING;
        	 
         } else if (value instanceof PackedValue) {
        	 //store as a string
        	 setValue(value.toString());
        	 valueType = PACKEDSTRING;
        	 
         }	else if (value instanceof Document) {
         	clearVals();
         	valueType = XML;
         	try {
         		xmlValue = new SSClobString(XmlFileUtil.writeString((Document)value, OutputFormat.createPrettyPrint()));
         	} catch (Exception ex) {
      			throw new IllegalArgumentException(ex.getLocalizedMessage());
         	}	           		
          } else if (value instanceof Attachment) {
         	clearVals();
         	valueType = ATTACHMENT;
         	Attachment att = (Attachment)value; 
         	att.setName(name);
         	owner.getEntity().addAttachment(att);
         	stringValue=att.getId();
         } else if (value instanceof Event) {
         	clearVals();
         	valueType = EVENT;  
        	Event e = (Event) value;
         	e.setName(name);
        	owner.getEntity().addEvent(e);
         	stringValue = e.getId();
         } else if (value instanceof Survey) {
        	 setValue(value.toString());
            valueType = SURVEY;
         } else if (value instanceof EncryptedValue) {
        	 setValue(value.toString());
			 valueType = ENCRYPTEDSTRING;
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
    		    return "";
    		case DESCRIPTION:
    			return description;
       		case COMMASEPARATEDSTRING:
       			CommaSeparatedValue v1 = new CommaSeparatedValue();
    		    if (!Validator.isNull(stringValue))
    		        v1.setValue(stringValue);
    		    else if (description != null)
    		        v1.setValue(description.getText());
    		    return v1;
    			
       		case PACKEDSTRING:
       			PackedValue v2 = new PackedValue();
    		    if (!Validator.isNull(stringValue))
    		        v2.setValue(stringValue);
    		    return v2;
    			
    		case BOOLEAN:
    			return booleanValue;
    		case LONG:
    		    return longValue;
    		case DATE:
    		    return dateValue;
    		case SERIALIZED:
    		    return serializedValue.getValue();
    		case XML:
    	    	try {
    	    		return XmlFileUtil.generateXMLFromString(xmlValue.getText());
    	    	} catch (Exception ex) {
           			throw new IllegalArgumentException(ex.getLocalizedMessage());
    	    	}
    	    case ORDEREDSET:
    	    	Set v = new LinkedHashSet();
    	    	if (iValues == null) {//probably not in order if bulk loaded, but not a problem for indexing
    	    		for (Iterator iter=values.iterator(); iter.hasNext();) {
    	    			v.add(((CustomAttributeListElement)iter.next()).getValue());
    	    		}
    	    	} else {
    	    		for (Iterator iter=iValues.iterator(); iter.hasNext();) {
    	    			v.add(((CustomAttributeListElement)iter.next()).getValue());
    	    		}
    	    	}
    	    	return v;
    	    case SET:   	    	
    	    	Set s = new TreeSet();  // order naturally
    	    	if (iValues == null) {
    	    		for (Iterator iter=values.iterator(); iter.hasNext();) {
    	    			s.add(((CustomAttributeListElement)iter.next()).getValue());
    	    		}
    	    	} else {
    	    		for (Iterator iter=iValues.iterator(); iter.hasNext();) {
    	    			s.add(((CustomAttributeListElement)iter.next()).getValue());
    	    		}
    	    	}
    	    	return s;
       		case EVENT:
    		    return owner.getEntity().getEvent(stringValue);
       		case SURVEY:
	       		if (!Validator.isNull(stringValue))
	 		        return new Survey(stringValue);
	 		    else if (description != null)
	 		        return new Survey(description.getText());
	 		    return null;
    		case ATTACHMENT:
    			return owner.getEntity().getAttachment(stringValue);
        	case ENCRYPTEDSTRING:
				EncryptedValue ev = new EncryptedValue();
				ev.setEncryptedValue(stringValue);
				return ev.getValue();

 	    }
	    return null;
	}
	public Object getRawValue(int descriptionFormat) {
	    switch(getValueType()) {
    		case STRING:
            case DESCRIPTION:
    		    if (!Validator.isNull(stringValue))
    		        return stringValue;
    		    else if (description != null)
                    if (descriptionFormat==Description.FORMAT_NONE) {
                        return description.getStrippedText();
                    } else if (descriptionFormat==Description.FORMAT_HTML) {
                        return description.getHtmlText();
                    } else {
                        return description.getText();
                    }
    		    return null;
       		case COMMASEPARATEDSTRING:
       			CommaSeparatedValue v1 = new CommaSeparatedValue();
    		    if (!Validator.isNull(stringValue))
    		        v1.setValue(stringValue);
    		    else if (description != null)
    		        v1.setValue(description.getText());
    		    return v1.getValueSet();
       		case PACKEDSTRING:
       			PackedValue v2 = new PackedValue();
    		    if (!Validator.isNull(stringValue))
    		        v2.setValue(stringValue);
    		    return v2.getValueSet();
    		case BOOLEAN:
    			return booleanValue;
    		case LONG:
    		    return longValue;
    		case DATE:
    		    return dateValue;
    		case SERIALIZED:
    		    return serializedValue.getValue();
    		case XML:
    	    	try {
    	    		return xmlValue.getText();
                } catch (Exception ex) {
           			throw new IllegalArgumentException(ex.getLocalizedMessage());
    	    	}
    	    case ORDEREDSET:
    	    	Set v = new LinkedHashSet();
    	    	if (iValues == null) {//probably not in order if bulk loaded, but not a problem for indexing
    	    		for (Iterator iter=values.iterator(); iter.hasNext();) {
    	    			v.add(((CustomAttributeListElement)iter.next()).getRawValue(descriptionFormat));
    	    		}
    	    	} else {
    	    		for (Iterator iter=iValues.iterator(); iter.hasNext();) {
    	    			v.add(((CustomAttributeListElement)iter.next()).getRawValue(descriptionFormat));
    	    		}
    	    	}
    	    	return v;
    	    case SET:
    	    	Set s = new TreeSet();  // order naturally
    	    	if (iValues == null) {
    	    		for (Iterator iter=values.iterator(); iter.hasNext();) {
    	    			s.add(((CustomAttributeListElement)iter.next()).getRawValue(descriptionFormat));
    	    		}
    	    	} else {
    	    		for (Iterator iter=iValues.iterator(); iter.hasNext();) {
    	    			s.add(((CustomAttributeListElement)iter.next()).getRawValue(descriptionFormat));
    	    		}
    	    	}
    	    	return s;
       		case EVENT:
    		    return stringValue;
       		case SURVEY:
	       		if (!Validator.isNull(stringValue))
	 		        return stringValue;
	 		    else if (description != null)
	 		        return description.getText();
	 		    return null;
    		case ATTACHMENT:
    			return stringValue;
        	case ENCRYPTEDSTRING:
				EncryptedValue ev = new EncryptedValue();
				ev.setEncryptedValue(stringValue);
				return ev.getValue();

 	    }
	    return null;
	}
	/**
	 * Return the value as a set.
	 * @return
	 */
	public Set getValueSet() {
		Object result = getValue();
		if (result instanceof Set) return (Set)result;
		if (result instanceof CommaSeparatedValue) 
			return ((CommaSeparatedValue)(result)).getValueSet();
		if (result instanceof PackedValue) 
			return ((PackedValue)(result)).getValueSet();
    	Set v = new LinkedHashSet();
    	if (result != null) v.add(result);
    	return v;
		
	}
	public int hashCode() {
		if (getValue() == null) return 0;
    	return getValue().hashCode();
    }
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if ((obj == null) || (obj.getClass() != getClass()))
            return false;
        
        CustomAttribute o = (CustomAttribute) obj;
        
        if (getName().equals(o.getName())) {
        	if(getValue() == null) {
        		if(o.getValue() == null)
        			return true;
        	}
        	else {
        		if(getValue().equals(o.getValue()))
        			return true;
        	}
            return true;
        }

        return false;
    }	
    /**
     * This method is used for performance optimization during indexing.
     * The values of each collection are loaded and built by hand.  
     * They are not persisted.  This allows us to load greater than the 
     * hibernate "batch-size" number of collections at once.
     */
    public void addIndexValue(CustomAttributeListElement value) {
    	if (iValues == null) iValues = new HashSet();
    	iValues.add(value);
    }
    public Element addChangeLog(Element parent) {
		Element element = null;
	    if (getValueType() == SET ) {
  			element = parent.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_ATTRIBUTE_SET);
  			element.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_NAME, getName());
  			//don't use ivalues = not ordered correctly
    		for (Iterator iter=values.iterator(); iter.hasNext();) {
	    			((CustomAttributeListElement)iter.next()).addChangeLog(element);
    		}
 	    } else {
	    	switch(getValueType()) {
       			case STRING:
       				if (!Validator.isNull(stringValue))
       					element =  XmlUtils.addCustomAttribute(parent, getName(), ObjectKeys.XTAG_TYPE_STRING, stringValue);
       				else if (description != null)
       					element =  XmlUtils.addCustomAttribute(parent, getName(), ObjectKeys.XTAG_TYPE_STRING, description.getText());
       				break;
       			case DESCRIPTION:
       				element =  XmlUtils.addCustomAttribute(parent, getName(), ObjectKeys.XTAG_TYPE_DESCRIPTION, description.getText());
      				break;
       			case COMMASEPARATEDSTRING:
       				if (!Validator.isNull(stringValue))
       					element =  XmlUtils.addCustomAttribute(parent, getName(), ObjectKeys.XTAG_TYPE_COMMASEPARATED, stringValue);
       				else if (description != null)
       					element =  XmlUtils.addCustomAttribute(parent, getName(), ObjectKeys.XTAG_TYPE_COMMASEPARATED, description.getText());
      				break;
       			case BOOLEAN:		
       				element =  XmlUtils.addCustomAttribute(parent, getName(), ObjectKeys.XTAG_TYPE_BOOLEAN, booleanValue.toString());    	
      				break;
       			case LONG:
       				element =  XmlUtils.addCustomAttribute(parent, getName(), ObjectKeys.XTAG_TYPE_LONG, longValue.toString());  
      				break;
       			case DATE:
       				element =  XmlUtils.addCustomAttribute(parent, getName(), ObjectKeys.XTAG_TYPE_DATE, dateValue.toString());  
      				break;
       			case SERIALIZED:
       				element =  XmlUtils.addCustomAttribute(parent, getName(), ObjectKeys.XTAG_TYPE_SERIALIZED, serializedValue.toBase64String());
      				break;
        		case XML:
        			element =  XmlUtils.addCustomAttribute(parent, getName(), ObjectKeys.XTAG_TYPE_XML, xmlValue.getText()); 
      				break;
       			case EVENT:
         			Event event = owner.getEntity().getEvent(stringValue);
        			if (event != null) element = event.addChangeLog(parent);
      				break;
       			case SURVEY:
       				if (!Validator.isNull(stringValue))
       					element =  XmlUtils.addCustomAttribute(parent, getName(), ObjectKeys.XTAG_TYPE_STRING, stringValue);
       				else if (description != null)
       					element =  XmlUtils.addCustomAttribute(parent, getName(), ObjectKeys.XTAG_TYPE_STRING, description.getText());
       				break;     				
       			case ATTACHMENT:
       				//attachments are logged separetly
       				element = XmlUtils.addCustomAttribute(parent, getName(), ObjectKeys.XTAG_TYPE_FILE, stringValue);
      				break;
      	    }
    	}
    

	    return element;
    }
    public void toXml(Element parent) {
    	//TODO: need to do something about attachments
    	addChangeLog(parent);
    }

    public boolean multivalued() {
        return getValueType()==ORDEREDSET || getValueType()==SET;
    }
    
    public String toString() {
    	try {
	    	//Get the definition being used
	    	DefinableEntity entity = owner.getEntity();
	    	Document defDoc = entity.getEntryDefDoc();
	    	Element attrDefEle = DefinitionHelper.findAttribute(name, defDoc);
	    	String attrType = attrDefEle.attributeValue("name");
		    if (getValueType() == SET ) {
		    	StringBuffer result = new StringBuffer();
		    	boolean firstItem = true;
		    	DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	    		for (Iterator iter=values.iterator(); iter.hasNext();) {
	    			if (!firstItem) {
	    				result.append(", ");
	    			}
	    			firstItem = false;
		    		Object value = iter.next();
		    		if (value instanceof String) {
			    		switch (attrType) {
		    				case "user_list":
		    				case "external_user_list":
		    				case "group_list":
		    					Long id = Long.valueOf((String)value);
			    				break;
			    			default:
			    				result.append((String)value);
			    				break;
			    		}
		    			
		    		} else if (value instanceof Boolean) {
		    			result.append(String.valueOf((Boolean)value));
		    		} else if (value instanceof Long) {
		    			result.append(String.valueOf((Long)value));
		    		} else if (value instanceof Date) {
		    			result.append(dateFormatter.format((Date)value));
		    		} else if (value instanceof Event) {
		    			result.append(((Event)value).toString().replaceAll("'", ";"));
		    		}
	      	    }
	    		return result.toString();
	 	    } else {
		    	switch(getValueType()) {
	       			case STRING:
	       				if (!Validator.isNull(stringValue))
	       					return stringValue;
	       				else if (description != null)
	       					return description.getText();
	       				break;
	       			case DESCRIPTION:
	       				return description.getText();
	       			case COMMASEPARATEDSTRING:
	       				if (!Validator.isNull(stringValue))
	       					return stringValue;
	       				else if (description != null)
	       					return description.getText();
	       				break;
	       			case BOOLEAN:		
	       				return booleanValue.toString();    	
	       			case LONG:
	       				return longValue.toString();  
	       			case DATE:
	       				return dateValue.toString();  
	       			case SERIALIZED:
	       				return serializedValue.toBase64String();
	        		case XML:
	        			return xmlValue.getText(); 
	       			case EVENT:
	       				Event e = (Event)getValue();
	       				return e.toCsvString();
	       			case SURVEY:
	       				if (!Validator.isNull(stringValue))
	       					return stringValue;
	       				else if (description != null)
	       					return description.getText();
	       				break;     				
	       			case ATTACHMENT:
	       				//attachments aren't handled
	      				break;
	      	    }
	    	}
    	} catch(Exception e) {}
	    return "";
    }
 
}

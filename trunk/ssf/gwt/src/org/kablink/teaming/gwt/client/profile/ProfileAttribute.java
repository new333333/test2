package org.kablink.teaming.gwt.client.profile;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ProfileAttribute implements IsSerializable {

	public static final int NONE=0;
	public static final int STRING= 1;
	public static final int LONG= 2;
	public static final int DATE= 3;
	public static final int SERIALIZED= 4;
	public static final int LIST=5;
   	public static final int BOOLEAN=6;
   	public static final int DESCRIPTION=7;
   	public static final int XML=8;
   	public static final int EVENT=9;
   	public static final int ATTACHMENT=10;
   	public static final int COMMASEPARATEDSTRING=11;
   	public static final int SURVEY= 12;
   	public static final int PACKEDSTRING= 14;

	private String title;
	 /**
	  * ArrayList that will always contain profileAttributeList 
	  * @gwt.typeArgs <org.kablink.teaming.gwt.client.profile.ProfileAttributeListElement>
	  */
	private ArrayList<ProfileAttributeListElement> listEleValues;
	private String name;
	private String dataName;
	private String stringValue;
	private Long longValue;
	private Date dateValue;
	private Boolean booleanValue = Boolean.FALSE;
	private int valueType = NONE;
	private String displayType;
	private ProfileAttributeAttachment attachment;
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public ProfileAttribute() {
	}

	public int getValueType() {
		return this.valueType;
	}

	protected void setValueType(int type) {
		this.valueType = type;
	}
	
	private void clearVals() {
		stringValue=null;
		longValue=null;
		dateValue=null;
		booleanValue=Boolean.FALSE;
		if (listEleValues != null) listEleValues.clear();
    }

	public String getTitle() {
		return title;
	}

	public void setTitle(String t) {
		this.title = t;
	}

	public String getName() {
		return name;
	}

	public void setName(String n) {
		this.name = n;
	}
	
	public String getDataName() {
		return dataName;
	}

	public void setDataName(String dn) {
		this.dataName = dn;
	}
	
	public void setValue(Object value) {
		if (value == null) {
       		setValue(value, true);
    		return;
    	}
		
		if (value instanceof String) {
        	setValue(value, true);
		} else if (value instanceof String[]) {
    		//convert to set than compare, order not maintained
         	ArrayList<String> newValues = new ArrayList<String>();
         	String[] vals = (String[]) value;
         	for (int i=0; i<vals.length; ++i) {
         		newValues.add(vals[i]);
         	}
        	setValue(value, true);
 		} else if (value instanceof ArrayList) {
         	setValue(value, true);
		} else {
        	setValue(value, true);
		}
	}
	
	public void setValue(Object value, boolean allowed)  {
		if (value == null) {
    		clearVals();
    		valueType=NONE;
    		return;    		
    	}
    	if (value instanceof String) {
    		clearVals();
    		valueType = STRING;
            stringValue=(String)value;
    	} else if (value instanceof Boolean) {
    		clearVals();
    		valueType = BOOLEAN;
			booleanValue = (Boolean)value;			
        } else if (value instanceof Long) {
            valueType = LONG;
            longValue = (Long)value;
        } else if (value instanceof Integer) {
        	valueType = LONG;
        	longValue=new Long(((Integer)value).longValue());
        } else if (value instanceof Date) {
            valueType = DATE;
            dateValue = (Date)value;
        } else if (allowed && (value instanceof ArrayList)) {
        	valueType = LIST;
         	listEleValues = (ArrayList<ProfileAttributeListElement>) value;
         }	else if (value instanceof ProfileAttributeAttachment) {
         	valueType = ATTACHMENT;
         	ProfileAttributeAttachment att = (ProfileAttributeAttachment)value; 
         	att.setName(name);
         	stringValue=att.getId();
         	attachment = att;
         }  else {
        	 valueType = STRING;
             stringValue= value.toString();
         }
	}
		
	public Object getValue() {
	    switch(getValueType()) {
    		case STRING:
    		    if (stringValue != null)
    		        return stringValue;
    		    return "";
    		case BOOLEAN:
    			return booleanValue;
    		case LONG:
    		    return longValue;
    		case DATE:
    		    return dateValue;
    		case LIST:
		    	return listEleValues;
			case ATTACHMENT:
				return attachment;
	    }
	    return null;
	}

	public void setDisplayType(String t) {
		this.displayType = t;
	}
	
	public String getDisplayType(){
		return this.displayType;
	}
}

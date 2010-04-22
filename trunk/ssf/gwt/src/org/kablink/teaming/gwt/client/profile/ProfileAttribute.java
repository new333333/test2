package org.kablink.teaming.gwt.client.profile;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ProfileAttribute implements IsSerializable {

	public String title;
	public String value;
	public String name;

	private String stringValue;
	private Long longValue;
	private Date dateValue;
	private Boolean booleanValue = Boolean.FALSE;

	private int valueType = NONE;
	private static final int NONE = 0;

	public static final int STRING = 1;
	public static final int LONG = 2;
	public static final int DATE = 3;
	public static final int BOOLEAN = 6;
	public static final int DESCRIPTION = 7;
	public static final int COMMASEPARATEDSTRING = 11;
	public static final int PACKEDSTRING = 14;

	protected String id;
	private String displayType;
	
	public ProfileAttribute() {

	}

	public int getValueType() {
		return this.valueType;
	}

	protected void setValueType(int type) {
		this.valueType = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setValue(Object value, boolean allowed)  {
       	if (value == null) {
    		valueType=NONE;
    		return;    		
    	}
    	if (value instanceof String) {
            valueType = STRING;
            stringValue=(String)value;
    	} else if (value instanceof Boolean) {
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
 	    }
	    return null;
	}

	public void setDisplayType(String type) {
		this.displayType = type;
	}
	
	public String getDisplayType(){
		return this.displayType;
	}
}

/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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

package org.kablink.teaming.gwt.client.profile;

import java.util.ArrayList;
import java.util.Date;

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Nathan Jensen
 */
public class ProfileAttribute implements IsSerializable, VibeRpcResponseData {

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
	
	@SuppressWarnings("unchecked")
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

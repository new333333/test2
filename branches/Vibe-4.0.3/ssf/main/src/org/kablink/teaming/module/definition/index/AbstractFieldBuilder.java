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
package org.kablink.teaming.module.definition.index;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Field;
import org.dom4j.Element;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.util.InvokeUtil;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ObjectPropertyNotFoundException;

public abstract class AbstractFieldBuilder implements FieldBuilder {
	// IMPORTANT: 9/15/2010 - For efficiency reason, we want multi-threaded accesses to
	// shared builder instances. Therefore, it is imperative that we do NOT store ANY
	// state information in the instance variable.
    
	protected boolean isFieldsOnly(Map args) {
		Boolean fieldsOnly = (Boolean)args.get(DefinitionModule.INDEX_FIELDS_ONLY);
        if (fieldsOnly == null) 
        	fieldsOnly = Boolean.FALSE; // default
        return fieldsOnly.booleanValue();
	}

    public Field[] buildField(DefinableEntity entity, String dataElemName, Map args) {
        Set dataElemValue = getEntryElementValue(entity, dataElemName);
        
        if(dataElemValue != null)
            return build(dataElemName, dataElemValue, args);
        else
            return null;
    }

    protected Set getEntryElementValue(DefinableEntity entity, String dataElemName) {
	    Object dataElemValue = null;
	    Set result = null;
	    try {
	        dataElemValue = InvokeUtil.invokeGetter(entity, dataElemName);
	    }
	    catch (ObjectPropertyNotFoundException pe) {
	        CustomAttribute cAttr = entity.getCustomAttribute(dataElemName);
	        if(cAttr != null)
	        	//let customAttribute do set conversion to handle comman separated values
	            result = cAttr.getValueSet();
		}
	    
	    
	    if ((result == null) && (dataElemValue != null)) {
	        if(dataElemValue instanceof Set) {
	            result = (Set) dataElemValue;
	        }
	        else {
		        result = new HashSet();
		        result.add(dataElemValue);	            
	        }
	    }
	    
	    return result;
    }
    
    protected Set getEntryElementValueCaptions(DefinableEntity entity, String dataElemName, Element entryElement) {
	    Object dataElemValue = null;
	    Set elemValues = null;
	    Set result = new HashSet();
	    try {
	        dataElemValue = InvokeUtil.invokeGetter(entity, dataElemName);
	    }
	    catch (ObjectPropertyNotFoundException pe) {
	        CustomAttribute cAttr = entity.getCustomAttribute(dataElemName);
	        if(cAttr != null)
	        	//let customAttribute do set conversion to handle comman separated values
	        	elemValues = cAttr.getValueSet();
		}
	    
	    
	    if ((elemValues == null) && (dataElemValue != null)) {
	        if(dataElemValue instanceof Set) {
	        	elemValues = (Set) dataElemValue;
	        }
	        else {
	        	elemValues = new HashSet();
	        	elemValues.add(dataElemValue);	            
	        }
	    }
	    //Get the caption for each value
	    if (elemValues != null) {
	    	Iterator itElemValues = elemValues.iterator();
		    while (itElemValues.hasNext()) {
		    	Object obj = itElemValues.next();
		    	if(obj != null && obj instanceof String) {
			    	String valueName = (String)obj;
			    	Element nameEle = (Element)entryElement.selectSingleNode("./item/properties/property[@name='name' and @value='"+valueName+"']");
			    	if (nameEle != null) {
			    		Element captionEle = (Element)nameEle.getParent().selectSingleNode("./property[@name='caption']");
				    	if (captionEle != null) result.add(captionEle.attributeValue("value", ""));
			    	}
		    	}
		    }
	    }
	    
	    return result;
    }
    
    public String getEntryElementCaption(DefinableEntity entity, String dataElemName, Element entryElement) {
    	Element captionEle = (Element)entryElement.selectSingleNode("properties/property[@name='caption']");
    	if (captionEle != null) {
    		return captionEle.attributeValue("value", "");
    	} else {
    		return "";
    	}
    }
    
    public String getNltTagInAllLanguages(String tag) {
		if (tag != null && tag.startsWith("__")) {
			Set<Locale> locales = NLT.getLocales();
			String result = "";
			Iterator itLocales = locales.iterator();
			while (itLocales.hasNext()) {
				Locale lang = (Locale)itLocales.next();
				if (!lang.equals("")) {
					result += " " + NLT.get(tag, "", lang);
				}
			}
			return result;
		}
		return tag;
    }
    
    /*
    protected String makeFieldName(String dataElemName, String indexingType) {
        return indexingType + dataElemName;
    }*/
    
    protected abstract Field[] build(String dataElemName, Set dataElemValue, Map args);
    
    protected Object getFirstElement(Set set) {
        if(set.size() == 0)
            return null;
    
        return set.iterator().next();
    }
}

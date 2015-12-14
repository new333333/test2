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
package com.sitescape.team.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.dom4j.Document;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.web.util.DefinitionHelper;


public class Statistics implements Serializable {

	private static final long serialVersionUID = -8410750034091029845L;

	public static final String ATTRIBUTE_NAME = "statistics";
	
	public static final String TOTAL_KEY = "total";
	
	public static final String ATTRIBUTE_CAPTION = "attributeCaption";
	
	public static final String CAPTIONS = "captions";
	
	public static final String VALUES = "values";
	
	public static final String VALUES_LIST = "values_list";

	private Map value = new HashMap();

	public Map getValue() {
		return value;
	}

	public void setValue(Map value) {
		this.value = value;
	}

	public void addStatistics(String entryDefinitionId, Document entryDefinitionDoc, Map<String, CustomAttribute> customAttributes) {
		Iterator<Map.Entry<String, CustomAttribute>> it= customAttributes.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, CustomAttribute> attr = it.next();
			addStatistic(entryDefinitionId, entryDefinitionDoc, attr.getValue());
		}
	}
	
	public void deleteStatistics(String entryDefinitionId, Document entryDefinitionDoc, Map<String, CustomAttribute> customAttributes) {
		Iterator<Map.Entry<String, CustomAttribute>> it= customAttributes.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, CustomAttribute> attr = it.next();
			deleteStatistic(entryDefinitionId, entryDefinitionDoc, attr.getValue());
		}
	}

	private void addStatistic(String entryDefinitionId, Document entryDefinitionDoc, CustomAttribute attribute) {
		if (value.get(entryDefinitionId) == null) {
			Map entryDefinitionIdStats = new HashMap();
			value.put(entryDefinitionId, entryDefinitionIdStats);
		}
		Map entryDefinitionIdStats = (Map)value.get(entryDefinitionId);
		
		String attributeType = DefinitionHelper.findAttributeType(attribute.getName(), entryDefinitionDoc);
		if (!isStatisticable(attributeType, attribute)) {
			return;
		}
		
		if (entryDefinitionIdStats.get(attribute.getName()) == null) {
			
			String attributeCaption = DefinitionHelper.findCaptionForAttribute(attribute.getName(), entryDefinitionDoc);
			List attributesValueCaption = Collections.EMPTY_LIST;
			if ("selectbox".equals(attributeType)) {
				attributesValueCaption = DefinitionHelper.findSelectboxSelections(attribute.getName(), entryDefinitionDoc);
			} else if ("radio".equals(attributeType)) {
				attributesValueCaption = DefinitionHelper.findRadioSelections(attribute.getName(), entryDefinitionDoc);
			}
			
			List attributeValues = new ArrayList();
			Map attributeAllowedValues = new HashMap();
			Map attributeCaptions = new HashMap();
			Iterator attributeValuesIt = attributesValueCaption.iterator();
			while (attributeValuesIt.hasNext()) {
				
				String name = null;
				String caption = null;
				
				Iterator attributeValueIt = ((Map)attributeValuesIt.next()).entrySet().iterator();
				while (attributeValueIt.hasNext()) {
					Map.Entry mapEntry = (Map.Entry)attributeValueIt.next();
					if ("name".equals(mapEntry.getKey())) {
						name = (String)mapEntry.getValue();
					} else if ("caption".equals(mapEntry.getKey())) {
						caption = (String)mapEntry.getValue();
					}
				}
				attributeValues.add(name);
				attributeAllowedValues.put(name, 0);
				attributeCaptions.put(name, caption);
			}
			
			Map attributeStats = new HashMap();
			attributeStats.put(TOTAL_KEY, 0);
			if (attributeCaption != null) {
				attributeStats.put(ATTRIBUTE_CAPTION, attributeCaption);
			}
			attributeStats.put(CAPTIONS, attributeCaptions);
			attributeStats.put(VALUES, attributeAllowedValues);
			attributeStats.put(VALUES_LIST, attributeValues);
			entryDefinitionIdStats.put(attribute.getName(), attributeStats);
		}
		Map attributeStats = (Map)entryDefinitionIdStats.get(attribute.getName());
		
		if (attributeStats.get(TOTAL_KEY) == null) {
			attributeStats.put(TOTAL_KEY, 1);
		} else {
			Integer total = (Integer)attributeStats.get(TOTAL_KEY);
			total++;
			attributeStats.put(TOTAL_KEY, total);
		}
		
		if (attributeStats.get(VALUES) == null) {
			attributeStats.put(VALUES, new HashMap());
		}
		if (attributeStats.get(VALUES_LIST) == null) {
			attributeStats.put(VALUES_LIST, new ArrayList());
		}		
		Map attributeValueStats = (Map)attributeStats.get(VALUES);
		List attributeValues = (List)attributeStats.get(VALUES_LIST);
		
		if (isSimpleValueAttribute(attribute)) {
			if (attribute.getValue() == null || "".equals(attribute.getValue()) || 
					FileAttachment.class.isAssignableFrom(attribute.getValue().getClass()) ||
					Event.class.isAssignableFrom(attribute.getValue().getClass())) {
				return;
			}
			
			if (!attributeValues.contains(attribute.getValue())) {
				attributeValues.add(attribute.getValue());
			}
			
			if (attributeValueStats.get(attribute.getValue()) == null) {
				attributeValueStats.put(attribute.getValue(), 1);
			} else {
				Integer occures = (Integer)attributeValueStats.get(attribute.getValue());
				occures++;
				if (occures < 0) {
					occures = 0;
				}
				attributeValueStats.put(attribute.getValue(), occures);
			}
		} else if (isSetValueAttribute(attribute)) {
			Iterator valuesIt = attribute.getValueSet().iterator();
			while (valuesIt.hasNext()) {
				Object attrValue = valuesIt.next();
				if (attrValue == null || "".equals(attrValue) || 
						FileAttachment.class.isAssignableFrom(attrValue.getClass()) ||
						Event.class.isAssignableFrom(attribute.getValue().getClass())) {
					continue;
				}
				if (!attributeValues.contains(attrValue)) {
					attributeValues.add(attrValue);
				}
				if (attributeValueStats.get(attrValue) == null) {
					attributeValueStats.put(attrValue, 1);
				} else {
					Integer occures = (Integer)attributeValueStats.get(attrValue);
					occures++;
					attributeValueStats.put(attrValue, occures);
				}
			}
		}
	}
	
	private void deleteStatistic(String entryDefinitionId, Document entryDefinitionDoc, CustomAttribute attribute) {
		if (value.get(entryDefinitionId) == null) {
			return;
		}
		
		String attributeType = DefinitionHelper.findAttributeType(attribute.getName(), entryDefinitionDoc);
		if (!isStatisticable(attributeType, attribute)) {
			return;
		}

		Map entryDefinitionIdStats = (Map)value.get(entryDefinitionId);
		if (entryDefinitionIdStats.get(attribute.getName()) == null) {
			return;
		}
		Map attributeStats = (Map)entryDefinitionIdStats.get(attribute.getName());
		
		if (attributeStats.get(TOTAL_KEY) != null) {
			Integer total = (Integer)attributeStats.get(TOTAL_KEY);
			total--;
			if (total < 0) {
				total = 0;
			}
			attributeStats.put(TOTAL_KEY, total);
		}
		
		if (attributeStats.get(VALUES) == null) {
			attributeStats.put(VALUES, new HashMap());
		}
		Map attributeValueStats = (Map)attributeStats.get(VALUES);
		
		if (isSimpleValueAttribute(attribute)) {
			if (attribute.getValue() == null || "".equals(attribute.getValue()) || 
					FileAttachment.class.isAssignableFrom(attribute.getValue().getClass())) {
				return;
			}
			if (attributeValueStats.get(attribute.getValue()) != null) {
				Integer occures = (Integer)attributeValueStats.get(attribute.getValue());
				occures--;
				if (occures < 0) {
					occures = 0;
				}
				attributeValueStats.put(attribute.getValue(), occures);
			}
		} else if (isSetValueAttribute(attribute)) {
			Iterator valuesIt = attribute.getValueSet().iterator();
			while (valuesIt.hasNext()) {
				Object attrValue = valuesIt.next();
				if (attrValue == null || "".equals(attrValue) || 
						FileAttachment.class.isAssignableFrom(attrValue.getClass())) {
					continue;
				}
				if (attributeValueStats.get(attrValue) != null) {
					Integer occures = (Integer)attributeValueStats.get(attrValue);
					occures--;
					if (occures < 0) {
						occures = 0;
					}
					attributeValueStats.put(attrValue, occures);
				}
			}
		}
	}
	
	private boolean isStatisticable(String attributeType, CustomAttribute attribute) {
		return (attribute.getValueType() == CustomAttribute.BOOLEAN ||
				attribute.getValueType() == CustomAttribute.LONG ||
				attribute.getValueType() == CustomAttribute.STRING ||
				attribute.getValueType() == CustomAttribute.COMMASEPARATEDSTRING ||
				attribute.getValueType() == CustomAttribute.PACKEDSTRING ||
				attribute.getValueType() == CustomAttribute.ORDEREDSET ||
				attribute.getValueType() == CustomAttribute.SET) && 
				!("file".equals(attributeType) || "graphic".equals(attributeType) ||
						"profileEntryPicture".equals(attributeType) ||
						"attachFiles".equals(attributeType) ||
						"event".equals(attributeType));
	}
	
	private boolean isSimpleValueAttribute(CustomAttribute attribute) {
		return (attribute.getValueType() == CustomAttribute.BOOLEAN ||
				attribute.getValueType() == CustomAttribute.LONG ||
				attribute.getValueType() == CustomAttribute.STRING);
	}
	
	private boolean isSetValueAttribute(CustomAttribute attribute) {
		return (attribute.getValueType() == CustomAttribute.COMMASEPARATEDSTRING ||
				attribute.getValueType() == CustomAttribute.PACKEDSTRING ||
				attribute.getValueType() == CustomAttribute.ORDEREDSET ||
				attribute.getValueType() == CustomAttribute.SET);
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("value", value).toString();
	}
	
    public boolean equals(Object obj) {
    	//used for custom attribute value comparision
    	if ((obj == null) || !(obj instanceof Statistics))
            return false;
        
        Statistics o = (Statistics) obj;
        if (getValue().equals(o.getValue()))
            return true;
        return false;
    }	
	
}

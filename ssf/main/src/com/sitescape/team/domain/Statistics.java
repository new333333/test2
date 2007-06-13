package com.sitescape.team.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.dom4j.Document;

import com.sitescape.team.web.util.DefinitionHelper;

public class Statistics implements Serializable {

	private static final long serialVersionUID = -8410750034091029845L;

	public static final String ATTRIBUTE_NAME = "statistics";
	
	private static final String TOTAL_KEY = "total";
	
	private static final String ATTRIBUTE_CAPTION = "attributeCaption";
	
	private static final String CAPTIONS = "captions";
	
	private static final String VALUES = "values";

	private Map value = new HashMap();

	public Map getValue() {
		return value;
	}

	public void setValue(Map value) {
		this.value = value;
	}

	public void addStatistics(Definition entryDefinition, Map<String, CustomAttribute> customAttributes) {
		Iterator<Map.Entry<String, CustomAttribute>> it= customAttributes.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, CustomAttribute> attr = it.next();
			addStatistic(entryDefinition, attr.getValue());
		}
	}
	
	public void updateStatistics(Definition entryDefinition, Map<String, CustomAttribute> customAttributesOld, Map<String, CustomAttribute> customAttributes) {
		deleteStatistics(entryDefinition, customAttributesOld);
		addStatistics(entryDefinition, customAttributes);
	}

	public void deleteStatistics(Definition entryDefinition, Map<String, CustomAttribute> customAttributes) {
		Iterator<Map.Entry<String, CustomAttribute>> it= customAttributes.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, CustomAttribute> attr = it.next();
			deleteStatistic(entryDefinition, attr.getValue());
		}
	}

	private void addStatistic(Definition entryDefinition, CustomAttribute attribute) {
		String  entryDefinitionId = entryDefinition.getId();
	
		
		if (value.get(entryDefinitionId) == null) {
			Map entryDefinitionIdStats = new HashMap();
			value.put(entryDefinitionId, entryDefinitionIdStats);
		}
		Map entryDefinitionIdStats = (Map)value.get(entryDefinitionId);
		
		if (!isStatisticable(attribute)) {
			return;
		}
		
		if (entryDefinitionIdStats.get(attribute.getName()) == null) {
			
			String attributeCaption = DefinitionHelper.findCaptionForAttribute(attribute.getName(), entryDefinition.getDefinition());
			String attributeType = DefinitionHelper.findAttributeType(attribute.getName(), entryDefinition.getDefinition());
			List attributesValueCaption = Collections.EMPTY_LIST;
			if ("selectbox".equals(attributeType)) {
				attributesValueCaption = DefinitionHelper.findSelectboxSelections(attribute.getName(), entryDefinition.getDefinition());
			} else if ("radio".equals(attributeType)) {
				attributesValueCaption = DefinitionHelper.findRadioSelections(attribute.getName(), entryDefinition.getDefinition());
			}
			
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
				
				attributeAllowedValues.put(name, 0);
				attributeCaptions.put(name, caption);
			}
			
			Map attributeStats = new HashMap();
			attributeStats.put(TOTAL_KEY, 0);
			attributeStats.put(ATTRIBUTE_CAPTION, attributeCaption);
			attributeStats.put(CAPTIONS, attributeCaptions);
			attributeStats.put(VALUES, attributeAllowedValues);
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
		Map attributeValueStats = (Map)attributeStats.get(VALUES);
		
		if (isSimpleValueAttribute(attribute)) {
			if (attribute.getValue() == null || "".equals(attribute.getValue())) {
				return;
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
				if (attrValue == null || "".equals(attrValue)) {
					continue;
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
	
	private void deleteStatistic(Definition entryDefinition, CustomAttribute attribute) {
		String entryDefinitionId = entryDefinition.getId();
		if (value.get(entryDefinitionId) == null) {
			return;
		}
		
		if (!isStatisticable(attribute)) {
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
			if (attribute.getValue() == null || "".equals(attribute.getValue())) {
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
				if (attrValue == null || "".equals(attrValue)) {
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
	
	private boolean isStatisticable(CustomAttribute attribute) {
		return (attribute.getValueType() == CustomAttribute.BOOLEAN ||
				attribute.getValueType() == CustomAttribute.LONG ||
				attribute.getValueType() == CustomAttribute.STRING ||
				attribute.getValueType() == CustomAttribute.COMMASEPARATEDSTRING ||
				attribute.getValueType() == CustomAttribute.ORDEREDSET ||
				attribute.getValueType() == CustomAttribute.SET);
	}
	
	private boolean isSimpleValueAttribute(CustomAttribute attribute) {
		return (attribute.getValueType() == CustomAttribute.BOOLEAN ||
				attribute.getValueType() == CustomAttribute.LONG ||
				attribute.getValueType() == CustomAttribute.STRING);
	}
	
	private boolean isSetValueAttribute(CustomAttribute attribute) {
		return (attribute.getValueType() == CustomAttribute.COMMASEPARATEDSTRING ||
				attribute.getValueType() == CustomAttribute.ORDEREDSET ||
				attribute.getValueType() == CustomAttribute.SET);
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("value", value).toString();
	}
}

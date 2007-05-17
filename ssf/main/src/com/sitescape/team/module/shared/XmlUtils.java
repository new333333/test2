/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.module.shared;

import java.util.Date;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.dom4j.Element;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Description;
import com.sitescape.team.domain.NoDefinitionByTheIdException;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.util.Validator;
import com.sitescape.team.dao.CoreDao;

public class XmlUtils {


	public static Element addProperty(Element parent, String name, String value) {
		Element prop = parent.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_PROPERTY);
		prop.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_NAME, name);
		if (!Validator.isNull(value)) prop.addText(value);
		return prop;
	}
	//force comman date format.  This is a problem cause hibernate returns sql Timestamps which format
	//differently then java.util.date
	public static Element addProperty(Element parent, String name, Date value) {
		if (value != null) return addProperty(parent, name, value.toGMTString());
		return addProperty(parent, name, (Object)null);
	}
	public static Element addProperty(Element parent, String name, Object value) {
		Element prop = parent.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_PROPERTY);
		prop.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_NAME, name);
		if (value != null) prop.addText(value.toString());
		return prop;
	}
	public static Element addPropertyCData(Element parent, String name, Object value) {
		Element prop = parent.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_PROPERTY);
		prop.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_NAME, name);
		if (value != null) prop.addCDATA(value.toString());
		return prop;
	}
	 public static String getProperty(Element element, String name) {
		 Element variableEle = (Element)element.selectSingleNode("./property[@name='" + name + "']");
		 if (variableEle == null) return null;
		 return variableEle.getStringValue();   	
	 }

	//attributes are available through the definintion builder
	public static Element addAttribute(Element parent, String name, String type, String value) {
		if (Validator.isNull(value)) return null;
		Element prop = parent.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_ATTRIBUTE);
		prop.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_NAME, name);
		prop.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_TYPE, type);
		prop.addText(value);
		return prop;
	}
	public static Element addAttribute(Element parent, String name, String type, Collection values) {
		if ((values == null) || values.isEmpty()) return null;
		Element prop = parent.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_ATTRIBUTE);
		prop.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_NAME, name);
		prop.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_TYPE, type);
		StringBuffer buf = new StringBuffer();
		for (Iterator iter=values.iterator(); iter.hasNext();) {
			buf.append(iter.next().toString());
			buf.append(",");
		}
		prop.addText(buf.toString());
		return prop;
	}
	//attributes are available through the definintion builder
	public static Element addAttribute(Element parent, String name, String type, Object value) {
		if (value == null) return null;
		Element prop = parent.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_ATTRIBUTE);
		prop.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_NAME, name);
		prop.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_TYPE, type);
		prop.addText(value.toString());
		return prop;
	}
	//attributes are available through the definintion builder
	public static Element addAttributeCData(Element parent, String name, String type, String value) {
		if (Validator.isNull(value)) return null;
		Element prop = parent.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_ATTRIBUTE);
		prop.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_NAME, name);
		prop.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_TYPE, type);
		prop.addCDATA(value);
		return prop;
	}
	public static Element addAttributeCData(Element parent, String name, String type, Object value) {
		if (value == null) return null;
		Element prop = parent.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_ATTRIBUTE);
		prop.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_NAME, name);
		prop.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_TYPE, type);
		prop.addCDATA(value.toString());
		return prop;
	}
	 public static Object getAttribute(Element element, String name) {
		 Element variableEle = (Element)element.selectSingleNode("./" + ObjectKeys.XTAG_ELEMENT_TYPE_ATTRIBUTE + "[@name='" + name + "']");
		 if (variableEle == null) return null;
		 return getAttributeValue(variableEle);
	 }
	 public static Object getAttributeValue(Element element) {
		 String type = element.attributeValue(ObjectKeys.XTAG_ATTRIBUTE_TYPE);
		 if (type == null) type = ObjectKeys.XTAG_TYPE_STRING;
		 if (type.equals(ObjectKeys.XTAG_TYPE_STRING)) {
			 return element.getText();  		 
		 } else if (type.equals(ObjectKeys.XTAG_TYPE_DESCRIPTION)) {
			 return new Description(element.getText());
		 }
		 return null;
		 
	 }

	 public static Map getAttributes(Element config) {
		 Map updates = new HashMap();
		 List<Element> attributes = config.selectNodes("./" + ObjectKeys.XTAG_ELEMENT_TYPE_ATTRIBUTE);
		 for (Element att:attributes) {
			 String name = att.attributeValue(ObjectKeys.XTAG_ATTRIBUTE_NAME);
			 updates.put(name, getAttributeValue(att));
		 }
		 return updates;
	 }
	 public static void getDefinitions(Binder binder, Element config, CoreDao coreDao) {
		 List<Definition> defs = new ArrayList();
		 Map workflows = new HashMap();
		 List<Element> defElements = config.selectNodes("./" + ObjectKeys.XTAG_ELEMENT_TYPE_DEFINITION);
		 for (Element defElement:defElements) {
			 Definition def = null;
			 //first try databaseId because any workflows will reference it
			 String dId = defElement.attributeValue(ObjectKeys.XTAG_ATTRIBUTE_ID);
			 if (Validator.isNotNull(dId)) {
				 try {
					 def = coreDao.loadDefinition(dId, RequestContextHolder.getRequestContext().getZoneId());
				} catch (NoDefinitionByTheIdException nd) {};				 
			 }
		 	String id = defElement.attributeValue(ObjectKeys.XTAG_ATTRIBUTE_INTERNALID);
		 	if (Validator.isNotNull(id)) {
				 //first try internalId
				 try {
					 def = coreDao.loadReservedDefinition(id, RequestContextHolder.getRequestContext().getZoneId());
				 } catch (NoDefinitionByTheIdException nd) {};
			 }
			 // last try name
			 if (def == null) {
				 String name = defElement.attributeValue(ObjectKeys.XTAG_ATTRIBUTE_NAME);
				 if (Validator.isNotNull(name)) {
					 try {
						 List<Definition> matches = coreDao.loadObjects(Definition.class, 
								 new FilterControls(new String[] {ObjectKeys.FIELD_BINDER_NAME, ObjectKeys.FIELD_ZONE}, 
										 new Object[]{name, RequestContextHolder.getRequestContext().getZoneId()}));
						 if (!matches.isEmpty())  def = matches.get(0);
					} catch (NoDefinitionByTheIdException nd) {};
				 } 
			 }
			 if (def != null) {
				 if (def.getType() == Definition.WORKFLOW) {
					 String eId = getProperty(defElement, ObjectKeys.XTAG_ENTITY_DEFINITION); 
					 if (!Validator.isNotNull(id)) workflows.put(eId, def);
				 }
				 else defs.add(def);
			 }
		 }
		 //now see if we can map the workflows
		 binder.setDefinitions(defs);
		 binder.setWorkflowAssociations(workflows);
	 }
}

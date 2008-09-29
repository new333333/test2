/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.module.shared;

import static com.sitescape.team.util.Maybe.maybe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;
import org.dom4j.Node;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Description;
import com.sitescape.team.domain.NoDefinitionByTheIdException;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.security.function.Function;
import com.sitescape.team.security.function.WorkAreaFunctionMembership;
import com.sitescape.team.util.LongIdUtil;
import com.sitescape.team.util.CollectionUtil.Func1;
import com.sitescape.util.Validator;

public class XmlUtils {

	public static Element addDefinitionReference(Element parent, Definition def) {
		Element e = parent.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_DEFINITION);
		e.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_NAME, def.getName());
		e.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_INTERNALID, def.getInternalId());
		e.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_DATABASEID, def.getId().toString());
		return e;
	}
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
	 public static String getProperty(Element element, String name) {
		 Element variableEle = (Element)element.selectSingleNode("./property[@name='" + name + "']");
		 if (variableEle == null) return null;
		 return variableEle.getStringValue();   	
	 }

	//attributes are available through the definintion builder
	public static Element addCustomAttribute(Element parent, String name, String type, String value) {
		if (Validator.isNull(value)) return null;
		Element prop = parent.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_ATTRIBUTE);
		prop.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_NAME, name);
		prop.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_TYPE, type);
		prop.addText(value);
		return prop;
	}
	public static Element addCustomAttribute(Element parent, String name, String type, Collection values) {
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
	public static Element addCustomAttribute(Element parent, String name, String type, Object value) {
		if (value == null) return null;
		Element prop = parent.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_ATTRIBUTE);
		prop.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_NAME, name);
		prop.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_TYPE, type);
		prop.addText(value.toString());
		return prop;
	}

	 public static Object getCustomAttribute(Element element, String name) {
		 Element variableEle = (Element)element.selectSingleNode("./" + ObjectKeys.XTAG_ELEMENT_TYPE_ATTRIBUTE + "[@name='" + name + "']");
		 if (variableEle == null) return null;
		 return getCustomAttributeValue(variableEle);
	 }
	 public static Object getCustomAttributeValue(Element element) {
		 String type = element.attributeValue(ObjectKeys.XTAG_ATTRIBUTE_TYPE);
		 if (type == null) type = ObjectKeys.XTAG_TYPE_STRING;
		 if (type.equals(ObjectKeys.XTAG_TYPE_STRING)) {
			 return element.getText();  		 
		 } else if (type.equals(ObjectKeys.XTAG_TYPE_DESCRIPTION)) {
			 return new Description(element.getText());
		 }
		 return null;
		 
	 }

	 public static Map getCustomAttributes(Element config) {
		 Map updates = new HashMap();
		 List<Element> attributes = config.selectNodes("./" + ObjectKeys.XTAG_ELEMENT_TYPE_ATTRIBUTE);
		 for (Element att:attributes) {
			 String name = att.attributeValue(ObjectKeys.XTAG_ATTRIBUTE_NAME);
			 updates.put(name, getCustomAttributeValue(att));
		 }
		 return updates;
	 }
	 public static void getDefinitionsFromXml(Binder binder, Element config, CommonDependencyInjection ci) {
		 List<Definition> defs = new ArrayList();
		 Map workflows = new HashMap();
		 List<Element> defElements = config.selectNodes("./" + ObjectKeys.XTAG_ELEMENT_TYPE_DEFINITION);
		 for (Element defElement:defElements) {
			 Definition def = getDefinitionFromElement(defElement, ci);
			 if (def != null) {
				 if (def.getType() == Definition.WORKFLOW) {
					 List<Element> entryElements = defElement.selectNodes("./" + ObjectKeys.XTAG_ELEMENT_TYPE_DEFINITION);
					 if (entryElements.isEmpty()) {
						 //v1 stored only the id
						 String eId = getProperty(defElement, ObjectKeys.XTAG_ENTITY_DEFINITION); 
						 if (Validator.isNotNull(eId)) workflows.put(eId, def);
						 
					 } else {
						 for (Element entryElement:entryElements) {
							 Definition entryDef = getDefinitionFromElement(entryElement, ci);
							 if (entryDef != null) workflows.put(entryDef.getId(), def);
						 }
					 }

				 }
				 defs.add(def);
			 }
		 }
		 //now see if we can map the workflows
		 binder.setDefinitions(defs);
		 binder.setWorkflowAssociations(workflows);
	 }
	 public static Definition getDefinitionFromElement(Element defElement, CommonDependencyInjection ci) {
		 Definition def = null;
		 //first try databaseId because any workflows will reference it
		 String dId = defElement.attributeValue(ObjectKeys.XTAG_ATTRIBUTE_DATABASEID);
		 if (Validator.isNotNull(dId)) {
			 try {
				 def = ci.getCoreDao().loadDefinition(dId, RequestContextHolder.getRequestContext().getZoneId());
			} catch (NoDefinitionByTheIdException nd) {};				 
		 }
		 if (def == null) {
			 String id = defElement.attributeValue(ObjectKeys.XTAG_ATTRIBUTE_INTERNALID);
			 if (Validator.isNotNull(id)) {
				 //first try internalId
				 try {
					 def = ci.getCoreDao().loadReservedDefinition(id, RequestContextHolder.getRequestContext().getZoneId());
				 } catch (NoDefinitionByTheIdException nd) {};
			 }
		 }
	 	// last try name
		 if (def == null) {
			 String name = defElement.attributeValue(ObjectKeys.XTAG_ATTRIBUTE_NAME);
			 if (Validator.isNotNull(name)) {
				 try {
					 def = ci.getCoreDao().loadDefinitionByName(null, name, RequestContextHolder.getRequestContext().getZoneId());
				} catch (NoDefinitionByTheIdException nd) {};
			 } 
		 }
		 return def;
		 
	 }
	 public static Set getFunctionMembershipFromXml(Binder binder, Element config, CommonDependencyInjection ci) {
		 Long zoneId = RequestContextHolder.getRequestContext().getZoneId();  //may not be set yet on binder
		 List<Element> wfmElements = config.selectNodes("./" + ObjectKeys.XTAG_ELEMENT_TYPE_FUNCTION_MEMBERSHIP);
		 for (Element wfmElement:wfmElements) {
			 String functionName = getProperty(wfmElement, ObjectKeys.XTAG_WA_FUNCTION_NAME);
			 List<Function> fs = ci.getCoreDao().loadObjects(Function.class, 
					 new FilterControls("name", functionName), zoneId);
			 if (fs.isEmpty()) continue;
			 List<Element> teamElements = wfmElement.selectNodes("./" + ObjectKeys.XTAG_ELEMENT_TYPE_PROPERTY + "[@name='" + ObjectKeys.XTAG_WA_MEMBER_NAME + "']");
			 Set ids = namesToIds(teamElements, ci);
			 ids.addAll(LongIdUtil.getIdsAsLongSet(XmlUtils.getProperty(wfmElement, ObjectKeys.XTAG_WA_MEMBERS)));

			 if (ids.isEmpty()) continue;
			 WorkAreaFunctionMembership wfm = new WorkAreaFunctionMembership();
			 wfm.setFunctionId(fs.get(0).getId());
			 wfm.setZoneId(zoneId);
			 wfm.setMemberIds(ids);
			 wfm.setWorkAreaId(binder.getWorkAreaId());
			 wfm.setWorkAreaType(binder.getWorkAreaType());
			 ci.getCoreDao().save(wfm);
		 }
		 return new HashSet();
	 }
	 //convert principal names to real principals
	 public static void getTeamMembersFromXml(Binder binder, Element config, CommonDependencyInjection ci) {
		 List<Element> teamElements = config.selectNodes("./" + ObjectKeys.XTAG_ELEMENT_TYPE_PROPERTY + "[@name='" + ObjectKeys.XTAG_BINDER_TEAMMEMBER_NAME + "']");
		 binder.setTeamMemberIds(namesToIds(teamElements, ci));
	 }
	protected static Set namesToIds(List<Element>nameAttributes, CommonDependencyInjection ci) {
		Set<String> names = new HashSet();
		for (Element nameElement:nameAttributes) {
			 names.add(nameElement.getTextTrim());
		 }
		if (names.isEmpty()) return new HashSet();
		 Map filter = new HashMap();
		 filter.put(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId());
		 filter.put("name", names);
		 
		 List ids = ci.getCoreDao().loadObjects("select id from com.sitescape.team.domain.Principal where zoneId=:zoneId and name in (:name)", filter);
		 return new HashSet(ids);
	 }
	
	public static String maybeGetText(Node context, String xpath, String alternative) {
		return maybe(context.selectSingleNode(xpath)).and(new Func1<Node, String>() {
			public <Source extends Node> String apply(Source x) {
				return x.getText();
			}}, alternative);
	}

}

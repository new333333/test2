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
package org.kablink.teaming.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.module.shared.XmlUtils;
import org.kablink.util.Validator;


/**
 * This object represents an abstract dashboard.
 * 
 * @hibernate.class table="SS_Dashboards" dynamic-update="true" dynamic-insert="false" lazy="false"
 * @hibernate.discriminator type="string" length="1" column="type"
 * @hibernate.cache usage="read-write"
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 *
 */
public abstract class Dashboard extends PersistentTimestampObject {
	public final static String COMPONENTS = "components";
	public final static String DATA="data";
	
	//Dashboard map keys
	public final static String TITLE = "title";
	public final static String INCLUDEBINDERTITLE = "includeBinderTitle";
	public final static String DISPLAYSTYLE = "displayStyle";

	//Component map keys
	public final static String NAME = "name";
	public final static String COMPONENT_TITLE = "title";
	public final static String ROLES = "roles";

	//Component layout map keys (Components)
	public final static String ID = "id";
	public final static String SCOPE = "scope";
	public final static String VISIBLE = "visible";
	

	//Component Order lists
	public final static String WIDE_TOP = "wide_top";
	public final static String NARROW_FIXED = "narrow_fixed";
	public final static String NARROW_VARIABLE = "narrow_variable";
	public final static String WIDE_BOTTOM = "wide_bottom";
	
	//Component data keys
	public final static String COMPONENT_DATA_URL = "url";
	public final static String COMPONENT_DATA_HTML = "html";
	
	
    protected Map properties;
	private int nextComponentId=0;
	protected boolean showComponents=true;
	protected Integer version = 1;

	public Dashboard() {
	}
	public Dashboard(Dashboard source) {
		super();
		setNextComponentId(source.getNextComponentId());
		setShowComponents(source.isShowComponents());
		setVersion(source.getVersion());
		//have to make copies of all the nested maps incase they get changed
		//before another session = will change both
		Map sProps = source.getProperties();
		if (sProps == null) return;
		Map dProps = new HashMap(sProps);  //start by copying them all
		Map sComps = (Map)sProps.get(COMPONENTS);
		if (sComps != null) {
			Map dComps = new HashMap();
			dProps.put(COMPONENTS, dComps);
			//loop through components
			for (Iterator iter=sComps.entrySet().iterator(); iter.hasNext();) {
				Map.Entry me = (Map.Entry)iter.next();
				Map sCompMap = (Map)me.getValue();
				if (sCompMap == null) continue;
				Map dCompMap = new HashMap(sCompMap);
				dComps.put(me.getKey(), dCompMap);  //replace component
				Map sData = (Map)sCompMap.get(DATA);
				if (sData == null) continue;
				Map dData = new HashMap(sData);
				dCompMap.put(DATA, dData);   //replace data
			}
		}
		setProperties(dProps);  //save away but keep changing
	}
	public Dashboard(Element config) {
		fromXml(config);
	}
	
    /**
     * @hibernate.property type="org.springframework.orm.hibernate3.support.BlobSerializableType"
     * @return
     */
    public Map getProperties() {
    	return properties;
    }
    public void setProperties(Map properties) {
        this.properties = properties;
    }
    public void setProperty(String name, Object value) {
  	   if (value instanceof Object[]) throw new IllegalArgumentException("Arrays not supported");
  	   if (value instanceof Document) throw new IllegalArgumentException("XML docs not supported");
  	   if (properties == null) properties = new HashMap();
  	   properties.put(name, value);
    }
    public Object getProperty(String name) {
    	if (properties == null) return null;
    	return properties.get(name);
    }

    /**
     * @hiberate.property
     */
    public int getNextComponentId() {
    	return nextComponentId;
    }
    public void setNextComponentId(int nextComponentId) {
    	this.nextComponentId = nextComponentId;
    }
    /**
     * @hiberate.property - obsolete, stored as user/binder properties
     */
	public boolean isShowComponents() {
		return showComponents;
	}
	public void setShowComponents(boolean showComponents) {
		this.showComponents = showComponents;
	}    
    /**
     * @hiberate.property
     */
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public void asXml(Element parent) {
		Element d = parent.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_DASHBOARD);
		XmlUtils.addProperty(d, "nextComponentId", String.valueOf(getNextComponentId()));
		XmlUtils.addProperty(d, "showComponents", String.valueOf(isShowComponents()));
		XmlUtils.addProperty(d, "version", getVersion());
		XmlUtils.addProperty(d, TITLE, getProperty(TITLE));
		XmlUtils.addProperty(d, INCLUDEBINDERTITLE, getProperty(INCLUDEBINDERTITLE));
		layoutAsXml(d, WIDE_TOP);
		layoutAsXml(d, NARROW_FIXED);
		layoutAsXml(d, NARROW_VARIABLE);
		layoutAsXml(d,  WIDE_BOTTOM);
		Map components = (Map)getProperty(COMPONENTS);
		for (Iterator iter=components.entrySet().iterator(); iter.hasNext();) {
			Map.Entry me = (Map.Entry)iter.next();
			Map cMap = (Map)me.getValue();
			if (cMap == null) continue;
			Element component = d.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_DASHBOARD_COMPONENT);
			component.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_NAME, me.getKey().toString());
			componentAsXml(component, cMap);
		}
		
	}
	//rebuild dashboard from xml
	public void fromXml(Element config) {
		setNextComponentId(Integer.valueOf(XmlUtils.getProperty(config, "nextComponentId")));
		setShowComponents(Boolean.valueOf(XmlUtils.getProperty(config, "showComponents")));
		setVersion(Integer.valueOf(XmlUtils.getProperty(config, "version")));
		String val = XmlUtils.getProperty(config, TITLE);
		if (Validator.isNotNull(val)) setProperty(TITLE, val);
		
		val = XmlUtils.getProperty(config,INCLUDEBINDERTITLE);
		if (Validator.isNotNull(val)) setProperty(INCLUDEBINDERTITLE, Boolean.parseBoolean(val));
		
		layoutFromXml(config, WIDE_TOP);
		layoutFromXml(config, NARROW_FIXED);
		layoutFromXml(config, NARROW_VARIABLE);
		layoutFromXml(config, WIDE_BOTTOM);
		List<Element>componentElements= config.selectNodes("./" + ObjectKeys.XTAG_ELEMENT_TYPE_DASHBOARD_COMPONENT);
		Map components = new HashMap();
		for (Element ele:componentElements) {
			String name = ele.attributeValue(ObjectKeys.XTAG_ATTRIBUTE_NAME);
			components.put(name, componentFromXml(ele));
		}
		setProperty(COMPONENTS, components);
	}
	
	private void layoutFromXml(Element parent, String name) {
		List<Map> contents = new ArrayList();
		List<Element> layouts = parent.selectNodes("./" + 
				ObjectKeys.XTAG_ELEMENT_TYPE_DASHBOARD_LAYOUT + "[@name='" + name + "']");
		for (Element ele: layouts) {
			Map layout = new HashMap();
			List<Element> layoutProps = ele.selectNodes("./" + ObjectKeys.XTAG_ELEMENT_TYPE_PROPERTY);
			for (Element lEle:layoutProps) {
				layout.put(lEle.attributeValue(ObjectKeys.XTAG_ATTRIBUTE_NAME), lEle.getText());
			}
			if (layout.containsKey(VISIBLE)) {
				String val = (String)layout.get(VISIBLE);
				if (Validator.isNull(val)) layout.remove(VISIBLE);
				else layout.put(VISIBLE, Boolean.parseBoolean(val));
			}
			contents.add(layout);
		}
		setProperty(name, contents);
	}
	private void layoutAsXml(Element parent, String name) {
		List<Map> contents = (List)getProperty(name);
		for (Map cMap:contents) {
			Element layout = parent.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_DASHBOARD_LAYOUT);
			layout.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_NAME, name);
			for (Iterator iter=cMap.entrySet().iterator(); iter.hasNext();) {
				Map.Entry me = (Map.Entry)iter.next();
				Object val = me.getValue();
				if (val == null) continue;
				XmlUtils.addProperty(layout, (String)me.getKey(), me.getValue().toString());				
			}
		}		
	}
	private Map componentFromXml(Element component) {
		Map componentMap = new HashMap();
		List<Element> props = component.selectNodes(ObjectKeys.XTAG_ELEMENT_TYPE_PROPERTY);
		for (Element lEle:props) {
			componentMap.put(lEle.attributeValue(ObjectKeys.XTAG_ATTRIBUTE_NAME), lEle.getText());
		}
		Element dataElement = (Element)component.selectSingleNode(ObjectKeys.XTAG_ELEMENT_TYPE_DASHBOARD_COMPONENT_DATA);
		if (dataElement != null) {
			Map dataMap = new HashMap();
			props = dataElement.selectNodes(ObjectKeys.XTAG_ELEMENT_TYPE_PROPERTY);
			for (Element lEle:props) {
				dataMap.put(lEle.attributeValue(ObjectKeys.XTAG_ATTRIBUTE_NAME), lEle.getText());
			}
			componentMap.put(DATA, dataMap);
		}
		return componentMap;

	}
	private void componentAsXml(Element component, Map componentMap) {
		for (Iterator iter=componentMap.entrySet().iterator(); iter.hasNext();) {
			Map.Entry prop = (Map.Entry)iter.next();
			String propName = (String)prop.getKey();
			Object propValue = prop.getValue();
			if (DATA.equals(propName)) {
				Element dataElement = component.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_DASHBOARD_COMPONENT_DATA);
				Map dataMap = (Map)propValue;
				if ((dataMap != null) && !dataMap.isEmpty()) {
					for (Iterator iter2=dataMap.entrySet().iterator(); iter2.hasNext();) {
						Map.Entry dataProp = (Map.Entry)iter2.next();
						String dataName = (String)dataProp.getKey();
						if (Validator.isNull(dataName)) continue;
						XmlUtils.addProperty(dataElement, dataName, dataProp.getValue());
					}
				}
				
			} else {
				XmlUtils.addProperty(component, propName, propValue);
			} 
		}
		
	}

}

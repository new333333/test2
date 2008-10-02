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
package com.sitescape.team.module.definition.notify;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.InternalException;
import com.sitescape.team.SingletonViolationException;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.module.definition.DefinitionConfigurationBuilder;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.ReflectHelper;
import com.sitescape.util.Validator;
import org.apache.velocity.tools.view.XMLToolboxManager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.beans.factory.InitializingBean;


/**
 *
 * @author Jong Kim
 */
public class NotifyBuilderUtil implements InitializingBean {
	public static Log logger = LogFactory.getLog(NotifyBuilderUtil.class);
	protected VelocityEngine velocityEngine;
	protected  DefinitionConfigurationBuilder definitionConfigurationBuilder;
	private static NotifyBuilderUtil instance;
	protected ClassPathResource configFile;
	protected Map toolbox;
	public NotifyBuilderUtil() {
		if(instance != null)
			throw new SingletonViolationException(NotifyBuilderUtil.class);
		
		instance = this;

	}
	public static DefinitionConfigurationBuilder getDefinitionBuilderConfig() {
        return getInstance().definitionConfigurationBuilder;
    }
    public void setDefinitionBuilderConfig(DefinitionConfigurationBuilder definitionBuilderConfig) {
        this.definitionConfigurationBuilder = definitionBuilderConfig;
    }

	public void setVelocityEngine(VelocityEngine velocityEngine) {
	        this.velocityEngine = velocityEngine;
	}
	public static VelocityEngine getVelocityEngine() {
        return getInstance().velocityEngine;
	}
	public void setToolboxConfig(String configFile) {
		this.configFile = new ClassPathResource(configFile);
 	}
	public void afterPropertiesSet() {
		XMLToolboxManager manager = new XMLToolboxManager();
		try {
			manager.load(configFile.getInputStream());
			toolbox = manager.getToolbox(null);
		} catch (Exception ex) {
			logger.error("Error initializing toolbox: " + ex);
			throw new ConfigurationException("Error initializing toolbox: ", ex);
	    };
			
	}
	public static VelocityContext getVelocityContext(Map args) {
		Map ctxArgs = new HashMap(getInstance().toolbox);
		if (args != null) ctxArgs.putAll(args);
		return new VelocityContext(ctxArgs);
	}
	public static VelocityContext getVelocityContext() {
		return getVelocityContext(null);
	}
	private static NotifyBuilderUtil getInstance() {
		return instance;
	}
    public static void buildElements(DefinableEntity entity, Notify notifyDef, Writer writer, NotifyVisitor.WriterType writerType, Map params) {
		Definition def = entity.getEntryDef();
		if (def == null) return ;
		Document definitionTree = def.getDefinition();
		if (definitionTree != null) {
			Element root = definitionTree.getRootElement();

			//	Get a list of all of the items in the definition
			Element entryItem = (Element)root.selectSingleNode("//item[@name='entryView']");
			if (entryItem == null) return ;
	    	params.put("com.sitescape.team.notify.params.family",DefinitionUtils.getFamily(definitionTree));
	    	buildElements(entity, entryItem, notifyDef, writer, writerType, params, true);
		}

    }
    public static void buildElements(DefinableEntity entity, Element item, Notify notifyDef, Writer writer, NotifyVisitor.WriterType writerType,  Map params, boolean processItem) {
        String flagElementPath = "./notify";
        List<Element> items;
        if (processItem) {  //starting point
        	items = new ArrayList();
        	items.add(item);
        } else {
            items = item.elements("item");
        	
        }
        if (items == null) return;
        for (Element nextItem:items) {
        	Element flagElem = (Element) nextItem.selectSingleNode(flagElementPath);
           	if (flagElem == null) {
              	 // The current item in the entry definition does not contain
              	 // the flag element. Check the corresponding item in the default
               	 // config definition to see if it has it.
               	 // This two level mechanism allows entry definition (more specific
               	 // one) to override the settings in the default config definition
               	 // (more general one). This overriding works in its 
               	 // entirity only, that is, partial overriding is not supported.
           		 //Find the item in the base configuration definition to see if it is a data item
                 String itemName = (String) nextItem.attributeValue("name");	
                 if ("entryDataItem".equals(itemName)) {
                	 itemName = nextItem.attributeValue("formItem");
                 }
     			Element configItem = getDefinitionBuilderConfig().getItem(null, itemName);
     			if (configItem != null) flagElem = (Element) configItem.selectSingleNode(flagElementPath);
            }

           	if (flagElem == null) {
           		//proceed to contents
           		buildElements(entity, nextItem, notifyDef, writer, writerType, params, false);
           		continue;
           	}
           	Map oArgs = DefinitionUtils.getOptionalArgs(flagElem);
           	NotifyVisitor visitor = new NotifyVisitor(entity, notifyDef, nextItem, writer, writerType, params);
           	buildElement(entity, visitor, flagElem, oArgs);
        }
    }
    protected static void buildElement(DefinableEntity entity, NotifyVisitor visitor, Element flagElement, Map oArgs) {
    	VelocityContext ctx = getVelocityContext(oArgs);
		//Each item property that has a value is added as a "request attribute". 
		//  The key name is "property_xxx" where xxx is the property name.
		//At a minimum, make sure the name and caption variables are defined
		ctx.put("property_name", "");
		ctx.put("property_caption", "");
		ctx.put("ssVisitor", visitor);
		
		String itemType = visitor.getItem().attributeValue("name", "");
		//get Item from main config document
		Element itemDefinition = getDefinitionBuilderConfig().getItem(null, itemType);

		//Also set up the default values for all properties defined in the definition configuration
		//  These will be overwritten by the real values (if they exist) below
		List<Element> itemDefinitionProperties = itemDefinition.selectNodes("properties/property");
		Map propertyValuesMap = new HashMap();
		Map savedReqAttributes = new HashMap();
		for (Element property:itemDefinitionProperties) {
			String propertyName = property.attributeValue("name", "");
			if (Validator.isNull(propertyName)) continue;
			//Get the type from the config definition
			String propertyConfigType = property.attributeValue("type", "text");
			String propertyValue = "";	
			//Get the value(s) from the actual definition
			if (propertyConfigType.equals("selectbox")) {
				//get all items with same name
				List<Element> selProperties = visitor.getItem().selectNodes("properties/property[@name='"+propertyName+"']");
				if (selProperties == null) continue;
				//There might be multiple values so bulid a list
				List propertyValues = new ArrayList();
				for (Element selItem:selProperties) {
					String selValue = NLT.getDef(selItem.attributeValue("value", ""));
					if (Validator.isNotNull(selValue)) propertyValues.add(selValue);
					
				}
				propertyValuesMap.put("propertyValues_"+propertyName, propertyValues);
				propertyValuesMap.put("property_"+propertyName, "");
			} else {
				Element selItem = (Element)visitor.getItem().selectSingleNode("properties/property[@name='"+propertyName+"']");
				if (selItem == null) selItem=property;
				if (propertyConfigType.equals("textarea")) {
					propertyValue = selItem.getText();
				} else {										
					propertyValue = NLT.getDef(selItem.attributeValue("value", ""));
				}
				//defaults don't apply here
				//Set up any "setAttribute" values that need to be passed along. Save the old value so it can be restored
				String reqAttrName = property.attributeValue("setAttribute", "");
				if (Validator.isNotNull(reqAttrName)) {
					//Find this property in the current config
					savedReqAttributes.put(reqAttrName, ctx.get(reqAttrName));
					ctx.put(reqAttrName, propertyValue);
				}
				if (Validator.isNull(propertyValue)) {
					propertyValue = property.attributeValue("default", "");
					if (!Validator.isNull(propertyValue)) propertyValue = NLT.getDef(propertyValue);
				}
				propertyValuesMap.put("property_"+propertyName, propertyValue);
			
			}
				
		}
	
		Iterator itPropertyValuesMap = propertyValuesMap.entrySet().iterator();
		while (itPropertyValuesMap.hasNext()) {
			Map.Entry entry = (Map.Entry)itPropertyValuesMap.next();
			ctx.put((String)entry.getKey(), entry.getValue());
		}
			
	   	try {
    		String fieldBuilderClassName = flagElement.attributeValue("notifyBuilder");
       		String template = flagElement.attributeValue("velocity");
       		if (Validator.isNotNull(fieldBuilderClassName)) {
       			Class fieldBuilderClass = ReflectHelper.classForName(fieldBuilderClassName);
       			NotifyBuilder fieldBuilder = (NotifyBuilder) fieldBuilderClass.newInstance();
        		fieldBuilder.buildElement(visitor, template, ctx);
       		} else {
       			if (Validator.isNull(template)) template = "dataElement.vm";
       			try {
       				visitor.processTemplate(template, ctx);
       			} catch (Exception ex) {
       				NotifyBuilderUtil.logger.error("Error processing template", ex);
       			}
       		}
       
    	} catch (ClassNotFoundException e) {
    		throw new InternalException (e);
    	} catch (InstantiationException e) {
    		throw new InternalException (e);
    	} catch (IllegalAccessException e) {
    		throw new InternalException (e);
    	}

    }
 
}

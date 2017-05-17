/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.module.definition.notify;

import java.io.File;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import org.apache.velocity.runtime.RuntimeConstants;
import org.dom4j.Document;
import org.dom4j.Element;

import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.SingletonViolationException;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.module.definition.DefinitionConfigurationBuilder;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.util.EmailTemplatesHelper;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.cache.ClassInstanceCache;
import org.kablink.util.Validator;

import org.apache.velocity.tools.view.XMLToolboxManager;

import org.springframework.core.io.ClassPathResource;
import org.springframework.beans.factory.InitializingBean;

/**
 * ?
 * 
 * @author Jong Kim
 */
@SuppressWarnings("unchecked")
public class NotifyBuilderUtil implements InitializingBean {
	public static Log logger = LogFactory.getLog(NotifyBuilderUtil.class);

	protected VelocityEngine m_baseVelocityEngine;
	protected Map<String, VelocityEngine> m_zoneVEMap;
	protected  DefinitionConfigurationBuilder definitionConfigurationBuilder;
	private static NotifyBuilderUtil instance;
	protected ClassPathResource configFile;
	protected Map toolbox;
	
	/**
	 * Constructor method.
	 */
	public NotifyBuilderUtil() {
		if(instance != null)
			throw new SingletonViolationException(NotifyBuilderUtil.class);
		
		instance = this;
		
		m_zoneVEMap = new HashMap<String, VelocityEngine>();

	}
	public static DefinitionConfigurationBuilder getDefinitionBuilderConfig() {
        return getInstance().definitionConfigurationBuilder;
    }
    public void setDefinitionBuilderConfig(DefinitionConfigurationBuilder definitionBuilderConfig) {
        this.definitionConfigurationBuilder = definitionBuilderConfig;
    }

	public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.m_baseVelocityEngine = velocityEngine;
	}
	
	public static VelocityEngine getVelocityEngine() {
		VelocityEngine reply;
		
		// Do we have a VelocityEngine cached for this zone?
		String customPath  = EmailTemplatesHelper.getEmailTemplatesCustomizedPath(false);
		Map<String, VelocityEngine> zoneVEMap = getInstance().m_zoneVEMap;
		reply = zoneVEMap.get(customPath);
		if (null == reply) {
			// No!  Get the base VelocityEngine we were instantiated
			// with.
			VelocityEngine ve = getInstance().m_baseVelocityEngine;
	
			// Create a VelocityEngine based on that one, but
			// initialized with our zone specific settings...
			VelocityEngine zoneVE = new VelocityEngine();
			zoneVE.setProperty("input.encoding",                                 ve.getProperty("input.encoding")                                             );
			zoneVE.setProperty("output.encoding",                                ve.getProperty("output.encoding")                                            );
			zoneVE.setProperty("velocimacro.library",                            ve.getProperty("velocimacro.library")                                        );
			zoneVE.setProperty("file.resource.loader.cache",                     ve.getProperty("file.resource.loader.cache")                                 );
			zoneVE.setProperty("file.resource.loader.modificationCheckInterval", SPropsUtil.getString("velocity.engine.modification.check.interval", "60")    );
			zoneVE.setProperty("file.resource.loader.path",                      (customPath + "," + EmailTemplatesHelper.getEmailTemplatesDefaultPath(false)));
			zoneVE.setProperty(RuntimeConstants.RUNTIME_LOG, customPath + File.separator + "velocity.log");
			try {
				zoneVE.init();
				
				// ...and cache and return it.
				zoneVEMap.put(customPath, zoneVE);
				reply = zoneVE;
				logger.info("New VelocityEngine cached for '" + customPath + "'.");
			}
			catch (Exception e) {
				logger.error("Error initializing VelocityEngine for '" + customPath + "':  ", e);
				reply = ve;
			}
		}
	
        return reply;
	}
	
	public void setToolboxConfig(String configFile) {
		this.configFile = new ClassPathResource(configFile);
 	}
	
	/**
	 * Returns true if reseting the Velocity engine is enabled and
	 * false otherwise.
	 * 
	 * @return
	 */
	public static boolean isVelocityEngineResetEnabled() {
		return SPropsUtil.getBoolean("velocity.engine.reset.enabled", false);
	}
	
	/**
	 * If enabled, resets the Velocity engine for the customPath
	 * (i.e., a specific zone.)
	 * 
	 * @param customPath
	 */
	public static void resetCachedZoneVelocityEngine(String customPath) {
		// Is reseting the Velocity engine enabled?
		if (isVelocityEngineResetEnabled()) {
			// Yes!  If we're tracking a VelocityEngine for this custom
			// path...
			Map<String, VelocityEngine> zoneVEMap = getInstance().m_zoneVEMap;
			if (zoneVEMap.containsKey(customPath)) {
				// ...forget about it.
				zoneVEMap.remove(customPath);
				logger.info("Cached VelocityEngine for '" + customPath + "' has been reset.");
			}
		}
	}
	
	@Override
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
    public static void addVelocityTemplate(DefinableEntity entity, Notify notifyDef, Writer writer, 
    		NotifyVisitor.WriterType writerType, Map params, String template) {
		if (entity.getEntryDefId() == null) return ;
    	VelocityContext ctx = getVelocityContext(params);
 		ctx.put("ssEntity", entity);
 		NotifyVisitor visitor = new NotifyVisitor(entity, notifyDef, null, writer, writerType, params);
		ctx.put("ssVisitor", visitor);
		try {
			visitor.processTemplate(template, ctx);
		} catch (Exception ex) {
			NotifyBuilderUtil.logger.error("Error processing template", ex);
		}
    }

    public static void addVelocityTemplate(DefinableEntity entity, Map params, Writer writer, 
    		NotifyVisitor.WriterType writerType, String template) {
    	VelocityContext ctx = getVelocityContext(params);
 		ctx.put("ssEntity", entity);
 		NotifyVisitor visitor = new NotifyVisitor(entity, null, null, writer, writerType, params);
		ctx.put("ssVisitor", visitor);
		try {
			visitor.processTemplate(template, ctx);
		} catch (Exception ex) {
			NotifyBuilderUtil.logger.error("Error processing template", ex);
		}
    }

    public static void buildElements(DefinableEntity entity, Notify notifyDef, Writer writer, NotifyVisitor.WriterType writerType, Map params) {
		if (entity.getEntryDefId() == null) return ;
		Document definitionTree = entity.getEntryDefDoc();
		if (definitionTree != null) {
			Element root = definitionTree.getRootElement();

			//	Get a list of all of the items in the definition
			Element entryItem = (Element)root.selectSingleNode("//item[@name='entryView']");
			if (entryItem == null) return ;
	    	params.put("org.kablink.teaming.notify.params.family",DefinitionUtils.getFamily(definitionTree));
	    	buildElements(entity, entryItem, notifyDef, writer, writerType, params, true);
		}

    }
    public static void buildElements(DefinableEntity entity, Element item, Notify notifyDef, Writer writer, 
    		NotifyVisitor.WriterType writerType, Map params, boolean processItem) {
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

		// What Locale should we read resource strings with?
		Locale visitorLocale = null;
		Notify notifyDef = visitor.getNotifyDef();
		if (null != notifyDef)     visitorLocale = notifyDef.getLocale();
		if (null == visitorLocale) visitorLocale = NLT.getDefaultLocale();
		
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
					String selValue = NLT.getDef(selItem.attributeValue("value", ""), visitorLocale);
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
					propertyValue = NLT.getDef(selItem.attributeValue("value", ""), visitorLocale);
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
					if (!Validator.isNull(propertyValue)) propertyValue = NLT.getDef(propertyValue, visitorLocale);
				}
				propertyValuesMap.put("property_"+propertyName, propertyValue);
			
			}
				
		}
	
		Iterator itPropertyValuesMap = propertyValuesMap.entrySet().iterator();
		while (itPropertyValuesMap.hasNext()) {
			Map.Entry entry = (Map.Entry)itPropertyValuesMap.next();
			ctx.put((String)entry.getKey(), entry.getValue());
		}
			
   		String fieldBuilderClassName = flagElement.attributeValue("notifyBuilder");
   		String template = flagElement.attributeValue("velocity");
   		if (Validator.isNotNull(fieldBuilderClassName)) {
   			NotifyBuilder fieldBuilder = (NotifyBuilder)ClassInstanceCache.getInstance(fieldBuilderClassName);
   			fieldBuilder.buildElement(visitor, template, ctx);
   		} else {
   			if (Validator.isNull(template)) template = "dataElement.vm";
   			try {
   				visitor.processTemplate(template, ctx);
   			} catch (Exception ex) {
   				NotifyBuilderUtil.logger.error("Error processing template", ex);
   			}
   		}
    }
}

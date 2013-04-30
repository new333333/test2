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
package org.kablink.teaming.modelprocessor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.util.MergeableXmlClassPathConfigFiles;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.SpringContextUtil;


/**
 *
 * @author Jong Kim
 */
public class ProcessorManager {

	protected Log logger = LogFactory.getLog(getClass());

    private static final String SPRING_BEAN_TYPE_EXTERNAL_STR 	= "external";
    private static final String SPRING_BEAN_TYPE_INTERNAL_STR 	= "internal";
    private static final String SPRING_BEAN_TYPE_NONE_STR		= "none";
    private static final String SPRING_BEAN_TYPE_DEFAULT_STR	= SPRING_BEAN_TYPE_NONE_STR;
        
    private static final int SPRING_BEAN_TYPE_EXTERNAL 	= 1;
    private static final int SPRING_BEAN_TYPE_INTERNAL 	= 2;
    private static final int SPRING_BEAN_TYPE_NONE 		= 3;
    private static final int SPRING_BEAN_TYPE_DEFAULT 	= SPRING_BEAN_TYPE_NONE;
    
    private Document document;
    
    // TODO We might want to cache the processors (or their class names) 
    // so that we don't have to rebuild them over and over again...
    
    public void setConfig(MergeableXmlClassPathConfigFiles config) {
        this.document = config.getAsMergedDom4jDocument();
    }
    
    public Object getProcessor(Object model, String processorKey) 
    	throws ConfigurationException {
        String processorClassName = getProcessorClassName(model, processorKey);
        
        Object result = getProcessor(processorClassName);
		return result;
    }
    

    public Object getProcessor(String modelClassName, String processorKey) 
    	throws ConfigurationException {
        String processorClassName = getProcessorClassName(modelClassName, processorKey);
        
        return getProcessor(processorClassName);
    }
    
    public String getProcessorClassName(Object model, String processorKey)
    	throws ConfigurationException {
        String processorClassName = null;
        if(model instanceof InstanceLevelProcessorSupport) {
            // Try getting the processor's class name associated with the model instance.
            // null value is allowed.
            InstanceLevelProcessorSupport support = (InstanceLevelProcessorSupport) model;
            processorClassName = support.getProcessorClassName(processorKey);
        }
        if(processorClassName == null) {
            // Try getting the processor's class name associated with the model class.
            // null value is not allowed.
            processorClassName = getProcessorClassName(model.getClass().getName(), processorKey);
        }
        return processorClassName;
    }
    
    public String getProcessorClassName(String modelClassName, String processorKey) 
		throws ConfigurationException {
        String name = null;
        try {
            name = getProcessorClassNameRecursively(modelClassName, processorKey);
        } catch (ClassNotFoundException e) {
            throw new ConfigurationException
            	("Cannot get processor class name when model class name is '" + 
            	        modelClassName + "' and processor key is '" + processorKey + "'");
        }
        if(name == null)
            throw new ConfigurationException("Model class name '" + 
                    modelClassName + "' and processor key '" + processorKey + 
                    "' does not map to a processor class name");
        else
            return name;
    }

    private Object getProcessor(String processorClassName)
            throws ConfigurationException {
        Object processor = null;

        int springBeanType = getSpringBeanType(processorClassName);
        
        if(springBeanType == SPRING_BEAN_TYPE_INTERNAL) {
            String springBeanName = getSpringBeanName(processorClassName);
            processor = SpringContextUtil.getBean(springBeanName);
            if(processor == null)
                throw new ConfigurationException("Spring bean of name '" + 
                        springBeanName + "' not found for processor class '" + 
                        processorClassName + "'");
        }
        else {
	        // Load processor class
        	processor = ReflectHelper.getInstance(processorClassName);
	        
	        if(springBeanType == SPRING_BEAN_TYPE_EXTERNAL) {
	            // This is an externally instantiated bean whose dependencies 
	            // are defined in the Spring bean context. Inject dependencies 
	            // into the processor instance.
	            SpringContextUtil.applyDependencies(processor, getSpringBeanName(processorClassName));
	        }
        }

        return processor;
    }

    private String getProcessorClassNameRecursively(String modelClassName, 
            String processorKey) throws ClassNotFoundException { 
        StringBuffer sb = new StringBuffer();
        sb.append("/model-processor-mapping/default-mappings/model-class[@name='")
        .append(modelClassName)
        .append("']/mapping[@processor-key='")
        .append(processorKey)
        .append("']");
        String xPath = sb.toString();
        
        Element processorElem = (Element) document.selectSingleNode(xPath);
        if(processorElem != null) {
            return processorElem.attributeValue("processor-class");
        }
        else {
            Class superClass = ReflectHelper.classForName(modelClassName).getSuperclass();
            if(superClass != null)            
                return getProcessorClassNameRecursively(superClass.getName(), processorKey);
            else
                return null;
        }
    }
    
    private int getSpringBeanType(String processorClassName) {
        StringBuffer sb = new StringBuffer();
        sb.append("/model-processor-mapping/processors/processor[@class='")
        .append(processorClassName)
        .append("']/@spring-bean-type");
        String xPath = sb.toString();
        
        Attribute processorSpringBeanTypeAttr = (Attribute) document.selectSingleNode(xPath);
        
        int springBeanType = SPRING_BEAN_TYPE_DEFAULT;
        if(processorSpringBeanTypeAttr != null) {
            String processorSpringBeanTypeStr = processorSpringBeanTypeAttr.getValue();
            if(processorSpringBeanTypeStr.equals(SPRING_BEAN_TYPE_EXTERNAL_STR))
                springBeanType = SPRING_BEAN_TYPE_EXTERNAL;
            else if(processorSpringBeanTypeStr.equals(SPRING_BEAN_TYPE_INTERNAL_STR))
                springBeanType = SPRING_BEAN_TYPE_INTERNAL;
            else if(processorSpringBeanTypeStr.equals(SPRING_BEAN_TYPE_NONE_STR))
                springBeanType = SPRING_BEAN_TYPE_NONE;
            else
                logger.warn("Illegal value '" + processorSpringBeanTypeStr + 
                        "' for spring-bean-type attribute: defaulting to '" + 
                        SPRING_BEAN_TYPE_DEFAULT_STR + "'");
        }
        
        return springBeanType;
    }
    
    private String getSpringBeanName(String processorClassName)
        throws ConfigurationException {
        StringBuffer sb = new StringBuffer();
        sb.append("/model-processor-mapping/processors/processor[@class='")
        .append(processorClassName)
        .append("']/@spring-bean-name");
        String xPath = sb.toString();
        
        Attribute processorSpringBeanNameAttr = (Attribute) document.selectSingleNode(xPath);
        
        String beanName = null;
        if(processorSpringBeanNameAttr != null)
            beanName = processorSpringBeanNameAttr.getValue();
        
        if(beanName == null || beanName.length() == 0)
            throw new ConfigurationException("spring-bean-name attribute must be specified for '" + processorClassName + "' processor class");
        
        return beanName;
    }
}

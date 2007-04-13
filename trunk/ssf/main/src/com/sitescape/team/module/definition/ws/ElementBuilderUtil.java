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
package com.sitescape.team.module.definition.ws;

import java.util.Map;

import org.dom4j.Element;
import org.dom4j.DocumentHelper;
import com.sitescape.team.InternalException;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.util.ReflectHelper;

/**
 *
 * @author Joe DeStefano
 */
public class ElementBuilderUtil {

    public static void buildElement(Element parent, DefinableEntity entity, String dataElemName, 
    			String fieldBuilderClassName) {
        try {
            Class fieldBuilderClass = ReflectHelper.classForName(fieldBuilderClassName);
            ElementBuilder fieldBuilder = (ElementBuilder) fieldBuilderClass.newInstance();
            Element element = DocumentHelper.createElement("attribute");
            if (fieldBuilder.buildElement(element, entity, dataElemName))
            	parent.add(element);
        } catch (ClassNotFoundException e) {
            throw new InternalException (e);
        } catch (InstantiationException e) {
            throw new InternalException (e);
        } catch (IllegalAccessException e) {
            throw new InternalException (e);
        }
    }
    
}

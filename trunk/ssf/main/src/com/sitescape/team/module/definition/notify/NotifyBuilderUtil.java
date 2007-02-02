package com.sitescape.team.module.definition.notify;

import java.util.Map;

import org.dom4j.Element;
import org.dom4j.DocumentHelper;
import com.sitescape.team.InternalException;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.util.ReflectHelper;

/**
 *
 * @author Jong Kim
 */
public class NotifyBuilderUtil {

    public static void buildElement(Element parent, Notify notifyDef, DefinableEntity entity, String dataElemName, 
    			String fieldBuilderClassName, Map args) {
        try {
            Class fieldBuilderClass = ReflectHelper.classForName(fieldBuilderClassName);
            NotifyBuilder fieldBuilder = (NotifyBuilder) fieldBuilderClass.newInstance();
            Element element = DocumentHelper.createElement("attribute");
            if (fieldBuilder.buildElement(element, entity, notifyDef, dataElemName, args))
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

package com.sitescape.ef.module.definition.notify;

import java.util.Map;

import org.dom4j.Element;
import org.dom4j.DocumentHelper;
import com.sitescape.ef.InternalException;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.util.ReflectHelper;

/**
 *
 * @author Jong Kim
 */
public class NotifyBuilderUtil {

    public static void buildElement(Element parent, Notify notifyDef, Entry entry, String dataElemName, 
    			String fieldBuilderClassName, Map args) {
        try {
            Class fieldBuilderClass = ReflectHelper.classForName(fieldBuilderClassName);
            NotifyBuilder fieldBuilder = (NotifyBuilder) fieldBuilderClass.newInstance();
            Element element = DocumentHelper.createElement("attribute");
            if (fieldBuilder.buildElement(element, entry, notifyDef, dataElemName, args))
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

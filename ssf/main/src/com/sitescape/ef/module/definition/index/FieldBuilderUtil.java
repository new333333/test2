package com.sitescape.ef.module.definition.index;

import java.util.Map;

import org.apache.lucene.document.Field;

import com.sitescape.ef.InternalException;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.util.ReflectHelper;

/**
 *
 * @author Jong Kim
 */
public class FieldBuilderUtil {
    
    public static Field[] buildField(Entry entry, String dataElemName, String fieldBuilderClassName, Map args) {
        try {
            Class fieldBuilderClass = ReflectHelper.classForName(fieldBuilderClassName);
            FieldBuilder fieldBuilder = (FieldBuilder) fieldBuilderClass.newInstance();
            return fieldBuilder.buildField(entry, dataElemName, args);
        } catch (ClassNotFoundException e) {
            throw new InternalException (e);
        } catch (InstantiationException e) {
            throw new InternalException (e);
        } catch (IllegalAccessException e) {
            throw new InternalException (e);
        }
    }
    
}

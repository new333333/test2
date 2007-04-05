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
package com.sitescape.team.module.definition.index;

import java.util.Map;

import org.apache.lucene.document.Field;

import com.sitescape.team.InternalException;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.util.ReflectHelper;

/**
 *
 * @author Jong Kim
 */
public class FieldBuilderUtil {
    
    public static Field[] buildField(DefinableEntity entity, String dataElemName, String fieldBuilderClassName, Map args) {
        try {
            Class fieldBuilderClass = ReflectHelper.classForName(fieldBuilderClassName);
            FieldBuilder fieldBuilder = (FieldBuilder) fieldBuilderClass.newInstance();
            return fieldBuilder.buildField(entity, dataElemName, args);
        } catch (ClassNotFoundException e) {
            throw new InternalException (e);
        } catch (InstantiationException e) {
            throw new InternalException (e);
        } catch (IllegalAccessException e) {
            throw new InternalException (e);
        }
    }
    
}

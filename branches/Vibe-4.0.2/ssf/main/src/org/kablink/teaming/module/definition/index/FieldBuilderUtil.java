/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.module.definition.index;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;

import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.util.cache.ClassInstanceCache;
import org.kablink.util.search.Constants;

/**
 * ?
 * 
 * @author ?
 */
public class FieldBuilderUtil {
	private static final Log logger = LogFactory.getLog(FieldBuilderUtil.class);
	
	@SuppressWarnings("unchecked")
	public static Field[] buildField(DefinableEntity entity, String dataElemName, String fieldBuilderClassName, Map args) {
		FieldBuilder fieldBuilder = (FieldBuilder) ClassInstanceCache.getInstance(fieldBuilderClassName);
		try {
			return fieldBuilder.buildField(entity, dataElemName, args);
		}
		catch(ClassCastException e) {
			if(logger.isDebugEnabled())
				logger.warn("Error indexing field (element name=" + dataElemName + ", builder class=" + fieldBuilderClassName + ") on entity (id=" + entity.getId() + ", title=" + entity.getTitle() + ") - Consider repairing the entity", e);
			else 
				logger.warn("Error indexing field (element name=" + dataElemName + ", builder class=" + fieldBuilderClassName + ") on entity (id=" + entity.getId() + ", title=" + entity.getTitle() + ") - Consider repairing the entity");
			return null;
		}
	}    

	/**
	 * Given a Field that contains a numeric value, returns a
	 * NumericField equivalent of it.
	 * 
	 * If the mapping can't be performed, null is returned.
	 * 
	 * @param field
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static NumericField mapBasicFieldToNumericField(Field field) {
		// 4/26/2016 JK (bug 971209)
		if(field == null || Constants.GENERAL_TEXT_FIELD.equals(field.name())) 
			return null;
		
		// Do we have a Field with a value?
		NumericField reply = null;
		String fValue = ((null == field) ? null : field.stringValue());
		if ((null != fValue) && (0 < fValue.length())) {
			try {
				// If we can parse the value as a double...
				Double dValue = Double.parseDouble(fValue);
				
				// ...clone it as a NumericField.
				reply = new NumericField(
					field.name(),
					(field.isStored()   ?
						Field.Store.YES :
						Field.Store.NO),
					field.isIndexed());
				reply.setDoubleValue(dValue.doubleValue());
				reply.setBoost(field.getBoost());
				reply.setOmitTermFreqAndPositions(field.getOmitTermFreqAndPositions());
				reply.setOmitNorms(field.getOmitNorms());
			}
			
			catch (Exception ex) {
				// Ignored!  If we can't parse the value as a double,
				// we'll simply return null.
				reply = null;
			}
		}
		
		// If we get here, reply contains the NumericField equivalent
		// of a Field or is null.  Return it.
		return reply;
	}
}

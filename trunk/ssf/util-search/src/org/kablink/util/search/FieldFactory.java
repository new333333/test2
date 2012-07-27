/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.util.search;

import org.apache.lucene.document.Field;

import org.kablink.util.PropsUtil;

/**
 * @author jong
 *
 */
public class FieldFactory {

	private static final float APPLICATION_FIELD_BOOST_DEFAULT 	= 1.0f;
	private static final float SYSTEM_FIELD_BOOST_DEFAULT 		= 0.1f;

	public static Field createStoredNotAnalyzedNoNorms(String name, String value) {
		Field field = createFieldWithBoost(name, value, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
		field.setOmitTermFreqAndPositions(true);
		return field;
	}

	/**
	 * Create a field holding application data such as title, description, name, telephone number,
	 * etc. that are directly meaningful to application users. Typically this field corresponds
	 * to a data field or attribute that is part of the entry or binder composed by an user.
	 * 
	 * @param name
	 * @param value
	 * @param store
	 * @param index
	 * @return
	 */
	public static Field createApplicationField(String name, String value, Field.Store store, Field.Index index) {
		Field field = new Field(name, value, store, index);
		float boost = PropsUtil.getFloat("lucene.index.field.boost." + name, APPLICATION_FIELD_BOOST_DEFAULT);
		field.setBoost(boost);
		return field;
	}
	
	/**
	 * Create a field holding system data such as ACL, doc type, binder id, library flag, pre-delete flag, etc.
	 * or derived/computed data such as reply count, etc. that only aid with implementation. 
	 * 
	 * @param name
	 * @param value
	 * @param store
	 * @param index
	 * @return
	 */
	public static Field createSystemField(String name, String value, Field.Store store, Field.Index index) {
		Field field = new Field(name, value, store, index);
		float boost = PropsUtil.getFloat("lucene.index.field.boost." + name, SYSTEM_FIELD_BOOST_DEFAULT);
		field.setBoost(boost);
		field.setOmitTermFreqAndPositions(true);
		return field;
	}
		
	private static Field createFieldWithBoost(String name, String value, Field.Store store, Field.Index index) {
		Field field = new Field(name, value, store, index);
		float boost = PropsUtil.getFloat("lucene.index.field.boost." + name, 1.0f);
		field.setBoost(boost);
		return field;
	}
}

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
	
	private static final float FULL_TEXT_FIELD_BOOST_DEFAULT 		= 1.0f;
	
	private static final float NON_FULL_TEXT_FIELD_BOOST_DEFAULT 	= 0.1f;
	
	/**
	 * Create a field to index full-text data such as title, description, file content, etc.
	 * that are plainly meaningful to application users. Typically these fields correspond
	 * directly to data entered by users as they create or modify entries, binders, and files.
	 * This field is always analyzed and indexed. Optionally the value can be stored.
	 * <p>
	 * Use the following guideline when determining whether a field is a full-text field or
	 * non full-text field. If not full-text field, one of the <code>createField*</code> methods
	 * must be used instead.
	 * <p>
	 * 1. The field contains unstructured textual data.<br>
	 * 2. The field is not a single-token field.<br>
	 * 3. The field represents application/business data not system data.<br>
	 * 
	 * @param name
	 * @param value
	 * @param store
	 * @return
	 */
	public static Field createFullTextFieldIndexed(String name, String value, boolean store) {
		Field field = new Field(name, value, (store? Field.Store.YES:Field.Store.NO), Field.Index.ANALYZED);
		float boost = PropsUtil.getFloat("lucene.index.field.boost." + name, FULL_TEXT_FIELD_BOOST_DEFAULT);
		field.setBoost(boost);
		return field;
	}
	
	/**
	 * Create a field that is not used to index full-text data. For full-text indexed field containing
	 * application data, use <code>createFullTextFieldIndexed</code> method instead.
	 * <p>
	 * Most fields in the system belong in this category.
	 * 
	 * @param name
	 * @param value
	 * @param store
	 * @param index
	 * @return
	 */
	public static Field createField(String name, String value, Field.Store store, Field.Index index) {
		return createField(name, value, store, index, true);
	}

	/**
	 * Create a field that is not used to index full-text data. For full-text indexed field containing
	 * application data, use <code>createFullTextFieldIndexed</code> method instead.
	 * <p>
	 * Most fields in the system belong in this category.
	 * 
	 * @param name
	 * @param value
	 * @param store
	 * @param index
	 * @param omitTermFreqAndpositions
	 * @return
	 */
	public static Field createField(String name, String value, Field.Store store, Field.Index index, boolean omitTermFreqAndpositions) {
		Field field = new Field(name, value, store, index);
		float boost = PropsUtil.getFloat("lucene.index.field.boost." + name, NON_FULL_TEXT_FIELD_BOOST_DEFAULT);
		field.setBoost(boost);
		field.setOmitTermFreqAndPositions(omitTermFreqAndpositions);
		return field;
	}

	/**
	 * Convenience method that uses <code>createField</code> method underneath.
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public static Field createFieldStoredNotAnalyzed(String name, String value) {
		return createField(name, value, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
	}

	/**
	 * Convenience method that uses <code>createField</code> method underneath.
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public static Field createFieldStoredNotIndexed(String name, String value) {
		return createField(name, value, Field.Store.YES, Field.Index.NO);
	}

	/**
	 * Convenience method that uses <code>createField</code> method underneath.
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public static Field createFieldNotStoredNotAnalyzed(String name, String value) {
		return createField(name, value, Field.Store.NO, Field.Index.NOT_ANALYZED_NO_NORMS);
	}

}

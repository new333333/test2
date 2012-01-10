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
package org.kablink.teaming.module.definition.index;

import java.util.Map;

import org.apache.lucene.document.Field;
import org.kablink.teaming.domain.DefinableEntity;

public interface FieldBuilder {
    
	/**
	 * Build a Lucene field from the data element.
	 * 
	 * @param entity
	 * @param dataElemName
	 * @param args
	 * @return
	 */
    public Field[] buildField(DefinableEntity entity, String dataElemName, Map args);
    
    /**
     * Get the name of the index/search field for the data element.
     * <p>
     * If this data element does not produce any index field, or the data is broken
     * into multiple fields (hence requiring special treatment), this method returns 
     * <code>null</code>. However, if the data element produces a single primary index 
     * field and one or more peripheral/secondary fields, then this method can return 
     * the name of the primary index field.
     * 
     * @param dataElemName
     * @return
     */
    public String getSearchFieldName(String dataElemName);
    
    /**
     * Get the name of the sort field for the data element.
     * <p>
     * If no sort field exists for this data element indicating that this data element
     * does not support sorting in the index, or if this method is not applicable for 
     * the data element, for example, because this data element maps to multiple fields
     * in the index and the concept of sorting doesn't apply, then this method should 
     * return <code>null</code>.
     * <p>
     * When this method returns non-null value, it may or may not match the regular
     * field name (obtained via <code>getSearchFieldName</code> method). If they are identical,
     * it indicates that the data element has a single index field that serves both
     * indexing and sorting purposes. If different, the data element has a separate
     * sort field. 
     * 
     * @param dataElemName
     * @return
     */
    // This method is specific to each data element instance
    public String getSortFieldName(String dataElemName);
    
    /**
     * Returns Field.Store constant.
     * This method is relevant only if the <code>getSearchFieldName</code> method 
     * returns non-null value.
     * <p>
     * This method is specific to data element type
     * 
     * @return
     */
    public Field.Store getFieldStore();
    
    /**
     * Returns Field.Index constant.
     * This method is relevant only if the <code>getSearchFieldName</code> method 
     * returns non-null value.
     * <p>
     * This method is specific to data element type
     * 
     * @return
     */
    public Field.Index getFieldIndex();
}

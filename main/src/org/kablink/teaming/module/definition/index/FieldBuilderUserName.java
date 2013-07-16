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
import java.util.Set;

import org.apache.lucene.document.Field;

public class FieldBuilderUserName extends AbstractFieldBuilder {

    protected Field[] build(String dataElemName, Set dataElemValue, Map args) {    	
    	// This routine doesn't return anything because user name is indexed as part 
    	// of the default data items (such as creationDate, modificationDate, etc).
    	// Throughout the system, user name receives some special treatment.
    	// See {@link org.kablink.teaming.module.profile.index.ProfileIndexUtils#addName}.
    	return new Field[0];
    }

	@Override
	public String getSearchFieldName(String dataElemName) {
		return null;
	}

	@Override
	public String getSortFieldName(String dataElemName) {
		return null;
	}

	@Override
	public Field.Index getFieldIndex() {
		return Field.Index.NOT_ANALYZED_NO_NORMS;
	}

	@Override
	public Field.Store getFieldStore() {
		return Field.Store.NO;
	}

}

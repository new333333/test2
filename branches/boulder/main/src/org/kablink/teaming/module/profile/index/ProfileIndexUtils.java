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
package org.kablink.teaming.module.profile.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.kablink.teaming.domain.Application;
import org.kablink.teaming.domain.ApplicationGroup;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;

import java.util.ArrayList;
import java.util.Collection;


import static org.kablink.util.search.Constants.*;
/**
 *
 * @author Janet MCcann
 */
public class ProfileIndexUtils {
	   public static void addName(Document doc, User user, boolean fieldsOnly) {
    	//Add the id of the creator (no, not that one...)
        Field docNumField = new Field(LOGINNAME_FIELD, user.getName(), Field.Store.YES, Field.Index.UN_TOKENIZED);
        doc.add(docNumField);
    }    
    public static void addName(Document doc, Group user, boolean fieldsOnly) {
    	//Add the id of the creator (no, not that one...)
        Field docNumField = new Field(GROUPNAME_FIELD, user.getName(), Field.Store.YES, Field.Index.UN_TOKENIZED);
        doc.add(docNumField);
    }      
    public static void addZonName(Document doc, User user, boolean fieldsOnly) {
    	if (Validator.isNotNull(user.getZonName())) {
    		Field docNumField = new Field(ZONNAME_FIELD, user.getZonName(), Field.Store.YES, Field.Index.UN_TOKENIZED);
    		doc.add(docNumField);
    		if (!fieldsOnly) {
    			Field allText = new Field(Constants.ALL_TEXT_FIELD, user.getZonName(), Field.Store.NO, Field.Index.TOKENIZED);
    			doc.add(allText);
    		}
    	}
    }      
    public static void addWorkspaceId(Document doc, User user) {
    	if (user.getWorkspaceId() != null) {
    		Field workspaceIdField = new Field(WORKSPACE_ID_FIELD, user.getWorkspaceId().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
    		doc.add(workspaceIdField);
    	}
    }      
    public static void addEmail(Document doc, User user, boolean fieldsOnly) {
    	String mail = user.getEmailAddress();
    	if (Validator.isNotNull(mail)) {
    		Field docNumField =  new Field(EMAIL_FIELD, mail, Field.Store.YES, Field.Index.UN_TOKENIZED);
    		doc.add(docNumField);
    		if (!fieldsOnly) {
    			Field allText = new Field(Constants.ALL_TEXT_FIELD, mail, Field.Store.NO, Field.Index.TOKENIZED);
               	doc.add(allText);
    		}
    	}
    	mail = user.getTxtEmailAddress();
       	if (Validator.isNotNull(mail)) {
    		Field docNumField =  new Field(EMAIL_TXT_FIELD, mail, Field.Store.YES, Field.Index.UN_TOKENIZED);
    		doc.add(docNumField);
    		if (!fieldsOnly) {
    			Field allText = new Field(Constants.ALL_TEXT_FIELD, mail, Field.Store.NO, Field.Index.TOKENIZED);
               	doc.add(allText);
    		}
    	}
    	mail = user.getMobileEmailAddress();
       	if (Validator.isNotNull(mail)) {
    		Field docNumField =  new Field(EMAIL_MOBILE_FIELD, mail, Field.Store.YES, Field.Index.UN_TOKENIZED);
    		doc.add(docNumField);
    		if (!fieldsOnly) {
    			Field allText = new Field(Constants.ALL_TEXT_FIELD, mail, Field.Store.NO, Field.Index.TOKENIZED);
               	doc.add(allText);
    		}
    	}
    } 
    public static void addReservedId(Document doc, Principal principal, boolean fieldsOnly) {
    	if (Validator.isNotNull(principal.getInternalId())) {
    		Field docNumField =  new Field(RESERVEDID_FIELD, principal.getInternalId(), Field.Store.YES, Field.Index.UN_TOKENIZED);
    		doc.add(docNumField);
    	}
    } 
    public static void addName(Document doc, Application application, boolean fieldsOnly) {
        Field docNumField = new Field(APPLICATION_NAME_FIELD, application.getName(), Field.Store.YES, Field.Index.UN_TOKENIZED);
        doc.add(docNumField);
    }    
    public static void addName(Document doc, ApplicationGroup appGroup, boolean fieldsOnly) {
    	// share the same field name with Group, no good reason to create separate name
        Field docNumField = new Field(GROUPNAME_FIELD, appGroup.getName(), Field.Store.YES, Field.Index.UN_TOKENIZED);
        doc.add(docNumField);
    }      


}

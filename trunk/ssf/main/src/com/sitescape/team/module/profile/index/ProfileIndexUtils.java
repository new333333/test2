/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.module.profile.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import java.util.ArrayList;
import java.util.Collection;

import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.User;
import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.team.util.LongIdUtil;
import com.sitescape.util.Validator;
/**
 *
 * @author Janet MCcann
 */
public class ProfileIndexUtils  {
	   // Defines field names
    public static final String LOGINNAME_FIELD = "_loginName";
    public static final String GROUPNAME_FIELD = "_groupName";
    public static final String FIRSTNAME_FIELD="_firstName";
    public static final String MIDDLENAME_FIELD="_middleName";
    public static final String LASTNAME_FIELD="_lastName";
    public static final String EMAIL_FIELD="_email";
    public static final String ORGANIZATION_FIELD="_org";
    public static final String ZONNAME_FIELD="_zonName";
    public static final String RESERVEDID_FIELD="_reservedId";    
    
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
    			Field allText = new Field(BasicIndexUtils.ALL_TEXT_FIELD, user.getZonName(), Field.Store.NO, Field.Index.TOKENIZED);
    			doc.add(allText);
    		}
    	}
    }      
    public static void addEmail(Document doc, User user, boolean fieldsOnly) {
    	if (Validator.isNotNull(user.getEmailAddress())) {
    		Field docNumField =  new Field(EMAIL_FIELD, user.getEmailAddress(), Field.Store.YES, Field.Index.UN_TOKENIZED);
    		doc.add(docNumField);
    		if (!fieldsOnly) {
    			Field allText = new Field(BasicIndexUtils.ALL_TEXT_FIELD, user.getEmailAddress(), Field.Store.NO, Field.Index.TOKENIZED);
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


}

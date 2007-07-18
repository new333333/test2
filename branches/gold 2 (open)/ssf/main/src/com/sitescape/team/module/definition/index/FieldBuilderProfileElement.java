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

import java.util.Set;
import java.util.Map;

import org.apache.lucene.document.Field;

import com.sitescape.team.module.profile.index.ProfileIndexUtils;

/**
 * Implement here cause not all fields are included on every form
 * @author Jong Kim
 */
public class FieldBuilderProfileElement extends AbstractFieldBuilder {
   protected Field[] build(String dataElemName, Set dataElemValue, Map args) {
    	String val = (String) getFirstElement(dataElemValue);
        val = val.trim();
       
        if (val.length() == 0) {
           return new Field[0];
        }
        //email and zonName are indexed by default so they can be used in folderListing
    	if ("firstName".equals(dataElemName))  {
   	        Field nameField = new Field(ProfileIndexUtils.FIRSTNAME_FIELD, val, Field.Store.YES, Field.Index.TOKENIZED);
   	        return new Field[] {nameField};
    	} else if ("middleName".equals(dataElemName)) {
   	        Field nameField = new Field(ProfileIndexUtils.MIDDLENAME_FIELD, val, Field.Store.YES, Field.Index.TOKENIZED);
  	        return new Field[] {nameField};    		
       	} else if ("lastName".equals(dataElemName)) {
   	        Field nameField = new Field(ProfileIndexUtils.LASTNAME_FIELD, val, Field.Store.YES, Field.Index.TOKENIZED);
  	        return new Field[] {nameField};    		
      	} else if ("organization".equals(dataElemName)) {
   	        Field nameField = new Field(ProfileIndexUtils.ORGANIZATION_FIELD, val, Field.Store.YES, Field.Index.TOKENIZED);
  	        return new Field[] {nameField};    			
    	}
    	return new Field[0];
    }

}

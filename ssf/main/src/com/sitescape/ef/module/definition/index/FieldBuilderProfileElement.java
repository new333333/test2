package com.sitescape.ef.module.definition.index;

import java.util.Set;
import java.util.Map;

import org.apache.lucene.document.Field;
import com.sitescape.ef.module.profile.index.ProfileIndexUtils;

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
    	if ("firstName".equals(dataElemName))  {
   	        Field nameField = new Field(ProfileIndexUtils.FIRSTNAME_FIELD, val, Field.Store.YES, Field.Index.TOKENIZED);
   	        return new Field[] {nameField};
    	} else if ("middleName".equals(dataElemName)) {
   	        Field nameField = new Field(ProfileIndexUtils.MIDDLENAME_FIELD, val, Field.Store.YES, Field.Index.TOKENIZED);
  	        return new Field[] {nameField};    		
       	} else if ("lastName".equals(dataElemName)) {
   	        Field nameField = new Field(ProfileIndexUtils.LASTNAME_FIELD, val, Field.Store.YES, Field.Index.TOKENIZED);
  	        return new Field[] {nameField};    		
      	} else if ("emailAddress".equals(dataElemName)) {
   	        Field nameField = new Field(ProfileIndexUtils.EMAIL_FIELD, val, Field.Store.YES, Field.Index.TOKENIZED);
  	        return new Field[] {nameField};    		
      	} else if ("country".equals(dataElemName)) {
   	        Field nameField = new Field(ProfileIndexUtils.COUNTRY_FIELD, val, Field.Store.YES, Field.Index.TOKENIZED);
  	        return new Field[] {nameField};    		
      	} else if ("organization".equals(dataElemName)) {
   	        Field nameField = new Field(ProfileIndexUtils.ORGANIZATION_FIELD, val, Field.Store.YES, Field.Index.TOKENIZED);
  	        return new Field[] {nameField};    		
      	} else if ("zonName".equals(dataElemName)) {
   	        Field nameField = new Field(ProfileIndexUtils.ZONNAME_FIELD, val, Field.Store.YES, Field.Index.TOKENIZED);
  	        return new Field[] {nameField};    		
    	} else if ("homepage".equals(dataElemName)) {
	        Field nameField = new Field(ProfileIndexUtils.HOMEPAGE_FIELD, val, Field.Store.YES, Field.Index.TOKENIZED);
	        return new Field[] {nameField};    		
    	}
    	return new Field[0];
    }

}

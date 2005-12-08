package com.sitescape.ef.module.profile.index;


import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Group;
import com.sitescape.util.Validator;
/**
 *
 * @author Janet MCcann
 */
public class IndexUtils  {
    
    // Defines field names
    public static final String USERNAME_FIELD = "_userName";
    public static final String GROUPNAME_FIELD = "_groupName";
    public static final String FIRSTNAME_FIELD="_firstName";
    public static final String MIDDLENAME_FIELD="_middleName";
    public static final String LASTNAME_FIELD="_lastName";
    public static final String EMAIL_FIELD="_email";
   
    


    public static void addName(Document doc, User user) {
    	//Add the id of the creator (no, not that one...)
        Field docNumField = Field.Keyword(USERNAME_FIELD, user.getName());
        doc.add(docNumField);
    }    
    public static void addName(Document doc, Group user) {
    	//Add the id of the creator (no, not that one...)
        Field docNumField = Field.Keyword(GROUPNAME_FIELD, user.getName());
        doc.add(docNumField);
    }      
    public static void addFirstName(Document doc, User user) {
    	String name = user.getFirstName();
    	if (!Validator.isNull(name)) {
    		name = name.trim();
    		if (name.length() != 0) {
       	        Field nameField = new Field(FIRSTNAME_FIELD, name, true, true, true);
       	        doc.add(nameField);
    		}
    	}
    	
    }
    public static void addMiddleName(Document doc, User user) {
    	String name = user.getMiddleName();
    	if (!Validator.isNull(name)) {
    		name = name.trim();
    		if (name.length() != 0) {
       	        Field nameField = new Field(MIDDLENAME_FIELD, name, true, true, true);
       	        doc.add(nameField);
    		}
    	}
    	
    }
    public static void addLastName(Document doc, User user) {
    	String name = user.getLastName();
    	if (!Validator.isNull(name)) {
    		name = name.trim();
    		if (name.length() != 0) {
       	        Field nameField = new Field(LASTNAME_FIELD, name, true, true, true);
       	        doc.add(nameField);
    		}
    	}
    	
    }
    public static void addEmailAddress(Document doc, User user) {
    	String name = user.getEmailAddress();
    	if (!Validator.isNull(name)) {
    		name = name.trim();
    		if (name.length() != 0) {
       	        Field nameField = new Field(EMAIL_FIELD, name, true, true, true);
       	        doc.add(nameField);
    		}
    	}
    	
    }    
}

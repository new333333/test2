package com.sitescape.ef.module.profile.index;


import java.util.Iterator;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.search.BasicIndexUtils;
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
    public static final String COUNTRY_FIELD="_country";
    public static final String ORGANIZATION_FIELD="_org";
    public static final String HOMEPAGE_FIELD="_homepage";
    public static final String ZONNAME_FIELD="_zonName";
    public static final String MEMBEROF_FIELD="_memberOf";
    
    
    public static void addName(Document doc, User user) {
    	//Add the id of the creator (no, not that one...)
        Field docNumField = new Field(LOGINNAME_FIELD, user.getName(), Field.Store.YES, Field.Index.UN_TOKENIZED);
        doc.add(docNumField);
    }    
    public static void addName(Document doc, Group user) {
    	//Add the id of the creator (no, not that one...)
        Field docNumField = new Field(GROUPNAME_FIELD, user.getName(), Field.Store.YES, Field.Index.UN_TOKENIZED);
        doc.add(docNumField);
    }      
    public static void addZonName(Document doc, User user) {
    	if (Validator.isNotNull(user.getZonName())) {
    		Field docNumField = new Field(ZONNAME_FIELD, user.getZonName(), Field.Store.YES, Field.Index.UN_TOKENIZED);
    		doc.add(docNumField);
    	}
    }      
    public static void addEmail(Document doc, User user) {
    	if (Validator.isNotNull(user.getEmailAddress())) {
    		Field docNumField =  new Field(EMAIL_FIELD, user.getEmailAddress(), Field.Store.YES, Field.Index.UN_TOKENIZED);
    		doc.add(docNumField);
    	}
    } 
    public static void addMemberOf(Document doc, User user) {
        Field racField;
        // I'm not sure if putting together a long string value is more
        // efficient than processing multiple short strings... We will see.
        StringBuffer pIds = new StringBuffer();
   		for (Iterator i = user.getMemberOf().iterator(); i.hasNext();) {
   			Group g = (Group)i.next();
    		pIds.append(g.getId().toString()).append(" ");
    	}
        racField = new Field(MEMBEROF_FIELD, pIds.toString(), Field.Store.YES, Field.Index.TOKENIZED);      
        doc.add(racField);    	
    }
}

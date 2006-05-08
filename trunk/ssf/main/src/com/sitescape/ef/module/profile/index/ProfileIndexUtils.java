package com.sitescape.ef.module.profile.index;


import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Group;
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
    
    
    public static void addName(Document doc, User user) {
    	//Add the id of the creator (no, not that one...)
        Field docNumField = Field.Keyword(LOGINNAME_FIELD, user.getName());
        doc.add(docNumField);
    }    
    public static void addName(Document doc, Group user) {
    	//Add the id of the creator (no, not that one...)
        Field docNumField = Field.Keyword(GROUPNAME_FIELD, user.getName());
        doc.add(docNumField);
    }      
   
}

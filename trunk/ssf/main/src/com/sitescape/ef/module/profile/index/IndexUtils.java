package com.sitescape.ef.module.profile.index;


import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Group;
/**
 *
 * @author Janet MCcann
 */
public class IndexUtils  {
    
    // Defines field names
    public static final String USERNAME_FIELD = "_userName";
    public static final String GROUPNAME_FIELD = "_groupName";
   
    


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
}

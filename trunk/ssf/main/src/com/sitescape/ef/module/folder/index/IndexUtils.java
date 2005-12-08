package com.sitescape.ef.module.folder.index;

import java.util.Iterator;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.sitescape.ef.domain.AclControlledEntry;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.security.acl.AccessType;
import com.sitescape.ef.security.acl.AclManager;

/**
 *
 * @author Jong Kim
 */
public class IndexUtils {
    
    // Defines field names
    
    public static final String ALL_TEXT_FIELD = "_allText";
    public static final String CREATION_DATE_FIELD = "_creationDate";
    public static final String READ_ACL_FIELD = "_readAcl";
    public static final String COMMAND_DEFINITION_FIELD = "_commandDef";
    public static final String TITLE_FIELD = "_title";
    public static final String TITLE1_FIELD = "_title1";
    public static final String DESC_FIELD = "_desc";
    
    // Defines field values
    public static final String READ_ACL_ALL = "all";
    
    public static void addAllText(Document doc, String text) {
        doc.add(allTextField(text));
    }
    
    public static Field allTextField(String text) {
        return new Field(ALL_TEXT_FIELD, text, false, true, true);
    }
    
    public static void addCreationDate(Document doc, Entry entry) {
        // Add creation-date field
        Field creationDateField = Field.Keyword(CREATION_DATE_FIELD, entry.getCreation().getDate());
        doc.add(creationDateField);
    }
    
    public static void addCommandDefinition(Document doc, Entry entry) {
        Field cdefField = Field.Keyword(COMMAND_DEFINITION_FIELD, entry.getEntryDef().getId());
        doc.add(cdefField);
    }
    
    public static void addReadAcls(Document doc, Folder folder, Entry entry, AclManager aclManager) {
        // Add ACL field. We only need to index ACLs for read access. 
        Field racField;
        if(entry instanceof AclControlledEntry) {
	        StringBuffer pIds = new StringBuffer();
	        Set readMemberIds = aclManager.getMembers(folder, (AclControlledEntry) entry, AccessType.READ);
	        for(Iterator i = readMemberIds.iterator(); i.hasNext();) {
	            pIds.append(i.next()).append(" ");
	        }
	        // I'm not sure if putting together a long string value is more
	        // efficient than processing multiple short strings... We will see.
	        racField = new Field(READ_ACL_FIELD, pIds.toString(), false, true, true);
        }
        else {
            racField = new Field(READ_ACL_FIELD, READ_ACL_ALL, false, true, false);
        }
        
        doc.add(racField);
    }
}

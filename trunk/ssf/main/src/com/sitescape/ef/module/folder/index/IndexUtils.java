package com.sitescape.ef.module.folder.index;

import java.util.Iterator;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.sitescape.ef.domain.AclControlledEntry;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.security.acl.AccessType;
import com.sitescape.ef.security.acl.AclManager;
/**
 *
 * @author Jong Kim
 */
public class IndexUtils  {
    
    // Defines field names
    public static final String DOCNUMBER_FIELD = "_docNum";
    public static final String PARENT_FOLDERID_FIELD = "_parentFolderId";
    public static final String TOP_FOLDERID_FIELD = "_topFolderId";
   
     


    public static void addDocNumber(Document doc, FolderEntry entry) {
    	//Add the id of the creator (no, not that one...)
        Field docNumField = Field.Keyword(DOCNUMBER_FIELD, entry.getDocNumber());
        doc.add(docNumField);
    }    

    public static void addFolderId(Document doc, Folder folder) {
        
    	//Add the folder parentage to the document in the index
        Folder parentFolder = folder.getParentFolder();
        while (parentFolder != null) {
        	Field parentFolderField = Field.Keyword(PARENT_FOLDERID_FIELD, parentFolder.getId().toString());
            doc.add(parentFolderField);
        	parentFolder = parentFolder.getParentFolder();
        }

    	//Add the top folder id to the document in the index
        Folder topFolder = folder.getTopFolder();
        if (topFolder == null) topFolder = folder;
        Field topFolderField = Field.Keyword(TOP_FOLDERID_FIELD, topFolder.getId().toString());
        doc.add(topFolderField);
}   
    
 
    
}

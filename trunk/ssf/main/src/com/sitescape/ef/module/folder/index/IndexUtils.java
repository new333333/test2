package com.sitescape.ef.module.folder.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Folder;
/**
 *
 * @author Jong Kim
 */
public class IndexUtils  {
    
    // Defines field names
    public static final String DOCNUMBER_FIELD = "_docNum";
    public static final String SORTNUMBER_FIELD = "_sortNum";
    public static final String PARENT_FOLDERID_FIELD = "_parentFolderId";
    public static final String TOP_FOLDERID_FIELD = "_topFolderId";
    public static final String RESERVEDBYID_FIELD = "_reservedById";
   
     


    public static void addDocNumber(Document doc, FolderEntry entry) {
    	//Add the id of the creator (no, not that one...)
        Field docNumField = new Field(DOCNUMBER_FIELD, entry.getDocNumber(), Field.Store.YES, Field.Index.UN_TOKENIZED);
        doc.add(docNumField);
    }    
    public static void addSortNumber(Document doc, FolderEntry entry) {
    	//Add the id of the creator (no, not that one...)
        Field docNumField = new Field(SORTNUMBER_FIELD, entry.getHKey().getSortKey(), Field.Store.YES, Field.Index.UN_TOKENIZED);
        doc.add(docNumField);
    } 
    public static void addFolderId(Document doc, Folder folder) {
        
    	//Add the folder parentage to the document in the index
        Folder parentFolder = folder.getParentFolder();
        while (parentFolder != null) {
        	Field parentFolderField = new Field(PARENT_FOLDERID_FIELD, parentFolder.getId().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
            doc.add(parentFolderField);
        	parentFolder = parentFolder.getParentFolder();
        }

    	//Add the top folder id to the document in the index
        Folder topFolder = folder.getTopFolder();
        if (topFolder == null) topFolder = folder;
        Field topFolderField = new Field(TOP_FOLDERID_FIELD, topFolder.getId().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
        doc.add(topFolderField);
    }   
    
    public static void addReservedByPrincipalId(Document doc, FolderEntry entry) {
    	//Add the id of the reserver
        if (entry.getReservation() != null && entry.getReservation().getPrincipal() != null) {
        	Field reservedByIdField = new Field(RESERVEDBYID_FIELD, entry.getReservation().getPrincipal().getId().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
        	doc.add(reservedByIdField);
        }
    }   

 
    
}

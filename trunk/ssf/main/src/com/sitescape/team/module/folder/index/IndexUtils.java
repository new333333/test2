package com.sitescape.team.module.folder.index;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.module.shared.EntityIndexUtils;
/**
 *
 * @author Jong Kim
 */
public class IndexUtils  {
    
    // Defines field names
    public static final String DOCNUMBER_FIELD = "_docNum";
    public static final String SORTNUMBER_FIELD = "_sortNum";
    public static final String TOP_FOLDERID_FIELD = "_topFolderId";
    public static final String RESERVEDBYID_FIELD = "_reservedById";
    public static final String LASTACTIVITY_FIELD = "_lastActivity";
    public static final String LASTACTIVITY_DAY_FIELD = "_lastActivityDay";
    public static final String LASTACTIVITY_YEAR_MONTH_FIELD = "_lastActivityYearMonth";
    public static final String LASTACTIVITY_YEAR_FIELD = "_lastActivityYear";
      
    //only index for top leve; entries
    public static void addLastActivityDate(Document doc, FolderEntry entry) {
    	// Add modification-date field
    	if (entry.getLastActivity() != null ) {
    		Date modDate = entry.getLastActivity();
        	Field modificationDateField = new Field(LASTACTIVITY_FIELD, DateTools.dateToString(modDate,DateTools.Resolution.SECOND), Field.Store.YES, Field.Index.UN_TOKENIZED);
        	doc.add(modificationDateField);        
            // index the YYYYMMDD string
            String dayString = EntityIndexUtils.formatDayString(modDate);
            Field modificationDayField = new Field(LASTACTIVITY_DAY_FIELD, dayString, Field.Store.YES, Field.Index.UN_TOKENIZED);
            doc.add(modificationDayField);
            // index the YYYYMM string
            String yearMonthString = dayString.substring(0,6);
            Field modificationYearMonthField = new Field(LASTACTIVITY_YEAR_MONTH_FIELD, yearMonthString, Field.Store.YES, Field.Index.UN_TOKENIZED);
            doc.add(modificationYearMonthField);
            // index the YYYY string
            String yearString = dayString.substring(0,4);
            Field modificationYearField = new Field(LASTACTIVITY_YEAR_FIELD, yearString, Field.Store.YES, Field.Index.UN_TOKENIZED);
            doc.add(modificationYearField);   	}
    } 


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
    public static Set getPrincipalsFromSearch(List<Map> searchResults) {
    	Set ids = EntityIndexUtils.getPrincipalsFromSearch(searchResults);
    	//add in folder ids
    	for (Map entry: searchResults) {
    	       if (entry.get(IndexUtils.RESERVEDBYID_FIELD) != null) 
           		try {ids.add(new Long((String)entry.get(IndexUtils.RESERVEDBYID_FIELD)));
   	    	} catch (Exception ex) {};
    	}
    	return ids;
 
    }

 
    
}

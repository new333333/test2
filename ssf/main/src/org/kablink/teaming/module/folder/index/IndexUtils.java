/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package org.kablink.teaming.module.folder.index;

import java.util.Date;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.module.shared.EntityIndexUtils;

import static org.kablink.util.search.Constants.*;
/**
 *
 * @author Jong Kim
 */
public class IndexUtils  {
    
    //only index for top leve; entries
    public static void addLastActivityDate(Document doc, FolderEntry entry, boolean fieldsOnly) {
    	// Add modification-date field
		Date modDate = entry.getLastActivity();
		if (modDate == null) modDate= entry.getModification().getDate();
		if (modDate == null) modDate= entry.getCreation().getDate();
    	if (modDate != null ) {
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


    public static void addDocNumber(Document doc, FolderEntry entry, boolean fieldsOnly) {
    	//Add the id of the creator (no, not that one...)
        Field docNumField = new Field(DOCNUMBER_FIELD, entry.getDocNumber(), Field.Store.YES, Field.Index.UN_TOKENIZED);
        doc.add(docNumField);
    }    
    public static void addTotalReplyCount(Document doc, FolderEntry entry, boolean fieldsOnly) {
    	//Add the id of the creator (no, not that one...)
        Field countNumField = new Field(TOTALREPLYCOUNT_FIELD, Integer.toString(entry.getTotalReplyCount()), Field.Store.YES, Field.Index.UN_TOKENIZED);
        doc.add(countNumField);
    }    
    public static void addSortNumber(Document doc, FolderEntry entry, boolean fieldsOnly) {
    	//Add the id of the creator (no, not that one...)
        Field docNumField = new Field(SORTNUMBER_FIELD, entry.getHKey().getSortKey(), Field.Store.YES, Field.Index.UN_TOKENIZED);
        doc.add(docNumField);
    } 
    public static void addFolderId(Document doc, Folder folder, boolean fieldsOnly) {
        
    	//Add the top folder id to the document in the index
        Folder topFolder = folder.getTopFolder();
        if (topFolder == null) topFolder = folder;
        Field topFolderField = new Field(TOP_FOLDERID_FIELD, topFolder.getId().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
        doc.add(topFolderField);
    }   
    
    public static void addReservedByPrincipalId(Document doc, FolderEntry entry, boolean fieldsOnly) {
    	//Add the id of the reserver
        if (entry.getReservation() != null && entry.getReservation().getPrincipal() != null) {
        	Field reservedByIdField = new Field(RESERVEDBYID_FIELD, entry.getReservation().getPrincipal().getId().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
        	doc.add(reservedByIdField);
        }
    }   
    
}

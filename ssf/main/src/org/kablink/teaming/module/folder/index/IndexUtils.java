/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.module.folder.index;

import java.util.Date;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.module.shared.EntityIndexUtils;
import org.kablink.teaming.util.SPropsUtil;

import static org.kablink.util.search.Constants.*;
/**
 *
 * @author Jong Kim
 */
public class IndexUtils  {
    
	private static String totalReplyCountFormat;
	
    //only index for top leve; entries
    public static void addLastActivityDate(Document doc, FolderEntry entry, boolean fieldsOnly) {
    	// Add modification-date field
		Date modDate = entry.getLastActivity();
		if (modDate == null) modDate= entry.getModification().getDate();
		if (modDate == null) modDate= entry.getCreation().getDate();
    	if (modDate != null ) {
        	Field modificationDateField = new Field(LASTACTIVITY_FIELD, DateTools.dateToString(modDate,DateTools.Resolution.SECOND), Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
        	doc.add(modificationDateField);        
            // index the YYYYMMDD string
            String dayString = EntityIndexUtils.formatDayString(modDate);
            Field modificationDayField = new Field(LASTACTIVITY_DAY_FIELD, dayString, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
            doc.add(modificationDayField);
            // index the YYYYMM string
            String yearMonthString = dayString.substring(0,6);
            Field modificationYearMonthField = new Field(LASTACTIVITY_YEAR_MONTH_FIELD, yearMonthString, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
            doc.add(modificationYearMonthField);
            // index the YYYY string
            String yearString = dayString.substring(0,4);
            Field modificationYearField = new Field(LASTACTIVITY_YEAR_FIELD, yearString, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
            doc.add(modificationYearField);   	}
    } 


    public static void addDocNumber(Document doc, FolderEntry entry, boolean fieldsOnly) {
    	//Add the id of the creator (no, not that one...)
        Field docNumField = new Field(DOCNUMBER_FIELD, entry.getDocNumber(), Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
        doc.add(docNumField);
    }    
    public static void addTotalReplyCount(Document doc, FolderEntry entry, boolean fieldsOnly) {
    	//Add the id of the creator (no, not that one...)
    	//NumericField countNumField = new NumericField(TOTALREPLYCOUNT_FIELD, Field.Store.YES, true);
    	//countNumField.setIntValue(entry.getTotalReplyCount());
    	Field countNumField1 = new Field(TOTALREPLYCOUNT_FIELD, getTotalReplyCountPadded(entry.getTotalReplyCount()), Field.Store.NO, Field.Index.NOT_ANALYZED_NO_NORMS);
    	Field countNumField2 = new Field(TOTALREPLYCOUNT_FIELD, String.valueOf(entry.getTotalReplyCount()), Field.Store.YES, Field.Index.NO);
        doc.add(countNumField1);
        doc.add(countNumField2);
    }    
    public static void addSortNumber(Document doc, FolderEntry entry, boolean fieldsOnly) {
    	//Add the id of the creator (no, not that one...)
        Field docNumField = new Field(SORTNUMBER_FIELD, entry.getHKey().getSortKey(), Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
        doc.add(docNumField);
    } 
    public static void addFolderId(Document doc, Folder folder, boolean fieldsOnly) {
        
    	//Add the top folder id to the document in the index
        Folder topFolder = folder.getTopFolder();
        if (topFolder == null) topFolder = folder;
        Field topFolderField = new Field(TOP_FOLDERID_FIELD, topFolder.getId().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
        doc.add(topFolderField);
    }   
    
    public static void addReservedByPrincipalId(Document doc, FolderEntry entry, boolean fieldsOnly) {
    	//Add the id of the reserver
        if (entry.getReservation() != null && entry.getReservation().getPrincipal() != null) {
        	Field reservedByIdField = new Field(RESERVEDBYID_FIELD, entry.getReservation().getPrincipal().getId().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
        	doc.add(reservedByIdField);
        }
    }   
    
    private static String getTotalReplyCountPadded(int count) {
    	return String.format(getTotalReplyCountFormat(), count);
    }
    
    private static String getTotalReplyCountFormat() {
    	if(totalReplyCountFormat == null) {
    		int digits = SPropsUtil.getInt("totalreplycount.field.digits", 4);
    		totalReplyCountFormat = "%0" + digits + "d";
    	}
    	return totalReplyCountFormat;
    }
}

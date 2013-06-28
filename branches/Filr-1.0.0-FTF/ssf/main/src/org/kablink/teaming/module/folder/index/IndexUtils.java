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
import org.kablink.util.search.FieldFactory;

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
        	Field modificationDateField = FieldFactory.createFieldStoredNotAnalyzed(LASTACTIVITY_FIELD, DateTools.dateToString(modDate,DateTools.Resolution.SECOND));
        	doc.add(modificationDateField);        
            // index the YYYYMMDD string
            String dayString = EntityIndexUtils.formatDayString(modDate);
            Field modificationDayField = FieldFactory.createFieldStoredNotAnalyzed(LASTACTIVITY_DAY_FIELD, dayString);
            doc.add(modificationDayField);
            // index the YYYYMM string
            String yearMonthString = dayString.substring(0,6);
            Field modificationYearMonthField = FieldFactory.createFieldStoredNotAnalyzed(LASTACTIVITY_YEAR_MONTH_FIELD, yearMonthString);
            doc.add(modificationYearMonthField);
            // index the YYYY string
            String yearString = dayString.substring(0,4);
            Field modificationYearField = FieldFactory.createFieldStoredNotAnalyzed(LASTACTIVITY_YEAR_FIELD, yearString);
            doc.add(modificationYearField);   	}
    } 


    public static void addDocNumber(Document doc, FolderEntry entry, boolean fieldsOnly) {
    	//Add the id of the creator (no, not that one...)
        Field docNumField = FieldFactory.createFieldStoredNotAnalyzed(DOCNUMBER_FIELD, entry.getDocNumber());
        doc.add(docNumField);
    }    
    public static void addTotalReplyCount(Document doc, FolderEntry entry, boolean fieldsOnly) {
    	//Add the id of the creator (no, not that one...)
    	//NumericField countNumField = new NumericField(TOTALREPLYCOUNT_FIELD, Field.Store.YES, true);
    	//countNumField.setIntValue(entry.getTotalReplyCount());
    	Field countNumField1 = FieldFactory.createFieldNotStoredNotAnalyzed(TOTALREPLYCOUNT_FIELD, getTotalReplyCountPadded(entry.getTotalReplyCount()));
    	Field countNumField2 = FieldFactory.createField(TOTALREPLYCOUNT_FIELD, String.valueOf(entry.getTotalReplyCount()), Field.Store.YES, Field.Index.NO);
        doc.add(countNumField1);
        doc.add(countNumField2);
    }    
    public static void addSortNumber(Document doc, FolderEntry entry, boolean fieldsOnly) {
    	//Add the id of the creator (no, not that one...)
        Field docNumField = FieldFactory.createFieldStoredNotAnalyzed(SORTNUMBER_FIELD, entry.getHKey().getSortKey());
        doc.add(docNumField);
    } 
    public static void addFolderId(Document doc, Folder folder, boolean fieldsOnly) {
        
    	//Add the top folder id to the document in the index
        Folder topFolder = folder.getTopFolder();
        if (topFolder == null) topFolder = folder;
        Field topFolderField = FieldFactory.createFieldStoredNotAnalyzed(TOP_FOLDERID_FIELD, topFolder.getId().toString());
        doc.add(topFolderField);
    }   
    
    public static void addReservedByPrincipalId(Document doc, FolderEntry entry, boolean fieldsOnly) {
    	//Add the id of the reserver
        if (entry.getReservation() != null && entry.getReservation().getPrincipal() != null) {
        	Field reservedByIdField = FieldFactory.createFieldStoredNotAnalyzed(RESERVEDBYID_FIELD, entry.getReservation().getPrincipal().getId().toString());
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

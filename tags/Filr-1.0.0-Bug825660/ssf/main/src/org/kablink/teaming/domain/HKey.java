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
/*
 * Created on Jan 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.kablink.teaming.domain;
import org.kablink.teaming.InternalException;
import org.kablink.util.StringUtil;

/**
 * @author Janet McCann
 * The first 15 characters of a sortKey are the B36 encoded folderId.  For entries this provides
 * sorting by folder, which should result in better lookups.  
 */
public class HKey {
    public static final  String B10_TO_36 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private String sortKey;
    private String position;
    private int level=1;
    
    public HKey() { 
    }
    public HKey(String sortKey) {
        int length = sortKey.length();
        
        if (length == 15) { 
            level=0;
        } else {
            level = (length-15)/5;
        }
        this.sortKey = sortKey;
        
    }
    
 
    public HKey(HKey parent, int eNum) {
        level = parent.getLevel()+1;
        setKey(parent.getSortKey(), eNum);
    }
    public HKey(String parent, int eNum) {
        level = (parent.length()/5)-3+1;
        setKey(parent, eNum);
    }
    protected void setKey(String parent, int eNum) {
        StringBuffer key = new StringBuffer(100);

        // Base 36 conversion 
        while (eNum > 0) {
            key.insert(0,B10_TO_36.charAt(eNum%36));
            eNum = eNum/36;
        }
        int length = key.length();
        for (int i=length; i<5; ++i) {
            key.insert(0,"0");            
        }
        key.insert(0, parent);
        sortKey = key.toString();       
        
    }

    /**
     * @hibernate.property column="level"
     * @return
     */
    public int getLevel() {
        return this.level;
    }
    protected void setLevel(int level) {
        this.level = level;
    }
    /**
     * @hibernate.property length="255" column="sortKey"
     * @return
     */
    public String getSortKey() {
        return this.sortKey;
    }
    protected void setSortKey(String sortKey) {
        this.sortKey = sortKey;
        position=null;       
    }
    /*
     * Return array of ancestor sortKeys for use in queries.
     */
    public String[] getAncestorKeys() {
        if (level <= 1) return null;
        String[] result = new String[level-1];
        int endIndex=20;
        int pos=0;
        while (endIndex < sortKey.length()) {
            result[pos++] = sortKey.substring(0,endIndex);
            endIndex += 5;
           
        }
        return result;
    }
    public String getEntryNumber() {
        if (position == null) {
            //Base 10 representation as a dotted string
            StringBuffer dottyString = new StringBuffer(100);
            //Skip folder root which is first 15 characters
            dottyString.append(B36To10(sortKey.substring(15,20)));
            
            for (int i=20; i<sortKey.length(); i+=5) {
                dottyString.append(".");
                dottyString.append(B36To10(sortKey.substring(i,i+5)));
            }
            position = dottyString.toString();
        }
        return position;
    }
    private String B36To10(String B36) {
         int value=0;
         for (int i=0; i<B36.length(); ++i) {
             value = (value*36) + B10_TO_36.indexOf(B36.charAt(i));
         }
         return String.valueOf(value);
             
    }
    public String getRelativeNumber(int level) {
    	String [] levels = StringUtil.split(getEntryNumber(), ".");
    	if (level > levels.length) return null;
    	return levels[level-1];
    }
    public int getLastNumber() {
    	String num = B36To10(sortKey.substring(sortKey.length()-5, sortKey.length()));
    	return Integer.parseInt(num);
     }
    /*
     * Each binder has a unique root sort key that it uses to
     * generate sortkeys for its child docshareentries.
     * The root is generated from the folder id.
     */
    public static String generateRootKey(Long id) {
    	//the maximum long value encoded in base 36 will fit in 15 bytes
    	StringBuffer sortKey = new StringBuffer(15);
    	if (id == null) throw new InternalException("Entity must be saved");
    	long start = id.longValue();
    	
        // Base 36 conversion 
        while (start > 0) {
            sortKey.insert(0,HKey.B10_TO_36.charAt((int)(start%36)));
            start = start/36;
        }
        for (int i=sortKey.length(); i<15; ++i) {
            sortKey.insert(0,"0");            
        }
        
        return sortKey.toString();
    }
    public static String getSortKeyFromEntryNumber(String rootKey, String dottyString) {
        StringBuffer sortKey = new StringBuffer(100);
        sortKey.append(rootKey);
        String [] levels = StringUtil.split(dottyString, ".");
    	for (int cnt=0; cnt<levels.length; ++cnt) {
    		StringBuffer key = new StringBuffer(5);
    		long start = Long.valueOf(levels[cnt]);
            // Base 36 conversion 
            while (start > 0) {
            	key.insert(0,HKey.B10_TO_36.charAt((int)(start%36)));
                start = start/36;
            }
            for (int i=key.length(); i<5; ++i) {
            	key.insert(0,"0");            
            }
            sortKey.append(key.toString());
    	}
    	return sortKey.toString();
    }

  
}

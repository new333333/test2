/*
 * Created on Jan 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.domain;
import com.sitescape.util.StringUtil;
/**
 * @author Janet McCann
 * The first 15 characters of a sortKey are the B36 encoded folderId.  For entries this provides
 * sorting by folder, which should result in better lookups.  For folders the first 15 characters 
 * represent the topFolderId
 */
public class HKey {
    public static final  String B10_TO_36 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private int lastDescendantNumber=0;
    private String sortKey;
    private String position;
    private int level=1;
    
    public HKey() { 
    }
    public HKey(String sortKey) {
        int length = sortKey.length();
        
        if (length == 15) { 
            level=0;
        } else if (length == 20) {
            level=1;
        } else {
            level = (length-20)/4;
        }
        this.sortKey = sortKey;
        position = B36To10(sortKey); 
        
    }
    
    public HKey(HKey parent) {
        level = parent.level+1;
        
        StringBuffer key = new StringBuffer(100);
        int start = ++parent.lastDescendantNumber;

        // Base 36 conversion 
        while (start > 0) {
            key.insert(0,B10_TO_36.charAt(start%36));
            start = start/36;
        }
        int length = key.length();
        int fillLength=5;
        if (level>1) {
            fillLength=4;
        } 
        for (int i=length; i<fillLength; ++i) {
            key.insert(0,"0");            
        }
        key.insert(0, parent.sortKey);
        sortKey = key.toString();
        
        
    }
    public HKey(HKey parent, int eNum) {
        level = parent.level+1;
        
        StringBuffer key = new StringBuffer(100);

        // Base 36 conversion 
        while (eNum > 0) {
            key.insert(0,B10_TO_36.charAt(eNum%36));
            eNum = eNum/36;
        }
        int length = key.length();
        int fillLength=5;
        if (level>1) {
            fillLength=4;
        } 
        for (int i=length; i<fillLength; ++i) {
            key.insert(0,"0");            
        }
        key.insert(0, parent.sortKey);
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
     * @hibernate.property length="512" column="sortKey"
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
            endIndex += 4;
           
        }
        return result;
    }
    public String getEntryNumber() {
        if (position == null) {
            //Base 10 representation as a dotted string
            StringBuffer dottyString = new StringBuffer(100);
            //Skip folder root which is first 15 characters
            dottyString.append(B36To10(sortKey.substring(15,20)));
            
            for (int i=20; i<sortKey.length(); i+=4) {
                dottyString.append(".");
                dottyString.append(B36To10(sortKey.substring(i,i+4)));
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
  
}

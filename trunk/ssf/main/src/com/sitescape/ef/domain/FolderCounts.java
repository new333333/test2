
package com.sitescape.ef.domain;

/**
 * @hibernate.class table="SS_FolderCounts"
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * @author Janet McCann
 * This class represents the next docNumber for sub-folder and entries off of a folder.
 * It is kept in a separate class so we can update it quickly, without cacheing it.  
 * Using a one-to-one from folders, resulted in the folder lockVersion being updated and that
 * is what we are try to avoid, since folders are cached and may be slightly stale. 
 */
public class FolderCounts  {
	private Long lockVersion=null;
	private Long id=null;
	private int nextFolder=1;
	private int nextEntry=1;
	
	public FolderCounts() {
		
	}
	public FolderCounts(Long id) {
		this.id = id;
	}
	/**
	 * @hibernate.id generator-class="assigned" 
	 * @return
	 */
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
    /**
     * @hibernate.version column="lockVersion" type="java.lang.Long" unsaved-value="null"
     */
    protected Long getLockVersion() {
        return this.lockVersion;
    }
    protected void setLockVersion(Long lockVersion) {
        this.lockVersion = lockVersion;
    }
 
    /**
     * @hibernate.property
     */
    public int getNextFolder() {
    	return nextFolder;
    }
    public void setNextFolder(int nextFolder) {
    	this.nextFolder = nextFolder;
    }
    public int allocateFolderNumbers(int count) {
    	int current = nextFolder;
    	nextFolder += count;
    	return current;
    }
    /**
     * @hibernate.property
     */
    public int getNextEntry() {
    	return nextEntry;
    }
    public void setNextEntry(int nextEntry) {
    	this.nextEntry = nextEntry;
    }
    public int allocateEntryNumbers(int count) {
       	int current = nextEntry;
    	nextEntry += count;
    	return current;
    }
    public int hashCode() {
    	return id.hashCode();
    }
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if ((obj == null) || (obj.getClass() != getClass()))
            return false;
        
        FolderCounts o = (FolderCounts) obj;
        if (this.id.equals(o.getId()))
            return true;
                
        return false;
     	
    }
}

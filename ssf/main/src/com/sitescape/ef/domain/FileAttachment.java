/*
 * Created on Oct 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.domain;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.sitescape.ef.repository.RepositoryUtil;


/**
 * @hibernate.subclass discriminator-value="F" dynamic-update="true"
 * @author janet
 */
public class FileAttachment extends Attachment {
    private List fileVersions; //set by hibernate access="field"
    private Integer lastVersion;
    private String repositoryServiceName;//initialized by hibernate access=field

    private FileItem fileItem;
    
    private FileLock fileLock;

    public FileAttachment() {
        
    }
    public FileAttachment(String name) {
    	super(name);
    }
    /**
     * @hibernate.component
     * @return
     */
    public FileItem getFileItem() {
        return this.fileItem;
    }
    public void setFileItem(FileItem fileItem) {
        this.fileItem = fileItem;
    }
    /**
     * @hibernate.property
     * @return
     */
    public Integer getLastVersion() {
        return this.lastVersion;
    }
    public void setLastVersion(Integer lastVersion) {
        this.lastVersion = lastVersion;
    }
    
    /**
     * Returns the highest existing version number, or -1 if no version exists. 
     * @return
     */
    public int getHighestVersionNumber() {
    	if(fileVersions == null || fileVersions.size() == 0)
    		return -1;
    	
    	int result = Integer.MIN_VALUE;
    	
    	for(int i = 0; i < fileVersions.size(); i++) {
    		VersionAttachment va = (VersionAttachment) fileVersions.get(i);
    		int no = va.getVersionNumber();
    		if(no > result)
    			result = no;
    	}
    	
    	return result;
    }
    
    /**
     * Return list of versions, sorted with highest first.
     * Important: Note that the version with the highest version number is NOT 
     * necessarily one that has the latest modification time. They are orthogonal
     * concepts. 
     * @return
     */
    public Set getFileVersions() {
    	if (fileVersions == null) fileVersions=new ArrayList();
    	Set result = new TreeSet(new VersionComparator());
    	result.addAll(fileVersions);
    	return result;
    }

    /**
     * Returns a list of VersionAttachments. 
     * Important: This method returns a reference to the original list,
     * not a copy. So the caller must NEVER modify the returned list.
     * @return
     */
    public List getFileVersionsUnsorted() {
    	if(fileVersions == null)
    		fileVersions = new ArrayList();
    	return fileVersions;
    }
    
    public void addFileVersion(VersionAttachment v) {
    	if (v == null) return;
    	v.setParentAttachment(this);
    	v.setOwner(getOwner());
    	if (fileVersions == null) fileVersions=new ArrayList();
    	fileVersions.add(v);
    }
    /**
     * Remove a version attachment.  Will get deleted from persistent store
     * @param v
     */
    public void removeFileVersion(VersionAttachment v) {
       	if (v == null) return;
    	v.setParentAttachment(null);
    	v.setOwner((AnyOwner)null);
       	if (fileVersions == null) fileVersions=new ArrayList();
        fileVersions.remove(v);   	
    }
    /**
     * Remove a version attachment with the specified number.  Will get deleted from persistent store
     * @param v
     */
   public void removeFileVersion(int versionNumber) {
	   	if (fileVersions == null) fileVersions=new ArrayList();
    	for (int i=0; i<fileVersions.size(); ++i) {
    		VersionAttachment v = (VersionAttachment)fileVersions.get(i);
    		if (v.getVersionNumber() == versionNumber) {
    			fileVersions.remove(v);
    			v.setOwner((AnyOwner)null);
    			v.setParentAttachment(null);
    			break;
    		}
    	}
    }
   
   public VersionAttachment findFileVersion(String versionName) {
	   if (fileVersions == null) fileVersions=new ArrayList();
	   for(int i = 0; i < fileVersions.size(); i++) {
		   VersionAttachment v = (VersionAttachment) fileVersions.get(i);
		   if (v.getVersionName().equals(versionName))
			   return v;
	   }
	   return null;
   }
   public VersionAttachment findFileVersionById(String versionId) {
	   if (fileVersions == null) fileVersions=new ArrayList();
	   for(int i = 0; i < fileVersions.size(); i++) {
		   VersionAttachment v = (VersionAttachment) fileVersions.get(i);
		   if(v.getId().equals(versionId))
			   return v;
	   }
	   return null;
   }  
   /*
    * Looks like this equals method (and possibly hashCode too) is wrong,
    * especially when used by VersionAttachment subclass.
   public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof FileAttachment)) return false;
		FileAttachment o = (FileAttachment) obj;
		// Don't use id - may not be saved yet
		if (getRepositoryServiceName().equals(o.getRepositoryServiceName())
				&& fileItem.equals(o.getFileItem()))
			return true;
		else 
			return false;
	}
    public int hashCode() {
       	int hash = 7;
       	hash = 31*hash + getRepositoryServiceName().hashCode();
    	hash = 31*hash + fileItem.hashCode();
    	return hash;
    }*/
    public boolean update(Object newVal) {
    	boolean changed = super.update(newVal);
    	FileAttachment f = (FileAttachment)newVal;
    	if (!getFileItem().equals(f.getFileItem())) {
    		setFileItem(f.getFileItem());
    		changed=true;
    	}
    	return changed;
    }
    
	public String getRepositoryServiceName() {
		if(repositoryServiceName == null)
			return RepositoryUtil.getDefaultRepositoryServiceName();
		else
			return repositoryServiceName;
	}
	public void setRepositoryServiceName(String repositoryServiceName) {
		this.repositoryServiceName = repositoryServiceName;
	}
	
    /**
     * @hibernate.component class="com.sitescape.ef.domain.FileLock" prefix="filelock_"
     */
    public FileLock getFileLock() {
        return this.fileLock;
    }
    public void setFileLock(FileLock fileLock) {
        this.fileLock = fileLock;
    }
    
    public boolean isCurrentlyLocked() {
    	FileLock lock = getFileLock();
    	if(lock == null) {
    		return false;
    	}
    	else {
    		if(lock.getExpirationDate().getTime() > System.currentTimeMillis())
    			return true;
    		else
    			return false;
    	}
    }
    
    public static class FileLock implements Cloneable {

    	private String id;
    	private String subject;
    	private Principal owner;
    	private Date expirationDate;
    	private String ownerInfo;

    	public FileLock() {
    	}

    	public FileLock(String id, String subject, Principal owner, 
    			Date expirationDate, String ownerInfo) {
    		this.id = id;
    		this.subject = subject;
    		this.owner = owner;
    		this.expirationDate = expirationDate;
    		this.ownerInfo = ownerInfo;
    	}

    	public String getId() {
    		return id;
    	}

    	public void setId(String id) {
    		this.id = id;
    	}

    	public String getSubject() {
    		return subject;
    	}
    	
    	public void setSubject(String subject) {
    		this.subject = subject;
    	}
    	
    	public Principal getOwner() {
    		return owner;
    	}
    	
    	public void setOwner(Principal owner) {
    		this.owner = owner; 
    	}
    	
    	public Date getExpirationDate() {
    		return expirationDate;
    	}

    	public void setExpirationDate(Date expirationDate) {
    		this.expirationDate = expirationDate;
    	}
    	
    	public String getOwnerInfo() {
    		return ownerInfo;
    	}
    	
    	public void setOwnerInfo(String ownerInfo) {
    		this.ownerInfo = ownerInfo;
    	}
    	
    	public Object clone() {
    		try {
    			FileLock other = (FileLock) super.clone();

    			other.id = id;
    			other.owner = owner;
    			other.expirationDate = expirationDate;
    			other.ownerInfo = ownerInfo;

    			return other;
    		} catch (CloneNotSupportedException e) {
    			// This shouldn't happen, since we are Cloneable
    			throw new InternalError();
    		}
    	}
    }
    /**
     * Order version from highest to lowest
     * @author Janet McCann
     *
     */
    public static class VersionComparator implements Comparator {
    	public int compare(Object obj1, Object obj2) {
    		VersionAttachment f1,f2;
    		f1 = (VersionAttachment)obj1;
    		f2 = (VersionAttachment)obj2;
    				
    		if (f1 == f2) return 0;
    		if (f1==null) return 1;
    		if (f2 == null) return -1;
    		if (f1.getVersionNumber() == f2.getVersionNumber()) return 0;
    		if (f1.getVersionNumber() > f2.getVersionNumber()) return -1;
    		return 1;
    	}
    }
    public String toString() {
    	return fileItem.toString();
    }

}

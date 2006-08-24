/*
 * Created on Oct 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.domain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.sitescape.ef.repository.RepositoryUtil;
import com.sitescape.ef.util.CollectionUtil;
import com.sitescape.ef.util.SPropsUtil;


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
    
    
    public List getFileVersions() {
    	if (fileVersions == null) fileVersions=new ArrayList();
    	return fileVersions;
    }
    public void setFileVersions(Collection newVersions) {
     	fileVersions = CollectionUtil.mergeAsSet(getFileVersions(), newVersions);
     	for (Iterator iter=newVersions.iterator(); iter.hasNext();) {
     		VersionAttachment v = (VersionAttachment)iter.next();
     		v.setParentAttachment(this);
     		v.setOwner(getOwner());
     	}
    }
    public void addFileVersion(VersionAttachment v) {
    	if (v == null) return;
    	v.setParentAttachment(this);
    	v.setOwner(getOwner());
    	getFileVersions().add(v);
    }
    /**
     * Remove a version attachment.  Will get deleted from persistent store
     * @param v
     */
    public void removeFileVersion(VersionAttachment v) {
       	if (v == null) return;
    	v.setParentAttachment(null);
    	v.setOwner((AnyOwner)null);
    	getFileVersions().remove(v);   	
    }
    /**
     * Remove a version attachment with the specified number.  Will get deleted from persistent store
     * @param v
     */
   public void removeFileVersion(int versionNumber) {
    	List vList = getFileVersions();
    	for (int i=0; i<vList.size(); ++i) {
    		VersionAttachment v = (VersionAttachment)vList.get(i);
    		if (v.getVersionNumber() == versionNumber) {
    			vList.remove(v);
    			v.setOwner((AnyOwner)null);
    			v.setParentAttachment(null);
    			break;
    		}
    	}
    }
   
   public VersionAttachment findFileVersion(String versionName) {
	   List vList = getFileVersions();
	   for(int i = 0; i < vList.size(); i++) {
		   VersionAttachment v = (VersionAttachment) vList.get(i);
		   if(v.getVersionName().equals(versionName))
			   return v;
	   }
	   return null;
   }
   public VersionAttachment findFileVersionById(String versionId) {
	   List vList = getFileVersions();
	   for(int i = 0; i < vList.size(); i++) {
		   VersionAttachment v = (VersionAttachment) vList.get(i);
		   if(v.getId().equals(versionId))
			   return v;
	   }
	   return null;
   }   
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
    }
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

    	public FileLock() {
    	}

    	public FileLock(String id, String subject, Principal owner, 
    			Date expirationDate) {
    		this.id = id;
    		this.subject = subject;
    		this.owner = owner;
    		this.expirationDate = expirationDate;
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
    	
    	public Object clone() {
    		try {
    			FileLock other = (FileLock) super.clone();

    			other.id = id;
    			other.owner = owner;
    			other.expirationDate = expirationDate;

    			return other;
    		} catch (CloneNotSupportedException e) {
    			// This shouldn't happen, since we are Cloneable
    			throw new InternalError();
    		}
    	}
    }

}

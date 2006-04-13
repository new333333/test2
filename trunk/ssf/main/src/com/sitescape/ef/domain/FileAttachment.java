/*
 * Created on Oct 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.domain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.sitescape.ef.util.CollectionUtil;


/**
 * @hibernate.subclass discriminator-value="F" dynamic-update="true"
 * @author janet
 */
public class FileAttachment extends Attachment {
    private List fileVersions; //set by hibernate access="field"
    private Integer lastVersion;
    private String repositoryServiceName;

    private FileItem fileItem;
    
    private HistoryStamp checkout;

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
   public boolean equals(Object obj) {
		if (obj == null)
			return false;
		FileAttachment o = (FileAttachment) obj;
		// Don't use id - may not be saved yet
		if (repositoryServiceName.equals(o.getRepositoryServiceName())
				&& fileItem.equals(o.getFileItem()))
			return true;
		else 
			return false;
	}
    public int hashCode() {
       	int hash = 7;
    	hash = 31*hash + repositoryServiceName.hashCode();
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
    
    /**
     * @hibernate.property length="128"
     * @return
     */
	public String getRepositoryServiceName() {
		return repositoryServiceName;
	}
	public void setRepositoryServiceName(String repositoryServiceName) {
		this.repositoryServiceName = repositoryServiceName;
	}
    
    /**
     * @hibernate.component class="com.sitescape.ef.domain.HistoryStamp" prefix="checkout_"
     */
    public HistoryStamp getCheckout() {
        return this.checkout;
    }
    public void setCheckout(HistoryStamp stamp) {
        this.checkout = stamp;
    }
}

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
    private List fileVersions;
    private Integer lastVersion;

    private FileItem fileItem;
    
    public FileAttachment() {
        
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
     * @hibernate.bag  lazy="false" cascade="all,delete-orphan" inverse="true" batch-size="4" optimistic-lock="false"
 	 * @hibernate.key column="parentAttachment" 
	 * @hibernate.key-property length="32"
     * @hibernate.one-to-many class="com.sitescape.ef.domain.VersionAttachment"
     */
    private List getHFileVersions() {return fileVersions;}
    private void setHFileVersions(List fileVersions) {this.fileVersions = fileVersions;}
    
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
    	if (v == null) throw new IllegalArgumentException("null not allowed");
    	v.setParentAttachment(this);
    	v.setOwner(getOwner());
    	getFileVersions().add(v);
    }
    /**
     * Remove a version attachment.  Will get deleted from persistent store
     * @param v
     */
    public void removeFileVersion(VersionAttachment v) {
       	if (v == null) throw new IllegalArgumentException("null not allowed");
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
    }    public boolean equals(Object obj) {
 
        FileAttachment o = (FileAttachment) obj;
        //Don't use id - may not be saved yet
        if (fileItem.equals(o.getFileItem()))  return true;

        return false;
    }
    public int hashCode() {
    	return  31*super.hashCode() + fileItem.hashCode();
    }
    public void update(Object newVal) {
    	super.update(newVal);
    	FileAttachment f = (FileAttachment)newVal;
    	setFileItem(f.getFileItem());
    }
}

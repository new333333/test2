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
/*
 * Created on Oct 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.kablink.teaming.domain;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.module.shared.XmlUtils;
import org.kablink.teaming.repository.RepositoryUtil;
import org.kablink.util.Validator;


/**
 * @hibernate.subclass discriminator-value="F" dynamic-update="true"
 * @author janet
 */
public class FileAttachment extends Attachment {
    private List fileVersions; //set by hibernate access="field"
    private Integer lastVersion;
    private String repositoryName;//initialized by hibernate access=field

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
    	VersionAttachment va = getHighestVersion();
    	if(va != null)
    		return va.getVersionNumber();
    	else
    		return -1;
    }
    
    public VersionAttachment getHighestVersion() {
    	VersionAttachment hva = null;
    	if(fileVersions != null) {
	    	int hno = Integer.MIN_VALUE;
	    	for(Object va :  fileVersions) {
	    		int no = ((VersionAttachment) va).getVersionNumber();
	    		if(no > hno) {
	    			hno = no;
	    			hva = (VersionAttachment) va;
	    		}
	    	}
    	}
    	return hva;   	
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
    	v.setOwner(new AnyOwner(getOwner().getEntity(), false));  //version cannot set foreign key on owner or get read in to attachment collection
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
   public VersionAttachment findFileVersionByNumber(int versionNumber) {
	   if (fileVersions == null) fileVersions=new ArrayList();
	   for(int i = 0; i < fileVersions.size(); i++) {
		   VersionAttachment v = (VersionAttachment) fileVersions.get(i);
		   if(v.getVersionNumber() == versionNumber)
			   return v;
	   }
	   return null;
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
    
	public String getRepositoryName() {
		if(repositoryName == null)
			return RepositoryUtil.getDefaultRepositoryName();
		else
			return repositoryName;
	}
	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}
    /**
     * @hibernate.component class="org.kablink.teaming.domain.FileLock" prefix="filelock_"
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
    	private UserPrincipal owner;
    	private Date expirationDate;
    	private String ownerInfo;
    	private Boolean dirty;

    	public FileLock() {
    	}

    	public FileLock(String id, String subject, UserPrincipal owner, 
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
    	
    	public UserPrincipal getOwner() {
    		return owner;
    	}
    	
    	public void setOwner(UserPrincipal owner) {
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
    	
    	public void setDirty(Boolean dirty) {
    		this.dirty = dirty;
    	}
    	
    	public Boolean isDirty() {
    		return dirty;
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
	public Element addChangeLog(Element parent) {
		return addChangeLog(parent, true);
	}
	public Element addChangeLog(Element parent, boolean includeVersions) {
		Element element = parent.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_FILEATTACHMENT);
		element.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_DATABASEID, getId());
		if (!Validator.isNull(getName())) element.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_NAME, getName());
		
		if (creation != null) creation.addChangeLog(element, ObjectKeys.XTAG_ENTITY_CREATION);
		if (modification != null) modification.addChangeLog(element, ObjectKeys.XTAG_ENTITY_MODIFICATION);
		XmlUtils.addProperty(element, ObjectKeys.XTAG_FILE_NAME, getFileItem().getName());
		XmlUtils.addProperty(element, ObjectKeys.XTAG_FILE_LENGTH, Long.toString(getFileItem().getLength()));
		XmlUtils.addProperty(element, ObjectKeys.XTAG_FILE_REPOSITORY, getRepositoryName());
		XmlUtils.addProperty(element, ObjectKeys.XTAG_FILE_LAST_VERSION, getLastVersion().toString());
		if (includeVersions) {
			for (Iterator iter=getFileVersions().iterator(); iter.hasNext();) {
				VersionAttachment v = (VersionAttachment)iter.next();
				v.addChangeLog(element);
			}
		}
		return element;
    	
    }
}

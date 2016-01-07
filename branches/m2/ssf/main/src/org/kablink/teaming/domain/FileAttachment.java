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

import javax.crypto.SecretKey;

import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.module.shared.EntityIndexUtils;
import org.kablink.teaming.module.shared.XmlUtils;
import org.kablink.teaming.repository.RepositoryUtil;
import org.kablink.teaming.util.NLT;
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
    private Integer majorVersion;
    private Integer minorVersion;
    private Boolean agingEnabled = Boolean.FALSE;
    private Date agingDate;
    private Boolean encrypted = Boolean.FALSE;
    private byte[] encryptionKey;
    private Integer fileStatus = FileStatus.valueOf(FileStatus.not_set);
    private boolean fileExists = true;
   
    public FileAttachment() {
        
    }
    public FileAttachment(String name) {
    	super(name);
    }
 
	public enum FileStatus {
		not_set (0),
		official (1),
		draft (2),
		obsolete (3);
		
		int dbValue;
		FileStatus(int dbValue) {
			this.dbValue = dbValue;
		}

		public static FileStatus valueOf(int type) {
			switch (type) {
			case 0: return FileStatus.not_set;
			case 1: return FileStatus.official;
			case 2: return FileStatus.draft;
			case 3: return FileStatus.obsolete;
			default: return FileStatus.not_set;
			}
		}
		
		public static int valueOf(FileStatus fs) {
			switch (fs) {
			case not_set: return 0;
			case official: return 1;
			case draft: return 2;
			case obsolete: return 3;
			default: return 0;
			}
		}
	};
	
	public boolean getFileExists() {
		return this.fileExists;
	}

	public void setFileExists(boolean fileExists) {
		this.fileExists = fileExists;
	}

	public Date getAgingDate() {
		return this.agingDate;	//This can be null if it has never been initialized
	}

	public void setAgingDate(Date agingDate) {
		this.agingDate = agingDate;
	}

	public Boolean getAgingEnabled() {
		return this.agingEnabled;	//This can be null if it has never been initialized
	}

	public void setAgingEnabled(Boolean agingEnabled) {
		this.agingEnabled = agingEnabled;
	}

	public boolean isAgingEnabled() {
		if (this.agingEnabled == null || !this.agingEnabled) {
			return false;
		} else {
			return true;
		}
	}

	public Boolean getEncrypted() {
		return this.encrypted;	//This can be null if it has never been initialized
	}

	public void setEncrypted(Boolean encrypted) {
		this.encrypted = encrypted;
	}

	public boolean isEncrypted() {
		if (this.encrypted == null || !this.encrypted) {
			return false;
		} else {
			return true;
		}
	}

	public byte[] getEncryptionKey() {
		return this.encryptionKey;	//This can be null if it has never been initialized
	}

	public void setEncryptionKey(byte[] encryptionKey) {
		this.encryptionKey = encryptionKey;
	}

	public void setEncryptionKey(SecretKey encryptionKey) {
		this.encryptionKey = encryptionKey.getEncoded();
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
     * @hibernate.property
     * @return
     */
    public Integer getMajorVersion() {
    	if (this.majorVersion == null) return 1; //If no major version, this is an old entry. Return 1.
        return this.majorVersion;
    }
    public void setMajorVersion(Integer majorVersion) {
    	if (majorVersion == null) return;
    	if (majorVersion <= 0) throw new IllegalArgumentException("Invalid majorVersion");
        if (!majorVersion.equals(this.majorVersion)) this.majorVersion = majorVersion;
    }
    /**
     * @hibernate.property
     * @return
     */
    public Integer getMinorVersion() {
    	if (this.minorVersion == null) {
    		if (this instanceof VersionAttachment) {
    			int v = ((VersionAttachment)this).getVersionNumber();
    			if (v > 0) {
    				return  v - 1;
    			} else {
    				return 0;
    			}
    		} else {
	    		if (getHighestVersionNumber() > 0) {
	    			return getHighestVersionNumber() - 1;
	    		} else {
	    			return 0;
	    		}
    		}
    	}
        return this.minorVersion;
    }
    public void setMinorVersion(Integer minorVersion) {
    	if (minorVersion == null) return;
    	if (minorVersion < 0) return;
        if (!minorVersion.equals(this.minorVersion)) this.minorVersion = minorVersion;
    }
    public String getFileVersion() {
    	return String.valueOf(this.getMajorVersion()) + "." + String.valueOf(this.getMinorVersion());
    }
    /**
     * @hibernate.property
     * @return
     */
    public Integer getFileStatus() {
    	if (this.fileStatus == null) return FileStatus.valueOf(FileStatus.not_set);
        return this.fileStatus;
    }
    public void setFileStatus(Integer fileStatus) {
        this.fileStatus = fileStatus;
    }
    public String getFileStatusText() {
    	return NLT.get("file.status" + String.valueOf(this.getFileStatus()));
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
		DefinableEntity entry = getOwner().getEntity();
		if (entry instanceof Binder) {
			EntityIndexUtils.addReadAccess(parent, (Binder)entry, true);
		} else {
			EntityIndexUtils.addReadAccess(parent, entry.getParentBinder(), entry, true);
		}
		Element element = parent.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_FILEATTACHMENT);
		element.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_DATABASEID, getId());
		if (!Validator.isNull(getName())) element.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_NAME, getName());
		
		if (creation != null) creation.addChangeLog(element, ObjectKeys.XTAG_ENTITY_CREATION);
		if (modification != null) modification.addChangeLog(element, ObjectKeys.XTAG_ENTITY_MODIFICATION);
		XmlUtils.addProperty(element, ObjectKeys.XTAG_FILE_NAME, getFileItem().getName());
		XmlUtils.addProperty(element, ObjectKeys.XTAG_FILE_LENGTH, Long.toString(getFileItem().getLength()));
		XmlUtils.addProperty(element, ObjectKeys.XTAG_FILE_DESCRIPTION, getFileItem().getDescription().getText());
		XmlUtils.addProperty(element, ObjectKeys.XTAG_FILE_REPOSITORY, getRepositoryName());
		XmlUtils.addProperty(element, ObjectKeys.XTAG_FILE_LAST_VERSION, getLastVersion().toString());
		XmlUtils.addProperty(element, ObjectKeys.XTAG_FILE_MAJOR_VERSION, getMajorVersion().toString());
		XmlUtils.addProperty(element, ObjectKeys.XTAG_FILE_MINOR_VERSION, getMinorVersion().toString());
		XmlUtils.addProperty(element, ObjectKeys.XTAG_FILE_STATUS, getFileStatus().toString());
		Set fileVersions = getFileVersions();
		XmlUtils.addProperty(element, ObjectKeys.XTAG_FILE_VERSION_COUNT, String.valueOf(fileVersions.size()));
		if (includeVersions) {
			int count = 0;
			for (Iterator iter=fileVersions.iterator(); iter.hasNext();) {
				VersionAttachment v = (VersionAttachment)iter.next();
				v.addChangeLog(element);
				count++;
				//Only add the first few version to this log file
				if (count > ObjectKeys.XTAG_FILE_VERSION_COUNT_MAXIMUM) break;
			}
		}
		return element;
    	
    }
}

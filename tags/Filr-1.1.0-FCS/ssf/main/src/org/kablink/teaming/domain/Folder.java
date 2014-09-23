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
package org.kablink.teaming.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kablink.teaming.domain.ResourceDriverConfig.DriverType;
import org.kablink.teaming.fi.connection.ResourceDriverManagerUtil;

/**
 * @hibernate.subclass discriminator-value="folder" dynamic-update="true"
 * 
 * @author Jong Kim
 * 
 */
public class Folder extends Binder {
    protected Folder parentFolder;
    protected String entryRootKey;
    protected Folder topFolder;
    protected int nextEntryNumber=1;
	protected Boolean preDeleted;
    protected Long preDeletedWhen;
    protected Long preDeletedBy;
    
    //We don't maintain a list of entries because it is to big and expensive to 
    //maintain.
    public Folder() {
    	setLibrary(true);
    }
    public Folder(Folder folder) {
       	super(folder);
    }
 	public EntityIdentifier.EntityType getEntityType() {
		return EntityIdentifier.EntityType.folder;
	}
 
    /**
     * @hibernate.many-to-one 
     * @return
     */    
    public Folder getTopFolder() {
        return topFolder;
    }
    public void setTopFolder(Folder topFolder) {
        this.topFolder = (Folder)topFolder;
    }
    public boolean isTop() {
    	return topFolder == null;
    }
    public Folder getRootFolder() {
    	if (isTop()) return this;
    	return getTopFolder();
    }
    public Folder getParentFolder() {
        if (topFolder == null) return null;
    	return (Folder)getParentBinder();
    }
    public void setParentFolder(Folder parentFolder) {
        setParentBinder(parentFolder);
    }
    /**
     * Top folder always returns false.  Overloaded method
     */
    public boolean isDefinitionInheritanceSupported() {
    	if (isTop()) return false;
    	return true;
    }
 
    
   /** 
     * @hibernate.property 
     * @return
     */
    public int getNextEntryNumber() {
    	return nextEntryNumber;
    }
    public void setNextEntryNumber(int nextEntryNumber) {
    	this.nextEntryNumber = nextEntryNumber;
    }
 
    /**
     * @hibernate.property length="15"
     */
    public String getEntryRootKey() {
    	if (entryRootKey != null) return entryRootKey;
       	if (getId() == null) return null;
        entryRootKey = HKey.generateRootKey(getId());
    	return entryRootKey;
    }
    protected void setEntryRootKey(String entryRootKey) {
        this.entryRootKey = entryRootKey;
    }
   
    public List getFolders() {
    	return getBinders();
    }
    public void addBinder(Binder binder) {
    	addFolder((Folder)binder);
    }
    public void addFolder(Folder child) {
  		super.addBinder(child);
        if (topFolder == null) child.setTopFolder(this); else child.setTopFolder(topFolder);
   	}
    public void removeBinder(Binder binder) {
    	removeFolder((Folder)binder);
    }
    public void removeFolder(Folder child) {
        if (!child.getParentFolder().equals(this)) {
            throw new NoFolderByTheIdException(child.getId(),"Subfolder not in this folder");
        }
        super.removeBinder(child);
        child.setTopFolder(null);
        child.setParentFolder(null);
    }    
    
    /**
     * Add entry to this folder.  Setup parent/child connections.
     * @param entry
     */
    public void addEntry(FolderEntry entry) {
      entry.setParentEntry(null);
      entry.setTopEntry(null);
      entry.setParentBinder((Folder)this);
      entry.setOwningBinderKey(getBinderKey().getSortKey());
      entry.setHKey(new HKey(getEntryRootKey(), nextEntryNumber++));
    }
    /**
     * Add a new entry into the folder at a fixed position
     * The position must be greater than any current entries
     * @param source
     * @param entry
     */
    public void addEntry(FolderEntry entry, int docNumber) {
    	if (docNumber < nextEntryNumber) throw new IllegalArgumentException("docNumber already exists");
    	nextEntryNumber = docNumber;
    	addEntry(entry);    	
    }
    /**
     * Removes the connection of this entry from the folder.  
     * Replies must also be removed from their parent entry
     * Caller must call delete() to remove the persistent instance, otherwise connect 
     * it to another folder using <code>addEntry()</code> 
     * @param entry
     */
    public void removeEntry(FolderEntry entry) {
      if (!entry.getParentFolder().getId().equals(this.getId())) {
         throw new NoFolderEntryByTheIdException(entry.getId(),"Entry not in this folder");
      }
      if (entry.getParentEntry() != null) {
        throw new NoFolderEntryByTheIdException(entry.getId(),"Entry is a reply");
      }
      entry.setParentFolder(null);
      entry.setHKey(null);
      entry.setOwningBinderKey(null);

    }    

 
    //entries don't support acls except for workflow
    public List getChildAclControlled() {
        return new ArrayList();
    }
	/* (non-Javadoc)
	 * @see org.kablink.teaming.security.acl.AclContainer#getChildAclContainers()
	 */
	public List getChildAclContainers() {
		return getFolders();
	}


    public List getEntryDefinitions() {
   		return getDefs(Definition.FOLDER_ENTRY);
    }
    public List getViewDefinitions() {
    	if (definitionType != null) 
    		return getDefs(definitionType);
    	else
    		return getDefs(Definition.FOLDER_VIEW);
    }

    @Override
    protected Binder newInstance() {
        return new Folder();
    }

    /**
     * Processor type for folders may be different and dependent on
     * the definition
     * @param processorKey
     * @return new key
     */
    public String getProcessorKey(String processorKey) {
    	if (definitionType != null)
    		return processorKey+"_"+definitionType.toString();
    	return processorKey;
    }
    
    /**
     * @hibernate.property
     * @return
     */
    public Boolean isPreDeleted() {
    	return ((null != preDeleted) && preDeleted);
    }
    public void setPreDeleted(Boolean preDeleted) {
    	this.preDeleted = preDeleted;
    }
    
	/**
	 * @hibernate.property 
	 * @return
	 */
	public Long getPreDeletedWhen() {
		return preDeletedWhen;
	}
	public void setPreDeletedWhen(Long preDeletedWhen) {
		this.preDeletedWhen = preDeletedWhen;
	}
	
	/**
     * @hibernate.property
	 * @return
     */
	public Long getPreDeletedBy() {
		return preDeletedBy;
	}
	public void setPreDeletedBy(Long preDeletedBy) {
		this.preDeletedBy = preDeletedBy;
	}
	
}

/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.domain;

import java.util.ArrayList;
import java.util.List;

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
    //We don't maintain a list of entries because it is to big and expensive to 
    //maintain.
    public Folder() {
    	setType(EntityIdentifier.EntityType.folder.name());
    	setLibrary(true);
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
     * @hibernate.properoty length="15"
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
	 * @see com.sitescape.team.security.acl.AclContainer#getChildAclContainers()
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
}

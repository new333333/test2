package com.sitescape.ef.domain;

import java.util.ArrayList;
import java.util.List;

import com.sitescape.ef.InternalException;

/**
 * @hibernate.subclass discriminator-value="folder" dynamic-update="true"
 * 
 * @author Jong Kim
 * 
 */
public class Folder extends Binder {
    protected Folder parentFolder;
    protected HKey folderHKey;
    protected HKey entryRootHKey;
    protected Folder topFolder;
    protected int nextFolderNumber=1;
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
    	if (topFolder != null) return true;
    	return false;
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
     * @hibernate.property 
     * @return
     */
    public int getNextFolderNumber() {
    	return nextFolderNumber;
    }
    public void setNextFolderNumber(int nextFolderNumber) {
    	this.nextFolderNumber = nextFolderNumber;
    }   
    /**
     * Overload so we can return parents definition if not set for this folder
     */
    public Definition getDefaultPostingDef() {
    	Definition def = super.getDefaultPostingDef();
    	if (def != null) return def;
    	return getParentBinder().getDefaultPostingDef();
    }
    
    /**
     * @hibernate.component class="com.sitescape.ef.domain.HKey" prefix="folder_"
     */
    public HKey getFolderHKey() {
       	if (folderHKey != null) return folderHKey;
    	if (getId() == null) return null;
    	folderHKey = new HKey(generateFolderRootSortKey());
    	return folderHKey;
    }
    public void setFolderHKey(HKey folderHKey) {
        this.folderHKey = folderHKey;
    }
    /**
     * @hibernate.component class="com.sitescape.ef.domain.HKey" prefix="entryRoot_"
     */
    public HKey getEntryRootHKey() {
    	if (entryRootHKey != null) return entryRootHKey;
    	if (getId() == null) return null;
    	entryRootHKey = new HKey(generateEntryRootSortKey());
    	return entryRootHKey;
    }
    protected void setEntryRootHKey(HKey entryRootHKey) {
        this.entryRootHKey = entryRootHKey;
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
   		//	Set root for subfolders
 	   	child.setFolderHKey(new HKey(getFolderHKey(), nextFolderNumber++));   			
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
        child.setFolderHKey(null);
    }    
    
    /**
     * Add entry to this folder.  Setup parent/child connections.
     * @param entry
     */
    public void addEntry(FolderEntry entry) {
      entry.setParentEntry(null);
      entry.setTopEntry(null);
      entry.setParentBinder((Folder)this);
      entry.setOwningFolderSortKey(getFolderHKey().getSortKey());
      entry.setHKey(new HKey(getEntryRootHKey(), nextEntryNumber++));

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
      entry.setOwningFolderSortKey(null);

    }    

 
    //entries don't support acls except for workflow
    public List getChildAclControlled() {
        return new ArrayList();
    }
	/* (non-Javadoc)
	 * @see com.sitescape.ef.security.acl.AclContainer#getChildAclContainers()
	 */
	public List getChildAclContainers() {
		return getFolders();
	}


    public Long getOwnerId() {
    	HistoryStamp creation = getCreation();
    	if(creation != null) {
    		Principal principal = creation.getPrincipal();
    		if(principal != null)
    			return principal.getId();
    	}
    	return null;
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
    
    /*
     * Each folder has a unique root sort key that it uses to
     * generate sortkeys for its child docshareentries.
     * The root is generated from the folder id.
     */
    protected String generateEntryRootSortKey() {
    	//the maximum long value encoded in base 36 will fit in 15 bytes
    	StringBuffer sortKey = new StringBuffer(15);
    	Long id = getId();
    	if (id == null) throw new InternalException("Folder must be saved");
    	long start = id.longValue();
    	
        // Base 36 conversion 
        while (start > 0) {
            sortKey.insert(0,HKey.B10_TO_36.charAt((int)(start%36)));
            start = start/36;
        }
        for (int i=sortKey.length(); i<15; ++i) {
            sortKey.insert(0,"0");            
        }
        
        return sortKey.toString();
    }
    protected String generateFolderRootSortKey(){
    	return generateEntryRootSortKey() + "00001";
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

package com.sitescape.ef.domain;

import java.util.List;
import java.util.ArrayList;

import com.sitescape.ef.security.acl.AclContainer;
import com.sitescape.ef.InternalException;

/**
 * @hibernate.subclass discriminator-value="FOLDER" dynamic-update="true" node="ss_folder"
 * 
 * @author Jong Kim
 *
 */
public class Folder extends Binder {
    public static final int DISPLAY_STYLE_DEFAULT = 1;
    public static final int DISPLAY_STYLE_DISCUSSION = 2;
    public static final int DISPLAY_STYLE_LIBRARY_FOLDER = 3;
    public static final int DISPLAY_STYLE_DOCUMENT_LIBRARY = 4;
    
    protected int displayStyle = DISPLAY_STYLE_DEFAULT;
    protected List folders;
    protected List entries;
    protected Folder parentFolder;
    protected HKey folderHKey;
    protected HKey entryRootHKey;
    protected Folder topFolder;

    public Folder() {
        
    }
    public Folder(Folder parentFolder) {
        this.parentFolder = parentFolder;
    }
    /**
     * @hibernate.many-to-one node="topFolder/@id" embed-xml="false"
     * @return
     */
    public Folder getTopFolder() {
        return topFolder;
    }
    public void setTopFolder(Folder topFolder) {
        this.topFolder = (Folder)topFolder;
    }
    
    /**
     * @hibernate.many-to-one node="parentFolder/@id" embed-xml="false"
      * @return
     */
    public Folder getParentFolder() {
        return parentFolder;
    }
    public void setParentFolder(Folder parentFolder) {
        this.parentFolder = parentFolder;
    }
   
    /**
     * @hibernate.property node="displayStyle"
     * @return
     */
    public int getDisplayStyle() {
        return displayStyle;
    }
    public void setDisplayStyle(int displayStyle) {
        this.displayStyle = displayStyle;
    }
    /**
     * Overload so we can return parents definition if not set for this folder
     */
    public Definition getDefaultPostingDef() {
    	Definition def = super.getDefaultPostingDef();
    	if (def != null) return def;
    	if (parentFolder != null) return parentFolder.getDefaultPostingDef();
    	return getOwningWorkspace().getDefaultPostingDef();
    }
    
    /**
     * @hibernate.component class="com.sitescape.ef.domain.HKey" prefix="folder_"
     */
    public HKey getFolderHKey() {
       	// This should only be called when adding a child, which requires a write transaction
    	if (folderHKey.getSortKey() == null) {
    		folderHKey=new HKey(generateEntryRootSortKey());
    	}
        return folderHKey;
    }
    protected void setFolderHKey(HKey folderHKey) {
        this.folderHKey = folderHKey;
    }
    /**
     * @hibernate.component class="com.sitescape.ef.domain.HKey" prefix="entryRoot_"
     */
    public HKey getEntryRootHKey() {
    	// This should only be called when adding a child, which requires a write transaction
    	if (entryRootHKey.getSortKey() == null) entryRootHKey = new HKey(generateEntryRootSortKey());
        return entryRootHKey;
    }
    protected void setEntryRootHKey(HKey entryRootHKey) {
        this.entryRootHKey = entryRootHKey;
    }

    /**
     * @hibernate.bag  lazy="true" cascade="all" inverse="true" optimistic-lock="false" 
	 * @hibernate.key column="parentFolder" 
	 * @hibernate.one-to-many class="com.sitescape.ef.domain.Folder" 
     * @hibernate.cache usage="read-write"
     * Returns a List of Folder.
     * @return
     */
    private List getHFolders() {return folders;}
    private void setHFolders(List folders) {this.folders = folders;}
    
    public List getFolders() {
    	if (folders == null) folders = new ArrayList();
        return folders;
    }
    public void addFolder(Folder child, int childNum) {
  		getFolders().add(child);
   		child.setParentFolder(this);
   		child.setTopFolder(topFolder);
   		//	Set root for subfolders
   		child.setFolderHKey(new HKey(getFolderHKey(), childNum));
     }
    public void removeFolder(Folder child) {
        if (!child.getParentFolder().equals(this)) {
            throw new NoFolderByTheIdException(child.getId(),"Subfolder not in this folder");
        }
        getFolders().remove(child);
        child.setTopFolder(null);
        child.setParentFolder(null);
        child.setFolderHKey(null);
        child.setEntryRootHKey(null);
    }    

    /**
     * @hibernate.bag lazy="true" cascade="all" inverse="true" optimistic-lock="false"
 	 * @hibernate.key column="parentFolder" 
	 * @hibernate.one-to-many class="com.sitescape.ef.domain.FolderEntry"
     * Returns a Set of Folder.
     * @return
     */
    private List getIEntries() {
    	return entries;
    }
    private void setIEntries(List entries) {
    	this.entries = entries;
    }
    public List getEntries() {
    	if (entries == null) entries = new ArrayList();
    	return entries;
     }
     
    /**
     * Add entry to this folder.  Setup parent/child connections.
     * @param entry
     */
    public void addEntry(FolderEntry entry, int childNum) {
      entry.setParentEntry(null);
      entry.setTopEntry(null);
      entry.setParentFolder((Folder)this);
      entry.setOwningFolderSortKey(getFolderHKey().getSortKey());
      entry.setHKey(new HKey(getEntryRootHKey(), childNum));
      getEntries().add(entry);

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
      getEntries().remove(entry);

    }    

 
    public List getChildAclControlled() {
        return getEntries();
    }
	/* (non-Javadoc)
	 * @see com.sitescape.ef.security.acl.AclContainer#getChildAclContainers()
	 */
	public List getChildAclContainers() {
		return getFolders();
	}

    public AclContainer getParentAclContainer() {
        AclContainer ac = this.getParentFolder();
        if(ac == null)
            ac = this.getTopFolder();
        return ac;
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
}

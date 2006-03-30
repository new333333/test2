package com.sitescape.ef.domain;

import java.util.List;
import java.util.ArrayList;

import com.sitescape.ef.security.acl.AclContainer;
import com.sitescape.ef.InternalException;

/**
 * @hibernate.subclass discriminator-value="folder" dynamic-update="true" node="ss_folder"
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
    protected List entries; //set by hibernate acccess="field"
    protected Folder parentFolder;
    protected HKey folderHKey;
    protected HKey entryRootHKey;
    protected Folder topFolder;
    protected int nextFolderNumber=1;
    protected int nextEntryNumber=1;

    public Folder() {
    	setType(EntityIdentifier.EntityType.folder.name());
    }
    public EntityIdentifier getEntityIdentifier() {
    	return new EntityIdentifier(getId(), EntityIdentifier.EntityType.folder);
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
    protected void setFolderHKey(HKey folderHKey) {
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
    public void addFolder(Folder child) {
  		super.addBinder(child);
        if (topFolder == null) child.setTopFolder(this); else child.setTopFolder(topFolder);
   		//	Set root for subfolders
 	   	child.setFolderHKey(new HKey(getFolderHKey(), nextFolderNumber++));   			
   	}
    public void removeFolder(Folder child) {
        if (!child.getParentFolder().equals(this)) {
            throw new NoFolderByTheIdException(child.getId(),"Subfolder not in this folder");
        }
        super.removeBinder(child);
        child.setTopFolder(null);
        child.setParentFolder(null);
        child.setFolderHKey(null);
        child.setEntryRootHKey(null);
    }    

    public List getEntries() {
    	if (entries == null) entries = new ArrayList();
    	return entries;
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


    public Long getCreatorId() {
    	HistoryStamp creation = getCreation();
    	if(creation != null) {
    		Principal principal = creation.getPrincipal();
    		if(principal != null)
    			return principal.getId();
    	}
    	return null;
    }
    public List getEntryDefs() {
   		return getDefs(Definition.COMMAND);
    }
    public List getBinderViewDefs() {
   		return getDefs(Definition.FORUM_VIEW);
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
}

package com.sitescape.ef.domain;

/**
 * @author Jong Kim
 *
 */
public class FolderEmailAlias extends EmailAlias {
    private Folder folder;
    private String searchString;
       
    /**
     * ?? Should I return folder name instead of folder object 
     * (for efficiency reason)?
     * @return
     */
    public Folder getFolder() {
        return folder;
    }
    public void setFolder(Folder folder) {
        this.folder = folder;
    }
    
    public String getSearchString() {
        return searchString;
    }
    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
}

package com.sitescape.team.remoting;

/**
 * WS facade for business tier.
 * 
 * @author jong
 *
 */
public interface Facade {
	//
	// Definition operations
	// 
	public String getDefinitionAsXML(String definitionId);
	
	public String getDefinitionConfigAsXML();
	
	// 
	// Folder operations
	// 	
	public String getFolderEntryAsXML(long folderId, long entryId);
	
	public long addFolderEntry(long folderId, String definitionId, String inputDataAsXML);
	
	public void modifyFolderEntry(long folderId, long entryId, String inputDataAsXML);
	
    public void deleteFolderEntry(long folderId, long entryId);

	public long addReply(long folderId, long parentEntryId, String definitionId, String inputDataAsXML);

	public void uploadFolderFile(long folderId, long entryId, 
			String fileUploadDataItemName, String fileName);

	//
	// Profile operations
	//
	public String getPrincipalAsXML(long binderId, long principalId);
	
	public long addUser(long binderId, String definitionId, String inputDataAsXML);

	public long addGroup(long binderId, String definitionId, String inputDataAsXML);
	
	public void modifyPrincipal(long binderId, long principalId, String inputDataAsXML);
	
	public void deletePrincipal(long binderId, long principalId);
	
	//
	// Workspace operations
	//
	
	/**
	 * Returns workspace tree represented in XML.
	 * 
	 * @param binderId starting binder id
	 * @param levels depth to return. -1 means all
	 *  
	 * @return XML representation of the workspace tree
	 */
	public String getWorkspaceTreeAsXML(long binderId, int levels);
}


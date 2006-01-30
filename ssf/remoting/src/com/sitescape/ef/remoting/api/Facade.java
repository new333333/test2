package com.sitescape.ef.remoting.api;

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
	// Binder operations
	// 
	public Binder getBinder(long binderId);
	
	//public String[] getEntryDefinitionIds(long binderId);
	
	// 
	// Folder operations
	// 
	public Folder getFolder(long binderId);
	
	public String getFolderEntryAsXML(long binderId, long entryId);
	
	public long addFolderEntry(long binderId, String definitionId, String inputDataAsXML);
	
	public void modifyFolderEntry(long binderId, long entryId, String inputDataAsXML);
	
    public void deleteFolderEntry(long binderId, long entryId);

	public long addReply(long binderId, long parentId, String definitionId, String inputDataAsXML);

	public void uploadFolderFile(long binderId, long entryId, 
			String fileUploadDataItemName, String fileName);

	//
	// Profile operations
	//
	public String getPrincipalAsXML(long binderId, long principalId);
	
	public long addUser(long binderId, String definitionId, String inputDataAsXML);

	public long addGroup(long binderId, String definitionId, String inputDataAsXML);
	
	public void modifyPrincipal(long binderId, long principalId, String inputDataAsXML);
	
	public void deletePrincipal(long binderId, long principalId);
}


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
	
	// 
	// Folder operations
	// 
	//public Folder getFolder(long folderId);
	
	public String getFolderEntryAsXML(long folderId, long entryId);
	
	public long addFolderEntry(long folderId, String definitionId, String inputDataAsXML);
	
	//public void modifyFolderEntry(long folderId, long entryId, String inputDataAsXML);
	
	//public long addReply(long folderId, long parentId, String definitionId, String inputDataAsXML);

	
	public void uploadFolderFile(long binderId, long entryId, 
			String fileUploadDataItemName, String fileName);
}


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
	public String getFolderEntryAsXML(long binderId, long entryId);
	
	public long addFolderEntry(long binderId, String definitionId, String inputDataAsXML);
	
	public void uploadFile(long binderId, long entryId, 
			String fileUploadDataItemName, String fileName);

}

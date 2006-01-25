package com.sitescape.ef.remoting.api;


/**
 * WS facade for business tier.
 * 
 * @author jong
 *
 */
public interface Facade {
	public String getDefinitionAsXML(String definitionId);
	
	public String getDefinitionConfigAsXML();
	
	public Binder getBinder(long binderId);
	
	public String getEntryAsXML(long binderId, long entryId);
	
	public long addEntry(long binderId, String definitionId, String inputDataAsXML);
	
	public int uploadFile(long binderId, long entryId, 
			String fileUploadDataItemName, String fileName);

}

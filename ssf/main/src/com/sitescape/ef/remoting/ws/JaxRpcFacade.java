package com.sitescape.ef.remoting.ws;

import org.springframework.remoting.jaxrpc.ServletEndpointSupport;

import com.sitescape.ef.remoting.api.Binder;
import com.sitescape.ef.remoting.api.Facade;
import com.sitescape.ef.remoting.api.Folder;
import com.sitescape.ef.remoting.rmi.RemoteFacade;

/**
 * JAX-RPC compliant implementation that simply delegates to the Facade 
 * implementation in the root web application context. This class implements
 * both the RMI service interface (RemoteFacade) and the plain Java business
 * interface.
 * 
 * @author jong
 *
 */
public class JaxRpcFacade extends ServletEndpointSupport implements Facade,RemoteFacade {

	private Facade facade;
	
	protected void onInit() {
		this.facade = (Facade) getWebApplicationContext().getBean("wsFacade");
	}

	public String getDefinitionAsXML(String definitionId) {
		return this.facade.getDefinitionAsXML(definitionId);
	}
	
	public String getDefinitionConfigAsXML() {
		return this.facade.getDefinitionConfigAsXML();
	}

	public Binder getBinder(long binderId) {
		return this.facade.getBinder(binderId);
	}
	
	public String getFolderEntryAsXML(long binderId, long entryId) {
		return this.facade.getFolderEntryAsXML(binderId, entryId);
	}
	
	public long addFolderEntry(long binderId, String definitionId, String inputDataAsXML) {
		return this.facade.addFolderEntry(binderId, definitionId, inputDataAsXML);
	}
	
	public void uploadFolderFile(long binderId, long entryId, 
			String fileUploadDataItemName, String fileName) {
		this.facade.uploadFolderFile(binderId, entryId, fileUploadDataItemName, fileName);
	}

	public Folder getFolder(long binderId) {
		return this.facade.getFolder(binderId);
	}

	public void modifyFolderEntry(long binderId, long entryId, String inputDataAsXML) {
		this.facade.modifyFolderEntry(binderId, entryId, inputDataAsXML);
	}

	public void deleteFolderEntry(long binderId, long entryId) {
		this.facade.deleteFolderEntry(binderId, entryId);
	}

	public long addReply(long binderId, long parentId, String definitionId, String inputDataAsXML) {
		return this.facade.addReply(binderId, parentId, definitionId, inputDataAsXML);
	}

	public String getPrincipalAsXML(long binderId, long principalId) {
		return this.facade.getPrincipalAsXML(binderId, principalId);
	}

	public long addUser(long binderId, String definitionId, String inputDataAsXML) {
		return this.facade.addUser(binderId, definitionId, inputDataAsXML);
	}

	public long addGroup(long binderId, String definitionId, String inputDataAsXML) {
		return this.facade.addGroup(binderId, definitionId, inputDataAsXML);
	}

	public void modifyPrincipal(long binderId, long principalId, String inputDataAsXML) {
		this.facade.modifyPrincipal(binderId, principalId, inputDataAsXML);
	}

	public void deletePrincipal(long binderId, long principalId) {
		this.facade.deletePrincipal(binderId, principalId);
	}
}

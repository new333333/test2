package com.sitescape.ef.remoting.ws;

import org.springframework.remoting.jaxrpc.ServletEndpointSupport;

import com.sitescape.ef.remoting.api.Binder;
import com.sitescape.ef.remoting.api.Facade;
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
		this.facade = (Facade) getWebApplicationContext().getBean("facadeWS");
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
	
	public void uploadFile(long binderId, long entryId, 
			String fileUploadDataItemName, String fileName) {
		this.facade.uploadFile(binderId, entryId, fileUploadDataItemName, fileName);
	}
}

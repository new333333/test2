package com.sitescape.ef.remoting.ws.jaxrpc;

import org.springframework.remoting.jaxrpc.ServletEndpointSupport;

import com.sitescape.ef.remoting.api.Entry;
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
		this.facade = (Facade) getWebApplicationContext().getBean("facade");
	}

	public String getDefinitionAsXML(String definitionId) {
		return this.facade.getDefinitionAsXML(definitionId);
	}
	
	public String getDefinitionConfigAsXML() {
		return this.facade.getDefinitionConfigAsXML();
	}

	public String getEntryAsXML(long binderId, long entryId) {
		return this.facade.getEntryAsXML(binderId, entryId);
	}
	
	public long addEntry(long binderId, String definitionId, String inputDataAsXML) {
		return this.facade.addEntry(binderId, definitionId, inputDataAsXML);
	}
	
	public int uploadFile(long binderId, long entryId, 
			String fileUploadDataItemName, String fileName) {
		return this.facade.uploadFile(binderId, entryId, fileUploadDataItemName, fileName);
	}
}

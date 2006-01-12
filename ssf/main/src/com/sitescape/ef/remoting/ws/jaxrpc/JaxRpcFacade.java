package com.sitescape.ef.remoting.ws.jaxrpc;

import org.springframework.remoting.jaxrpc.ServletEndpointSupport;

import com.sitescape.ef.remoting.api.Entry;
import com.sitescape.ef.remoting.api.Facade;
import com.sitescape.ef.remoting.jaxrpc.RemoteFacade;

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

	public Entry getEntry(long binderId, long entryId) {
		return this.facade.getEntry(binderId, entryId);
	}

}

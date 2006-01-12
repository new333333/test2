package com.sitescape.ef.remoting.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.sitescape.ef.remoting.api.Entry;

/**
 * RMI interface (aka. port interface) for Facade service.
 * It differs from <code>Facade</code> interface only in the remote
 * exception thrown. 
 * <p>
 * This class is NOT used. Most JAX-RPC implementations (including Axis)
 * accept endpoint class that just implements a business interface. 
 * If not, the endpoint class will have to implement this RMI port interface
 * which mirrors the business interface but complies to RMI conventions.
 *  
 * 
 * @author jong
 *
 */
public interface RemoteFacade extends Remote {
	
	public Entry getEntry(long binderId, long entryId) throws RemoteException;

}

package com.sitescape.ef.remoting.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.sitescape.ef.remoting.api.Entry;

/**
 * RMI interface (aka. port interface) for Facade service.
 * It differs from <code>Facade</code> interface only in the remote
 * exception thrown. 
 * <p>
 * The JAX-RPC endpoint class <code>JaxRpcFacade</code> implements this
 * RMI port interface which mirros the business interface <code>Facade</code>
 * but complies to RMI conventions. Alternatively, we could choose not to
 * use this class because most JAX-RPC implementations (including Axis)
 * accept endpoint class that just implements a business interface. 
 * However specifying RMI interface might give adgantages on certain
 * JAX-RPC implementations, so we will use it. 
 *  
 * @author jong
 *
 */
public interface RemoteFacade extends Remote {
	
	public Entry getEntry(long binderId, long entryId) throws RemoteException;
	
	public String getEntryAsXML(long binderId, long entryId) throws RemoteException;
	
	public long addEntry(long binderId, String definitionId, String inputDataAsXML) 
		throws RemoteException;
	
	public int uploadFile(long binderId, long entryId, 
			String fileUploadDataItemName, String fileName) 
		throws RemoteException;

}

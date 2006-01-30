package com.sitescape.ef.remoting.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.sitescape.ef.remoting.api.Binder;
import com.sitescape.ef.remoting.api.Folder;

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

	public String getDefinitionAsXML(String definitionId) throws RemoteException;
	
	public String getDefinitionConfigAsXML() throws RemoteException;

	public Binder getBinder(long binderId) throws RemoteException;

	//public String[] getEntryDefinitionIds(long binderId) throws RemoteException;
	
	public Folder getFolder(long binderId) throws RemoteException;
	
	public String getFolderEntryAsXML(long binderId, long entryId) throws RemoteException;
	
	public long addFolderEntry(long binderId, String definitionId, String inputDataAsXML) throws RemoteException;
	
	public void modifyFolderEntry(long binderId, long entryId, String inputDataAsXML) throws RemoteException;
	
    public void deleteFolderEntry(long binderId, long entryId) throws RemoteException;

	public long addReply(long binderId, long parentId, String definitionId, String inputDataAsXML) throws RemoteException;

	public void uploadFolderFile(long binderId, long entryId, 
			String fileUploadDataItemName, String fileName) throws RemoteException;

	public String getPrincipalAsXML(long binderId, long principalId) throws RemoteException;
	
	public long addUser(long binderId, String definitionId, String inputDataAsXML) throws RemoteException;

	public long addGroup(long binderId, String definitionId, String inputDataAsXML) throws RemoteException;
	
	public void modifyPrincipal(long binderId, long principalId, String inputDataAsXML) throws RemoteException;
	
	public void deletePrincipal(long binderId, long principalId) throws RemoteException;
}


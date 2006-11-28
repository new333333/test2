package com.sitescape.ef.samples.remoting.client.ws.jaxrpc;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;

import com.sitescape.ef.samples.remoting.client.FacadeClientHelper;
import com.sitescape.ef.samples.remoting.client.ws.jaxrpc.JaxRpcFacade;
import com.sitescape.ef.samples.remoting.client.ws.jaxrpc.JaxRpcFacadeService;
import com.sitescape.ef.samples.remoting.client.ws.jaxrpc.JaxRpcFacadeServiceLocator;

/**
 * This WS client program uses JAX-RPC compliant client binding classes 
 * generated by Axis' WSDL2Java tool.
 * <p>
 * This program is written in most part to the standard JAX-RPC interface,
 * and in fact, all of the client binding classes generated by Axis and used
 * by this program adhere to JAX-RPC specification (that is, the classes are
 * generated in accordance with JAX-RPC specification). The only Axis-specific
 * parts of this program is 1) the use of org.apache.axis.client.Call class 
 * in setting a tool specific property on the stub object, and 2) the use of
 * addAttachment method on the stub which provides Axis specific way of
 * adding an attachment. This is due to lack of standard support for handling
 * attachments in the current version of JAX-RPC (1.1). It should be fairly
 * easy to rewrite this program to use another JAX-RPC compliant WS tool. 
 * 
 * @author jong
 *
 */
public class FacadeClient {

	public FacadeClient() {
	}

	public static void main(String[] args) {

		// Because this program uses Axis-generated client binding files, it
		// is important to compile this program AFTER the appropriate artifacts
		// have been generated. 
		
		System.out.println("*** This Facade client uses Axis-generated client bindings");

		if(args.length == 0) {
			printUsage();
			return;
		}

		// Note: Instead of specifying the wsdd file to the java launch
		// program (see build.xml), we could specify it programmatically
		// by passing config object to the service locator constructor
		// as shown below (which is commented out).

		//EngineConfiguration config = new FileProvider("client_deploy.wsdd");

		try {
			if(args[0].equals("readDefinition")) {
				readDefinition(args[1]);
			}
			else if(args[0].equals("readDefinitionConfig")) {
				readDefinitionConfig();
			}
			else if(args[0].equals("readBinder")) {
				readBinder(Long.parseLong(args[1]));
			}
			else if(args[0].equals("readEntry")) {
				readEntry(Long.parseLong(args[1]), Long.parseLong(args[2]));
			}
			else if(args[0].equals("addEntry")){			
				addEntry(Long.parseLong(args[1]), args[2]);
			}
			else if(args[0].equals("uploadFile")){	
				uploadFile(Long.parseLong(args[1]), Long.parseLong(args[2]), args[3], args[4]);
			}
			else {
				System.out.println("Invalid arguments");
				printUsage();
				return;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void readDefinition(String definitionId) throws ServiceException, RemoteException {
		System.out.println("*** Reading a definition ***");
		
		JaxRpcFacadeService locator = new JaxRpcFacadeServiceLocator(/*config*/);
		JaxRpcFacade service = locator.getFacade();
		
		String definitionAsXML = service.getDefinitionAsXML(definitionId);
		
		FacadeClientHelper.printXML(definitionAsXML);				
	}
	
	private static void readDefinitionConfig() throws ServiceException, RemoteException {
		System.out.println("*** Reading a definition config ***");
		
		JaxRpcFacadeService locator = new JaxRpcFacadeServiceLocator(/*config*/);
		JaxRpcFacade service = locator.getFacade();
		
		String definitionConfigAsXML = service.getDefinitionConfigAsXML();
		
		FacadeClientHelper.printXML(definitionConfigAsXML);				
	}
	
	private static void readBinder(long binderId) throws ServiceException, RemoteException {
		System.out.println("*** Reading a binder ***");
		
		JaxRpcFacadeService locator = new JaxRpcFacadeServiceLocator(/*config*/);
		JaxRpcFacade service = locator.getFacade();
		
		Binder binder = service.getBinder(binderId);
		
		printBinder(binder);			
	}
	
	private static void readEntry(long binderId, long entryId) throws ServiceException, RemoteException {
		System.out.println("*** Reading an entry ***");
		
		JaxRpcFacadeService locator = new JaxRpcFacadeServiceLocator(/*config*/);
		JaxRpcFacade service = locator.getFacade();
		
		// Invoke getEntryAsXML
		String entryAsXML = service.getFolderEntryAsXML(binderId, entryId);
		
		FacadeClientHelper.printXML(entryAsXML);				
	}
	
	private static void addEntry(long binderId, String definitionId) throws ServiceException, RemoteException {
		
		System.out.println("*** Adding an entry ***");

		JaxRpcFacadeService locator = new JaxRpcFacadeServiceLocator(/*config*/);
		JaxRpcFacade service = locator.getFacade();

		String entryInputDataAsXML = 
			FacadeClientHelper.generateEntryInputDataAsXML(binderId, definitionId);
		
		FacadeClientHelper.printXML(entryInputDataAsXML);
		
		long entryId = service.addFolderEntry(binderId, definitionId, entryInputDataAsXML);
		
		System.out.println("*** ID of the newly created entry is " + entryId);	
		System.out.println();
	}
	
	private static void uploadFile(long binderId, long entryId, 
			String fileUploadDataItemName, String filePath) 
		throws ServiceException, RemoteException, MalformedURLException {
		System.out.println("*** Uploading file " + filePath + " ***");
		
		// Specify the URL to your webservice that is needed by the Stub
		String endpoint = "http://peace:8080/ssf/ws/Facade";
		URL targetURL = new URL(endpoint);
		
		// Create a Service object
		JaxRpcFacadeService service = new JaxRpcFacadeServiceLocator(/*config*/);
		
		// Create a stub for the webservice at the URL specified.
		FacadeSoapBindingStub stub = new FacadeSoapBindingStub(targetURL, service);
		
		// Use classes from the Java Activation Framework to wrap the attachment.
		DataHandler attachmentFile = new DataHandler(new FileDataSource(filePath));
		
		// Tell the stub that the message being formed also contains an attachment,
		// and it is of type MIME encoding.
		stub._setProperty(Call.ATTACHMENT_ENCAPSULATION_FORMAT, Call.ATTACHMENT_ENCAPSULATION_FORMAT_MIME);

		// Add the attachment to the message
		stub.addAttachment(attachmentFile);
		
		// Invoke the remote method through the stub passing in the necessary
		// arguments. 
		
		stub.uploadFolderFile(binderId, entryId, fileUploadDataItemName, new File(filePath).getName());
		
		System.out.println();
	}
	
	private static void printBinder(Binder binder) {
		System.out.println();
		System.out.println("*** Binder (id = " + binder.getId() + ")");
		System.out.println("\tName: " + binder.getName());
		System.out.println("\tZone Name: " + binder.getZoneName());
		System.out.println("\tType: " + binder.getType());
		System.out.println("\tTitle: " + binder.getTitle());
		System.out.println("\tEntry Definition IDs: ");
		String[] ids = binder.getEntryDefinitionIds();
		for(int i = 0; i < ids.length; i++)
			System.out.println("\t\tID: " + ids[i]); 
		
		System.out.println();
	}
	
	private static void printUsage() {
		System.out.println("Usage:");
		System.out.println("readDefinition <definition id>");
		System.out.println("readDefinitionConfig");
		System.out.println("readBinder <binder id>");
		System.out.println("readEntry <binder id> <entry id>");
		System.out.println("addEntry <binder id> <definition id>");
		System.out.println("uploadFile <binder id> <entry id> <file path>");	
	}
}

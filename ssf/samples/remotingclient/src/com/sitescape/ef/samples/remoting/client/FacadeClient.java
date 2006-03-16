package com.sitescape.ef.samples.remoting.client;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.sitescape.ef.remoting.api.Binder;
import com.sitescape.ef.remoting.api.Facade;

/**
 * Sample client that interacts with the server through Facade interface.
 * Notice that there is nothing in this class that is specific to a remoting
 * protocol (eg. JAX-RPC) or a tool (eg. Axis). The protocol specific 
 * implementation details are handled transparently by the proxies that Spring 
 * creates for Facade interface.
 *
 * @author jong
 *
 */
public class FacadeClient {

	public static final String CLIENT_CONTEXT_CONFIG_LOCATION = "clientContext-jaxrpc.xml";

	private Facade facade;

	public void setFacade(Facade facade) {
		this.facade = facade;
	}
	
	public static void main(String[] args) {
		System.out.println("*** This Facade client uses Spring's jaxrpc proxy");

		if(args.length == 0) {
			printUsage();
			return;
		}

		ListableBeanFactory beanFactory = new FileSystemXmlApplicationContext(CLIENT_CONTEXT_CONFIG_LOCATION);
		FacadeClient client = (FacadeClient) beanFactory.getBean("facadeClient");

		if(args[0].equals("readDefinition")) {
			System.out.println("*** Reading a definition");
			String definitionAsXML = client.facade.getDefinitionAsXML(args[1]);
			FacadeClientHelper.printXML(definitionAsXML);
		}
		else if(args[0].equals("readDefinitionConfig")) {
			System.out.println("*** Reading a definition config");
			String definitionConfigAsXML = client.facade.getDefinitionConfigAsXML();
			FacadeClientHelper.printXML(definitionConfigAsXML);			
		}
		else if(args[0].equals("readBinder")) {
			System.out.println("*** Reading a binder");
			Binder binder = client.facade.getBinder(Long.parseLong(args[1]));
			printBinder(binder);
		}
		else if(args[0].equals("readEntry")) {
			System.out.println("*** Reading an entry ***");
			
			int binderId = Integer.parseInt(args[1]);
			int entryId = Integer.parseInt(args[2]);
			
			String entryAsXML = client.facade.getFolderEntryAsXML(binderId, entryId);
			
			FacadeClientHelper.printXML(entryAsXML);
		}
		else if(args[0].equals("addEntry")){
			
			System.out.println("*** Adding an entry ***");

			int binderId = Integer.parseInt(args[1]);
			String definitionId = args[2];
			
			String entryInputDataAsXML = 
				FacadeClientHelper.generateEntryInputDataAsXML(binderId, definitionId);
			
			long entryId = client.facade.addFolderEntry(binderId, definitionId, entryInputDataAsXML);
			
			System.out.println("*** ID of the newly created entry is " + entryId);
			System.out.println();
		}
		else {
			System.out.println("Invalid arguments");
			printUsage();
			return;
		}
	}

	private static void printBinder(Binder binder) {
		System.out.println();
		System.out.println("*** Binder (id = " + binder.getId() + ")");
		System.out.println("\tName: " + binder.getName());
		System.out.println("\tZone Name: " + binder.getZoneName());
		System.out.println("\tType: " + binder.getType());
		System.out.println("\tTitle: " + binder.getTitle());
		System.out.println("\tOwning Workspace ID: " + binder.getParentBinderId());
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
	}
}

package com.sitescape.ef.samples.remoting.client;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

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

		// Arguments
		// read <binder id> <entry id> 
		// OR
		// add <binder id> <definition id>

		if(args.length < 3) {
			System.out.println("Invalid arguments");
			return;
		}

		ListableBeanFactory beanFactory = new FileSystemXmlApplicationContext(CLIENT_CONTEXT_CONFIG_LOCATION);
		FacadeClient client = (FacadeClient) beanFactory.getBean("facadeClient");

		if(args[0].equals("read")) {
			System.out.println("*** Reading an entry ***");
			
			int binderId = Integer.parseInt(args[1]);
			int entryId = Integer.parseInt(args[2]);
			
			String entryAsXML = client.facade.getEntryAsXML(binderId, entryId);
			FacadeClientHelper.printEntryAsXML(entryAsXML);
		}
		else if(args[0].equals("add")){
			
			System.out.println("*** Adding an entry ***");

			int binderId = Integer.parseInt(args[1]);
			String definitionId = args[2];
			
			String entryInputDataAsXML = 
				FacadeClientHelper.generateEntryInputDataAsXML(binderId, definitionId);
			
			long entryId = client.facade.addEntry(binderId, definitionId, entryInputDataAsXML);
			
			System.out.println("*** ID of the newly created entry is " + entryId);
		}
		else {
			System.out.println("Invalid arguments");
			return;
		}
	}

}

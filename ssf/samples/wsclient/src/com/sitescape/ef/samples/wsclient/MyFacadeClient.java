package com.sitescape.ef.samples.wsclient;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.sitescape.ef.remoting.api.Entry;
import com.sitescape.ef.remoting.api.Facade;

/**
 * Sample client that interacts with the server through Facade interface.
 * Notice that there is nothing in this class that is specific to JAX-RPC
 * or Axis. The protocol specific implementation details are handled
 * transparently by the proxy that Spring creates for Facade interface.
 * 
 * @author jong
 *
 */
public class MyFacadeClient {
	
	public static final String CLIENT_CONTEXT_CONFIG_LOCATION = "clientContext-jaxrpc.xml";
	
	private Facade facade;
	
	public void setFacade(Facade facade) {
		this.facade = facade;
	}
	
	public void printEntry(long binderId, long entryId) {
		Entry entry = this.facade.getEntry(binderId, entryId);
		System.out.println("Entry(" + entry.getBinderId() + "," + entry.getId() + ") - " + entry.getTitle());
	}
	
	public static void main(String[] args) {
		// first argument - binder id
		// second argument - entry id
		if(args.length < 2) {
			System.out.println("You need to specify a binder id and an entry id");
		}
		else {
			System.out.println("binder id = " + args[0] + ", entry id = " + args[1]);
			int binderId = Integer.parseInt(args[0]);
			int entryId = Integer.parseInt(args[1]);
			
			ListableBeanFactory beanFactory = new FileSystemXmlApplicationContext(CLIENT_CONTEXT_CONFIG_LOCATION);
			MyFacadeClient client = (MyFacadeClient) beanFactory.getBean("myFacadeClient");
			
			client.printEntry(binderId, entryId);
		}
	}
}

package com.sitescape.ef.samples.remoting.client;

import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.sitescape.ef.remoting.api.Entry;
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

	public void printEntry(long binderId, long entryId) {
		Entry entry = this.facade.getEntry(binderId, entryId);
		System.out.println();
		System.out.println("*** Entry(" + entry.getBinderId() + "," + entry.getId() + ")");
		System.out.println("Title: " + entry.getTitle());
		System.out.println();
	}

	public void printEntryAsXML(long binderId, long entryId) {
		String entryAsXML = this.facade.getEntryAsXML(binderId, entryId);
		System.out.println();
		System.out.println("*** Entry(" + binderId + "," + entryId + ")");
		System.out.println(entryAsXML);
		System.out.println();
		try {
			Document document = DocumentHelper.parseText(entryAsXML);
			
			prettyPrint(document);
		} catch (DocumentException e) {
			System.out.println(e);
		}
		System.out.println();
	}
	
	public static void main(String[] args) {
		System.out.println("*** This Facade client uses Spring's jaxrpc proxy");

		System.out.println("*** Reading an entry ***");
		
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
			FacadeClient client = (FacadeClient) beanFactory.getBean("facadeClient");

			client.printEntry(binderId, entryId);
			
			client.printEntryAsXML(binderId, entryId);
		}
		
		System.out.println("*** Adding an entry ***");
	}
	
	private void prettyPrint(Document doc) {
		OutputFormat format = OutputFormat.createPrettyPrint();
		try {
			XMLWriter writer = new XMLWriter(System.out, format);
			writer.write(doc);
		}
		catch(IOException e) {}
	}
}

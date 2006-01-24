package com.sitescape.ef.samples.remoting.client;

import java.io.IOException;
import java.util.Date;

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
	
	public void addEntry(int binderId, String definitionId) {
		StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<entry>")
			.append("<attribute name=\"title\">WS test ")
			.append(new Date().getTime())
			.append("</attribute>")
			.append("<attribute name=\"description\">Added through Web Service</attribute>")
			.append("<attribute name=\"birthDate_date\">21</attribute>")
			.append("<attribute name=\"birthDate_month\">05</attribute>")
			.append("<attribute name=\"birthDate_year\">1992</attribute>")
			.append("<attribute name=\"birthDate_timezoneid\">GMT</attribute>")
			.append("<attribute name=\"colors\">white</attribute>")
			.append("<attribute name=\"colors\">blue</attribute>")
			.append("</entry>");

		long entryId =this.facade.addEntry(binderId, definitionId, sb.toString());
		
		System.out.println("*** ID of the newly created entry is " + entryId);
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

			client.printEntry(binderId, entryId);
			
			client.printEntryAsXML(binderId, entryId);			
		}
		else if(args[0].equals("add")){
			
			System.out.println("*** Adding an entry ***");

			int binderId = Integer.parseInt(args[1]);
			String definitionId = args[2];
			
			client.addEntry(binderId, definitionId);
		}
		else {
			System.out.println("Invalid arguments");
			return;
		}
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

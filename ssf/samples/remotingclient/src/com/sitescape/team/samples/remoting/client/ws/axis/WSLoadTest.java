/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.samples.remoting.client.ws.axis;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.namespace.QName;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.configuration.FileProvider;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.message.token.UsernameToken;

import com.sitescape.team.samples.remoting.client.util.FacadeClientHelper;
import com.sitescape.util.PasswordEncryptor;

public class WSLoadTest
{
	public static void main(String[] args) {
		if(args.length < 3 || args.length > 4) {
			System.err.println("Usage: WSLoadTest <count> <folderId> <definitionId> [<attachmentFilename>]");
			return;
		}
		Integer count = Integer.parseInt(args[0]);
		Long folderId = Long.parseLong(args[1]);
		String definitionId = args[2];
		String filename = null;
		if(args.length == 4) { filename = args[3]; }
		Date start = new Date();
		try {
			for(int i = 0; i < count.intValue(); i++) {
				if((i+1)%10 == 0) {
					System.err.println(i+1);
				}
				String s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><entry>  <attribute name=\"title\" type=\"title\">Load Test Entry"+ i + " " + start.toString()  + "</attribute></entry>";
				Long entryId = (Long) fetch("addFolderEntry", new Object[] {folderId, definitionId, s, filename}, filename);
//				justDoIt("uploadFolderFile", new Object[] {folderId, entryId, "ss_attachFile1", "attachment" + i + ".txt"}, filename);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		System.err.println("Total time: " + ((new Date()).getTime() - start.getTime()) + "ms");
	}
	
	static Object fetch(String operation, Object[] args, String filename) throws Exception {
		// Replace the hostname in the endpoint appropriately.
		String endpoint = "https://localhost:8443/ssf/ws/Facade";

		// Make sure that the client_deploy.wsdd file is accessible to the program.
		EngineConfiguration config = new FileProvider("client_deploy.wsdd");

		Service service = new Service(config);

		Call call = (Call) service.createCall();

		call.setTargetEndpointAddress(new URL(endpoint));

		// We are going to invoke the remote operation to fetch the workspace
		//  or folder to print.
		call.setOperationName(new QName(operation));

		// Programmatically set the username. Alternatively you can specify
		// the username in the WS deployment descriptor client_deploy.wsdd
		// if the username is known at deployment time and does not change
		// between calls, which is rarely the case in Aspen.
		call.setProperty(WSHandlerConstants.USER, "administrator");
		
		if(filename != null) {
			DataHandler dhSource = new DataHandler(new FileDataSource(new File(filename)));
		
			call.addAttachmentPart(dhSource); //Add the file.
        
			call.setProperty(Call.ATTACHMENT_ENCAPSULATION_FORMAT, Call.ATTACHMENT_ENCAPSULATION_FORMAT_DIME);
		}
		
		Object result = call.invoke(args);
		
		org.apache.axis.MessageContext messageContext = call.getMessageContext();
		org.apache.axis.Message returnedMessage = messageContext.getResponseMessage();
		Iterator iteAtta = returnedMessage.getAttachments();
		DataHandler[] dhTab = new DataHandler[returnedMessage.countAttachments()];
		for (int i=0;iteAtta.hasNext();i++) {
			AttachmentPart ap = (AttachmentPart) iteAtta.next();
			dhTab[i] = ap.getDataHandler();
			System.out.println("Filename=" + dhTab[i].getName());
		}
		return result;
	}

	static void justDoIt(String operation, Object[] args, String filename) throws Exception {
		fetch(operation, args, filename);
	}
}
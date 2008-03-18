/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.client.ws;

import java.io.File;
import java.net.URL;
import java.util.Iterator;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.xml.namespace.QName;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.configuration.FileProvider;
import org.apache.ws.security.handler.WSHandlerConstants;

public abstract class WebServiceClient {

	static String hostURL = "http://localhost:8080/";
	static String user = "admin";
	
	Service service = null;
	String endpoint = null;
	
	public WebServiceClient(String endpoint)
	{
		// Replace the hostname in the endpoint appropriately.
		this.endpoint = hostURL + "ssf/ws/" + endpoint;

		// Make sure that the client_deploy.wsdd file is accessible to the program.
		EngineConfiguration config = new FileProvider("client_deploy.wsdd");

		this.service = new Service(config);
	}
	
	public static void setHostURL(String url)
	{
		if(!url.endsWith("/")) {
			url = url + "/";
		}
		hostURL = url;
	}
	public static void setUser(String user)
	{
		WebServiceClient.user = user;
	}
	
	protected Object fetch(String operation, Object[] args) {
		return fetch(operation, args, (DataSource) null);
	}
	
	protected Object fetch(String operation, Object[] args, String filename) {
		if(filename != null) {
			return fetch(operation, args, new FileDataSource(new File(filename)));
		} else {
			return fetch(operation, args);
		}
	}
	
	protected Object fetch(String operation, Object[] args, DataSource attachment) {
		try {
			Call call = (Call) service.createCall();

			call.setTargetEndpointAddress(new URL(endpoint));

			// We are going to invoke the remote operation to fetch the workspace
			//  or folder to print.
			call.setOperationName(new QName(operation));

			// Programmatically set the username. Alternatively you can specify
			// the username in the WS deployment descriptor client_deploy.wsdd
			// if the username is known at deployment time and does not change
			// between calls, which is rarely the case in ICEcore.
			call.setProperty(WSHandlerConstants.USER, user);

			if(attachment != null) {
				DataHandler dhSource = new DataHandler(attachment);
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
				String s = ap.getMimeHeader("Content-Disposition")[0];
				s = s.substring(s.indexOf('"')+1, s.lastIndexOf('"'));
				File src = new File(dhTab[i].getName());
				src.renameTo(new File(s));
			}
			return result;
		} catch(Exception e) {
			throw new RuntimeException("Exception calling remote service", e);
		}
	}
}

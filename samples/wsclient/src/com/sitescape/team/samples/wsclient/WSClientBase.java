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
package com.sitescape.team.samples.wsclient;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.sitescape.team.client.ws.WebServiceClientUtil;

public abstract class WSClientBase {

	protected String host; // optional - default to localhost
	protected String port; // optional - default to 8080
	protected String username; // required
	protected String password; // required
	protected boolean authWSS; // optional - default to wss (the other available value is basic)
	
	protected WSClientBase() {
		// Read intrinsic properties specified with -D switches from the command line.
		host = System.getProperty("host", "localhost");
		
		port = System.getProperty("port", "8080");
		
		username = System.getProperty("username");
		if(username == null)
			throw new IllegalArgumentException("username must be specified with -D switch");
		
		password = System.getProperty("password");
		if(password == null)
			throw new IllegalArgumentException("password must be specified with -D switch");
		
		String authMethod = System.getProperty("authmethod", "wss");
		if(authMethod.equalsIgnoreCase("wss"))
			authWSS = true;
		else if(authMethod.equalsIgnoreCase("basic"))
			authWSS = false;
		else
			throw new IllegalArgumentException("Illegal authmethod value: " + authMethod);			
	}
	
	protected EngineConfiguration getCustomEngineConfiguration() {
		// There are a variety of options to choose from when it comes to 
		// configuring Axis engine. Some of the guidelines are:
		// 
		// If using WS-Security, the engine must be configured with the
		// WS-Security Axis handler (eg. WSS4J). Typically the configuration
		// information is stored in a config file and read in at runtime.
		// See client-config.wsdd file for sample config file. 
		// Alternatively, the configuration information can be put together
		// inside a program at runtime. This method uses the second approach
		// for the purpose of demonstration.
		// 
		// If using HTTP Basic Authentication, on the other hand, the sample
		// program does not require any custom configuration for the Axis
		// engine. Therefore, this method returns null. However, if your
		// program resuires additional custom settings for the engine, 
		// you can either create a configuration file and load it or simply
		// hard-code the configuration information in your program. 
		// It is your choice. 
		//
		// Finally, regardless of the authentication mechanism in use, if your 
		// program is using org.apache.axis.client.Call (as opposed to the
		// WSDL2Java generated stubs provided in the web service client library 
		// ssf-wsclient.jar) to invoke web service operations, you may need to 
		// specify typeMapping information in the config file if you're calling 
		// operations that require the information. If you're using the stubs
		// in the web service client library, there is no need to specify the 
		// typeMapping information since the generated stubs automatically take 
		// care of it. 
		if(authWSS) {
		    java.lang.StringBuffer sb = new java.lang.StringBuffer();
		    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		    sb.append("<deployment xmlns=\"http://xml.apache.org/axis/wsdd/\" xmlns:java=\"http://xml.apache.org/axis/wsdd/providers/java\">\r\n");
		    sb.append("<transport name=\"http\" pivot=\"java:org.apache.axis.transport.http.CommonsHTTPSender\" />\r\n");
		    sb.append("<globalConfiguration >\r\n");
		    sb.append("<requestFlow >\r\n");
		    sb.append("<handler type=\"java:org.apache.ws.axis.security.WSDoAllSender\" >\r\n");
		    sb.append("<parameter name=\"action\" value=\"UsernameToken\"/>\r\n");
		    sb.append("</handler>\r\n");
		    sb.append("</requestFlow >\r\n");
		    sb.append("</globalConfiguration >\r\n");
		    sb.append("</deployment>\r\n");
		    return new org.apache.axis.configuration.XMLStringProvider(sb.toString());
			
		    // Alternatively you can use a configuration file.
			//return  new org.apache.axis.configuration.FileProvider("client-config.wsdd");
		}
		else {
			// If you have custom configuration you can either hard-code it here
			// as below, or use a configuration file to store it.
			// Since our sample program does not require any custom configuration
			// this code is commented out and it simply returns null.
			/*
		    java.lang.StringBuffer sb = new java.lang.StringBuffer();
		    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		    sb.append("<deployment xmlns=\"http://xml.apache.org/axis/wsdd/\" xmlns:java=\"http://xml.apache.org/axis/wsdd/providers/java\">\r\n");
		    sb.append("<transport name=\"http\" pivot=\"java:org.apache.axis.transport.http.CommonsHTTPSender\" />\r\n");
		    sb.append("</deployment>\r\n");
		    return new org.apache.axis.configuration.XMLStringProvider(sb.toString());
		    */

		    return null;
		}
	}
	
	protected String getEndpointAddress(String serviceName) {
		// The endpoint is different depending on the authentication mechanism to be used.
		// If using WS-Security, the endpoint is http://<host>:<port>/ssf/ws/<servicename>
		// With Basic Authentication, it is http://<host>:<port>/ssr/secure/ws/<servicename> 
		StringBuilder sb = new StringBuilder("http://");
		sb.append(host);
		if(port != null)
			sb.append(":").append(port);
		if(authWSS)
			sb.append("/ssf/ws/");
		else
			sb.append("/ssr/secure/ws/");
		sb.append(serviceName);
		return sb.toString();
	}
	
	protected void setUserCredential(Call call) {
		if(authWSS) {
			WebServiceClientUtil.setUserCredentialWSSecurity(call, username, password, true);
		}
		else {
			WebServiceClientUtil.setUserCredentialBasicAuth(call, username, password);
		}
	}
	
	protected Object invokeWithCall(String serviceName, String operation, Object[] args, 
			File inputAttachment, File outputAttachmentsDirectory) throws Exception {
		// Invoke web service operation using Axis Call object.
		Call call = prepareCall(serviceName, operation, args);
		
		if(inputAttachment != null)
			attachInputFile(call, inputAttachment);
		
		Object result = call.invoke(args);
		
		extractOutputFiles(call, outputAttachmentsDirectory);
		
		return result;
	}

	protected Call prepareCall(String serviceName, String operation, Object[] args) throws Exception {
		EngineConfiguration config = getCustomEngineConfiguration();

		Service service = (config == null)? new Service() : new Service(config); 

		Call call = (Call) service.createCall();
		
		String endpointAddr = getEndpointAddress(serviceName);
		
		call.setTargetEndpointAddress(new URL(endpointAddr));

		call.setOperationName(new QName(operation));

		setUserCredential(call);
		
		return call;
	}
	
	protected void attachInputFile(Call call, File inputAttachment) {
		WebServiceClientUtil.attachFile(call, inputAttachment);
	}

	protected void extractOutputFiles(Call call, File outputAttachmentsDirectory) throws Exception {
		WebServiceClientUtil.extractFiles(call, outputAttachmentsDirectory);
	}
	
	public static void printXML(String xml) {
		System.out.println();
		try {
			Document document = DocumentHelper.parseText(xml);
			
			prettyPrintXML(document);
		} catch (DocumentException e) {
			System.out.println(e);
		}
		System.out.println();
	}
	
	public static String readText(File file)
	{
		FileReader reader = null;
		String s = "";
		try {
			reader = new FileReader(file);
			s = readText(reader);
		} catch(IOException e) {
		} finally {
			if(reader != null) {try { reader.close(); } catch(Exception e) {}}
		}
		return s;
	}
	
	public static String readText(String filename)
	{
		FileReader reader = null;
		String s = "";
		try {
			reader = new FileReader(filename);
			s = readText(reader);
		} catch(IOException e) {
		} finally {
			if(reader != null) {try { reader.close(); } catch(Exception e) {}}
		}
		
		return s;
	}
	
	public static String readText(Reader reader)
	{
		StringBuffer buf = new StringBuffer();
		try {
			char in[] = new char[32768];
			int len;
			while((len = reader.read(in, 0, 32767)) > 0) {
				buf.append(in, 0, len);
			}
		} catch(IOException e) {
			System.err.println("Error reading file: " + e);
		}
		return buf.toString();
	}

	private static void prettyPrintXML(Document doc) {
		OutputFormat format = OutputFormat.createPrettyPrint();
		try {
			XMLWriter writer = new XMLWriter(System.out, format);
			writer.write(doc);
		}
		catch(IOException e) {
			System.out.println(e);
		}
	}
	
}

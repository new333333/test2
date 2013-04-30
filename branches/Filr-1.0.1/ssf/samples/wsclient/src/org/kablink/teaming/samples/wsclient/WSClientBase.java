/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.samples.wsclient;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.Calendar;

import javax.xml.namespace.QName;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.kablink.teaming.client.ws.WebServiceClientUtil;

import org.kablink.teaming.client.ws.model.Attachment;
import org.kablink.teaming.client.ws.model.AttachmentsField;
import org.kablink.teaming.client.ws.model.Binder;
import org.kablink.teaming.client.ws.model.BinderBrief;
import org.kablink.teaming.client.ws.model.DefinableEntity;
import org.kablink.teaming.client.ws.model.FileVersion;
import org.kablink.teaming.client.ws.model.FileVersions;
import org.kablink.teaming.client.ws.model.FolderBrief;
import org.kablink.teaming.client.ws.model.FolderCollection;
import org.kablink.teaming.client.ws.model.FolderEntry;
import org.kablink.teaming.client.ws.model.FolderEntryBrief;
import org.kablink.teaming.client.ws.model.FolderEntryCollection;
import org.kablink.teaming.client.ws.model.GroupBrief;
import org.kablink.teaming.client.ws.model.GroupCollection;
import org.kablink.teaming.client.ws.model.Principal;
import org.kablink.teaming.client.ws.model.Tag;
import org.kablink.teaming.client.ws.model.TeamBrief;
import org.kablink.teaming.client.ws.model.TeamCollection;
import org.kablink.teaming.client.ws.model.TemplateBrief;
import org.kablink.teaming.client.ws.model.TemplateCollection;
import org.kablink.teaming.client.ws.model.TrashBrief;
import org.kablink.teaming.client.ws.model.TrashCollection;
import org.kablink.teaming.client.ws.model.User;
import org.kablink.teaming.client.ws.model.ReleaseInfo;
import org.kablink.teaming.client.ws.model.ZoneConfig;

public abstract class WSClientBase {

	Class[] modelClasses = new Class[] {
			org.kablink.teaming.client.ws.model.AttachmentsField.class,
			org.kablink.teaming.client.ws.model.Attachment.class,
			org.kablink.teaming.client.ws.model.AverageRating.class,
			org.kablink.teaming.client.ws.model.Binder.class,
			org.kablink.teaming.client.ws.model.CustomBooleanField.class,
			org.kablink.teaming.client.ws.model.CustomDateField.class,
			org.kablink.teaming.client.ws.model.CustomEventField.class,
			org.kablink.teaming.client.ws.model.DayAndPosition.class,
			org.kablink.teaming.client.ws.model.DefinableEntity.class,
			org.kablink.teaming.client.ws.model.DefinitionBrief.class,
			org.kablink.teaming.client.ws.model.DefinitionCollection.class,
			org.kablink.teaming.client.ws.model.Description.class,
			org.kablink.teaming.client.ws.model.Entry.class,
			org.kablink.teaming.client.ws.model.Event.class,
			org.kablink.teaming.client.ws.model.Field.class,
			org.kablink.teaming.client.ws.model.TrashBrief.class,
			org.kablink.teaming.client.ws.model.TrashCollection.class,
			org.kablink.teaming.client.ws.model.FolderBrief.class,
			org.kablink.teaming.client.ws.model.FolderCollection.class,
			org.kablink.teaming.client.ws.model.FolderEntry.class,
			org.kablink.teaming.client.ws.model.FolderEntryBrief.class,
			org.kablink.teaming.client.ws.model.FolderEntryCollection.class,
			org.kablink.teaming.client.ws.model.FunctionMembership.class,
			org.kablink.teaming.client.ws.model.Group.class,
			org.kablink.teaming.client.ws.model.CustomLongArrayField.class,
			org.kablink.teaming.client.ws.model.Principal.class,
			org.kablink.teaming.client.ws.model.PrincipalBrief.class,
			org.kablink.teaming.client.ws.model.PrincipalCollection.class,
			org.kablink.teaming.client.ws.model.CustomStringArrayField.class,
			org.kablink.teaming.client.ws.model.CustomStringField.class,
			org.kablink.teaming.client.ws.model.SubscriptionStyle.class,
			org.kablink.teaming.client.ws.model.Subscription.class,
			org.kablink.teaming.client.ws.model.Tag.class,
			org.kablink.teaming.client.ws.model.TeamBrief.class,
			org.kablink.teaming.client.ws.model.TeamCollection.class,
			org.kablink.teaming.client.ws.model.TeamMemberCollection.class,
			org.kablink.teaming.client.ws.model.TemplateBrief.class,
			org.kablink.teaming.client.ws.model.TemplateCollection.class,
			org.kablink.teaming.client.ws.model.Timestamp.class,
			org.kablink.teaming.client.ws.model.User.class,
			org.kablink.teaming.client.ws.model.UserBrief.class,
			org.kablink.teaming.client.ws.model.UserCollection.class,
			org.kablink.teaming.client.ws.model.GroupBrief.class,
			org.kablink.teaming.client.ws.model.GroupCollection.class,
			org.kablink.teaming.client.ws.model.Workflow.class,
			org.kablink.teaming.client.ws.model.WorkflowResponse.class,
			org.kablink.teaming.client.ws.model.FileVersions.class,
			org.kablink.teaming.client.ws.model.FileVersion.class,
			org.kablink.teaming.client.ws.model.ReleaseInfo.class,
			org.kablink.teaming.client.ws.model.BinderBrief.class,
			org.kablink.teaming.client.ws.model.ZoneConfig.class
	};

	protected String host; // optional - default to localhost
	protected String port; // optional - default to 8080
	protected String username; // required
	protected String password; // required
	protected boolean authWSS; // optional - default to wss (the other available value is basic)
	protected boolean passwordText; // optional - default to true meaning wsse:PasswordText (this value is applicable only when authWSS is true)
	
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
		
		String authMethod = System.getProperty("authmethod", "wss_text");
		if(authMethod.equalsIgnoreCase("wss_text")) {
			authWSS = true;
			passwordText = true;
		}
		else if(authMethod.equalsIgnoreCase("wss_digest")) {
			authWSS = true;
			passwordText = false;
		}
		else if(authMethod.equalsIgnoreCase("basic")) {
			authWSS = false;
			passwordText = true; // although this value doesn't really matter...
		}
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
		// See client-config-wss.wsdd file for sample config file. 
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
			return WebServiceClientUtil.getMinimumEngineConfigurationWSSecurity();
			
		    // Alternatively you can use a configuration file.
			//return  new org.apache.axis.configuration.FileProvider("client-config-wss.wsdd");
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
		StringBuilder sb = new StringBuilder();
		if(host.startsWith("http"))
			sb.append(host); // it has a scheme
		else
			sb.append("http://").append(host); // default to http
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
			WebServiceClientUtil.setUserCredentialWSSecurity(call, username, password, passwordText);
		}
		else {
			WebServiceClientUtil.setUserCredentialBasicAuth(call, username, password);
		}
	}
	
	protected Object invokeWithCall(String serviceName, String operation, Object[] args, 
			File inputAttachment, File outputAttachmentsDirectory) throws Exception {
		// Invoke web service operation using Axis Call object.
		Call call = prepareCall(serviceName, operation, args);
		
		if(serviceName.equalsIgnoreCase("TeamingServiceV1")) {
			// The old Facade service does not require custom serializers/deserializers
			// to be set up, because it passes arguments only in primitive types.
			// The new TeamingService, however, does require it.
			setupTypeMapping(call);
		}
		
		if(inputAttachment != null)
			attachInputFile(call, inputAttachment);
		
		Object result = call.invoke(args);
		
		extractOutputFiles(call, outputAttachmentsDirectory);
		
		return result;
	}

	protected void setupTypeMapping(Call call) {
		for(int i = 0; i < modelClasses.length; i++) {
			QName qname = new QName("http://model.ws.remoting.teaming.kablink.org/v1", modelClasses[i].getSimpleName());
			call.registerTypeMapping(modelClasses[i], 
					qname, 
					new BeanSerializerFactory(modelClasses[i], qname), 
					new BeanDeserializerFactory(modelClasses[i], qname));
		}
	}
	
	protected Call prepareCall(String serviceName, String operation, Object[] args) throws Exception {
		EngineConfiguration config = getCustomEngineConfiguration();

		Service service = (config == null)? new Service() : new Service(config); 

		Call call = (Call) service.createCall();
		
        // By default, Teaming WS (server-side) has multirefs turned off for better compatibility with other toolkits. 
		// For more compatible testing, we turn off multirefs on the client side as well.
        call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);

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
	
	Object fetch(String serviceName, String operation, Object[] args) throws Exception {
		return fetch(serviceName, operation, args, null);
	}

	void fetchAndPrintXML(String serviceName, String operation, Object[] args) throws Exception {
		String wsTreeAsXML = (String) fetch(serviceName, operation, args);

		printXML(wsTreeAsXML);
	}

	void fetchAndPrintByteArray(String serviceName, String operation, Object[] args) throws Exception {
		byte[] bytes = (byte[]) fetch(serviceName, operation, args);

		System.out.println("Successfully executed " + operation + " on " + serviceName + " returning byte array of size " + bytes.length);
	}

	void fetchAndPrintFileVersions(String serviceName, String operation, Object[] args) throws Exception {
		FileVersions fileVersions = (FileVersions) fetch(serviceName, operation, args);
		printFileVersions(fileVersions);
	}

	void fetchAndPrintDE(String serviceName, String operation, Object[] args) throws Exception {
		DefinableEntity entity = (DefinableEntity) fetch(serviceName, operation, args);

		printDefinableEntity(entity);
	}

	void fetchAndPrintFolderEntry(String serviceName, String operation, Object[] args) throws Exception {
		FolderEntry entry = (FolderEntry) fetch(serviceName, operation, args);
		
		printFolderEntry(entry);
	}

	void fetchAndPrintUser(String serviceName, String operation, Object[] args) throws Exception {
		User user = (User) fetch(serviceName, operation, args);

		printUser(user);
	}

	void fetchAndPrintBinder(String serviceName, String operation, Object[] args) throws Exception {
		Binder binder = (Binder) fetch(serviceName, operation, args);

		printBinder(binder);
	}

	void fetchAndPrintTags(String serviceName, String operation, Object[] args) throws Exception {
		Tag[] tags = (Tag[]) fetch(serviceName, operation, args);
		System.out.println("Number of tags: " + tags.length);
		for(int i = 0; i < tags.length; i++) {
			Tag tag = tags[i];
			System.out.println("Tag " + (i+1) + ": id=" + tag.getId() + ", name=" +
					tag.getName() + ", entity id=" + tag.getEntityId() + ", public=" +
					tag.is_public());
		}
	}

	void fetchAndPrintFEC(String serviceName, String operation, Object[] args) throws Exception {
		FolderEntryCollection fec = (FolderEntryCollection) fetch(serviceName, operation, args);

		System.out.println("First: " + fec.getFirst());
		System.out.println("Total: " + fec.getTotal());
		FolderEntryBrief[] febs = fec.getEntries();
		System.out.println("Size: " + febs.length);
		if(febs != null) {
			for(FolderEntryBrief feb:febs) {
				printFolderEntryBrief(feb);
			}
		}
	}

	void fetchAndPrintDEArray(String serviceName, String operation, Object[] args) throws Exception {
		Object deArray = fetch(serviceName, operation, args);

		printDefinableEntityArray(deArray);
	}

	void fetchAndPrintBBArray(String serviceName, String operation, Object[] args) throws Exception {
		Object bbArray = fetch(serviceName, operation, args);

		printBinderBriefArray((BinderBrief[]) bbArray);
	}

	void fetchAndPrintPrimitiveArray(String serviceName, String operation, Object[] args) throws Exception {
		Object deArray = fetch(serviceName, operation, args);

		printPrimitiveArray(deArray);
	}
	
	void fetchAndPrintACK(String serviceName, String operation, Object[] args) throws Exception {
		fetchAndPrintACK(serviceName, operation, args, null);
	}
	
	void fetchAndPrintFC(String serviceName, String operation, Object[] args) throws Exception {
		FolderCollection fc = (FolderCollection) fetch(serviceName, operation, args);
		printFolderCollection(fc);
	}
	
	void fetchAndPrintGC(String serviceName, String operation, Object[] args) throws Exception {
		GroupCollection gc = (GroupCollection) fetch(serviceName, operation, args);
		printGroupCollection(gc);
	}
	
	void fetchAndPrintTemplateC(String serviceName, String operation, Object[] args) throws Exception {
		TemplateCollection tc = (TemplateCollection) fetch(serviceName, operation, args);
		printTemplateCollection(tc);
	}
	
	void fetchAndPrintTeamC(String serviceName, String operation, Object[] args) throws Exception {
		TeamCollection tc = (TeamCollection) fetch(serviceName, operation, args);
		printTeamCollection(tc);
	}
	
	void fetchAndPrintTRC(String serviceName, String operation, Object[] args) throws Exception {
		TrashCollection tc = (TrashCollection) fetch(serviceName, operation, args);
		printTrashCollection(tc);
	}
	
	void fetchAndPrintACK(String serviceName, String operation, Object[] args, String filename) throws Exception {
		Object object = fetch(serviceName, operation, args, filename);
		System.out.println("Successfully executed " + operation + " on " + serviceName);
		if(object != null) {
			System.out.println("Return type: " + object.getClass().toString());
			System.out.println("Return value: " + object.toString());
		}
		else {
			System.out.println("The operation returns no value");
		}
	}
	
	void fetchAndPrintReleaseInfo(String serviceName, String operation, Object[] args) throws Exception {
		ReleaseInfo ri = (ReleaseInfo) fetch(serviceName, operation, args);
		System.out.println("Product name: " + ri.getProductName());
		System.out.println("Product version: " + ri.getProductVersion());
		System.out.println("Build number: " + ri.getBuildNumber());
		System.out.println("Build date: " + ((ri.getBuildDate() == null)? "null" : ri.getBuildDate().getTime().toString()));
		System.out.println("Server start time: " + ((ri.getServerStartTime() == null)? "null" : ri.getServerStartTime().getTime().toString()));
		System.out.println("License required edition: " + ri.isLicenseRequiredEdition());
		System.out.println("Content version: " + ri.getContentVersion());
	}
	
	void fetchAndPrintZoneConfig(String serviceName, String operation, Object[] args) throws Exception {
		ZoneConfig zc = (ZoneConfig) fetch(serviceName, operation, args);
		System.out.println("FSA enabled: " + zc.isFsaEnabled());
		System.out.println("FSA synch interval: " + zc.getFsaSynchInterval());
		System.out.println("FSA auto update URL: " + zc.getFsaAutoUpdateUrl());
		System.out.println("FSA max file size: " + zc.getFsaMaxFileSize());
		System.out.println("Mobile access enabled: " + zc.isMobileAccessEnabled());
		System.out.println("Disk quotas enabled: " + zc.isDiskQuotasEnabled());
		System.out.println("Disk quota user default: " + zc.getDiskQuotaUserDefault());
		System.out.println("Disk quota high water percentage: " + zc.getDiskQuotasHighwaterPercentage());
		System.out.println("File size limit user default: " + zc.getFileSizeLimitUserDefault());
		System.out.println("Binder quotas initialized: " + zc.isBinderQuotasInitialized());
		System.out.println("Binder quotas enabled: " + zc.isBinderQuotasEnabled());
		System.out.println("Binder quotas allow owner: " + zc.isBinderQuotasAllowOwner());
		System.out.println("File version max age: " + zc.getFileVersionsMaxAge());
	}
	
	void printFolderEntry(FolderEntry entry) {
		printDefinableEntity(entry);
		System.out.println("Reserved by: " + entry.getReservedBy());
		System.out.println("Description: " + entry.getDescription().getText());
	}
	
	void printDefinableEntity(DefinableEntity entity) {
		if(entity != null) {
			System.out.println("Entity class: " + entity.getClass().getName());
			System.out.println("Entity ID: " + entity.getId());
			System.out.println("Entity title: " + entity.getTitle());
			System.out.println("Entity family: " + entity.getFamily());
			System.out.println("Entity creation principal name: "  + entity.getCreation().getPrincipal());
			System.out.println("Entity creation principal id: "  + entity.getCreation().getPrincipalId());
			System.out.println("Entity creation date: "  + entity.getCreation().getDate().getTime().toString());
			System.out.println("Entity modification principal name: "  + entity.getModification().getPrincipal());
			System.out.println("Entity modification principal id: "  + entity.getModification().getPrincipalId());
			System.out.println("Entity modification date: "  + entity.getModification().getDate().getTime().toString());
			AttachmentsField aField = entity.getAttachmentsField();
			if(aField != null) {
				System.out.println("Attachments name: " + aField.getName());
				System.out.println("Attachments type: " + aField.getType());
				System.out.println("Attachments size: " + aField.getAttachments().length);
				Attachment[] attachments = aField.getAttachments();
				for(int i = 0; i < attachments.length; i++) {
					System.out.println("Attachment " + i + " file name: " + attachments[i].getFileName());
					System.out.println("Attachment " + i + " file href: " + attachments[i].getHref());
					System.out.println("Attachment " + i + " file id: " + attachments[i].getId());
					System.out.println("Attachment " + i + " major version: " + attachments[i].getMajorVersion());
					System.out.println("Attachment " + i + " minor version: " + attachments[i].getMinorVersion());
					System.out.println("Attachment " + i + " note: " + attachments[i].getNote());
					System.out.println("Attachment " + i + " status: " + attachments[i].getStatus());
					System.out.println("Attachment " + i + " locked by: " + attachments[i].getLockedBy());
					System.out.println("Attachment " + i + " lock expiration: " + 
							((attachments[i].getLockExpiration() != null)? attachments[i].getLockExpiration().getTime().toString():null));
				}
			}
			else {
				System.out.println("No attachments field returned");
			}
			System.out.println("PermaLink: " + entity.getPermaLink());
			if(entity instanceof FolderEntry) {
				System.out.println("Folder entry href: " + ((FolderEntry)entity).getHref());
			}
		}
		else {
			System.out.println("No entity returned");
		}
	}
	
	void printBinder(Binder binder) {
		printDefinableEntity(binder);
		System.out.println("Path: " + binder.getPath());
		System.out.println("Family: " + binder.getFamily());
		System.out.println("Library: " + binder.getLibrary());
		System.out.println("Mirrored: " + binder.getMirrored());
		System.out.println("Parent binder ID: " + binder.getParentBinderId());
	}
	
	void printUser(User user) {
		printPrincipal(user);
		System.out.println("First name: " + user.getFirstName());
		System.out.println("Midele name: " + user.getMiddleName());
		System.out.println("Last name: " + user.getLastName());
		System.out.println("Organization: " + user.getOrganization());
		System.out.println("Phone: " + user.getPhone());
		System.out.println("Zon name: " + user.getZonName());
		System.out.println("Locale language: " + user.getLocaleLanguage());
		System.out.println("Locale country: " + user.getLocaleCountry());
		System.out.println("Timezone: " + user.getTimeZone());
		System.out.println("Skype id: " + user.getSkypeId());
		System.out.println("Twitter id: " + user.getTwitterId());
		System.out.println("Mini blog id: " + user.getMiniBlogId());
		System.out.println("Disk quota: " + user.getDiskQuota());
		System.out.println("File size limit: " + user.getFileSizeLimit());
		System.out.println("Disk space used: " + user.getDiskSpaceUsed());
		System.out.println("Max groups quota: " + user.getMaxGroupsQuota());
		System.out.println("Max groups file size limit: " + user.getMaxGroupsFileSizeLimit());
		System.out.println("Workspace ID: " + user.getWorkspaceId());
	}
	
	void printPrincipal(Principal principal) {
		printDefinableEntity(principal);
		System.out.println("Disabled: " + principal.isDisabled());
		System.out.println("Email address: " + principal.getEmailAddress());
		System.out.println("Name: " + principal.getName());
		System.out.println("Reserved: " + principal.isReserved());
	}
	
	void printFileVersions(FileVersions fileVersions) {
		System.out.println("File name: " + fileVersions.getFileName());
		FileVersion[] versions = fileVersions.getVersions();
		System.out.println("File versions size: " + versions.length);
		for(int i = 0; i < versions.length; i++) {
			System.out.println("File version " + i + " id: " + versions[i].getId());
			System.out.println("File version " + i + " length: " + versions[i].getLength());
			System.out.println("File version " + i + " number: " + versions[i].getVersionNumber());
			System.out.println("File version " + i + " major version: " + versions[i].getMajorVersion());
			System.out.println("File version " + i + " minor version: " + versions[i].getMinorVersion());
			System.out.println("File version " + i + " href: " + versions[i].getHref());
			System.out.println("File version " + i + " note: " + versions[i].getNote());
			System.out.println("File version " + i + " status: " + versions[i].getStatus());
		}
	}
	
	void printFolderCollection(FolderCollection fc) {
		System.out.println("First: " + fc.getFirst());
		System.out.println("Total: " + fc.getTotal());
		FolderBrief[] fb = fc.getFolders();
		System.out.println("Number of folders: " + fb.length);
		for(int i = 0; i < fb.length; i++) {
			System.out.println();
			System.out.println("Folder " + i + " id: " + fb[i].getId());
			System.out.println("Folder " + i + " title: " + fb[i].getTitle());
			System.out.println("Folder " + i + " family: " + fb[i].getFamily());
			System.out.println("Folder " + i + " parent binder id: " + fb[i].getParentBinderId());
			System.out.println("Folder " + i + " is library: " + fb[i].getLibrary());
			System.out.println("Folder " + i + " is mirrored: " + fb[i].getMirrored());
			System.out.println("Folder " + i + " path: " + fb[i].getPath());
			System.out.println("Folder " + i + " permalink: " + fb[i].getPermaLink());
		}
	}
	
	void printGroupCollection(GroupCollection gc) {
		GroupBrief[] gb = gc.getGroups();
		System.out.println("Number of groups: " + gb.length);
		for(int i = 0; i < gb.length; i++) {
			System.out.println();
			System.out.println("Group " + i + " id: " + gb[i].getId());
			System.out.println("Group " + i + " name: " + gb[i].getName());
			System.out.println("Group " + i + " title: " + gb[i].getTitle());
		}
	}
	
	void printTemplateCollection(TemplateCollection tc) {
		TemplateBrief[] fb = tc.getTemplates();
		System.out.println("Number of templates: " + fb.length);
		for(int i = 0; i < fb.length; i++) {
			System.out.println();
			System.out.println("Template " + i + " id: " + fb[i].getId());
			System.out.println("Template " + i + " title: " + fb[i].getTitle());
			System.out.println("Template " + i + " family: " + fb[i].getFamily());
			System.out.println("Template " + i + " name: " + fb[i].getName());
			System.out.println("Template " + i + " internalId: " + fb[i].getInternalId());
			System.out.println("Template " + i + " definitionType: " + fb[i].getDefinitionType());
		}
	}
	
	void printTeamCollection(TeamCollection tc) {
		TeamBrief[] fb = tc.getTeams();
		System.out.println("Number of teams: " + fb.length);
		for(int i = 0; i < fb.length; i++) {
			System.out.println();
			System.out.println("Team " + i + " id: " + fb[i].getId());
			System.out.println("Team " + i + " title: " + fb[i].getTitle());
			System.out.println("Team " + i + " family: " + fb[i].getFamily());
			System.out.println("Team " + i + " definitionType: " + fb[i].getDefinitionType());
			System.out.println("Team " + i + " path: " + fb[i].getPath());
			System.out.println("Team " + i + " permalink: " + fb[i].getPermaLink());
		}
	}
	
	void printTrashCollection(TrashCollection tc) {
		TrashBrief[] fb = tc.getTrashEntries();
		System.out.println("Number of trash entries: " + fb.length);
		for(int i = 0; i < fb.length; i++) {
			System.out.println();
			System.out.println("Trash entry " + i + " isBinder: " + fb[i].isBinder());
			System.out.println("Trash entry " + i + " isFolderEntry: " + fb[i].isFolderEntry());
			System.out.println("Trash entry " + i + " binderBrief: ");
			printBinderBrief(fb[i].getBinderBrief());
			System.out.println("Trash entry " + i + " folderEntryBrief: ");
			printFolderEntryBrief(fb[i].getFolderEntryBrief());
		}
	}
	
	void printFolderEntryBrief(FolderEntryBrief feb) {
		System.out.println();
		System.out.println("ID: " + feb.getId());
		System.out.println("Creation principal name: " + feb.getCreation().getPrincipal());
		System.out.println("Creation principal id: " + feb.getCreation().getPrincipalId());
		System.out.println("Creation date: " + feb.getCreation().getDate().getTime().toString());
		System.out.println("Modification principal name: " + feb.getModification().getPrincipal());
		System.out.println("Modification principal id: " + feb.getModification().getPrincipalId());
		System.out.println("Modification date: " + feb.getModification().getDate().getTime().toString());
		System.out.println("Definition ID: " + feb.getDefinitionId());
		System.out.println("Doc Level: " + feb.getDocLevel());
		System.out.println("Doc Number: " + feb.getDocNumber());
		System.out.println("Title: " + feb.getTitle());
		System.out.println("Family: " + feb.getFamily());
		System.out.println("Href: " + feb.getHref());
		System.out.println("Permalink: " + feb.getPermaLink());
		String[] fileNames = feb.getFileNames();
		if(fileNames != null) {
			System.out.println("Number of files: " + fileNames.length);
			for(String fileName:fileNames)
				System.out.println("    File: " + fileName);
		}
		else {
			System.out.println("No files");
		}		
	}
	
	void printDefinableEntityArray(Object array) {
		if(array != null) {
			if(array instanceof User[]) {
				User[] uArray = (User[]) array;
				System.out.println("Array size is " + uArray.length);
				for(User u:uArray)
					printDefinableEntity(u);
			}
			else {
				System.out.println("Unrecognized array type: " + array.getClass().getName()); 
			}
		}
		else {
			System.out.println("Null array");
		}
	}
	
	void printBinderBriefArray(BinderBrief[] array) {
		if(array != null) {
			System.out.println("BinderBrief array size: " + array.length);
			for(BinderBrief ff:array)
				printBinderBrief(ff);
		}
		else {
			System.out.println("Null array");
		}
	}
	
	void printBinderBrief(BinderBrief bb) {
		if(bb != null) {
			StringBuilder sb = new StringBuilder();
			sb.append("id=").append(bb.getId())
			.append(", title=").append(bb.getTitle())
			.append(", family=").append(bb.getFamily())
			.append(", library=").append(bb.getLibrary())
			.append(", definitionType=").append(bb.getDefinitionType())
			.append(", path=").append(bb.getPath());
			System.out.println(sb.toString());
		}
		else {
			System.out.println(bb);
		}
	}
	
	void printPrimitiveArray(Object array) {
		if(array != null) {
			if(array instanceof long[]) {
				long[] lArray = (long[]) array;
				System.out.println("Array size is " + lArray.length);
				for(long l:lArray)
					System.out.println(l);
			}
			else if(array instanceof int[]) {
				int[] iArray = (int[]) array;
				System.out.println("Array size is " + iArray.length);
				for(int i:iArray)
					System.out.println(i);
			}
			else if(array instanceof boolean[]) {
				boolean[] bArray = (boolean[]) array;
				System.out.println("Array size is " + bArray.length);
				for(boolean b:bArray)
					System.out.println(b);
			}
			else {
				System.out.println("Unrecognized array type: " + array.getClass().getName()); 
			}
		}
		else {
			System.out.println("Null array");
		}
	}
	
	void fetchAndPrintIdentifier(String serviceName, String operation, Object[] args) throws Exception {
		fetchAndPrintIdentifier(serviceName, operation, args, null);
	}
	
	void fetchAndPrintIdentifier(String serviceName, String operation, Object[] args, String filename) throws Exception {
		Long ident = (Long) fetch(serviceName, operation, args, filename);

		System.out.println(ident);
	}

	void fetchAndPrintString(String serviceName, String operation, Object[] args) throws Exception {
		fetchAndPrintString(serviceName, operation, args, null);
	}
	
	void fetchAndPrintString(String serviceName, String operation, Object[] args, String filename) throws Exception {
		String str = (String) fetch(serviceName, operation, args, filename);

		System.out.println(str);
	}
	
	void fetchAndPrintCalendar(String serviceName, String operation, Object[] args) throws Exception {
		Calendar cal = (Calendar) fetch(serviceName, operation, args);

		System.out.println(cal.getTime());
	}

	Object fetch(String serviceName, String operation, Object[] args, String filename) throws Exception {
		return invokeWithCall(serviceName, operation, args, ((filename != null)? new File(filename) : null), null);
	}

	protected static String[] split(String s) {
		String[] ss = s.split(",");
		if(ss.length == 1 && ss[0].length() == 0)
			ss = new String[0];
		return ss;
	}
	
	protected static long[] splitLong(String s) {
		String[] ss = split(s);
		long[] l = new long[ss.length];
		for(int i = 0; i < ss.length; i++)
			l[i] = Long.valueOf(ss[i]).longValue();
		return l;
	}

	protected static Long stringToLongOrNull(String str) {
		try {
			return Long.valueOf(str);
		}
		catch(NumberFormatException e) {
			return null;
		}
	}
	
	protected static Boolean stringToBooleanOrNull(String str) {
		try {
			if(str.equalsIgnoreCase("true"))
				return Boolean.TRUE;
			else if(str.equalsIgnoreCase("false"))
				return Boolean.FALSE;
			else
				return null;
		}
		catch(NumberFormatException e) {
			return null;
		}
	}
}

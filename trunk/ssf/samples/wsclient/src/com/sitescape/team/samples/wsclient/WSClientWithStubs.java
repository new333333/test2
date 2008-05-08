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

import org.apache.axis.EngineConfiguration;

import com.sitescape.team.client.ws.TeamingServiceSoapBindingStub;
import com.sitescape.team.client.ws.TeamingServiceSoapServiceLocator;
import com.sitescape.team.client.ws.WebServiceClientUtil;
import com.sitescape.team.client.ws.model.FolderEntry;
import com.sitescape.team.client.ws.model.FolderEntryBrief;
import com.sitescape.team.client.ws.model.FolderEntryCollection;
import com.sitescape.team.client.ws.model.Principal;
import com.sitescape.team.client.ws.model.PrincipalBrief;
import com.sitescape.team.client.ws.model.PrincipalCollection;

/**
 * This WS client program uses JAX-RPC compliant client binding classes 
 * generated by Axis' WSDL2Java tool.
 * 
 * @author jong
 *
 */
public class WSClientWithStubs {

	private static final String TEAMING_SERVICE_ADDRESS_WSS 	= "http://localhost:8080/ssf/ws/TeamingService";
	private static final String TEAMING_SERVICE_ADDRESS_BASIC 	= "http://localhost:8080/ssr/secure/ws/TeamingService";
	
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "test";
	
	public static void main(String[] args) throws Exception {
		//getFolderEntryWSSecurity(85, 47, true);
		
		//getFolderEntry(85, 47, false);
		
		//getFolderEntries(85);
		
		//getPrincipal(2, 1);
		
		getPrincipals(2, 5);
	}
	
	public static void getFolderEntryWSSecurity(long binderId, long entryId, boolean includeAttachments) throws Exception {
		// Use WS-Security
		
		EngineConfiguration config = WebServiceClientUtil.getMinimumEngineConfigurationWSSecurity();
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator(config);
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_WSS);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialWSSecurity(stub, USERNAME, PASSWORD, true);

		FolderEntry entry = stub.folder_getFolderEntry(null, binderId, entryId, includeAttachments);
		
		System.out.println("(WSS) Entry title: " + entry.getTitle());
		
		System.out.println("(WSS) Number of attachments downloaded = " + WebServiceClientUtil.extractFiles(stub, null));
	}

	public static void getFolderEntry(long binderId, long entryId, boolean includeAttachments) throws Exception {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_BASIC);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialBasicAuth(stub, USERNAME, PASSWORD);

		FolderEntry entry = stub.folder_getFolderEntry(null, binderId, entryId, includeAttachments);
		
		System.out.println("Entry title: " + entry.getTitle());
		
		System.out.println("Number of attachments downloaded = " + WebServiceClientUtil.extractFiles(stub, null));
	}

	public static void getFolderEntries(long binderId) throws Exception {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_BASIC);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialBasicAuth(stub, USERNAME, PASSWORD);

		FolderEntryCollection result = stub.folder_getFolderEntries(null, binderId);
		FolderEntryBrief[] entries = result.getEntries();
				
		System.out.println("Number of entries = " + entries.length);
		for(int i = 0; i < entries.length; i++) {
			System.out.println("(" + (i+1) + ") id=" + entries[i].getId() + ", title=" + entries[i].getTitle()); 
		}
	}

	public static void getPrincipal(long binderId, long principalId) throws Exception {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_BASIC);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialBasicAuth(stub, USERNAME, PASSWORD);
		
		Principal principal = stub.profile_getPrincipal(null, binderId, principalId);
		
		System.out.println("Principal title: " + principal.getTitle());
	}

	public static void getPrincipals(int first, int max) throws Exception {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_BASIC);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialBasicAuth(stub, USERNAME, PASSWORD);

		PrincipalCollection result = stub.profile_getPrincipals(null, first, max);
		PrincipalBrief[] entries = result.getEntries();
				
		System.out.println("First = " + result.getFirst());
		System.out.println("Count = " + entries.length);
		System.out.println("Total = " + result.getTotal());
		for(int i = 0; i < entries.length; i++) {
			System.out.println("(" + i + ") id=" + entries[i].getId() + ", name=" + entries[i].getName() + ", type=" + entries[i].getType() + ", title=" + entries[i].getTitle() + ", email=" + entries[i].getEmailAddress()); 
		}
	}


}

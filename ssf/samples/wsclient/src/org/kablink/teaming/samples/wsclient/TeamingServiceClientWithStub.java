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
package org.kablink.teaming.samples.wsclient;

import static org.kablink.util.search.Restrictions.between;
import static org.kablink.util.search.Restrictions.eq;
import static org.kablink.util.search.Restrictions.in;

import java.io.File;
import java.util.List;
import org.apache.axis.EngineConfiguration;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.kablink.teaming.client.ws.WebServiceClientUtil;

import org.kablink.teaming.client.ws.TeamingServiceSoapBindingStub;
import org.kablink.teaming.client.ws.TeamingServiceSoapServiceLocator;
import org.kablink.teaming.client.ws.model.*;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;

/**
 * This WS client program uses JAX-RPC compliant client binding classes 
 * generated by Axis' WSDL2Java tool.
 * 
 * @author jong
 *
 */
public class TeamingServiceClientWithStub {

	private static final String TEAMING_SERVICE_ADDRESS_WSS 	= "http://localhost:8080/ssf/ws/TeamingServiceV1";
	private static final String TEAMING_SERVICE_ADDRESS_BASIC 	= "http://localhost:8080/ssr/secure/ws/TeamingServiceV1";
	private static final String TEAMING_SERVICE_ADDRESS_TOKEN   = "http://localhost:8080/ssr/token/ws/TeamingServiceV1";

	
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin";
	
	public static void main(String[] args) throws Exception {
		FolderEntry entry;
		testApplicationScopedToken();
		//calendarSync();
		//checkUsers();
		//checkGroups();
		//checkBinder();
		//checkEntry();
		//getFolderEntryWSSecurity(47, true);
		//getFolderEntry(6, false);
		
		// Test add
		//entry = getFolderEntry(80, false);
		//addFolderEntryByCopying(entry);
		
		// Test modify
		//entry = getFolderEntry(80, false);
		//modifyFolderEntry(entry);
		
		// Test delete
		//deleteFolderEntry(47);
		
		// Upload files
		//uploadFolderEntryFiles(85);
		
		//getFolderEntries(33);
		
		//getPrincipal(1);
		
		//getPrincipals(2, 5);
		
/*		try {
			getEntryFileVersions(9, "debug.doc");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		try {
			getEntryFileVersions(9, "non-existing.file");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		try {
			getEntryFileVersions(99999, "non-existing.entry");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
*/
	}
	
	private static void callGetTeamsUsingToken(String token) throws Exception {
		System.out.println(token);
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_TOKEN);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		try {
			stub.search_getTeams(token);
		}
		catch(Exception e) {
			System.out.println(e.toString());
		}
	}
	
	public static void testApplicationScopedToken() throws Exception {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_BASIC);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialBasicAuth(stub, USERNAME, PASSWORD);
		
		// Non-existing application ID
		String token = stub.getApplicationScopedToken(null, 12345, new Long(7));
		callGetTeamsUsingToken(token);
		stub.destroyApplicationScopedToken(null, token);
		
		// Non-existing user ID
		token = stub.getApplicationScopedToken(null, 8, new Long(777));
		callGetTeamsUsingToken(token);
		stub.destroyApplicationScopedToken(null, token);
		
		// Existing application ID and user ID
		token = stub.getApplicationScopedToken(null, 8, new Long(7));
		callGetTeamsUsingToken(token);
		stub.destroyApplicationScopedToken(null, token);
	}
	
	public static void calendarSync() throws Exception {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_BASIC);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialBasicAuth(stub, USERNAME, PASSWORD);
		User testUser = stub.profile_getUserByName(null, "kelly", false);
		Long wsId = testUser.getWorkspaceId();
		
		//search for calendar folder
    	Criteria crit = new Criteria()
			.add(eq(Constants.BINDERS_PARENT_ID_FIELD, wsId.toString())) //child 
			.add(eq(Constants.FAMILY_FIELD, Constants.FAMILY_FIELD_CALENDAR))
			.add(eq(Constants.ENTITY_FIELD, "folder"));
    	
    	Document xmlResults = DocumentHelper.parseText(stub.search_search(null, crit.toQuery().asXML(), 0, -1));
    	//grab the first calendar
    	List<Element> binders = xmlResults.getRootElement().selectNodes("/searchResults/binder");
    	if (binders == null || binders.isEmpty()) {
    		System.out.println("User calendar not found");
    		return;
    	}
    	Element calEle =  binders.get(0);
    	Long calId = Long.valueOf(calEle.attributeValue("id"));
    	
    	crit = new Criteria()
    		.add(eq(Constants.BINDER_ID_FIELD, calId.toString()))
    		.add(eq(Constants.ENTRY_TYPE_FIELD, Constants.ENTRY_TYPE_ENTRY));//no replies
    	
    	//now search for calendar entries
    	xmlResults = DocumentHelper.parseText(stub.search_search(null, crit.toQuery().asXML(), 0, 100));
    	List<Element> entries = xmlResults.getRootElement().selectNodes("/searchResults/entry");
    	if (entries == null || entries.isEmpty()) {
    		System.out.println("Calendar empty");
    		return;
    	}
    	for (Element eEle:entries) {
    		Long id = Long.valueOf(eEle.attributeValue("id"));
    		FolderEntry entry = stub.folder_getEntry(null, id, true);
    		WebServiceClientUtil.extractFiles(stub, new File("icals"));
    		System.out.println(entry.getTitle());
    	}
	}
	public static void checkTags(long binderId) throws Exception {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_BASIC);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialBasicAuth(stub, USERNAME, PASSWORD);
		Tag[] tags = setupTags(binderId);
		for (int i=0; i<tags.length; ++i) {
			stub.binder_setTag(null, tags[i]);			
		}
		tags = stub.binder_getTags(null, binderId);
		validateTags(tags);
		stub.binder_deleteTag(null, binderId, tags[0].getId());
		tags = stub.binder_getTags(null, binderId);
		if (tags.length != 1) System.out.println("Number of tags expected=1, actual=" + tags.length);
		System.out.println("End tags");
		
	}
	public static void checkEntryTags(long entryId) throws Exception {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_BASIC);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialBasicAuth(stub, USERNAME, PASSWORD);
		Tag[] tags = setupTags(entryId);
		for (int i=0; i<tags.length; ++i) {
			stub.folder_setEntryTag(null, tags[i]);			
		}
		tags = stub.folder_getEntryTags(null, entryId);
		validateTags(tags);
		stub.folder_deleteEntryTag(null, entryId, tags[0].getId());
		tags = stub.folder_getEntryTags(null, entryId);
		if (tags.length != 1) System.out.println("Number of tags expected=1, actual=" + tags.length);
		System.out.println("End tags");
		
	}
	private static Tag[] setupTags(long entityId) {
		Tag tag1 = new Tag();
		tag1.setEntityId(entityId);
		tag1.setName("tag1");
		Tag tag2 = new Tag();
		tag2.setName("tag2");
		tag2.setEntityId(entityId);
		return new Tag[] {tag1, tag2};
	}
	private static void validateTags(Tag[] tags) {
		if (tags.length != 2) System.out.println("Number of tags expected=2, actual=" + tags.length);
		for (int i=0; i<tags.length; ++i) {			
			if (!tags[i].getName().equals("tag1") && !tags[i].getName().equals("tag2")) {
				System.out.println("Unexpected tag=" + tags[i].getName());
			}
		}		
	}
	public static void checkBinder() throws Exception {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_BASIC);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialBasicAuth(stub, USERNAME, PASSWORD);

		Binder testFolder1 = getTestFolder("MyTestingFolder");
		Binder testFolder2 = new Binder();
		testFolder2.setTitle("MyTestingFolder3");
		testFolder2.setDefinitionId(testFolder1.getDefinitionId());
		testFolder2.setParentBinderId(testFolder1.getId());
		try {
			stub.binder_indexBinder(null, testFolder1.getId());
			stub.binder_indexTree(null, testFolder1.getId());
			//add new binder without config
			long testFolder2Id = stub.binder_addBinder(null, testFolder2);
			checkTags(testFolder1.getId());
			checkBinderSubscriptions(testFolder1.getId());
			//move new binder to under global
			stub.binder_moveBinder(null, testFolder2Id, testFolder1.getParentBinderId());
			//copy binder back to under test folder
			long testFolder3Id = stub.binder_copyBinder(null, testFolder2Id, testFolder1.getId(), true);			
			stub.binder_deleteBinder(null, testFolder2Id, true);
			Binder testFolder3 = modifyBinder(stub.binder_getBinder(null, testFolder3Id, false));
			testFolder3 = uploadBinderFiles(testFolder3);
		} finally {
			stub.binder_deleteBinder(null, testFolder1.getId(), true);
		}
	}
	public static void checkEntrySubscriptions(long entryId) throws Exception {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_BASIC);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialBasicAuth(stub, USERNAME, PASSWORD);
		Subscription subscription = setupSubscription(entryId);
		stub.folder_setSubscription(null, entryId, subscription);
		subscription = stub.folder_getSubscription(null, entryId);
		validateSubscription(subscription);
	}
	public static void checkBinderSubscriptions(long binderId) throws Exception {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_BASIC);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialBasicAuth(stub, USERNAME, PASSWORD);
		Subscription subscription = setupSubscription(binderId);
		stub.binder_setSubscription(null, binderId, subscription);
		subscription = stub.binder_getSubscription(null, binderId);
		validateSubscription(subscription);
	}

	private static Subscription setupSubscription(long entityId) {
		SubscriptionStyle style3 = new SubscriptionStyle();
		SubscriptionStyle style2 = new SubscriptionStyle();
		style3.setStyle(3);
		style3.setEmailTypes(new String[] {"_primary","_mobile"});
		style2.setStyle(2);
		style2.setEmailTypes(new String[] {"_text","_mobile"});
		Subscription subscription = new Subscription();
		subscription.setStyles(new SubscriptionStyle[] {style3, style2});
		return subscription;
	}
	private static void validateSubscription(Subscription subscription) {
		SubscriptionStyle[] styles = subscription.getStyles();
		if (styles.length != 2) System.out.println("Expect 2 subscriptions styles, got " + styles.length);
		for (int i=0; i<styles.length; ++i) {
			String [] emails = styles[i].getEmailTypes();
			if (emails.length != 2) System.out.println("Expect 2 emailTypes, got " + emails.length);
			if (styles[i].getStyle() == 1) {
				if (emails[0].equals("_primary") || emails[0].equals("_mobile")) continue;
				if (emails[1].equals("_primary") || emails[1].equals("_mobile")) continue;
				System.out.println("Unexpected emailTypes " + emails);
				
			} else if (styles[i].getStyle() == 2) {
				if (emails[0].equals("_text") || emails[0].equals("_mobile")) continue;
				if (emails[1].equals("_text") || emails[1].equals("_mobile")) continue;				
				System.out.println("Unexpected emailTypes " + emails);
			}			
		}
		
	}
	public static void checkEntry()  throws Exception {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_BASIC);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialBasicAuth(stub, USERNAME, PASSWORD);
		Binder testFolder = getTestFolder("MyTestingFolder");
		try {
			FolderEntry testEntry = new FolderEntry();
			testEntry.setDescription(new Description(1,"This entry was added by web-services.  Workflow is next"));
			testEntry.setParentBinderId(testFolder.getId());
			testEntry.setTitle("Added by web-services");
			File file = new File("C:/junk/junk1/debug.txt");		
			WebServiceClientUtil.attachFile(stub, file);
			long testEntryId = stub.folder_addEntry(null, testEntry, "debug1.txt");
			testEntry = stub.folder_getEntry(null, testEntryId, true);
			System.out.println("(WSS) Number of attachments downloaded = " + WebServiceClientUtil.extractFiles(stub, null));
			checkWorkflow(testEntryId);
			checkEntryTags(testEntryId);
			checkEntrySubscriptions(testEntryId);
			stub.folder_setRating(null, testEntryId, 4);
			testEntry = stub.folder_getEntry(null, testEntryId, false);
			if (!testEntry.getAverageRating().getAverageRating().equals(Double.valueOf(4))) System.out.println("Error unexpected average ");
			file = new File("C:/junk/junk1/debug.txt");		
			WebServiceClientUtil.attachFile(stub, file);
			stub.folder_uploadFile(null, testEntry.getId(), "", "debug2.txt");
			testEntry = stub.folder_getEntry(null, testEntryId, true);
			System.out.println("(WSS) Number of attachments downloaded = " + WebServiceClientUtil.extractFiles(stub, null));

			long copiedEntryId = stub.folder_copyEntry(null, testEntryId, testFolder.getId());
			Binder testFolder2 = getTestFolder("MyTestingFolder2");
			stub.folder_moveEntry(null, copiedEntryId, testFolder2.getId());
			FolderEntry movedEntry = stub.folder_getEntry(null, copiedEntryId, false);
			if (!movedEntry.getParentBinderId().equals(Long.valueOf(testFolder2.getId()))) System.out.println("Error - entry not moved");
			stub.binder_deleteBinder(null, testFolder2.getId(), true);
			stub.folder_deleteEntry(null, testEntryId);
		} finally {
			stub.binder_deleteBinder(null, testFolder.getId(), true);
		}
		
	}
	public static void checkWorkflow(long entryId)  throws Exception {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_BASIC);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialBasicAuth(stub, USERNAME, PASSWORD);
		FolderEntry	entry = stub.folder_getEntry(null, entryId, false);
		DefinitionBrief def = stub.definition_getLocalDefinitionByName(null, entry.getParentBinderId(), "testworkflow", true);
		stub.folder_addEntryWorkflow(null, entryId, def.getId());
		entry = stub.folder_getEntry(null, entryId, false);
		Workflow[] wfs = entry.getWorkflows();
		boolean found = false;
		for (int i=0; i<wfs.length; ++i) {
			if ("state1".equals(wfs[i].getState())) {
				found=true;
				break;
			}
		}
		if (!found) System.out.println("Error, expecting state1");
		stub.folder_modifyWorkflowState(null, entryId, wfs[0].getTokenId(), "state2");
		entry = stub.folder_getEntry(null, entryId, false);
		wfs = entry.getWorkflows();
		found = false;
		for (int i=0; i<wfs.length; ++i) {
			if ("state2".equals(wfs[i].getState())) {
				found=true;
				break;
			}
		}
		if (!found) System.out.println("Error, expecting state2");
		stub.folder_setWorkflowResponse(null,  entryId, wfs[0].getTokenId(), "dayquestion", "sundayresponse");
		entry = stub.folder_getEntry(null, entryId, false);
		wfs	= entry.getWorkflows();
		found = false;
		for (int i=0; i<wfs.length; ++i) {
			if ("itssunday".equals(wfs[i].getState())) {
				WorkflowResponse[] wfr = wfs[i].getResponses();
				if (wfr.length == 1) {
					if ("dayquestion".equals(wfr[0].getQuestion()) &&
							"sundayresponse".equals(wfr[0].getResponse())) {
						found=true;
						break;
						
					}
				}
			}
		}
		if (!found) System.out.println("Error, expecting itssunday with response sundayresponse");
		int count = wfs.length;
		stub.folder_deleteEntryWorkflow(null,  entryId, def.getId());
		entry = stub.folder_getEntry(null,  entryId, false);
		wfs = entry.getWorkflows();
		if (wfs.length != count-1) System.out.println("Error workflow not deleted");
		
	}
	
	public static FolderEntry getFolderEntryWSSecurity(long entryId, boolean includeAttachments) throws Exception {
		// Use WS-Security
		
		EngineConfiguration config = WebServiceClientUtil.getMinimumEngineConfigurationWSSecurity();
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator(config);
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_WSS);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialWSSecurity(stub, USERNAME, PASSWORD, true);

		FolderEntry entry = stub.folder_getEntry(null, entryId, includeAttachments);
		
		System.out.println("(WSS) Entry title: " + entry.getTitle());
		
		System.out.println("(WSS) Number of attachments downloaded = " + WebServiceClientUtil.extractFiles(stub, null));
		
		return entry;
	}

	public static FolderEntry getFolderEntry(long entryId, boolean includeAttachments) throws Exception {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_BASIC);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialBasicAuth(stub, USERNAME, PASSWORD);

		FolderEntry entry = stub.folder_getEntry(null, entryId, includeAttachments);
		
		System.out.println("Entry title: " + entry.getTitle());
		System.out.println("Entry description: " + entry.getDescription().getText());
		
		System.out.println("Number of attachments downloaded = " + WebServiceClientUtil.extractFiles(stub, null));
		
		return entry;
	}

	public static void getFolderEntries(long binderId) throws Exception {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_BASIC);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialBasicAuth(stub, USERNAME, PASSWORD);

		FolderEntryCollection result = stub.folder_getEntries(null, binderId);
		FolderEntryBrief[] entries = result.getEntries();
				
		System.out.println("Number of entries = " + entries.length);
		for(int i = 0; i < entries.length; i++) {
			System.out.println("(" + (i+1) + ") id=" + entries[i].getId() + ", title=" + entries[i].getTitle()); 
		}
	}

	public static void addFolderEntryByCopying(FolderEntry entry) throws Exception {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_BASIC);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialBasicAuth(stub, USERNAME, PASSWORD);

		// We don't have to bother writing the code that copies the entry. 
		// All we have to do is to nullify the id field of the persistent entry
		// (actually we don't even have to do this), then simply re-use it
		// for add operation.
		
		entry.setTitle(entry.getTitle() + " (Copied)");
		entry.getDescription().setText(entry.getDescription().getText() + " (Copied)");
		
		File file = new File("C:/junk/junk1/debug.txt");
		WebServiceClientUtil.attachFile(stub, file);
		long entryId = stub.folder_addEntry(null, entry, "debug.txt");
		//long entryId = stub.folder_addEntry(null, entry, null);
		
		System.out.println("ID of the newly added entry: " + entryId);
	}

	public static void uploadFolderEntryFiles(long entryId) throws Exception {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_BASIC);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialBasicAuth(stub, USERNAME, PASSWORD);

		File file = new File("C:/junk/junk1/Water lilies.jpg");		
		WebServiceClientUtil.attachFile(stub, file);
		stub.folder_uploadFile(null, entryId, "myPicture", "Jong Image.jpg");
		
		file = new File("C:/junk/junk1/Book1.xls");		
		WebServiceClientUtil.attachFile(stub, file);
		stub.folder_uploadFile(null, entryId, "myFile", "Jong Book.xls");
				
		System.out.println("Files uploaded for the entry : " + entryId);
	}

	public static void modifyFolderEntry(FolderEntry entry) throws Exception {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_BASIC);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialBasicAuth(stub, USERNAME, PASSWORD);

		entry.setTitle(entry.getTitle() + " (Modified)");
		entry.getDescription().setText(entry.getDescription().getText() + " (Modified)");
		
		/*
		// Set all boolean fields to true
		for(int i=0; i<entry.getCustomBooleanFields().length; i++) {
			entry.getCustomBooleanFields()[i].setValue(Boolean.TRUE);			
		}
		// Set all date fields to current date/time
		for(int i=0; i<entry.getCustomDateFields().length; i++) {
			entry.getCustomDateFields()[i].setValue(Calendar.getInstance());			
		}
		// Append "(Modified)" to all string fields
		for(int i=0; i<entry.getCustomStringFields().length; i++) {
			entry.getCustomStringFields()[i].setValue(entry.getCustomStringFields()[i].getValue() + " (Modified)");			
		}
		// Shrink all long-array fields by one element (cut the last one).
		for(int i=0; i<entry.getCustomLongArrayFields().length; i++) {
			Long[] array = entry.getCustomLongArrayFields()[i].getValues();
			if(array.length > 0) {
				Long[] newArray = new Long[array.length-1];
				for(int j=0; j<newArray.length; j++)
					newArray[j] = array[j];
				
				entry.getCustomLongArrayFields()[i].setValues(newArray);							
			}
		}
		// Shrink all string-array fields by one element (cut the last one).
		for(int i=0; i<entry.getCustomStringArrayFields().length; i++) {
			String[] array = entry.getCustomStringArrayFields()[i].getValues();
			if(array.length > 0) {
				String[] newArray = new String[array.length-1];
				for(int j=0; j<newArray.length; j++)
					newArray[j] = array[j];
				
				entry.getCustomStringArrayFields()[i].setValues(newArray);							
			}
		}
		*/

		stub.folder_modifyEntry(null, entry);
		
		System.out.println("ID of the modified entry: " + entry.getId());
	}
	public static Binder modifyBinder(Binder binder) throws Exception {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_BASIC);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialBasicAuth(stub, USERNAME, PASSWORD);

		binder.setTitle(binder.getTitle() + " (Modified)");
		binder.getDescription().setText(binder.getDescription().getText() + " (Modified)");
		
		stub.binder_modifyBinder(null, binder);
		stub.binder_getBinder(null, binder.getId(), true);
		
		System.out.println("ID of the modified binder: " + binder.getId());
		return binder;
	}
	public static Binder uploadBinderFiles(Binder binder) throws Exception {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_BASIC);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialBasicAuth(stub, USERNAME, PASSWORD);

		File file = new File("C:/junk/junk1/Water lilies.jpg");		
		WebServiceClientUtil.attachFile(stub, file);
		stub.binder_uploadFile(null, binder.getId(), "", "Jong Image.jpg"); //will add to attachments 
		
		file = new File("C:/junk/junk1/debug.txt");		
		WebServiceClientUtil.attachFile(stub, file);
		stub.binder_uploadFile(null, binder.getId(), "", "Jong Book.xls");
		binder = stub.binder_getBinder(null, binder.getId(), true);
		System.out.println("(WSS) Number of attachments downloaded = " + WebServiceClientUtil.extractFiles(stub, null));				
		System.out.println("Files uploaded for the binder : " + binder.getId());
		return binder;
	}

	public static void deleteFolderEntry(long entryId) throws Exception {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_BASIC);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialBasicAuth(stub, USERNAME, PASSWORD);

		stub.folder_deleteEntry(null, entryId);
		
		System.out.println("ID of the deleted entry: " + entryId);
	}

	public static void getUser(long principalId) throws Exception {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_BASIC);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialBasicAuth(stub, USERNAME, PASSWORD);
		
		User user = stub.profile_getUser(null, principalId, false);
		
		System.out.println("User title: " + user.getTitle());
	}

	public static void getGroup(long principalId) throws Exception {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_BASIC);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialBasicAuth(stub, USERNAME, PASSWORD);
		
		Group group = stub.profile_getGroup(null, principalId, false);
		
		System.out.println("Group title: " + group.getTitle());
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
	public static void checkUsers() throws Exception {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_BASIC);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialBasicAuth(stub, USERNAME, PASSWORD);

		User testUser = new User();
		testUser.setName("Jodi");
		testUser.setFirstName("Jodi");
		testUser.setMiddleName("Anne");
		testUser.setLastName("Tester");
		testUser.setEmailAddress("boulder@foo.bar");
		long testUserId = stub.profile_addUser(null, testUser);
		Long wsId = stub.profile_addUserWorkspace(null, testUserId);
		testUser = stub.profile_getUser(null, testUserId, false);
		if (!"Jodi Anne Tester".equals(testUser.getTitle())) System.out.println("Title not set");
		if (!wsId.equals(testUser.getWorkspaceId())) System.out.println("Workspace Id not set correctly");
		testUser.setZonName("jodi");
		stub.profile_modifyUser(null, testUser);
		testUser = stub.profile_getUser(null, testUserId, false);
		if (!"jodi".equals(testUser.getZonName())) System.out.println("Modify of zonName failed");
		try {
			testUser = uploadUserFile(testUser);
			Attachment[] atts = testUser.getAttachmentsField().getAttachments();
			stub.profile_removeFile(null, testUserId, atts[0].getFileName());
			testUser = stub.profile_getUserByName(null, "jodi", false);
			if (testUser.getAttachmentsField().getAttachments().length != atts.length-1) 
				System.out.println("removeFile failed");
		} finally {
			stub.profile_deletePrincipal(null, testUser.getId(), true);
		}
	}
	public static User uploadUserFile(User user) throws Exception {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_BASIC);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialBasicAuth(stub, USERNAME, PASSWORD);

		File file = new File("C:/junk/junk1/Water lilies.jpg");		
		WebServiceClientUtil.attachFile(stub, file);
		stub.profile_uploadFile(null, user.getId(), "picture", "Jong Image.jpg"); //will add to attachments 
		System.out.println("Picture uploaded for the user : " + user.getId());
		
		user = stub.profile_getUser(null, user.getId(), true);
		System.out.println("(WSS) Number of attachments downloaded = " + WebServiceClientUtil.extractFiles(stub, null));				
		Attachment[] atts = user.getAttachmentsField().getAttachments();
		for (int i=0; i<atts.length; ++i) {
			System.out.println("Attachment: " + atts[i].getFileName() + " " + atts[i].getHref());
		}
		return user;
	}

	public static FileVersions getEntryFileVersions(long entryId, String fileName) throws Exception {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_BASIC);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialBasicAuth(stub, USERNAME, PASSWORD);
		
		FileVersions fileVersions = stub.folder_getFileVersions(null, entryId, fileName);
		
		if(fileVersions != null) {
			System.out.println("File name: " + fileVersions.getFileName());
			FileVersion[] versions = fileVersions.getVersions();
			for(int i = 0; i < versions.length; i++)
				System.out.println("Version " + versions[i].getVersionNumber() + ": " + versions[i].getId());
		}
		else {
			System.out.println("No such file");
		}
		
		return fileVersions;
	}
	
	private static Long getGlobalWorkspace(TeamingServiceSoapBindingStub stub) throws Exception {
		
		String xml = stub.search_getWorkspaceTreeAsXML(null, -1, 2, "");
		try {
			Document document = DocumentHelper.parseText(xml);
			Element ws = (Element)document.getRootElement().selectSingleNode("./child[@title='Global Workspaces']");
			return Long.valueOf(ws.attributeValue("id"));
		} catch (DocumentException e) {
			System.out.println(e);
		}
		return null;
	}
	private static TemplateBrief getTemplateByName(TeamingServiceSoapBindingStub stub, String name) throws Exception {
		TemplateCollection templates = stub.template_getTemplates(null);
		TemplateBrief[] array = templates.getTemplates();
		for (int i=0; i<array.length; ++i) {
			TemplateBrief template = array[i];
			if (name.equals(template.getName())) return template;
		}
		return null;
	}
	private static Binder getTestFolder(String title) throws Exception {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_BASIC);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialBasicAuth(stub, USERNAME, PASSWORD);
		long gblId = getGlobalWorkspace(stub);
		try {
			return stub.binder_getBinderByPathName(null, "/Workspaces/Global Workspaces/" + title, false);
		} catch (Exception ex) {
			TemplateBrief tb = getTemplateByName(stub, "_folder");
			return stub.binder_getBinder(null, stub.template_addBinder(null, gblId, tb.getId(), title), false);

		}
	}
	private static void checkGroups() throws Exception {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_BASIC);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialBasicAuth(stub, USERNAME, PASSWORD);
		Group group = new Group();
		group.setName("testgroup");
		stub.profile_addGroup(null, group);
		group = stub.profile_getGroupByName(null, "testgroup", false);
		User testUser = getTestUser();
		stub.profile_addGroupMember(null, "testgroup", testUser.getName());
		PrincipalCollection members = stub.profile_getGroupMembers(null, "testgroup");
		PrincipalBrief[] entries = members.getEntries();
		
		System.out.println("First = " + members.getFirst());
		System.out.println("Count = " + entries.length);
		System.out.println("Total = " + members.getTotal());
		for(int i = 0; i < entries.length; i++) {
			System.out.println("(" + i + ") id=" + entries[i].getId() + ", name=" + entries[i].getName() + ", type=" + entries[i].getType() + ", title=" + entries[i].getTitle() + ", email=" + entries[i].getEmailAddress()); 
		}
		stub.profile_removeGroupMember(null, "testgroup", testUser.getName());
		members = stub.profile_getGroupMembers(null, "testgroup");
		entries = members.getEntries();
		System.out.println("First = " + members.getFirst());
		System.out.println("Count = " + entries.length);
		System.out.println("Total = " + members.getTotal());
		for(int i = 0; i < entries.length; i++) {
			System.out.println("(" + i + ") id=" + entries[i].getId() + ", name=" + entries[i].getName() + ", type=" + entries[i].getType() + ", title=" + entries[i].getTitle() + ", email=" + entries[i].getEmailAddress()); 
		}
		stub.profile_deletePrincipal(null, testUser.getId(), true);
		stub.profile_deletePrincipal(null, group.getId(), true);
	}
	private static User getTestUser() throws Exception {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS_BASIC);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		WebServiceClientUtil.setUserCredentialBasicAuth(stub, USERNAME, PASSWORD);
		User testUser =null;
		try {
			testUser = stub.profile_getUserByName(null, "Jodi", false);
		} catch (Exception ex) {
			testUser = new User();
			testUser.setName("Jodi");
			testUser.setFirstName("Jodi");
			testUser.setMiddleName("Anne");
			testUser.setLastName("Tester");
			testUser.setEmailAddress("boulder@foo.bar");
			long testUserId = stub.profile_addUser(null, testUser);
			testUser = stub.profile_getUser(null, testUserId, false);
		}
		return testUser;
	}
}

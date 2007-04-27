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
package com.sitescape.team.portlet.forum;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.TimeZone;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.module.shared.FolderUtils;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.portletadapter.MultipartFileSupport;
import com.sitescape.team.repository.RepositoryUtil;
import com.sitescape.team.util.AllBusinessServicesInjected;
import com.sitescape.team.util.FileUploadItem;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.util.Validator;
import com.sitescape.util.cal.Duration;

/**
 * @author Peter Hurley
 *
 */
public class AddEntryController extends SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
	throws Exception {
		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		String blogReply = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BLOG_REPLY, "");
		String addEntryFromIFrame = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ADD_DEFAULT_ENTRY_FROM_INFRAME, "");
		String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, "");
		//See if the add entry form was submitted
		Long entryId=null;
		if (formData.containsKey("okBtn")) {
			//The form was submitted. Go process it
			String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
			Map fileMap=null;
			if (request instanceof MultipartFileSupport) {
				fileMap = ((MultipartFileSupport) request).getFileMap();
			} else {
				fileMap = new HashMap();
			}
			MapInputData inputData = new MapInputData(formData);
			if (action.equals(WebKeys.ACTION_ADD_FOLDER_ENTRY)) {
				entryId= getFolderModule().addEntry(folderId, entryType, inputData, fileMap);
				setupReloadOpener(response, folderId, entryId);
				if (!addEntryFromIFrame.equals("")) {
					response.setRenderParameter(WebKeys.NAMESPACE, namespace);
					response.setRenderParameter(WebKeys.IN_IFRAME_ADD_ENTRY, "1");
					response.setRenderParameter(WebKeys.ENTRY_ID, entryId.toString());
				}
			} else if (action.equals(WebKeys.ACTION_ADD_FOLDER_REPLY)) {
				Long id = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
				entryId = getFolderModule().addReply(folderId, id, entryType, inputData, fileMap);
				//Show the parent entry when this operation finishes
				setupReloadOpener(response, folderId, id);
				if (!blogReply.equals("")) {
			    	FolderEntry entry = getFolderModule().getEntry(folderId, entryId);
					response.setRenderParameter(WebKeys.BLOG_REPLY, "1");
					response.setRenderParameter(WebKeys.NAMESPACE, namespace);
					response.setRenderParameter(WebKeys.ENTRY_ID, entry.getParentEntry().getId().toString());
					response.setRenderParameter(WebKeys.BLOG_REPLY_COUNT, String.valueOf(entry.getParentEntry().getTotalReplyCount()));
				}
			}
			//flag reload of folder listing
			//response.setRenderParameter(WebKeys.RELOAD_URL_FORCED, "");
		} else if (formData.containsKey("cancelBtn")) {
			if (!blogReply.equals("")) {
				setupReloadOpener(response, folderId, entryId);
		    	FolderEntry entry = getFolderModule().getEntry(folderId, entryId);
				response.setRenderParameter(WebKeys.BLOG_REPLY, "1");
				response.setRenderParameter(WebKeys.NAMESPACE, namespace);
				response.setRenderParameter(WebKeys.ENTRY_ID, entryId.toString());
				response.setRenderParameter(WebKeys.BLOG_REPLY_COUNT, String.valueOf(entry.getTotalReplyCount()));
			} else if (!addEntryFromIFrame.equals("")) {
				setupReloadOpener(response, folderId, Long.MIN_VALUE);
				//clear entryId out
				response.setRenderParameter(WebKeys.URL_ENTRY_ID, "");
				response.setRenderParameter(WebKeys.IN_IFRAME_ADD_ENTRY, "1");				
			} else {
				setupCloseWindow(response);
			}
		} else if (action.equals(WebKeys.ACTION_ADD_FOLDER_ATTACHMENT)) {
			String isLibraryBinder = PortletRequestUtils.getStringParameter(request, WebKeys.URL_IS_LIBRARY_BINDER, "");
			
			Map fileMap = null;
			if (request instanceof MultipartFileSupport) {
				fileMap = ((MultipartFileSupport) request).getFileMap();
			} else {
				fileMap = new HashMap();
			}
			
			//If the folder is a Library Folder 
			if (isLibraryBinder.equalsIgnoreCase("true")) {
				//We need to get the name of the files and the path information
				//Based on the path information, we need to see if the folder is already present or not 
				//If the folder is already present, we will use it, if not we will create the folder
				//Then we will check if the file we are trying to place in the folder is already present
				//If they are already present, then we will create a new version of the file
				//If they are not already present, we will have to create a new entry and associate the entry with 
				//the new attachment.
				String nameValue = ObjectKeys.FILES_FROM_APPLET_FOR_BINDER;
				String folderNameValue = ObjectKeys.FILES_FROM_APPLET_FOLDER_INFO_FOR_BINDER;
	        	boolean blnCheckForAppletFile = true;
	        	int intFileCount = 1;

	        	//Looping through each of the uploaded file
	        	while (blnCheckForAppletFile) {
	        		
	        		//Get File Name Identifier
	        		String fileEleName = nameValue + Integer.toString(intFileCount);
	        		//Get Folder Name Identifier
	        		String fileFolderName = folderNameValue + Integer.toString(intFileCount);
	        		
	        		//Checking if the file with name is present 
	        		if (fileMap.containsKey(fileEleName)) {
	        	    	//Setting the parent folder to be used first
	        	    	Long lngFolderIdToUse = folderId;
	        			
	        			//Getting the file information
	        			MultipartFile myFile = (MultipartFile)fileMap.get(fileEleName);
	        	    	String orginalFileName = myFile.getOriginalFilename();
	        	    	
	        	    	//Getting the file folder information
	        	    	String fileFolderNameVal = PortletRequestUtils.getStringParameter(request, fileFolderName, "");
	        	    	//Getting the list of folders as a arraylist
	        	    	ArrayList folderArrayList = getFolderList(fileFolderNameVal);
	        	    	
	        	    	//Looping through the folder name to identify under which folder we need to add the attachment
	        	    	for (int i = 0; i < folderArrayList.size(); i++) {
	        	    		String strFolderName = (String) folderArrayList.get(i);
	        	    		if (!strFolderName.equals("")) {
	        	    			Folder folderObj = getFolderModule().getFolder(lngFolderIdToUse);
	        	    			
	        	    			//Getting the sub-folders that have been created under the current folder
			        	    	java.util.List arrSubFolders = (java.util.List) folderObj.getFolders();
			        	    	boolean doesFolderExist = false;
			        	    	
			        	    	//Looping through the sub-folders that have already been created
			        	    	for (int j = 0; j < arrSubFolders.size(); j++) {
			        	    		Binder subFolder = (Binder) arrSubFolders.get(j);
			        	    		String strSubFolderName = subFolder.getTitle();
			        	    		//Checking to see if the sub-folder already created matches with the folder that has been dragged & dropped
			        	    		if (strSubFolderName.equals(strFolderName)) {
			        	    			doesFolderExist = true;
			        	    			//Since the folder exists, we will use this folder
			        	    			lngFolderIdToUse = subFolder.getId();
			        	    			break;
			        	    		}
			        	    	}
			        	    	
			        	    	//Create a sub folder, if it does not exist
			        	    	if (!doesFolderExist) {
			        	    		lngFolderIdToUse = FolderUtils.createLibraryFolder(folderObj, strFolderName);
			        	    	}
	        	    		}
	        	    	}
	        	    	
	        	    	//Using the Folder object that was already present or that was recently created 
	        	    	Folder entryCreationFolder = getFolderModule().getFolder(lngFolderIdToUse);
	        	    	//Checking to see if the folder already contains the file that we are trying to create
	        	    	FolderEntry preExistingEntry = getFolderModule().getLibraryFolderEntryByFileName(entryCreationFolder, orginalFileName);
	        	    	
	        	    	//If there is not pre-existing entry - we create a new entry
	        	    	//If there is a pre-existing entry - we modify the entry
	        	    	if (preExistingEntry == null) {
	        	    		FolderUtils.createFolderEntry(entryCreationFolder, orginalFileName, myFile.getInputStream(), null);
	        	    	} else {
	        	    		FolderUtils.modifyFolderEntry(preExistingEntry, orginalFileName, myFile.getInputStream(), null);
	        	    	}
	        	    	intFileCount++;
	        		} else {
	        			//We will set this boolean value to false to exit from the while loop
	        			blnCheckForAppletFile = false;
	        		}
	        	}
				
			} else if (isLibraryBinder.equalsIgnoreCase("false")) {
				//If the folder is a non-library folder, we need to take one attached file at a time and 
				//for each attached file we will create a entry and add the attached file to the entry. 
				String nameValue = ObjectKeys.FILES_FROM_APPLET_FOR_BINDER;
	        	boolean blnCheckForAppletFile = true;
	        	int intFileCount = 1;

	        	while (blnCheckForAppletFile) {
	        		Map oneFileMap = new HashMap();
	        		Map entryNameOnly = new HashMap();
	        		
	        		String fileEleName = nameValue + Integer.toString(intFileCount);
	        		if (fileMap.containsKey(fileEleName)) {
	        	    	MultipartFile myFile = (MultipartFile)fileMap.get(fileEleName);
	        	    	String fileName = myFile.getOriginalFilename();
	        	    	intFileCount++;
	        	    	
	        	    	oneFileMap.put(nameValue+"1", myFile);
	        	    	entryNameOnly.put(ObjectKeys.FIELD_ENTITY_TITLE, fileName);
	        	    	
	        	    	MapInputData inputData = new MapInputData(entryNameOnly);
	        	    	entryId= getFolderModule().addEntry(folderId, null, inputData, oneFileMap, new Boolean(true));
	        		} else {
	        			blnCheckForAppletFile = false;
	        		}
	        	}
			} else {
				System.out.println("Not able to identify if the Folder is Library or Not");
			}
		} else {
			response.setRenderParameters(formData);
		}
	}
	
	private ArrayList getFolderList(String strFolderAndFileName) {
		ArrayList arrFolders = new ArrayList();
		String [] strSplitValue = strFolderAndFileName.split("/");
		for (int i = 0; i < strSplitValue.length; i++) {
			arrFolders.add(strSplitValue[i]);
		}
		return arrFolders;
	}
	
	private void setupReloadOpener(ActionResponse response, Long folderId, Long entryId) {
		//return to view entry
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_RELOAD_OPENER);
		response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());
		response.setRenderParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
	}
	private void setupCloseWindow(ActionResponse response) {
		//return to view entry
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_CLOSE_WINDOW);
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		
		Map model = new HashMap();
			
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		String title = PortletRequestUtils.getStringParameter(request, "title", "");
		model.put(WebKeys.ENTRY_TITLE, title);
		model.put(WebKeys.OPERATION, action);
		String path = WebKeys.VIEW_ADD_ENTRY;
		
		if (action.equals(WebKeys.ACTION_ADD_FOLDER_ATTACHMENT)) {
			path="definition_elements/applet_dummy_response";
		} else {
			Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));		
			
			//See if this is an "add entry" or an "add reply" request
			if (action.equals(WebKeys.ACTION_ADD_FOLDER_ENTRY)) {
				Folder folder = getFolderModule().getFolder(folderId);
				//Adding an entry; get the specific definition
				Map folderEntryDefs = DefinitionHelper.getEntryDefsAsMap(folder);
				String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
				model.put(WebKeys.FOLDER, folder);
				model.put(WebKeys.ENTRY_DEFINTION_MAP, folderEntryDefs);
				model.put(WebKeys.CONFIG_JSP_STYLE, "form");
				//Make sure the requested definition is legal
				if (folderEntryDefs.containsKey(entryType)) {
					DefinitionHelper.getDefinition((Definition)folderEntryDefs.get(entryType), model, "//item[@type='form']");
				} else {
					DefinitionHelper.getDefinition(null, model, "//item[@name='entryForm']");
				}
				parseInitialCalendarEventData(model, request);
			} else {
		    	Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));
		    	request.setAttribute(WebKeys.URL_ENTRY_ID,entryId.toString());
		    	FolderEntry entry = getFolderModule().getEntry(folderId, entryId);
		    	Folder folder = entry.getParentFolder();
		    	model.put(WebKeys.FOLDER, folder); 
					
		    	//Get the legal reply types from the parent entry definition
				Document entryView = null;
				Definition entryDefinition = entry.getEntryDef();
				if (entryDefinition != null) {
					entryView = entryDefinition.getDefinition();
				}
				Iterator replyStyles = null;
				if (entryView != null) {
					//See if there is a reply style for this entry definition
					replyStyles = entryView.getRootElement().selectNodes("properties/property[@name='replyStyle']").iterator();
				}
		   	
		    	//Adding an entry; get the specific definition
				Map folderEntryDefs = DefinitionHelper.getEntryDefsAsMap(folder);
		    	String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
		    	model.put(WebKeys.ENTRY_DEFINTION_MAP, folderEntryDefs);
		    	model.put(WebKeys.CONFIG_JSP_STYLE, "form");
		    	
		        //Make sure the requested reply definition is legal
		    	boolean replyStyleIsGood = false;
		    	while (replyStyles.hasNext()) {
		    		if (((String)((Element)replyStyles.next()).attributeValue("value", "")).equals(entryType)) {
		    			replyStyleIsGood = true;
		    			break;
		    		}
		    	}
			    	
				if (replyStyleIsGood) {
					DefinitionHelper.getDefinition(getDefinitionModule().getDefinition(entryType), model, "//item[@type='form']");
				} else {
					DefinitionHelper.getDefinition(null, model, "//item[@name='entryForm']");
				}
			}
		}
		return new ModelAndView(path, model);
	}

	private void parseInitialCalendarEventData(Map model, RenderRequest request) {
		int year = PortletRequestUtils.getIntParameter(request, WebKeys.URL_DATE_YEAR, -1);
		int month = PortletRequestUtils.getIntParameter(request, WebKeys.URL_DATE_MONTH, -1);
		int dayOfMonth = PortletRequestUtils.getIntParameter(request, WebKeys.URL_DATE_DAY_OF_MONTH, -1);

		String time = PortletRequestUtils.getStringParameter(request, WebKeys.URL_DATE_TIME, null);
		int duration = PortletRequestUtils.getIntParameter(request, WebKeys.URL_DATE_TIME_DURATION, -1);
		
		if (year != -1 && month != -1 && dayOfMonth != -1) {
			TimeZone timeZone = RequestContextHolder.getRequestContext().getUser().getTimeZone();
						
			Calendar startDate = new GregorianCalendar(timeZone);
			startDate.set(Calendar.YEAR, year);
			startDate.set(Calendar.MONTH, month);
			startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			
			if (time != null) {
				String[] timeS = time.split(":");
				if (timeS != null) {
					try {
						if (timeS.length > 0) {
							int hour = Integer.parseInt(timeS[0]);
							if (hour != -1) {
								startDate.set(Calendar.HOUR_OF_DAY, hour);
								startDate.set(Calendar.MINUTE, 0);
							}
						}
						if (timeS.length > 1) {
							int minute = Integer.parseInt(timeS[1]);
							startDate.set(Calendar.MINUTE, minute);
						}
					} catch (NumberFormatException e) {
						// do nothing, no hour, no minute
					}
				}
			}
		
			Event event = new Event(startDate, new Duration(0, 0, duration, 0), 0);
			
			model.put(WebKeys.CALENDAR_INITIAL_EVENT, event);
		}
		
	}

}

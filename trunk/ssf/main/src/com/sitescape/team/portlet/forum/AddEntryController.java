package com.sitescape.team.portlet.forum;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.portletadapter.MultipartFileSupport;
import com.sitescape.team.repository.RepositoryUtil;
import com.sitescape.team.util.FileUploadItem;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.util.Validator;

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
			//response.setRenderParameter("ssReloadUrl", "");
		} else if (formData.containsKey("cancelBtn")) {
			if (!blogReply.equals("")) {
				setupReloadOpener(response, folderId, entryId);
		    	FolderEntry entry = getFolderModule().getEntry(folderId, entryId);
				response.setRenderParameter(WebKeys.BLOG_REPLY, "1");
				response.setRenderParameter(WebKeys.NAMESPACE, namespace);
				response.setRenderParameter(WebKeys.ENTRY_ID, entryId.toString());
				response.setRenderParameter(WebKeys.BLOG_REPLY_COUNT, String.valueOf(entry.getTotalReplyCount()));
			} else if (!addEntryFromIFrame.equals("")) {
				response.setRenderParameter(WebKeys.IN_IFRAME_ADD_ENTRY, "1");				
			} else {
				setupCloseWindow(response);
			}
		} else if (action.equals(WebKeys.ACTION_ADD_FOLDER_ATTACHMENT)) {
			String binderId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID, "");
			String isLibraryBinder = PortletRequestUtils.getStringParameter(request, WebKeys.URL_IS_LIBRARY_BINDER, "");
			
			Map fileMap = null;
			if (request instanceof MultipartFileSupport) {
				fileMap = ((MultipartFileSupport) request).getFileMap();
			} else {
				fileMap = new HashMap();
			}
			
			if (isLibraryBinder.equalsIgnoreCase("true")) {
				//Library Binder/Folder - Need to use WebDav Code that Jong will be providing
				System.out.println("Hemanth : This is a Library Folder, need to use the same functionality as WebDAV\n\n");
				
				//We need to get the name of the files and the path information
				//Based on the path information, we need to see if the folder is already present or now 
				//If the folder is already present, we will use it, if not we will create the folder
				//Then we will check if the file we are trying to place in the folder is already present
				//If they are already present, then we will create a new version of the file
				//If they are not already present, we will have to create a new entry and associate the entry with 
				//the new attachment.
				
				String nameValue = ObjectKeys.FILES_FROM_APPLET_FOR_BINDER;
				String folderNameValue = ObjectKeys.FILES_FROM_APPLET_FOLDER_INFO_FOR_BINDER;
	        	boolean blnCheckForAppletFile = true;
	        	int intFileCount = 1;

	        	while (blnCheckForAppletFile) {
	        		Map oneFileMap = new HashMap();
	        		Map entryNameOnly = new HashMap();
	        		
	        		String fileEleName = nameValue + Integer.toString(intFileCount);
	        		String fileFolderName = folderNameValue + Integer.toString(intFileCount);
	        		
	        		if (fileMap.containsKey(fileEleName)) {
	        	    	MultipartFile myFile = (MultipartFile)fileMap.get(fileEleName);
	        	    	String orginalFileName = myFile.getOriginalFilename();
	        	    	String fileName = myFile.getName();
	        	    	String fileFolderNameVal = PortletRequestUtils.getStringParameter(request, fileFolderName, "");
	        	    	
	        	    	Long lngFolderIdToUse = folderId;
	        	    	ArrayList folderArrayList = getFolderList(fileFolderNameVal);
	        	    	for (int i = 0; i < folderArrayList.size(); i++) {
	        	    		String strFolderName = (String) folderArrayList.get(i);
	        	    		
	        	    		System.out.println("Hemanth: Check if Folder Exists: "+strFolderName+"\n");
	        	    		
	        	    		//Make Call to check if folder exists, if not create the folder and then, get the id for the folder
	        	    		//Long newFolderId = JongsWebDAVMethod.checkAndCreateFolder(currentFolderId, strFolderName);
	        	    		
	        	    		//After getting the new folder id, we need to set that to the value lngFolderIdToUse
	        	    		//lngFolderIdToUse = newFolderId;
	        	    	}
	        	    	
	        	    	System.out.println("Hemanth: AddEntryController: fileName: "+fileName + ", orginalFileName: "+orginalFileName + ", fileFolderNameVal:" +fileFolderNameVal);
	        	    	intFileCount++;
	        	    	
	        	    	//Once the new folderId is obtained we need to pass that folderId along with the below call, new file and filename
	        	    	//if the filename we have specified already exists in the repository for the folder, it will create a new version of the file
	        	    	//if the filename we have specified does not exist, then we will create a new entry with the filename as the name of the entry,
	        	    	//and the adding the file as an attachment to the entry. For this we will be using Jong's code.
	        	    	
	        	    	//oneFileMap.put(nameValue+"1", myFile);
	        	    	//entryNameOnly.put(ObjectKeys.FIELD_ENTITY_TITLE, fileName);
	        	    	//MapInputData inputData = new MapInputData(entryNameOnly);
	        	    	//entryId= getFolderModule().addEntry(folderId, null, inputData, oneFileMap, new Boolean(true));
	        	    	//System.out.println("Hemanth: entryId: "+entryId);
	        	    	
	        		} else {
	        			blnCheckForAppletFile = false;
	        		}
	        	}
				
			} else if (isLibraryBinder.equalsIgnoreCase("false")) {
				//Non-Library Binder/Folder
				//We need to take one attached file at a time and try to save that, 
				//instead of sending the whole list of files.
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
	        	    	System.out.println("Hemanth: AddEntryController: fileName: "+fileName);
	        	    	intFileCount++;
	        	    	
	        	    	oneFileMap.put(nameValue+"1", myFile);
	        	    	entryNameOnly.put(ObjectKeys.FIELD_ENTITY_TITLE, fileName);
	        	    	
	        	    	MapInputData inputData = new MapInputData(entryNameOnly);
	        	    	entryId= getFolderModule().addEntry(folderId, null, inputData, oneFileMap, new Boolean(true));
	        	    	
	        	    	System.out.println("Hemanth: entryId: "+entryId);
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
					DefinitionHelper.getDefinition(getDefinitionModule().getDefinition(entryType), model, "//item[@type='form']");
				} else {
					DefinitionHelper.getDefinition(null, model, "//item[@name='entryForm']");
				}
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
}



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
package com.sitescape.team.portlet.profile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.portlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.PasswordMismatchException;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.ProfileBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.profile.ProfileModule.ProfileOperation;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.portletadapter.MultipartFileSupport;
import com.sitescape.team.util.EncryptUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.util.GetterUtil;

/**
 * @author Peter Hurley
 *
 */
public class ModifyEntryController extends SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
	throws Exception {

		Map formData = request.getParameterMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		if (formData.containsKey("okBtn") && op.equals(WebKeys.OPERATION_DELETE)) {
			String deleteWs = PortletRequestUtils.getStringParameter(request, "deleteWs", null);
			Map options = new HashMap();
			options.put(ObjectKeys.INPUT_OPTION_DELETE_USER_WORKSPACE, GetterUtil.getBoolean(deleteWs, false));
			getProfileModule().deleteEntry(entryId, options);			
			response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());		
			response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
			response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_RELOAD_LISTING);
			response.setRenderParameter(WebKeys.RELOAD_URL_FORCED, "");
		} else if (formData.containsKey("okBtn") && op.equals("")) {
			//The modify form was submitted. Go process it
			Map fileMap=null;
			if (request instanceof MultipartFileSupport) {
				fileMap = ((MultipartFileSupport) request).getFileMap();
			} else {
				fileMap = new HashMap();
			}
			Set deleteAtts = new HashSet();
			for (Iterator iter=formData.entrySet().iterator(); iter.hasNext();) {
				Map.Entry e = (Map.Entry)iter.next();
				String key = (String)e.getKey();
				if (key.startsWith("_delete_")) {
					deleteAtts.add(key.substring(8));
				}
				
			}
			MapInputData inputData = new MapInputData(formData);
        	String password = inputData.getSingleValue(WebKeys.USER_PROFILE_PASSWORD);
        	String password2 = inputData.getSingleValue(WebKeys.USER_PROFILE_PASSWORD2);
        	if (password == null || !password.equals(password2)) {
        		throw new PasswordMismatchException("errorcode.password.mismatch");
        	}
            ProfileBinder binder = getProfileModule().getProfileBinder();
            if (!getProfileModule().testAccess(binder, ProfileOperation.manageEntries)) {
            	String passwordOriginal = inputData.getSingleValue(WebKeys.USER_PROFILE_PASSWORD_ORIGINAL);
            	//Check that the user knows the current password
            	Principal p = getProfileModule().getEntry(entryId);
            	if (p instanceof User) {
            		if (!EncryptUtil.encryptPassword(passwordOriginal).equals(((User)p).getPassword())) {
                    	throw new PasswordMismatchException("errorcode.password.invalid");            			
            		}
            	}
            }
            if (inputData.exists(WebKeys.USER_PROFILE_PASSWORD) && 
            		inputData.getSingleValue(WebKeys.USER_PROFILE_PASSWORD).equals("")) {
            	//Don't allow blank password (either on purpose or by accident)
            	Map writeableFormData = new HashMap(formData);
            	writeableFormData.remove(WebKeys.USER_PROFILE_PASSWORD);
            	inputData = new MapInputData(writeableFormData);
            }
			getProfileModule().modifyEntry(entryId, inputData, fileMap, deleteAtts, null, null);

			//See if there was a request to reorder the graphic files
			String graphicFileIds = PortletRequestUtils.getStringParameter(request, "_graphic_id_order", "");
			
			setupReloadOpener(response, binderId, entryId);
			//flag reload of folder listing
			//response.setRenderParameter(WebKeys.RELOAD_URL_FORCED, "");
		} else if (formData.containsKey("cancelBtn")) {
			//The user clicked the cancel button
			setupCloseWindow(response);
		} else {
			response.setRenderParameters(formData);
		}
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
	private void setupViewEntry(ActionResponse response, Long folderId, Long entryId) {
		response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());		
		response.setRenderParameter(WebKeys.URL_ENTRY_ID, entryId.toString());		
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_ENTRY);
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
		RenderResponse response) throws Exception {

		Map model = new HashMap();	
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		Principal entry  = getProfileModule().getEntry(entryId);
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		if (op.equals(WebKeys.OPERATION_DELETE)) {
			model.put(WebKeys.ENTRY, entry);
			model.put(WebKeys.BINDER, entry.getParentBinder());
			return new ModelAndView(WebKeys.VIEW_CONFIRM_DELETE_USER_WORKSPACE, model);
		} else {
			model.put(WebKeys.ENTRY, entry);
			model.put(WebKeys.FOLDER, entry.getParentBinder());
			model.put(WebKeys.BINDER, entry.getParentBinder());
			model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_FORM);
			ProfileBinder binder = getProfileModule().getProfileBinder();
			if (getProfileModule().testAccess(binder, ProfileOperation.manageEntries)) {
				model.put(WebKeys.IS_BINDER_ADMIN, true);
			}
			Definition entryDef = entry.getEntryDef();
			if (entryDef == null) {
				DefinitionHelper.getDefaultEntryView(entry, model, "//item[@name='entryForm' or @name='profileEntryForm']");
			} else {
				DefinitionHelper.getDefinition(entryDef, model, "//item[@type='form']");
			}
		
			return new ModelAndView(WebKeys.VIEW_MODIFY_ENTRY, model);
		}
	}
}


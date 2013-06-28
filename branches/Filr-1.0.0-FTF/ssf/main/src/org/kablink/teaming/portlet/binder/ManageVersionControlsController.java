/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.portlet.binder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.file.FilesErrors;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.springframework.web.portlet.ModelAndView;


/**
 * @author Peter Hurley
 * 
 */
@SuppressWarnings({"unchecked", "unused"})
public class ManageVersionControlsController extends AbstractBinderController {

	public void handleActionRequestAfterValidation(ActionRequest request,
			ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Binder binder = getBinderModule().getBinder(binderId);
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		Map formData = request.getParameterMap();

		if (formData.containsKey("inheritBtn") && WebHelper.isMethodPost(request)) {
			if (getBinderModule().testAccess(binder, BinderOperation.manageConfiguration) ||
					getAdminModule().testAccess(AdminOperation.manageFunction)) {
				getBinderModule().setBinderVersionsInherited(binderId, Boolean.TRUE);
			}
		} else if (formData.containsKey("encryptAllFiles") && WebHelper.isMethodPost(request)) {
			if (getBinderModule().testAccess(binder, BinderOperation.manageConfiguration) ||
					getAdminModule().testAccess(AdminOperation.manageFunction)) {
				FilesErrors errors = new FilesErrors();
				getBinderModule().setBinderFileEncryptionEnabled(binderId, true, errors);
	         	if(errors.getProblems().size() > 0) {
	        		// At least one error occured during the operation. 
		    		response.setRenderParameter(WebKeys.FILE_PROCESSING_ERRORS, errors.toString());
		    		response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		    		return;
	        	}
			}
		} else if (formData.containsKey("stopInheritBtn") && WebHelper.isMethodPost(request)) {
			if (getBinderModule().testAccess(binder, BinderOperation.manageConfiguration) ||
					getAdminModule().testAccess(AdminOperation.manageFunction)) {
				getBinderModule().setBinderVersionsEnabled(binderId, Boolean.TRUE);
			}
		} else if (formData.containsKey("inheritEncryptionBtn") && WebHelper.isMethodPost(request)) {
			if (getBinderModule().testAccess(binder, BinderOperation.manageConfiguration) ||
					getAdminModule().testAccess(AdminOperation.manageFunction)) {
				getBinderModule().setBinderFileEncryptionInherited(binderId, Boolean.TRUE);
			}
		} else if (formData.containsKey("stopInheritEncryptionBtn") && WebHelper.isMethodPost(request)) {
			if (getBinderModule().testAccess(binder, BinderOperation.manageConfiguration) ||
					getAdminModule().testAccess(AdminOperation.manageFunction)) {
				FilesErrors errors = new FilesErrors();
				getBinderModule().setBinderFileEncryptionEnabled(binderId, Boolean.TRUE, errors);
	         	if(errors.getProblems().size() > 0) {
	        		// At least one error occured during the operation. 
		    		response.setRenderParameter(WebKeys.FILE_PROCESSING_ERRORS, errors.toString());
		    		response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		    		return;
	        	}
			}

		} else if ((formData.containsKey("okBtn") || formData.containsKey("applyBtn")) && 
				WebHelper.isMethodPost(request)) {
			if (getBinderModule().testAccess(binder, BinderOperation.manageConfiguration) ||
					getAdminModule().testAccess(AdminOperation.manageFunction)) {
				//See if inheriting
				if (binder.getVersionsEnabled() != null) {
					//Save the settings
					//Are versions enabled
					Boolean enableBinderVersions = PortletRequestUtils.getBooleanParameter(request, "enableBinderVersions", Boolean.FALSE);
					getBinderModule().setBinderVersionsEnabled(binderId, enableBinderVersions);
					
					// Get the maximum number of versions.
					String s_versionsToKeep;
					Long versionsToKeep = null;
					try {
						s_versionsToKeep = PortletRequestUtils.getStringParameter(request, "versionsToKeep", "").trim();
						if (!enableBinderVersions) {
							//If versions are disabled, then set the versions to keep to 0
							s_versionsToKeep = "0";
						}
						if (!s_versionsToKeep.equals("")) {
							versionsToKeep = Long.valueOf(s_versionsToKeep);
						}
						getBinderModule().setBinderVersionsToKeep(binderId, versionsToKeep);
					} catch (Exception ex) {
						// The value entered by the user must not be valid, don't set it.
					}
				}
				
				//Save the settings
				//Is version aging enabled
				Boolean originalAgingEnabled = getBinderModule().getBinderVersionAgingEnabled(binder);
				Boolean enableBinderVersionAging = PortletRequestUtils.getBooleanParameter(request, "enableBinderVersionAging", Boolean.FALSE);
				getBinderModule().setBinderVersionAgingEnabled(binderId, enableBinderVersionAging);
				// Get the aging days.
				String s_agingDays;
				Long agingDays = null;
				try {
					s_agingDays = PortletRequestUtils.getStringParameter(request, "versionAgingDays", "").trim();
					if (!s_agingDays.equals("")) {
						agingDays = Long.valueOf(s_agingDays);
					}
				} catch (Exception ex) {
					// The value entered by the user must not be valid, don't set it.
					agingDays = getBinderModule().getBinderVersionAgingDays(binder);
				}
				Long originalAgingDays = getBinderModule().getBinderVersionAgingDays(binder);
				if (originalAgingDays != agingDays || !originalAgingEnabled.equals(enableBinderVersionAging) ) {
					//Store the new value, then fix up the entries in this binder to have the correct agingDate
					getBinderModule().setBinderVersionAgingDays(binderId, agingDays);
					getBinderModule().setBinderFileAgingDates(binder);
				}
				
				// Get the maximum file size.
				String s_maxFileSize;
				Long maxFileSize = null;
				try {
					//The file size is specified in MB 
					s_maxFileSize = PortletRequestUtils.getStringParameter(request, "maxFileSize", "").trim();
					if (!s_maxFileSize.equals("")) {
						maxFileSize = Long.valueOf(s_maxFileSize);
					}
					getBinderModule().setBinderMaxFileSize(binderId, maxFileSize);
				} catch (Exception ex) {
					// The value entered by the user must not be valid, don't set it.
				}
				
				//Is encryption enabled
				Boolean fileEncryptionEnabled = PortletRequestUtils.getBooleanParameter(request, "enableFileEncryption", Boolean.FALSE);
				FilesErrors errors = new FilesErrors();
				getBinderModule().setBinderFileEncryptionEnabled(binderId, fileEncryptionEnabled, errors);
	         	if(errors.getProblems().size() > 0) {
	        		// At least one error occured during the operation. 
		    		response.setRenderParameter(WebKeys.FILE_PROCESSING_ERRORS, errors.toString());
		    		response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		    		return;
	        	}
				
			}
			if (formData.containsKey("okBtn")) {
				if (binder instanceof TemplateBinder) {
					setupViewTemplateBinder(response, binderId, binder.getEntityType().name());
				} else {
					setupViewBinder(response, binderId, binder.getEntityType().name());
				}
			}

		} else if (formData.containsKey("closeBtn") || formData.containsKey("cancelBtn")) {
			//The user clicked the cancel button
			if (binder instanceof TemplateBinder) {
				response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
				response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());
			} else {
				setupViewBinder(response, binderId, binder.getEntityType().name());
			}
		}
	}

	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request,
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Long binderId = PortletRequestUtils.getLongParameter(request,
				WebKeys.URL_BINDER_ID);
		Binder binder = getBinderModule().getBinder(binderId);
		model.put(WebKeys.BINDER, binder);
		
		model.put(WebKeys.BINDER_VERSIONS_INHERITED, Boolean.FALSE);
		if (binder.getVersionsEnabled() == null) {
			model.put(WebKeys.BINDER_VERSIONS_INHERITED, Boolean.TRUE);
		}
		model.put(WebKeys.BINDER_FILE_SIZE_LIMIT_INHERITED, Boolean.FALSE);
		if (binder.getMaxFileSize() == null) {
			model.put(WebKeys.BINDER_FILE_SIZE_LIMIT_INHERITED, Boolean.TRUE);
		}
		model.put(WebKeys.BINDER_VERSIONS_ENABLED, getBinderModule().getBinderVersionsEnabled(binder));
		model.put(WebKeys.BINDER_VERSIONS_TO_KEEP, getBinderModule().getBinderVersionsToKeep(binder));
		model.put(WebKeys.BINDER_VERSION_AGING_ENABLED, getBinderModule().getBinderVersionAgingEnabled(binder));
		model.put(WebKeys.BINDER_VERSION_AGING_DAYS, getBinderModule().getBinderVersionAgingDays(binder));
		model.put(WebKeys.BINDER_VERSIONS_MAX_FILE_SIZE, getBinderModule().getBinderMaxFileSize(binder));
		model.put(WebKeys.BINDER_FILE_ENCRYPTION_ENABLED, binder.getFileEncryptionEnabled());
		model.put(WebKeys.BINDER_FILE_ENCRYPTION_ENABLED_INHERITED, getBinderModule().isBinderFileEncryptionEnabled(binder));
		model.put(WebKeys.FILE_SIZE_LIMIT_USER_DEFAULT, getAdminModule().getFileSizeLimitUserDefault());
		Boolean encryptAllFiles = SPropsUtil.getBoolean("file.encryption.encryptAll", false);
		model.put(WebKeys.FILE_ENCRYPTION_ENABLED_ALL, encryptAllFiles);
		Boolean allowBinderFilesEncryption = SPropsUtil.getBoolean("file.encryption.allowPerFolderEncryption", false);
		model.put(WebKeys.BINDER_FILE_ENCRYPTION_ALLOWED, allowBinderFilesEncryption);
		Set<Long> entryIds = getBinderModule().getUnEncryptedBinderEntryIds(binderId, false);
		model.put(WebKeys.BINDER_FILES_NOT_ENCRYPTED, Integer.valueOf(entryIds.size()));
		
		Long fileVersionMaxAge = getAdminModule().getFileVersionsMaxAge();
		model.put(WebKeys.FILE_VERSION_MAXIMUM_AGE, fileVersionMaxAge);

		//Set up navigation beans
		model.put(WebKeys.DEFINITION_ENTRY, binder);
		BinderHelper.buildNavigationLinkBeans(this, binder, model);

		return new ModelAndView(WebKeys.VIEW_MANAGE_BINDER_VERSION_CONTROLS, model);
	}

}

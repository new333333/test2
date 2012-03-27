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
package org.kablink.teaming.portlet.administration;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.NameMissingException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.portletadapter.MultipartFileSupport;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.util.Validator;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.portlet.ModelAndView;


public class ExtensionsController extends  SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (WebHelper.isMethodPost(request)) {
			Map fileMap=null;
			if (request instanceof MultipartFileSupport) {
		    	getAdminModule().checkAccess(AdminOperation.manageExtensions);
				
				//ZoneConfig zoneConfig = getZoneModule().getZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
				//String zoneName = zoneConfig.;
				
				//String zoneName = PortletRequestUtils.getStringParameter(request,"zoneName");
				//if(Validator.isNull(zoneName))
				//	throw new NameMissingException("errorcode.zonename.missing");
				
				//SZoneConfig.getDefaultZoneName();
				//if(!(zoneName.equals(SZoneConfig.getDefaultZoneName()))){
				//Long zoneId = getZoneModule().getZoneIdByZoneName(zoneName);
				//zoneFolderKey = zoneName + "_" + zoneId;
				//}

				String zoneFolderKey = Utils.getZoneKey(); 

				fileMap = ((MultipartFileSupport) request).getFileMap();
		    	MultipartFile myFile = (MultipartFile)fileMap.get("uploadFormElement");
		    	InputStream fIn = myFile.getInputStream();
		    	
		    	String sharedExtensionDir = SPropsUtil.getDirPath("data.extension.root.dir") + "extensions" + File.separator + zoneFolderKey;
				File sharedDir = new File(sharedExtensionDir);		
				if (!sharedDir.exists()) sharedDir.mkdirs();
				
		    	String fileName = sharedExtensionDir + File.separator + myFile.getOriginalFilename();
		    	File outFile = new File(fileName);
		    	
		    	OutputStream os = new BufferedOutputStream(new FileOutputStream(outFile));
		    	
		    	FileCopyUtils.copy(fIn, os);
		    	
		    	//now the file is there, call the extension deployer
		    	getAdminModule().getExtensionManager().deploy();

				response.setRenderParameter( "extensions_updated", "success" );

			} 
			
			response.setRenderParameters(formData);
		} 
		else
			response.setRenderParameters(formData);
	}

	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		
    	getAdminModule().checkAccess(AdminOperation.manageExtensions);
		Map model = new HashMap();
		
//		String extensionUpdate = PortletRequestUtils.getStringParameter( request, "extensions_updated", "" );
//		if (extensionUpdate != null){
//
//			Map statusMap = new HashMap();
//			statusMap.put("ss_operation_denied", NLT.get("task.update.unauthorized"));
//
//			return new ModelAndView(WebKeys.AJAX_STATUS, statusMap);	
//		}
	    model.put( WebKeys.ZONE_INFO_LIST, getZoneModule().getZoneInfos());		
		
		// Pass back any error information.
		model.put( WebKeys.EXCEPTION, request.getParameter( WebKeys.EXCEPTION ) );
	
		return new ModelAndView(WebKeys.VIEW_ADMIN_MANAGE_EXTENSIONS, model);
	}
	
}

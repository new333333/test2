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
package com.sitescape.team.web.upload;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import com.sitescape.team.portletadapter.portlet.ActionRequestImpl;
import com.sitescape.team.portletadapter.support.AdaptedPortlets;
import com.sitescape.team.portletadapter.support.KeyNames;
import com.sitescape.team.portletadapter.support.PortletInfo;
import com.sitescape.team.web.WebKeys;

public class MultipartResolverProgressListening extends
		CommonsMultipartResolver {

	public MultipartHttpServletRequest resolveMultipart(
			HttpServletRequest request) throws MultipartException {
		String encoding = determineEncoding(request);
		FileUpload fileUpload = prepareFileUpload(encoding);

		try {
			String portletName = RequestUtils.getRequiredStringParameter(
					request, KeyNames.PORTLET_URL_PORTLET_NAME);
			String uploadRequestUid = RequestUtils.getStringParameter(request,
					WebKeys.URL_UPLOAD_REQUEST_UID, "");

			PortletInfo portletInfo = (PortletInfo) AdaptedPortlets
					.getPortletInfo(portletName);

			ActionRequestImpl actionReq = new ActionRequestImpl(request,
					portletInfo, AdaptedPortlets.getPortletContext());

			FileUploadProgressListener fileUploadProgressListener = new FileUploadProgressListener();
			fileUpload.setProgressListener(fileUploadProgressListener);
			ProgressListenerSessionResolver.set(actionReq
					.getPortletSession(), uploadRequestUid,
					fileUploadProgressListener);

		} catch (ServletRequestBindingException e) {
			// skip progress listening and continue without it
		}

		try {
			List fileItems = ((ServletFileUpload) fileUpload)
					.parseRequest(request);
			MultipartParsingResult parsingResult = parseFileItems(fileItems,
					encoding);
			return new DefaultMultipartHttpServletRequest(request,
					parsingResult.getMultipartFiles(), parsingResult
							.getMultipartParameters());
		} catch (FileUploadBase.SizeLimitExceededException ex) {
			throw new MaxUploadSizeExceededException(fileUpload.getSizeMax(),
					ex);
		} catch (FileUploadException ex) {
			throw new MultipartException(
					"Could not parse multipart servlet request", ex);
		}
	}
}
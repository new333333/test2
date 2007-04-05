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
package com.sitescape.team.web.portlet;

import java.util.Iterator;
import java.util.Map;

import javax.portlet.ActionRequest;

import org.springframework.web.multipart.MultipartFile;

import com.sitescape.team.portletadapter.MultipartFileSupport;

public class ParamsWrappedActionRequestWithMultipartFileSupport 
	extends ParamsWrappedActionRequest implements MultipartFileSupport {

	private MultipartFileSupport mfs;

	public ParamsWrappedActionRequestWithMultipartFileSupport(ActionRequest req, Map params) {
		super(req, params);
		mfs = (MultipartFileSupport) req;
	}

	public Iterator getFileNames() {
		return mfs.getFileNames();
	}

	public MultipartFile getFile(String name) {
		return mfs.getFile(name);
	}

	public Map getFileMap() {
		return mfs.getFileMap();
	}
}

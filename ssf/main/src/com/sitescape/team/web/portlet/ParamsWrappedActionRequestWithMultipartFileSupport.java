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

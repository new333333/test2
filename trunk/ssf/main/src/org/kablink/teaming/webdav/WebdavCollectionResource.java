/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.webdav;

import groovy.lang.Binding;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.util.NLT;

import com.bradmcevoy.http.CollectionResource;
import com.bradmcevoy.http.GetableResource;
import com.bradmcevoy.http.PropFindableResource;
import com.bradmcevoy.http.Range;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.bradmcevoy.http.exceptions.NotFoundException;

/**
 * @author jong
 *
 */
public abstract class WebdavCollectionResource extends WebdavResource implements PropFindableResource, GetableResource, CollectionResource {

	protected static final String CONTENT_TYPE_TEXT_HTML_UTF8 = "text/html; charset=utf-8";
	
	protected WebdavCollectionResource(WebdavResourceFactory factory, String webdavPath, String name) {
		super(factory, webdavPath, name);
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.GetableResource#getContentType(java.lang.String)
	 */
	@Override
	public String getContentType(String accepts) {
		return CONTENT_TYPE_TEXT_HTML_UTF8;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.GetableResource#getContentLength()
	 */
	@Override
	public Long getContentLength() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.GetableResource#sendContent(java.io.OutputStream, com.bradmcevoy.http.Range, java.util.Map, java.lang.String)
	 */
	@Override
	public void sendContent(OutputStream out, Range range,
			Map<String, String> params, String contentType) throws IOException,
			NotAuthorizedException, BadRequestException, NotFoundException {
		String content = getDirectoryListing(getName(), getChildren());
		out.write(content.getBytes("UTF-8"));
	}

	protected String getDirectoryListing(String name, List<? extends Resource> list) {
		if(factory.isAllowDirectoryBrowsing()) {
			/*
			List<String> childNames = new ArrayList<String>(list.size());
			for(Resource child:list)
				childNames.add(child.getName());
			Binding binding = new Binding();
			binding.setVariable("parent", name);
			binding.setVariable("children", childNames);
			*/
			
			Binding binding = new Binding();
			binding.setVariable("parent", this);
			binding.setVariable("children", list);
			
			try {
				getGroovyScriptService().execute("webdav_dir_list.groovy", binding);
			} catch (ResourceException e) {
				return e.toString();
			} catch (ScriptException e) {
				return e.toString();
			}
			Object output = binding.getVariable("output");
			return output.toString();
		}
		else {
			return NLT.get("wd.dir.browsing.disabled");
		}
	}
	
}

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
package com.sitescape.team.servlet.definition;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.domain.Definition;
import com.sitescape.team.web.servlet.SAbstractController;
import com.sitescape.util.Validator;
import org.springframework.web.bind.RequestUtils;

public class ViewDefinitionXmlController extends SAbstractController {
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {		

		String id = RequestUtils.getStringParameter(request, "id", "");

		String data = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
		if (!Validator.isNull(id) ) {
			//A definition was selected, go view it
			Definition def = getDefinitionModule().getDefinition(id);
			Document workflowDoc = def.getDefinition();
			data = workflowDoc.asXML();
		}
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "private");
		response.setHeader("Pragma", "no-cache");
		response.getOutputStream().write(data.getBytes());

		response.getOutputStream().flush();
		return null;
	}
}

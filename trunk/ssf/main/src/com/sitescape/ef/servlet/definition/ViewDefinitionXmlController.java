package com.sitescape.ef.servlet.definition;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.springframework.web.servlet.ModelAndView;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.web.servlet.SAbstractController;
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

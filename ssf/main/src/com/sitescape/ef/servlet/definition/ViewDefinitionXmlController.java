package com.sitescape.ef.servlet.definition;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.springframework.web.servlet.ModelAndView;
import javax.activation.FileTypeMap;
import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.servlet.SAbstractController;
import com.sitescape.util.FileUtil;
import com.sitescape.util.Validator;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.ef.repository.RepositoryService;
import com.sitescape.ef.repository.RepositoryServiceUtil;
import org.springframework.web.bind.RequestUtils;

public class ViewDefinitionXmlController extends SAbstractController {
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {		

		Map model;
		String id = RequestUtils.getStringParameter(request, "id", "");
        model = getForumActionModule().getDefinitionXml(request, id);

		String data = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
		if (!Validator.isNull(id) ) {
			//A definition was selected, go view it
			Definition def = (Definition)model.get(WebKeys.DEFINITION);
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

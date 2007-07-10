package com.sitescape.team.portlet.administration;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DocumentSource;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Node;

import com.sitescape.team.license.LicenseException;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.util.Validator;

public class ManageLicenseController extends SAbstractController {
	private static final String LICENSE_XSL_FILE = "/WEB-INF/xslt/license.xslt";
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		Map formData = request.getParameterMap();
		if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			response.setRenderParameter("redirect", "true");
		}
		if(formData.containsKey("updateBtn")) {
			try {
				getLicenseModule().updateLicense();
			} catch(LicenseException e) {
			}
		}
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		if (!Validator.isNull(request.getParameter("redirect"))) {
			return new ModelAndView(WebKeys.VIEW_ADMIN_REDIRECT);
		}
		Map model = new HashMap();
		try {
			getLicenseModule().validateLicense();
		} catch(LicenseException e) {
			model.put(WebKeys.LICENSE_EXCEPTION,
					  e.getLocalizedMessage());
		}
		Document doc = getLicenseModule().getLicense();
		String visibleDoc = "";
		if(doc != null) {
			try {
				TransformerFactory transFactory = TransformerFactory.newInstance();
				Source xsltSource = new StreamSource(request.getPortletSession().getPortletContext().getResourceAsStream(LICENSE_XSL_FILE));
				Templates template = transFactory.newTemplates(xsltSource);
				Transformer trans = template.newTransformer();
				StreamResult result = new StreamResult(new StringWriter());
				trans.transform(new DocumentSource(doc), result);
				visibleDoc = result.getWriter().toString().replaceAll("<\\?xml .*\\?>", "");
				Document helperDoc = DocumentHelper.createDocument();
				Element helperRoot = helperDoc.addElement("pre");
				helperRoot.addText(visibleDoc);
				visibleDoc = helperRoot.asXML();
			} catch(TransformerConfigurationException e) {
				logger.warn("Unable to process license with XSL", e);
			}
		}
		model.put(WebKeys.LICENSE, visibleDoc);
		return new ModelAndView("administration/manage_license", model);
		
	}
}

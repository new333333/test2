package com.sitescape.team.portlet.administration;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
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
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.DocumentSource;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.license.LicenseException;
import com.sitescape.team.util.NLT;
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
		
		model.put(WebKeys.LICENSE_KEY, getValue(doc, "//KeyInfo/@uid"));
		model.put(WebKeys.LICENSE_ISSUED, getValue(doc, "//KeyInfo/@issued"));
		
		String expireDate = getValue(doc, "//Dates/@expiration");
		
		if(expireDate.equals("1/1/2500"))
			model.put(WebKeys.LICENSE_EFFECTIVE, getValue(doc, "//Dates/@effective") 
					+ " - " + NLT.get("license.expire.never"));
		else
			model.put(WebKeys.LICENSE_EFFECTIVE, getValue(doc, "//Dates/@effective") + " - " + expireDate);
		model.put(WebKeys.LICENSE_USERS, getValue(doc, "//Users/@registered"));
		model.put(WebKeys.LICENSE_CONTACT, getValue(doc, "//AuditPolicy/ReportContact"));
		
		model.put(WebKeys.LICENSE_ISSUER, getValue(doc, "//KeyInfo/@by"));
		model.put(WebKeys.LICENSE_PRODUCT_ID, getValue(doc, "//Product/@id"));
		model.put(WebKeys.LICENSE_PRODUCT_TITLE, getValue(doc, "//Product/@title"));
		model.put(WebKeys.LICENSE_PRODUCT_VERSION, getValue(doc, "//Product/@version"));
		model.put(WebKeys.LICENSE_EXTERNAL_USERS, getValue(doc, "//Users/@external"));
		
		
		if(doc != null) {
			
			Object obj = doc.selectObject("//Options/*");
			
			if(obj != null) {
				if(obj instanceof List) {
					List options = null;
					options = (List) obj;
		
					if(options != null) {
						StringBuilder optionsList = new StringBuilder();
			
						for(int i = 0; i < options.size(); i++) {
							Element ele = (Element) options.get(i);
							optionsList.append(ele.attribute("title").getValue() + ",");
						}
						model.put(WebKeys.LICENSE_OPTIONS_LIST, optionsList.toString());
					}
				}
				if(obj instanceof Element) {
					Element singleOption = null;
					singleOption = (Element) obj;
				
					if(singleOption != null) {
						model.put(WebKeys.LICENSE_OPTIONS_LIST, singleOption.attribute("title").getValue());
					}
				}
			}
			
			obj = doc.selectObject("//ExternalAccess/*");
			
			if(obj != null) {
				if(obj instanceof List) {
					List extAccess = null;
					extAccess = (List) obj;
		
					if(extAccess != null) {
						StringBuilder extAccessList = new StringBuilder();
			
						for(int i = 0; i < extAccess.size(); i++) {
							Element ele = (Element) extAccess.get(i);
							extAccessList.append(ele.asXML().replace("<", "").replace("/>", "") + ",");
						}
						model.put(WebKeys.LICENSE_EXTERNAL_ACCESS_LIST, extAccessList.toString());
					}
				}
				if(obj instanceof Element) {
					Element singleExtAccess = null;
					singleExtAccess = (Element) obj;
				
					if(singleExtAccess != null) {
						model.put(WebKeys.LICENSE_EXTERNAL_ACCESS_LIST, singleExtAccess.asXML().replace("<", "").replace("/>", ""));
					}
				}
			}
			
		}
			
		model.put(WebKeys.LICENSE, visibleDoc);
		return new ModelAndView("administration/manage_license", model);
		
	}
	
	private String getValue(Document doc, String xpath)
	{
		Node node = null;
		return (doc != null && (node=doc.selectSingleNode(xpath))!=null)?node.getText():"";
	}
	
	private List<Node> getMultipleValues(Document doc, String xpath)
	{
		List<Node> list = null;
		return (doc != null && (list=doc.selectNodes(xpath))!=null)?list:null;
	}
}

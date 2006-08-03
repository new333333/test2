package com.sitescape.ef.portlet.administration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.io.FileWriter;
import java.io.File;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.dom4j.io.OutputFormat;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.util.FileHelper;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.util.SPropsUtil;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.util.Validator;
public class ExportDefinitionController extends  SAbstractController {
	
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("okBtn")) {
			List errors = new ArrayList();
			//Get the forums to be indexed
			String dirPath = SPropsUtil.getDirPath("data.root.dir") + File.separator + RequestContextHolder.getRequestContext().getZoneName() +
				File.separator + "definitions";
			FileHelper.mkdirsIfNecessary(dirPath);
			Iterator itFormData = formData.entrySet().iterator();
			while (itFormData.hasNext()) {
				Map.Entry me = (Map.Entry) itFormData.next();
				if (((String)me.getKey()).startsWith("id_")) {
					String defId = ((String)me.getKey()).substring(3);
					if (Validator.isNotNull(defId)) {
						Definition def =null;
						try {
							def = getDefinitionModule().getDefinition(defId);
				    		FileWriter fOut = new FileWriter(dirPath + File.separator +  def.getName() + ".xml");
				    		XMLWriter xOut = new XMLWriter(fOut, OutputFormat.createPrettyPrint());
				    		xOut.write(def.getDefinition());
				    		xOut.close();
						} catch (Exception ex) {
							errors.add(ex.getLocalizedMessage());
						}
					}
				}
			}
			
			response.setRenderParameter(WebKeys.ERROR_LIST, (String[])errors.toArray( new String[0]));
			response.setRenderParameter("redirect", "true");
			
		} else if (formData.containsKey("cancelBtn")) {
			response.setRenderParameter("redirect", "true");
		} else
			response.setRenderParameters(formData);
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();			
		if (!Validator.isNull(request.getParameter("redirect"))) {
			String [] errors = request.getParameterValues(WebKeys.ERROR_LIST);
			model.put(WebKeys.ERROR_LIST, errors);
			return new ModelAndView(WebKeys.VIEW_ADMIN_REDIRECT, model);
		}
		Document definitionConfig = getDefinitionModule().getDefinitionConfig();
		List currentDefinitions = getDefinitionModule().getDefinitions();
		
		//Build the definition tree
		Document definitionTree = DocumentHelper.createDocument();
		Element dtRoot = definitionTree.addElement(DomTreeBuilder.NODE_ROOT);
		dtRoot.addAttribute("title", NLT.getDef("__definitions"));
		dtRoot.addAttribute("id", "definitions");
		dtRoot.addAttribute("displayOnly", "true");
		dtRoot.addAttribute("url", "");
		Element root = definitionConfig.getRootElement();
		
		Iterator definitions = root.elementIterator("definition");
		while (definitions.hasNext()) {
			Element defEle = (Element) definitions.next();
			Element treeEle = dtRoot.addElement("child");
			treeEle.addAttribute("type", "definition");
			treeEle.addAttribute("title", NLT.getDef(defEle.attributeValue("caption")));
			treeEle.addAttribute("id", defEle.attributeValue("name"));	
			treeEle.addAttribute("displayOnly", "true");
			treeEle.addAttribute("url", "");
			//Add the current definitions (if any)
			ListIterator li = currentDefinitions.listIterator();
			while (li.hasNext()) {
				Definition curDef = (Definition)li.next();
				Document curDefDoc = curDef.getDefinition();
				if (curDefDoc == null) continue;
				if (curDef.getType() == Integer.valueOf(defEle.attributeValue("definitionType", "0")).intValue()) {
					Element curDefEle = treeEle.addElement("child");
					curDefEle.addAttribute("type", defEle.attributeValue("name"));
					String title = NLT.getDef(curDef.getName());
					//TODO get the caption from the definition meta data
					String caption = curDefDoc.getRootElement().attributeValue("caption", "");
					if (!caption.equals("")) {
						title = caption + " (" + title + ")";
					}
					curDefEle.addAttribute("title", title);
					curDefEle.addAttribute("id", curDef.getId());
					curDefEle.addAttribute("url", "");
				}
			}
		}
		model.put(WebKeys.DOM_TREE, definitionTree);
 		return new ModelAndView(WebKeys.VIEW_ADMIN_EXPORT_DEFINITIONS, model);
	}
}

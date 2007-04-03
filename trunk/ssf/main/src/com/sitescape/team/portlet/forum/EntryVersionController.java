package com.sitescape.team.portlet.forum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.ChangeLog;
import com.sitescape.team.domain.User;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.util.LcsAlgorithm;


public class EntryVersionController extends  SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Long entityId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTITY_ID);
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, null);
		String viewPath = "forum/view_description_history";
		Map model = new HashMap();
		
		Map formData = request.getParameterMap();
				
		List changeList = new ArrayList();
		List changes = null;
		String entityType = PortletRequestUtils.getStringParameter(request,  "entityType", "folderEntry");
		if (entityId != null) {
			if (operation.equals(ChangeLog.MODIFYENTRY)) {
				//Start the list with the "addEntry" entry
				changes = getAdminModule().getChanges(entityId, entityType, ChangeLog.ADDENTRY);
				changeList.addAll(BinderHelper.BuildChangeLogBeans(changes));
			}
			changes = getAdminModule().getChanges(entityId, entityType, operation);
			changeList.addAll(BinderHelper.BuildChangeLogBeans(changes));
		}
		
		model.put(WebKeys.CHANGE_LOG_LIST, changeList);
		model.put(WebKeys.ENTITY_ID, entityId);

		if (formData.containsKey("okBtn")) {
			//This is a request to compare two versions; build compare beans
			String item1 = PortletRequestUtils.getStringParameter(request, "item1", "");
			String item2 = PortletRequestUtils.getStringParameter(request, "item2", "");
			String title1 = "";
			String title2 = "";
			String description1 = "";
			String description2 = "";
			for (int i = 0; i < changeList.size(); i++) {
				Map changeMap = (Map) changeList.get(i);
				Map entityTypeMap = (Map) changeMap.get(entityType);
				Map attributesMap = (Map) entityTypeMap.get("attributes");
				if (attributesMap.get("logVersion").equals(item1)) {
					Map attributeMap = (Map) entityTypeMap.get("attribute");
					title1 = (String) attributeMap.get("title");
					description1 = (String) attributeMap.get("description");
				}
				if (attributesMap.get("logVersion").equals(item2)) {
					Map attributeMap = (Map) entityTypeMap.get("attribute");
					title2 = ((String)attributeMap.get("title")).trim();
					description2 = ((String)attributeMap.get("description")).trim();
				}
			}
			LcsAlgorithm titleDiff = new LcsAlgorithm(title1, title2);
			LcsAlgorithm descriptionDiff = new LcsAlgorithm(description1, description2);
			model.put("item1", item1);
			model.put("item2", item2);
			model.put("title1", title1);
			model.put("title2", title2);
			model.put("titleDiff", titleDiff.getString());
			model.put("description1", description1);
			model.put("description2", description2);
			model.put("descriptionDiff", descriptionDiff.getString());
		}

		return new ModelAndView(viewPath, model);
	} 

}
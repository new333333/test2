package com.sitescape.ef.portlet.profile;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.module.shared.MapInputData;
import com.sitescape.ef.web.util.PortletRequestUtils;

public class ListProfilesController extends  SAbstractProfileController {
	
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		if (op.equals(WebKeys.FORUM_OPERATION_SET_DISPLAY_STYLE)) {
			Map updates = new HashMap();
		   	User user = RequestContextHolder.getRequestContext().getUser();
			updates.put("displayStyle", PortletRequestUtils.getStringParameter(request,WebKeys.URL_VALUE,""));
			getProfileModule().modifyEntry(user.getParentBinder().getId(), user.getId(), new MapInputData(updates), new HashMap());
		}
		return returnToView(request, response);
	}

}

package com.sitescape.ef.portlet.forum;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.domain.EntityIdentifier;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.BinderHelper;
import com.sitescape.ef.web.util.PortletRequestUtils;


/**
 * @author Peter Hurley
 *
 */
public class ReloadOpenerController  extends SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
 		Map<String,Object> model = new HashMap<String,Object>();
 		
	    return new ModelAndView("forum/reload_opener", model);
	}

}

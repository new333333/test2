package org.kablink.teaming.portal.jsr168;

import java.util.HashMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;

public class TeamingController extends AbstractController {
	protected Log logger = LogFactory.getLog(getClass());
	
	protected void handleActionRequestInternal(ActionRequest request, ActionResponse response)
	throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}

	protected ModelAndView handleRenderRequestInternal(RenderRequest request, RenderResponse response)
	throws Exception {
		HashMap model = new HashMap();
		
		return new ModelAndView("teaming", model);
	}
}

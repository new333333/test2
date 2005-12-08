package com.sitescape.ef.portlet.administration;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.WindowState;
import javax.portlet.PortletMode;

import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.WebKeys;
public class ManageProfileIndexController extends  SAbstractController {
	
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		getProfileModule().index();
		response.setRenderParameter(WebKeys.ACTION, "");
		response.setWindowState(WindowState.NORMAL);
		response.setPortletMode(PortletMode.VIEW);
			
	}

}

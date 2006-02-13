package com.sitescape.ef.portlet.forum;

import javax.portlet.ActionResponse;

import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.portlet.binder.AbstractAccessControlController;
import com.sitescape.ef.portlet.binder.AbstractConfigureController;

/**
 * @author Peter Hurley
 *
 */
public class AccessControlController extends AbstractAccessControlController {
	protected void setResponseOnClose(ActionResponse response, Long binderId) {
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
		response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());
	}

}

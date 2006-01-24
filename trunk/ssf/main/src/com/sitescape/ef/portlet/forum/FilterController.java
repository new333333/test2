package com.sitescape.ef.portlet.forum;

import javax.portlet.ActionResponse;

import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.portlet.binder.AbstractConfigureController;
import com.sitescape.ef.portlet.binder.AbstractFilterController;

/**
 * @author Peter Hurley
 *
 */
public class FilterController extends AbstractFilterController {
	protected void setResponseOnClose(ActionResponse response, Long binderId) {
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
		response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());
	}

}

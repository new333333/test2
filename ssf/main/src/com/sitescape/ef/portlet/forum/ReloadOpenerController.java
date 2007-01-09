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
 		
		String blogReply = PortletRequestUtils.getStringParameter(request, WebKeys.BLOG_REPLY, "");
		if (!blogReply.equals("")) {
			String entryId = PortletRequestUtils.getStringParameter(request, WebKeys.ENTRY_ID, "");
			String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.NAMESPACE, "");
			String blogReplyCount = PortletRequestUtils.getStringParameter(request, WebKeys.BLOG_REPLY_COUNT, "");
			model.put(WebKeys.ENTRY_ID, entryId);
			model.put(WebKeys.NAMESPACE, namespace);
			model.put(WebKeys.BLOG_REPLY_COUNT, blogReplyCount);
			return new ModelAndView("forum/reload_blog_reply", model);
		}
		
	    return new ModelAndView("forum/reload_opener", model);
	}

}

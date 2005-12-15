package com.sitescape.ef.portlet.forum;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.WebHelper;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.lucene.Hits;
/**
 * @author Peter Hurley
 *
 */
public class ListUnseenController  extends SAbstractForumController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		if(!WebHelper.isUserLoggedIn(request)) {
			// TODO Output whatever error message here or return appropriate
			// view for reporting error. 
			// Note: Because request context is not set up in this case, the
			// scope of the work this method can perform is very restricted.
			// For example, no database access is possible, etc. So the code
			// here should be limited to outputing appropriate error message.
			return null;
		}
		
        User user = RequestContextHolder.getRequestContext().getUser();
		Map model = new HashMap();
		Map seenMaps = new HashMap();

		String op = PortletRequestUtils.getStringParameter(request, WebKeys.FORUM_URL_OPERATION, "");
		if (op.equals(WebKeys.FORUM_OPERATION_UNSEEN_COUNTS)) {
			List folderIds = new ArrayList();
			String[] forumList = new String[0];
			if (PortletRequestUtils.getStringParameter(request, "forumList") != null) {
				forumList = PortletRequestUtils.getStringParameter(request, "forumList").split(" ");
			}
			for (int i = 0; i < forumList.length; i++) {
				folderIds.add(new Long(forumList[i]));
			}
			Map unseenCounts = getFolderModule().getUnseenCounts(folderIds);

			response.setContentType("text/xml");
			
			model.put("unseenCounts", unseenCounts);
			return new ModelAndView("forum/unseen_counts", model);
			
		} else if (op.equals(WebKeys.FORUM_OPERATION_UNSEEN_LIST)) {
			
		}
		return new ModelAndView(WebKeys.VIEW, model);
	} 
}

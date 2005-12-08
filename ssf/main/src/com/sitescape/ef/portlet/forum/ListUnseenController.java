package com.sitescape.ef.portlet.forum;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.portlet.PortletSession;

import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.TreeMap;

import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.WebHelper;
import com.sitescape.ef.web.util.DateHelper;
import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.NoFolderByTheIdException;
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
        User user = RequestContextHolder.getRequestContext().getUser();
		Map formData = request.getParameterMap();
		Long folderId = null;
		Map model = new HashMap();
		Map seenMaps = new HashMap();

		String op = PortletRequestUtils.getStringParameter(request, WebKeys.FORUM_URL_OPERATION, "");
		if (op.equals(WebKeys.FORUM_OPERATION_UNSEEN_COUNTS)) {
			List folderIds = null;
			String[] forumList = new String[0];
			if (PortletRequestUtils.getStringParameter(request, "forumList") != null) {
				forumList = PortletRequestUtils.getStringParameter(request, "forumList").split(" ");
			}
			for (int i = 0; i < forumList.length; i++) {
				folderIds.add(new Long(forumList[i]));
			}
			List folders = getFolderModule().getFolders(folderIds);
			getProfileModule().getUserSeenMap(user.getId(), folderId);
			Hits hits = getFolderModule().getRecentEntries(folders, seenMaps);
			response.setContentType("text/xml");
			return new ModelAndView("forum/unseen_counts");
			
		} else if (op.equals(WebKeys.FORUM_OPERATION_UNSEEN_LIST)) {
			
		}
		return new ModelAndView(WebKeys.VIEW, model);
	} 
}

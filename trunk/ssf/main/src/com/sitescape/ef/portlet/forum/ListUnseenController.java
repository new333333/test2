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
			Map unseenCounts = new HashMap();
			List folders = getFolderModule().getFolders(folderIds);
			Iterator itFolders = folders.iterator();
			while (itFolders.hasNext()) {
				Folder folder = (Folder) itFolders.next();
				seenMaps.put(folder.getId(), getProfileModule().getUserSeenMap(user.getId(), folder.getId()));
				unseenCounts.put(folder.getId().toString(), new Integer(0));
			}
			Hits hits = getFolderModule().getRecentEntries(folders, seenMaps);
			for (int i = 0; i < hits.length(); i++) {
				String folderIdString = hits.doc(i).getField("_folderId").stringValue();
				if (!unseenCounts.containsKey(folderIdString)) {
					unseenCounts.put(folderIdString, new Integer(0));
				}
				Integer count = (Integer) unseenCounts.get(folderIdString);
				count = new Integer(count.intValue()+1);
				unseenCounts.put(folderIdString, count);
			}

			response.setContentType("text/xml");
			
			model.put("forums", folders);
			model.put("unseenCounts", unseenCounts);
			return new ModelAndView("forum/unseen_counts", model);
			
		} else if (op.equals(WebKeys.FORUM_OPERATION_UNSEEN_LIST)) {
			
		}
		return new ModelAndView(WebKeys.VIEW, model);
	} 
}

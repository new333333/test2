
package com.sitescape.ef.portlet.forum;
import java.util.Map;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sitescape.ef.domain.HistoryMap;

/**
 * @author Janet McCann
 * ReadOnly transactions that gather the information needed for forum UI
 */
public interface ForumActionModule {

	public Map getShowEntry(Map formData, RenderRequest request, RenderResponse response, Long folderId);
	public Map getShowEntry(Map formData, HttpServletRequest request, HttpServletResponse response, Long folderId);
	public Map getShowEntry(Map formData, HistoryMap history, Long folderId);
	public Map getShowFolder(Map formData, RenderRequest request, RenderResponse response, Long folderId);
	public Map getConfigureForum(Map formData, RenderRequest request, Long folderId);
	public Map getAddEntry(Map formData, RenderRequest request, Long folderId);
	public Map getDeleteEntry(Map formData, RenderRequest request, Long folderId);
	public Map getModifyEntry(Map formData, RenderRequest request, Long folderId);
	public Map getAddReply(Map formData, RenderRequest request, Long folderId);
	public Map getDefinitionBuilder(Map formData, RenderRequest request, String defId);
}
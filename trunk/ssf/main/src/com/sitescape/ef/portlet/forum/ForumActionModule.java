
package com.sitescape.ef.portlet.forum;
import java.util.Map;

import javax.portlet.RenderRequest;

/**
 * @author Janet McCann
 * ReadOnly transactions that gather the information needed for forum UI
 */
public interface ForumActionModule {

	public Map getShowEntry(Map formData, RenderRequest req, Long folderId);
	public Map getShowFolder(Map formData, RenderRequest req, Long folderId);
	public Map getConfigureForum(Map formData, RenderRequest req, Long folderId);
	public Map getAddEntry(Map formData, RenderRequest req, Long folderId);
	public Map getDeleteEntry(Map formData, RenderRequest req, Long folderId);
	public Map getModifyEntry(Map formData, RenderRequest req, Long folderId);
	public Map getAddReply(Map formData, RenderRequest req, Long folderId);
	public Map getDefinitionBuilder(Map formData, RenderRequest req, String defId);
}
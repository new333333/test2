
package com.sitescape.ef.portlet.forum;
import java.util.Map;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.portlet.bind.PortletRequestBindingException;
/**
 * @author Janet McCann
 * ReadOnly transactions that gather the information needed for forum UI
 */
public interface ForumActionModule {

	public Map getShowEntry(String entryId, Map formData, RenderRequest request, RenderResponse response, Long folderId) throws PortletRequestBindingException;
	public Map getShowFolder(Map formData, RenderRequest request, RenderResponse response, Long folderId) throws PortletRequestBindingException;
	public Map getConfigureForum(Map formData, RenderRequest request, Long folderId) throws PortletRequestBindingException;
	public Map getAddEntry(Map formData, RenderRequest request, Long folderId) throws PortletRequestBindingException;
	public Map getDeleteEntry(Map formData, RenderRequest request, Long folderId) throws PortletRequestBindingException;
	public Map getModifyEntry(Map formData, RenderRequest request, Long folderId) throws PortletRequestBindingException;
	public Map getAddReply(Map formData, RenderRequest request, Long folderId) throws PortletRequestBindingException;
	public Map getDefinitionXml(HttpServletRequest request, String defId) throws PortletRequestBindingException;
	public Map getDefinitionBuilder(Map formData, RenderRequest request, String defId) throws PortletRequestBindingException;
}
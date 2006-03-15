package com.sitescape.ef.portlet.administration;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.portlet.PortletMode;

import org.dom4j.Document;
import org.dom4j.Element;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.domain.EmailAlias;
import com.sitescape.util.Validator;


public class ConfigurePostingController extends  SAbstractController  {
	
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("okBtn")) {
			Map updates = new HashMap();		
			List aliases = getAdminModule().getEmailAliases();
			for (int i=0; i != -1; ++i) {
				Long id = null;
				try {
					id = PortletRequestUtils.getLongParameter(request, "folder" + i);
				} catch (Exception ex) {break;};
				if (id == null) break;
				String postingId = PortletRequestUtils.getStringParameter(request, "posting" + i, "");
				if (!Validator.isNull(postingId)) {
					if (formData.containsKey("delete" + i)) {
						getAdminModule().deletePosting(id, postingId);
					} else {				
						//update existing posting
						if (formData.containsKey("subject" + i)) {
							updates.put("subject", PortletRequestUtils.getStringParameter(request, "subject" + i, ""));
						}
						if (formData.containsKey("select" + i)) {
							EmailAlias alias = findAlias(aliases, PortletRequestUtils.getStringParameter(request, "select" + i,  ""));
							updates.put("emailAlias", alias);
						}
						getAdminModule().modifyPosting(id, postingId, updates);
					} 
				} else {
					//new entry
					if (formData.containsKey("subject" + i)) {
						updates.put("subject", PortletRequestUtils.getStringParameter(request, "subject" + i, ""));
					}
					if (formData.containsKey("select" + i)) {
						EmailAlias alias = findAlias(aliases, PortletRequestUtils.getStringParameter(request, "select" + i,  ""));
						updates.put("emailAlias", alias);
					}
					getAdminModule().addPosting(id, updates);
				}
				updates.clear();
			}			
			response.setRenderParameters(formData);
		} else if (formData.containsKey("cancelBtn")) {
			response.setRenderParameter("redirect", "true");
		} else
			response.setRenderParameters(formData);
	
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		if (!Validator.isNull(request.getParameter("redirect"))) {
			return new ModelAndView(WebKeys.VIEW_ADMIN_REDIRECT);
		}
		try {
			Map model = new HashMap();
			Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
			Folder folder = getFolderModule().getFolder(folderId);
			Folder topFolder = folder.getTopFolder();
			Document tree; 
			FolderTreeHelper helper = new FolderTreeHelper();
			if (topFolder == null) {
				tree = getFolderModule().getDomFolderTree(folderId, helper);
			} else {
				tree = getFolderModule().getDomFolderTree(topFolder.getId(), helper);			
			}
			model.put(WebKeys.FOLDER_DOM_TREE, tree);
			model.put(WebKeys.FOLDER, folder);
			model.put(WebKeys.SCHEDULE_INFO, getAdminModule().getPostingSchedule());
			model.put(WebKeys.EMAIL_ALIASES, getAdminModule().getEmailAliases());	
			model.put(WebKeys.FOLDERS, helper.getFolders());
			return new ModelAndView(WebKeys.VIEW_ADMIN_CONFIGURE_POSTING, model); 
		} catch (Exception e) {
			//assume not selected yet
			Document wsTree = getWorkspaceModule().getDomWorkspaceTree(new WSTreeHelper(response));
			return new ModelAndView(WebKeys.VIEW_ADMIN_CONFIGURE_POSTING, WebKeys.WORKSPACE_DOM_TREE, wsTree);		
		}
	}
	private EmailAlias findAlias(List aliases, String aliasId) {
		if (Validator.isNull(aliasId)) return null;
		for (int i=0; i<aliases.size(); ++i) {
			EmailAlias alias = (EmailAlias)aliases.get(i);
			if (alias.getId().equals(aliasId)) return alias;
		}
		return null;
	}
	private class FolderTreeHelper implements DomTreeBuilder {
		private List folderList=new ArrayList();
		public List getFolders() {
			return folderList;
		}
		public Element setupDomElement(String type, Object source, Element element) {
			if (type.equals(DomTreeBuilder.TYPE_FOLDER)) {
				Folder f = (Folder)source;
				folderList.add(f);
				element.addAttribute("type", "forum");
				element.addAttribute("title", f.getTitle());
				element.addAttribute("id", f.getId().toString());
				element.addAttribute("image", "forum");
				element.addAttribute("url", "javascript:");
			} else return null;
			return element;
		}
	}
	private class WSTreeHelper implements DomTreeBuilder {
		private RenderResponse response;
		public WSTreeHelper(RenderResponse response) {
			this.response = response;
		}
		public Element setupDomElement(String type, Object source, Element element) {
			PortletURL url;
			if (type.equals(DomTreeBuilder.TYPE_WORKSPACE)) {
				Workspace ws = (Workspace)source;
				element.addAttribute("type", "workspace");
				element.addAttribute("title", ws.getTitle());
				element.addAttribute("id", ws.getId().toString());
				element.addAttribute("image", "workspace");
				element.addAttribute("displayOnly", "true");
				element.addAttribute("url", "");
			} else if (type.equals(DomTreeBuilder.TYPE_FOLDER)) {
				Folder f = (Folder)source;
				element.addAttribute("type", "forum");
				element.addAttribute("title", f.getTitle());
				element.addAttribute("id", f.getId().toString());
				element.addAttribute("image", "forum");
				url = response.createRenderURL();
				url.setParameter(WebKeys.ACTION, WebKeys.POSTING_ACTION_CONFIGURE);
				url.setParameter(WebKeys.URL_BINDER_ID, f.getId().toString());
				try {
					url.setWindowState(WindowState.MAXIMIZED);
				} catch (Exception e) {};
				try {
					url.setPortletMode(PortletMode.VIEW);
				} catch (Exception e) {};
				element.addAttribute("url", url.toString());

			} else return null;
			return element;
		}
	}	
}

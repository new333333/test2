package com.sitescape.team.portlet.forum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.NotSupportedException;
import com.sitescape.team.NoObjectByTheNameException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.NoDefinitionByTheIdException;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.SeenMap;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.module.workflow.WorkflowUtils;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.portletadapter.support.PortletAdapterUtil;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.ssfs.util.SsfsUtil;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.Tabs;
import com.sitescape.team.web.util.Toolbar;
import com.sitescape.util.Validator;


public class ViewEntryController extends  SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);				
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		
		if (entryId != null) {
			//See if the user asked to change state
			if (formData.containsKey("changeStateBtn")) {
				//Change the state
				//Get the workflow process to change and the name of the new state
				Long replyId = new Long(PortletRequestUtils.getLongParameter(request, "replyId"));
				if (replyId == null) replyId = entryId;
		        Long tokenId = new Long(PortletRequestUtils.getRequiredLongParameter(request, "tokenId"));	
				String toState = PortletRequestUtils.getRequiredStringParameter(request, "toState");
				getFolderModule().modifyWorkflowState(folderId, replyId, tokenId, toState);
				response.setRenderParameter(WebKeys.IS_REFRESH, "1");
			} else if (formData.containsKey("changeRatingBtn")) {
				Long replyId = new Long(PortletRequestUtils.getLongParameter(request, "replyId"));
				if (replyId == null) replyId = entryId;
				long rating = PortletRequestUtils.getRequiredLongParameter(request, "rating");
				getFolderModule().setUserRating(folderId, replyId, rating);
				response.setRenderParameter(WebKeys.IS_REFRESH, "1");
			} else if (formData.containsKey("changeTags")) {
				boolean community = true;
				Long replyId = new Long(PortletRequestUtils.getLongParameter(request, "replyId"));
				if (replyId == null) replyId = entryId;
				String tag = PortletRequestUtils.getRequiredStringParameter(request, "tag");
				String scope = PortletRequestUtils.getRequiredStringParameter(request,"scope");
				if (scope.equalsIgnoreCase("Personal")) community = false;
				getFolderModule().setTag(folderId, replyId, tag, community);
				response.setRenderParameter(WebKeys.IS_REFRESH, "1");
			} else if (formData.containsKey("respondBtn")) {
				Long replyId = new Long(PortletRequestUtils.getLongParameter(request, "replyId"));
				if (replyId == null) replyId = entryId;
		        Long tokenId = new Long(PortletRequestUtils.getRequiredLongParameter(request, "tokenId"));	
		        getFolderModule().setWorkflowResponse(folderId, replyId, tokenId, new MapInputData(formData));
		        //force reload of listing for state change
				response.setRenderParameter(WebKeys.IS_REFRESH, "1");
			} else if (formData.containsKey("subscribeBtn")) {
				Integer style = PortletRequestUtils.getIntParameter(request, "notifyType");
				if (style != null) {
					if (style.intValue() == -1) getFolderModule().deleteSubscription(folderId, entryId);
					else getFolderModule().addSubscription(folderId, entryId, style.intValue());
					response.setRenderParameter(WebKeys.IS_REFRESH, "1");
				} 
			} else if (op.equals(WebKeys.OPERATION_SET_WIKI_HOMEPAGE)) {
				Binder binder = getBinderModule().getBinder(folderId);
				//Check the access rights of the user
				if (getBinderModule().testAccess(binder, "setProperty")) {
					getBinderModule().setProperty(folderId, ObjectKeys.BINDER_PROPERTY_WIKI_HOMEPAGE, entryId.toString());
				}
			} 
		}
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		String entryId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		Map formData = request.getParameterMap();
		String viewPath = BinderHelper.getViewListingJsp(this);
		Map model = new HashMap();

		model.put(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_ENTRY);

		boolean blnEditAttachment = SsfsUtil.supportAttachmentEdit();
		String strEditTypeForIE = SsfsUtil.attachmentEditTypeForIE();
		String strEditTypeForNonIE = SsfsUtil.attachmentEditTypeForNonIE();

		model.put(WebKeys.ENTRY_ATTACHMENT_ALLOW_EDIT, ""+blnEditAttachment);
		model.put(WebKeys.ENTRY_ATTACHMENT_EDIT_TYPE_FOR_IE, strEditTypeForIE);
		model.put(WebKeys.ENTRY_ATTACHMENT_EDIT_TYPE_FOR_NON_IE, strEditTypeForNonIE);
		
		if (formData.containsKey("ssReloadUrl")) {
			PortletURL reloadUrl = response.createRenderURL();
			reloadUrl.setParameter(WebKeys.URL_BINDER_ID, folderId.toString());
			reloadUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
			reloadUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_VIEW_ENTRY);
			reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
			model = new HashMap();
			model.put("ssReloadUrl", reloadUrl.toString());			
			return new ModelAndView(viewPath, model);
		} else {
			if (Validator.isNull(entryId)) {
				entryId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TITLE, "");
				model.put(WebKeys.ENTRY_TITLE, PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_PAGE_TITLE, ""));
				Set entries = getFolderModule().getFolderEntryByNormalizedTitle(folderId, entryId);
				if (entries.size() == 1) {
					FolderEntry entry = (FolderEntry)entries.iterator().next();
					entryId = entry.getId().toString();
					getShowEntry(entryId, formData, request, response, folderId, model);
				} else if (entries.size() == 0) {
					//There are no entries by this title
					Folder folder = getFolderModule().getFolder(folderId);
					buildNoEntryBeans(request, response, folder, entryId, model);
					return new ModelAndView(WebKeys.VIEW_NO_TITLE_ENTRY, model);
				} else {
					//There are multiple matches
					model.put(WebKeys.FOLDER_ENTRIES, entries);
					return new ModelAndView(WebKeys.VIEW_MULTIPLE_TITLE_ENTRIES, model);
				}
			} else {
				getShowEntry(entryId, formData, request, response, folderId, model);
			}
			FolderEntry fe = (FolderEntry)model.get(WebKeys.ENTRY);
			
			//Set up the tabs
			Tabs tabs = new Tabs(request);
			Integer tabId = PortletRequestUtils.getIntParameter(request, WebKeys.URL_TAB_ID);
			String newTab = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NEW_TAB, "");
			if (newTab.equals("1")) {
				tabs.setCurrentTab(tabs.findTab(fe));
			} else if (newTab.equals("2")) {
				tabs.setCurrentTab(tabs.addTab(fe));
			} else if (tabId != null) {
				tabs.setCurrentTab(tabs.setTab(tabId.intValue(), fe));
			} else {
				//Change the tab only if not using the adaptor url
				if (!PortletAdapterUtil.isRunByAdapter((PortletRequest) request)) {
					// Indicates that the request is being served by the adapter framework.
					//Don't overwrite a search tab
					if (tabs.getTabType(tabs.getCurrentTab()).equals(Tabs.QUERY)) {
						tabs.setCurrentTab(tabs.findTab(fe));
					} else {
						tabs.setCurrentTab(tabs.setTab(fe));
					}
				}
			}
			model.put(WebKeys.TABS, tabs.getTabs());

			//Build the navigation beans
			BinderHelper.buildNavigationLinkBeans(this, fe.getParentBinder(), model);
			
			//only want to update visits when first enter.  Don't want cancels on modifies
			//to increment count
			if (!PortletRequestUtils.getStringParameter(request, WebKeys.IS_REFRESH, "0").equals("1")) { 
				getFolderModule().setUserVisit(fe);
			}
			Object obj = model.get(WebKeys.CONFIG_ELEMENT);
			if ((obj == null) || (obj.equals(""))) 
				return new ModelAndView(WebKeys.VIEW_NO_DEFINITION, model);
			obj = model.get(WebKeys.CONFIG_DEFINITION);
			if ((obj == null) || (obj.equals(""))) 
				return new ModelAndView(WebKeys.VIEW_NO_DEFINITION, model);
		}
		return new ModelAndView(viewPath, model);
	} 

	protected Toolbar buildEntryToolbar(RenderRequest request, RenderResponse response, Map model, 
			String folderId, String entryId) {
		Element entryViewElement = (Element)model.get(WebKeys.CONFIG_ELEMENT);
		Document entryView = entryViewElement.getDocument();
		Definition def = (Definition)model.get(WebKeys.ENTRY_DEFINITION);
		String entryDefId="";
		if (def != null)
			entryDefId= def.getId().toString();
	    //Build the toolbar array
		Toolbar toolbar = new Toolbar();
	    //The "Reply" menu
		List replyStyles = entryView.getRootElement().selectNodes("properties/property[@name='replyStyle']");
		PortletURL url;
		FolderEntry entry = (FolderEntry)model.get(WebKeys.ENTRY);
		
		User user = RequestContextHolder.getRequestContext().getUser();

		Map disabledQual = new HashMap();
		disabledQual.put("disabled", new Boolean(true));
		PortletURL nullPortletUrl = null;
		boolean reserveAccessCheck = false;
		boolean isUserBinderAdministrator = false;
		boolean isEntryReserved = false;
		boolean isLockedByAndLoginUserSame = false;

		if (getFolderModule().testAccess(entry, "reserveEntry")) {
			reserveAccessCheck = true;
		}
		if (getFolderModule().testAccess(entry, "overrideReserveEntry")) {
			isUserBinderAdministrator = true;
		}
		
		HistoryStamp historyStamp = entry.getReservation();
		if (historyStamp != null) isEntryReserved = true;

		if (isEntryReserved) {
			Principal lockedByUser = historyStamp.getPrincipal();
			if (lockedByUser.getId().equals(user.getId())) {
				isLockedByAndLoginUserSame = true;
			}
		}
		
		if (!replyStyles.isEmpty()) {
			if (getFolderModule().testAccess(entry, "addReply")) {
				if (replyStyles.size() == 1) {
					//There is only one reply style, so show it not as a drop down menu
					String replyStyleId = ((Element)replyStyles.get(0)).attributeValue("value", "");
					
					if (reserveAccessCheck && isEntryReserved && !(isUserBinderAdministrator || isLockedByAndLoginUserSame) ){
						toolbar.addToolbarMenu("1_reply", NLT.get("toolbar.reply"), nullPortletUrl, disabledQual);
					}
					else {
						if (!replyStyleId.equals("")) {
							AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
							adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_FOLDER_REPLY);
							adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folderId);
							adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, replyStyleId);
							adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
	
							Map qualifiers = new HashMap();
							qualifiers.put("popup", new Boolean(true));
							toolbar.addToolbarMenu("1_reply", NLT.get("toolbar.reply"), 
									adapterUrl.toString(), qualifiers);
						}
					}
				} else {
					if (reserveAccessCheck && isEntryReserved && !(isUserBinderAdministrator || isLockedByAndLoginUserSame) ){
						toolbar.addToolbarMenu("1_reply", NLT.get("toolbar.reply"), nullPortletUrl, disabledQual);
					}
					else {
						toolbar.addToolbarMenu("1_reply", NLT.get("toolbar.reply"));
						Map qualifiers = new HashMap();
						qualifiers.put("popup", new Boolean(true));
						for (int i = 0; i < replyStyles.size(); i++) {
							String replyStyleId = ((Element)replyStyles.get(i)).attributeValue("value", "");
							try {
								Definition replyDef = getDefinitionModule().getDefinition(replyStyleId);
								AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
								adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_FOLDER_REPLY);
								adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folderId);
								adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, replyStyleId);
								adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
								toolbar.addToolbarMenuItem("1_reply", "replies", replyDef.getTitle(), 
										adapterUrl.toString(), qualifiers);
							} catch (NoDefinitionByTheIdException e) {
								continue;
							}
						}
					}
				} 
			}
		}
	    
		if (getFolderModule().testAccess(entry, "modifyEntry")) {
			if (reserveAccessCheck && isEntryReserved && !(isUserBinderAdministrator || isLockedByAndLoginUserSame) ) {
				toolbar.addToolbarMenu("2_modify", NLT.get("toolbar.modify"), nullPortletUrl, disabledQual);
				toolbar.addToolbarMenu("4_move", NLT.get("toolbar.move"), nullPortletUrl, disabledQual);
			}
			else {
				//The "Modify" menu
				Map qualifiers = new HashMap();
				qualifiers.put("popup", new Boolean(true));
				AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
				adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_FOLDER_ENTRY);
				adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folderId);
				adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, entryDefId);
				adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
				toolbar.addToolbarMenu("2_modify", NLT.get("toolbar.modify"), adapterUrl.toString(), qualifiers);
				//The "Move" menu
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_FOLDER_ENTRY);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOVE);
				url.setParameter(WebKeys.URL_BINDER_ID, folderId);
				url.setParameter(WebKeys.URL_ENTRY_ID, entryId);
				toolbar.addToolbarMenu("4_move", NLT.get("toolbar.move"), url);
			}
		}

		//Does the user have access to reserve the entry
		if (reserveAccessCheck) {
			//If no one has reserved the entry
			if (!isEntryReserved) {
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_LOCK_FOLDER_ENTRY);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_LOCK);
				url.setParameter(WebKeys.URL_BINDER_ID, folderId);
				url.setParameter(WebKeys.URL_ENTRY_ID, entryId);
				toolbar.addToolbarMenu("3_lock", NLT.get("toolbar.lock"), url);
			}
			else {
			//If some one has reserved the entry	
				//If the person who has locked the entry and the logged in user are the same we allow access to unlock
				if (isLockedByAndLoginUserSame) {
		   			url = response.createActionURL();
					url.setParameter(WebKeys.ACTION, WebKeys.ACTION_LOCK_FOLDER_ENTRY);
					url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_UNLOCK);
					url.setParameter(WebKeys.URL_BINDER_ID, folderId);
					url.setParameter(WebKeys.URL_ENTRY_ID, entryId);
					toolbar.addToolbarMenu("3_lock", NLT.get("toolbar.unlock"), url);
				}
				else {
					//If the person who has logged in is the binder administrator we allow access to unlock
					if (isUserBinderAdministrator) {
						Map qualifiers = new HashMap();
						qualifiers.put("onClick", "return ss_confirmUnlockEntry();");
						url = response.createActionURL();
						url.setParameter(WebKeys.ACTION, WebKeys.ACTION_LOCK_FOLDER_ENTRY);
						url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_UNLOCK);
						url.setParameter(WebKeys.URL_BINDER_ID, folderId);
						url.setParameter(WebKeys.URL_ENTRY_ID, entryId);
						toolbar.addToolbarMenu("3_lock", NLT.get("toolbar.unlock"), url, qualifiers);
					}
					else {
					//If the person logged is not binder administrator
						toolbar.addToolbarMenu("3_lock", NLT.get("toolbar.lock"), nullPortletUrl, disabledQual);
					}
				}
			}
		}
		
		if (getFolderModule().testAccess(entry, "deleteEntry")) {
			//The "Delete" menu
			if (reserveAccessCheck && isEntryReserved && !(isUserBinderAdministrator || isLockedByAndLoginUserSame) ) {
				toolbar.addToolbarMenu("5_delete", NLT.get("toolbar.delete"), nullPortletUrl, disabledQual);
			}
			else {
				Map qualifiers = new HashMap();
				qualifiers.put("onClick", "return ss_confirmDeleteEntry();");
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_FOLDER_ENTRY);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_DELETE);
				url.setParameter(WebKeys.URL_BINDER_ID, folderId);
				url.setParameter(WebKeys.URL_ENTRY_TYPE, entryDefId);
				url.setParameter(WebKeys.URL_ENTRY_ID, entryId); 
				toolbar.addToolbarMenu("5_delete", NLT.get("toolbar.delete"), url, qualifiers);
			}
		}
		Binder binder = getBinderModule().getBinder(Long.valueOf(folderId));
		//Check the access rights of the user
		if (getBinderModule().testAccess(binder, "setProperty")) {
			UserProperties userProperties = getProfileModule().getUserProperties(user.getId(), Long.valueOf(folderId)); 
			String displayDefId = (String) userProperties.getProperty(ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION);
			Definition displayDef = binder.getDefaultViewDef();
			if (displayDefId != null && !displayDefId.equals("")) {
				displayDef = DefinitionHelper.getDefinition(displayDefId);
			}
			Document defDoc = displayDef.getDefinition();
			String viewType = "";
			if (defDoc != null) {
				Element rootElement = defDoc.getRootElement();
				Element elementView = (Element) rootElement.selectSingleNode("//item[@name='forumView' or @name='profileView' or @name='workspaceView' or @name='userWorkspaceView']");
				if (elementView != null) {
					Element viewElement = (Element)elementView.selectSingleNode("./properties/property[@name='type']");
					if (viewElement != null) viewType = viewElement.attributeValue("value", "");
				}
			}
			if (viewType.equals(Definition.VIEW_STYLE_WIKI)) {
				Map qualifiers = new HashMap();
				qualifiers.put("onClick", "if (parent.ss_confirmSetWikiHomepage) {return parent.ss_confirmSetWikiHomepage()} else {return false}");
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_ENTRY);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_WIKI_HOMEPAGE);
				url.setParameter(WebKeys.URL_BINDER_ID, folderId);
				url.setParameter(WebKeys.URL_ENTRY_TYPE, entryDefId);
				url.setParameter(WebKeys.URL_ENTRY_ID, entryId); 
				toolbar.addToolbarMenu("6_setHomepage", NLT.get("toolbar.setWikiHomepage"), url, qualifiers);
			}
		}
	    
		//The "Footer" menu
		Toolbar footerToolbar = new Toolbar();
		Map qualifiers = new HashMap();
		AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folderId);
		adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
		adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, entry.getEntityType().toString());
		if (PortletAdapterUtil.isRunByAdapter((PortletRequest) request)) {
			//If this is being shown in an adaptor, open link in parent
			qualifiers.put("onClick", "self.parent.location.href = this.href;return false;");
			adapterUrl.setParameter(WebKeys.URL_NEW_TAB, "1");
		}
		footerToolbar.addToolbarMenu("permalink", NLT.get("toolbar.menu.entryPermalink"), adapterUrl.toString(), qualifiers);
		qualifiers = new HashMap();
		qualifiers.put("onClick", "ss_showPopupDivCentered('ss_subscription_entry'); return false;");
		footerToolbar.addToolbarMenu("subscribe", NLT.get("toolbar.menu.subscribeToEntry"), "#", qualifiers);

		adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_SEND_ENTRY_EMAIL);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folderId);
		adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
		qualifiers = new HashMap();
		qualifiers.put("popup", Boolean.TRUE);
		footerToolbar.addToolbarMenu("sendMail", NLT.get("toolbar.menu.sendMail"), adapterUrl.toString(), qualifiers);

		adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_START_MEETING);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folderId);
		adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
		adapterUrl.setParameter(WebKeys.USER_IDS_TO_ADD, collectCreatorAndMoficationIds(entry));
		qualifiers = new HashMap();
		qualifiers.put("popup", Boolean.TRUE);
		footerToolbar.addToolbarMenu("startMeeting", NLT.get("toolbar.menu.startMeeting"), adapterUrl.toString(), qualifiers);

		adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_SCHEDULE_MEETING);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folderId);
		adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
		adapterUrl.setParameter(WebKeys.USER_IDS_TO_ADD, collectCreatorAndMoficationIds(entry));
		qualifiers = new HashMap();
		qualifiers.put("popup", Boolean.TRUE);
		footerToolbar.addToolbarMenu("scheduleMeeting", NLT.get("toolbar.menu.scheduleMeeting"), adapterUrl.toString(), qualifiers);
		
		model.put(WebKeys.FOLDER_ENTRY_TOOLBAR,  toolbar.getToolbar());
		model.put(WebKeys.FOOTER_TOOLBAR,  footerToolbar.getToolbar());

		return toolbar;
	}
	
	protected void buildNoEntryBeans(RenderRequest request, 
			RenderResponse response, Folder folder, String entryTitle, Map model) {
		//Build the "add entry" beans
		List defaultEntryDefinitions = folder.getEntryDefinitions();
		Map urls = new HashMap();
		Map titles = new TreeMap();
		model.put(WebKeys.ADD_ENTRY_DEFINITIONS,  defaultEntryDefinitions);
		model.put(WebKeys.ADD_ENTRY_URLS,  urls);
		model.put(WebKeys.ADD_ENTRY_TITLES,  titles);
		model.put(WebKeys.ADD_ENTRY_TITLE,  entryTitle);
		if (getFolderModule().testAccess(folder, "addEntry")) {				
			if (!defaultEntryDefinitions.isEmpty()) {
				if (getFolderModule().testAccess(folder, "addEntry")) {				
					for (int i=0; i<defaultEntryDefinitions.size(); ++i) {
						Definition def = (Definition) defaultEntryDefinitions.get(i);
						AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
						adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_FOLDER_ENTRY);
						adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folder.getId().toString());
						adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, def.getId());
						urls.put(def.getId(), adapterUrl.toString());
						titles.put(NLT.getDef(def.getTitle()), def.getId());
						if (i == 0) {
							adapterUrl.setParameter(WebKeys.URL_NAMESPACE, response.getNamespace());
							adapterUrl.setParameter(WebKeys.URL_ADD_DEFAULT_ENTRY_FROM_INFRAME, "1");
							model.put(WebKeys.URL_ADD_DEFAULT_ENTRY, adapterUrl.toString());
						}
					}
				}
			}
		}
	}

	private String[] collectCreatorAndMoficationIds(FolderEntry entry) {		
		Set principals = new HashSet();
		collectCreatorAndMoficationIdsRecursive(entry, principals);
		String[] as = new String[principals.size()];
		principals.toArray(as);
		return as;
	}
		
	private void collectCreatorAndMoficationIdsRecursive(FolderEntry entry, Set principals) {		
		principals.add(entry.getCreation().getPrincipal().getId().toString());
		principals.add(entry.getModification().getPrincipal().getId().toString());
		Iterator repliesIt = entry.getReplies().iterator();
		while (repliesIt.hasNext()) {
			collectCreatorAndMoficationIdsRecursive((FolderEntry)repliesIt.next(), principals);
		}
	}
	
	protected void getShowEntry(String entryId, Map formData, RenderRequest req, RenderResponse response, 
			Long folderId, Map model)  {
		Folder folder = null;
		FolderEntry entry = null;
		Map folderEntries = null;
		if (!entryId.equals("")) {
			folderEntries  = getFolderModule().getEntryTree(folderId, Long.valueOf(entryId));
			entry = (FolderEntry)folderEntries.get(ObjectKeys.FOLDER_ENTRY);
			folder = entry.getParentFolder();
			model.put(WebKeys.FOLDER_ENTRY_DESCENDANTS, folderEntries.get(ObjectKeys.FOLDER_ENTRY_DESCENDANTS));
			model.put(WebKeys.FOLDER_ENTRY_ANCESTORS, folderEntries.get(ObjectKeys.FOLDER_ENTRY_ANCESTORS));
		} else {
			folder = getFolderModule().getFolder(folderId);
		}
		
		boolean isAppletSupported = SsfsUtil.supportApplets();
		
		String strEntryURL = DefinitionUtils.getWebDAVURL(folder, entry);
		//String strEntryURL = SsfsUtil.getEntryUrl(folder, entry, strRepositoryName);
		//String strWebDavURL = SsfsUtil.getLibraryBinderUrl(folder);
		
		SeenMap seen = getProfileModule().getUserSeenMap(null);
		
		model.put(WebKeys.IS_APPLET_SUPPORTED, isAppletSupported);
		model.put(WebKeys.SEEN_MAP, seen);
		model.put(WebKeys.ENTRY, entry);
		model.put(WebKeys.DEFINITION_ENTRY, entry);
		model.put(WebKeys.FOLDER, folder);
		model.put(WebKeys.BINDER, (Binder) folder);
		model.put(WebKeys.BINDER_WEBDAV_URL, strEntryURL);
		//model.put(WebKeys.BINDER_WEBDAV_URL, strWebDavURL);
		model.put(WebKeys.CONFIG_JSP_STYLE, "view");
		model.put(WebKeys.USER_PROPERTIES, getProfileModule().getUserProperties(null).getProperties());
		model.put(WebKeys.COMMUNITY_TAGS, getFolderModule().getCommunityTags(folderId,Long.valueOf(entryId)));
		model.put(WebKeys.PERSONAL_TAGS, getFolderModule().getPersonalTags(folderId,Long.valueOf(entryId)));
		model.put(WebKeys.SUBSCRIPTION, getFolderModule().getSubscription(folderId,Long.valueOf(entryId)));
		if (entry == null) {
			DefinitionHelper.getDefinition(null, model, "//item[@name='entryView']");
			return;
		}
		if (DefinitionHelper.getDefinition(entry.getEntryDef(), model, "//item[@name='entryView']") == false) {
			DefinitionHelper.getDefaultEntryView(entry, model);
		}
		if (!entryId.equals("")) {
			buildEntryToolbar(req, response, model, folderId.toString(), entryId);
		}
		//only start transaction if necessary
		List replies = new ArrayList((List)model.get(WebKeys.FOLDER_ENTRY_DESCENDANTS));
		if (replies != null)  {
			replies.add(entry);
			for (int i=0; i<replies.size(); i++) {
				FolderEntry reply = (FolderEntry)replies.get(i);
				//if any reply is not seen, add it to list - try to avoid update transaction
				if (!seen.checkIfSeen(reply)) {
					getProfileModule().setSeen(null, replies);
					break;
				}
			}
		} else if (!seen.checkIfSeen(entry)) {
			getProfileModule().setSeen(null, entry);
		}
		Map captionMap = new HashMap();
		Map questionsMap = new HashMap();
		Map transitionMap = new HashMap();
		for (int i=0; i<replies.size(); i++) {
			FolderEntry reply = (FolderEntry)replies.get(i);
			Set states = reply.getWorkflowStates();
			for (Iterator iter=states.iterator(); iter.hasNext();) {
				WorkflowState ws = (WorkflowState)iter.next();
				//store the UI caption for each state
				captionMap.put(ws.getTokenId(), WorkflowUtils.getStateCaption(ws.getDefinition(), ws.getState()));
				try {
					//See if user can transition out of this state
					getFolderModule().checkTransitionOutStateAllowed(reply, ws.getTokenId());
					//get all manual transitions
					Map trans = getFolderModule().getManualTransitions(reply, ws.getTokenId());
					transitionMap.put(ws.getTokenId(), trans);
				} catch (AccessControlException ac) {}
					
				Map qMap = getFolderModule().getWorkflowQuestions(reply, ws.getTokenId());
				questionsMap.put(ws.getTokenId(), qMap);
			}
		}
		model.put(WebKeys.WORKFLOW_CAPTIONS, captionMap);
		model.put(WebKeys.WORKFLOW_QUESTIONS, questionsMap);
		model.put(WebKeys.WORKFLOW_TRANSITIONS, transitionMap);
		
		return;
	}
	
	
}

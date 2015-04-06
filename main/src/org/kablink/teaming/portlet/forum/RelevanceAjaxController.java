/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.portlet.forum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Element;

import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.admin.SendMailErrorWrapper;
import org.kablink.teaming.module.mail.MailSentStatus;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractControllerRetry;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.MiscUtil.IdTriple;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.RelevanceDashboardHelper;
import org.kablink.teaming.web.util.WebHelper;

import org.springframework.web.portlet.ModelAndView;

/**
 * ?
 * 
 * @author Peter Hurley
 */
@SuppressWarnings("unchecked")
public class RelevanceAjaxController  extends SAbstractControllerRetry {
	//caller will retry on OptimisiticLockExceptions
	@Override
	public void handleActionRequestWithRetry(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		if (WebHelper.isUserLoggedIn(request)) {
			String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
			if (op.equals(WebKeys.OPERATION_TRACK_THIS_BINDER)) {
				ajaxSaveTrackThisBinder(this, request, response, "add");
			} else if (op.equals(WebKeys.OPERATION_TRACK_THIS_BINDER_DELETE)) {
				ajaxSaveTrackThisBinder(this, request, response, "delete");
			} else if (op.equals(WebKeys.OPERATION_TRACK_THIS_PERSON_DELETE)) {
				ajaxSaveTrackThisBinder(this, request, response, "deletePerson");
			} else if (op.equals(WebKeys.OPERATION_SHARE_THIS_BINDER)) {
				ajaxSaveShareThisBinder(request, response);
			} else if (op.equals(WebKeys.OPERATION_CLEAR_UNSEEN)) {
				ajaxClearUnseenBinder(request, response);
			}
		}
	}
	
	@Override
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map formData = request.getParameterMap();
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");

		if (!WebHelper.isUserLoggedIn(request)) {
			Map model = new HashMap();
			Map statusMap = new HashMap();
			
			//Signal that the user is not logged in. 
			//  The code on the calling page will output the proper translated message.
			statusMap.put(WebKeys.AJAX_STATUS_NOT_LOGGED_IN, new Boolean(true));
			model.put(WebKeys.AJAX_STATUS, statusMap);

			return new ModelAndView("forum/fetch_url_return", model);
		}
		
		//The user is logged in
		if (op.equals(WebKeys.OPERATION_GET_RELEVANCE_DASHBOARD)) {
			return ajaxGetRelevanceDashboard(request, response);
		} else if (op.equals(WebKeys.OPERATION_GET_RELEVANCE_DASHBOARD_PAGE)) {
			return ajaxGetRelevanceDashboardPage(request, response);
		} else if (op.equals(WebKeys.OPERATION_GET_WHATS_NEW_PAGE)) {
			return ajaxGetWhatsNewPage(request, response);
		} else if (op.equals(WebKeys.OPERATION_CLEAR_UNSEEN)) {
			return ajaxGetWhatsNewPage(request, response);
		} else if (op.equals(WebKeys.OPERATION_SHARE_THIS_BINDER)) {
			if (formData.containsKey("okBtn")) {
				Map model = new HashMap();
				String [] errors = request.getParameterValues(WebKeys.ERROR_LIST);
				if (errors != null) {
					model.put(WebKeys.ERROR_LIST, errors);
					model.put(WebKeys.EMAIL_FAILED_ACCESS, request.getParameterValues(WebKeys.EMAIL_FAILED_ACCESS));
				}
				model.put(WebKeys.ERROR_MESSAGE, PortletRequestUtils.getStringParameter(request, WebKeys.ERROR_MESSAGE, ""));
				return new ModelAndView("binder/sendMailResult", model);
			} else {
				return ajaxShareThisBinder(this, request, response);
			}
		} else if (op.equals(WebKeys.OPERATION_TRACK_THIS_BINDER)) {
			Map model = new HashMap();
			Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);				
			Long entryId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);
			String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, "");
			model.put(WebKeys.NAMESPACE, namespace);
			if (entryId==null) {
				Binder binder = getBinderModule().getBinder(binderId);
				model.put(WebKeys.BINDER, binder);
				return new ModelAndView("forum/relevance_dashboard/track_this_item_return", model);			
			} else {
				FolderEntry entry = getFolderModule().getEntry(binderId, entryId);
				model.put(WebKeys.ENTRY, entry);
				return new ModelAndView("forum/relevance_dashboard/track_this_item_return", model);
			}
		}

		return new ModelAndView("forum/fetch_url_return");
	} 	
	
	private void ajaxSaveTrackThisBinder(AllModulesInjected bs, ActionRequest request, 
			ActionResponse response, String type) throws Exception {
		//The list of tracked binders and shared binders are kept in the user' user workspace user folder properties
		@SuppressWarnings("unused")
		User user = RequestContextHolder.getRequestContext().getUser();
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		BinderHelper.trackThisBinder(bs, binderId, type);
	}
	
	private void ajaxSaveShareThisBinder(ActionRequest request, 
			ActionResponse response) throws Exception {
		//TODO Add more code to store the share request
        User user = RequestContextHolder.getRequestContext().getUser();
		Map formData = request.getParameterMap();
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		Long entryId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);
		Set<Long> ids = new HashSet();
		ids.addAll(LongIdUtil.getIdsAsLongSet(request.getParameterValues("users")));
		ids.addAll(LongIdUtil.getIdsAsLongSet(request.getParameterValues("groups")));
		Set<Long>teams = new HashSet();
		for (Iterator iter=formData.entrySet().iterator(); iter.hasNext();) {
			Map.Entry e = (Map.Entry)iter.next();
			String key = (String)e.getKey();
			if (key.startsWith("cb_")) {
				try {
					teams.add(Long.valueOf(key.substring(3)));
				} catch (Exception ignoreIt) {}
			}
		}
		
		String baseMailTitle = NLT.get(
			"relevance.mailShared",
			new Object[] {
				Utils.getUserTitle(RequestContextHolder.getRequestContext().getUser())
			});
		
		// If we don't have multiple entry IDs to worry about...
		List<IdTriple> meIds = MiscUtil.getIdTriplesFromMultipleEntryIds(
			PortletRequestUtils.getStringParameter(
				request,
				WebKeys.URL_MULTIPLE_ENTITY_IDS,
				""));
		if (null == meIds) {
			meIds = new ArrayList<IdTriple>();
		}
		if (meIds.isEmpty()) {
			// ...simply use the single IDs we were given.
			meIds.add(new IdTriple(binderId, entryId, EntityType.folderEntry.name()));
		}
		
		// Scan the entity IDs...
		List<String> noAccessPrincipals = null;
		List combinedErrors = new ArrayList();
		for (IdTriple meId:  meIds) {
			// ...accessing the next one as a DefinableEntity.
			binderId = meId.m_binderId;
			entryId  = meId.m_entryId;
			String entityType = meId.m_entityType;
			DefinableEntity entity;
			if (entryId == null) {
				entity = getBinderModule().getBinder(binderId);
			} else if (!(entityType.equals(EntityType.folderEntry.name()))) {
				entity = getBinderModule().getBinder(entryId);
			} else {
				entity = getFolderModule().getEntry(binderId, entryId);
			}
			
			getProfileModule().setShares(entity, ids, teams);
			String title;
			String shortTitle;
			if (entity instanceof Principal) {
				title = Utils.getUserTitle((Principal)entity);
			} else {
				title = entity.getTitle();
			}
			shortTitle = title;
			if (entity.getParentBinder() != null) title = entity.getParentBinder().getPathName() + "/" + title;
			String addedComments = PortletRequestUtils.getStringParameter(request, "mailBody", "", false);
			// Do NOT use interactive context when constructing permalink for email. See Bug 536092.
			Description body = new Description("<a href=\"" + PermaLinkUtil.getPermalinkForEmail(entity) +
					"\">" + title + "</a>\n<br/><br/>\n" + addedComments);
			try {
				String mailTitle = (baseMailTitle + " (" + shortTitle +")");
				Set emailAddress = new HashSet();
				//See if this user wants to be BCC'd on all mail sent out
				String bccEmailAddress = user.getBccEmailAddress();
				if (bccEmailAddress != null && !bccEmailAddress.equals("")) {
					if (!emailAddress.contains(bccEmailAddress.trim())) {
						//Add the user's chosen bcc email address
						emailAddress.add(bccEmailAddress.trim());
					}
				}
				Map status = getAdminModule().sendMail(ids, teams, emailAddress, null, null, mailTitle, body);
				Set totalIds = new HashSet();
				totalIds.addAll(ids);
				Set<Principal> totalUsers = getProfileModule().getPrincipals(totalIds);
				if (null == noAccessPrincipals) {
					noAccessPrincipals = new ArrayList<String>();
				}
				for (Principal p : totalUsers) {
					if (p instanceof User) {
						try {
							AccessUtils.readCheck((User)p, (DefinableEntity) entity);
						} catch(AccessControlException e) {
							noAccessPrincipals.add(Utils.getUserTitle(p) + " (" + p.getName() + "):  " + shortTitle);
						}
					}
				}
				
				@SuppressWarnings("unused")
				MailSentStatus result = (MailSentStatus)status.get(ObjectKeys.SENDMAIL_STATUS);
				
				// If there were any errors from this send...
				List<SendMailErrorWrapper> errors = ((List<SendMailErrorWrapper>) status.get(ObjectKeys.SENDMAIL_ERRORS));
				if ((null != errors) && (!(errors.isEmpty()))) {
					// ...copy them into the combined errors list.
					List<String> errorStrings = SendMailErrorWrapper.getErrorMessages(errors);
					for (Object error:  errorStrings) {
						combinedErrors.add(error);
					}
				}
				
			} catch(ConfigurationException e) {
				// Log the configuration error, and break out of the share loop.
				response.setRenderParameter(WebKeys.ERROR_MESSAGE, e.getLocalizedMessage());
				break;
			}
		}

		// Finally, store any errors we got back from any of the shares
		// in the response.
		if (null != noAccessPrincipals) {
			response.setRenderParameter(WebKeys.EMAIL_FAILED_ACCESS, noAccessPrincipals.toArray(new String[noAccessPrincipals.size()]));
		}
		response.setRenderParameter(WebKeys.ERROR_LIST, (String[])combinedErrors.toArray( new String[0]));
	}
	
	private void ajaxClearUnseenBinder(ActionRequest request, 
			ActionResponse response) throws Exception {
		Set<Long> ids = LongIdUtil.getIdsAsLongSet(request.getParameterValues(WebKeys.URL_IDS));
		getProfileModule().setSeenIds(null, ids);
	}
	
	private ModelAndView ajaxGetRelevanceDashboard(RenderRequest request, 
			RenderResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser();
		String type = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TYPE, "");
		String type2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TYPE2, "");
		String type3 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TYPE3, "");
		String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, "");
		Map model = new HashMap();
		model.put(WebKeys.TYPE, type);
		model.put(WebKeys.TYPE2, type2);
		model.put(WebKeys.TYPE3, type3);
		model.put(WebKeys.USER_PRINCIPAL, user);
		model.put(WebKeys.NAMESPACE, namespace);
		model.put(WebKeys.NAMESPACE_RELEVANCE_DASHBOARD, namespace);
		setupDashboardBeans(this, type, request, response, model);
		
		if ( type.equalsIgnoreCase( ObjectKeys.RELEVANCE_DASHBOARD_OVERVIEW ) )
		{
			// Right now when the user clicks on the Overview tab an ajax request is not issued.
			// So we will never get here.  If we ever change and issue an ajax request when
			// the Overview tab is selected we would need to add code here to gather up
			// the necessary beans.
		}
		
		return new ModelAndView("forum/relevance_dashboard/ajax", model);
	}
	
	private ModelAndView ajaxGetRelevanceDashboardPage(RenderRequest request, 
			RenderResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser();
		String type = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2, "");
		String type2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TYPE2, "");
		String page = PortletRequestUtils.getStringParameter(request, WebKeys.URL_PAGE_NUMBER, "0");
		String direction = PortletRequestUtils.getStringParameter(request, WebKeys.URL_DIRECTION, "next");
		String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, "");
		Map model = new HashMap();
		model.put(WebKeys.TYPE, type);
		model.put(WebKeys.TYPE2, type2);
		model.put(WebKeys.PAGE_NUMBER, page);
		model.put(WebKeys.DIRECTION, direction);
		model.put(WebKeys.USER_PRINCIPAL, user);
		model.put(WebKeys.NAMESPACE, namespace);
		model.put(WebKeys.NAMESPACE_RELEVANCE_DASHBOARD, namespace);
		setupDashboardPageBeans(this, type, request, response, model);
		return new ModelAndView("forum/relevance_dashboard/ajax_page", model);
	}
	
	private ModelAndView ajaxGetWhatsNewPage(RenderRequest request, 
			RenderResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser();
        Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
		Binder binder = getBinderModule().getBinder(binderId);
		String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, "");
		String type = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TYPE, "");
		String type2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TYPE2, "");
		String type3 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TYPE3, "");
		String page = PortletRequestUtils.getStringParameter(request, WebKeys.URL_PAGE, "0");
		Map model = new HashMap();
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.TYPE, type);
		model.put(WebKeys.TYPE2, type2);
		model.put(WebKeys.TYPE3, type3);
		model.put(WebKeys.PAGE_NUMBER, page);
		model.put(WebKeys.NAMESPACE, namespace);
		model.put(WebKeys.USER_PRINCIPAL, user);
		if (type.equals(WebKeys.URL_WHATS_NEW)) BinderHelper.setupWhatsNewBinderBeans(this, binder, model, page);
		if (type.equals(WebKeys.URL_UNSEEN)) BinderHelper.setupUnseenBinderBeans(this, binder, model, page);
		return new ModelAndView("forum/whats_new_page_ajax", model);
	}
	
	private ModelAndView ajaxShareThisBinder(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);				
		Long entryId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);
		if (binderId != null) {
			Binder binder = bs.getBinderModule().getBinder(binderId);
			model.put(WebKeys.BINDER, binder);
			model.put(WebKeys.BINDER_ID, binderId.toString());
			Element familyProperty = (Element) binder.getDefaultViewDef().getDefinition().getRootElement().selectSingleNode("//properties/property[@name='family']");
			if (familyProperty != null) {
				String family = familyProperty.attributeValue("value", "");
				model.put(WebKeys.DEFINITION_FAMILY, family);
			}
		}
		if (entryId != null) {
			FolderEntry entry = bs.getFolderModule().getEntry(binderId, entryId);
			model.put(WebKeys.ENTRY, entry);
			model.put(WebKeys.ENTRY_ID, entryId.toString());
		}
		String multipleEntityIds = PortletRequestUtils.getStringParameter(request, WebKeys.URL_MULTIPLE_ENTITY_IDS, "");
		model.put(WebKeys.URL_MULTIPLE_ENTITY_IDS, multipleEntityIds);
		RelevanceDashboardHelper.setupMyTeamsBeans(bs, model);
		return new ModelAndView("forum/relevance_dashboard/share_this_item", model);
	}
	
	private void setupDashboardBeans(AllModulesInjected bs, String type, RenderRequest request, 
			RenderResponse response, Map model) throws Exception {
		String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.NAMESPACE, "");
		String page = PortletRequestUtils.getStringParameter(request, WebKeys.URL_PAGE_NUMBER, "0");
		model.put(WebKeys.NAMESPACE, namespace);
		model.put(WebKeys.PAGE_NUMBER, page);
        Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		if (binderId != null) model.put(WebKeys.BINDER_ID, binderId.toString());
		RelevanceDashboardHelper.setupRelevanceDashboardBeans(bs, request, response, binderId, type, model);
	}
	
	private void setupDashboardPageBeans(AllModulesInjected bs, String type, RenderRequest request, 
			RenderResponse response, Map model) throws Exception {
		String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.NAMESPACE, "");
		String page = PortletRequestUtils.getStringParameter(request, WebKeys.URL_PAGE_NUMBER, "0");
		model.put(WebKeys.NAMESPACE, namespace);
		model.put(WebKeys.PAGE_NUMBER, page);
        Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		if (binderId != null) model.put(WebKeys.BINDER_ID, binderId.toString());
		RelevanceDashboardHelper.setupRelevanceDashboardPageBeans(bs, binderId, type, model);
	}
}

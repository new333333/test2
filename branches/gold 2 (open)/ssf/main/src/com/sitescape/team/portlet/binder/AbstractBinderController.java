/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.portlet.binder;

import javax.portlet.ActionResponse;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;

public abstract class AbstractBinderController extends SAbstractController {
	protected void setupViewBinder(ActionResponse response, Binder binder) {
		setupViewBinder(response, binder.getId(), binder.getEntityType().name());
	}
	protected void setupViewBinder(ActionResponse response, Long binderId, String binderType) {
		if (binderType.equals(EntityIdentifier.EntityType.folder.name())) {
			response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());		
			response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
			response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_RELOAD_LISTING);
		} else if (binderType.equals(EntityIdentifier.EntityType.workspace.name())) {
			response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());		
			response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_WS_LISTING);
			response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_RELOAD_LISTING);
		} else if (binderType.equals(EntityIdentifier.EntityType.profiles.name())) {
			response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());		
			response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
			response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_RELOAD_LISTING);
		} else throw new IllegalArgumentException("Unknown binderType" + binderType);		
	}
	protected void setupViewOnDelete(ActionResponse response, Binder binder, String binderType) {
		if (binderType.equals(EntityIdentifier.EntityType.folder.name())) {
			Folder folder = (Folder)binder;
			Binder parent = folder.getParentFolder();
			if (parent != null) {
				setupViewBinder(response, parent.getId(), parent.getEntityType().name());
			}
		}
		Binder parent = binder.getParentBinder();	
		setupViewBinder(response, parent.getId(), parent.getEntityType().name());
	}
}

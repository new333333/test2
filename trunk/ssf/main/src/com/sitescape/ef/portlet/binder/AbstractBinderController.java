package com.sitescape.ef.portlet.binder;

import javax.portlet.ActionResponse;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.EntityIdentifier;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;

public abstract class AbstractBinderController extends SAbstractController {
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
				setupViewBinder(response, parent.getId(), parent.getEntityIdentifier().getEntityType().name());
			}
		}
		Binder parent = binder.getParentBinder();	
		setupViewBinder(response, parent.getId(), parent.getEntityIdentifier().getEntityType().name());
	}
}

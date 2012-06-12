/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.portlet.binder;

import javax.portlet.ActionResponse;

import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;


public abstract class AbstractBinderController extends SAbstractController {
	protected void setupViewBinder(ActionResponse response, Binder binder) {
		if (binder instanceof TemplateBinder) {
			response.setRenderParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());		
			response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
			response.setRenderParameter(WebKeys.URL_OPERATION, "");
			
		} else setupViewBinder(response, binder.getId(), binder.getEntityType().name());
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

	protected void setupViewTemplateBinder(ActionResponse response, Long binderId, String binderType) {
			response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());		
			response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
	}

	protected void setupReloadBinder(ActionResponse response, Long binderId) {
		//return to view binder
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
		response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());
	}
	protected void setupReloadOpener(ActionResponse response, Long binderId) {
		//return to view binder
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_RELOAD_OPENER);
		response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());
	}
	protected void setupViewBinderInParent(ActionResponse response, Long binderId) {
		//return to view binder parent
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_BINDER_IN_PARENT);
		response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());
	}
	protected void setupCloseWindow(ActionResponse response) {
		//return to view entry
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_CLOSE_WINDOW);
	}
}

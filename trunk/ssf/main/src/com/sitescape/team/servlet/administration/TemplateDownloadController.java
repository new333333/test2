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
package com.sitescape.team.servlet.administration;

import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.util.Validator;

public class TemplateDownloadController extends  ZipDownloadController {
	
	@Override
	protected String getFilename() { return "exportTemplates.zip"; }
	@Override
	protected NamedDocument getDocumentForId(String defId) {
		TemplateBinder binder = getAdminModule().getTemplate(Long.valueOf(defId));
		String name = binder.getName();
		if (Validator.isNull(name)) name = binder.getTemplateTitle();
		return new NamedDocument(name, getAdminModule().getTemplateAsXml(binder));
	}
}

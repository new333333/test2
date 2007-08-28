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

import com.sitescape.team.domain.Definition;
import com.sitescape.util.Validator;

public class ExportDefinitionDownloadController extends  ZipDownloadController {
	
	@Override
	protected String getFilename() { return "definitions.zip"; }
	@Override
	protected NamedDocument getDocumentForId(String defId) {
		Definition def = getDefinitionModule().getDefinition(defId);
		String name = def.getName();
		if (Validator.isNull(name)) name = def.getTitle();
		return new NamedDocument(name, def.getDefinition());
	}
}

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
package org.kablink.teaming.servlet.administration;

import java.util.Iterator;
import java.util.Map;

import javax.activation.FileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.dom4j.Document;
import org.kablink.teaming.util.XmlFileUtil;
import org.kablink.teaming.web.servlet.SAbstractController;
import org.kablink.util.Validator;
import org.springframework.web.servlet.ModelAndView;

public abstract class ZipDownloadController extends  SAbstractController {
	
	protected abstract String getFilename();
	protected abstract NamedDocument getDocumentForId(String defId);
	private FileTypeMap mimeTypes;
	
	protected FileTypeMap getFileTypeMap() {
		return mimeTypes;
	}
	public void setFileTypeMap(FileTypeMap mimeTypes) {
		this.mimeTypes = mimeTypes;
	}
	
	protected ModelAndView handleRequestAfterValidation(HttpServletRequest request,
            HttpServletResponse response) throws Exception {		
		String filename = getFilename();
		response.setContentType(mimeTypes.getContentType(filename));
		response.setHeader("Cache-Control", "private");
		response.setHeader(
					"Content-Disposition",
					"attachment; filename=\"" + filename + "\"");
		ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
		
		// As the fix for bug 505921, changed the parameter passed to setEncoding() from "cp437" to "UTF-8"
		String encoding = "UTF-8";
		zipOut.setEncoding( encoding );
		Map formData = request.getParameterMap();
		Iterator itFormData = formData.entrySet().iterator();
		while (itFormData.hasNext()) {
			Map.Entry me = (Map.Entry) itFormData.next();
			if (((String)me.getKey()).startsWith("id_")) {
				String defId = ((String)me.getKey()).substring(3);
				if (defId.startsWith("%")) defId = defId.substring(1);
				if (Validator.isNotNull(defId)) {
					try {
						NamedDocument doc = getDocumentForId(defId);
						if (doc != null) {
							zipOut.putNextEntry(new ZipEntry(Validator.replacePathCharacters(doc.name) + ".xml"));
							XmlFileUtil.writeFile(doc.doc, zipOut);
						}
					} catch (Exception ex) {
					}
				}
			}
		}
		zipOut.finish();
		
		return null;
	}
	
	protected class NamedDocument {
		public String name;
		public Document doc;
		public NamedDocument(String name, Document doc) {
			this.name = name;
			this.doc = doc;
		}
	}
}

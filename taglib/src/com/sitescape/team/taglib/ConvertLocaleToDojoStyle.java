/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.taglib;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.User;

/**
 * 
 * Escapes JavaScript characters from given string value and outputs to page.
 * 
 */
public class ConvertLocaleToDojoStyle extends BodyTagSupport {

	protected static final Log logger = LogFactory.getLog(ConvertLocaleToDojoStyle.class);

	private Locale locale = null;
	private String cachedBody=null;
	public int doStartTag() throws JspTagException {
		try {
			if (this.locale == null) {
				RequestContext rc = RequestContextHolder.getRequestContext();
				User user = null;
				if (rc != null) user = rc.getUser();
				if (user != null) {
					this.locale = user.getLocale();
				}
			}
			pageContext.getOut().print(convertLocale(this.locale));

			return EVAL_BODY_BUFFERED;
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage(), e);
		} finally {
			this.locale = null;
			this.cachedBody=null;
		}

		return SKIP_BODY;
	}
	public int doAfterBody() {
		cachedBody = getBodyContent().getString();
		return SKIP_BODY;
	}

	public int doEndTag() throws JspTagException {
		try {

		} catch (Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
		} finally {
			release();
		}

		return EVAL_PAGE;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	private static List<String> supportedDojoLanguages = Arrays.asList(new String[]{
			"da", "de", "en", "es", "fi", "fr", "hu", "it", "ja", "ko", "nl", "pl", "pt", "sv", "zh" 
	});
	
	private static Map<String, List<String>> supportedDojoLanguageCoutries = new HashMap<String, List<String>>();
	static {
		supportedDojoLanguageCoutries.put("en", Arrays.asList(new String[]{"GB", "US"}));
		supportedDojoLanguageCoutries.put("pt", Arrays.asList(new String[]{"BR"}));
		supportedDojoLanguageCoutries.put("zh", Arrays.asList(new String[]{"CN", "HK", "TW"}));
	}
	
	private String convertLocale(Locale locale) {
		String language = "en";
		String country = null;
		if (locale != null) {
			if (supportedDojoLanguages.contains(locale.getLanguage())) {
				language = locale.getLanguage().toLowerCase();
			}
			if (supportedDojoLanguageCoutries.get(language) != null &&
					supportedDojoLanguageCoutries.get(language).contains(locale.getCountry())) {
				country = locale.getCountry().toLowerCase();
			}
		}
		
		return language + 
					(country!=null ? "-" + country : "");
	}

}
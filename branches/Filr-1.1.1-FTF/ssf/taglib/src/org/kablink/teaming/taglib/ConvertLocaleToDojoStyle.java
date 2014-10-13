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
package org.kablink.teaming.taglib;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.User;

/**
 * Escapes JavaScript characters from given string value and outputs to
 * page.
 *
 * @author ?
 */
@SuppressWarnings("unused")
public class ConvertLocaleToDojoStyle extends BodyTagSupport {
	protected static final Log logger = LogFactory.getLog(ConvertLocaleToDojoStyle.class);

	private Locale locale = null;
	private String cachedBody=null;
	
	@Override
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
	
	@Override
	public int doAfterBody() {
		cachedBody = getBodyContent().getString();
		return SKIP_BODY;
	}

	@Override
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
			"ar", "cs", "da", "de", "el", "en", "es", "fi", "fr", "he", "hu", "it", "ja", "ko", "nb", "nl", "pl", "pt", "ru", "sv", "tr", "zh" 
	});
	
	private static Map<String, List<String>> supportedDojoLanguageCoutries = new HashMap<String, List<String>>();
	static {
		supportedDojoLanguageCoutries.put("cs", Arrays.asList(new String[]{"CS", "CZ"}));
		supportedDojoLanguageCoutries.put("da", Arrays.asList(new String[]{"DK"}));
		supportedDojoLanguageCoutries.put("de", Arrays.asList(new String[]{"DE"}));
		supportedDojoLanguageCoutries.put("el", Arrays.asList(new String[]{"GR"}));
		supportedDojoLanguageCoutries.put("en", Arrays.asList(new String[]{"AU", "GB", "US"}));
		supportedDojoLanguageCoutries.put("es", Arrays.asList(new String[]{"ES"}));
		supportedDojoLanguageCoutries.put("fi", Arrays.asList(new String[]{"FI"}));
		supportedDojoLanguageCoutries.put("fr", Arrays.asList(new String[]{"FR"}));
		supportedDojoLanguageCoutries.put("he", Arrays.asList(new String[]{"IL"}));
		supportedDojoLanguageCoutries.put("hu", Arrays.asList(new String[]{"HU"}));
		supportedDojoLanguageCoutries.put("it", Arrays.asList(new String[]{"IT"}));
		supportedDojoLanguageCoutries.put("ja", Arrays.asList(new String[]{"JP"}));
		supportedDojoLanguageCoutries.put("ko", Arrays.asList(new String[]{"KR"}));
		supportedDojoLanguageCoutries.put("nb", Arrays.asList(new String[]{"NO"}));
		supportedDojoLanguageCoutries.put("nl", Arrays.asList(new String[]{"NL"}));
		supportedDojoLanguageCoutries.put("pl", Arrays.asList(new String[]{"PL"}));
		supportedDojoLanguageCoutries.put("pt", Arrays.asList(new String[]{"BR"}));
		supportedDojoLanguageCoutries.put("ru", Arrays.asList(new String[]{"RU"}));
		supportedDojoLanguageCoutries.put("sv", Arrays.asList(new String[]{"SE"}));
		supportedDojoLanguageCoutries.put("tr", Arrays.asList(new String[]{"TR"}));
		supportedDojoLanguageCoutries.put("zh", Arrays.asList(new String[]{"CN", "TW"}));
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

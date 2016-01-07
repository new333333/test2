/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

package org.kablink.teaming.domain;

/**
 * @author jong
 *
 */
public class OpenIDProvider extends ZonedObject {

	public OpenIDProvider() {	
	}
	
	public OpenIDProvider(String name, String title, String url, String regex, String emailRegex) {
		this.name = name;
		this.title = title;
		this.url = url;
		this.regex = regex; 
		this.emailRegex = emailRegex;
	}
	
	// Internal database id.
	private String id;
	// Provider name (e.g. google). This is all low case and must be unique within a zone.
	private String name;
	// Provider title used for display (e.g. Google).
	private String title;
	// Provider discovery URL (e.g. https://www.google.com/accounts/o8/id). This is NOT OpenID endpoint.
	private String url;
	// Regex used to validate claimed identity or discovery URL.
	private String regex;
	// Regex used to filter on email address.
	private String emailRegex;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		if(name != null)
			name = name.toLowerCase();
		this.name = name;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getEmailRegex() {
		return emailRegex;
	}

	public void setEmailRegex(String emailRegex) {
		this.emailRegex = emailRegex;
	}

}

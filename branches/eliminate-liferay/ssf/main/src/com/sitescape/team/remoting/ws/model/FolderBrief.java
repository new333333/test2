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
package com.sitescape.team.remoting.ws.model;

import java.io.Serializable;

public class FolderBrief extends BinderBrief implements Serializable {

	private String rssUrl;
	private String icalUrl;
	// Actually, legitimate webdav URL can be constructed for any type of binder. 
	// However, since construction of webdav url is rather expensive (because it 
	// requires retrieval of corresponding binder object from the database), this 
	// property is placed here in FolderBrief rather than in BinderBrief class, 
	// with the observation that most of time binder webdav url is useful only
	// for folders. As a matter of fact, the browser UI exposes webdav URL (via
	// "WebDAV URL" button) for even smaller subset of folders called library
	// folders.
	private String webdavUrl;
	
	public FolderBrief() {}
	
	public FolderBrief(Long id, String title, Timestamp creation, Timestamp modification, String permalink, String webdavUrl, String rssUrl, String icalUrl) {
		super(id, title, creation, modification, permalink);
		this.webdavUrl = webdavUrl;
		this.rssUrl = rssUrl;
		this.icalUrl = icalUrl;
	}

	public String getWebdavUrl() {
		return webdavUrl;
	}

	public void setWebdavUrl(String webdavUrl) {
		this.webdavUrl = webdavUrl;
	}

	public String getIcalUrl() {
		return icalUrl;
	}

	public void setIcalUrl(String icalUrl) {
		this.icalUrl = icalUrl;
	}

	public String getRssUrl() {
		return rssUrl;
	}

	public void setRssUrl(String rssUrl) {
		this.rssUrl = rssUrl;
	}
	
}

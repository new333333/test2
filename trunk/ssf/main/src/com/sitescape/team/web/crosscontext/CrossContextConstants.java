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
package com.sitescape.team.web.crosscontext;

/**
 * IMPORTANT: Do NOT make this class dependent upon any other class in the
 * system. In other word, do NOT import any class other than java or
 * javax classes.
 * 
 * @author jong
 *
 */
public abstract class CrossContextConstants {
	
	// Key names - We need to qualify each name with something unique 
	// (i.e., com.sitescape.crosscontext.portal) so that the name will 
	// not collide with other names already in the request object.
	public static final String OPERATION = "com.sitescape.crosscontext.portal.operation";
	public static final String USER_NAME = "com.sitescape.crosscontext.portal.username";
	public static final String PASSWORD = "com.sitescape.crosscontext.portal.password";
	public static final String ZONE_NAME = "com.sitescape.crosscontext.portal.zonename";
	public static final String USER_INFO="com.sitescape.crosscontext.portal.userinfo";
	public static final String AUTHENTICATOR="com.sitescape.crosscontext.portal.authenticator";
	public static final String VIRTUAL_HOST = "com.sitescape.crosscontext.portal.virtualhost";
	// Operation names
	public static final String OPERATION_AUTHENTICATE = "authenticate";
	public static final String OPERATION_SETUP_SESSION = "setupSession";
	public static final String OPERATION_WRITE_ZONE = "writeZone";
}

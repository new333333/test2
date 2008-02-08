<%
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
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%
	String style = (String) request.getAttribute("style");
	com.sitescape.team.domain.FileAttachment thumbnail = (com.sitescape.team.domain.FileAttachment) request.getAttribute("thumbnail");
	String photo_folder = (String) request.getAttribute("photo_folder");
	String photo_entry = (String) request.getAttribute("photo_entry");
%>
<c:set var="style" value="<%= style %>"/>
<c:set var="thumbnail" value="<%= thumbnail %>"/>
<c:set var="photo_folder" value="<%= photo_folder %>"/>
<c:set var="photo_entry" value="<%= photo_entry %>"/>

<div class="<c:if test="${!empty style}">${style}</c:if><c:if test="${empty style}">ss_thumbnail_small_buddies_list</c:if>"><div>
  	<c:if test="${!empty thumbnail}">
		<img border="0" <ssf:alt tag="alt.thumbnail"/> src="<ssf:url 
		    webPath="viewFile"
		    folderId="${photo_folder}"
		    entryId="${photo_entry}" >
	    	<ssf:param name="entityType" value="${thumbnail.owner.entity.entityType}"/>
		    <ssf:param name="fileId" value="${thumbnail.id}"/>
		    <ssf:param name="fileTime" value="${thumbnail.modification.date.time}"/>
		    <ssf:param name="viewType" value="thumbnail"/>
		    </ssf:url>" onerror="ss_buddyPhotoLoadError(this, '<html:brandedImagesPath/>pics/thumbnail_no_photo.jpg');" />
	</c:if>



	<c:if test="${empty thumbnail}">
		<img border="0" <ssf:alt tag="alt.thumbnail"/> src="<html:brandedImagesPath/>pics/thumbnail_no_photo.jpg">
	</c:if>
</div></div>
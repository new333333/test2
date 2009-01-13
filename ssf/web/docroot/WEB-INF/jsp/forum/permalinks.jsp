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
<div style="padding:10px;">
  <div style="padding-bottom:6px;">
    <span><ssf:nlt tag="permalink.hint"/></span>
    <c:if test="${ssBinder.entityType == 'folder'}">
      <div style="padding-top:10px;">
        <span><ssf:nlt tag="permalink.hint.folder"/></span>
      </div>
    </c:if>
  </div>
<table cellspacing="2" cellpadding="6" border="1" style="background:#cecece;">
  <tr>
    <td valign="top" nowrap>
      <span>
        <c:if test="${empty ssSimpleUrlNames}"><ssf:nlt tag="permalink"/></c:if>
        <c:if test="${!empty ssSimpleUrlNames}"><ssf:nlt tag="permalinks"/></c:if>
      </span>
    </td>
    <td valign="top" nowrap>
      <span>${ssPermalink}</span><br/>
      <c:if test="${!empty ssSimpleUrlNames}">
        <c:forEach var="name" items="${ssSimpleUrlNames}">
          <span>${ssSimpleUrlPrefix}${name.name}</span><br/>
        </c:forEach>
      </c:if>
    </td>
  </tr>

  <c:if test="${!empty ssSimpleUrlNames}">
    <tr>
    <td valign="top" nowrap>
        <span><ssf:nlt tag="permalink.emailAddresses"/></span>
      </td>
      <td valign="top" nowrap>
	    <c:forEach var="name" items="${ssSimpleUrlNames}">
		  <span>${name.emailAddress}@${ssSimpleEmailHostname}</span>
		  <br/>
	    </c:forEach>
      </td>
    </tr>
  </c:if>

  <c:if test="${!empty ss_toolbar_url_subscribe_rss}">
    <tr>
      <td valign="top" nowrap>
       <span><ssf:nlt tag="permalink.rssUrl"/></span>
      </td>
      <td valign="top" nowrap>
        <span>${ss_toolbar_url_subscribe_rss}</span>
      </td>
    </tr>
  </c:if>

  <c:if test="${!empty ss_toolbar_url_webdav}">
    <tr>
      <td valign="top" nowrap>
        <span><ssf:nlt tag="permalink.webdavUrl"/></span>
      </td>
      <td valign="top" nowrap>
        <span>${ss_toolbar_url_webdav}</span>
      </td>
    </tr>
  </c:if>

  <c:if test="${!empty ss_toolbar_url_ical}">
    <tr>
      <td valign="top" nowrap>
        <span><ssf:nlt tag="permalink.icalUrl"/></span>
      </td>
      <td valign="top" nowrap>
        <span>${ss_toolbar_url_ical}</span>
      </td>
    </tr>
  </c:if>
  
</table>

<c:if test="${ssBinder.entityType == 'folder'}">
  <div style="padding-top:10px;">
    <span><ssf:nlt tag="permalink.caution.rss"/></span>
  </div>
</c:if>

</div>

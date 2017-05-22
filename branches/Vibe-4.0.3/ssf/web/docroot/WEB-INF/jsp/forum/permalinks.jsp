<%
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
%>
<%@ page import="org.kablink.teaming.util.Utils" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<div style="padding:10px;">
<table cellspacing="2" cellpadding="6" border="1" style="width:100%; background:#cecece;">
  <tr>
    <td valign="middle" nowrap width="10%">
      <span>
        <c:if test="${empty ssSimpleUrlNames}"><ssf:nlt tag="permalink"/></c:if>
        <c:if test="${!empty ssSimpleUrlNames}"><ssf:nlt tag="permalinks"/></c:if>
      </span>
    </td>
    <td valign="top" nowrap width="90%">
      <div style="padding-right:4px;">
        <input type="text" value="${ssPermalink}" 
          style="width:100%; background-color:#cecece; overflow:hidden;" onClick="this.select();"/>
      </div>
      <c:if test="${!empty ssSimpleUrlNames}">
        <c:forEach var="name" items="${ssSimpleUrlNames}">
          <div style="padding-right:4px;">
            <input type="text" value="${ssSimpleUrlPrefix}${name.name}" 
              style="width:100%; background-color:#cecece; overflow:hidden;" onClick="this.select();"/>
          </div>
        </c:forEach>
      </c:if>
    </td>
  </tr>

  <c:if test="${!empty ssBinder && ssBinder.entityType == 'folder'}">
   <c:if test="${!empty ssSimpleUrlNames && ssBinder.postingEnabled}">
    <tr>
    <td valign="top" nowrap>
        <span><ssf:nlt tag="permalink.emailAddresses"/></span>
      </td>
      <td valign="top" nowrap>
	    <c:forEach var="name" items="${ssSimpleUrlNames}">
		  <div><ssf:mailto email="${name.emailAddress}@${ssSimpleEmailHostname}" noLink="true"/></div>
	    </c:forEach>
      </td>
    </tr>
   </c:if>
  </c:if>

  <% if (!(Utils.checkIfFilr())) { %>
	  <c:if test="${!empty ss_toolbar_url_subscribe_rss}">
	    <tr>
	      <td valign="top" nowrap>
	       <span><ssf:nlt tag="permalink.rssUrl"/></span>
	      </td>
	      <td valign="top" nowrap>
	        <a href="${ss_toolbar_url_subscribe_rss}" 
				onclick="ss_openUrlInWindow(this, 'teamingSubscribe', null, null);return false;"><span>${ss_toolbar_url_subscribe_rss}</span></a>
	      </td>
	    </tr>
	  </c:if>
	
	  <c:if test="${!empty ss_toolbar_url_subscribe_atom}">
	    <tr>
	      <td valign="top" nowrap>
	       <span><ssf:nlt tag="permalink.atomUrl"/></span>
	      </td>
	      <td valign="top" nowrap>
	        <a href="${ss_toolbar_url_subscribe_atom}" 
				onclick="ss_openUrlInWindow(this, 'teamingSubscribe', null, null);return false;"><span>${ss_toolbar_url_subscribe_atom}</span></a>
	      </td>
	    </tr>
	  </c:if>
  <% } %>

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
      <td valign="middle" nowrap>
        <span><ssf:nlt tag="permalink.icalUrl"/></span>
      </td>
      <td valign="top" nowrap>
        <a href="${ss_toolbar_url_ical}" 
			onclick="ss_openUrlInWindow(this, 'teamingSubscribe', null, null);return false;">
		  <span>${ss_toolbar_url_ical}</span>
		</a>
      </td>
    </tr>
  </c:if>
  
</table>

<br/>
<br/>
<table cellspacing="6" cellpadding="6">
<tr>
<th colspan="2">
    <span>
		<% if (Utils.checkIfFilr()) { %>
    		<ssf:nlt tag="permalink.hint.title.filr"/>
    	<% } %>
		<% if (!(Utils.checkIfFilr())) { %>
    		<ssf:nlt tag="permalink.hint.title.vibe"/>
    	<% } %>
    </span>
</th>
</tr>

<tr>
  <td valign="top">
    <span><ssf:nlt tag="permalinks"/></span>
  </td>
  <td valign="top">
    <span><ssf:nlt tag="permalink.hint.permalink"/></span>
  </td>
</tr>

<tr>
  <td valign="top">
    <span><ssf:nlt tag="permalink.emailAddresses"/></span>
  </td>
  <td valign="top">
    <span><ssf:nlt tag="permalink.hint.email"/></span>
  </td>
</tr>

<tr>
  <td valign="top">
    <span><ssf:nlt tag="permalink.webdavUrl"/></span>
  </td>
  <td valign="top">
    <span>
		<% if (Utils.checkIfFilr()) { %>
    		<ssf:nlt tag="permalink.hint.webdav.filr"/>
    	<% } %>
		<% if (!(Utils.checkIfFilr())) { %>
    		<ssf:nlt tag="permalink.hint.webdav.vibe"/>
    	<% } %>
    </span>
  </td>
</tr>

<% if (!(Utils.checkIfFilr())) { %>
	<tr>
	  <td valign="top">
	    <span><ssf:nlt tag="permalink.icalUrl"/></span>
	  </td>
	  <td valign="top">
	    <span><ssf:nlt tag="permalink.hint.ical"/></span>
	  </td>
	</tr>
	
	<tr>
	  <td valign="top">
	    <span><ssf:nlt tag="permalink.rssUrl"/></span>
	  </td>
	  <td valign="top">
	    <span><ssf:nlt tag="permalink.hint.rss"/></span>
	  </td>
	</tr>
<% } %>

</table>

<% if (!(Utils.checkIfFilr())) { %>
	<c:if test="${ssBinder.entityType == 'folder'}">
	  <div style="padding:10px 10px 0px 6px;">
	    <span><ssf:nlt tag="permalink.caution.rss"/></span>
	  </div>
	</c:if>
<% } %>

</div>

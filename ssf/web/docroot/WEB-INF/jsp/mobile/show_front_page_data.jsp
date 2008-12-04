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
<%@ page import="org.kablink.teaming.ObjectKeys" %>

<div id="pagebody">
	<c:set var="guestInternalId" value="<%= ObjectKeys.GUEST_USER_INTERNALID %>"/>
	<c:if test="${ssUser.internalId == guestInternalId}">
	  <c:if test='<%= !org.kablink.teaming.util.SPropsUtil.getBoolean("form.login.auth.disallowed",false) %>' >
	    <div>
	    <a href="<ssf:url action="__ajax_mobile" actionUrl="false" 
						operation="mobile_login" />"
	    >
	    <span><ssf:nlt tag="login"/></span>
	    </a>
	    </div>
	  </c:if>
	</c:if>

	<c:if test="${ssUser.internalId != guestInternalId}">
	<div>
	  <span>
	    <ssf:nlt tag="mobile.welcome">
	      <ssf:param name="value" useBody="true">
	        <a href="<ssf:url adapter="true" portletName="ss_forum" 
				    action="__ajax_mobile"
				    operation="mobile_show_workspace"
				    binderId="${ssUser.workspaceId}" />">${ssUser.title}</a>
	      </ssf:param>
	    </ssf:nlt>
	  </span>
	</div>
	<br/>
	</c:if>
	
	<div id="menu">
	  <ul>
	    <li>
		  <a href="<ssf:url adapter="true" portletName="ss_forum" 
						action="__ajax_mobile" actionUrl="false" 
						operation="mobile_whats_new" ><ssf:param
						name="type" value="whatsNewTracked"/></ssf:url>"
		  ><ssf:nlt tag="mobile.whatsNewTracked"/></a>
		</li>
		<li>
		  <a href="<ssf:url adapter="true" portletName="ss_forum" 
						action="__ajax_mobile" actionUrl="false" 
						operation="mobile_whats_new" ><ssf:param
						name="type" value="whatsNew"/></ssf:url>"
		  ><ssf:nlt tag="mobile.whatsNewSite"/></a>
		</li>
	  </ul>
	</div>

	<%@ include file="/WEB-INF/jsp/mobile/miniblog.jsp" %>
		
	<%@ include file="/WEB-INF/jsp/mobile/favorites.jsp" %>
	
	<%@ include file="/WEB-INF/jsp/mobile/saved_searches.jsp" %>
		
	<div class="pagebody">
		<div id="search_people_global">
		  <span class="ss_bold"><ssf:nlt tag="navigation.find"/></span>
		</div>
		<div class="pagebody_border">
		  <div class="maincontent">
	        <c:if test="${ss_accessControlMap['ss_canViewUserProfiles'] == true}">
		      <form method="post" action="<ssf:url adapter="true" portletName="ss_forum" 
							action="__ajax_mobile" actionUrl="true" 
							operation="mobile_find_people" />">
		    
	    	    <label for="searchText"><ssf:nlt tag="navigation.findUser"/></label>
	    	    <br/>
			    <input type="text" size="15" name="searchText" />&nbsp;<input 
		  	      type="submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>" />
		      </form><br/>
	        </c:if>
	
		    <form method="post"
			  action="<ssf:url adapter="true" portletName="ss_forum" 
							action="__ajax_mobile" actionUrl="true" 
							operation="mobile_show_search_results" />">
	    	  <label for="searchText"><ssf:nlt tag="searchForm.button.label"/></label>
	    	  <br/>
		      <input name="searchText" type="text" size="15"/>&nbsp;<input type="submit" 
		        name="searchBtn" value="<ssf:nlt tag="button.ok"/>" />
		      <input type="hidden" name="quickSearch" value="true"/>
		    </form>
		  </div>
	    </div>
	</div>

	<%@ include file="/WEB-INF/jsp/mobile/footer.jsp" %>
</div>

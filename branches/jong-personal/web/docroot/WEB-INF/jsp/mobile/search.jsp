<%
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
%>
<%@ page import="org.kablink.teaming.ObjectKeys" %>

<script type="text/javascript">
function ss_setMobileSearchActionUrl(formObj) {
	var type = formObj.search_type.options[formObj.search_type.selectedIndex].value;
	formObj.action = '<ssf:url adapter="true" portletName="ss_forum" 
		action="__ajax_mobile" actionUrl="true" 
		binderId="${ssBinder.id}"
		operation="mobile_show_search_results" />';

	if (type == 'all') formObj.action = '<ssf:url adapter="true" portletName="ss_forum" 
		action="__ajax_mobile" actionUrl="true" 
		binderId="${ssBinder.id}"
		operation="mobile_show_search_results" />';
	if (type == 'people') formObj.action = '<ssf:url adapter="true" portletName="ss_forum" 
		action="__ajax_mobile" actionUrl="true" 
		binderId="${ssBinder.id}"
		operation="mobile_find_people" />';
	if (type == 'places') formObj.action = '<ssf:url adapter="true" portletName="ss_forum" 
		action="__ajax_mobile" actionUrl="true" 
		binderId="${ssBinder.id}"
		operation="mobile_find_places" />';
}
</script>
<div id="search-dialog" class="action-dialog" style="display: none;z-index:2">
	<div class="close-menu">
        <input id="search-cancel" type="image" src="<html:rootPath/>images/icons/close_menu.png" 
          name="cancelBtn" onClick="ss_hideMenu('search-dialog');return false;"/>
	</div>

    <div class="dialog-head">
      <span><ssf:nlt tag="navigation.search" /></span>
    </div>
	<form id="searchForm" 
		method="post" 
		action="<ssf:url adapter="true" portletName="ss_forum" 
			action="__ajax_mobile" actionUrl="true" 
			binderId="${ssBinder.id}"
			operation="mobile_show_search_results" />"
		onSubmit="ss_setMobileSearchActionUrl(this);return true;"
	>
	  <div class="dialog-content">
		  <div class="pad-left20 marginbottom2">
		    <select size="1" name="search_type" >
		    
		      <option value="all" <c:if test="${ss_searchType == 'all'}">selected</c:if>
		      ><ssf:nlt tag="mobile.searchAll"/></option>
		      
		      <option value="" >----------</option>
		      
		      <option value="people" <c:if test="${ss_searchType == 'people'}">selected</c:if>
		      ><ssf:nlt tag="mobile.searchPeople"/></option>
		      
		      <option value="places" <c:if test="${ss_searchType == 'places'}">selected</c:if>
		      ><ssf:nlt tag="mobile.searchPlaces"/></option>
		      
		    </select>
		  </div>
		  <div>
		    <div class="margintop2"><input type="radio" name="scope" value="site" checked="checked"/> <ssf:nlt tag="mobile.searchTheSite"/></div>
		    
		    <c:if test="${ssBinder.entityType == 'folder'}">
		      <div class="margintop2"><input type="radio" name="scope" value="local" /> <ssf:nlt tag="mobile.searchThisFolder"/></div>
		    </c:if>
		    <c:if test="${ssBinder.entityType != 'folder'}">
		      <div class="margintop2"><input type="radio" name="scope" value="local" /> <ssf:nlt tag="mobile.searchThisWorkspace"/></div>
		    </c:if>
		  </div>
	  </div>
	  <div>
		<input class="mediumtext" style="width: 90%;" type="text" size="33" name="searchText"/>
	  </div>			
      <div id="search-buttons" class="margintop2">
        <input id="search-ok" type="submit" value="<ssf:nlt tag="button.search"/>" 
          name="quickSearch" onClick="ss_hideMenu('search-dialog');return true;" />
       </div>
    </form>
    
	<%@ include file="/WEB-INF/jsp/mobile/saved_searches.jsp" %>

  </div>



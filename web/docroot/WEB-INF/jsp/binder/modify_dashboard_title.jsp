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

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_js.jsp" %>

<body class="ss_style_body tundra">
<div class="ss_style ss_portlet">
<ssf:form titleTag="dashboard.setTitle">
<div class="ss_form" style="margin:6px;">
<div style="margin:6px;">

<div class="ss_form">
<form method="post" action="<ssf:url action="modify_dashboard" actionUrl="true"><ssf:param 
	name="binderId" value="${ssBinder.id}"/></ssf:url>">
<div class="ss_form ss_buttonBarRight">
<input type="submit" class="ss_submit" name="closeBtn" 
  value="<ssf:nlt tag="button.close" text="Close"/>">
<input type="hidden" name="_returnView" value="binder"/>
</div>
</form>
</div>

<div style="width:100%;">
<span class="ss_bold"><ssf:nlt tag="<ssf:form" /></span>
<br/>
<div class="ss_indent_medium">
  <table>
    <tr>
      <td valign="top"><ssf:nlt tag="dashboard.currentTitle.local" />:&nbsp;</td>
      <td valign="top"><c:out value="${ssDashboard.dashboard_local.title}"/>
		<c:if test="${ssDashboard.dashboard_local.includeBinderTitle}">
		  <br>
		  <span class="ss_smallprint ss_italic"><ssf:nlt tag="dashboard.binderTitleIncluded"/></span>
		</c:if>
      </td>
    </tr>
    <tr>
      <td valign="top"><ssf:nlt tag="dashboard.currentTitle.global" />:&nbsp;</td>
      <td valign="top"><c:out value="${ssDashboard.dashboard_global.title}"/>
		<c:if test="${ssDashboard.dashboard_global.includeBinderTitle}">
		  <br>
		  <span class="ss_smallprint ss_italic"><ssf:nlt tag="dashboard.binderTitleIncluded"/></span>
		</c:if>
      </td>
    </tr>
    <tr>
      <td valign="top"><ssf:nlt tag="dashboard.currentTitle.binder" />:&nbsp;</td>
      <td valign="top"><c:out value="${ssDashboard.dashboard_binder.title}"/>
		<c:if test="${ssDashboard.dashboard_binder.includeBinderTitle}">
		  <br>
		  <span class="ss_smallprint ss_italic"><ssf:nlt tag="dashboard.binderTitleIncluded"/></span>
		</c:if>
      </td>
    </tr>
  </table>
</div>
<br/>

<span class="ss_bold"><ssf:nlt tag="dashboard.newTitle" /></span>
<br/>
<div class="ss_indent_medium">
	  <form method="post" action="<ssf:url action="modify_dashboard" actionUrl="true"><ssf:param 
	  	name="binderId" value="${ssBinder.id}"/></ssf:url>">
		<input type="text" name="title" size="80"/><br/>
		<input type="checkbox" name="includeBinderTitle" id="includeBinderTitle"/>
		<label for="includeBinderTitle"><span><ssf:nlt tag="dashboard.includeBinderTitle"/></span></label>
		<br/>
		<br/>
	
	    <span class="ss_bold"><ssf:nlt tag="dashboard.titleScope"/></span><br>
	    <input type="radio" name="_scope" value="local" id="local" checked="checked" />
	    	<label for="local"><ssf:nlt tag="dashboard.titleScope.local"/><br></label>
	    <input type="radio" name="_scope" value="global" id="global"/>
	    	<label for="global"><ssf:nlt tag="dashboard.titleScope.global"/><br></label>
	    <c:if test="${ssDashboard.sharedModificationAllowed}">
	      <input type="radio" name="_scope" value="binder" id="binder"/>
	      	<label for="binder"><ssf:nlt tag="dashboard.titleScope.binder"/><br></label>
	    </c:if>
		<br/>
		<br/>
		
		<input type="submit" class="ss_submit" name="set_title" 
		  value="<ssf:nlt tag="button.ok"/>"> 
		&nbsp;&nbsp;
		<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
		<input type="hidden" name="_returnView" value="binder"/>
	  </form>
</div>
</div>

</div>
</div>
</ssf:form>
</div>

</body>
</html>

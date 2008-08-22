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
<%@ page import="java.util.ArrayList" %>

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">
<div class="ss_pseudoPortal">
<div class="ss_style ss_portlet">

<c:set var="formName" value="${renderResponse.namespace}fm"/>
<script type="text/javascript">
var ssReportURL="<ssf:url action="login_report" actionUrl="true"><ssf:param 
	name="binderId" value="${ssBinder.id}"/><ssf:param 
	name="binderType" value="${ssBinder.entityType}"/></ssf:url>";
</script>

<table class="ss_style" width="100%"><tr><td>
<form class="ss_style ss_form" 
	action="<ssf:url webPath="reportDownload"/>" 
	method="post" 
	name="${formName}">
<input type="hidden" name="ss_reportType" value="login"/>
<div class="ss_buttonBarRight">
    <input type="submit" class="ss_submit" name="forumOkBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
		  onClick="self.window.close();return false;"/>
</div>
   <br/>
   <br/>
   <span class="ss_bold"><ssf:nlt tag="administration.report.login2"/></span>
   <br/>
   <br/>
   <ssf:nlt tag="administration.report.dates"/>
   <div class="ss_toolbar_color" style="display:inline;"><ssf:datepicker formName="${formName}" showSelectors="true" 
				 popupDivId="ss_startPopup" id="ss_startDate" initDate="${startDate}"
				 immediateMode="false" 
				 altText="<%= com.sitescape.team.util.NLT.get("calendar.view.popupAltText") %>"
				 />
   </div>
   <div id="ss_startPopup" class="ss_calPopupDiv"></div>
   <ssf:nlt tag="smallWords.and"/>
   <div class="ss_toolbar_color" style="display:inline;"><ssf:datepicker formName="${formName}" showSelectors="true" 
				 popupDivId="ss_endPopup" id="ss_endDate" initDate="${endDate}"
				 immediateMode="false" 
				 altText="<%= com.sitescape.team.util.NLT.get("calendar.view.popupAltText") %>"
				 />
   </div>
   <br/>
   <div id="ss_endPopup" class="ss_calPopupDiv"></div>
   
   <br/>
   
   <div>	
  
	</br>
	<br>
	<!-- Find people -->
		<div class="ss_global_toolbar_findUser"  id="ss_navbarFindUserButton${renderResponse.namespace}"
	     onMouseOver="this.style.cursor = 'pointer';">
		<ssf:find formName="${formName}" formElement="users" type="user"/>
		</div>
  
   </div>
  
   <br>  
   
   <div>
   
   <input type="radio" name="ss_reportOptionType" value="shortReport" checked/> <ssf:nlt tag="administration.report.option.short"/>
   
   </div>
   
   <br/>
   
   <div style="margin-left:35px;">
   
   <ssf:nlt tag="administration.report.sort"/>
   
   <select name="ss_reportSortType">
   <option value = "none_sort"><span class="ss_labelRight"><ssf:nlt tag="common.select.none"/></option>
   <option value = "user_sort"><span class="ss_labelRight"><ssf:nlt tag="report.columns.user"/></option>
   <option value = "last_login_sort"><span class="ss_labelRight"><ssf:nlt tag="report.columns.last_login"/></option>
   <option value = "login_count_sort"><span class="ss_labelRight"><ssf:nlt tag="report.columns.login_count"/></option>   
   </select>
   
   </div>

   <br/>

   <div>

   <input type="radio" name="ss_reportOptionType" value="longReport"/> <ssf:nlt tag="administration.report.option.long"/>
   <br/>
   
   </div>

   <br/>

   <div style="margin-left:35px;">
   
   <ssf:nlt tag="administration.report.sort"/>
   
   <select name="ss_reportSortType_2">
   <option value = "login_date_sort"><span class="ss_labelRight"><ssf:nlt tag="report.columns.login_date"/></option>   
   <option value = "user_sort"><span class="ss_labelRight"><ssf:nlt tag="report.columns.user"/></option>
   </select>
   
   </div>

   <br/>
   
   <br/>
      
   <div class="ss_buttonBarLeft">
    <input type="submit" class="ss_submit" name="forumOkBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
		  onClick="self.window.close();return false;"/>
   </div>
</form>
<br>
</td></tr></table>
</div>
</div>
</body>
</html>

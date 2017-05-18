<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>

<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.report.title.login") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">

<script type="text/javascript">
	/**
	 * 
	 */
	function handleCloseBtn()
	{
	<% 	if ( GwtUIHelper.isGwtUIActive( request ) ) { %>
			// Tell the Teaming GWT ui to close the administration content panel.
			if ( window.parent.ss_closeAdministrationContentPanel ) {
				window.parent.ss_closeAdministrationContentPanel();
			} else {
				ss_cancelButtonCloseWindow();
			}

			return false;
	<% 	}
		else { %>
			ss_cancelButtonCloseWindow();
			return false;
	<%	} %>
	
	}// end handleCloseBtn()
</script>

<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>

<div class="ss_pseudoPortal">
	<div class="ss_style ss_portlet">
	
	<ssf:form titleTag="administration.report.title.login">

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
			 <input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
				  onClick="return handleCloseBtn();"/>
		</div>
		<div class="ss_largeprint ss_bold marginbottom3"><ssf:nlt tag="administration.report.login2"/></div>
	  <div class="ss_largeprint ss_bold marginbottom1"><ssf:nlt tag="administration.report.dates"/></div>
		<div class="roundcornerSM" style="border: 1px solid #cccccc; padding: 5px; background-color: #ededed;">
		
		   <div class="n_date_picker" style="display:inline; vertical-align: middle; padding-right: 10px;">
		   		<ssf:datepicker formName="${formName}" showSelectors="true" 
				popupDivId="ss_startPopup" id="ss_startDate" initDate="${startDate}"
				immediateMode="false" 
				altText='<%= org.kablink.teaming.util.NLT.get("calendar.view.popupAltText") %>'
				/>
		   </div>
		   <div id="ss_startPopup" class="ss_calPopupDiv"></div>
		   <ssf:nlt tag="smallWords.and"/>
		   <div class="n_date_picker" style="display:inline; vertical-align: middle;">
		   		<ssf:datepicker formName="${formName}" showSelectors="true" 
				popupDivId="ss_endPopup" id="ss_endDate" initDate="${endDate}"
				immediateMode="false" 
				altText='<%= org.kablink.teaming.util.NLT.get("calendar.view.popupAltText") %>'
				/>
		   </div>
		   <br/>
		   <div id="ss_endPopup" class="ss_calPopupDiv"></div>

			<div class="marginleft1">
			   <div class="margintop3">		  
				<!-- Find people -->
					<div class="ss_global_toolbar_findUser"  id="ss_navbarFindUserButton${renderResponse.namespace}"
					 onMouseOver="this.style.cursor = 'pointer';">
					<ssf:find formName="${formName}" formElement="users" type="user" width="200px"/>
					</div>
			   </div>
			  
			   <div class="margintop3">
				   <input type="radio" name="ss_reportOptionType" value="shortReport" id="shortReport" checked/> 
				   <label for="shortReport"><ssf:nlt tag="administration.report.option.short"/></label>		   
			   </div>
	
			   <div style="margin-left:35px;">
				   <label for="ss_reportSortType"><ssf:nlt tag="administration.report.sort"/></label>
				   
				   <select name="ss_reportSortType" id="ss_reportSortType">
				   <option value = "none_sort"><span class="ss_labelRight"><ssf:nlt tag="common.select.none"/></span></option>
				   <option value = "user_sort"><span class="ss_labelRight"><ssf:nlt tag="report.columns.user"/></span></option>
				   <option value = "last_login_sort"><span class="ss_labelRight"><ssf:nlt tag="report.columns.last_login"/></span></option>
				   <option value = "login_count_sort"><span class="ss_labelRight"><ssf:nlt tag="report.columns.login_count"/></span></option>   
				   </select>		   
			   </div>
			   
			   <div class="margintop3">
				   <input type="radio" name="ss_reportOptionType" value="longReport" id="longReport"/> 
				   <label for="longReport"><ssf:nlt tag="administration.report.option.long"/></label>
			   </div>
	
			   <div style="margin-left:35px;">
				   <label for="ss_reportSortType_2"><ssf:nlt tag="administration.report.sort"/></label>
				   
				   <select name="ss_reportSortType_2" id="ss_reportSortType_2">
				   <option value = "login_date_sort"><span class="ss_labelRight"><ssf:nlt tag="report.columns.login_date"/></span></option>   
				   <option value = "user_sort"><span class="ss_labelRight"><ssf:nlt tag="report.columns.user"/></span></option>
				   </select>
			   </div>
			</div>
			
			<div class="margintop3 marginbottom1" style="margin-left: 5px;">
		   		<input type="submit" class="ss_submit" name="forumOkBtn" value="<ssf:nlt tag="administration.create.report" />">
			</div>
		</div>
			  
	   <div class="ss_buttonBarRight" style="margin-top: 10px;">
		<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" />"
			  onClick="return handleCloseBtn();"/>
	   </div>
			<sec:csrfInput />
		</form>
	</td></tr></table>
</ssf:form>
</div>
</div>
</body>
</html>

<%
/**
 * Copyright (c) 2005 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>


<form name="<portlet:namespace/>fm" method="post" action="<portlet:actionURL>
			<portlet:param name="action" value="configure_posting_job"/>
		</portlet:actionURL>">
<script language="javascript" type="text/javascript">
function <portlet:namespace/>setEnable() {
	if (document.<portlet:namespace/>fm.disabled.checked) {
		document.<portlet:namespace/>fm.enabled.value = "false";
	} else {
		document.<portlet:namespace/>fm.enabled.value = "true";
	}
}
function <portlet:namespace/>toggleDiv(id) {
  var div = document.getElementById(id);
  if (div.style.visibility == 'visible') {
  	  div.style.visibility = "hidden";
 	  
  } else {
	  div.style.visibility="visible";
	 }

}
</script>
<input type="hidden" id="enabled" name="enabled" value="${ssPostingConfig.enabled}"/>
<div class="ss_portlet">
<span class="ss_titlebold">Set the incoming e-mail schedule</span><br/>
<br/>
<c:set var="toolbar" value="${ssToolbar}" scope="request" />
<%@ include file="/WEB-INF/jsp/definition_elements/toolbar_view.jsp" %>

<table border ="0" cellspacing="0" cellpadding="3">
<tr><td class="ss_content"> 
<input type="checkbox" class="ss_content" id="disabled" name="disabled" onClick="<portlet:namespace/>setEnable();" <c:if test="${!ssPostingConfig.enabled}">checked</c:if>/>
Disable all incoming e-mail<br/>
</td></tr></table>

<div class="ss_divider"></div>
<span class="ss_contentbold">Specify when e-mail is fetched from the mail server</span>

<c:set var="schedule" value="${ssPostingConfig.schedule}"/>
<%@ include file="/WEB-INF/jsp/administration/schedule.jsp" %>

<div class="ss_divider"></div>

<br/>
<input class="ss_submit" type="submit" name="okBtn" value="Ok">
<input class="ss_submit" type="submit" name="cancelBtn" value="Cancel">
</div>
</form>


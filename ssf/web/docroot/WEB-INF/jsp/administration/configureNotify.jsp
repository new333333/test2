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
<div class="ss_style ss_portlet">
<span class="ss_titlebold"><ssf:nlt tag="notify.title"/></span><br/>
<br/>
<c:choose>
<c:when test="${!empty ssWsDomTree}">
<jsp:useBean id="ssWsDomTree" type="org.dom4j.Document" scope="request" />
	<table class="ss_style" border="0" cellpadding="0" cellspacing="0" width="95%">
	<tr align="left"><td><ssf:nlt tag="notify.choose"/></td></tr>
	<tr>
		<td>
			<div>
				<ssf:tree treeName="ssWsDomTree" treeDocument="<%= ssWsDomTree %>" rootOpen="true" />
			</div>
		</td>
	</tr>
	</table>
	<br/>
</c:when>
<c:otherwise>

<form class="ss_style ss_form" name="${renderResponse.namespace}fm" method="post" 
  onSubmit="return ss_onSubmit(this);"

    action="<portlet:actionURL>
			<portlet:param name="action" value="configure_notify"/>
			<portlet:param name="binderId" value="${ssFolder.id}"/>
		</portlet:actionURL>">

<span class="ss_bold"><ssf:nlt tag="notify.forum.label"/> ${ssFolder.title}</span>
<div class="ss_buttonBarRight">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</div>


<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="notify.schedule" /></legend>
<table class="ss_style" border ="0" cellspacing="0" cellpadding="3" width="100%">
<tr><td> 
<input type="checkbox" id="disabled" name="disabled" <c:if test="${!ssScheduleInfo.enabled}">checked</c:if>/>
<ssf:nlt tag="notify.disable"/>
</td>
</tr>
</table><br/>
<c:set var="schedule" value="${ssScheduleInfo.schedule}"/>
<%@ include file="/WEB-INF/jsp/administration/schedule.jsp" %>
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
</fieldset>

<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="notify.addresses" /></legend>
<br />
<span class="ss_bold"><ssf:nlt tag="notify.addresses.instructions"/>
</span><br />
<textarea name="emailAddress" rows="4" cols="50" >
<c:forEach var="addr" items="${ssNotification.emailAddress}">
<c:out value="${addr}"/>
</c:forEach>
</textarea>
<br/>
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
</fieldset>


<br/>
	<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>">
</form>

</c:otherwise>
</c:choose>
</div>

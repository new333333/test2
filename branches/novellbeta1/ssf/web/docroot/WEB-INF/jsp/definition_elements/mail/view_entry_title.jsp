<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<% //Title view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div class="ss_entryContent">
<span class="ss_entryTitle">
<table border="0" width="100%">
<tr><td align="left">
<c:if test="${!empty ssDefinitionEntry.docNumber}">
  <c:out value="${ssDefinitionEntry.docNumber}"/>.
</c:if>
  <a href="<ssf:url 
  		adapter="true" 
    	portletName="ss_forum" 
   		action="view_permalink"
		binderId="${ssDefinitionEntry.parentBinder.id}"
		entryId="${ssDefinitionEntry.id}">
		<ssf:param name="entityType" value="${ssDefinitionEntry.entityType}" />
    	<ssf:param name="newTab" value="1"/>
 	  	</ssf:url>">
<c:if test="${empty ssDefinitionEntry.title}">
  <span class="ss_light">--<ssf:nlt tag="entry.noTitle"/>--</span>
</c:if><c:out value="${ssDefinitionEntry.title}"/></a></span>
</td><td align="right">
<c:if test="${!empty ssDefinitionEntry.parentBinder.posting.emailAddress}">
&nbsp;&nbsp;<a href="mailto:${ssDefinitionEntry.parentBinder.posting.emailAddress}?subject=RE: DocId:${ssDefinitionEntry.parentBinder.id}:${ssDefinitionEntry.id}"
		style="font-size: 11px; color: #3366cc; font-weight:bold;
border-top: 1px solid #d5d5d5; border-left:
  1px solid #d5d5d5;
border-right: 1px solid #666666;
  border-bottom: 1px solid #666666;
background-color:
  #e5e5e5; padding: 3px;
font-family: arial, helvetica,
  sans-serif;
margin-left: 0px; margin-right: 6px; margin-bottom,
  margin-top: 2px;
line-height: 200%; text-decoration:
  none;" ><ssf:nlt tag="mail.reply"/></a>
</c:if>
 	  	
</td>
</tr>
</table>

</div>
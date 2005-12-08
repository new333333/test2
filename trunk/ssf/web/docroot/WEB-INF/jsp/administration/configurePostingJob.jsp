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

var <portlet:namespace/>_isAliasVisible=false;

function <portlet:namespace/>_toggleAlias(name) {
   if (<portlet:namespace/>_isAliasVisible) {
     ss_showHideObj(name, 'hidden', 'none');
     <portlet:namespace/>_isAliasVisible = false;
     ss_replaceImage('<portlet:namespace/>_expandgif', '<html:imagesPath />pics/sym_s_expand.gif');
   } else {
     ss_showHideObj(name, 'visible', 'block');
     <portlet:namespace/>_isAliasVisible = true;
     ss_replaceImage('<portlet:namespace/>_expandgif', '<html:imagesPath />pics/sym_s_collapse.gif');
   }
     
}
var <portlet:namespace/>_alias_count=0;

function <portlet:namespace/>_addAlias(alias, forums) {
   var tbl = document.getElementById('<portlet:namespace/>_alias_table');
   var body = document.getElementById('<portlet:namespace/>_alias_body');
   var row = document.createElement("tr");
   body.appendChild(row);
    
    // delete cell
	var cellLeft = document.createElement("td");
	row.appendChild(cellLeft);
    cellLeft.setAttribute("align", "center");
    cellLeft.setAttribute("class", "ss_content");
    cellLeft.setAttribute("className", "ss_content");
	var inner = document.createElement("input");
	inner.setAttribute("type", "checkbox");
    inner.setAttribute("name", "delete" + <portlet:namespace/>_alias_count);
    inner.setAttribute("id", "delete" + <portlet:namespace/>_alias_count);
    cellLeft.appendChild(inner);
  
	//alias name
	cellLeft = document.createElement("td");
	row.appendChild(cellLeft);
	cellLeft.setAttribute("class", "ss_content");
	cellLeft.setAttribute("className", "ss_content");
	inner = document.createElement("input");
	inner.setAttribute("type", "text");
  	inner.setAttribute("size", "32");
	inner.setAttribute("name", "alias" + <portlet:namespace/>_alias_count);
	inner.setAttribute("id", "alias" + <portlet:namespace/>_alias_count);
	inner.setAttribute("value", alias);
    cellLeft.appendChild(inner);

	//mapped forums
	cellLeft = document.createElement("td");
	row.appendChild(cellLeft);
 	cellLeft.setAttribute("class", "ss_content");
	cellLeft.setAttribute("className", "ss_content");
	inner = document.createTextNode(forums);
 	cellLeft.appendChild(inner);

	<portlet:namespace/>_alias_count++;

}

</script>
<input type="hidden" id="enabled" name="enabled" value="${ssPostingConfig.enabled}"/>

<div class="ss_portlet">
<span class="ss_titlebold"><ssf:nlt tag="incoming.job_title" /></span><br/>
<br/>
<c:set var="toolbar" value="${ssToolbar}" scope="request" />
<%@ include file="/WEB-INF/jsp/definition_elements/toolbar_view.jsp" %>

<table border ="0" cellspacing="0" cellpadding="3">
<tr><td class="ss_content"> 
<input type="checkbox" class="ss_content" id="disabled" name="disabled" onClick="<portlet:namespace/>setEnable();" <c:if test="${!ssPostingConfig.enabled}">checked</c:if>/>
Disable all incoming e-mail<br/>
</td></tr></table>

<div class="ss_divider"></div>
<span class="ss_contentbold"><ssf:nlt tag="incoming.schedule_title" /></span>

<c:set var="schedule" value="${ssPostingConfig.schedule}"/>
<%@ include file="/WEB-INF/jsp/administration/schedule.jsp" %>

<div class="ss_divider"></div>
<a href="javascript: ;" onClick="<portlet:namespace/>_toggleAlias('<portlet:namespace/>_alias_div')" >
<img border="0" src="<html:imagesPath />pics/sym_s_expand.gif" name="<portlet:namespace/>_expandgif" /></a>
<a href="javascript: ;" onClick="<portlet:namespace/>_toggleAlias('<portlet:namespace/>_alias_div')" >
<b><ssf:nlt tag="incoming.aliases" /></b></a><br></a>

<div id="<portlet:namespace/>_alias_div" name="<portlet:namespace/>_alias_div" style="visibility:hidden; display:none;">
<a class="ss_linkbutton" href="javascript:" onClick="<portlet:namespace/>_addAlias('','');"><ssf:nlt tag="button.add_alias" /></a>

<table border="0" cellspacing="0" cellpadding="3" class="ss_borderTable" name="<portlet:namespace/>_alias_table" id="<portlet:namespace/>_alias_table">
<tbody name="<portlet:namespace/>_alias_body" id="<portlet:namespace/>_alias_body">

  <tr class="ss_headerRow">
  <td class="ss_finestprintgray" align="center" width="5%" scope="col"><ssf:nlt tag="incoming.delete" /></td>
  <td class="ss_contentbold" scope="col"><ssf:nlt tag="incoming.alias" /></td>
  <td class="ss_contentbold" scope="col"><ssf:nlt tag="incoming.forums" /></td>
</tr>
</tBody>
</table>
<jsp:useBean id="ssPostings" type="java.util.List" scope="request" />
<%
			java.util.HashMap postingMap = new java.util.HashMap();
			java.util.HashSet forums;
			for (int i=0; i<ssPostings.size(); ++i) {
				com.sitescape.ef.domain.PostingDef post = (com.sitescape.ef.domain.PostingDef)ssPostings.get(i);
				com.sitescape.ef.domain.Binder top = post.getBinder();
				if (top instanceof com.sitescape.ef.domain.Folder) {
					if (((com.sitescape.ef.domain.Folder)top).getTopFolder() != null) {
						top = ((com.sitescape.ef.domain.Folder)top).getTopFolder();
					}
				}
				Long key = post.getEmailId();
				if (postingMap.containsKey(key)) {
					forums = (java.util.HashSet)postingMap.get(key);
					if (forums.contains(top)) continue;
					forums.add(top);	
				} else {
					forums = new java.util.HashSet();
					forums.add(top);
					postingMap.put(key, forums);					
				}
			}
			request.setAttribute("postMap", postingMap);
	
%>

<c:forEach var="post" varStatus="status" items="${ssPostingConfig.aliases}" >
<c:set var="title" value=" " scope="request"/>
<c:forEach var="forum" varStatus="fStatus" items="${postMap[post.value]}">
<c:choose>
<c:when test="${fStatus.first}"><c:set var="title" value="${forum.title}"/></c:when>
<c:otherwise><c:set var="title" value="${title},${forum.title}"/></c:otherwise>
</c:choose>
</c:forEach>
<input type="hidden" id="aliasId${status.index}" name="aliasId${status.index}" value="${post.value}"/>
<script language="javascript" type="text/javascript">
<portlet:namespace/>_addAlias('<c:out value="${post.key}"/>','<c:out value="${title}"/>');
</script>
</c:forEach>

<br/>

<div class="ss_divider"></div>
</div>
<br/>
<input class="ss_submit" type="submit" name="okBtn" value="<ssf:nlt tag="button.ok" />">
<input class="ss_submit" type="submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>">
</div>

</form>


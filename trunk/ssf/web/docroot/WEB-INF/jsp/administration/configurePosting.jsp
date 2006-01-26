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
<%@ page import="com.sitescape.ef.domain.Folder" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<div class="ss_style ss_portlet">
<span class="ss_titlebold"><ssf:nlt tag="incoming.forum_title"/></span><br/>
<br/>
<c:choose>
<c:when test="${!empty ssWsDomTree}">
<jsp:useBean id="ssWsDomTree" type="org.dom4j.Document" scope="request" />
	<table class="ss_style" border="0" cellpadding="0" cellspacing="0" width="95%">
	<tr align="left"><td><ssf:nlt tag="tree.choose_forum"/></td></tr>
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
<jsp:useBean id="ssFolderDomTree" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssFolder" type="com.sitescape.ef.domain.Folder" scope="request" />

<form class="ss_style" name="<portlet:namespace/>fm" id="<portlet:namespace/>fm" method="post" action="<portlet:actionURL>
			<portlet:param name="action" value="configure_posting"/>
			<portlet:param name="binderId" value="${ssFolder.id}"/>
		</portlet:actionURL>">
<script type="text/javascript">
function <portlet:namespace/>setEnable() {
	if (document.<portlet:namespace/>fm.disabled.checked) {
		document.<portlet:namespace/>fm.enabled.value = "false";
	} else {
		document.<portlet:namespace/>fm.enabled.value = "true";
	}
}
var <portlet:namespace/>_alias_count=0;

function <portlet:namespace/>_addAlias(title, alias, subject) {
   var tbl = document.getElementById('<portlet:namespace/>_alias_table');
   var body = document.getElementById('<portlet:namespace/>_alias_body');
   var row = document.createElement("tr");
   body.appendChild(row);
    
    // delete cell
	var cellLeft = document.createElement("td");
	row.appendChild(cellLeft);
    cellLeft.setAttribute("align", "center");
	var inner = document.createElement("input");
	inner.setAttribute("type", "checkbox");
    inner.setAttribute("name", "delete" + <portlet:namespace/>_alias_count);
    inner.setAttribute("id", "delete" + <portlet:namespace/>_alias_count);
    cellLeft.appendChild(inner);
  
	//folder title
	cellLeft = document.createElement("td");
	row.appendChild(cellLeft);
    cellLeft.appendChild(document.createTextNode(title));

	//alias 
	cellLeft = document.createElement("td");
	row.appendChild(cellLeft);
	cellLeft.innerHTML=<portlet:namespace/>_buildSelectBox(alias);
	
	//subject
	cellLeft = document.createElement("td");
	row.appendChild(cellLeft);
	inner = document.createElement("input");
	inner.setAttribute("type", "text");
  	inner.setAttribute("size", "32");
	inner.setAttribute("name", "subject" + <portlet:namespace/>_alias_count);
	inner.setAttribute("id", "subject" + <portlet:namespace/>_alias_count);
	inner.setAttribute("value", subject);
    cellLeft.appendChild(inner);
	<portlet:namespace/>_select(alias);
	<portlet:namespace/>_alias_count++;

}
function t_<portlet:namespace/>_folderTree_showId(id, obj) {
	//User selected an item from the tree
	//See if this id has any info associated with it
    var frm = document.getElementById('<portlet:namespace/>fm');
	inner = document.createElement("input");
	inner.setAttribute("type", "hidden");
	inner.setAttribute("name", "folder" + <portlet:namespace/>_alias_count);
	inner.setAttribute("id", "folder" + <portlet:namespace/>_alias_count);
	inner.setAttribute("value", id);
	frm.appendChild(inner);
 	<portlet:namespace/>_addAlias(<portlet:namespace/>_folderList[id],'','');
	return true;
}
var <portlet:namespace/>_selectKeys = new Array();
var <portlet:namespace/>_selectValues = new Array();

<c:forEach var="alias" varStatus="aStatus" items="${ssEmailAliases}">

<portlet:namespace/>_selectValues[<c:out value="${aStatus.index}"/>] = '<c:out value="${alias.id}"/>';
<portlet:namespace/>_selectKeys[<c:out value="${aStatus.index}"/>] = '<c:out value="${alias.aliasName}"/>';

</c:forEach>
function <portlet:namespace/>_buildSelectBox(alias) {
	var selectHTML='<select name="select' + <portlet:namespace/>_alias_count +
			'" id="select' + <portlet:namespace/>_alias_count + '"> ';
	selectHTML+= '<option value=""><ssf:nlt tag="incoming.select"/></option>';
	for (i=0; i < <portlet:namespace/>_selectValues.length; ++i) {
		selectHTML+= '<option value="' + <portlet:namespace/>_selectValues[i];
		selectHTML += '">' + <portlet:namespace/>_selectKeys[i] + '</option>';
	}	
	selectHTML +=' </select>';
	return selectHTML;
}
function <portlet:namespace/>_select(alias) {
   var tbl = document.getElementById('select' + <portlet:namespace/>_alias_count);
	if (alias == '') {
		tbl.options[0].selected=true;
	} else {
		for (i=0; i < <portlet:namespace/>_selectValues.length; ++i) {
			if (<portlet:namespace/>_selectValues[i] == alias) {
				tbl.options[i+1].selected=true;
			}
		}
	}	
}
</script>
<c:if test="${!ssScheduleInfo.enabled}"><ssf:nlt tag="incoming.disabled"/></c:if><br/>
<div class="ss_divider"></div>
<br/>
<%
	String folderId = ssFolder.getId().toString();
	String parentFolderId = "";
	if (ssFolder instanceof Folder) {
		Folder parentFolder = ((Folder) ssFolder).getParentFolder();
		if (parentFolder != null) parentFolderId = parentFolder.getId().toString();
	}
%>
<table class="ss_style" width="100%" border="0" cellpadding="2" cellspacing="0">
 <tr align="left"><td class="ss_bold"><ssf:nlt tag="tree.choose_folder"/></td></tr>
 <tr>
  <td>
	  <ssf:tree treeName="folderTree" treeDocument="<%= ssFolderDomTree %>" 
	    rootOpen="false" 
	    nodeOpen="<%= parentFolderId %>" highlightNode="<%= folderId %>" /></td>
 </tr>
</table>
<div class="ss_divider"></div>

<div id="<portlet:namespace/>_alias_div" name="<portlet:namespace/>_alias_div">
<table border="0" cellspacing="0" cellpadding="3" class="ss_style ss_borderTable" name="<portlet:namespace/>_alias_table" id="<portlet:namespace/>_alias_table">
<tbody name="<portlet:namespace/>_alias_body" id="<portlet:namespace/>_alias_body">
<tr class="ss_headerRow">
<td class="ss_finestprintgray" align="center" width="5%" scope="col"><ssf:nlt tag="incoming.delete" /></td>
<td class="ss_bold" scope="col"><ssf:nlt tag="incoming.folder"/></td>
<td class="ss_bold" scope="col"><ssf:nlt tag="incoming.alias"/></td>
<td class="ss_bold" scope="col"><ssf:nlt tag="incoming.subject"/></td>
</tr>
</tbody>
</table>
</div>
<script type="text/javascript">
var <portlet:namespace/>_folderList = new Array();

<c:forEach var="fld" items="${ssFolders}">
<portlet:namespace/>_folderList['<c:out value="${fld.id}"/>']='<c:out value="${fld.title}"/>';
<c:forEach var="post" items="${fld.postings}">

<portlet:namespace/>_addAlias('<c:out value="${post.binder.title}"/>', '<c:out value="${post.emailAlias.id}"/>', 
					'<c:out value="${post.subject}"/>');
</c:forEach>
</c:forEach>
</script>
<c:set var="pCount" value="0"/>
<c:forEach var="fld" items="${ssFolders}">
<c:forEach var="post" items="${fld.postings}">
<input type="hidden" id="posting<c:out value="${pCount}"/>" name="posting<c:out value="${pCount}"/>" value="<c:out value="${post.id}"/>"/>
<input type="hidden" id="folder<c:out value="${pCount}"/>" name="folder<c:out value="${pCount}"/>" value="<c:out value="${fld.id}"/>"/>
<c:set var="pCount" value="${pCount+1}"/>
</c:forEach>
</c:forEach>
<br/>
<div class="ss_divider"></div>
<br/>
	<input type="submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>">
	<input type="submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>">
</form>

</c:otherwise>
</c:choose>
</div>
		
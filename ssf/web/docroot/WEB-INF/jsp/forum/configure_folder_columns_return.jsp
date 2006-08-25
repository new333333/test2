<%@ include file="/WEB-INF/jsp/common/snippet.include.jsp" %>
<%@ page import="java.util.Map" %>
<jsp:useBean id="ss_ajaxStatus" type="java.util.Map" scope="request" />
<%
	if (ss_ajaxStatus.containsKey("ss_ajaxNotLoggedIn")) {
%>
<span><ssf:nlt tag="general.notLoggedIn"/></span>
<%
	} else {
%>
<c:set var="numberChecked" value=""/>
<c:if test="${!empty folderColumns.number}"><c:set var="numberChecked" value="checked"/></c:if>
<c:set var="titleChecked" value=""/>
<c:if test="${!empty folderColumns.title}"><c:set var="titleChecked" value="checked"/></c:if>
<c:set var="stateChecked" value=""/>
<c:if test="${!empty folderColumns.state}"><c:set var="stateChecked" value="checked"/></c:if>
<c:set var="authorChecked" value=""/>
<c:if test="${!empty folderColumns.author}"><c:set var="authorChecked" value="checked"/></c:if>
<c:set var="dateChecked" value=""/>
<c:if test="${!empty folderColumns.date}"><c:set var="dateChecked" value="checked"/></c:if>

<div class="ss_style" align="left">
<form method="post" onSubmit="ss_configureColumnsSetActionUrl(this);">
<span class="ss_largerprint ss_bold"><ssf:nlt tag="folder.selectColumns"/></span>
<br/>
<br/>
<div class="ss_indent_medium">
  <input type="checkbox" name="number" ${numberChecked}> <ssf:nlt tag="folder.column.Number"/><br/>
  <input type="checkbox" name="title" ${titleChecked}> <ssf:nlt tag="folder.column.Title"/><br/>
  <input type="checkbox" name="state" ${stateChecked}> <ssf:nlt tag="folder.column.State"/><br/>
  <input type="checkbox" name="author" ${authorChecked}> <ssf:nlt tag="folder.column.Author"/><br/>
  <input type="checkbox" name="date" ${dateChecked}> <ssf:nlt tag="folder.column.Date"/><br/>
  
  <br/>
  <input type="submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>">
  &nbsp;&nbsp;&nbsp;
  <input type="submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>"
  onClick="ss_hideDiv('ss_folder_column_menu');return false;">
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <input type="submit" name="defaultBtn" value="<ssf:nlt tag="button.restoreDefaults"/>">
  
</div>
</form>
</div>
<%
	}
%>	

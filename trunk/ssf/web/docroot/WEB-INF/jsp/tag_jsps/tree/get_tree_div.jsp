<%@ page session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ssf" uri="http://www.sitescape.com/tags-ssf" %>
<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>
<%@ page contentType="text/xml" %>
<%@ page import="java.util.Map" %>
<jsp:useBean id="ss_ajaxStatus" type="java.util.Map" scope="request" />
<jsp:useBean id="ss_tree_treeName" type="java.lang.String" scope="request" />
<jsp:useBean id="ss_tree_binderId" type="java.lang.String" scope="request" />
<jsp:useBean id="ss_tree_topId" type="java.lang.String" scope="request" />
<jsp:useBean id="ssWsDomTree" type="org.dom4j.Document" scope="request" />
<taconite-root xml:space="preserve">
<%
	if (ss_ajaxStatus.containsKey("ss_ajaxNotLoggedIn")) {
%>
	<taconite-replace contextNodeID="ss_status_message" parseInBrowser="true">
		<div id="ss_status_message" style="visibility:hidden; display:none;">error</div>
	</taconite-replace>
<%
	} else {
%>
	<taconite-replace contextNodeID="ss_status_message" parseInBrowser="true">
		<div id="ss_status_message" style="visibility:hidden; display:none;">ok</div>
	</taconite-replace>
	<taconite-insert-after contextNodeID="<%= ss_tree_treeName + "temp" + ss_tree_binderId %>" parseInBrowser="true">
		<ssf:tree treeName="<%= ss_tree_treeName %>" 
		  treeDocument="<%= ssWsDomTree %>"  
		  startingId="<%= ss_tree_binderId %>"
		  topId="<%= ss_tree_topId %>"
		  rootOpen="true" />
	</taconite-insert-after>
<%
	}
%>	
</taconite-root>

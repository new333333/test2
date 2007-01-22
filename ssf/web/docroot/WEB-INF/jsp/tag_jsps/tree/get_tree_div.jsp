<%@ page session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ssf" uri="http://www.sitescape.com/tags-ssf" %>
<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>
<%@ page contentType="text/xml; charset=UTF-8" %>
<jsp:useBean id="ssWsDomTree" type="org.dom4j.Document" scope="request" />

<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>
<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">

	<taconite-insert-after contextNodeID="${ss_tree_treeName}temp${ss_tree_binderId}" parseInBrowser="true">

	<c:choose>
	<c:when test="${ss_tree_select_type == '0'}">	
		<ssf:tree treeName="${ss_tree_treeName}" 
		  treeDocument="<%= ssWsDomTree %>"  
		  startingId="${ss_tree_binderId}"
		  topId="${ss_tree_topId}"
		  rootOpen="true" 
		  showIdRoutine="${ss_tree_showIdRoutine}"
		/>
	</c:when>
	<c:when test="${ss_tree_select_type == '1'}">
		<ssf:tree treeName="${ss_tree_treeName}" 
		  treeDocument="<%= ssWsDomTree %>"  
		  startingId="${ss_tree_binderId}"
		  topId="${ss_tree_topId}"
		  rootOpen="true" 
		  showIdRoutine="${ss_tree_showIdRoutine}"
		  singleSelect="${ss_tree_select}"
		  singleSelectName="${ss_tree_select_id}"
		/>
	</c:when>
	<c:when test="${ss_tree_select_type == '2'}">
<jsp:useBean id="ss_tree_select" type="java.util.List" scope="request" />
		<ssf:tree treeName="${ss_tree_treeName}" 
		  treeDocument="<%= ssWsDomTree %>"  
		  startingId="${ss_tree_binderId}"
		  topId="${ss_tree_topId}"
		  rootOpen="true" 
		  showIdRoutine="${ss_tree_showIdRoutine}"
		  multiSelect="<%= ss_tree_select %>"
		  multiSelectPrefix="${ss_tree_select_id}"
		/>
	</c:when>
	</c:choose>

	</taconite-insert-after>
</c:if>	
</taconite-root>

<% // The folder collection view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="configDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="configElement" type="org.dom4j.Element" scope="request" />
<jsp:useBean id="configJspStyle" type="String" scope="request" />
<jsp:useBean id="seenMap" type="com.sitescape.ef.domain.SeenMap" scope="request" />
<jsp:useBean id="folderDomTree" type="org.dom4j.Document" scope="request" />
<%
	//Get the collection style displayed. 
	String style = (String) request.getAttribute("property_style");

	if (style.equals("folderList")) {
%>
<%@ include file="/jsp/definition_elements/folder_list.jsp" %>
<%
	} else if (style.equals("fileLibrary")) {
%>
<%@ include file="/jsp/definition_elements/file_library.jsp" %>
<%
	}
%>
<% // The folder collection view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssConfigElement" type="org.dom4j.Element" scope="request" />
<jsp:useBean id="ssConfigJspStyle" type="String" scope="request" />
<jsp:useBean id="ssSeenMap" type="com.sitescape.ef.domain.SeenMap" scope="request" />
<jsp:useBean id="ssFolderDomTree" type="org.dom4j.Document" scope="request" />
<%
	//Get the collection style displayed. 
	String style = (String) request.getAttribute("property_style");

	if (style.equals("folderList")) {
%>
<%@ include file="/WEB-INF/jsp/definition_elements/folder_list.jsp" %>
<%
	} else if (style.equals("fileLibrary")) {
%>
<%@ include file="/WEB-INF/jsp/definition_elements/file_library.jsp" %>
<%
	}
%>
<% // The folder collection view %>
<%@ include file="/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ss_forum_forum" type="com.sitescape.ef.domain.Binder" scope="request" />
<jsp:useBean id="ss_forum_config_definition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ss_forum_config" type="org.dom4j.Element" scope="request" />
<jsp:useBean id="ss_forum_configJspStyle" type="String" scope="request" />
<jsp:useBean id="ss_folder_folders" type="java.util.List" scope="request" />
<jsp:useBean id="ss_folder_entries" type="java.util.ArrayList" scope="request" />
<jsp:useBean id="ss_folder_seenmap" type="com.sitescape.ef.domain.SeenMap" scope="request" />
<jsp:useBean id="ss_folder_tree" type="org.dom4j.Document" scope="request" />
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
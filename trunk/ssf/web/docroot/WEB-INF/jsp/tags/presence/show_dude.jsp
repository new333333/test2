<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="portletadapter" uri="http://www.sitescape.com/tags-portletadapter" %>

<%@ page import="com.sitescape.util.ParamUtil" %>
<portletadapter:defineObjects1/>
<ssf:ifadapter><portletadapter:defineObjects2/></ssf:ifadapter>
<ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter>

<jsp:useBean id="ss_presence_userStatus" type="java.lang.Integer" scope="request" />
<%
	// Get the presence info
	Integer userStatus = (Integer) renderRequest.getAttribute("ss_presence_userStatus");
	String dude = "sym_s_white_dude.gif";
    switch (userStatus.intValue()) {
		case -1: 
			dude = "sym_s_white_dude.gif";
			break;
		case 1:
			dude = "sym_s_green_dude.gif";
			break;
		case -99:
			break;
		default:
			dude = "sym_s_yellow_dude.gif";
    }
%>
<img border="0" src="<html:imagesPath/>pics/<%= dude %>">

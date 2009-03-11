<%@ page import="java.util.List"%>
<%@ page import="java.util.Arrays"%>
<%
    String[] userElementsArray = new String[] {"street_address", "city", "state", "zip"};
    List userElements = Arrays.asList(userElementsArray);
    request.setAttribute("user_elements", userElements);
%>
<c:set var="user_elements" value="<%= userElements %>" scope="request"/>

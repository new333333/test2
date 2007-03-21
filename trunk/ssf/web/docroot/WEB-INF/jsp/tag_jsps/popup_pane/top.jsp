<%@ page session="false" %>
<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ssf" uri="http://www.sitescape.com/tags-ssf" %>
<%@ taglib prefix="portletadapter" uri="http://www.sitescape.com/tags-portletadapter" %>
<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>
<portletadapter:defineObjects1/>
<ssf:ifadapter><portletadapter:defineObjects2/></ssf:ifadapter>
<ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter>
<table class="ss_popup" cellpadding="0" cellspacing="0" border="0" style="width: ${width};">
 <tbody>
  <tr>
   <td width="30px"><div class="ss_popup_topleft"></td>
   <td width="100%"><div class="ss_popup_topright">
    <div onClick="${closeScript}" class="ss_popup_close"></div>
    <div class="ss_popup_title"><ssf:nlt tag="${titleTag}"/></div></div>
   </td>
  </tr>
  <tr><td colspan="2">
   <div class="ss_popup_body">

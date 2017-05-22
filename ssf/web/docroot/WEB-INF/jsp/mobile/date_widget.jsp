<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% //Date widget form element %>
<c:if test="${empty timeZoneID}">
	<c:set var="timeZoneID" value="${ssUser.timeZone.ID}" />
</c:if>

<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.kablink.teaming.util.CalendarHelper" %>
<c:set var="initDay" value="" />
<c:set var="initmonth" value="" />
<c:set var="initYear" value="" />
<c:if test="${!empty ss_dateWidgetDate}">
  <c:set var="initDay"><fmt:formatDate value="${ss_dateWidgetDate}" pattern="dd" timeZone="${timeZoneID}"/></c:set>
  <c:set var="initmonth"><fmt:formatDate value="${ss_dateWidgetDate}" pattern="MM" timeZone="${timeZoneID}"/></c:set>
  <c:set var="initYear"><fmt:formatDate value="${ss_dateWidgetDate}" pattern="yyyy" timeZone="${timeZoneID}"/></c:set>
</c:if>
<table>
  <tr class="ss_dateinput">
   <td>
	<select name="${ss_dateWidgetId}_date">
	  <option value="0" >--</option>
	  <option value="1" <c:if test="${initDay == '01'}">selected</c:if>>1</option>
	  <option value="2" <c:if test="${initDay == '02'}">selected</c:if>>2</option>
	  <option value="3" <c:if test="${initDay == '03'}">selected</c:if>>3</option>
	  <option value="4" <c:if test="${initDay == '04'}">selected</c:if>>4</option>
	  <option value="5" <c:if test="${initDay == '05'}">selected</c:if>>5</option>
	  <option value="6" <c:if test="${initDay == '06'}">selected</c:if>>6</option>
	  <option value="7" <c:if test="${initDay == '07'}">selected</c:if>>7</option>
	  <option value="8" <c:if test="${initDay == '08'}">selected</c:if>>8</option>
	  <option value="9" <c:if test="${initDay == '09'}">selected</c:if>>9</option>
	  <option value="10" <c:if test="${initDay == '10'}">selected</c:if>>10</option>
	  <option value="11" <c:if test="${initDay == '11'}">selected</c:if>>11</option>
	  <option value="12" <c:if test="${initDay == '12'}">selected</c:if>>12</option>
	  <option value="13" <c:if test="${initDay == '13'}">selected</c:if>>13</option>
	  <option value="14" <c:if test="${initDay == '14'}">selected</c:if>>14</option>
	  <option value="15" <c:if test="${initDay == '15'}">selected</c:if>>15</option>
	  <option value="16" <c:if test="${initDay == '16'}">selected</c:if>>16</option>
	  <option value="17" <c:if test="${initDay == '17'}">selected</c:if>>17</option>
	  <option value="18" <c:if test="${initDay == '18'}">selected</c:if>>18</option>
	  <option value="19" <c:if test="${initDay == '19'}">selected</c:if>>19</option>
	  <option value="20" <c:if test="${initDay == '20'}">selected</c:if>>20</option>
	  <option value="21" <c:if test="${initDay == '21'}">selected</c:if>>21</option>
	  <option value="22" <c:if test="${initDay == '22'}">selected</c:if>>22</option>
	  <option value="23" <c:if test="${initDay == '23'}">selected</c:if>>23</option>
	  <option value="24" <c:if test="${initDay == '24'}">selected</c:if>>24</option>
	  <option value="25" <c:if test="${initDay == '25'}">selected</c:if>>25</option>
	  <option value="26" <c:if test="${initDay == '26'}">selected</c:if>>26</option>
	  <option value="27" <c:if test="${initDay == '27'}">selected</c:if>>27</option>
	  <option value="28" <c:if test="${initDay == '28'}">selected</c:if>>28</option>
	  <option value="29" <c:if test="${initDay == '29'}">selected</c:if>>29</option>
	  <option value="30" <c:if test="${initDay == '30'}">selected</c:if>>30</option>
	  <option value="31" <c:if test="${initDay == '31'}">selected</c:if>>31</option>
	</select>
   </td>
   
   <td>
	<select name="${ss_dateWidgetId}_month">
	  <option value="0" >--</option>
	  <option value="1" <c:if test="${initmonth == '01'}">selected</c:if>><ssf:nlt tag="calendar.abbreviation.january"/></option>
	  <option value="2" <c:if test="${initmonth == '02'}">selected</c:if>><ssf:nlt tag="calendar.abbreviation.february"/></option>
	  <option value="3" <c:if test="${initmonth == '03'}">selected</c:if>><ssf:nlt tag="calendar.abbreviation.march"/></option>
	  <option value="4" <c:if test="${initmonth == '04'}">selected</c:if>><ssf:nlt tag="calendar.abbreviation.april"/></option>
	  <option value="5" <c:if test="${initmonth == '05'}">selected</c:if>><ssf:nlt tag="calendar.abbreviation.may"/></option>
	  <option value="6" <c:if test="${initmonth == '06'}">selected</c:if>><ssf:nlt tag="calendar.abbreviation.june"/></option>
	  <option value="7" <c:if test="${initmonth == '07'}">selected</c:if>><ssf:nlt tag="calendar.abbreviation.july"/></option>
	  <option value="8" <c:if test="${initmonth == '08'}">selected</c:if>><ssf:nlt tag="calendar.abbreviation.august"/></option>
	  <option value="9" <c:if test="${initmonth == '09'}">selected</c:if>><ssf:nlt tag="calendar.abbreviation.september"/></option>
	  <option value="10" <c:if test="${initmonth == '10'}">selected</c:if>><ssf:nlt tag="calendar.abbreviation.october"/></option>
	  <option value="11" <c:if test="${initmonth == '11'}">selected</c:if>><ssf:nlt tag="calendar.abbreviation.november"/></option>
	  <option value="12" <c:if test="${initmonth == '12'}">selected</c:if>><ssf:nlt tag="calendar.abbreviation.december"/></option>
	</select>
   </td>
   
   <td>	
	<select name="${ss_dateWidgetId}_year">
	  <option value="" >--</option>
	  <option value="1998" <c:if test="${initYear == '1998'}">selected</c:if>>1998</option>
	  <option value="1999" <c:if test="${initYear == '1999'}">selected</c:if>>1999</option>
	  <option value="2000" <c:if test="${initYear == '2000'}">selected</c:if>>2000</option>
	  <option value="2001" <c:if test="${initYear == '2001'}">selected</c:if>>2001</option>
	  <option value="2002" <c:if test="${initYear == '2002'}">selected</c:if>>2002</option>
	  <option value="2003" <c:if test="${initYear == '2003'}">selected</c:if>>2003</option>
	  <option value="2004" <c:if test="${initYear == '2004'}">selected</c:if>>2004</option>
	  <option value="2005" <c:if test="${initYear == '2005'}">selected</c:if>>2005</option>
	  <option value="2006" <c:if test="${initYear == '2006'}">selected</c:if>>2006</option>
	  <option value="2007" <c:if test="${initYear == '2007'}">selected</c:if>>2007</option>
	  <option value="2008" <c:if test="${initYear == '2008'}">selected</c:if>>2008</option>
	  <option value="2009" <c:if test="${initYear == '2009'}">selected</c:if>>2009</option>
	  <option value="2010" <c:if test="${initYear == '2010'}">selected</c:if>>2010</option>
	  <option value="2011" <c:if test="${initYear == '2011'}">selected</c:if>>2011</option>
	  <option value="2012" <c:if test="${initYear == '2012'}">selected</c:if>>2012</option>
	  <option value="2013" <c:if test="${initYear == '2013'}">selected</c:if>>2013</option>
	  <option value="2014" <c:if test="${initYear == '2014'}">selected</c:if>>2014</option>
	  <option value="2015" <c:if test="${initYear == '2015'}">selected</c:if>>2015</option>
	  <option value="2016" <c:if test="${initYear == '2016'}">selected</c:if>>2016</option>
	  <option value="2017" <c:if test="${initYear == '2017'}">selected</c:if>>2017</option>
	  <option value="2018" <c:if test="${initYear == '2018'}">selected</c:if>>2018</option>
	  <option value="2019" <c:if test="${initYear == '2019'}">selected</c:if>>2019</option>
	  <option value="2020" <c:if test="${initYear == '2020'}">selected</c:if>>2020</option>
	</select>
   </td>
  </tr>
</table>

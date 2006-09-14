<%@ include file="/WEB-INF/jsp/box/init.jsp" %>
<jsp:useBean id="ss_box_color" type="String" scope="request" />
<jsp:useBean id="ss_box_canvas_color" type="String" scope="request" />
<%
boolean brWrapContent = ParamUtil.get(request, "box_br_wrap_content", true);
%>
<c:set var="boxColor" value="<%= ParamUtil.get(request, "box_color", ss_box_color) %>" />
<c:set var="boxBgColor" value="<%= ParamUtil.get(request, "box_canvas_color", ss_box_canvas_color) %>" />

<c:if test="<%= brWrapContent %>">
  <br>
</c:if>
	  </td>
	  <td class="ss_decor-border8" style="background-color:${boxBgColor};"></td>
	  </tr>


<%@ include file="/WEB-INF/jsp/box/box_bottom-ext.jsp" %>
	  <tr>
	  <td colspan="${ss_boxColCount + 1}" style=" background-color:${boxBgColor};"><div 
	    class="ss_decor-round-corners-bottom3"><div><div></div></div></div></td>
	  </tr>
	</table>
</div>



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
          </div>
	   </div>
	</div>


<%@ include file="/WEB-INF/jsp/box/box_bottom-ext.jsp" %>
  <div class="ss_box_bottom_rounded" style="background-color:${boxColor};"></div>
</div>
<script type="text/javascript">
function ss_roundBox() {
	if(!NiftyCheck()) return;
	Rounded("div.ss_box_rounded","top","${boxBgColor}","${boxColor}","smooth");
	Rounded("div.ss_box_bottom_rounded","bottom","${boxBgColor}","${boxColor}","smooth");
}
ss_createOnLoadObj('ss_roundBox', ss_roundBox);
</script>



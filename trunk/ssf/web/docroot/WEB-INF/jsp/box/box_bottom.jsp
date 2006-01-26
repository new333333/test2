<%@ include file="/WEB-INF/jsp/box/init.jsp" %>
<%
boolean brWrapContent = ParamUtil.get(request, "box_br_wrap_content", true);
String ss_boxColor = ParamUtil.get(request, "box_color", "#cecece");
%>
<c:if test="<%= brWrapContent %>">
  <br>
</c:if>
          </div>
	   </div>
	</div>


<%@ include file="/WEB-INF/jsp/box/box_bottom-ext.jsp" %>
  <div class="ss_box_bottom_rounded" style="background-color:<%= ss_boxColor %>;"></div>
</div>
<script type="text/javascript">
function ss_roundBox() {
	if(!NiftyCheck()) return;
	Rounded("div.ss_box_rounded","top","#FFF","<%= ss_boxColor %>","smooth");
	Rounded("div.ss_box_bottom_rounded","bottom","#CCC","<%= ss_boxColor %>","small");
}
createOnLoadObj('ss_roundBox', ss_roundBox);
</script>



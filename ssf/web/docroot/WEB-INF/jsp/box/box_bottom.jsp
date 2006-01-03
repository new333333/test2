<%@ include file="/WEB-INF/jsp/box/init.jsp" %>
<%
boolean brWrapContent = ParamUtil.get(request, "box_br_wrap_content", true);
%>
<c:if test="<%= brWrapContent %>">
  <br>
</c:if>
          </div>
	   </div>
	</div>


<%@ include file="/WEB-INF/jsp/box/box_bottom-ext.jsp" %>
  <div class="ss_box_bottom_rounded"></div>
</div>
<script language="javascript">
function ss_roundBox() {
	if(!NiftyCheck()) return;
	Rounded("div.ss_box_rounded","top","#FFF","#999999","smooth");
	Rounded("div.ss_box_bottom_rounded","bottom","#CCC","#999999","small");
}
createOnLoadObj('ss_roundBox', ss_roundBox);
</script>



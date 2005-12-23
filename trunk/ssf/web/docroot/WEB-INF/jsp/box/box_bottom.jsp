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
</div>
<script language="javascript">
function ss_roundBox() {
	if(!NiftyCheck()) return;
	Rounded("div.ss_box_rounded","all","#FFF","#cccccc","smooth");
}
createOnLoadObj('ss_roundBox', ss_roundBox);
</script>



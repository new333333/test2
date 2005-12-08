<%@ include file="/WEB-INF/jsp/box/init.jsp" %>

<%
String divId = ParamUtil.get(request, "box_id", "");
String bodyClassName = ParamUtil.get(request, "box_body_class", "bg");

int iWidth = (int)ParamUtil.get(request, "box_width", 600);
String width = Integer.toString(iWidth);

String wildWidth = "*";
try {
	wildWidth = Integer.toString(iWidth - 2);
}
catch (Exception e) {
}

boolean brWrapContent = ParamUtil.get(request, "box_br_wrap_content", true);
%>

<c:if test="<%= brWrapContent %>">
  <br>
</c:if>

          </div>
	    </div>
	</div>

	<div class="ssf-box-bottom-decoration" id="<%= divId %>_bottom_decoration">
      <div class="ssf-box-corner-bl"></div>
	  <div class="ssf-box-corner-br"></div>
	</div>
	<div class="ssf-box-bottom-decoration-2" id="<%= divId %>_bottom_decoration_2">
	  <div class="ssf-box-corner-2-bl"></div>
	  <div class="ssf-box-corner-2-br"></div>
	</div>

<%@ include file="/WEB-INF/jsp/box/box_bottom-ext.jsp" %>
</div>


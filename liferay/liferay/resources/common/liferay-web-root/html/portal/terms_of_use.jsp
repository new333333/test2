<%@ include file="/html/portal/init.jsp" %>
</style>

<form action="<%= themeDisplay.getPathMain() %>/portal/update_terms_of_use" name="fm">

<h3><%= LanguageUtil.get(pageContext, "license") %></h3>
<iframe src="/html/portal/CPAL_license.html" id="license_iframe" width="100%" height="450">123</iframe>

<br/>
<br/>
<input class="portlet-form-button" type="button" value="<%= LanguageUtil.get(pageContext, "i-agree") %>" 
  onClick="submitForm(document.fm);"> 
<input class="portlet-form-button" type="button" value="<%= LanguageUtil.get(pageContext, "i-disagree") %>" 
  onClick="alert('<%= UnicodeLanguageUtil.get(pageContext, "you-must-agree-with-the-terms-of-use-to-continue") %>');">

</form>

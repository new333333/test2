<% //View the listing part of a blog folder %>

  <table class="ss_blog" width="100%">
    <tr>
      <td class="ss_blog_content" width="80%" valign="top">
		  <c:forEach var="entry" items="${ssBlogEntries}" >
			<div class="ss_blog_content" style="margin:2px 8px 20px 2px;">
			  <ssf:displayConfiguration configDefinition="${entry.value.ssConfigDefinition}" 
			    configElement="${entry.value.ssConfigElement}" 
			    configJspStyle="view"
			    processThisItem="true" 
			    entry="${entry.value.entry}" />
			</div>
		  </c:forEach>
	  </td>
	  <td class="ss_blog_sidebar" width="20%" valign="top">
		<span class="ss_bold"><ssf:nlt tag="blog.calendar"/></span>
		<br>
		<div id="ss_blog_sidebar_date_popup"></div>
		<form name="ss_blog_sidebar_date_form" style="display:inline;">
		  <ssf:datepicker id="ss_blog_sidebar_date" 
            calendarDivId="ss_blog_sidebar_date_popup"
            formName="ss_blog_sidebar_date_form"
            immediateMode="true" initDate="<%= new Date() %>"
			callbackRoutine="ss_blog_sidebar_date_callback" />
        </form>
        <br>
        <br>
		<span class="ss_bold"><ssf:nlt tag="blog.archives"/></span>
	  </td>
    </tr>
  </table>

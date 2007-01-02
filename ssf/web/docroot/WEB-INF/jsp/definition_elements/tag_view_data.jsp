<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<div id="ss_tags${ss_tagViewNamespace}" class="ss_tag_pane">
<div align="right">
<a onClick="ss_hideTags${ss_tagViewNamespace}();return false;"><img 
  border="0" src="<html:imagesPath/>box/close_off.gif"/></a>
</div>
<div style="padding:0px 10px;">
<form class="ss_style ss_form ss_tag_pane_color" 
  method="post" action=""
  id="ss_modifyTagsForm${ss_tagViewNamespace}" name="ss_modifyTagsForm${ss_tagViewNamespace}">
<table class="ss_tag_pane_color">
<tbody>

<tr><th align="left" colspan="2"><ssf:nlt tag="tags.personalTags"/></th></tr>
<c:if test="${!empty ssPersonalTags}">
<c:forEach var="ptag" items="${ssPersonalTags}">
<tr>
  <td style="padding-left:10px;">
    <span class="ss_tags" style="padding-right:10px;"><c:out value="${ptag.name}"/></span>
    <a class="ss_fineprint ss_linkButton" href="#"
      onClick="ss_deleteTag${ss_tagViewNamespace}('${ptag.id}');return false;"
    ><ssf:nlt tag="button.delete"/></a>
  </td>
</tr>
</c:forEach>
</c:if>
<c:if test="${empty ssPersonalTags}">
<tr><td colspan="2"><ssf:nlt tag="tags.none" text="--none--"/></td></tr>
</c:if>

<tr><td colspan="2">
  <table class="ss_tag_pane_color"><tbody><tr><td>
    <input type="text" class="ss_text" name="personalTag" />
    </td><td>
      <a class="ss_linkButton" href="#" 
        onClick="ss_addTag${ss_tagViewNamespace}();return false;"
      ><ssf:nlt tag="button.add"/></a>
    </td></tr>
  </tbody></table>
</td></tr>
<tr><td></td><td></td></tr>
<tr><th align="left" colspan="2"><ssf:nlt tag="tags.communityTags"/></th></tr>
<c:if test="${!empty ssCommunityTags}">
<c:forEach var="tag" items="${ssCommunityTags}">
<tr>
  <td style="padding-left:10px;">
    <span class="ss_tags" style="padding-right:10px;"><c:out value="${tag.name}"/></span>
  </td>
  <td><a class="ss_fineprint ss_linkButton" href="#"
    onClick="ss_deleteTag${ss_tagViewNamespace}('${tag.id}');return false;"
    ><ssf:nlt tag="button.delete"/></a>
  </td>
</tr>
</c:forEach>
</c:if>
<c:if test="${empty ssCommunityTags}">
<tr><td colspan="2"><ssf:nlt tag="tags.none" text="--none--"/></td></tr>
</c:if>

<tr><td colspan="2">
  <table class="ss_tag_pane_color"><tbody><tr><td>
    <input type="text" class="ss_text" name="communityTag"/>
    </td><td style="padding-left:4px;">
    <a class="ss_linkButton" href="#" 
      onClick="ss_addTag${ss_tagViewNamespace}();return false;"
    ><ssf:nlt tag="button.add"/></a>
    </td></tr>
  </tbody></table>
</td></tr>

</tbody>
</table>
<input type="submit" value="ok" style="height:10px; width:10px;"
  onClick="ss_addTag${ss_tagViewNamespace}();return false;"/>
<div class="ss_tag_pane_ok_cover">
</div>
</form>
</div>

</div>

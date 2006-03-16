<% //view a folder forum in accessible mode %>

<div id="ss_showfolder" class="ss_style ss_portlet" style="display:block; margin:2;">
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${ssConfigElement}" 
  configJspStyle="${ssConfigJspStyle}" />
</div>

<script type="text/javascript">

function ss_showForumEntryInIframe(url) {
    self.location.href = url;
    return false;
}

</script>

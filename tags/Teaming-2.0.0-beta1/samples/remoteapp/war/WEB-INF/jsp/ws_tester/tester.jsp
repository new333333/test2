<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Iterator" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<form method="post" action="/remoteapp/wstester/submit">
<input type="radio" name="operation" value="search_search" checked/> Search<br/>
<div style="padding-left:50px;">
  Query<br/>
  <textarea name="query" rows="8" cols="60"></textarea>
  <br/>
  <input type="text" size="4" name="startCount"/> Starting point<br/>
  <input type="text" size="4" name="maxCount"/> Max number to return<br/>
  <br/>
</div>
<br/>

<input type="radio" name="operation" value="folder_getFolderEntryAsXML"/> Get folder entry<br/>
<div style="padding-left:50px;">
  Binder Id <input type="text" size="20" name="binderId_getFolderEntry"/><br/>
  Entry Id <input type="text" size="20" name="entryId_getFolderEntry"/><br/>
  <input type="radio" name="includeAttachments" value="true"/>True&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input 
    type="radio" name="includeAttachments_getFolderEntry" value="false"/>False<br/>
  <br/>
</div>
<br/>

<input type="radio" name="operation" value="folder_addFolderEntry"/> Add folder entry<br/>
<div style="padding-left:50px;">
  Binder Id <input type="text" size="20" name="binderId_addFolderEntry"/><br/>
  Definition Id <input type="text" size="40" name="definitionId_addFolderEntry" value="402883b90cc53079010cc539bf260002"/><br/>
  Entry XML<br/>
  <textarea name="entryXml" rows="8" cols="60"></textarea>
  <br/>
</div>
<br/>


<br/>
<input type="submit" name="okBtn" value="OK"/>
<input type="hidden" name="ss_access_token" value="${ss_access_token}"/>

</form>

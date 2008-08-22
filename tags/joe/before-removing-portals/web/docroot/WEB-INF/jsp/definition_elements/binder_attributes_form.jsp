<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<% //Binder attributes form %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
  		
<div class="ss_entryContent">
  <span class="ss_labelRight">${property_caption}</span><br/>
  <div class="ss_editorHints" style="padding-left:10px;"><em><ssf:nlt tag="attributes.tips"/> </em>
  		<ssf:inlineHelp tag="ihelp.other.attributes.info"/></div>
  <table cellpadding="1">
  <tbody>
    <tr><td valign="top">
    <c:set var="attributeCounter" value="0"/> <% // set counter to zero (first pass) %>
   		<c:forEach var="attributeSet" items="${ssDefinitionEntry.customAttributes[property_name].valueSet}">
  			<c:set var="attributeCounter" value="${attributeCounter + 1}"/>
   		</c:forEach>

	<c:set var="attributeCounter2" value="0"/>  <% // set counter to zero (second pass) %>
	<c:set var="column2Seen" value="0"/>
      
   <c:forEach var="attributeSet" items="${ssDefinitionEntry.customAttributes[property_name].valueSet}">
    	<c:if test="${attributeCounter2 >= (attributeCounter/2) && column2Seen == '0'}">
    		<c:set var="column2Seen" value="1"/>
    		</td><!-- end of Column 1 -->
			<!-- Start Right Column -->
      		<td valign="top">
  		</c:if>

    	
      <table  class="ss_attribute" cellpadding="2"><tbody>
        <tr>
        	<td class="ss_bold" colspan="2" align="left">${attributeSet}
          	</td></tr>
          	 <tr><td colspan="2" valign="top" class="ss_smallprint ss_bold" align="left" style="padding-bottom:3px;"><ssf:nlt tag="attributes.deleteSet"/>
          	 <input type="hidden" name="${property_name}__set__${attributeSet}" value="${attributeSet}"/>&nbsp&nbsp
        	
        	<input type="submit" class="ss_submit" name="${property_name}__delete__${attributeSet}" 
		      value="<ssf:nlt tag="button.delete" text="Delete"/>" 
		      onClick="ss_deleteAttributeSet(this, '${property_name}__delete__${attributeSet}');" /> 
        	  
		    
		     
		 
		</td></tr>
        	<tr><td class="ss_bold" colspan="2" align="left">
        <span class="ss_smallprint" align="left"><ssf:nlt tag="attributes.delete"/></span>
          	</td></tr>
			
        	<c:set var="attributes" value="${property_name}__set__${attributeSet}"/>
        	<c:forEach var="attribute" items="${ssDefinitionEntry.customAttributes[attributes].valueSet}">
        	<tr>
        	 <td width="30%" align="left"><span class="ss_fineprint" align="left" style="padding-left:5px;">${attribute}</span>
        	  </td>
        	 <td>
        	  <a href="javascript:;"
      onClick="ss_deleteAttribute(this, '${property_name}__delete__${attribute}');return false;"
    ><img border="0" valign="absmiddle" src="<html:imagesPath/>pics/1pix.gif" class="ss_generic_close"/></a> </td>
        	  </tr>
        	
        	</c:forEach>
        	<tr>
        	<td colspan="2" class="ss_smallprint ss_bold">
        	<input type="checkbox" name="${property_name}__setMultipleAllowed__${attributeSet}" 
        	  <c:set var="attributeSetMA" value="${property_name}__setMultipleAllowed__${attributeSet}"/>
        	  <c:if test="${ssDefinitionEntry.customAttributes[attributeSetMA].value}">checked</c:if> /> 
        	<input type="hidden" name="${property_name}" value="${attributeSet}"/>
        	<ssf:nlt tag="attributes.multiple"/>
        	</td>
        	<td>&nbsp;</td></tr>
        	<tr><td colspan=2 style="padding:3px;">
        	<span class="ss_fineprint ss_bold"><ssf:nlt tag="attributes.more"/></span><br/>
        	<input type="text" name="${property_name}__set__${attributeSet}" size="40"/><br/>

          </td>
        </tr>
        <tr><td colspan="2" style="padding:3px;" align="center">
        	
        	<input class="ss_submit" type="submit" name="applyBtn" value="<ssf:nlt tag="button.saveChanges"/>" />
          </td>
        </tr>
        </tbody></table>
        <c:set var="attributeCounter2" value="${attributeCounter2 + 1}"/>
        
      </c:forEach>
  	</td>
    <td valign="top">
      <span class="ss_labelAbove"><ssf:nlt tag="attributes.add"/></span>
      <input type="text" name="${property_name}" size="40"/>
      <input class="ss_submit" type="submit" name="applyBtn" value="<ssf:nlt tag="button.add"/>" />
      
      
      
    </td>
    </tr>
    </tbody>
  </table>
</div>

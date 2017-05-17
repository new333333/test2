<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page contentType="text/xml; charset=UTF-8" %>
<%@ page import="org.kablink.teaming.module.ic.RecordType" %>


<taconite-root xml:space="preserve">
	<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">
		<taconite-replace contextNodeID="${ss_recordsDivId}" parseInBrowser="true">
			<div id="${ss_recordsDivId}">
				<form id="ss_form${ss_recordsDivId}">
			
					<input type="hidden" name="entryId" value="${entryId}" />
					<input type="hidden" name="binderId" value="${binderId}" />
					
					<ssf:nlt tag="attachMeeting.meetingsHeld"/>: 
					
					<input type="button" <c:if test="${ssHeld == 1}">style="border: 1px solid black;"</c:if> value="<ssf:nlt tag="attachMeeting.past1day"/>" onclick="ss_showAttachMeetingRecords('${binderId}', '${entryId}', '${ssNamespace}', 1);" />	
					<input type="button" <c:if test="${ssHeld == 7}">style="border: 1px solid black;"</c:if> value="<ssf:nlt tag="attachMeeting.past7days"/>" onclick="ss_showAttachMeetingRecords('${binderId}', '${entryId}', '${ssNamespace}', 7);" />
					<input type="button" <c:if test="${ssHeld == 31}">style="border: 1px solid black;"</c:if> value="<ssf:nlt tag="attachMeeting.past31days"/>" onclick="ss_showAttachMeetingRecords('${binderId}', '${entryId}', '${ssNamespace}', 31);" />
						
					<table class="ssMeetingRecords" cellpadding="0" cellspacing="0">
						
						<c:forEach var="meeting" items="${ss_meeting_records}" varStatus="status">

							<c:if test="${not empty meeting.value.records}">
								
								<tr>
									<th colspan="6">
										${meeting.key[5]} (<ssf:nlt tag="attachMeeting.meetingId"/>: ${meeting.key[0]})
										<c:choose>
											<c:when test="${meeting.key[2] == 1}">
												<ssf:nlt tag="attachMeeting.instant"/>
											</c:when>
											<c:when test="${meeting.key[2] == 2}">
												<ssf:nlt tag="attachMeeting.scheduled"/>
											</c:when>								
										</c:choose>
									</th>
								</tr>
								
								<tr class="ssHeader">
									<td><ssf:nlt tag="attachMeeting.add"/></td>
									<td><ssf:nlt tag="attachMeeting.addDelete"/></td>
									<td><ssf:nlt tag="attachMeeting.meetingDate"/></td>
									<td><ssf:nlt tag="attachMeeting.typeAudio"/></td>
									<td><ssf:nlt tag="attachMeeting.typeFlash"/></td>
									<td><ssf:nlt tag="attachMeeting.typeChat"/></td>	
								</tr>				
										
								<c:forEach var="record" items="${meeting.value.records}" varStatus="status">
									<c:if test="${!empty record.value['audio'] || !empty record.value['flash'] || !empty record.value['chat']}">
										<input type="hidden" name="ssMeetingRecordId" value="${record.key}" />
										<tr>
											<td><input type="radio" name="ssMeetingRecordsOperation${record.key}" value="add" /></td>
											<td><input type="radio" name="ssMeetingRecordsOperation${record.key}" value="addAndDelete" /></td>
											<td>
												<fmt:parseDate var="ssRecordDate" value="${fn:substring(record.key, fn:indexOf(record.key, '-') + 1, fn:length(record.key))}" pattern="yyyy-MM-dd-HH-mm-ss" />
												<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
												      value="${ssRecordDate}" type="both" 
													  timeStyle="short" dateStyle="short" />
											</td>
											<td><c:if test="${!empty record.value['audio']}"><img class="ss_icon_link" src="<html:imagesPath/>pics/sym_s_checkmark.gif"/></c:if></td>
											<td><c:if test="${!empty record.value['flash']}"><img class="ss_icon_link" src="<html:imagesPath/>pics/sym_s_checkmark.gif"/></c:if></td>
											<td><c:if test="${!empty record.value['chat']}"><img class="ss_icon_link" src="<html:imagesPath/>pics/sym_s_checkmark.gif"/></c:if></td>
										</tr>
									</c:if>
								</c:forEach>
							
								<c:if test="${!empty meeting.value.docs}">
									<tr class="ssDocuments">
										<td colspan="6">
											<ul>
											<c:forEach var="doc" items="${meeting.value.docs}" varStatus="status">
												<li><input type="checkbox" name="ssMeetingDocumentId" id="ssMeetingDocumentId${meeting.key[0]}${doc[0]}" value="${meeting.key[0]}/${doc[0]}" /> <label for="ssMeetingDocumentId${meeting.key[0]}${doc[0]}">${doc[0]}</label></li>
											</c:forEach>
											</ul>
										</td>
									</tr>
								</c:if>
							
							</c:if>
							
						</c:forEach>
					
					</table>
						

					<input type="button" value="<ssf:nlt tag="__ok"/>" onclick="ss_attacheMeetingRecords('ss_form${ss_recordsDivId}', '${binderId}', '${entryId}', '${ssNamespace}')" />
					<sec:csrfInput />
				</form>
			</div>

		</taconite-replace>
	</c:if>
</taconite-root>

<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page contentType="text/xml; charset=UTF-8" %>
<%@ page import="com.sitescape.team.module.ic.RecordType" %>


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
												<% /* TODO: missing nlt so don't display it! 
													instant
												*/ %>
											</c:when>
											<c:when test="${meeting.key[2] == 2}">
												<% /* TODO: missing nlt so don't display it! 
													scheduled
												*/ %>
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
				</form>
			</div>

		</taconite-replace>
	</c:if>
</taconite-root>

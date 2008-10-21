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
package com.sitescape.team.module.ic.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.joda.time.YearMonthDay;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.ic.ICBrokerModule;
import com.sitescape.team.module.ic.ICException;
import com.sitescape.team.module.ic.RecordType;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.util.PermaLinkUtil;
import com.sitescape.team.web.WebKeys;

public class ICBrokerModuleImpl extends CommonDependencyInjection implements
		ICBrokerModule, ICBrokerModuleImplMBean, InitializingBean, DisposableBean {
	
	private final static String AUTHENTICATE_USER = "addressbk.authenticate_user";

	private final static String FIND_USERS = "addressbk.find_users_by_screenname";

	private final static String SEND_IM = "controller.send_im";

	private final static String FETCH_CONTACT = "addressbk.fetch_contact";

	private final static String REMOVE_CONTACT = "addressbk.remove_contact";

	private final static String FETCH_COMMUNITY_LIST = "addressbk.fetch_community_list";

	private final static String ADD_USER = "addressbk.add_user";

	private final static String MOD_USER = "addressbk.update_contact";

	private final static String ADD_MEETING = "controller.add_meeting_param";
	
	
	private final static String GET_RECORDING_LIST = "addressbk.get_recording_list";
	
	private final static String GET_DOC_LIST = "addressbk.get_doc_list";
	
	private final static String FIND_MEETINGS_BY_HOST = "schedule.find_meetings_by_host";
	
	private final static String REMOVE_RECORDINGS = "addressbk.remove_recordings";
		

	private String sessionId = "";

	protected String zonUrl = "";

	private HashMap communityIdCache = new HashMap(250);

	private XmlRpcClient server = null;

	// The location of our server.
	protected String adminId;

	protected String adminPasswd;

	protected String jabberDomain;

	protected String defaultCommunityId;
	
	protected boolean enable = false;

	private String[] zonFieldSet = { "username", "directoryid", "profileid",
			"type", "server", "screenname", "title", "firstname", "middlename",
			"lastname", "suffix", "company", "jobtitle", "address1",
			"address2", "state", " country", "postalcode", "email", "email2",
			"email3", "busphone", "homephone", "mobilephone", "otherphone",
			"extension", "busdirect", "homedirect", "mobiledirect",
			"otherdirect", "user1", "user2", "user3", "user4", "user5" };

	// private String [] zonMapSet =
	// {"username","directoryid","profileid","type","server","screenname","title","firstname","middlename","lastname","suffix","company","jobtitle","address1","address2","state","
	// country","postalcode","email","email2","email3","busphone","homephone","mobilephone","otherphone","extension","busdirect","homedirect","mobiledirect","otherdirect","msnscreen","yahooscreen","
	// aimscreen","user1","user2"};
	private String[] addUserParams = { "password", "communityid", "title",
			"firstname", "middlename", "lastname", "suffix", "company",
			"jobtitle", "address1", "address2", "state", "country",
			"postalcode", "screenname", "email", "email2", "email3",
			"busphone", "homephone", "mobilephone", "otherphone", "extension",
			"aimscreen", "yahooscreen", "msnscreen", "user1", "user2",
			"defphone", "defemail", "type", "profileid", "userid" };

	private String[] addUserParamDefaultValues = { "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "0", "0", "1", "", "" };

	private String[] modUserParams = { "password", "userid", "communityid",
			"directoryid", "username", "server", "title", "firstname",
			"middlename", "lastname", "suffix", "company", "jobtitle",
			"address1", "address2", "state", "country", "postalcode",
			"screenname", "oldaimscreen", "email", "email2", "email3",
			"busphone", "busdirect", "homephone", "homedirect", "mobilephone",
			"mobiledirect", "otherphone", "otherdirect", "extension",
			"msnscreen", "yahooscreen", "aimscreen", "user1", "user2", "type",
			"profileid", "adminmaxsize", "adminmaxpriority",
			"adminprioritytype", "adminenabledfeatures",
			"admindisabledfeatures" };

	private String[] modUserParamDefaultValues = { "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "1", "", "0",
			"0", "0", "0", "0" };

	/**
	 * @return Returns the adminId.
	 */
	public String getAdminId() {
		return adminId;
	}

	/**
	 * @param adminId
	 *            The adminId to set.
	 */
	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

	/**
	 * @return Returns the adminPasswd.
	 */
	public String getAdminPasswd() {
		return adminPasswd;
	}

	/**
	 * @param adminPasswd
	 *            The adminPasswd to set.
	 */
	public void setAdminPasswd(String adminPasswd) {
		this.adminPasswd = adminPasswd;
	}

	/**
	 * @return Returns the zonUrl.
	 */
	public String getZonUrl() {
		return zonUrl;
	}

	/**
	 * @param zonUrl
	 *            The zonUrl to set.
	 */
	public void setZonUrl(String zonUrl) {
		this.zonUrl = zonUrl;
	}

	/**
	 * @return Returns the jabberDomain.
	 */
	public String getJabberDomain() {
		return jabberDomain;
	}

	/**
	 * @param jabberDomain
	 *            The jabberDomain to set.
	 */
	public void setJabberDomain(String jabberDomain) {
		this.jabberDomain = jabberDomain;
	}

	/**
	 * @return Returns the defaultCommunityId.
	 */
	public String getDefaultCommunityId() {
		return defaultCommunityId;
	}

	/**
	 * @param defaultCommunityId
	 *            The defaultCommunityId to set.
	 */
	public void setDefaultCommunityId(String defaultCommunityId) {
		this.defaultCommunityId = defaultCommunityId;
	}

	public void setEnabled(boolean enable) {
		this.enable = enable;
	}
	public boolean isEnabled() {
		return enable;
	}
	public void PresenceBroker() {
		try {
			server = new XmlRpcClient(zonUrl);
		} catch (MalformedURLException e) {
			logger.error(e);
		}
	}

	public void afterPropertiesSet() {
		if (isEnabled()) {
			try {
				server = new XmlRpcClient(zonUrl);
			} catch (MalformedURLException e) {
				logger.error(e);
			}
		}
	}

	public void destroy() throws Exception {
		// Close the socket connection that you established in
		// afterPropertiesSet.
		// Do any other cleanup stuff as necessary.
	}
	
	private String findUserIdByScreenName(String screenname) throws ICException {
		if (!isEnabled()) return null;
		
		Vector result = (Vector)findUserByScreenName(screenname);
		
		// the result is buried 4 deep (vectors within vectors)
		if (result == null) return null;
		try {
			return (String) ((Vector) ((Vector) ((Vector)result).get(0))
				.get(0)).get(1);
		} catch (ArrayIndexOutOfBoundsException e) {
			// leave userId null
		}
		return null;
	}

	private Object findUserByScreenName(String screenname) throws ICException {
		// Build our parameter list.
		Vector users = new Vector();
		users.add(User.getNormalizedConferencingName(screenname));
		Vector params = new Vector();
		params.addElement(getSessionId());
		params.addElement("");
		params.addElement(users);

		// Call the server, and get our result.
		try {
			Object result = (Object) server.execute(FIND_USERS, params);
			return result;
		} catch (XmlRpcException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}

		return null;
	}

	public boolean getScreenNameExists(String screenname) throws ICException {
		if (!isEnabled()) return false;
		screenname = User.getNormalizedConferencingName(screenname);
		Object result = findUserByScreenName(screenname);
		// System.out.println("Object is " + result);
		// the result is buried 4 deep (vectors within vectors)
		if (result == null) return false;
		String screenName = (String) ((Vector) ((Vector) ((Vector) (result))
				.get(0)).get(0)).get(0);
	
		if (screenName.equalsIgnoreCase(screenname))
			return true;
		else
			return false;
	}

	private String getSessionId() throws ICException {
		if (sessionId.length() > 0) {
			return sessionId;
		}

		List result = null;
		// Build our parameter list.
		Vector params = new Vector();
		params.addElement(adminId);
		params.addElement(adminPasswd);
		params.addElement("");

		// Call the server, and get our result.
		try {
			result = (List) server.execute(AUTHENTICATE_USER, params);
		} catch (XmlRpcException e) {
			sessionId = "";
			throw new ICException(e);
		} catch (IOException e) {
			sessionId = "";
			throw new ICException(e);
		}
		sessionId = (String) result.get(0);
		return sessionId;
	}

	public void sendIm(String from, String recipient, String message) throws ICException {
		if (!isEnabled()) return;

		if (from.length() == 0)
			from = "aspen";
		from = from + "@" + jabberDomain;
		recipient = recipient + "@" + jabberDomain;

		// Build our parameter list.
		Vector params = new Vector();
		params.addElement(getSessionId());
		params.addElement(User.getNormalizedConferencingName(from));
		params.addElement(User.getNormalizedConferencingName(recipient));
		params.addElement(message);

		// Call the server, and get our result.
		try {
			server.execute(SEND_IM, params);
		} catch (XmlRpcException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	public Vector fetchContact(String screenname) throws ICException {
		if (!isEnabled()) return null;
		Object result = null;
		
		String userId = findUserIdByScreenName(screenname);

		if (userId == null) {
			return null;
		}

		// Build our parameter list.
		Vector fields = new Vector();
		for (int i = 0; i < zonFieldSet.length; i++)
			fields.add(zonFieldSet[i]);
		Vector params = new Vector();
		params.addElement(getSessionId());
		params.addElement(userId);
		params.addElement(fields);
		try {
			result = (List) server.execute(FETCH_CONTACT, params);
		} catch (XmlRpcException e) {
			logger.error(e);
			return null;
		} catch (IOException e) {
			return null;
		}
		
		Vector userInfo = (Vector) ((Vector) ((Vector) (result)).get(0)).get(0);
		userInfo.insertElementAt(userId, 0);
		return userInfo;
	}

	public int deleteUser(String screenname) throws ICException {
		if (!isEnabled()) return 0;
		Object result = null;
		String userId = findUserIdByScreenName(screenname);

		if (userId == null) {
			return 0;
		}

		Vector params = new Vector();
		params.addElement(getSessionId());
		params.addElement(userId);
		try {
			result = (List) server.execute(REMOVE_CONTACT, params);
		} catch (XmlRpcException e) {
			logger.error(e);
			return 0;
		} catch (IOException e) {
			logger.error(e);
			return 0;
		}
			
		return ((Integer) (result)).intValue();
	}

	public String getCommunityId(String communityname) throws ICException {
		if (!isEnabled()) return null;
		Object result = null;
		String name = "";
		String id = "";

		if (communityIdCache != null) {
			String commId = (String) communityIdCache.get(communityname
					.toLowerCase());
			if (commId != null)
				return commId;
		}


		Vector params = new Vector();
		params.addElement(getSessionId());
		params.addElement(new Integer(1));
		params.addElement(new Integer(9999));
		try {
			result = (List) server.execute(FETCH_COMMUNITY_LIST, params);
		} catch (XmlRpcException e) {
			logger.error(e);
			return "";
		} catch (IOException e) {
			logger.error(e);
			return "";
		}

		Vector communityIds = (Vector) ((Vector) (result)).get(0);
		for (int i = 0; i < communityIds.size(); i++) {
			id = (String) ((Vector) communityIds.elementAt(i)).get(0);
			name = (String) ((Vector) communityIds.elementAt(i)).get(1);
			communityIdCache.put(name.toLowerCase(), id);
		}

		result = (String) communityIdCache.get(communityname.toLowerCase());

		return (String) result;
	}

	public boolean addUser(HashMap adduserparams) throws ICException {
		// private String[] userParams = {"password", "communityid", "title",
		// "firstname", "middlename", "lastname", "suffix", "company",
		// "jobtitle", "address1", "address2", "state", "country", "postalcode",
		// "screenname", "email", "email2", "email3", "busphone", "homephone",
		// "mobilephone", "otherphone", "extension", "aimscreen", "yahooscreen",
		// "msnscreen", "user1", "user2", "defphone", "defemail", "type",
		// "profileid", "userid"};
		// private String[] userParamDefaultValues =
		// {"","","","","","","","","","","","","","","","","","","","","","","","","","","","","0","0","1","",""};

		if (!isEnabled()) return false;

		if (adduserparams.get("Communityid") == null) {
			adduserparams.put("Communityid", defaultCommunityId);
		}

		Vector params = new Vector();
		params.addElement(getSessionId());
		for (int i = 0; i < addUserParams.length; i++) {
			if (adduserparams.get(addUserParams[i]) == null)
				params.addElement(addUserParamDefaultValues[i]);
			else
				params.addElement(adduserparams.get(addUserParams[i]));
		}
		try {
			server.execute(ADD_USER, params);
		} catch (XmlRpcException e) {
			logger.error(e);
			return false;
		} catch (IOException e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean modUser(HashMap moduserparams) throws ICException {
		if (!isEnabled()) return false;
		if (moduserparams.get("Communityid") == null) {
			moduserparams.put("Communityid", defaultCommunityId);
		}

		Vector params = new Vector();
		params.addElement(getSessionId());
		for (int i = 0; i < modUserParams.length; i++) {
			if (moduserparams.get(modUserParams[i]) == null)
				params.addElement(modUserParamDefaultValues[i]);
			else
				params.addElement(moduserparams.get(modUserParams[i]));
		}
		try {
			server.execute(MOD_USER, params);
		} catch (XmlRpcException e) {
			logger.error(e);
			return false;
		} catch (IOException e) {
			logger.error(e);
			return false;
		}
		return true;
	}
	
	public String addMeeting(Set memberIds, Binder binder,
			Entry entry, String password, int scheduleTime, String forumToken,
			int[] meetingType)
			throws ICException {
		if (!isEnabled()) return null;
		return addMeeting(memberIds, getMeetingTitle(binder, entry),
				getMeetingDescription(binder, entry), getModelLink(binder,
						entry), password, scheduleTime, forumToken,
						meetingType);
	}

	private String getMeetingTitle(Binder binder, Entry entry) {
		if (entry != null || binder != null) {
			return NLT.get("meeting.forumMeetingTitle");
		} 
		
		// no model no meeeting title
		return "";
	}

	private String getModelLink(Binder binder, Entry entry) {

		if (entry == null) {
			return PermaLinkUtil.getPermalink(binder);
		} else if (entry != null) {
			return PermaLinkUtil.getPermalink(entry);
		} else {
			// no model no link
			return "";
		}

	}

	private String getMeetingDescription(Binder binder, Entry entry) {
		return entry != null ? entry.getTitle() : (binder != null ? binder
				.getTitle() : "");
	}

	private Vector getMeetingParticipants(Set userIds) {
		Vector participants = new Vector();

		if (userIds == null || userIds.isEmpty()) {
			return participants;
		}

		User user = RequestContextHolder.getRequestContext().getUser();
		// TODO is there accesschecking on creating meeting

		List users = getProfileDao().loadUsers(userIds, user.getZoneId());

		// Each participants list element consists of: screenName displayName
		// phone email IM system (int: 0=none, 1=aol, 2=yahoo, 3=msn) IM
		// screenName moderator (int: 0=normal, 1=moderator)

		Iterator userIt = users.iterator();
		while (userIt.hasNext()) {
			User participant = (User) userIt.next();
			Vector part = new Vector();
			part.add(User.getNormalizedConferencingName(participant.getZonName()));
			part.add(participant.getTitle());
			part.add(participant.getPhone());
			part.add(participant.getEmailAddress());
			part.add(0);
			part.add(User.getNormalizedConferencingName(participant.getZonName()));// TODO: is this right value?
			part.add(participant.equals(user) ? 1 : 0);

			participants.add(part);
		}

		return participants;
	}

	public String addMeeting(Set participantsLongIds, String title,
			String description, String message, String password,
			int scheduleTime, String forumToken, int[] meetingType) throws ICException {
		if (!isEnabled()) return null;

		// HashMap participant = null;
		// String[] participantFields = { "zonScreenName", "displayName",
		// "phone",
		// "email", "imType", "imScreenName", "moderator" };


		/*
		 * Each participants list element consists of: screenName displayName
		 * phone email IM system (int: 0=none, 1=aol, 2=yahoo, 3=msn) IM
		 * screenName moderator (int: 0=normal, 1=moderator)
		 */
		Vector params = new Vector();
		params.addElement(getSessionId());

		Vector participantArray = getMeetingParticipants(participantsLongIds);
		params.addElement(participantArray);
		params.addElement(title);
		params.addElement(description);
		params.addElement(message);
		params.addElement(password);
		params.addElement(new Integer(scheduleTime));
		params.addElement(forumToken);
		params.addElement(new Integer(meetingType[0]));
		params.addElement(new Integer(meetingType[1]));
		params.addElement(new Integer(meetingType[2]));

		List result = null;
		try {
			result = (List) server.execute(ADD_MEETING, params);
			if (result.get(0) != null && ((Integer) result.get(0)).equals(0)) {
				return (String) result.get(1);
			}
			throw new ICException((Integer) result.get(0));
		} catch (XmlRpcException e) {
			throw new ICException(e);
		} catch (IOException e) {
			throw new ICException(e);
		}
	}
	
	public Map getMeetingRecords(String meetingId) throws ICException {
		Map<String, Map<String, List>> records = new HashMap();
		
		if (!isEnabled()) {
			return records;
		}

		getSessionId();

		Vector params = new Vector();
		params.addElement(meetingId);

		try {
			List result = (List) server.execute(GET_RECORDING_LIST, params);
			if (result.get(0) != null && result.get(0) != null) {
				Iterator<List> recordsIt = ((List)result.get(0)).iterator();
				while (recordsIt.hasNext()) {
					List record = recordsIt.next();
					String recordDate = (String)record.get(0);
					int recordType = Integer.parseInt((String)record.get(1));
					
					if (records.get(recordDate) == null) {
						Map<String, List> recordsByType = new HashMap();
						RecordType[] values = RecordType.values();
						for (int i = 0; i < values.length; i++) {
							recordsByType.put(values[i].name(), new ArrayList());
						}
						
						records.put(recordDate, recordsByType);
					}
					records.get(recordDate).get(RecordType.getByNumber(recordType).name()).add(record);
				}
			} else {
				throw new ICException((Integer) result.get(0));
			}
		} catch (XmlRpcException e) {
			throw new ICException(e);
		} catch (IOException e) {
			throw new ICException(e);
		}
	
		return records;
	}
	
	public List getDocumentList(String meetingId) throws ICException {
		List documents = new ArrayList();
		
		if (!isEnabled()) {
			return documents;
		}
		
		getSessionId();

		Vector params = new Vector();
		params.addElement(meetingId);

		try {
			List result = (List) server.execute(GET_DOC_LIST, params);
			if (result.get(0) != null && result.get(0) != null) {
				Iterator<List> documentsIt = ((List)result.get(0)).iterator();
				while (documentsIt.hasNext()) {
					List document = documentsIt.next();
					String docId = (String)document.get(0);
					
					// I don't know what does it mean, copied from SSF implementation, no spec.
					if (docId.length() >= 17 && docId.substring(0, 17).equals("%5eTempoUpload%5e")) {
						continue;
					}
				
					documents.add(document);
				}
				
			} else {
				throw new ICException((Integer) result.get(0));
			}
		} catch (XmlRpcException e) {
			throw new ICException(e);
		} catch (IOException e) {
			throw new ICException(e);
		}
	
		return documents;
	}
	
	public List findUserMeetings(String screenName) throws ICException {
		List meetings = new ArrayList();
		
		if (!isEnabled()) {
			return meetings;
		}
		
		String userId = findUserIdByScreenName(screenName);
		if (userId == null) {
			return meetings;
		}
		
		Vector params = new Vector();
		params.addElement(getSessionId());
		params.addElement(userId);

		List result = null;
		try {
			result = (List) server.execute(FIND_MEETINGS_BY_HOST, params);
			if (result.get(0) != null) {
				meetings = (List)result.get(0);
			} else {
				throw new ICException((Integer) result.get(0));
			}
		} catch (XmlRpcException e) {
			throw new ICException(e);
		} catch (IOException e) {
			throw new ICException(e);
		}
		
	
		return meetings;
	}
	
	public Map getUserMeetingAttachments(String screenName, int held) throws ICException {
		Map meetingAttachments = new HashMap();
		Iterator<List> meetings = findUserMeetings(screenName).iterator();
		while (meetings.hasNext()) {
			List meeting = meetings.next();
			String meetingId = (String)meeting.get(0);
			Map records = getMeetingRecords(meetingId);
			if (held > 0) {
				DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd-HH-mm-ss");
				YearMonthDay range = (new YearMonthDay()).minusDays(held);
				Iterator<Map.Entry<String, Map<String, List>>> it = records.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, Map<String, List>> entry = it.next();
					String id = entry.getKey();
					Map<String, List> record = entry.getValue();
					
					YearMonthDay recordDate = fmt.parseDateTime(id.substring(id.indexOf("-") + 1, id.length())).toYearMonthDay();
					if (recordDate.isBefore(range)) {
						it.remove();
						continue;
					}
					
					if (record.get(RecordType.audio.name()).isEmpty() && 
							record.get(RecordType.flash.name()).isEmpty() && 
							record.get(RecordType.chat.name()).isEmpty()) {
						it.remove();
					}
				}
			}
			
			List docs = getDocumentList(meetingId);
			
			Iterator<List> docsIt = docs.iterator();
			while (docsIt.hasNext()) {
				List document = docsIt.next();
				String docId = (String)document.get(0);	
				if (docId.indexOf("/") > -1) {
					docsIt.remove();
				}
			}
			
			Map rpd = new HashMap();
			rpd.put("records", records);
			rpd.put("docs", docs);
			
			meetingAttachments.put(meeting, rpd);
		}
		return meetingAttachments;
	}
	
	public boolean removeRecordings(String meetingId, String recordingURL) throws ICException {
		if (!isEnabled()) {
			return false;
		}
	
		Vector params = new Vector();
		params.addElement(meetingId);
		params.addElement(recordingURL);

		try {
			server.execute(REMOVE_RECORDINGS, params);
			return true;
		} catch (XmlRpcException e) {
			throw new ICException(e);
		} catch (IOException e) {
			throw new ICException(e);
		}
	}

	public String getBASE64AuthorizationToken() {
		return new sun.misc.BASE64Encoder().encode ((getAdminId() + ":" + getAdminPasswd()).getBytes());
	}
	
	
}

package com.sitescape.team.ic.impl;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import javax.portlet.ActionResponse;
import javax.portlet.RenderResponse;

import org.apache.xmlrpc.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.User;
import com.sitescape.team.ic.ICBroker;
import com.sitescape.team.ic.ICException;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.web.WebKeys;

public class ICBrokerImpl extends CommonDependencyInjection implements
		ICBroker, InitializingBean, DisposableBean {
	private final static String AUTHENTICATE_USER = "addressbk.authenticate_user";

	private final static String FIND_USERS = "addressbk.find_users_by_screenname";

	private final static String SEND_IM = "controller.send_im";

	private final static String FETCH_CONTACT = "addressbk.fetch_contact";

	private final static String REMOVE_CONTACT = "addressbk.remove_contact";

	private final static String FETCH_COMMUNITY_LIST = "addressbk.fetch_community_list";

	private final static String ADD_USER = "addressbk.add_user";

	private final static String MOD_USER = "addressbk.update_contact";

	private final static String ADD_MEETING = "controller.add_meeting_param";

	private String sessionId = "";

	protected String zonUrl = "";

	private HashMap communityIdCache = new HashMap(250);

	private XmlRpcClient server = null;

	// The location of our server.
	protected String adminId;

	protected String adminPasswd;

	protected String jabberDomain;

	protected String defaultCommunityId;

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

	public void PresenceBroker() {
		try {
			server = new XmlRpcClient(zonUrl);
		} catch (Exception e) {
		}
	}

	public void afterPropertiesSet() throws Exception {
		try {
			server = new XmlRpcClient(zonUrl);
		} catch (Exception e) {
		}
	}

	public void destroy() throws Exception {

		// Close the socket connection that you established in
		// afterPropertiesSet.
		// Do any other cleanup stuff as necessary.
	}

	private Object findScreenName(String screenname) {

		if (sessionId.length() <= 0) {
			try {
				getSessionId();
			} catch (Exception e) {
				System.out.println("PresenceBroker: Failed to get sessionId");
			}
		}

		// Build our parameter list.
		Vector users = new Vector();
		users.add(screenname);
		Vector params = new Vector();
		params.addElement(sessionId);
		params.addElement("");
		params.addElement(users);

		// Call the server, and get our result.
		try {

			Object result = (Object) server.execute(FIND_USERS, params);
			// System.out.println("Object is " + result);
			// the result is buried 4 deep (vectors within vectors)
			return result;
		} catch (XmlRpcException exception) {
			// System.err.println("JavaClient: XML-RPC Fault #" +
			// Integer.toString(exception.code) + ": " +
			// exception.toString());
		} catch (Exception exception) {
			// System.err.println("JavaClient: " + exception.toString());
		}
		return null;
	}

	public boolean getScreenNameExists(String screenname) {

		try {

			Object result = findScreenName(screenname);
			// System.out.println("Object is " + result);
			// the result is buried 4 deep (vectors within vectors)

			String screenName = (String) ((Vector) ((Vector) ((Vector) (result))
					.get(0)).get(0)).get(0);

			if (screenName.equalsIgnoreCase(screenname))
				return true;
			else
				return false;
		} catch (Exception exception) {
			// System.err.println("JavaClient: " + exception.toString());
		}
		return false;
	}

	private void getSessionId() throws ICException {
		if (sessionId.length() > 0)
			return;

		List result = null;
		// Build our parameter list.
		Vector params = new Vector();
		params.addElement(adminId);
		params.addElement(adminPasswd);
		params.addElement("");

		// Call the server, and get our result.
		try {

			result = (List) server.execute(AUTHENTICATE_USER, params);
			// System.out.println("Object is " + result);

		} catch (XmlRpcException e) {
			// System.err.println("JavaClient: XML-RPC Fault #" +
			// Integer.toString(exception.code) + ": " +
			// exception.toString());
			sessionId = "";
			throw new ICException(e);
		} catch (IOException e) {
			// System.err.println("JavaClient: " + exception.toString());
			sessionId = "";
			throw new ICException(e);
		}
		sessionId = (String) result.get(0);
	}

	public void sendIm(String from, String recipient, String message) {
		List result = null;

		if (sessionId.length() <= 0) {
			try {
				getSessionId();
			} catch (Exception e) {
				System.out.println("PresenceBroker: Failed to get sessionId");
			}
		}
		if (from.length() == 0)
			from = "aspen";
		from = from + "@" + jabberDomain;
		recipient = recipient + "@" + jabberDomain;

		// Build our parameter list.
		Vector params = new Vector();
		params.addElement(sessionId);
		params.addElement(from);
		params.addElement(recipient);
		params.addElement(message);

		// Call the server, and get our result.
		try {
			result = (List) server.execute(SEND_IM, params);
			// System.out.println("Object is " + result);

		} catch (XmlRpcException exception) {
			// System.err.println("JavaClient: XML-RPC Fault #" +
			// Integer.toString(exception.code) + ": " +
			// exception.toString());
		} catch (Exception exception) {
			// System.err.println("JavaClient: " + exception.toString());
		}
	}

	public Vector fetchContact(String screenname) {
		Object result = null;
		String userId = "";
		if (sessionId.length() <= 0) {
			try {
				getSessionId();
			} catch (Exception e) {
				System.out.println("PresenceBroker: Failed to get sessionId");
			}
		}
		try {

			result = findScreenName(screenname);
			// System.out.println("Object is " + result);
			// the result is buried 4 deep (vectors within vectors)

			userId = (String) ((Vector) ((Vector) ((Vector) (result)).get(0))
					.get(0)).get(1);
		} catch (Exception e) {
			return null;
		}

		if (userId.length() == 0)
			return null;

		// Build our parameter list.
		Vector fields = new Vector();
		for (int i = 0; i < zonFieldSet.length; i++)
			fields.add(zonFieldSet[i]);
		Vector params = new Vector();
		params.addElement(sessionId);
		params.addElement(userId);
		params.addElement(fields);
		try {
			result = (List) server.execute(FETCH_CONTACT, params);
			// System.out.println("Object is " + result);

		} catch (XmlRpcException exception) {
			// System.err.println("JavaClient: XML-RPC Fault #" +
			// Integer.toString(exception.code) + ": " +
			// exception.toString());
			return null;
		} catch (Exception exception) {
			// System.err.println("JavaClient: " + exception.toString());
			return null;
		}
		Vector userInfo = (Vector) ((Vector) ((Vector) (result)).get(0)).get(0);
		userInfo.insertElementAt(userId, 0);
		return userInfo;
	}

	public int deleteUser(String screenname) {
		Object result = null;
		String userId = "";

		if (sessionId.length() <= 0) {
			try {
				getSessionId();
			} catch (Exception e) {
				System.out.println("PresenceBroker: Failed to get sessionId");
			}
		}

		try {
			result = findScreenName(screenname);
			// System.out.println("Object is " + result);
			// the result is buried 4 deep (vectors within vectors)

			userId = (String) ((Vector) ((Vector) ((Vector) (result)).get(0))
					.get(0)).get(1);
		} catch (Exception e) {
			return 0;
		}

		if (userId.length() == 0)
			return 0;

		Vector params = new Vector();
		params.addElement(sessionId);
		params.addElement(userId);
		try {
			result = (List) server.execute(REMOVE_CONTACT, params);
			// System.out.println("Object is " + result);

		} catch (XmlRpcException exception) {
			// System.err.println("JavaClient: XML-RPC Fault #" +
			// Integer.toString(exception.code) + ": " +
			// exception.toString());
			return 0;
		} catch (Exception exception) {
			// System.err.println("JavaClient: " + exception.toString());
			return 0;
		}
		return ((Integer) (result)).intValue();
	}

	public String getCommunityId(String communityname) {
		Object result = null;
		String name = "";
		String id = "";

		if (communityIdCache != null) {
			String commId = (String) communityIdCache.get(communityname
					.toLowerCase());
			if (commId != null)
				return commId;
		}

		if (sessionId.length() <= 0) {
			try {
				getSessionId();
			} catch (Exception e) {
				// System.out.println("PresenceBroker: Failed to get
				// sessionId");
				return "";
			}
		}

		Vector params = new Vector();
		params.addElement(sessionId);
		params.addElement(new Integer(1));
		params.addElement(new Integer(9999));
		try {
			result = (List) server.execute(FETCH_COMMUNITY_LIST, params);
			// System.out.println("Object is " + result);

		} catch (XmlRpcException exception) {
			// System.err.println("JavaClient: XML-RPC Fault #" +
			// Integer.toString(exception.code) + ": " +
			// exception.toString());
			return "";
		} catch (Exception exception) {
			// System.err.println("JavaClient: " + exception.toString());
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

	public boolean addUser(HashMap adduserparams) {
		// private String[] userParams = {"password", "communityid", "title",
		// "firstname", "middlename", "lastname", "suffix", "company",
		// "jobtitle", "address1", "address2", "state", "country", "postalcode",
		// "screenname", "email", "email2", "email3", "busphone", "homephone",
		// "mobilephone", "otherphone", "extension", "aimscreen", "yahooscreen",
		// "msnscreen", "user1", "user2", "defphone", "defemail", "type",
		// "profileid", "userid"};
		// private String[] userParamDefaultValues =
		// {"","","","","","","","","","","","","","","","","","","","","","","","","","","","","0","0","1","",""};

		Object result = null;

		if (sessionId.length() <= 0) {
			try {
				getSessionId();
			} catch (Exception e) {
				// System.out.println("PresenceBroker: Failed to get
				// sessionId");
				return false;
			}
		}

		if (adduserparams.get("Communityid") == null) {
			adduserparams.put("Communityid", defaultCommunityId);
		}

		Vector params = new Vector();
		params.addElement(sessionId);
		for (int i = 0; i < addUserParams.length; i++) {
			if (adduserparams.get(addUserParams[i]) == null)
				params.addElement(addUserParamDefaultValues[i]);
			else
				params.addElement(adduserparams.get(addUserParams[i]));
		}
		try {
			result = (List) server.execute(ADD_USER, params);
			// System.out.println("Object is " + result);

		} catch (XmlRpcException exception) {
			// System.err.println("JavaClient: XML-RPC Fault #" +
			// Integer.toString(exception.code) + ": " +
			// exception.toString());
			return false;
		} catch (Exception exception) {
			// System.err.println("JavaClient: " + exception.toString());
			return false;

		}
		return true;
	}

	public boolean modUser(HashMap moduserparams) {

		Object result = null;

		if (sessionId.length() <= 0) {
			try {
				getSessionId();
			} catch (Exception e) {
				// System.out.println("PresenceBroker: Failed to get
				// sessionId");
				return false;
			}
		}

		if (moduserparams.get("Communityid") == null) {
			moduserparams.put("Communityid", defaultCommunityId);
		}

		Vector params = new Vector();
		params.addElement(sessionId);
		for (int i = 0; i < modUserParams.length; i++) {
			if (moduserparams.get(modUserParams[i]) == null)
				params.addElement(modUserParamDefaultValues[i]);
			else
				params.addElement(moduserparams.get(modUserParams[i]));
		}
		try {
			result = (List) server.execute(MOD_USER, params);
			// System.out.println("Object is " + result);

		} catch (XmlRpcException exception) {
			// System.err.println("JavaClient: XML-RPC Fault #" +
			// Integer.toString(exception.code) + ": " +
			// exception.toString());
			return false;
		} catch (Exception exception) {
			// System.err.println("JavaClient: " + exception.toString());
			return false;

		}
		return true;
	}

	public String addMeeting(Set participantsLongIds, String title,
			String description, String message, String password,
			int scheduleTime, String forumToken, int forumOptions,
			int optionMaskSet, int optionMaskClear) throws ICException {

		// HashMap participant = null;
		// String[] participantFields = { "zonScreenName", "displayName",
		// "phone",
		// "email", "imType", "imScreenName", "moderator" };

		getSessionId();

		/*
		 * Each participants list element consists of: screenName displayName
		 * phone email IM system (int: 0=none, 1=aol, 2=yahoo, 3=msn) IM
		 * screenName moderator (int: 0=normal, 1=moderator)
		 */
		Vector params = new Vector();
		params.addElement(sessionId);

		Vector participantArray = getMeetingParticipants(participantsLongIds);
		// Vector part = new Vector();
		//
		// for (int i = 0; i < participants.size(); i++) {
		// participant = (HashMap) participants.get(i);
		// for (int j = 0; j < participantFields.length; j++) {
		// part.addElement(participant.get(participantFields[j]));
		// }
		// participantArray.addElement(part);
		//
		// }
		params.addElement(participantArray);
		params.addElement(title);
		params.addElement(description);
		params.addElement(message);
		params.addElement(password);
		params.addElement(new Integer(scheduleTime));
		params.addElement(forumToken);
		params.addElement(new Integer(forumOptions));
		params.addElement(new Integer(optionMaskSet));
		params.addElement(new Integer(optionMaskClear));

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

	public String addMeeting(Set memberIds, String title, Binder binder,
			Entry entry, String password, int scheduleTime, String forumToken,
			int forumOptions, int optionMaskSet, int optionMaskClear)
			throws ICException {
		return addMeeting(memberIds, title,
				getMeetingDescription(binder, entry), getModelLink(binder,
						entry), password, scheduleTime, forumToken,
				forumOptions, optionMaskSet, optionMaskClear);
	}

	private String getModelLink(Binder binder, Entry entry) {
		AdaptedPortletURL adapterUrl = AdaptedPortletURL
				.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);

		if (entry == null && binder != null) {
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binder.getId()
					.toString());
			adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, binder
					.getEntityType().toString());
		} else if (entry != null && binder != null) {
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binder.getId()
					.toString());
			adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entry.getId()
					.toString());
			adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, entry
					.getEntityType().toString());
		} else {
			// no model no link
			return "";
		}

		return adapterUrl.toString();
	}

	private String getMeetingDescription(Binder binder, Entry entry) {
		return entry != null ? entry.getTitle() : (binder != null ? binder
				.getTitle() : "");
	}

	private Vector getMeetingParticipants(Set userIds) {
		Vector participants = new Vector();

		if (userIds == null) {
			return participants;
		}

		User user = RequestContextHolder.getRequestContext().getUser();
		// TODO is there accesschecking on creating meeting

		List users = getCoreDao().loadObjects(userIds, User.class,
				user.getZoneId());

		// * Each participants list element consists of: screenName displayName
		// * phone email IM system (int: 0=none, 1=aol, 2=yahoo, 3=msn) IM
		// * screenName moderator (int: 0=normal, 1=moderator)

		Iterator userIt = users.iterator();
		while (userIt.hasNext()) {
			User participant = (User) userIt.next();
			Vector part = new Vector();
			part.add(participant.getZonName());
			part.add(participant.getTitle());
			part.add(participant.getPhone());
			part.add(participant.getEmailAddress());
			part.add(0);
			part.add(participant.getZonName());// TODO: is this right value?
			part.add(participant.equals(user) ? 1 : 0);

			participants.add(part);
		}

		return participants;
	}

}

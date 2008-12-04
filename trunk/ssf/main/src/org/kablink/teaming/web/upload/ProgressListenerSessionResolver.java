package org.kablink.teaming.web.upload;

import javax.portlet.PortletSession;

public class ProgressListenerSessionResolver {

	public static FileUploadProgressListener get(
			PortletSession session, String uid) {
		return (FileUploadProgressListener) session
				.getAttribute(getSessionKey(uid));
	}

	public static void set(PortletSession session, String uid,
			FileUploadProgressListener fListener) {
		session.setAttribute(getSessionKey(uid), fListener);
	}

	public static void remove(PortletSession session, String uid) {
		session.removeAttribute(getSessionKey(uid));
	}

	protected static String getSessionKey(String uid) {
		return "ssProgressListener_" + uid;
	}

}

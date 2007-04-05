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

package com.sitescape.team.portlet.forum;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionBindingEvent;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Date;

import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.HistoryMap;
import com.sitescape.team.domain.UserPerFolderPK;
import com.sitescape.team.module.profile.ProfileModule;
import com.sitescape.team.util.SessionUtil;
import com.sitescape.team.util.SpringContextUtil;
/**
 * @author Janet McCann
 *
 */

public class HistoryCache implements HttpSessionBindingListener  {
	protected Date lastFlush;
	protected ProfileModule profileModule;
	protected SessionFactory sessionFactory;
	private HistoryMap history;
	public HistoryCache(HistoryMap history) {
    	profileModule = (ProfileModule)SpringContextUtil.getBean("profileModule");
    	sessionFactory = (SessionFactory)SpringContextUtil.getBean("sessionFactory");
    	lastFlush = new Date();
    	this.history = history;
	}
	public void valueBound(HttpSessionBindingEvent event) {
	}
	
	public void valueUnbound(HttpSessionBindingEvent event) {
		//need to flush history into database
		//open shared session
/*		try {
			if (SessionUtil.sessionActive()) {
				try {
					profileModule.updateUserHistory(history);
				} finally {}
			} else {
				try {
					SessionUtil.sessionStartup();
					profileModule.updateUserHistory(history);
				} finally {
					SessionUtil.sessionStop();
				}
			} 
		} catch (Exception e) {}
*/
		history = null;
	}
	public UserPerFolderPK getId() {
		return history.getId();
	}
	public void flush() {
		if (history != null) {
//			profileModule.updateUserHistory(history);
			lastFlush = new Date();
		}
	}
    public void sortAndSetHistorySeen(Entry entry) {
    	history.sortMap();
    	setHistorySeen(entry);
	}
    public void setHistorySeen(Entry entry) {
     	Date now = new Date();
     	history.setSeen(entry, now);
       	if (now.getTime() - lastFlush.getTime() > 5*60*1000) {
//			profileModule.updateUserHistory(history);
			lastFlush = now;
		}
	}
    public void setHistorySeenInPlace(Entry entry) {
     	Date now = new Date();
     	history.setSeenInPlace((Entry) entry, now);
       	if (now.getTime() - lastFlush.getTime() > 5*60*1000) {
//			profileModule.updateUserHistory(history);
			lastFlush = now;
		}
	}
    public HistoryMap getHistory() {
    	return history;
    }
}

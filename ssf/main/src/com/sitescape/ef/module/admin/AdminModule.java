/*
 * Created on Dec 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.module.admin;

import java.util.Map;
import java.util.Set;

import com.sitescape.ef.domain.NotificationDef;

/**
 * @author Janet McCann
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface AdminModule {

    public void updateNotification(Long forumId, Map updates, Set users); 
    public void disableNotification(Long forumId);
    public void enableNotification(Long forumId);

}
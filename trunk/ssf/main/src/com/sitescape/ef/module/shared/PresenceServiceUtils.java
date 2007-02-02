package com.sitescape.ef.module.shared;



import com.sitescape.ef.presence.PresenceBroker;
import com.sitescape.ef.presence.PresenceService;
import com.sitescape.team.domain.User;
import com.sitescape.team.util.SpringContextUtil;

/**
 *
 * @author Roy Klein
 */
public class PresenceServiceUtils {
    
    
    public static int getPresence(User user) {
    	PresenceService ps = (PresenceService)SpringContextUtil.getBean("presenceService");
    	return ps.getPresenceInfo(user);
    }
    public static int getPresence(String user) {
    	PresenceService ps = (PresenceService)SpringContextUtil.getBean("presenceService");
    	return ps.getPresenceInfo(user);
    }
    public static boolean getScreenNameExists(String zonname) {
    	PresenceBroker pb = (PresenceBroker)SpringContextUtil.getBean("presenceBroker");
    	return pb.getScreenNameExists(zonname);   	
    }
}

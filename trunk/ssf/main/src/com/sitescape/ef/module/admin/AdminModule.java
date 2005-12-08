/*
 * Created on Dec 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.module.admin;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sitescape.ef.jobs.Schedule;
import com.sitescape.ef.jobs.ScheduleInfo;
import com.sitescape.ef.security.function.Function;

/**
 * @author Janet McCann
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface AdminModule {

	public void addFunction(Function function);
    public List getFunctions();
    public void modifyFunction(Long functionId, Map updates);
    public void modifyNotification(Long binderId, Map updates, Set users); 
    public void disableNotification(Long binderId);
    public void enableNotification(Long binderId);
    public void setEnablePostings(boolean enable);
    public PostingConfig getPostingConfig();
    public void setPostingConfig(PostingConfig postingConfig) throws ParseException;
    public List getPostingDefs();

}
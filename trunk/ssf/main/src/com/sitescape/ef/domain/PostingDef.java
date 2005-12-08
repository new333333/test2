package com.sitescape.ef.domain;

import java.util.List;

/**
 * @author Jong Kim
 *
 */
public class PostingDef extends PersistentObject {
    public static final int RETURN_TO_SENDER = 1; // default
    public static final int POST_AS_A_NEW_TOPIC = 2;
    public static final int TRY_AGAIN_LATER = 3;
    
    private List emailAliases;
    private int replyPostingOption = RETURN_TO_SENDER;
    
    public List getEmailAliases() {
        return emailAliases;
    }
    public void setEmailAliases(List emailAliases) {
        this.emailAliases = emailAliases;
    }
    public int getReplyPostingOption() {
        return replyPostingOption;
    }
    public void setReplyPostingOption(int replyPostingOption) {
    	if ((replyPostingOption != RETURN_TO_SENDER) &&
    		(replyPostingOption != POST_AS_A_NEW_TOPIC) &&
    		(replyPostingOption != TRY_AGAIN_LATER)) throw new IllegalArgumentException("replyPostingOption");
        this.replyPostingOption = replyPostingOption;
    }
}

package com.sitescape.team.domain;

import java.util.Date;

import com.sitescape.team.domain.HistoryStamp;

/**
 * @author janet
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PersistentLongIdTimestampObject extends PersistentLongIdObject 
	implements PersistentLongIdTimestamp {
    protected HistoryStamp creation;
    protected HistoryStamp modification;
    /**
     * @hibernate.component class="com.sitescape.team.domain.HistoryStamp" prefix="creation_" 
     */
    public HistoryStamp getCreation() {
        return this.creation;
    }
    public void setCreation(HistoryStamp stamp) {
        this.creation = stamp;
    }
    /**
     * @hibernate.component class="com.sitescape.team.domain.HistoryStamp" prefix="modification_"
     */
    public HistoryStamp getModification() {
        return this.modification;
    }
    public void setModification(HistoryStamp stamp) {
        this.modification = stamp;
    }
 }

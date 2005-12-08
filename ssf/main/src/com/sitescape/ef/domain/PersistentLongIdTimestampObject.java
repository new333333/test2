package com.sitescape.ef.domain;

import java.util.Date;

import com.sitescape.ef.domain.HistoryStamp;
/*
 * Created on Oct 21, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

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
     * @hibernate.component class="com.sitescape.ef.domain.HistoryStamp" prefix="creation_" node="creation"
     */
    public HistoryStamp getCreation() {
        return this.creation;
    }
    public void setCreation(HistoryStamp stamp) {
        this.creation = stamp;
    }
    /**
     * @hibernate.component class="com.sitescape.ef.domain.HistoryStamp" prefix="modification_" node="modification"
     */
    public HistoryStamp getModification() {
        return this.modification;
    }
    public void setModification(HistoryStamp stamp) {
        this.modification = stamp;
    }
 }

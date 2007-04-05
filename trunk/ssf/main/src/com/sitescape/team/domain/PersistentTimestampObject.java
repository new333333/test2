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
/*
 * Created on Sep 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.team.domain;


/**
 * @author janet
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PersistentTimestampObject extends PersistentObject 
	implements PersistentTimestamp {
    protected HistoryStamp creation;
    protected HistoryStamp modification;
    /**
     * @hibernate.component class="com.sitescape.team.domain.HistoryStamp" prefix="creation_"
     */
    public HistoryStamp getCreation() {
        return this.creation;
    }
    public void setCreation(HistoryStamp stamp) {
        this.creation = (HistoryStamp) stamp;
    }
    /**
     * @hibernate.component class="com.sitescape.team.domain.HistoryStamp" prefix="modification_"
     */
    public HistoryStamp getModification() {
        return this.modification;
    }
    public void setModification(HistoryStamp stamp) {
        this.modification = (HistoryStamp) stamp;
    }
 }

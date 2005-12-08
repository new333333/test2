package com.sitescape.ef.domain;


/**
 * @author Jong Kim
 *
 */
public interface TimestampSupport {
    public HistoryStamp getCreation();
    public void setCreation(HistoryStamp stamp);
    public HistoryStamp getModification();
    public void setModification(HistoryStamp stamp);
}

package com.sitescape.ef.domain;

public interface Reservable extends PersistentLongId {

	public HistoryStamp getReservation();

    public void setReservation(HistoryStamp reservation);
    
    public void setReservation(User owner);    
    
    public void setLockedFileCount(int lockedFileCount);
    
    public int getLockedFileCount();
    
    public void incrLockedFileCount();
    
    public void decrLockedFileCount();
}

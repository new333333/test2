package com.sitescape.team.domain;

public interface Reservable extends PersistentLongId {

	public HistoryStamp getReservation();

    public void setReservation(HistoryStamp reservation);
    
    public void setReservation(User owner);    
    
    public void clearReservation();
}

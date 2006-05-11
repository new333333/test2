package com.sitescape.ef.domain;

public interface Reservable {

	public HistoryStamp getReservation();

    public void setReservation(HistoryStamp reservation);
    
    public void setReservation(User owner);    
}

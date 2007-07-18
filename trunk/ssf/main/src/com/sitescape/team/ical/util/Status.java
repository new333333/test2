package com.sitescape.team.ical.util;

public enum Status {

	s1, s2, s3, s4;
	
	public net.fortuna.ical4j.model.property.Status toIcalStatus() {
		if (this.equals(s1)) {
			return new net.fortuna.ical4j.model.property.Status("NEEDS-ACTION");
		} else if (this.equals(s2)) {
			return new net.fortuna.ical4j.model.property.Status("IN-PROCESS");
		} else if (this.equals(s3)) {
			return new net.fortuna.ical4j.model.property.Status("COMPLETED");
		} else if (this.equals(s4)) {
			return new net.fortuna.ical4j.model.property.Status("CANCELLED");
		}
		return null;
	}
	
	public static Status fromIcalStatus(net.fortuna.ical4j.model.property.Status status) {
		if (status == null) {
			return null;
		}
		
		if ("NEEDS-ACTION".equals(status.getValue())) {
			return s1;
		} else if ("IN-PROCESS".equals(status.getValue())) {
			return s2;
		} else if ("COMPLETED".equals(status.getValue())) {
			return s3;
		} else if ("CANCELLED".equals(status.getValue())) {
			return s4;
		}
		
		return null;
	}
	
}

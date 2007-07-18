package com.sitescape.team.ical.util;

public enum Priority {

	p1, p2, p3, p4, p5;
	
	public net.fortuna.ical4j.model.property.Priority toIcalPriority() {
		if (this.equals(p1)) {
			return new net.fortuna.ical4j.model.property.Priority(1);
		} else if (this.equals(p2)) {
			return new net.fortuna.ical4j.model.property.Priority(2);
		} else if (this.equals(p3)) {
			return new net.fortuna.ical4j.model.property.Priority(5);
		} else if (this.equals(p4)) {
			return new net.fortuna.ical4j.model.property.Priority(8);
		} else if (this.equals(p5)) {
			return new net.fortuna.ical4j.model.property.Priority(9);
		}
		return null;
	}
	
	public static Priority fromIcalPriority(net.fortuna.ical4j.model.property.Priority priority) {
		if (priority == null) {
			return null;
		}
		
		if ("1".equals(priority.getValue())) {
			return p1;
		} else if ("2".equals(priority.getValue()) || 
					"3".equals(priority.getValue()) || 
					"4".equals(priority.getValue())) {
			return p2;
		} else if ("5".equals(priority.getValue())) {
			return p3;
		} else if ("6".equals(priority.getValue()) || 
					"7".equals(priority.getValue()) || 
					"8".equals(priority.getValue())) {
			return p4;
		} else if ("9".equals(priority.getValue())) {
			return p5;
		}
		
		return null;
	}
	
}

package com.sitescape.team.ical.util;

public enum PercentComplete {

	c000, c010, c020, c030, c040, c050, c060, c070, c080, c090, c100;
	
	public net.fortuna.ical4j.model.property.PercentComplete toIcalPercentComplete() {
		if (this.equals(c000)) {
			return new net.fortuna.ical4j.model.property.PercentComplete(0);
		} else if (this.equals(c010)) {
			return new net.fortuna.ical4j.model.property.PercentComplete(10);
		} else if (this.equals(c020)) {
			return new net.fortuna.ical4j.model.property.PercentComplete(20);
		} else if (this.equals(c030)) {
			return new net.fortuna.ical4j.model.property.PercentComplete(30);
		} else if (this.equals(c040)) {
			return new net.fortuna.ical4j.model.property.PercentComplete(40);
		} else if (this.equals(c050)) {
			return new net.fortuna.ical4j.model.property.PercentComplete(50);
		} else if (this.equals(c060)) {
			return new net.fortuna.ical4j.model.property.PercentComplete(60);
		} else if (this.equals(c070)) {
			return new net.fortuna.ical4j.model.property.PercentComplete(70);
		} else if (this.equals(c080)) {
			return new net.fortuna.ical4j.model.property.PercentComplete(80);
		} else if (this.equals(c090)) {
			return new net.fortuna.ical4j.model.property.PercentComplete(90);
		} else if (this.equals(c100)) {
			return new net.fortuna.ical4j.model.property.PercentComplete(100);
		}
		return null;
	}
	
	public static PercentComplete fromIcalPercentComplete(net.fortuna.ical4j.model.property.PercentComplete percentComplete) {
		if (percentComplete == null) {
			return null;
		}
		
		if (percentComplete.getPercentage() == 0) {
			return c000;
		} else if (percentComplete.getPercentage() == 10) {
			return c010;
		} else if (percentComplete.getPercentage() == 20) {
			return c020;
		} else if (percentComplete.getPercentage() == 30) {
			return c030;
		} else if (percentComplete.getPercentage() == 40) {
			return c040;
		} else if (percentComplete.getPercentage() == 50) {
			return c050;
		} else if (percentComplete.getPercentage() == 60) {
			return c060;
		} else if (percentComplete.getPercentage() == 70) {
			return c070;
		} else if (percentComplete.getPercentage() == 80) {
			return c080;
		} else if (percentComplete.getPercentage() == 90) {
			return c090;
		} else if (percentComplete.getPercentage() == 100) {
			return c100;
		}
		
		return null;
	}
	
}

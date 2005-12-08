package com.sitescape.ef.module.definition.notify;
import java.util.Locale;
import java.text.DateFormat;

public class Notify {
	private boolean full=false;
	private int summaryLines;
	private Locale locale;
	private DateFormat dateFormat;
	
	public boolean isFull() {
		return full;
	}
	public void setFull(boolean full) {
		this.full = full;
	}
	public boolean isSummary() {
		return !full;
	}
	public void setSummary(boolean full) {
		this.full = !full;
	}
	public int getSummaryLines() {
		return summaryLines;
	}
	public void setSummaryLines(int summaryLines) {
		this.summaryLines = summaryLines;
	}
	public Locale getLocale() {
		return this.locale;
	}
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	public DateFormat getDateFormat() {
		if (dateFormat == null) dateFormat=DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.FULL);
		return dateFormat;
	}
	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}
}

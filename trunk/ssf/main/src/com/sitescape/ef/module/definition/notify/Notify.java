package com.sitescape.ef.module.definition.notify;
import java.util.Locale;
import java.util.Set;
import java.util.HashSet;
import java.text.DateFormat;
import java.util.Date;
import java.sql.Timestamp;
import com.sitescape.ef.domain.FileAttachment;

public class Notify {
	public static final String FULL="full";
	public static final String SUMMARY="summary";
	
	protected boolean full=false;
	protected Locale locale;
	protected DateFormat dateFormat;
	protected HashSet files= null;
	protected Timestamp startTs;
	
	public Set getAttachments() {
		if (files == null) files = new HashSet();
		return files;
	}
	public void addAttachment(FileAttachment att) {
		getAttachments().add(att);
	}
	public void clearAttachments() {
		if (files != null) files.clear();
	}
	
	public String getType() {
		if (isFull()) return FULL;
		return SUMMARY;
	}
	public void setType(String type) {
		if (FULL.equals(type)) setFull(true);
		else if (SUMMARY.equals(type)) setSummary(true);
		else throw new IllegalArgumentException("Illegal type value");
	}
	public boolean isFull() {
		return full;
	}
	public void setFull(boolean full) {
		this.full = full;
	}
	public boolean isSummary() {
		return !full;
	}
	public void setSummary(boolean summary) {
		this.full = !summary;
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
	/**
	 * The dates returned from the database are java.sql.Timestamp
	 * compareTo doesn't work on a Date that is not a Timestamp
	 * @return
	 */
	public Date getStartDate() {
		return startTs;
	}
	public void setStartDate(Date startDate) {
		this.startTs = new Timestamp(startDate.getTime());
	}
}

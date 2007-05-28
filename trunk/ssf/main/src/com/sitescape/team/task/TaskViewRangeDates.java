package com.sitescape.team.task;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public interface TaskViewRangeDates {

	public abstract List getExtViewDayDates();

	public abstract Calendar getEndViewCal();

	public abstract Calendar getEndViewExtWindow();

	public abstract Calendar getStartViewCal();

	public abstract Calendar getStartViewExtWindow();

	public abstract boolean dateInView(Date dateToTest);

	public abstract boolean periodInView(long startDate, long endDate);

}
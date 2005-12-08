package com.sitescape.ef.jobs;


public class Schedule {
	private boolean daily=false;
	private int hours=12;
	private int minutes=15;
	private boolean onMonday,onTuesday,onWednesday,onThursday,onFriday,onSaturday,onSunday;

	public Schedule() {
	}
	public Schedule(String schedule) {
		String[] vals = schedule.split(" +");
		String val;
		int pos = 1;
		int len = vals.length;
		if (pos > len) return;
		val = vals[pos++];
		minutes = Integer.parseInt(val);
		if (pos > len) return;
		val = vals[pos++];
		hours = Integer.parseInt(val);
		//skip dayOfMonth and months
		pos += 2;
		if (pos > len) return;
		val = vals[pos].trim();
		if (val.equals("*")) {
			setDaily(true);
		} else {
			vals = val.split(",");
			for (int i=0; i<vals.length; ++i) {
				doDaySegment(vals[i].trim());
			}
		}
		
	}
	private void doDaySegment(String daysString) {
		String[] days = daysString.split("-");
		String start = days[0];
		String end;
		if (days.length==1) end=""; else end=days[1];
		if (start.compareToIgnoreCase("sun")==0) {
			onSunday=true;
			if (isLast("sun", end)) return;
			start="mon";
		}
		if (start.compareToIgnoreCase("mon")==0) {
			onMonday=true;
			if (isLast("mon", end)) return;
			start="tue";
		}
		if (start.compareToIgnoreCase("tue")==0) {
			onTuesday=true;
			if (isLast("tue", end)) return;
			start="wed";
		} 
		if (start.compareToIgnoreCase("wed")==0) {
			onWednesday=true;
			if (isLast("wed", end)) return;
			start="thu";
		}
		if (start.compareToIgnoreCase("thu")==0) {
			onThursday=true;
			if (isLast("thu", end)) return;
			start="fri";
		} 
		if (start.compareToIgnoreCase("fri")==0) {
			onFriday=true;
			if (isLast("fri", end)) return;
			start="sat";
		} 
		if (start.compareToIgnoreCase("sat")==0) {
			onSaturday=true;
		}  
	}
	private boolean isLast(String day, String end) {
		if (end.equals("")) return true;
		if (day.compareToIgnoreCase(end)==0) return true;
		return false;
	}
	public boolean isDaily()  {
		return daily;
	}
	public void setDaily(boolean daily) {
		this.daily = daily;
	}
	public boolean isOnMonday() {
		return onMonday;
	}
	public void setOnMonday(boolean onMonday) {
		this.onMonday = onMonday;
	}
	
	public boolean isOnTuesday() {
		return onTuesday;
	}
	public void setOnTuesday(boolean onTuesday) {
		this.onTuesday = onTuesday;
	}
	public boolean isOnWednesday() {
		return onWednesday;
	}
	public void setOnWednesday(boolean onWednesday) {
		this.onWednesday = onWednesday;
	}
	public boolean isOnThursday() {
		return onThursday;
	}
	public void setOnThursday(boolean onThursday) {
		this.onThursday = onThursday;
	}
	public boolean isOnFriday() {
		return onFriday;
	}
	public void setOnFriday(boolean onFriday) {
		this.onFriday = onFriday;
	}
	public boolean isOnSaturday() {
		return onSaturday;
	}
	public void setOnSaturday(boolean onSaturday) {
		this.onSaturday = onSaturday;
	}
	public boolean isOnSunday() {
		return onSunday;
	}
	public void setOnSunday(boolean onSunday) {
		this.onSunday = onSunday;
	}
	public int getHours() {
		return hours;
	}
	public void setHours(int hours) {
		this.hours = hours;
	}
	public int getMinutes() {
		return minutes;
	}
	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}
	public String getHoursMinutes() {
		return (hours + ":" + minutes);
	}
	public void setHoursMinutes(String hoursMinutes) {
		int pos = hoursMinutes.indexOf(':');
		if (pos == -1) {
			minutes = 0;
			hours = Integer.parseInt(hoursMinutes);
		} else {
			minutes = Integer.parseInt(hoursMinutes.substring(pos+1, hoursMinutes.length()));
			hours = Integer.parseInt(hoursMinutes.substring(0, pos));
		}

	}
	/**
	 * Return quartz schedule string.  We don't support seconds, dayOfMonth, months, year
	 * seconds minutes hours dayOfMonth months days year"
	 *
	 * @return
	 */
	public String getQuartzSchedule() {
		StringBuffer schedule = new StringBuffer("0 " + minutes + " " + hours + " ? * ");
		if (isDaily()) {
			schedule.append("* *");
		} else {
			if (isOnSunday()) schedule.append("sun,");
			if (isOnMonday()) schedule.append("mon,");
			if (isOnTuesday()) schedule.append("tue,");
			if (isOnWednesday()) schedule.append("wed,");
			if (isOnThursday()) schedule.append("thu,");
			if (isOnFriday()) schedule.append("fri,");
			if (isOnSaturday()) schedule.append("sat,");
			schedule.replace(schedule.length()-1, schedule.length(), " *");
		}
			
		return schedule.toString();
	}
}

package com.sitescape.ef.module.mail;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import com.sitescape.ef.jobs.ScheduleInfo;

public class PostingConfig extends ScheduleInfo {
	public PostingConfig() {		
	}
	public PostingConfig(ScheduleInfo scheduleInfo) {
		setSchedule(scheduleInfo.getSchedule());
		setDetails(scheduleInfo.getDetails());
		setEnabled(scheduleInfo.isEnabled());
	}
	public PostingConfig(Map details) {
		super(details);
	}
	/**
	 * Map of aliases.  The id of the alias name is stored in the
	 * PostingDef along with the name.  This allows us to change names,
	 * based on id which is necessary if names are editted.
	 * @return
	 */
	public Map getAliases() {
		Map aliases = (Map)getDetails().get("aliases");
		if (aliases == null) return new HashMap();
		return aliases;
	}
	public void setAliases(Map aliases) {
		getDetails().put("aliases", aliases);
	}
	public Map getIds() {
		Map aliases = (Map)getDetails().get("aliases");
		if (aliases == null) return new HashMap();
		Map ids = new HashMap();
		//invert map
		for (Iterator iter=aliases.entrySet().iterator(); iter.hasNext(); ) {
     		Map.Entry newE = (Map.Entry)iter.next();
     		ids.put(newE.getValue(), newE.getKey());
		}
		return ids;
	}
	public String getAlias(Long id) {
      	if (id == null) return null;
		for (Iterator iter=getAliases().entrySet().iterator(); iter.hasNext(); ) {
    		Map.Entry newE = (Map.Entry)iter.next();
    		if (id.equals(newE.getValue())) return (String)newE.getKey();
      	}
		return null;
 	}
}

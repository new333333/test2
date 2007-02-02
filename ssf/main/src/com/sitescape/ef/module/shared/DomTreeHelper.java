package com.sitescape.ef.module.shared;
import com.sitescape.team.util.AllBusinessServicesInjected;

public interface DomTreeHelper {
	public boolean supportsType(int type, Object source);
	
	public String getAction(int type, Object source);
	public String getURL(int type, Object source);
	public String getDisplayOnly(int type, Object source);
	public String getTreeNameKey();
	public boolean hasChildren(AllBusinessServicesInjected bs, Object source, int type);

}

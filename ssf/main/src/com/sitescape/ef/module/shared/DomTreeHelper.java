package com.sitescape.ef.module.shared;
import com.sitescape.ef.util.AllBusinessServicesInjected;

public interface DomTreeHelper {
	public boolean supportsType(int type);
	
	public String getAction(int type);
	public String getURL(int type);
	public String getDisplayOnly(int type);
	public String getTreeNameKey();
	public boolean hasChildren(AllBusinessServicesInjected bs, Object source, int type);

}

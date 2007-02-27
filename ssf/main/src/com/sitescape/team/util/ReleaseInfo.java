package com.sitescape.team.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReleaseInfo {

	static String title = "SiteScape Team Workspace";
	static String version;
	static String buildNumber,buildDate;
	
	static {
		if(SPropsUtil.getString("release.type").equals("plus"))
			title += " Enterprise/Premium";
		else
			title += " Core"; // Open Source version
			
		version = SPropsUtil.getString("release.version", "0.0.0");
		
		buildNumber = SPropsUtil.getString("release.build.number", "0");
		buildDate = SPropsUtil.getString("release.build.date", "");
	}
	
	static final String releaseInfo = 
		title + " " + version + " ( Build " + buildNumber + " / " + buildDate + ")";
	
	/**
	 * Returns version number if official release or <code>0.0.0</code> if unofficial.
	 * @return
	 */
	public static final String getVersion() {
		return version;
	}
	
	/**
	 * Returns build number if official release or <code>0</code> if unofficial. 
	 * @return
	 */
	public static final int getBuildNumber() {
		return Integer.parseInt(buildNumber);
	}
	
	/**
	 * Returns build date if official release or<code>null</code> if unofficial. 
	 * @return
	 */
	public static final Date getBuildDate() {
		if(buildDate.length() == 0)
			return null;
		
		SimpleDateFormat formatter =  new SimpleDateFormat("MMMM dd, yyyy");
		
		try {
			return formatter.parse(buildDate);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static final String getReleaseInfo() {
		return releaseInfo;
	}
	
	/*
	public static void main(String[] args) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
        
        String input = "2007-02-26 11:36:20";
        
        Date date = formatter.parse(input);
        
        String output = formatter.format(date);

        if(input.equals(output))
        	System.out.println("Match 1");
        else
        	System.out.println("Does not match 1");
        
        
        formatter = new SimpleDateFormat("MMMM dd, yyyy");
        input = "January 7, 2007";
        
        date = formatter.parse(input);
        
        output = formatter.format(date);

        if(input.equals(output))
        	System.out.println("Match 2");
        else {
        	System.out.println("Does not match 2");
        	System.out.println("Output: " + output);
        }
	}*/
}

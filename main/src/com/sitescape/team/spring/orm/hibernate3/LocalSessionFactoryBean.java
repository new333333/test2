package com.sitescape.team.spring.orm.hibernate3;

import com.sitescape.team.util.ClassPathConfigFiles;

public class LocalSessionFactoryBean extends org.springframework.orm.hibernate3.LocalSessionFactoryBean {
	
    public void setConfigFiles(String[] cFiles) {
    	ClassPathConfigFiles configFiles = new ClassPathConfigFiles();
    	configFiles.setConfigFiles(cFiles);
    	super.setConfigLocations(configFiles.getResources());
    }
}

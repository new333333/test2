package com.sitescape.ef.domain;

import java.util.List;
import java.util.ArrayList;
/**
 * @hibernate.subclass discriminator-value="PROFILES" dynamic-update="true" 
 */
public class ProfileBinder extends Binder {
	private List emptyList = new ArrayList();
	public List getChildAclContainers() {
		// TODO Auto-generated method stub
		return emptyList;
	}

	public List getChildAclControlled() {
		// TODO Auto-generated method stub
		return emptyList;
	}

}

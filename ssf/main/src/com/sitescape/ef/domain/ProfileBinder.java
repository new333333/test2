package com.sitescape.ef.domain;

import java.util.List;
/**
 * @hibernate.subclass discriminator-value="PROFILES" dynamic-update="true" 
 */
public class ProfileBinder extends Binder {

	public List getChildAclContainers() {
		// TODO Auto-generated method stub
		return null;
	}

	public List getChildAclControlled() {
		// TODO Auto-generated method stub
		return null;
	}

}

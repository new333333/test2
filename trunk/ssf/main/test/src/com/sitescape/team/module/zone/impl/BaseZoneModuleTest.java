package com.sitescape.team.module.zone.impl;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.support.AbstractTestBase;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.util.Pair;

public class BaseZoneModuleTest extends AbstractTestBase {
	
	@Autowired
	private BaseZoneModule zoneModule;
	
	@Test
	public void getDefaultZone() throws Exception {
		assertEquals(coreDao.findById(Workspace.class, zoneModule
				.getZoneIdByZoneName(SZoneConfig.getDefaultZoneName())),
				zoneModule.getDefaultZone());
	}
	
	@Test
	public void getZoneByName() throws Exception {
		String zoneName = RandomStringUtils.randomAlphabetic(20);
		Pair<User, Workspace> p = setupWorkspace(zoneName);
		assertEquals(p.getSecond(), zoneModule.getZoneByName(zoneName));
	}

}

package com.sitescape.team.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.sitescape.team.domain.User;
import com.sitescape.team.support.AbstractTestBase;

public class CoreDaoTest extends AbstractTestBase {

	@Test
	public void findById() throws Exception {
		User u = setupWorkspace("zone").getFirst();
		assertEquals(u, coreDao.findById(User.class, u.getId()));
	}
}

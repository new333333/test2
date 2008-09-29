package com.sitescape.team.web.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.Repeat;

import com.sitescape.team.domain.Attachment;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FileItem;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.support.AbstractTestBase;
import com.sitescape.team.web.WebKeys;

public class WebUrlUtilTest extends AbstractTestBase {
	
	private SecureRandom r = new SecureRandom(RandomStringUtils.random(5)
			.getBytes());
	
	@Test
	@Repeat(100)
	public void getAttachmentUrls() throws Exception {
		Folder b = new Folder();
		b.setId(r.nextLong());
		b.setupRoot();
		FolderEntry e = new FolderEntry();
		e.setId(r.nextLong());
		e.setParentBinder(b);
		FileAttachment f = new FileAttachment();
		f.setId(RandomStringUtils.randomAlphabetic(5));
		f.setModification(new HistoryStamp(null, new Date()));
		FileItem fi = new FileItem();
		fi.setName(RandomStringUtils.randomAlphabetic(10));
		f.setFileItem(fi);
		e.setAttachments(Collections.singleton((Attachment) f));
		String host = (RandomStringUtils.randomAlphabetic(7) + ".com").toLowerCase();
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.setServerPort(8080);
		req.setServerName(host);
		req.setContextPath("/ssf");
		
		List<String> urls = WebUrlUtil.getAttachmentUrls(req, e);
		assertNotNull(urls);
		assertEquals(1, urls.size());
		assertEquals("http://" + host + ":8080/ssf/s/" + WebKeys.ACTION_READ_FILE + "/"
				+ e.getEntityType().name() + "/" + e.getParentBinder().getId()
				+ "/" + e.getId() + "/" + f.getId() + "/"
				+ f.getModification().getDate().getTime() + "/"
				+ f.getFileItem().getName(), urls.get(0));
		
	}

}

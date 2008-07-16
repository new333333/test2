package com.sitescape.team.util;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.junit.Before;
import org.junit.Test;

import com.sitescape.team.support.AbstractTestBase;

public class DirectoryChangePollerTest extends AbstractTestBase {
	
	private DirectoryChangePoller dcp;
	private File dir;
	private List<File> fs = new ArrayList<File>();

	@Before
	public void setup() throws Exception {
		dcp = new DirectoryChangePoller();
		dir = File.createTempFile("tmp", "");
		dir.delete();
		dir.mkdir();
		for (int i = 0; i < 10; ++i) {
			File f = new File(dir, String.valueOf(i));
			f.createNewFile();
			f.setLastModified(0L);
			fs.add(f);
		}
		dir.setLastModified(0L);
		dcp.setTarget(dir);
		dcp.setLastModified(dir.lastModified());
	}

	@Test
	public void check() throws Exception {
		final List<File> es = new ArrayList<File>(2);
		dcp.register(new EventListener<FileChangePoller, File>() {
			public void onNotification(FileChangePoller source, File event) {
				es.add(event);
			}});
		int i = Math.abs(rand.nextInt()); 
		fs.get(i % fs.size()).setLastModified(1000L);
		fs.get((i + 1) % fs.size()).setLastModified(1000L);
		dir.setLastModified(1000L);
		dcp.check();
		assertEquals(2, es.size());
	}
	
	@Test
	public void setFileFilter() throws Exception {
		final List<File> es = new ArrayList<File>(2);
		dcp.register(new EventListener<FileChangePoller, File>() {
			public void onNotification(FileChangePoller source, File event) {
				es.add(event);
			}});
		int i = Math.abs(rand.nextInt()); 
		fs.get(i % fs.size()).setLastModified(1000L);
		fs.get((i + 1) % fs.size()).setLastModified(1000L);
		dir.setLastModified(1000L);
		dcp.setFileFilter(FileFilterUtils.falseFileFilter());
		
		dcp.check();
		assertEquals(0, es.size());
	}
}

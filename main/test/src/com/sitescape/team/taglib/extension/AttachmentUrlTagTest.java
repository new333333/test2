package com.sitescape.team.taglib.extension;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.domain.Attachment;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FileItem;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.module.binder.processor.EntryProcessor;
import com.sitescape.team.module.shared.EmptyInputData;
import com.sitescape.team.module.template.TemplateModule;
import com.sitescape.team.repository.RepositoryUtil;
import com.sitescape.team.support.AbstractTestBase;
import com.sitescape.team.util.FileUploadItem;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.WebUrlUtil;

public class AttachmentUrlTagTest extends AbstractTestBase {
	
	@Resource(name = "defaultFolderCoreProcessor")
	private EntryProcessor entries;
	@Autowired
	private TemplateModule templates;

	@Test
	public void apply() throws Exception {
		AttachmentUrlTag aut = new AttachmentUrlTag();
		String attr = RandomStringUtils.randomAlphabetic(4);
		String prefix = RandomStringUtils.randomAlphabetic(5);
		String suffix = RandomStringUtils.randomAlphabetic(5);
		MockHttpServletRequest req = new MockHttpServletRequest();
		RequestContext rc = defaultRequestContext();
		// XXX this is goddamn crazy...
		// create an owning folder
		Binder parent = (Binder) rc.getZone().getBinders().get(0);
		TemplateBinder folderCfg = templates.getTemplates(Definition.FOLDER_VIEW).get(0);
		Binder folder = coreDao.loadBinder(templates.addBinder(folderCfg.getId(), parent.getId(), "title", "name"), rc.getZoneId());
		// create an owning entry (with attachment)
		Map<String, List<FileUploadItem>> entryData = Collections.singletonMap(
				ObjectKeys.DEFINITION_FILE_DATA,
				Collections.singletonList(new FileUploadItem(
						FileUploadItem.TYPE_ATTACHMENT, "attachment", null,
						RepositoryUtil.getDefaultRepositoryName())));
		Definition def = (Definition) coreDao.loadDefinitions(rc.getZoneId(),
				Definition.FOLDER_ENTRY).get(0);
		Entry entry = entries.addEntry(folder, def, FolderEntry.class,
				new EmptyInputData(), entryData,
				Collections.EMPTY_MAP);
		req.setAttribute(WebKeys.ENTRY, entry);
		FileAttachment attachment = new FileAttachment("attachment");
		FileItem f = new FileItem();
		f.setName(RandomStringUtils.randomAlphabetic(5));
		attachment.setId(RandomStringUtils.randomAlphanumeric(5));
		attachment.setFileItem(f);
		attachment.setModification(new HistoryStamp(null, new Date()));
		attachment.setName(RandomStringUtils.randomAlphabetic(5));
		entry.setAttachments(Collections.singletonList((Attachment) attachment));
		aut.setAttr(attr);
		aut.setPrefix(prefix);
		aut.setSuffix(suffix);
		aut.setPageContext(new MockPageContext(new MockServletContext(), req));
		
		assertEquals(prefix + WebUrlUtil.getAttachmentUrls(req, entry).get(0) + suffix, aut.apply(
				Collections.singletonList((Element) new DefaultElement("div")))
				.get(0).attribute(attr).getValue());
	}
	
}

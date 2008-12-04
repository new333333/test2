package org.kablink.teaming.taglib.extension;

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
import org.kablink.teaming.support.AbstractTestBase;
import org.kablink.teaming.util.FileUploadItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FileItem;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.module.binder.processor.EntryProcessor;
import org.kablink.teaming.module.shared.EmptyInputData;
import org.kablink.teaming.module.template.TemplateModule;
import org.kablink.teaming.repository.RepositoryUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.WebUrlUtil;

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
		Definition def = (Definition) coreDao.loadDefinitions(rc.getZoneId()).get(0);
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

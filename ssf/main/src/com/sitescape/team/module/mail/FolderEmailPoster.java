package com.sitescape.team.module.mail;

import java.util.List;

import javax.mail.Message;
import javax.mail.Session;

import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.PostingDef;

public interface FolderEmailPoster {
    public static final String PROCESSOR_KEY = "processorKey_folderEmailPoster";
	public List postMessages(Folder folder, PostingDef pDef, Message[] msgs, Session session);

}

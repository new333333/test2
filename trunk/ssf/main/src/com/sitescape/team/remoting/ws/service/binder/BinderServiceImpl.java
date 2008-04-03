package com.sitescape.team.remoting.ws.service.binder;

import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.remoting.RemotingException;
import com.sitescape.team.remoting.ws.BaseService;
import com.sitescape.team.remoting.ws.util.DomInputData;
import com.sitescape.team.util.stringcheck.StringCheckUtil;

public class BinderServiceImpl extends BaseService implements BinderService {
	public long addBinder(String accessToken, long parentId, String definitionId, String inputDataAsXML)
	{
		inputDataAsXML = StringCheckUtil.check(inputDataAsXML);
		
		try {
			Document doc = getDocument(inputDataAsXML);
			return getBinderModule().addBinder(new Long(parentId), definitionId, 
					new DomInputData(doc, getIcalModule()), new HashMap(), null).longValue();
		} catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
	}
	public String getTeamMembersAsXML(String accessToken, long binderId)
	{
		Binder binder = getBinderModule().getBinder(new Long(binderId));
		SortedSet<Principal> principals = getBinderModule().getTeamMembers(binder, true);
		Document doc = DocumentHelper.createDocument();
		Element team = doc.addElement("team");
		team.addAttribute("inherited", binder.isTeamMembershipInherited()?"true":"false");
		for(Principal p : principals) {
			addPrincipalToDocument(team, p);
		}
		
		return doc.getRootElement().asXML();
	}
	public void setTeamMembers(String accessToken, long binderId, List<Long> memberIds) {
		getBinderModule().setTeamMembers(binderId, memberIds);
	}

}

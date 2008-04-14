package com.sitescape.team.remoting.ws.service.binder;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.shared.XmlUtils;
import com.sitescape.team.remoting.RemotingException;
import com.sitescape.team.remoting.ws.BaseService;
import com.sitescape.team.remoting.ws.util.DomInputData;
import com.sitescape.team.security.function.Function;
import com.sitescape.team.util.LongIdUtil;
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
	public void setDefinitions(String accessToken, long binderId, String[] definitionIds, String[] workflowAssociations) {
		HashMap wfs = new HashMap();
		for (int i=0; i+1<workflowAssociations.length; i+=2) {
			wfs.put(workflowAssociations[i], workflowAssociations[i+1]);
		}
		getBinderModule().setDefinitions(binderId, Arrays.asList(definitionIds), wfs);
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
	public void setTeamMembers(String accessToken, long binderId, Long[] memberIds) {
		getBinderModule().setTeamMembers(binderId, Arrays.asList(memberIds));
	}
	public void setFunctionMembership(String accessToken, long binderId, String inputDataAsXml) {
		Binder binder = getBinderModule().getBinder(binderId);
		List<Function> functions = getAdminModule().getFunctions();
		Document doc = getDocument(inputDataAsXml);
		Map wfms = new HashMap();
		List<Element> wfmElements = doc.selectNodes("./" + ObjectKeys.XTAG_ELEMENT_TYPE_FUNCTION_MEMBERSHIP);
		for (Element wfmElement:wfmElements) {
			 String functionName = XmlUtils.getProperty(wfmElement, ObjectKeys.XTAG_WA_FUNCTION_NAME);
			 Function func = null;
			 for (Function f:functions) {
				 if (f.getName().equals(functionName)) {
					 func = f;
					 break;
				 }
			 }
			 if (func == null) continue;
			 List<Element> nameElements = wfmElement.selectNodes("./" + ObjectKeys.XTAG_ELEMENT_TYPE_PROPERTY + "[@name='" + ObjectKeys.XTAG_WA_MEMBER_NAME + "']");
			 Set<String> names = new HashSet();
			 for (Element e:nameElements) {
				 names.add(e.getTextTrim());				 
			 }
			 Collection<Principal> principals = getProfileModule().getPrincipalsByName(names);
			 Set<Long>ids = new HashSet();
			 for (Principal p:principals) {
				 ids.add(p.getId());
			 }
			 ids.addAll(LongIdUtil.getIdsAsLongSet(XmlUtils.getProperty(wfmElement, ObjectKeys.XTAG_WA_MEMBERS), ","));

			 if (ids.isEmpty()) continue;
			 wfms.put(func.getId(), ids);
		}
		getAdminModule().setWorkAreaFunctionMembershipInherited(binder, false); 
		getAdminModule().setWorkAreaFunctionMemberships(binder, wfms);
	}
	public void setFunctionMembershipInherited(String accessToken, long binderId, boolean inherit) {
		Binder binder = getBinderModule().getBinder(binderId);
		getAdminModule().setWorkAreaFunctionMembershipInherited(binder, inherit); 		
	}
	public void setOwner(String accessToken, long binderId, long userId) {
		Binder binder = getBinderModule().getBinder(binderId);
		getAdminModule().setWorkAreaOwner(binder, userId, false); 		
	}

}

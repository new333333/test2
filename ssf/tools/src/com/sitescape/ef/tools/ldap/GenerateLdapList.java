package com.sitescape.ef.tools.ldap;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


public class GenerateLdapList {
	public static void main(String[] args) {
		if ((args.length == 0) || (args.length > 2)) {
			System.out.println("usage: java GenerateLdapList <outputFile [nogroups]>");
			return;
		}
		else if (args.length == 2) {
			if (args[1].equalsIgnoreCase("nogroups")) {
				System.out.println("java GenerateCreateTablesUnconstrained " + args[0] + " " + args[1]);
			} else {
				System.out.println("usage: java GenerateLdapList <outputFile [nogroups]>");
				return;
					
			}
		} 
		
		try {
			if (args.length == 1) {
				doMain(args[0], true);
			} else {
				doMain(args[0],false);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void doMain(String outFile, boolean doGroups) throws Exception {
		
		ClassLoader cl = GenerateLdapList.class.getClassLoader();
		URL inUrl = cl.getResource("zone.cfg.xml");
		Document document = null;
        SAXReader reader = new SAXReader();
        InputStreamReader fIn=null;
        try {
        	fIn = new InputStreamReader(new FileInputStream(inUrl.getFile()), "UTF-8");
        	document = reader.read(fIn);
 
        } catch (Exception ex) {
        	System.out.println("Cannot read XML file " + inUrl + ":error is: " + ex.getMessage());
        	throw ex;
        } finally {
        	if (fIn != null) {
        		try {
        			fIn.close(); 
        		} catch (Exception ex) {}
        	}
        }
		FileOutputStream fOut = null;
		XMLWriter xOut=null;
		try {
			//explicity set encoding so their is no mistake.
			//cannot guarentee default will be set to UTF-8
			fOut = new FileOutputStream(outFile);
			OutputFormat fmt = OutputFormat.createPrettyPrint();
			fmt.setEncoding("UTF-8");
    		xOut = new XMLWriter(fOut, fmt);
           	Element cfgRoot = document.getRootElement();
            LdapContext ctx = getContext(cfgRoot);
           	Document profileDoc = DocumentHelper.createDocument();
        	Element profileRoot = profileDoc.addElement("profiles");
        	Element userRoot = profileRoot.addElement("users");
            doUsers(ctx, cfgRoot, userRoot);
            if (doGroups) {
            	Element groupRoot = profileRoot.addElement("groups");
            	doGroups(ctx, cfgRoot, groupRoot);
            }
            xOut.write(profileDoc);
    		xOut.flush();
	    } catch (Exception ex) {
	    	System.out.println("Can't write XML file " + outFile + ":error is: " + ex.getMessage());
	    	throw(ex);
	    } finally {
	    	if (xOut != null) xOut.close();
	    	else if (fOut != null) fOut.close();
	    }
       
	}
	private static void doUsers(LdapContext ctx, Element cfgRoot, Element userRoot) throws Exception {
		Element next;
	    String userFilter=null;
	    next = (Element)cfgRoot.selectSingleNode("/zoneConfiguration/ldapConfiguration/property[@name='com.sitescape.ldap.user.search']");
	    if (next != null) userFilter = next.getTextTrim();
	    if ((next == null) || userFilter.equals("")) 
	    	userFilter = "(|(objectClass=person)(objectClass=inetOrgPerson)(objectClass=organizationalPerson)(objectClass=residentialPerson))";

	    Map userAttributes = new HashMap();
		List mappings = cfgRoot.selectNodes("/zoneConfiguration/ldapConfiguration/userMapping/mapping");
		for (int i=0; i<mappings.size(); ++i) {
			next = (Element)mappings.get(i);
			userAttributes.put(next.attributeValue("from"), next.attributeValue("to"));
		}
		
		String [] userAttributeNames = 	(String[])(userAttributes.keySet().toArray(new String[0]));

		SearchControls sch = new SearchControls(
				SearchControls.SUBTREE_SCOPE, 0, 0, userAttributeNames, false, false);
		NamingEnumeration ctxSearch = ctx.search("", userFilter, sch);
		while (ctxSearch.hasMore()) {
			Binding bd = (Binding)ctxSearch.next();
			Attributes lAttrs = ctx.getAttributes(bd.getName());
			String dn;
			if (bd.isRelative()) {
				dn = (bd.getName().trim() + "," + ctx.getNameInNamespace());
			} else {
				dn = bd.getName().trim();
			}
			Element user = userRoot.addElement("user");
			getUpdates(user, userAttributeNames, userAttributes, lAttrs);
			Element prop=user.addElement("property");
			prop.addAttribute("name", "foreignName");
			prop.addText(dn);
		}
				
	}
	private static void doGroups(LdapContext ctx, Element cfgRoot, Element groupRoot) throws Exception {
       	Element next;
        String groupFilter = null;
        next = (Element)cfgRoot.selectSingleNode("/zoneConfiguration/ldapConfiguration/property[@name='com.sitescape.ldap.group.search']");
        if (next != null) groupFilter = next.getTextTrim();
	    if ((groupFilter == null) || groupFilter.equals("")) 
        	groupFilter = "(|(objectClass=group)(objectClass=groupOfUniqueNames)(objectClass=groupOfNames))";

 
		//get attributes that contain membership
		List memberAttributes = new ArrayList();
		List classes = cfgRoot.selectNodes("/zoneConfiguration/ldapConfiguration/groupMapping/memberAttribute");
		for (int i=0; i<classes.size(); ++i) {
			next = (Element)classes.get(i);
			memberAttributes.add(next.getTextTrim());
		}

		Map groupAttributes = new HashMap();
		List mappings = cfgRoot.selectNodes("/zoneConfiguration/ldapConfiguration/groupMapping/mapping");
		for (int i=0; i<mappings.size(); ++i) {
			next = (Element)mappings.get(i);
			groupAttributes.put(next.attributeValue("from"), next.attributeValue("to"));
		}
		Set la = new HashSet(groupAttributes.keySet());
		la.addAll(memberAttributes);
		SearchControls sch = new SearchControls(
				SearchControls.SUBTREE_SCOPE, 0, 0, (String [])la.toArray(new String[0]), false, false);
	
		String [] groupAttributeNames = (String[])(groupAttributes.keySet().toArray(new String[0]));

		NamingEnumeration ctxSearch = ctx.search("", groupFilter, sch);
		while (ctxSearch.hasMore()) {
			Binding bd = (Binding)ctxSearch.next();
			Attributes lAttrs = ctx.getAttributes(bd.getName());
			String dn;
			if (bd.isRelative()) {
				dn = bd.getName().trim() + "," + ctx.getNameInNamespace();
			} else {
				dn = bd.getName().trim();
			}
			Element group = groupRoot.addElement("group");
			getUpdates(group, groupAttributeNames, groupAttributes, lAttrs);
			Element prop=group.addElement("property");
			prop.addAttribute("name", "foreignName");
			prop.addText(dn);
			for (int i=0; i<memberAttributes.size(); i++) {
				Attribute att = lAttrs.get((String)memberAttributes.get(i));
				if (att == null) continue;
				Object val = att.get();
				if (val == null) {
					continue;
				} else if (att.size() == 0) {
					continue;
				} else {
					//build new membership
					for (NamingEnumeration valEnum=att.getAll(); valEnum.hasMoreElements();) {
						String mDn = ((String)valEnum.nextElement()).trim();
						prop = group.addElement("property");
						prop.addAttribute("name", "memberName");
						prop.addText(mDn);
					}
				}
			}		
			
		}

	}
	private static void getUpdates(Element node, String []ldapAttrNames, Map mapping, Attributes attrs)  throws NamingException {
		Element prop;
		for (int i=0; i<ldapAttrNames.length; i++) {
			Attribute att = attrs.get(ldapAttrNames[i]);
			if (att == null) continue;
			Object val = att.get();
			if (val == null) {
				continue;
			} else if (att.size() == 0) {
				continue;
			} else if (att.size() == 1) {
				prop = node.addElement("attribute");
				prop.addAttribute("name", (String)mapping.get(ldapAttrNames[i]));
				prop.addAttribute("type", "string");
				prop.addText(val.toString());
			} else {
				prop = node.addElement("attribute-set");
				prop.addAttribute("name", (String)mapping.get(ldapAttrNames[i]));
				for (NamingEnumeration valEnum=att.getAll(); valEnum.hasMoreElements();) {
					Element prop1 = prop.addElement("attribute");
					prop.addAttribute("name", (String)mapping.get(ldapAttrNames[i]));
					prop1.addAttribute("type", "string");
					prop1.addText(valEnum.nextElement().toString());
			
				}
			}
		}		
	}

	
	private static LdapContext getContext(Element root) throws NamingException { 
		// Load user from ldap
		try {
			Hashtable env = new Hashtable();
			String val=null;
	        Element next = (Element)root.selectSingleNode("/zoneConfiguration/ldapConfiguration/property[@name='" + Context.INITIAL_CONTEXT_FACTORY + "']");
	        if (next != null) val = next.getTextTrim();
		    if ((next == null) || val.equals(""))  
	        	env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
	        else
	        	env.put(Context.INITIAL_CONTEXT_FACTORY, val);
	        next = (Element)root.selectSingleNode("/zoneConfiguration/ldapConfiguration/property[@name='" + Context.PROVIDER_URL + "']");
	        if (next != null) val = next.getTextTrim();
	        if (next == null || val.equals("")) 
	        	env.put(Context.PROVIDER_URL, "ldap://localhost:389/");
	        else
	        	env.put(Context.PROVIDER_URL, val);
	        String user=null,pwd=null;
	        next = (Element)root.selectSingleNode("/zoneConfiguration/ldapConfiguration/property[@name='" + Context.SECURITY_PRINCIPAL + "']");
	        if (next != null) user = next.getTextTrim();

	        next = (Element)root.selectSingleNode("/zoneConfiguration/ldapConfiguration/property[@name='" + Context.SECURITY_CREDENTIALS + "']");
	        if (next != null) pwd = next.getTextTrim();
	        
			if ((user != null) && (pwd != null) &&
					!user.equals("") && !pwd.equals("")) {
				env.put(Context.SECURITY_PRINCIPAL, user);
				env.put(Context.SECURITY_CREDENTIALS, pwd);		
		        next = (Element)root.selectSingleNode("/zoneConfiguration/ldapConfiguration/property[@name='" + Context.SECURITY_AUTHENTICATION + "']");
		        if (next != null) val = next.getTextTrim();
			    if ((next == null) || val.equals(""))  
					env.put(Context.SECURITY_AUTHENTICATION, "simple");
		        else
		        	env.put(Context.SECURITY_AUTHENTICATION, val);
			} 
	        next = (Element)root.selectSingleNode("/zoneConfiguration/ldapConfiguration/property[@name='java.naming.ldap.factory.socket']");
	        if (next != null) val = next.getTextTrim();
		    if ((next != null) && !val.equals(""))  
				env.put("java.naming.ldap.factory.socket", val);
		
			return new InitialLdapContext(env, null);
		} catch (NamingException ex) {
			System.out.println("context error:" + ex);
			throw ex;
		}
	}

}

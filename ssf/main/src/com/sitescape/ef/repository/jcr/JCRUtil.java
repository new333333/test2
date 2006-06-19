package com.sitescape.ef.repository.jcr;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.sitescape.ef.context.request.RequestContextHolder;

public class JCRUtil {

	public static Node getFolderNode(Node node, String name)
			throws RepositoryException {

		Node folderNode = null;

		if (node.hasNode(name)) {
			folderNode = node.getNode(name);
		} else {
			folderNode = node.addNode(name, JCRConstants.NT_FOLDER);
		}

		return folderNode;
	}

	public static Node getRootNode(Session session) throws RepositoryException {
		String zoneName = RequestContextHolder.getRequestContext()
				.getZoneName();

		Node zoneNode = getFolderNode(session.getRootNode(), zoneName);

		return zoneNode;
	}
}

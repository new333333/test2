/*
 * $Header$
 * $Revision: 356258 $
 * $Date: 2005-12-12 09:29:55 -0500 (Mon, 12 Dec 2005) $
 *
 * ====================================================================
 *
 * Copyright 1999-2002 The Apache Software Foundation 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.slide.structure;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.apache.slide.common.Domain;
import org.apache.slide.common.Namespace;
import org.apache.slide.common.NamespaceConfig;
import org.apache.slide.common.ServiceAccessException;
import org.apache.slide.common.SlideToken;
import org.apache.slide.common.Uri;
import org.apache.slide.common.UriPath;
import org.apache.slide.common.UriTokenizer;
import org.apache.slide.content.NodeRevisionDescriptor;
import org.apache.slide.content.NodeRevisionNumber;
import org.apache.slide.content.RevisionDescriptorNotFoundException;
import org.apache.slide.event.EventDispatcher;
import org.apache.slide.event.StructureEvent;
import org.apache.slide.event.VetoException;
import org.apache.slide.lock.Lock;
import org.apache.slide.lock.ObjectLockedException;
import org.apache.slide.security.AccessDeniedException;
import org.apache.slide.security.Security;
import org.apache.slide.store.ResourceId;
import org.apache.slide.store.Store;
import org.apache.slide.util.Configuration;

/**
 * Default implementation of the Structure interface.
 * 
 * @version $Revision: 356258 $
 */
public class StructureImpl implements Structure {

	// 4/21/06 JK - begin
	private static final ThreadLocal ssf_retrieveChild = new ThreadLocal();
	public static void ssf_setRetrieveChild(Boolean bool) {
		ssf_retrieveChild.set(bool);
	}
	public static Boolean ssf_getRetrieveChild() {
		Boolean value = (Boolean) ssf_retrieveChild.get();
		if(value == null)
			return Boolean.TRUE; // default is true
		else
			return value;
	}
	// JK - end
	
	// ----------------------------------------------------------- Constructors

	/**
	 * Constructor.
	 * 
	 * @param namespace
	 *            the namespace associated with the helper object
	 * @param namespaceConfig
	 *            configuration of the namespace
	 * @param securityHelper
	 *            the associated security helper
	 * @param lockHelper
	 *            the associated lock helper
	 */
	public StructureImpl(Namespace namespace, NamespaceConfig namespaceConfig,
			Security securityHelper, Lock lockHelper) {
		this.namespace = namespace;
		this.namespaceConfig = namespaceConfig;
		this.securityHelper = securityHelper;
		this.lockHelper = lockHelper;
	}

	// ----------------------------------------------------- Instance Variables

	/**
	 * Namespace.
	 */
	private Namespace namespace;

	/**
	 * Namespace configuration.
	 */
	private NamespaceConfig namespaceConfig;

	/**
	 * Security helper.
	 */
	private Security securityHelper;

	/**
	 * Lock helper.
	 */
	private Lock lockHelper;

	// ------------------------------------------------------ Structure Methods

	public String generateUniqueUri(SlideToken token, String parentUri)
			throws ServiceAccessException {
		String sequenceName = parentUri.replace('/', '-');

		Uri uri = namespace.getUri(token, parentUri);
		Store store = uri.getStore();
		if (!store.isSequenceSupported()) {
			return null;
		} else {
			if (!store.sequenceExists(sequenceName)) {
				store.createSequence(sequenceName);
			}
			long next = store.nextSequenceValue(sequenceName);
			String uniqueUri = parentUri + "/" + next;
			return uniqueUri;
		}
	}

	public Enumeration getChildren(SlideToken token, ObjectNode object)
			throws ServiceAccessException, ObjectNotFoundException,
			LinkedObjectNotFoundException, VetoException {
		// 4/21/06 JK - Do not fetch children of children.
		ssf_setRetrieveChild(Boolean.FALSE);
		try {
			Enumeration childrenUri = object.enumerateChildren();
			Vector result = new Vector();
			while (childrenUri.hasMoreElements()) {
				String childUri = (String) childrenUri.nextElement();
				try {
					ObjectNode child = retrieve(token, childUri, false);
					result.addElement(child);
				} catch (AccessDeniedException e) {
				}
			}
			return result.elements();
		}
		finally {
			ssf_setRetrieveChild(Boolean.TRUE);
		}
	}

	public ObjectNode getParent(SlideToken token, ObjectNode object)
			throws ServiceAccessException, ObjectNotFoundException,
			LinkedObjectNotFoundException, AccessDeniedException, VetoException {
		String objectUriStr = object.getUri();
		Uri parentUri = namespace.getUri(token, objectUriStr).getParentUri();
		if (parentUri == null) {
			return null;
		}
		String parentUriStr = parentUri.toString();
		ObjectNode parent = retrieve(token, parentUriStr);
		return parent;
	}

	public ObjectNode retrieve(SlideToken token, String strUri)
			throws ServiceAccessException, ObjectNotFoundException,
			LinkedObjectNotFoundException, AccessDeniedException, VetoException {
		return retrieve(token, strUri, true);
	}

	public ObjectNode retrieve(SlideToken token, String strUri,
			boolean translateLastUriElement) throws ServiceAccessException,
			ObjectNotFoundException, LinkedObjectNotFoundException,
			AccessDeniedException, VetoException {
		Uri uri = namespace.getUri(token, strUri);
		ObjectNode result = null;

		// Fire event
		if (StructureEvent.RETRIEVE.isEnabled())
			EventDispatcher.getInstance().fireVetoableEvent(
					StructureEvent.RETRIEVE,
					new StructureEvent(this, token, namespace, strUri));

		// First of all, we try to load the object directly from the given Uri.
		try {
			result = uri.getStore().retrieveObject(uri);
			securityHelper.checkCredentials(token, result, namespaceConfig
					.getReadObjectAction());
			if ((translateLastUriElement) && (result instanceof LinkNode)) {
				LinkNode link = (LinkNode) result;
				Uri linkedUri = namespace.getUri(token, link.getLinkedUri());
				result = linkedUri.getStore().retrieveObject(linkedUri);
				securityHelper.checkCredentials(token, result, namespaceConfig
						.getReadObjectAction());
			}
		} catch (ObjectNotFoundException e) {
		}

		// If the attempt to load the uri failed, it means there is at least
		// one link in the uri (or that the uri doe'sn't have any associated
		// object).
		if (result == null) {

			/* 9/19/06 JK - The following section of code attempts to check the
			 * existence of each intermediary directory node at each level in the
			 * path. In the context of SSFS implementation and with the limited use
			 * cases we know about, this code doesn't appear to be necessary. 
			 * But the primary reason for commenting out this section is because its
			 * negative impact on the performance is more than trivial.
			 *
			
			String resolvedUri = uri.toString();

			// 1 - Tokemization of the Uri
			UriTokenizer uriTokenizer = new UriTokenizer(token, uri
					.getNamespace(), resolvedUri);

			// 2 - For each element of the Uri
			Uri courUri = null;
			ObjectNode courObject = null;
			while (uriTokenizer.hasMoreElements()) {

				// 3 - Load object's class from the uri. If the object
				// does not exist, a DataException is thrown.
				courUri = uriTokenizer.nextUri();
				courObject = courUri.getStore().retrieveObject(courUri);

				// We check to see if the credentials gives access to
				// the current object
				if (Domain.getParameter("ancestors-read-permissions-required",
						"true").equals("true")) {
					securityHelper.checkCredentials(token, courObject,
							namespaceConfig.getReadObjectAction());
				}

				// 4 - Test if object is a link, ie if it is an instance
				// of LinkNode or one of its subclasses
				if (((translateLastUriElement) && (courObject instanceof LinkNode))
						|| ((!translateLastUriElement)
								&& (uriTokenizer.hasMoreElements()) && (courObject instanceof LinkNode))) {

					// 5 - If the object is a link, we get the uri of
					// the linked object
					// Note : courUri still IS the Uri of the link, and so,
					// in a way courUri is the parent of linkedUri.
					Uri linkedUri = namespace.getUri(token,
							((LinkNode) courObject).getLinkedUri());

					// 6 - We replace the courUri scope in the original uri
					String courStrUri = courUri.toString();
					resolvedUri = linkedUri.toString()
							+ resolvedUri.substring(courStrUri.length());

					// 7 - We tokenize again the uri
					uriTokenizer = new UriTokenizer(token, uri.getNamespace(),
							resolvedUri);

					// 8 - We parse it till we get back to the point
					// where we stopped
					boolean isUriFound = false;
					while ((!isUriFound) && (uriTokenizer.hasMoreElements())) {
						if (linkedUri.equals(uriTokenizer.nextUri())) {
							isUriFound = true;
						}
					}
					if (!isUriFound) {
						throw new LinkedObjectNotFoundException(courUri,
								resolvedUri);
					}

				}

				// 9 - We continue to go down in the Uri tree
			}

			// 10 - We return the last object which has been found

			result = courObject;
			*/
			
			// 9/19/06 JK - Simply throw an exception.
			throw new ObjectNotFoundException(uri);
		}

		return result;

	}

	public void create(SlideToken token, ObjectNode object, String strUri)
			throws ServiceAccessException, ObjectAlreadyExistsException,
			ObjectNotFoundException, LinkedObjectNotFoundException,
			AccessDeniedException, ObjectLockedException, VetoException {

		// Fire event
		if (StructureEvent.CREATE.isEnabled())
			EventDispatcher.getInstance().fireVetoableEvent(
					StructureEvent.CREATE,
					new StructureEvent(this, token, namespace, strUri));

		// Checking roles
		Enumeration roles = securityHelper.getRoles(object);
		while (roles.hasMoreElements()) {
			if (!securityHelper.hasRole(token, (String) roles.nextElement())) {
				// Allow only the namespace admin to create roles
				// he doesn't have
				Uri rootUri = namespace.getUri(token, "/");
				ObjectNode rootObject = rootUri.getStore().retrieveObject(
						rootUri);
				securityHelper.checkCredentials(token, rootObject,
						namespaceConfig.getGrantPermissionAction());
				break;
			}
		}

		String resolvedUri = strUri;

		// 1 - Tokenization of the Uri
		UriTokenizer uriTokenizer = new UriTokenizer(token, namespace,
				resolvedUri);

		// 2 - For each element of the Uri
		Uri courUri = null;
		ObjectNode courObject = null;
		ObjectNode parentObject = null;

		boolean alreadyExists = false;

		while (uriTokenizer.hasMoreElements()) {

			parentObject = courObject;

			// 3 - Load object's class from the uri. If the object does
			// not exist, a DataException is thrown.
			courUri = uriTokenizer.nextUri();
			try {
				courObject = courUri.getStore().retrieveObject(courUri);
				if (Domain.getParameter("ancestors-read-permissions-required",
						"true").equals("true")) {
					securityHelper.checkCredentials(token, courObject,
							namespaceConfig.getReadObjectAction());
				}
				if (!uriTokenizer.hasMoreElements()) {
					// The object already exists
					alreadyExists = true;
				}
			} catch (ObjectNotFoundException e) {
				// Load failed, probably because object was not found
				// We try to create a new one.
				// We have to test if the uri is the last in the list,
				// we must create the requested element.
				// By default, we create a SubjectNode.
				ObjectNode newObject = null;
				if (uriTokenizer.hasMoreElements()) {
					throw new ObjectNotFoundException(courUri);
				} else {
					newObject = object;
				}
				if (parentObject != null) {

					securityHelper.checkCredentials(token, courObject,
							namespaceConfig.getBindMemberAction());

					// Now creating the new object
					newObject.setUri(courUri.toString());
					courUri.getStore().createObject(courUri, newObject);

					// re-read to obtain UURI
					// newObject = courUri.getStore().retrieveObject(courUri);

					// re-read the parent taking the forceEnlistment flag into
					// account
					Uri parentUri = namespace.getUri(token, parentObject
							.getUri());
					parentObject = parentUri.getStore().retrieveObject(
							parentUri);
					// Add the newly created object to its parent's
					// children list
					ObjectNode oldChild = null;
					// we can check the parentUri, it's in the same store as
					// newObject
					if (Configuration.useBinding(parentUri.getStore())) {
						String bindingName = newObject.getPath().lastSegment();
						if (parentObject.hasBinding(bindingName)) {
							oldChild = retrieve(token, parentObject.getUri()
									+ "/" + bindingName, false);
							parentObject.removeChild(oldChild);
							store(token, oldChild);
						}
					}
					lockHelper.checkLock(token, parentObject, namespaceConfig
							.getCreateObjectAction());
					parentObject.addChild(newObject);
					// namespace.getUri(token, parentObject.getUri())
					// .getDataSource().storeObject(parentObject, false);
					store(token, parentObject, true);
					store(token, newObject);

				} else {
					throw new ObjectNotFoundException(courUri);
				}
				courObject = newObject;
			}

			// 4 - Test if object is a link, ie if it is an instance of
			// LinkNode or one of its subclasses
			if ((uriTokenizer.hasMoreElements())
					&& (courObject instanceof LinkNode)) {

				// 5 - If the object is a link, we get the uri of the
				// linked object
				// Note : courUri still IS the Uri of the link, and so,
				// in a way courUri is the parent of linkedUri.
				Uri linkedUri = namespace.getUri(token, ((LinkNode) courObject)
						.getLinkedUri());

				// 6 - We replace the courUri scope in the original uri
				String courStrUri = courUri.toString();
				resolvedUri = linkedUri.toString()
						+ resolvedUri.substring(courStrUri.length());

				// 7 - We tokenize again the uri
				uriTokenizer = new UriTokenizer(token, namespace, resolvedUri);

				// 8 - We parse it till we get back to the point
				// where we stopped
				boolean isUriFound = false;
				while ((!isUriFound) && (uriTokenizer.hasMoreElements())) {
					if (linkedUri.equals(uriTokenizer.nextUri())) {
						isUriFound = true;
					}
				}
				if (!isUriFound) {
					throw new LinkedObjectNotFoundException(courUri,
							resolvedUri);
				}

			}

			// 9 - We continue to go down in the Uri tree
		}

		if (alreadyExists) {
			if (courUri.isStoreRoot()) {
				// if the object already exists map it anyway into
				// the node hierarchy, to prevent loose of nodes
				// during start up
				if (parentObject != null && !parentObject.hasChild(courObject)) {
					parentObject.addChild(courObject);
					store(token, parentObject, true);
				}
			}
			throw new ObjectAlreadyExistsException(strUri);
		}
	}

	public void createLink(SlideToken token, LinkNode link, String linkUri,
			ObjectNode linkedObject) throws ServiceAccessException,
			ObjectAlreadyExistsException, ObjectNotFoundException,
			LinkedObjectNotFoundException, AccessDeniedException,
			ObjectLockedException, VetoException {
		link.setLinkedUri(linkedObject.getUri());

		// Fire event
		if (StructureEvent.CREATE.isEnabled())
			EventDispatcher.getInstance().fireVetoableEvent(
					StructureEvent.CREATE_LINK,
					new StructureEvent(this, token, link, linkUri));

		create(token, link, linkUri);
	}

	public void store(SlideToken token, ObjectNode object)
			throws ServiceAccessException, ObjectNotFoundException,
			AccessDeniedException, LinkedObjectNotFoundException, VetoException {

		store(token, object, false);
	}

	protected void store(SlideToken token, ObjectNode object,
			boolean setModificationDate) throws ServiceAccessException,
			ObjectNotFoundException, AccessDeniedException,
			LinkedObjectNotFoundException, VetoException {

		// Fire event
		if (StructureEvent.STORE.isEnabled())
			EventDispatcher.getInstance().fireVetoableEvent(
					StructureEvent.STORE,
					new StructureEvent(this, token, namespace, object));

		// Checking roles
		Enumeration roles = securityHelper.getRoles(object);
		while (roles.hasMoreElements()) {
			if (!securityHelper.hasRole(token, (String) roles.nextElement())) {
				// Allow only the namespace admin to create roles
				// he doesn't have
				Uri rootUri = namespace.getUri(token, "/");
				ObjectNode rootObject = rootUri.getStore().retrieveObject(
						rootUri);
				securityHelper.checkCredentials(token, rootObject,
						namespaceConfig.getGrantPermissionAction());
				break;
			}
		}

		// working on realObject, we will lose changes immediatly done before
		// call of store
		// i observerd this with some BIND testcases
		// ObjectNode realObject = retrieve(token, object.getUri(), false);
		securityHelper.checkCredentials(token, object, namespaceConfig
				.getCreateObjectAction());
		Uri uri = namespace.getUri(token, object.getUri());
		Store store = uri.getStore();
		store.storeObject(uri, object);

		if (setModificationDate) {
			try {
				NodeRevisionDescriptor revisionDescriptor = store
						.retrieveRevisionDescriptor(uri,
								new NodeRevisionNumber());
				revisionDescriptor.setModificationDate(new Date());
				revisionDescriptor.setModificationUser(securityHelper
						.getPrincipal(token).getPath().lastSegment());
				store.storeRevisionDescriptor(uri, revisionDescriptor);
			} catch (RevisionDescriptorNotFoundException e) {
				// ignore silently
			}
		}
	}

	/**
	 * Method remove
	 * 
	 * @param token
	 *            a SlideToken
	 * @param object
	 *            an ObjectNode
	 * 
	 * @throws ServiceAccessException
	 * @throws ObjectNotFoundException
	 * @throws ObjectHasChildrenException
	 * @throws AccessDeniedException
	 * @throws LinkedObjectNotFoundException
	 * @throws ObjectLockedException
	 * 
	 */
	public void remove(SlideToken token, ObjectNode object)
			throws ServiceAccessException, ObjectNotFoundException,
			ObjectHasChildrenException, AccessDeniedException,
			LinkedObjectNotFoundException, ObjectLockedException, VetoException {

		ObjectNode nodeToDelete = retrieve(token, object.getUri(), false);
		Uri uri = namespace.getUri(token, nodeToDelete.getUri());

		// Fire event
		if (StructureEvent.REMOVE.isEnabled())
			EventDispatcher.getInstance().fireVetoableEvent(
					StructureEvent.REMOVE,
					new StructureEvent(this, token, object, uri.toString()));

		if (!object.getUri().equals("/")) {
			Uri curUri = namespace.getUri(token, nodeToDelete.getUri());
			Uri parentUri = curUri.getParentUri();

			ObjectNode parentNode = parentUri.getStore().retrieveObject(
					parentUri);

			securityHelper.checkCredentials(token, nodeToDelete,
					namespaceConfig.getRemoveObjectAction());
			securityHelper.checkCredentials(token, parentNode, namespaceConfig
					.getUnbindMemberAction());
			lockHelper.checkLock(token, nodeToDelete, namespaceConfig
					.getRemoveObjectAction());
			lockHelper.checkLock(token, parentNode, namespaceConfig
					.getUnbindMemberAction());

			parentNode.removeChild(nodeToDelete);
			store(token, parentNode, true);

			if (Configuration.useBinding(curUri.getStore())
					&& nodeToDelete.numberOfParentBindings() > 0) {
				store(token, nodeToDelete);
			} else {
				Enumeration children = nodeToDelete.enumerateChildren();
				if (children.hasMoreElements()) {
					throw new ObjectHasChildrenException(uri);
				}
				uri.getStore().removeObject(uri, nodeToDelete);
			}
		}
	}

	/**
	 * Modifies the collection identified by <b>collectionNode</b>, by adding a
	 * new binding from the specified segment to the resource identified by
	 * <b>sourceNode</b>.
	 * 
	 * @param token
	 *            a SlideToken
	 * @param collectionNode
	 *            an ObjectNode
	 * @param segment
	 *            a String
	 * @param sourceNode
	 *            an ObjectNode
	 * 
	 * @throws ServiceAccessException
	 * @throws ObjectNotFoundException
	 * @throws AccessDeniedException
	 * @throws LinkedObjectNotFoundException
	 * @throws ObjectLockedException
	 * 
	 */
	public void addBinding(SlideToken token, ObjectNode collectionNode,
			String segment, ObjectNode sourceNode)
			throws ServiceAccessException, ObjectNotFoundException,
			AccessDeniedException, LinkedObjectNotFoundException,
			ObjectLockedException, CrossServerBindingException, VetoException {
		if (Configuration.useBinding(namespace.getUri(token,
				collectionNode.getUri()).getStore())) {
			collectionNode = retrieve(token, collectionNode.getUri(), false);
			sourceNode = retrieve(token, sourceNode.getUri(), false);
			Uri collectionUri = namespace
					.getUri(token, collectionNode.getUri());
			// Uri sourceUri = namespace.getUri(token, sourceNode.getUri());

			// Fire event
			if (StructureEvent.ADD_BINDING.isEnabled())
				EventDispatcher.getInstance().fireVetoableEvent(
						StructureEvent.ADD_BINDING,
						new StructureEvent(this, token, sourceNode,
								collectionUri.toString()));

			// if (collectionUri.getStore() != sourceUri.getStore()) {
			// throw new CrossServerBindingException(collectionNode.getUri(),
			// sourceNode.getUri());
			// }

			lockHelper.checkLock(token, collectionNode, namespaceConfig
					.getCreateObjectAction());

			ObjectNode oldChild = null;
			if (collectionNode.hasBinding(segment)) {
				oldChild = retrieve(token, collectionNode.getUri() + "/"
						+ segment, false);
				lockHelper.checkLock(token, oldChild, namespaceConfig
						.getCreateObjectAction());
				collectionNode.removeChild(oldChild);
				store(token, oldChild);
			}
			collectionNode.addBinding(segment, sourceNode);

			store(token, collectionNode, true);
			store(token, sourceNode);
		}
	}

	/**
	 * Modifies the collection identified by <b>collectionNode</b>, by removing
	 * the binding for the specified segment.
	 * 
	 * @param token
	 *            a SlideToken
	 * @param collectionNode
	 *            an ObjectNode
	 * @param segment
	 *            a String
	 * 
	 * @throws ServiceAccessException
	 * @throws ObjectNotFoundException
	 * @throws AccessDeniedException
	 * @throws LinkedObjectNotFoundException
	 * @throws ObjectLockedException
	 * 
	 */
	public void removeBinding(SlideToken token, ObjectNode collectionNode,
			String segment) throws ServiceAccessException,
			ObjectNotFoundException, AccessDeniedException,
			LinkedObjectNotFoundException, ObjectLockedException, VetoException {
		if (Configuration.useBinding(namespace.getUri(token,
				collectionNode.getUri()).getStore())) {
			collectionNode = retrieve(token, collectionNode.getUri(), false);
			ObjectNode childNode = retrieve(token, collectionNode.getUri()
					+ "/" + segment, false);

			// Fire event
			if (StructureEvent.REMOVE_BINDING.isEnabled())
				EventDispatcher.getInstance().fireVetoableEvent(
						StructureEvent.REMOVE_BINDING,
						new StructureEvent(this, token, childNode,
								collectionNode.getUri()));

			lockHelper.checkLock(token, collectionNode, namespaceConfig
					.getCreateObjectAction());
			lockHelper.checkLock(token, childNode, namespaceConfig
					.getCreateObjectAction());

			collectionNode.removeChild(childNode);

			store(token, childNode);
			store(token, collectionNode, true);
		}
	}

	/**
	 * Return all parents of this object node. If pathOnly=true, only parents on
	 * the path of the specified ObjectNode are returned, all parents (binding!)
	 * otherwise. If storeOnly=true, only parents within the scope of the store
	 * in charge of the specified ObjectNode are returned, parents up to the
	 * root ObjectNode (uri="/") otherwise.
	 * 
	 * @param token
	 *            a SlideToken
	 * @param object
	 *            an ObjectNode
	 * @param pathOnly
	 *            if true, only parents on the path of the specified ObjectNode
	 *            are returned, all parents (binding!) otherwise
	 * @param storeOnly
	 *            if true, only parents within the scope of the store in charge
	 *            of the specified ObjectNode are returned, parents up to the
	 *            root ObjectNode (uri="/") otherwise
	 * @param includeSelf
	 *            if true, the ObjectNode specified by object is included,
	 *            otherwise, it is excluded
	 * 
	 * @return a List of ObjectNode instances
	 * 
	 * @throws ServiceAccessException
	 * @throws ObjectNotFoundException
	 * @throws LinkedObjectNotFoundException
	 * @throws AccessDeniedException
	 * 
	 */
	public List getParents(SlideToken token, ObjectNode object,
			boolean pathOnly, boolean storeOnly, boolean includeSelf)
			throws ServiceAccessException, ObjectNotFoundException,
			LinkedObjectNotFoundException, AccessDeniedException, VetoException {
		List result = new ArrayList();

		if (pathOnly) {
			String[] uriTokens = object.getPath().tokens();
			UriPath path = new UriPath("/");
			Uri currentUri = namespace.getUri(token, path.toString());
			Uri objectUri = namespace.getUri(token, object.getUri());
			if (!storeOnly || currentUri.getStore() == objectUri.getStore()) {
				result.add(retrieve(token, path.toString()));
			}

			for (int i = 0; i < uriTokens.length; i++) {
				path = path.child(uriTokens[i]);
				currentUri = namespace.getUri(token, path.toString());
				if (i == uriTokens.length - 1 && !includeSelf) {
					break;
				}
				if (!storeOnly || currentUri.getStore() == objectUri.getStore()) {
					result.add(retrieve(token, path.toString()));
				}
			}
		} else {
			// TODO
		}

		return result;
	}
}

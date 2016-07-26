/**
 * 
 */
package org.kablink.teaming.web.socket.room;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.InternalException;
import org.kablink.teaming.asmodule.zonecontext.ZoneContext;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.context.request.RequestContextUtil;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.FolderDao;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.runasync.RunAsyncCallback;
import org.kablink.teaming.runasync.RunAsyncManager;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.NetworkUtil;
import org.kablink.teaming.util.SessionUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.WindowsUtil;
import org.kablink.teaming.web.socket.ServerEndpointConfigurator;
import org.kablink.teaming.web.socket.WebsocketException;
import org.kablink.teaming.web.socket.room.impl.RoomEndpointManagerImpl;

/**
 * @author jong
 *
 */
@ServerEndpoint
(
		value = "/websocket/rooms/{room-id}",
		configurator = ServerEndpointConfigurator.class
)
public class RoomEndpoint {

	protected static Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());
	
	private static final String serverNodeAddress = NetworkUtil.getNodeAddress();
	
	private static final Future<Void> COMPLETED = new CompletedFuture();
		
	/*
	 * Underlying websocket session
	 */
	private Session session;
	/*
	 * The ID of the room this session/connection is associated with.
	 * Note: "Endpoint -> Room" is a many-to-one relationship.
	 */
	private Long roomId;
	/*
	 * Time at which session/connection is established
	 */
	private long time;
	/*
	 * Runtime context for the session/connection. This doesn't change for the duration of the session.
	 */
	private RuntimeContext runtimeContext;
	/*
	 * Unique name for the session/connection.
	 */
	private String name;

	private Future<Void> future = COMPLETED;
	
	/*
	 * When a new connection/session to a room has been opened/established
	 */
	@OnOpen
	public void onOpen(Session session, EndpointConfig endpointConfig, @PathParam("room-id") Long roomId) {
		// Set up endpoint
		this.session = session;
		this.roomId = roomId;
		this.time = System.currentTimeMillis();
		this.runtimeContext = new RuntimeContext
				(WindowsUtil.getSamaccountname((String) endpointConfig.getUserProperties().get("username")), 
						(ZoneContext) endpointConfig.getUserProperties().get("zonecontext"));
		this.name = createName();
		
		// Validate that the specified room exists AND the context user is a member
		// of the room (or should we instead check that the user has at least read access to it?)
		// TODO
		
		
		// Push initial content of the room to the client. This is done synchronously.
		try {
			pushInitialRoomContent();
		} catch (IOException e) {
			throw new WebsocketException(e);
		}
		
		// Now that the endpoint is set up and initial content successfully delivered 
		// to the client, register this endpoint with the endpoint manager. 
		// It is important this should be the last step so that we don't make this 
		// endpoint "known" to the rest of the system until it is ready to participate
		// in the normal notification.
		// 
		getRoomEndpointManager().addRoomEndpoint(this);
		
		logger.info("Connection opened: " + this);
	}
	
	/*
	 * When a connection/session to a room has been closed
	 */
	@OnClose
    public void onClose(Session session, CloseReason reason) {
		getRoomEndpointManager().removeRoomEndpoint(this);
		
		logger.info("Connection closed: " + this + ", " + reason);
	}
	
	/*
	 * When an error occurs
	 */
	@OnError
    public void onError(Session session, Throwable t) {
		logger.error("Error on connection: " + this, t);
	}
	
	@Override
	public String toString() {
		return (this.name != null)? this.name : super.toString();
	}
	
	/*
	 * When a message is available to be processed
	 */
    @OnMessage
    public void onMessage(Session session, final String message) {
    	// Unlike HTTP protocol, websocket protocol is connection oriented.
    	// Therefore, there is no need to repeatedly check authentication
    	// for each message.
    	
    	setupThreadContext();	
    	try {
			getRunAsyncManager().execute(new RunAsyncCallback<Object>() {
				@Override
				public Object doAsynchronously() throws Exception {
					addCommentToRoom(message);
					return null;
				}
			}, RunAsyncManager.TaskType.MISC);
    	}
    	finally {
    		teardownThreadContext();
    	}

    }
    
    public Long getRoomId() {
    	return roomId;
    }
    
    /**
     * Send the message to the client associated with this endpoint.
     * 
     * @param msg
     */
    public void sendMessage(String msg) {
    	if(session == null)
    		throw new IllegalStateException("Cannot send message because there is no session on: " + this);
    	
    	if(!session.isOpen())
    		throw new IllegalStateException("Cannot send message because session is closed on: " + this);
    	
    	// Just because websocket supports full-duplex does NOT mean that you can send
    	// more than one message at a time in one direction. We need to coordinate access
    	// from multiple threads to ensure that only one thread is sending a message
    	// and all other threads are blocked until the previous message is complete.
    	synchronized(session) {
    		try {
				future.get();
			} catch (InterruptedException | ExecutionException e) {
				throw new WebsocketException(e);
			}
    		// Send the message asynchronously. This doesn't really improve the
    		// response time or even throughput for the single session. However,
    		// it greatly helps increase the efficiency with which the manager
    		// can broadcast a message to a large number of clients, since it
    		// can quickly iterate over them to initiate message sending without
    		// having to wait for each message to complete before moving on to the
    		// next one. So it significantly helps optimize across multiple sessions.
    		future = session.getAsyncRemote().sendText(msg);
    	}
    }
    
    public void close() {
    	if(session != null && session.isOpen())
			try {
				session.close();
			} catch (IOException e) {
				logger.warn("Error closing session on: " + this, e);
			}
    }
    
    /*
     * Push initial content of the room to the client. This is done synchronously.
     */
    private void pushInitialRoomContent() throws IOException {
		setupThreadContext();
		try{
			String initialRoomContent = getInitialRoomContent();
			session.getBasicRemote().sendText(initialRoomContent);
		}
		finally {
			teardownThreadContext();
		}
    }
    
    private FolderEntry getTopFolderEntry() {
    	// TODO
    	Long folderId = getRoomFolderId();
    	Folder folder = (Folder) getCoreDao().loadBinder(folderId, runtimeContext.getZoneId());
    	List<FolderEntry> entries = getFolderDao().loadEntries(folder, null);
    	if(entries != null && entries.size() > 0)
    		return entries.get(0);        	
    	else
    		return null;
    }
    
    private String getInitialRoomContent() {
    	// TODO
		StringBuilder sb = new StringBuilder();

		FolderEntry topEntry = getTopFolderEntry();
		
		if(topEntry != null) {
			sb.append(toOutText(topEntry));
			
			List<FolderEntry> replies = topEntry.getReplies();
			
			for(FolderEntry reply:replies) {
				sb.append("---------------------------------------------<br/>");
				sb.append(toOutText(reply));
			}
		}

		return sb.toString();
    }
    
    private void addCommentToRoom(String text) throws AccessControlException, WriteFilesException, WriteEntryDataException {
    	// TODO
    	Long folderId = getRoomFolderId();
    	String definitionId = "402883b90cc53079010cc539bf260002"; // Discussion entry
    	
    	FolderEntry topEntry = getTopFolderEntry();
    	FolderEntry comment; // Newly added comment
    	
    	if(topEntry != null) {
     		Map<String,Object> data = new HashMap<>();
    		//data.put("title", "");
    		data.put("description", text);
    		MapInputData inputData = new MapInputData(data);    		
    		comment = getFolderModule().addReply(folderId, topEntry.getId(), definitionId, inputData, null, null);
    	}
    	else {
        	Map<String,Object> data = new HashMap<>();
    		data.put("title", "Singleton Top");
    		data.put("description", text);
    		MapInputData inputData = new MapInputData(data);    		
    		topEntry = getFolderModule().addEntry(folderId, definitionId, inputData, null, null);
    		comment = topEntry;
    	}
    	
		// If still here, the comment has been added to the room successfully.
		// Let's notify the clients of this change in the room state.
		String outText = toOutText(comment);
				
		getRoomEndpointManager().broadcast(this.roomId, outText);
    }

	private Long getRoomFolderId() {
		// TODO
		if(this.roomId != null)
			return this.roomId;
		else
			throw new RuntimeException("Should never happen!");
	}

    private String toOutText(FolderEntry comment) {
    	// TODO
    	return toOutText(comment.getModification().getPrincipal().getName(),
    			comment.getModification().getDate(),
    			comment.getDescription().getText());
    }
    
    private String toOutText(String userName, Date date, String text) {
    	// TODO
    	//SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy HH:mm:ss zzzz");
    	SimpleDateFormat format = new SimpleDateFormat();
    	String dateStr = format.format(date);
    	
		String outText = "[" + userName + " - " +
				dateStr + "]<br/>" +
				text + "<br/>";
		return outText;
    }

    private void setupThreadContext() {
		// Set up zone context
		setupZoneContext();
		
		// Set up Hibernate session
		setupHibernateSession();

		// Set up request context
		setupRequestContext();
    }
    
    private void teardownThreadContext() {

		// Clear request context
		clearRequestContext();
		
		// Clear Hibernate session
		clearHibernateSession();
		
		// Clear zone context
		clearZoneContext();
    }

    private void setupZoneContext() {
		this.runtimeContext.setupZoneContext();
    }
    
    private void clearZoneContext() {
		this.runtimeContext.clearZoneContext();
    }
    
    private void setupHibernateSession() {
        if (SessionUtil.sessionActive())
        	// This should never happen. 
        	// TODO Remove this later after sufficient testing/bake.
        	throw new InternalException("We've got a pre-existing active Hibernate session");
        
        SessionUtil.sessionStartup();
    }
    
    private void clearHibernateSession() {
    	SessionUtil.sessionStop();
    }
    
	private void setupRequestContext() {
		this.runtimeContext.setupRequestContext();
	}
	
	private void clearRequestContext() {
		this.runtimeContext.clearRequestContext();
	}

	/*
	 * Create a name that uniquely identifies this endpoint. It only needs to be
	 * "reasonably" unique so that the information can be used mostly for logging
	 * purpose without creating ambiguity within a single deployment.
	 */
	private String createName() {
		return this.runtimeContext.getUserName() + "_" 
				+ this.runtimeContext.getClientAddr() + "_"
				+ serverNodeAddress + "_"
				+ this.time;
	}
	
    private RoomEndpointManagerImpl getRoomEndpointManager() {
    	return (RoomEndpointManagerImpl) SpringContextUtil.getBean("roomEndpointManager");
    }
    
    private ZoneModule getZoneModule() {
    	return (ZoneModule) SpringContextUtil.getBean("zoneModule");
    }
    
    private FolderModule getFolderModule() {
    	return (FolderModule) SpringContextUtil.getBean("folderModule");
    }

	private RunAsyncManager getRunAsyncManager() {
		return (RunAsyncManager) SpringContextUtil.getBean("runAsyncManager");
	}
	
	private CoreDao getCoreDao() {
		return (CoreDao) SpringContextUtil.getBean("coreDao");
	}

	private FolderDao getFolderDao() {
		return (FolderDao) SpringContextUtil.getBean("folderDao");
	}

    class RuntimeContext {
    	// Supplied fields - These values are always present.
    	private String userName;
    	/*
    	 * Zone context for the session/connection. This doesn't change for the duration of the session.
    	 */
    	private ZoneContext zoneContext;
    	
    	// Derived fields - These values may or may not be present depending
    	// on the time of access. Once these values are obtained, they are
    	// cached for faster response in subsequent accesses.
    	private Long userId;
    	private String zoneName;
    	private Long zoneId;
    	
    	RuntimeContext(String userName, ZoneContext zoneContext) {
    		this.userName = userName;
    		this.zoneContext = zoneContext;
    	}
    	
    	void setupRequestContext() {
    		if(userId == null) {
    			// This route is "slightly" less efficient since it involves fetching the user object
    			// from the database (or more likely from second level cache).
                zoneId = getZoneModule().getZoneIdByVirtualHost(zoneContext.getServerName());
        		RequestContextUtil.setThreadContext(zoneId, userName);
        		RequestContextHolder.getRequestContext().resolve(); // Make sure to resolve it here.
        		userId = RequestContextHolder.getRequestContext().getUserId();
        		zoneName = RequestContextHolder.getRequestContext().getZoneName();
    		}
    		else {
    			// This route is a bit more efficient since it uses cached information to avoid
    			// having to go to the database.
    			RequestContextUtil.setThreadContext(zoneName, zoneId, userName, userId);
        		RequestContextHolder.getRequestContext().resolve(); // Make sure to resolve it here.
    		}
    	}
    	
    	void clearRequestContext() {
    		RequestContextHolder.clear();
    	}

    	void setupZoneContext() {
        	ZoneContextHolder.setZoneContext(zoneContext);
    	}
    	
    	void clearZoneContext() {
    	   	ZoneContextHolder.clear();
    	}
    	
    	Long getZoneId() {
    		return zoneId;
    	}
    	
    	String getClientAddr() {
    		return zoneContext.getClientAddr();
    	}
    	
    	String getUserName() {
    		return userName;
    	}
    }
    
    private static class CompletedFuture implements Future<Void> {

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public Void get() throws InterruptedException, ExecutionException {
            return null;
        }

        @Override
        public Void get(long timeout, TimeUnit unit)
                throws InterruptedException, ExecutionException, TimeoutException {
            return null;
        }
    }
}

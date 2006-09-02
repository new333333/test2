package com.sitescape.ef.repository.jcr.jackrabbit;

import java.io.File;
import java.io.IOException;

import javax.activation.FileTypeMap;
import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.api.JackrabbitRepository;
import org.apache.jackrabbit.core.TransientRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.repository.RepositoryServiceException;
import com.sitescape.ef.repository.RepositorySession;
import com.sitescape.ef.repository.RepositorySessionFactory;
import com.sitescape.ef.repository.jcr.JCRRepositorySession;
import com.sitescape.ef.util.Constants;
import com.sitescape.ef.util.SPropsUtil;

public class JCRRepositorySessionFactory implements RepositorySessionFactory {

	protected Log logger = LogFactory.getLog(getClass());

	protected FileTypeMap mimeTypes;
	protected String repositoryRootDir;
	protected String homeSubdirName;
	protected String configFileName;
	protected String username;
	protected char[] password;
	protected boolean initializeOnStartup;

	private String repositoryHomeDir;
	private boolean initialized; 
	private Repository repository;
	private String workspaceName;
	
	public void setFileTypeMap(FileTypeMap mimeTypes) {
		this.mimeTypes = mimeTypes;
	}
	
	public void setConfigFileName(String configFileName) {
		this.configFileName = configFileName;
	}

	public void setHomeSubdirName(String homeSubdirName) {
		this.homeSubdirName = homeSubdirName;
	}

	public void setPassword(String password) {
		this.password = password.toCharArray();
	}

	public void setInitializeOnStartup(boolean initializeOnStartup) {
		this.initializeOnStartup = initializeOnStartup;
	}

	public void setRepositoryRootDir(String repositoryRootDir) {
		if(repositoryRootDir.endsWith(Constants.SLASH))
			this.repositoryRootDir = repositoryRootDir;
		else
			this.repositoryRootDir = repositoryRootDir + Constants.SLASH;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void initialize() throws RepositoryServiceException, UncheckedIOException {
		repositoryHomeDir = repositoryRootDir + homeSubdirName;
		workspaceName = SPropsUtil.getString("jcr.workspace.name", "sitescape");
		
		try {
			repository = getRepository();
			
			if(initializeOnStartup)
				initializeRepository(repository);
		}
		catch(IOException e) {
			logger.error("Could not initialize Jackrabbit JCR", e);
			throw new UncheckedIOException(e);
		}
		catch(RepositoryException e) {
			logger.error("Could not initialize Jackrabbit JCR", e);
			throw new RepositoryServiceException(e);
		}
	}

	public void shutdown() {
		if(initialized) {
			try {
				Session session = createSession(null);
				JackrabbitRepository repository = (JackrabbitRepository) session.getRepository();
				repository.shutdown();
			}
			catch(RepositoryException e) {
				logger.error("Could not shutdown Jackrabbit properly");
			}
			finally {
				initialized = false;
			}
		}
	}

	public RepositorySession openSession() throws RepositoryServiceException, UncheckedIOException {
		try {
			return new JCRRepositorySession(workspaceName, createSession(workspaceName), mimeTypes);
		} catch (RepositoryException e) {
			throw new RepositoryServiceException(e);
		}
	}

	public boolean supportVersioning() {
		return true;
	}

	public boolean supportVersionDeletion() {
		return false;
	}

	private Session createSession(String workspaceName)
	throws RepositoryException {
		Credentials credentials = new SimpleCredentials(username, password);
	
		try {
			return repository.login(credentials, workspaceName);
		}
		catch (RepositoryException e) {
			logger.error("Failed to login to the workspace " + workspaceName);
	
			throw e;
		}
	}

	private Repository getRepository() throws IOException {
		File repositoryFile;
		
		File repositoryFileInRootDir = new File(repositoryRootDir + configFileName);
		if(repositoryFileInRootDir.exists()) {
			// Repository configuration file is found in the repository root
			// directory. This most likely indicates that the user has 
			// customized the repository configuration. 
			repositoryFile = repositoryFileInRootDir;
		}
		else {
			// In this case we use factory-shipped default configuration file.
			Resource configFileResource = new ClassPathResource("config/" + configFileName);
			try {
				repositoryFile = configFileResource.getFile();
			} catch (IOException e) {
				logger.error("Could not find factory shipped Jackrabbit repository configuration file", e);
				throw e;
			}
		}

		return new TransientRepository(repositoryFile.getAbsolutePath(), repositoryHomeDir);	
	}
	
	private void initializeRepository(Repository repository) throws RepositoryException {
		Session session = createSession(null);
		
		session.logout();
		
		initialized = true;		
	}
	
}

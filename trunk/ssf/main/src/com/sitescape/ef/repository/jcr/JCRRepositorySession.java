package com.sitescape.ef.repository.jcr;

import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;
import javax.activation.FileTypeMap;
import javax.jcr.Session;

import org.springframework.web.multipart.MultipartFile;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.repository.RepositoryServiceException;
import com.sitescape.ef.repository.RepositorySession;

public class JCRRepositorySession implements RepositorySession {

	private Session session;
	
	public JCRRepositorySession(Session session) {
		this.session = session;
	}
	
	public void close() throws RepositoryServiceException, UncheckedIOException {
		if(session != null) {
			session.logout();
			session = null;
		}
	}

	public int fileInfo(Binder binder, DefinableEntity entity, String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String createVersioned(Binder binder, DefinableEntity entity, String relativeFilePath, MultipartFile mf) throws RepositoryServiceException, UncheckedIOException {
		// TODO Auto-generated method stub
		return null;
	}

	public String createVersioned(Binder binder, DefinableEntity entity, String relativeFilePath, InputStream in) throws RepositoryServiceException, UncheckedIOException {
		// TODO Auto-generated method stub
		return null;
	}

	public void createUnversioned(Binder binder, DefinableEntity entity, String relativeFilePath, InputStream in) throws RepositoryServiceException, UncheckedIOException {
		// TODO Auto-generated method stub
		
	}

	public void update(Binder binder, DefinableEntity entity, String relativeFilePath, MultipartFile mf) throws RepositoryServiceException, UncheckedIOException {
		// TODO Auto-generated method stub
		
	}

	public void update(Binder binder, DefinableEntity entity, String relativeFilePath, InputStream in) throws RepositoryServiceException, UncheckedIOException {
		// TODO Auto-generated method stub
		
	}

	public void delete(Binder binder, DefinableEntity entity, String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
		// TODO Auto-generated method stub
		
	}

	public void read(Binder binder, DefinableEntity entity, String relativeFilePath, OutputStream out) throws RepositoryServiceException, UncheckedIOException {
		// TODO Auto-generated method stub
		
	}

	public InputStream read(Binder binder, DefinableEntity entity, String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
		// TODO Auto-generated method stub
		return null;
	}

	public void readVersion(Binder binder, DefinableEntity entity, String relativeFilePath, String versionName, OutputStream out) throws RepositoryServiceException, UncheckedIOException {
		// TODO Auto-generated method stub
		
	}

	public DataSource getDataSource(Binder binder, DefinableEntity entity, String relativeFilePath, FileTypeMap fileTypeMap) throws RepositoryServiceException, UncheckedIOException {
		// TODO Auto-generated method stub
		return null;
	}

	public DataSource getDataSourceVersion(Binder binder, DefinableEntity entity, String relativeFilePath, String versionName, FileTypeMap fileTypeMap) throws RepositoryServiceException, UncheckedIOException {
		// TODO Auto-generated method stub
		return null;
	}

	public void checkout(Binder binder, DefinableEntity entity, String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
		// TODO Auto-generated method stub
		
	}

	public void uncheckout(Binder binder, DefinableEntity entity, String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
		// TODO Auto-generated method stub
		
	}

	public String checkin(Binder binder, DefinableEntity entity, String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isCheckedOut(Binder binder, DefinableEntity entity, String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
		// TODO Auto-generated method stub
		return false;
	}

	public long getContentLength(Binder binder, DefinableEntity entity, String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getContentLength(Binder binder, DefinableEntity entity, String relativeFilePath, String versionName) throws RepositoryServiceException, UncheckedIOException {
		// TODO Auto-generated method stub
		return 0;
	}

}

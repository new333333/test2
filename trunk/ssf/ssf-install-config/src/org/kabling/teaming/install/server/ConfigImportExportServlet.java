/*
 * ========================================================================
 *
 * Copyright (c) 2012 Unpublished Work of Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS AN UNPUBLISHED WORK AND CONTAINS CONFIDENTIAL,
 * PROPRIETARY AND TRADE SECRET INFORMATION OF NOVELL, INC. ACCESS TO
 * THIS WORK IS RESTRICTED TO (I) NOVELL, INC. EMPLOYEES WHO HAVE A NEED
 * TO KNOW HOW TO PERFORM TASKS WITHIN THE SCOPE OF THEIR ASSIGNMENTS AND
 * (II) ENTITIES OTHER THAN NOVELL, INC. WHO HAVE ENTERED INTO
 * APPROPRIATE LICENSE AGREEMENTS. NO PART OF THIS WORK MAY BE USED,
 * PRACTICED, PERFORMED, COPIED, DISTRIBUTED, REVISED, MODIFIED,
 * TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED, COMPILED,
 * LINKED, RECAST, TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN
 * CONSENT OF NOVELL, INC. ANY USE OR EXPLOITATION OF THIS WORK WITHOUT
 * AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL
 * LIABILITY.
 *
 * ========================================================================
 */
package org.kabling.teaming.install.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.kabling.teaming.install.shared.InstallerConfig;
import org.kabling.teaming.install.shared.LicenseInformation;
import org.kabling.teaming.install.shared.ShellCommandInfo;

/**
 * @author Rajesh
 * 
 */
public class ConfigImportExportServlet extends HttpServlet
{
	Logger logger = Logger.getLogger("org.kabling.teaming.install.server.ConfigImportExportServlet");
	private static final long serialVersionUID = 1L;
	private static Map<String, String> filesToZipMap = new HashMap<String, String>();

	private static final String CONFIG_ZIP_NAME = "filrconfig.zip";
	private static final String ZIP_MIME_TYPE = "application/zip";

	@Override
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);

		// Add the files that need to be zipped part of export
		if (TimeZoneHelper.isUnix())
		{
			// Cert Files
			filesToZipMap.put("cacerts", "/usr/lib64/jvm/jre-1.6.0-ibm/lib/security/cacerts");
			
			filesToZipMap.put("Novell-VA-release", "/vastorage/conf/Novell-VA-release");

			// Ganglia Files
			filesToZipMap.put("gmontd.conf", "/etc/opt/novell/ganglia/monitor/gmontd.conf");
			filesToZipMap.put("gmetad.conf", "/etc/opt/novell/ganglia/monitor/gmetad.conf");

			filesToZipMap.put("installer.xml", "/filrinstall/installer.xml");
			filesToZipMap.put("license-key.xml", "/filrinstall/license-key.xml");
			filesToZipMap.put("mysql-liquibase.properties", "/filrinstall/db/mysql-liquibase.properties");
			filesToZipMap.put("configurationDetails.properties", "/filrinstall/configurationDetails.properties");

			filesToZipMap.put("hibernate-ext.cfg.xml",
					"/opt/novell/filr/apache-tomcat/webapps/ssf/WEB-INF/classes/config/hibernate-ext.cfg.xml");
			filesToZipMap.put("zone-ext.cfg.xml", "/opt/novell/filr/apache-tomcat/webapps/ssf/WEB-INF/classes/config/zone-ext.cfg.xml");
			filesToZipMap.put("ssf-ext.properties", "/opt/novell/filr/apache-tomcat/webapps/ssf/WEB-INF/classes/config/ssf-ext.properties");
			filesToZipMap.put("messages-ext.properties",
					"/opt/novell/filr/apache-tomcat/webapps/ssf/WEB-INF/messages/messages-ext.properties");
			filesToZipMap.put("applicationContext-ext.xml",
					"/opt/novell/filr/apache-tomcat/webapps/ssf/WEB-INF/context/applicationContext-ext.xml");

		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		try
		{
			// return the current configurations settings as a zip file
			getFilrConfigurationSettings(resp);
		}
		catch (Exception e)
		{
			logger.error("Error trying to get filr configuation as a zip file " + e.getMessage());
		}
	}

	@Override
	protected void doPost(HttpServletRequest res, HttpServletResponse response) throws ServletException, IOException
	{

		// Commons file upload classes are specifically instantiated
		FileItemFactory factory = new DiskFileItemFactory();

		ServletFileUpload upload = new ServletFileUpload(factory);
		ServletOutputStream out = null;

		try
		{
			// Parse the incoming HTTP request
			// Commons takes over incoming request at this point
			// Get an iterator for all the data that was sent
			List<?> items = upload.parseRequest(res);
			Iterator<?> iter = items.iterator();

			boolean licenseKey = false;
			boolean upgradeOverwrite = false;
			boolean hostNameNotValidContinue = false;
			boolean dataDriveNotFoundContinue = false;
			while (iter.hasNext())
			{
				// Get the current item in the iteration
				FileItem item = (FileItem) iter.next();

				if (item.isFormField() && item.getFieldName().equals("licenseKey"))
				{
					licenseKey = Boolean.valueOf(item.getString());
					continue;
				}

				if (item.isFormField() && item.getFieldName().equals("upgradeOverwrite"))
				{
					upgradeOverwrite = Boolean.valueOf(item.getString());

					// For now, we will just use upgrade overwrite
					hostNameNotValidContinue = upgradeOverwrite;
					dataDriveNotFoundContinue = upgradeOverwrite;
					continue;
				}

				// Specify where on disk to write the file
				// Write the file data to disk
				// TODO: Place restrictions on upload data

				byte[] data = item.get();

				if (licenseKey)
				{
					FileOutputStream outStream = new FileOutputStream("/filrinstall/license-key.xml");

					outStream.write(item.get());
					outStream.close();
				}
				// Zip File - Import Process
				else
				{
					handleImportProcess(data, res, response, hostNameNotValidContinue, dataDriveNotFoundContinue);
				}
			}
		}
		catch (FileUploadException fue)
		{
			logger.error("File Upload exception " + fue.getMessage());
			returnFailureResponse(response, fue.getMessage());
		}
		catch (IOException ioe)
		{
			logger.error("IO exception " + ioe.getMessage());
			returnFailureResponse(response, ioe.getMessage());
		}
		catch (Exception e)
		{
			logger.error("Exception " + e.getMessage());
			returnFailureResponse(response, e.getMessage());
		}
		finally
		{
			if (out != null)
				out.close();
		}
	}

	private void getFilrConfigurationSettings(HttpServletResponse response) throws Exception
	{
		ServletOutputStream op = response.getOutputStream();
	

		response.setContentType(ZIP_MIME_TYPE);
		response.setHeader("Content-Disposition", "attachment;filename=\"" + CONFIG_ZIP_NAME + "\"");

		op.write(getFilrConfigZipFile());
		op.flush();

	}
	
	private byte[] getFilrConfigZipFile() throws Exception
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);

		// Zip all the files that we need to send as part of export configuration
		for (Entry<String, String> entry : filesToZipMap.entrySet())
		{
			// If the file does not exist, ignore and continue
			if (!(new File(entry.getValue()).exists()))
				continue;

			zos.putNextEntry(new ZipEntry(entry.getKey()));
			byte[] b = new byte[1024];
			int len;
			FileInputStream fis = new FileInputStream(entry.getValue());
			while ((len = fis.read(b)) != -1)
			{
				zos.write(b, 0, len);
			}
			fis.close();
			zos.closeEntry();
		}
		zos.close();
		
		return baos.toByteArray();
	}
	
	public static void saveFilrConfigLocally() throws Exception
	{
		initializeFileMap();
		FileOutputStream baos = new FileOutputStream("/vastorage/conf/filrconfig.zip");
		ZipOutputStream zos = new ZipOutputStream(baos);

		// Zip all the files that we need to send as part of export configuration
		for (Entry<String, String> entry : filesToZipMap.entrySet())
		{
			// If the file does not exist, ignore and continue
			if (!(new File(entry.getValue()).exists()))
				continue;

			zos.putNextEntry(new ZipEntry(entry.getKey()));
			byte[] b = new byte[1024];
			int len;
			FileInputStream fis = new FileInputStream(entry.getValue());
			while ((len = fis.read(b)) != -1)
			{
				zos.write(b, 0, len);
			}
			fis.close();
			zos.closeEntry();
		}
		zos.close();
	}

	private void handleImportProcess(byte[] data, HttpServletRequest res, HttpServletResponse response, boolean hostNameNotValidContinue,
			boolean dataDriveNotFoundContinue) throws ServletException, IOException
	{
		ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(data));
		ZipEntry entry = null;

		// First, extract to temp directory
		File tempDir = createTempDir();

		{
			// Extract the zip file into a temporary location
			while ((entry = zipStream.getNextEntry()) != null)
			{
				String entryName = entry.getName();
				String filePath = tempDir.getAbsolutePath() + File.separator + entryName;

				// If it is a file we know, we can save it to the file system
				if (filePath != null)
				{
					FileOutputStream outStream = new FileOutputStream(filePath);

					byte[] buf = new byte[4096];
					int bytesRead = 0;
					while ((bytesRead = zipStream.read(buf)) != -1)
					{
						outStream.write(buf, 0, bytesRead);
					}
					outStream.close();
					zipStream.closeEntry();
				}
			}
			zipStream.close();
		}

		{
			// Validate Requirements
			// If we have an invalid data drive or if the new appliance host name does not match the host name of the installer.xml
			// We should get out
			boolean validDataDriveFound = dataDriveNotFoundContinue;
			if (!dataDriveNotFoundContinue)
			{
				if (isVAReleaseMatch(tempDir.getAbsolutePath() + File.separator + "Novell-VA-release"))
				{
					validDataDriveFound = true;
				}
			}

			boolean validHostNameFound = hostNameNotValidContinue;
			if (!hostNameNotValidContinue)
			{
				if (isHostNameMatch(tempDir.getAbsolutePath() + File.separator + "installer.xml"))
				{
					validHostNameFound = true;
				}
			}

			if (!validDataDriveFound || !validHostNameFound)
			{
				// Set a response content type
				response.setContentType("text/html");

				ServletOutputStream out = response.getOutputStream();
				
				//Sending as html will not keep the case, so, sendign everything as lower case
				out.println("<status><datadrive>" + validDataDriveFound + "</datadrive><hostname>" + validHostNameFound
						+ "</hostname></status>");
				return;
			}
		}

		// Do we need to override license?
		boolean overrideLicense = isNeedToOverwriteLicense(tempDir.getAbsolutePath() + File.separator + "license-key.xml");

		// Copy installer.xml
		File oldLocation = new File(tempDir + File.separator + "installer.xml");
		File newLocation = new File("/filrinstall/installer.xml");
		FileUtils.copyFile(oldLocation, newLocation);

		// Copy mysql-liquibase.properties
		oldLocation = new File(tempDir + File.separator + "mysql-liquibase.properties");
		newLocation = new File("/filrinstall/db/mysql-liquibase.properties");
		FileUtils.copyFile(oldLocation, newLocation);

		// Update the database
		int result = ConfigService.executeCommand("cd /filrinstall/db; pwd; sudo sh manage-database.sh mysql updateDatabase", true)
				.getExitValue();

		// We got an error ( 0 for success, 107 for database exists)
		if (result != 0)
		{
			returnFailureResponse(response, "Error updating database ");
			return;
		}

		// Stop gmetad service
		if (ApplianceService.gmetadService(false).getExitValue() != 0)
		{
			logger.debug("Error stoping gmetad service,Error code " + result);
			returnFailureResponse(response, "Error stopping gmetad ");
			return;
		}

		// Stop gmontd service
		if (ApplianceService.gmondService(false).getExitValue() != 0)
		{
			logger.debug("Error stoping gmontd service,Error code " + result);
			returnFailureResponse(response, "Error stopping gmond ");
			return;
		}

		ConfigService.reconfigure(false);

		// Copy files (we have already copied installer.xml and mysql-liquibase.properties)
		extractZipContent(data, overrideLicense);

		// This should disable mysql and lucene for large deployment and creates
		// configured filed in /filrinstall
		ConfigService.markConfigurationDone(null);

		// Start gmetad service
		if (ApplianceService.gmetadService(true).getExitValue() != 0)
		{
			logger.debug("Error starting gmetad service,Error code " + result);
			returnFailureResponse(response, "Error starting gmetad ");
			return;
		}

		// Start gmontd service
		if (ApplianceService.gmondService(true).getExitValue() != 0)
		{
			logger.debug("Error starting gmontd service,Error code " + result);
			returnFailureResponse(response, "Error starting gmond ");
			return;
		}

		//Delete temp files
		String[] filesToDelete = tempDir.list();
		if (filesToDelete != null)
		{
			for (String file : filesToDelete)
			{
				logger.debug("Deleting file="+file  +" from temp directory");
				new File(file).delete();
			}
		}
		tempDir.delete();
		
		
		ConfigService.startFilrServer();

		returnSucessResponse(response, null);
	}

	private boolean isVAReleaseMatch(String newFilePath)
	{
		File oldFile = new File("/vastorage/conf/Novell-VA-release");

		// File does not exist, no match
		if (!oldFile.exists())
		{
			logger.debug("VA Release Match false, did not exist");
			return false;
		}

		Properties prop = new Properties();
		try
		{
			prop.load(new FileInputStream(oldFile));
			String oldProductName = prop.getProperty("product");

			prop.load(new FileInputStream(new File(newFilePath)));

			// product name matches
			if (prop.getProperty("product").equals(oldProductName))
			{
				logger.debug("VA Release Match true");
				return true;
			}
		}
		catch (Exception e)
		{
		}
		logger.debug("VA Release Match false");
		return false;
	}

	private boolean isHostNameMatch(String newFilePath)
	{

		InstallerConfig config = ConfigService.getConfiguration(newFilePath);
		String hostName = config.getNetwork().getHost();

		ShellCommandInfo info = ConfigService.executeCommand("sudo hostname -f", true);
		if (info.getExitValue() == 0)
		{
			String sysHostName = info.getOutputAsString();
			logger.info("Comparing "+sysHostName + " with "+hostName);
			if (sysHostName.contains(hostName))
			{
				logger.debug("Host Names match");
				return true;
			}
		}
		logger.debug("Host Names NO match");
		return false;
	}

	private boolean isNeedToOverwriteLicense(String newFilePath)
	{

		LicenseInformation newLicenseInfo = ConfigService.getLicenseInformation(newFilePath);

		if (newLicenseInfo.getDatesEffective().equals("trial"))
		{
			logger.debug("Need to overwrite license false");
			return false;
		}
		
		logger.debug("Need to overwrite license true");
		return true;
	}

	public static File createTempDir()
	{
		final String baseTempPath = System.getProperty("java.io.tmpdir");
		File tempDir = null;
		for (int i = 0; i < 100; i++)
		{
			Random rand = new Random();
			int randomInt = 1 + rand.nextInt();

			tempDir = new File(baseTempPath + File.separator + "tempDir" + randomInt);
			if (tempDir.exists() == false)
			{
				tempDir.mkdir();
				tempDir.deleteOnExit();
				break;
			}
		}

		return tempDir;
	}

	private void extractZipContent(byte[] data, boolean overwriteLicenseKey) throws FileNotFoundException, IOException
	{
		ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(data));
		ZipEntry entry = null;

		// Go through each file entry
		while ((entry = zipStream.getNextEntry()) != null)
		{
			String entryName = entry.getName();

			// These files have already been copied
			if (entryName.endsWith("installer.xml") || entryName.equals("mysql-liquibase.properties"))
			{
				continue;
			}
			
			if (entryName.endsWith("license-key.xml") && !overwriteLicenseKey)
				continue;
			String filePath = filesToZipMap.get(entryName);

			// If it is a file we know, we can save it to the file system
			if (filePath != null)
			{
				FileOutputStream outStream = new FileOutputStream(filePath);

				byte[] buf = new byte[4096];
				int bytesRead = 0;
				while ((bytesRead = zipStream.read(buf)) != -1)
				{
					outStream.write(buf, 0, bytesRead);
				}
				outStream.close();
				zipStream.closeEntry();
			}
		}
		zipStream.close();
	}

	private void returnFailureResponse(HttpServletResponse response, String statusMsg) throws IOException
	{
		String FAILURE_XML = "<status success=\"false\"></status>";

		// Set a response content type
		response.setContentType("text/html");

		ServletOutputStream out = response.getOutputStream();
		out.println(FAILURE_XML);
	}

	private void returnSucessResponse(HttpServletResponse response, String statusMsg) throws IOException
	{
		String SUCESS_XML = "<status success=\"true\"></status>";

		// Set a response content type
		response.setContentType("text/html");

		ServletOutputStream out = response.getOutputStream();
		out.println(SUCESS_XML);
	}
	
	private static void initializeFileMap()
	{
		if (TimeZoneHelper.isUnix() && filesToZipMap.size() == 0)
		{
			// Cert Files
			filesToZipMap.put("cacerts", "/usr/lib64/jvm/jre-1.6.0-ibm/lib/security/cacerts");
			
			filesToZipMap.put("Novell-VA-release", "/vastorage/conf/Novell-VA-release");

			// Ganglia Files
			filesToZipMap.put("gmontd.conf", "/etc/opt/novell/ganglia/monitor/gmontd.conf");
			filesToZipMap.put("gmetad.conf", "/etc/opt/novell/ganglia/monitor/gmetad.conf");

			filesToZipMap.put("installer.xml", "/filrinstall/installer.xml");
			filesToZipMap.put("license-key.xml", "/filrinstall/license-key.xml");
			filesToZipMap.put("mysql-liquibase.properties", "/filrinstall/db/mysql-liquibase.properties");
			filesToZipMap.put("configurationDetails.properties", "/filrinstall/configurationDetails.properties");

			filesToZipMap.put("hibernate-ext.cfg.xml",
					"/opt/novell/filr/apache-tomcat/webapps/ssf/WEB-INF/classes/config/hibernate-ext.cfg.xml");
			filesToZipMap.put("zone-ext.cfg.xml", "/opt/novell/filr/apache-tomcat/webapps/ssf/WEB-INF/classes/config/zone-ext.cfg.xml");
			filesToZipMap.put("ssf-ext.properties", "/opt/novell/filr/apache-tomcat/webapps/ssf/WEB-INF/classes/config/ssf-ext.properties");
			filesToZipMap.put("messages-ext.properties",
					"/opt/novell/filr/apache-tomcat/webapps/ssf/WEB-INF/messages/messages-ext.properties");
			filesToZipMap.put("applicationContext-ext.xml",
					"/opt/novell/filr/apache-tomcat/webapps/ssf/WEB-INF/context/applicationContext-ext.xml");

		}
	}
}

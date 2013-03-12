package org.kabling.teaming.install.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.kabling.teaming.install.shared.InstallerConfig;
import org.kabling.teaming.install.shared.LicenseInformation;
import org.kabling.teaming.install.shared.ProductInfo;
import org.kabling.teaming.install.shared.ShellCommandInfo;
import org.kabling.teaming.install.shared.UpdateStatus;


public final class UpdateService
{
	static Logger logger = Logger.getLogger("org.kabling.teaming.install.server.UpdateService");

	private UpdateService()
	{
	}

	public static UpdateStatus updateFilrSystem(boolean dataDriveNotFoundContinue, boolean hostNameNotValidContinue)
	{
		ZipInputStream zipStream;
		UpdateStatus updateStatus = new UpdateStatus();
		// First, extract to temp directory
		File tempDir = createTempDir();
		
		try
		{
			zipStream = new ZipInputStream(new FileInputStream("/vastorage/conf/vaconfig.zip"));
			ZipEntry entry = null;

			{
				// Extract the zip file into a temporary location
				while ((entry = zipStream.getNextEntry()) != null)
				{
					String entryName = entry.getName();
					String filePath = tempDir.getAbsolutePath() + File.separator + entryName;

					// If it is a file we know, we can save it to the file system
					if (filePath != null)
					{
						String parent = new File(filePath).getParent();
						new File(parent).mkdirs();
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
					if (isVAReleaseMatch(tempDir.getAbsolutePath() + File.separator + "etc/Novell-VA-release"))
					{
						validDataDriveFound = true;
					}
				}

				boolean validHostNameFound = hostNameNotValidContinue;
				if (!hostNameNotValidContinue)
				{
					if (isHostNameMatch(tempDir.getAbsolutePath() + File.separator + "filrinstall/installer.xml"))
					{
						validHostNameFound = true;
					}
				}

				if (!validDataDriveFound || !validHostNameFound)
				{
					updateStatus.setValidDataDrive(validDataDriveFound);
					updateStatus.setValidHostName(validHostNameFound);
					
					deleteTempDir(tempDir.getAbsolutePath());
					return updateStatus;
				}
			}

			// Do we need to override license?
			boolean overrideLicense = isNeedToOverwriteLicense(tempDir.getAbsolutePath() + File.separator + "filrinstall/license-key.xml");

			// Copy installer.xml
			File oldLocation = new File(tempDir + File.separator + "filrinstall/installer.xml");
			File newLocation = new File("/filrinstall/installer.xml");
			FileUtils.copyFile(oldLocation, newLocation);

			// Copy mysql-liquibase.properties
			oldLocation = new File(tempDir + File.separator + "filrinstall/db/mysql-liquibase.properties");
			newLocation = new File("/filrinstall/db/mysql-liquibase.properties");
			FileUtils.copyFile(oldLocation, newLocation);

			// Update the database
			int result = ConfigService.executeCommand("cd /filrinstall/db; pwd; sudo sh manage-database.sh mysql updateDatabase", true)
					.getExitValue();

			// We got an error ( 0 for success, 107 for database exists)
			if (result != 0)
			{
				updateStatus.setMessage("Error updating database");
				updateStatus.setReturnCode(result);
				updateStatus.setSuccess(false);

				deleteTempDir(tempDir.getAbsolutePath());
				return updateStatus;
			}

			// Stop gmetad service
			if (ApplianceService.gmetadService(false).getExitValue() != 0)
			{
				logger.debug("Error stoping gmetad service,Error code " + result);

				updateStatus.setMessage("Error stoping gmetad service");
				updateStatus.setReturnCode(result);
				updateStatus.setSuccess(false);

				deleteTempDir(tempDir.getAbsolutePath());
				return updateStatus;
			}

			// Stop gmontd service
			if (ApplianceService.gmondService(false).getExitValue() != 0)
			{
				logger.debug("Error stoping gmontd service,Error code " + result);

				updateStatus.setMessage("Error stoping gmontd service");
				updateStatus.setReturnCode(result);
				updateStatus.setSuccess(false);

				deleteTempDir(tempDir.getAbsolutePath());
				return updateStatus;
			}

			ConfigService.reconfigure(false);

			// Copy files (we have already copied installer.xml and mysql-liquibase.properties)
			extractZipContent(overrideLicense);

			// This should disable mysql and lucene for large deployment and creates
			// configured filed in /filrinstall
			ConfigService.markConfigurationDone(null);

			// Start gmetad service
			if (ApplianceService.gmetadService(true).getExitValue() != 0)
			{
				logger.debug("Error starting gmetad service,Error code " + result);

				updateStatus.setMessage("Error starting gmetad service");
				updateStatus.setReturnCode(result);
				updateStatus.setSuccess(false);

				deleteTempDir(tempDir.getAbsolutePath());
				return updateStatus;
			}

			// Start gmontd service
			if (ApplianceService.gmondService(true).getExitValue() != 0)
			{
				logger.debug("Error starting gmontd service,Error code " + result);

				updateStatus.setMessage("Error starting gmontd service");
				updateStatus.setReturnCode(result);
				updateStatus.setSuccess(false);
				
				deleteTempDir(tempDir.getAbsolutePath());
				return updateStatus;
			}

			// Delete temp files
			deleteTempDir(tempDir.getAbsolutePath());
			ConfigService.startFilrServer();

			updateStatus.setMessage("success");
			updateStatus.setSuccess(true);
			return updateStatus;
		}
		catch (FileNotFoundException e)
		{
			updateStatus.setSuccess(false);
			updateStatus.setMessage(e.getMessage());
			deleteTempDir(tempDir.getAbsolutePath());
		}
		catch (IOException e)
		{
			updateStatus.setSuccess(false);
			updateStatus.setMessage(e.getMessage());
			deleteTempDir(tempDir.getAbsolutePath());
		}
		deleteTempDir(tempDir.getAbsolutePath());
		return updateStatus;
	}

	private static void deleteTempDir(String path)
	{
		ConfigService.executeCommand("sudo rm -rf "+path, true);
	}
	
	private static boolean isVAReleaseMatch(String newFilePath)
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

	private static boolean isHostNameMatch(String newFilePath)
	{

		InstallerConfig config = ConfigService.getConfiguration(newFilePath);
		String hostName = config.getNetwork().getHost();

		ShellCommandInfo info = ConfigService.executeCommand("sudo hostname -f", true);
		if (info.getExitValue() == 0)
		{
			String sysHostName = info.getOutputAsString();
			logger.info("Comparing " + sysHostName + " with " + hostName);
			if (sysHostName.contains(hostName))
			{
				logger.debug("Host Names match");
				return true;
			}
		}
		logger.debug("Host Names NO match");
		return false;
	}

	private static boolean isNeedToOverwriteLicense(String newFilePath)
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

	private static void extractZipContent(boolean overwriteLicenseKey) throws FileNotFoundException, IOException
	{
		ZipInputStream zipStream = new ZipInputStream(new FileInputStream(new File("/vastorage/conf/vaconfig.zip")));
		ZipEntry entry = null;

		// Go through each file entry
		while ((entry = zipStream.getNextEntry()) != null)
		{
			String entryName = entry.getName();

			// These files have already been copied
			if (entryName.endsWith("filrinstall/installer.xml") || entryName.equals("filrinstall/db/mysql-liquibase.properties") || entryName.endsWith("Novell-VA-release"))
			{
				continue;
			}

			if (entryName.endsWith("license-key.xml") && !overwriteLicenseKey)
				continue;
			String filePath = "/" + entryName;

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

	public static ProductInfo getProductInfoFromZipFile()
	{
		ProductInfo productInfo = new ProductInfo();

		ZipFile zipFile = null;
		try
		{
			zipFile = new ZipFile("/vastorage/conf/vaconfig.zip");
			final Enumeration<? extends ZipEntry> entries = zipFile.entries();

			while (entries.hasMoreElements())
			{
				final ZipEntry zipEntry = entries.nextElement();
				if (zipEntry.getName().equals("etc/Novell-VA-release"))
				{
					InputStream in = zipFile.getInputStream(zipEntry);

					ByteArrayOutputStream outStream = new ByteArrayOutputStream();

					byte[] buf = new byte[4096];
					int bytesRead = 0;
					while ((bytesRead = in.read(buf)) != -1)
					{
						outStream.write(buf, 0, bytesRead);
					}
					outStream.close();
					
					Properties prop = new Properties();
					prop.load(new ByteArrayInputStream(outStream.toByteArray()));
					productInfo.setProductVersion(prop.getProperty("version"));
				}
			}
			zipFile.close();
		}
		catch (IOException e)
		{
		}
		return productInfo;
	}
}

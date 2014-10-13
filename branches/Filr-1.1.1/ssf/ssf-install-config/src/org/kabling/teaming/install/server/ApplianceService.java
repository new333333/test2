package org.kabling.teaming.install.server;

import org.apache.log4j.Logger;
import org.kabling.teaming.install.shared.ShellCommandInfo;

public final class ApplianceService
{
    static Logger logger = Logger.getLogger("org.kabling.teaming.install.server.ApplianceService");
	private ApplianceService()
	{
	}

	public static ShellCommandInfo gmetadService(boolean start)
	{
		if (start)
			return ConfigService.executeCommand(" sudo rcnovell-gmetad start", true);
		
		return ConfigService.executeCommand(" sudo rcnovell-gmetad stop", true);
	}
	
	public static ShellCommandInfo gmondService(boolean start)
	{
		if (start)
			return ConfigService.executeCommand(" sudo rcnovell-gmond start", true);
		
		return ConfigService.executeCommand(" sudo rcnovell-gmond stop", true);
	}

    public static void restartFirewall()
    {
        // Restart the firewall after the changes
        ConfigService.executeCommand("sudo SuSEfirewall2 stop", true);
        ConfigService.executeCommand("sudo SuSEfirewall2 start", true);
    }

    public static void enableAndStartMemcache(boolean start)
    {
        if (start)
        {
            logger.debug("Starting memcached service");

            ConfigService.executeCommand("chkconfig memcached on", true);
            ConfigService.executeCommand("rcmemcached start", true);
        }
        else
        {
            logger.debug("Stopping memcached service");

            ConfigService.executeCommand("rcmemcached stop", true);
            ConfigService.executeCommand("chkconfig memcached off", true);
        }
    }

}

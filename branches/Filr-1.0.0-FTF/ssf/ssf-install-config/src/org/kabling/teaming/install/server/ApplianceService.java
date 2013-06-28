package org.kabling.teaming.install.server;

import org.kabling.teaming.install.shared.ShellCommandInfo;

public final class ApplianceService
{
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
}

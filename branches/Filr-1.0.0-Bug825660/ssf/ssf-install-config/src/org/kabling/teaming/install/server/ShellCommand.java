package org.kabling.teaming.install.server;

import java.io.*;
import org.apache.log4j.Logger;

public class ShellCommand

{
	private Logger logger = Logger.getLogger("org.kabling.teaming.install.server.ShellCommand");

	public Process p;
	public OutputStreamWriter stdin;
	public BufferedReader stdout, stderr;

	public ShellCommand(String cmd) throws IOException
	{
		logger.debug("Command to execute " + cmd);
		p = Runtime.getRuntime().exec(new String[] {cmd});
		// This sleep is to work around a bug in JVM 1.4.1 on Linux/Solaris.
		// We need to give up control before calling getOutputStream().
		try
		{
			Thread.sleep(10);
		}
		catch (Exception ex)
		{
		}
		stdin = new OutputStreamWriter(p.getOutputStream());
		stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
		stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
	}

	public void waitFor()
	{
		try
		{
			stdin.close();
		}
		catch (IOException e)
		{
			logger.error(e.getMessage());
		}
		try
		{
			// IASys.out.println("Waiting for process to exit");
			p.waitFor();
			// IASys.out.println("Process exited successfully");
		}
		catch (InterruptedException e)
		{
			logger.error(e.getMessage());
		}
	}

	public int exitValue()
	{
		try
		{
			return p.exitValue();
		}
		catch (IllegalThreadStateException e)
		{
			logger.error(e.getMessage());
		}
		return -1;
	}

	// Write output to System.out, which is ignored by IA
	public void eatOutput()
	{
		Thread thread = new Thread()
		{
			public void run()
			{
				int c;
				try
				{
					while ((c = stdout.read()) >= 0)
					{
						System.out.write(c);
					}
				}
				catch (IOException ex)
				{
					System.out.println("IOException caught");
				}
			}
		};
		thread.start();
	}

	public void logOutput()
	{
		Thread thread = new Thread()
		{
			public void run()
			{
				int c;
				try
				{
					while ((c = stdout.read()) >= 0)
					{
						logger.error(c);
					}
				}
				catch (IOException ex)
				{
					logger.error(ex.getMessage());
				}
			}
		};
		thread.start();
	}

	public void feedInput(String text)
	{
		// IASys.out.println("input to ShellCommand: "+text);
		try
		{
			if (text == null)
			{
				stdin.close();
			}
			else
			{
				stdin.write(text + "\n");
			}
		}
		catch (IOException ex)
		{
			logger.error(ex.getMessage());
		}
	}

	// Write output to System.err, which is ignored by IA
	public void eatErrorOutput()
	{
		Thread thread = new Thread()
		{
			public void run()
			{
				int c;
				try
				{
					while ((c = stderr.read()) >= 0)
					{
						System.err.write(c);
					}
				}
				catch (IOException ex)
				{
					logger.error(ex.getMessage());
				}
			}
		};
		thread.start();
	}

	public void logErrorOutput()
	{
		Thread thread = new Thread()
		{
			public void run()
			{
				int c;
				try
				{
					while ((c = stderr.read()) >= 0)
					{
						logger.error(c);
					}
				}
				catch (IOException ex)
				{
					logger.error(ex.getMessage());
				}
			}
		};
		thread.start();
	}
}

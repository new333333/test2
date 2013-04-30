package org.kabling.teaming.install.shared;

import java.util.List;

public class ShellCommandInfo {
	private int exitValue;
	private List<String> output;
	
	public ShellCommandInfo()
	{
		
	}

	public int getExitValue() {
		return exitValue;
	}

	public void setExitValue(int exitValue) {
		this.exitValue = exitValue;
	}

	public List<String> getOutput() {
		return output;
	}

	public void setOutput(List<String> output) {
		this.output = output;
	}
	
	public String getOutputAsString()
	{
		
		if (output != null)
		{
			StringBuilder builder = new StringBuilder();
			for (String str: output)
			{
				builder.append(str);
				builder.append(" ");
			}
			return builder.toString();
		}
		return null;
	}
	
}

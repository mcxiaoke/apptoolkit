package com.mcxiaoke.shell.others;

public class HostsUtils {
/*
	public static boolean copyHosts(String text, String fileName) {
		try {
			FileUtils.rewriteFile(fileName, text);
			String cmd = String.format("busybox cp %s /system/etc/", fileName);
			CommandResult result = RootUtils.runCommand(cmd, true, null);
			if (result.error.equals("")) {
				result = RootUtils.runCommand("chmod 644 /system/etc/hosts",
						true, null);
			}
			return result.error.equals("");
		} catch (Exception e) {
			return false;
		}
	}*/
}

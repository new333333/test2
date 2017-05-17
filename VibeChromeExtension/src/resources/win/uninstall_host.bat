
:: Copyright 2016 MicroFocus. All rights reserved.
:: Deletes the entry created by install_host.bat
REG DELETE "HKCU\Software\Google\Chrome\NativeMessagingHosts\com.microfocus.jnlplauncher" /f
REG DELETE "HKLM\Software\Google\Chrome\NativeMessagingHosts\com.microfocus.jnlplauncher" /f

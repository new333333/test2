:: Copyright 2016 MicroFocus. All rights reserved.
:: Change HKCU to HKLM if you want to install globally.
:: %~dp0 is the directory containing this bat script and ends with a backslash.
REG ADD "HKCU\Software\Google\Chrome\NativeMessagingHosts\com.microfocus.jnlplauncher" /ve /t REG_SZ /d "%~dp0mf_nh_manifest.json" /f
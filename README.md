# RunInBackgroundPermissionSetter

<a href="https://f-droid.org/packages/com.pavelsikun.runinbackgroundpermissionsetter/" target="_blank">
<img src="https://f-droid.org/badge/get-it-on.png" alt="Get it on F-Droid" height="80"/></a>

Overview
---
Android 7.0 added new permission called RUN_IN_BACKGROUND, which can restrict background behavior of apps(blocking broadcast receivers, services, etc.). However this permission is fully hidden and there's no way to activate it besides using adb commands.

You can revoke RUN_IN_BACKGROUND permission on any app by executing following command in the adb shell:

```cmd appops set <package_name> RUN_IN_BACKGROUND ignore```

To grant this permission back, one should execute this:

```cmd appops set <package_name> RUN_IN_BACKGROUND allow```

To check status of RUN_IN_BACKGROUND permission on any app, one can execute this command:

```cmd appops get <package_name> RUN_IN_BACKGROUND```


```<package_name>``` is obviously name of application package, be it com.android.calculator2 or anything else.

By disabling RUN_IN_BACKGROUND, apps such as Hangouts or Facebook will completely stop syncing in the background until you open them up.
You can find more information about these commands by reading [this xda article](https://www.xda-developers.com/freeze-app-background-processes-without-root-android-nougat).

About this app
---
Anyway, this app is for those, who don't want to mess with adb shell and input commands manually and prefer to just click on app name to enable/disable it from running in the background.
Sadly, android apps are not allowed to implicitely edit permissions of other apps, so this application requires root to overcome it.


Contributors
---
[Pavel Sikun (MrBIMC)](https://github.com/MrBIMC) - creator of the app

[Gianmarco Scarano (SlimShadys)](https://github.com/SlimShadys) - provided translations for Italian language

[basemym](https://github.com/basemym) - provided translations for Arabic language

[ToxxMe](https://github.com/ToxxMe) - provided translations for German language



Licence is GPLv3
----





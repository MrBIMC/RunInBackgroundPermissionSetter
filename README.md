# RunInBackgroundPermissionSetter

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

About this app
---
Anyway, this app is for those, who don't want to mess with adb shell and input commands manually and prefer to just click on app name to enable/disable it from running in the background.
Sadly, android apps are not allowed to implicitely edit permissions of other apps, so this application requires root to overcome it.


[Download](https://github.com/MrBIMC/RunInBackgroundPermissionSetter/releases/download/1.0/app-release.apk)


Licence is GPLv3
----





package com.pavelsikun.runinbackgroundpermissionsetter

import android.graphics.drawable.Drawable

/**
 * Created by Pavel Sikun on 16.07.17.
 */
data class AppItem(val appIcon: Drawable, val appName: String, val appPackage: String, var isEnabled: Boolean = true)
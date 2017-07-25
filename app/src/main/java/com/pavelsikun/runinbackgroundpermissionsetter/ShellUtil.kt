package com.pavelsikun.runinbackgroundpermissionsetter

import eu.chainfire.libsuperuser.Shell
import java.util.concurrent.CompletableFuture

/**
 * Created by Pavel Sikun on 16.07.17.
 */

typealias Callback = (isSuccess: Boolean) -> Unit

private val shell by lazy { Shell.Builder()
        .setShell("su")
        .open()
}

fun checkRunInBackgroundPermission(pkg: String): CompletableFuture<Boolean> {
    val future = CompletableFuture<Boolean>()

    shell.addCommand("cmd appops get $pkg RUN_IN_BACKGROUND", 1) { _, _, output: MutableList<String> ->
        val outputString = output.joinToString()
        val runInBackgroundEnabled = outputString.contains("allow")
        future.complete(runInBackgroundEnabled)
    }

    return future
}

fun setRunInBackgroundPermission(pkg: String, setEnabled: Boolean, callback: Callback): CompletableFuture<Boolean> {
    val future = CompletableFuture<Boolean>()
    val cmd = if (setEnabled) "allow" else "ignore"

    shell.addCommand("cmd appops set $pkg RUN_IN_BACKGROUND $cmd", 1) { _, _, output: MutableList<String> ->
        val outputString = output.joinToString()
        val isSuccess = outputString.trim().isEmpty()
        callback(isSuccess)
        future.complete(isSuccess)
    }

    return future
}
package com.pavelsikun.runinbackgroundpermissionsetter

import android.app.Activity
import android.support.design.widget.Snackbar
import android.view.View
import eu.chainfire.libsuperuser.Shell
import java.util.concurrent.CompletableFuture

/**
 * Created by Pavel Sikun on 16.07.17.
 */

private val shell by lazy { Shell.Builder()
        .setShell("su")
        .open()
}

fun checkRunInBackgroundPermission(pkg: String): CompletableFuture<Boolean> {
    val future = CompletableFuture<Boolean>()

    shell.addCommand("cmd appops get $pkg RUN_IN_BACKGROUND", 1) { _, _, output: MutableList<String> ->
        val outputString = output.joinToString()
        val runInBackgroundDisabled = outputString.contains("ignore")
        future.complete(!runInBackgroundDisabled)
    }

    return future
}

fun Activity.setRunInBackgroundPermission(pkg: String, setEnabled: Boolean): CompletableFuture<Boolean> {
    val future = CompletableFuture<Boolean>()
    val cmdFlag = if (setEnabled) "allow" else "ignore"
    val msgFlag = if (setEnabled) getString(R.string.message_allow) else getString(R.string.message_ignore)

    shell.addCommand("cmd appops set $pkg RUN_IN_BACKGROUND $cmdFlag", 1) { _, _, output: MutableList<String> ->
        val outputString = output.joinToString()
        val isError = outputString.trim().isNotEmpty()

        if (!isError) {
            runOnUiThread {
                val rootView: View = findViewById(android.R.id.content)
                val msg = "$pkg RUN_IN_BACKGROUND ${getString(R.string.message_was_set_to)} '$msgFlag'"
                Snackbar.make(rootView, msg, Snackbar.LENGTH_SHORT).show()
            }
        }
        else {
            runOnUiThread {
                val rootView: View = findViewById(android.R.id.content)
                val msg = "${getString(R.string.message_there_was_error)} $pkg RUN_IN_BACKGROUND ${getString(R.string.message_to)} '$msgFlag'"
                Snackbar.make(rootView, msg, Snackbar.LENGTH_SHORT).show()
            }
        }

        future.complete(!outputString.trim().isEmpty())
    }

    return future
}
package com.pavelsikun.runinbackgroundpermissionsetter

import android.app.Activity
import android.util.Log
import android.widget.Toast
import eu.chainfire.libsuperuser.Shell
import java.util.concurrent.CompletableFuture

/**
 * Created by Pavel Sikun on 16.07.17.
 */

val shell by lazy { Shell.Builder()
        .setShell("su")
        .open()
}

fun checkRunInBackgroundPermission(pkg: String): CompletableFuture<Boolean> {
    val future = CompletableFuture<Boolean>()

    shell.addCommand("cmd appops get $pkg RUN_IN_BACKGROUND", 1) { _, _, output: MutableList<String> ->
        val outputString = output.joinToString()
        val runInBackgroundDisabled = outputString.contains("ignore")
        future.complete(!runInBackgroundDisabled)

        Log.d("app", outputString)
        Log.d("app", "output contains 'ignore' ? " + runInBackgroundDisabled)
    }

    return future
}

fun Activity.setRunInBackgroundPermission(pkg: String, setEnabled: Boolean): CompletableFuture<Boolean> {
    val future = CompletableFuture<Boolean>()
    val cmdFlag = if (setEnabled) "allow" else "ignore"

    shell.addCommand("cmd appops set $pkg RUN_IN_BACKGROUND $cmdFlag", 1) { _, _, output: MutableList<String> ->
        val outputString = output.joinToString()
        val isError = outputString.trim().isNotEmpty()

        if (!isError) {
            runOnUiThread {
                Toast.makeText(this, "$pkg RUN_IN_BACKGROUND set to $cmdFlag", Toast.LENGTH_LONG).show()
            }
        }
        else {
            runOnUiThread {
                Toast.makeText(this, "There was error setting $pkg RUN_IN_BACKGROUND set to $cmdFlag", Toast.LENGTH_LONG).show()
            }
        }

        future.complete(!outputString.trim().isEmpty())
    }

    return future
}
package com.pavelsikun.runinbackgroundpermissionsetter

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager

import android.content.pm.PackageManager
import android.content.Intent
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg

class MainActivity : AppCompatActivity() {

    val adapter by lazy {
        AppListAdapter {
            setRunInBackgroundPermission(it.appPackage, !it.isEnabled)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        swipeRefreshLayout.setOnRefreshListener {
            adapter.clear()
            loadApps()
        }

        loadApps()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_github) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/MrBIMC/RunInBackgroundPermissionSetter")))
        }
        return super.onOptionsItemSelected(item)
    }

    fun loadApps() {
        swipeRefreshLayout.isRefreshing = false
        val ad = ProgressDialog(this)
        ad.setTitle("Loading apps and their RUN_IN_BACKGROUND status...")
        ad.setCancelable(false)
        ad.show()

        async(UI) {
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            val apps = packageManager.queryIntentActivities(intent, PackageManager.GET_META_DATA)

            apps.map {
                val data: Deferred<AppItem> = bg {
                    AppItem(it.loadIcon(packageManager),
                            it.loadLabel(packageManager).toString(),
                            it.activityInfo.packageName,
                            checkRunInBackgroundPermission(it.activityInfo.packageName).get())
                }

                adapter.addItem(data.await())

                if (adapter.itemCount == apps.size) {
                    ad.cancel()
                }
            }
        }
    }

}

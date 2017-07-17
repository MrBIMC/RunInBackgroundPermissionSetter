package com.pavelsikun.runinbackgroundpermissionsetter

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
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import com.yarolegovich.lovelydialog.LovelyProgressDialog

class MainActivity : AppCompatActivity() {

    val adapter by lazy {
        AppListAdapter {
            setRunInBackgroundPermission(it.appPackage, it.isEnabled)
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
        when (item.itemId) {
            R.id.action_github -> openGithub()
            R.id.action_info -> showInfoDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    fun loadApps() {
        swipeRefreshLayout.isRefreshing = false
        val ad = LovelyProgressDialog(this)
                .setTopColorRes(R.color.accent)
                .setTopTitle(getString(R.string.loading_dialog_title))
                .setTopTitleColor(getColor(android.R.color.white))
                .setIcon(R.drawable.clock_alert)
                .setMessage(getString(R.string.loading_dialog_message)).show()

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
                    ad.dismiss()
                }
            }
        }
    }

    fun openGithub() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/MrBIMC/RunInBackgroundPermissionSetter")))
    }

    fun showInfoDialog() {
        LovelyStandardDialog(this)
                .setTopColorRes(R.color.accent)
                .setTopTitle(getString(R.string.button_open_information))
                .setTopTitleColor(getColor(android.R.color.white))
                .setButtonsColorRes(R.color.primary)
                .setIcon(R.drawable.information)
                .setMessage(R.string.info_dialog_message)
                .setNegativeButton(getString(R.string.button_close_dialog), null)
                .setPositiveButton(getString(R.string.button_open_github)) {
                    openGithub()
                }
                .show()
    }

}

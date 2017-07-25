package com.pavelsikun.runinbackgroundpermissionsetter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager

import android.content.pm.PackageManager
import android.content.Intent
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import com.yarolegovich.lovelydialog.LovelyProgressDialog
import kotlinx.android.synthetic.main.search_view.*
import android.view.ViewAnimationUtils
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.support.design.widget.Snackbar
import android.view.inputmethod.InputMethodManager
import com.pavelsikun.runinbackgroundpermissionsetter.AppListAdapter.SortMethod


class MainActivity : AppCompatActivity() {

    val adapter by lazy {
        AppListAdapter { app ->
            setRunInBackgroundPermission(app.appPackage, app.isEnabled) { isSuccess ->
                val status = if (app.isEnabled) getString(R.string.message_allow) else getString(R.string.message_ignore)
                val msgSuccess = "${app.appPackage} RUN_IN_BACKGROUND ${getString(R.string.message_was_set_to)} '$status'"
                val msgError = "${getString(R.string.message_there_was_error)} $${app.appPackage} RUN_IN_BACKGROUND ${getString(R.string.message_to)} '$status'"

                runOnUiThread {
                    val msg = if (isSuccess) msgSuccess else msgError
                    Snackbar.make(coordinator, msg, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

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
            R.id.action_search -> showSearchBar()
            R.id.action_info -> showInfoDialog()
            R.id.action_sort_name -> adapter.sort(SortMethod.NAME)
            R.id.action_sort_package -> adapter.sort(SortMethod.PACKAGE)
            R.id.action_sort_disabled_first -> adapter.sort(SortMethod.STATE)
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
                    adapter.sort()
                    ad.dismiss()
                }
            }
        }
    }

    fun openGithub() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/MrBIMC/RunInBackgroundPermissionSetter")))
    }

    fun showSearchBar() {
        val viewWidth = searchOverlay.measuredWidth.toFloat()
        val x = (searchOverlay.measuredWidth * 0.95).toInt()
        val y = searchOverlay.measuredHeight / 2

        val enterAnim = ViewAnimationUtils.createCircularReveal(searchOverlay, x, y, 0f, viewWidth)
        val exitAnim = ViewAnimationUtils.createCircularReveal(searchOverlay, x, y, viewWidth, 0f)

        val inputManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        enterAnim.addListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                searchBox.requestFocus()
                inputManager.showSoftInput(searchBox, InputMethodManager.SHOW_IMPLICIT)
            }
        })

        exitAnim.addListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animator: Animator) {
                searchOverlay.visibility = View.INVISIBLE
            }
        })

        buttonClear.setOnClickListener {
            searchBox.text.clear()
        }

        buttonBack.setOnClickListener {
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
            exitAnim.start()
        }

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) { /*IGNORE*/ }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { /*IGNORE*/ }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter.filter(searchBox.text.toString().toLowerCase())
            }
        })

        searchOverlay.visibility = View.VISIBLE
        enterAnim.start()
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

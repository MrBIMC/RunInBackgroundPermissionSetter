package com.pavelsikun.runinbackgroundpermissionsetter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.listitem_app.view.*

/**
 * Created by Pavel Sikun on 16.07.17.
 */
class AppListAdapter(val appItems: MutableList<AppItem> = ArrayList<AppItem>(), val itemClick: (AppItem) -> Unit) : RecyclerView.Adapter<AppListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View, val itemClick: (AppItem) -> Unit) : RecyclerView.ViewHolder(view) {

        fun bindAppItem(appItem: AppItem) = with(itemView) {
            permissionSwitch.setOnCheckedChangeListener(null)

            appIcon.setImageDrawable(appItem.appIcon)
            appName.text = appItem.appName
            appPackage.text = appItem.appPackage
            permissionSwitch.isChecked = appItem.isEnabled

            permissionSwitch.setOnCheckedChangeListener { _, _ ->
                itemClick(appItem)
                appItem.isEnabled = !appItem.isEnabled
            }

            container.setOnClickListener {
                permissionSwitch.isChecked = !permissionSwitch.isChecked
            }
        }

    }

    fun addItem(appItem: AppItem) {
        appItems.add(appItem)
        notifyDataSetChanged()
    }

    fun clear() {
        appItems.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listitem_app, parent, false)
        return ViewHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindAppItem(appItems[position])
    }

    override fun getItemCount() = appItems.size
}
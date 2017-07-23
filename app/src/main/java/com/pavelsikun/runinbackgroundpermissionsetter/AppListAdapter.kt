package com.pavelsikun.runinbackgroundpermissionsetter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import kotlinx.android.synthetic.main.listitem_app.view.*

/**
 * Created by Pavel Sikun on 16.07.17.
 */
class AppListAdapter(val itemClick: (AppItem) -> Unit) : RecyclerView.Adapter<AppListAdapter.ViewHolder>(), FastScrollRecyclerView.SectionedAdapter {

    val allItems = ArrayList<AppItem>()
    val displayedItems = ArrayList<AppItem>()

    inner class ViewHolder(view: View, val itemClick: (AppItem) -> Unit) : RecyclerView.ViewHolder(view) {

        fun bindAppItem(appItem: AppItem) = with(itemView) {
            fun setStatus(isEnabled: Boolean) {
                appItem.isEnabled = isEnabled
                permissionStatus.text = if (appItem.isEnabled) {
                    context.getString(R.string.message_allow)
                } else {
                    context.getString(R.string.message_ignore)
                }
            }

            permissionSwitch.setOnCheckedChangeListener(null)

            appIcon.setImageDrawable(appItem.appIcon)
            appName.text = appItem.appName
            appPackage.text = appItem.appPackage

            permissionSwitch.isChecked = appItem.isEnabled
            setStatus(appItem.isEnabled)

            permissionSwitch.setOnCheckedChangeListener { _, _ ->
                setStatus(!appItem.isEnabled)
                itemClick(appItem)
            }

            container.setOnClickListener {
                permissionSwitch.isChecked = !permissionSwitch.isChecked
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listitem_app, parent, false)
        return ViewHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindAppItem(displayedItems[position])
    }

    override fun getSectionName(position: Int) = displayedItems[position].appName[0].toString()

    override fun getItemCount() = displayedItems.size

    fun addItem(appItem: AppItem) {
        allItems.add(appItem)
        setItemsToDisplay(allItems)
        notifyDataSetChanged()
    }

    fun clear() {
        allItems.clear()
        setItemsToDisplay(allItems)
        notifyDataSetChanged()
    }

    fun sortByName() {
        allItems.sortBy { it.appName }
        setItemsToDisplay(allItems)
        notifyDataSetChanged()
    }

    fun sortByPackage() {
        allItems.sortBy { it.appPackage }
        setItemsToDisplay(allItems)
        notifyDataSetChanged()
    }

    fun filter(keyword: String) {
        val filteredList = allItems.filter {
            it.appName.toLowerCase().contains(keyword) or it.appPackage.toLowerCase().contains(keyword)
        }

        setItemsToDisplay(filteredList)
    }

    fun setItemsToDisplay(items: List<AppItem>) {
        displayedItems.clear()
        displayedItems.addAll(items)
        notifyDataSetChanged()
    }
}